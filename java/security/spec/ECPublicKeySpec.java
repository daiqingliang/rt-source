package java.security.spec;

public class ECPublicKeySpec implements KeySpec {
  private ECPoint w;
  
  private ECParameterSpec params;
  
  public ECPublicKeySpec(ECPoint paramECPoint, ECParameterSpec paramECParameterSpec) {
    if (paramECPoint == null)
      throw new NullPointerException("w is null"); 
    if (paramECParameterSpec == null)
      throw new NullPointerException("params is null"); 
    if (paramECPoint == ECPoint.POINT_INFINITY)
      throw new IllegalArgumentException("w is ECPoint.POINT_INFINITY"); 
    this.w = paramECPoint;
    this.params = paramECParameterSpec;
  }
  
  public ECPoint getW() { return this.w; }
  
  public ECParameterSpec getParams() { return this.params; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\spec\ECPublicKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */