-- Table des frais d'adhésion (membership fees) - Fonctionnalité C
CREATE TABLE IF NOT EXISTS membership_fee (
    id VARCHAR(50) PRIMARY KEY,
    collectivity_id VARCHAR(50) NOT NULL,
    eligible_from DATE NOT NULL,
    frequency VARCHAR(20) NOT NULL CHECK (frequency IN ('WEEKLY', 'MONTHLY', 'ANNUALLY', 'PUNCTUALLY')),
    amount DECIMAL(15,2) NOT NULL,
    label VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (collectivity_id) REFERENCES collectivity(id) ON DELETE CASCADE
);