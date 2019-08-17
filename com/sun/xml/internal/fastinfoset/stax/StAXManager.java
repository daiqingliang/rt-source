package com.sun.xml.internal.fastinfoset.stax;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.util.HashMap;

public class StAXManager {
  protected static final String STAX_NOTATIONS = "javax.xml.stream.notations";
  
  protected static final String STAX_ENTITIES = "javax.xml.stream.entities";
  
  HashMap features = new HashMap();
  
  public static final int CONTEXT_READER = 1;
  
  public static final int CONTEXT_WRITER = 2;
  
  public StAXManager() {}
  
  public StAXManager(int paramInt) {
    switch (paramInt) {
      case 1:
        initConfigurableReaderProperties();
        break;
      case 2:
        initWriterProps();
        break;
    } 
  }
  
  public StAXManager(StAXManager paramStAXManager) {
    HashMap hashMap = paramStAXManager.getProperties();
    this.features.putAll(hashMap);
  }
  
  private HashMap getProperties() { return this.features; }
  
  private void initConfigurableReaderProperties() {
    this.features.put("javax.xml.stream.isNamespaceAware", Boolean.TRUE);
    this.features.put("javax.xml.stream.isValidating", Boolean.FALSE);
    this.features.put("javax.xml.stream.isReplacingEntityReferences", Boolean.TRUE);
    this.features.put("javax.xml.stream.isSupportingExternalEntities", Boolean.TRUE);
    this.features.put("javax.xml.stream.isCoalescing", Boolean.FALSE);
    this.features.put("javax.xml.stream.supportDTD", Boolean.FALSE);
    this.features.put("javax.xml.stream.reporter", null);
    this.features.put("javax.xml.stream.resolver", null);
    this.features.put("javax.xml.stream.allocator", null);
    this.features.put("javax.xml.stream.notations", null);
  }
  
  private void initWriterProps() { this.features.put("javax.xml.stream.isRepairingNamespaces", Boolean.FALSE); }
  
  public boolean containsProperty(String paramString) { return this.features.containsKey(paramString); }
  
  public Object getProperty(String paramString) {
    checkProperty(paramString);
    return this.features.get(paramString);
  }
  
  public void setProperty(String paramString, Object paramObject) {
    checkProperty(paramString);
    if (paramString.equals("javax.xml.stream.isValidating") && Boolean.TRUE.equals(paramObject))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.validationNotSupported") + CommonResourceBundle.getInstance().getString("support_validation")); 
    if (paramString.equals("javax.xml.stream.isSupportingExternalEntities") && Boolean.TRUE.equals(paramObject))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.externalEntities") + CommonResourceBundle.getInstance().getString("resolve_external_entities_")); 
    this.features.put(paramString, paramObject);
  }
  
  public void checkProperty(String paramString) {
    if (!this.features.containsKey(paramString))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.propertyNotSupported", new Object[] { paramString })); 
  }
  
  public String toString() { return this.features.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\StAXManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */