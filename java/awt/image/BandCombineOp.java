package java.awt.image;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import sun.awt.image.ImagingLib;

public class BandCombineOp implements RasterOp {
  float[][] matrix;
  
  int nrows = 0;
  
  int ncols = 0;
  
  RenderingHints hints;
  
  public BandCombineOp(float[][] paramArrayOfFloat, RenderingHints paramRenderingHints) {
    this.nrows = paramArrayOfFloat.length;
    this.ncols = paramArrayOfFloat[0].length;
    this.matrix = new float[this.nrows][];
    for (byte b = 0; b < this.nrows; b++) {
      if (this.ncols > paramArrayOfFloat[b].length)
        throw new IndexOutOfBoundsException("row " + b + " too short"); 
      this.matrix[b] = Arrays.copyOf(paramArrayOfFloat[b], this.ncols);
    } 
    this.hints = paramRenderingHints;
  }
  
  public final float[][] getMatrix() {
    float[][] arrayOfFloat = new float[this.nrows][];
    for (byte b = 0; b < this.nrows; b++)
      arrayOfFloat[b] = Arrays.copyOf(this.matrix[b], this.ncols); 
    return arrayOfFloat;
  }
  
  public WritableRaster filter(Raster paramRaster, WritableRaster paramWritableRaster) {
    int i = paramRaster.getNumBands();
    if (this.ncols != i && this.ncols != i + 1)
      throw new IllegalArgumentException("Number of columns in the matrix (" + this.ncols + ") must be equal to the number of bands ([+1]) in src (" + i + ")."); 
    if (paramWritableRaster == null) {
      paramWritableRaster = createCompatibleDestRaster(paramRaster);
    } else if (this.nrows != paramWritableRaster.getNumBands()) {
      throw new IllegalArgumentException("Number of rows in the matrix (" + this.nrows + ") must be equal to the number of bands ([+1]) in dst (" + i + ").");
    } 
    if (ImagingLib.filter(this, paramRaster, paramWritableRaster) != null)
      return paramWritableRaster; 
    int[] arrayOfInt1 = null;
    int[] arrayOfInt2 = new int[paramWritableRaster.getNumBands()];
    int j = paramRaster.getMinX();
    int k = paramRaster.getMinY();
    int m = paramWritableRaster.getMinX();
    int n = paramWritableRaster.getMinY();
    if (this.ncols == i) {
      byte b = 0;
      while (b < paramRaster.getHeight()) {
        int i2 = m;
        int i1 = j;
        byte b1 = 0;
        while (b1 < paramRaster.getWidth()) {
          arrayOfInt1 = paramRaster.getPixel(i1, k, arrayOfInt1);
          for (byte b2 = 0; b2 < this.nrows; b2++) {
            float f = 0.0F;
            for (byte b3 = 0; b3 < this.ncols; b3++)
              f += this.matrix[b2][b3] * arrayOfInt1[b3]; 
            arrayOfInt2[b2] = (int)f;
          } 
          paramWritableRaster.setPixel(i2, n, arrayOfInt2);
          b1++;
          i1++;
          i2++;
        } 
        b++;
        k++;
        n++;
      } 
    } else {
      byte b = 0;
      while (b < paramRaster.getHeight()) {
        int i2 = m;
        int i1 = j;
        byte b1 = 0;
        while (b1 < paramRaster.getWidth()) {
          arrayOfInt1 = paramRaster.getPixel(i1, k, arrayOfInt1);
          for (byte b2 = 0; b2 < this.nrows; b2++) {
            float f = 0.0F;
            for (byte b3 = 0; b3 < i; b3++)
              f += this.matrix[b2][b3] * arrayOfInt1[b3]; 
            arrayOfInt2[b2] = (int)(f + this.matrix[b2][i]);
          } 
          paramWritableRaster.setPixel(i2, n, arrayOfInt2);
          b1++;
          i1++;
          i2++;
        } 
        b++;
        k++;
        n++;
      } 
    } 
    return paramWritableRaster;
  }
  
  public final Rectangle2D getBounds2D(Raster paramRaster) { return paramRaster.getBounds(); }
  
  public WritableRaster createCompatibleDestRaster(Raster paramRaster) {
    int i = paramRaster.getNumBands();
    if (this.ncols != i && this.ncols != i + 1)
      throw new IllegalArgumentException("Number of columns in the matrix (" + this.ncols + ") must be equal to the number of bands ([+1]) in src (" + i + ")."); 
    if (paramRaster.getNumBands() == this.nrows)
      return paramRaster.createCompatibleWritableRaster(); 
    throw new IllegalArgumentException("Don't know how to create a  compatible Raster with " + this.nrows + " bands.");
  }
  
  public final Point2D getPoint2D(Point2D paramPoint2D1, Point2D paramPoint2D2) {
    if (paramPoint2D2 == null)
      paramPoint2D2 = new Point2D.Float(); 
    paramPoint2D2.setLocation(paramPoint2D1.getX(), paramPoint2D1.getY());
    return paramPoint2D2;
  }
  
  public final RenderingHints getRenderingHints() { return this.hints; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\BandCombineOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */