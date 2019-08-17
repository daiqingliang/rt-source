package sun.awt.image.codec;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RescaleOp;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class JPEGImageEncoderImpl implements JPEGImageEncoder {
  private OutputStream outStream = null;
  
  private JPEGParam param = null;
  
  private boolean pack = false;
  
  private static final Class OutputStreamClass = OutputStream.class;
  
  public JPEGImageEncoderImpl(OutputStream paramOutputStream) {
    if (paramOutputStream == null)
      throw new IllegalArgumentException("OutputStream is null."); 
    this.outStream = paramOutputStream;
    initEncoder(OutputStreamClass);
  }
  
  public JPEGImageEncoderImpl(OutputStream paramOutputStream, JPEGEncodeParam paramJPEGEncodeParam) {
    this(paramOutputStream);
    setJPEGEncodeParam(paramJPEGEncodeParam);
  }
  
  public int getDefaultColorId(ColorModel paramColorModel) {
    boolean bool = paramColorModel.hasAlpha();
    ColorSpace colorSpace1 = paramColorModel.getColorSpace();
    ColorSpace colorSpace2 = null;
    switch (colorSpace1.getType()) {
      case 6:
        return 1;
      case 5:
        return bool ? 7 : 3;
      case 3:
        if (colorSpace2 == null)
          try {
            colorSpace2 = ColorSpace.getInstance(1002);
          } catch (IllegalArgumentException illegalArgumentException) {} 
        return (colorSpace1 == colorSpace2) ? (bool ? 10 : 5) : (bool ? 7 : 3);
      case 9:
        return 4;
    } 
    return 0;
  }
  
  public OutputStream getOutputStream() { return this.outStream; }
  
  public void setJPEGEncodeParam(JPEGEncodeParam paramJPEGEncodeParam) { this.param = new JPEGParam(paramJPEGEncodeParam); }
  
  public JPEGEncodeParam getJPEGEncodeParam() { return (JPEGEncodeParam)this.param.clone(); }
  
  public JPEGEncodeParam getDefaultJPEGEncodeParam(Raster paramRaster, int paramInt) {
    JPEGParam jPEGParam = new JPEGParam(paramInt, paramRaster.getNumBands());
    jPEGParam.setWidth(paramRaster.getWidth());
    jPEGParam.setHeight(paramRaster.getHeight());
    return jPEGParam;
  }
  
  public JPEGEncodeParam getDefaultJPEGEncodeParam(BufferedImage paramBufferedImage) {
    JPEGParam jPEGParam;
    ColorModel colorModel = paramBufferedImage.getColorModel();
    int i = getDefaultColorId(colorModel);
    if (!(colorModel instanceof IndexColorModel))
      return getDefaultJPEGEncodeParam(paramBufferedImage.getRaster(), i); 
    if (colorModel.hasAlpha()) {
      jPEGParam = new JPEGParam(i, 4);
    } else {
      jPEGParam = new JPEGParam(i, 3);
    } 
    jPEGParam.setWidth(paramBufferedImage.getWidth());
    jPEGParam.setHeight(paramBufferedImage.getHeight());
    return jPEGParam;
  }
  
  public JPEGEncodeParam getDefaultJPEGEncodeParam(int paramInt1, int paramInt2) { return new JPEGParam(paramInt2, paramInt1); }
  
  public JPEGEncodeParam getDefaultJPEGEncodeParam(JPEGDecodeParam paramJPEGDecodeParam) throws ImageFormatException { return new JPEGParam(paramJPEGDecodeParam); }
  
  public void encode(BufferedImage paramBufferedImage) throws IOException, ImageFormatException {
    if (this.param == null)
      setJPEGEncodeParam(getDefaultJPEGEncodeParam(paramBufferedImage)); 
    if (paramBufferedImage.getWidth() != this.param.getWidth() || paramBufferedImage.getHeight() != this.param.getHeight())
      throw new ImageFormatException("Param block's width/height doesn't match the BufferedImage"); 
    if (this.param.getEncodedColorID() != getDefaultColorId(paramBufferedImage.getColorModel()))
      throw new ImageFormatException("The encoded COLOR_ID doesn't match the BufferedImage"); 
    WritableRaster writableRaster = paramBufferedImage.getRaster();
    ColorModel colorModel = paramBufferedImage.getColorModel();
    if (colorModel instanceof IndexColorModel) {
      IndexColorModel indexColorModel = (IndexColorModel)colorModel;
      paramBufferedImage = indexColorModel.convertToIntDiscrete(writableRaster, false);
      writableRaster = paramBufferedImage.getRaster();
      colorModel = paramBufferedImage.getColorModel();
    } 
    encode(writableRaster, colorModel);
  }
  
  public void encode(BufferedImage paramBufferedImage, JPEGEncodeParam paramJPEGEncodeParam) throws IOException, ImageFormatException {
    setJPEGEncodeParam(paramJPEGEncodeParam);
    encode(paramBufferedImage);
  }
  
  public void encode(Raster paramRaster) throws IOException, ImageFormatException {
    if (this.param == null)
      setJPEGEncodeParam(getDefaultJPEGEncodeParam(paramRaster, 0)); 
    if (paramRaster.getNumBands() != paramRaster.getSampleModel().getNumBands())
      throw new ImageFormatException("Raster's number of bands doesn't match the SampleModel"); 
    if (paramRaster.getWidth() != this.param.getWidth() || paramRaster.getHeight() != this.param.getHeight())
      throw new ImageFormatException("Param block's width/height doesn't match the Raster"); 
    if (this.param.getEncodedColorID() != 0 && this.param.getNumComponents() != paramRaster.getNumBands())
      throw new ImageFormatException("Param block's COLOR_ID doesn't match the Raster."); 
    encode(paramRaster, (ColorModel)null);
  }
  
  public void encode(Raster paramRaster, JPEGEncodeParam paramJPEGEncodeParam) throws IOException, ImageFormatException {
    setJPEGEncodeParam(paramJPEGEncodeParam);
    encode(paramRaster);
  }
  
  private boolean useGiven(Raster paramRaster) {
    SampleModel sampleModel = paramRaster.getSampleModel();
    if (sampleModel.getDataType() != 0)
      return false; 
    if (!(sampleModel instanceof ComponentSampleModel))
      return false; 
    ComponentSampleModel componentSampleModel = (ComponentSampleModel)sampleModel;
    if (componentSampleModel.getPixelStride() != sampleModel.getNumBands())
      return false; 
    int[] arrayOfInt = componentSampleModel.getBandOffsets();
    for (byte b = 0; b < arrayOfInt.length; b++) {
      if (arrayOfInt[b] != b)
        return false; 
    } 
    return true;
  }
  
  private boolean canPack(Raster paramRaster) {
    SampleModel sampleModel = paramRaster.getSampleModel();
    if (sampleModel.getDataType() != 3)
      return false; 
    if (!(sampleModel instanceof SinglePixelPackedSampleModel))
      return false; 
    SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)sampleModel;
    int[] arrayOfInt1 = { 16711680, 65280, 255, -16777216 };
    int[] arrayOfInt2 = singlePixelPackedSampleModel.getBitMasks();
    if (arrayOfInt2.length != 3 && arrayOfInt2.length != 4)
      return false; 
    for (byte b = 0; b < arrayOfInt2.length; b++) {
      if (arrayOfInt2[b] != arrayOfInt1[b])
        return false; 
    } 
    return true;
  }
  
  private void encode(Raster paramRaster, ColorModel paramColorModel) throws IOException, ImageFormatException {
    int m;
    int k;
    byte[] arrayOfByte;
    SampleModel sampleModel = paramRaster.getSampleModel();
    int i = sampleModel.getNumBands();
    if (paramColorModel == null)
      for (byte b = 0; b < i; b++) {
        if (sampleModel.getSampleSize(b) > 8)
          throw new ImageFormatException("JPEG encoder can only accept 8 bit data."); 
      }  
    int j = this.param.getEncodedColorID();
    switch (this.param.getNumComponents()) {
      case 1:
        if (j != 1 && j != 0 && this.param.findAPP0() != null)
          throw new ImageFormatException("1 band JFIF files imply Y or unknown encoding.\nParam block indicates alternate encoding."); 
        break;
      case 3:
        if (j != 3 && this.param.findAPP0() != null)
          throw new ImageFormatException("3 band JFIF files imply YCbCr encoding.\nParam block indicates alternate encoding."); 
        break;
      case 4:
        if (j != 4 && this.param.findAPP0() != null)
          throw new ImageFormatException("4 band JFIF files imply CMYK encoding.\nParam block indicates alternate encoding."); 
        break;
    } 
    if (!this.param.isImageInfoValid()) {
      writeJPEGStream(this.param, paramColorModel, this.outStream, null, 0, 0);
      return;
    } 
    DataBuffer dataBuffer = paramRaster.getDataBuffer();
    boolean bool1 = false;
    boolean bool2 = true;
    int[] arrayOfInt = null;
    if (paramColorModel != null) {
      if (paramColorModel.hasAlpha() && paramColorModel.isAlphaPremultiplied()) {
        bool1 = true;
        bool2 = false;
      } 
      arrayOfInt = paramColorModel.getComponentSize();
      for (byte b = 0; b < i; b++) {
        if (arrayOfInt[b] != 8)
          bool2 = false; 
      } 
    } 
    this.pack = false;
    if (bool2 && useGiven(paramRaster)) {
      ComponentSampleModel componentSampleModel = (ComponentSampleModel)sampleModel;
      m = dataBuffer.getOffset() + componentSampleModel.getOffset(paramRaster.getMinX() - paramRaster.getSampleModelTranslateX(), paramRaster.getMinY() - paramRaster.getSampleModelTranslateY());
      k = componentSampleModel.getScanlineStride();
      arrayOfByte = ((DataBufferByte)dataBuffer).getData();
    } else if (bool2 && canPack(paramRaster)) {
      SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)sampleModel;
      m = dataBuffer.getOffset() + singlePixelPackedSampleModel.getOffset(paramRaster.getMinX() - paramRaster.getSampleModelTranslateX(), paramRaster.getMinY() - paramRaster.getSampleModelTranslateY());
      k = singlePixelPackedSampleModel.getScanlineStride();
      arrayOfByte = ((DataBufferInt)dataBuffer).getData();
      this.pack = true;
    } else {
      int[] arrayOfInt1 = new int[i];
      float[] arrayOfFloat = new float[i];
      byte b;
      for (b = 0; b < i; b++) {
        arrayOfInt1[b] = b;
        if (!bool2)
          if (arrayOfInt[b] != 8) {
            arrayOfFloat[b] = 255.0F / ((1 << arrayOfInt[b]) - 1);
          } else {
            arrayOfFloat[b] = 1.0F;
          }  
      } 
      ComponentSampleModel componentSampleModel = new ComponentSampleModel(0, paramRaster.getWidth(), paramRaster.getHeight(), i, i * paramRaster.getWidth(), arrayOfInt1);
      WritableRaster writableRaster = Raster.createWritableRaster(componentSampleModel, new Point(paramRaster.getMinX(), paramRaster.getMinY()));
      if (bool2) {
        writableRaster.setRect(paramRaster);
      } else {
        float[] arrayOfFloat1 = new float[i];
        RescaleOp rescaleOp = new RescaleOp(arrayOfFloat, arrayOfFloat1, null);
        rescaleOp.filter(paramRaster, writableRaster);
        if (bool1) {
          int[] arrayOfInt2 = new int[i];
          for (b = 0; b < i; b++)
            arrayOfInt2[b] = 8; 
          ComponentColorModel componentColorModel = new ComponentColorModel(paramColorModel.getColorSpace(), arrayOfInt2, true, true, 3, 0);
          componentColorModel.coerceData(writableRaster, false);
        } 
      } 
      dataBuffer = writableRaster.getDataBuffer();
      arrayOfByte = ((DataBufferByte)dataBuffer).getData();
      m = dataBuffer.getOffset() + componentSampleModel.getOffset(0, 0);
      k = componentSampleModel.getScanlineStride();
    } 
    verify(m, k, dataBuffer.getSize());
    writeJPEGStream(this.param, paramColorModel, this.outStream, arrayOfByte, m, k);
  }
  
  private void verify(int paramInt1, int paramInt2, int paramInt3) throws ImageFormatException {
    int i = this.param.getWidth();
    int j = this.param.getHeight();
    int k = this.pack ? 1 : this.param.getNumComponents();
    if (i <= 0 || j <= 0 || j > Integer.MAX_VALUE / i)
      throw new ImageFormatException("Invalid image dimensions"); 
    if (paramInt2 < 0 || paramInt2 > Integer.MAX_VALUE / j || paramInt2 > paramInt3)
      throw new ImageFormatException("Invalid scanline stride: " + paramInt2); 
    int m = (j - 1) * paramInt2;
    if (!k || k > Integer.MAX_VALUE / i || k > paramInt3 || k * i > paramInt2)
      throw new ImageFormatException("Invalid pixel stride: " + k); 
    int n = i * k;
    if (n > Integer.MAX_VALUE - m)
      throw new ImageFormatException("Invalid raster attributes"); 
    int i1 = m + n;
    if (paramInt1 < 0 || paramInt1 > Integer.MAX_VALUE - i1)
      throw new ImageFormatException("Invalid data offset"); 
    int i2 = paramInt1 + i1;
    if (i2 > paramInt3)
      throw new ImageFormatException("Computed buffer size doesn't match DataBuffer"); 
  }
  
  private int getNearestColorId(ColorModel paramColorModel) {
    ColorSpace colorSpace = paramColorModel.getColorSpace();
    switch (colorSpace.getType()) {
      case 5:
        return paramColorModel.hasAlpha() ? 6 : 2;
    } 
    return getDefaultColorId(paramColorModel);
  }
  
  private native void initEncoder(Class paramClass);
  
  private native void writeJPEGStream(JPEGEncodeParam paramJPEGEncodeParam, ColorModel paramColorModel, OutputStream paramOutputStream, Object paramObject, int paramInt1, int paramInt2) throws IOException, ImageFormatException;
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("jpeg");
            return null;
          }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\codec\JPEGImageEncoderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */