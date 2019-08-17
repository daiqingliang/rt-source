package java.lang.invoke;

public class WrongMethodTypeException extends RuntimeException {
  private static final long serialVersionUID = 292L;
  
  public WrongMethodTypeException() {}
  
  public WrongMethodTypeException(String paramString) { super(paramString); }
  
  WrongMethodTypeException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  WrongMethodTypeException(Throwable paramThrowable) { super(paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\WrongMethodTypeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */