package javax.swing.colorchooser;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.Serializable;

class SmartGridLayout implements LayoutManager, Serializable {
  int rows = 2;
  
  int columns = 2;
  
  int xGap = 2;
  
  int yGap = 2;
  
  int componentCount = 0;
  
  Component[][] layoutGrid;
  
  public SmartGridLayout(int paramInt1, int paramInt2) {
    this.rows = paramInt2;
    this.columns = paramInt1;
    this.layoutGrid = new Component[paramInt1][paramInt2];
  }
  
  public void layoutContainer(Container paramContainer) {
    buildLayoutGrid(paramContainer);
    int[] arrayOfInt1 = new int[this.rows];
    int[] arrayOfInt2 = new int[this.columns];
    byte b;
    for (b = 0; b < this.rows; b++)
      arrayOfInt1[b] = computeRowHeight(b); 
    for (b = 0; b < this.columns; b++)
      arrayOfInt2[b] = computeColumnWidth(b); 
    Insets insets = paramContainer.getInsets();
    if (paramContainer.getComponentOrientation().isLeftToRight()) {
      int i = insets.left;
      for (byte b1 = 0; b1 < this.columns; b1++) {
        int j = insets.top;
        for (byte b2 = 0; b2 < this.rows; b2++) {
          Component component = this.layoutGrid[b1][b2];
          component.setBounds(i, j, arrayOfInt2[b1], arrayOfInt1[b2]);
          j += arrayOfInt1[b2] + this.yGap;
        } 
        i += arrayOfInt2[b1] + this.xGap;
      } 
    } else {
      int i = paramContainer.getWidth() - insets.right;
      for (byte b1 = 0; b1 < this.columns; b1++) {
        int j = insets.top;
        i -= arrayOfInt2[b1];
        for (byte b2 = 0; b2 < this.rows; b2++) {
          Component component = this.layoutGrid[b1][b2];
          component.setBounds(i, j, arrayOfInt2[b1], arrayOfInt1[b2]);
          j += arrayOfInt1[b2] + this.yGap;
        } 
        i -= this.xGap;
      } 
    } 
  }
  
  public Dimension minimumLayoutSize(Container paramContainer) {
    buildLayoutGrid(paramContainer);
    Insets insets = paramContainer.getInsets();
    int i = 0;
    int j = 0;
    byte b;
    for (b = 0; b < this.rows; b++)
      i += computeRowHeight(b); 
    for (b = 0; b < this.columns; b++)
      j += computeColumnWidth(b); 
    i += this.yGap * (this.rows - 1) + insets.top + insets.bottom;
    j += this.xGap * (this.columns - 1) + insets.right + insets.left;
    return new Dimension(j, i);
  }
  
  public Dimension preferredLayoutSize(Container paramContainer) { return minimumLayoutSize(paramContainer); }
  
  public void addLayoutComponent(String paramString, Component paramComponent) {}
  
  public void removeLayoutComponent(Component paramComponent) {}
  
  private void buildLayoutGrid(Container paramContainer) {
    Component[] arrayOfComponent = paramContainer.getComponents();
    for (int i = 0; i < arrayOfComponent.length; i++) {
      int j = 0;
      int k = 0;
      if (i) {
        k = i % this.columns;
        j = (i - k) / this.columns;
      } 
      this.layoutGrid[k][j] = arrayOfComponent[i];
    } 
  }
  
  private int computeColumnWidth(int paramInt) {
    int i = 1;
    for (byte b = 0; b < this.rows; b++) {
      int j = (this.layoutGrid[paramInt][b].getPreferredSize()).width;
      if (j > i)
        i = j; 
    } 
    return i;
  }
  
  private int computeRowHeight(int paramInt) {
    int i = 1;
    for (byte b = 0; b < this.columns; b++) {
      int j = (this.layoutGrid[b][paramInt].getPreferredSize()).height;
      if (j > i)
        i = j; 
    } 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\colorchooser\SmartGridLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */