import { createReducer, on } from '@ngrx/store';
import { EntityState, EntityAdapter, createEntityAdapter } from '@ngrx/entity';
import { Covenant } from '../../core/models/models';
import * as CovenantActions from './covenant.actions';

export interface State extends EntityState<Covenant> {
  selectedCovenantId: number | null;
  loading: boolean;
  error: any;
}

export const adapter: EntityAdapter<Covenant> = createEntityAdapter<Covenant>();

export const initialState: State = adapter.getInitialState({
  selectedCovenantId: null,
  loading: false,
  error: null
});

export const reducer = createReducer(
  initialState,
  on(CovenantActions.loadCovenants, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(CovenantActions.loadCovenantsSuccess, (state, { covenants }) => 
    adapter.setAll(covenants, {
      ...state,
      loading: false
    })
  ),
  on(CovenantActions.loadCovenantsFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(CovenantActions.loadCovenant, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(CovenantActions.loadCovenantSuccess, (state, { covenant }) => 
    adapter.upsertOne(covenant, {
      ...state,
      selectedCovenantId: covenant.id || null,
      loading: false
    })
  ),
  on(CovenantActions.loadCovenantFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(CovenantActions.loadCovenantsByContract, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(CovenantActions.loadCovenantsByContractSuccess, (state, { covenants }) => 
    adapter.setAll(covenants, {
      ...state,
      loading: false
    })
  ),
  on(CovenantActions.loadCovenantsByContractFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(CovenantActions.loadCovenantsByStatus, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(CovenantActions.loadCovenantsByStatusSuccess, (state, { covenants }) => 
    adapter.setAll(covenants, {
      ...state,
      loading: false
    })
  ),
  on(CovenantActions.loadCovenantsByStatusFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(CovenantActions.loadCovenantsDueForMonitoring, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(CovenantActions.loadCovenantsDueForMonitoringSuccess, (state, { covenants }) => 
    adapter.setAll(covenants, {
      ...state,
      loading: false
    })
  ),
  on(CovenantActions.loadCovenantsDueForMonitoringFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(CovenantActions.createCovenant, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(CovenantActions.createCovenantSuccess, (state, { covenant }) => 
    adapter.addOne(covenant, {
      ...state,
      loading: false
    })
  ),
  on(CovenantActions.createCovenantFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(CovenantActions.updateCovenant, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(CovenantActions.updateCovenantSuccess, (state, { covenant }) => 
    adapter.updateOne(
      { id: covenant.id!, changes: covenant },
      {
        ...state,
        loading: false
      }
    )
  ),
  on(CovenantActions.updateCovenantFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(CovenantActions.deleteCovenant, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(CovenantActions.deleteCovenantSuccess, (state, { id }) => 
    adapter.removeOne(id, {
      ...state,
      loading: false
    })
  ),
  on(CovenantActions.deleteCovenantFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  }))
);

// Selectors
export const getSelectedCovenantId = (state: State) => state.selectedCovenantId;

// Get the selectors
const {
  selectIds,
  selectEntities,
  selectAll,
  selectTotal,
} = adapter.getSelectors();

// Select the array of covenant ids
export const selectCovenantIds = selectIds;

// Select the dictionary of covenant entities
export const selectCovenantEntities = selectEntities;

// Select all covenants
export const selectAllCovenants = selectAll;

// Select the total covenant count
export const selectCovenantTotal = selectTotal;
