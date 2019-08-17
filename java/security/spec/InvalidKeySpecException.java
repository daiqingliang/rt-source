package java.security.spec;

import java.security.GeneralSecurityException;

public class InvalidKeySpecException extends GeneralSecurityException {
  private static final long serialVersionUID = 3546139293998810778L;
  
  public InvalidKeySpecException() {}
  
  public InvalidKeySpecException(String paramString) { super(paramString); }
  
  public InvalidKeySpecException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  public InvalidKeySpecException(Throwable paramThrowable) { super(paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\InvalidKeySpecException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */