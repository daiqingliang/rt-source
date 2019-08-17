package java.lang.reflect;

import java.lang.reflect.UndeclaredThrowableException;

public class UndeclaredThrowableException extends RuntimeException {
  static final long serialVersionUID = 330127114055056639L;
  
  private Throwable undeclaredThrowable;
  
  public UndeclaredThrowableException(Throwable paramThrowable) {
    super((Throwable)null);
    this.undeclaredThrowable = paramThrowable;
  }
  
  public UndeclaredThrowableException(Throwable paramThrowable, String paramString) {
    super(paramString, null);
    this.undeclaredThrowable = paramThrowable;
  }
  
  public Throwable getUndeclaredThrowable() { return this.undeclaredThrowable; }
  
  public Throwable getCause() { return this.undeclaredThrowable; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\UndeclaredThrowableException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */