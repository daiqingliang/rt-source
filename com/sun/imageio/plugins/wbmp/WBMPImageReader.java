package com.sun.imageio.plugins.wbmp;

import com.sun.imageio.plugins.common.I18N;
import com.sun.imageio.plugins.common.ReaderUtil;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class WBMPImageReader extends ImageReader {
  private ImageInputStream iis = null;
  
  private boolean gotHeader = false;
  
  private int width;
  
  private int height;
  
  private int wbmpType;
  
  private WBMPMetadata metadata;
  
  public WBMPImageReader(ImageReaderSpi paramImageReaderSpi) { super(paramImageReaderSpi); }
  
  public void setInput(Object paramObject, boolean paramBoolean1, boolean paramBoolean2) {
    super.setInput(paramObject, paramBoolean1, paramBoolean2);
    this.iis = (ImageInputStream)paramObject;
    this.gotHeader = false;
  }
  
  public int getNumImages(boolean paramBoolean) throws IOException {
    if (this.iis == null)
      throw new IllegalStateException(I18N.getString("GetNumImages0")); 
    if (this.seekForwardOnly && paramBoolean)
      throw new IllegalStateException(I18N.getString("GetNumImages1")); 
    return 1;
  }
  
  public int getWidth(int paramInt) throws IOException {
    checkIndex(paramInt);
    readHeader();
    return this.width;
  }
  
  public int getHeight(int paramInt) throws IOException {
    checkIndex(paramInt);
    readHeader();
    return this.height;
  }
  
  public boolean isRandomAccessEasy(int paramInt) throws IOException {
    checkIndex(paramInt);
    return true;
  }
  
  private void checkIndex(int paramInt) {
    if (paramInt != 0)
      throw new IndexOutOfBoundsException(I18N.getString("WBMPImageReader0")); 
  }
  
  public void readHeader() throws IOException {
    if (this.gotHeader)
      return; 
    if (this.iis == null)
      throw new IllegalStateException("Input source not set!"); 
    this.metadata = new WBMPMetadata();
    this.wbmpType = this.iis.readByte();
    byte b = this.iis.readByte();
    if (b != 0 || !isValidWbmpType(this.wbmpType))
      throw new IIOException(I18N.getString("WBMPImageReader2")); 
    this.metadata.wbmpType = this.wbmpType;
    this.width = ReaderUtil.readMultiByteInteger(this.iis);
    this.metadata.width = this.width;
    this.height = ReaderUtil.readMultiByteInteger(this.iis);
    this.metadata.height = this.height;
    this.gotHeader = true;
  }
  
  public Iterator getImageTypes(int paramInt) throws IOException {
    checkIndex(paramInt);
    readHeader();
    BufferedImage bufferedImage = new BufferedImage(1, 1, 12);
    ArrayList arrayList = new ArrayList(1);
    arrayList.add(new ImageTypeSpecifier(bufferedImage));
    return arrayList.iterator();
  }
  
  public ImageReadParam getDefaultReadParam() { return new ImageReadParam(); }
  
  public IIOMetadata getImageMetadata(int paramInt) throws IOException {
    checkIndex(paramInt);
    if (this.metadata == null)
      readHeader(); 
    return this.metadata;
  }
  
  public IIOMetadata getStreamMetadata() throws IOException { return null; }
  
  public BufferedImage read(int paramInt, ImageReadParam paramImageReadParam) throws IOException {
    if (this.iis == null)
      throw new IllegalStateException(I18N.getString("WBMPImageReader1")); 
    checkIndex(paramInt);
    clearAbortRequest();
    processImageStarted(paramInt);
    if (paramImageReadParam == null)
      paramImageReadParam = getDefaultReadParam(); 
    readHeader();
    Rectangle rectangle1 = new Rectangle(0, 0, 0, 0);
    Rectangle rectangle2 = new Rectangle(0, 0, 0, 0);
    computeRegions(paramImageReadParam, this.width, this.height, paramImageReadParam.getDestination(), rectangle1, rectangle2);
    int i = paramImageReadParam.getSourceXSubsampling();
    int j = paramImageReadParam.getSourceYSubsampling();
    int k = paramImageReadParam.getSubsamplingXOffset();
    int m = paramImageReadParam.getSubsamplingYOffset();
    BufferedImage bufferedImage = paramImageReadParam.getDestination();
    if (bufferedImage == null)
      bufferedImage = new BufferedImage(rectangle2.x + rectangle2.width, rectangle2.y + rectangle2.height, 12); 
    boolean bool = (rectangle2.equals(new Rectangle(0, 0, this.width, this.height)) && rectangle2.equals(new Rectangle(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight()))) ? 1 : 0;
    WritableRaster writableRaster = bufferedImage.getWritableTile(0, 0);
    MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)bufferedImage.getSampleModel();
    if (bool) {
      if (abortRequested()) {
        processReadAborted();
        return bufferedImage;
      } 
      this.iis.read(((DataBufferByte)writableRaster.getDataBuffer()).getData(), 0, this.height * multiPixelPackedSampleModel.getScanlineStride());
      processImageUpdate(bufferedImage, 0, 0, this.width, this.height, 1, 1, new int[] { 0 });
      processImageProgress(100.0F);
    } else {
      int n = (this.width + 7) / 8;
      byte[] arrayOfByte1 = new byte[n];
      byte[] arrayOfByte2 = ((DataBufferByte)writableRaster.getDataBuffer()).getData();
      int i1 = multiPixelPackedSampleModel.getScanlineStride();
      this.iis.skipBytes(n * rectangle1.y);
      int i2 = n * (j - 1);
      int[] arrayOfInt1 = new int[rectangle2.width];
      int[] arrayOfInt2 = new int[rectangle2.width];
      int[] arrayOfInt3 = new int[rectangle2.width];
      int[] arrayOfInt4 = new int[rectangle2.width];
      int i3 = rectangle2.x;
      int i4 = rectangle1.x;
      int i5 = 0;
      while (i3 < rectangle2.x + rectangle2.width) {
        arrayOfInt3[i5] = i4 >> 3;
        arrayOfInt1[i5] = 7 - (i4 & 0x7);
        arrayOfInt4[i5] = i3 >> 3;
        arrayOfInt2[i5] = 7 - (i3 & 0x7);
        i3++;
        i5++;
        i4 += i;
      } 
      i3 = 0;
      i4 = rectangle1.y;
      i5 = rectangle2.y * i1;
      while (i3 < rectangle2.height && !abortRequested()) {
        this.iis.read(arrayOfByte1, 0, n);
        for (byte b = 0; b < rectangle2.width; b++) {
          byte b1 = arrayOfByte1[arrayOfInt3[b]] >> arrayOfInt1[b] & true;
          arrayOfByte2[i5 + arrayOfInt4[b]] = (byte)(arrayOfByte2[i5 + arrayOfInt4[b]] | b1 << arrayOfInt2[b]);
        } 
        i5 += i1;
        this.iis.skipBytes(i2);
        processImageUpdate(bufferedImage, 0, i3, rectangle2.width, 1, 1, 1, new int[] { 0 });
        processImageProgress(100.0F * i3 / rectangle2.height);
        i3++;
        i4 += j;
      } 
    } 
    if (abortRequested()) {
      processReadAborted();
    } else {
      processImageComplete();
    } 
    return bufferedImage;
  }
  
  public boolean canReadRaster() { return true; }
  
  public Raster readRaster(int paramInt, ImageReadParam paramImageReadParam) throws IOException {
    BufferedImage bufferedImage = read(paramInt, paramImageReadParam);
    return bufferedImage.getData();
  }
  
  public void reset() throws IOException {
    super.reset();
    this.iis = null;
    this.gotHeader = false;
  }
  
  boolean isValidWbmpType(int paramInt) throws IOException { return (paramInt == 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\wbmp\WBMPImageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */