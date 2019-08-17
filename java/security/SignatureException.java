package java.security;

public class SignatureException extends GeneralSecurityException {
  private static final long serialVersionUID = 7509989324975124438L;
  
  public SignatureException() {}
  
  public SignatureException(String paramString) { super(paramString); }
  
  public SignatureException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  public SignatureException(Throwable paramThrowable) { super(paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\SignatureException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */