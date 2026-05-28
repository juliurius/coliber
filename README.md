# coliber

## Database

The app uses a local PostgreSQL database named `coliber`.
PostgreSQL must be installed and running.

Create the database:

```bash
scripts/setup-db.sh
```

On Windows:

```bat
scripts\setup-db.bat
```

## Run

```bash
cd client
./gradlew run
```

On Windows:

```bat
cd client
gradlew.bat run
```
