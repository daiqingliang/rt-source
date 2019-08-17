package sun.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.peer.ComponentPeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.AccessController;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.RepaintManager;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import sun.awt.AWTAccessor;
import sun.awt.DisplayChangedListener;
import sun.awt.LightweightFrame;
import sun.awt.OverrideNativeWindowHandle;
import sun.security.action.GetPropertyAction;

public final class JLightweightFrame extends LightweightFrame implements RootPaneContainer {
  private final JRootPane rootPane = new JRootPane();
  
  private LightweightContent content;
  
  private Component component;
  
  private JPanel contentPane;
  
  private BufferedImage bbImage;
  
  private static boolean copyBufferEnabled;
  
  private int[] copyBuffer;
  
  private PropertyChangeListener layoutSizeListener;
  
  private SwingUtilities2.RepaintListener repaintListener;
  
  public JLightweightFrame() {
    copyBufferEnabled = "true".equals(AccessController.doPrivileged(new GetPropertyAction("swing.jlf.copyBufferEnabled", "true")));
    add(this.rootPane, "Center");
    setFocusTraversalPolicy(new LayoutFocusTraversalPolicy());
    if (getGraphicsConfiguration().isTranslucencyCapable())
      setBackground(new Color(0, 0, 0, 0)); 
    this.layoutSizeListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
          Dimension dimension = (Dimension)param1PropertyChangeEvent.getNewValue();
          if ("preferredSize".equals(param1PropertyChangeEvent.getPropertyName())) {
            JLightweightFrame.this.content.preferredSizeChanged(dimension.width, dimension.height);
          } else if ("maximumSize".equals(param1PropertyChangeEvent.getPropertyName())) {
            JLightweightFrame.this.content.maximumSizeChanged(dimension.width, dimension.height);
          } else if ("minimumSize".equals(param1PropertyChangeEvent.getPropertyName())) {
            JLightweightFrame.this.content.minimumSizeChanged(dimension.width, dimension.height);
          } 
        }
      };
    this.repaintListener = ((paramJComponent, paramInt1, paramInt2, paramInt3, paramInt4) -> {
        Window window = SwingUtilities.getWindowAncestor(paramJComponent);
        if (window != this)
          return; 
        Point point = SwingUtilities.convertPoint(paramJComponent, paramInt1, paramInt2, window);
        Rectangle rectangle = (new Rectangle(point.x, point.y, paramInt3, paramInt4)).intersection(new Rectangle(0, 0, this.bbImage.getWidth() / this.scaleFactor, this.bbImage.getHeight() / this.scaleFactor));
        if (!rectangle.isEmpty())
          notifyImageUpdated(rectangle.x, rectangle.y, rectangle.width, rectangle.height); 
      });
    SwingAccessor.getRepaintManagerAccessor().addRepaintListener(RepaintManager.currentManager(this), this.repaintListener);
  }
  
  public void dispose() {
    SwingAccessor.getRepaintManagerAccessor().removeRepaintListener(RepaintManager.currentManager(this), this.repaintListener);
    super.dispose();
  }
  
  public void setContent(LightweightContent paramLightweightContent) {
    if (paramLightweightContent == null) {
      System.err.println("JLightweightFrame.setContent: content may not be null!");
      return;
    } 
    this.content = paramLightweightContent;
    this.component = paramLightweightContent.getComponent();
    Dimension dimension = this.component.getPreferredSize();
    paramLightweightContent.preferredSizeChanged(dimension.width, dimension.height);
    dimension = this.component.getMaximumSize();
    paramLightweightContent.maximumSizeChanged(dimension.width, dimension.height);
    dimension = this.component.getMinimumSize();
    paramLightweightContent.minimumSizeChanged(dimension.width, dimension.height);
    initInterior();
  }
  
  public Graphics getGraphics() {
    if (this.bbImage == null)
      return null; 
    Graphics2D graphics2D = this.bbImage.createGraphics();
    graphics2D.setBackground(getBackground());
    graphics2D.setColor(getForeground());
    graphics2D.setFont(getFont());
    graphics2D.scale(this.scaleFactor, this.scaleFactor);
    return graphics2D;
  }
  
  public void grabFocus() {
    if (this.content != null)
      this.content.focusGrabbed(); 
  }
  
  public void ungrabFocus() {
    if (this.content != null)
      this.content.focusUngrabbed(); 
  }
  
  public int getScaleFactor() { return this.scaleFactor; }
  
  public void notifyDisplayChanged(int paramInt) {
    if (paramInt != this.scaleFactor) {
      if (!copyBufferEnabled)
        this.content.paintLock(); 
      try {
        if (this.bbImage != null)
          resizeBuffer(getWidth(), getHeight(), paramInt); 
      } finally {
        if (!copyBufferEnabled)
          this.content.paintUnlock(); 
      } 
      this.scaleFactor = paramInt;
    } 
    if (getPeer() instanceof DisplayChangedListener)
      ((DisplayChangedListener)getPeer()).displayChanged(); 
    repaint();
  }
  
  public void addNotify() {
    super.addNotify();
    if (getPeer() instanceof DisplayChangedListener)
      ((DisplayChangedListener)getPeer()).displayChanged(); 
  }
  
  private void syncCopyBuffer(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    this.content.paintLock();
    try {
      int[] arrayOfInt = ((DataBufferInt)this.bbImage.getRaster().getDataBuffer()).getData();
      if (paramBoolean)
        this.copyBuffer = new int[arrayOfInt.length]; 
      int i = this.bbImage.getWidth();
      paramInt1 *= paramInt5;
      paramInt2 *= paramInt5;
      paramInt3 *= paramInt5;
      paramInt4 *= paramInt5;
      for (int j = 0; j < paramInt4; j++) {
        int k = (paramInt2 + j) * i + paramInt1;
        System.arraycopy(arrayOfInt, k, this.copyBuffer, k, paramInt3);
      } 
    } finally {
      this.content.paintUnlock();
    } 
  }
  
  private void notifyImageUpdated(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (copyBufferEnabled)
      syncCopyBuffer(false, paramInt1, paramInt2, paramInt3, paramInt4, this.scaleFactor); 
    this.content.imageUpdated(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  private void initInterior() {
    this.contentPane = new JPanel() {
        public void paint(Graphics param1Graphics) {
          if (!copyBufferEnabled)
            JLightweightFrame.this.content.paintLock(); 
          try {
            super.paint(param1Graphics);
            final Rectangle clip = (param1Graphics.getClipBounds() != null) ? param1Graphics.getClipBounds() : new Rectangle(0, 0, JLightweightFrame.this.contentPane.getWidth(), JLightweightFrame.this.contentPane.getHeight());
            rectangle.x = Math.max(0, rectangle.x);
            rectangle.y = Math.max(0, rectangle.y);
            rectangle.width = Math.min(JLightweightFrame.this.contentPane.getWidth(), rectangle.width);
            rectangle.height = Math.min(JLightweightFrame.this.contentPane.getHeight(), rectangle.height);
            EventQueue.invokeLater(new Runnable() {
                  public void run() {
                    Rectangle rectangle = JLightweightFrame.null.this.this$0.contentPane.getBounds().intersection(clip);
                    JLightweightFrame.null.this.this$0.notifyImageUpdated(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                  }
                });
          } finally {
            if (!copyBufferEnabled)
              JLightweightFrame.this.content.paintUnlock(); 
          } 
        }
        
        protected boolean isPaintingOrigin() { return true; }
      };
    this.contentPane.setLayout(new BorderLayout());
    this.contentPane.add(this.component);
    if ("true".equals(AccessController.doPrivileged(new GetPropertyAction("swing.jlf.contentPaneTransparent", "false"))))
      this.contentPane.setOpaque(false); 
    setContentPane(this.contentPane);
    this.contentPane.addContainerListener(new ContainerListener() {
          public void componentAdded(ContainerEvent param1ContainerEvent) {
            Component component = JLightweightFrame.this.component;
            if (param1ContainerEvent.getChild() == component) {
              component.addPropertyChangeListener("preferredSize", JLightweightFrame.this.layoutSizeListener);
              component.addPropertyChangeListener("maximumSize", JLightweightFrame.this.layoutSizeListener);
              component.addPropertyChangeListener("minimumSize", JLightweightFrame.this.layoutSizeListener);
            } 
          }
          
          public void componentRemoved(ContainerEvent param1ContainerEvent) {
            Component component = JLightweightFrame.this.component;
            if (param1ContainerEvent.getChild() == component)
              component.removePropertyChangeListener(JLightweightFrame.this.layoutSizeListener); 
          }
        });
  }
  
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramInt3 == 0 || paramInt4 == 0)
      return; 
    if (!copyBufferEnabled)
      this.content.paintLock(); 
    try {
      boolean bool = (this.bbImage == null) ? 1 : 0;
      int i = paramInt3;
      int j = paramInt4;
      if (this.bbImage != null) {
        int k = this.bbImage.getWidth() / this.scaleFactor;
        int m = this.bbImage.getHeight() / this.scaleFactor;
        if (paramInt3 != k || paramInt4 != m) {
          bool = true;
          if (this.bbImage != null) {
            int n = k;
            int i1 = m;
            if (n >= i && i1 >= j) {
              bool = false;
            } else {
              if (n >= i) {
                i = n;
              } else {
                i = Math.max((int)(n * 1.2D), paramInt3);
              } 
              if (i1 >= j) {
                j = i1;
              } else {
                j = Math.max((int)(i1 * 1.2D), paramInt4);
              } 
            } 
          } 
        } 
      } 
      if (bool) {
        resizeBuffer(i, j, this.scaleFactor);
        return;
      } 
      this.content.imageReshaped(0, 0, paramInt3, paramInt4);
    } finally {
      if (!copyBufferEnabled)
        this.content.paintUnlock(); 
    } 
  }
  
  private void resizeBuffer(int paramInt1, int paramInt2, int paramInt3) {
    this.bbImage = new BufferedImage(paramInt1 * paramInt3, paramInt2 * paramInt3, 3);
    int[] arrayOfInt = ((DataBufferInt)this.bbImage.getRaster().getDataBuffer()).getData();
    if (copyBufferEnabled) {
      syncCopyBuffer(true, 0, 0, paramInt1, paramInt2, paramInt3);
      arrayOfInt = this.copyBuffer;
    } 
    this.content.imageBufferReset(arrayOfInt, 0, 0, paramInt1, paramInt2, paramInt1 * paramInt3, paramInt3);
  }
  
  public JRootPane getRootPane() { return this.rootPane; }
  
  public void setContentPane(Container paramContainer) { getRootPane().setContentPane(paramContainer); }
  
  public Container getContentPane() { return getRootPane().getContentPane(); }
  
  public void setLayeredPane(JLayeredPane paramJLayeredPane) { getRootPane().setLayeredPane(paramJLayeredPane); }
  
  public JLayeredPane getLayeredPane() { return getRootPane().getLayeredPane(); }
  
  public void setGlassPane(Component paramComponent) { getRootPane().setGlassPane(paramComponent); }
  
  public Component getGlassPane() { return getRootPane().getGlassPane(); }
  
  private void updateClientCursor() {
    Point point = MouseInfo.getPointerInfo().getLocation();
    SwingUtilities.convertPointFromScreen(point, this);
    Component component1 = SwingUtilities.getDeepestComponentAt(this, point.x, point.y);
    if (component1 != null)
      this.content.setCursor(component1.getCursor()); 
  }
  
  public void overrideNativeWindowHandle(long paramLong, Runnable paramRunnable) {
    ComponentPeer componentPeer = AWTAccessor.getComponentAccessor().getPeer(this);
    if (componentPeer instanceof OverrideNativeWindowHandle)
      ((OverrideNativeWindowHandle)componentPeer).overrideWindowHandle(paramLong); 
    if (paramRunnable != null)
      paramRunnable.run(); 
  }
  
  public <T extends java.awt.dnd.DragGestureRecognizer> T createDragGestureRecognizer(Class<T> paramClass, DragSource paramDragSource, Component paramComponent, int paramInt, DragGestureListener paramDragGestureListener) { return (T)((this.content == null) ? null : this.content.createDragGestureRecognizer(paramClass, paramDragSource, paramComponent, paramInt, paramDragGestureListener)); }
  
  public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent paramDragGestureEvent) throws InvalidDnDOperationException { return (this.content == null) ? null : this.content.createDragSourceContextPeer(paramDragGestureEvent); }
  
  public void addDropTarget(DropTarget paramDropTarget) {
    if (this.content == null)
      return; 
    this.content.addDropTarget(paramDropTarget);
  }
  
  public void removeDropTarget(DropTarget paramDropTarget) {
    if (this.content == null)
      return; 
    this.content.removeDropTarget(paramDropTarget);
  }
  
  static  {
    SwingAccessor.setJLightweightFrameAccessor(new SwingAccessor.JLightweightFrameAccessor() {
          public void updateCursor(JLightweightFrame param1JLightweightFrame) { param1JLightweightFrame.updateClientCursor(); }
        });
    copyBufferEnabled = "true".equals(AccessController.doPrivileged(new GetPropertyAction("swing.jlf.copyBufferEnabled", "true")));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\JLightweightFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */