package java.util;

public class ConcurrentModificationException extends RuntimeException {
  private static final long serialVersionUID = -3666751008965953603L;
  
  public ConcurrentModificationException() {}
  
  public ConcurrentModificationException(String paramString) { super(paramString); }
  
  public ConcurrentModificationException(Throwable paramThrowable) { super(paramThrowable); }
  
  public ConcurrentModificationException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\ConcurrentModificationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */