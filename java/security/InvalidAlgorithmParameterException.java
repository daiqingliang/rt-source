package java.security;

public class InvalidAlgorithmParameterException extends GeneralSecurityException {
  private static final long serialVersionUID = 2864672297499471472L;
  
  public InvalidAlgorithmParameterException() {}
  
  public InvalidAlgorithmParameterException(String paramString) { super(paramString); }
  
  public InvalidAlgorithmParameterException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  public InvalidAlgorithmParameterException(Throwable paramThrowable) { super(paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\InvalidAlgorithmParameterException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */