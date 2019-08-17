package sun.java2d.d3d;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Window;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import sun.awt.AWTAccessor;
import sun.awt.Win32GraphicsConfig;
import sun.awt.windows.WComponentPeer;
import sun.java2d.InvalidPipeException;
import sun.java2d.ScreenUpdateManager;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.windows.GDIWindowSurfaceData;
import sun.java2d.windows.WindowsFlags;
import sun.misc.ThreadGroupUtils;

public class D3DScreenUpdateManager extends ScreenUpdateManager implements Runnable {
  private static final int MIN_WIN_SIZE = 150;
  
  private boolean needsUpdateNow;
  
  private Object runLock = new Object();
  
  private ArrayList<D3DSurfaceData.D3DWindowSurfaceData> d3dwSurfaces;
  
  private HashMap<D3DSurfaceData.D3DWindowSurfaceData, GDIWindowSurfaceData> gdiSurfaces;
  
  public D3DScreenUpdateManager() { AccessController.doPrivileged(() -> {
          ThreadGroup threadGroup = ThreadGroupUtils.getRootThreadGroup();
          Thread thread = new Thread(threadGroup, ());
          thread.setContextClassLoader(null);
          try {
            Runtime.getRuntime().addShutdownHook(thread);
          } catch (Exception exception) {
            this.done = true;
          } 
          return null;
        }); }
  
  public SurfaceData createScreenSurface(Win32GraphicsConfig paramWin32GraphicsConfig, WComponentPeer paramWComponentPeer, int paramInt, boolean paramBoolean) {
    if (this.done || !(paramWin32GraphicsConfig instanceof D3DGraphicsConfig))
      return super.createScreenSurface(paramWin32GraphicsConfig, paramWComponentPeer, paramInt, paramBoolean); 
    GDIWindowSurfaceData gDIWindowSurfaceData = null;
    if (canUseD3DOnScreen(paramWComponentPeer, paramWin32GraphicsConfig, paramInt))
      try {
        gDIWindowSurfaceData = D3DSurfaceData.createData(paramWComponentPeer);
      } catch (InvalidPipeException invalidPipeException) {
        gDIWindowSurfaceData = null;
      }  
    if (gDIWindowSurfaceData == null)
      gDIWindowSurfaceData = GDIWindowSurfaceData.createData(paramWComponentPeer); 
    if (paramBoolean)
      repaintPeerTarget(paramWComponentPeer); 
    return gDIWindowSurfaceData;
  }
  
  public static boolean canUseD3DOnScreen(WComponentPeer paramWComponentPeer, Win32GraphicsConfig paramWin32GraphicsConfig, int paramInt) {
    if (!(paramWin32GraphicsConfig instanceof D3DGraphicsConfig))
      return false; 
    D3DGraphicsConfig d3DGraphicsConfig = (D3DGraphicsConfig)paramWin32GraphicsConfig;
    D3DGraphicsDevice d3DGraphicsDevice = d3DGraphicsConfig.getD3DDevice();
    String str = paramWComponentPeer.getClass().getName();
    Rectangle rectangle = paramWComponentPeer.getBounds();
    Component component = (Component)paramWComponentPeer.getTarget();
    Window window = d3DGraphicsDevice.getFullScreenWindow();
    return (WindowsFlags.isD3DOnScreenEnabled() && d3DGraphicsDevice.isD3DEnabledOnDevice() && paramWComponentPeer.isAccelCapable() && (rectangle.width > 150 || rectangle.height > 150) && paramInt == 0 && (window == null || (window == component && !hasHWChildren(component))) && (str.equals("sun.awt.windows.WCanvasPeer") || str.equals("sun.awt.windows.WDialogPeer") || str.equals("sun.awt.windows.WPanelPeer") || str.equals("sun.awt.windows.WWindowPeer") || str.equals("sun.awt.windows.WFramePeer") || str.equals("sun.awt.windows.WEmbeddedFramePeer")));
  }
  
  public Graphics2D createGraphics(SurfaceData paramSurfaceData, WComponentPeer paramWComponentPeer, Color paramColor1, Color paramColor2, Font paramFont) {
    if (!this.done && paramSurfaceData instanceof D3DSurfaceData.D3DWindowSurfaceData) {
      D3DSurfaceData.D3DWindowSurfaceData d3DWindowSurfaceData = (D3DSurfaceData.D3DWindowSurfaceData)paramSurfaceData;
      if (!d3DWindowSurfaceData.isSurfaceLost() || validate(d3DWindowSurfaceData)) {
        trackScreenSurface(d3DWindowSurfaceData);
        return new SunGraphics2D(paramSurfaceData, paramColor1, paramColor2, paramFont);
      } 
      paramSurfaceData = getGdiSurface(d3DWindowSurfaceData);
    } 
    return super.createGraphics(paramSurfaceData, paramWComponentPeer, paramColor1, paramColor2, paramFont);
  }
  
  private void repaintPeerTarget(WComponentPeer paramWComponentPeer) {
    Component component = (Component)paramWComponentPeer.getTarget();
    Rectangle rectangle = AWTAccessor.getComponentAccessor().getBounds(component);
    paramWComponentPeer.handlePaint(0, 0, rectangle.width, rectangle.height);
  }
  
  private void trackScreenSurface(SurfaceData paramSurfaceData) {
    if (!this.done && paramSurfaceData instanceof D3DSurfaceData.D3DWindowSurfaceData) {
      synchronized (this) {
        if (this.d3dwSurfaces == null)
          this.d3dwSurfaces = new ArrayList(); 
        D3DSurfaceData.D3DWindowSurfaceData d3DWindowSurfaceData = (D3DSurfaceData.D3DWindowSurfaceData)paramSurfaceData;
        if (!this.d3dwSurfaces.contains(d3DWindowSurfaceData))
          this.d3dwSurfaces.add(d3DWindowSurfaceData); 
      } 
      startUpdateThread();
    } 
  }
  
  public void dropScreenSurface(SurfaceData paramSurfaceData) {
    if (this.d3dwSurfaces != null && paramSurfaceData instanceof D3DSurfaceData.D3DWindowSurfaceData) {
      D3DSurfaceData.D3DWindowSurfaceData d3DWindowSurfaceData = (D3DSurfaceData.D3DWindowSurfaceData)paramSurfaceData;
      removeGdiSurface(d3DWindowSurfaceData);
      this.d3dwSurfaces.remove(d3DWindowSurfaceData);
    } 
  }
  
  public SurfaceData getReplacementScreenSurface(WComponentPeer paramWComponentPeer, SurfaceData paramSurfaceData) {
    SurfaceData surfaceData = super.getReplacementScreenSurface(paramWComponentPeer, paramSurfaceData);
    trackScreenSurface(surfaceData);
    return surfaceData;
  }
  
  private void removeGdiSurface(D3DSurfaceData.D3DWindowSurfaceData paramD3DWindowSurfaceData) {
    if (this.gdiSurfaces != null) {
      GDIWindowSurfaceData gDIWindowSurfaceData = (GDIWindowSurfaceData)this.gdiSurfaces.get(paramD3DWindowSurfaceData);
      if (gDIWindowSurfaceData != null) {
        gDIWindowSurfaceData.invalidate();
        this.gdiSurfaces.remove(paramD3DWindowSurfaceData);
      } 
    } 
  }
  
  private void startUpdateThread() {
    if (this.screenUpdater == null) {
      this.screenUpdater = (Thread)AccessController.doPrivileged(() -> {
            ThreadGroup threadGroup = ThreadGroupUtils.getRootThreadGroup();
            Thread thread = new Thread(threadGroup, this, "D3D Screen Updater");
            thread.setPriority(7);
            thread.setDaemon(true);
            return thread;
          });
      this.screenUpdater.start();
    } else {
      wakeUpUpdateThread();
    } 
  }
  
  public void wakeUpUpdateThread() {
    synchronized (this.runLock) {
      this.runLock.notifyAll();
    } 
  }
  
  public void runUpdateNow() {
    synchronized (this) {
      if (this.done || this.screenUpdater == null || this.d3dwSurfaces == null || this.d3dwSurfaces.size() == 0)
        return; 
    } 
    synchronized (this.runLock) {
      this.needsUpdateNow = true;
      this.runLock.notifyAll();
      while (this.needsUpdateNow) {
        try {
          this.runLock.wait();
        } catch (InterruptedException interruptedException) {}
      } 
    } 
  }
  
  public void run() {
    while (!this.done) {
      synchronized (this.runLock) {
        long l = (this.d3dwSurfaces.size() > 0) ? 100L : 0L;
        if (!this.needsUpdateNow)
          try {
            this.runLock.wait(l);
          } catch (InterruptedException interruptedException) {} 
      } 
      D3DWindowSurfaceData[] arrayOfD3DWindowSurfaceData = new D3DSurfaceData.D3DWindowSurfaceData[0];
      synchronized (this) {
        arrayOfD3DWindowSurfaceData = (D3DWindowSurfaceData[])this.d3dwSurfaces.toArray(arrayOfD3DWindowSurfaceData);
      } 
      for (D3DWindowSurfaceData d3DWindowSurfaceData : arrayOfD3DWindowSurfaceData) {
        if (d3DWindowSurfaceData.isValid() && (d3DWindowSurfaceData.isDirty() || d3DWindowSurfaceData.isSurfaceLost()))
          if (!d3DWindowSurfaceData.isSurfaceLost()) {
            d3DRenderQueue = D3DRenderQueue.getInstance();
            d3DRenderQueue.lock();
            try {
              Rectangle rectangle = d3DWindowSurfaceData.getBounds();
              D3DSurfaceData.swapBuffers(d3DWindowSurfaceData, 0, 0, rectangle.width, rectangle.height);
              d3DWindowSurfaceData.markClean();
            } finally {
              d3DRenderQueue.unlock();
            } 
          } else if (!validate(d3DWindowSurfaceData)) {
            d3DWindowSurfaceData.getPeer().replaceSurfaceDataLater();
          }  
      } 
      synchronized (this.runLock) {
        this.needsUpdateNow = false;
        this.runLock.notifyAll();
      } 
    } 
  }
  
  private boolean validate(D3DSurfaceData.D3DWindowSurfaceData paramD3DWindowSurfaceData) {
    if (paramD3DWindowSurfaceData.isSurfaceLost())
      try {
        paramD3DWindowSurfaceData.restoreSurface();
        Color color = paramD3DWindowSurfaceData.getPeer().getBackgroundNoSync();
        SunGraphics2D sunGraphics2D = new SunGraphics2D(paramD3DWindowSurfaceData, color, color, null);
        sunGraphics2D.fillRect(0, 0, (paramD3DWindowSurfaceData.getBounds()).width, (paramD3DWindowSurfaceData.getBounds()).height);
        sunGraphics2D.dispose();
        paramD3DWindowSurfaceData.markClean();
        repaintPeerTarget(paramD3DWindowSurfaceData.getPeer());
      } catch (InvalidPipeException invalidPipeException) {
        return false;
      }  
    return true;
  }
  
  private SurfaceData getGdiSurface(D3DSurfaceData.D3DWindowSurfaceData paramD3DWindowSurfaceData) {
    if (this.gdiSurfaces == null)
      this.gdiSurfaces = new HashMap(); 
    GDIWindowSurfaceData gDIWindowSurfaceData = (GDIWindowSurfaceData)this.gdiSurfaces.get(paramD3DWindowSurfaceData);
    if (gDIWindowSurfaceData == null) {
      gDIWindowSurfaceData = GDIWindowSurfaceData.createData(paramD3DWindowSurfaceData.getPeer());
      this.gdiSurfaces.put(paramD3DWindowSurfaceData, gDIWindowSurfaceData);
    } 
    return gDIWindowSurfaceData;
  }
  
  private static boolean hasHWChildren(Component paramComponent) {
    if (paramComponent instanceof Container)
      for (Component component : ((Container)paramComponent).getComponents()) {
        if (component.getPeer() instanceof WComponentPeer || hasHWChildren(component))
          return true; 
      }  
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\d3d\D3DScreenUpdateManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */