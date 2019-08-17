package sun.java2d.loops;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import sun.java2d.SunCompositeContext;
import sun.java2d.SurfaceData;

public final class XORComposite implements Composite {
  Color xorColor;
  
  int xorPixel;
  
  int alphaMask;
  
  public XORComposite(Color paramColor, SurfaceData paramSurfaceData) {
    this.xorColor = paramColor;
    SurfaceType surfaceType = paramSurfaceData.getSurfaceType();
    this.xorPixel = paramSurfaceData.pixelFor(paramColor.getRGB());
    this.alphaMask = surfaceType.getAlphaMask();
  }
  
  public Color getXorColor() { return this.xorColor; }
  
  public int getXorPixel() { return this.xorPixel; }
  
  public int getAlphaMask() { return this.alphaMask; }
  
  public CompositeContext createContext(ColorModel paramColorModel1, ColorModel paramColorModel2, RenderingHints paramRenderingHints) { return new SunCompositeContext(this, paramColorModel1, paramColorModel2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\java2d\loops\XORComposite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */