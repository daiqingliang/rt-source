package java.awt.print;

import java.io.IOException;

public class PrinterIOException extends PrinterException {
  static final long serialVersionUID = 5850870712125932846L;
  
  private IOException mException;
  
  public PrinterIOException(IOException paramIOException) {
    initCause(null);
    this.mException = paramIOException;
  }
  
  public IOException getIOException() { return this.mException; }
  
  public Throwable getCause() { return this.mException; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\print\PrinterIOException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */