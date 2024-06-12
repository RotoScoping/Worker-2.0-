package org.example.util.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }


    private PropertiesUtil() {

    }





    private static void loadProperties() {
        try(var input = PropertiesUtil.class.getClassLoader().getResourceAsStream("application.properties")){
            if (input == null) {
                throw new FileNotFoundException("Property file 'application.properties' not found in the classpath");
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static String get(String urlKey) {
        return PROPERTIES.getProperty(urlKey);
    }
}
