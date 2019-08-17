package java.security;

public class ProviderException extends RuntimeException {
  private static final long serialVersionUID = 5256023526693665674L;
  
  public ProviderException() {}
  
  public ProviderException(String paramString) { super(paramString); }
  
  public ProviderException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  public ProviderException(Throwable paramThrowable) { super(paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\ProviderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */