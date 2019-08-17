package java.awt;

import java.io.Serializable;

public class GridLayout implements LayoutManager, Serializable {
  private static final long serialVersionUID = -7411804673224730901L;
  
  int hgap;
  
  int vgap;
  
  int rows;
  
  int cols;
  
  public GridLayout() { this(1, 0, 0, 0); }
  
  public GridLayout(int paramInt1, int paramInt2) { this(paramInt1, paramInt2, 0, 0); }
  
  public GridLayout(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt1 == 0 && paramInt2 == 0)
      throw new IllegalArgumentException("rows and cols cannot both be zero"); 
    this.rows = paramInt1;
    this.cols = paramInt2;
    this.hgap = paramInt3;
    this.vgap = paramInt4;
  }
  
  public int getRows() { return this.rows; }
  
  public void setRows(int paramInt) {
    if (paramInt == 0 && this.cols == 0)
      throw new IllegalArgumentException("rows and cols cannot both be zero"); 
    this.rows = paramInt;
  }
  
  public int getColumns() { return this.cols; }
  
  public void setColumns(int paramInt) {
    if (paramInt == 0 && this.rows == 0)
      throw new IllegalArgumentException("rows and cols cannot both be zero"); 
    this.cols = paramInt;
  }
  
  public int getHgap() { return this.hgap; }
  
  public void setHgap(int paramInt) { this.hgap = paramInt; }
  
  public int getVgap() { return this.vgap; }
  
  public void setVgap(int paramInt) { this.vgap = paramInt; }
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent) {}
  
  public Dimension preferredLayoutSize(Container paramContainer) {
    synchronized (paramContainer.getTreeLock()) {
      Insets insets = paramContainer.getInsets();
      int i = paramContainer.getComponentCount();
      int j = this.rows;
      int k = this.cols;
      if (j > 0) {
        k = (i + j - 1) / j;
      } else {
        j = (i + k - 1) / k;
      } 
      int m = 0;
      int n = 0;
      for (byte b = 0; b < i; b++) {
        Component component = paramContainer.getComponent(b);
        Dimension dimension = component.getPreferredSize();
        if (m < dimension.width)
          m = dimension.width; 
        if (n < dimension.height)
          n = dimension.height; 
      } 
      return new Dimension(insets.left + insets.right + k * m + (k - 1) * this.hgap, insets.top + insets.bottom + j * n + (j - 1) * this.vgap);
    } 
  }
  
  public Dimension minimumLayoutSize(Container paramContainer) {
    synchronized (paramContainer.getTreeLock()) {
      Insets insets = paramContainer.getInsets();
      int i = paramContainer.getComponentCount();
      int j = this.rows;
      int k = this.cols;
      if (j > 0) {
        k = (i + j - 1) / j;
      } else {
        j = (i + k - 1) / k;
      } 
      int m = 0;
      int n = 0;
      for (byte b = 0; b < i; b++) {
        Component component = paramContainer.getComponent(b);
        Dimension dimension = component.getMinimumSize();
        if (m < dimension.width)
          m = dimension.width; 
        if (n < dimension.height)
          n = dimension.height; 
      } 
      return new Dimension(insets.left + insets.right + k * m + (k - 1) * this.hgap, insets.top + insets.bottom + j * n + (j - 1) * this.vgap);
    } 
  }
  
  public void layoutContainer(Container paramContainer) {
    synchronized (paramContainer.getTreeLock()) {
      Insets insets = paramContainer.getInsets();
      int i = paramContainer.getComponentCount();
      int j = this.rows;
      int k = this.cols;
      boolean bool = paramContainer.getComponentOrientation().isLeftToRight();
      if (i == 0)
        return; 
      if (j > 0) {
        k = (i + j - 1) / j;
      } else {
        j = (i + k - 1) / k;
      } 
      int m = (k - 1) * this.hgap;
      int n = paramContainer.width - insets.left + insets.right;
      int i1 = (n - m) / k;
      int i2 = (n - i1 * k + m) / 2;
      int i3 = (j - 1) * this.vgap;
      int i4 = paramContainer.height - insets.top + insets.bottom;
      int i5 = (i4 - i3) / j;
      int i6 = (i4 - i5 * j + i3) / 2;
      if (bool) {
        int i7 = 0;
        int i8;
        for (i8 = insets.left + i2; i7 < k; i8 += i1 + this.hgap) {
          int i9 = 0;
          int i10;
          for (i10 = insets.top + i6; i9 < j; i10 += i5 + this.vgap) {
            int i11 = i9 * k + i7;
            if (i11 < i)
              paramContainer.getComponent(i11).setBounds(i8, i10, i1, i5); 
            i9++;
          } 
          i7++;
        } 
      } else {
        int i7 = 0;
        int i8;
        for (i8 = paramContainer.width - insets.right - i1 - i2; i7 < k; i8 -= i1 + this.hgap) {
          int i9 = 0;
          int i10;
          for (i10 = insets.top + i6; i9 < j; i10 += i5 + this.vgap) {
            int i11 = i9 * k + i7;
            if (i11 < i)
              paramContainer.getComponent(i11).setBounds(i8, i10, i1, i5); 
            i9++;
          } 
          i7++;
        } 
      } 
    } 
  }
  
  public String toString() { return getClass().getName() + "[hgap=" + this.hgap + ",vgap=" + this.vgap + ",rows=" + this.rows + ",cols=" + this.cols + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\GridLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */