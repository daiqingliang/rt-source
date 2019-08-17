package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import org.xml.sax.AttributeList;
import org.xml.sax.ext.Attributes2;

public final class AttributesProxy implements AttributeList, Attributes2 {
  private XMLAttributes fAttributes;
  
  public AttributesProxy(XMLAttributes paramXMLAttributes) { this.fAttributes = paramXMLAttributes; }
  
  public void setAttributes(XMLAttributes paramXMLAttributes) { this.fAttributes = paramXMLAttributes; }
  
  public XMLAttributes getAttributes() { return this.fAttributes; }
  
  public int getLength() { return this.fAttributes.getLength(); }
  
  public String getQName(int paramInt) { return this.fAttributes.getQName(paramInt); }
  
  public String getURI(int paramInt) {
    String str = this.fAttributes.getURI(paramInt);
    return (str != null) ? str : XMLSymbols.EMPTY_STRING;
  }
  
  public String getLocalName(int paramInt) { return this.fAttributes.getLocalName(paramInt); }
  
  public String getType(int paramInt) { return this.fAttributes.getType(paramInt); }
  
  public String getType(String paramString) { return this.fAttributes.getType(paramString); }
  
  public String getType(String paramString1, String paramString2) { return paramString1.equals(XMLSymbols.EMPTY_STRING) ? this.fAttributes.getType(null, paramString2) : this.fAttributes.getType(paramString1, paramString2); }
  
  public String getValue(int paramInt) { return this.fAttributes.getValue(paramInt); }
  
  public String getValue(String paramString) { return this.fAttributes.getValue(paramString); }
  
  public String getValue(String paramString1, String paramString2) { return paramString1.equals(XMLSymbols.EMPTY_STRING) ? this.fAttributes.getValue(null, paramString2) : this.fAttributes.getValue(paramString1, paramString2); }
  
  public int getIndex(String paramString) { return this.fAttributes.getIndex(paramString); }
  
  public int getIndex(String paramString1, String paramString2) { return paramString1.equals(XMLSymbols.EMPTY_STRING) ? this.fAttributes.getIndex(null, paramString2) : this.fAttributes.getIndex(paramString1, paramString2); }
  
  public boolean isDeclared(int paramInt) {
    if (paramInt < 0 || paramInt >= this.fAttributes.getLength())
      throw new ArrayIndexOutOfBoundsException(paramInt); 
    return Boolean.TRUE.equals(this.fAttributes.getAugmentations(paramInt).getItem("ATTRIBUTE_DECLARED"));
  }
  
  public boolean isDeclared(String paramString) {
    int i = getIndex(paramString);
    if (i == -1)
      throw new IllegalArgumentException(paramString); 
    return Boolean.TRUE.equals(this.fAttributes.getAugmentations(i).getItem("ATTRIBUTE_DECLARED"));
  }
  
  public boolean isDeclared(String paramString1, String paramString2) {
    int i = getIndex(paramString1, paramString2);
    if (i == -1)
      throw new IllegalArgumentException(paramString2); 
    return Boolean.TRUE.equals(this.fAttributes.getAugmentations(i).getItem("ATTRIBUTE_DECLARED"));
  }
  
  public boolean isSpecified(int paramInt) {
    if (paramInt < 0 || paramInt >= this.fAttributes.getLength())
      throw new ArrayIndexOutOfBoundsException(paramInt); 
    return this.fAttributes.isSpecified(paramInt);
  }
  
  public boolean isSpecified(String paramString) {
    int i = getIndex(paramString);
    if (i == -1)
      throw new IllegalArgumentException(paramString); 
    return this.fAttributes.isSpecified(i);
  }
  
  public boolean isSpecified(String paramString1, String paramString2) {
    int i = getIndex(paramString1, paramString2);
    if (i == -1)
      throw new IllegalArgumentException(paramString2); 
    return this.fAttributes.isSpecified(i);
  }
  
  public String getName(int paramInt) { return this.fAttributes.getQName(paramInt); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\AttributesProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */