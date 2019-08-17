package java.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.util.Vector;

public class RenderableImageOp implements RenderableImage {
  ParameterBlock paramBlock;
  
  ContextualRenderedImageFactory myCRIF;
  
  Rectangle2D boundingBox;
  
  public RenderableImageOp(ContextualRenderedImageFactory paramContextualRenderedImageFactory, ParameterBlock paramParameterBlock) {
    this.myCRIF = paramContextualRenderedImageFactory;
    this.paramBlock = (ParameterBlock)paramParameterBlock.clone();
  }
  
  public Vector<RenderableImage> getSources() { return getRenderableSources(); }
  
  private Vector getRenderableSources() {
    Vector vector = null;
    if (this.paramBlock.getNumSources() > 0) {
      vector = new Vector();
      byte b = 0;
      while (b < this.paramBlock.getNumSources()) {
        Object object = this.paramBlock.getSource(b);
        if (object instanceof RenderableImage) {
          vector.add((RenderableImage)object);
          b++;
        } 
      } 
    } 
    return vector;
  }
  
  public Object getProperty(String paramString) { return this.myCRIF.getProperty(this.paramBlock, paramString); }
  
  public String[] getPropertyNames() { return this.myCRIF.getPropertyNames(); }
  
  public boolean isDynamic() { return this.myCRIF.isDynamic(); }
  
  public float getWidth() {
    if (this.boundingBox == null)
      this.boundingBox = this.myCRIF.getBounds2D(this.paramBlock); 
    return (float)this.boundingBox.getWidth();
  }
  
  public float getHeight() {
    if (this.boundingBox == null)
      this.boundingBox = this.myCRIF.getBounds2D(this.paramBlock); 
    return (float)this.boundingBox.getHeight();
  }
  
  public float getMinX() {
    if (this.boundingBox == null)
      this.boundingBox = this.myCRIF.getBounds2D(this.paramBlock); 
    return (float)this.boundingBox.getMinX();
  }
  
  public float getMinY() {
    if (this.boundingBox == null)
      this.boundingBox = this.myCRIF.getBounds2D(this.paramBlock); 
    return (float)this.boundingBox.getMinY();
  }
  
  public ParameterBlock setParameterBlock(ParameterBlock paramParameterBlock) {
    ParameterBlock parameterBlock = this.paramBlock;
    this.paramBlock = (ParameterBlock)paramParameterBlock.clone();
    return parameterBlock;
  }
  
  public ParameterBlock getParameterBlock() { return this.paramBlock; }
  
  public RenderedImage createScaledRendering(int paramInt1, int paramInt2, RenderingHints paramRenderingHints) {
    double d1 = paramInt1 / getWidth();
    double d2 = paramInt2 / getHeight();
    if (Math.abs(d1 / d2 - 1.0D) < 0.01D)
      d1 = d2; 
    AffineTransform affineTransform = AffineTransform.getScaleInstance(d1, d2);
    RenderContext renderContext = new RenderContext(affineTransform, paramRenderingHints);
    return createRendering(renderContext);
  }
  
  public RenderedImage createDefaultRendering() {
    AffineTransform affineTransform = new AffineTransform();
    RenderContext renderContext = new RenderContext(affineTransform);
    return createRendering(renderContext);
  }
  
  public RenderedImage createRendering(RenderContext paramRenderContext) {
    Object object = null;
    RenderContext renderContext = null;
    ParameterBlock parameterBlock = (ParameterBlock)this.paramBlock.clone();
    Vector vector = getRenderableSources();
    try {
      if (vector != null) {
        Vector vector1 = new Vector();
        for (byte b = 0; b < vector.size(); b++) {
          renderContext = this.myCRIF.mapRenderContext(b, paramRenderContext, this.paramBlock, this);
          RenderedImage renderedImage = ((RenderableImage)vector.elementAt(b)).createRendering(renderContext);
          if (renderedImage == null)
            return null; 
          vector1.addElement(renderedImage);
        } 
        if (vector1.size() > 0)
          parameterBlock.setSources(vector1); 
      } 
      return this.myCRIF.create(paramRenderContext, parameterBlock);
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\renderable\RenderableImageOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */