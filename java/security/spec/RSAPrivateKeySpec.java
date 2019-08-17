package java.security.spec;

import java.math.BigInteger;

public class RSAPrivateKeySpec implements KeySpec {
  private BigInteger modulus;
  
  private BigInteger privateExponent;
  
  public RSAPrivateKeySpec(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    this.modulus = paramBigInteger1;
    this.privateExponent = paramBigInteger2;
  }
  
  public BigInteger getModulus() { return this.modulus; }
  
  public BigInteger getPrivateExponent() { return this.privateExponent; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\RSAPrivateKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */