package com.sun.org.apache.xerces.internal.jaxp.validation;

import org.xml.sax.SAXException;

public class WrappedSAXException extends RuntimeException {
  public final SAXException exception;
  
  WrappedSAXException(SAXException paramSAXException) { this.exception = paramSAXException; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\WrappedSAXException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */