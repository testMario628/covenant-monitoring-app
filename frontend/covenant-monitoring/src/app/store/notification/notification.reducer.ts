import { createReducer, on } from '@ngrx/store';
import { EntityState, EntityAdapter, createEntityAdapter } from '@ngrx/entity';
import { Notification } from '../../core/models/models';
import * as NotificationActions from './notification.actions';

export interface State extends EntityState<Notification> {
  unreadCount: number;
  loading: boolean;
  error: any;
}

export const adapter: EntityAdapter<Notification> = createEntityAdapter<Notification>();

export const initialState: State = adapter.getInitialState({
  unreadCount: 0,
  loading: false,
  error: null
});

export const reducer = createReducer(
  initialState,
  on(NotificationActions.loadNotifications, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(NotificationActions.loadNotificationsSuccess, (state, { notifications }) => 
    adapter.setAll(notifications, {
      ...state,
      loading: false
    })
  ),
  on(NotificationActions.loadNotificationsFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(NotificationActions.loadUnreadNotifications, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(NotificationActions.loadUnreadNotificationsSuccess, (state, { notifications }) => 
    adapter.setAll(notifications, {
      ...state,
      loading: false
    })
  ),
  on(NotificationActions.loadUnreadNotificationsFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(NotificationActions.countUnreadNotifications, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(NotificationActions.countUnreadNotificationsSuccess, (state, { count }) => ({
    ...state,
    unreadCount: count,
    loading: false
  })),
  on(NotificationActions.countUnreadNotificationsFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(NotificationActions.markNotificationAsRead, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(NotificationActions.markNotificationAsReadSuccess, (state, { notification }) => 
    adapter.updateOne(
      { id: notification.id!, changes: notification },
      {
        ...state,
        unreadCount: state.unreadCount > 0 ? state.unreadCount - 1 : 0,
        loading: false
      }
    )
  ),
  on(NotificationActions.markNotificationAsReadFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  })),
  on(NotificationActions.deleteNotification, state => ({
    ...state,
    loading: true,
    error: null
  })),
  on(NotificationActions.deleteNotificationSuccess, (state, { id }) => {
    const entity = state.entities[id];
    const unreadCount = entity && !entity.isRead ? state.unreadCount - 1 : state.unreadCount;
    
    return adapter.removeOne(id, {
      ...state,
      unreadCount: unreadCount > 0 ? unreadCount : 0,
      loading: false
    });
  }),
  on(NotificationActions.deleteNotificationFailure, (state, { error }) => ({
    ...state,
    loading: false,
    error
  }))
);

// Get the selectors
const {
  selectIds,
  selectEntities,
  selectAll,
  selectTotal,
} = adapter.getSelectors();

// Select the array of notification ids
export const selectNotificationIds = selectIds;

// Select the dictionary of notification entities
export const selectNotificationEntities = selectEntities;

// Select all notifications
export const selectAllNotifications = selectAll;

// Select the total notification count
export const selectNotificationTotal = selectTotal;

// Select the unread notification count
export const selectUnreadCount = (state: State) => state.unreadCount;
