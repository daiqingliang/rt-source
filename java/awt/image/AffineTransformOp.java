package java.awt.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import sun.awt.image.ImagingLib;

public class AffineTransformOp implements BufferedImageOp, RasterOp {
  private AffineTransform xform;
  
  RenderingHints hints;
  
  public static final int TYPE_NEAREST_NEIGHBOR = 1;
  
  public static final int TYPE_BILINEAR = 2;
  
  public static final int TYPE_BICUBIC = 3;
  
  int interpolationType = 1;
  
  public AffineTransformOp(AffineTransform paramAffineTransform, RenderingHints paramRenderingHints) {
    validateTransform(paramAffineTransform);
    this.xform = (AffineTransform)paramAffineTransform.clone();
    this.hints = paramRenderingHints;
    if (paramRenderingHints != null) {
      paramRenderingHints;
      Object object = paramRenderingHints.get(RenderingHints.KEY_INTERPOLATION);
      if (object == null) {
        paramRenderingHints;
        object = paramRenderingHints.get(RenderingHints.KEY_RENDERING);
        paramRenderingHints;
        if (object == RenderingHints.VALUE_RENDER_SPEED) {
          this.interpolationType = 1;
        } else {
          paramRenderingHints;
          if (object == RenderingHints.VALUE_RENDER_QUALITY)
            this.interpolationType = 2; 
        } 
      } else {
        paramRenderingHints;
        if (object == RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR) {
          this.interpolationType = 1;
        } else {
          paramRenderingHints;
          if (object == RenderingHints.VALUE_INTERPOLATION_BILINEAR) {
            this.interpolationType = 2;
          } else {
            paramRenderingHints;
            if (object == RenderingHints.VALUE_INTERPOLATION_BICUBIC)
              this.interpolationType = 3; 
          } 
        } 
      } 
    } else {
      this.interpolationType = 1;
    } 
  }
  
  public AffineTransformOp(AffineTransform paramAffineTransform, int paramInt) {
    validateTransform(paramAffineTransform);
    this.xform = (AffineTransform)paramAffineTransform.clone();
    switch (paramInt) {
      case 1:
      case 2:
      case 3:
        break;
      default:
        throw new IllegalArgumentException("Unknown interpolation type: " + paramInt);
    } 
    this.interpolationType = paramInt;
  }
  
  public final int getInterpolationType() { return this.interpolationType; }
  
  public final BufferedImage filter(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2) {
    if (paramBufferedImage1 == null)
      throw new NullPointerException("src image is null"); 
    if (paramBufferedImage1 == paramBufferedImage2)
      throw new IllegalArgumentException("src image cannot be the same as the dst image"); 
    boolean bool = false;
    ColorModel colorModel = paramBufferedImage1.getColorModel();
    BufferedImage bufferedImage = paramBufferedImage2;
    if (paramBufferedImage2 == null) {
      paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
      ColorModel colorModel1 = colorModel;
      bufferedImage = paramBufferedImage2;
    } else {
      ColorModel colorModel1 = paramBufferedImage2.getColorModel();
      if (colorModel.getColorSpace().getType() != colorModel1.getColorSpace().getType()) {
        int i = this.xform.getType();
        this.xform;
        this.xform;
        boolean bool1 = ((i & (0x18 | 0x20)) != 0) ? 1 : 0;
        this.xform;
        this.xform;
        if (!bool1 && i != 1 && i != 0) {
          double[] arrayOfDouble = new double[4];
          this.xform.getMatrix(arrayOfDouble);
          bool1 = (arrayOfDouble[0] != (int)arrayOfDouble[0] || arrayOfDouble[3] != (int)arrayOfDouble[3]) ? 1 : 0;
        } 
        if (bool1 && colorModel.getTransparency() == 1) {
          ColorConvertOp colorConvertOp = new ColorConvertOp(this.hints);
          BufferedImage bufferedImage1 = null;
          int j = paramBufferedImage1.getWidth();
          int k = paramBufferedImage1.getHeight();
          if (colorModel1.getTransparency() == 1) {
            bufferedImage1 = new BufferedImage(j, k, 2);
          } else {
            WritableRaster writableRaster = colorModel1.createCompatibleWritableRaster(j, k);
            bufferedImage1 = new BufferedImage(colorModel1, writableRaster, colorModel1.isAlphaPremultiplied(), null);
          } 
          paramBufferedImage1 = colorConvertOp.filter(paramBufferedImage1, bufferedImage1);
        } else {
          bool = true;
          paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
        } 
      } 
    } 
    if (this.interpolationType != 1 && paramBufferedImage2.getColorModel() instanceof IndexColorModel)
      paramBufferedImage2 = new BufferedImage(paramBufferedImage2.getWidth(), paramBufferedImage2.getHeight(), 2); 
    if (ImagingLib.filter(this, paramBufferedImage1, paramBufferedImage2) == null)
      throw new ImagingOpException("Unable to transform src image"); 
    if (bool) {
      ColorConvertOp colorConvertOp = new ColorConvertOp(this.hints);
      colorConvertOp.filter(paramBufferedImage2, bufferedImage);
    } else if (bufferedImage != paramBufferedImage2) {
      graphics2D = bufferedImage.createGraphics();
      try {
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.drawImage(paramBufferedImage2, 0, 0, null);
      } finally {
        graphics2D.dispose();
      } 
    } 
    return bufferedImage;
  }
  
  public final WritableRaster filter(Raster paramRaster, WritableRaster paramWritableRaster) {
    if (paramRaster == null)
      throw new NullPointerException("src image is null"); 
    if (paramWritableRaster == null)
      paramWritableRaster = createCompatibleDestRaster(paramRaster); 
    if (paramRaster == paramWritableRaster)
      throw new IllegalArgumentException("src image cannot be the same as the dst image"); 
    if (paramRaster.getNumBands() != paramWritableRaster.getNumBands())
      throw new IllegalArgumentException("Number of src bands (" + paramRaster.getNumBands() + ") does not match number of  dst bands (" + paramWritableRaster.getNumBands() + ")"); 
    if (ImagingLib.filter(this, paramRaster, paramWritableRaster) == null)
      throw new ImagingOpException("Unable to transform src image"); 
    return paramWritableRaster;
  }
  
  public final Rectangle2D getBounds2D(BufferedImage paramBufferedImage) { return getBounds2D(paramBufferedImage.getRaster()); }
  
  public final Rectangle2D getBounds2D(Raster paramRaster) {
    int i = paramRaster.getWidth();
    int j = paramRaster.getHeight();
    float[] arrayOfFloat = { 0.0F, 0.0F, i, 0.0F, i, j, 0.0F, j };
    this.xform.transform(arrayOfFloat, 0, arrayOfFloat, 0, 4);
    float f1 = arrayOfFloat[0];
    float f2 = arrayOfFloat[1];
    float f3 = arrayOfFloat[0];
    float f4 = arrayOfFloat[1];
    for (byte b = 2; b < 8; b += 2) {
      if (arrayOfFloat[b] > f1) {
        f1 = arrayOfFloat[b];
      } else if (arrayOfFloat[b] < f3) {
        f3 = arrayOfFloat[b];
      } 
      if (arrayOfFloat[b + 1] > f2) {
        f2 = arrayOfFloat[b + 1];
      } else if (arrayOfFloat[b + 1] < f4) {
        f4 = arrayOfFloat[b + 1];
      } 
    } 
    return new Rectangle2D.Float(f3, f4, f1 - f3, f2 - f4);
  }
  
  public BufferedImage createCompatibleDestImage(BufferedImage paramBufferedImage, ColorModel paramColorModel) {
    BufferedImage bufferedImage;
    Rectangle rectangle = getBounds2D(paramBufferedImage).getBounds();
    int i = rectangle.x + rectangle.width;
    int j = rectangle.y + rectangle.height;
    if (i <= 0)
      throw new RasterFormatException("Transformed width (" + i + ") is less than or equal to 0."); 
    if (j <= 0)
      throw new RasterFormatException("Transformed height (" + j + ") is less than or equal to 0."); 
    if (paramColorModel == null) {
      ColorModel colorModel = paramBufferedImage.getColorModel();
      if (this.interpolationType != 1 && (colorModel instanceof IndexColorModel || colorModel.getTransparency() == 1)) {
        bufferedImage = new BufferedImage(i, j, 2);
      } else {
        bufferedImage = new BufferedImage(colorModel, paramBufferedImage.getRaster().createCompatibleWritableRaster(i, j), colorModel.isAlphaPremultiplied(), null);
      } 
    } else {
      bufferedImage = new BufferedImage(paramColorModel, paramColorModel.createCompatibleWritableRaster(i, j), paramColorModel.isAlphaPremultiplied(), null);
    } 
    return bufferedImage;
  }
  
  public WritableRaster createCompatibleDestRaster(Raster paramRaster) {
    Rectangle2D rectangle2D = getBounds2D(paramRaster);
    return paramRaster.createCompatibleWritableRaster((int)rectangle2D.getX(), (int)rectangle2D.getY(), (int)rectangle2D.getWidth(), (int)rectangle2D.getHeight());
  }
  
  public final Point2D getPoint2D(Point2D paramPoint2D1, Point2D paramPoint2D2) { return this.xform.transform(paramPoint2D1, paramPoint2D2); }
  
  public final AffineTransform getTransform() { return (AffineTransform)this.xform.clone(); }
  
  public final RenderingHints getRenderingHints() {
    if (this.hints == null) {
      Object object;
      switch (this.interpolationType) {
        case 1:
          object = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
          break;
        case 2:
          object = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
          break;
        case 3:
          object = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
          break;
        default:
          throw new InternalError("Unknown interpolation type " + this.interpolationType);
      } 
      this.hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION, object);
    } 
    return this.hints;
  }
  
  void validateTransform(AffineTransform paramAffineTransform) {
    if (Math.abs(paramAffineTransform.getDeterminant()) <= Double.MIN_VALUE)
      throw new ImagingOpException("Unable to invert transform " + paramAffineTransform); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\AffineTransformOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */