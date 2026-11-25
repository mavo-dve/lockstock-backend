-- =====================================================================
--  Simulatore di borsa – dati iniziali (seed)
--  Da eseguire DOPO lockstock_prj.sql. Solo titoli in EUR.
-- =====================================================================

USE lockstock;

-- ---------------------------------------------------------------------
--  Configurazione del mercato (unica riga, id = 1)
--  Lun–ven 09:00–17:30, commissione 2 €, saldo iniziale 10.000 €
-- ---------------------------------------------------------------------
INSERT INTO m_configurazione_mercato
(id, ora_apertura, ora_chiusura,
 aperto_lunedi, aperto_martedi, aperto_mercoledi, aperto_giovedi, aperto_venerdi, aperto_sabato, aperto_domenica,
 stato_manuale, commissione_fissa, saldo_iniziale)
VALUES
    (1, '09:00:00', '17:30:00',
     TRUE, TRUE, TRUE, TRUE, TRUE, FALSE, FALSE,
     'AUTOMATICO', 2.00, 10000.00);

-- ---------------------------------------------------------------------
--  Utenti: id 1 = utente corrente (lockstock.utente-corrente-id=1), id 2 = amministratore
-- ---------------------------------------------------------------------
INSERT INTO m_utente (id, nome, cognome, email, privilegi) VALUES
                                                               (1, 'Mario', 'Rossi',   'mario.rossi@example.com',  'UTENTE'),
                                                               (2, 'Anna',  'Bianchi', 'anna.bianchi@example.com', 'AMMINISTRATORE');

-- ---------------------------------------------------------------------
--  Portafoglio dell'utente 1 con il saldo iniziale
-- ---------------------------------------------------------------------
INSERT INTO m_portafoglio (id, nome, numero, saldo, fk_utente) VALUES
    (1, 'Portafoglio principale', 'PF-000001', 10000.00, 1);

-- ---------------------------------------------------------------------
--  Titoli (tutti in EUR). Ogni titolo ha la riga in m_titolo
--  e la riga nella tabella figlia (m_azione o m_etf) con lo STESSO id.
--  Quantità diverse; FERRARI ne ha solo 2 per provare la concorrenza.
-- ---------------------------------------------------------------------
INSERT INTO m_titolo (id, nome, descrizione, isin, ticker, valuta, quotazione, quantita_disponibile) VALUES
                                                                                                         (1, 'Enel',                         'Energia elettrica e gas',                 'IT0003128367', 'ENEL', 'EUR',   6.85, 5000),
                                                                                                         (2, 'Eni',                          'Petrolio e gas',                          'IT0003132476', 'ENI',  'EUR',  14.20, 3000),
                                                                                                         (3, 'Intesa Sanpaolo',              'Gruppo bancario',                         'IT0000072618', 'ISP',  'EUR',   3.95, 8000),
                                                                                                         (4, 'Ferrari',                      'Automobili sportive',                     'NL0011585146', 'RACE', 'EUR', 410.50,    2),
                                                                                                         (5, 'iShares Core MSCI World',      'ETF azionario globale, paesi sviluppati', 'IE00B4L5Y983', 'SWDA', 'EUR',  98.30, 1500),
                                                                                                         (6, 'Vanguard FTSE All-World',      'ETF azionario globale, mercati sviluppati ed emergenti', 'IE00BK5BQT80', 'VWCE', 'EUR', 125.40, 1000);

INSERT INTO m_azione (id, capitalizzazione, paese, settore, dividendo_yield) VALUES
                                                                                 (1,  69000000000.00, 'Italia',  'Utility',    6.10),
                                                                                 (2,  46000000000.00, 'Italia',  'Energia',    6.80),
                                                                                 (3,  70000000000.00, 'Italia',  'Finanza',    7.50),
                                                                                 (4,  74000000000.00, 'Italia',  'Automotive', 0.70);

INSERT INTO m_etf (id, ter, politica_distribuzione, replica, emittente, dimensione_fondo, data_lancio, partecipazione) VALUES
                                                                                                                           (5, 0.20, 'ACCUMULAZIONE', 'FISICA', 'BlackRock', 95000000000.00, '2009-09-25', 1400),
                                                                                                                           (6, 0.22, 'ACCUMULAZIONE', 'FISICA', 'Vanguard',  20000000000.00, '2019-07-23', 3700);

-- ---------------------------------------------------------------------
--  Storico: un primo punto per ogni titolo (il prezzo attuale)
-- ---------------------------------------------------------------------
INSERT INTO m_storico_quotazione (fk_titolo, prezzo)
SELECT id, quotazione FROM m_titolo;