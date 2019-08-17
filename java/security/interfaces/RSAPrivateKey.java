package java.security.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

public interface RSAPrivateKey extends PrivateKey, RSAKey {
  public static final long serialVersionUID = 5187144804936595022L;
  
  BigInteger getPrivateExponent();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\interfaces\RSAPrivateKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */