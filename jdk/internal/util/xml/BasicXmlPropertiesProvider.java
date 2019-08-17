package jdk.internal.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import sun.util.spi.XmlPropertiesProvider;

public class BasicXmlPropertiesProvider extends XmlPropertiesProvider {
  public void load(Properties paramProperties, InputStream paramInputStream) throws IOException, InvalidPropertiesFormatException {
    PropertiesDefaultHandler propertiesDefaultHandler = new PropertiesDefaultHandler();
    propertiesDefaultHandler.load(paramProperties, paramInputStream);
  }
  
  public void store(Properties paramProperties, OutputStream paramOutputStream, String paramString1, String paramString2) throws IOException {
    PropertiesDefaultHandler propertiesDefaultHandler = new PropertiesDefaultHandler();
    propertiesDefaultHandler.store(paramProperties, paramOutputStream, paramString1, paramString2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\interna\\util\xml\BasicXmlPropertiesProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */