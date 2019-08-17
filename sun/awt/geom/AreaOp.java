package sun.awt.geom;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

public abstract class AreaOp {
  public static final int CTAG_LEFT = 0;
  
  public static final int CTAG_RIGHT = 1;
  
  public static final int ETAG_IGNORE = 0;
  
  public static final int ETAG_ENTER = 1;
  
  public static final int ETAG_EXIT = -1;
  
  public static final int RSTAG_INSIDE = 1;
  
  public static final int RSTAG_OUTSIDE = -1;
  
  private static Comparator YXTopComparator = new Comparator() {
      public int compare(Object param1Object1, Object param1Object2) {
        Curve curve1 = ((Edge)param1Object1).getCurve();
        Curve curve2 = ((Edge)param1Object2).getCurve();
        double d1;
        double d2;
        return ((d1 = curve1.getYTop()) == (d2 = curve2.getYTop()) && (d1 = curve1.getXTop()) == (d2 = curve2.getXTop())) ? 0 : ((d1 < d2) ? -1 : 1);
      }
    };
  
  private static CurveLink[] EmptyLinkList = new CurveLink[2];
  
  private static ChainEnd[] EmptyChainList = new ChainEnd[2];
  
  private AreaOp() {}
  
  public abstract void newRow();
  
  public abstract int classify(Edge paramEdge);
  
  public abstract int getState();
  
  public Vector calculate(Vector paramVector1, Vector paramVector2) {
    null = new Vector();
    addEdges(null, paramVector1, 0);
    addEdges(null, paramVector2, 1);
    return pruneEdges(null);
  }
  
  private static void addEdges(Vector paramVector1, Vector paramVector2, int paramInt) {
    Enumeration enumeration = paramVector2.elements();
    while (enumeration.hasMoreElements()) {
      Curve curve = (Curve)enumeration.nextElement();
      if (curve.getOrder() > 0)
        paramVector1.add(new Edge(curve, paramInt)); 
    } 
  }
  
  private Vector pruneEdges(Vector paramVector) {
    int i = paramVector.size();
    if (i < 2)
      return paramVector; 
    Edge[] arrayOfEdge = (Edge[])paramVector.toArray(new Edge[i]);
    Arrays.sort(arrayOfEdge, YXTopComparator);
    byte b1 = 0;
    byte b2 = 0;
    byte b3 = 0;
    byte b4 = 0;
    double[] arrayOfDouble = new double[2];
    Vector vector1 = new Vector();
    Vector vector2 = new Vector();
    Vector vector3 = new Vector();
    while (b1 < i) {
      double d1 = arrayOfDouble[0];
      for (b3 = b4 = b2 - true; b3 >= b1; b3--) {
        Edge edge = arrayOfEdge[b3];
        if (edge.getCurve().getYBot() > d1) {
          if (b4 > b3)
            arrayOfEdge[b4] = edge; 
          b4--;
        } 
      } 
      b1 = b4 + 1;
      if (b1 >= b2) {
        if (b2 >= i)
          break; 
        d1 = arrayOfEdge[b2].getCurve().getYTop();
        if (d1 > arrayOfDouble[0])
          finalizeSubCurves(vector1, vector2); 
        arrayOfDouble[0] = d1;
      } 
      while (b2 < i) {
        Edge edge = arrayOfEdge[b2];
        if (edge.getCurve().getYTop() > d1)
          break; 
        b2++;
      } 
      arrayOfDouble[1] = arrayOfEdge[b1].getCurve().getYBot();
      if (b2 < i) {
        d1 = arrayOfEdge[b2].getCurve().getYTop();
        if (arrayOfDouble[1] > d1)
          arrayOfDouble[1] = d1; 
      } 
      byte b = 1;
      for (b3 = b1; b3 < b2; b3++) {
        Edge edge = arrayOfEdge[b3];
        edge.setEquivalence(0);
        for (b4 = b3; b4 > b1; b4--) {
          Edge edge1 = arrayOfEdge[b4 - 1];
          int j = edge.compareTo(edge1, arrayOfDouble);
          if (arrayOfDouble[1] <= arrayOfDouble[0])
            throw new InternalError("backstepping to " + arrayOfDouble[1] + " from " + arrayOfDouble[0]); 
          if (j >= 0) {
            if (j == 0) {
              int k = edge1.getEquivalence();
              if (k == 0) {
                k = b++;
                edge1.setEquivalence(k);
              } 
              edge.setEquivalence(k);
            } 
            break;
          } 
          arrayOfEdge[b4] = edge1;
        } 
        arrayOfEdge[b4] = edge;
      } 
      newRow();
      double d2 = arrayOfDouble[0];
      double d3 = arrayOfDouble[1];
      for (b3 = b1; b3 < b2; b3++) {
        int j;
        Edge edge = arrayOfEdge[b3];
        int k = edge.getEquivalence();
        if (k != 0) {
          int m = getState();
          j = (m == 1) ? -1 : 1;
          Edge edge1 = null;
          Edge edge2 = edge;
          double d = d3;
          do {
            classify(edge);
            if (edge1 == null && edge.isActiveFor(d2, j))
              edge1 = edge; 
            d1 = edge.getCurve().getYBot();
            if (d1 <= d)
              continue; 
            edge2 = edge;
            d = d1;
          } while (++b3 < b2 && (edge = arrayOfEdge[b3]).getEquivalence() == k);
          b3--;
          if (getState() == m) {
            j = 0;
          } else {
            edge = (edge1 != null) ? edge1 : edge2;
          } 
        } else {
          j = classify(edge);
        } 
        if (j != 0) {
          edge.record(d3, j);
          vector3.add(new CurveLink(edge.getCurve(), d2, d3, j));
        } 
      } 
      if (getState() != -1) {
        System.out.println("Still inside at end of active edge list!");
        System.out.println("num curves = " + (b2 - b1));
        System.out.println("num links = " + vector3.size());
        System.out.println("y top = " + arrayOfDouble[0]);
        if (b2 < i) {
          System.out.println("y top of next curve = " + arrayOfEdge[b2].getCurve().getYTop());
        } else {
          System.out.println("no more curves");
        } 
        for (b3 = b1; b3 < b2; b3++) {
          Edge edge = arrayOfEdge[b3];
          System.out.println(edge);
          int j = edge.getEquivalence();
          if (j != 0)
            System.out.println("  was equal to " + j + "..."); 
        } 
      } 
      resolveLinks(vector1, vector2, vector3);
      vector3.clear();
      arrayOfDouble[0] = d3;
    } 
    finalizeSubCurves(vector1, vector2);
    Vector vector4 = new Vector();
    Enumeration enumeration = vector1.elements();
    while (enumeration.hasMoreElements()) {
      CurveLink curveLink1 = (CurveLink)enumeration.nextElement();
      vector4.add(curveLink1.getMoveto());
      CurveLink curveLink2 = curveLink1;
      while ((curveLink2 = curveLink2.getNext()) != null) {
        if (!curveLink1.absorb(curveLink2)) {
          vector4.add(curveLink1.getSubCurve());
          curveLink1 = curveLink2;
        } 
      } 
      vector4.add(curveLink1.getSubCurve());
    } 
    return vector4;
  }
  
  public static void finalizeSubCurves(Vector paramVector1, Vector paramVector2) {
    int i = paramVector2.size();
    if (i == 0)
      return; 
    if ((i & true) != 0)
      throw new InternalError("Odd number of chains!"); 
    ChainEnd[] arrayOfChainEnd = new ChainEnd[i];
    paramVector2.toArray(arrayOfChainEnd);
    for (boolean bool = true; bool < i; bool += true) {
      ChainEnd chainEnd1 = arrayOfChainEnd[bool - true];
      ChainEnd chainEnd2 = arrayOfChainEnd[bool];
      CurveLink curveLink = chainEnd1.linkTo(chainEnd2);
      if (curveLink != null)
        paramVector1.add(curveLink); 
    } 
    paramVector2.clear();
  }
  
  public static void resolveLinks(Vector paramVector1, Vector paramVector2, Vector paramVector3) {
    ChainEnd[] arrayOfChainEnd;
    CurveLink[] arrayOfCurveLink;
    int i = paramVector3.size();
    if (i == 0) {
      arrayOfCurveLink = EmptyLinkList;
    } else {
      if ((i & true) != 0)
        throw new InternalError("Odd number of new curves!"); 
      arrayOfCurveLink = new CurveLink[i + 2];
      paramVector3.toArray(arrayOfCurveLink);
    } 
    int j = paramVector2.size();
    if (j == 0) {
      arrayOfChainEnd = EmptyChainList;
    } else {
      if ((j & true) != 0)
        throw new InternalError("Odd number of chains!"); 
      arrayOfChainEnd = new ChainEnd[j + 2];
      paramVector2.toArray(arrayOfChainEnd);
    } 
    byte b1 = 0;
    byte b2 = 0;
    paramVector2.clear();
    ChainEnd chainEnd1 = arrayOfChainEnd[0];
    ChainEnd chainEnd2 = arrayOfChainEnd[1];
    CurveLink curveLink1 = arrayOfCurveLink[0];
    CurveLink curveLink2 = arrayOfCurveLink[1];
    while (chainEnd1 != null || curveLink1 != null) {
      boolean bool1 = (curveLink1 == null) ? 1 : 0;
      boolean bool2 = (chainEnd1 == null) ? 1 : 0;
      if (!bool1 && !bool2) {
        bool1 = (!(b1 & true) && chainEnd1.getX() == chainEnd2.getX()) ? 1 : 0;
        bool2 = (!(b2 & true) && curveLink1.getX() == curveLink2.getX()) ? 1 : 0;
        if (!bool1 && !bool2) {
          double d1 = chainEnd1.getX();
          double d2 = curveLink1.getX();
          bool1 = (chainEnd2 != null && d1 < d2 && obstructs(chainEnd2.getX(), d2, b1)) ? 1 : 0;
          bool2 = (curveLink2 != null && d2 < d1 && obstructs(curveLink2.getX(), d1, b2)) ? 1 : 0;
        } 
      } 
      if (bool1) {
        CurveLink curveLink = chainEnd1.linkTo(chainEnd2);
        if (curveLink != null)
          paramVector1.add(curveLink); 
        b1 += 2;
        chainEnd1 = arrayOfChainEnd[b1];
        chainEnd2 = arrayOfChainEnd[b1 + 1];
      } 
      if (bool2) {
        ChainEnd chainEnd3 = new ChainEnd(curveLink1, null);
        ChainEnd chainEnd4 = new ChainEnd(curveLink2, chainEnd3);
        chainEnd3.setOtherEnd(chainEnd4);
        paramVector2.add(chainEnd3);
        paramVector2.add(chainEnd4);
        b2 += 2;
        curveLink1 = arrayOfCurveLink[b2];
        curveLink2 = arrayOfCurveLink[b2 + 1];
      } 
      if (!bool1 && !bool2) {
        chainEnd1.addLink(curveLink1);
        paramVector2.add(chainEnd1);
        b1++;
        chainEnd1 = chainEnd2;
        chainEnd2 = arrayOfChainEnd[b1 + 1];
        b2++;
        curveLink1 = curveLink2;
        curveLink2 = arrayOfCurveLink[b2 + 1];
      } 
    } 
    if ((paramVector2.size() & true) != 0)
      System.out.println("Odd number of chains!"); 
  }
  
  public static boolean obstructs(double paramDouble1, double paramDouble2, int paramInt) { return ((paramInt & true) == 0) ? ((paramDouble1 <= paramDouble2)) : ((paramDouble1 < paramDouble2)); }
  
  public static class AddOp extends CAGOp {
    public boolean newClassification(boolean param1Boolean1, boolean param1Boolean2) { return (param1Boolean1 || param1Boolean2); }
  }
  
  public static abstract class CAGOp extends AreaOp {
    boolean inLeft;
    
    boolean inRight;
    
    boolean inResult;
    
    public CAGOp() { super(null); }
    
    public void newRow() {
      this.inLeft = false;
      this.inRight = false;
      this.inResult = false;
    }
    
    public int classify(Edge param1Edge) {
      if (param1Edge.getCurveTag() == 0) {
        this.inLeft = !this.inLeft;
      } else {
        this.inRight = !this.inRight;
      } 
      boolean bool = newClassification(this.inLeft, this.inRight);
      if (this.inResult == bool)
        return 0; 
      this.inResult = bool;
      return bool ? 1 : -1;
    }
    
    public int getState() { return this.inResult ? 1 : -1; }
    
    public abstract boolean newClassification(boolean param1Boolean1, boolean param1Boolean2);
  }
  
  public static class EOWindOp extends AreaOp {
    private boolean inside;
    
    public EOWindOp() { super(null); }
    
    public void newRow() { this.inside = false; }
    
    public int classify(Edge param1Edge) {
      boolean bool = !this.inside ? 1 : 0;
      this.inside = bool;
      return bool ? 1 : -1;
    }
    
    public int getState() { return this.inside ? 1 : -1; }
  }
  
  public static class IntOp extends CAGOp {
    public boolean newClassification(boolean param1Boolean1, boolean param1Boolean2) { return (param1Boolean1 && param1Boolean2); }
  }
  
  public static class NZWindOp extends AreaOp {
    private int count;
    
    public NZWindOp() { super(null); }
    
    public void newRow() { this.count = 0; }
    
    public int classify(Edge param1Edge) {
      int i = this.count;
      boolean bool = (i == 0) ? 1 : 0;
      i += param1Edge.getCurve().getDirection();
      this.count = i;
      return (i == 0) ? -1 : bool;
    }
    
    public int getState() { return (this.count == 0) ? -1 : 1; }
  }
  
  public static class SubOp extends CAGOp {
    public boolean newClassification(boolean param1Boolean1, boolean param1Boolean2) { return (param1Boolean1 && !param1Boolean2); }
  }
  
  public static class XorOp extends CAGOp {
    public boolean newClassification(boolean param1Boolean1, boolean param1Boolean2) { return (param1Boolean1 != param1Boolean2); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\geom\AreaOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */