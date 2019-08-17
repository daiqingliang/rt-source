package javax.swing;

import com.sun.awt.AWTUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.beans.PropertyVetoException;
import java.io.Serializable;
import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;

public class DefaultDesktopManager implements DesktopManager, Serializable {
  static final String HAS_BEEN_ICONIFIED_PROPERTY = "wasIconOnce";
  
  static final int DEFAULT_DRAG_MODE = 0;
  
  static final int OUTLINE_DRAG_MODE = 1;
  
  static final int FASTER_DRAG_MODE = 2;
  
  int dragMode = 0;
  
  private Rectangle currentBounds = null;
  
  private Graphics desktopGraphics = null;
  
  private Rectangle desktopBounds = null;
  
  private Rectangle[] floatingItems = new Rectangle[0];
  
  private boolean didDrag;
  
  private Point currentLoc = null;
  
  public void openFrame(JInternalFrame paramJInternalFrame) {
    if (paramJInternalFrame.getDesktopIcon().getParent() != null) {
      paramJInternalFrame.getDesktopIcon().getParent().add(paramJInternalFrame);
      removeIconFor(paramJInternalFrame);
    } 
  }
  
  public void closeFrame(JInternalFrame paramJInternalFrame) {
    JDesktopPane jDesktopPane = paramJInternalFrame.getDesktopPane();
    if (jDesktopPane == null)
      return; 
    boolean bool = paramJInternalFrame.isSelected();
    Container container = paramJInternalFrame.getParent();
    JInternalFrame jInternalFrame = null;
    if (bool) {
      jInternalFrame = jDesktopPane.getNextFrame(paramJInternalFrame);
      try {
        paramJInternalFrame.setSelected(false);
      } catch (PropertyVetoException propertyVetoException) {}
    } 
    if (container != null) {
      container.remove(paramJInternalFrame);
      container.repaint(paramJInternalFrame.getX(), paramJInternalFrame.getY(), paramJInternalFrame.getWidth(), paramJInternalFrame.getHeight());
    } 
    removeIconFor(paramJInternalFrame);
    if (paramJInternalFrame.getNormalBounds() != null)
      paramJInternalFrame.setNormalBounds(null); 
    if (wasIcon(paramJInternalFrame))
      setWasIcon(paramJInternalFrame, null); 
    if (jInternalFrame != null) {
      try {
        jInternalFrame.setSelected(true);
      } catch (PropertyVetoException propertyVetoException) {}
    } else if (bool && jDesktopPane.getComponentCount() == 0) {
      jDesktopPane.requestFocus();
    } 
  }
  
  public void maximizeFrame(JInternalFrame paramJInternalFrame) {
    if (paramJInternalFrame.isIcon()) {
      try {
        paramJInternalFrame.setIcon(false);
      } catch (PropertyVetoException propertyVetoException) {}
    } else {
      paramJInternalFrame.setNormalBounds(paramJInternalFrame.getBounds());
      Rectangle rectangle = paramJInternalFrame.getParent().getBounds();
      setBoundsForFrame(paramJInternalFrame, 0, 0, rectangle.width, rectangle.height);
    } 
    try {
      paramJInternalFrame.setSelected(true);
    } catch (PropertyVetoException propertyVetoException) {}
  }
  
  public void minimizeFrame(JInternalFrame paramJInternalFrame) {
    if (paramJInternalFrame.isIcon()) {
      iconifyFrame(paramJInternalFrame);
      return;
    } 
    if (paramJInternalFrame.getNormalBounds() != null) {
      Rectangle rectangle = paramJInternalFrame.getNormalBounds();
      paramJInternalFrame.setNormalBounds(null);
      try {
        paramJInternalFrame.setSelected(true);
      } catch (PropertyVetoException propertyVetoException) {}
      setBoundsForFrame(paramJInternalFrame, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    } 
  }
  
  public void iconifyFrame(JInternalFrame paramJInternalFrame) {
    Container container = paramJInternalFrame.getParent();
    JDesktopPane jDesktopPane = paramJInternalFrame.getDesktopPane();
    boolean bool = paramJInternalFrame.isSelected();
    JInternalFrame.JDesktopIcon jDesktopIcon = paramJInternalFrame.getDesktopIcon();
    if (!wasIcon(paramJInternalFrame)) {
      Rectangle rectangle = getBoundsForIconOf(paramJInternalFrame);
      jDesktopIcon.setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      jDesktopIcon.revalidate();
      setWasIcon(paramJInternalFrame, Boolean.TRUE);
    } 
    if (container == null || jDesktopPane == null)
      return; 
    if (container instanceof JLayeredPane) {
      JLayeredPane jLayeredPane = (JLayeredPane)container;
      int i = jLayeredPane.getLayer(paramJInternalFrame);
      jLayeredPane.putLayer(jDesktopIcon, i);
    } 
    if (!paramJInternalFrame.isMaximum())
      paramJInternalFrame.setNormalBounds(paramJInternalFrame.getBounds()); 
    jDesktopPane.setComponentOrderCheckingEnabled(false);
    container.remove(paramJInternalFrame);
    container.add(jDesktopIcon);
    jDesktopPane.setComponentOrderCheckingEnabled(true);
    container.repaint(paramJInternalFrame.getX(), paramJInternalFrame.getY(), paramJInternalFrame.getWidth(), paramJInternalFrame.getHeight());
    if (bool && jDesktopPane.selectFrame(true) == null)
      paramJInternalFrame.restoreSubcomponentFocus(); 
  }
  
  public void deiconifyFrame(JInternalFrame paramJInternalFrame) {
    JInternalFrame.JDesktopIcon jDesktopIcon = paramJInternalFrame.getDesktopIcon();
    Container container = jDesktopIcon.getParent();
    JDesktopPane jDesktopPane = paramJInternalFrame.getDesktopPane();
    if (container != null && jDesktopPane != null) {
      container.add(paramJInternalFrame);
      if (paramJInternalFrame.isMaximum()) {
        Rectangle rectangle = container.getBounds();
        if (paramJInternalFrame.getWidth() != rectangle.width || paramJInternalFrame.getHeight() != rectangle.height)
          setBoundsForFrame(paramJInternalFrame, 0, 0, rectangle.width, rectangle.height); 
      } 
      removeIconFor(paramJInternalFrame);
      if (paramJInternalFrame.isSelected()) {
        paramJInternalFrame.moveToFront();
        paramJInternalFrame.restoreSubcomponentFocus();
      } else {
        try {
          paramJInternalFrame.setSelected(true);
        } catch (PropertyVetoException propertyVetoException) {}
      } 
    } 
  }
  
  public void activateFrame(JInternalFrame paramJInternalFrame) {
    Container container = paramJInternalFrame.getParent();
    JDesktopPane jDesktopPane = paramJInternalFrame.getDesktopPane();
    JInternalFrame jInternalFrame = (jDesktopPane == null) ? null : jDesktopPane.getSelectedFrame();
    if (container == null) {
      container = paramJInternalFrame.getDesktopIcon().getParent();
      if (container == null)
        return; 
    } 
    if (jInternalFrame == null) {
      if (jDesktopPane != null)
        jDesktopPane.setSelectedFrame(paramJInternalFrame); 
    } else if (jInternalFrame != paramJInternalFrame) {
      if (jInternalFrame.isSelected())
        try {
          jInternalFrame.setSelected(false);
        } catch (PropertyVetoException propertyVetoException) {} 
      if (jDesktopPane != null)
        jDesktopPane.setSelectedFrame(paramJInternalFrame); 
    } 
    paramJInternalFrame.moveToFront();
  }
  
  public void deactivateFrame(JInternalFrame paramJInternalFrame) {
    JDesktopPane jDesktopPane = paramJInternalFrame.getDesktopPane();
    JInternalFrame jInternalFrame = (jDesktopPane == null) ? null : jDesktopPane.getSelectedFrame();
    if (jInternalFrame == paramJInternalFrame)
      jDesktopPane.setSelectedFrame(null); 
  }
  
  public void beginDraggingFrame(JComponent paramJComponent) {
    setupDragMode(paramJComponent);
    if (this.dragMode == 2) {
      Container container = paramJComponent.getParent();
      this.floatingItems = findFloatingItems(paramJComponent);
      this.currentBounds = paramJComponent.getBounds();
      if (container instanceof JComponent) {
        this.desktopBounds = ((JComponent)container).getVisibleRect();
      } else {
        this.desktopBounds = container.getBounds();
        this.desktopBounds.x = this.desktopBounds.y = 0;
      } 
      this.desktopGraphics = JComponent.safelyGetGraphics(container);
      ((JInternalFrame)paramJComponent).isDragging = true;
      this.didDrag = false;
    } 
  }
  
  private void setupDragMode(JComponent paramJComponent) {
    JDesktopPane jDesktopPane = getDesktopPane(paramJComponent);
    Container container = paramJComponent.getParent();
    this.dragMode = 0;
    if (jDesktopPane != null) {
      String str = (String)jDesktopPane.getClientProperty("JDesktopPane.dragMode");
      Window window = SwingUtilities.getWindowAncestor(paramJComponent);
      if (window != null && !AWTUtilities.isWindowOpaque(window)) {
        this.dragMode = 0;
      } else if (str != null && str.equals("outline")) {
        this.dragMode = 1;
      } else if (str != null && str.equals("faster") && paramJComponent instanceof JInternalFrame && ((JInternalFrame)paramJComponent).isOpaque() && (container == null || container.isOpaque())) {
        this.dragMode = 2;
      } else if (jDesktopPane.getDragMode() == 1) {
        this.dragMode = 1;
      } else if (jDesktopPane.getDragMode() == 0 && paramJComponent instanceof JInternalFrame && ((JInternalFrame)paramJComponent).isOpaque()) {
        this.dragMode = 2;
      } else {
        this.dragMode = 0;
      } 
    } 
  }
  
  public void dragFrame(JComponent paramJComponent, int paramInt1, int paramInt2) {
    if (this.dragMode == 1) {
      JDesktopPane jDesktopPane = getDesktopPane(paramJComponent);
      if (jDesktopPane != null) {
        Graphics graphics = JComponent.safelyGetGraphics(jDesktopPane);
        graphics.setXORMode(Color.white);
        if (this.currentLoc != null)
          graphics.drawRect(this.currentLoc.x, this.currentLoc.y, paramJComponent.getWidth() - 1, paramJComponent.getHeight() - 1); 
        graphics.drawRect(paramInt1, paramInt2, paramJComponent.getWidth() - 1, paramJComponent.getHeight() - 1);
        SurfaceData surfaceData = ((SunGraphics2D)graphics).getSurfaceData();
        if (!surfaceData.isSurfaceLost())
          this.currentLoc = new Point(paramInt1, paramInt2); 
        graphics.dispose();
      } 
    } else if (this.dragMode == 2) {
      dragFrameFaster(paramJComponent, paramInt1, paramInt2);
    } else {
      setBoundsForFrame(paramJComponent, paramInt1, paramInt2, paramJComponent.getWidth(), paramJComponent.getHeight());
    } 
  }
  
  public void endDraggingFrame(JComponent paramJComponent) {
    if (this.dragMode == 1 && this.currentLoc != null) {
      setBoundsForFrame(paramJComponent, this.currentLoc.x, this.currentLoc.y, paramJComponent.getWidth(), paramJComponent.getHeight());
      this.currentLoc = null;
    } else if (this.dragMode == 2) {
      this.currentBounds = null;
      if (this.desktopGraphics != null) {
        this.desktopGraphics.dispose();
        this.desktopGraphics = null;
      } 
      this.desktopBounds = null;
      ((JInternalFrame)paramJComponent).isDragging = false;
    } 
  }
  
  public void beginResizingFrame(JComponent paramJComponent, int paramInt) { setupDragMode(paramJComponent); }
  
  public void resizeFrame(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (this.dragMode == 0 || this.dragMode == 2) {
      setBoundsForFrame(paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      JDesktopPane jDesktopPane = getDesktopPane(paramJComponent);
      if (jDesktopPane != null) {
        Graphics graphics = JComponent.safelyGetGraphics(jDesktopPane);
        graphics.setXORMode(Color.white);
        if (this.currentBounds != null)
          graphics.drawRect(this.currentBounds.x, this.currentBounds.y, this.currentBounds.width - 1, this.currentBounds.height - 1); 
        graphics.drawRect(paramInt1, paramInt2, paramInt3 - 1, paramInt4 - 1);
        SurfaceData surfaceData = ((SunGraphics2D)graphics).getSurfaceData();
        if (!surfaceData.isSurfaceLost())
          this.currentBounds = new Rectangle(paramInt1, paramInt2, paramInt3, paramInt4); 
        graphics.setPaintMode();
        graphics.dispose();
      } 
    } 
  }
  
  public void endResizingFrame(JComponent paramJComponent) {
    if (this.dragMode == 1 && this.currentBounds != null) {
      setBoundsForFrame(paramJComponent, this.currentBounds.x, this.currentBounds.y, this.currentBounds.width, this.currentBounds.height);
      this.currentBounds = null;
    } 
  }
  
  public void setBoundsForFrame(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    paramJComponent.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    paramJComponent.revalidate();
  }
  
  protected void removeIconFor(JInternalFrame paramJInternalFrame) {
    JInternalFrame.JDesktopIcon jDesktopIcon = paramJInternalFrame.getDesktopIcon();
    Container container = jDesktopIcon.getParent();
    if (container != null) {
      container.remove(jDesktopIcon);
      container.repaint(jDesktopIcon.getX(), jDesktopIcon.getY(), jDesktopIcon.getWidth(), jDesktopIcon.getHeight());
    } 
  }
  
  protected Rectangle getBoundsForIconOf(JInternalFrame paramJInternalFrame) {
    JInternalFrame.JDesktopIcon jDesktopIcon1 = paramJInternalFrame.getDesktopIcon();
    Dimension dimension = jDesktopIcon1.getPreferredSize();
    Container container = paramJInternalFrame.getParent();
    if (container == null)
      container = paramJInternalFrame.getDesktopIcon().getParent(); 
    if (container == null)
      return new Rectangle(0, 0, dimension.width, dimension.height); 
    Rectangle rectangle1 = container.getBounds();
    Component[] arrayOfComponent = container.getComponents();
    Rectangle rectangle2 = null;
    JInternalFrame.JDesktopIcon jDesktopIcon2 = null;
    int i = 0;
    int j = rectangle1.height - dimension.height;
    int k = dimension.width;
    int m = dimension.height;
    boolean bool = false;
    while (!bool) {
      rectangle2 = new Rectangle(i, j, k, m);
      bool = true;
      for (byte b = 0; b < arrayOfComponent.length; b++) {
        if (arrayOfComponent[b] instanceof JInternalFrame) {
          jDesktopIcon2 = ((JInternalFrame)arrayOfComponent[b]).getDesktopIcon();
        } else if (arrayOfComponent[b] instanceof JInternalFrame.JDesktopIcon) {
          jDesktopIcon2 = (JInternalFrame.JDesktopIcon)arrayOfComponent[b];
        } else {
          continue;
        } 
        if (!jDesktopIcon2.equals(jDesktopIcon1) && rectangle2.intersects(jDesktopIcon2.getBounds())) {
          bool = false;
          break;
        } 
        continue;
      } 
      if (jDesktopIcon2 == null)
        return rectangle2; 
      i += (jDesktopIcon2.getBounds()).width;
      if (i + k > rectangle1.width) {
        i = 0;
        j -= m;
      } 
    } 
    return rectangle2;
  }
  
  protected void setPreviousBounds(JInternalFrame paramJInternalFrame, Rectangle paramRectangle) { paramJInternalFrame.setNormalBounds(paramRectangle); }
  
  protected Rectangle getPreviousBounds(JInternalFrame paramJInternalFrame) { return paramJInternalFrame.getNormalBounds(); }
  
  protected void setWasIcon(JInternalFrame paramJInternalFrame, Boolean paramBoolean) {
    if (paramBoolean != null)
      paramJInternalFrame.putClientProperty("wasIconOnce", paramBoolean); 
  }
  
  protected boolean wasIcon(JInternalFrame paramJInternalFrame) { return (paramJInternalFrame.getClientProperty("wasIconOnce") == Boolean.TRUE); }
  
  JDesktopPane getDesktopPane(JComponent paramJComponent) {
    JDesktopPane jDesktopPane = null;
    for (Container container = paramJComponent.getParent(); jDesktopPane == null; container = container.getParent()) {
      if (container instanceof JDesktopPane) {
        jDesktopPane = (JDesktopPane)container;
        continue;
      } 
      if (container == null)
        break; 
    } 
    return jDesktopPane;
  }
  
  private void dragFrameFaster(JComponent paramJComponent, int paramInt1, int paramInt2) {
    Rectangle rectangle1 = new Rectangle(this.currentBounds.x, this.currentBounds.y, this.currentBounds.width, this.currentBounds.height);
    this.currentBounds.x = paramInt1;
    this.currentBounds.y = paramInt2;
    if (this.didDrag) {
      emergencyCleanup(paramJComponent);
    } else {
      this.didDrag = true;
      ((JInternalFrame)paramJComponent).danger = false;
    } 
    boolean bool = isFloaterCollision(rectangle1, this.currentBounds);
    JComponent jComponent = (JComponent)paramJComponent.getParent();
    Rectangle rectangle2 = rectangle1.intersection(this.desktopBounds);
    repaintManager = RepaintManager.currentManager(paramJComponent);
    repaintManager.beginPaint();
    try {
      if (!bool)
        repaintManager.copyArea(jComponent, this.desktopGraphics, rectangle2.x, rectangle2.y, rectangle2.width, rectangle2.height, paramInt1 - rectangle1.x, paramInt2 - rectangle1.y, true); 
      paramJComponent.setBounds(this.currentBounds);
      if (!bool) {
        Rectangle rectangle = this.currentBounds;
        repaintManager.notifyRepaintPerformed(jComponent, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      } 
      if (bool) {
        ((JInternalFrame)paramJComponent).isDragging = false;
        jComponent.paintImmediately(this.currentBounds);
        ((JInternalFrame)paramJComponent).isDragging = true;
      } 
      repaintManager.markCompletelyClean(jComponent);
      repaintManager.markCompletelyClean(paramJComponent);
      Rectangle[] arrayOfRectangle = null;
      if (rectangle1.intersects(this.currentBounds)) {
        arrayOfRectangle = SwingUtilities.computeDifference(rectangle1, this.currentBounds);
      } else {
        arrayOfRectangle = new Rectangle[1];
        arrayOfRectangle[0] = rectangle1;
      } 
      byte b;
      for (b = 0; b < arrayOfRectangle.length; b++) {
        jComponent.paintImmediately(arrayOfRectangle[b]);
        Rectangle rectangle = arrayOfRectangle[b];
        repaintManager.notifyRepaintPerformed(jComponent, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
      } 
      if (!rectangle2.equals(rectangle1)) {
        arrayOfRectangle = SwingUtilities.computeDifference(rectangle1, this.desktopBounds);
        for (b = 0; b < arrayOfRectangle.length; b++) {
          (arrayOfRectangle[b]).x += paramInt1 - rectangle1.x;
          (arrayOfRectangle[b]).y += paramInt2 - rectangle1.y;
          ((JInternalFrame)paramJComponent).isDragging = false;
          jComponent.paintImmediately(arrayOfRectangle[b]);
          ((JInternalFrame)paramJComponent).isDragging = true;
          Rectangle rectangle = arrayOfRectangle[b];
          repaintManager.notifyRepaintPerformed(jComponent, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        } 
      } 
    } finally {
      repaintManager.endPaint();
    } 
    Window window = SwingUtilities.getWindowAncestor(paramJComponent);
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    if (!window.isOpaque() && toolkit instanceof SunToolkit && ((SunToolkit)toolkit).needUpdateWindow())
      AWTAccessor.getWindowAccessor().updateWindow(window); 
  }
  
  private boolean isFloaterCollision(Rectangle paramRectangle1, Rectangle paramRectangle2) {
    if (this.floatingItems.length == 0)
      return false; 
    for (byte b = 0; b < this.floatingItems.length; b++) {
      boolean bool1 = paramRectangle1.intersects(this.floatingItems[b]);
      if (bool1)
        return true; 
      boolean bool2 = paramRectangle2.intersects(this.floatingItems[b]);
      if (bool2)
        return true; 
    } 
    return false;
  }
  
  private Rectangle[] findFloatingItems(JComponent paramJComponent) {
    Container container = paramJComponent.getParent();
    Component[] arrayOfComponent = container.getComponents();
    byte b = 0;
    for (b = 0; b < arrayOfComponent.length && arrayOfComponent[b] != paramJComponent; b++);
    Rectangle[] arrayOfRectangle = new Rectangle[b];
    for (b = 0; b < arrayOfRectangle.length; b++)
      arrayOfRectangle[b] = arrayOfComponent[b].getBounds(); 
    return arrayOfRectangle;
  }
  
  private void emergencyCleanup(final JComponent f) {
    if (((JInternalFrame)paramJComponent).danger) {
      SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              ((JInternalFrame)this.val$f).isDragging = false;
              f.paintImmediately(0, 0, f.getWidth(), f.getHeight());
              ((JInternalFrame)this.val$f).isDragging = true;
            }
          });
      ((JInternalFrame)paramJComponent).danger = false;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\DefaultDesktopManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */