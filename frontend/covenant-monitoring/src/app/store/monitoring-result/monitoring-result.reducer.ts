import { createReducer, on } from '@ngrx/store';
import { EntityState, EntityAdapter, createEntityAdapter } from '@ngrx/entity';
import { MonitoringResult } from '../../core/models/models';
import * as MonitoringResultActions from './monitoring-result.actions';

export interface State extends EntityState<MonitoringResult> {
  selectedMonitoringResultId: number | null;
  loading: boolean;
  error: any;
}

export const adapter: EntityAdapter<MonitoringResult> = createEntityAdapter<MonitoringResult>();

export const initialState: State = adapter.getInitialState({
  selectedMonitoringResultId: null,
  loading: false,
  error: null
});

export const reducer = createReducer(
  initialState,
  on(MonitoringResultActions.loadMonitoringResults, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(MonitoringResultActions.loadMonitoringResultsSuccess, (state, { monitoringResults }) => 
    adapter.setAll(monitoringResults, {
      ...state,
      loading: false
    })
  ),
  on(MonitoringResultActions.loadMonitoringResultsFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(MonitoringResultActions.loadMonitoringResult, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(MonitoringResultActions.loadMonitoringResultSuccess, (state, { monitoringResult }) => 
    adapter.upsertOne(monitoringResult, {
      ...state,
      selectedMonitoringResultId: monitoringResult.id || null,
      loading: false
    })
  ),
  on(MonitoringResultActions.loadMonitoringResultFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(MonitoringResultActions.loadMonitoringResultsByCovenant, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(MonitoringResultActions.loadMonitoringResultsByCovenantSuccess, (state, { monitoringResults }) => 
    adapter.setAll(monitoringResults, {
      ...state,
      loading: false
    })
  ),
  on(MonitoringResultActions.loadMonitoringResultsByCovenantFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(MonitoringResultActions.loadLatestMonitoringResultForCovenant, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(MonitoringResultActions.loadLatestMonitoringResultForCovenantSuccess, (state, { monitoringResults }) => 
    adapter.setAll(monitoringResults, {
      ...state,
      loading: false
    })
  ),
  on(MonitoringResultActions.loadLatestMonitoringResultForCovenantFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(MonitoringResultActions.createMonitoringResult, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(MonitoringResultActions.createMonitoringResultSuccess, (state, { monitoringResult }) => 
    adapter.addOne(monitoringResult, {
      ...state,
      loading: false
    })
  ),
  on(MonitoringResultActions.createMonitoringResultFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(MonitoringResultActions.updateMonitoringResult, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(MonitoringResultActions.updateMonitoringResultSuccess, (state, { monitoringResult }) => 
    adapter.updateOne(
      { id: monitoringResult.id!, changes: monitoringResult },
      {
        ...state,
        loading: false
      }
    )
  ),
  on(MonitoringResultActions.updateMonitoringResultFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(MonitoringResultActions.deleteMonitoringResult, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(MonitoringResultActions.deleteMonitoringResultSuccess, (state, { id }) => 
    adapter.removeOne(id, {
      ...state,
      loading: false
    })
  ),
  on(MonitoringResultActions.deleteMonitoringResultFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  }))
);

// Selectors
export const getSelectedMonitoringResultId = (state: State) => state.selectedMonitoringResultId;

// Get the selectors
const {
  selectIds,
  selectEntities,
  selectAll,
  selectTotal,
} = adapter.getSelectors();

// Select the array of monitoring result ids
export const selectMonitoringResultIds = selectIds;

// Select the dictionary of monitoring result entities
export const selectMonitoringResultEntities = selectEntities;

// Select all monitoring results
export const selectAllMonitoringResults = selectAll;

// Select the total monitoring result count
export const selectMonitoringResultTotal = selectTotal;
