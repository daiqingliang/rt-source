package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import sun.awt.image.ImagingLib;

public class LookupOp implements BufferedImageOp, RasterOp {
  private LookupTable ltable;
  
  private int numComponents;
  
  RenderingHints hints;
  
  public LookupOp(LookupTable paramLookupTable, RenderingHints paramRenderingHints) {
    this.ltable = paramLookupTable;
    this.hints = paramRenderingHints;
    this.numComponents = this.ltable.getNumComponents();
  }
  
  public final LookupTable getTable() { return this.ltable; }
  
  public final BufferedImage filter(BufferedImage paramBufferedImage1, BufferedImage paramBufferedImage2) {
    ColorModel colorModel2;
    ColorModel colorModel1 = paramBufferedImage1.getColorModel();
    int i = colorModel1.getNumColorComponents();
    if (colorModel1 instanceof IndexColorModel)
      throw new IllegalArgumentException("LookupOp cannot be performed on an indexed image"); 
    int j = this.ltable.getNumComponents();
    if (j != 1 && j != colorModel1.getNumComponents() && j != colorModel1.getNumColorComponents())
      throw new IllegalArgumentException("Number of arrays in the  lookup table (" + j + " is not compatible with the  src image: " + paramBufferedImage1); 
    boolean bool = false;
    int k = paramBufferedImage1.getWidth();
    int m = paramBufferedImage1.getHeight();
    if (paramBufferedImage2 == null) {
      paramBufferedImage2 = createCompatibleDestImage(paramBufferedImage1, null);
      colorModel2 = colorModel1;
    } else {
      if (k != paramBufferedImage2.getWidth())
        throw new IllegalArgumentException("Src width (" + k + ") not equal to dst width (" + paramBufferedImage2.getWidth() + ")"); 
      if (m != paramBufferedImage2.getHeight())
        throw new IllegalArgumentException("Src height (" + m + ") not equal to dst height (" + paramBufferedImage2.getHeight() + ")"); 
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
      if (colorModel1.hasAlpha() && (i - 1 == j || j == 1)) {
        int n = writableRaster1.getMinX();
        int i1 = writableRaster1.getMinY();
        int[] arrayOfInt = new int[i - 1];
        for (byte b = 0; b < i - 1; b++)
          arrayOfInt[b] = b; 
        writableRaster1 = writableRaster1.createWritableChild(n, i1, writableRaster1.getWidth(), writableRaster1.getHeight(), n, i1, arrayOfInt);
      } 
      if (colorModel2.hasAlpha()) {
        int n = writableRaster2.getNumBands();
        if (n - 1 == j || j == 1) {
          int i1 = writableRaster2.getMinX();
          int i2 = writableRaster2.getMinY();
          int[] arrayOfInt = new int[i - 1];
          for (byte b = 0; b < i - 1; b++)
            arrayOfInt[b] = b; 
          writableRaster2 = writableRaster2.createWritableChild(i1, i2, writableRaster2.getWidth(), writableRaster2.getHeight(), i1, i2, arrayOfInt);
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
    int j = paramWritableRaster.getNumBands();
    int k = paramRaster.getHeight();
    int m = paramRaster.getWidth();
    int[] arrayOfInt = new int[i];
    if (paramWritableRaster == null) {
      paramWritableRaster = createCompatibleDestRaster(paramRaster);
    } else if (k != paramWritableRaster.getHeight() || m != paramWritableRaster.getWidth()) {
      throw new IllegalArgumentException("Width or height of Rasters do not match");
    } 
    j = paramWritableRaster.getNumBands();
    if (i != j)
      throw new IllegalArgumentException("Number of channels in the src (" + i + ") does not match number of channels in the destination (" + j + ")"); 
    int n = this.ltable.getNumComponents();
    if (n != 1 && n != paramRaster.getNumBands())
      throw new IllegalArgumentException("Number of arrays in the  lookup table (" + n + " is not compatible with the  src Raster: " + paramRaster); 
    if (ImagingLib.filter(this, paramRaster, paramWritableRaster) != null)
      return paramWritableRaster; 
    if (this.ltable instanceof ByteLookupTable) {
      byteFilter((ByteLookupTable)this.ltable, paramRaster, paramWritableRaster, m, k, i);
    } else if (this.ltable instanceof ShortLookupTable) {
      shortFilter((ShortLookupTable)this.ltable, paramRaster, paramWritableRaster, m, k, i);
    } else {
      int i1 = paramRaster.getMinX();
      int i2 = paramRaster.getMinY();
      int i3 = paramWritableRaster.getMinX();
      int i4 = paramWritableRaster.getMinY();
      byte b = 0;
      while (b < k) {
        int i5 = i1;
        int i6 = i3;
        byte b1 = 0;
        while (b1 < m) {
          paramRaster.getPixel(i5, i2, arrayOfInt);
          this.ltable.lookupPixel(arrayOfInt, arrayOfInt);
          paramWritableRaster.setPixel(i6, i4, arrayOfInt);
          b1++;
          i5++;
          i6++;
        } 
        b++;
        i2++;
        i4++;
      } 
    } 
    return paramWritableRaster;
  }
  
  public final Rectangle2D getBounds2D(BufferedImage paramBufferedImage) { return getBounds2D(paramBufferedImage.getRaster()); }
  
  public final Rectangle2D getBounds2D(Raster paramRaster) { return paramRaster.getBounds(); }
  
  public BufferedImage createCompatibleDestImage(BufferedImage paramBufferedImage, ColorModel paramColorModel) {
    BufferedImage bufferedImage;
    int i = paramBufferedImage.getWidth();
    int j = paramBufferedImage.getHeight();
    byte b = 0;
    if (paramColorModel == null) {
      ColorModel colorModel = paramBufferedImage.getColorModel();
      WritableRaster writableRaster = paramBufferedImage.getRaster();
      if (colorModel instanceof ComponentColorModel) {
        DataBuffer dataBuffer = writableRaster.getDataBuffer();
        boolean bool1 = colorModel.hasAlpha();
        boolean bool2 = colorModel.isAlphaPremultiplied();
        int k = colorModel.getTransparency();
        int[] arrayOfInt = null;
        if (this.ltable instanceof ByteLookupTable) {
          dataBuffer;
          if (dataBuffer.getDataType() == 1) {
            if (bool1) {
              arrayOfInt = new int[2];
              colorModel;
              if (k == 2) {
                arrayOfInt[1] = 1;
              } else {
                arrayOfInt[1] = 8;
              } 
            } else {
              arrayOfInt = new int[1];
            } 
            arrayOfInt[0] = 8;
          } 
        } else {
          b = 1;
          dataBuffer;
          if (this.ltable instanceof ShortLookupTable && dataBuffer.getDataType() == 0) {
            if (bool1) {
              arrayOfInt = new int[2];
              colorModel;
              if (k == 2) {
                arrayOfInt[1] = 1;
              } else {
                arrayOfInt[1] = 16;
              } 
            } else {
              arrayOfInt = new int[1];
            } 
            arrayOfInt[0] = 16;
          } 
        } 
        if (arrayOfInt != null)
          colorModel = new ComponentColorModel(colorModel.getColorSpace(), arrayOfInt, bool1, bool2, k, b); 
      } 
      bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(i, j), colorModel.isAlphaPremultiplied(), null);
    } else {
      bufferedImage = new BufferedImage(paramColorModel, paramColorModel.createCompatibleWritableRaster(i, j), paramColorModel.isAlphaPremultiplied(), null);
    } 
    return bufferedImage;
  }
  
  public WritableRaster createCompatibleDestRaster(Raster paramRaster) { return paramRaster.createCompatibleWritableRaster(); }
  
  public final Point2D getPoint2D(Point2D paramPoint2D1, Point2D paramPoint2D2) {
    if (paramPoint2D2 == null)
      paramPoint2D2 = new Point2D.Float(); 
    paramPoint2D2.setLocation(paramPoint2D1.getX(), paramPoint2D1.getY());
    return paramPoint2D2;
  }
  
  public final RenderingHints getRenderingHints() { return this.hints; }
  
  private final void byteFilter(ByteLookupTable paramByteLookupTable, Raster paramRaster, WritableRaster paramWritableRaster, int paramInt1, int paramInt2, int paramInt3) {
    int[] arrayOfInt = null;
    byte[][] arrayOfByte = paramByteLookupTable.getTable();
    int i = paramByteLookupTable.getOffset();
    byte b1 = 1;
    if (arrayOfByte.length == 1)
      b1 = 0; 
    int j = arrayOfByte[0].length;
    for (byte b2 = 0; b2 < paramInt2; b2++) {
      byte b3 = 0;
      byte b4 = 0;
      while (b4 < paramInt3) {
        arrayOfInt = paramRaster.getSamples(0, b2, paramInt1, 1, b4, arrayOfInt);
        for (byte b = 0; b < paramInt1; b++) {
          int k = arrayOfInt[b] - i;
          if (k < 0 || k > j)
            throw new IllegalArgumentException("index (" + k + "(out of range:  srcPix[" + b + "]=" + arrayOfInt[b] + " offset=" + i); 
          arrayOfInt[b] = arrayOfByte[b3][k];
        } 
        paramWritableRaster.setSamples(0, b2, paramInt1, 1, b4, arrayOfInt);
        b4++;
        b3 += b1;
      } 
    } 
  }
  
  private final void shortFilter(ShortLookupTable paramShortLookupTable, Raster paramRaster, WritableRaster paramWritableRaster, int paramInt1, int paramInt2, int paramInt3) {
    int[] arrayOfInt = null;
    short[][] arrayOfShort = paramShortLookupTable.getTable();
    int i = paramShortLookupTable.getOffset();
    byte b1 = 1;
    if (arrayOfShort.length == 1)
      b1 = 0; 
    byte b2 = 0;
    byte b3 = 0;
    char c = '￿';
    for (b3 = 0; b3 < paramInt2; b3++) {
      byte b5 = 0;
      byte b4 = 0;
      while (b4 < paramInt3) {
        arrayOfInt = paramRaster.getSamples(0, b3, paramInt1, 1, b4, arrayOfInt);
        for (b2 = 0; b2 < paramInt1; b2++) {
          int j = arrayOfInt[b2] - i;
          if (j < 0 || j > c)
            throw new IllegalArgumentException("index out of range " + j + " x is " + b2 + "srcPix[x]=" + arrayOfInt[b2] + " offset=" + i); 
          arrayOfInt[b2] = arrayOfShort[b5][j];
        } 
        paramWritableRaster.setSamples(0, b3, paramInt1, 1, b4, arrayOfInt);
        b4++;
        b5 += b1;
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\LookupOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */