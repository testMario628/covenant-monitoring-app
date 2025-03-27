import { createAction, props } from '@ngrx/store';
import { MonitoringResult } from '../../core/models/models';

// Load monitoring results
export const loadMonitoringResults = createAction('[MonitoringResult] Load MonitoringResults');

export const loadMonitoringResultsSuccess = createAction(
  '[MonitoringResult] Load MonitoringResults Success',
  props<{ monitoringResults: MonitoringResult[] }>()
);

export const loadMonitoringResultsFailure = createAction(
  '[MonitoringResult] Load MonitoringResults Failure',
  props<{ error: any }>()
);

// Load monitoring result by id
export const loadMonitoringResult = createAction(
  '[MonitoringResult] Load MonitoringResult',
  props<{ id: number }>()
);

export const loadMonitoringResultSuccess = createAction(
  '[MonitoringResult] Load MonitoringResult Success',
  props<{ monitoringResult: MonitoringResult }>()
);

export const loadMonitoringResultFailure = createAction(
  '[MonitoringResult] Load MonitoringResult Failure',
  props<{ error: any }>()
);

// Load monitoring results by covenant
export const loadMonitoringResultsByCovenant = createAction(
  '[MonitoringResult] Load MonitoringResults By Covenant',
  props<{ covenantId: number }>()
);

export const loadMonitoringResultsByCovenantSuccess = createAction(
  '[MonitoringResult] Load MonitoringResults By Covenant Success',
  props<{ monitoringResults: MonitoringResult[] }>()
);

export const loadMonitoringResultsByCovenantFailure = createAction(
  '[MonitoringResult] Load MonitoringResults By Covenant Failure',
  props<{ error: any }>()
);

// Load latest monitoring result for covenant
export const loadLatestMonitoringResultForCovenant = createAction(
  '[MonitoringResult] Load Latest MonitoringResult For Covenant',
  props<{ covenantId: number }>()
);

export const loadLatestMonitoringResultForCovenantSuccess = createAction(
  '[MonitoringResult] Load Latest MonitoringResult For Covenant Success',
  props<{ monitoringResults: MonitoringResult[] }>()
);

export const loadLatestMonitoringResultForCovenantFailure = createAction(
  '[MonitoringResult] Load Latest MonitoringResult For Covenant Failure',
  props<{ error: any }>()
);

// Create monitoring result
export const createMonitoringResult = createAction(
  '[MonitoringResult] Create MonitoringResult',
  props<{ monitoringResult: MonitoringResult }>()
);

export const createMonitoringResultSuccess = createAction(
  '[MonitoringResult] Create MonitoringResult Success',
  props<{ monitoringResult: MonitoringResult }>()
);

export const createMonitoringResultFailure = createAction(
  '[MonitoringResult] Create MonitoringResult Failure',
  props<{ error: any }>()
);

// Update monitoring result
export const updateMonitoringResult = createAction(
  '[MonitoringResult] Update MonitoringResult',
  props<{ id: number, monitoringResult: MonitoringResult }>()
);

export const updateMonitoringResultSuccess = createAction(
  '[MonitoringResult] Update MonitoringResult Success',
  props<{ monitoringResult: MonitoringResult }>()
);

export const updateMonitoringResultFailure = createAction(
  '[MonitoringResult] Update MonitoringResult Failure',
  props<{ error: any }>()
);

// Delete monitoring result
export const deleteMonitoringResult = createAction(
  '[MonitoringResult] Delete MonitoringResult',
  props<{ id: number }>()
);

export const deleteMonitoringResultSuccess = createAction(
  '[MonitoringResult] Delete MonitoringResult Success',
  props<{ id: number }>()
);

export const deleteMonitoringResultFailure = createAction(
  '[MonitoringResult] Delete MonitoringResult Failure',
  props<{ error: any }>()
);
