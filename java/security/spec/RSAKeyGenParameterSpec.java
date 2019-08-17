package java.security.spec;

import java.math.BigInteger;

public class RSAKeyGenParameterSpec implements AlgorithmParameterSpec {
  private int keysize;
  
  private BigInteger publicExponent;
  
  public static final BigInteger F0;
  
  public static final BigInteger F4 = (F0 = BigInteger.valueOf(3L)).valueOf(65537L);
  
  public RSAKeyGenParameterSpec(int paramInt, BigInteger paramBigInteger) {
    this.keysize = paramInt;
    this.publicExponent = paramBigInteger;
  }
  
  public int getKeysize() { return this.keysize; }
  
  public BigInteger getPublicExponent() { return this.publicExponent; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\RSAKeyGenParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */