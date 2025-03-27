import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { Contract } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class ContractService {
  private endpoint = 'contracts';
  
  constructor(private apiService: ApiService) { }

  getAllContracts(): Observable<Contract[]> {
    return this.apiService.get<Contract[]>(this.endpoint);
  }

  getContractById(id: number): Observable<Contract> {
    return this.apiService.getById<Contract>(this.endpoint, id);
  }

  getContractsByCountry(country: string): Observable<Contract[]> {
    return this.apiService.get<Contract[]>(`${this.endpoint}/country/${country}`);
  }

  getContractsByLegalEntity(legalEntity: string): Observable<Contract[]> {
    return this.apiService.get<Contract[]>(`${this.endpoint}/legal-entity/${legalEntity}`);
  }

  getContractsByStatus(status: string): Observable<Contract[]> {
    return this.apiService.get<Contract[]>(`${this.endpoint}/status/${status}`);
  }

  createContract(contract: Contract): Observable<Contract> {
    return this.apiService.post<Contract>(this.endpoint, contract);
  }

  updateContract(id: number, contract: Contract): Observable<Contract> {
    return this.apiService.put<Contract>(this.endpoint, id, contract);
  }

  deleteContract(id: number): Observable<void> {
    return this.apiService.delete(this.endpoint, id);
  }
}
