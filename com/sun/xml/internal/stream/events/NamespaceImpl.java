package com.sun.xml.internal.stream.events;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Namespace;

public class NamespaceImpl extends AttributeImpl implements Namespace {
  public NamespaceImpl() { init(); }
  
  public NamespaceImpl(String paramString) {
    super("xmlns", "http://www.w3.org/2000/xmlns/", "", paramString, null);
    init();
  }
  
  public NamespaceImpl(String paramString1, String paramString2) {
    super("xmlns", "http://www.w3.org/2000/xmlns/", paramString1, paramString2, null);
    init();
  }
  
  public boolean isDefaultNamespaceDeclaration() {
    QName qName = getName();
    return (qName != null && qName.getLocalPart().equals(""));
  }
  
  void setPrefix(String paramString) {
    if (paramString == null) {
      setName(new QName("http://www.w3.org/2000/xmlns/", "", "xmlns"));
    } else {
      setName(new QName("http://www.w3.org/2000/xmlns/", paramString, "xmlns"));
    } 
  }
  
  public String getPrefix() {
    QName qName = getName();
    return (qName != null) ? qName.getLocalPart() : null;
  }
  
  public String getNamespaceURI() { return getValue(); }
  
  void setNamespaceURI(String paramString) { setValue(paramString); }
  
  protected void init() { setEventType(13); }
  
  public int getEventType() { return 13; }
  
  public boolean isNamespace() { return true; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\events\NamespaceImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */