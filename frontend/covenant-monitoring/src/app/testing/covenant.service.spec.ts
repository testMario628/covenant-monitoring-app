import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CovenantService } from '../core/services/covenant.service';
import { environment } from '../../environments/environment';

describe('CovenantService', () => {
  let service: CovenantService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CovenantService]
    });
    service = TestBed.inject(CovenantService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should retrieve all covenants', () => {
    const mockCovenants = [
      { id: 1, name: 'Debt Service Coverage Ratio', type: 'DSCR', contractId: 1, threshold: 1.2, frequency: 'Trimestrale', nextMonitoringDate: new Date('2025-04-15') },
      { id: 2, name: 'Leverage Ratio', type: 'LeverageRatio', contractId: 2, threshold: 4.0, frequency: 'Semestrale', nextMonitoringDate: new Date('2025-05-10') }
    ];

    service.getCovenants().subscribe(covenants => {
      expect(covenants.length).toBe(2);
      expect(covenants).toEqual(mockCovenants);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/covenants`);
    expect(req.request.method).toBe('GET');
    req.flush(mockCovenants);
  });

  it('should retrieve a covenant by id', () => {
    const mockCovenant = { id: 1, name: 'Debt Service Coverage Ratio', type: 'DSCR', contractId: 1, threshold: 1.2, frequency: 'Trimestrale', nextMonitoringDate: new Date('2025-04-15') };

    service.getCovenant(1).subscribe(covenant => {
      expect(covenant).toEqual(mockCovenant);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/covenants/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockCovenant);
  });

  it('should create a new covenant', () => {
    const newCovenant = { name: 'Interest Coverage Ratio', type: 'ICR', contractId: 3, threshold: 1.5, frequency: 'Trimestrale', nextMonitoringDate: new Date('2025-04-20') };
    const mockResponse = { id: 3, ...newCovenant };

    service.createCovenant(newCovenant).subscribe(covenant => {
      expect(covenant).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/covenants`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newCovenant);
    req.flush(mockResponse);
  });

  it('should update an existing covenant', () => {
    const updatedCovenant = { id: 1, name: 'Debt Service Coverage Ratio', type: 'DSCR', contractId: 1, threshold: 1.3, frequency: 'Trimestrale', nextMonitoringDate: new Date('2025-04-15') };

    service.updateCovenant(updatedCovenant).subscribe(covenant => {
      expect(covenant).toEqual(updatedCovenant);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/covenants/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedCovenant);
    req.flush(updatedCovenant);
  });

  it('should delete a covenant', () => {
    service.deleteCovenant(1).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/covenants/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });

  it('should retrieve covenants by contract id', () => {
    const mockCovenants = [
      { id: 1, name: 'Debt Service Coverage Ratio', type: 'DSCR', contractId: 1, threshold: 1.2, frequency: 'Trimestrale', nextMonitoringDate: new Date('2025-04-15') },
      { id: 3, name: 'Working Capital Ratio', type: 'WorkingCapitalRatio', contractId: 1, threshold: 1.0, frequency: 'Annuale', nextMonitoringDate: new Date('2025-06-30') }
    ];

    service.getCovenantsByContract(1).subscribe(covenants => {
      expect(covenants.length).toBe(2);
      expect(covenants).toEqual(mockCovenants);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/contracts/1/covenants`);
    expect(req.request.method).toBe('GET');
    req.flush(mockCovenants);
  });
});
