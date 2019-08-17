package com.sun.org.apache.xerces.internal.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DraconianErrorHandler implements ErrorHandler {
  public static final ErrorHandler theInstance = new DraconianErrorHandler();
  
  public void error(SAXParseException paramSAXParseException) throws SAXException { throw paramSAXParseException; }
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException { throw paramSAXParseException; }
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\DraconianErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */