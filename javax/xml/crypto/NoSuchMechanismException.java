package javax.xml.crypto;

import java.io.PrintStream;
import java.io.PrintWriter;

public class NoSuchMechanismException extends RuntimeException {
  private static final long serialVersionUID = 4189669069570660166L;
  
  private Throwable cause;
  
  public NoSuchMechanismException() {}
  
  public NoSuchMechanismException(String paramString) { super(paramString); }
  
  public NoSuchMechanismException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public NoSuchMechanismException(Throwable paramThrowable) {
    super((paramThrowable == null) ? null : paramThrowable.toString());
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() { return this.cause; }
  
  public void printStackTrace() { super.printStackTrace(); }
  
  public void printStackTrace(PrintStream paramPrintStream) { super.printStackTrace(paramPrintStream); }
  
  public void printStackTrace(PrintWriter paramPrintWriter) { super.printStackTrace(paramPrintWriter); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\NoSuchMechanismException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */