import { createAction, props } from '@ngrx/store';
import { AuthRequest, AuthResponse } from '../../core/models/models';

// Login actions
export const login = createAction(
  '[Auth] Login',
  props<{ credentials: AuthRequest }>()
);

export const loginSuccess = createAction(
  '[Auth] Login Success',
  props<{ response: AuthResponse }>()
);

export const loginFailure = createAction(
  '[Auth] Login Failure',
  props<{ error: any }>()
);

// Logout action
export const logout = createAction('[Auth] Logout');

// Check auth status
export const checkAuthStatus = createAction('[Auth] Check Auth Status');
