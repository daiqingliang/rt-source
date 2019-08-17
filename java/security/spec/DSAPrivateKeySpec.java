package java.security.spec;

import java.math.BigInteger;

public class DSAPrivateKeySpec implements KeySpec {
  private BigInteger x;
  
  private BigInteger p;
  
  private BigInteger q;
  
  private BigInteger g;
  
  public DSAPrivateKeySpec(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) {
    this.x = paramBigInteger1;
    this.p = paramBigInteger2;
    this.q = paramBigInteger3;
    this.g = paramBigInteger4;
  }
  
  public BigInteger getX() { return this.x; }
  
  public BigInteger getP() { return this.p; }
  
  public BigInteger getQ() { return this.q; }
  
  public BigInteger getG() { return this.g; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\DSAPrivateKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */