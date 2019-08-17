package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class MotifSplitPaneDivider extends BasicSplitPaneDivider {
  private static final Cursor defaultCursor = Cursor.getPredefinedCursor(0);
  
  public static final int minimumThumbSize = 6;
  
  public static final int defaultDividerSize = 18;
  
  protected static final int pad = 6;
  
  private int hThumbOffset = 30;
  
  private int vThumbOffset = 40;
  
  protected int hThumbWidth = 12;
  
  protected int hThumbHeight = 18;
  
  protected int vThumbWidth = 18;
  
  protected int vThumbHeight = 12;
  
  protected Color highlightColor = UIManager.getColor("SplitPane.highlight");
  
  protected Color shadowColor = UIManager.getColor("SplitPane.shadow");
  
  protected Color focusedColor = UIManager.getColor("SplitPane.activeThumb");
  
  public MotifSplitPaneDivider(BasicSplitPaneUI paramBasicSplitPaneUI) {
    super(paramBasicSplitPaneUI);
    setDividerSize(this.hThumbWidth + 6);
  }
  
  public void setDividerSize(int paramInt) {
    Insets insets = getInsets();
    int i = 0;
    if (getBasicSplitPaneUI().getOrientation() == 1) {
      if (insets != null)
        i = insets.left + insets.right; 
    } else if (insets != null) {
      i = insets.top + insets.bottom;
    } 
    if (paramInt < 12 + i) {
      setDividerSize(12 + i);
    } else {
      this.vThumbHeight = this.hThumbWidth = paramInt - 6 - i;
      super.setDividerSize(paramInt);
    } 
  }
  
  public void paint(Graphics paramGraphics) {
    Color color = getBackground();
    Dimension dimension = getSize();
    paramGraphics.setColor(getBackground());
    paramGraphics.fillRect(0, 0, dimension.width, dimension.height);
    if (getBasicSplitPaneUI().getOrientation() == 1) {
      int i = dimension.width / 2;
      int j = i - this.hThumbWidth / 2;
      int k = this.hThumbOffset;
      paramGraphics.setColor(this.shadowColor);
      paramGraphics.drawLine(i - 1, 0, i - 1, dimension.height);
      paramGraphics.setColor(this.highlightColor);
      paramGraphics.drawLine(i, 0, i, dimension.height);
      paramGraphics.setColor(this.splitPane.hasFocus() ? this.focusedColor : getBackground());
      paramGraphics.fillRect(j + 1, k + 1, this.hThumbWidth - 2, this.hThumbHeight - 1);
      paramGraphics.setColor(this.highlightColor);
      paramGraphics.drawLine(j, k, j + this.hThumbWidth - 1, k);
      paramGraphics.drawLine(j, k + 1, j, k + this.hThumbHeight - 1);
      paramGraphics.setColor(this.shadowColor);
      paramGraphics.drawLine(j + 1, k + this.hThumbHeight - 1, j + this.hThumbWidth - 1, k + this.hThumbHeight - 1);
      paramGraphics.drawLine(j + this.hThumbWidth - 1, k + 1, j + this.hThumbWidth - 1, k + this.hThumbHeight - 2);
    } else {
      int i = dimension.height / 2;
      int j = dimension.width - this.vThumbOffset;
      int k = dimension.height / 2 - this.vThumbHeight / 2;
      paramGraphics.setColor(this.shadowColor);
      paramGraphics.drawLine(0, i - 1, dimension.width, i - 1);
      paramGraphics.setColor(this.highlightColor);
      paramGraphics.drawLine(0, i, dimension.width, i);
      paramGraphics.setColor(this.splitPane.hasFocus() ? this.focusedColor : getBackground());
      paramGraphics.fillRect(j + 1, k + 1, this.vThumbWidth - 1, this.vThumbHeight - 1);
      paramGraphics.setColor(this.highlightColor);
      paramGraphics.drawLine(j, k, j + this.vThumbWidth, k);
      paramGraphics.drawLine(j, k + 1, j, k + this.vThumbHeight);
      paramGraphics.setColor(this.shadowColor);
      paramGraphics.drawLine(j + 1, k + this.vThumbHeight, j + this.vThumbWidth, k + this.vThumbHeight);
      paramGraphics.drawLine(j + this.vThumbWidth, k + 1, j + this.vThumbWidth, k + this.vThumbHeight - 1);
    } 
    super.paint(paramGraphics);
  }
  
  public Dimension getMinimumSize() { return getPreferredSize(); }
  
  public void setBasicSplitPaneUI(BasicSplitPaneUI paramBasicSplitPaneUI) {
    if (this.splitPane != null) {
      this.splitPane.removePropertyChangeListener(this);
      if (this.mouseHandler != null) {
        this.splitPane.removeMouseListener(this.mouseHandler);
        this.splitPane.removeMouseMotionListener(this.mouseHandler);
        removeMouseListener(this.mouseHandler);
        removeMouseMotionListener(this.mouseHandler);
        this.mouseHandler = null;
      } 
    } 
    this.splitPaneUI = paramBasicSplitPaneUI;
    if (paramBasicSplitPaneUI != null) {
      this.splitPane = paramBasicSplitPaneUI.getSplitPane();
      if (this.splitPane != null) {
        if (this.mouseHandler == null)
          this.mouseHandler = new MotifMouseHandler(null); 
        this.splitPane.addMouseListener(this.mouseHandler);
        this.splitPane.addMouseMotionListener(this.mouseHandler);
        addMouseListener(this.mouseHandler);
        addMouseMotionListener(this.mouseHandler);
        this.splitPane.addPropertyChangeListener(this);
        if (this.splitPane.isOneTouchExpandable())
          oneTouchExpandableChanged(); 
      } 
    } else {
      this.splitPane = null;
    } 
  }
  
  private boolean isInThumb(int paramInt1, int paramInt2) {
    int m;
    int k;
    int j;
    int i;
    Dimension dimension = getSize();
    if (getBasicSplitPaneUI().getOrientation() == 1) {
      int n = dimension.width / 2;
      i = n - this.hThumbWidth / 2;
      j = this.hThumbOffset;
      k = this.hThumbWidth;
      m = this.hThumbHeight;
    } else {
      int n = dimension.height / 2;
      i = dimension.width - this.vThumbOffset;
      j = dimension.height / 2 - this.vThumbHeight / 2;
      k = this.vThumbWidth;
      m = this.vThumbHeight;
    } 
    return (paramInt1 >= i && paramInt1 < i + k && paramInt2 >= j && paramInt2 < j + m);
  }
  
  private BasicSplitPaneDivider.DragController getDragger() { return this.dragger; }
  
  private JSplitPane getSplitPane() { return this.splitPane; }
  
  private class MotifMouseHandler extends BasicSplitPaneDivider.MouseHandler {
    private MotifMouseHandler() { super(MotifSplitPaneDivider.this); }
    
    public void mousePressed(MouseEvent param1MouseEvent) {
      if (param1MouseEvent.getSource() == MotifSplitPaneDivider.this && MotifSplitPaneDivider.this.getDragger() == null && MotifSplitPaneDivider.this.getSplitPane().isEnabled() && MotifSplitPaneDivider.this.isInThumb(param1MouseEvent.getX(), param1MouseEvent.getY()))
        super.mousePressed(param1MouseEvent); 
    }
    
    public void mouseMoved(MouseEvent param1MouseEvent) {
      if (MotifSplitPaneDivider.this.getDragger() != null)
        return; 
      if (!MotifSplitPaneDivider.this.isInThumb(param1MouseEvent.getX(), param1MouseEvent.getY())) {
        if (MotifSplitPaneDivider.this.getCursor() != defaultCursor)
          MotifSplitPaneDivider.this.setCursor(defaultCursor); 
        return;
      } 
      super.mouseMoved(param1MouseEvent);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifSplitPaneDivider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */