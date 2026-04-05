CREATE TABLE pauta (
    id              UUID PRIMARY KEY,
    titulo          VARCHAR(255) NOT NULL,
    descricao       TEXT,
    status          VARCHAR(20) NOT NULL DEFAULT 'CRIADA'
                    CHECK (status IN ('CRIADA', 'EM_VOTACAO', 'ENCERRADA')),
    data_abertura   TIMESTAMPTZ,
    data_fechamento TIMESTAMPTZ,
    criada_em       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_pauta_status on pauta(status);