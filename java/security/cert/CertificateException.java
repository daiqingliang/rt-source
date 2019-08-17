package java.security.cert;

import java.security.GeneralSecurityException;

public class CertificateException extends GeneralSecurityException {
  private static final long serialVersionUID = 3192535253797119798L;
  
  public CertificateException() {}
  
  public CertificateException(String paramString) { super(paramString); }
  
  public CertificateException(String paramString, Throwable paramThrowable) { super(paramString, paramThrowable); }
  
  public CertificateException(Throwable paramThrowable) { super(paramThrowable); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\CertificateException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */