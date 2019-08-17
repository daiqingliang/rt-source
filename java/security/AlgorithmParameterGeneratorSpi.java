package java.security;

import java.security.spec.AlgorithmParameterSpec;

public abstract class AlgorithmParameterGeneratorSpi {
  protected abstract void engineInit(int paramInt, SecureRandom paramSecureRandom);
  
  protected abstract void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException;
  
  protected abstract AlgorithmParameters engineGenerateParameters();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\AlgorithmParameterGeneratorSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */