package com.sun.xml.internal.messaging.saaj;

import java.io.PrintStream;
import java.io.PrintWriter;
import javax.xml.soap.SOAPException;

public class SOAPExceptionImpl extends SOAPException {
  private Throwable cause;
  
  public SOAPExceptionImpl() { this.cause = null; }
  
  public SOAPExceptionImpl(String paramString) {
    super(paramString);
    this.cause = null;
  }
  
  public SOAPExceptionImpl(String paramString, Throwable paramThrowable) {
    super(paramString);
    initCause(paramThrowable);
  }
  
  public SOAPExceptionImpl(Throwable paramThrowable) {
    super(paramThrowable.toString());
    initCause(paramThrowable);
  }
  
  public String getMessage() {
    String str = super.getMessage();
    return (str == null && this.cause != null) ? this.cause.getMessage() : str;
  }
  
  public Throwable getCause() { return this.cause; }
  
  public Throwable initCause(Throwable paramThrowable) {
    if (this.cause != null)
      throw new IllegalStateException("Can't override cause"); 
    if (paramThrowable == this)
      throw new IllegalArgumentException("Self-causation not permitted"); 
    this.cause = paramThrowable;
    return this;
  }
  
  public void printStackTrace() {
    super.printStackTrace();
    if (this.cause != null) {
      System.err.println("\nCAUSE:\n");
      this.cause.printStackTrace();
    } 
  }
  
  public void printStackTrace(PrintStream paramPrintStream) {
    super.printStackTrace(paramPrintStream);
    if (this.cause != null) {
      paramPrintStream.println("\nCAUSE:\n");
      this.cause.printStackTrace(paramPrintStream);
    } 
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter) {
    super.printStackTrace(paramPrintWriter);
    if (this.cause != null) {
      paramPrintWriter.println("\nCAUSE:\n");
      this.cause.printStackTrace(paramPrintWriter);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\SOAPExceptionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */