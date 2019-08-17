package java.lang;

public class AssertionError extends Error {
  private static final long serialVersionUID = -5013299493970297370L;
  
  public AssertionError() {}
  
  private AssertionError(String paramString) { super(paramString); }
  
  public AssertionError(Object paramObject) {
    this(String.valueOf(paramObject));
    if (paramObject instanceof Throwable)
      initCause((Throwable)paramObject); 
  }
  
  public AssertionError(boolean paramBoolean) { this(String.valueOf(paramBoolean)); }
  
  public AssertionError(char paramChar) { this(String.valueOf(paramChar)); }
  
  public AssertionError(int paramInt) { this(String.valueOf(paramInt)); }
  
  public AssertionError(long paramLong) { this(String.valueOf(paramLong)); }
  
  public AssertionError(float paramFloat) { this(String.valueOf(paramFloat)); }
  
  public AssertionError(double paramDouble) { this(String.valueOf(paramDouble)); }
  
  public AssertionError(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\AssertionError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */