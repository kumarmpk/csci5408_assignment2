package propertyreader;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertyFileReader implements IPropertyFileReader{

    public Properties loadPropertyFile(String fileName) {
        Properties prop;

        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            prop = new Properties();
            prop.load(inputStream);
        } catch (Exception e){
            throw new IllegalArgumentException("File Not Found Exception in PropertyfileReader for "+fileName);
        }

        return prop;
    }

}
