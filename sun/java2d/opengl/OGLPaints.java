package sun.java2d.opengl;

import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;

abstract class OGLPaints {
  private static Map<Integer, OGLPaints> impls = new HashMap(4, 1.0F);
  
  static boolean isValid(SunGraphics2D paramSunGraphics2D) {
    OGLPaints oGLPaints = (OGLPaints)impls.get(Integer.valueOf(paramSunGraphics2D.paintState));
    return (oGLPaints != null && oGLPaints.isPaintValid(paramSunGraphics2D));
  }
  
  abstract boolean isPaintValid(SunGraphics2D paramSunGraphics2D);
  
  static  {
    impls.put(Integer.valueOf(2), new Gradient(null));
    impls.put(Integer.valueOf(3), new LinearGradient(null));
    impls.put(Integer.valueOf(4), new RadialGradient(null));
    impls.put(Integer.valueOf(5), new Texture(null));
  }
  
  private static class Gradient extends OGLPaints {
    private Gradient() {}
    
    boolean isPaintValid(SunGraphics2D param1SunGraphics2D) { return true; }
  }
  
  private static class LinearGradient extends MultiGradient {
    private LinearGradient() {}
    
    boolean isPaintValid(SunGraphics2D param1SunGraphics2D) {
      LinearGradientPaint linearGradientPaint = (LinearGradientPaint)param1SunGraphics2D.paint;
      return (linearGradientPaint.getFractions().length == 2 && linearGradientPaint.getCycleMethod() != MultipleGradientPaint.CycleMethod.REPEAT && linearGradientPaint.getColorSpace() != MultipleGradientPaint.ColorSpaceType.LINEAR_RGB) ? true : super.isPaintValid(param1SunGraphics2D);
    }
  }
  
  private static abstract class MultiGradient extends OGLPaints {
    boolean isPaintValid(SunGraphics2D param1SunGraphics2D) {
      MultipleGradientPaint multipleGradientPaint = (MultipleGradientPaint)param1SunGraphics2D.paint;
      if (multipleGradientPaint.getFractions().length > 12)
        return false; 
      OGLSurfaceData oGLSurfaceData = (OGLSurfaceData)param1SunGraphics2D.surfaceData;
      OGLGraphicsConfig oGLGraphicsConfig = oGLSurfaceData.getOGLGraphicsConfig();
      return !!oGLGraphicsConfig.isCapPresent(524288);
    }
  }
  
  private static class RadialGradient extends MultiGradient {
    private RadialGradient() {}
  }
  
  private static class Texture extends OGLPaints {
    private Texture() {}
    
    boolean isPaintValid(SunGraphics2D param1SunGraphics2D) {
      TexturePaint texturePaint = (TexturePaint)param1SunGraphics2D.paint;
      OGLSurfaceData oGLSurfaceData1 = (OGLSurfaceData)param1SunGraphics2D.surfaceData;
      BufferedImage bufferedImage = texturePaint.getImage();
      if (!oGLSurfaceData1.isTexNonPow2Available()) {
        int i = bufferedImage.getWidth();
        int j = bufferedImage.getHeight();
        if ((i & i - 1) != 0 || (j & j - 1) != 0)
          return false; 
      } 
      SurfaceData surfaceData = oGLSurfaceData1.getSourceSurfaceData(bufferedImage, 0, CompositeType.SrcOver, null);
      if (!(surfaceData instanceof OGLSurfaceData)) {
        surfaceData = oGLSurfaceData1.getSourceSurfaceData(bufferedImage, 0, CompositeType.SrcOver, null);
        if (!(surfaceData instanceof OGLSurfaceData))
          return false; 
      } 
      OGLSurfaceData oGLSurfaceData2 = (OGLSurfaceData)surfaceData;
      return !(oGLSurfaceData2.getType() != 3);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\opengl\OGLPaints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */