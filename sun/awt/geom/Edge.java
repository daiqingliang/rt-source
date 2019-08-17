package sun.awt.geom;

final class Edge {
  static final int INIT_PARTS = 4;
  
  static final int GROW_PARTS = 10;
  
  Curve curve;
  
  int ctag;
  
  int etag;
  
  double activey;
  
  int equivalence;
  
  private Edge lastEdge;
  
  private int lastResult;
  
  private double lastLimit;
  
  public Edge(Curve paramCurve, int paramInt) { this(paramCurve, paramInt, 0); }
  
  public Edge(Curve paramCurve, int paramInt1, int paramInt2) {
    this.curve = paramCurve;
    this.ctag = paramInt1;
    this.etag = paramInt2;
  }
  
  public Curve getCurve() { return this.curve; }
  
  public int getCurveTag() { return this.ctag; }
  
  public int getEdgeTag() { return this.etag; }
  
  public void setEdgeTag(int paramInt) { this.etag = paramInt; }
  
  public int getEquivalence() { return this.equivalence; }
  
  public void setEquivalence(int paramInt) { this.equivalence = paramInt; }
  
  public int compareTo(Edge paramEdge, double[] paramArrayOfDouble) {
    if (paramEdge == this.lastEdge && paramArrayOfDouble[0] < this.lastLimit) {
      if (paramArrayOfDouble[1] > this.lastLimit)
        paramArrayOfDouble[1] = this.lastLimit; 
      return this.lastResult;
    } 
    if (this == paramEdge.lastEdge && paramArrayOfDouble[0] < paramEdge.lastLimit) {
      if (paramArrayOfDouble[1] > paramEdge.lastLimit)
        paramArrayOfDouble[1] = paramEdge.lastLimit; 
      return 0 - paramEdge.lastResult;
    } 
    int i = this.curve.compareTo(paramEdge.curve, paramArrayOfDouble);
    this.lastEdge = paramEdge;
    this.lastLimit = paramArrayOfDouble[1];
    this.lastResult = i;
    return i;
  }
  
  public void record(double paramDouble, int paramInt) {
    this.activey = paramDouble;
    this.etag = paramInt;
  }
  
  public boolean isActiveFor(double paramDouble, int paramInt) { return (this.etag == paramInt && this.activey >= paramDouble); }
  
  public String toString() { return "Edge[" + this.curve + ", " + ((this.ctag == 0) ? "L" : "R") + ", " + ((this.etag == 1) ? "I" : ((this.etag == -1) ? "O" : "N")) + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\geom\Edge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */