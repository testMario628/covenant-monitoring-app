import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { Notification } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private endpoint = 'notifications';
  
  constructor(private apiService: ApiService) { }

  getCurrentUserNotifications(): Observable<Notification[]> {
    return this.apiService.get<Notification[]>(this.endpoint);
  }

  getUnreadNotifications(): Observable<Notification[]> {
    return this.apiService.get<Notification[]>(`${this.endpoint}/unread`);
  }

  countUnreadNotifications(): Observable<number> {
    return this.apiService.get<number>(`${this.endpoint}/count-unread`);
  }

  markAsRead(id: number): Observable<Notification> {
    return this.apiService.put<Notification>(`${this.endpoint}/${id}/mark-as-read`, id, {});
  }

  deleteNotification(id: number): Observable<void> {
    return this.apiService.delete(this.endpoint, id);
  }
}
