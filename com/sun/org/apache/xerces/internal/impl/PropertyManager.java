package com.sun.org.apache.xerces.internal.impl;

import com.sun.org.apache.xerces.internal.utils.XMLSecurityManager;
import com.sun.org.apache.xerces.internal.utils.XMLSecurityPropertyManager;
import com.sun.xml.internal.stream.StaxEntityResolverWrapper;
import java.util.HashMap;
import javax.xml.stream.XMLResolver;

public class PropertyManager {
  public static final String STAX_NOTATIONS = "javax.xml.stream.notations";
  
  public static final String STAX_ENTITIES = "javax.xml.stream.entities";
  
  private static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
  
  private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
  
  private static final String XML_SECURITY_PROPERTY_MANAGER = "http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager";
  
  HashMap supportedProps = new HashMap();
  
  private XMLSecurityManager fSecurityManager;
  
  private XMLSecurityPropertyManager fSecurityPropertyMgr;
  
  public static final int CONTEXT_READER = 1;
  
  public static final int CONTEXT_WRITER = 2;
  
  public PropertyManager(int paramInt) {
    switch (paramInt) {
      case 1:
        initConfigurableReaderProperties();
        break;
      case 2:
        initWriterProps();
        break;
    } 
  }
  
  public PropertyManager(PropertyManager paramPropertyManager) {
    HashMap hashMap = paramPropertyManager.getProperties();
    this.supportedProps.putAll(hashMap);
    this.fSecurityManager = (XMLSecurityManager)getProperty("http://apache.org/xml/properties/security-manager");
    this.fSecurityPropertyMgr = (XMLSecurityPropertyManager)getProperty("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager");
  }
  
  private HashMap getProperties() { return this.supportedProps; }
  
  private void initConfigurableReaderProperties() {
    this.supportedProps.put("javax.xml.stream.isNamespaceAware", Boolean.TRUE);
    this.supportedProps.put("javax.xml.stream.isValidating", Boolean.FALSE);
    this.supportedProps.put("javax.xml.stream.isReplacingEntityReferences", Boolean.TRUE);
    this.supportedProps.put("javax.xml.stream.isSupportingExternalEntities", Boolean.TRUE);
    this.supportedProps.put("javax.xml.stream.isCoalescing", Boolean.FALSE);
    this.supportedProps.put("javax.xml.stream.supportDTD", Boolean.TRUE);
    this.supportedProps.put("javax.xml.stream.reporter", null);
    this.supportedProps.put("javax.xml.stream.resolver", null);
    this.supportedProps.put("javax.xml.stream.allocator", null);
    this.supportedProps.put("javax.xml.stream.notations", null);
    this.supportedProps.put("http://xml.org/sax/features/string-interning", new Boolean(true));
    this.supportedProps.put("http://apache.org/xml/features/allow-java-encodings", new Boolean(true));
    this.supportedProps.put("add-namespacedecl-as-attrbiute", Boolean.FALSE);
    this.supportedProps.put("http://java.sun.com/xml/stream/properties/reader-in-defined-state", new Boolean(true));
    this.supportedProps.put("reuse-instance", new Boolean(true));
    this.supportedProps.put("http://java.sun.com/xml/stream/properties/report-cdata-event", new Boolean(false));
    this.supportedProps.put("http://java.sun.com/xml/stream/properties/ignore-external-dtd", Boolean.FALSE);
    this.supportedProps.put("http://apache.org/xml/features/validation/warn-on-duplicate-attdef", new Boolean(false));
    this.supportedProps.put("http://apache.org/xml/features/warn-on-duplicate-entitydef", new Boolean(false));
    this.supportedProps.put("http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", new Boolean(false));
    this.fSecurityManager = new XMLSecurityManager(true);
    this.supportedProps.put("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
    this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
    this.supportedProps.put("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
  }
  
  private void initWriterProps() {
    this.supportedProps.put("javax.xml.stream.isRepairingNamespaces", Boolean.FALSE);
    this.supportedProps.put("escapeCharacters", Boolean.TRUE);
    this.supportedProps.put("reuse-instance", new Boolean(true));
  }
  
  public boolean containsProperty(String paramString) { return (this.supportedProps.containsKey(paramString) || (this.fSecurityManager != null && this.fSecurityManager.getIndex(paramString) > -1) || (this.fSecurityPropertyMgr != null && this.fSecurityPropertyMgr.getIndex(paramString) > -1)); }
  
  public Object getProperty(String paramString) { return this.supportedProps.get(paramString); }
  
  public void setProperty(String paramString, Object paramObject) {
    String str = null;
    if (paramString == "javax.xml.stream.isNamespaceAware" || paramString.equals("javax.xml.stream.isNamespaceAware")) {
      str = "http://apache.org/xml/features/namespaces";
    } else if (paramString == "javax.xml.stream.isValidating" || paramString.equals("javax.xml.stream.isValidating")) {
      if (paramObject instanceof Boolean && ((Boolean)paramObject).booleanValue())
        throw new IllegalArgumentException("true value of isValidating not supported"); 
    } else if (paramString == "http://xml.org/sax/features/string-interning" || paramString.equals("http://xml.org/sax/features/string-interning")) {
      if (paramObject instanceof Boolean && !((Boolean)paramObject).booleanValue())
        throw new IllegalArgumentException("false value of http://xml.org/sax/features/string-interningfeature is not supported"); 
    } else if (paramString == "javax.xml.stream.resolver" || paramString.equals("javax.xml.stream.resolver")) {
      this.supportedProps.put("http://apache.org/xml/properties/internal/stax-entity-resolver", new StaxEntityResolverWrapper((XMLResolver)paramObject));
    } 
    if (paramString.equals("http://apache.org/xml/properties/security-manager")) {
      this.fSecurityManager = XMLSecurityManager.convert(paramObject, this.fSecurityManager);
      this.supportedProps.put("http://apache.org/xml/properties/security-manager", this.fSecurityManager);
      return;
    } 
    if (paramString.equals("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager")) {
      if (paramObject == null) {
        this.fSecurityPropertyMgr = new XMLSecurityPropertyManager();
      } else {
        this.fSecurityPropertyMgr = (XMLSecurityPropertyManager)paramObject;
      } 
      this.supportedProps.put("http://www.oracle.com/xml/jaxp/properties/xmlSecurityPropertyManager", this.fSecurityPropertyMgr);
      return;
    } 
    if ((this.fSecurityManager == null || !this.fSecurityManager.setLimit(paramString, XMLSecurityManager.State.APIPROPERTY, paramObject)) && (this.fSecurityPropertyMgr == null || !this.fSecurityPropertyMgr.setValue(paramString, XMLSecurityPropertyManager.State.APIPROPERTY, paramObject)))
      this.supportedProps.put(paramString, paramObject); 
    if (str != null)
      this.supportedProps.put(str, paramObject); 
  }
  
  public String toString() { return this.supportedProps.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\PropertyManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */