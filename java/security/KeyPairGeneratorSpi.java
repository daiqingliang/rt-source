package java.security;

import java.security.spec.AlgorithmParameterSpec;

public abstract class KeyPairGeneratorSpi {
  public abstract void initialize(int paramInt, SecureRandom paramSecureRandom);
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException { throw new UnsupportedOperationException(); }
  
  public abstract KeyPair generateKeyPair();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\KeyPairGeneratorSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */