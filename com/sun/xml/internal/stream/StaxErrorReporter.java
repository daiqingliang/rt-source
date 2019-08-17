package com.sun.xml.internal.stream;

import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;

public class StaxErrorReporter extends XMLErrorReporter {
  protected XMLReporter fXMLReporter = null;
  
  public StaxErrorReporter(PropertyManager paramPropertyManager) {
    putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", new XMLMessageFormatter());
    reset(paramPropertyManager);
  }
  
  public StaxErrorReporter() { putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", new XMLMessageFormatter()); }
  
  public void reset(PropertyManager paramPropertyManager) { this.fXMLReporter = (XMLReporter)paramPropertyManager.getProperty("javax.xml.stream.reporter"); }
  
  public String reportError(XMLLocator paramXMLLocator, String paramString1, String paramString2, Object[] paramArrayOfObject, short paramShort) throws XNIException {
    String str;
    MessageFormatter messageFormatter = getMessageFormatter(paramString1);
    if (messageFormatter != null) {
      str = messageFormatter.formatMessage(this.fLocale, paramString2, paramArrayOfObject);
    } else {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append(paramString1);
      stringBuffer.append('#');
      stringBuffer.append(paramString2);
      int i = (paramArrayOfObject != null) ? paramArrayOfObject.length : 0;
      if (i > 0) {
        stringBuffer.append('?');
        for (byte b = 0; b < i; b++) {
          stringBuffer.append(paramArrayOfObject[b]);
          if (b < i - 1)
            stringBuffer.append('&'); 
        } 
      } 
      str = stringBuffer.toString();
    } 
    switch (paramShort) {
      case 0:
        try {
          if (this.fXMLReporter != null)
            this.fXMLReporter.report(str, "WARNING", null, convertToStaxLocation(paramXMLLocator)); 
        } catch (XMLStreamException xMLStreamException) {
          throw new XNIException(xMLStreamException);
        } 
        break;
      case 1:
        try {
          if (this.fXMLReporter != null)
            this.fXMLReporter.report(str, "ERROR", null, convertToStaxLocation(paramXMLLocator)); 
        } catch (XMLStreamException xMLStreamException) {
          throw new XNIException(xMLStreamException);
        } 
        break;
      case 2:
        if (!this.fContinueAfterFatalError)
          throw new XNIException(str); 
        break;
    } 
    return str;
  }
  
  Location convertToStaxLocation(final XMLLocator location) { return new Location() {
        public int getColumnNumber() { return location.getColumnNumber(); }
        
        public int getLineNumber() { return location.getLineNumber(); }
        
        public String getPublicId() { return location.getPublicId(); }
        
        public String getSystemId() { return location.getLiteralSystemId(); }
        
        public int getCharacterOffset() { return location.getCharacterOffset(); }
        
        public String getLocationURI() { return ""; }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\StaxErrorReporter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */