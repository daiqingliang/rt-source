package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ErrorHandlerWrapper implements XMLErrorHandler {
  protected ErrorHandler fErrorHandler;
  
  public ErrorHandlerWrapper() {}
  
  public ErrorHandlerWrapper(ErrorHandler paramErrorHandler) { setErrorHandler(paramErrorHandler); }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler) { this.fErrorHandler = paramErrorHandler; }
  
  public ErrorHandler getErrorHandler() { return this.fErrorHandler; }
  
  public void warning(String paramString1, String paramString2, XMLParseException paramXMLParseException) throws XNIException {
    if (this.fErrorHandler != null) {
      SAXParseException sAXParseException = createSAXParseException(paramXMLParseException);
      try {
        this.fErrorHandler.warning(sAXParseException);
      } catch (SAXParseException sAXParseException1) {
        throw createXMLParseException(sAXParseException1);
      } catch (SAXException sAXException) {
        throw createXNIException(sAXException);
      } 
    } 
  }
  
  public void error(String paramString1, String paramString2, XMLParseException paramXMLParseException) throws XNIException {
    if (this.fErrorHandler != null) {
      SAXParseException sAXParseException = createSAXParseException(paramXMLParseException);
      try {
        this.fErrorHandler.error(sAXParseException);
      } catch (SAXParseException sAXParseException1) {
        throw createXMLParseException(sAXParseException1);
      } catch (SAXException sAXException) {
        throw createXNIException(sAXException);
      } 
    } 
  }
  
  public void fatalError(String paramString1, String paramString2, XMLParseException paramXMLParseException) throws XNIException {
    if (this.fErrorHandler != null) {
      SAXParseException sAXParseException = createSAXParseException(paramXMLParseException);
      try {
        this.fErrorHandler.fatalError(sAXParseException);
      } catch (SAXParseException sAXParseException1) {
        throw createXMLParseException(sAXParseException1);
      } catch (SAXException sAXException) {
        throw createXNIException(sAXException);
      } 
    } 
  }
  
  protected static SAXParseException createSAXParseException(XMLParseException paramXMLParseException) { return new SAXParseException(paramXMLParseException.getMessage(), paramXMLParseException.getPublicId(), paramXMLParseException.getExpandedSystemId(), paramXMLParseException.getLineNumber(), paramXMLParseException.getColumnNumber(), paramXMLParseException.getException()); }
  
  protected static XMLParseException createXMLParseException(SAXParseException paramSAXParseException) {
    final String fPublicId = paramSAXParseException.getPublicId();
    final String fExpandedSystemId = paramSAXParseException.getSystemId();
    final int fLineNumber = paramSAXParseException.getLineNumber();
    final int fColumnNumber = paramSAXParseException.getColumnNumber();
    XMLLocator xMLLocator = new XMLLocator() {
        public String getPublicId() { return fPublicId; }
        
        public String getExpandedSystemId() { return fExpandedSystemId; }
        
        public String getBaseSystemId() { return null; }
        
        public String getLiteralSystemId() { return null; }
        
        public int getColumnNumber() { return fColumnNumber; }
        
        public int getLineNumber() { return fLineNumber; }
        
        public int getCharacterOffset() { return -1; }
        
        public String getEncoding() { return null; }
        
        public String getXMLVersion() { return null; }
      };
    return new XMLParseException(xMLLocator, paramSAXParseException.getMessage(), paramSAXParseException);
  }
  
  protected static XNIException createXNIException(SAXException paramSAXException) { return new XNIException(paramSAXException.getMessage(), paramSAXException); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\ErrorHandlerWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */