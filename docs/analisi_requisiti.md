# Analisi dei Requisiti dell'Applicazione

## Panoramica
Il documento fornito descrive i requisiti per un'applicazione di "Monitoraggio Covenant ai sensi del Principio Contabile IAS 1". L'applicazione deve supportare il processo operativo di gestione e monitoraggio dei Covenant finanziari per garantire la conformità agli aggiornamenti del principio contabile IAS 1.

## Obiettivo dell'Applicazione
Sviluppare un sistema informatico ("sistema target") per la gestione delle attività di mappatura e monitoraggio dei Covenant finanziari, con l'obiettivo di garantire il rispetto degli aggiornamenti del principio contabile IAS 1 relativi alla classificazione delle passività.

## Ambito di Applicazione
L'applicazione si applica alle seguenti tipologie di contratti finanziari:
- Linee di Credito revolving e relative erogazioni
- Contratti di finanziamento e relative erogazioni
- Emissioni obbligazionarie

Sono escluse le obbligazioni ibride perpetue e i contratti finanziari relativi a società fuori dal perimetro di Bilancio consolidato.

## Utenti e Ruoli
- **Unità Finanza Country**: Responsabili della registrazione dei contratti e dell'assegnazione delle ownership
- **Unità Finanza Holding**: Accesso a tutti i contratti del Gruppo
- **Unità Legale**: Responsabili della mappatura dei Covenant
- **Owner di monitoraggio**: Responsabili del monitoraggio dei singoli Covenant
- **Unità Amministrazione**: Utilizzano gli esiti del monitoraggio per la predisposizione dei bilanci

## Flusso del Processo
1. **Registrazione del contratto finanziario** (Owner: Treasury/Corporate Finance Country)
2. **Mappatura dei Covenant** (Owner: Financial Legal Affairs/Legal & Corporate Affairs)
3. **Assegnazione delle ownership di monitoraggio** (Owner: Treasury/Corporate Finance Country)
4. **Monitoraggio dei Covenant** (Owner: Tutte le strutture assegnate)
5. **Gestione degli esiti del monitoraggio** (Owner: Varie strutture in base allo status)
6. **Predisposizione del Bilancio** (Owner: Amministrazione Holding/Country)

## Requisiti Funzionali

### 1. Gestione Contratti Finanziari
- Registrazione delle informazioni anagrafiche dei contratti
- Caricamento dei documenti contrattuali
- Visualizzazione differenziata in base ai permessi utente (Country/Holding)

### 2. Gestione Covenant
- Mappatura dei Covenant presenti nei contratti
- Assegnazione delle ownership di monitoraggio
- Tracciamento delle modifiche alle ownership

### 3. Monitoraggio Trimestrale
- Invio automatico di promemoria prima della chiusura trimestrale
- Registrazione degli esiti del monitoraggio
- Invio automatico di reminder in caso di mancato riscontro

### 4. Gestione degli Esiti
- Classificazione dei Covenant in base allo status (Verde, Giallo, Rosso)
- Gestione delle azioni in base allo status
- Invio automatico di alert in caso di status critici

### 5. Reporting
- Generazione di report di sintesi per le unità di Amministrazione
- Consultazione degli esiti del monitoraggio per la predisposizione dei bilanci

## Requisiti di Dati
L'applicazione dovrà gestire le seguenti informazioni:

### Dati Contratto
- ID Contratto
- Legal Entity di riferimento
- Country
- Tipologia di finanziamento
- Importo
- Divisa
- Controparte
- Status (Firmato/Da firmare)
- Data inizio del debito
- Data di scadenza del debito
- Presenza di Garanzia
- Società emittente Garanzia
- Altre informazioni specifiche (ISIN, programma, ecc.)

### Dati Covenant
- ID Covenant
- Titolo Covenant
- Presenza di "cure period"
- Articolo del contratto ed eventuali rimandi
- Owner (nominativo e Unità)
- Esito monitoraggio
- Data ultimo monitoraggio
- Possibili rischi futuri

## Requisiti di Sistema
- Sistema di gestione dei permessi basato sui ruoli
- Notifiche automatiche e alert
- Gestione documentale
- Interfaccia utente differenziata per ruoli

## Requisiti di Integrazione
Non sono specificati requisiti di integrazione con altri sistemi, ma potrebbe essere necessario considerare l'integrazione con:
- Sistemi di gestione documentale esistenti
- Sistemi contabili per la predisposizione dei bilanci

## Considerazioni Tecniche
Per implementare questa applicazione con Angular, Spring Boot e PostgreSQL, sarà necessario:

1. **Frontend (Angular)**:
   - Interfaccia utente per la gestione dei contratti e dei Covenant
   - Dashboard per il monitoraggio
   - Sistema di notifiche e alert
   - Visualizzazioni differenziate per i diversi ruoli

2. **Backend (Spring Boot)**:
   - API RESTful per la gestione dei dati
   - Logica di business per il monitoraggio
   - Sistema di autenticazione e autorizzazione
   - Gestione delle notifiche e degli alert

3. **Database (PostgreSQL)**:
   - Schema per la gestione dei contratti
   - Schema per la gestione dei Covenant
   - Schema per la gestione degli utenti e dei permessi
   - Relazioni tra le entità

4. **Sicurezza**:
   - Autenticazione degli utenti
   - Autorizzazione basata sui ruoli
   - Protezione dei dati sensibili
