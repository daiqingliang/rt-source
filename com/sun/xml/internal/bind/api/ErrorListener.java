package com.sun.xml.internal.bind.api;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public interface ErrorListener extends ErrorHandler {
  void error(SAXParseException paramSAXParseException);
  
  void fatalError(SAXParseException paramSAXParseException);
  
  void warning(SAXParseException paramSAXParseException);
  
  void info(SAXParseException paramSAXParseException);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\api\ErrorListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */