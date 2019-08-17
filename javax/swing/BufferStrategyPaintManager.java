package javax.swing;

import com.sun.java.swing.SwingUtilities3;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.ImageCapabilities;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import sun.awt.SubRegionShowable;
import sun.awt.SunToolkit;
import sun.java2d.SunGraphics2D;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;
import sun.util.logging.PlatformLogger;

class BufferStrategyPaintManager extends RepaintManager.PaintManager {
  private static Method COMPONENT_CREATE_BUFFER_STRATEGY_METHOD;
  
  private static Method COMPONENT_GET_BUFFER_STRATEGY_METHOD;
  
  private static final PlatformLogger LOGGER = PlatformLogger.getLogger("javax.swing.BufferStrategyPaintManager");
  
  private ArrayList<BufferInfo> bufferInfos = new ArrayList(1);
  
  private boolean painting;
  
  private boolean showing;
  
  private int accumulatedX;
  
  private int accumulatedY;
  
  private int accumulatedMaxX;
  
  private int accumulatedMaxY;
  
  private JComponent rootJ;
  
  private int xOffset;
  
  private int yOffset;
  
  private Graphics bsg;
  
  private BufferStrategy bufferStrategy;
  
  private BufferInfo bufferInfo;
  
  private boolean disposeBufferOnEnd;
  
  private static Method getGetBufferStrategyMethod() {
    if (COMPONENT_GET_BUFFER_STRATEGY_METHOD == null)
      getMethods(); 
    return COMPONENT_GET_BUFFER_STRATEGY_METHOD;
  }
  
  private static Method getCreateBufferStrategyMethod() {
    if (COMPONENT_CREATE_BUFFER_STRATEGY_METHOD == null)
      getMethods(); 
    return COMPONENT_CREATE_BUFFER_STRATEGY_METHOD;
  }
  
  private static void getMethods() { AccessController.doPrivileged(new PrivilegedAction<Object>() {
          public Object run() {
            try {
              COMPONENT_CREATE_BUFFER_STRATEGY_METHOD = java.awt.Component.class.getDeclaredMethod("createBufferStrategy", new Class[] { int.class, BufferCapabilities.class });
              COMPONENT_CREATE_BUFFER_STRATEGY_METHOD.setAccessible(true);
              COMPONENT_GET_BUFFER_STRATEGY_METHOD = java.awt.Component.class.getDeclaredMethod("getBufferStrategy", new Class[0]);
              COMPONENT_GET_BUFFER_STRATEGY_METHOD.setAccessible(true);
            } catch (SecurityException securityException) {
              assert false;
            } catch (NoSuchMethodException noSuchMethodException) {
              assert false;
            } 
            return null;
          }
        }); }
  
  protected void dispose() { SwingUtilities.invokeLater(new Runnable() {
          public void run() {
            ArrayList arrayList;
            synchronized (BufferStrategyPaintManager.this) {
              while (BufferStrategyPaintManager.this.showing) {
                try {
                  BufferStrategyPaintManager.this.wait();
                } catch (InterruptedException interruptedException) {}
              } 
              arrayList = BufferStrategyPaintManager.this.bufferInfos;
              BufferStrategyPaintManager.this.bufferInfos = null;
            } 
            BufferStrategyPaintManager.this.dispose(arrayList);
          }
        }); }
  
  private void dispose(List<BufferInfo> paramList) {
    if (LOGGER.isLoggable(PlatformLogger.Level.FINER))
      LOGGER.finer("BufferStrategyPaintManager disposed", new RuntimeException()); 
    if (paramList != null)
      for (BufferInfo bufferInfo1 : paramList)
        bufferInfo1.dispose();  
  }
  
  public boolean show(Container paramContainer, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    synchronized (this) {
      if (this.painting)
        return false; 
      this.showing = true;
    } 
    try {
      BufferInfo bufferInfo1 = getBufferInfo(paramContainer);
      BufferStrategy bufferStrategy1;
      if (bufferInfo1 != null && bufferInfo1.isInSync() && (bufferStrategy1 = bufferInfo1.getBufferStrategy(false)) != null) {
        SubRegionShowable subRegionShowable = (SubRegionShowable)bufferStrategy1;
        boolean bool = bufferInfo1.getPaintAllOnExpose();
        bufferInfo1.setPaintAllOnExpose(false);
        if (subRegionShowable.showIfNotLost(paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4))
          return !bool; 
        this.bufferInfo.setContentsLostDuringExpose(true);
      } 
    } finally {
      synchronized (this) {
        this.showing = false;
        notifyAll();
      } 
    } 
    return false;
  }
  
  public boolean paint(JComponent paramJComponent1, JComponent paramJComponent2, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Container container = fetchRoot(paramJComponent1);
    if (prepare(paramJComponent1, container, true, paramInt1, paramInt2, paramInt3, paramInt4)) {
      if (paramGraphics instanceof SunGraphics2D && ((SunGraphics2D)paramGraphics).getDestination() == container) {
        int i = ((SunGraphics2D)this.bsg).constrainX;
        int j = ((SunGraphics2D)this.bsg).constrainY;
        if (i != 0 || j != 0)
          this.bsg.translate(-i, -j); 
        ((SunGraphics2D)this.bsg).constrain(this.xOffset + i, this.yOffset + j, paramInt1 + paramInt3, paramInt2 + paramInt4);
        this.bsg.setClip(paramInt1, paramInt2, paramInt3, paramInt4);
        paramJComponent1.paintToOffscreen(this.bsg, paramInt1, paramInt2, paramInt3, paramInt4, paramInt1 + paramInt3, paramInt2 + paramInt4);
        accumulate(this.xOffset + paramInt1, this.yOffset + paramInt2, paramInt3, paramInt4);
        return true;
      } 
      this.bufferInfo.setInSync(false);
    } 
    if (LOGGER.isLoggable(PlatformLogger.Level.FINER))
      LOGGER.finer("prepare failed"); 
    return super.paint(paramJComponent1, paramJComponent2, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public void copyArea(JComponent paramJComponent, Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean) {
    Container container = fetchRoot(paramJComponent);
    if (prepare(paramJComponent, container, false, 0, 0, 0, 0) && this.bufferInfo.isInSync()) {
      if (paramBoolean) {
        Rectangle rectangle = paramJComponent.getVisibleRect();
        int i = this.xOffset + paramInt1;
        int j = this.yOffset + paramInt2;
        this.bsg.clipRect(this.xOffset + rectangle.x, this.yOffset + rectangle.y, rectangle.width, rectangle.height);
        this.bsg.copyArea(i, j, paramInt3, paramInt4, paramInt5, paramInt6);
      } else {
        this.bsg.copyArea(this.xOffset + paramInt1, this.yOffset + paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      } 
      accumulate(paramInt1 + this.xOffset + paramInt5, paramInt2 + this.yOffset + paramInt6, paramInt3, paramInt4);
    } else {
      if (LOGGER.isLoggable(PlatformLogger.Level.FINER))
        LOGGER.finer("copyArea: prepare failed or not in sync"); 
      if (!flushAccumulatedRegion()) {
        this.rootJ.repaint();
      } else {
        super.copyArea(paramJComponent, paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramBoolean);
      } 
    } 
  }
  
  public void beginPaint() {
    synchronized (this) {
      this.painting = true;
      while (this.showing) {
        try {
          wait();
        } catch (InterruptedException interruptedException) {}
      } 
    } 
    if (LOGGER.isLoggable(PlatformLogger.Level.FINEST))
      LOGGER.finest("beginPaint"); 
    resetAccumulated();
  }
  
  public void endPaint() {
    if (LOGGER.isLoggable(PlatformLogger.Level.FINEST))
      LOGGER.finest("endPaint: region " + this.accumulatedX + " " + this.accumulatedY + " " + this.accumulatedMaxX + " " + this.accumulatedMaxY); 
    if (this.painting && !flushAccumulatedRegion())
      if (!isRepaintingRoot()) {
        repaintRoot(this.rootJ);
      } else {
        resetDoubleBufferPerWindow();
        this.rootJ.repaint();
      }  
    BufferInfo bufferInfo1 = null;
    synchronized (this) {
      this.painting = false;
      if (this.disposeBufferOnEnd) {
        this.disposeBufferOnEnd = false;
        bufferInfo1 = this.bufferInfo;
        this.bufferInfos.remove(bufferInfo1);
      } 
    } 
    if (bufferInfo1 != null)
      bufferInfo1.dispose(); 
  }
  
  private boolean flushAccumulatedRegion() {
    boolean bool = true;
    if (this.accumulatedX != Integer.MAX_VALUE) {
      SubRegionShowable subRegionShowable = (SubRegionShowable)this.bufferStrategy;
      boolean bool1 = this.bufferStrategy.contentsLost();
      if (!bool1) {
        subRegionShowable.show(this.accumulatedX, this.accumulatedY, this.accumulatedMaxX, this.accumulatedMaxY);
        bool1 = this.bufferStrategy.contentsLost();
      } 
      if (bool1) {
        if (LOGGER.isLoggable(PlatformLogger.Level.FINER))
          LOGGER.finer("endPaint: contents lost"); 
        this.bufferInfo.setInSync(false);
        bool = false;
      } 
    } 
    resetAccumulated();
    return bool;
  }
  
  private void resetAccumulated() {
    this.accumulatedX = Integer.MAX_VALUE;
    this.accumulatedY = Integer.MAX_VALUE;
    this.accumulatedMaxX = 0;
    this.accumulatedMaxY = 0;
  }
  
  public void doubleBufferingChanged(final JRootPane rootPane) {
    if ((!paramJRootPane.isDoubleBuffered() || !paramJRootPane.getUseTrueDoubleBuffering()) && paramJRootPane.getParent() != null)
      if (!SwingUtilities.isEventDispatchThread()) {
        Runnable runnable = new Runnable() {
            public void run() { BufferStrategyPaintManager.this.doubleBufferingChanged0(rootPane); }
          };
        SwingUtilities.invokeLater(runnable);
      } else {
        doubleBufferingChanged0(paramJRootPane);
      }  
  }
  
  private void doubleBufferingChanged0(JRootPane paramJRootPane) {
    BufferInfo bufferInfo1;
    synchronized (this) {
      while (this.showing) {
        try {
          wait();
        } catch (InterruptedException interruptedException) {}
      } 
      bufferInfo1 = getBufferInfo(paramJRootPane.getParent());
      if (this.painting && this.bufferInfo == bufferInfo1) {
        this.disposeBufferOnEnd = true;
        bufferInfo1 = null;
      } else if (bufferInfo1 != null) {
        this.bufferInfos.remove(bufferInfo1);
      } 
    } 
    if (bufferInfo1 != null)
      bufferInfo1.dispose(); 
  }
  
  private boolean prepare(JComponent paramJComponent, Container paramContainer, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (this.bsg != null) {
      this.bsg.dispose();
      this.bsg = null;
    } 
    this.bufferStrategy = null;
    if (paramContainer != null) {
      boolean bool = false;
      BufferInfo bufferInfo1 = getBufferInfo(paramContainer);
      if (bufferInfo1 == null) {
        bool = true;
        bufferInfo1 = new BufferInfo(paramContainer);
        this.bufferInfos.add(bufferInfo1);
        if (LOGGER.isLoggable(PlatformLogger.Level.FINER))
          LOGGER.finer("prepare: new BufferInfo: " + paramContainer); 
      } 
      this.bufferInfo = bufferInfo1;
      if (!bufferInfo1.hasBufferStrategyChanged()) {
        this.bufferStrategy = bufferInfo1.getBufferStrategy(true);
        if (this.bufferStrategy != null) {
          this.bsg = this.bufferStrategy.getDrawGraphics();
          if (this.bufferStrategy.contentsRestored()) {
            bool = true;
            if (LOGGER.isLoggable(PlatformLogger.Level.FINER))
              LOGGER.finer("prepare: contents restored in prepare"); 
          } 
        } else {
          return false;
        } 
        if (bufferInfo1.getContentsLostDuringExpose()) {
          bool = true;
          bufferInfo1.setContentsLostDuringExpose(false);
          if (LOGGER.isLoggable(PlatformLogger.Level.FINER))
            LOGGER.finer("prepare: contents lost on expose"); 
        } 
        if (paramBoolean && paramJComponent == this.rootJ && paramInt1 == 0 && paramInt2 == 0 && paramJComponent.getWidth() == paramInt3 && paramJComponent.getHeight() == paramInt4) {
          bufferInfo1.setInSync(true);
        } else if (bool) {
          bufferInfo1.setInSync(false);
          if (!isRepaintingRoot()) {
            repaintRoot(this.rootJ);
          } else {
            resetDoubleBufferPerWindow();
          } 
        } 
        return (this.bufferInfos != null);
      } 
    } 
    return false;
  }
  
  private Container fetchRoot(JComponent paramJComponent) {
    boolean bool = false;
    this.rootJ = paramJComponent;
    Container container = paramJComponent;
    this.xOffset = this.yOffset = 0;
    while (container != null && !(container instanceof Window) && !SunToolkit.isInstanceOf(container, "java.applet.Applet")) {
      this.xOffset += container.getX();
      this.yOffset += container.getY();
      container = container.getParent();
      if (container != null) {
        if (container instanceof JComponent) {
          this.rootJ = (JComponent)container;
          continue;
        } 
        if (!container.isLightweight()) {
          if (!bool) {
            bool = true;
            continue;
          } 
          return null;
        } 
      } 
    } 
    return (container instanceof RootPaneContainer && this.rootJ instanceof JRootPane && this.rootJ.isDoubleBuffered() && ((JRootPane)this.rootJ).getUseTrueDoubleBuffering()) ? container : null;
  }
  
  private void resetDoubleBufferPerWindow() {
    if (this.bufferInfos != null) {
      dispose(this.bufferInfos);
      this.bufferInfos = null;
      this.repaintManager.setPaintManager(null);
    } 
  }
  
  private BufferInfo getBufferInfo(Container paramContainer) {
    for (int i = this.bufferInfos.size() - 1; i >= 0; i--) {
      BufferInfo bufferInfo1 = (BufferInfo)this.bufferInfos.get(i);
      Container container = bufferInfo1.getRoot();
      if (container == null) {
        this.bufferInfos.remove(i);
        if (LOGGER.isLoggable(PlatformLogger.Level.FINER))
          LOGGER.finer("BufferInfo pruned, root null"); 
      } else if (container == paramContainer) {
        return bufferInfo1;
      } 
    } 
    return null;
  }
  
  private void accumulate(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.accumulatedX = Math.min(paramInt1, this.accumulatedX);
    this.accumulatedY = Math.min(paramInt2, this.accumulatedY);
    this.accumulatedMaxX = Math.max(this.accumulatedMaxX, paramInt1 + paramInt3);
    this.accumulatedMaxY = Math.max(this.accumulatedMaxY, paramInt2 + paramInt4);
  }
  
  private class BufferInfo extends ComponentAdapter implements WindowListener {
    private WeakReference<BufferStrategy> weakBS;
    
    private WeakReference<Container> root;
    
    private boolean inSync;
    
    private boolean contentsLostDuringExpose;
    
    private boolean paintAllOnExpose;
    
    public BufferInfo(Container param1Container) {
      this.root = new WeakReference(param1Container);
      param1Container.addComponentListener(this);
      if (param1Container instanceof Window)
        ((Window)param1Container).addWindowListener(this); 
    }
    
    public void setPaintAllOnExpose(boolean param1Boolean) { this.paintAllOnExpose = param1Boolean; }
    
    public boolean getPaintAllOnExpose() { return this.paintAllOnExpose; }
    
    public void setContentsLostDuringExpose(boolean param1Boolean) { this.contentsLostDuringExpose = param1Boolean; }
    
    public boolean getContentsLostDuringExpose() { return this.contentsLostDuringExpose; }
    
    public void setInSync(boolean param1Boolean) { this.inSync = param1Boolean; }
    
    public boolean isInSync() { return this.inSync; }
    
    public Container getRoot() { return (this.root == null) ? null : (Container)this.root.get(); }
    
    public BufferStrategy getBufferStrategy(boolean param1Boolean) {
      BufferStrategy bufferStrategy = (this.weakBS == null) ? null : (BufferStrategy)this.weakBS.get();
      if (bufferStrategy == null && param1Boolean) {
        bufferStrategy = createBufferStrategy();
        if (bufferStrategy != null)
          this.weakBS = new WeakReference(bufferStrategy); 
        if (LOGGER.isLoggable(PlatformLogger.Level.FINER))
          LOGGER.finer("getBufferStrategy: created bs: " + bufferStrategy); 
      } 
      return bufferStrategy;
    }
    
    public boolean hasBufferStrategyChanged() {
      Container container = getRoot();
      if (container != null) {
        BufferStrategy bufferStrategy1 = null;
        BufferStrategy bufferStrategy2 = null;
        bufferStrategy1 = getBufferStrategy(false);
        if (container instanceof Window) {
          bufferStrategy2 = ((Window)container).getBufferStrategy();
        } else {
          try {
            bufferStrategy2 = (BufferStrategy)BufferStrategyPaintManager.getGetBufferStrategyMethod().invoke(container, new Object[0]);
          } catch (InvocationTargetException invocationTargetException) {
            assert false;
          } catch (IllegalArgumentException illegalArgumentException) {
            assert false;
          } catch (IllegalAccessException illegalAccessException) {
            assert false;
          } 
        } 
        if (bufferStrategy2 != bufferStrategy1) {
          if (bufferStrategy1 != null)
            bufferStrategy1.dispose(); 
          this.weakBS = null;
          return true;
        } 
      } 
      return false;
    }
    
    private BufferStrategy createBufferStrategy() {
      Container container = getRoot();
      if (container == null)
        return null; 
      BufferStrategy bufferStrategy = null;
      if (SwingUtilities3.isVsyncRequested(container)) {
        bufferStrategy = createBufferStrategy(container, true);
        if (LOGGER.isLoggable(PlatformLogger.Level.FINER))
          LOGGER.finer("createBufferStrategy: using vsynced strategy"); 
      } 
      if (bufferStrategy == null)
        bufferStrategy = createBufferStrategy(container, false); 
      if (!(bufferStrategy instanceof SubRegionShowable))
        bufferStrategy = null; 
      return bufferStrategy;
    }
    
    private BufferStrategy createBufferStrategy(Container param1Container, boolean param1Boolean) {
      BufferCapabilities bufferCapabilities;
      if (param1Boolean) {
        bufferCapabilities = new ExtendedBufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), BufferCapabilities.FlipContents.COPIED, ExtendedBufferCapabilities.VSyncType.VSYNC_ON);
      } else {
        bufferCapabilities = new BufferCapabilities(new ImageCapabilities(true), new ImageCapabilities(true), null);
      } 
      BufferStrategy bufferStrategy = null;
      if (SunToolkit.isInstanceOf(param1Container, "java.applet.Applet")) {
        try {
          BufferStrategyPaintManager.getCreateBufferStrategyMethod().invoke(param1Container, new Object[] { Integer.valueOf(2), bufferCapabilities });
          bufferStrategy = (BufferStrategy)BufferStrategyPaintManager.getGetBufferStrategyMethod().invoke(param1Container, new Object[0]);
        } catch (InvocationTargetException invocationTargetException) {
          if (LOGGER.isLoggable(PlatformLogger.Level.FINER))
            LOGGER.finer("createBufferStratety failed", invocationTargetException); 
        } catch (IllegalArgumentException illegalArgumentException) {
          assert false;
        } catch (IllegalAccessException illegalAccessException) {
          assert false;
        } 
      } else {
        try {
          ((Window)param1Container).createBufferStrategy(2, bufferCapabilities);
          bufferStrategy = ((Window)param1Container).getBufferStrategy();
        } catch (AWTException aWTException) {
          if (LOGGER.isLoggable(PlatformLogger.Level.FINER))
            LOGGER.finer("createBufferStratety failed", aWTException); 
        } 
      } 
      return bufferStrategy;
    }
    
    public void dispose() {
      Container container = getRoot();
      if (LOGGER.isLoggable(PlatformLogger.Level.FINER))
        LOGGER.finer("disposed BufferInfo for: " + container); 
      if (container != null) {
        container.removeComponentListener(this);
        if (container instanceof Window)
          ((Window)container).removeWindowListener(this); 
        BufferStrategy bufferStrategy = getBufferStrategy(false);
        if (bufferStrategy != null)
          bufferStrategy.dispose(); 
      } 
      this.root = null;
      this.weakBS = null;
    }
    
    public void componentHidden(ComponentEvent param1ComponentEvent) {
      Container container = getRoot();
      if (container != null && container.isVisible()) {
        container.repaint();
      } else {
        setPaintAllOnExpose(true);
      } 
    }
    
    public void windowIconified(WindowEvent param1WindowEvent) { setPaintAllOnExpose(true); }
    
    public void windowClosed(WindowEvent param1WindowEvent) {
      synchronized (BufferStrategyPaintManager.this) {
        while (BufferStrategyPaintManager.this.showing) {
          try {
            BufferStrategyPaintManager.this.wait();
          } catch (InterruptedException interruptedException) {}
        } 
        BufferStrategyPaintManager.this.bufferInfos.remove(this);
      } 
      dispose();
    }
    
    public void windowOpened(WindowEvent param1WindowEvent) {}
    
    public void windowClosing(WindowEvent param1WindowEvent) {}
    
    public void windowDeiconified(WindowEvent param1WindowEvent) {}
    
    public void windowActivated(WindowEvent param1WindowEvent) {}
    
    public void windowDeactivated(WindowEvent param1WindowEvent) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\BufferStrategyPaintManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */