CREATE TABLE pauta (
    id              UUID PRIMARY KEY,
    titulo          VARCHAR(255) NOT NULL,
    descricao       TEXT,
    status          VARCHAR(20) NOT NULL DEFAULT 'CRIADA'
                    CHECK (status IN ('CRIADA', 'EM_VOTACAO', 'ENCERRADA')),
    data_abertura   TIMESTAMP WITH TIME ZONE,
    data_fechamento TIMESTAMP WITH TIME ZONE,
    criada_em       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE INDEX idx_pauta_status on pauta(status);