import { createAction, props } from '@ngrx/store';
import { Covenant } from '../../core/models/models';

// Load covenants
export const loadCovenants = createAction('[Covenant] Load Covenants');

export const loadCovenantsSuccess = createAction(
  '[Covenant] Load Covenants Success',
  props<{ covenants: Covenant[] }>()
);

export const loadCovenantsFailure = createAction(
  '[Covenant] Load Covenants Failure',
  props<{ error: any }>()
);

// Load covenant by id
export const loadCovenant = createAction(
  '[Covenant] Load Covenant',
  props<{ id: number }>()
);

export const loadCovenantSuccess = createAction(
  '[Covenant] Load Covenant Success',
  props<{ covenant: Covenant }>()
);

export const loadCovenantFailure = createAction(
  '[Covenant] Load Covenant Failure',
  props<{ error: any }>()
);

// Load covenants by contract
export const loadCovenantsByContract = createAction(
  '[Covenant] Load Covenants By Contract',
  props<{ contractId: number }>()
);

export const loadCovenantsByContractSuccess = createAction(
  '[Covenant] Load Covenants By Contract Success',
  props<{ covenants: Covenant[] }>()
);

export const loadCovenantsByContractFailure = createAction(
  '[Covenant] Load Covenants By Contract Failure',
  props<{ error: any }>()
);

// Load covenants by status
export const loadCovenantsByStatus = createAction(
  '[Covenant] Load Covenants By Status',
  props<{ status: string }>()
);

export const loadCovenantsByStatusSuccess = createAction(
  '[Covenant] Load Covenants By Status Success',
  props<{ covenants: Covenant[] }>()
);

export const loadCovenantsByStatusFailure = createAction(
  '[Covenant] Load Covenants By Status Failure',
  props<{ error: any }>()
);

// Load covenants due for monitoring
export const loadCovenantsDueForMonitoring = createAction(
  '[Covenant] Load Covenants Due For Monitoring'
);

export const loadCovenantsDueForMonitoringSuccess = createAction(
  '[Covenant] Load Covenants Due For Monitoring Success',
  props<{ covenants: Covenant[] }>()
);

export const loadCovenantsDueForMonitoringFailure = createAction(
  '[Covenant] Load Covenants Due For Monitoring Failure',
  props<{ error: any }>()
);

// Create covenant
export const createCovenant = createAction(
  '[Covenant] Create Covenant',
  props<{ covenant: Covenant }>()
);

export const createCovenantSuccess = createAction(
  '[Covenant] Create Covenant Success',
  props<{ covenant: Covenant }>()
);

export const createCovenantFailure = createAction(
  '[Covenant] Create Covenant Failure',
  props<{ error: any }>()
);

// Update covenant
export const updateCovenant = createAction(
  '[Covenant] Update Covenant',
  props<{ id: number, covenant: Covenant }>()
);

export const updateCovenantSuccess = createAction(
  '[Covenant] Update Covenant Success',
  props<{ covenant: Covenant }>()
);

export const updateCovenantFailure = createAction(
  '[Covenant] Update Covenant Failure',
  props<{ error: any }>()
);

// Delete covenant
export const deleteCovenant = createAction(
  '[Covenant] Delete Covenant',
  props<{ id: number }>()
);

export const deleteCovenantSuccess = createAction(
  '[Covenant] Delete Covenant Success',
  props<{ id: number }>()
);

export const deleteCovenantFailure = createAction(
  '[Covenant] Delete Covenant Failure',
  props<{ error: any }>()
);
