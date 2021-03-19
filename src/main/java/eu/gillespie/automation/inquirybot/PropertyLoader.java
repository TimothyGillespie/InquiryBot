package eu.gillespie.automation.inquirybot;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyLoader {

    public static String loadProperty(String property) {
        Properties properties = new Properties();
        File propertiesFile = new File("application.properties");
        if(propertiesFile.exists()) {
            try {
                properties.load(new FileInputStream(propertiesFile));
            } catch (IOException ignored) {
                return null;
            }
        }

        return properties.getProperty(property);

    }
}
