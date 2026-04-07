# ── Sobe somente no docker o banco ──────────────────────
db:
	docker compose --env-file .env.dev -f docker-compose.yml -f docker-compose.dev.yml up -d db

# ── Sobe tudo no Docker ───────────────────────────────
dev:
	docker compose --env-file .env.dev -f docker-compose.yml -f docker-compose.dev.yml --profile full up --build

# ── Produção ───────────────────────────────────────────────────
prod:
	docker compose --env-file .env.prod -f docker-compose.yml -f docker-compose.prod.yml up --build -d

# ── Parar tudo ─────────────────────────────────────────────────
down:
	docker compose down

# ── Reset total (apaga volumes/dados) ──────────────────────────
reset:
	docker compose down -v

# ── Logs ───────────────────────────────────────────────────────
logs:
	docker compose logs -f app

logs-db:
	docker compose logs -f db

# ── Testes ───────────────────────────────────────────────────
test:
	mvn test

test-coverage:
	mvn clean verify

# ── Backup do Banco ──────────────────────────────────────────
dump:
	docker exec -t votacao-db pg_dump -U postgres votacao > backup_votacao.sql

restore:
	docker exec -i votacao-db psql -U postgres votacao < backup_votacao.sql
