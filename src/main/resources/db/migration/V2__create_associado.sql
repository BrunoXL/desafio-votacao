CREATE TABLE associado (
    id      UUID PRIMARY KEY,
    nome    VARCHAR(255),
    cpf     VARCHAR(11) NOT NULL UNIQUE
);

CREATE INDEX idx_associado_cpf ON associado(CPF)