package sun.security.provider;

import java.math.BigInteger;
import java.security.AlgorithmParameterGeneratorSpi;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAGenParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import sun.security.util.SecurityProviderConstants;

public class DSAParameterGenerator extends AlgorithmParameterGeneratorSpi {
  private int valueL = -1;
  
  private int valueN = -1;
  
  private int seedLen = -1;
  
  private SecureRandom random;
  
  private static final BigInteger TWO = BigInteger.valueOf(2L);
  
  protected void engineInit(int paramInt, SecureRandom paramSecureRandom) {
    if (paramInt != 2048 && paramInt != 3072 && (paramInt < 512 || paramInt > 1024 || paramInt % 64 != 0))
      throw new InvalidParameterException("Unexpected strength (size of prime): " + paramInt + ". Prime size should be 512-1024, 2048, or 3072"); 
    this.valueL = paramInt;
    this.valueN = SecurityProviderConstants.getDefDSASubprimeSize(paramInt);
    this.seedLen = this.valueN;
    this.random = paramSecureRandom;
  }
  
  protected void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    if (!(paramAlgorithmParameterSpec instanceof DSAGenParameterSpec))
      throw new InvalidAlgorithmParameterException("Invalid parameter"); 
    DSAGenParameterSpec dSAGenParameterSpec = (DSAGenParameterSpec)paramAlgorithmParameterSpec;
    this.valueL = dSAGenParameterSpec.getPrimePLength();
    this.valueN = dSAGenParameterSpec.getSubprimeQLength();
    this.seedLen = dSAGenParameterSpec.getSeedLength();
    this.random = paramSecureRandom;
  }
  
  protected AlgorithmParameters engineGenerateParameters() {
    AlgorithmParameters algorithmParameters = null;
    try {
      if (this.random == null)
        this.random = new SecureRandom(); 
      if (this.valueL == -1)
        engineInit(SecurityProviderConstants.DEF_DSA_KEY_SIZE, this.random); 
      BigInteger[] arrayOfBigInteger = generatePandQ(this.random, this.valueL, this.valueN, this.seedLen);
      BigInteger bigInteger1 = arrayOfBigInteger[0];
      BigInteger bigInteger2 = arrayOfBigInteger[1];
      BigInteger bigInteger3 = generateG(bigInteger1, bigInteger2);
      DSAParameterSpec dSAParameterSpec = new DSAParameterSpec(bigInteger1, bigInteger2, bigInteger3);
      algorithmParameters = AlgorithmParameters.getInstance("DSA", "SUN");
      algorithmParameters.init(dSAParameterSpec);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      throw new RuntimeException(invalidParameterSpecException.getMessage());
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new RuntimeException(noSuchAlgorithmException.getMessage());
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new RuntimeException(noSuchProviderException.getMessage());
    } 
    return algorithmParameters;
  }
  
  private static BigInteger[] generatePandQ(SecureRandom paramSecureRandom, int paramInt1, int paramInt2, int paramInt3) {
    String str = null;
    if (paramInt2 == 160) {
      str = "SHA";
    } else if (paramInt2 == 224) {
      str = "SHA-224";
    } else if (paramInt2 == 256) {
      str = "SHA-256";
    } 
    MessageDigest messageDigest = null;
    try {
      messageDigest = MessageDigest.getInstance(str);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      noSuchAlgorithmException.printStackTrace();
    } 
    int i = messageDigest.getDigestLength() * 8;
    int j = (paramInt1 - 1) / i;
    int k = (paramInt1 - 1) % i;
    byte[] arrayOfByte = new byte[paramInt3 / 8];
    BigInteger bigInteger1 = TWO.pow(paramInt3);
    short s = -1;
    if (paramInt1 <= 1024) {
      s = 80;
    } else if (paramInt1 == 2048) {
      s = 112;
    } else if (paramInt1 == 3072) {
      s = 128;
    } 
    if (s < 0)
      throw new ProviderException("Invalid valueL: " + paramInt1); 
    BigInteger bigInteger2 = null;
    while (true) {
      paramSecureRandom.nextBytes(arrayOfByte);
      bigInteger2 = new BigInteger(1, arrayOfByte);
      BigInteger bigInteger4 = (new BigInteger(1, messageDigest.digest(arrayOfByte))).mod(TWO.pow(paramInt2 - 1));
      BigInteger bigInteger3 = TWO.pow(paramInt2 - 1).add(bigInteger4).add(BigInteger.ONE).subtract(bigInteger4.mod(TWO));
      if (bigInteger3.isProbablePrime(s)) {
        bigInteger4 = BigInteger.ONE;
        for (byte b = 0; b < 4 * paramInt1; b++) {
          BigInteger[] arrayOfBigInteger = new BigInteger[j + 1];
          for (byte b1 = 0; b1 <= j; b1++) {
            BigInteger bigInteger10 = BigInteger.valueOf(b1);
            BigInteger bigInteger11 = bigInteger2.add(bigInteger4).add(bigInteger10).mod(bigInteger1);
            byte[] arrayOfByte1 = messageDigest.digest(toByteArray(bigInteger11));
            arrayOfBigInteger[b1] = new BigInteger(1, arrayOfByte1);
          } 
          BigInteger bigInteger6 = arrayOfBigInteger[0];
          for (int m = 1; m < j; m++)
            bigInteger6 = bigInteger6.add(arrayOfBigInteger[m].multiply(TWO.pow(m * i))); 
          bigInteger6 = bigInteger6.add(arrayOfBigInteger[j].mod(TWO.pow(k)).multiply(TWO.pow(j * i)));
          BigInteger bigInteger7 = TWO.pow(paramInt1 - 1);
          BigInteger bigInteger8 = bigInteger6.add(bigInteger7);
          BigInteger bigInteger9 = bigInteger8.mod(bigInteger3.multiply(TWO));
          BigInteger bigInteger5 = bigInteger8.subtract(bigInteger9.subtract(BigInteger.ONE));
          if (bigInteger5.compareTo(bigInteger7) > -1 && bigInteger5.isProbablePrime(s))
            return new BigInteger[] { bigInteger5, bigInteger3, (new BigInteger[4][2] = bigInteger2).valueOf(b) }; 
          bigInteger4 = bigInteger4.add(BigInteger.valueOf(j)).add(BigInteger.ONE);
        } 
      } 
    } 
  }
  
  private static BigInteger generateG(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    BigInteger bigInteger1 = BigInteger.ONE;
    BigInteger bigInteger2 = paramBigInteger1.subtract(BigInteger.ONE).divide(paramBigInteger2);
    BigInteger bigInteger3 = BigInteger.ONE;
    while (bigInteger3.compareTo(TWO) < 0) {
      bigInteger3 = bigInteger1.modPow(bigInteger2, paramBigInteger1);
      bigInteger1 = bigInteger1.add(BigInteger.ONE);
    } 
    return bigInteger3;
  }
  
  private static byte[] toByteArray(BigInteger paramBigInteger) {
    byte[] arrayOfByte = paramBigInteger.toByteArray();
    if (arrayOfByte[0] == 0) {
      byte[] arrayOfByte1 = new byte[arrayOfByte.length - 1];
      System.arraycopy(arrayOfByte, 1, arrayOfByte1, 0, arrayOfByte1.length);
      arrayOfByte = arrayOfByte1;
    } 
    return arrayOfByte;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\DSAParameterGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */