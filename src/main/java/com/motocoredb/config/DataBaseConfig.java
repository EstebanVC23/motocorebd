package com.motocoredb.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataBaseConfig {
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties;

    static {
        try {
            loadConfiguration();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    public static void loadConfiguration() throws IOException {
        properties = new Properties();
        try (InputStream input = DataBaseConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IOException("Database configuration file not found: " + CONFIG_FILE);
            }
            properties.load(input);
        }
    }

    public static String getDbUrl() {
        return properties.getProperty("db.url");
    }

    public static String getDbUser() {
        return properties.getProperty("db.user");
    }

    public static String getDbPassword() {
        return properties.getProperty("db.password");
    }
}