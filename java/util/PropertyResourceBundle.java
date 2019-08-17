package java.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import sun.util.ResourceBundleEnumeration;

public class PropertyResourceBundle extends ResourceBundle {
  private Map<String, Object> lookup;
  
  public PropertyResourceBundle(InputStream paramInputStream) throws IOException {
    Properties properties = new Properties();
    properties.load(paramInputStream);
    this.lookup = new HashMap(properties);
  }
  
  public PropertyResourceBundle(Reader paramReader) throws IOException {
    Properties properties = new Properties();
    properties.load(paramReader);
    this.lookup = new HashMap(properties);
  }
  
  public Object handleGetObject(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    return this.lookup.get(paramString);
  }
  
  public Enumeration<String> getKeys() {
    ResourceBundle resourceBundle = this.parent;
    return new ResourceBundleEnumeration(this.lookup.keySet(), (resourceBundle != null) ? resourceBundle.getKeys() : null);
  }
  
  protected Set<String> handleKeySet() { return this.lookup.keySet(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\PropertyResourceBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */