package java.awt.image.renderable;

import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;

public interface ContextualRenderedImageFactory extends RenderedImageFactory {
  RenderContext mapRenderContext(int paramInt, RenderContext paramRenderContext, ParameterBlock paramParameterBlock, RenderableImage paramRenderableImage);
  
  RenderedImage create(RenderContext paramRenderContext, ParameterBlock paramParameterBlock);
  
  Rectangle2D getBounds2D(ParameterBlock paramParameterBlock);
  
  Object getProperty(ParameterBlock paramParameterBlock, String paramString);
  
  String[] getPropertyNames();
  
  boolean isDynamic();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\renderable\ContextualRenderedImageFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */