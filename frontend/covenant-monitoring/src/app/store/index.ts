import { ActionReducerMap, MetaReducer } from '@ngrx/store';
import { environment } from '../../environments/environment';
import * as fromAuth from './auth/auth.reducer';
import * as fromContract from './contract/contract.reducer';
import * as fromCovenant from './covenant/covenant.reducer';
import * as fromMonitoringResult from './monitoring-result/monitoring-result.reducer';
import * as fromNotification from './notification/notification.reducer';

export interface AppState {
  auth: fromAuth.State;
  contracts: fromContract.State;
  covenants: fromCovenant.State;
  monitoringResults: fromMonitoringResult.State;
  notifications: fromNotification.State;
}

export const reducers: ActionReducerMap<AppState> = {
  auth: fromAuth.reducer,
  contracts: fromContract.reducer,
  covenants: fromCovenant.reducer,
  monitoringResults: fromMonitoringResult.reducer,
  notifications: fromNotification.reducer
};

export const metaReducers: MetaReducer<AppState>[] = !environment.production ? [] : [];
