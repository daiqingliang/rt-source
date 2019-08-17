package com.sun.org.apache.xerces.internal.util;

public final class SecurityManager {
  private static final int DEFAULT_ENTITY_EXPANSION_LIMIT = 64000;
  
  private static final int DEFAULT_MAX_OCCUR_NODE_LIMIT = 5000;
  
  private static final int DEFAULT_ELEMENT_ATTRIBUTE_LIMIT = 10000;
  
  private int entityExpansionLimit = 64000;
  
  private int maxOccurLimit = 5000;
  
  private int fElementAttributeLimit = 10000;
  
  public SecurityManager() { readSystemProperties(); }
  
  public void setEntityExpansionLimit(int paramInt) { this.entityExpansionLimit = paramInt; }
  
  public int getEntityExpansionLimit() { return this.entityExpansionLimit; }
  
  public void setMaxOccurNodeLimit(int paramInt) { this.maxOccurLimit = paramInt; }
  
  public int getMaxOccurNodeLimit() { return this.maxOccurLimit; }
  
  public int getElementAttrLimit() { return this.fElementAttributeLimit; }
  
  public void setElementAttrLimit(int paramInt) { this.fElementAttributeLimit = paramInt; }
  
  private void readSystemProperties() {
    try {
      String str = System.getProperty("entityExpansionLimit");
      if (str != null && !str.equals("")) {
        this.entityExpansionLimit = Integer.parseInt(str);
        if (this.entityExpansionLimit < 0)
          this.entityExpansionLimit = 64000; 
      } else {
        this.entityExpansionLimit = 64000;
      } 
    } catch (Exception exception) {}
    try {
      String str = System.getProperty("maxOccurLimit");
      if (str != null && !str.equals("")) {
        this.maxOccurLimit = Integer.parseInt(str);
        if (this.maxOccurLimit < 0)
          this.maxOccurLimit = 5000; 
      } else {
        this.maxOccurLimit = 5000;
      } 
    } catch (Exception exception) {}
    try {
      String str = System.getProperty("elementAttributeLimit");
      if (str != null && !str.equals("")) {
        this.fElementAttributeLimit = Integer.parseInt(str);
        if (this.fElementAttributeLimit < 0)
          this.fElementAttributeLimit = 10000; 
      } else {
        this.fElementAttributeLimit = 10000;
      } 
    } catch (Exception exception) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\SecurityManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */