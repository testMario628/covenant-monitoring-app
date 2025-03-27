import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, mergeMap, switchMap } from 'rxjs/operators';
import { CovenantService } from '../../core/services/covenant.service';
import * as CovenantActions from './covenant.actions';

@Injectable()
export class CovenantEffects {
  
  loadCovenants$ = createEffect(() => this.actions$.pipe(
    ofType(CovenantActions.loadCovenants),
    switchMap(() => 
      this.covenantService.getAllCovenants().pipe(
        map(covenants => CovenantActions.loadCovenantsSuccess({ covenants })),
        catchError(error => of(CovenantActions.loadCovenantsFailure({ error })))
      )
    )
  ));

  loadCovenant$ = createEffect(() => this.actions$.pipe(
    ofType(CovenantActions.loadCovenant),
    mergeMap(action => 
      this.covenantService.getCovenantById(action.id).pipe(
        map(covenant => CovenantActions.loadCovenantSuccess({ covenant })),
        catchError(error => of(CovenantActions.loadCovenantFailure({ error })))
      )
    )
  ));

  loadCovenantsByContract$ = createEffect(() => this.actions$.pipe(
    ofType(CovenantActions.loadCovenantsByContract),
    mergeMap(action => 
      this.covenantService.getCovenantsByContract(action.contractId).pipe(
        map(covenants => CovenantActions.loadCovenantsByContractSuccess({ covenants })),
        catchError(error => of(CovenantActions.loadCovenantsByContractFailure({ error })))
      )
    )
  ));

  loadCovenantsByStatus$ = createEffect(() => this.actions$.pipe(
    ofType(CovenantActions.loadCovenantsByStatus),
    mergeMap(action => 
      this.covenantService.getCovenantsByStatus(action.status).pipe(
        map(covenants => CovenantActions.loadCovenantsByStatusSuccess({ covenants })),
        catchError(error => of(CovenantActions.loadCovenantsByStatusFailure({ error })))
      )
    )
  ));

  loadCovenantsDueForMonitoring$ = createEffect(() => this.actions$.pipe(
    ofType(CovenantActions.loadCovenantsDueForMonitoring),
    mergeMap(() => 
      this.covenantService.getCovenantsDueForMonitoring().pipe(
        map(covenants => CovenantActions.loadCovenantsDueForMonitoringSuccess({ covenants })),
        catchError(error => of(CovenantActions.loadCovenantsDueForMonitoringFailure({ error })))
      )
    )
  ));

  createCovenant$ = createEffect(() => this.actions$.pipe(
    ofType(CovenantActions.createCovenant),
    mergeMap(action => 
      this.covenantService.createCovenant(action.covenant).pipe(
        map(covenant => CovenantActions.createCovenantSuccess({ covenant })),
        catchError(error => of(CovenantActions.createCovenantFailure({ error })))
      )
    )
  ));

  updateCovenant$ = createEffect(() => this.actions$.pipe(
    ofType(CovenantActions.updateCovenant),
    mergeMap(action => 
      this.covenantService.updateCovenant(action.id, action.covenant).pipe(
        map(covenant => CovenantActions.updateCovenantSuccess({ covenant })),
        catchError(error => of(CovenantActions.updateCovenantFailure({ error })))
      )
    )
  ));

  deleteCovenant$ = createEffect(() => this.actions$.pipe(
    ofType(CovenantActions.deleteCovenant),
    mergeMap(action => 
      this.covenantService.deleteCovenant(action.id).pipe(
        map(() => CovenantActions.deleteCovenantSuccess({ id: action.id })),
        catchError(error => of(CovenantActions.deleteCovenantFailure({ error })))
      )
    )
  ));

  constructor(
    private actions$: Actions,
    private covenantService: CovenantService
  ) {}
}
