package sun.security.krb5.internal;

import sun.security.krb5.KrbException;

public class KdcErrException extends KrbException {
  private static final long serialVersionUID = -8788186031117310306L;
  
  public KdcErrException(int paramInt) { super(paramInt); }
  
  public KdcErrException(int paramInt, String paramString) { super(paramInt, paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\KdcErrException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */