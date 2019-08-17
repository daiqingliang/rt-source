package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ListingErrorHandler implements ErrorHandler, ErrorListener {
  protected PrintWriter m_pw = null;
  
  protected boolean throwOnWarning = false;
  
  protected boolean throwOnError = true;
  
  protected boolean throwOnFatalError = true;
  
  public ListingErrorHandler(PrintWriter paramPrintWriter) {
    if (null == paramPrintWriter)
      throw new NullPointerException(XMLMessages.createXMLMessage("ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER", null)); 
    this.m_pw = paramPrintWriter;
  }
  
  public ListingErrorHandler() { this.m_pw = new PrintWriter(System.err, true); }
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException {
    logExceptionLocation(this.m_pw, paramSAXParseException);
    this.m_pw.println("warning: " + paramSAXParseException.getMessage());
    this.m_pw.flush();
    if (getThrowOnWarning())
      throw paramSAXParseException; 
  }
  
  public void error(SAXParseException paramSAXParseException) throws SAXException {
    logExceptionLocation(this.m_pw, paramSAXParseException);
    this.m_pw.println("error: " + paramSAXParseException.getMessage());
    this.m_pw.flush();
    if (getThrowOnError())
      throw paramSAXParseException; 
  }
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException {
    logExceptionLocation(this.m_pw, paramSAXParseException);
    this.m_pw.println("fatalError: " + paramSAXParseException.getMessage());
    this.m_pw.flush();
    if (getThrowOnFatalError())
      throw paramSAXParseException; 
  }
  
  public void warning(TransformerException paramTransformerException) throws TransformerException {
    logExceptionLocation(this.m_pw, paramTransformerException);
    this.m_pw.println("warning: " + paramTransformerException.getMessage());
    this.m_pw.flush();
    if (getThrowOnWarning())
      throw paramTransformerException; 
  }
  
  public void error(TransformerException paramTransformerException) throws TransformerException {
    logExceptionLocation(this.m_pw, paramTransformerException);
    this.m_pw.println("error: " + paramTransformerException.getMessage());
    this.m_pw.flush();
    if (getThrowOnError())
      throw paramTransformerException; 
  }
  
  public void fatalError(TransformerException paramTransformerException) throws TransformerException {
    logExceptionLocation(this.m_pw, paramTransformerException);
    this.m_pw.println("error: " + paramTransformerException.getMessage());
    this.m_pw.flush();
    if (getThrowOnError())
      throw paramTransformerException; 
  }
  
  public static void logExceptionLocation(PrintWriter paramPrintWriter, Throwable paramThrowable) {
    if (null == paramPrintWriter)
      paramPrintWriter = new PrintWriter(System.err, true); 
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
      String str = (sourceLocator.getPublicId() != sourceLocator.getPublicId()) ? sourceLocator.getPublicId() : ((null != sourceLocator.getSystemId()) ? sourceLocator.getSystemId() : "SystemId-Unknown");
      paramPrintWriter.print(str + ":Line=" + sourceLocator.getLineNumber() + ";Column=" + sourceLocator.getColumnNumber() + ": ");
      paramPrintWriter.println("exception:" + paramThrowable.getMessage());
      paramPrintWriter.println("root-cause:" + ((null != throwable) ? throwable.getMessage() : "null"));
      logSourceLine(paramPrintWriter, sourceLocator);
    } else {
      paramPrintWriter.print("SystemId-Unknown:locator-unavailable: ");
      paramPrintWriter.println("exception:" + paramThrowable.getMessage());
      paramPrintWriter.println("root-cause:" + ((null != throwable) ? throwable.getMessage() : "null"));
    } 
  }
  
  public static void logSourceLine(PrintWriter paramPrintWriter, SourceLocator paramSourceLocator) {
    if (null == paramSourceLocator)
      return; 
    if (null == paramPrintWriter)
      paramPrintWriter = new PrintWriter(System.err, true); 
    String str = paramSourceLocator.getSystemId();
    if (null == str) {
      paramPrintWriter.println("line: (No systemId; cannot read file)");
      paramPrintWriter.println();
      return;
    } 
    try {
      int i = paramSourceLocator.getLineNumber();
      int j = paramSourceLocator.getColumnNumber();
      paramPrintWriter.println("line: " + getSourceLine(str, i));
      StringBuffer stringBuffer = new StringBuffer("line: ");
      for (byte b = 1; b < j; b++)
        stringBuffer.append(' '); 
      stringBuffer.append('^');
      paramPrintWriter.println(stringBuffer.toString());
    } catch (Exception exception) {
      paramPrintWriter.println("line: logSourceLine unavailable due to: " + exception.getMessage());
      paramPrintWriter.println();
    } 
  }
  
  protected static String getSourceLine(String paramString, int paramInt) throws Exception {
    URL uRL = null;
    try {
      uRL = new URL(paramString);
    } catch (MalformedURLException malformedURLException) {
      int i = paramString.indexOf(':');
      int j = paramString.indexOf('/');
      if (i != -1 && j != -1 && i < j)
        throw malformedURLException; 
      uRL = new URL(SystemIDResolver.getAbsoluteURI(paramString));
    } 
    String str = null;
    inputStream = null;
    bufferedReader = null;
    try {
      URLConnection uRLConnection = uRL.openConnection();
      inputStream = uRLConnection.getInputStream();
      bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      for (byte b = 1; b <= paramInt; b++)
        str = bufferedReader.readLine(); 
    } finally {
      bufferedReader.close();
      inputStream.close();
    } 
    return str;
  }
  
  public void setThrowOnWarning(boolean paramBoolean) { this.throwOnWarning = paramBoolean; }
  
  public boolean getThrowOnWarning() { return this.throwOnWarning; }
  
  public void setThrowOnError(boolean paramBoolean) { this.throwOnError = paramBoolean; }
  
  public boolean getThrowOnError() { return this.throwOnError; }
  
  public void setThrowOnFatalError(boolean paramBoolean) { this.throwOnFatalError = paramBoolean; }
  
  public boolean getThrowOnFatalError() { return this.throwOnFatalError; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\ListingErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */