package java.lang;

public class SecurityException extends RuntimeException {
  private static final long serialVersionUID = 6878364983674394167L;
  
  public SecurityException() {}
  
  public SecurityException(String paramString) { super(paramString); }
  
  public SecurityException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  public SecurityException(Throwable paramThrowable) { super(paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\SecurityException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */