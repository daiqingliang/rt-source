package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.io.Serializable;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;

public class ScrollPaneLayout implements LayoutManager, ScrollPaneConstants, Serializable {
  protected JViewport viewport;
  
  protected JScrollBar vsb;
  
  protected JScrollBar hsb;
  
  protected JViewport rowHead;
  
  protected JViewport colHead;
  
  protected Component lowerLeft;
  
  protected Component lowerRight;
  
  protected Component upperLeft;
  
  protected Component upperRight;
  
  protected int vsbPolicy = 20;
  
  protected int hsbPolicy = 30;
  
  public void syncWithScrollPane(JScrollPane paramJScrollPane) {
    this.viewport = paramJScrollPane.getViewport();
    this.vsb = paramJScrollPane.getVerticalScrollBar();
    this.hsb = paramJScrollPane.getHorizontalScrollBar();
    this.rowHead = paramJScrollPane.getRowHeader();
    this.colHead = paramJScrollPane.getColumnHeader();
    this.lowerLeft = paramJScrollPane.getCorner("LOWER_LEFT_CORNER");
    this.lowerRight = paramJScrollPane.getCorner("LOWER_RIGHT_CORNER");
    this.upperLeft = paramJScrollPane.getCorner("UPPER_LEFT_CORNER");
    this.upperRight = paramJScrollPane.getCorner("UPPER_RIGHT_CORNER");
    this.vsbPolicy = paramJScrollPane.getVerticalScrollBarPolicy();
    this.hsbPolicy = paramJScrollPane.getHorizontalScrollBarPolicy();
  }
  
  protected Component addSingletonComponent(Component paramComponent1, Component paramComponent2) {
    if (paramComponent1 != null && paramComponent1 != paramComponent2)
      paramComponent1.getParent().remove(paramComponent1); 
    return paramComponent2;
  }
  
  public void addLayoutComponent(String paramString, Component paramComponent) {
    if (paramString.equals("VIEWPORT")) {
      this.viewport = (JViewport)addSingletonComponent(this.viewport, paramComponent);
    } else if (paramString.equals("VERTICAL_SCROLLBAR")) {
      this.vsb = (JScrollBar)addSingletonComponent(this.vsb, paramComponent);
    } else if (paramString.equals("HORIZONTAL_SCROLLBAR")) {
      this.hsb = (JScrollBar)addSingletonComponent(this.hsb, paramComponent);
    } else if (paramString.equals("ROW_HEADER")) {
      this.rowHead = (JViewport)addSingletonComponent(this.rowHead, paramComponent);
    } else if (paramString.equals("COLUMN_HEADER")) {
      this.colHead = (JViewport)addSingletonComponent(this.colHead, paramComponent);
    } else if (paramString.equals("LOWER_LEFT_CORNER")) {
      this.lowerLeft = addSingletonComponent(this.lowerLeft, paramComponent);
    } else if (paramString.equals("LOWER_RIGHT_CORNER")) {
      this.lowerRight = addSingletonComponent(this.lowerRight, paramComponent);
    } else if (paramString.equals("UPPER_LEFT_CORNER")) {
      this.upperLeft = addSingletonComponent(this.upperLeft, paramComponent);
    } else if (paramString.equals("UPPER_RIGHT_CORNER")) {
      this.upperRight = addSingletonComponent(this.upperRight, paramComponent);
    } else {
      throw new IllegalArgumentException("invalid layout key " + paramString);
    } 
  }
  
  public void removeLayoutComponent(Component paramComponent) {
    if (paramComponent == this.viewport) {
      this.viewport = null;
    } else if (paramComponent == this.vsb) {
      this.vsb = null;
    } else if (paramComponent == this.hsb) {
      this.hsb = null;
    } else if (paramComponent == this.rowHead) {
      this.rowHead = null;
    } else if (paramComponent == this.colHead) {
      this.colHead = null;
    } else if (paramComponent == this.lowerLeft) {
      this.lowerLeft = null;
    } else if (paramComponent == this.lowerRight) {
      this.lowerRight = null;
    } else if (paramComponent == this.upperLeft) {
      this.upperLeft = null;
    } else if (paramComponent == this.upperRight) {
      this.upperRight = null;
    } 
  }
  
  public int getVerticalScrollBarPolicy() { return this.vsbPolicy; }
  
  public void setVerticalScrollBarPolicy(int paramInt) {
    switch (paramInt) {
      case 20:
      case 21:
      case 22:
        this.vsbPolicy = paramInt;
        return;
    } 
    throw new IllegalArgumentException("invalid verticalScrollBarPolicy");
  }
  
  public int getHorizontalScrollBarPolicy() { return this.hsbPolicy; }
  
  public void setHorizontalScrollBarPolicy(int paramInt) {
    switch (paramInt) {
      case 30:
      case 31:
      case 32:
        this.hsbPolicy = paramInt;
        return;
    } 
    throw new IllegalArgumentException("invalid horizontalScrollBarPolicy");
  }
  
  public JViewport getViewport() { return this.viewport; }
  
  public JScrollBar getHorizontalScrollBar() { return this.hsb; }
  
  public JScrollBar getVerticalScrollBar() { return this.vsb; }
  
  public JViewport getRowHeader() { return this.rowHead; }
  
  public JViewport getColumnHeader() { return this.colHead; }
  
  public Component getCorner(String paramString) { return paramString.equals("LOWER_LEFT_CORNER") ? this.lowerLeft : (paramString.equals("LOWER_RIGHT_CORNER") ? this.lowerRight : (paramString.equals("UPPER_LEFT_CORNER") ? this.upperLeft : (paramString.equals("UPPER_RIGHT_CORNER") ? this.upperRight : null))); }
  
  public Dimension preferredLayoutSize(Container paramContainer) {
    JScrollPane jScrollPane = (JScrollPane)paramContainer;
    this.vsbPolicy = jScrollPane.getVerticalScrollBarPolicy();
    this.hsbPolicy = jScrollPane.getHorizontalScrollBarPolicy();
    Insets insets = paramContainer.getInsets();
    int i = insets.left + insets.right;
    int j = insets.top + insets.bottom;
    Dimension dimension1 = null;
    Dimension dimension2 = null;
    Component component = null;
    if (this.viewport != null) {
      dimension1 = this.viewport.getPreferredSize();
      component = this.viewport.getView();
      if (component != null) {
        dimension2 = component.getPreferredSize();
      } else {
        dimension2 = new Dimension(0, 0);
      } 
    } 
    if (dimension1 != null) {
      i += dimension1.width;
      j += dimension1.height;
    } 
    Border border = jScrollPane.getViewportBorder();
    if (border != null) {
      Insets insets1 = border.getBorderInsets(paramContainer);
      i += insets1.left + insets1.right;
      j += insets1.top + insets1.bottom;
    } 
    if (this.rowHead != null && this.rowHead.isVisible())
      i += (this.rowHead.getPreferredSize()).width; 
    if (this.colHead != null && this.colHead.isVisible())
      j += (this.colHead.getPreferredSize()).height; 
    if (this.vsb != null && this.vsbPolicy != 21)
      if (this.vsbPolicy == 22) {
        i += (this.vsb.getPreferredSize()).width;
      } else if (dimension2 != null && dimension1 != null) {
        boolean bool = true;
        if (component instanceof Scrollable)
          bool = !((Scrollable)component).getScrollableTracksViewportHeight() ? 1 : 0; 
        if (bool && dimension2.height > dimension1.height)
          i += (this.vsb.getPreferredSize()).width; 
      }  
    if (this.hsb != null && this.hsbPolicy != 31)
      if (this.hsbPolicy == 32) {
        j += (this.hsb.getPreferredSize()).height;
      } else if (dimension2 != null && dimension1 != null) {
        boolean bool = true;
        if (component instanceof Scrollable)
          bool = !((Scrollable)component).getScrollableTracksViewportWidth() ? 1 : 0; 
        if (bool && dimension2.width > dimension1.width)
          j += (this.hsb.getPreferredSize()).height; 
      }  
    return new Dimension(i, j);
  }
  
  public Dimension minimumLayoutSize(Container paramContainer) {
    JScrollPane jScrollPane = (JScrollPane)paramContainer;
    this.vsbPolicy = jScrollPane.getVerticalScrollBarPolicy();
    this.hsbPolicy = jScrollPane.getHorizontalScrollBarPolicy();
    Insets insets = paramContainer.getInsets();
    int i = insets.left + insets.right;
    int j = insets.top + insets.bottom;
    if (this.viewport != null) {
      Dimension dimension = this.viewport.getMinimumSize();
      i += dimension.width;
      j += dimension.height;
    } 
    Border border = jScrollPane.getViewportBorder();
    if (border != null) {
      Insets insets1 = border.getBorderInsets(paramContainer);
      i += insets1.left + insets1.right;
      j += insets1.top + insets1.bottom;
    } 
    if (this.rowHead != null && this.rowHead.isVisible()) {
      Dimension dimension = this.rowHead.getMinimumSize();
      i += dimension.width;
      j = Math.max(j, dimension.height);
    } 
    if (this.colHead != null && this.colHead.isVisible()) {
      Dimension dimension = this.colHead.getMinimumSize();
      i = Math.max(i, dimension.width);
      j += dimension.height;
    } 
    if (this.vsb != null && this.vsbPolicy != 21) {
      Dimension dimension = this.vsb.getMinimumSize();
      i += dimension.width;
      j = Math.max(j, dimension.height);
    } 
    if (this.hsb != null && this.hsbPolicy != 31) {
      Dimension dimension = this.hsb.getMinimumSize();
      i = Math.max(i, dimension.width);
      j += dimension.height;
    } 
    return new Dimension(i, j);
  }
  
  public void layoutContainer(Container paramContainer) {
    int i;
    boolean bool5;
    Object object;
    Insets insets2;
    JScrollPane jScrollPane = (JScrollPane)paramContainer;
    this.vsbPolicy = jScrollPane.getVerticalScrollBarPolicy();
    this.hsbPolicy = jScrollPane.getHorizontalScrollBarPolicy();
    Rectangle rectangle1 = jScrollPane.getBounds();
    rectangle1.x = rectangle1.y = 0;
    Insets insets1 = paramContainer.getInsets();
    rectangle1.x = insets1.left;
    rectangle1.y = insets1.top;
    rectangle1.width -= insets1.left + insets1.right;
    rectangle1.height -= insets1.top + insets1.bottom;
    boolean bool1 = SwingUtilities.isLeftToRight(jScrollPane);
    Rectangle rectangle2 = new Rectangle(0, rectangle1.y, 0, 0);
    if (this.colHead != null && this.colHead.isVisible()) {
      int j = Math.min(rectangle1.height, (this.colHead.getPreferredSize()).height);
      rectangle2.height = j;
      rectangle1.y += j;
      rectangle1.height -= j;
    } 
    Rectangle rectangle3 = new Rectangle(0, 0, 0, 0);
    if (this.rowHead != null && this.rowHead.isVisible()) {
      int j = Math.min(rectangle1.width, (this.rowHead.getPreferredSize()).width);
      rectangle3.width = j;
      rectangle1.width -= j;
      if (bool1) {
        rectangle3.x = rectangle1.x;
        rectangle1.x += j;
      } else {
        rectangle1.x += rectangle1.width;
      } 
    } 
    Border border = jScrollPane.getViewportBorder();
    if (border != null) {
      insets2 = border.getBorderInsets(paramContainer);
      rectangle1.x += insets2.left;
      rectangle1.y += insets2.top;
      rectangle1.width -= insets2.left + insets2.right;
      rectangle1.height -= insets2.top + insets2.bottom;
    } else {
      insets2 = new Insets(0, 0, 0, 0);
    } 
    Component component = (this.viewport != null) ? this.viewport.getView() : null;
    Dimension dimension1 = (component != null) ? component.getPreferredSize() : new Dimension(0, 0);
    Dimension dimension2 = (this.viewport != null) ? this.viewport.toViewCoordinates(rectangle1.getSize()) : new Dimension(0, 0);
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = (rectangle1.width < 0 || rectangle1.height < 0) ? 1 : 0;
    if (!bool4 && component instanceof Scrollable) {
      object = (Scrollable)component;
      bool2 = object.getScrollableTracksViewportWidth();
      bool3 = object.getScrollableTracksViewportHeight();
    } else {
      object = null;
    } 
    Rectangle rectangle4 = new Rectangle(0, rectangle1.y - insets2.top, 0, 0);
    if (bool4) {
      bool5 = false;
    } else if (this.vsbPolicy == 22) {
      bool5 = true;
    } else if (this.vsbPolicy == 21) {
      bool5 = false;
    } else {
      bool5 = (!bool3 && dimension1.height > dimension2.height) ? 1 : 0;
    } 
    if (this.vsb != null && bool5) {
      adjustForVSB(true, rectangle1, rectangle4, insets2, bool1);
      dimension2 = this.viewport.toViewCoordinates(rectangle1.getSize());
    } 
    Rectangle rectangle5 = new Rectangle(rectangle1.x - insets2.left, 0, 0, 0);
    if (bool4) {
      i = 0;
    } else if (this.hsbPolicy == 32) {
      i = 1;
    } else if (this.hsbPolicy == 31) {
      i = 0;
    } else {
      i = (!bool2 && dimension1.width > dimension2.width) ? 1 : 0;
    } 
    if (this.hsb != null && i) {
      adjustForHSB(true, rectangle1, rectangle5, insets2);
      if (this.vsb != null && !bool5 && this.vsbPolicy != 21) {
        dimension2 = this.viewport.toViewCoordinates(rectangle1.getSize());
        bool5 = (dimension1.height > dimension2.height) ? 1 : 0;
        if (bool5)
          adjustForVSB(true, rectangle1, rectangle4, insets2, bool1); 
      } 
    } 
    if (this.viewport != null) {
      this.viewport.setBounds(rectangle1);
      if (object != null) {
        dimension2 = this.viewport.toViewCoordinates(rectangle1.getSize());
        boolean bool6 = i;
        boolean bool7 = bool5;
        bool2 = object.getScrollableTracksViewportWidth();
        bool3 = object.getScrollableTracksViewportHeight();
        if (this.vsb != null && this.vsbPolicy == 20) {
          byte b = (!bool3 && dimension1.height > dimension2.height) ? 1 : 0;
          if (b != bool5) {
            bool5 = b;
            adjustForVSB(bool5, rectangle1, rectangle4, insets2, bool1);
            dimension2 = this.viewport.toViewCoordinates(rectangle1.getSize());
          } 
        } 
        if (this.hsb != null && this.hsbPolicy == 30) {
          byte b = (!bool2 && dimension1.width > dimension2.width) ? 1 : 0;
          if (b != i) {
            i = b;
            adjustForHSB(i, rectangle1, rectangle5, insets2);
            if (this.vsb != null && bool5 == 0 && this.vsbPolicy != 21) {
              dimension2 = this.viewport.toViewCoordinates(rectangle1.getSize());
              bool5 = (dimension1.height > dimension2.height) ? 1 : 0;
              if (bool5)
                adjustForVSB(true, rectangle1, rectangle4, insets2, bool1); 
            } 
          } 
        } 
        if (bool6 != i || bool7 != bool5)
          this.viewport.setBounds(rectangle1); 
      } 
    } 
    rectangle4.height = rectangle1.height + insets2.top + insets2.bottom;
    rectangle5.width = rectangle1.width + insets2.left + insets2.right;
    rectangle3.height = rectangle1.height + insets2.top + insets2.bottom;
    rectangle1.y -= insets2.top;
    rectangle2.width = rectangle1.width + insets2.left + insets2.right;
    rectangle1.x -= insets2.left;
    if (this.rowHead != null)
      this.rowHead.setBounds(rectangle3); 
    if (this.colHead != null)
      this.colHead.setBounds(rectangle2); 
    if (this.vsb != null)
      if (bool5) {
        if (this.colHead != null && UIManager.getBoolean("ScrollPane.fillUpperCorner") && ((bool1 && this.upperRight == null) || (!bool1 && this.upperLeft == null))) {
          rectangle4.y = rectangle2.y;
          rectangle4.height += rectangle2.height;
        } 
        this.vsb.setVisible(true);
        this.vsb.setBounds(rectangle4);
      } else {
        this.vsb.setVisible(false);
      }  
    if (this.hsb != null)
      if (i != 0) {
        if (this.rowHead != null && UIManager.getBoolean("ScrollPane.fillLowerCorner") && ((bool1 && this.lowerLeft == null) || (!bool1 && this.lowerRight == null))) {
          if (bool1)
            rectangle5.x = rectangle3.x; 
          rectangle5.width += rectangle3.width;
        } 
        this.hsb.setVisible(true);
        this.hsb.setBounds(rectangle5);
      } else {
        this.hsb.setVisible(false);
      }  
    if (this.lowerLeft != null)
      this.lowerLeft.setBounds(bool1 ? rectangle3.x : rectangle4.x, rectangle5.y, bool1 ? rectangle3.width : rectangle4.width, rectangle5.height); 
    if (this.lowerRight != null)
      this.lowerRight.setBounds(bool1 ? rectangle4.x : rectangle3.x, rectangle5.y, bool1 ? rectangle4.width : rectangle3.width, rectangle5.height); 
    if (this.upperLeft != null)
      this.upperLeft.setBounds(bool1 ? rectangle3.x : rectangle4.x, rectangle2.y, bool1 ? rectangle3.width : rectangle4.width, rectangle2.height); 
    if (this.upperRight != null)
      this.upperRight.setBounds(bool1 ? rectangle4.x : rectangle3.x, rectangle2.y, bool1 ? rectangle4.width : rectangle3.width, rectangle2.height); 
  }
  
  private void adjustForVSB(boolean paramBoolean1, Rectangle paramRectangle1, Rectangle paramRectangle2, Insets paramInsets, boolean paramBoolean2) {
    int i = paramRectangle2.width;
    if (paramBoolean1) {
      int j = Math.max(0, Math.min((this.vsb.getPreferredSize()).width, paramRectangle1.width));
      paramRectangle1.width -= j;
      paramRectangle2.width = j;
      if (paramBoolean2) {
        paramRectangle2.x = paramRectangle1.x + paramRectangle1.width + paramInsets.right;
      } else {
        paramRectangle1.x -= paramInsets.left;
        paramRectangle1.x += j;
      } 
    } else {
      paramRectangle1.width += i;
    } 
  }
  
  private void adjustForHSB(boolean paramBoolean, Rectangle paramRectangle1, Rectangle paramRectangle2, Insets paramInsets) {
    int i = paramRectangle2.height;
    if (paramBoolean) {
      int j = Math.max(0, Math.min(paramRectangle1.height, (this.hsb.getPreferredSize()).height));
      paramRectangle1.height -= j;
      paramRectangle2.y = paramRectangle1.y + paramRectangle1.height + paramInsets.bottom;
      paramRectangle2.height = j;
    } else {
      paramRectangle1.height += i;
    } 
  }
  
  @Deprecated
  public Rectangle getViewportBorderBounds(JScrollPane paramJScrollPane) { return paramJScrollPane.getViewportBorderBounds(); }
  
  public static class UIResource extends ScrollPaneLayout implements UIResource {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\ScrollPaneLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */