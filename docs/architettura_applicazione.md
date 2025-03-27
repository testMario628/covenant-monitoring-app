# Architettura dell'Applicazione per il Monitoraggio dei Covenant

## 1. Panoramica dell'Architettura

L'applicazione sarà sviluppata utilizzando un'architettura a tre livelli:

1. **Frontend**: Sviluppato con Angular
2. **Backend**: Sviluppato con Spring Boot
3. **Database**: PostgreSQL

L'architettura seguirà il pattern Model-View-Controller (MVC) con una chiara separazione delle responsabilità tra i diversi componenti.

## 2. Architettura del Frontend (Angular)

### 2.1 Struttura dei Moduli

- **Core Module**: Contiene servizi singleton, modelli di dati e guardie di autenticazione
- **Shared Module**: Contiene componenti, direttive e pipe riutilizzabili
- **Feature Modules**:
  - **Auth Module**: Gestione dell'autenticazione e autorizzazione
  - **Contract Module**: Gestione dei contratti finanziari
  - **Covenant Module**: Gestione dei covenant
  - **Monitoring Module**: Monitoraggio trimestrale
  - **Reporting Module**: Generazione di report e dashboard

### 2.2 Componenti Principali

- **Layout Components**: Header, Footer, Sidebar, ecc.
- **Auth Components**: Login, Gestione Profilo, ecc.
- **Contract Components**: Lista Contratti, Dettaglio Contratto, Form Contratto, ecc.
- **Covenant Components**: Lista Covenant, Dettaglio Covenant, Form Covenant, ecc.
- **Monitoring Components**: Dashboard Monitoraggio, Form Monitoraggio, ecc.
- **Reporting Components**: Report Sintesi, Report Dettaglio, ecc.

### 2.3 Servizi

- **AuthService**: Gestione dell'autenticazione e dei token JWT
- **ContractService**: Operazioni CRUD sui contratti
- **CovenantService**: Operazioni CRUD sui covenant
- **MonitoringService**: Gestione del monitoraggio
- **NotificationService**: Gestione delle notifiche e degli alert
- **UserService**: Gestione degli utenti e dei permessi

### 2.4 Routing

- Routing gerarchico basato sui moduli
- Guardie di autenticazione e autorizzazione per proteggere le rotte
- Lazy loading dei moduli per migliorare le performance

## 3. Architettura del Backend (Spring Boot)

### 3.1 Struttura dei Package

- **com.covenant.monitoring.config**: Configurazioni dell'applicazione
- **com.covenant.monitoring.controller**: Controller REST
- **com.covenant.monitoring.model**: Entità JPA
- **com.covenant.monitoring.repository**: Repository JPA
- **com.covenant.monitoring.service**: Servizi di business logic
- **com.covenant.monitoring.security**: Configurazione di sicurezza
- **com.covenant.monitoring.exception**: Gestione delle eccezioni
- **com.covenant.monitoring.dto**: Oggetti di trasferimento dati
- **com.covenant.monitoring.util**: Classi di utilità

### 3.2 Controller REST

- **AuthController**: Endpoint per l'autenticazione
- **ContractController**: Endpoint per la gestione dei contratti
- **CovenantController**: Endpoint per la gestione dei covenant
- **MonitoringController**: Endpoint per il monitoraggio
- **UserController**: Endpoint per la gestione degli utenti
- **ReportController**: Endpoint per la generazione di report

### 3.3 Servizi

- **AuthService**: Logica di autenticazione e autorizzazione
- **ContractService**: Logica di business per i contratti
- **CovenantService**: Logica di business per i covenant
- **MonitoringService**: Logica di business per il monitoraggio
- **NotificationService**: Logica per l'invio di notifiche e alert
- **UserService**: Logica per la gestione degli utenti
- **SchedulerService**: Gestione dei job schedulati (promemoria, reminder)

### 3.4 Sicurezza

- Autenticazione basata su JWT (JSON Web Token)
- Autorizzazione basata sui ruoli
- Protezione contro attacchi CSRF, XSS, ecc.
- Validazione degli input

## 4. Architettura del Database (PostgreSQL)

### 4.1 Schema del Database

#### Tabelle Principali

- **users**: Informazioni sugli utenti
- **roles**: Ruoli degli utenti
- **user_roles**: Relazione molti-a-molti tra utenti e ruoli
- **contracts**: Informazioni sui contratti finanziari
- **contract_documents**: Documenti allegati ai contratti
- **covenants**: Informazioni sui covenant
- **covenant_owners**: Assegnazione delle ownership di monitoraggio
- **monitoring_results**: Risultati del monitoraggio
- **notifications**: Notifiche e alert

#### Relazioni

- Un utente può avere più ruoli (many-to-many)
- Un contratto può avere più covenant (one-to-many)
- Un contratto può avere più documenti (one-to-many)
- Un covenant può avere più owner (one-to-many)
- Un covenant può avere più risultati di monitoraggio (one-to-many)

### 4.2 Indici e Ottimizzazioni

- Indici sulle chiavi primarie e straniere
- Indici sui campi di ricerca frequente (ID Contratto, Country, ecc.)
- Ottimizzazione delle query per le operazioni più frequenti

## 5. Integrazione tra i Componenti

### 5.1 Comunicazione Frontend-Backend

- API RESTful per la comunicazione tra frontend e backend
- Utilizzo di DTOs (Data Transfer Objects) per il trasferimento dei dati
- Gestione centralizzata degli errori

### 5.2 Comunicazione Backend-Database

- Utilizzo di Spring Data JPA per l'accesso al database
- Repository pattern per l'astrazione dell'accesso ai dati
- Transazioni per garantire l'integrità dei dati

## 6. Funzionalità Trasversali

### 6.1 Gestione delle Notifiche

- Notifiche in-app per gli utenti
- Invio di email per promemoria e alert
- Notifiche push per eventi critici

### 6.2 Gestione dei Documenti

- Upload e download di documenti
- Versionamento dei documenti
- Anteprima dei documenti

### 6.3 Logging e Monitoraggio

- Logging delle attività degli utenti
- Monitoraggio delle performance dell'applicazione
- Tracciamento degli errori

### 6.4 Internazionalizzazione

- Supporto per più lingue
- Formattazione di date, numeri e valute in base alla locale

## 7. Deployment e Scalabilità

### 7.1 Containerizzazione

- Utilizzo di Docker per il packaging dell'applicazione
- Docker Compose per l'orchestrazione dei container

### 7.2 Scalabilità

- Architettura stateless per facilitare la scalabilità orizzontale
- Caching per migliorare le performance
- Load balancing per distribuire il carico

## 8. Considerazioni sulla Sicurezza

- Crittografia dei dati sensibili
- Protezione contro attacchi comuni (SQL Injection, XSS, CSRF)
- Gestione sicura delle sessioni
- Audit trail per tracciare le modifiche ai dati sensibili

## 9. Diagramma dell'Architettura

```
+------------------+      +------------------+      +------------------+
|                  |      |                  |      |                  |
|  Angular Frontend|<---->|  Spring Backend  |<---->|  PostgreSQL DB   |
|                  |      |                  |      |                  |
+------------------+      +------------------+      +------------------+
        |                         |                         |
        v                         v                         v
+------------------+      +------------------+      +------------------+
|                  |      |                  |      |                  |
|  User Interface  |      |  Business Logic  |      |   Data Storage   |
|                  |      |                  |      |                  |
+------------------+      +------------------+      +------------------+
```

## 10. Prossimi Passi

1. Configurazione dell'ambiente di sviluppo
2. Implementazione del database PostgreSQL
3. Sviluppo del backend Spring Boot
4. Sviluppo del frontend Angular
5. Integrazione e test
6. Documentazione
7. Deployment
