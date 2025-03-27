import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';
import { AuthService } from '../../core/services/auth.service';
import * as AuthActions from './auth.actions';
import { Router } from '@angular/router';

@Injectable()
export class AuthEffects {
  
  login$ = createEffect(() => this.actions$.pipe(
    ofType(AuthActions.login),
    switchMap(action => 
      this.authService.login(action.credentials).pipe(
        map(response => AuthActions.loginSuccess({ response })),
        catchError(error => of(AuthActions.loginFailure({ error })))
      )
    )
  ));

  loginSuccess$ = createEffect(() => this.actions$.pipe(
    ofType(AuthActions.loginSuccess),
    tap(({ response }) => {
      this.authService.saveToken(response.token);
      this.router.navigate(['/dashboard']);
    })
  ), { dispatch: false });

  logout$ = createEffect(() => this.actions$.pipe(
    ofType(AuthActions.logout),
    tap(() => {
      this.authService.logout();
      this.router.navigate(['/login']);
    })
  ), { dispatch: false });

  constructor(
    private actions$: Actions,
    private authService: AuthService,
    private router: Router
  ) {}
}
