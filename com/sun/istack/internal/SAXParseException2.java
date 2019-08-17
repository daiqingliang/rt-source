package com.sun.istack.internal;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

public class SAXParseException2 extends SAXParseException {
  public SAXParseException2(String paramString, Locator paramLocator) { super(paramString, paramLocator); }
  
  public SAXParseException2(String paramString, Locator paramLocator, Exception paramException) { super(paramString, paramLocator, paramException); }
  
  public SAXParseException2(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2) { super(paramString1, paramString2, paramString3, paramInt1, paramInt2); }
  
  public SAXParseException2(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, Exception paramException) { super(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramException); }
  
  public Throwable getCause() { return getException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\istack\internal\SAXParseException2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */