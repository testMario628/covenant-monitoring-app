import { Injectable } from '@angular/core';
import * as ExcelJS from 'exceljs';
import { saveAs } from 'file-saver';

@Injectable({
  providedIn: 'root'
})
export class ExcelExportService {
  
  constructor() { }

  /**
   * Esporta dati in formato Excel
   * @param data Array di oggetti da esportare
   * @param headers Intestazioni delle colonne
   * @param filename Nome del file Excel
   * @param sheetName Nome del foglio Excel
   */
  exportToExcel(data: any[], headers: {key: string, header: string}[], filename: string, sheetName: string = 'Dati'): void {
    // Crea un nuovo workbook
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet(sheetName);
    
    // Aggiungi le intestazioni
    worksheet.columns = headers;
    
    // Aggiungi i dati
    worksheet.addRows(data);
    
    // Formatta le intestazioni
    worksheet.getRow(1).font = { bold: true };
    worksheet.getRow(1).fill = {
      type: 'pattern',
      pattern: 'solid',
      fgColor: { argb: 'FFD3D3D3' }
    };
    
    // Adatta la larghezza delle colonne
    worksheet.columns.forEach(column => {
      let maxLength = 0;
      column.eachCell({ includeEmpty: true }, (cell) => {
        const columnLength = cell.value ? cell.value.toString().length : 10;
        if (columnLength > maxLength) {
          maxLength = columnLength;
        }
      });
      column.width = maxLength < 10 ? 10 : maxLength + 2;
    });
    
    // Genera il file Excel
    workbook.xlsx.writeBuffer().then((buffer) => {
      const blob = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      saveAs(blob, `${filename}.xlsx`);
    });
  }
}
