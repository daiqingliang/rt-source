package java.lang;

public class RuntimeException extends Exception {
  static final long serialVersionUID = -7034897190745766939L;
  
  public RuntimeException() {}
  
  public RuntimeException(String paramString) { super(paramString); }
  
  public RuntimeException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  public RuntimeException(Throwable paramThrowable) { super(paramThrowable); }
  
  protected RuntimeException(String paramString, Throwable paramThrowable, boolean paramBoolean1, boolean paramBoolean2) { super(paramString, paramThrowable, paramBoolean1, paramBoolean2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\RuntimeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */