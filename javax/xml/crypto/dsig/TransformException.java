package javax.xml.crypto.dsig;

import java.io.PrintStream;
import java.io.PrintWriter;

public class TransformException extends Exception {
  private static final long serialVersionUID = 5082634801360427800L;
  
  private Throwable cause;
  
  public TransformException() {}
  
  public TransformException(String paramString) { super(paramString); }
  
  public TransformException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public TransformException(Throwable paramThrowable) {
    super((paramThrowable == null) ? null : paramThrowable.toString());
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() { return this.cause; }
  
  public void printStackTrace() {
    super.printStackTrace();
    if (this.cause != null)
      this.cause.printStackTrace(); 
  }
  
  public void printStackTrace(PrintStream paramPrintStream) {
    super.printStackTrace(paramPrintStream);
    if (this.cause != null)
      this.cause.printStackTrace(paramPrintStream); 
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter) {
    super.printStackTrace(paramPrintWriter);
    if (this.cause != null)
      this.cause.printStackTrace(paramPrintWriter); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\TransformException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */