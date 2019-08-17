package sun.awt.windows;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.VolatileImage;
import java.security.AccessController;
import sun.awt.image.BufImgSurfaceData;
import sun.java2d.DestSurfaceProvider;
import sun.java2d.InvalidPipeException;
import sun.java2d.Surface;
import sun.java2d.d3d.D3DSurfaceData;
import sun.java2d.opengl.WGLSurfaceData;
import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.hw.AccelGraphicsConfig;
import sun.java2d.pipe.hw.AccelSurface;
import sun.security.action.GetPropertyAction;

abstract class TranslucentWindowPainter {
  protected Window window;
  
  protected WWindowPeer peer;
  
  private static final boolean forceOpt = Boolean.valueOf((String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.twp.forceopt", "false"))).booleanValue();
  
  private static final boolean forceSW = Boolean.valueOf((String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.twp.forcesw", "false"))).booleanValue();
  
  public static TranslucentWindowPainter createInstance(WWindowPeer paramWWindowPeer) {
    GraphicsConfiguration graphicsConfiguration = paramWWindowPeer.getGraphicsConfiguration();
    if (!forceSW && graphicsConfiguration instanceof AccelGraphicsConfig) {
      String str = graphicsConfiguration.getClass().getSimpleName();
      AccelGraphicsConfig accelGraphicsConfig = (AccelGraphicsConfig)graphicsConfiguration;
      if ((accelGraphicsConfig.getContextCapabilities().getCaps() & 0x100) != 0 || forceOpt) {
        if (str.startsWith("D3D"))
          return new VIOptD3DWindowPainter(paramWWindowPeer); 
        if (forceOpt && str.startsWith("WGL"))
          return new VIOptWGLWindowPainter(paramWWindowPeer); 
      } 
    } 
    return new BIWindowPainter(paramWWindowPeer);
  }
  
  protected TranslucentWindowPainter(WWindowPeer paramWWindowPeer) {
    this.peer = paramWWindowPeer;
    this.window = (Window)paramWWindowPeer.getTarget();
  }
  
  protected abstract Image getBackBuffer(boolean paramBoolean);
  
  protected abstract boolean update(Image paramImage);
  
  public abstract void flush();
  
  public void updateWindow(boolean paramBoolean) {
    boolean bool = false;
    Image image = getBackBuffer(paramBoolean);
    while (!bool) {
      if (paramBoolean) {
        graphics2D = (Graphics2D)image.getGraphics();
        try {
          this.window.paintAll(graphics2D);
        } finally {
          graphics2D.dispose();
        } 
      } 
      bool = update(image);
      if (!bool) {
        paramBoolean = true;
        image = getBackBuffer(true);
      } 
    } 
  }
  
  private static final Image clearImage(Image paramImage) {
    Graphics2D graphics2D = (Graphics2D)paramImage.getGraphics();
    int i = paramImage.getWidth(null);
    int j = paramImage.getHeight(null);
    graphics2D.setComposite(AlphaComposite.Src);
    graphics2D.setColor(new Color(0, 0, 0, 0));
    graphics2D.fillRect(0, 0, i, j);
    return paramImage;
  }
  
  private static class BIWindowPainter extends TranslucentWindowPainter {
    private BufferedImage backBuffer;
    
    protected BIWindowPainter(WWindowPeer param1WWindowPeer) { super(param1WWindowPeer); }
    
    protected Image getBackBuffer(boolean param1Boolean) {
      int i = this.window.getWidth();
      int j = this.window.getHeight();
      if (this.backBuffer == null || this.backBuffer.getWidth() != i || this.backBuffer.getHeight() != j) {
        flush();
        this.backBuffer = new BufferedImage(i, j, 3);
      } 
      return param1Boolean ? (BufferedImage)TranslucentWindowPainter.clearImage(this.backBuffer) : this.backBuffer;
    }
    
    protected boolean update(Image param1Image) {
      VolatileImage volatileImage = null;
      if (param1Image instanceof BufferedImage) {
        BufferedImage bufferedImage1 = (BufferedImage)param1Image;
        int[] arrayOfInt1 = ((DataBufferInt)bufferedImage1.getRaster().getDataBuffer()).getData();
        this.peer.updateWindowImpl(arrayOfInt1, bufferedImage1.getWidth(), bufferedImage1.getHeight());
        return true;
      } 
      if (param1Image instanceof VolatileImage) {
        volatileImage = (VolatileImage)param1Image;
        if (param1Image instanceof DestSurfaceProvider) {
          Surface surface = ((DestSurfaceProvider)param1Image).getDestSurface();
          if (surface instanceof BufImgSurfaceData) {
            int i = volatileImage.getWidth();
            int j = volatileImage.getHeight();
            BufImgSurfaceData bufImgSurfaceData = (BufImgSurfaceData)surface;
            int[] arrayOfInt1 = ((DataBufferInt)bufImgSurfaceData.getRaster(0, 0, i, j).getDataBuffer()).getData();
            this.peer.updateWindowImpl(arrayOfInt1, i, j);
            return true;
          } 
        } 
      } 
      BufferedImage bufferedImage = (BufferedImage)TranslucentWindowPainter.clearImage(this.backBuffer);
      int[] arrayOfInt = ((DataBufferInt)bufferedImage.getRaster().getDataBuffer()).getData();
      this.peer.updateWindowImpl(arrayOfInt, bufferedImage.getWidth(), bufferedImage.getHeight());
      return (volatileImage != null) ? (!volatileImage.contentsLost()) : true;
    }
    
    public void flush() {
      if (this.backBuffer != null) {
        this.backBuffer.flush();
        this.backBuffer = null;
      } 
    }
  }
  
  private static class VIOptD3DWindowPainter extends VIOptWindowPainter {
    protected VIOptD3DWindowPainter(WWindowPeer param1WWindowPeer) { super(param1WWindowPeer); }
    
    protected boolean updateWindowAccel(long param1Long, int param1Int1, int param1Int2) { return D3DSurfaceData.updateWindowAccelImpl(param1Long, this.peer.getData(), param1Int1, param1Int2); }
  }
  
  private static class VIOptWGLWindowPainter extends VIOptWindowPainter {
    protected VIOptWGLWindowPainter(WWindowPeer param1WWindowPeer) { super(param1WWindowPeer); }
    
    protected boolean updateWindowAccel(long param1Long, int param1Int1, int param1Int2) { return WGLSurfaceData.updateWindowAccelImpl(param1Long, this.peer, param1Int1, param1Int2); }
  }
  
  private static abstract class VIOptWindowPainter extends VIWindowPainter {
    protected VIOptWindowPainter(WWindowPeer param1WWindowPeer) { super(param1WWindowPeer); }
    
    protected abstract boolean updateWindowAccel(long param1Long, int param1Int1, int param1Int2);
    
    protected boolean update(Image param1Image) {
      if (param1Image instanceof DestSurfaceProvider) {
        Surface surface = ((DestSurfaceProvider)param1Image).getDestSurface();
        if (surface instanceof AccelSurface) {
          final int w = param1Image.getWidth(null);
          final int h = param1Image.getHeight(null);
          final boolean[] arr = { false };
          final AccelSurface as = (AccelSurface)surface;
          renderQueue = accelSurface.getContext().getRenderQueue();
          renderQueue.lock();
          try {
            BufferedContext.validateContext(accelSurface);
            renderQueue.flushAndInvokeNow(new Runnable() {
                  public void run() {
                    long l = as.getNativeOps();
                    arr[0] = TranslucentWindowPainter.VIOptWindowPainter.this.updateWindowAccel(l, w, h);
                  }
                });
          } catch (InvalidPipeException invalidPipeException) {
          
          } finally {
            renderQueue.unlock();
          } 
          return arrayOfBoolean[0];
        } 
      } 
      return super.update(param1Image);
    }
  }
  
  private static class VIWindowPainter extends BIWindowPainter {
    private VolatileImage viBB;
    
    protected VIWindowPainter(WWindowPeer param1WWindowPeer) { super(param1WWindowPeer); }
    
    protected Image getBackBuffer(boolean param1Boolean) {
      int i = this.window.getWidth();
      int j = this.window.getHeight();
      GraphicsConfiguration graphicsConfiguration = this.peer.getGraphicsConfiguration();
      if (this.viBB == null || this.viBB.getWidth() != i || this.viBB.getHeight() != j || this.viBB.validate(graphicsConfiguration) == 2) {
        flush();
        if (graphicsConfiguration instanceof AccelGraphicsConfig) {
          AccelGraphicsConfig accelGraphicsConfig = (AccelGraphicsConfig)graphicsConfiguration;
          this.viBB = accelGraphicsConfig.createCompatibleVolatileImage(i, j, 3, 2);
        } 
        if (this.viBB == null)
          this.viBB = graphicsConfiguration.createCompatibleVolatileImage(i, j, 3); 
        this.viBB.validate(graphicsConfiguration);
      } 
      return param1Boolean ? TranslucentWindowPainter.clearImage(this.viBB) : this.viBB;
    }
    
    public void flush() {
      if (this.viBB != null) {
        this.viBB.flush();
        this.viBB = null;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\TranslucentWindowPainter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */