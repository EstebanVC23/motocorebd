package com.motocoredb.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase para cargar la configuración de la base de datos desde un archivo de propiedades.
 */
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

    /**
        * Carga la configuración de la base de datos desde el archivo de propiedades.
        * @throws IOException si ocurre un error al cargar el archivo de propiedades.
     */
    public static void loadConfiguration() throws IOException {
        properties = new Properties();
        try (InputStream input = DataBaseConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IOException("Database configuration file not found: " + CONFIG_FILE);
            }
            properties.load(input);
        }
    }

    /**
     * Obtiene la URL de la base de datos.
     * @return la URL de la base de datos.
     */
    public static String getDbUrl() {
        return properties.getProperty("db.url");
    }

    /**
     * Obtiene el nombre de usuario de la base de datos.
     * @return el nombre de usuario de la base de datos.
     */
    public static String getDbUser() {
        return properties.getProperty("db.user");
    }

    /**
     * Obtiene la contraseña de la base de datos.
     * @return la contraseña de la base de datos.
     */
    public static String getDbPassword() {
        return properties.getProperty("db.password");
    }
}