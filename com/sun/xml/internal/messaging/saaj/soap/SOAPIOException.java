package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class SOAPIOException extends IOException {
  SOAPExceptionImpl soapException = new SOAPExceptionImpl();
  
  public SOAPIOException() { this.soapException.fillInStackTrace(); }
  
  public SOAPIOException(String paramString) { this.soapException.fillInStackTrace(); }
  
  public SOAPIOException(String paramString, Throwable paramThrowable) { this.soapException.fillInStackTrace(); }
  
  public SOAPIOException(Throwable paramThrowable) {
    super(paramThrowable.toString());
    this.soapException.fillInStackTrace();
  }
  
  public Throwable fillInStackTrace() {
    if (this.soapException != null)
      this.soapException.fillInStackTrace(); 
    return this;
  }
  
  public String getLocalizedMessage() { return this.soapException.getLocalizedMessage(); }
  
  public String getMessage() { return this.soapException.getMessage(); }
  
  public void printStackTrace() { this.soapException.printStackTrace(); }
  
  public void printStackTrace(PrintStream paramPrintStream) { this.soapException.printStackTrace(paramPrintStream); }
  
  public void printStackTrace(PrintWriter paramPrintWriter) { this.soapException.printStackTrace(paramPrintWriter); }
  
  public String toString() { return this.soapException.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\SOAPIOException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */