package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.xni.QName;

public class XMLAttributeDecl {
  public final QName name = new QName();
  
  public final XMLSimpleType simpleType = new XMLSimpleType();
  
  public boolean optional;
  
  public void setValues(QName paramQName, XMLSimpleType paramXMLSimpleType, boolean paramBoolean) {
    this.name.setValues(paramQName);
    this.simpleType.setValues(paramXMLSimpleType);
    this.optional = paramBoolean;
  }
  
  public void clear() {
    this.name.clear();
    this.simpleType.clear();
    this.optional = false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\XMLAttributeDecl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */