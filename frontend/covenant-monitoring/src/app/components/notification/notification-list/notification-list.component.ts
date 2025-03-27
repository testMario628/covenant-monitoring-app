import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../../store';
import * as NotificationActions from '../../store/notification/notification.actions';
import { Notification } from '../../core/models/models';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-notification-list',
  templateUrl: './notification-list.component.html',
  styleUrls: ['./notification-list.component.scss']
})
export class NotificationListComponent implements OnInit {
  notifications$: Observable<Notification[]>;
  loading$: Observable<boolean>;
  error$: Observable<any>;
  unreadCount$: Observable<number>;
  
  constructor(
    private store: Store<AppState>
  ) { }

  ngOnInit(): void {
    // Carica le notifiche
    this.store.dispatch(NotificationActions.loadNotifications());
    
    // Conta le notifiche non lette
    this.store.dispatch(NotificationActions.countUnreadNotifications());
    
    // Sottoscrivi agli osservabili dallo store
    this.notifications$ = this.store.select(state => state.notifications.entities);
    this.loading$ = this.store.select(state => state.notifications.loading);
    this.error$ = this.store.select(state => state.notifications.error);
    this.unreadCount$ = this.store.select(state => state.notifications.unreadCount);
  }
  
  markAsRead(id: number): void {
    this.store.dispatch(NotificationActions.markNotificationAsRead({ id }));
  }
  
  deleteNotification(id: number): void {
    if (confirm('Sei sicuro di voler eliminare questa notifica?')) {
      this.store.dispatch(NotificationActions.deleteNotification({ id }));
    }
  }
  
  markAllAsRead(): void {
    // In un'implementazione reale, qui si invierebbe un'azione per segnare tutte le notifiche come lette
    console.log('Marking all notifications as read');
  }
  
  getNotificationIcon(type: string): string {
    switch (type) {
      case 'WARNING':
        return 'warning';
      case 'ALERT':
        return 'error';
      case 'INFO':
        return 'info';
      case 'SUCCESS':
        return 'check_circle';
      default:
        return 'notifications';
    }
  }
  
  getNotificationClass(type: string): string {
    switch (type) {
      case 'WARNING':
        return 'notification-warning';
      case 'ALERT':
        return 'notification-alert';
      case 'INFO':
        return 'notification-info';
      case 'SUCCESS':
        return 'notification-success';
      default:
        return '';
    }
  }
}
