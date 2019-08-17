package sun.java2d.opengl;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.pipe.RenderQueue;

final class OGLBlitLoops {
  private static final int OFFSET_SRCTYPE = 16;
  
  private static final int OFFSET_HINT = 8;
  
  private static final int OFFSET_TEXTURE = 3;
  
  private static final int OFFSET_RTT = 2;
  
  private static final int OFFSET_XFORM = 1;
  
  private static final int OFFSET_ISOBLIT = 0;
  
  static void register() {
    OGLSwToSurfaceBlit oGLSwToSurfaceBlit = new OGLSwToSurfaceBlit(SurfaceType.IntArgbPre, 1);
    OGLSwToTextureBlit oGLSwToTextureBlit = new OGLSwToTextureBlit(SurfaceType.IntArgbPre, 1);
    OGLSwToSurfaceTransform oGLSwToSurfaceTransform = new OGLSwToSurfaceTransform(SurfaceType.IntArgbPre, 1);
    OGLSurfaceToSwBlit oGLSurfaceToSwBlit = new OGLSurfaceToSwBlit(SurfaceType.IntArgbPre, 1);
    GraphicsPrimitive[] arrayOfGraphicsPrimitive = { 
        new OGLSurfaceToSurfaceBlit(), new OGLSurfaceToSurfaceScale(), new OGLSurfaceToSurfaceTransform(), new OGLRTTSurfaceToSurfaceBlit(), new OGLRTTSurfaceToSurfaceScale(), new OGLRTTSurfaceToSurfaceTransform(), new OGLSurfaceToSwBlit(SurfaceType.IntArgb, 0), oGLSurfaceToSwBlit, oGLSwToSurfaceBlit, new OGLSwToSurfaceBlit(SurfaceType.IntRgb, 2), 
        new OGLSwToSurfaceBlit(SurfaceType.IntRgbx, 3), new OGLSwToSurfaceBlit(SurfaceType.IntBgr, 4), new OGLSwToSurfaceBlit(SurfaceType.IntBgrx, 5), new OGLSwToSurfaceBlit(SurfaceType.ThreeByteBgr, 11), new OGLSwToSurfaceBlit(SurfaceType.Ushort565Rgb, 6), new OGLSwToSurfaceBlit(SurfaceType.Ushort555Rgb, 7), new OGLSwToSurfaceBlit(SurfaceType.Ushort555Rgbx, 8), new OGLSwToSurfaceBlit(SurfaceType.ByteGray, 9), new OGLSwToSurfaceBlit(SurfaceType.UshortGray, 10), new OGLGeneralBlit(OGLSurfaceData.OpenGLSurface, CompositeType.AnyAlpha, oGLSwToSurfaceBlit), 
        new OGLAnyCompositeBlit(OGLSurfaceData.OpenGLSurface, oGLSurfaceToSwBlit, oGLSurfaceToSwBlit, oGLSwToSurfaceBlit), new OGLAnyCompositeBlit(SurfaceType.Any, null, oGLSurfaceToSwBlit, oGLSwToSurfaceBlit), new OGLSwToSurfaceScale(SurfaceType.IntRgb, 2), new OGLSwToSurfaceScale(SurfaceType.IntRgbx, 3), new OGLSwToSurfaceScale(SurfaceType.IntBgr, 4), new OGLSwToSurfaceScale(SurfaceType.IntBgrx, 5), new OGLSwToSurfaceScale(SurfaceType.ThreeByteBgr, 11), new OGLSwToSurfaceScale(SurfaceType.Ushort565Rgb, 6), new OGLSwToSurfaceScale(SurfaceType.Ushort555Rgb, 7), new OGLSwToSurfaceScale(SurfaceType.Ushort555Rgbx, 8), 
        new OGLSwToSurfaceScale(SurfaceType.ByteGray, 9), new OGLSwToSurfaceScale(SurfaceType.UshortGray, 10), new OGLSwToSurfaceScale(SurfaceType.IntArgbPre, 1), new OGLSwToSurfaceTransform(SurfaceType.IntRgb, 2), new OGLSwToSurfaceTransform(SurfaceType.IntRgbx, 3), new OGLSwToSurfaceTransform(SurfaceType.IntBgr, 4), new OGLSwToSurfaceTransform(SurfaceType.IntBgrx, 5), new OGLSwToSurfaceTransform(SurfaceType.ThreeByteBgr, 11), new OGLSwToSurfaceTransform(SurfaceType.Ushort565Rgb, 6), new OGLSwToSurfaceTransform(SurfaceType.Ushort555Rgb, 7), 
        new OGLSwToSurfaceTransform(SurfaceType.Ushort555Rgbx, 8), new OGLSwToSurfaceTransform(SurfaceType.ByteGray, 9), new OGLSwToSurfaceTransform(SurfaceType.UshortGray, 10), oGLSwToSurfaceTransform, new OGLGeneralTransformedBlit(oGLSwToSurfaceTransform), new OGLTextureToSurfaceBlit(), new OGLTextureToSurfaceScale(), new OGLTextureToSurfaceTransform(), oGLSwToTextureBlit, new OGLSwToTextureBlit(SurfaceType.IntRgb, 2), 
        new OGLSwToTextureBlit(SurfaceType.IntRgbx, 3), new OGLSwToTextureBlit(SurfaceType.IntBgr, 4), new OGLSwToTextureBlit(SurfaceType.IntBgrx, 5), new OGLSwToTextureBlit(SurfaceType.ThreeByteBgr, 11), new OGLSwToTextureBlit(SurfaceType.Ushort565Rgb, 6), new OGLSwToTextureBlit(SurfaceType.Ushort555Rgb, 7), new OGLSwToTextureBlit(SurfaceType.Ushort555Rgbx, 8), new OGLSwToTextureBlit(SurfaceType.ByteGray, 9), new OGLSwToTextureBlit(SurfaceType.UshortGray, 10), new OGLGeneralBlit(OGLSurfaceData.OpenGLTexture, CompositeType.SrcNoEa, oGLSwToTextureBlit) };
    GraphicsPrimitiveMgr.register(arrayOfGraphicsPrimitive);
  }
  
  private static int createPackedParams(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, int paramInt1, int paramInt2) { return paramInt2 << 16 | paramInt1 << 8 | (paramBoolean2 ? 1 : 0) << 3 | (paramBoolean3 ? 1 : 0) << 2 | (paramBoolean4 ? 1 : 0) << true | (paramBoolean1 ? 1 : 0) << false; }
  
  private static void enqueueBlit(RenderQueue paramRenderQueue, SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
    RenderBuffer renderBuffer = paramRenderQueue.getBuffer();
    paramRenderQueue.ensureCapacityAndAlignment(72, 24);
    renderBuffer.putInt(31);
    renderBuffer.putInt(paramInt1);
    renderBuffer.putInt(paramInt2).putInt(paramInt3);
    renderBuffer.putInt(paramInt4).putInt(paramInt5);
    renderBuffer.putDouble(paramDouble1).putDouble(paramDouble2);
    renderBuffer.putDouble(paramDouble3).putDouble(paramDouble4);
    renderBuffer.putLong(paramSurfaceData1.getNativeOps());
    renderBuffer.putLong(paramSurfaceData2.getNativeOps());
  }
  
  static void Blit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, Composite paramComposite, Region paramRegion, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, int paramInt6, boolean paramBoolean) {
    byte b = 0;
    if (paramSurfaceData1.getTransparency() == 1)
      b |= true; 
    oGLRenderQueue = OGLRenderQueue.getInstance();
    oGLRenderQueue.lock();
    try {
      oGLRenderQueue.addReference(paramSurfaceData1);
      OGLSurfaceData oGLSurfaceData = (OGLSurfaceData)paramSurfaceData2;
      if (paramBoolean) {
        OGLGraphicsConfig oGLGraphicsConfig = oGLSurfaceData.getOGLGraphicsConfig();
        OGLContext.setScratchSurface(oGLGraphicsConfig);
      } else {
        OGLContext.validateContext(oGLSurfaceData, oGLSurfaceData, paramRegion, paramComposite, paramAffineTransform, null, null, b);
      } 
      int i = createPackedParams(false, paramBoolean, false, (paramAffineTransform != null), paramInt1, paramInt6);
      enqueueBlit(oGLRenderQueue, paramSurfaceData1, paramSurfaceData2, i, paramInt2, paramInt3, paramInt4, paramInt5, paramDouble1, paramDouble2, paramDouble3, paramDouble4);
      oGLRenderQueue.flushNow();
    } finally {
      oGLRenderQueue.unlock();
    } 
  }
  
  static void IsoBlit(SurfaceData paramSurfaceData1, SurfaceData paramSurfaceData2, BufferedImage paramBufferedImage, BufferedImageOp paramBufferedImageOp, Composite paramComposite, Region paramRegion, AffineTransform paramAffineTransform, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, boolean paramBoolean) {
    byte b = 0;
    if (paramSurfaceData1.getTransparency() == 1)
      b |= true; 
    oGLRenderQueue = OGLRenderQueue.getInstance();
    oGLRenderQueue.lock();
    try {
      OGLSurfaceData oGLSurfaceData3;
      boolean bool;
      OGLSurfaceData oGLSurfaceData1 = (OGLSurfaceData)paramSurfaceData1;
      OGLSurfaceData oGLSurfaceData2 = (OGLSurfaceData)paramSurfaceData2;
      int i = oGLSurfaceData1.getType();
      if (i == 3) {
        bool = false;
        oGLSurfaceData3 = oGLSurfaceData2;
      } else {
        bool = true;
        if (i == 5) {
          oGLSurfaceData3 = oGLSurfaceData2;
        } else {
          oGLSurfaceData3 = oGLSurfaceData1;
        } 
      } 
      OGLContext.validateContext(oGLSurfaceData3, oGLSurfaceData2, paramRegion, paramComposite, paramAffineTransform, null, null, b);
      if (paramBufferedImageOp != null)
        OGLBufImgOps.enableBufImgOp(oGLRenderQueue, oGLSurfaceData1, paramBufferedImage, paramBufferedImageOp); 
      int j = createPackedParams(true, paramBoolean, bool, (paramAffineTransform != null), paramInt1, 0);
      enqueueBlit(oGLRenderQueue, paramSurfaceData1, paramSurfaceData2, j, paramInt2, paramInt3, paramInt4, paramInt5, paramDouble1, paramDouble2, paramDouble3, paramDouble4);
      if (paramBufferedImageOp != null)
        OGLBufImgOps.disableBufImgOp(oGLRenderQueue, paramBufferedImageOp); 
      if (bool && oGLSurfaceData2.isOnScreen())
        oGLRenderQueue.flushNow(); 
    } finally {
      oGLRenderQueue.unlock();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\opengl\OGLBlitLoops.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */