package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.xml.namespace.NamespaceContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public final class InterningXmlVisitor implements XmlVisitor {
  private final XmlVisitor next;
  
  private final AttributesImpl attributes = new AttributesImpl(null);
  
  public InterningXmlVisitor(XmlVisitor paramXmlVisitor) { this.next = paramXmlVisitor; }
  
  public void startDocument(LocatorEx paramLocatorEx, NamespaceContext paramNamespaceContext) throws SAXException { this.next.startDocument(paramLocatorEx, paramNamespaceContext); }
  
  public void endDocument() throws SAXException { this.next.endDocument(); }
  
  public void startElement(TagName paramTagName) throws SAXException {
    this.attributes.setAttributes(paramTagName.atts);
    paramTagName.atts = this.attributes;
    paramTagName.uri = intern(paramTagName.uri);
    paramTagName.local = intern(paramTagName.local);
    this.next.startElement(paramTagName);
  }
  
  public void endElement(TagName paramTagName) throws SAXException {
    paramTagName.uri = intern(paramTagName.uri);
    paramTagName.local = intern(paramTagName.local);
    this.next.endElement(paramTagName);
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException { this.next.startPrefixMapping(intern(paramString1), intern(paramString2)); }
  
  public void endPrefixMapping(String paramString) throws SAXException { this.next.endPrefixMapping(intern(paramString)); }
  
  public void text(CharSequence paramCharSequence) throws SAXException { this.next.text(paramCharSequence); }
  
  public UnmarshallingContext getContext() { return this.next.getContext(); }
  
  public XmlVisitor.TextPredictor getPredictor() { return this.next.getPredictor(); }
  
  private static String intern(String paramString) { return (paramString == null) ? null : paramString.intern(); }
  
  private static class AttributesImpl implements Attributes {
    private Attributes core;
    
    private AttributesImpl() throws SAXException {}
    
    void setAttributes(Attributes param1Attributes) { this.core = param1Attributes; }
    
    public int getIndex(String param1String) { return this.core.getIndex(param1String); }
    
    public int getIndex(String param1String1, String param1String2) { return this.core.getIndex(param1String1, param1String2); }
    
    public int getLength() { return this.core.getLength(); }
    
    public String getLocalName(int param1Int) { return InterningXmlVisitor.intern(this.core.getLocalName(param1Int)); }
    
    public String getQName(int param1Int) { return InterningXmlVisitor.intern(this.core.getQName(param1Int)); }
    
    public String getType(int param1Int) { return InterningXmlVisitor.intern(this.core.getType(param1Int)); }
    
    public String getType(String param1String) { return InterningXmlVisitor.intern(this.core.getType(param1String)); }
    
    public String getType(String param1String1, String param1String2) { return InterningXmlVisitor.intern(this.core.getType(param1String1, param1String2)); }
    
    public String getURI(int param1Int) { return InterningXmlVisitor.intern(this.core.getURI(param1Int)); }
    
    public String getValue(int param1Int) { return this.core.getValue(param1Int); }
    
    public String getValue(String param1String) { return this.core.getValue(param1String); }
    
    public String getValue(String param1String1, String param1String2) { return this.core.getValue(param1String1, param1String2); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\InterningXmlVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */