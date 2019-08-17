package java.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.util.Vector;

public interface RenderableImage {
  public static final String HINTS_OBSERVED = "HINTS_OBSERVED";
  
  Vector<RenderableImage> getSources();
  
  Object getProperty(String paramString);
  
  String[] getPropertyNames();
  
  boolean isDynamic();
  
  float getWidth();
  
  float getHeight();
  
  float getMinX();
  
  float getMinY();
  
  RenderedImage createScaledRendering(int paramInt1, int paramInt2, RenderingHints paramRenderingHints);
  
  RenderedImage createDefaultRendering();
  
  RenderedImage createRendering(RenderContext paramRenderContext);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\renderable\RenderableImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */