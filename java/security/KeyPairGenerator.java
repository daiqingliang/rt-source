package java.security;

import java.security.spec.AlgorithmParameterSpec;
import java.util.Iterator;
import java.util.List;
import sun.security.jca.GetInstance;
import sun.security.jca.JCAUtil;
import sun.security.util.Debug;

public abstract class KeyPairGenerator extends KeyPairGeneratorSpi {
  private static final Debug pdebug;
  
  private static final boolean skipDebug = ((pdebug = Debug.getInstance("provider", "Provider")).isOn("engine=") && !Debug.isOn("keypairgenerator"));
  
  private final String algorithm;
  
  Provider provider;
  
  protected KeyPairGenerator(String paramString) { this.algorithm = paramString; }
  
  public String getAlgorithm() { return this.algorithm; }
  
  private static KeyPairGenerator getInstance(GetInstance.Instance paramInstance, String paramString) {
    Delegate delegate;
    if (paramInstance.impl instanceof KeyPairGenerator) {
      delegate = (KeyPairGenerator)paramInstance.impl;
    } else {
      KeyPairGeneratorSpi keyPairGeneratorSpi = (KeyPairGeneratorSpi)paramInstance.impl;
      delegate = new Delegate(keyPairGeneratorSpi, paramString);
    } 
    delegate.provider = paramInstance.provider;
    if (!skipDebug && pdebug != null)
      pdebug.println("KeyPairGenerator." + paramString + " algorithm from: " + delegate.provider.getName()); 
    return delegate;
  }
  
  public static KeyPairGenerator getInstance(String paramString) throws NoSuchAlgorithmException {
    List list = GetInstance.getServices("KeyPairGenerator", paramString);
    Iterator iterator = list.iterator();
    if (!iterator.hasNext())
      throw new NoSuchAlgorithmException(paramString + " KeyPairGenerator not available"); 
    NoSuchAlgorithmException noSuchAlgorithmException = null;
    while (true) {
      Provider.Service service = (Provider.Service)iterator.next();
      try {
        GetInstance.Instance instance = GetInstance.getInstance(service, KeyPairGeneratorSpi.class);
        return (instance.impl instanceof KeyPairGenerator) ? getInstance(instance, paramString) : new Delegate(instance, iterator, paramString);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException1) {
        if (noSuchAlgorithmException == null)
          noSuchAlgorithmException = noSuchAlgorithmException1; 
        if (!iterator.hasNext())
          break; 
      } 
    } 
    throw noSuchAlgorithmException;
  }
  
  public static KeyPairGenerator getInstance(String paramString1, String paramString2) throws NoSuchAlgorithmException, NoSuchProviderException {
    GetInstance.Instance instance = GetInstance.getInstance("KeyPairGenerator", KeyPairGeneratorSpi.class, paramString1, paramString2);
    return getInstance(instance, paramString1);
  }
  
  public static KeyPairGenerator getInstance(String paramString, Provider paramProvider) throws NoSuchAlgorithmException {
    GetInstance.Instance instance = GetInstance.getInstance("KeyPairGenerator", KeyPairGeneratorSpi.class, paramString, paramProvider);
    return getInstance(instance, paramString);
  }
  
  public final Provider getProvider() {
    disableFailover();
    return this.provider;
  }
  
  void disableFailover() {}
  
  public void initialize(int paramInt) { initialize(paramInt, JCAUtil.getSecureRandom()); }
  
  public void initialize(int paramInt, SecureRandom paramSecureRandom) {}
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidAlgorithmParameterException { initialize(paramAlgorithmParameterSpec, JCAUtil.getSecureRandom()); }
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {}
  
  public final KeyPair genKeyPair() { return generateKeyPair(); }
  
  public KeyPair generateKeyPair() { return null; }
  
  private static final class Delegate extends KeyPairGenerator {
    private final Object lock = new Object();
    
    private Iterator<Provider.Service> serviceIterator;
    
    private static final int I_NONE = 1;
    
    private static final int I_SIZE = 2;
    
    private static final int I_PARAMS = 3;
    
    private int initType;
    
    private int initKeySize;
    
    private AlgorithmParameterSpec initParams;
    
    private SecureRandom initRandom;
    
    Delegate(KeyPairGeneratorSpi param1KeyPairGeneratorSpi, String param1String) {
      super(param1String);
      this.spi = param1KeyPairGeneratorSpi;
    }
    
    Delegate(GetInstance.Instance param1Instance, Iterator<Provider.Service> param1Iterator, String param1String) {
      super(param1String);
      this.spi = (KeyPairGeneratorSpi)param1Instance.impl;
      this.provider = param1Instance.provider;
      this.serviceIterator = param1Iterator;
      this.initType = 1;
      if (!skipDebug && pdebug != null)
        pdebug.println("KeyPairGenerator." + param1String + " algorithm from: " + this.provider.getName()); 
    }
    
    private KeyPairGeneratorSpi nextSpi(KeyPairGeneratorSpi param1KeyPairGeneratorSpi, boolean param1Boolean) {
      synchronized (this.lock) {
        if (param1KeyPairGeneratorSpi != null && param1KeyPairGeneratorSpi != this.spi)
          return this.spi; 
        if (this.serviceIterator == null)
          return null; 
        while (this.serviceIterator.hasNext()) {
          Provider.Service service = (Provider.Service)this.serviceIterator.next();
          try {
            Object object = service.newInstance(null);
            if (!(object instanceof KeyPairGeneratorSpi) || object instanceof KeyPairGenerator)
              continue; 
            KeyPairGeneratorSpi keyPairGeneratorSpi = (KeyPairGeneratorSpi)object;
            if (param1Boolean)
              if (this.initType == 2) {
                keyPairGeneratorSpi.initialize(this.initKeySize, this.initRandom);
              } else if (this.initType == 3) {
                keyPairGeneratorSpi.initialize(this.initParams, this.initRandom);
              } else if (this.initType != 1) {
                throw new AssertionError("KeyPairGenerator initType: " + this.initType);
              }  
            this.provider = service.getProvider();
            this.spi = keyPairGeneratorSpi;
            return keyPairGeneratorSpi;
          } catch (Exception exception) {}
        } 
        disableFailover();
        return null;
      } 
    }
    
    void disableFailover() {
      this.serviceIterator = null;
      this.initType = 0;
      this.initParams = null;
      this.initRandom = null;
    }
    
    public void initialize(int param1Int, SecureRandom param1SecureRandom) {
      if (this.serviceIterator == null) {
        this.spi.initialize(param1Int, param1SecureRandom);
        return;
      } 
      RuntimeException runtimeException = null;
      KeyPairGeneratorSpi keyPairGeneratorSpi = this.spi;
      while (true) {
        try {
          keyPairGeneratorSpi.initialize(param1Int, param1SecureRandom);
          this.initType = 2;
          this.initKeySize = param1Int;
          this.initParams = null;
          this.initRandom = param1SecureRandom;
          return;
        } catch (RuntimeException runtimeException1) {
          if (runtimeException == null)
            runtimeException = runtimeException1; 
          keyPairGeneratorSpi = nextSpi(keyPairGeneratorSpi, false);
          if (keyPairGeneratorSpi == null)
            break; 
        } 
      } 
      throw runtimeException;
    }
    
    public void initialize(AlgorithmParameterSpec param1AlgorithmParameterSpec, SecureRandom param1SecureRandom) throws InvalidAlgorithmParameterException {
      if (this.serviceIterator == null) {
        this.spi.initialize(param1AlgorithmParameterSpec, param1SecureRandom);
        return;
      } 
      Exception exception = null;
      KeyPairGeneratorSpi keyPairGeneratorSpi = this.spi;
      while (true) {
        try {
          keyPairGeneratorSpi.initialize(param1AlgorithmParameterSpec, param1SecureRandom);
          this.initType = 3;
          this.initKeySize = 0;
          this.initParams = param1AlgorithmParameterSpec;
          this.initRandom = param1SecureRandom;
          return;
        } catch (Exception exception1) {
          if (exception == null)
            exception = exception1; 
          keyPairGeneratorSpi = nextSpi(keyPairGeneratorSpi, false);
          if (keyPairGeneratorSpi == null)
            break; 
        } 
      } 
      if (exception instanceof RuntimeException)
        throw (RuntimeException)exception; 
      throw (InvalidAlgorithmParameterException)exception;
    }
    
    public KeyPair generateKeyPair() {
      if (this.serviceIterator == null)
        return this.spi.generateKeyPair(); 
      RuntimeException runtimeException = null;
      KeyPairGeneratorSpi keyPairGeneratorSpi = this.spi;
      while (true) {
        try {
          return keyPairGeneratorSpi.generateKeyPair();
        } catch (RuntimeException runtimeException1) {
          if (runtimeException == null)
            runtimeException = runtimeException1; 
          keyPairGeneratorSpi = nextSpi(keyPairGeneratorSpi, true);
          if (keyPairGeneratorSpi == null)
            break; 
        } 
      } 
      throw runtimeException;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\KeyPairGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */