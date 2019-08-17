package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

abstract class StAXConnector {
  protected final XmlVisitor visitor;
  
  protected final UnmarshallingContext context;
  
  protected final XmlVisitor.TextPredictor predictor;
  
  protected final TagName tagName = new TagNameImpl(null);
  
  public abstract void bridge() throws XMLStreamException;
  
  protected StAXConnector(XmlVisitor paramXmlVisitor) {
    this.visitor = paramXmlVisitor;
    this.context = paramXmlVisitor.getContext();
    this.predictor = paramXmlVisitor.getPredictor();
  }
  
  protected abstract Location getCurrentLocation();
  
  protected abstract String getCurrentQName();
  
  protected final void handleStartDocument(NamespaceContext paramNamespaceContext) throws SAXException { this.visitor.startDocument(new LocatorEx() {
          public ValidationEventLocator getLocation() { return new ValidationEventLocatorImpl(this); }
          
          public int getColumnNumber() { return StAXConnector.this.getCurrentLocation().getColumnNumber(); }
          
          public int getLineNumber() { return StAXConnector.this.getCurrentLocation().getLineNumber(); }
          
          public String getPublicId() { return StAXConnector.this.getCurrentLocation().getPublicId(); }
          
          public String getSystemId() { return StAXConnector.this.getCurrentLocation().getSystemId(); }
        },  paramNamespaceContext); }
  
  protected final void handleEndDocument() throws XMLStreamException { this.visitor.endDocument(); }
  
  protected static String fixNull(String paramString) { return (paramString == null) ? "" : paramString; }
  
  protected final String getQName(String paramString1, String paramString2) { return (paramString1 == null || paramString1.length() == 0) ? paramString2 : (paramString1 + ':' + paramString2); }
  
  private final class TagNameImpl extends TagName {
    private TagNameImpl() {}
    
    public String getQname() { return StAXConnector.this.getCurrentQName(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\StAXConnector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */