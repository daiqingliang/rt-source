package java.security;

import java.security.spec.AlgorithmParameterSpec;

public class AlgorithmParameterGenerator {
  private Provider provider;
  
  private AlgorithmParameterGeneratorSpi paramGenSpi;
  
  private String algorithm;
  
  protected AlgorithmParameterGenerator(AlgorithmParameterGeneratorSpi paramAlgorithmParameterGeneratorSpi, Provider paramProvider, String paramString) {
    this.paramGenSpi = paramAlgorithmParameterGeneratorSpi;
    this.provider = paramProvider;
    this.algorithm = paramString;
  }
  
  public final String getAlgorithm() { return this.algorithm; }
  
  public static AlgorithmParameterGenerator getInstance(String paramString) throws NoSuchAlgorithmException {
    try {
      Object[] arrayOfObject = Security.getImpl(paramString, "AlgorithmParameterGenerator", (String)null);
      return new AlgorithmParameterGenerator((AlgorithmParameterGeneratorSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new NoSuchAlgorithmException(paramString + " not found");
    } 
  }
  
  public static AlgorithmParameterGenerator getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException {
    if (paramString2 == null || paramString2.length() == 0)
      throw new IllegalArgumentException("missing provider"); 
    Object[] arrayOfObject = Security.getImpl(paramString1, "AlgorithmParameterGenerator", paramString2);
    return new AlgorithmParameterGenerator((AlgorithmParameterGeneratorSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString1);
  }
  
  public static AlgorithmParameterGenerator getInstance(String paramString, Provider paramProvider) throws NoSuchAlgorithmException {
    if (paramProvider == null)
      throw new IllegalArgumentException("missing provider"); 
    Object[] arrayOfObject = Security.getImpl(paramString, "AlgorithmParameterGenerator", paramProvider);
    return new AlgorithmParameterGenerator((AlgorithmParameterGeneratorSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
  }
  
  public final Provider getProvider() { return this.provider; }
  
  public final void init(int paramInt) { this.paramGenSpi.engineInit(paramInt, new SecureRandom()); }
  
  public final void init(int paramInt, SecureRandom paramSecureRandom) { this.paramGenSpi.engineInit(paramInt, paramSecureRandom); }
  
  public final void init(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidAlgorithmParameterException { this.paramGenSpi.engineInit(paramAlgorithmParameterSpec, new SecureRandom()); }
  
  public final void init(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException { this.paramGenSpi.engineInit(paramAlgorithmParameterSpec, paramSecureRandom); }
  
  public final AlgorithmParameters generateParameters() { return this.paramGenSpi.engineGenerateParameters(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\AlgorithmParameterGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */