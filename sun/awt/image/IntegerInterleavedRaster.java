package sun.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.RasterFormatException;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;

public class IntegerInterleavedRaster extends IntegerComponentRaster {
  private int maxX = this.minX + this.width;
  
  private int maxY = this.minY + this.height;
  
  public IntegerInterleavedRaster(SampleModel paramSampleModel, Point paramPoint) { this(paramSampleModel, paramSampleModel.createDataBuffer(), new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null); }
  
  public IntegerInterleavedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint) { this(paramSampleModel, paramDataBuffer, new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null); }
  
  public IntegerInterleavedRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, IntegerInterleavedRaster paramIntegerInterleavedRaster) {
    super(paramSampleModel, paramDataBuffer, paramRectangle, paramPoint, paramIntegerInterleavedRaster);
    if (!(paramDataBuffer instanceof DataBufferInt))
      throw new RasterFormatException("IntegerInterleavedRasters must haveinteger DataBuffers"); 
    DataBufferInt dataBufferInt = (DataBufferInt)paramDataBuffer;
    this.data = stealData(dataBufferInt, 0);
    if (paramSampleModel instanceof SinglePixelPackedSampleModel) {
      SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)paramSampleModel;
      this.scanlineStride = singlePixelPackedSampleModel.getScanlineStride();
      this.pixelStride = 1;
      this.dataOffsets = new int[1];
      this.dataOffsets[0] = dataBufferInt.getOffset();
      this.bandOffset = this.dataOffsets[0];
      int i = paramRectangle.x - paramPoint.x;
      int j = paramRectangle.y - paramPoint.y;
      this.dataOffsets[0] = this.dataOffsets[0] + i + j * this.scanlineStride;
      this.numDataElems = singlePixelPackedSampleModel.getNumDataElements();
    } else {
      throw new RasterFormatException("IntegerInterleavedRasters must have SinglePixelPackedSampleModel");
    } 
    verify();
  }
  
  public int[] getDataOffsets() { return (int[])this.dataOffsets.clone(); }
  
  public int getDataOffset(int paramInt) { return this.dataOffsets[paramInt]; }
  
  public int getScanlineStride() { return this.scanlineStride; }
  
  public int getPixelStride() { return this.pixelStride; }
  
  public int[] getDataStorage() { return this.data; }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject) {
    int[] arrayOfInt;
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 >= this.maxX || paramInt2 >= this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramObject == null) {
      arrayOfInt = new int[1];
    } else {
      arrayOfInt = (int[])paramObject;
    } 
    int i = (paramInt2 - this.minY) * this.scanlineStride + paramInt1 - this.minX + this.dataOffsets[0];
    arrayOfInt[0] = this.data[i];
    return arrayOfInt;
  }
  
  public Object getDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) {
    int[] arrayOfInt;
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    if (paramObject instanceof int[]) {
      arrayOfInt = (int[])paramObject;
    } else {
      arrayOfInt = new int[paramInt3 * paramInt4];
    } 
    int i = (paramInt2 - this.minY) * this.scanlineStride + paramInt1 - this.minX + this.dataOffsets[0];
    int j = 0;
    for (byte b = 0; b < paramInt4; b++) {
      System.arraycopy(this.data, i, arrayOfInt, j, paramInt3);
      j += paramInt3;
      i += this.scanlineStride;
    } 
    return arrayOfInt;
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Object paramObject) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 >= this.maxX || paramInt2 >= this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int[] arrayOfInt = (int[])paramObject;
    int i = (paramInt2 - this.minY) * this.scanlineStride + paramInt1 - this.minX + this.dataOffsets[0];
    this.data[i] = arrayOfInt[0];
    markDirty();
  }
  
  public void setDataElements(int paramInt1, int paramInt2, Raster paramRaster) {
    int i = paramInt1 + paramRaster.getMinX();
    int j = paramInt2 + paramRaster.getMinY();
    int k = paramRaster.getWidth();
    int m = paramRaster.getHeight();
    if (i < this.minX || j < this.minY || i + k > this.maxX || j + m > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    setDataElements(i, j, k, m, paramRaster);
  }
  
  private void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Raster paramRaster) {
    if (paramInt3 <= 0 || paramInt4 <= 0)
      return; 
    int i = paramRaster.getMinX();
    int j = paramRaster.getMinY();
    int[] arrayOfInt = null;
    if (paramRaster instanceof IntegerInterleavedRaster) {
      IntegerInterleavedRaster integerInterleavedRaster = (IntegerInterleavedRaster)paramRaster;
      arrayOfInt = integerInterleavedRaster.getDataStorage();
      int m = integerInterleavedRaster.getScanlineStride();
      int n = integerInterleavedRaster.getDataOffset(0);
      int i1 = n;
      int i2 = this.dataOffsets[0] + (paramInt2 - this.minY) * this.scanlineStride + paramInt1 - this.minX;
      for (byte b = 0; b < paramInt4; b++) {
        System.arraycopy(arrayOfInt, i1, this.data, i2, paramInt3);
        i1 += m;
        i2 += this.scanlineStride;
      } 
      markDirty();
      return;
    } 
    Object object = null;
    for (int k = 0; k < paramInt4; k++) {
      object = paramRaster.getDataElements(i, j + k, paramInt3, 1, object);
      setDataElements(paramInt1, paramInt2 + k, paramInt3, 1, object);
    } 
  }
  
  public void setDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) {
    if (paramInt1 < this.minX || paramInt2 < this.minY || paramInt1 + paramInt3 > this.maxX || paramInt2 + paramInt4 > this.maxY)
      throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!"); 
    int[] arrayOfInt = (int[])paramObject;
    int i = (paramInt2 - this.minY) * this.scanlineStride + paramInt1 - this.minX + this.dataOffsets[0];
    int j = 0;
    for (byte b = 0; b < paramInt4; b++) {
      System.arraycopy(arrayOfInt, j, this.data, i, paramInt3);
      j += paramInt3;
      i += this.scanlineStride;
    } 
    markDirty();
  }
  
  public WritableRaster createWritableChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt) {
    SampleModel sampleModel;
    if (paramInt1 < this.minX)
      throw new RasterFormatException("x lies outside raster"); 
    if (paramInt2 < this.minY)
      throw new RasterFormatException("y lies outside raster"); 
    if (paramInt1 + paramInt3 < paramInt1 || paramInt1 + paramInt3 > this.minX + this.width)
      throw new RasterFormatException("(x + width) is outside raster"); 
    if (paramInt2 + paramInt4 < paramInt2 || paramInt2 + paramInt4 > this.minY + this.height)
      throw new RasterFormatException("(y + height) is outside raster"); 
    if (paramArrayOfInt != null) {
      sampleModel = this.sampleModel.createSubsetSampleModel(paramArrayOfInt);
    } else {
      sampleModel = this.sampleModel;
    } 
    int i = paramInt5 - paramInt1;
    int j = paramInt6 - paramInt2;
    return new IntegerInterleavedRaster(sampleModel, this.dataBuffer, new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(this.sampleModelTranslateX + i, this.sampleModelTranslateY + j), this);
  }
  
  public Raster createChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt) { return createWritableChild(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramArrayOfInt); }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2) {
    if (paramInt1 <= 0 || paramInt2 <= 0)
      throw new RasterFormatException("negative " + ((paramInt1 <= 0) ? "width" : "height")); 
    SampleModel sampleModel = this.sampleModel.createCompatibleSampleModel(paramInt1, paramInt2);
    return new IntegerInterleavedRaster(sampleModel, new Point(0, 0));
  }
  
  public WritableRaster createCompatibleWritableRaster() { return createCompatibleWritableRaster(this.width, this.height); }
  
  public String toString() { return new String("IntegerInterleavedRaster: width = " + this.width + " height = " + this.height + " #Bands = " + this.numBands + " xOff = " + this.sampleModelTranslateX + " yOff = " + this.sampleModelTranslateY + " dataOffset[0] " + this.dataOffsets[0]); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\IntegerInterleavedRaster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */