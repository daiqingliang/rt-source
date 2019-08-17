package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.TypeInfo;

public class AttrImpl extends NodeImpl implements Attr {
  Element element;
  
  String value;
  
  public AttrImpl() { this.nodeType = 2; }
  
  public AttrImpl(Element paramElement, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    super(paramString1, paramString2, paramString3, paramString4, (short)2);
    this.element = paramElement;
    this.value = paramString5;
  }
  
  public String getName() { return this.rawname; }
  
  public boolean getSpecified() { return true; }
  
  public String getValue() { return this.value; }
  
  public String getNodeValue() { return getValue(); }
  
  public Element getOwnerElement() { return this.element; }
  
  public Document getOwnerDocument() { return this.element.getOwnerDocument(); }
  
  public void setValue(String paramString) throws DOMException { this.value = paramString; }
  
  public boolean isId() { return false; }
  
  public TypeInfo getSchemaTypeInfo() { return null; }
  
  public String toString() { return getName() + "=\"" + getValue() + "\""; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\opti\AttrImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */