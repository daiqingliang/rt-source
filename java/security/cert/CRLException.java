package java.security.cert;

import java.security.GeneralSecurityException;

public class CRLException extends GeneralSecurityException {
  private static final long serialVersionUID = -6694728944094197147L;
  
  public CRLException() {}
  
  public CRLException(String paramString) { super(paramString); }
  
  public CRLException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  public CRLException(Throwable paramThrowable) { super(paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\CRLException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */