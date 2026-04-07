-- Adiciona campo de versão para controle de concorrência com optimistic locking (JPA/Hibernate)
ALTER TABLE pauta ADD COLUMN version INTEGER DEFAULT 0 NOT NULL;
