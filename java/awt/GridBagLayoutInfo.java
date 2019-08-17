package java.awt;

import java.io.Serializable;

public class GridBagLayoutInfo implements Serializable {
  private static final long serialVersionUID = -4899416460737170217L;
  
  int width;
  
  int height;
  
  int startx;
  
  int starty;
  
  int[] minWidth;
  
  int[] minHeight;
  
  double[] weightX;
  
  double[] weightY;
  
  boolean hasBaseline;
  
  short[] baselineType;
  
  int[] maxAscent;
  
  int[] maxDescent;
  
  GridBagLayoutInfo(int paramInt1, int paramInt2) {
    this.width = paramInt1;
    this.height = paramInt2;
  }
  
  boolean hasConstantDescent(int paramInt) { return ((this.baselineType[paramInt] & 1 << Component.BaselineResizeBehavior.CONSTANT_DESCENT.ordinal()) != 0); }
  
  boolean hasBaseline(int paramInt) { return (this.hasBaseline && this.baselineType[paramInt] != 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\GridBagLayoutInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */