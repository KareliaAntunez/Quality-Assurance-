//CENTRALIZA ACCESO A CONFIGURACION - EVITA CODIGO REPETIDO DE LECTURAS DE ARCHIVOS

package com.proyecto.automatizacion.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    
    private static Properties properties;
    
    static {
        try {
            String configPath = "src/test/resources/config/config.properties";
            FileInputStream file = new FileInputStream(configPath);
            properties = new Properties();
            properties.load(file);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}