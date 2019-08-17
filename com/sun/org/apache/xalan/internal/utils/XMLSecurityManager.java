package com.sun.org.apache.xalan.internal.utils;

import java.util.concurrent.CopyOnWriteArrayList;
import org.xml.sax.SAXException;

public final class XMLSecurityManager {
  private final int[] values = new int[Limit.values().length];
  
  private State[] states = new State[Limit.values().length];
  
  private boolean[] isSet = new boolean[Limit.values().length];
  
  private final int indexEntityCountInfo = 10000;
  
  private String printEntityCountInfo = "";
  
  private static final CopyOnWriteArrayList<String> printedWarnings = new CopyOnWriteArrayList();
  
  public XMLSecurityManager() { this(false); }
  
  public XMLSecurityManager(boolean paramBoolean) {
    for (Limit limit : Limit.values()) {
      if (paramBoolean) {
        this.values[limit.ordinal()] = limit.secureValue();
        this.states[limit.ordinal()] = State.FSP;
      } else {
        this.values[limit.ordinal()] = limit.defaultValue();
        this.states[limit.ordinal()] = State.DEFAULT;
      } 
    } 
    readSystemProperties();
  }
  
  public void setSecureProcessing(boolean paramBoolean) {
    for (Limit limit : Limit.values()) {
      if (paramBoolean) {
        setLimit(limit.ordinal(), State.FSP, limit.secureValue());
      } else {
        setLimit(limit.ordinal(), State.FSP, limit.defaultValue());
      } 
    } 
  }
  
  public boolean setLimit(String paramString, State paramState, Object paramObject) {
    int i = getIndex(paramString);
    if (i > -1) {
      setLimit(i, paramState, paramObject);
      return true;
    } 
    return false;
  }
  
  public void setLimit(Limit paramLimit, State paramState, int paramInt) { setLimit(paramLimit.ordinal(), paramState, paramInt); }
  
  public void setLimit(int paramInt, State paramState, Object paramObject) {
    if (paramInt == 10000) {
      this.printEntityCountInfo = (String)paramObject;
    } else {
      int i = 0;
      try {
        i = Integer.parseInt((String)paramObject);
        if (i < 0)
          i = 0; 
      } catch (NumberFormatException numberFormatException) {}
      setLimit(paramInt, paramState, i);
    } 
  }
  
  public void setLimit(int paramInt1, State paramState, int paramInt2) {
    if (paramInt1 == 10000) {
      this.printEntityCountInfo = "yes";
    } else if (paramState.compareTo(this.states[paramInt1]) >= 0) {
      this.values[paramInt1] = paramInt2;
      this.states[paramInt1] = paramState;
      this.isSet[paramInt1] = true;
    } 
  }
  
  public String getLimitAsString(String paramString) {
    int i = getIndex(paramString);
    return (i > -1) ? getLimitValueByIndex(i) : null;
  }
  
  public String getLimitValueAsString(Limit paramLimit) { return Integer.toString(this.values[paramLimit.ordinal()]); }
  
  public int getLimit(Limit paramLimit) { return this.values[paramLimit.ordinal()]; }
  
  public int getLimitByIndex(int paramInt) { return this.values[paramInt]; }
  
  public String getLimitValueByIndex(int paramInt) { return (paramInt == 10000) ? this.printEntityCountInfo : Integer.toString(this.values[paramInt]); }
  
  public State getState(Limit paramLimit) { return this.states[paramLimit.ordinal()]; }
  
  public String getStateLiteral(Limit paramLimit) { return this.states[paramLimit.ordinal()].literal(); }
  
  public int getIndex(String paramString) {
    for (Limit limit : Limit.values()) {
      if (limit.equalsAPIPropertyName(paramString))
        return limit.ordinal(); 
    } 
    return paramString.equals("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo") ? 10000 : -1;
  }
  
  public boolean isSet(int paramInt) { return this.isSet[paramInt]; }
  
  public boolean printEntityCountInfo() { return this.printEntityCountInfo.equals("yes"); }
  
  private void readSystemProperties() {
    for (Limit limit : Limit.values()) {
      if (!getSystemProperty(limit, limit.systemProperty()))
        for (NameMap nameMap : NameMap.values()) {
          String str = nameMap.getOldName(limit.systemProperty());
          if (str != null)
            getSystemProperty(limit, str); 
        }  
    } 
  }
  
  public static void printWarning(String paramString1, String paramString2, SAXException paramSAXException) {
    String str = paramString1 + ":" + paramString2;
    if (printedWarnings.addIfAbsent(str))
      System.err.println("Warning: " + paramString1 + ": " + paramSAXException.getMessage()); 
  }
  
  private boolean getSystemProperty(Limit paramLimit, String paramString) {
    try {
      String str = SecuritySupport.getSystemProperty(paramString);
      if (str != null && !str.equals("")) {
        this.values[paramLimit.ordinal()] = Integer.parseInt(str);
        this.states[paramLimit.ordinal()] = State.SYSTEMPROPERTY;
        return true;
      } 
      str = SecuritySupport.readJAXPProperty(paramString);
      if (str != null && !str.equals("")) {
        this.values[paramLimit.ordinal()] = Integer.parseInt(str);
        this.states[paramLimit.ordinal()] = State.JAXPDOTPROPERTIES;
        return true;
      } 
    } catch (NumberFormatException numberFormatException) {
      throw new NumberFormatException("Invalid setting for system property: " + paramLimit.systemProperty());
    } 
    return false;
  }
  
  public enum Limit {
    ENTITY_EXPANSION_LIMIT("EntityExpansionLimit", "http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", "jdk.xml.entityExpansionLimit", 0, 64000),
    MAX_OCCUR_NODE_LIMIT("MaxOccurLimit", "http://www.oracle.com/xml/jaxp/properties/maxOccurLimit", "jdk.xml.maxOccurLimit", 0, 5000),
    ELEMENT_ATTRIBUTE_LIMIT("ElementAttributeLimit", "http://www.oracle.com/xml/jaxp/properties/elementAttributeLimit", "jdk.xml.elementAttributeLimit", 0, 10000),
    TOTAL_ENTITY_SIZE_LIMIT("TotalEntitySizeLimit", "http://www.oracle.com/xml/jaxp/properties/totalEntitySizeLimit", "jdk.xml.totalEntitySizeLimit", 0, 50000000),
    GENERAL_ENTITY_SIZE_LIMIT("MaxEntitySizeLimit", "http://www.oracle.com/xml/jaxp/properties/maxGeneralEntitySizeLimit", "jdk.xml.maxGeneralEntitySizeLimit", 0, 0),
    PARAMETER_ENTITY_SIZE_LIMIT("MaxEntitySizeLimit", "http://www.oracle.com/xml/jaxp/properties/maxParameterEntitySizeLimit", "jdk.xml.maxParameterEntitySizeLimit", 0, 1000000),
    MAX_ELEMENT_DEPTH_LIMIT("MaxElementDepthLimit", "http://www.oracle.com/xml/jaxp/properties/maxElementDepth", "jdk.xml.maxElementDepth", 0, 0),
    MAX_NAME_LIMIT("MaxXMLNameLimit", "http://www.oracle.com/xml/jaxp/properties/maxXMLNameLimit", "jdk.xml.maxXMLNameLimit", 1000, 1000),
    ENTITY_REPLACEMENT_LIMIT("EntityReplacementLimit", "http://www.oracle.com/xml/jaxp/properties/entityReplacementLimit", "jdk.xml.entityReplacementLimit", 0, 3000000);
    
    final String key;
    
    final String apiProperty;
    
    final String systemProperty;
    
    final int defaultValue;
    
    final int secureValue;
    
    Limit(String param1String1, String param1String2, int param1Int1, int param1Int2, int param1Int3) {
      this.key = param1String1;
      this.apiProperty = param1String2;
      this.systemProperty = param1Int1;
      this.defaultValue = param1Int2;
      this.secureValue = param1Int3;
    }
    
    public boolean equalsAPIPropertyName(String param1String) { return (param1String == null) ? false : this.apiProperty.equals(param1String); }
    
    public boolean equalsSystemPropertyName(String param1String) { return (param1String == null) ? false : this.systemProperty.equals(param1String); }
    
    public String key() { return this.key; }
    
    public String apiProperty() { return this.apiProperty; }
    
    String systemProperty() { return this.systemProperty; }
    
    public int defaultValue() { return this.defaultValue; }
    
    int secureValue() { return this.secureValue; }
  }
  
  public enum NameMap {
    ENTITY_EXPANSION_LIMIT("jdk.xml.entityExpansionLimit", "entityExpansionLimit"),
    MAX_OCCUR_NODE_LIMIT("jdk.xml.maxOccurLimit", "maxOccurLimit"),
    ELEMENT_ATTRIBUTE_LIMIT("jdk.xml.elementAttributeLimit", "elementAttributeLimit");
    
    final String newName;
    
    final String oldName;
    
    NameMap(String param1String1, String param1String2) {
      this.newName = param1String1;
      this.oldName = param1String2;
    }
    
    String getOldName(String param1String) { return param1String.equals(this.newName) ? this.oldName : null; }
  }
  
  public enum State {
    DEFAULT("default"),
    FSP("FEATURE_SECURE_PROCESSING"),
    JAXPDOTPROPERTIES("jaxp.properties"),
    SYSTEMPROPERTY("system property"),
    APIPROPERTY("property");
    
    final String literal;
    
    State(String param1String1) { this.literal = param1String1; }
    
    String literal() { return this.literal; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\interna\\utils\XMLSecurityManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */