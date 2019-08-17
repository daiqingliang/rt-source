package com.sun.xml.internal.bind.v2.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class FatalAdapter implements ErrorHandler {
  private final ErrorHandler core;
  
  public FatalAdapter(ErrorHandler paramErrorHandler) { this.core = paramErrorHandler; }
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException { this.core.warning(paramSAXParseException); }
  
  public void error(SAXParseException paramSAXParseException) throws SAXException { this.core.fatalError(paramSAXParseException); }
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException { this.core.fatalError(paramSAXParseException); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v\\util\FatalAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */