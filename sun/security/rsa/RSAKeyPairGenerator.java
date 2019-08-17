package sun.security.rsa;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGeneratorSpi;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import sun.security.jca.JCAUtil;
import sun.security.util.SecurityProviderConstants;

public final class RSAKeyPairGenerator extends KeyPairGeneratorSpi {
  private BigInteger publicExponent;
  
  private int keySize;
  
  private SecureRandom random;
  
  public RSAKeyPairGenerator() { initialize(SecurityProviderConstants.DEF_RSA_KEY_SIZE, null); }
  
  public void initialize(int paramInt, SecureRandom paramSecureRandom) {
    try {
      RSAKeyFactory.checkKeyLengths(paramInt, RSAKeyGenParameterSpec.F4, 512, 65536);
    } catch (InvalidKeyException invalidKeyException) {
      throw new InvalidParameterException(invalidKeyException.getMessage());
    } 
    this.keySize = paramInt;
    this.random = paramSecureRandom;
    this.publicExponent = RSAKeyGenParameterSpec.F4;
  }
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    if (!(paramAlgorithmParameterSpec instanceof RSAKeyGenParameterSpec))
      throw new InvalidAlgorithmParameterException("Params must be instance of RSAKeyGenParameterSpec"); 
    RSAKeyGenParameterSpec rSAKeyGenParameterSpec = (RSAKeyGenParameterSpec)paramAlgorithmParameterSpec;
    int i = rSAKeyGenParameterSpec.getKeysize();
    BigInteger bigInteger = rSAKeyGenParameterSpec.getPublicExponent();
    if (bigInteger == null) {
      bigInteger = RSAKeyGenParameterSpec.F4;
    } else {
      if (bigInteger.compareTo(RSAKeyGenParameterSpec.F0) < 0)
        throw new InvalidAlgorithmParameterException("Public exponent must be 3 or larger"); 
      if (bigInteger.bitLength() > i)
        throw new InvalidAlgorithmParameterException("Public exponent must be smaller than key size"); 
    } 
    try {
      RSAKeyFactory.checkKeyLengths(i, bigInteger, 512, 65536);
    } catch (InvalidKeyException invalidKeyException) {
      throw new InvalidAlgorithmParameterException("Invalid key sizes", invalidKeyException);
    } 
    this.keySize = i;
    this.publicExponent = bigInteger;
    this.random = paramSecureRandom;
  }
  
  public KeyPair generateKeyPair() {
    BigInteger bigInteger7;
    BigInteger bigInteger6;
    BigInteger bigInteger5;
    BigInteger bigInteger4;
    BigInteger bigInteger3;
    BigInteger bigInteger2;
    int i = this.keySize + 1 >> 1;
    int j = this.keySize - i;
    if (this.random == null)
      this.random = JCAUtil.getSecureRandom(); 
    BigInteger bigInteger1 = this.publicExponent;
    while (true) {
      bigInteger2 = BigInteger.probablePrime(i, this.random);
      do {
        bigInteger3 = BigInteger.probablePrime(j, this.random);
        if (bigInteger2.compareTo(bigInteger3) < 0) {
          BigInteger bigInteger = bigInteger2;
          bigInteger2 = bigInteger3;
          bigInteger3 = bigInteger;
        } 
        bigInteger4 = bigInteger2.multiply(bigInteger3);
      } while (bigInteger4.bitLength() < this.keySize);
      bigInteger5 = bigInteger2.subtract(BigInteger.ONE);
      bigInteger6 = bigInteger3.subtract(BigInteger.ONE);
      bigInteger7 = bigInteger5.multiply(bigInteger6);
      if (!bigInteger1.gcd(bigInteger7).equals(BigInteger.ONE))
        continue; 
      break;
    } 
    BigInteger bigInteger8 = bigInteger1.modInverse(bigInteger7);
    BigInteger bigInteger9 = bigInteger8.mod(bigInteger5);
    BigInteger bigInteger10 = bigInteger8.mod(bigInteger6);
    BigInteger bigInteger11 = bigInteger3.modInverse(bigInteger2);
    try {
      RSAPublicKeyImpl rSAPublicKeyImpl = new RSAPublicKeyImpl(bigInteger4, bigInteger1);
      RSAPrivateCrtKeyImpl rSAPrivateCrtKeyImpl = new RSAPrivateCrtKeyImpl(bigInteger4, bigInteger1, bigInteger8, bigInteger2, bigInteger3, bigInteger9, bigInteger10, bigInteger11);
      return new KeyPair(rSAPublicKeyImpl, rSAPrivateCrtKeyImpl);
    } catch (InvalidKeyException invalidKeyException) {
      throw new RuntimeException(invalidKeyException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\rsa\RSAKeyPairGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */