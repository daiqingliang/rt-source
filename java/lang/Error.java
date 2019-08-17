package java.lang;

public class Error extends Throwable {
  static final long serialVersionUID = 4980196508277280342L;
  
  public Error() {}
  
  public Error(String paramString) { super(paramString); }
  
  public Error(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  public Error(Throwable paramThrowable) { super(paramThrowable); }
  
  protected Error(String paramString, Throwable paramThrowable, boolean paramBoolean1, boolean paramBoolean2) { super(paramString, paramThrowable, paramBoolean1, paramBoolean2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Error.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */