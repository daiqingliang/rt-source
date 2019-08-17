package java.lang.reflect;

import java.lang.reflect.InvocationTargetException;

public class InvocationTargetException extends ReflectiveOperationException {
  private static final long serialVersionUID = 4085088731926701167L;
  
  private Throwable target;
  
  protected InvocationTargetException() { super((Throwable)null); }
  
  public InvocationTargetException(Throwable paramThrowable) {
    super((Throwable)null);
    this.target = paramThrowable;
  }
  
  public InvocationTargetException(Throwable paramThrowable, String paramString) {
    super(paramString, null);
    this.target = paramThrowable;
  }
  
  public Throwable getTargetException() { return this.target; }
  
  public Throwable getCause() { return this.target; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\InvocationTargetException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */