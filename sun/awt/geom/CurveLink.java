package sun.awt.geom;

final class CurveLink {
  Curve curve;
  
  double ytop;
  
  double ybot;
  
  int etag;
  
  CurveLink next;
  
  public CurveLink(Curve paramCurve, double paramDouble1, double paramDouble2, int paramInt) {
    this.curve = paramCurve;
    this.ytop = paramDouble1;
    this.ybot = paramDouble2;
    this.etag = paramInt;
    if (this.ytop < paramCurve.getYTop() || this.ybot > paramCurve.getYBot())
      throw new InternalError("bad curvelink [" + this.ytop + "=>" + this.ybot + "] for " + paramCurve); 
  }
  
  public boolean absorb(CurveLink paramCurveLink) { return absorb(paramCurveLink.curve, paramCurveLink.ytop, paramCurveLink.ybot, paramCurveLink.etag); }
  
  public boolean absorb(Curve paramCurve, double paramDouble1, double paramDouble2, int paramInt) {
    if (this.curve != paramCurve || this.etag != paramInt || this.ybot < paramDouble1 || this.ytop > paramDouble2)
      return false; 
    if (paramDouble1 < paramCurve.getYTop() || paramDouble2 > paramCurve.getYBot())
      throw new InternalError("bad curvelink [" + paramDouble1 + "=>" + paramDouble2 + "] for " + paramCurve); 
    this.ytop = Math.min(this.ytop, paramDouble1);
    this.ybot = Math.max(this.ybot, paramDouble2);
    return true;
  }
  
  public boolean isEmpty() { return (this.ytop == this.ybot); }
  
  public Curve getCurve() { return this.curve; }
  
  public Curve getSubCurve() { return (this.ytop == this.curve.getYTop() && this.ybot == this.curve.getYBot()) ? this.curve.getWithDirection(this.etag) : this.curve.getSubCurve(this.ytop, this.ybot, this.etag); }
  
  public Curve getMoveto() { return new Order0(getXTop(), getYTop()); }
  
  public double getXTop() { return this.curve.XforY(this.ytop); }
  
  public double getYTop() { return this.ytop; }
  
  public double getXBot() { return this.curve.XforY(this.ybot); }
  
  public double getYBot() { return this.ybot; }
  
  public double getX() { return this.curve.XforY(this.ytop); }
  
  public int getEdgeTag() { return this.etag; }
  
  public void setNext(CurveLink paramCurveLink) { this.next = paramCurveLink; }
  
  public CurveLink getNext() { return this.next; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\geom\CurveLink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */