package javax.imageio;

import java.awt.Point;
import java.awt.Rectangle;

public abstract class IIOParam {
  protected Rectangle sourceRegion = null;
  
  protected int sourceXSubsampling = 1;
  
  protected int sourceYSubsampling = 1;
  
  protected int subsamplingXOffset = 0;
  
  protected int subsamplingYOffset = 0;
  
  protected int[] sourceBands = null;
  
  protected ImageTypeSpecifier destinationType = null;
  
  protected Point destinationOffset = new Point(0, 0);
  
  protected IIOParamController defaultController = null;
  
  protected IIOParamController controller = null;
  
  protected IIOParam() { this.controller = this.defaultController; }
  
  public void setSourceRegion(Rectangle paramRectangle) {
    if (paramRectangle == null) {
      this.sourceRegion = null;
      return;
    } 
    if (paramRectangle.x < 0)
      throw new IllegalArgumentException("sourceRegion.x < 0!"); 
    if (paramRectangle.y < 0)
      throw new IllegalArgumentException("sourceRegion.y < 0!"); 
    if (paramRectangle.width <= 0)
      throw new IllegalArgumentException("sourceRegion.width <= 0!"); 
    if (paramRectangle.height <= 0)
      throw new IllegalArgumentException("sourceRegion.height <= 0!"); 
    if (paramRectangle.width <= this.subsamplingXOffset)
      throw new IllegalStateException("sourceRegion.width <= subsamplingXOffset!"); 
    if (paramRectangle.height <= this.subsamplingYOffset)
      throw new IllegalStateException("sourceRegion.height <= subsamplingYOffset!"); 
    this.sourceRegion = (Rectangle)paramRectangle.clone();
  }
  
  public Rectangle getSourceRegion() { return (this.sourceRegion == null) ? null : (Rectangle)this.sourceRegion.clone(); }
  
  public void setSourceSubsampling(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt1 <= 0)
      throw new IllegalArgumentException("sourceXSubsampling <= 0!"); 
    if (paramInt2 <= 0)
      throw new IllegalArgumentException("sourceYSubsampling <= 0!"); 
    if (paramInt3 < 0 || paramInt3 >= paramInt1)
      throw new IllegalArgumentException("subsamplingXOffset out of range!"); 
    if (paramInt4 < 0 || paramInt4 >= paramInt2)
      throw new IllegalArgumentException("subsamplingYOffset out of range!"); 
    if (this.sourceRegion != null && (paramInt3 >= this.sourceRegion.width || paramInt4 >= this.sourceRegion.height))
      throw new IllegalStateException("region contains no pixels!"); 
    this.sourceXSubsampling = paramInt1;
    this.sourceYSubsampling = paramInt2;
    this.subsamplingXOffset = paramInt3;
    this.subsamplingYOffset = paramInt4;
  }
  
  public int getSourceXSubsampling() { return this.sourceXSubsampling; }
  
  public int getSourceYSubsampling() { return this.sourceYSubsampling; }
  
  public int getSubsamplingXOffset() { return this.subsamplingXOffset; }
  
  public int getSubsamplingYOffset() { return this.subsamplingYOffset; }
  
  public void setSourceBands(int[] paramArrayOfInt) {
    if (paramArrayOfInt == null) {
      this.sourceBands = null;
    } else {
      int i = paramArrayOfInt.length;
      for (byte b = 0; b < i; b++) {
        int j = paramArrayOfInt[b];
        if (j < 0)
          throw new IllegalArgumentException("Band value < 0!"); 
        for (byte b1 = b + true; b1 < i; b1++) {
          if (j == paramArrayOfInt[b1])
            throw new IllegalArgumentException("Duplicate band value!"); 
        } 
      } 
      this.sourceBands = (int[])paramArrayOfInt.clone();
    } 
  }
  
  public int[] getSourceBands() { return (this.sourceBands == null) ? null : (int[])this.sourceBands.clone(); }
  
  public void setDestinationType(ImageTypeSpecifier paramImageTypeSpecifier) { this.destinationType = paramImageTypeSpecifier; }
  
  public ImageTypeSpecifier getDestinationType() { return this.destinationType; }
  
  public void setDestinationOffset(Point paramPoint) {
    if (paramPoint == null)
      throw new IllegalArgumentException("destinationOffset == null!"); 
    this.destinationOffset = (Point)paramPoint.clone();
  }
  
  public Point getDestinationOffset() { return (Point)this.destinationOffset.clone(); }
  
  public void setController(IIOParamController paramIIOParamController) { this.controller = paramIIOParamController; }
  
  public IIOParamController getController() { return this.controller; }
  
  public IIOParamController getDefaultController() { return this.defaultController; }
  
  public boolean hasController() { return (this.controller != null); }
  
  public boolean activateController() {
    if (!hasController())
      throw new IllegalStateException("hasController() == false!"); 
    return getController().activate(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\IIOParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */