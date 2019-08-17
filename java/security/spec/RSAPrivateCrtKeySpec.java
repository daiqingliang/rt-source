package java.security.spec;

import java.math.BigInteger;

public class RSAPrivateCrtKeySpec extends RSAPrivateKeySpec {
  private final BigInteger publicExponent;
  
  private final BigInteger primeP;
  
  private final BigInteger primeQ;
  
  private final BigInteger primeExponentP;
  
  private final BigInteger primeExponentQ;
  
  private final BigInteger crtCoefficient;
  
  public RSAPrivateCrtKeySpec(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigInteger paramBigInteger6, BigInteger paramBigInteger7, BigInteger paramBigInteger8) {
    super(paramBigInteger1, paramBigInteger3);
    this.publicExponent = paramBigInteger2;
    this.primeP = paramBigInteger4;
    this.primeQ = paramBigInteger5;
    this.primeExponentP = paramBigInteger6;
    this.primeExponentQ = paramBigInteger7;
    this.crtCoefficient = paramBigInteger8;
  }
  
  public BigInteger getPublicExponent() { return this.publicExponent; }
  
  public BigInteger getPrimeP() { return this.primeP; }
  
  public BigInteger getPrimeQ() { return this.primeQ; }
  
  public BigInteger getPrimeExponentP() { return this.primeExponentP; }
  
  public BigInteger getPrimeExponentQ() { return this.primeExponentQ; }
  
  public BigInteger getCrtCoefficient() { return this.crtCoefficient; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\RSAPrivateCrtKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */