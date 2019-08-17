package com.sun.org.apache.xerces.internal.xni;

public interface XMLAttributes {
  int addAttribute(QName paramQName, String paramString1, String paramString2);
  
  void removeAllAttributes();
  
  void removeAttributeAt(int paramInt);
  
  int getLength();
  
  int getIndex(String paramString);
  
  int getIndex(String paramString1, String paramString2);
  
  void setName(int paramInt, QName paramQName);
  
  void getName(int paramInt, QName paramQName);
  
  String getPrefix(int paramInt);
  
  String getURI(int paramInt);
  
  String getLocalName(int paramInt);
  
  String getQName(int paramInt);
  
  QName getQualifiedName(int paramInt);
  
  void setType(int paramInt, String paramString);
  
  String getType(int paramInt);
  
  String getType(String paramString);
  
  String getType(String paramString1, String paramString2);
  
  void setValue(int paramInt, String paramString);
  
  void setValue(int paramInt, String paramString, XMLString paramXMLString);
  
  String getValue(int paramInt);
  
  String getValue(String paramString);
  
  String getValue(String paramString1, String paramString2);
  
  void setNonNormalizedValue(int paramInt, String paramString);
  
  String getNonNormalizedValue(int paramInt);
  
  void setSpecified(int paramInt, boolean paramBoolean);
  
  boolean isSpecified(int paramInt);
  
  Augmentations getAugmentations(int paramInt);
  
  Augmentations getAugmentations(String paramString1, String paramString2);
  
  Augmentations getAugmentations(String paramString);
  
  void setAugmentations(int paramInt, Augmentations paramAugmentations);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xni\XMLAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */