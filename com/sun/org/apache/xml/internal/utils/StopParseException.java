package com.sun.org.apache.xml.internal.utils;

import org.xml.sax.SAXException;

public class StopParseException extends SAXException {
  static final long serialVersionUID = 210102479218258961L;
  
  StopParseException() { super("Stylesheet PIs found, stop the parse"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\StopParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */