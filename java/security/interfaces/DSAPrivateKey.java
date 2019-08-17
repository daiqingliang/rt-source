package java.security.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

public interface DSAPrivateKey extends DSAKey, PrivateKey {
  public static final long serialVersionUID = 7776497482533790279L;
  
  BigInteger getX();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\interfaces\DSAPrivateKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */