package sun.java2d.opengl;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.security.AccessController;
import sun.awt.image.PixelConverter;
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
import sun.security.action.GetPropertyAction;

public abstract class OGLSurfaceData extends SurfaceData implements AccelSurface {
  public static final int PBUFFER = 2;
  
  public static final int FBOBJECT = 5;
  
  public static final int PF_INT_ARGB = 0;
  
  public static final int PF_INT_ARGB_PRE = 1;
  
  public static final int PF_INT_RGB = 2;
  
  public static final int PF_INT_RGBX = 3;
  
  public static final int PF_INT_BGR = 4;
  
  public static final int PF_INT_BGRX = 5;
  
  public static final int PF_USHORT_565_RGB = 6;
  
  public static final int PF_USHORT_555_RGB = 7;
  
  public static final int PF_USHORT_555_RGBX = 8;
  
  public static final int PF_BYTE_GRAY = 9;
  
  public static final int PF_USHORT_GRAY = 10;
  
  public static final int PF_3BYTE_BGR = 11;
  
  private static final String DESC_OPENGL_SURFACE = "OpenGL Surface";
  
  private static final String DESC_OPENGL_SURFACE_RTT = "OpenGL Surface (render-to-texture)";
  
  private static final String DESC_OPENGL_TEXTURE = "OpenGL Texture";
  
  static final SurfaceType OpenGLSurface = SurfaceType.Any.deriveSubType("OpenGL Surface", PixelConverter.ArgbPre.instance);
  
  static final SurfaceType OpenGLSurfaceRTT = OpenGLSurface.deriveSubType("OpenGL Surface (render-to-texture)");
  
  static final SurfaceType OpenGLTexture = SurfaceType.Any.deriveSubType("OpenGL Texture");
  
  private static boolean isFBObjectEnabled;
  
  private static boolean isLCDShaderEnabled;
  
  private static boolean isBIOpShaderEnabled;
  
  private static boolean isGradShaderEnabled;
  
  private OGLGraphicsConfig graphicsConfig;
  
  protected int type;
  
  private int nativeWidth;
  
  private int nativeHeight;
  
  protected static OGLRenderer oglRenderPipe;
  
  protected static PixelToParallelogramConverter oglTxRenderPipe;
  
  protected static ParallelogramPipe oglAAPgramPipe;
  
  protected static OGLTextRenderer oglTextPipe;
  
  protected static OGLDrawImage oglImagePipe;
  
  protected native boolean initTexture(long paramLong, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt1, int paramInt2);
  
  protected native boolean initFBObject(long paramLong, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt1, int paramInt2);
  
  protected native boolean initFlipBackbuffer(long paramLong);
  
  protected abstract boolean initPbuffer(long paramLong1, long paramLong2, boolean paramBoolean, int paramInt1, int paramInt2);
  
  private native int getTextureTarget(long paramLong);
  
  private native int getTextureID(long paramLong);
  
  protected OGLSurfaceData(OGLGraphicsConfig paramOGLGraphicsConfig, ColorModel paramColorModel, int paramInt) {
    super(getCustomSurfaceType(paramInt), paramColorModel);
    this.graphicsConfig = paramOGLGraphicsConfig;
    this.type = paramInt;
    setBlitProxyKey(paramOGLGraphicsConfig.getProxyKey());
  }
  
  public SurfaceDataProxy makeProxyFor(SurfaceData paramSurfaceData) { return OGLSurfaceDataProxy.createProxy(paramSurfaceData, this.graphicsConfig); }
  
  private static SurfaceType getCustomSurfaceType(int paramInt) {
    switch (paramInt) {
      case 3:
        return OpenGLTexture;
      case 5:
        return OpenGLSurfaceRTT;
    } 
    return OpenGLSurface;
  }
  
  private void initSurfaceNow(int paramInt1, int paramInt2) {
    boolean bool1 = (getTransparency() == 1);
    boolean bool2 = false;
    switch (this.type) {
      case 2:
        bool2 = initPbuffer(getNativeOps(), this.graphicsConfig.getNativeConfigInfo(), bool1, paramInt1, paramInt2);
        break;
      case 3:
        bool2 = initTexture(getNativeOps(), bool1, isTexNonPow2Available(), isTexRectAvailable(), paramInt1, paramInt2);
        break;
      case 5:
        bool2 = initFBObject(getNativeOps(), bool1, isTexNonPow2Available(), isTexRectAvailable(), paramInt1, paramInt2);
        break;
      case 4:
        bool2 = initFlipBackbuffer(getNativeOps());
        break;
    } 
    if (!bool2)
      throw new OutOfMemoryError("can't create offscreen surface"); 
  }
  
  protected void initSurface(final int width, final int height) {
    oGLRenderQueue = OGLRenderQueue.getInstance();
    oGLRenderQueue.lock();
    try {
      switch (this.type) {
        case 2:
        case 3:
        case 5:
          OGLContext.setScratchSurface(this.graphicsConfig);
          break;
      } 
      oGLRenderQueue.flushAndInvokeNow(new Runnable() {
            public void run() { OGLSurfaceData.this.initSurfaceNow(width, height); }
          });
    } finally {
      oGLRenderQueue.unlock();
    } 
  }
  
  public final OGLContext getContext() { return this.graphicsConfig.getContext(); }
  
  final OGLGraphicsConfig getOGLGraphicsConfig() { return this.graphicsConfig; }
  
  public final int getType() { return this.type; }
  
  public final int getTextureTarget() { return getTextureTarget(getNativeOps()); }
  
  public final int getTextureID() { return getTextureID(getNativeOps()); }
  
  public long getNativeResource(int paramInt) { return (paramInt == 3) ? getTextureID() : 0L; }
  
  public Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { throw new InternalError("not implemented yet"); }
  
  public boolean canRenderLCDText(SunGraphics2D paramSunGraphics2D) { return (this.graphicsConfig.isCapPresent(131072) && paramSunGraphics2D.surfaceData.getTransparency() == 1 && paramSunGraphics2D.paintState <= 0 && (paramSunGraphics2D.compositeState <= 0 || (paramSunGraphics2D.compositeState <= 1 && canHandleComposite(paramSunGraphics2D.composite)))); }
  
  private boolean canHandleComposite(Composite paramComposite) {
    if (paramComposite instanceof AlphaComposite) {
      AlphaComposite alphaComposite = (AlphaComposite)paramComposite;
      return (alphaComposite.getRule() == 3 && alphaComposite.getAlpha() >= 1.0F);
    } 
    return false;
  }
  
  public void validatePipe(SunGraphics2D paramSunGraphics2D) {
    TextPipe textPipe;
    boolean bool = false;
    if ((paramSunGraphics2D.compositeState <= 0 && paramSunGraphics2D.paintState <= 1) || (paramSunGraphics2D.compositeState == 1 && paramSunGraphics2D.paintState <= 1 && ((AlphaComposite)paramSunGraphics2D.composite).getRule() == 3) || (paramSunGraphics2D.compositeState == 2 && paramSunGraphics2D.paintState <= 1)) {
      textPipe = oglTextPipe;
    } else {
      super.validatePipe(paramSunGraphics2D);
      textPipe = paramSunGraphics2D.textpipe;
      bool = true;
    } 
    PixelToParallelogramConverter pixelToParallelogramConverter = null;
    OGLRenderer oGLRenderer = null;
    if (paramSunGraphics2D.antialiasHint != 2) {
      if (paramSunGraphics2D.paintState <= 1) {
        if (paramSunGraphics2D.compositeState <= 2) {
          pixelToParallelogramConverter = oglTxRenderPipe;
          oGLRenderer = oglRenderPipe;
        } 
      } else if (paramSunGraphics2D.compositeState <= 1 && OGLPaints.isValid(paramSunGraphics2D)) {
        pixelToParallelogramConverter = oglTxRenderPipe;
        oGLRenderer = oglRenderPipe;
      } 
    } else if (paramSunGraphics2D.paintState <= 1) {
      if (this.graphicsConfig.isCapPresent(256) && (paramSunGraphics2D.imageComp == CompositeType.SrcOverNoEa || paramSunGraphics2D.imageComp == CompositeType.SrcOver)) {
        if (!bool) {
          super.validatePipe(paramSunGraphics2D);
          bool = true;
        } 
        PixelToParallelogramConverter pixelToParallelogramConverter1 = new PixelToParallelogramConverter(paramSunGraphics2D.shapepipe, oglAAPgramPipe, 0.125D, 0.499D, false);
        paramSunGraphics2D.drawpipe = pixelToParallelogramConverter1;
        paramSunGraphics2D.fillpipe = pixelToParallelogramConverter1;
        paramSunGraphics2D.shapepipe = pixelToParallelogramConverter1;
      } else if (paramSunGraphics2D.compositeState == 2) {
        pixelToParallelogramConverter = oglTxRenderPipe;
        oGLRenderer = oglRenderPipe;
      } 
    } 
    if (pixelToParallelogramConverter != null) {
      if (paramSunGraphics2D.transformState >= 3) {
        paramSunGraphics2D.drawpipe = pixelToParallelogramConverter;
        paramSunGraphics2D.fillpipe = pixelToParallelogramConverter;
      } else if (paramSunGraphics2D.strokeState != 0) {
        paramSunGraphics2D.drawpipe = pixelToParallelogramConverter;
        paramSunGraphics2D.fillpipe = oGLRenderer;
      } else {
        paramSunGraphics2D.drawpipe = oGLRenderer;
        paramSunGraphics2D.fillpipe = oGLRenderer;
      } 
      paramSunGraphics2D.shapepipe = pixelToParallelogramConverter;
    } else if (!bool) {
      super.validatePipe(paramSunGraphics2D);
    } 
    paramSunGraphics2D.textpipe = textPipe;
    paramSunGraphics2D.imagepipe = oglImagePipe;
  }
  
  protected MaskFill getMaskFill(SunGraphics2D paramSunGraphics2D) { return (paramSunGraphics2D.paintState > 1 && (!OGLPaints.isValid(paramSunGraphics2D) || !this.graphicsConfig.isCapPresent(16))) ? null : super.getMaskFill(paramSunGraphics2D); }
  
  public boolean copyArea(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    if (paramSunGraphics2D.transformState < 3 && paramSunGraphics2D.compositeState < 2) {
      paramInt1 += paramSunGraphics2D.transX;
      paramInt2 += paramSunGraphics2D.transY;
      oglRenderPipe.copyArea(paramSunGraphics2D, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
      return true;
    } 
    return false;
  }
  
  public void flush() {
    invalidate();
    oGLRenderQueue = OGLRenderQueue.getInstance();
    oGLRenderQueue.lock();
    try {
      OGLContext.setScratchSurface(this.graphicsConfig);
      RenderBuffer renderBuffer = oGLRenderQueue.getBuffer();
      oGLRenderQueue.ensureCapacityAndAlignment(12, 4);
      renderBuffer.putInt(72);
      renderBuffer.putLong(getNativeOps());
      oGLRenderQueue.flushNow();
    } finally {
      oGLRenderQueue.unlock();
    } 
  }
  
  static void dispose(long paramLong1, long paramLong2) {
    oGLRenderQueue = OGLRenderQueue.getInstance();
    oGLRenderQueue.lock();
    try {
      OGLContext.setScratchSurface(paramLong2);
      RenderBuffer renderBuffer = oGLRenderQueue.getBuffer();
      oGLRenderQueue.ensureCapacityAndAlignment(12, 4);
      renderBuffer.putInt(73);
      renderBuffer.putLong(paramLong1);
      oGLRenderQueue.flushNow();
    } finally {
      oGLRenderQueue.unlock();
    } 
  }
  
  static void swapBuffers(long paramLong) {
    oGLRenderQueue = OGLRenderQueue.getInstance();
    oGLRenderQueue.lock();
    try {
      RenderBuffer renderBuffer = oGLRenderQueue.getBuffer();
      oGLRenderQueue.ensureCapacityAndAlignment(12, 4);
      renderBuffer.putInt(80);
      renderBuffer.putLong(paramLong);
      oGLRenderQueue.flushNow();
    } finally {
      oGLRenderQueue.unlock();
    } 
  }
  
  boolean isTexNonPow2Available() { return this.graphicsConfig.isCapPresent(32); }
  
  boolean isTexRectAvailable() { return this.graphicsConfig.isCapPresent(1048576); }
  
  public Rectangle getNativeBounds() {
    oGLRenderQueue = OGLRenderQueue.getInstance();
    oGLRenderQueue.lock();
    try {
      return new Rectangle(this.nativeWidth, this.nativeHeight);
    } finally {
      oGLRenderQueue.unlock();
    } 
  }
  
  boolean isOnScreen() { return (getType() == 1); }
  
  static  {
    if (!GraphicsEnvironment.isHeadless()) {
      String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.opengl.fbobject"));
      isFBObjectEnabled = !"false".equals(str1);
      String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.opengl.lcdshader"));
      isLCDShaderEnabled = !"false".equals(str2);
      String str3 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.opengl.biopshader"));
      isBIOpShaderEnabled = !"false".equals(str3);
      String str4 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.opengl.gradshader"));
      isGradShaderEnabled = !"false".equals(str4);
      OGLRenderQueue oGLRenderQueue = OGLRenderQueue.getInstance();
      oglImagePipe = new OGLDrawImage();
      oglTextPipe = new OGLTextRenderer(oGLRenderQueue);
      oglRenderPipe = new OGLRenderer(oGLRenderQueue);
      if (GraphicsPrimitive.tracingEnabled())
        oglTextPipe = oglTextPipe.traceWrap(); 
      oglAAPgramPipe = oglRenderPipe.getAAParallelogramPipe();
      oglTxRenderPipe = new PixelToParallelogramConverter(oglRenderPipe, oglRenderPipe, 1.0D, 0.25D, true);
      OGLBlitLoops.register();
      OGLMaskFill.register();
      OGLMaskBlit.register();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\opengl\OGLSurfaceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */