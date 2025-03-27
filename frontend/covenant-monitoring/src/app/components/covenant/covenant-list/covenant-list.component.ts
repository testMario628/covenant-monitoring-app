import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../../store';
import * as CovenantActions from '../../store/covenant/covenant.actions';
import { Covenant } from '../../core/models/models';
import { Observable } from 'rxjs';
import { ExcelExportService } from '../../core/services/excel-export.service';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-covenant-list',
  templateUrl: './covenant-list.component.html',
  styleUrls: ['./covenant-list.component.scss']
})
export class CovenantListComponent implements OnInit {
  covenants$: Observable<Covenant[]>;
  loading$: Observable<boolean>;
  error$: Observable<any>;
  
  filterForm: FormGroup;
  
  // Opzioni per i filtri
  covenantTypes = ['DSCR', 'LeverageRatio', 'ICR', 'DebtToEquity', 'WorkingCapitalRatio'];
  statuses = ['VERDE', 'GIALLO', 'ROSSO'];
  frequencies = ['Mensile', 'Trimestrale', 'Semestrale', 'Annuale'];
  
  // Configurazione per la vista personalizzata
  savedViews = [
    { id: 1, name: 'Covenant Critici', filters: { status: 'ROSSO' } },
    { id: 2, name: 'Covenant in Scadenza', filters: { dueInDays: '30' } },
    { id: 3, name: 'DSCR', filters: { type: 'DSCR' } }
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
    // Carica i covenant
    this.store.dispatch(CovenantActions.loadCovenants());
    
    // Sottoscrivi agli osservabili dallo store
    this.covenants$ = this.store.select(state => state.covenants.entities);
    this.loading$ = this.store.select(state => state.covenants.loading);
    this.error$ = this.store.select(state => state.covenants.error);
  }
  
  createFilterForm(): void {
    this.filterForm = this.fb.group({
      covenantId: [''],
      name: [''],
      type: [''],
      contractId: [''],
      status: [''],
      frequency: [''],
      dueInDays: ['']
    });
  }
  
  applyFilters(): void {
    console.log('Applying filters:', this.filterForm.value);
    // In un'implementazione reale, qui si filtrerebbero i covenant in base ai criteri
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
  
  exportToExcel(covenants: Covenant[]): void {
    // Prepara le intestazioni per l'export Excel
    const headers = [
      { key: 'id', header: 'ID' },
      { key: 'name', header: 'Nome' },
      { key: 'type', header: 'Tipo' },
      { key: 'contractId', header: 'ID Contratto' },
      { key: 'threshold', header: 'Threshold' },
      { key: 'frequency', header: 'Frequenza' },
      { key: 'nextMonitoringDate', header: 'Prossimo Monitoraggio' },
      { key: 'status', header: 'Stato' }
    ];
    
    // Esporta i dati in Excel
    this.excelExportService.exportToExcel(covenants, headers, 'covenant');
  }
  
  viewCovenant(covenant: Covenant): void {
    // Naviga alla pagina di dettaglio del covenant
    console.log('Viewing covenant:', covenant);
  }
  
  editCovenant(covenant: Covenant): void {
    // Naviga alla pagina di modifica del covenant
    console.log('Editing covenant:', covenant);
  }
  
  deleteCovenant(id: number): void {
    if (confirm('Sei sicuro di voler eliminare questo covenant?')) {
      this.store.dispatch(CovenantActions.deleteCovenant({ id }));
    }
  }
  
  monitorCovenant(covenant: Covenant): void {
    // Naviga alla pagina di monitoraggio del covenant
    console.log('Monitoring covenant:', covenant);
  }
  
  getStatusClass(status: string): string {
    switch (status) {
      case 'VERDE':
        return 'bg-success';
      case 'GIALLO':
        return 'bg-warning';
      case 'ROSSO':
        return 'bg-danger';
      default:
        return '';
    }
  }
}
