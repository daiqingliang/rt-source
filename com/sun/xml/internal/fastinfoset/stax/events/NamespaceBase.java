package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Namespace;

public class NamespaceBase extends AttributeBase implements Namespace {
  static final String DEFAULT_NS_PREFIX = "";
  
  static final String XML_NS_URI = "http://www.w3.org/XML/1998/namespace";
  
  static final String XML_NS_PREFIX = "xml";
  
  static final String XMLNS_ATTRIBUTE_NS_URI = "http://www.w3.org/2000/xmlns/";
  
  static final String XMLNS_ATTRIBUTE = "xmlns";
  
  static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/XMLSchema";
  
  static final String W3C_XML_SCHEMA_INSTANCE_NS_URI = "http://www.w3.org/2001/XMLSchema-instance";
  
  private boolean defaultDeclaration = false;
  
  public NamespaceBase(String paramString) {
    super("xmlns", "", paramString);
    setEventType(13);
  }
  
  public NamespaceBase(String paramString1, String paramString2) {
    super("xmlns", paramString1, paramString2);
    setEventType(13);
    if (Util.isEmptyString(paramString1))
      this.defaultDeclaration = true; 
  }
  
  void setPrefix(String paramString) {
    if (paramString == null) {
      setName(new QName("http://www.w3.org/2000/xmlns/", "", "xmlns"));
    } else {
      setName(new QName("http://www.w3.org/2000/xmlns/", paramString, "xmlns"));
    } 
  }
  
  public String getPrefix() { return this.defaultDeclaration ? "" : getLocalName(); }
  
  void setNamespaceURI(String paramString) { setValue(paramString); }
  
  public String getNamespaceURI() { return getValue(); }
  
  public boolean isNamespace() { return true; }
  
  public boolean isDefaultNamespaceDeclaration() { return this.defaultDeclaration; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\NamespaceBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */