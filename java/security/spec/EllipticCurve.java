package java.security.spec;

import java.math.BigInteger;

public class EllipticCurve {
  private final ECField field;
  
  private final BigInteger a;
  
  private final BigInteger b;
  
  private final byte[] seed;
  
  private static void checkValidity(ECField paramECField, BigInteger paramBigInteger, String paramString) {
    if (paramECField instanceof ECFieldFp) {
      BigInteger bigInteger = ((ECFieldFp)paramECField).getP();
      if (bigInteger.compareTo(paramBigInteger) != 1)
        throw new IllegalArgumentException(paramString + " is too large"); 
      if (paramBigInteger.signum() < 0)
        throw new IllegalArgumentException(paramString + " is negative"); 
    } else if (paramECField instanceof ECFieldF2m) {
      int i = ((ECFieldF2m)paramECField).getM();
      if (paramBigInteger.bitLength() > i)
        throw new IllegalArgumentException(paramString + " is too large"); 
    } 
  }
  
  public EllipticCurve(ECField paramECField, BigInteger paramBigInteger1, BigInteger paramBigInteger2) { this(paramECField, paramBigInteger1, paramBigInteger2, null); }
  
  public EllipticCurve(ECField paramECField, BigInteger paramBigInteger1, BigInteger paramBigInteger2, byte[] paramArrayOfByte) {
    if (paramECField == null)
      throw new NullPointerException("field is null"); 
    if (paramBigInteger1 == null)
      throw new NullPointerException("first coefficient is null"); 
    if (paramBigInteger2 == null)
      throw new NullPointerException("second coefficient is null"); 
    checkValidity(paramECField, paramBigInteger1, "first coefficient");
    checkValidity(paramECField, paramBigInteger2, "second coefficient");
    this.field = paramECField;
    this.a = paramBigInteger1;
    this.b = paramBigInteger2;
    if (paramArrayOfByte != null) {
      this.seed = (byte[])paramArrayOfByte.clone();
    } else {
      this.seed = null;
    } 
  }
  
  public ECField getField() { return this.field; }
  
  public BigInteger getA() { return this.a; }
  
  public BigInteger getB() { return this.b; }
  
  public byte[] getSeed() { return (this.seed == null) ? null : (byte[])this.seed.clone(); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof EllipticCurve) {
      EllipticCurve ellipticCurve = (EllipticCurve)paramObject;
      if (this.field.equals(ellipticCurve.field) && this.a.equals(ellipticCurve.a) && this.b.equals(ellipticCurve.b))
        return true; 
    } 
    return false;
  }
  
  public int hashCode() { return this.field.hashCode() << 6 + (this.a.hashCode() << 4) + (this.b.hashCode() << 2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\EllipticCurve.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */