import { Component, OnInit, ViewChild } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../../store';
import * as ContractActions from '../../store/contract/contract.actions';
import { Contract } from '../../core/models/models';
import { Observable } from 'rxjs';
import { ExcelExportService } from '../../core/services/excel-export.service';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-contract-list',
  templateUrl: './contract-list.component.html',
  styleUrls: ['./contract-list.component.scss']
})
export class ContractListComponent implements OnInit {
  contracts$: Observable<Contract[]>;
  loading$: Observable<boolean>;
  error$: Observable<any>;
  
  filterForm: FormGroup;
  
  // Opzioni per i filtri
  countries = ['Italia', 'Spagna', 'Romania', 'Brasile', 'Cile'];
  statuses = ['Attivo', 'Scaduto', 'In Revisione'];
  
  // Configurazione per la vista personalizzata
  savedViews = [
    { id: 1, name: 'Contratti Attivi', filters: { status: 'Attivo' } },
    { id: 2, name: 'Contratti Italia', filters: { country: 'Italia' } },
    { id: 3, name: 'Contratti Scaduti', filters: { status: 'Scaduto' } }
  ];
  
  selectedView: any = null;
  
  constructor(
    private store: Store<AppState>,
    private fb: FormBuilder,
    private excelExportService: ExcelExportService
  ) {
    this.createFilterForm();
  }

  ngOnInit(): void {
    // Carica i contratti
    this.store.dispatch(ContractActions.loadContracts());
    
    // Sottoscrivi agli osservabili dallo store
    this.contracts$ = this.store.select(state => state.contracts.entities);
    this.loading$ = this.store.select(state => state.contracts.loading);
    this.error$ = this.store.select(state => state.contracts.error);
  }
  
  createFilterForm(): void {
    this.filterForm = this.fb.group({
      contractId: [''],
      title: [''],
      legalEntity: [''],
      country: [''],
      status: [''],
      startDateFrom: [''],
      startDateTo: [''],
      endDateFrom: [''],
      endDateTo: ['']
    });
  }
  
  applyFilters(): void {
    console.log('Applying filters:', this.filterForm.value);
    // In un'implementazione reale, qui si filtrerebbero i contratti in base ai criteri
    // o si invierebbe una richiesta al backend con i parametri di filtro
  }
  
  resetFilters(): void {
    this.filterForm.reset();
    this.selectedView = null;
    this.applyFilters();
  }
  
  loadSavedView(view: any): void {
    this.selectedView = view;
    this.filterForm.reset();
    
    // Applica i filtri della vista salvata
    if (view.filters) {
      Object.keys(view.filters).forEach(key => {
        if (this.filterForm.get(key)) {
          this.filterForm.get(key)?.setValue(view.filters[key]);
        }
      });
    }
    
    this.applyFilters();
  }
  
  saveCurrentView(): void {
    // In un'implementazione reale, qui si salverebbe la vista corrente
    console.log('Saving current view with filters:', this.filterForm.value);
    // Mostrerebbe un dialog per inserire il nome della vista e poi la salverebbe
  }
  
  exportToExcel(contracts: Contract[]): void {
    // Prepara le intestazioni per l'export Excel
    const headers = [
      { key: 'contractId', header: 'ID Contratto' },
      { key: 'title', header: 'Titolo' },
      { key: 'legalEntity', header: 'Entità Legale' },
      { key: 'country', header: 'Paese' },
      { key: 'startDate', header: 'Data Inizio' },
      { key: 'endDate', header: 'Data Fine' },
      { key: 'status', header: 'Stato' }
    ];
    
    // Esporta i dati in Excel
    this.excelExportService.exportToExcel(contracts, headers, 'contratti');
  }
  
  viewContract(contract: Contract): void {
    // Naviga alla pagina di dettaglio del contratto
    console.log('Viewing contract:', contract);
  }
  
  editContract(contract: Contract): void {
    // Naviga alla pagina di modifica del contratto
    console.log('Editing contract:', contract);
  }
  
  deleteContract(id: number): void {
    if (confirm('Sei sicuro di voler eliminare questo contratto?')) {
      this.store.dispatch(ContractActions.deleteContract({ id }));
    }
  }
}
