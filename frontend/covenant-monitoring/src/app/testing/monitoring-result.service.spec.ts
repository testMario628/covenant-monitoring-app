import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MonitoringResultService } from '../core/services/monitoring-result.service';
import { environment } from '../../environments/environment';

describe('MonitoringResultService', () => {
  let service: MonitoringResultService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [MonitoringResultService]
    });
    service = TestBed.inject(MonitoringResultService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve all monitoring results', () => {
    const mockResults = [
      { id: 1, covenantId: 1, monitoringDate: new Date('2025-01-15'), actualValue: 1.3, status: 'VERDE', notes: 'Conforme' },
      { id: 2, covenantId: 2, monitoringDate: new Date('2025-02-10'), actualValue: 4.5, status: 'ROSSO', notes: 'Non conforme' }
    ];

    service.getMonitoringResults().subscribe(results => {
      expect(results.length).toBe(2);
      expect(results).toEqual(mockResults);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/monitoring-results`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResults);
  });

  it('should retrieve a monitoring result by id', () => {
    const mockResult = { id: 1, covenantId: 1, monitoringDate: new Date('2025-01-15'), actualValue: 1.3, status: 'VERDE', notes: 'Conforme' };

    service.getMonitoringResult(1).subscribe(result => {
      expect(result).toEqual(mockResult);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/monitoring-results/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResult);
  });

  it('should create a new monitoring result', () => {
    const newResult = { covenantId: 3, monitoringDate: new Date('2025-03-20'), actualValue: 1.6, status: 'VERDE', notes: 'Conforme' };
    const mockResponse = { id: 3, ...newResult };

    service.createMonitoringResult(newResult).subscribe(result => {
      expect(result).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/monitoring-results`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newResult);
    req.flush(mockResponse);
  });

  it('should update an existing monitoring result', () => {
    const updatedResult = { id: 1, covenantId: 1, monitoringDate: new Date('2025-01-15'), actualValue: 1.2, status: 'GIALLO', notes: 'Aggiornato' };

    service.updateMonitoringResult(updatedResult).subscribe(result => {
      expect(result).toEqual(updatedResult);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/monitoring-results/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedResult);
    req.flush(updatedResult);
  });

  it('should delete a monitoring result', () => {
    service.deleteMonitoringResult(1).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/monitoring-results/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });

  it('should retrieve monitoring results by covenant id', () => {
    const mockResults = [
      { id: 1, covenantId: 1, monitoringDate: new Date('2025-01-15'), actualValue: 1.3, status: 'VERDE', notes: 'Conforme' },
      { id: 3, covenantId: 1, monitoringDate: new Date('2024-10-15'), actualValue: 1.1, status: 'GIALLO', notes: 'Attenzione' }
    ];

    service.getMonitoringResultsByCovenant(1).subscribe(results => {
      expect(results.length).toBe(2);
      expect(results).toEqual(mockResults);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/covenants/1/monitoring-results`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResults);
  });
});
