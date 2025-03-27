import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../../store';
import * as CovenantActions from '../../store/covenant/covenant.actions';
import { Covenant } from '../../core/models/models';
import { Observable } from 'rxjs';
import { ExcelExportService } from '../../core/services/excel-export.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  // Dati per il sommario degli stati dei covenant per country
  countryStatusSummary: any[] = [];
  
  // Dati per il sommario degli stati dei covenant per compagnia
  companyStatusSummary: any[] = [];
  
  // Covenant che richiedono monitoraggio immediato
  urgentCovenants$: Observable<Covenant[]>;
  
  // Colori per gli stati dei covenant
  statusColors = {
    'VERDE': '#00843D',
    'GIALLO': '#FFD100',
    'ROSSO': '#FF3300'
  };
  
  // Statistiche generali
  totalCovenants = 0;
  verdeCovenants = 0;
  gialloCovenants = 0;
  rossoCovenants = 0;
  
  constructor(
    private store: Store<AppState>,
    private excelExportService: ExcelExportService
  ) { }

  ngOnInit(): void {
    // Carica i covenant
    this.store.dispatch(CovenantActions.loadCovenants());
    
    // Carica i covenant che richiedono monitoraggio immediato
    this.store.dispatch(CovenantActions.loadCovenantsDueForMonitoring());
    
    // Simula dati per il sommario degli stati dei covenant per country
    this.countryStatusSummary = [
      { country: 'Italia', verde: 45, giallo: 12, rosso: 5 },
      { country: 'Spagna', verde: 32, giallo: 8, rosso: 3 },
      { country: 'Romania', verde: 18, giallo: 6, rosso: 2 },
      { country: 'Brasile', verde: 27, giallo: 9, rosso: 4 },
      { country: 'Cile', verde: 15, giallo: 5, rosso: 1 }
    ];
    
    // Simula dati per il sommario degli stati dei covenant per compagnia
    this.companyStatusSummary = [
      { company: 'Enel Green Power', verde: 38, giallo: 10, rosso: 3 },
      { company: 'Enel Distribuzione', verde: 42, giallo: 15, rosso: 6 },
      { company: 'Endesa', verde: 29, giallo: 7, rosso: 2 },
      { company: 'Enel X', verde: 18, giallo: 5, rosso: 1 },
      { company: 'Enel Energia', verde: 25, giallo: 8, rosso: 3 }
    ];
    
    // Calcola le statistiche generali
    this.calculateStatistics();
  }
  
  calculateStatistics(): void {
    // Calcola le statistiche generali dai dati di sommario
    this.countryStatusSummary.forEach(country => {
      this.verdeCovenants += country.verde;
      this.gialloCovenants += country.giallo;
      this.rossoCovenants += country.rosso;
    });
    
    this.totalCovenants = this.verdeCovenants + this.gialloCovenants + this.rossoCovenants;
  }
  
  exportToExcel(data: any[], filename: string): void {
    // Prepara le intestazioni per l'export Excel
    const headers = [
      { key: 'entity', header: 'Entità' },
      { key: 'verde', header: 'Verde' },
      { key: 'giallo', header: 'Giallo' },
      { key: 'rosso', header: 'Rosso' },
      { key: 'total', header: 'Totale' }
    ];
    
    // Prepara i dati per l'export Excel
    const exportData = data.map(item => {
      const entityName = item.country || item.company;
      const total = item.verde + item.giallo + item.rosso;
      return {
        entity: entityName,
        verde: item.verde,
        giallo: item.giallo,
        rosso: item.rosso,
        total: total
      };
    });
    
    // Esporta i dati in Excel
    this.excelExportService.exportToExcel(exportData, headers, filename);
  }
}
