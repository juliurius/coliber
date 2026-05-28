@echo off
set DB_NAME=coliber

dropdb -h localhost --if-exists %DB_NAME%
createdb -h localhost %DB_NAME%
psql -h localhost -d %DB_NAME% -f create.sql

echo Database %DB_NAME% is ready
