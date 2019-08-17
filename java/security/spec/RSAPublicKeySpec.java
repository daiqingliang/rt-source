package java.security.spec;

import java.math.BigInteger;

public class RSAPublicKeySpec implements KeySpec {
  private BigInteger modulus;
  
  private BigInteger publicExponent;
  
  public RSAPublicKeySpec(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    this.modulus = paramBigInteger1;
    this.publicExponent = paramBigInteger2;
  }
  
  public BigInteger getModulus() { return this.modulus; }
  
  public BigInteger getPublicExponent() { return this.publicExponent; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\RSAPublicKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */