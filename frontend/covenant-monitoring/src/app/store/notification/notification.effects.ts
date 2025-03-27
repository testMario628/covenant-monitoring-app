import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, mergeMap, switchMap } from 'rxjs/operators';
import { NotificationService } from '../../core/services/notification.service';
import * as NotificationActions from './notification.actions';

@Injectable()
export class NotificationEffects {
  
  loadNotifications$ = createEffect(() => this.actions$.pipe(
    ofType(NotificationActions.loadNotifications),
    switchMap(() => 
      this.notificationService.getCurrentUserNotifications().pipe(
        map(notifications => NotificationActions.loadNotificationsSuccess({ notifications })),
        catchError(error => of(NotificationActions.loadNotificationsFailure({ error })))
      )
    )
  ));

  loadUnreadNotifications$ = createEffect(() => this.actions$.pipe(
    ofType(NotificationActions.loadUnreadNotifications),
    switchMap(() => 
      this.notificationService.getUnreadNotifications().pipe(
        map(notifications => NotificationActions.loadUnreadNotificationsSuccess({ notifications })),
        catchError(error => of(NotificationActions.loadUnreadNotificationsFailure({ error })))
      )
    )
  ));

  countUnreadNotifications$ = createEffect(() => this.actions$.pipe(
    ofType(NotificationActions.countUnreadNotifications),
    switchMap(() => 
      this.notificationService.countUnreadNotifications().pipe(
        map(count => NotificationActions.countUnreadNotificationsSuccess({ count })),
        catchError(error => of(NotificationActions.countUnreadNotificationsFailure({ error })))
      )
    )
  ));

  markNotificationAsRead$ = createEffect(() => this.actions$.pipe(
    ofType(NotificationActions.markNotificationAsRead),
    mergeMap(action => 
      this.notificationService.markAsRead(action.id).pipe(
        map(notification => NotificationActions.markNotificationAsReadSuccess({ notification })),
        catchError(error => of(NotificationActions.markNotificationAsReadFailure({ error })))
      )
    )
  ));

  deleteNotification$ = createEffect(() => this.actions$.pipe(
    ofType(NotificationActions.deleteNotification),
    mergeMap(action => 
      this.notificationService.deleteNotification(action.id).pipe(
        map(() => NotificationActions.deleteNotificationSuccess({ id: action.id })),
        catchError(error => of(NotificationActions.deleteNotificationFailure({ error })))
      )
    )
  ));

  constructor(
    private actions$: Actions,
    private notificationService: NotificationService
  ) {}
}
