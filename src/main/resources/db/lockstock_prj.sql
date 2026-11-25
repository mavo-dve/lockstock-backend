-- =====================================================================
--  Simulatore di borsa – schema lockstock (MySQL 8.0.16+)
-- =====================================================================

DROP DATABASE IF EXISTS lockstock;
CREATE DATABASE lockstock CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lockstock;

-- ---------------------------------------------------------------------
--  Utenti
-- ---------------------------------------------------------------------
CREATE TABLE m_utente (
                          id          BIGINT       NOT NULL AUTO_INCREMENT,
                          nome        VARCHAR(50)  NOT NULL,
                          cognome     VARCHAR(50)  NOT NULL,
                          email       VARCHAR(255) NOT NULL,
                          hashpwd     VARCHAR(255) NULL,                      -- da rivedere con Keycloak
                          enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
                          privilegi   ENUM('UTENTE','AMMINISTRATORE') NOT NULL DEFAULT 'UTENTE',
                          created     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          lastlogin   DATETIME     NULL,
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_utente_email (email)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
--  Titoli (padre dell'ereditarietà JOINED)
-- ---------------------------------------------------------------------
CREATE TABLE m_titolo (
                          id                    BIGINT        NOT NULL AUTO_INCREMENT,
                          nome                  VARCHAR(100)  NOT NULL,
                          descrizione           VARCHAR(1000) NULL,
                          isin                  CHAR(12)      NOT NULL,
                          ticker                VARCHAR(10)   NOT NULL,
                          valuta                ENUM('EUR','USD','GBP','CHF') NOT NULL,
                          quotazione            DECIMAL(9,2)  NOT NULL,
                          quantita_disponibile  MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,   -- risorsa condivisa (concorrenza)
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_titolo_isin (isin),
                          UNIQUE KEY uk_titolo_ticker (ticker),
                          CONSTRAINT ck_titolo_quotazione CHECK (quotazione > 0)
) ENGINE=InnoDB;

CREATE TABLE m_azione (
                          id                BIGINT        NOT NULL,             -- PK e FK verso m_titolo
                          capitalizzazione  DECIMAL(15,2) NULL,
                          paese             VARCHAR(50)   NULL,
                          settore           VARCHAR(50)   NULL,
                          dividendo_yield   DECIMAL(5,2)  NULL,                 -- percentuale, es. 3.25
                          PRIMARY KEY (id),
                          CONSTRAINT fk_azione_titolo FOREIGN KEY (id) REFERENCES m_titolo (id) ON DELETE CASCADE,
                          CONSTRAINT ck_azione_capitalizzazione CHECK (capitalizzazione IS NULL OR capitalizzazione >= 0),
                          CONSTRAINT ck_azione_dividendo CHECK (dividendo_yield IS NULL OR dividendo_yield >= 0)
) ENGINE=InnoDB;

CREATE TABLE m_etf (
                       id                      BIGINT        NOT NULL,       -- PK e FK verso m_titolo
                       ter                     DECIMAL(4,2)  NULL,           -- percentuale, es. 0.20
                       politica_distribuzione  ENUM('ACCUMULAZIONE','DISTRIBUZIONE') NULL,
                       replica                 ENUM('FISICA','SINTETICA') NULL,
                       emittente               VARCHAR(100)  NULL,
                       dimensione_fondo        DECIMAL(15,2) NULL,
                       data_lancio             DATE          NULL,
                       partecipazione          INT UNSIGNED  NULL,           -- numero di titoli contenuti nel fondo
                       PRIMARY KEY (id),
                       CONSTRAINT fk_etf_titolo FOREIGN KEY (id) REFERENCES m_titolo (id) ON DELETE CASCADE,
                       CONSTRAINT ck_etf_ter CHECK (ter IS NULL OR ter >= 0),
                       CONSTRAINT ck_etf_dimensione CHECK (dimensione_fondo IS NULL OR dimensione_fondo >= 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
--  Storico delle quotazioni (una riga per ogni variazione di prezzo)
-- ---------------------------------------------------------------------
CREATE TABLE m_storico_quotazione (
                                      id         BIGINT        NOT NULL AUTO_INCREMENT,
                                      fk_titolo  BIGINT        NOT NULL,
                                      prezzo     DECIMAL(9,2)  NOT NULL,
                                      data_ora   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      PRIMARY KEY (id),
                                      INDEX idx_storico_titolo_data (fk_titolo, data_ora),     -- grafici: titolo + intervallo
                                      CONSTRAINT fk_storico_titolo  FOREIGN KEY (fk_titolo) REFERENCES m_titolo (id) ON DELETE CASCADE,
                                      CONSTRAINT ck_storico_prezzo  CHECK (prezzo > 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
--  Portafogli
-- ---------------------------------------------------------------------
CREATE TABLE m_portafoglio (
                               id         BIGINT       NOT NULL AUTO_INCREMENT,
                               nome       VARCHAR(100) NULL,
                               numero     VARCHAR(20)  NOT NULL,
                               saldo      DECIMAL(9,2) NOT NULL DEFAULT 0.00,        -- risorsa condivisa (concorrenza)
                               created    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               fk_utente  BIGINT       NOT NULL,
                               PRIMARY KEY (id),
                               UNIQUE KEY uk_portafoglio_numero (numero),
                               CONSTRAINT fk_portafoglio_utente FOREIGN KEY (fk_utente) REFERENCES m_utente (id),
                               CONSTRAINT ck_portafoglio_saldo CHECK (saldo >= 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
--  Ordini
-- ---------------------------------------------------------------------
CREATE TABLE m_ordine (
                          id                     BIGINT       NOT NULL AUTO_INCREMENT,
                          fk_portafoglio         BIGINT       NOT NULL,
                          fk_titolo              BIGINT       NOT NULL,
                          tipo_operazione        ENUM('BUY','SELL')    NOT NULL,
                          tipo_ordine            ENUM('MARKET','LIMIT') NOT NULL,
                          stato                  ENUM('PENDING','FILLED','CANCELLED','REJECTED')
                                        NOT NULL DEFAULT 'PENDING',
                          quantita_richiesta     INT UNSIGNED NOT NULL,
                          prezzo_limite          DECIMAL(9,2) NULL,
                          prezzo_medio_eseguito  DECIMAL(9,2) NULL,
                          commissioni            DECIMAL(9,2) NOT NULL DEFAULT 0.00,
                          motivo_rifiuto         VARCHAR(255) NULL,                  -- valorizzato solo se REJECTED
                          data_inserimento       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          data_esecuzione        DATETIME     NULL,
                          PRIMARY KEY (id),
                          INDEX idx_ordine_titolo_stato (fk_titolo, stato, tipo_ordine),   -- LIMIT eseguibili per titolo
                          CONSTRAINT fk_ordine_portafoglio FOREIGN KEY (fk_portafoglio) REFERENCES m_portafoglio (id),
                          CONSTRAINT fk_ordine_titolo      FOREIGN KEY (fk_titolo)      REFERENCES m_titolo (id),
                          CONSTRAINT ck_ordine_qta_richiesta CHECK (quantita_richiesta > 0),
    -- LIMIT => prezzo_limite obbligatorio e positivo
                          CONSTRAINT ck_ordine_prezzo_limite CHECK (tipo_ordine <> 'LIMIT' OR (prezzo_limite IS NOT NULL AND prezzo_limite > 0)),
                          CONSTRAINT ck_ordine_prezzo_medio  CHECK (prezzo_medio_eseguito IS NULL OR prezzo_medio_eseguito > 0),
                          CONSTRAINT ck_ordine_commissioni   CHECK (commissioni >= 0),
    -- REJECTED => motivo obbligatorio
                          CONSTRAINT ck_ordine_motivo        CHECK (stato <> 'REJECTED' OR motivo_rifiuto IS NOT NULL)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
--  Posizioni (cosa possiede un portafoglio)
-- ---------------------------------------------------------------------
CREATE TABLE m_posizione (
                             id                         BIGINT       NOT NULL AUTO_INCREMENT,
                             fk_portafoglio             BIGINT       NOT NULL,
                             fk_titolo                  BIGINT       NOT NULL,
                             quantita_totale            INT UNSIGNED NOT NULL DEFAULT 0,
                             prezzo_medio_carico        DECIMAL(9,2) NOT NULL,
                             data_ultimo_aggiornamento  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             PRIMARY KEY (id),
                             UNIQUE KEY uk_posizione_portafoglio_titolo (fk_portafoglio, fk_titolo),
                             CONSTRAINT fk_posizione_portafoglio FOREIGN KEY (fk_portafoglio) REFERENCES m_portafoglio (id),
                             CONSTRAINT fk_posizione_titolo      FOREIGN KEY (fk_titolo)      REFERENCES m_titolo (id),
                             CONSTRAINT ck_posizione_prezzo CHECK (prezzo_medio_carico >= 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
--  Configurazione del mercato (UNA sola riga, id = 1)
-- ---------------------------------------------------------------------
CREATE TABLE m_configurazione_mercato (
                                          id                 INT           NOT NULL,          -- niente AUTO_INCREMENT
                                          ora_apertura       TIME          NOT NULL,
                                          ora_chiusura       TIME          NOT NULL,
                                          aperto_lunedi      BOOLEAN       NOT NULL,
                                          aperto_martedi     BOOLEAN       NOT NULL,
                                          aperto_mercoledi   BOOLEAN       NOT NULL,
                                          aperto_giovedi     BOOLEAN       NOT NULL,
                                          aperto_venerdi     BOOLEAN       NOT NULL,
                                          aperto_sabato      BOOLEAN       NOT NULL,
                                          aperto_domenica    BOOLEAN       NOT NULL,
                                          stato_manuale      ENUM('AUTOMATICO','FORZATO_APERTO','FORZATO_CHIUSO') NOT NULL DEFAULT 'AUTOMATICO',
                                          commissione_fissa  DECIMAL(9,2)  NOT NULL,
                                          saldo_iniziale     DECIMAL(9,2)  NOT NULL,
                                          PRIMARY KEY (id),
                                          CONSTRAINT ck_configurazione_una_riga   CHECK (id = 1),     -- una sola riga ammessa
                                          CONSTRAINT ck_configurazione_orari      CHECK (ora_chiusura > ora_apertura),
                                          CONSTRAINT ck_configurazione_commissione CHECK (commissione_fissa >= 0),
                                          CONSTRAINT ck_configurazione_saldo      CHECK (saldo_iniziale > 0)
) ENGINE=InnoDB;
