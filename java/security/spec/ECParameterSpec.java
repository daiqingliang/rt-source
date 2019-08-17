package java.security.spec;

import java.math.BigInteger;

public class ECParameterSpec implements AlgorithmParameterSpec {
  private final EllipticCurve curve;
  
  private final ECPoint g;
  
  private final BigInteger n;
  
  private final int h;
  
  public ECParameterSpec(EllipticCurve paramEllipticCurve, ECPoint paramECPoint, BigInteger paramBigInteger, int paramInt) {
    if (paramEllipticCurve == null)
      throw new NullPointerException("curve is null"); 
    if (paramECPoint == null)
      throw new NullPointerException("g is null"); 
    if (paramBigInteger == null)
      throw new NullPointerException("n is null"); 
    if (paramBigInteger.signum() != 1)
      throw new IllegalArgumentException("n is not positive"); 
    if (paramInt <= 0)
      throw new IllegalArgumentException("h is not positive"); 
    this.curve = paramEllipticCurve;
    this.g = paramECPoint;
    this.n = paramBigInteger;
    this.h = paramInt;
  }
  
  public EllipticCurve getCurve() { return this.curve; }
  
  public ECPoint getGenerator() { return this.g; }
  
  public BigInteger getOrder() { return this.n; }
  
  public int getCofactor() { return this.h; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\ECParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */