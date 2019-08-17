package jdk.xml.internal;

public class JdkXmlFeatures {
  public static final String ORACLE_JAXP_PROPERTY_PREFIX = "http://www.oracle.com/xml/jaxp/properties/";
  
  public static final String XML_FEATURE_MANAGER = "http://www.oracle.com/xml/jaxp/properties/XmlFeatureManager";
  
  public static final String ORACLE_FEATURE_SERVICE_MECHANISM = "http://www.oracle.com/feature/use-service-mechanism";
  
  public static final String ORACLE_ENABLE_EXTENSION_FUNCTION = "http://www.oracle.com/xml/jaxp/properties/enableExtensionFunctions";
  
  public static final String SP_ENABLE_EXTENSION_FUNCTION = "javax.xml.enableExtensionFunctions";
  
  public static final String SP_ENABLE_EXTENSION_FUNCTION_SPEC = "jdk.xml.enableExtensionFunctions";
  
  private final boolean[] featureValues = new boolean[XmlFeature.values().length];
  
  private final State[] states = new State[XmlFeature.values().length];
  
  boolean secureProcessing;
  
  public JdkXmlFeatures(boolean paramBoolean) {
    this.secureProcessing = paramBoolean;
    for (XmlFeature xmlFeature : XmlFeature.values()) {
      if (paramBoolean && xmlFeature.enforced()) {
        this.featureValues[xmlFeature.ordinal()] = xmlFeature.enforcedValue();
        this.states[xmlFeature.ordinal()] = State.FSP;
      } else {
        this.featureValues[xmlFeature.ordinal()] = xmlFeature.defaultValue();
        this.states[xmlFeature.ordinal()] = State.DEFAULT;
      } 
    } 
    readSystemProperties();
  }
  
  public void update() { readSystemProperties(); }
  
  public boolean setFeature(String paramString, State paramState, Object paramObject) {
    int i = getIndex(paramString);
    if (i > -1) {
      setFeature(i, paramState, paramObject);
      return true;
    } 
    return false;
  }
  
  public void setFeature(XmlFeature paramXmlFeature, State paramState, boolean paramBoolean) { setFeature(paramXmlFeature.ordinal(), paramState, paramBoolean); }
  
  public boolean getFeature(XmlFeature paramXmlFeature) { return this.featureValues[paramXmlFeature.ordinal()]; }
  
  public boolean getFeature(int paramInt) { return this.featureValues[paramInt]; }
  
  public void setFeature(int paramInt, State paramState, Object paramObject) {
    boolean bool;
    if (Boolean.class.isAssignableFrom(paramObject.getClass())) {
      bool = ((Boolean)paramObject).booleanValue();
    } else {
      bool = Boolean.parseBoolean((String)paramObject);
    } 
    setFeature(paramInt, paramState, bool);
  }
  
  public void setFeature(int paramInt, State paramState, boolean paramBoolean) {
    if (paramState.compareTo(this.states[paramInt]) >= 0) {
      this.featureValues[paramInt] = paramBoolean;
      this.states[paramInt] = paramState;
    } 
  }
  
  public int getIndex(String paramString) {
    for (XmlFeature xmlFeature : XmlFeature.values()) {
      if (xmlFeature.equalsPropertyName(paramString))
        return xmlFeature.ordinal(); 
    } 
    return -1;
  }
  
  private void readSystemProperties() {
    for (XmlFeature xmlFeature : XmlFeature.values()) {
      if (!getSystemProperty(xmlFeature, xmlFeature.systemProperty())) {
        String str = xmlFeature.systemPropertyOld();
        if (str != null)
          getSystemProperty(xmlFeature, str); 
      } 
    } 
  }
  
  private boolean getSystemProperty(XmlFeature paramXmlFeature, String paramString) {
    try {
      String str = SecuritySupport.getSystemProperty(paramString);
      if (str != null && !str.equals("")) {
        setFeature(paramXmlFeature, State.SYSTEMPROPERTY, Boolean.parseBoolean(str));
        return true;
      } 
      str = SecuritySupport.readJAXPProperty(paramString);
      if (str != null && !str.equals("")) {
        setFeature(paramXmlFeature, State.JAXPDOTPROPERTIES, Boolean.parseBoolean(str));
        return true;
      } 
    } catch (NumberFormatException numberFormatException) {
      throw new NumberFormatException("Invalid setting for system property: " + paramXmlFeature.systemProperty());
    } 
    return false;
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
  
  public enum XmlFeature {
    ENABLE_EXTENSION_FUNCTION("http://www.oracle.com/xml/jaxp/properties/enableExtensionFunctions", "jdk.xml.enableExtensionFunctions", "http://www.oracle.com/xml/jaxp/properties/enableExtensionFunctions", "javax.xml.enableExtensionFunctions", true, false, true, true),
    JDK_OVERRIDE_PARSER("jdk.xml.overrideDefaultParser", "jdk.xml.overrideDefaultParser", "http://www.oracle.com/feature/use-service-mechanism", "http://www.oracle.com/feature/use-service-mechanism", false, false, true, false);
    
    private final String name;
    
    private final String nameSP;
    
    private final String nameOld;
    
    private final String nameOldSP;
    
    private final boolean valueDefault;
    
    private final boolean valueEnforced;
    
    private final boolean hasSystem;
    
    private final boolean enforced;
    
    XmlFeature(String param1String1, String param1String2, String param1String3, boolean param1Boolean1, boolean param1Boolean2, boolean param1Boolean3, boolean param1Boolean4, boolean param1Boolean5) {
      this.name = param1String1;
      this.nameSP = param1String2;
      this.nameOld = param1String3;
      this.nameOldSP = param1Boolean1;
      this.valueDefault = param1Boolean2;
      this.valueEnforced = param1Boolean3;
      this.hasSystem = param1Boolean4;
      this.enforced = param1Boolean5;
    }
    
    boolean equalsPropertyName(String param1String) { return (this.name.equals(param1String) || (this.nameOld != null && this.nameOld.equals(param1String))); }
    
    public String apiProperty() { return this.name; }
    
    String systemProperty() { return this.nameSP; }
    
    String systemPropertyOld() { return this.nameOldSP; }
    
    public boolean defaultValue() { return this.valueDefault; }
    
    public boolean enforcedValue() { return this.valueEnforced; }
    
    boolean hasSystemProperty() { return this.hasSystem; }
    
    boolean enforced() { return this.enforced; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\xml\internal\JdkXmlFeatures.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */