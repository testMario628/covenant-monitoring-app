import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { MonitoringResult, Covenant } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class MonitoringResultService {
  private endpoint = 'monitoring-results';
  
  constructor(private apiService: ApiService) { }

  getAllMonitoringResults(): Observable<MonitoringResult[]> {
    return this.apiService.get<MonitoringResult[]>(this.endpoint);
  }

  getMonitoringResultById(id: number): Observable<MonitoringResult> {
    return this.apiService.getById<MonitoringResult>(this.endpoint, id);
  }

  getMonitoringResultsByCovenant(covenantId: number): Observable<MonitoringResult[]> {
    return this.apiService.get<MonitoringResult[]>(`${this.endpoint}/covenant/${covenantId}`);
  }

  getMonitoringResultsByStatus(status: string): Observable<MonitoringResult[]> {
    return this.apiService.get<MonitoringResult[]>(`${this.endpoint}/status/${status}`);
  }

  getMonitoringResultsByDateRange(startDate: string, endDate: string): Observable<MonitoringResult[]> {
    return this.apiService.get<MonitoringResult[]>(`${this.endpoint}/date-range?startDate=${startDate}&endDate=${endDate}`);
  }

  getLatestMonitoringResultForCovenant(covenantId: number): Observable<MonitoringResult[]> {
    return this.apiService.get<MonitoringResult[]>(`${this.endpoint}/latest/${covenantId}`);
  }

  createMonitoringResult(monitoringResult: MonitoringResult): Observable<MonitoringResult> {
    return this.apiService.post<MonitoringResult>(this.endpoint, monitoringResult);
  }

  updateMonitoringResult(id: number, monitoringResult: MonitoringResult): Observable<MonitoringResult> {
    return this.apiService.put<MonitoringResult>(this.endpoint, id, monitoringResult);
  }

  deleteMonitoringResult(id: number): Observable<void> {
    return this.apiService.delete(this.endpoint, id);
  }
}
