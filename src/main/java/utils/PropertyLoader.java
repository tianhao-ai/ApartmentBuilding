package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for loading and accessing application properties.
 * Provides type-safe access to configuration values stored in application.properties file.
 * Properties are loaded once during class initialization.
 */
public class PropertyLoader {
    private static final Properties properties = new Properties();

    /**
     * Static initializer block that loads properties from application.properties file.
     * Throws RuntimeException if the file cannot be found or loaded.
     */
    static {
        try (InputStream input = PropertyLoader.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find application.properties");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading application.properties", ex);
        }
    }

    /**
     * Retrieves a property value as a double.
     * @param key The property key to look up
     * @return The property value as a double
     * @throws NumberFormatException if the property value cannot be parsed as a double
     */
    public static double getDoubleProperty(String key) {
        return Double.parseDouble(properties.getProperty(key));
    }

    /**
     * Retrieves a property value as an integer.
     * @param key The property key to look up
     * @return The property value as an integer
     * @throws NumberFormatException if the property value cannot be parsed as an integer
     */
    public static int getIntProperty(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    /**
     * Retrieves a property value as a string.
     * @param key The property key to look up
     * @return The property value as a string, or null if the key doesn't exist
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
} 