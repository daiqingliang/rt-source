package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.io.PrintStream;
import java.io.PrintWriter;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DefaultErrorHandler implements ErrorHandler, ErrorListener {
  PrintWriter m_pw;
  
  boolean m_throwExceptionOnError = true;
  
  public DefaultErrorHandler(PrintWriter paramPrintWriter) { this.m_pw = paramPrintWriter; }
  
  public DefaultErrorHandler(PrintStream paramPrintStream) { this.m_pw = new PrintWriter(paramPrintStream, true); }
  
  public DefaultErrorHandler() { this(true); }
  
  public DefaultErrorHandler(boolean paramBoolean) {
    this.m_pw = new PrintWriter(System.err, true);
    this.m_throwExceptionOnError = paramBoolean;
  }
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException {
    printLocation(this.m_pw, paramSAXParseException);
    this.m_pw.println("Parser warning: " + paramSAXParseException.getMessage());
  }
  
  public void error(SAXParseException paramSAXParseException) throws SAXException { throw paramSAXParseException; }
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException { throw paramSAXParseException; }
  
  public void warning(TransformerException paramTransformerException) throws TransformerException {
    printLocation(this.m_pw, paramTransformerException);
    this.m_pw.println(paramTransformerException.getMessage());
  }
  
  public void error(TransformerException paramTransformerException) throws TransformerException {
    if (this.m_throwExceptionOnError)
      throw paramTransformerException; 
    printLocation(this.m_pw, paramTransformerException);
    this.m_pw.println(paramTransformerException.getMessage());
  }
  
  public void fatalError(TransformerException paramTransformerException) throws TransformerException {
    if (this.m_throwExceptionOnError)
      throw paramTransformerException; 
    printLocation(this.m_pw, paramTransformerException);
    this.m_pw.println(paramTransformerException.getMessage());
  }
  
  public static void ensureLocationSet(TransformerException paramTransformerException) throws TransformerException {
    SourceLocator sourceLocator = null;
    Throwable throwable = paramTransformerException;
    do {
      if (throwable instanceof SAXParseException) {
        sourceLocator = new SAXSourceLocator((SAXParseException)throwable);
      } else if (throwable instanceof TransformerException) {
        SourceLocator sourceLocator1 = ((TransformerException)throwable).getLocator();
        if (null != sourceLocator1)
          sourceLocator = sourceLocator1; 
      } 
      if (throwable instanceof TransformerException) {
        throwable = ((TransformerException)throwable).getCause();
      } else if (throwable instanceof SAXException) {
        throwable = ((SAXException)throwable).getException();
      } else {
        throwable = null;
      } 
    } while (null != throwable);
    paramTransformerException.setLocator(sourceLocator);
  }
  
  public static void printLocation(PrintStream paramPrintStream, TransformerException paramTransformerException) { printLocation(new PrintWriter(paramPrintStream), paramTransformerException); }
  
  public static void printLocation(PrintStream paramPrintStream, SAXParseException paramSAXParseException) { printLocation(new PrintWriter(paramPrintStream), paramSAXParseException); }
  
  public static void printLocation(PrintWriter paramPrintWriter, Throwable paramThrowable) {
    SourceLocator sourceLocator = null;
    Throwable throwable = paramThrowable;
    do {
      if (throwable instanceof SAXParseException) {
        sourceLocator = new SAXSourceLocator((SAXParseException)throwable);
      } else if (throwable instanceof TransformerException) {
        SourceLocator sourceLocator1 = ((TransformerException)throwable).getLocator();
        if (null != sourceLocator1)
          sourceLocator = sourceLocator1; 
      } 
      if (throwable instanceof TransformerException) {
        throwable = ((TransformerException)throwable).getCause();
      } else if (throwable instanceof WrappedRuntimeException) {
        throwable = ((WrappedRuntimeException)throwable).getException();
      } else if (throwable instanceof SAXException) {
        throwable = ((SAXException)throwable).getException();
      } else {
        throwable = null;
      } 
    } while (null != throwable);
    if (null != sourceLocator) {
      String str = (null != sourceLocator.getPublicId()) ? sourceLocator.getPublicId() : ((null != sourceLocator.getSystemId()) ? sourceLocator.getSystemId() : XMLMessages.createXMLMessage("ER_SYSTEMID_UNKNOWN", null));
      paramPrintWriter.print(str + "; " + XMLMessages.createXMLMessage("line", null) + sourceLocator.getLineNumber() + "; " + XMLMessages.createXMLMessage("column", null) + sourceLocator.getColumnNumber() + "; ");
    } else {
      paramPrintWriter.print("(" + XMLMessages.createXMLMessage("ER_LOCATION_UNKNOWN", null) + ")");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\DefaultErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */