package java.util.concurrent;

public class ExecutionException extends Exception {
  private static final long serialVersionUID = 7830266012832686185L;
  
  protected ExecutionException() {}
  
  protected ExecutionException(String paramString) { super(paramString); }
  
  public ExecutionException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  public ExecutionException(Throwable paramThrowable) { super(paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\ExecutionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */