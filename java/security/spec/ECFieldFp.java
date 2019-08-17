package java.security.spec;

import java.math.BigInteger;

public class ECFieldFp implements ECField {
  private BigInteger p;
  
  public ECFieldFp(BigInteger paramBigInteger) {
    if (paramBigInteger.signum() != 1)
      throw new IllegalArgumentException("p is not positive"); 
    this.p = paramBigInteger;
  }
  
  public int getFieldSize() { return this.p.bitLength(); }
  
  public BigInteger getP() { return this.p; }
  
  public boolean equals(Object paramObject) { return (this == paramObject) ? true : ((paramObject instanceof ECFieldFp) ? this.p.equals(((ECFieldFp)paramObject).p) : 0); }
  
  public int hashCode() { return this.p.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\ECFieldFp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */