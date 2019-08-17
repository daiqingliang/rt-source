package com.sun.org.apache.xerces.internal.utils;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

public final class XMLLimitAnalyzer {
  private final int[] values = new int[XMLSecurityManager.Limit.values().length];
  
  private final String[] names = new String[XMLSecurityManager.Limit.values().length];
  
  private final int[] totalValue = new int[XMLSecurityManager.Limit.values().length];
  
  private final Map[] caches = new Map[XMLSecurityManager.Limit.values().length];
  
  private String entityStart;
  
  private String entityEnd;
  
  public void addValue(XMLSecurityManager.Limit paramLimit, String paramString, int paramInt) { addValue(paramLimit.ordinal(), paramString, paramInt); }
  
  public void addValue(int paramInt1, String paramString, int paramInt2) {
    Map map;
    if (paramInt1 == XMLSecurityManager.Limit.ENTITY_EXPANSION_LIMIT.ordinal() || paramInt1 == XMLSecurityManager.Limit.MAX_OCCUR_NODE_LIMIT.ordinal() || paramInt1 == XMLSecurityManager.Limit.ELEMENT_ATTRIBUTE_LIMIT.ordinal() || paramInt1 == XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT.ordinal() || paramInt1 == XMLSecurityManager.Limit.ENTITY_REPLACEMENT_LIMIT.ordinal()) {
      this.totalValue[paramInt1] = this.totalValue[paramInt1] + paramInt2;
      return;
    } 
    if (paramInt1 == XMLSecurityManager.Limit.MAX_ELEMENT_DEPTH_LIMIT.ordinal() || paramInt1 == XMLSecurityManager.Limit.MAX_NAME_LIMIT.ordinal()) {
      this.values[paramInt1] = paramInt2;
      this.totalValue[paramInt1] = paramInt2;
      return;
    } 
    if (this.caches[paramInt1] == null) {
      map = new HashMap(10);
      this.caches[paramInt1] = map;
    } else {
      map = this.caches[paramInt1];
    } 
    int i = paramInt2;
    if (map.containsKey(paramString)) {
      i += ((Integer)map.get(paramString)).intValue();
      map.put(paramString, Integer.valueOf(i));
    } else {
      map.put(paramString, Integer.valueOf(paramInt2));
    } 
    if (i > this.values[paramInt1]) {
      this.values[paramInt1] = i;
      this.names[paramInt1] = paramString;
    } 
    if (paramInt1 == XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT.ordinal() || paramInt1 == XMLSecurityManager.Limit.PARAMETER_ENTITY_SIZE_LIMIT.ordinal())
      this.totalValue[XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT.ordinal()] = this.totalValue[XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT.ordinal()] + paramInt2; 
  }
  
  public int getValue(XMLSecurityManager.Limit paramLimit) { return getValue(paramLimit.ordinal()); }
  
  public int getValue(int paramInt) { return (paramInt == XMLSecurityManager.Limit.ENTITY_REPLACEMENT_LIMIT.ordinal()) ? this.totalValue[paramInt] : this.values[paramInt]; }
  
  public int getTotalValue(XMLSecurityManager.Limit paramLimit) { return this.totalValue[paramLimit.ordinal()]; }
  
  public int getTotalValue(int paramInt) { return this.totalValue[paramInt]; }
  
  public int getValueByIndex(int paramInt) { return this.values[paramInt]; }
  
  public void startEntity(String paramString) { this.entityStart = paramString; }
  
  public boolean isTracking(String paramString) { return (this.entityStart == null) ? false : this.entityStart.equals(paramString); }
  
  public void endEntity(XMLSecurityManager.Limit paramLimit, String paramString) {
    this.entityStart = "";
    Map map = this.caches[paramLimit.ordinal()];
    if (map != null)
      map.remove(paramString); 
  }
  
  public void reset(XMLSecurityManager.Limit paramLimit) {
    if (paramLimit.ordinal() == XMLSecurityManager.Limit.TOTAL_ENTITY_SIZE_LIMIT.ordinal()) {
      this.totalValue[paramLimit.ordinal()] = 0;
    } else if (paramLimit.ordinal() == XMLSecurityManager.Limit.GENERAL_ENTITY_SIZE_LIMIT.ordinal()) {
      this.names[paramLimit.ordinal()] = null;
      this.values[paramLimit.ordinal()] = 0;
      this.caches[paramLimit.ordinal()] = null;
      this.totalValue[paramLimit.ordinal()] = 0;
    } 
  }
  
  public void debugPrint(XMLSecurityManager paramXMLSecurityManager) {
    Formatter formatter = new Formatter();
    System.out.println(formatter.format("%30s %15s %15s %15s %30s", new Object[] { "Property", "Limit", "Total size", "Size", "Entity Name" }));
    for (XMLSecurityManager.Limit limit : XMLSecurityManager.Limit.values()) {
      formatter = new Formatter();
      System.out.println(formatter.format("%30s %15d %15d %15d %30s", new Object[] { limit.name(), Integer.valueOf(paramXMLSecurityManager.getLimit(limit)), Integer.valueOf(this.totalValue[limit.ordinal()]), Integer.valueOf(this.values[limit.ordinal()]), this.names[limit.ordinal()] }));
    } 
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\utils\XMLLimitAnalyzer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */