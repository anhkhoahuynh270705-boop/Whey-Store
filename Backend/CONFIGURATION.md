# Local configuration

The tracked configuration and Compose files use environment-variable references instead of stored passwords.

1. For a new clone, copy `.env.example` to `.env` in this directory. If `.env` already exists, merge the missing database variables from the example without replacing its current values.
2. Replace each `change-me` value with a local password or secret. Compose reads this `.env` file automatically when run from `Backend`.
3. Start the normal stack with `docker compose -f docker-compose.example.yml up -d --build`, or the development stack with `docker compose -f docker-compose.dev.example.yml up -d --build`.

The original `config/*.yml`, Compose files, and `db-migrations/postgres-setup.sql` are local-only and ignored by Git. Copy the corresponding `config/*.example.yml` files to their original names when running a service locally outside Docker. The Compose examples use `db-migrations/postgres-setup.sql.example` directly.

The PostgreSQL initialization script runs only when the data volume is first created. On an existing volume, changing the values in `.env` does not change existing database-user passwords; use values matching that database or update the database users separately.
