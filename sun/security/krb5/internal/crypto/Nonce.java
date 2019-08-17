package sun.security.krb5.internal.crypto;

import sun.security.krb5.Confounder;

public class Nonce {
  public static int value() { return Confounder.intValue() & 0x7FFFFFFF; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\crypto\Nonce.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */