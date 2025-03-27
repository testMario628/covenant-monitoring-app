import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { NotificationService } from '../core/services/notification.service';
import { environment } from '../../environments/environment';

describe('NotificationService', () => {
  let service: NotificationService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [NotificationService]
    });
    service = TestBed.inject(NotificationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve all notifications', () => {
    const mockNotifications = [
      { id: 1, title: 'Covenant in stato critico', message: 'Il covenant "Leverage Ratio" è passato in stato ROSSO', type: 'WARNING', read: false, createdAt: new Date('2025-03-25T10:00:00') },
      { id: 2, title: 'Monitoraggio in scadenza', message: 'Il covenant "DSCR" deve essere monitorato entro 7 giorni', type: 'INFO', read: true, createdAt: new Date('2025-03-24T14:30:00') }
    ];

    service.getNotifications().subscribe(notifications => {
      expect(notifications.length).toBe(2);
      expect(notifications).toEqual(mockNotifications);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/notifications`);
    expect(req.request.method).toBe('GET');
    req.flush(mockNotifications);
  });

  it('should retrieve unread notifications count', () => {
    const mockCount = { count: 3 };

    service.getUnreadCount().subscribe(result => {
      expect(result).toEqual(mockCount);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/notifications/unread/count`);
    expect(req.request.method).toBe('GET');
    req.flush(mockCount);
  });

  it('should mark a notification as read', () => {
    const mockResponse = { id: 1, read: true };

    service.markAsRead(1).subscribe(result => {
      expect(result).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/notifications/1/read`);
    expect(req.request.method).toBe('PUT');
    req.flush(mockResponse);
  });

  it('should mark all notifications as read', () => {
    const mockResponse = { count: 5 };

    service.markAllAsRead().subscribe(result => {
      expect(result).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/notifications/read-all`);
    expect(req.request.method).toBe('PUT');
    req.flush(mockResponse);
  });

  it('should delete a notification', () => {
    service.deleteNotification(1).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/notifications/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });
});
