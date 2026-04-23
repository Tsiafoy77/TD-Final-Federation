-- Types ENUM
CREATE TYPE account_type AS ENUM ('CASH', 'MOBILE_BANKING', 'BANK');
CREATE TYPE mobile_banking_service AS ENUM ('AIRTEL_MONEY', 'MVOLA', 'ORANGE_MONEY');
CREATE TYPE bank AS ENUM ('BRED', 'MCB', 'BMOI', 'BOA', 'BGFI', 'AFG', 'ACCES_BANQUE', 'BAOBAB', 'SIPEM');

CREATE TABLE financial_account (
                                   id                   VARCHAR(36)             PRIMARY KEY,
                                   collectivity_id      VARCHAR(36)             NOT NULL REFERENCES collectivity(id),
                                   type                 account_type            NOT NULL,
                                   amount               NUMERIC(15, 2)          NOT NULL DEFAULT 0,
                                   holder_name          VARCHAR(255),
                                   mobile_banking_service mobile_banking_service,
                                   mobile_number        VARCHAR(20),
                                   bank_name            bank,
                                   bank_code            VARCHAR(20),
                                   bank_branch_code     VARCHAR(20),
                                   bank_account_number  VARCHAR(30),
                                   bank_account_key     VARCHAR(10),
                                   created_at           TIMESTAMP               DEFAULT NOW()
);


CREATE TABLE collectivity_transaction (
                                          id                   VARCHAR(36)     PRIMARY KEY,
                                          collectivity_id      VARCHAR(36)     NOT NULL REFERENCES collectivity(id),
                                          creation_date        DATE            NOT NULL DEFAULT CURRENT_DATE,
                                          amount               NUMERIC(15, 2)  NOT NULL,
                                          payment_mode         payment_mode    NOT NULL,
                                          account_credited_id  VARCHAR(36)     NOT NULL REFERENCES financial_account(id),
                                          member_debited_id    VARCHAR(36)     NOT NULL REFERENCES member(id)
);

CREATE INDEX idx_transaction_collectivity_date ON collectivity_transaction(collectivity_id, creation_date);