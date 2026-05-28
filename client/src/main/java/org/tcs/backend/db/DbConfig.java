package org.tcs.backend.db;

public record DbConfig(String url, String user, String password) {
  public static DbConfig local() {
    return new DbConfig(
        "jdbc:postgresql://localhost:5432/coliber", System.getProperty("user.name"), "");
  }

  public static DbConfig fromEnv() {
    return new DbConfig(
        System.getenv().getOrDefault("COLIBER_DB_URL", "jdbc:postgresql://localhost:5432/coliber"),
        System.getenv().getOrDefault("COLIBER_DB_USER", System.getProperty("user.name")),
        System.getenv().getOrDefault("COLIBER_DB_PASSWORD", ""));
  }
}
