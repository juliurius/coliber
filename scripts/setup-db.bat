@echo off
set DB_NAME=coliber

dropdb -h localhost --if-exists %DB_NAME%
createdb -h localhost %DB_NAME%

pushd "%~dp0..\db"
psql -h localhost -d %DB_NAME% -f install.sql
popd

echo Database %DB_NAME% is ready
