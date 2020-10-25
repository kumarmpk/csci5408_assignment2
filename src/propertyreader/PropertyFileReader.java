package propertyreader;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertyFileReader implements IPropertyFileReader{

    public Properties loadPropertyFile(String fileName) {
        Properties prop = null;

        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            prop = new Properties();
            prop.load(inputStream);
        } catch (Exception e){
            e.printStackTrace();
            throw new IllegalArgumentException("File Not Found Exception in ProperfileReader for "+fileName);
        }

        return prop;
    }

}
