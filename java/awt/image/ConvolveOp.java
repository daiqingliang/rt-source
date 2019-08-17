package java.awt.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import sun.awt.image.ImagingLib;

public class ConvolveOp implements BufferedImageOp, RasterOp {
  Kernel kernel;
  
  int edgeHint;
  
  RenderingHints hints;
  
  public static final int EDGE_ZERO_FILL = 0;
  
  public static final int EDGE_NO_OP = 1;
  
  public ConvolveOp(Kernel paramKernel, int paramInt, RenderingHints paramRenderingHints) {
    this.kernel = paramKernel;
    this.edgeHint = paramInt;
    this.hints = paramRenderingHints;
  }
  
  public ConvolveOp(Kernel paramKernel) {
    this.kernel = paramKernel;
    this.edgeHint = 0;
  }
  
  public int getEdgeCondition() { return this.edgeHint; }
  
  public final Kernel getKernel() { return (Kernel)this.kernel.clone(); }
  
  public final BufferedImage filter(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2) {
    if (paramBufferedImage1 == null)
      throw new NullPointerException("src image is null"); 
    if (paramBufferedImage1 == paramBufferedImage2)
      throw new IllegalArgumentException("src image cannot be the same as the dst image"); 
    boolean bool = false;
    ColorModel colorModel = paramBufferedImage1.getColorModel();
    BufferedImage bufferedImage = paramBufferedImage2;
    if (colorModel instanceof IndexColorModel) {
      IndexColorModel indexColorModel = (IndexColorModel)colorModel;
      paramBufferedImage1 = indexColorModel.convertToIntDiscrete(paramBufferedImage1.getRaster(), false);
      colorModel = paramBufferedImage1.getColorModel();
    } 
    if (paramBufferedImage2 == null) {
      paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
      ColorModel colorModel1 = colorModel;
      bufferedImage = paramBufferedImage2;
    } else {
      ColorModel colorModel1 = paramBufferedImage2.getColorModel();
      if (colorModel.getColorSpace().getType() != colorModel1.getColorSpace().getType()) {
        bool = true;
        paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
        colorModel1 = paramBufferedImage2.getColorModel();
      } else if (colorModel1 instanceof IndexColorModel) {
        paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
        colorModel1 = paramBufferedImage2.getColorModel();
      } 
    } 
    if (ImagingLib.filter(this, paramBufferedImage1, paramBufferedImage2) == null)
      throw new ImagingOpException("Unable to convolve src image"); 
    if (bool) {
      ColorConvertOp colorConvertOp = new ColorConvertOp(this.hints);
      colorConvertOp.filter(paramBufferedImage2, bufferedImage);
    } else if (bufferedImage != paramBufferedImage2) {
      graphics2D = bufferedImage.createGraphics();
      try {
        graphics2D.drawImage(paramBufferedImage2, 0, 0, null);
      } finally {
        graphics2D.dispose();
      } 
    } 
    return bufferedImage;
  }
  
  public final WritableRaster filter(Raster paramRaster, WritableRaster paramWritableRaster) {
    if (paramWritableRaster == null) {
      paramWritableRaster = createCompatibleDestRaster(paramRaster);
    } else {
      if (paramRaster == paramWritableRaster)
        throw new IllegalArgumentException("src image cannot be the same as the dst image"); 
      if (paramRaster.getNumBands() != paramWritableRaster.getNumBands())
        throw new ImagingOpException("Different number of bands in src  and dst Rasters"); 
    } 
    if (ImagingLib.filter(this, paramRaster, paramWritableRaster) == null)
      throw new ImagingOpException("Unable to convolve src image"); 
    return paramWritableRaster;
  }
  
  public BufferedImage createCompatibleDestImage(BufferedImage paramBufferedImage, ColorModel paramColorModel) {
    int i = paramBufferedImage.getWidth();
    int j = paramBufferedImage.getHeight();
    WritableRaster writableRaster = null;
    if (paramColorModel == null) {
      paramColorModel = paramBufferedImage.getColorModel();
      if (paramColorModel instanceof IndexColorModel) {
        paramColorModel = ColorModel.getRGBdefault();
      } else {
        writableRaster = paramBufferedImage.getData().createCompatibleWritableRaster(i, j);
      } 
    } 
    if (writableRaster == null)
      writableRaster = paramColorModel.createCompatibleWritableRaster(i, j); 
    return new BufferedImage(paramColorModel, writableRaster, paramColorModel.isAlphaPremultiplied(), null);
  }
  
  public WritableRaster createCompatibleDestRaster(Raster paramRaster) { return paramRaster.createCompatibleWritableRaster(); }
  
  public final Rectangle2D getBounds2D(BufferedImage paramBufferedImage) { return getBounds2D(paramBufferedImage.getRaster()); }
  
  public final Rectangle2D getBounds2D(Raster paramRaster) { return paramRaster.getBounds(); }
  
  public final Point2D getPoint2D(Point2D paramPoint2D1, Point2D paramPoint2D2) {
    if (paramPoint2D2 == null)
      paramPoint2D2 = new Point2D.Float(); 
    paramPoint2D2.setLocation(paramPoint2D1.getX(), paramPoint2D1.getY());
    return paramPoint2D2;
  }
  
  public final RenderingHints getRenderingHints() { return this.hints; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\ConvolveOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */