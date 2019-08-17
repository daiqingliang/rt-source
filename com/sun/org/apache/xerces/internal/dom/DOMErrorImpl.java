package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMLocator;

public class DOMErrorImpl implements DOMError {
  public short fSeverity = 1;
  
  public String fMessage = null;
  
  public DOMLocatorImpl fLocator = new DOMLocatorImpl();
  
  public Exception fException = null;
  
  public String fType;
  
  public Object fRelatedData;
  
  public DOMErrorImpl() {}
  
  public DOMErrorImpl(short paramShort, XMLParseException paramXMLParseException) {
    this.fSeverity = paramShort;
    this.fException = paramXMLParseException;
    this.fLocator = createDOMLocator(paramXMLParseException);
  }
  
  public short getSeverity() { return this.fSeverity; }
  
  public String getMessage() { return this.fMessage; }
  
  public DOMLocator getLocation() { return this.fLocator; }
  
  private DOMLocatorImpl createDOMLocator(XMLParseException paramXMLParseException) { return new DOMLocatorImpl(paramXMLParseException.getLineNumber(), paramXMLParseException.getColumnNumber(), paramXMLParseException.getCharacterOffset(), paramXMLParseException.getExpandedSystemId()); }
  
  public Object getRelatedException() { return this.fException; }
  
  public void reset() {
    this.fSeverity = 1;
    this.fException = null;
  }
  
  public String getType() { return this.fType; }
  
  public Object getRelatedData() { return this.fRelatedData; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DOMErrorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */