package java.lang;

public class ClassNotFoundException extends ReflectiveOperationException {
  private static final long serialVersionUID = 9176873029745254542L;
  
  private Throwable ex;
  
  public ClassNotFoundException() { super((Throwable)null); }
  
  public ClassNotFoundException(String paramString) { super(paramString, null); }
  
  public ClassNotFoundException(String paramString, Throwable paramThrowable) {
    super(paramString, null);
    this.ex = paramThrowable;
  }
  
  public Throwable getException() { return this.ex; }
  
  public Throwable getCause() { return this.ex; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ClassNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */