package java.security.interfaces;

import java.math.BigInteger;
import java.security.PrivateKey;

public interface ECPrivateKey extends PrivateKey, ECKey {
  public static final long serialVersionUID = -7896394956925609184L;
  
  BigInteger getS();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\interfaces\ECPrivateKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */