#!/usr/bin/env bash
set -e

DB_NAME="coliber"
POSTGRES="/opt/homebrew/opt/postgresql@16/bin"

"$POSTGRES/pg_isready" -h localhost

"$POSTGRES/dropdb" -h localhost --if-exists "$DB_NAME"
"$POSTGRES/createdb" -h localhost "$DB_NAME"
"$POSTGRES/psql" -h localhost -d "$DB_NAME" -f create.sql

echo "Database $DB_NAME is ready"
