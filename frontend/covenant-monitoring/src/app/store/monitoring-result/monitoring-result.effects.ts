import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, mergeMap, switchMap } from 'rxjs/operators';
import { MonitoringResultService } from '../../core/services/monitoring-result.service';
import * as MonitoringResultActions from './monitoring-result.actions';

@Injectable()
export class MonitoringResultEffects {
  
  loadMonitoringResults$ = createEffect(() => this.actions$.pipe(
    ofType(MonitoringResultActions.loadMonitoringResults),
    switchMap(() => 
      this.monitoringResultService.getAllMonitoringResults().pipe(
        map(monitoringResults => MonitoringResultActions.loadMonitoringResultsSuccess({ monitoringResults })),
        catchError(error => of(MonitoringResultActions.loadMonitoringResultsFailure({ error })))
      )
    )
  ));

  loadMonitoringResult$ = createEffect(() => this.actions$.pipe(
    ofType(MonitoringResultActions.loadMonitoringResult),
    mergeMap(action => 
      this.monitoringResultService.getMonitoringResultById(action.id).pipe(
        map(monitoringResult => MonitoringResultActions.loadMonitoringResultSuccess({ monitoringResult })),
        catchError(error => of(MonitoringResultActions.loadMonitoringResultFailure({ error })))
      )
    )
  ));

  loadMonitoringResultsByCovenant$ = createEffect(() => this.actions$.pipe(
    ofType(MonitoringResultActions.loadMonitoringResultsByCovenant),
    mergeMap(action => 
      this.monitoringResultService.getMonitoringResultsByCovenant(action.covenantId).pipe(
        map(monitoringResults => MonitoringResultActions.loadMonitoringResultsByCovenantSuccess({ monitoringResults })),
        catchError(error => of(MonitoringResultActions.loadMonitoringResultsByCovenantFailure({ error })))
      )
    )
  ));

  loadLatestMonitoringResultForCovenant$ = createEffect(() => this.actions$.pipe(
    ofType(MonitoringResultActions.loadLatestMonitoringResultForCovenant),
    mergeMap(action => 
      this.monitoringResultService.getLatestMonitoringResultForCovenant(action.covenantId).pipe(
        map(monitoringResults => MonitoringResultActions.loadLatestMonitoringResultForCovenantSuccess({ monitoringResults })),
        catchError(error => of(MonitoringResultActions.loadLatestMonitoringResultForCovenantFailure({ error })))
      )
    )
  ));

  createMonitoringResult$ = createEffect(() => this.actions$.pipe(
    ofType(MonitoringResultActions.createMonitoringResult),
    mergeMap(action => 
      this.monitoringResultService.createMonitoringResult(action.monitoringResult).pipe(
        map(monitoringResult => MonitoringResultActions.createMonitoringResultSuccess({ monitoringResult })),
        catchError(error => of(MonitoringResultActions.createMonitoringResultFailure({ error })))
      )
    )
  ));

  updateMonitoringResult$ = createEffect(() => this.actions$.pipe(
    ofType(MonitoringResultActions.updateMonitoringResult),
    mergeMap(action => 
      this.monitoringResultService.updateMonitoringResult(action.id, action.monitoringResult).pipe(
        map(monitoringResult => MonitoringResultActions.updateMonitoringResultSuccess({ monitoringResult })),
        catchError(error => of(MonitoringResultActions.updateMonitoringResultFailure({ error })))
      )
    )
  ));

  deleteMonitoringResult$ = createEffect(() => this.actions$.pipe(
    ofType(MonitoringResultActions.deleteMonitoringResult),
    mergeMap(action => 
      this.monitoringResultService.deleteMonitoringResult(action.id).pipe(
        map(() => MonitoringResultActions.deleteMonitoringResultSuccess({ id: action.id })),
        catchError(error => of(MonitoringResultActions.deleteMonitoringResultFailure({ error })))
      )
    )
  ));

  constructor(
    private actions$: Actions,
    private monitoringResultService: MonitoringResultService
  ) {}
}
