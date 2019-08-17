package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import java.util.Locale;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

class DefaultValidationErrorHandler extends DefaultHandler {
  private static int ERROR_COUNT_LIMIT = 10;
  
  private int errorCount = 0;
  
  private Locale locale = Locale.getDefault();
  
  public DefaultValidationErrorHandler(Locale paramLocale) { this.locale = paramLocale; }
  
  public void error(SAXParseException paramSAXParseException) throws SAXException {
    if (this.errorCount >= ERROR_COUNT_LIMIT)
      return; 
    if (this.errorCount == 0)
      System.err.println(SAXMessageFormatter.formatMessage(this.locale, "errorHandlerNotSet", new Object[] { Integer.valueOf(this.errorCount) })); 
    String str1 = paramSAXParseException.getSystemId();
    if (str1 == null)
      str1 = "null"; 
    String str2 = "Error: URI=" + str1 + " Line=" + paramSAXParseException.getLineNumber() + ": " + paramSAXParseException.getMessage();
    System.err.println(str2);
    this.errorCount++;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\DefaultValidationErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */