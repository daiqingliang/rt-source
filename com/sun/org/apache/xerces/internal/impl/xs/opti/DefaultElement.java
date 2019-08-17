package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;

public class DefaultElement extends NodeImpl implements Element {
  public DefaultElement() {}
  
  public DefaultElement(String paramString1, String paramString2, String paramString3, String paramString4, short paramShort) { super(paramString1, paramString2, paramString3, paramString4, paramShort); }
  
  public String getTagName() { return null; }
  
  public String getAttribute(String paramString) { return null; }
  
  public Attr getAttributeNode(String paramString) { return null; }
  
  public NodeList getElementsByTagName(String paramString) { return null; }
  
  public String getAttributeNS(String paramString1, String paramString2) { return null; }
  
  public Attr getAttributeNodeNS(String paramString1, String paramString2) { return null; }
  
  public NodeList getElementsByTagNameNS(String paramString1, String paramString2) { return null; }
  
  public boolean hasAttribute(String paramString) { return false; }
  
  public boolean hasAttributeNS(String paramString1, String paramString2) { return false; }
  
  public TypeInfo getSchemaTypeInfo() { return null; }
  
  public void setAttribute(String paramString1, String paramString2) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public void removeAttribute(String paramString) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public Attr removeAttributeNode(Attr paramAttr) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public Attr setAttributeNode(Attr paramAttr) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public void setAttributeNS(String paramString1, String paramString2, String paramString3) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public void removeAttributeNS(String paramString1, String paramString2) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public Attr setAttributeNodeNS(Attr paramAttr) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public void setIdAttributeNode(Attr paramAttr, boolean paramBoolean) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public void setIdAttribute(String paramString, boolean paramBoolean) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public void setIdAttributeNS(String paramString1, String paramString2, boolean paramBoolean) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\opti\DefaultElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */