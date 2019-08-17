package java.util.concurrent;

public class CompletionException extends RuntimeException {
  private static final long serialVersionUID = 7830266012832686185L;
  
  protected CompletionException() {}
  
  protected CompletionException(String paramString) { super(paramString); }
  
  public CompletionException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  public CompletionException(Throwable paramThrowable) { super(paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\CompletionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */