import { createReducer, on } from '@ngrx/store';
import * as AuthActions from './auth.actions';

export interface State {
  token: string | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: any;
}

export const initialState: State = {
  token: localStorage.getItem('auth_token'),
  isAuthenticated: !!localStorage.getItem('auth_token'),
  loading: false,
  error: null
};

export const reducer = createReducer(
  initialState,
  on(AuthActions.login, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(AuthActions.loginSuccess, (state, { response }) => {
    localStorage.setItem('auth_token', response.token);
    return {
      ...state,
      token: response.token,
      isAuthenticated: true,
      loading: false,
      error: null
    };
  }),
  on(AuthActions.loginFailure, (state, { error }) => ({
    ...state,
    token: null,
    isAuthenticated: false,
    loading: false,
    error
  })),
  on(AuthActions.logout, state => {
    localStorage.removeItem('auth_token');
    return {
      ...state,
      token: null,
      isAuthenticated: false,
      loading: false,
      error: null
    };
  }),
  on(AuthActions.checkAuthStatus, state => ({
    ...state,
    token: localStorage.getItem('auth_token'),
    isAuthenticated: !!localStorage.getItem('auth_token')
  }))
);
