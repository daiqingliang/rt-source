package com.sun.org.apache.xerces.internal.utils;

public final class XMLSecurityPropertyManager {
  private final String[] values = new String[Property.values().length];
  
  private State[] states = { State.DEFAULT, State.DEFAULT };
  
  public XMLSecurityPropertyManager() {
    for (Property property : Property.values())
      this.values[property.ordinal()] = property.defaultValue(); 
    readSystemProperties();
  }
  
  public boolean setValue(String paramString, State paramState, Object paramObject) {
    int i = getIndex(paramString);
    if (i > -1) {
      setValue(i, paramState, (String)paramObject);
      return true;
    } 
    return false;
  }
  
  public void setValue(Property paramProperty, State paramState, String paramString) {
    if (paramState.compareTo(this.states[paramProperty.ordinal()]) >= 0) {
      this.values[paramProperty.ordinal()] = paramString;
      this.states[paramProperty.ordinal()] = paramState;
    } 
  }
  
  public void setValue(int paramInt, State paramState, String paramString) {
    if (paramState.compareTo(this.states[paramInt]) >= 0) {
      this.values[paramInt] = paramString;
      this.states[paramInt] = paramState;
    } 
  }
  
  public String getValue(String paramString) {
    int i = getIndex(paramString);
    return (i > -1) ? getValueByIndex(i) : null;
  }
  
  public String getValue(Property paramProperty) { return this.values[paramProperty.ordinal()]; }
  
  public String getValueByIndex(int paramInt) { return this.values[paramInt]; }
  
  public int getIndex(String paramString) {
    for (Property property : Property.values()) {
      if (property.equalsName(paramString))
        return property.ordinal(); 
    } 
    return -1;
  }
  
  private void readSystemProperties() {
    getSystemProperty(Property.ACCESS_EXTERNAL_DTD, "javax.xml.accessExternalDTD");
    getSystemProperty(Property.ACCESS_EXTERNAL_SCHEMA, "javax.xml.accessExternalSchema");
  }
  
  private void getSystemProperty(Property paramProperty, String paramString) {
    try {
      String str = SecuritySupport.getSystemProperty(paramString);
      if (str != null) {
        this.values[paramProperty.ordinal()] = str;
        this.states[paramProperty.ordinal()] = State.SYSTEMPROPERTY;
        return;
      } 
      str = SecuritySupport.readJAXPProperty(paramString);
      if (str != null) {
        this.values[paramProperty.ordinal()] = str;
        this.states[paramProperty.ordinal()] = State.JAXPDOTPROPERTIES;
      } 
    } catch (NumberFormatException numberFormatException) {}
  }
  
  public enum Property {
    ACCESS_EXTERNAL_DTD("http://javax.xml.XMLConstants/property/accessExternalDTD", "all"),
    ACCESS_EXTERNAL_SCHEMA("http://javax.xml.XMLConstants/property/accessExternalSchema", "all");
    
    final String name;
    
    final String defaultValue;
    
    Property(String param1String1, String param1String2) {
      this.name = param1String1;
      this.defaultValue = param1String2;
    }
    
    public boolean equalsName(String param1String) { return (param1String == null) ? false : this.name.equals(param1String); }
    
    String defaultValue() { return this.defaultValue; }
  }
  
  public enum State {
    DEFAULT, FSP, JAXPDOTPROPERTIES, SYSTEMPROPERTY, APIPROPERTY;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\utils\XMLSecurityPropertyManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */