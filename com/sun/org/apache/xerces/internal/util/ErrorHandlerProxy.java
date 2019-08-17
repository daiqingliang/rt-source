package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public abstract class ErrorHandlerProxy implements ErrorHandler {
  public void error(SAXParseException paramSAXParseException) throws SAXException {
    XMLErrorHandler xMLErrorHandler = getErrorHandler();
    if (xMLErrorHandler instanceof ErrorHandlerWrapper) {
      ((ErrorHandlerWrapper)xMLErrorHandler).fErrorHandler.error(paramSAXParseException);
    } else {
      xMLErrorHandler.error("", "", ErrorHandlerWrapper.createXMLParseException(paramSAXParseException));
    } 
  }
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException {
    XMLErrorHandler xMLErrorHandler = getErrorHandler();
    if (xMLErrorHandler instanceof ErrorHandlerWrapper) {
      ((ErrorHandlerWrapper)xMLErrorHandler).fErrorHandler.fatalError(paramSAXParseException);
    } else {
      xMLErrorHandler.fatalError("", "", ErrorHandlerWrapper.createXMLParseException(paramSAXParseException));
    } 
  }
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException {
    XMLErrorHandler xMLErrorHandler = getErrorHandler();
    if (xMLErrorHandler instanceof ErrorHandlerWrapper) {
      ((ErrorHandlerWrapper)xMLErrorHandler).fErrorHandler.warning(paramSAXParseException);
    } else {
      xMLErrorHandler.warning("", "", ErrorHandlerWrapper.createXMLParseException(paramSAXParseException));
    } 
  }
  
  protected abstract XMLErrorHandler getErrorHandler();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\ErrorHandlerProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */