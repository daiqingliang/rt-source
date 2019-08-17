package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import sun.awt.image.ImagingLib;

public class RescaleOp implements BufferedImageOp, RasterOp {
  float[] scaleFactors;
  
  float[] offsets;
  
  int length = 0;
  
  RenderingHints hints;
  
  private int srcNbits;
  
  private int dstNbits;
  
  public RescaleOp(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, RenderingHints paramRenderingHints) {
    this.length = paramArrayOfFloat1.length;
    if (this.length > paramArrayOfFloat2.length)
      this.length = paramArrayOfFloat2.length; 
    this.scaleFactors = new float[this.length];
    this.offsets = new float[this.length];
    for (byte b = 0; b < this.length; b++) {
      this.scaleFactors[b] = paramArrayOfFloat1[b];
      this.offsets[b] = paramArrayOfFloat2[b];
    } 
    this.hints = paramRenderingHints;
  }
  
  public RescaleOp(float paramFloat1, float paramFloat2, RenderingHints paramRenderingHints) {
    this.length = 1;
    this.scaleFactors = new float[1];
    this.offsets = new float[1];
    this.scaleFactors[0] = paramFloat1;
    this.offsets[0] = paramFloat2;
    this.hints = paramRenderingHints;
  }
  
  public final float[] getScaleFactors(float[] paramArrayOfFloat) {
    if (paramArrayOfFloat == null)
      return (float[])this.scaleFactors.clone(); 
    System.arraycopy(this.scaleFactors, 0, paramArrayOfFloat, 0, Math.min(this.scaleFactors.length, paramArrayOfFloat.length));
    return paramArrayOfFloat;
  }
  
  public final float[] getOffsets(float[] paramArrayOfFloat) {
    if (paramArrayOfFloat == null)
      return (float[])this.offsets.clone(); 
    System.arraycopy(this.offsets, 0, paramArrayOfFloat, 0, Math.min(this.offsets.length, paramArrayOfFloat.length));
    return paramArrayOfFloat;
  }
  
  public final int getNumFactors() { return this.length; }
  
  private ByteLookupTable createByteLut(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt1, int paramInt2) {
    byte[][] arrayOfByte = new byte[paramArrayOfFloat1.length][paramInt2];
    for (byte b = 0; b < paramArrayOfFloat1.length; b++) {
      float f1 = paramArrayOfFloat1[b];
      float f2 = paramArrayOfFloat2[b];
      byte[] arrayOfByte1 = arrayOfByte[b];
      for (byte b1 = 0; b1 < paramInt2; b1++) {
        int i = (int)(b1 * f1 + f2);
        if ((i & 0xFFFFFF00) != 0)
          if (i < 0) {
            i = 0;
          } else {
            i = 255;
          }  
        arrayOfByte1[b1] = (byte)i;
      } 
    } 
    return new ByteLookupTable(0, arrayOfByte);
  }
  
  private ShortLookupTable createShortLut(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2, int paramInt1, int paramInt2) {
    short[][] arrayOfShort = new short[paramArrayOfFloat1.length][paramInt2];
    for (byte b = 0; b < paramArrayOfFloat1.length; b++) {
      float f1 = paramArrayOfFloat1[b];
      float f2 = paramArrayOfFloat2[b];
      short[] arrayOfShort1 = arrayOfShort[b];
      for (byte b1 = 0; b1 < paramInt2; b1++) {
        int i = (int)(b1 * f1 + f2);
        if ((i & 0xFFFF0000) != 0)
          if (i < 0) {
            i = 0;
          } else {
            i = 65535;
          }  
        arrayOfShort1[b1] = (short)i;
      } 
    } 
    return new ShortLookupTable(0, arrayOfShort);
  }
  
  private boolean canUseLookup(Raster paramRaster1, Raster paramRaster2) {
    int i = paramRaster1.getDataBuffer().getDataType();
    if (i != 0 && i != 1)
      return false; 
    SampleModel sampleModel1 = paramRaster2.getSampleModel();
    this.dstNbits = sampleModel1.getSampleSize(0);
    if (this.dstNbits != 8 && this.dstNbits != 16)
      return false; 
    for (byte b1 = 1; b1 < paramRaster1.getNumBands(); b1++) {
      int j = sampleModel1.getSampleSize(b1);
      if (j != this.dstNbits)
        return false; 
    } 
    SampleModel sampleModel2 = paramRaster1.getSampleModel();
    this.srcNbits = sampleModel2.getSampleSize(0);
    if (this.srcNbits > 16)
      return false; 
    for (byte b2 = 1; b2 < paramRaster1.getNumBands(); b2++) {
      int j = sampleModel2.getSampleSize(b2);
      if (j != this.srcNbits)
        return false; 
    } 
    return true;
  }
  
  public final BufferedImage filter(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2) {
    ColorModel colorModel2;
    ColorModel colorModel1 = paramBufferedImage1.getColorModel();
    int i = colorModel1.getNumColorComponents();
    if (colorModel1 instanceof IndexColorModel)
      throw new IllegalArgumentException("Rescaling cannot be performed on an indexed image"); 
    if (this.length != 1 && this.length != i && this.length != colorModel1.getNumComponents())
      throw new IllegalArgumentException("Number of scaling constants does not equal the number of of color or color/alpha  components"); 
    boolean bool = false;
    if (this.length > i && colorModel1.hasAlpha())
      this.length = i + 1; 
    int j = paramBufferedImage1.getWidth();
    int k = paramBufferedImage1.getHeight();
    if (paramBufferedImage2 == null) {
      paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
      colorModel2 = colorModel1;
    } else {
      if (j != paramBufferedImage2.getWidth())
        throw new IllegalArgumentException("Src width (" + j + ") not equal to dst width (" + paramBufferedImage2.getWidth() + ")"); 
      if (k != paramBufferedImage2.getHeight())
        throw new IllegalArgumentException("Src height (" + k + ") not equal to dst height (" + paramBufferedImage2.getHeight() + ")"); 
      colorModel2 = paramBufferedImage2.getColorModel();
      if (colorModel1.getColorSpace().getType() != colorModel2.getColorSpace().getType()) {
        bool = true;
        paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
      } 
    } 
    BufferedImage bufferedImage = paramBufferedImage2;
    if (ImagingLib.filter(this, paramBufferedImage1, paramBufferedImage2) == null) {
      WritableRaster writableRaster1 = paramBufferedImage1.getRaster();
      WritableRaster writableRaster2 = paramBufferedImage2.getRaster();
      if (colorModel1.hasAlpha() && (i - 1 == this.length || this.length == 1)) {
        int m = writableRaster1.getMinX();
        int n = writableRaster1.getMinY();
        int[] arrayOfInt = new int[i - 1];
        for (byte b = 0; b < i - 1; b++)
          arrayOfInt[b] = b; 
        writableRaster1 = writableRaster1.createWritableChild(m, n, writableRaster1.getWidth(), writableRaster1.getHeight(), m, n, arrayOfInt);
      } 
      if (colorModel2.hasAlpha()) {
        int m = writableRaster2.getNumBands();
        if (m - 1 == this.length || this.length == 1) {
          int n = writableRaster2.getMinX();
          int i1 = writableRaster2.getMinY();
          int[] arrayOfInt = new int[i - 1];
          for (byte b = 0; b < i - 1; b++)
            arrayOfInt[b] = b; 
          writableRaster2 = writableRaster2.createWritableChild(n, i1, writableRaster2.getWidth(), writableRaster2.getHeight(), n, i1, arrayOfInt);
        } 
      } 
      filter(writableRaster1, writableRaster2);
    } 
    if (bool) {
      ColorConvertOp colorConvertOp = new ColorConvertOp(this.hints);
      colorConvertOp.filter(paramBufferedImage2, bufferedImage);
    } 
    return bufferedImage;
  }
  
  public final WritableRaster filter(Raster paramRaster, WritableRaster paramWritableRaster) {
    int i = paramRaster.getNumBands();
    int j = paramRaster.getWidth();
    int k = paramRaster.getHeight();
    int[] arrayOfInt = null;
    byte b1 = 0;
    byte b2 = 0;
    if (paramWritableRaster == null) {
      paramWritableRaster = createCompatibleDestRaster(paramRaster);
    } else {
      if (k != paramWritableRaster.getHeight() || j != paramWritableRaster.getWidth())
        throw new IllegalArgumentException("Width or height of Rasters do not match"); 
      if (i != paramWritableRaster.getNumBands())
        throw new IllegalArgumentException("Number of bands in src " + i + " does not equal number of bands in dest " + paramWritableRaster.getNumBands()); 
    } 
    if (this.length != 1 && this.length != paramRaster.getNumBands())
      throw new IllegalArgumentException("Number of scaling constants does not equal the number of of bands in the src raster"); 
    if (ImagingLib.filter(this, paramRaster, paramWritableRaster) != null)
      return paramWritableRaster; 
    if (canUseLookup(paramRaster, paramWritableRaster)) {
      int m = 1 << this.srcNbits;
      int n = 1 << this.dstNbits;
      if (n == 256) {
        ByteLookupTable byteLookupTable = createByteLut(this.scaleFactors, this.offsets, i, m);
        LookupOp lookupOp = new LookupOp(byteLookupTable, this.hints);
        lookupOp.filter(paramRaster, paramWritableRaster);
      } else {
        ShortLookupTable shortLookupTable = createShortLut(this.scaleFactors, this.offsets, i, m);
        LookupOp lookupOp = new LookupOp(shortLookupTable, this.hints);
        lookupOp.filter(paramRaster, paramWritableRaster);
      } 
    } else {
      if (this.length > 1)
        b1 = 1; 
      int m = paramRaster.getMinX();
      int n = paramRaster.getMinY();
      int i1 = paramWritableRaster.getMinX();
      int i2 = paramWritableRaster.getMinY();
      int[] arrayOfInt1 = new int[i];
      int[] arrayOfInt2 = new int[i];
      SampleModel sampleModel = paramWritableRaster.getSampleModel();
      int i3;
      for (i3 = 0; i3 < i; i3++) {
        int i4 = sampleModel.getSampleSize(i3);
        arrayOfInt1[i3] = (1 << i4) - 1;
        arrayOfInt2[i3] = arrayOfInt1[i3] ^ 0xFFFFFFFF;
      } 
      byte b = 0;
      while (b < k) {
        int i5 = i1;
        int i4 = m;
        byte b3 = 0;
        while (b3 < j) {
          arrayOfInt = paramRaster.getPixel(i4, n, arrayOfInt);
          b2 = 0;
          byte b4 = 0;
          while (b4 < i) {
            i3 = (int)(arrayOfInt[b4] * this.scaleFactors[b2] + this.offsets[b2]);
            if ((i3 & arrayOfInt2[b4]) != 0)
              if (i3 < 0) {
                i3 = 0;
              } else {
                i3 = arrayOfInt1[b4];
              }  
            arrayOfInt[b4] = i3;
            b4++;
            b2 += b1;
          } 
          paramWritableRaster.setPixel(i5, i2, arrayOfInt);
          b3++;
          i4++;
          i5++;
        } 
        b++;
        n++;
        i2++;
      } 
    } 
    return paramWritableRaster;
  }
  
  public final Rectangle2D getBounds2D(BufferedImage paramBufferedImage) { return getBounds2D(paramBufferedImage.getRaster()); }
  
  public final Rectangle2D getBounds2D(Raster paramRaster) { return paramRaster.getBounds(); }
  
  public BufferedImage createCompatibleDestImage(BufferedImage paramBufferedImage, ColorModel paramColorModel) {
    BufferedImage bufferedImage;
    if (paramColorModel == null) {
      ColorModel colorModel = paramBufferedImage.getColorModel();
      bufferedImage = new BufferedImage(colorModel, paramBufferedImage.getRaster().createCompatibleWritableRaster(), colorModel.isAlphaPremultiplied(), null);
    } else {
      int i = paramBufferedImage.getWidth();
      int j = paramBufferedImage.getHeight();
      bufferedImage = new BufferedImage(paramColorModel, paramColorModel.createCompatibleWritableRaster(i, j), paramColorModel.isAlphaPremultiplied(), null);
    } 
    return bufferedImage;
  }
  
  public WritableRaster createCompatibleDestRaster(Raster paramRaster) { return paramRaster.createCompatibleWritableRaster(paramRaster.getWidth(), paramRaster.getHeight()); }
  
  public final Point2D getPoint2D(Point2D paramPoint2D1, Point2D paramPoint2D2) {
    if (paramPoint2D2 == null)
      paramPoint2D2 = new Point2D.Float(); 
    paramPoint2D2.setLocation(paramPoint2D1.getX(), paramPoint2D1.getY());
    return paramPoint2D2;
  }
  
  public final RenderingHints getRenderingHints() { return this.hints; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\RescaleOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */