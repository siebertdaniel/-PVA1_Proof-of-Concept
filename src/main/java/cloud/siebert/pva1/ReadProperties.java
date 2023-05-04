package cloud.siebert.pva1;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class ReadProperties {
    /**
     * This method is used to return a Properties object from a given path.
     *
     * @param pathToFile is a string containing the path on the filesystem to the properties file
     * @return The method returns a Properties object containing the information of the given properties file
     */
    public static Properties readProperties(String pathToFile) throws IOException {
        // Initialize the return value
        Properties returnProperties = new Properties();
        // Crate a new FileInputStream and fill it with the data from the given file
        FileInputStream ip = new FileInputStream(pathToFile);
        // Load the Properties object with the FileInputStream
        returnProperties.load(ip);
        // And lastly return that Properties object
        return returnProperties;
    }
}
