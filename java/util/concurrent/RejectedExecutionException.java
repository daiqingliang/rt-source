package java.util.concurrent;

public class RejectedExecutionException extends RuntimeException {
  private static final long serialVersionUID = -375805702767069545L;
  
  public RejectedExecutionException() {}
  
  public RejectedExecutionException(String paramString) { super(paramString); }
  
  public RejectedExecutionException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  public RejectedExecutionException(Throwable paramThrowable) { super(paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\RejectedExecutionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */