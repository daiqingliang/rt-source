package javax.xml.crypto;

import java.io.PrintStream;
import java.io.PrintWriter;

public class URIReferenceException extends Exception {
  private static final long serialVersionUID = 7173469703932561419L;
  
  private Throwable cause;
  
  private URIReference uriReference;
  
  public URIReferenceException() {}
  
  public URIReferenceException(String paramString) { super(paramString); }
  
  public URIReferenceException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public URIReferenceException(String paramString, Throwable paramThrowable, URIReference paramURIReference) {
    this(paramString, paramThrowable);
    if (paramURIReference == null)
      throw new NullPointerException("uriReference cannot be null"); 
    this.uriReference = paramURIReference;
  }
  
  public URIReferenceException(Throwable paramThrowable) {
    super((paramThrowable == null) ? null : paramThrowable.toString());
    this.cause = paramThrowable;
  }
  
  public URIReference getURIReference() { return this.uriReference; }
  
  public Throwable getCause() { return this.cause; }
  
  public void printStackTrace() { super.printStackTrace(); }
  
  public void printStackTrace(PrintStream paramPrintStream) { super.printStackTrace(paramPrintStream); }
  
  public void printStackTrace(PrintWriter paramPrintWriter) { super.printStackTrace(paramPrintWriter); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\URIReferenceException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */