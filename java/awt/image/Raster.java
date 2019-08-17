package java.awt.image;

import java.awt.Point;
import java.awt.Rectangle;
import sun.awt.image.ByteBandedRaster;
import sun.awt.image.ByteInterleavedRaster;
import sun.awt.image.BytePackedRaster;
import sun.awt.image.IntegerInterleavedRaster;
import sun.awt.image.ShortBandedRaster;
import sun.awt.image.ShortInterleavedRaster;
import sun.awt.image.SunWritableRaster;

public class Raster {
  protected SampleModel sampleModel;
  
  protected DataBuffer dataBuffer;
  
  protected int minX;
  
  protected int minY;
  
  protected int width;
  
  protected int height;
  
  protected int sampleModelTranslateX;
  
  protected int sampleModelTranslateY;
  
  protected int numBands;
  
  protected int numDataElements;
  
  protected Raster parent;
  
  private static native void initIDs();
  
  public static WritableRaster createInterleavedRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Point paramPoint) {
    int[] arrayOfInt = new int[paramInt4];
    for (byte b = 0; b < paramInt4; b++)
      arrayOfInt[b] = b; 
    return createInterleavedRaster(paramInt1, paramInt2, paramInt3, paramInt2 * paramInt4, paramInt4, arrayOfInt, paramPoint);
  }
  
  public static WritableRaster createInterleavedRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt, Point paramPoint) {
    DataBufferByte dataBufferByte;
    DataBufferUShort dataBufferUShort;
    int i = paramInt4 * (paramInt3 - 1) + paramInt5 * paramInt2;
    switch (paramInt1) {
      case 0:
        dataBufferByte = new DataBufferByte(i);
        return createInterleavedRaster(dataBufferByte, paramInt2, paramInt3, paramInt4, paramInt5, paramArrayOfInt, paramPoint);
      case 1:
        dataBufferUShort = new DataBufferUShort(i);
        return createInterleavedRaster(dataBufferUShort, paramInt2, paramInt3, paramInt4, paramInt5, paramArrayOfInt, paramPoint);
    } 
    throw new IllegalArgumentException("Unsupported data type " + paramInt1);
  }
  
  public static WritableRaster createBandedRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Point paramPoint) {
    if (paramInt4 < 1)
      throw new ArrayIndexOutOfBoundsException("Number of bands (" + paramInt4 + ") must be greater than 0"); 
    int[] arrayOfInt1 = new int[paramInt4];
    int[] arrayOfInt2 = new int[paramInt4];
    for (byte b = 0; b < paramInt4; b++) {
      arrayOfInt1[b] = b;
      arrayOfInt2[b] = 0;
    } 
    return createBandedRaster(paramInt1, paramInt2, paramInt3, paramInt2, arrayOfInt1, arrayOfInt2, paramPoint);
  }
  
  public static WritableRaster createBandedRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2, Point paramPoint) {
    DataBufferUShort dataBufferUShort;
    DataBufferByte dataBufferByte;
    DataBufferInt dataBufferInt;
    int i = paramArrayOfInt2.length;
    if (paramArrayOfInt1 == null)
      throw new ArrayIndexOutOfBoundsException("Bank indices array is null"); 
    if (paramArrayOfInt2 == null)
      throw new ArrayIndexOutOfBoundsException("Band offsets array is null"); 
    int j = paramArrayOfInt1[0];
    int k = paramArrayOfInt2[0];
    int m;
    for (m = 1; m < i; m++) {
      if (paramArrayOfInt1[m] > j)
        j = paramArrayOfInt1[m]; 
      if (paramArrayOfInt2[m] > k)
        k = paramArrayOfInt2[m]; 
    } 
    m = j + 1;
    int n = k + paramInt4 * (paramInt3 - 1) + paramInt2;
    switch (paramInt1) {
      case 0:
        dataBufferByte = new DataBufferByte(n, m);
        return createBandedRaster(dataBufferByte, paramInt2, paramInt3, paramInt4, paramArrayOfInt1, paramArrayOfInt2, paramPoint);
      case 1:
        dataBufferUShort = new DataBufferUShort(n, m);
        return createBandedRaster(dataBufferUShort, paramInt2, paramInt3, paramInt4, paramArrayOfInt1, paramArrayOfInt2, paramPoint);
      case 3:
        dataBufferInt = new DataBufferInt(n, m);
        return createBandedRaster(dataBufferInt, paramInt2, paramInt3, paramInt4, paramArrayOfInt1, paramArrayOfInt2, paramPoint);
    } 
    throw new IllegalArgumentException("Unsupported data type " + paramInt1);
  }
  
  public static WritableRaster createPackedRaster(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt, Point paramPoint) {
    DataBufferUShort dataBufferUShort;
    DataBufferByte dataBufferByte;
    DataBufferInt dataBufferInt;
    switch (paramInt1) {
      case 0:
        dataBufferByte = new DataBufferByte(paramInt2 * paramInt3);
        return createPackedRaster(dataBufferByte, paramInt2, paramInt3, paramInt2, paramArrayOfInt, paramPoint);
      case 1:
        dataBufferUShort = new DataBufferUShort(paramInt2 * paramInt3);
        return createPackedRaster(dataBufferUShort, paramInt2, paramInt3, paramInt2, paramArrayOfInt, paramPoint);
      case 3:
        dataBufferInt = new DataBufferInt(paramInt2 * paramInt3);
        return createPackedRaster(dataBufferInt, paramInt2, paramInt3, paramInt2, paramArrayOfInt, paramPoint);
    } 
    throw new IllegalArgumentException("Unsupported data type " + paramInt1);
  }
  
  public static WritableRaster createPackedRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Point paramPoint) {
    DataBufferUShort dataBufferUShort;
    DataBufferByte dataBufferByte;
    DataBufferInt dataBufferInt;
    if (paramInt4 <= 0)
      throw new IllegalArgumentException("Number of bands (" + paramInt4 + ") must be greater than 0"); 
    if (paramInt5 <= 0)
      throw new IllegalArgumentException("Bits per band (" + paramInt5 + ") must be greater than 0"); 
    if (paramInt4 != 1) {
      int[] arrayOfInt = new int[paramInt4];
      int i = (1 << paramInt5) - 1;
      int j = (paramInt4 - 1) * paramInt5;
      if (j + paramInt5 > DataBuffer.getDataTypeSize(paramInt1))
        throw new IllegalArgumentException("bitsPerBand(" + paramInt5 + ") * bands is  greater than data type size."); 
      switch (paramInt1) {
        case 0:
        case 1:
        case 3:
          break;
        default:
          throw new IllegalArgumentException("Unsupported data type " + paramInt1);
      } 
      for (byte b = 0; b < paramInt4; b++) {
        arrayOfInt[b] = i << j;
        j -= paramInt5;
      } 
      return createPackedRaster(paramInt1, paramInt2, paramInt3, arrayOfInt, paramPoint);
    } 
    double d = paramInt2;
    switch (paramInt1) {
      case 0:
        dataBufferByte = new DataBufferByte((int)Math.ceil(d / (8 / paramInt5)) * paramInt3);
        return createPackedRaster(dataBufferByte, paramInt2, paramInt3, paramInt5, paramPoint);
      case 1:
        dataBufferUShort = new DataBufferUShort((int)Math.ceil(d / (16 / paramInt5)) * paramInt3);
        return createPackedRaster(dataBufferUShort, paramInt2, paramInt3, paramInt5, paramPoint);
      case 3:
        dataBufferInt = new DataBufferInt((int)Math.ceil(d / (32 / paramInt5)) * paramInt3);
        return createPackedRaster(dataBufferInt, paramInt2, paramInt3, paramInt5, paramPoint);
    } 
    throw new IllegalArgumentException("Unsupported data type " + paramInt1);
  }
  
  public static WritableRaster createInterleavedRaster(DataBuffer paramDataBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, Point paramPoint) {
    if (paramDataBuffer == null)
      throw new NullPointerException("DataBuffer cannot be null"); 
    if (paramPoint == null)
      paramPoint = new Point(0, 0); 
    int i = paramDataBuffer.getDataType();
    PixelInterleavedSampleModel pixelInterleavedSampleModel = new PixelInterleavedSampleModel(i, paramInt1, paramInt2, paramInt4, paramInt3, paramArrayOfInt);
    switch (i) {
      case 0:
        return new ByteInterleavedRaster(pixelInterleavedSampleModel, paramDataBuffer, paramPoint);
      case 1:
        return new ShortInterleavedRaster(pixelInterleavedSampleModel, paramDataBuffer, paramPoint);
    } 
    throw new IllegalArgumentException("Unsupported data type " + i);
  }
  
  public static WritableRaster createBandedRaster(DataBuffer paramDataBuffer, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt1, int[] paramArrayOfInt2, Point paramPoint) {
    if (paramDataBuffer == null)
      throw new NullPointerException("DataBuffer cannot be null"); 
    if (paramPoint == null)
      paramPoint = new Point(0, 0); 
    int i = paramDataBuffer.getDataType();
    int j = paramArrayOfInt1.length;
    if (paramArrayOfInt2.length != j)
      throw new IllegalArgumentException("bankIndices.length != bandOffsets.length"); 
    BandedSampleModel bandedSampleModel = new BandedSampleModel(i, paramInt1, paramInt2, paramInt3, paramArrayOfInt1, paramArrayOfInt2);
    switch (i) {
      case 0:
        return new ByteBandedRaster(bandedSampleModel, paramDataBuffer, paramPoint);
      case 1:
        return new ShortBandedRaster(bandedSampleModel, paramDataBuffer, paramPoint);
      case 3:
        return new SunWritableRaster(bandedSampleModel, paramDataBuffer, paramPoint);
    } 
    throw new IllegalArgumentException("Unsupported data type " + i);
  }
  
  public static WritableRaster createPackedRaster(DataBuffer paramDataBuffer, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt, Point paramPoint) {
    if (paramDataBuffer == null)
      throw new NullPointerException("DataBuffer cannot be null"); 
    if (paramPoint == null)
      paramPoint = new Point(0, 0); 
    int i = paramDataBuffer.getDataType();
    SinglePixelPackedSampleModel singlePixelPackedSampleModel = new SinglePixelPackedSampleModel(i, paramInt1, paramInt2, paramInt3, paramArrayOfInt);
    switch (i) {
      case 0:
        return new ByteInterleavedRaster(singlePixelPackedSampleModel, paramDataBuffer, paramPoint);
      case 1:
        return new ShortInterleavedRaster(singlePixelPackedSampleModel, paramDataBuffer, paramPoint);
      case 3:
        return new IntegerInterleavedRaster(singlePixelPackedSampleModel, paramDataBuffer, paramPoint);
    } 
    throw new IllegalArgumentException("Unsupported data type " + i);
  }
  
  public static WritableRaster createPackedRaster(DataBuffer paramDataBuffer, int paramInt1, int paramInt2, int paramInt3, Point paramPoint) {
    if (paramDataBuffer == null)
      throw new NullPointerException("DataBuffer cannot be null"); 
    if (paramPoint == null)
      paramPoint = new Point(0, 0); 
    int i = paramDataBuffer.getDataType();
    if (i != 0 && i != 1 && i != 3)
      throw new IllegalArgumentException("Unsupported data type " + i); 
    if (paramDataBuffer.getNumBanks() != 1)
      throw new RasterFormatException("DataBuffer for packed Rasters must only have 1 bank."); 
    MultiPixelPackedSampleModel multiPixelPackedSampleModel = new MultiPixelPackedSampleModel(i, paramInt1, paramInt2, paramInt3);
    return (i == 0 && (paramInt3 == 1 || paramInt3 == 2 || paramInt3 == 4)) ? new BytePackedRaster(multiPixelPackedSampleModel, paramDataBuffer, paramPoint) : new SunWritableRaster(multiPixelPackedSampleModel, paramDataBuffer, paramPoint);
  }
  
  public static Raster createRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint) {
    if (paramSampleModel == null || paramDataBuffer == null)
      throw new NullPointerException("SampleModel and DataBuffer cannot be null"); 
    if (paramPoint == null)
      paramPoint = new Point(0, 0); 
    int i = paramSampleModel.getDataType();
    if (paramSampleModel instanceof PixelInterleavedSampleModel) {
      switch (i) {
        case 0:
          return new ByteInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
        case 1:
          return new ShortInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
      } 
    } else if (paramSampleModel instanceof SinglePixelPackedSampleModel) {
      switch (i) {
        case 0:
          return new ByteInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
        case 1:
          return new ShortInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
        case 3:
          return new IntegerInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
      } 
    } else if (paramSampleModel instanceof MultiPixelPackedSampleModel && i == 0 && paramSampleModel.getSampleSize(0) < 8) {
      return new BytePackedRaster(paramSampleModel, paramDataBuffer, paramPoint);
    } 
    return new Raster(paramSampleModel, paramDataBuffer, paramPoint);
  }
  
  public static WritableRaster createWritableRaster(SampleModel paramSampleModel, Point paramPoint) {
    if (paramPoint == null)
      paramPoint = new Point(0, 0); 
    return createWritableRaster(paramSampleModel, paramSampleModel.createDataBuffer(), paramPoint);
  }
  
  public static WritableRaster createWritableRaster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint) {
    if (paramSampleModel == null || paramDataBuffer == null)
      throw new NullPointerException("SampleModel and DataBuffer cannot be null"); 
    if (paramPoint == null)
      paramPoint = new Point(0, 0); 
    int i = paramSampleModel.getDataType();
    if (paramSampleModel instanceof PixelInterleavedSampleModel) {
      switch (i) {
        case 0:
          return new ByteInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
        case 1:
          return new ShortInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
      } 
    } else if (paramSampleModel instanceof SinglePixelPackedSampleModel) {
      switch (i) {
        case 0:
          return new ByteInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
        case 1:
          return new ShortInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
        case 3:
          return new IntegerInterleavedRaster(paramSampleModel, paramDataBuffer, paramPoint);
      } 
    } else if (paramSampleModel instanceof MultiPixelPackedSampleModel && i == 0 && paramSampleModel.getSampleSize(0) < 8) {
      return new BytePackedRaster(paramSampleModel, paramDataBuffer, paramPoint);
    } 
    return new SunWritableRaster(paramSampleModel, paramDataBuffer, paramPoint);
  }
  
  protected Raster(SampleModel paramSampleModel, Point paramPoint) { this(paramSampleModel, paramSampleModel.createDataBuffer(), new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null); }
  
  protected Raster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Point paramPoint) { this(paramSampleModel, paramDataBuffer, new Rectangle(paramPoint.x, paramPoint.y, paramSampleModel.getWidth(), paramSampleModel.getHeight()), paramPoint, null); }
  
  protected Raster(SampleModel paramSampleModel, DataBuffer paramDataBuffer, Rectangle paramRectangle, Point paramPoint, Raster paramRaster) {
    if (paramSampleModel == null || paramDataBuffer == null || paramRectangle == null || paramPoint == null)
      throw new NullPointerException("SampleModel, dataBuffer, aRegion and sampleModelTranslate cannot be null"); 
    this.sampleModel = paramSampleModel;
    this.dataBuffer = paramDataBuffer;
    this.minX = paramRectangle.x;
    this.minY = paramRectangle.y;
    this.width = paramRectangle.width;
    this.height = paramRectangle.height;
    if (this.width <= 0 || this.height <= 0)
      throw new RasterFormatException("negative or zero " + ((this.width <= 0) ? "width" : "height")); 
    if (this.minX + this.width < this.minX)
      throw new RasterFormatException("overflow condition for X coordinates of Raster"); 
    if (this.minY + this.height < this.minY)
      throw new RasterFormatException("overflow condition for Y coordinates of Raster"); 
    this.sampleModelTranslateX = paramPoint.x;
    this.sampleModelTranslateY = paramPoint.y;
    this.numBands = paramSampleModel.getNumBands();
    this.numDataElements = paramSampleModel.getNumDataElements();
    this.parent = paramRaster;
  }
  
  public Raster getParent() { return this.parent; }
  
  public final int getSampleModelTranslateX() { return this.sampleModelTranslateX; }
  
  public final int getSampleModelTranslateY() { return this.sampleModelTranslateY; }
  
  public WritableRaster createCompatibleWritableRaster() { return new SunWritableRaster(this.sampleModel, new Point(0, 0)); }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2) {
    if (paramInt1 <= 0 || paramInt2 <= 0)
      throw new RasterFormatException("negative " + ((paramInt1 <= 0) ? "width" : "height")); 
    SampleModel sampleModel1 = this.sampleModel.createCompatibleSampleModel(paramInt1, paramInt2);
    return new SunWritableRaster(sampleModel1, new Point(0, 0));
  }
  
  public WritableRaster createCompatibleWritableRaster(Rectangle paramRectangle) {
    if (paramRectangle == null)
      throw new NullPointerException("Rect cannot be null"); 
    return createCompatibleWritableRaster(paramRectangle.x, paramRectangle.y, paramRectangle.width, paramRectangle.height);
  }
  
  public WritableRaster createCompatibleWritableRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    WritableRaster writableRaster = createCompatibleWritableRaster(paramInt3, paramInt4);
    return writableRaster.createWritableChild(0, 0, paramInt3, paramInt4, paramInt1, paramInt2, null);
  }
  
  public Raster createTranslatedChild(int paramInt1, int paramInt2) { return createChild(this.minX, this.minY, this.width, this.height, paramInt1, paramInt2, null); }
  
  public Raster createChild(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int[] paramArrayOfInt) {
    SampleModel sampleModel1;
    if (paramInt1 < this.minX)
      throw new RasterFormatException("parentX lies outside raster"); 
    if (paramInt2 < this.minY)
      throw new RasterFormatException("parentY lies outside raster"); 
    if (paramInt1 + paramInt3 < paramInt1 || paramInt1 + paramInt3 > this.width + this.minX)
      throw new RasterFormatException("(parentX + width) is outside raster"); 
    if (paramInt2 + paramInt4 < paramInt2 || paramInt2 + paramInt4 > this.height + this.minY)
      throw new RasterFormatException("(parentY + height) is outside raster"); 
    if (paramArrayOfInt == null) {
      sampleModel1 = this.sampleModel;
    } else {
      sampleModel1 = this.sampleModel.createSubsetSampleModel(paramArrayOfInt);
    } 
    int i = paramInt5 - paramInt1;
    int j = paramInt6 - paramInt2;
    return new Raster(sampleModel1, getDataBuffer(), new Rectangle(paramInt5, paramInt6, paramInt3, paramInt4), new Point(this.sampleModelTranslateX + i, this.sampleModelTranslateY + j), this);
  }
  
  public Rectangle getBounds() { return new Rectangle(this.minX, this.minY, this.width, this.height); }
  
  public final int getMinX() { return this.minX; }
  
  public final int getMinY() { return this.minY; }
  
  public final int getWidth() { return this.width; }
  
  public final int getHeight() { return this.height; }
  
  public final int getNumBands() { return this.numBands; }
  
  public final int getNumDataElements() { return this.sampleModel.getNumDataElements(); }
  
  public final int getTransferType() { return this.sampleModel.getTransferType(); }
  
  public DataBuffer getDataBuffer() { return this.dataBuffer; }
  
  public SampleModel getSampleModel() { return this.sampleModel; }
  
  public Object getDataElements(int paramInt1, int paramInt2, Object paramObject) { return this.sampleModel.getDataElements(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramObject, this.dataBuffer); }
  
  public Object getDataElements(int paramInt1, int paramInt2, int paramInt3, int paramInt4, Object paramObject) { return this.sampleModel.getDataElements(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramObject, this.dataBuffer); }
  
  public int[] getPixel(int paramInt1, int paramInt2, int[] paramArrayOfInt) { return this.sampleModel.getPixel(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramArrayOfInt, this.dataBuffer); }
  
  public float[] getPixel(int paramInt1, int paramInt2, float[] paramArrayOfFloat) { return this.sampleModel.getPixel(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramArrayOfFloat, this.dataBuffer); }
  
  public double[] getPixel(int paramInt1, int paramInt2, double[] paramArrayOfDouble) { return this.sampleModel.getPixel(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramArrayOfDouble, this.dataBuffer); }
  
  public int[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt) { return this.sampleModel.getPixels(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramArrayOfInt, this.dataBuffer); }
  
  public float[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat) { return this.sampleModel.getPixels(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramArrayOfFloat, this.dataBuffer); }
  
  public double[] getPixels(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[] paramArrayOfDouble) { return this.sampleModel.getPixels(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramArrayOfDouble, this.dataBuffer); }
  
  public int getSample(int paramInt1, int paramInt2, int paramInt3) { return this.sampleModel.getSample(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, this.dataBuffer); }
  
  public float getSampleFloat(int paramInt1, int paramInt2, int paramInt3) { return this.sampleModel.getSampleFloat(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, this.dataBuffer); }
  
  public double getSampleDouble(int paramInt1, int paramInt2, int paramInt3) { return this.sampleModel.getSampleDouble(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, this.dataBuffer); }
  
  public int[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfInt) { return this.sampleModel.getSamples(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramInt5, paramArrayOfInt, this.dataBuffer); }
  
  public float[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, float[] paramArrayOfFloat) { return this.sampleModel.getSamples(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramInt5, paramArrayOfFloat, this.dataBuffer); }
  
  public double[] getSamples(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, double[] paramArrayOfDouble) { return this.sampleModel.getSamples(paramInt1 - this.sampleModelTranslateX, paramInt2 - this.sampleModelTranslateY, paramInt3, paramInt4, paramInt5, paramArrayOfDouble, this.dataBuffer); }
  
  static  {
    ColorModel.loadLibraries();
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\image\Raster.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */