package java.security;

import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Iterator;
import java.util.List;
import sun.security.jca.GetInstance;
import sun.security.util.Debug;

public class KeyFactory {
  private static final Debug debug = Debug.getInstance("jca", "KeyFactory");
  
  private final String algorithm;
  
  private Provider provider;
  
  private final Object lock = new Object();
  
  private Iterator<Provider.Service> serviceIterator;
  
  protected KeyFactory(KeyFactorySpi paramKeyFactorySpi, Provider paramProvider, String paramString) {
    this.spi = paramKeyFactorySpi;
    this.provider = paramProvider;
    this.algorithm = paramString;
  }
  
  private KeyFactory(String paramString) throws NoSuchAlgorithmException {
    this.algorithm = paramString;
    List list = GetInstance.getServices("KeyFactory", paramString);
    this.serviceIterator = list.iterator();
    if (nextSpi(null) == null)
      throw new NoSuchAlgorithmException(paramString + " KeyFactory not available"); 
  }
  
  public static KeyFactory getInstance(String paramString) throws NoSuchAlgorithmException { return new KeyFactory(paramString); }
  
  public static KeyFactory getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException {
    GetInstance.Instance instance = GetInstance.getInstance("KeyFactory", KeyFactorySpi.class, paramString1, paramString2);
    return new KeyFactory((KeyFactorySpi)instance.impl, instance.provider, paramString1);
  }
  
  public static KeyFactory getInstance(String paramString, Provider paramProvider) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = GetInstance.getInstance("KeyFactory", KeyFactorySpi.class, paramString, paramProvider);
    return new KeyFactory((KeyFactorySpi)instance.impl, instance.provider, paramString);
  }
  
  public final Provider getProvider() {
    synchronized (this.lock) {
      this.serviceIterator = null;
      return this.provider;
    } 
  }
  
  public final String getAlgorithm() { return this.algorithm; }
  
  private KeyFactorySpi nextSpi(KeyFactorySpi paramKeyFactorySpi) {
    synchronized (this.lock) {
      if (paramKeyFactorySpi != null && paramKeyFactorySpi != this.spi)
        return this.spi; 
      if (this.serviceIterator == null)
        return null; 
      while (this.serviceIterator.hasNext()) {
        Provider.Service service = (Provider.Service)this.serviceIterator.next();
        try {
          Object object = service.newInstance(null);
          if (!(object instanceof KeyFactorySpi))
            continue; 
          KeyFactorySpi keyFactorySpi = (KeyFactorySpi)object;
          this.provider = service.getProvider();
          this.spi = keyFactorySpi;
          return keyFactorySpi;
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {}
      } 
      this.serviceIterator = null;
      return null;
    } 
  }
  
  public final PublicKey generatePublic(KeySpec paramKeySpec) throws InvalidKeySpecException {
    if (this.serviceIterator == null)
      return this.spi.engineGeneratePublic(paramKeySpec); 
    Exception exception = null;
    KeyFactorySpi keyFactorySpi = this.spi;
    while (true) {
      try {
        return keyFactorySpi.engineGeneratePublic(paramKeySpec);
      } catch (Exception exception1) {
        if (exception == null)
          exception = exception1; 
        keyFactorySpi = nextSpi(keyFactorySpi);
        if (keyFactorySpi == null)
          break; 
      } 
    } 
    if (exception instanceof RuntimeException)
      throw (RuntimeException)exception; 
    if (exception instanceof InvalidKeySpecException)
      throw (InvalidKeySpecException)exception; 
    throw new InvalidKeySpecException("Could not generate public key", exception);
  }
  
  public final PrivateKey generatePrivate(KeySpec paramKeySpec) throws InvalidKeySpecException {
    if (this.serviceIterator == null)
      return this.spi.engineGeneratePrivate(paramKeySpec); 
    Exception exception = null;
    KeyFactorySpi keyFactorySpi = this.spi;
    while (true) {
      try {
        return keyFactorySpi.engineGeneratePrivate(paramKeySpec);
      } catch (Exception exception1) {
        if (exception == null)
          exception = exception1; 
        keyFactorySpi = nextSpi(keyFactorySpi);
        if (keyFactorySpi == null)
          break; 
      } 
    } 
    if (exception instanceof RuntimeException)
      throw (RuntimeException)exception; 
    if (exception instanceof InvalidKeySpecException)
      throw (InvalidKeySpecException)exception; 
    throw new InvalidKeySpecException("Could not generate private key", exception);
  }
  
  public final <T extends KeySpec> T getKeySpec(Key paramKey, Class<T> paramClass) throws InvalidKeySpecException {
    if (this.serviceIterator == null)
      return (T)this.spi.engineGetKeySpec(paramKey, paramClass); 
    Exception exception = null;
    KeyFactorySpi keyFactorySpi = this.spi;
    while (true) {
      try {
        return (T)keyFactorySpi.engineGetKeySpec(paramKey, paramClass);
      } catch (Exception exception1) {
        if (exception == null)
          exception = exception1; 
        keyFactorySpi = nextSpi(keyFactorySpi);
        if (keyFactorySpi == null)
          break; 
      } 
    } 
    if (exception instanceof RuntimeException)
      throw (RuntimeException)exception; 
    if (exception instanceof InvalidKeySpecException)
      throw (InvalidKeySpecException)exception; 
    throw new InvalidKeySpecException("Could not get key spec", exception);
  }
  
  public final Key translateKey(Key paramKey) throws InvalidKeyException {
    if (this.serviceIterator == null)
      return this.spi.engineTranslateKey(paramKey); 
    Exception exception = null;
    KeyFactorySpi keyFactorySpi = this.spi;
    while (true) {
      try {
        return keyFactorySpi.engineTranslateKey(paramKey);
      } catch (Exception exception1) {
        if (exception == null)
          exception = exception1; 
        keyFactorySpi = nextSpi(keyFactorySpi);
        if (keyFactorySpi == null)
          break; 
      } 
    } 
    if (exception instanceof RuntimeException)
      throw (RuntimeException)exception; 
    if (exception instanceof InvalidKeyException)
      throw (InvalidKeyException)exception; 
    throw new InvalidKeyException("Could not translate key", exception);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\KeyFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */