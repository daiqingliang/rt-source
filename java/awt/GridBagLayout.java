package java.awt;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Hashtable;

public class GridBagLayout implements LayoutManager2, Serializable {
  static final int EMPIRICMULTIPLIER = 2;
  
  protected static final int MAXGRIDSIZE = 512;
  
  protected static final int MINSIZE = 1;
  
  protected static final int PREFERREDSIZE = 2;
  
  protected Hashtable<Component, GridBagConstraints> comptable = new Hashtable();
  
  protected GridBagConstraints defaultConstraints = new GridBagConstraints();
  
  protected GridBagLayoutInfo layoutInfo;
  
  public int[] columnWidths;
  
  public int[] rowHeights;
  
  public double[] columnWeights;
  
  public double[] rowWeights;
  
  private Component componentAdjusting;
  
  boolean rightToLeft = false;
  
  static final long serialVersionUID = 8838754796412211005L;
  
  public void setConstraints(Component paramComponent, GridBagConstraints paramGridBagConstraints) { this.comptable.put(paramComponent, (GridBagConstraints)paramGridBagConstraints.clone()); }
  
  public GridBagConstraints getConstraints(Component paramComponent) {
    GridBagConstraints gridBagConstraints = (GridBagConstraints)this.comptable.get(paramComponent);
    if (gridBagConstraints == null) {
      setConstraints(paramComponent, this.defaultConstraints);
      gridBagConstraints = (GridBagConstraints)this.comptable.get(paramComponent);
    } 
    return (GridBagConstraints)gridBagConstraints.clone();
  }
  
  protected GridBagConstraints lookupConstraints(Component paramComponent) {
    GridBagConstraints gridBagConstraints = (GridBagConstraints)this.comptable.get(paramComponent);
    if (gridBagConstraints == null) {
      setConstraints(paramComponent, this.defaultConstraints);
      gridBagConstraints = (GridBagConstraints)this.comptable.get(paramComponent);
    } 
    return gridBagConstraints;
  }
  
  private void removeConstraints(Component paramComponent) { this.comptable.remove(paramComponent); }
  
  public Point getLayoutOrigin() {
    Point point = new Point(0, 0);
    if (this.layoutInfo != null) {
      point.x = this.layoutInfo.startx;
      point.y = this.layoutInfo.starty;
    } 
    return point;
  }
  
  public int[][] getLayoutDimensions() {
    if (this.layoutInfo == null)
      return new int[2][0]; 
    int[][] arrayOfInt = new int[2][];
    arrayOfInt[0] = new int[this.layoutInfo.width];
    arrayOfInt[1] = new int[this.layoutInfo.height];
    System.arraycopy(this.layoutInfo.minWidth, 0, arrayOfInt[0], 0, this.layoutInfo.width);
    System.arraycopy(this.layoutInfo.minHeight, 0, arrayOfInt[1], 0, this.layoutInfo.height);
    return arrayOfInt;
  }
  
  public double[][] getLayoutWeights() {
    if (this.layoutInfo == null)
      return new double[2][0]; 
    double[][] arrayOfDouble = new double[2][];
    arrayOfDouble[0] = new double[this.layoutInfo.width];
    arrayOfDouble[1] = new double[this.layoutInfo.height];
    System.arraycopy(this.layoutInfo.weightX, 0, arrayOfDouble[0], 0, this.layoutInfo.width);
    System.arraycopy(this.layoutInfo.weightY, 0, arrayOfDouble[1], 0, this.layoutInfo.height);
    return arrayOfDouble;
  }
  
  public Point location(int paramInt1, int paramInt2) {
    Point point = new Point(0, 0);
    if (this.layoutInfo == null)
      return point; 
    int j = this.layoutInfo.startx;
    if (!this.rightToLeft) {
      for (i = 0; i < this.layoutInfo.width; i++) {
        j += this.layoutInfo.minWidth[i];
        if (j > paramInt1)
          break; 
      } 
    } else {
      for (i = this.layoutInfo.width - 1; i >= 0 && j <= paramInt1; i--)
        j += this.layoutInfo.minWidth[i]; 
      i++;
    } 
    point.x = i;
    j = this.layoutInfo.starty;
    int i;
    for (i = 0; i < this.layoutInfo.height; i++) {
      j += this.layoutInfo.minHeight[i];
      if (j > paramInt2)
        break; 
    } 
    point.y = i;
    return point;
  }
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void addLayoutComponent(Component paramComponent, Object paramObject) {
    if (paramObject instanceof GridBagConstraints) {
      setConstraints(paramComponent, (GridBagConstraints)paramObject);
    } else if (paramObject != null) {
      throw new IllegalArgumentException("cannot add to layout: constraints must be a GridBagConstraint");
    } 
  }
  
  public void removeLayoutComponent(Component paramComponent) { removeConstraints(paramComponent); }
  
  public Dimension preferredLayoutSize(Container paramContainer) {
    GridBagLayoutInfo gridBagLayoutInfo = getLayoutInfo(paramContainer, 2);
    return getMinSize(paramContainer, gridBagLayoutInfo);
  }
  
  public Dimension minimumLayoutSize(Container paramContainer) {
    GridBagLayoutInfo gridBagLayoutInfo = getLayoutInfo(paramContainer, 1);
    return getMinSize(paramContainer, gridBagLayoutInfo);
  }
  
  public Dimension maximumLayoutSize(Container paramContainer) { return new Dimension(2147483647, 2147483647); }
  
  public float getLayoutAlignmentX(Container paramContainer) { return 0.5F; }
  
  public float getLayoutAlignmentY(Container paramContainer) { return 0.5F; }
  
  public void invalidateLayout(Container paramContainer) {}
  
  public void layoutContainer(Container paramContainer) { arrangeGrid(paramContainer); }
  
  public String toString() { return getClass().getName(); }
  
  protected GridBagLayoutInfo getLayoutInfo(Container paramContainer, int paramInt) { return GetLayoutInfo(paramContainer, paramInt); }
  
  private long[] preInitMaximumArraySizes(Container paramContainer) {
    Component[] arrayOfComponent = paramContainer.getComponents();
    int i = 0;
    int j = 0;
    long[] arrayOfLong = new long[2];
    for (byte b = 0; b < arrayOfComponent.length; b++) {
      Component component = arrayOfComponent[b];
      if (component.isVisible()) {
        GridBagConstraints gridBagConstraints = lookupConstraints(component);
        int k = gridBagConstraints.gridx;
        int m = gridBagConstraints.gridy;
        int n = gridBagConstraints.gridwidth;
        int i1 = gridBagConstraints.gridheight;
        if (k < 0)
          k = ++j; 
        if (m < 0)
          m = ++i; 
        if (n <= 0)
          n = 1; 
        if (i1 <= 0)
          i1 = 1; 
        i = Math.max(m + i1, i);
        j = Math.max(k + n, j);
      } 
    } 
    arrayOfLong[0] = i;
    arrayOfLong[1] = j;
    return arrayOfLong;
  }
  
  protected GridBagLayoutInfo GetLayoutInfo(Container paramContainer, int paramInt) {
    synchronized (paramContainer.getTreeLock()) {
      Component[] arrayOfComponent = paramContainer.getComponents();
      int n = 0;
      int i1 = 0;
      int i2 = 1;
      int i3 = 1;
      int i6 = 0;
      int i7 = 0;
      int j = 0;
      int i = j;
      int i5 = -1;
      int i4 = i5;
      long[] arrayOfLong = preInitMaximumArraySizes(paramContainer);
      i6 = (2L * arrayOfLong[0] > 2147483647L) ? Integer.MAX_VALUE : (2 * (int)arrayOfLong[0]);
      i7 = (2L * arrayOfLong[1] > 2147483647L) ? Integer.MAX_VALUE : (2 * (int)arrayOfLong[1]);
      if (this.rowHeights != null)
        i6 = Math.max(i6, this.rowHeights.length); 
      if (this.columnWidths != null)
        i7 = Math.max(i7, this.columnWidths.length); 
      int[] arrayOfInt1 = new int[i6];
      int[] arrayOfInt2 = new int[i7];
      boolean bool = false;
      byte b;
      for (b = 0; b < arrayOfComponent.length; b++) {
        Component component = arrayOfComponent[b];
        if (component.isVisible()) {
          Dimension dimension;
          GridBagConstraints gridBagConstraints = lookupConstraints(component);
          n = gridBagConstraints.gridx;
          i1 = gridBagConstraints.gridy;
          i2 = gridBagConstraints.gridwidth;
          if (i2 <= 0)
            i2 = 1; 
          i3 = gridBagConstraints.gridheight;
          if (i3 <= 0)
            i3 = 1; 
          if (n < 0 && i1 < 0)
            if (i4 >= 0) {
              i1 = i4;
            } else if (i5 >= 0) {
              n = i5;
            } else {
              i1 = 0;
            }  
          if (n < 0) {
            int i12 = 0;
            for (int i11 = i1; i11 < i1 + i3; i11++)
              i12 = Math.max(i12, arrayOfInt1[i11]); 
            n = i12 - n - 1;
            if (n < 0)
              n = 0; 
          } else if (i1 < 0) {
            int i12 = 0;
            for (int i11 = n; i11 < n + i2; i11++)
              i12 = Math.max(i12, arrayOfInt2[i11]); 
            i1 = i12 - i1 - 1;
            if (i1 < 0)
              i1 = 0; 
          } 
          int i9 = n + i2;
          if (i < i9)
            i = i9; 
          int i10 = i1 + i3;
          if (j < i10)
            j = i10; 
          int i8;
          for (i8 = n; i8 < n + i2; i8++)
            arrayOfInt2[i8] = i10; 
          for (i8 = i1; i8 < i1 + i3; i8++)
            arrayOfInt1[i8] = i9; 
          if (paramInt == 2) {
            dimension = component.getPreferredSize();
          } else {
            dimension = component.getMinimumSize();
          } 
          gridBagConstraints.minWidth = dimension.width;
          gridBagConstraints.minHeight = dimension.height;
          if (calculateBaseline(component, gridBagConstraints, dimension))
            bool = true; 
          if (gridBagConstraints.gridheight == 0 && gridBagConstraints.gridwidth == 0)
            i4 = i5 = -1; 
          if (gridBagConstraints.gridheight == 0 && i4 < 0) {
            i5 = n + i2;
          } else if (gridBagConstraints.gridwidth == 0 && i5 < 0) {
            i4 = i1 + i3;
          } 
        } 
      } 
      if (this.columnWidths != null && i < this.columnWidths.length)
        i = this.columnWidths.length; 
      if (this.rowHeights != null && j < this.rowHeights.length)
        j = this.rowHeights.length; 
      GridBagLayoutInfo gridBagLayoutInfo = new GridBagLayoutInfo(i, j);
      i4 = i5 = -1;
      Arrays.fill(arrayOfInt1, 0);
      Arrays.fill(arrayOfInt2, 0);
      int[] arrayOfInt3 = null;
      int[] arrayOfInt4 = null;
      short[] arrayOfShort = null;
      if (bool) {
        gridBagLayoutInfo.maxAscent = arrayOfInt3 = new int[j];
        gridBagLayoutInfo.maxDescent = arrayOfInt4 = new int[j];
        gridBagLayoutInfo.baselineType = arrayOfShort = new short[j];
        gridBagLayoutInfo.hasBaseline = true;
      } 
      for (b = 0; b < arrayOfComponent.length; b++) {
        Component component = arrayOfComponent[b];
        if (component.isVisible()) {
          GridBagConstraints gridBagConstraints = lookupConstraints(component);
          n = gridBagConstraints.gridx;
          i1 = gridBagConstraints.gridy;
          i2 = gridBagConstraints.gridwidth;
          i3 = gridBagConstraints.gridheight;
          if (n < 0 && i1 < 0)
            if (i4 >= 0) {
              i1 = i4;
            } else if (i5 >= 0) {
              n = i5;
            } else {
              i1 = 0;
            }  
          if (n < 0) {
            if (i3 <= 0) {
              i3 += gridBagLayoutInfo.height - i1;
              if (i3 < 1)
                i3 = 1; 
            } 
            int i13 = 0;
            for (int i12 = i1; i12 < i1 + i3; i12++)
              i13 = Math.max(i13, arrayOfInt1[i12]); 
            n = i13 - n - 1;
            if (n < 0)
              n = 0; 
          } else if (i1 < 0) {
            if (i2 <= 0) {
              i2 += gridBagLayoutInfo.width - n;
              if (i2 < 1)
                i2 = 1; 
            } 
            int i13 = 0;
            for (int i12 = n; i12 < n + i2; i12++)
              i13 = Math.max(i13, arrayOfInt2[i12]); 
            i1 = i13 - i1 - 1;
            if (i1 < 0)
              i1 = 0; 
          } 
          if (i2 <= 0) {
            i2 += gridBagLayoutInfo.width - n;
            if (i2 < 1)
              i2 = 1; 
          } 
          if (i3 <= 0) {
            i3 += gridBagLayoutInfo.height - i1;
            if (i3 < 1)
              i3 = 1; 
          } 
          int i9 = n + i2;
          int i10 = i1 + i3;
          int i8;
          for (i8 = n; i8 < n + i2; i8++)
            arrayOfInt2[i8] = i10; 
          for (i8 = i1; i8 < i1 + i3; i8++)
            arrayOfInt1[i8] = i9; 
          if (gridBagConstraints.gridheight == 0 && gridBagConstraints.gridwidth == 0)
            i4 = i5 = -1; 
          if (gridBagConstraints.gridheight == 0 && i4 < 0) {
            i5 = n + i2;
          } else if (gridBagConstraints.gridwidth == 0 && i5 < 0) {
            i4 = i1 + i3;
          } 
          gridBagConstraints.tempX = n;
          gridBagConstraints.tempY = i1;
          gridBagConstraints.tempWidth = i2;
          gridBagConstraints.tempHeight = i3;
          int i11 = gridBagConstraints.anchor;
          if (bool) {
            int i12;
            switch (i11) {
              case 256:
              case 512:
              case 768:
                if (gridBagConstraints.ascent >= 0) {
                  if (i3 == 1) {
                    arrayOfInt3[i1] = Math.max(arrayOfInt3[i1], gridBagConstraints.ascent);
                    arrayOfInt4[i1] = Math.max(arrayOfInt4[i1], gridBagConstraints.descent);
                  } else if (gridBagConstraints.baselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
                    arrayOfInt4[i1 + i3 - 1] = Math.max(arrayOfInt4[i1 + i3 - 1], gridBagConstraints.descent);
                  } else {
                    arrayOfInt3[i1] = Math.max(arrayOfInt3[i1], gridBagConstraints.ascent);
                  } 
                  if (gridBagConstraints.baselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
                    arrayOfShort[i1 + i3 - 1] = (short)(arrayOfShort[i1 + i3 - 1] | 1 << gridBagConstraints.baselineResizeBehavior.ordinal());
                    break;
                  } 
                  arrayOfShort[i1] = (short)(arrayOfShort[i1] | 1 << gridBagConstraints.baselineResizeBehavior.ordinal());
                } 
                break;
              case 1024:
              case 1280:
              case 1536:
                i12 = gridBagConstraints.minHeight + gridBagConstraints.insets.top + gridBagConstraints.ipady;
                arrayOfInt3[i1] = Math.max(arrayOfInt3[i1], i12);
                arrayOfInt4[i1] = Math.max(arrayOfInt4[i1], gridBagConstraints.insets.bottom);
                break;
              case 1792:
              case 2048:
              case 2304:
                i12 = gridBagConstraints.minHeight + gridBagConstraints.insets.bottom + gridBagConstraints.ipady;
                arrayOfInt4[i1] = Math.max(arrayOfInt4[i1], i12);
                arrayOfInt3[i1] = Math.max(arrayOfInt3[i1], gridBagConstraints.insets.top);
                break;
            } 
          } 
        } 
      } 
      gridBagLayoutInfo.weightX = new double[i7];
      gridBagLayoutInfo.weightY = new double[i6];
      gridBagLayoutInfo.minWidth = new int[i7];
      gridBagLayoutInfo.minHeight = new int[i6];
      if (this.columnWidths != null)
        System.arraycopy(this.columnWidths, 0, gridBagLayoutInfo.minWidth, 0, this.columnWidths.length); 
      if (this.rowHeights != null)
        System.arraycopy(this.rowHeights, 0, gridBagLayoutInfo.minHeight, 0, this.rowHeights.length); 
      if (this.columnWeights != null)
        System.arraycopy(this.columnWeights, 0, gridBagLayoutInfo.weightX, 0, Math.min(gridBagLayoutInfo.weightX.length, this.columnWeights.length)); 
      if (this.rowWeights != null)
        System.arraycopy(this.rowWeights, 0, gridBagLayoutInfo.weightY, 0, Math.min(gridBagLayoutInfo.weightY.length, this.rowWeights.length)); 
      int m = Integer.MAX_VALUE;
      int k = 1;
      while (k != Integer.MAX_VALUE) {
        for (b = 0; b < arrayOfComponent.length; b++) {
          Component component = arrayOfComponent[b];
          if (component.isVisible()) {
            GridBagConstraints gridBagConstraints = lookupConstraints(component);
            if (gridBagConstraints.tempWidth == k) {
              int i9 = gridBagConstraints.tempX + gridBagConstraints.tempWidth;
              double d = gridBagConstraints.weightx;
              int i8;
              for (i8 = gridBagConstraints.tempX; i8 < i9; i8++)
                d -= gridBagLayoutInfo.weightX[i8]; 
              if (d > 0.0D) {
                double d1 = 0.0D;
                for (i8 = gridBagConstraints.tempX; i8 < i9; i8++)
                  d1 += gridBagLayoutInfo.weightX[i8]; 
                for (i8 = gridBagConstraints.tempX; d1 > 0.0D && i8 < i9; i8++) {
                  double d2 = gridBagLayoutInfo.weightX[i8];
                  double d3 = d2 * d / d1;
                  gridBagLayoutInfo.weightX[i8] = gridBagLayoutInfo.weightX[i8] + d3;
                  d -= d3;
                  d1 -= d2;
                } 
                gridBagLayoutInfo.weightX[i9 - 1] = gridBagLayoutInfo.weightX[i9 - 1] + d;
              } 
              int i10 = gridBagConstraints.minWidth + gridBagConstraints.ipadx + gridBagConstraints.insets.left + gridBagConstraints.insets.right;
              for (i8 = gridBagConstraints.tempX; i8 < i9; i8++)
                i10 -= gridBagLayoutInfo.minWidth[i8]; 
              if (i10 > 0) {
                double d1 = 0.0D;
                for (i8 = gridBagConstraints.tempX; i8 < i9; i8++)
                  d1 += gridBagLayoutInfo.weightX[i8]; 
                for (i8 = gridBagConstraints.tempX; d1 > 0.0D && i8 < i9; i8++) {
                  double d2 = gridBagLayoutInfo.weightX[i8];
                  int i11 = (int)(d2 * i10 / d1);
                  gridBagLayoutInfo.minWidth[i8] = gridBagLayoutInfo.minWidth[i8] + i11;
                  i10 -= i11;
                  d1 -= d2;
                } 
                gridBagLayoutInfo.minWidth[i9 - 1] = gridBagLayoutInfo.minWidth[i9 - 1] + i10;
              } 
            } else if (gridBagConstraints.tempWidth > k && gridBagConstraints.tempWidth < m) {
              m = gridBagConstraints.tempWidth;
            } 
            if (gridBagConstraints.tempHeight == k) {
              int i9 = gridBagConstraints.tempY + gridBagConstraints.tempHeight;
              double d = gridBagConstraints.weighty;
              int i8;
              for (i8 = gridBagConstraints.tempY; i8 < i9; i8++)
                d -= gridBagLayoutInfo.weightY[i8]; 
              if (d > 0.0D) {
                double d1 = 0.0D;
                for (i8 = gridBagConstraints.tempY; i8 < i9; i8++)
                  d1 += gridBagLayoutInfo.weightY[i8]; 
                for (i8 = gridBagConstraints.tempY; d1 > 0.0D && i8 < i9; i8++) {
                  double d2 = gridBagLayoutInfo.weightY[i8];
                  double d3 = d2 * d / d1;
                  gridBagLayoutInfo.weightY[i8] = gridBagLayoutInfo.weightY[i8] + d3;
                  d -= d3;
                  d1 -= d2;
                } 
                gridBagLayoutInfo.weightY[i9 - 1] = gridBagLayoutInfo.weightY[i9 - 1] + d;
              } 
              int i10 = -1;
              if (bool)
                switch (gridBagConstraints.anchor) {
                  case 256:
                  case 512:
                  case 768:
                    if (gridBagConstraints.ascent >= 0) {
                      if (gridBagConstraints.tempHeight == 1) {
                        i10 = arrayOfInt3[gridBagConstraints.tempY] + arrayOfInt4[gridBagConstraints.tempY];
                        break;
                      } 
                      if (gridBagConstraints.baselineResizeBehavior != Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
                        i10 = arrayOfInt3[gridBagConstraints.tempY] + gridBagConstraints.descent;
                        break;
                      } 
                      i10 = gridBagConstraints.ascent + arrayOfInt4[gridBagConstraints.tempY + gridBagConstraints.tempHeight - 1];
                    } 
                    break;
                  case 1024:
                  case 1280:
                  case 1536:
                    i10 = gridBagConstraints.insets.top + gridBagConstraints.minHeight + gridBagConstraints.ipady + arrayOfInt4[gridBagConstraints.tempY];
                    break;
                  case 1792:
                  case 2048:
                  case 2304:
                    i10 = arrayOfInt3[gridBagConstraints.tempY] + gridBagConstraints.minHeight + gridBagConstraints.insets.bottom + gridBagConstraints.ipady;
                    break;
                }  
              if (i10 == -1)
                i10 = gridBagConstraints.minHeight + gridBagConstraints.ipady + gridBagConstraints.insets.top + gridBagConstraints.insets.bottom; 
              for (i8 = gridBagConstraints.tempY; i8 < i9; i8++)
                i10 -= gridBagLayoutInfo.minHeight[i8]; 
              if (i10 > 0) {
                double d1 = 0.0D;
                for (i8 = gridBagConstraints.tempY; i8 < i9; i8++)
                  d1 += gridBagLayoutInfo.weightY[i8]; 
                for (i8 = gridBagConstraints.tempY; d1 > 0.0D && i8 < i9; i8++) {
                  double d2 = gridBagLayoutInfo.weightY[i8];
                  int i11 = (int)(d2 * i10 / d1);
                  gridBagLayoutInfo.minHeight[i8] = gridBagLayoutInfo.minHeight[i8] + i11;
                  i10 -= i11;
                  d1 -= d2;
                } 
                gridBagLayoutInfo.minHeight[i9 - 1] = gridBagLayoutInfo.minHeight[i9 - 1] + i10;
              } 
            } else if (gridBagConstraints.tempHeight > k && gridBagConstraints.tempHeight < m) {
              m = gridBagConstraints.tempHeight;
            } 
          } 
        } 
        k = m;
        m = Integer.MAX_VALUE;
      } 
      return gridBagLayoutInfo;
    } 
  }
  
  private boolean calculateBaseline(Component paramComponent, GridBagConstraints paramGridBagConstraints, Dimension paramDimension) {
    int i = paramGridBagConstraints.anchor;
    if (i == 256 || i == 512 || i == 768) {
      int j = paramDimension.width + paramGridBagConstraints.ipadx;
      int k = paramDimension.height + paramGridBagConstraints.ipady;
      paramGridBagConstraints.ascent = paramComponent.getBaseline(j, k);
      if (paramGridBagConstraints.ascent >= 0) {
        int m = paramGridBagConstraints.ascent;
        paramGridBagConstraints.descent = k - paramGridBagConstraints.ascent + paramGridBagConstraints.insets.bottom;
        paramGridBagConstraints.ascent += paramGridBagConstraints.insets.top;
        paramGridBagConstraints.baselineResizeBehavior = paramComponent.getBaselineResizeBehavior();
        paramGridBagConstraints.centerPadding = 0;
        if (paramGridBagConstraints.baselineResizeBehavior == Component.BaselineResizeBehavior.CENTER_OFFSET) {
          int n = paramComponent.getBaseline(j, k + 1);
          paramGridBagConstraints.centerOffset = m - k / 2;
          if (k % 2 == 0) {
            if (m != n)
              paramGridBagConstraints.centerPadding = 1; 
          } else if (m == n) {
            paramGridBagConstraints.centerOffset--;
            paramGridBagConstraints.centerPadding = 1;
          } 
        } 
      } 
      return true;
    } 
    paramGridBagConstraints.ascent = -1;
    return false;
  }
  
  protected void adjustForGravity(GridBagConstraints paramGridBagConstraints, Rectangle paramRectangle) { AdjustForGravity(paramGridBagConstraints, paramRectangle); }
  
  protected void AdjustForGravity(GridBagConstraints paramGridBagConstraints, Rectangle paramRectangle) {
    int k = paramRectangle.y;
    int m = paramRectangle.height;
    if (!this.rightToLeft) {
      paramRectangle.x += paramGridBagConstraints.insets.left;
    } else {
      paramRectangle.x -= paramRectangle.width - paramGridBagConstraints.insets.right;
    } 
    paramRectangle.width -= paramGridBagConstraints.insets.left + paramGridBagConstraints.insets.right;
    paramRectangle.y += paramGridBagConstraints.insets.top;
    paramRectangle.height -= paramGridBagConstraints.insets.top + paramGridBagConstraints.insets.bottom;
    int i = 0;
    if (paramGridBagConstraints.fill != 2 && paramGridBagConstraints.fill != 1 && paramRectangle.width > paramGridBagConstraints.minWidth + paramGridBagConstraints.ipadx) {
      i = paramRectangle.width - paramGridBagConstraints.minWidth + paramGridBagConstraints.ipadx;
      paramRectangle.width = paramGridBagConstraints.minWidth + paramGridBagConstraints.ipadx;
    } 
    int j = 0;
    if (paramGridBagConstraints.fill != 3 && paramGridBagConstraints.fill != 1 && paramRectangle.height > paramGridBagConstraints.minHeight + paramGridBagConstraints.ipady) {
      j = paramRectangle.height - paramGridBagConstraints.minHeight + paramGridBagConstraints.ipady;
      paramRectangle.height = paramGridBagConstraints.minHeight + paramGridBagConstraints.ipady;
    } 
    switch (paramGridBagConstraints.anchor) {
      case 256:
        paramRectangle.x += i / 2;
        alignOnBaseline(paramGridBagConstraints, paramRectangle, k, m);
      case 512:
        if (this.rightToLeft)
          paramRectangle.x += i; 
        alignOnBaseline(paramGridBagConstraints, paramRectangle, k, m);
      case 768:
        if (!this.rightToLeft)
          paramRectangle.x += i; 
        alignOnBaseline(paramGridBagConstraints, paramRectangle, k, m);
      case 1024:
        paramRectangle.x += i / 2;
        alignAboveBaseline(paramGridBagConstraints, paramRectangle, k, m);
      case 1280:
        if (this.rightToLeft)
          paramRectangle.x += i; 
        alignAboveBaseline(paramGridBagConstraints, paramRectangle, k, m);
      case 1536:
        if (!this.rightToLeft)
          paramRectangle.x += i; 
        alignAboveBaseline(paramGridBagConstraints, paramRectangle, k, m);
      case 1792:
        paramRectangle.x += i / 2;
        alignBelowBaseline(paramGridBagConstraints, paramRectangle, k, m);
      case 2048:
        if (this.rightToLeft)
          paramRectangle.x += i; 
        alignBelowBaseline(paramGridBagConstraints, paramRectangle, k, m);
      case 2304:
        if (!this.rightToLeft)
          paramRectangle.x += i; 
        alignBelowBaseline(paramGridBagConstraints, paramRectangle, k, m);
      case 10:
        paramRectangle.x += i / 2;
        paramRectangle.y += j / 2;
      case 11:
      case 19:
        paramRectangle.x += i / 2;
      case 12:
        paramRectangle.x += i;
      case 13:
        paramRectangle.x += i;
        paramRectangle.y += j / 2;
      case 14:
        paramRectangle.x += i;
        paramRectangle.y += j;
      case 15:
      case 20:
        paramRectangle.x += i / 2;
        paramRectangle.y += j;
      case 16:
        paramRectangle.y += j;
      case 17:
        paramRectangle.y += j / 2;
      case 18:
        return;
      case 21:
        if (this.rightToLeft)
          paramRectangle.x += i; 
        paramRectangle.y += j / 2;
      case 22:
        if (!this.rightToLeft)
          paramRectangle.x += i; 
        paramRectangle.y += j / 2;
      case 23:
        if (this.rightToLeft)
          paramRectangle.x += i; 
      case 24:
        if (!this.rightToLeft)
          paramRectangle.x += i; 
      case 25:
        if (this.rightToLeft)
          paramRectangle.x += i; 
        paramRectangle.y += j;
      case 26:
        if (!this.rightToLeft)
          paramRectangle.x += i; 
        paramRectangle.y += j;
    } 
    throw new IllegalArgumentException("illegal anchor value");
  }
  
  private void alignOnBaseline(GridBagConstraints paramGridBagConstraints, Rectangle paramRectangle, int paramInt1, int paramInt2) {
    if (paramGridBagConstraints.ascent >= 0) {
      if (paramGridBagConstraints.baselineResizeBehavior == Component.BaselineResizeBehavior.CONSTANT_DESCENT) {
        int i = paramInt1 + paramInt2 - this.layoutInfo.maxDescent[paramGridBagConstraints.tempY + paramGridBagConstraints.tempHeight - 1] + paramGridBagConstraints.descent - paramGridBagConstraints.insets.bottom;
        if (!paramGridBagConstraints.isVerticallyResizable()) {
          paramRectangle.y = i - paramGridBagConstraints.minHeight;
          paramRectangle.height = paramGridBagConstraints.minHeight;
        } else {
          paramRectangle.height = i - paramInt1 - paramGridBagConstraints.insets.top;
        } 
      } else {
        int i;
        int j = paramGridBagConstraints.ascent;
        if (this.layoutInfo.hasConstantDescent(paramGridBagConstraints.tempY)) {
          i = paramInt2 - this.layoutInfo.maxDescent[paramGridBagConstraints.tempY];
        } else {
          i = this.layoutInfo.maxAscent[paramGridBagConstraints.tempY];
        } 
        if (paramGridBagConstraints.baselineResizeBehavior == Component.BaselineResizeBehavior.OTHER) {
          boolean bool = false;
          j = this.componentAdjusting.getBaseline(paramRectangle.width, paramRectangle.height);
          if (j >= 0)
            j += paramGridBagConstraints.insets.top; 
          if (j >= 0 && j <= i)
            if (i + paramRectangle.height - j - paramGridBagConstraints.insets.top <= paramInt2 - paramGridBagConstraints.insets.bottom) {
              bool = true;
            } else if (paramGridBagConstraints.isVerticallyResizable()) {
              int k = this.componentAdjusting.getBaseline(paramRectangle.width, paramInt2 - paramGridBagConstraints.insets.bottom - i + j);
              if (k >= 0)
                k += paramGridBagConstraints.insets.top; 
              if (k >= 0 && k <= j) {
                paramRectangle.height = paramInt2 - paramGridBagConstraints.insets.bottom - i + j;
                j = k;
                bool = true;
              } 
            }  
          if (!bool) {
            j = paramGridBagConstraints.ascent;
            paramRectangle.width = paramGridBagConstraints.minWidth;
            paramRectangle.height = paramGridBagConstraints.minHeight;
          } 
        } 
        paramRectangle.y = paramInt1 + i - j + paramGridBagConstraints.insets.top;
        if (paramGridBagConstraints.isVerticallyResizable()) {
          int n;
          int m;
          int k;
          switch (paramGridBagConstraints.baselineResizeBehavior) {
            case CONSTANT_ASCENT:
              paramRectangle.height = Math.max(paramGridBagConstraints.minHeight, paramInt1 + paramInt2 - paramRectangle.y - paramGridBagConstraints.insets.bottom);
              break;
            case CENTER_OFFSET:
              k = paramRectangle.y - paramInt1 - paramGridBagConstraints.insets.top;
              m = paramInt1 + paramInt2 - paramRectangle.y - paramGridBagConstraints.minHeight - paramGridBagConstraints.insets.bottom;
              n = Math.min(k, m);
              n += n;
              if (n > 0 && (paramGridBagConstraints.minHeight + paramGridBagConstraints.centerPadding + n) / 2 + paramGridBagConstraints.centerOffset != i)
                n--; 
              paramRectangle.height = paramGridBagConstraints.minHeight + n;
              paramRectangle.y = paramInt1 + i - (paramRectangle.height + paramGridBagConstraints.centerPadding) / 2 - paramGridBagConstraints.centerOffset;
              break;
          } 
        } 
      } 
    } else {
      centerVertically(paramGridBagConstraints, paramRectangle, paramInt2);
    } 
  }
  
  private void alignAboveBaseline(GridBagConstraints paramGridBagConstraints, Rectangle paramRectangle, int paramInt1, int paramInt2) {
    if (this.layoutInfo.hasBaseline(paramGridBagConstraints.tempY)) {
      int i;
      if (this.layoutInfo.hasConstantDescent(paramGridBagConstraints.tempY)) {
        i = paramInt1 + paramInt2 - this.layoutInfo.maxDescent[paramGridBagConstraints.tempY];
      } else {
        i = paramInt1 + this.layoutInfo.maxAscent[paramGridBagConstraints.tempY];
      } 
      if (paramGridBagConstraints.isVerticallyResizable()) {
        paramRectangle.y = paramInt1 + paramGridBagConstraints.insets.top;
        paramRectangle.height = i - paramRectangle.y;
      } else {
        paramRectangle.height = paramGridBagConstraints.minHeight + paramGridBagConstraints.ipady;
        paramRectangle.y = i - paramRectangle.height;
      } 
    } else {
      centerVertically(paramGridBagConstraints, paramRectangle, paramInt2);
    } 
  }
  
  private void alignBelowBaseline(GridBagConstraints paramGridBagConstraints, Rectangle paramRectangle, int paramInt1, int paramInt2) {
    if (this.layoutInfo.hasBaseline(paramGridBagConstraints.tempY)) {
      if (this.layoutInfo.hasConstantDescent(paramGridBagConstraints.tempY)) {
        paramRectangle.y = paramInt1 + paramInt2 - this.layoutInfo.maxDescent[paramGridBagConstraints.tempY];
      } else {
        paramRectangle.y = paramInt1 + this.layoutInfo.maxAscent[paramGridBagConstraints.tempY];
      } 
      if (paramGridBagConstraints.isVerticallyResizable())
        paramRectangle.height = paramInt1 + paramInt2 - paramRectangle.y - paramGridBagConstraints.insets.bottom; 
    } else {
      centerVertically(paramGridBagConstraints, paramRectangle, paramInt2);
    } 
  }
  
  private void centerVertically(GridBagConstraints paramGridBagConstraints, Rectangle paramRectangle, int paramInt) {
    if (!paramGridBagConstraints.isVerticallyResizable())
      paramRectangle.y += Math.max(0, (paramInt - paramGridBagConstraints.insets.top - paramGridBagConstraints.insets.bottom - paramGridBagConstraints.minHeight - paramGridBagConstraints.ipady) / 2); 
  }
  
  protected Dimension getMinSize(Container paramContainer, GridBagLayoutInfo paramGridBagLayoutInfo) { return GetMinSize(paramContainer, paramGridBagLayoutInfo); }
  
  protected Dimension GetMinSize(Container paramContainer, GridBagLayoutInfo paramGridBagLayoutInfo) {
    Dimension dimension = new Dimension();
    Insets insets = paramContainer.getInsets();
    int i = 0;
    byte b;
    for (b = 0; b < paramGridBagLayoutInfo.width; b++)
      i += paramGridBagLayoutInfo.minWidth[b]; 
    dimension.width = i + insets.left + insets.right;
    i = 0;
    for (b = 0; b < paramGridBagLayoutInfo.height; b++)
      i += paramGridBagLayoutInfo.minHeight[b]; 
    dimension.height = i + insets.top + insets.bottom;
    return dimension;
  }
  
  protected void arrangeGrid(Container paramContainer) { ArrangeGrid(paramContainer); }
  
  protected void ArrangeGrid(Container paramContainer) {
    Insets insets = paramContainer.getInsets();
    Component[] arrayOfComponent = paramContainer.getComponents();
    Rectangle rectangle = new Rectangle();
    this.rightToLeft = !paramContainer.getComponentOrientation().isLeftToRight();
    if (arrayOfComponent.length == 0 && (this.columnWidths == null || this.columnWidths.length == 0) && (this.rowHeights == null || this.rowHeights.length == 0))
      return; 
    GridBagLayoutInfo gridBagLayoutInfo = getLayoutInfo(paramContainer, 2);
    Dimension dimension = getMinSize(paramContainer, gridBagLayoutInfo);
    if (paramContainer.width < dimension.width || paramContainer.height < dimension.height) {
      gridBagLayoutInfo = getLayoutInfo(paramContainer, 1);
      dimension = getMinSize(paramContainer, gridBagLayoutInfo);
    } 
    this.layoutInfo = gridBagLayoutInfo;
    rectangle.width = dimension.width;
    rectangle.height = dimension.height;
    int i = paramContainer.width - rectangle.width;
    if (i != 0) {
      double d = 0.0D;
      byte b1;
      for (b1 = 0; b1 < gridBagLayoutInfo.width; b1++)
        d += gridBagLayoutInfo.weightX[b1]; 
      if (d > 0.0D)
        for (b1 = 0; b1 < gridBagLayoutInfo.width; b1++) {
          int k = (int)(i * gridBagLayoutInfo.weightX[b1] / d);
          gridBagLayoutInfo.minWidth[b1] = gridBagLayoutInfo.minWidth[b1] + k;
          rectangle.width += k;
          if (gridBagLayoutInfo.minWidth[b1] < 0) {
            rectangle.width -= gridBagLayoutInfo.minWidth[b1];
            gridBagLayoutInfo.minWidth[b1] = 0;
          } 
        }  
      i = paramContainer.width - rectangle.width;
    } else {
      i = 0;
    } 
    int j = paramContainer.height - rectangle.height;
    if (j != 0) {
      double d = 0.0D;
      byte b1;
      for (b1 = 0; b1 < gridBagLayoutInfo.height; b1++)
        d += gridBagLayoutInfo.weightY[b1]; 
      if (d > 0.0D)
        for (b1 = 0; b1 < gridBagLayoutInfo.height; b1++) {
          int k = (int)(j * gridBagLayoutInfo.weightY[b1] / d);
          gridBagLayoutInfo.minHeight[b1] = gridBagLayoutInfo.minHeight[b1] + k;
          rectangle.height += k;
          if (gridBagLayoutInfo.minHeight[b1] < 0) {
            rectangle.height -= gridBagLayoutInfo.minHeight[b1];
            gridBagLayoutInfo.minHeight[b1] = 0;
          } 
        }  
      j = paramContainer.height - rectangle.height;
    } else {
      j = 0;
    } 
    gridBagLayoutInfo.startx = i / 2 + insets.left;
    gridBagLayoutInfo.starty = j / 2 + insets.top;
    for (byte b = 0; b < arrayOfComponent.length; b++) {
      Component component = arrayOfComponent[b];
      if (component.isVisible()) {
        GridBagConstraints gridBagConstraints = lookupConstraints(component);
        if (!this.rightToLeft) {
          rectangle.x = gridBagLayoutInfo.startx;
          for (byte b1 = 0; b1 < gridBagConstraints.tempX; b1++)
            rectangle.x += gridBagLayoutInfo.minWidth[b1]; 
        } else {
          rectangle.x = paramContainer.width - i / 2 + insets.right;
          for (byte b1 = 0; b1 < gridBagConstraints.tempX; b1++)
            rectangle.x -= gridBagLayoutInfo.minWidth[b1]; 
        } 
        rectangle.y = gridBagLayoutInfo.starty;
        int k;
        for (k = 0; k < gridBagConstraints.tempY; k++)
          rectangle.y += gridBagLayoutInfo.minHeight[k]; 
        rectangle.width = 0;
        for (k = gridBagConstraints.tempX; k < gridBagConstraints.tempX + gridBagConstraints.tempWidth; k++)
          rectangle.width += gridBagLayoutInfo.minWidth[k]; 
        rectangle.height = 0;
        for (k = gridBagConstraints.tempY; k < gridBagConstraints.tempY + gridBagConstraints.tempHeight; k++)
          rectangle.height += gridBagLayoutInfo.minHeight[k]; 
        this.componentAdjusting = component;
        adjustForGravity(gridBagConstraints, rectangle);
        if (rectangle.x < 0) {
          rectangle.width += rectangle.x;
          rectangle.x = 0;
        } 
        if (rectangle.y < 0) {
          rectangle.height += rectangle.y;
          rectangle.y = 0;
        } 
        if (rectangle.width <= 0 || rectangle.height <= 0) {
          component.setBounds(0, 0, 0, 0);
        } else if (component.x != rectangle.x || component.y != rectangle.y || component.width != rectangle.width || component.height != rectangle.height) {
          component.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        } 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\GridBagLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */