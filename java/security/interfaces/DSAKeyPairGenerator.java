package java.security.interfaces;

import java.security.InvalidParameterException;
import java.security.SecureRandom;

public interface DSAKeyPairGenerator {
  void initialize(DSAParams paramDSAParams, SecureRandom paramSecureRandom) throws InvalidParameterException;
  
  void initialize(int paramInt, boolean paramBoolean, SecureRandom paramSecureRandom) throws InvalidParameterException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\interfaces\DSAKeyPairGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */