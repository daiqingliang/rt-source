package sun.java2d.d3d;

import java.awt.AlphaComposite;
import java.awt.BufferCapabilities;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import sun.awt.SunToolkit;
import sun.awt.image.DataBufferNative;
import sun.awt.image.PixelConverter;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.SurfaceManager;
import sun.awt.image.WritableRasterNative;
import sun.awt.windows.WComponentPeer;
import sun.java2d.InvalidPipeException;
import sun.java2d.ScreenUpdateManager;
import sun.java2d.StateTracker;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.MaskFill;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.ParallelogramPipe;
import sun.java2d.pipe.PixelToParallelogramConverter;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.TextPipe;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.pipe.hw.ExtendedBufferCapabilities;

public class D3DSurfaceData extends SurfaceData implements AccelSurface {
  public static final int D3D_DEVICE_RESOURCE = 100;
  
  public static final int ST_INT_ARGB = 0;
  
  public static final int ST_INT_ARGB_PRE = 1;
  
  public static final int ST_INT_ARGB_BM = 2;
  
  public static final int ST_INT_RGB = 3;
  
  public static final int ST_INT_BGR = 4;
  
  public static final int ST_USHORT_565_RGB = 5;
  
  public static final int ST_USHORT_555_RGB = 6;
  
  public static final int ST_BYTE_INDEXED = 7;
  
  public static final int ST_BYTE_INDEXED_BM = 8;
  
  public static final int ST_3BYTE_BGR = 9;
  
  public static final int SWAP_DISCARD = 1;
  
  public static final int SWAP_FLIP = 2;
  
  public static final int SWAP_COPY = 3;
  
  private static final String DESC_D3D_SURFACE = "D3D Surface";
  
  private static final String DESC_D3D_SURFACE_RTT = "D3D Surface (render-to-texture)";
  
  private static final String DESC_D3D_TEXTURE = "D3D Texture";
  
  static final SurfaceType D3DSurface = SurfaceType.Any.deriveSubType("D3D Surface", PixelConverter.ArgbPre.instance);
  
  static final SurfaceType D3DSurfaceRTT = D3DSurface.deriveSubType("D3D Surface (render-to-texture)");
  
  static final SurfaceType D3DTexture = SurfaceType.Any.deriveSubType("D3D Texture");
  
  private int type;
  
  private int width;
  
  private int height;
  
  private int nativeWidth;
  
  private int nativeHeight;
  
  protected WComponentPeer peer;
  
  private Image offscreenImage;
  
  protected D3DGraphicsDevice graphicsDevice;
  
  private int swapEffect;
  
  private ExtendedBufferCapabilities.VSyncType syncType;
  
  private int backBuffersNum;
  
  private WritableRasterNative wrn;
  
  protected static D3DRenderer d3dRenderPipe;
  
  protected static PixelToParallelogramConverter d3dTxRenderPipe;
  
  protected static ParallelogramPipe d3dAAPgramPipe;
  
  protected static D3DTextRenderer d3dTextPipe;
  
  protected static D3DDrawImage d3dImagePipe;
  
  private native boolean initTexture(long paramLong, boolean paramBoolean1, boolean paramBoolean2);
  
  private native boolean initFlipBackbuffer(long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3);
  
  private native boolean initRTSurface(long paramLong, boolean paramBoolean);
  
  private native void initOps(int paramInt1, int paramInt2, int paramInt3);
  
  protected D3DSurfaceData(WComponentPeer paramWComponentPeer, D3DGraphicsConfig paramD3DGraphicsConfig, int paramInt1, int paramInt2, Image paramImage, ColorModel paramColorModel, int paramInt3, int paramInt4, ExtendedBufferCapabilities.VSyncType paramVSyncType, int paramInt5) {
    super(getCustomSurfaceType(paramInt5), paramColorModel);
    this.graphicsDevice = paramD3DGraphicsConfig.getD3DDevice();
    this.peer = paramWComponentPeer;
    this.type = paramInt5;
    this.width = paramInt1;
    this.height = paramInt2;
    this.offscreenImage = paramImage;
    this.backBuffersNum = paramInt3;
    this.swapEffect = paramInt4;
    this.syncType = paramVSyncType;
    initOps(this.graphicsDevice.getScreen(), paramInt1, paramInt2);
    if (paramInt5 == 1) {
      setSurfaceLost(true);
    } else {
      initSurface();
    } 
    setBlitProxyKey(paramD3DGraphicsConfig.getProxyKey());
  }
  
  public SurfaceDataProxy makeProxyFor(SurfaceData paramSurfaceData) { return D3DSurfaceDataProxy.createProxy(paramSurfaceData, (D3DGraphicsConfig)this.graphicsDevice.getDefaultConfiguration()); }
  
  public static D3DSurfaceData createData(WComponentPeer paramWComponentPeer, Image paramImage) {
    byte b;
    D3DGraphicsConfig d3DGraphicsConfig = getGC(paramWComponentPeer);
    if (d3DGraphicsConfig == null || !paramWComponentPeer.isAccelCapable())
      return null; 
    BufferCapabilities bufferCapabilities = paramWComponentPeer.getBackBufferCaps();
    ExtendedBufferCapabilities.VSyncType vSyncType = ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT;
    if (bufferCapabilities instanceof ExtendedBufferCapabilities)
      vSyncType = ((ExtendedBufferCapabilities)bufferCapabilities).getVSync(); 
    Rectangle rectangle = paramWComponentPeer.getBounds();
    BufferCapabilities.FlipContents flipContents = bufferCapabilities.getFlipContents();
    if (flipContents == BufferCapabilities.FlipContents.COPIED) {
      b = 3;
    } else if (flipContents == BufferCapabilities.FlipContents.PRIOR) {
      b = 2;
    } else {
      b = 1;
    } 
    return new D3DSurfaceData(paramWComponentPeer, d3DGraphicsConfig, rectangle.width, rectangle.height, paramImage, paramWComponentPeer.getColorModel(), paramWComponentPeer.getBackBuffersNum(), b, vSyncType, 4);
  }
  
  public static D3DSurfaceData createData(WComponentPeer paramWComponentPeer) {
    D3DGraphicsConfig d3DGraphicsConfig = getGC(paramWComponentPeer);
    return (d3DGraphicsConfig == null || !paramWComponentPeer.isAccelCapable()) ? null : new D3DWindowSurfaceData(paramWComponentPeer, d3DGraphicsConfig);
  }
  
  public static D3DSurfaceData createData(D3DGraphicsConfig paramD3DGraphicsConfig, int paramInt1, int paramInt2, ColorModel paramColorModel, Image paramImage, int paramInt3) {
    if (paramInt3 == 5) {
      boolean bool = (paramColorModel.getTransparency() == 1) ? 1 : 0;
      byte b = bool ? 8 : 4;
      if (!paramD3DGraphicsConfig.getD3DDevice().isCapPresent(b))
        paramInt3 = 2; 
    } 
    D3DSurfaceData d3DSurfaceData = null;
    try {
      d3DSurfaceData = new D3DSurfaceData(null, paramD3DGraphicsConfig, paramInt1, paramInt2, paramImage, paramColorModel, 0, 1, ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT, paramInt3);
    } catch (InvalidPipeException invalidPipeException) {
      if (paramInt3 == 5 && ((SunVolatileImage)paramImage).getForcedAccelSurfaceType() != 5) {
        paramInt3 = 2;
        d3DSurfaceData = new D3DSurfaceData(null, paramD3DGraphicsConfig, paramInt1, paramInt2, paramImage, paramColorModel, 0, 1, ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT, paramInt3);
      } 
    } 
    return d3DSurfaceData;
  }
  
  private static SurfaceType getCustomSurfaceType(int paramInt) {
    switch (paramInt) {
      case 3:
        return D3DTexture;
      case 5:
        return D3DSurfaceRTT;
    } 
    return D3DSurface;
  }
  
  private boolean initSurfaceNow() {
    boolean bool = (getTransparency() == 1);
    switch (this.type) {
      case 2:
        return initRTSurface(getNativeOps(), bool);
      case 3:
        return initTexture(getNativeOps(), false, bool);
      case 5:
        return initTexture(getNativeOps(), true, bool);
      case 1:
      case 4:
        return initFlipBackbuffer(getNativeOps(), this.peer.getData(), this.backBuffersNum, this.swapEffect, this.syncType.id());
    } 
    return false;
  }
  
  protected void initSurface() {
    synchronized (this) {
      this.wrn = null;
    } 
    class Status {
      boolean success = false;
    };
    final Status status = new Status();
    d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      d3DRenderQueue.flushAndInvokeNow(new Runnable() {
            public void run() { this.val$status.success = D3DSurfaceData.this.initSurfaceNow(); }
          });
      if (!status.success)
        throw new InvalidPipeException("Error creating D3DSurface"); 
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
  
  public final D3DContext getContext() { return this.graphicsDevice.getContext(); }
  
  public final int getType() { return this.type; }
  
  private static native int dbGetPixelNative(long paramLong, int paramInt1, int paramInt2);
  
  private static native void dbSetPixelNative(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  public Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (this.wrn == null) {
      DirectColorModel directColorModel = (DirectColorModel)getColorModel();
      byte b = 0;
      int i = this.width;
      if (directColorModel.getPixelSize() > 16) {
        b = 3;
      } else {
        b = 1;
      } 
      SinglePixelPackedSampleModel singlePixelPackedSampleModel = new SinglePixelPackedSampleModel(b, this.width, this.height, i, directColorModel.getMasks());
      D3DDataBufferNative d3DDataBufferNative = new D3DDataBufferNative(this, b, this.width, this.height);
      this.wrn = WritableRasterNative.createNativeRaster(singlePixelPackedSampleModel, d3DDataBufferNative);
    } 
    return this.wrn;
  }
  
  public boolean canRenderLCDText(SunGraphics2D paramSunGraphics2D) { return (this.graphicsDevice.isCapPresent(65536) && paramSunGraphics2D.compositeState <= 0 && paramSunGraphics2D.paintState <= 0 && paramSunGraphics2D.surfaceData.getTransparency() == 1); }
  
  void disableAccelerationForSurface() {
    if (this.offscreenImage != null) {
      SurfaceManager surfaceManager = SurfaceManager.getManager(this.offscreenImage);
      if (surfaceManager instanceof D3DVolatileSurfaceManager) {
        setSurfaceLost(true);
        ((D3DVolatileSurfaceManager)surfaceManager).setAccelerationEnabled(false);
      } 
    } 
  }
  
  public void validatePipe(SunGraphics2D paramSunGraphics2D) {
    TextPipe textPipe;
    boolean bool = false;
    paramSunGraphics2D;
    if (paramSunGraphics2D.compositeState >= 2) {
      super.validatePipe(paramSunGraphics2D);
      paramSunGraphics2D.imagepipe = d3dImagePipe;
      disableAccelerationForSurface();
      return;
    } 
    paramSunGraphics2D;
    paramSunGraphics2D;
    paramSunGraphics2D;
    paramSunGraphics2D;
    paramSunGraphics2D;
    paramSunGraphics2D;
    if ((paramSunGraphics2D.compositeState <= 0 && paramSunGraphics2D.paintState <= 1) || (paramSunGraphics2D.compositeState == 1 && paramSunGraphics2D.paintState <= 1 && ((AlphaComposite)paramSunGraphics2D.composite).getRule() == 3) || (paramSunGraphics2D.compositeState == 2 && paramSunGraphics2D.paintState <= 1)) {
      textPipe = d3dTextPipe;
    } else {
      super.validatePipe(paramSunGraphics2D);
      textPipe = paramSunGraphics2D.textpipe;
      bool = true;
    } 
    PixelToParallelogramConverter pixelToParallelogramConverter = null;
    D3DRenderer d3DRenderer = null;
    if (paramSunGraphics2D.antialiasHint != 2) {
      paramSunGraphics2D;
      if (paramSunGraphics2D.paintState <= 1) {
        paramSunGraphics2D;
        if (paramSunGraphics2D.compositeState <= 2) {
          pixelToParallelogramConverter = d3dTxRenderPipe;
          d3DRenderer = d3dRenderPipe;
        } 
      } else {
        paramSunGraphics2D;
        if (paramSunGraphics2D.compositeState <= 1 && D3DPaints.isValid(paramSunGraphics2D)) {
          pixelToParallelogramConverter = d3dTxRenderPipe;
          d3DRenderer = d3dRenderPipe;
        } 
      } 
    } else {
      paramSunGraphics2D;
      if (paramSunGraphics2D.paintState <= 1)
        if (this.graphicsDevice.isCapPresent(524288) && (paramSunGraphics2D.imageComp == CompositeType.SrcOverNoEa || paramSunGraphics2D.imageComp == CompositeType.SrcOver)) {
          if (!bool) {
            super.validatePipe(paramSunGraphics2D);
            bool = true;
          } 
          PixelToParallelogramConverter pixelToParallelogramConverter1 = new PixelToParallelogramConverter(paramSunGraphics2D.shapepipe, d3dAAPgramPipe, 0.125D, 0.499D, false);
          paramSunGraphics2D.drawpipe = pixelToParallelogramConverter1;
          paramSunGraphics2D.fillpipe = pixelToParallelogramConverter1;
          paramSunGraphics2D.shapepipe = pixelToParallelogramConverter1;
        } else {
          paramSunGraphics2D;
          if (paramSunGraphics2D.compositeState == 2) {
            pixelToParallelogramConverter = d3dTxRenderPipe;
            d3DRenderer = d3dRenderPipe;
          } 
        }  
    } 
    if (pixelToParallelogramConverter != null) {
      paramSunGraphics2D;
      if (paramSunGraphics2D.transformState >= 3) {
        paramSunGraphics2D.drawpipe = pixelToParallelogramConverter;
        paramSunGraphics2D.fillpipe = pixelToParallelogramConverter;
      } else {
        paramSunGraphics2D;
        if (paramSunGraphics2D.strokeState != 0) {
          paramSunGraphics2D.drawpipe = pixelToParallelogramConverter;
          paramSunGraphics2D.fillpipe = d3DRenderer;
        } else {
          paramSunGraphics2D.drawpipe = d3DRenderer;
          paramSunGraphics2D.fillpipe = d3DRenderer;
        } 
      } 
      paramSunGraphics2D.shapepipe = pixelToParallelogramConverter;
    } else if (!bool) {
      super.validatePipe(paramSunGraphics2D);
    } 
    paramSunGraphics2D.textpipe = textPipe;
    paramSunGraphics2D.imagepipe = d3dImagePipe;
  }
  
  protected MaskFill getMaskFill(SunGraphics2D paramSunGraphics2D) {
    paramSunGraphics2D;
    return (paramSunGraphics2D.paintState > 1 && (!D3DPaints.isValid(paramSunGraphics2D) || !this.graphicsDevice.isCapPresent(16))) ? null : super.getMaskFill(paramSunGraphics2D);
  }
  
  public boolean copyArea(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    paramSunGraphics2D;
    paramSunGraphics2D;
    if (paramSunGraphics2D.transformState < 3 && paramSunGraphics2D.compositeState < 2) {
      paramInt1 += paramSunGraphics2D.transX;
      paramInt2 += paramSunGraphics2D.transY;
      d3dRenderPipe.copyArea(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      return true;
    } 
    return false;
  }
  
  public void flush() {
    d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      RenderBuffer renderBuffer = d3DRenderQueue.getBuffer();
      d3DRenderQueue.ensureCapacityAndAlignment(12, 4);
      renderBuffer.putInt(72);
      renderBuffer.putLong(getNativeOps());
      d3DRenderQueue.flushNow();
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
  
  static void dispose(long paramLong) {
    d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      RenderBuffer renderBuffer = d3DRenderQueue.getBuffer();
      d3DRenderQueue.ensureCapacityAndAlignment(12, 4);
      renderBuffer.putInt(73);
      renderBuffer.putLong(paramLong);
      d3DRenderQueue.flushNow();
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
  
  static void swapBuffers(D3DSurfaceData paramD3DSurfaceData, final int x1, final int y1, final int x2, final int y2) {
    long l = paramD3DSurfaceData.getNativeOps();
    d3DRenderQueue = D3DRenderQueue.getInstance();
    if (d3DRenderQueue.isRenderQueueThread()) {
      if (!d3DRenderQueue.tryLock()) {
        final Component target = (Component)paramD3DSurfaceData.getPeer().getTarget();
        SunToolkit.executeOnEventHandlerThread(component, new Runnable() {
              public void run() { target.repaint(x1, y1, x2, y2); }
            });
        return;
      } 
    } else {
      d3DRenderQueue.lock();
    } 
    try {
      RenderBuffer renderBuffer = d3DRenderQueue.getBuffer();
      d3DRenderQueue.ensureCapacityAndAlignment(28, 4);
      renderBuffer.putInt(80);
      renderBuffer.putLong(l);
      renderBuffer.putInt(paramInt1);
      renderBuffer.putInt(paramInt2);
      renderBuffer.putInt(paramInt3);
      renderBuffer.putInt(paramInt4);
      d3DRenderQueue.flushNow();
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
  
  public Object getDestination() { return this.offscreenImage; }
  
  public Rectangle getBounds() {
    if (this.type == 4 || this.type == 1) {
      Rectangle rectangle = this.peer.getBounds();
      rectangle.x = rectangle.y = 0;
      return rectangle;
    } 
    return new Rectangle(this.width, this.height);
  }
  
  public Rectangle getNativeBounds() {
    d3DRenderQueue = D3DRenderQueue.getInstance();
    d3DRenderQueue.lock();
    try {
      return new Rectangle(this.nativeWidth, this.nativeHeight);
    } finally {
      d3DRenderQueue.unlock();
    } 
  }
  
  public GraphicsConfiguration getDeviceConfiguration() { return this.graphicsDevice.getDefaultConfiguration(); }
  
  public SurfaceData getReplacement() { return restoreContents(this.offscreenImage); }
  
  private static D3DGraphicsConfig getGC(WComponentPeer paramWComponentPeer) {
    GraphicsConfiguration graphicsConfiguration;
    if (paramWComponentPeer != null) {
      graphicsConfiguration = paramWComponentPeer.getGraphicsConfiguration();
    } else {
      GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice graphicsDevice1 = graphicsEnvironment.getDefaultScreenDevice();
      graphicsConfiguration = graphicsDevice1.getDefaultConfiguration();
    } 
    return (graphicsConfiguration instanceof D3DGraphicsConfig) ? (D3DGraphicsConfig)graphicsConfiguration : null;
  }
  
  void restoreSurface() { initSurface(); }
  
  WComponentPeer getPeer() { return this.peer; }
  
  public void setSurfaceLost(boolean paramBoolean) {
    super.setSurfaceLost(paramBoolean);
    if (paramBoolean && this.offscreenImage != null) {
      SurfaceManager surfaceManager = SurfaceManager.getManager(this.offscreenImage);
      surfaceManager.acceleratedSurfaceLost();
    } 
  }
  
  private static native long getNativeResourceNative(long paramLong, int paramInt);
  
  public long getNativeResource(int paramInt) { return getNativeResourceNative(getNativeOps(), paramInt); }
  
  public static native boolean updateWindowAccelImpl(long paramLong1, long paramLong2, int paramInt1, int paramInt2);
  
  static  {
    D3DRenderQueue d3DRenderQueue = D3DRenderQueue.getInstance();
    d3dImagePipe = new D3DDrawImage();
    d3dTextPipe = new D3DTextRenderer(d3DRenderQueue);
    d3dRenderPipe = new D3DRenderer(d3DRenderQueue);
    if (GraphicsPrimitive.tracingEnabled()) {
      d3dTextPipe = d3dTextPipe.traceWrap();
      d3dRenderPipe = d3dRenderPipe.traceWrap();
    } 
    d3dAAPgramPipe = d3dRenderPipe.getAAParallelogramPipe();
    d3dTxRenderPipe = new PixelToParallelogramConverter(d3dRenderPipe, d3dRenderPipe, 1.0D, 0.25D, true);
    D3DBlitLoops.register();
    D3DMaskFill.register();
    D3DMaskBlit.register();
  }
  
  static class D3DDataBufferNative extends DataBufferNative {
    int pixel;
    
    protected D3DDataBufferNative(SurfaceData param1SurfaceData, int param1Int1, int param1Int2, int param1Int3) { super(param1SurfaceData, param1Int1, param1Int2, param1Int3); }
    
    protected int getElem(final int x, final int y, final SurfaceData sData) {
      if (param1SurfaceData.isSurfaceLost())
        return 0; 
      d3DRenderQueue = D3DRenderQueue.getInstance();
      d3DRenderQueue.lock();
      try {
        d3DRenderQueue.flushAndInvokeNow(new Runnable() {
              public void run() { D3DSurfaceData.D3DDataBufferNative.this.pixel = D3DSurfaceData.dbGetPixelNative(sData.getNativeOps(), x, y); }
            });
      } finally {
        i = this.pixel;
        d3DRenderQueue.unlock();
      } 
      return i;
    }
    
    protected void setElem(final int x, final int y, final int pixel, final SurfaceData sData) {
      if (param1SurfaceData.isSurfaceLost())
        return; 
      d3DRenderQueue = D3DRenderQueue.getInstance();
      d3DRenderQueue.lock();
      try {
        d3DRenderQueue.flushAndInvokeNow(new Runnable() {
              public void run() { D3DSurfaceData.dbSetPixelNative(sData.getNativeOps(), x, y, pixel); }
            });
        param1SurfaceData.markDirty();
      } finally {
        d3DRenderQueue.unlock();
      } 
    }
  }
  
  public static class D3DWindowSurfaceData extends D3DSurfaceData {
    StateTracker dirtyTracker = getStateTracker();
    
    public D3DWindowSurfaceData(WComponentPeer param1WComponentPeer, D3DGraphicsConfig param1D3DGraphicsConfig) { super(param1WComponentPeer, param1D3DGraphicsConfig, (param1WComponentPeer.getBounds()).width, (param1WComponentPeer.getBounds()).height, null, param1WComponentPeer.getColorModel(), 1, 3, ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT, 1); }
    
    public SurfaceData getReplacement() {
      ScreenUpdateManager screenUpdateManager = ScreenUpdateManager.getInstance();
      return screenUpdateManager.getReplacementScreenSurface(this.peer, this);
    }
    
    public Object getDestination() { return this.peer.getTarget(); }
    
    void disableAccelerationForSurface() {
      setSurfaceLost(true);
      invalidate();
      flush();
      this.peer.disableAcceleration();
      ScreenUpdateManager.getInstance().dropScreenSurface(this);
    }
    
    void restoreSurface() {
      if (!this.peer.isAccelCapable())
        throw new InvalidPipeException("Onscreen acceleration disabled for this surface"); 
      Window window = this.graphicsDevice.getFullScreenWindow();
      if (window != null && window != this.peer.getTarget())
        throw new InvalidPipeException("Can't restore onscreen surface when in full-screen mode"); 
      super.restoreSurface();
      setSurfaceLost(false);
      d3DRenderQueue = D3DRenderQueue.getInstance();
      d3DRenderQueue.lock();
      try {
        getContext().invalidateContext();
      } finally {
        d3DRenderQueue.unlock();
      } 
    }
    
    public boolean isDirty() { return !this.dirtyTracker.isCurrent(); }
    
    public void markClean() { this.dirtyTracker = getStateTracker(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\d3d\D3DSurfaceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */