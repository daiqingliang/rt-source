package java.security.interfaces;

import java.security.PublicKey;
import java.security.spec.ECPoint;

public interface ECPublicKey extends PublicKey, ECKey {
  public static final long serialVersionUID = -3314988629879632826L;
  
  ECPoint getW();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\interfaces\ECPublicKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */