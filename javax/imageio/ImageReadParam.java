package javax.imageio;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class ImageReadParam extends IIOParam {
  protected boolean canSetSourceRenderSize = false;
  
  protected Dimension sourceRenderSize = null;
  
  protected BufferedImage destination = null;
  
  protected int[] destinationBands = null;
  
  protected int minProgressivePass = 0;
  
  protected int numProgressivePasses = Integer.MAX_VALUE;
  
  public void setDestinationType(ImageTypeSpecifier paramImageTypeSpecifier) {
    super.setDestinationType(paramImageTypeSpecifier);
    setDestination(null);
  }
  
  public void setDestination(BufferedImage paramBufferedImage) { this.destination = paramBufferedImage; }
  
  public BufferedImage getDestination() { return this.destination; }
  
  public void setDestinationBands(int[] paramArrayOfInt) {
    if (paramArrayOfInt == null) {
      this.destinationBands = null;
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
      this.destinationBands = (int[])paramArrayOfInt.clone();
    } 
  }
  
  public int[] getDestinationBands() { return (this.destinationBands == null) ? null : (int[])this.destinationBands.clone(); }
  
  public boolean canSetSourceRenderSize() { return this.canSetSourceRenderSize; }
  
  public void setSourceRenderSize(Dimension paramDimension) throws UnsupportedOperationException {
    if (!canSetSourceRenderSize())
      throw new UnsupportedOperationException("Can't set source render size!"); 
    if (paramDimension == null) {
      this.sourceRenderSize = null;
    } else {
      if (paramDimension.width <= 0 || paramDimension.height <= 0)
        throw new IllegalArgumentException("width or height <= 0!"); 
      this.sourceRenderSize = (Dimension)paramDimension.clone();
    } 
  }
  
  public Dimension getSourceRenderSize() { return (this.sourceRenderSize == null) ? null : (Dimension)this.sourceRenderSize.clone(); }
  
  public void setSourceProgressivePasses(int paramInt1, int paramInt2) {
    if (paramInt1 < 0)
      throw new IllegalArgumentException("minPass < 0!"); 
    if (paramInt2 <= 0)
      throw new IllegalArgumentException("numPasses <= 0!"); 
    if (paramInt2 != Integer.MAX_VALUE && (paramInt1 + paramInt2 - 1 & 0x80000000) != 0)
      throw new IllegalArgumentException("minPass + numPasses - 1 > INTEGER.MAX_VALUE!"); 
    this.minProgressivePass = paramInt1;
    this.numProgressivePasses = paramInt2;
  }
  
  public int getSourceMinProgressivePass() { return this.minProgressivePass; }
  
  public int getSourceMaxProgressivePass() { return (this.numProgressivePasses == Integer.MAX_VALUE) ? Integer.MAX_VALUE : (this.minProgressivePass + this.numProgressivePasses - 1); }
  
  public int getSourceNumProgressivePasses() { return this.numProgressivePasses; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\ImageReadParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */