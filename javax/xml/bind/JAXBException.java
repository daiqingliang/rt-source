package javax.xml.bind;

import java.io.PrintStream;
import java.io.PrintWriter;

public class JAXBException extends Exception {
  private String errorCode;
  
  static final long serialVersionUID = -5621384651494307979L;
  
  public JAXBException(String paramString) { this(paramString, null, null); }
  
  public JAXBException(String paramString1, String paramString2) { this(paramString1, paramString2, null); }
  
  public JAXBException(Throwable paramThrowable) { this(null, null, paramThrowable); }
  
  public JAXBException(String paramString, Throwable paramThrowable) { this(paramString, null, paramThrowable); }
  
  public JAXBException(String paramString1, String paramString2, Throwable paramThrowable) {
    super(paramString1);
    this.errorCode = paramString2;
    this.linkedException = paramThrowable;
  }
  
  public String getErrorCode() { return this.errorCode; }
  
  public Throwable getLinkedException() { return this.linkedException; }
  
  public void setLinkedException(Throwable paramThrowable) { this.linkedException = paramThrowable; }
  
  public String toString() { return (this.linkedException == null) ? super.toString() : (super.toString() + "\n - with linked exception:\n[" + this.linkedException.toString() + "]"); }
  
  public void printStackTrace(PrintStream paramPrintStream) { super.printStackTrace(paramPrintStream); }
  
  public void printStackTrace() { super.printStackTrace(); }
  
  public void printStackTrace(PrintWriter paramPrintWriter) { super.printStackTrace(paramPrintWriter); }
  
  public Throwable getCause() { return this.linkedException; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\JAXBException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */