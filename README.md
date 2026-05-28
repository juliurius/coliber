# coliber

## Database

Install and start PostgreSQL:

```bash
brew install postgresql@16
brew services start postgresql@16
```

Create the local database and load `create.sql`:

```bash
scripts/setup-db.sh
```

On Windows:

```bat
scripts\setup-db.bat
```

The app connects to this database:

```text
jdbc:postgresql://localhost:5432/coliber
```

By default, it uses your computer username and no password.

If your PostgreSQL uses another user or a password, set:

```bash
COLIBER_DB_URL=jdbc:postgresql://localhost:5432/coliber
COLIBER_DB_USER=postgres
COLIBER_DB_PASSWORD=your_password
```

Then use `DbConfig.fromEnv()` in the code instead of `DbConfig.local()`.
