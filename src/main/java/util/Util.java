package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Small helper functions that don't need their own class.
 */
public class Util {

    /**
     * Loads version string from properties file in resources folder.
     *
     * @return Version string
     */
    public static String getVersion() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = loader.getResourceAsStream("version.properties");
        Properties prop = new Properties();
        String version = "";

        try {
            prop.load(inputStream);
        } catch (IOException e) {
            version = "x.y.z";
        }
        
        version = prop.getProperty("version");
        return version;
    }
}
