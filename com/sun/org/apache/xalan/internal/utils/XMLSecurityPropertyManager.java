package com.sun.org.apache.xalan.internal.utils;

public final class XMLSecurityPropertyManager extends FeaturePropertyBase {
  public XMLSecurityPropertyManager() {
    for (Property property : Property.values())
      this.values[property.ordinal()] = property.defaultValue(); 
    readSystemProperties();
  }
  
  public int getIndex(String paramString) {
    for (Property property : Property.values()) {
      if (property.equalsName(paramString))
        return property.ordinal(); 
    } 
    return -1;
  }
  
  private void readSystemProperties() {
    getSystemProperty(Property.ACCESS_EXTERNAL_DTD, "javax.xml.accessExternalDTD");
    getSystemProperty(Property.ACCESS_EXTERNAL_STYLESHEET, "javax.xml.accessExternalStylesheet");
  }
  
  public enum Property {
    ACCESS_EXTERNAL_DTD("http://javax.xml.XMLConstants/property/accessExternalDTD", "all"),
    ACCESS_EXTERNAL_STYLESHEET("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "all");
    
    final String name;
    
    final String defaultValue;
    
    Property(String param1String1, String param1String2) {
      this.name = param1String1;
      this.defaultValue = param1String2;
    }
    
    public boolean equalsName(String param1String) { return (param1String == null) ? false : this.name.equals(param1String); }
    
    String defaultValue() { return this.defaultValue; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\interna\\utils\XMLSecurityPropertyManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */