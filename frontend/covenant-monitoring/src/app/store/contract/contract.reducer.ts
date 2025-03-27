import { createReducer, on } from '@ngrx/store';
import { EntityState, EntityAdapter, createEntityAdapter } from '@ngrx/entity';
import { Contract } from '../../core/models/models';
import * as ContractActions from './contract.actions';

export interface State extends EntityState<Contract> {
  selectedContractId: number | null;
  loading: boolean;
  error: any;
}

export const adapter: EntityAdapter<Contract> = createEntityAdapter<Contract>();

export const initialState: State = adapter.getInitialState({
  selectedContractId: null,
  loading: false,
  error: null
});

export const reducer = createReducer(
  initialState,
  on(ContractActions.loadContracts, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(ContractActions.loadContractsSuccess, (state, { contracts }) => 
    adapter.setAll(contracts, {
      ...state,
      loading: false
    })
  ),
  on(ContractActions.loadContractsFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(ContractActions.loadContract, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(ContractActions.loadContractSuccess, (state, { contract }) => 
    adapter.upsertOne(contract, {
      ...state,
      selectedContractId: contract.id || null,
      loading: false
    })
  ),
  on(ContractActions.loadContractFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(ContractActions.createContract, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(ContractActions.createContractSuccess, (state, { contract }) => 
    adapter.addOne(contract, {
      ...state,
      loading: false
    })
  ),
  on(ContractActions.createContractFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(ContractActions.updateContract, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(ContractActions.updateContractSuccess, (state, { contract }) => 
    adapter.updateOne(
      { id: contract.id!, changes: contract },
      {
        ...state,
        loading: false
      }
    )
  ),
  on(ContractActions.updateContractFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(ContractActions.deleteContract, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(ContractActions.deleteContractSuccess, (state, { id }) => 
    adapter.removeOne(id, {
      ...state,
      loading: false
    })
  ),
  on(ContractActions.deleteContractFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  }))
);

// Selectors
export const getSelectedContractId = (state: State) => state.selectedContractId;

// Get the selectors
const {
  selectIds,
  selectEntities,
  selectAll,
  selectTotal,
} = adapter.getSelectors();

// Select the array of contract ids
export const selectContractIds = selectIds;

// Select the dictionary of contract entities
export const selectContractEntities = selectEntities;

// Select all contracts
export const selectAllContracts = selectAll;

// Select the total contract count
export const selectContractTotal = selectTotal;
