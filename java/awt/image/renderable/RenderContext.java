package java.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

public class RenderContext implements Cloneable {
  RenderingHints hints;
  
  AffineTransform usr2dev;
  
  Shape aoi;
  
  public RenderContext(AffineTransform paramAffineTransform, Shape paramShape, RenderingHints paramRenderingHints) {
    this.hints = paramRenderingHints;
    this.aoi = paramShape;
    this.usr2dev = (AffineTransform)paramAffineTransform.clone();
  }
  
  public RenderContext(AffineTransform paramAffineTransform) { this(paramAffineTransform, null, null); }
  
  public RenderContext(AffineTransform paramAffineTransform, RenderingHints paramRenderingHints) { this(paramAffineTransform, null, paramRenderingHints); }
  
  public RenderContext(AffineTransform paramAffineTransform, Shape paramShape) { this(paramAffineTransform, paramShape, null); }
  
  public RenderingHints getRenderingHints() { return this.hints; }
  
  public void setRenderingHints(RenderingHints paramRenderingHints) { this.hints = paramRenderingHints; }
  
  public void setTransform(AffineTransform paramAffineTransform) { this.usr2dev = (AffineTransform)paramAffineTransform.clone(); }
  
  public void preConcatenateTransform(AffineTransform paramAffineTransform) { preConcetenateTransform(paramAffineTransform); }
  
  @Deprecated
  public void preConcetenateTransform(AffineTransform paramAffineTransform) { this.usr2dev.preConcatenate(paramAffineTransform); }
  
  public void concatenateTransform(AffineTransform paramAffineTransform) { concetenateTransform(paramAffineTransform); }
  
  @Deprecated
  public void concetenateTransform(AffineTransform paramAffineTransform) { this.usr2dev.concatenate(paramAffineTransform); }
  
  public AffineTransform getTransform() { return (AffineTransform)this.usr2dev.clone(); }
  
  public void setAreaOfInterest(Shape paramShape) { this.aoi = paramShape; }
  
  public Shape getAreaOfInterest() { return this.aoi; }
  
  public Object clone() { return new RenderContext(this.usr2dev, this.aoi, this.hints); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\renderable\RenderContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */