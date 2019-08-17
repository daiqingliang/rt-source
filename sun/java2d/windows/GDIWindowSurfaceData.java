package sun.java2d.windows;

import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import sun.awt.Win32GraphicsConfig;
import sun.awt.Win32GraphicsDevice;
import sun.awt.windows.WComponentPeer;
import sun.java2d.InvalidPipeException;
import sun.java2d.ScreenUpdateManager;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.RenderLoops;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.PixelToShapeConverter;
import sun.java2d.pipe.Region;

public class GDIWindowSurfaceData extends SurfaceData {
  private WComponentPeer peer;
  
  private Win32GraphicsConfig graphicsConfig;
  
  private RenderLoops solidloops;
  
  public static final String DESC_GDI = "GDI";
  
  public static final SurfaceType AnyGdi = SurfaceType.IntRgb.deriveSubType("GDI");
  
  public static final SurfaceType IntRgbGdi = SurfaceType.IntRgb.deriveSubType("GDI");
  
  public static final SurfaceType Ushort565RgbGdi = SurfaceType.Ushort565Rgb.deriveSubType("GDI");
  
  public static final SurfaceType Ushort555RgbGdi = SurfaceType.Ushort555Rgb.deriveSubType("GDI");
  
  public static final SurfaceType ThreeByteBgrGdi = SurfaceType.ThreeByteBgr.deriveSubType("GDI");
  
  protected static GDIRenderer gdiPipe;
  
  protected static PixelToShapeConverter gdiTxPipe;
  
  private static native void initIDs(Class paramClass);
  
  public static SurfaceType getSurfaceType(ColorModel paramColorModel) {
    switch (paramColorModel.getPixelSize()) {
      case 24:
      case 32:
        return (paramColorModel instanceof DirectColorModel) ? ((((DirectColorModel)paramColorModel).getRedMask() == 16711680) ? IntRgbGdi : SurfaceType.IntRgbx) : ThreeByteBgrGdi;
      case 15:
        return Ushort555RgbGdi;
      case 16:
        return (paramColorModel instanceof DirectColorModel && ((DirectColorModel)paramColorModel).getBlueMask() == 62) ? SurfaceType.Ushort555Rgbx : Ushort565RgbGdi;
      case 8:
        return (paramColorModel.getColorSpace().getType() == 6 && paramColorModel instanceof java.awt.image.ComponentColorModel) ? SurfaceType.ByteGray : ((paramColorModel instanceof IndexColorModel && isOpaqueGray((IndexColorModel)paramColorModel)) ? SurfaceType.Index8Gray : SurfaceType.ByteIndexedOpaque);
    } 
    throw new InvalidPipeException("Unsupported bit depth: " + paramColorModel.getPixelSize());
  }
  
  public static GDIWindowSurfaceData createData(WComponentPeer paramWComponentPeer) {
    SurfaceType surfaceType = getSurfaceType(paramWComponentPeer.getDeviceColorModel());
    return new GDIWindowSurfaceData(paramWComponentPeer, surfaceType);
  }
  
  public SurfaceDataProxy makeProxyFor(SurfaceData paramSurfaceData) { return SurfaceDataProxy.UNCACHED; }
  
  public Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { throw new InternalError("not implemented yet"); }
  
  public void validatePipe(SunGraphics2D paramSunGraphics2D) {
    paramSunGraphics2D;
    paramSunGraphics2D;
    paramSunGraphics2D;
    if (paramSunGraphics2D.antialiasHint != 2 && paramSunGraphics2D.paintState <= 1 && (paramSunGraphics2D.compositeState <= 0 || paramSunGraphics2D.compositeState == 2)) {
      paramSunGraphics2D;
      if (paramSunGraphics2D.clipState == 2) {
        super.validatePipe(paramSunGraphics2D);
      } else {
        switch (paramSunGraphics2D.textAntialiasHint) {
          case 0:
          case 1:
            paramSunGraphics2D.textpipe = solidTextRenderer;
            break;
          case 2:
            paramSunGraphics2D.textpipe = aaTextRenderer;
            break;
          default:
            switch ((paramSunGraphics2D.getFontInfo()).aaHint) {
              case 4:
              case 6:
                paramSunGraphics2D.textpipe = lcdTextRenderer;
                break;
              case 2:
                paramSunGraphics2D.textpipe = aaTextRenderer;
                break;
            } 
            paramSunGraphics2D.textpipe = solidTextRenderer;
            break;
        } 
      } 
      paramSunGraphics2D.imagepipe = imagepipe;
      paramSunGraphics2D;
      if (paramSunGraphics2D.transformState >= 3) {
        paramSunGraphics2D.drawpipe = gdiTxPipe;
        paramSunGraphics2D.fillpipe = gdiTxPipe;
      } else {
        paramSunGraphics2D;
        if (paramSunGraphics2D.strokeState != 0) {
          paramSunGraphics2D.drawpipe = gdiTxPipe;
          paramSunGraphics2D.fillpipe = gdiPipe;
        } else {
          paramSunGraphics2D.drawpipe = gdiPipe;
          paramSunGraphics2D.fillpipe = gdiPipe;
        } 
      } 
      paramSunGraphics2D.shapepipe = gdiPipe;
      if (paramSunGraphics2D.loops == null)
        paramSunGraphics2D.loops = getRenderLoops(paramSunGraphics2D); 
    } else {
      super.validatePipe(paramSunGraphics2D);
    } 
  }
  
  public RenderLoops getRenderLoops(SunGraphics2D paramSunGraphics2D) {
    paramSunGraphics2D;
    paramSunGraphics2D;
    return (paramSunGraphics2D.paintState <= 1 && paramSunGraphics2D.compositeState <= 0) ? this.solidloops : super.getRenderLoops(paramSunGraphics2D);
  }
  
  public GraphicsConfiguration getDeviceConfiguration() { return this.graphicsConfig; }
  
  private native void initOps(WComponentPeer paramWComponentPeer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  private GDIWindowSurfaceData(WComponentPeer paramWComponentPeer, SurfaceType paramSurfaceType) {
    super(paramSurfaceType, paramWComponentPeer.getDeviceColorModel());
    ColorModel colorModel = paramWComponentPeer.getDeviceColorModel();
    this.peer = paramWComponentPeer;
    int i = 0;
    int j = 0;
    int k = 0;
    switch (colorModel.getPixelSize()) {
      case 24:
      case 32:
        if (colorModel instanceof DirectColorModel) {
          byte b = 32;
          break;
        } 
        m = 24;
        break;
      default:
        m = colorModel.getPixelSize();
        break;
    } 
    if (colorModel instanceof DirectColorModel) {
      DirectColorModel directColorModel = (DirectColorModel)colorModel;
      i = directColorModel.getRedMask();
      j = directColorModel.getGreenMask();
      k = directColorModel.getBlueMask();
    } 
    this.graphicsConfig = (Win32GraphicsConfig)paramWComponentPeer.getGraphicsConfiguration();
    this.solidloops = this.graphicsConfig.getSolidLoops(paramSurfaceType);
    Win32GraphicsDevice win32GraphicsDevice = (Win32GraphicsDevice)this.graphicsConfig.getDevice();
    initOps(paramWComponentPeer, m, i, j, k, win32GraphicsDevice.getScreen());
    setBlitProxyKey(this.graphicsConfig.getProxyKey());
  }
  
  public SurfaceData getReplacement() {
    ScreenUpdateManager screenUpdateManager = ScreenUpdateManager.getInstance();
    return screenUpdateManager.getReplacementScreenSurface(this.peer, this);
  }
  
  public Rectangle getBounds() {
    Rectangle rectangle = this.peer.getBounds();
    rectangle.x = rectangle.y = 0;
    return rectangle;
  }
  
  public boolean copyArea(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    CompositeType compositeType = paramSunGraphics2D.imageComp;
    paramSunGraphics2D;
    paramSunGraphics2D;
    if (paramSunGraphics2D.transformState < 3 && paramSunGraphics2D.clipState != 2 && (CompositeType.SrcOverNoEa.equals(compositeType) || CompositeType.SrcNoEa.equals(compositeType))) {
      paramInt1 += paramSunGraphics2D.transX;
      paramInt2 += paramSunGraphics2D.transY;
      int i = paramInt1 + paramInt5;
      int j = paramInt2 + paramInt6;
      int k = i + paramInt3;
      int m = j + paramInt4;
      Region region = paramSunGraphics2D.getCompClip();
      if (i < region.getLoX())
        i = region.getLoX(); 
      if (j < region.getLoY())
        j = region.getLoY(); 
      if (k > region.getHiX())
        k = region.getHiX(); 
      if (m > region.getHiY())
        m = region.getHiY(); 
      if (i < k && j < m)
        gdiPipe.devCopyArea(this, i - paramInt5, j - paramInt6, paramInt5, paramInt6, k - i, m - j); 
      return true;
    } 
    return false;
  }
  
  private native void invalidateSD();
  
  public void invalidate() {
    if (isValid()) {
      invalidateSD();
      super.invalidate();
    } 
  }
  
  public Object getDestination() { return this.peer.getTarget(); }
  
  public WComponentPeer getPeer() { return this.peer; }
  
  static  {
    initIDs(sun.java2d.loops.XORComposite.class);
    if (WindowsFlags.isGdiBlitEnabled())
      GDIBlitLoops.register(); 
    gdiPipe = new GDIRenderer();
    if (GraphicsPrimitive.tracingEnabled())
      gdiPipe = gdiPipe.traceWrap(); 
    gdiTxPipe = new PixelToShapeConverter(gdiPipe);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\windows\GDIWindowSurfaceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */