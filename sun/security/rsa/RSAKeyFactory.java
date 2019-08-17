package sun.security.rsa;

import java.math.BigInteger;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactorySpi;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import sun.security.action.GetPropertyAction;

public final class RSAKeyFactory extends KeyFactorySpi {
  private static final Class<?> rsaPublicKeySpecClass = RSAPublicKeySpec.class;
  
  private static final Class<?> rsaPrivateKeySpecClass = RSAPrivateKeySpec.class;
  
  private static final Class<?> rsaPrivateCrtKeySpecClass = RSAPrivateCrtKeySpec.class;
  
  private static final Class<?> x509KeySpecClass = X509EncodedKeySpec.class;
  
  private static final Class<?> pkcs8KeySpecClass = PKCS8EncodedKeySpec.class;
  
  public static final int MIN_MODLEN = 512;
  
  public static final int MAX_MODLEN = 16384;
  
  public static final int MAX_MODLEN_RESTRICT_EXP = 3072;
  
  public static final int MAX_RESTRICTED_EXPLEN = 64;
  
  private static final boolean restrictExpLen = "true".equalsIgnoreCase((String)AccessController.doPrivileged(new GetPropertyAction("sun.security.rsa.restrictRSAExponent", "true")));
  
  private static final RSAKeyFactory INSTANCE = new RSAKeyFactory();
  
  public static RSAKey toRSAKey(Key paramKey) throws InvalidKeyException { return (paramKey instanceof RSAPrivateKeyImpl || paramKey instanceof RSAPrivateCrtKeyImpl || paramKey instanceof RSAPublicKeyImpl) ? (RSAKey)paramKey : (RSAKey)INSTANCE.engineTranslateKey(paramKey); }
  
  static void checkRSAProviderKeyLengths(int paramInt, BigInteger paramBigInteger) throws InvalidKeyException { checkKeyLengths(paramInt + 7 & 0xFFFFFFF8, paramBigInteger, 512, 2147483647); }
  
  public static void checkKeyLengths(int paramInt1, BigInteger paramBigInteger, int paramInt2, int paramInt3) throws InvalidKeyException {
    if (paramInt2 > 0 && paramInt1 < paramInt2)
      throw new InvalidKeyException("RSA keys must be at least " + paramInt2 + " bits long"); 
    int i = Math.min(paramInt3, 16384);
    if (paramInt1 > i)
      throw new InvalidKeyException("RSA keys must be no longer than " + i + " bits"); 
    if (restrictExpLen && paramBigInteger != null && paramInt1 > 3072 && paramBigInteger.bitLength() > 64)
      throw new InvalidKeyException("RSA exponents can be no longer than 64 bits  if modulus is greater than 3072 bits"); 
  }
  
  protected Key engineTranslateKey(Key paramKey) throws InvalidKeyException {
    if (paramKey == null)
      throw new InvalidKeyException("Key must not be null"); 
    String str = paramKey.getAlgorithm();
    if (!str.equals("RSA"))
      throw new InvalidKeyException("Not an RSA key: " + str); 
    if (paramKey instanceof PublicKey)
      return translatePublicKey((PublicKey)paramKey); 
    if (paramKey instanceof PrivateKey)
      return translatePrivateKey((PrivateKey)paramKey); 
    throw new InvalidKeyException("Neither a public nor a private key");
  }
  
  protected PublicKey engineGeneratePublic(KeySpec paramKeySpec) throws InvalidKeySpecException {
    try {
      return generatePublic(paramKeySpec);
    } catch (InvalidKeySpecException invalidKeySpecException) {
      throw invalidKeySpecException;
    } catch (GeneralSecurityException generalSecurityException) {
      throw new InvalidKeySpecException(generalSecurityException);
    } 
  }
  
  protected PrivateKey engineGeneratePrivate(KeySpec paramKeySpec) throws InvalidKeySpecException {
    try {
      return generatePrivate(paramKeySpec);
    } catch (InvalidKeySpecException invalidKeySpecException) {
      throw invalidKeySpecException;
    } catch (GeneralSecurityException generalSecurityException) {
      throw new InvalidKeySpecException(generalSecurityException);
    } 
  }
  
  private PublicKey translatePublicKey(PublicKey paramPublicKey) throws InvalidKeyException {
    if (paramPublicKey instanceof RSAPublicKey) {
      if (paramPublicKey instanceof RSAPublicKeyImpl)
        return paramPublicKey; 
      RSAPublicKey rSAPublicKey = (RSAPublicKey)paramPublicKey;
      try {
        return new RSAPublicKeyImpl(rSAPublicKey.getModulus(), rSAPublicKey.getPublicExponent());
      } catch (RuntimeException runtimeException) {
        throw new InvalidKeyException("Invalid key", runtimeException);
      } 
    } 
    if ("X.509".equals(paramPublicKey.getFormat())) {
      byte[] arrayOfByte = paramPublicKey.getEncoded();
      return new RSAPublicKeyImpl(arrayOfByte);
    } 
    throw new InvalidKeyException("Public keys must be instance of RSAPublicKey or have X.509 encoding");
  }
  
  private PrivateKey translatePrivateKey(PrivateKey paramPrivateKey) throws InvalidKeyException {
    if (paramPrivateKey instanceof RSAPrivateCrtKey) {
      if (paramPrivateKey instanceof RSAPrivateCrtKeyImpl)
        return paramPrivateKey; 
      RSAPrivateCrtKey rSAPrivateCrtKey = (RSAPrivateCrtKey)paramPrivateKey;
      try {
        return new RSAPrivateCrtKeyImpl(rSAPrivateCrtKey.getModulus(), rSAPrivateCrtKey.getPublicExponent(), rSAPrivateCrtKey.getPrivateExponent(), rSAPrivateCrtKey.getPrimeP(), rSAPrivateCrtKey.getPrimeQ(), rSAPrivateCrtKey.getPrimeExponentP(), rSAPrivateCrtKey.getPrimeExponentQ(), rSAPrivateCrtKey.getCrtCoefficient());
      } catch (RuntimeException runtimeException) {
        throw new InvalidKeyException("Invalid key", runtimeException);
      } 
    } 
    if (paramPrivateKey instanceof RSAPrivateKey) {
      if (paramPrivateKey instanceof RSAPrivateKeyImpl)
        return paramPrivateKey; 
      RSAPrivateKey rSAPrivateKey = (RSAPrivateKey)paramPrivateKey;
      try {
        return new RSAPrivateKeyImpl(rSAPrivateKey.getModulus(), rSAPrivateKey.getPrivateExponent());
      } catch (RuntimeException runtimeException) {
        throw new InvalidKeyException("Invalid key", runtimeException);
      } 
    } 
    if ("PKCS#8".equals(paramPrivateKey.getFormat())) {
      byte[] arrayOfByte = paramPrivateKey.getEncoded();
      return RSAPrivateCrtKeyImpl.newKey(arrayOfByte);
    } 
    throw new InvalidKeyException("Private keys must be instance of RSAPrivate(Crt)Key or have PKCS#8 encoding");
  }
  
  private PublicKey generatePublic(KeySpec paramKeySpec) throws InvalidKeySpecException {
    if (paramKeySpec instanceof X509EncodedKeySpec) {
      X509EncodedKeySpec x509EncodedKeySpec = (X509EncodedKeySpec)paramKeySpec;
      return new RSAPublicKeyImpl(x509EncodedKeySpec.getEncoded());
    } 
    if (paramKeySpec instanceof RSAPublicKeySpec) {
      RSAPublicKeySpec rSAPublicKeySpec = (RSAPublicKeySpec)paramKeySpec;
      return new RSAPublicKeyImpl(rSAPublicKeySpec.getModulus(), rSAPublicKeySpec.getPublicExponent());
    } 
    throw new InvalidKeySpecException("Only RSAPublicKeySpec and X509EncodedKeySpec supported for RSA public keys");
  }
  
  private PrivateKey generatePrivate(KeySpec paramKeySpec) throws InvalidKeySpecException {
    if (paramKeySpec instanceof PKCS8EncodedKeySpec) {
      PKCS8EncodedKeySpec pKCS8EncodedKeySpec = (PKCS8EncodedKeySpec)paramKeySpec;
      return RSAPrivateCrtKeyImpl.newKey(pKCS8EncodedKeySpec.getEncoded());
    } 
    if (paramKeySpec instanceof RSAPrivateCrtKeySpec) {
      RSAPrivateCrtKeySpec rSAPrivateCrtKeySpec = (RSAPrivateCrtKeySpec)paramKeySpec;
      return new RSAPrivateCrtKeyImpl(rSAPrivateCrtKeySpec.getModulus(), rSAPrivateCrtKeySpec.getPublicExponent(), rSAPrivateCrtKeySpec.getPrivateExponent(), rSAPrivateCrtKeySpec.getPrimeP(), rSAPrivateCrtKeySpec.getPrimeQ(), rSAPrivateCrtKeySpec.getPrimeExponentP(), rSAPrivateCrtKeySpec.getPrimeExponentQ(), rSAPrivateCrtKeySpec.getCrtCoefficient());
    } 
    if (paramKeySpec instanceof RSAPrivateKeySpec) {
      RSAPrivateKeySpec rSAPrivateKeySpec = (RSAPrivateKeySpec)paramKeySpec;
      return new RSAPrivateKeyImpl(rSAPrivateKeySpec.getModulus(), rSAPrivateKeySpec.getPrivateExponent());
    } 
    throw new InvalidKeySpecException("Only RSAPrivate(Crt)KeySpec and PKCS8EncodedKeySpec supported for RSA private keys");
  }
  
  protected <T extends KeySpec> T engineGetKeySpec(Key paramKey, Class<T> paramClass) throws InvalidKeySpecException {
    try {
      paramKey = engineTranslateKey(paramKey);
    } catch (InvalidKeyException invalidKeyException) {
      throw new InvalidKeySpecException(invalidKeyException);
    } 
    if (paramKey instanceof RSAPublicKey) {
      RSAPublicKey rSAPublicKey = (RSAPublicKey)paramKey;
      if (rsaPublicKeySpecClass.isAssignableFrom(paramClass))
        return (T)(KeySpec)paramClass.cast(new RSAPublicKeySpec(rSAPublicKey.getModulus(), rSAPublicKey.getPublicExponent())); 
      if (x509KeySpecClass.isAssignableFrom(paramClass))
        return (T)(KeySpec)paramClass.cast(new X509EncodedKeySpec(paramKey.getEncoded())); 
      throw new InvalidKeySpecException("KeySpec must be RSAPublicKeySpec or X509EncodedKeySpec for RSA public keys");
    } 
    if (paramKey instanceof RSAPrivateKey) {
      if (pkcs8KeySpecClass.isAssignableFrom(paramClass))
        return (T)(KeySpec)paramClass.cast(new PKCS8EncodedKeySpec(paramKey.getEncoded())); 
      if (rsaPrivateCrtKeySpecClass.isAssignableFrom(paramClass)) {
        if (paramKey instanceof RSAPrivateCrtKey) {
          RSAPrivateCrtKey rSAPrivateCrtKey = (RSAPrivateCrtKey)paramKey;
          return (T)(KeySpec)paramClass.cast(new RSAPrivateCrtKeySpec(rSAPrivateCrtKey.getModulus(), rSAPrivateCrtKey.getPublicExponent(), rSAPrivateCrtKey.getPrivateExponent(), rSAPrivateCrtKey.getPrimeP(), rSAPrivateCrtKey.getPrimeQ(), rSAPrivateCrtKey.getPrimeExponentP(), rSAPrivateCrtKey.getPrimeExponentQ(), rSAPrivateCrtKey.getCrtCoefficient()));
        } 
        throw new InvalidKeySpecException("RSAPrivateCrtKeySpec can only be used with CRT keys");
      } 
      if (rsaPrivateKeySpecClass.isAssignableFrom(paramClass)) {
        RSAPrivateKey rSAPrivateKey = (RSAPrivateKey)paramKey;
        return (T)(KeySpec)paramClass.cast(new RSAPrivateKeySpec(rSAPrivateKey.getModulus(), rSAPrivateKey.getPrivateExponent()));
      } 
      throw new InvalidKeySpecException("KeySpec must be RSAPrivate(Crt)KeySpec or PKCS8EncodedKeySpec for RSA private keys");
    } 
    throw new InvalidKeySpecException("Neither public nor private key");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\rsa\RSAKeyFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */