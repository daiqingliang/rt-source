package sun.security.provider;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.interfaces.DSAKeyPairGenerator;
import java.security.interfaces.DSAParams;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import sun.security.jca.JCAUtil;
import sun.security.util.SecurityProviderConstants;

class DSAKeyPairGenerator extends KeyPairGenerator {
  private int plen;
  
  private int qlen;
  
  boolean forceNewParameters;
  
  private DSAParameterSpec params;
  
  private SecureRandom random;
  
  DSAKeyPairGenerator(int paramInt) {
    super("DSA");
    initialize(paramInt, null);
  }
  
  private static void checkStrength(int paramInt1, int paramInt2) {
    if ((paramInt1 >= 512 && paramInt1 <= 1024 && paramInt1 % 64 == 0 && paramInt2 == 160) || (paramInt1 == 2048 && (paramInt2 == 224 || paramInt2 == 256)) || (paramInt1 == 3072 && paramInt2 == 256))
      return; 
    throw new InvalidParameterException("Unsupported prime and subprime size combination: " + paramInt1 + ", " + paramInt2);
  }
  
  public void initialize(int paramInt, SecureRandom paramSecureRandom) { init(paramInt, paramSecureRandom, false); }
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    if (!(paramAlgorithmParameterSpec instanceof DSAParameterSpec))
      throw new InvalidAlgorithmParameterException("Inappropriate parameter"); 
    init((DSAParameterSpec)paramAlgorithmParameterSpec, paramSecureRandom, false);
  }
  
  void init(int paramInt, SecureRandom paramSecureRandom, boolean paramBoolean) {
    int i = SecurityProviderConstants.getDefDSASubprimeSize(paramInt);
    checkStrength(paramInt, i);
    this.plen = paramInt;
    this.qlen = i;
    this.params = null;
    this.random = paramSecureRandom;
    this.forceNewParameters = paramBoolean;
  }
  
  void init(DSAParameterSpec paramDSAParameterSpec, SecureRandom paramSecureRandom, boolean paramBoolean) {
    int i = paramDSAParameterSpec.getP().bitLength();
    int j = paramDSAParameterSpec.getQ().bitLength();
    checkStrength(i, j);
    this.plen = i;
    this.qlen = j;
    this.params = paramDSAParameterSpec;
    this.random = paramSecureRandom;
    this.forceNewParameters = paramBoolean;
  }
  
  public KeyPair generateKeyPair() {
    DSAParameterSpec dSAParameterSpec;
    if (this.random == null)
      this.random = JCAUtil.getSecureRandom(); 
    try {
      if (this.forceNewParameters) {
        dSAParameterSpec = ParameterCache.getNewDSAParameterSpec(this.plen, this.qlen, this.random);
      } else {
        if (this.params == null)
          this.params = ParameterCache.getDSAParameterSpec(this.plen, this.qlen, this.random); 
        dSAParameterSpec = this.params;
      } 
    } catch (GeneralSecurityException generalSecurityException) {
      throw new ProviderException(generalSecurityException);
    } 
    return generateKeyPair(dSAParameterSpec.getP(), dSAParameterSpec.getQ(), dSAParameterSpec.getG(), this.random);
  }
  
  private KeyPair generateKeyPair(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, SecureRandom paramSecureRandom) {
    BigInteger bigInteger1 = generateX(paramSecureRandom, paramBigInteger2);
    BigInteger bigInteger2 = generateY(bigInteger1, paramBigInteger1, paramBigInteger3);
    try {
      DSAPublicKeyImpl dSAPublicKeyImpl;
      if (DSAKeyFactory.SERIAL_INTEROP) {
        dSAPublicKeyImpl = new DSAPublicKey(bigInteger2, paramBigInteger1, paramBigInteger2, paramBigInteger3);
      } else {
        dSAPublicKeyImpl = new DSAPublicKeyImpl(bigInteger2, paramBigInteger1, paramBigInteger2, paramBigInteger3);
      } 
      DSAPrivateKey dSAPrivateKey = new DSAPrivateKey(bigInteger1, paramBigInteger1, paramBigInteger2, paramBigInteger3);
      return new KeyPair(dSAPublicKeyImpl, dSAPrivateKey);
    } catch (InvalidKeyException invalidKeyException) {
      throw new ProviderException(invalidKeyException);
    } 
  }
  
  private BigInteger generateX(SecureRandom paramSecureRandom, BigInteger paramBigInteger) {
    BigInteger bigInteger = null;
    byte[] arrayOfByte = new byte[this.qlen];
    do {
      paramSecureRandom.nextBytes(arrayOfByte);
      bigInteger = (new BigInteger(1, arrayOfByte)).mod(paramBigInteger);
    } while (bigInteger.signum() <= 0 || bigInteger.compareTo(paramBigInteger) >= 0);
    return bigInteger;
  }
  
  BigInteger generateY(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) { return paramBigInteger3.modPow(paramBigInteger1, paramBigInteger2); }
  
  public static final class Current extends DSAKeyPairGenerator {
    public Current() { super(SecurityProviderConstants.DEF_DSA_KEY_SIZE); }
  }
  
  public static final class Legacy extends DSAKeyPairGenerator implements DSAKeyPairGenerator {
    public Legacy() { super(1024); }
    
    public void initialize(int param1Int, boolean param1Boolean, SecureRandom param1SecureRandom) throws InvalidParameterException {
      if (param1Boolean) {
        init(param1Int, param1SecureRandom, true);
      } else {
        DSAParameterSpec dSAParameterSpec = ParameterCache.getCachedDSAParameterSpec(param1Int, SecurityProviderConstants.getDefDSASubprimeSize(param1Int));
        if (dSAParameterSpec == null)
          throw new InvalidParameterException("No precomputed parameters for requested modulus size available"); 
        init(dSAParameterSpec, param1SecureRandom, false);
      } 
    }
    
    public void initialize(DSAParams param1DSAParams, SecureRandom param1SecureRandom) throws InvalidParameterException {
      if (param1DSAParams == null)
        throw new InvalidParameterException("Params must not be null"); 
      DSAParameterSpec dSAParameterSpec = new DSAParameterSpec(param1DSAParams.getP(), param1DSAParams.getQ(), param1DSAParams.getG());
      init(dSAParameterSpec, param1SecureRandom, false);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\DSAKeyPairGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */