-- Script di creazione del database per l'applicazione di monitoraggio dei Covenant

-- Creazione del database
CREATE DATABASE covenant_monitoring;

-- Connessione al database
\c covenant_monitoring;

-- Creazione delle tabelle

-- Tabella degli utenti
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabella dei ruoli
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- Tabella di relazione utenti-ruoli
CREATE TABLE user_roles (
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Tabella dei contratti finanziari
CREATE TABLE contracts (
    id SERIAL PRIMARY KEY,
    contract_id VARCHAR(50) NOT NULL UNIQUE,
    legal_entity VARCHAR(100) NOT NULL,
    country VARCHAR(50) NOT NULL,
    contract_type VARCHAR(50) NOT NULL,
    amount DECIMAL(20, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    counterparty VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    has_guarantee BOOLEAN DEFAULT FALSE,
    guarantee_company VARCHAR(100),
    additional_info TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabella dei documenti contrattuali
CREATE TABLE contract_documents (
    id SERIAL PRIMARY KEY,
    contract_id INTEGER NOT NULL,
    document_name VARCHAR(255) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE
);

-- Tabella dei covenant
CREATE TABLE covenants (
    id SERIAL PRIMARY KEY,
    contract_id INTEGER NOT NULL,
    covenant_id VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    has_cure_period BOOLEAN DEFAULT FALSE,
    contract_article VARCHAR(50),
    status VARCHAR(20) DEFAULT 'PENDING',
    last_monitoring_date TIMESTAMP,
    future_risks TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (contract_id) REFERENCES contracts(id) ON DELETE CASCADE
);

-- Tabella delle ownership dei covenant
CREATE TABLE covenant_owners (
    id SERIAL PRIMARY KEY,
    covenant_id INTEGER NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    owner_unit VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (covenant_id) REFERENCES covenants(id) ON DELETE CASCADE
);

-- Tabella dei risultati del monitoraggio
CREATE TABLE monitoring_results (
    id SERIAL PRIMARY KEY,
    covenant_id INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    monitoring_date TIMESTAMP NOT NULL,
    notes TEXT,
    created_by INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (covenant_id) REFERENCES covenants(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- Tabella delle notifiche
CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Inserimento dei ruoli predefiniti
INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', 'Amministratore del sistema'),
('ROLE_FINANCE_HOLDING', 'Finanza Holding'),
('ROLE_FINANCE_COUNTRY', 'Finanza Country'),
('ROLE_LEGAL_HOLDING', 'Legal Holding'),
('ROLE_LEGAL_COUNTRY', 'Legal Country'),
('ROLE_ADMIN_HOLDING', 'Amministrazione Holding'),
('ROLE_ADMIN_COUNTRY', 'Amministrazione Country');

-- Indici per migliorare le performance
CREATE INDEX idx_contracts_country ON contracts(country);
CREATE INDEX idx_contracts_legal_entity ON contracts(legal_entity);
CREATE INDEX idx_covenants_contract_id ON covenants(contract_id);
CREATE INDEX idx_covenants_status ON covenants(status);
CREATE INDEX idx_monitoring_results_covenant_id ON monitoring_results(covenant_id);
CREATE INDEX idx_monitoring_results_status ON monitoring_results(status);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
