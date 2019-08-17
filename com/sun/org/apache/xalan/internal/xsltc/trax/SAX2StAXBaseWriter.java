package com.sun.org.apache.xalan.internal.xsltc.trax;

import java.util.Vector;
import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public abstract class SAX2StAXBaseWriter extends DefaultHandler implements LexicalHandler {
  protected boolean isCDATA;
  
  protected StringBuffer CDATABuffer;
  
  protected Vector namespaces;
  
  protected Locator docLocator;
  
  protected XMLReporter reporter;
  
  public SAX2StAXBaseWriter() {}
  
  public SAX2StAXBaseWriter(XMLReporter paramXMLReporter) { this.reporter = paramXMLReporter; }
  
  public void setXMLReporter(XMLReporter paramXMLReporter) { this.reporter = paramXMLReporter; }
  
  public void setDocumentLocator(Locator paramLocator) { this.docLocator = paramLocator; }
  
  public Location getCurrentLocation() { return (this.docLocator != null) ? new SAXLocation(this.docLocator, null) : null; }
  
  public void error(SAXParseException paramSAXParseException) throws SAXException { reportException("ERROR", paramSAXParseException); }
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException { reportException("FATAL", paramSAXParseException); }
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException { reportException("WARNING", paramSAXParseException); }
  
  public void startDocument() { this.namespaces = new Vector(2); }
  
  public void endDocument() { this.namespaces = null; }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException { this.namespaces = null; }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException { this.namespaces = null; }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    if (paramString1 == null) {
      paramString1 = "";
    } else if (paramString1.equals("xml")) {
      return;
    } 
    if (this.namespaces == null)
      this.namespaces = new Vector(2); 
    this.namespaces.addElement(paramString1);
    this.namespaces.addElement(paramString2);
  }
  
  public void endPrefixMapping(String paramString) throws SAXException {}
  
  public void startCDATA() {
    this.isCDATA = true;
    if (this.CDATABuffer == null) {
      this.CDATABuffer = new StringBuffer();
    } else {
      this.CDATABuffer.setLength(0);
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.isCDATA)
      this.CDATABuffer.append(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public void endCDATA() {
    this.isCDATA = false;
    this.CDATABuffer.setLength(0);
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {}
  
  public void endDTD() {}
  
  public void endEntity(String paramString) throws SAXException {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void startEntity(String paramString) throws SAXException {}
  
  protected void reportException(String paramString, SAXException paramSAXException) throws SAXException {
    if (this.reporter != null)
      try {
        this.reporter.report(paramSAXException.getMessage(), paramString, paramSAXException, getCurrentLocation());
      } catch (XMLStreamException xMLStreamException) {
        throw new SAXException(xMLStreamException);
      }  
  }
  
  public static final void parseQName(String paramString, String[] paramArrayOfString) {
    String str2;
    String str1;
    int i = paramString.indexOf(':');
    if (i >= 0) {
      str1 = paramString.substring(0, i);
      str2 = paramString.substring(i + 1);
    } else {
      str1 = "";
      str2 = paramString;
    } 
    paramArrayOfString[0] = str1;
    paramArrayOfString[1] = str2;
  }
  
  private static final class SAXLocation implements Location {
    private int lineNumber;
    
    private int columnNumber;
    
    private String publicId;
    
    private String systemId;
    
    private SAXLocation(Locator param1Locator) {
      this.lineNumber = param1Locator.getLineNumber();
      this.columnNumber = param1Locator.getColumnNumber();
      this.publicId = param1Locator.getPublicId();
      this.systemId = param1Locator.getSystemId();
    }
    
    public int getLineNumber() { return this.lineNumber; }
    
    public int getColumnNumber() { return this.columnNumber; }
    
    public int getCharacterOffset() { return -1; }
    
    public String getPublicId() { return this.publicId; }
    
    public String getSystemId() { return this.systemId; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\SAX2StAXBaseWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */