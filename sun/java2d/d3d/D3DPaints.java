package sun.java2d.d3d;

import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;

abstract class D3DPaints {
  private static Map<Integer, D3DPaints> impls = new HashMap(4, 1.0F);
  
  static boolean isValid(SunGraphics2D paramSunGraphics2D) {
    D3DPaints d3DPaints = (D3DPaints)impls.get(Integer.valueOf(paramSunGraphics2D.paintState));
    return (d3DPaints != null && d3DPaints.isPaintValid(paramSunGraphics2D));
  }
  
  abstract boolean isPaintValid(SunGraphics2D paramSunGraphics2D);
  
  static  {
    impls.put(Integer.valueOf(2), new Gradient(null));
    impls.put(Integer.valueOf(3), new LinearGradient(null));
    impls.put(Integer.valueOf(4), new RadialGradient(null));
    impls.put(Integer.valueOf(5), new Texture(null));
  }
  
  private static class Gradient extends D3DPaints {
    private Gradient() {}
    
    boolean isPaintValid(SunGraphics2D param1SunGraphics2D) {
      D3DSurfaceData d3DSurfaceData = (D3DSurfaceData)param1SunGraphics2D.surfaceData;
      D3DGraphicsDevice d3DGraphicsDevice = (D3DGraphicsDevice)d3DSurfaceData.getDeviceConfiguration().getDevice();
      return d3DGraphicsDevice.isCapPresent(65536);
    }
  }
  
  private static class LinearGradient extends MultiGradient {
    private LinearGradient() {}
    
    boolean isPaintValid(SunGraphics2D param1SunGraphics2D) {
      LinearGradientPaint linearGradientPaint = (LinearGradientPaint)param1SunGraphics2D.paint;
      if (linearGradientPaint.getFractions().length == 2 && linearGradientPaint.getCycleMethod() != MultipleGradientPaint.CycleMethod.REPEAT && linearGradientPaint.getColorSpace() != MultipleGradientPaint.ColorSpaceType.LINEAR_RGB) {
        D3DSurfaceData d3DSurfaceData = (D3DSurfaceData)param1SunGraphics2D.surfaceData;
        D3DGraphicsDevice d3DGraphicsDevice = (D3DGraphicsDevice)d3DSurfaceData.getDeviceConfiguration().getDevice();
        if (d3DGraphicsDevice.isCapPresent(65536))
          return true; 
      } 
      return super.isPaintValid(param1SunGraphics2D);
    }
  }
  
  private static abstract class MultiGradient extends D3DPaints {
    public static final int MULTI_MAX_FRACTIONS_D3D = 8;
    
    boolean isPaintValid(SunGraphics2D param1SunGraphics2D) {
      MultipleGradientPaint multipleGradientPaint = (MultipleGradientPaint)param1SunGraphics2D.paint;
      if (multipleGradientPaint.getFractions().length > 8)
        return false; 
      D3DSurfaceData d3DSurfaceData = (D3DSurfaceData)param1SunGraphics2D.surfaceData;
      D3DGraphicsDevice d3DGraphicsDevice = (D3DGraphicsDevice)d3DSurfaceData.getDeviceConfiguration().getDevice();
      return !!d3DGraphicsDevice.isCapPresent(65536);
    }
  }
  
  private static class RadialGradient extends MultiGradient {
    private RadialGradient() {}
  }
  
  private static class Texture extends D3DPaints {
    private Texture() {}
    
    public boolean isPaintValid(SunGraphics2D param1SunGraphics2D) {
      TexturePaint texturePaint = (TexturePaint)param1SunGraphics2D.paint;
      D3DSurfaceData d3DSurfaceData1 = (D3DSurfaceData)param1SunGraphics2D.surfaceData;
      BufferedImage bufferedImage = texturePaint.getImage();
      D3DGraphicsDevice d3DGraphicsDevice = (D3DGraphicsDevice)d3DSurfaceData1.getDeviceConfiguration().getDevice();
      int i = bufferedImage.getWidth();
      int j = bufferedImage.getHeight();
      if (!d3DGraphicsDevice.isCapPresent(32) && ((i & i - 1) != 0 || (j & j - 1) != 0))
        return false; 
      if (!d3DGraphicsDevice.isCapPresent(64) && i != j)
        return false; 
      param1SunGraphics2D;
      SurfaceData surfaceData = d3DSurfaceData1.getSourceSurfaceData(bufferedImage, 0, CompositeType.SrcOver, null);
      if (!(surfaceData instanceof D3DSurfaceData)) {
        param1SunGraphics2D;
        surfaceData = d3DSurfaceData1.getSourceSurfaceData(bufferedImage, 0, CompositeType.SrcOver, null);
        if (!(surfaceData instanceof D3DSurfaceData))
          return false; 
      } 
      D3DSurfaceData d3DSurfaceData2 = (D3DSurfaceData)surfaceData;
      return !(d3DSurfaceData2.getType() != 3);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\d3d\D3DPaints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */