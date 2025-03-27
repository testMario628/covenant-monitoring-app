// Modelli di dati per l'applicazione di monitoraggio dei Covenant

// Modello per il contratto
export interface Contract {
  id?: number;
  contractId: string;
  title: string;
  description?: string;
  legalEntity: string;
  country: string;
  startDate: Date;
  endDate?: Date;
  status: string;
  createdAt?: Date;
  updatedAt?: Date;
}

// Modello per il covenant
export interface Covenant {
  id?: number;
  covenantId: string;
  contract: Contract;
  description: string;
  type: string;
  threshold: number;
  unit: string;
  frequency: string;
  startDate: Date;
  endDate?: Date;
  status: string; // VERDE, GIALLO, ROSSO
  lastMonitoringDate?: Date;
  createdAt?: Date;
  updatedAt?: Date;
}

// Modello per il proprietario del covenant
export interface CovenantOwner {
  id?: number;
  covenant: Covenant;
  ownerName: string;
  ownerUnit: string;
  ownerEmail: string;
  ownerPhone?: string;
  isPrimary: boolean;
}

// Modello per il risultato del monitoraggio
export interface MonitoringResult {
  id?: number;
  covenant: Covenant;
  monitoringDate: Date;
  actualValue: number;
  status: string; // VERDE, GIALLO, ROSSO
  notes?: string;
  attachments?: string[];
  createdBy: User;
  createdAt?: Date;
  updatedAt?: Date;
}

// Modello per l'utente
export interface User {
  id?: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  enabled: boolean;
  roles: Role[];
}

// Modello per il ruolo
export interface Role {
  id?: number;
  name: string;
}

// Modello per la notifica
export interface Notification {
  id?: number;
  user: User;
  title: string;
  message: string;
  isRead: boolean;
  createdAt?: Date;
}

// Modello per il documento del contratto
export interface ContractDocument {
  id?: number;
  contract: Contract;
  documentType: string;
  fileName: string;
  fileUrl: string;
  uploadDate: Date;
  uploadedBy: User;
}

// Modello per la richiesta di autenticazione
export interface AuthRequest {
  username: string;
  password: string;
}

// Modello per la risposta di autenticazione
export interface AuthResponse {
  token: string;
}
