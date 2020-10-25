package propertyreader;

import java.util.Properties;

public interface IPropertyFileReader {

    Properties loadPropertyFile(String fileName) throws Exception;

}
