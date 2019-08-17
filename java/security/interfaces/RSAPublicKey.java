package java.security.interfaces;

import java.math.BigInteger;
import java.security.PublicKey;

public interface RSAPublicKey extends PublicKey, RSAKey {
  public static final long serialVersionUID = -8727434096241101194L;
  
  BigInteger getPublicExponent();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\interfaces\RSAPublicKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */