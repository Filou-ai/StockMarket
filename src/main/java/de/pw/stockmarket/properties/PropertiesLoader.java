package de.pw.stockmarket.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    public Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream input =
                     PropertiesLoader.class.getClassLoader()
                             .getResourceAsStream("config-local.properties")) {
            if (input == null) {
                throw new IOException("config.properties wurde nicht gefunden");
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return properties;
    }
}
