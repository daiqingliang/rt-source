package com.sun.org.apache.xerces.internal.jaxp.validation;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

final class DraconianErrorHandler implements ErrorHandler {
  private static final DraconianErrorHandler ERROR_HANDLER_INSTANCE = new DraconianErrorHandler();
  
  public static DraconianErrorHandler getInstance() { return ERROR_HANDLER_INSTANCE; }
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException {}
  
  public void error(SAXParseException paramSAXParseException) throws SAXException { throw paramSAXParseException; }
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException { throw paramSAXParseException; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\DraconianErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */