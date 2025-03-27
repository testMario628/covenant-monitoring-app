import { createAction, props } from '@ngrx/store';
import { Notification } from '../../core/models/models';

// Load notifications
export const loadNotifications = createAction('[Notification] Load Notifications');

export const loadNotificationsSuccess = createAction(
  '[Notification] Load Notifications Success',
  props<{ notifications: Notification[] }>()
);

export const loadNotificationsFailure = createAction(
  '[Notification] Load Notifications Failure',
  props<{ error: any }>()
);

// Load unread notifications
export const loadUnreadNotifications = createAction('[Notification] Load Unread Notifications');

export const loadUnreadNotificationsSuccess = createAction(
  '[Notification] Load Unread Notifications Success',
  props<{ notifications: Notification[] }>()
);

export const loadUnreadNotificationsFailure = createAction(
  '[Notification] Load Unread Notifications Failure',
  props<{ error: any }>()
);

// Count unread notifications
export const countUnreadNotifications = createAction('[Notification] Count Unread Notifications');

export const countUnreadNotificationsSuccess = createAction(
  '[Notification] Count Unread Notifications Success',
  props<{ count: number }>()
);

export const countUnreadNotificationsFailure = createAction(
  '[Notification] Count Unread Notifications Failure',
  props<{ error: any }>()
);

// Mark notification as read
export const markNotificationAsRead = createAction(
  '[Notification] Mark Notification As Read',
  props<{ id: number }>()
);

export const markNotificationAsReadSuccess = createAction(
  '[Notification] Mark Notification As Read Success',
  props<{ notification: Notification }>()
);

export const markNotificationAsReadFailure = createAction(
  '[Notification] Mark Notification As Read Failure',
  props<{ error: any }>()
);

// Delete notification
export const deleteNotification = createAction(
  '[Notification] Delete Notification',
  props<{ id: number }>()
);

export const deleteNotificationSuccess = createAction(
  '[Notification] Delete Notification Success',
  props<{ id: number }>()
);

export const deleteNotificationFailure = createAction(
  '[Notification] Delete Notification Failure',
  props<{ error: any }>()
);
