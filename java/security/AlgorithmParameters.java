package java.security;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;

public class AlgorithmParameters {
  private Provider provider;
  
  private AlgorithmParametersSpi paramSpi;
  
  private String algorithm;
  
  private boolean initialized = false;
  
  protected AlgorithmParameters(AlgorithmParametersSpi paramAlgorithmParametersSpi, Provider paramProvider, String paramString) {
    this.paramSpi = paramAlgorithmParametersSpi;
    this.provider = paramProvider;
    this.algorithm = paramString;
  }
  
  public final String getAlgorithm() { return this.algorithm; }
  
  public static AlgorithmParameters getInstance(String paramString) throws NoSuchAlgorithmException {
    try {
      Object[] arrayOfObject = Security.getImpl(paramString, "AlgorithmParameters", (String)null);
      return new AlgorithmParameters((AlgorithmParametersSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new NoSuchAlgorithmException(paramString + " not found");
    } 
  }
  
  public static AlgorithmParameters getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException {
    if (paramString2 == null || paramString2.length() == 0)
      throw new IllegalArgumentException("missing provider"); 
    Object[] arrayOfObject = Security.getImpl(paramString1, "AlgorithmParameters", paramString2);
    return new AlgorithmParameters((AlgorithmParametersSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString1);
  }
  
  public static AlgorithmParameters getInstance(String paramString, Provider paramProvider) throws NoSuchAlgorithmException {
    if (paramProvider == null)
      throw new IllegalArgumentException("missing provider"); 
    Object[] arrayOfObject = Security.getImpl(paramString, "AlgorithmParameters", paramProvider);
    return new AlgorithmParameters((AlgorithmParametersSpi)arrayOfObject[0], (Provider)arrayOfObject[1], paramString);
  }
  
  public final Provider getProvider() { return this.provider; }
  
  public final void init(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidParameterSpecException {
    if (this.initialized)
      throw new InvalidParameterSpecException("already initialized"); 
    this.paramSpi.engineInit(paramAlgorithmParameterSpec);
    this.initialized = true;
  }
  
  public final void init(byte[] paramArrayOfByte) throws IOException {
    if (this.initialized)
      throw new IOException("already initialized"); 
    this.paramSpi.engineInit(paramArrayOfByte);
    this.initialized = true;
  }
  
  public final void init(byte[] paramArrayOfByte, String paramString) throws IOException {
    if (this.initialized)
      throw new IOException("already initialized"); 
    this.paramSpi.engineInit(paramArrayOfByte, paramString);
    this.initialized = true;
  }
  
  public final <T extends AlgorithmParameterSpec> T getParameterSpec(Class<T> paramClass) throws InvalidParameterSpecException {
    if (!this.initialized)
      throw new InvalidParameterSpecException("not initialized"); 
    return (T)this.paramSpi.engineGetParameterSpec(paramClass);
  }
  
  public final byte[] getEncoded() throws IOException {
    if (!this.initialized)
      throw new IOException("not initialized"); 
    return this.paramSpi.engineGetEncoded();
  }
  
  public final byte[] getEncoded(String paramString) throws IOException {
    if (!this.initialized)
      throw new IOException("not initialized"); 
    return this.paramSpi.engineGetEncoded(paramString);
  }
  
  public final String toString() { return !this.initialized ? null : this.paramSpi.engineToString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\AlgorithmParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */