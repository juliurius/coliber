-- Wszystko w jednej transakcji, żeby odroczony trigger main_arbiter sprawdził się dopiero na COMMIT.

\set ON_ERROR_STOP on

BEGIN;

\ir 01_create.sql
\ir 02_functions.sql
\ir 03_constraints.sql
\ir 04_consistency.sql
\ir 05_views.sql
\ir 06_reference_data.sql
\ir 07_sample_data.sql

COMMIT;
