package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public abstract class ErrorHandlerAdaptor implements XMLErrorHandler {
  private boolean hadError = false;
  
  public boolean hadError() { return this.hadError; }
  
  public void reset() { this.hadError = false; }
  
  protected abstract ErrorHandler getErrorHandler();
  
  public void fatalError(String paramString1, String paramString2, XMLParseException paramXMLParseException) {
    try {
      this.hadError = true;
      getErrorHandler().fatalError(Util.toSAXParseException(paramXMLParseException));
    } catch (SAXException sAXException) {
      throw new WrappedSAXException(sAXException);
    } 
  }
  
  public void error(String paramString1, String paramString2, XMLParseException paramXMLParseException) {
    try {
      this.hadError = true;
      getErrorHandler().error(Util.toSAXParseException(paramXMLParseException));
    } catch (SAXException sAXException) {
      throw new WrappedSAXException(sAXException);
    } 
  }
  
  public void warning(String paramString1, String paramString2, XMLParseException paramXMLParseException) {
    try {
      getErrorHandler().warning(Util.toSAXParseException(paramXMLParseException));
    } catch (SAXException sAXException) {
      throw new WrappedSAXException(sAXException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\ErrorHandlerAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */