package java.security.spec;

import java.math.BigInteger;
import java.security.interfaces.DSAParams;

public class DSAParameterSpec implements AlgorithmParameterSpec, DSAParams {
  BigInteger p;
  
  BigInteger q;
  
  BigInteger g;
  
  public DSAParameterSpec(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) {
    this.p = paramBigInteger1;
    this.q = paramBigInteger2;
    this.g = paramBigInteger3;
  }
  
  public BigInteger getP() { return this.p; }
  
  public BigInteger getQ() { return this.q; }
  
  public BigInteger getG() { return this.g; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\DSAParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */