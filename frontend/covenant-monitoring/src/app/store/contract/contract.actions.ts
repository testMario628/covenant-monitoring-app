import { createAction, props } from '@ngrx/store';
import { Contract } from '../../core/models/models';

// Load contracts
export const loadContracts = createAction('[Contract] Load Contracts');

export const loadContractsSuccess = createAction(
  '[Contract] Load Contracts Success',
  props<{ contracts: Contract[] }>()
);

export const loadContractsFailure = createAction(
  '[Contract] Load Contracts Failure',
  props<{ error: any }>()
);

// Load contract by id
export const loadContract = createAction(
  '[Contract] Load Contract',
  props<{ id: number }>()
);

export const loadContractSuccess = createAction(
  '[Contract] Load Contract Success',
  props<{ contract: Contract }>()
);

export const loadContractFailure = createAction(
  '[Contract] Load Contract Failure',
  props<{ error: any }>()
);

// Create contract
export const createContract = createAction(
  '[Contract] Create Contract',
  props<{ contract: Contract }>()
);

export const createContractSuccess = createAction(
  '[Contract] Create Contract Success',
  props<{ contract: Contract }>()
);

export const createContractFailure = createAction(
  '[Contract] Create Contract Failure',
  props<{ error: any }>()
);

// Update contract
export const updateContract = createAction(
  '[Contract] Update Contract',
  props<{ id: number, contract: Contract }>()
);

export const updateContractSuccess = createAction(
  '[Contract] Update Contract Success',
  props<{ contract: Contract }>()
);

export const updateContractFailure = createAction(
  '[Contract] Update Contract Failure',
  props<{ error: any }>()
);

// Delete contract
export const deleteContract = createAction(
  '[Contract] Delete Contract',
  props<{ id: number }>()
);

export const deleteContractSuccess = createAction(
  '[Contract] Delete Contract Success',
  props<{ id: number }>()
);

export const deleteContractFailure = createAction(
  '[Contract] Delete Contract Failure',
  props<{ error: any }>()
);
