#!/bin/bash
set -e

# Create required service databases on first container initialization.
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname postgres <<-EOSQL
  CREATE DATABASE admin_db;
  CREATE DATABASE recruiter_db;
  CREATE DATABASE job_db;
  CREATE DATABASE application_db;
EOSQL

