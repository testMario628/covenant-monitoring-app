import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, mergeMap, switchMap } from 'rxjs/operators';
import { ContractService } from '../../core/services/contract.service';
import * as ContractActions from './contract.actions';

@Injectable()
export class ContractEffects {
  
  loadContracts$ = createEffect(() => this.actions$.pipe(
    ofType(ContractActions.loadContracts),
    switchMap(() => 
      this.contractService.getAllContracts().pipe(
        map(contracts => ContractActions.loadContractsSuccess({ contracts })),
        catchError(error => of(ContractActions.loadContractsFailure({ error })))
      )
    )
  ));

  loadContract$ = createEffect(() => this.actions$.pipe(
    ofType(ContractActions.loadContract),
    mergeMap(action => 
      this.contractService.getContractById(action.id).pipe(
        map(contract => ContractActions.loadContractSuccess({ contract })),
        catchError(error => of(ContractActions.loadContractFailure({ error })))
      )
    )
  ));

  createContract$ = createEffect(() => this.actions$.pipe(
    ofType(ContractActions.createContract),
    mergeMap(action => 
      this.contractService.createContract(action.contract).pipe(
        map(contract => ContractActions.createContractSuccess({ contract })),
        catchError(error => of(ContractActions.createContractFailure({ error })))
      )
    )
  ));

  updateContract$ = createEffect(() => this.actions$.pipe(
    ofType(ContractActions.updateContract),
    mergeMap(action => 
      this.contractService.updateContract(action.id, action.contract).pipe(
        map(contract => ContractActions.updateContractSuccess({ contract })),
        catchError(error => of(ContractActions.updateContractFailure({ error })))
      )
    )
  ));

  deleteContract$ = createEffect(() => this.actions$.pipe(
    ofType(ContractActions.deleteContract),
    mergeMap(action => 
      this.contractService.deleteContract(action.id).pipe(
        map(() => ContractActions.deleteContractSuccess({ id: action.id })),
        catchError(error => of(ContractActions.deleteContractFailure({ error })))
      )
    )
  ));

  constructor(
    private actions$: Actions,
    private contractService: ContractService
  ) {}
}
