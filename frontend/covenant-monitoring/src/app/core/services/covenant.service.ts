import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { Covenant, Contract } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class CovenantService {
  private endpoint = 'covenants';
  
  constructor(private apiService: ApiService) { }

  getAllCovenants(): Observable<Covenant[]> {
    return this.apiService.get<Covenant[]>(this.endpoint);
  }

  getCovenantById(id: number): Observable<Covenant> {
    return this.apiService.getById<Covenant>(this.endpoint, id);
  }

  getCovenantsByContract(contractId: number): Observable<Covenant[]> {
    return this.apiService.get<Covenant[]>(`${this.endpoint}/contract/${contractId}`);
  }

  getCovenantsByStatus(status: string): Observable<Covenant[]> {
    return this.apiService.get<Covenant[]>(`${this.endpoint}/status/${status}`);
  }

  getCovenantsDueForMonitoring(): Observable<Covenant[]> {
    return this.apiService.get<Covenant[]>(`${this.endpoint}/due-for-monitoring`);
  }

  createCovenant(covenant: Covenant): Observable<Covenant> {
    return this.apiService.post<Covenant>(this.endpoint, covenant);
  }

  updateCovenant(id: number, covenant: Covenant): Observable<Covenant> {
    return this.apiService.put<Covenant>(this.endpoint, id, covenant);
  }

  deleteCovenant(id: number): Observable<void> {
    return this.apiService.delete(this.endpoint, id);
  }
}
