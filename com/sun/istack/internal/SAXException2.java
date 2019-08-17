package com.sun.istack.internal;

import org.xml.sax.SAXException;

public class SAXException2 extends SAXException {
  public SAXException2(String paramString) { super(paramString); }
  
  public SAXException2(Exception paramException) { super(paramException); }
  
  public SAXException2(String paramString, Exception paramException) { super(paramString, paramException); }
  
  public Throwable getCause() { return getException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\istack\internal\SAXException2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */