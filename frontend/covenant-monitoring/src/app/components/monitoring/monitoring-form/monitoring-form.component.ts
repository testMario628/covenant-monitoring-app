import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../../store';
import * as CovenantActions from '../../store/covenant/covenant.actions';
import { Covenant } from '../../core/models/models';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ExcelExportService } from '../../core/services/excel-export.service';

@Component({
  selector: 'app-monitoring-form',
  templateUrl: './monitoring-form.component.html',
  styleUrls: ['./monitoring-form.component.scss']
})
export class MonitoringFormComponent implements OnInit {
  monitoringForm: FormGroup;
  selectedCovenant: Covenant | null = null;
  covenants: Covenant[] = [];
  loading = false;
  submitted = false;
  
  // Opzioni per i campi select
  statusOptions = [
    { value: 'VERDE', label: 'Verde - Conforme' },
    { value: 'GIALLO', label: 'Giallo - Attenzione' },
    { value: 'ROSSO', label: 'Rosso - Critico' }
  ];
  
  // Regole di validazione per diversi tipi di covenant
  validationRules = {
    'DSCR': { min: 1.0, max: 3.0, warningThreshold: 1.2, criticalThreshold: 1.1 },
    'LeverageRatio': { min: 0, max: 10.0, warningThreshold: 4.0, criticalThreshold: 5.0 },
    'ICR': { min: 1.0, max: 5.0, warningThreshold: 1.5, criticalThreshold: 1.2 }
  };
  
  constructor(
    private store: Store<AppState>,
    private fb: FormBuilder,
    private excelExportService: ExcelExportService
  ) {
    this.createForm();
  }

  ngOnInit(): void {
    // Carica i covenant
    this.store.dispatch(CovenantActions.loadCovenants());
    
    // Simula il caricamento dei covenant dal backend
    this.covenants = [
      { id: 1, name: 'Debt Service Coverage Ratio', type: 'DSCR', contractId: 1, threshold: 1.2, frequency: 'Trimestrale', nextMonitoringDate: new Date('2025-04-15') },
      { id: 2, name: 'Leverage Ratio', type: 'LeverageRatio', contractId: 2, threshold: 4.0, frequency: 'Semestrale', nextMonitoringDate: new Date('2025-05-10') },
      { id: 3, name: 'Interest Coverage Ratio', type: 'ICR', contractId: 3, threshold: 1.5, frequency: 'Trimestrale', nextMonitoringDate: new Date('2025-04-20') }
    ];
  }
  
  createForm(): void {
    this.monitoringForm = this.fb.group({
      covenantId: ['', Validators.required],
      monitoringDate: [new Date().toISOString().split('T')[0], Validators.required],
      actualValue: ['', [Validators.required, Validators.min(0)]],
      status: ['', Validators.required],
      notes: [''],
      attachments: [''],
      reviewedBy: ['', Validators.required],
      reviewDate: [''],
      approvedBy: [''],
      approvalDate: ['']
    });
  }
  
  onCovenantChange(): void {
    const covenantId = this.monitoringForm.get('covenantId')?.value;
    this.selectedCovenant = this.covenants.find(c => c.id === parseInt(covenantId)) || null;
    
    if (this.selectedCovenant) {
      // Aggiorna i validatori in base al tipo di covenant
      const rules = this.validationRules[this.selectedCovenant.type as keyof typeof this.validationRules];
      if (rules) {
        this.monitoringForm.get('actualValue')?.setValidators([
          Validators.required,
          Validators.min(rules.min),
          Validators.max(rules.max)
        ]);
        this.monitoringForm.get('actualValue')?.updateValueAndValidity();
      }
    }
  }
  
  calculateStatus(): void {
    if (!this.selectedCovenant) return;
    
    const actualValue = parseFloat(this.monitoringForm.get('actualValue')?.value);
    const rules = this.validationRules[this.selectedCovenant.type as keyof typeof this.validationRules];
    
    if (!rules || isNaN(actualValue)) return;
    
    let status = '';
    
    // Logica di calcolo dello stato in base al tipo di covenant
    switch (this.selectedCovenant.type) {
      case 'DSCR':
      case 'ICR':
        // Per questi ratio, valori più alti sono migliori
        if (actualValue < rules.criticalThreshold) {
          status = 'ROSSO';
        } else if (actualValue < rules.warningThreshold) {
          status = 'GIALLO';
        } else {
          status = 'VERDE';
        }
        break;
      case 'LeverageRatio':
        // Per il leverage ratio, valori più bassi sono migliori
        if (actualValue > rules.criticalThreshold) {
          status = 'ROSSO';
        } else if (actualValue > rules.warningThreshold) {
          status = 'GIALLO';
        } else {
          status = 'VERDE';
        }
        break;
      default:
        status = 'VERDE';
    }
    
    this.monitoringForm.get('status')?.setValue(status);
  }
  
  onSubmit(): void {
    this.submitted = true;
    
    // Verifica validità del form
    if (this.monitoringForm.invalid) {
      return;
    }
    
    this.loading = true;
    
    // Simula l'invio dei dati al backend
    setTimeout(() => {
      console.log('Form submitted:', this.monitoringForm.value);
      this.loading = false;
      alert('Monitoraggio salvato con successo!');
      this.resetForm();
    }, 1500);
  }
  
  resetForm(): void {
    this.submitted = false;
    this.selectedCovenant = null;
    this.monitoringForm.reset({
      monitoringDate: new Date().toISOString().split('T')[0]
    });
  }
}
