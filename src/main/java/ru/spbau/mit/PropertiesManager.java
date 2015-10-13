package ru.spbau.mit;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by John on 10/2/2015.
 */
final public class PropertiesManager {
    static Properties prop;
    static final String PROP_FILE_NAME = "config.properties";

    public static Properties getProperties() {
        if (prop != null){
            return prop;
        }
        prop = new Properties();
        try {
            prop = new Properties();
            prop.load(new FileInputStream(PROP_FILE_NAME));
            return prop;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
