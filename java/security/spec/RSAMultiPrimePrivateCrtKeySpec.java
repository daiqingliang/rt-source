package java.security.spec;

import java.math.BigInteger;

public class RSAMultiPrimePrivateCrtKeySpec extends RSAPrivateKeySpec {
  private final BigInteger publicExponent;
  
  private final BigInteger primeP;
  
  private final BigInteger primeQ;
  
  private final BigInteger primeExponentP;
  
  private final BigInteger primeExponentQ;
  
  private final BigInteger crtCoefficient;
  
  private final RSAOtherPrimeInfo[] otherPrimeInfo;
  
  public RSAMultiPrimePrivateCrtKeySpec(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigInteger paramBigInteger6, BigInteger paramBigInteger7, BigInteger paramBigInteger8, RSAOtherPrimeInfo[] paramArrayOfRSAOtherPrimeInfo) {
    super(paramBigInteger1, paramBigInteger3);
    if (paramBigInteger1 == null)
      throw new NullPointerException("the modulus parameter must be non-null"); 
    if (paramBigInteger2 == null)
      throw new NullPointerException("the publicExponent parameter must be non-null"); 
    if (paramBigInteger3 == null)
      throw new NullPointerException("the privateExponent parameter must be non-null"); 
    if (paramBigInteger4 == null)
      throw new NullPointerException("the primeP parameter must be non-null"); 
    if (paramBigInteger5 == null)
      throw new NullPointerException("the primeQ parameter must be non-null"); 
    if (paramBigInteger6 == null)
      throw new NullPointerException("the primeExponentP parameter must be non-null"); 
    if (paramBigInteger7 == null)
      throw new NullPointerException("the primeExponentQ parameter must be non-null"); 
    if (paramBigInteger8 == null)
      throw new NullPointerException("the crtCoefficient parameter must be non-null"); 
    this.publicExponent = paramBigInteger2;
    this.primeP = paramBigInteger4;
    this.primeQ = paramBigInteger5;
    this.primeExponentP = paramBigInteger6;
    this.primeExponentQ = paramBigInteger7;
    this.crtCoefficient = paramBigInteger8;
    if (paramArrayOfRSAOtherPrimeInfo == null) {
      this.otherPrimeInfo = null;
    } else {
      if (paramArrayOfRSAOtherPrimeInfo.length == 0)
        throw new IllegalArgumentException("the otherPrimeInfo parameter must not be empty"); 
      this.otherPrimeInfo = (RSAOtherPrimeInfo[])paramArrayOfRSAOtherPrimeInfo.clone();
    } 
  }
  
  public BigInteger getPublicExponent() { return this.publicExponent; }
  
  public BigInteger getPrimeP() { return this.primeP; }
  
  public BigInteger getPrimeQ() { return this.primeQ; }
  
  public BigInteger getPrimeExponentP() { return this.primeExponentP; }
  
  public BigInteger getPrimeExponentQ() { return this.primeExponentQ; }
  
  public BigInteger getCrtCoefficient() { return this.crtCoefficient; }
  
  public RSAOtherPrimeInfo[] getOtherPrimeInfo() { return (this.otherPrimeInfo == null) ? null : (RSAOtherPrimeInfo[])this.otherPrimeInfo.clone(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\RSAMultiPrimePrivateCrtKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */