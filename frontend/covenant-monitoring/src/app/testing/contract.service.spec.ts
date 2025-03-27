import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ContractService } from '../core/services/contract.service';
import { environment } from '../../environments/environment';

describe('ContractService', () => {
  let service: ContractService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ContractService]
    });
    service = TestBed.inject(ContractService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve all contracts', () => {
    const mockContracts = [
      { id: 1, title: 'Finanziamento Progetto Eolico', legalEntity: 'Enel Green Power', country: 'Italia' },
      { id: 2, title: 'Finanziamento Impianto Solare', legalEntity: 'Enel Green Power', country: 'Spagna' }
    ];

    service.getContracts().subscribe(contracts => {
      expect(contracts.length).toBe(2);
      expect(contracts).toEqual(mockContracts);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/contracts`);
    expect(req.request.method).toBe('GET');
    req.flush(mockContracts);
  });

  it('should retrieve a contract by id', () => {
    const mockContract = { id: 1, title: 'Finanziamento Progetto Eolico', legalEntity: 'Enel Green Power', country: 'Italia' };

    service.getContract(1).subscribe(contract => {
      expect(contract).toEqual(mockContract);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/contracts/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockContract);
  });

  it('should create a new contract', () => {
    const newContract = { title: 'Nuovo Finanziamento', legalEntity: 'Enel S.p.A.', country: 'Italia' };
    const mockResponse = { id: 3, ...newContract };

    service.createContract(newContract).subscribe(contract => {
      expect(contract).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/contracts`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newContract);
    req.flush(mockResponse);
  });

  it('should update an existing contract', () => {
    const updatedContract = { id: 1, title: 'Finanziamento Progetto Eolico Aggiornato', legalEntity: 'Enel Green Power', country: 'Italia' };

    service.updateContract(updatedContract).subscribe(contract => {
      expect(contract).toEqual(updatedContract);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/contracts/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedContract);
    req.flush(updatedContract);
  });

  it('should delete a contract', () => {
    service.deleteContract(1).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/contracts/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });
});
