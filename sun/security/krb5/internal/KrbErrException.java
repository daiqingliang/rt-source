package sun.security.krb5.internal;

import sun.security.krb5.KrbException;

public class KrbErrException extends KrbException {
  private static final long serialVersionUID = 2186533836785448317L;
  
  public KrbErrException(int paramInt) { super(paramInt); }
  
  public KrbErrException(int paramInt, String paramString) { super(paramInt, paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\KrbErrException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */