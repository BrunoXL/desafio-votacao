CREATE TABLE voto (
    id              UUID PRIMARY KEY,
    pauta_id        UUID NOT NULL REFERENCES pauta(id),
    associado_id    UUID NOT NULL REFERENCES associado(id),
    voto            VARCHAR(3) NOT NULL CHECK (voto IN ('SIM', 'NAO')),
    data_voto       TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (pauta_id, associado_id)
);

CREATE INDEX idx_voto_pauta_id ON voto (pauta_id);