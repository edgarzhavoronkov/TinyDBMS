package ru.spbau.mit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
        try (FileInputStream inputStream = new FileInputStream(PROP_FILE_NAME)) {
            prop = new Properties();
            prop.load(inputStream);
            return prop;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void close() throws IOException {
        if (prop != null) {
            try (FileOutputStream outputStream = new FileOutputStream(PROP_FILE_NAME)) {
                prop.store(outputStream, "Changed in " + System.currentTimeMillis());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
