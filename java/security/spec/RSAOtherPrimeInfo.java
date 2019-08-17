package java.security.spec;

import java.math.BigInteger;

public class RSAOtherPrimeInfo {
  private BigInteger prime;
  
  private BigInteger primeExponent;
  
  private BigInteger crtCoefficient;
  
  public RSAOtherPrimeInfo(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) {
    if (paramBigInteger1 == null)
      throw new NullPointerException("the prime parameter must be non-null"); 
    if (paramBigInteger2 == null)
      throw new NullPointerException("the primeExponent parameter must be non-null"); 
    if (paramBigInteger3 == null)
      throw new NullPointerException("the crtCoefficient parameter must be non-null"); 
    this.prime = paramBigInteger1;
    this.primeExponent = paramBigInteger2;
    this.crtCoefficient = paramBigInteger3;
  }
  
  public final BigInteger getPrime() { return this.prime; }
  
  public final BigInteger getExponent() { return this.primeExponent; }
  
  public final BigInteger getCrtCoefficient() { return this.crtCoefficient; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\RSAOtherPrimeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */