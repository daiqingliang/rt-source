package java.security.spec;

import java.math.BigInteger;

public class ECPoint {
  private final BigInteger x;
  
  private final BigInteger y;
  
  public static final ECPoint POINT_INFINITY = new ECPoint();
  
  private ECPoint() {
    this.x = null;
    this.y = null;
  }
  
  public ECPoint(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    if (paramBigInteger1 == null || paramBigInteger2 == null)
      throw new NullPointerException("affine coordinate x or y is null"); 
    this.x = paramBigInteger1;
    this.y = paramBigInteger2;
  }
  
  public BigInteger getAffineX() { return this.x; }
  
  public BigInteger getAffineY() { return this.y; }
  
  public boolean equals(Object paramObject) { return (this == paramObject) ? true : ((this == POINT_INFINITY) ? false : ((paramObject instanceof ECPoint) ? ((this.x.equals(((ECPoint)paramObject).x) && this.y.equals(((ECPoint)paramObject).y))) : false)); }
  
  public int hashCode() { return (this == POINT_INFINITY) ? 0 : (this.x.hashCode() << 5 + this.y.hashCode()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\ECPoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */