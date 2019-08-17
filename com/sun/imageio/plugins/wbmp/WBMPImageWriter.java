package com.sun.imageio.plugins.wbmp;

import com.sun.imageio.plugins.common.I18N;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

public class WBMPImageWriter extends ImageWriter {
  private ImageOutputStream stream = null;
  
  private static int getNumBits(int paramInt) {
    byte b = 32;
    for (int i = Integer.MIN_VALUE; i != 0 && (paramInt & i) == 0; i >>>= 1)
      b--; 
    return b;
  }
  
  private static byte[] intToMultiByte(int paramInt) {
    int i = getNumBits(paramInt);
    byte[] arrayOfByte = new byte[(i + 6) / 7];
    int j = arrayOfByte.length - 1;
    for (int k = 0; k <= j; k++) {
      arrayOfByte[k] = (byte)(paramInt >>> (j - k) * 7 & 0x7F);
      if (k != j)
        arrayOfByte[k] = (byte)(arrayOfByte[k] | 0xFFFFFF80); 
    } 
    return arrayOfByte;
  }
  
  public WBMPImageWriter(ImageWriterSpi paramImageWriterSpi) { super(paramImageWriterSpi); }
  
  public void setOutput(Object paramObject) {
    super.setOutput(paramObject);
    if (paramObject != null) {
      if (!(paramObject instanceof ImageOutputStream))
        throw new IllegalArgumentException(I18N.getString("WBMPImageWriter")); 
      this.stream = (ImageOutputStream)paramObject;
    } else {
      this.stream = null;
    } 
  }
  
  public IIOMetadata getDefaultStreamMetadata(ImageWriteParam paramImageWriteParam) { return null; }
  
  public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam) {
    WBMPMetadata wBMPMetadata = new WBMPMetadata();
    wBMPMetadata.wbmpType = 0;
    return wBMPMetadata;
  }
  
  public IIOMetadata convertStreamMetadata(IIOMetadata paramIIOMetadata, ImageWriteParam paramImageWriteParam) { return null; }
  
  public IIOMetadata convertImageMetadata(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam) { return null; }
  
  public boolean canWriteRasters() { return true; }
  
  public void write(IIOMetadata paramIIOMetadata, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam) throws IOException {
    if (this.stream == null)
      throw new IllegalStateException(I18N.getString("WBMPImageWriter3")); 
    if (paramIIOImage == null)
      throw new IllegalArgumentException(I18N.getString("WBMPImageWriter4")); 
    clearAbortRequest();
    processImageStarted(0);
    if (paramImageWriteParam == null)
      paramImageWriteParam = getDefaultWriteParam(); 
    RenderedImage renderedImage = null;
    Raster raster = null;
    boolean bool = paramIIOImage.hasRaster();
    Rectangle rectangle1 = paramImageWriteParam.getSourceRegion();
    SampleModel sampleModel1 = null;
    if (bool) {
      raster = paramIIOImage.getRaster();
      sampleModel1 = raster.getSampleModel();
    } else {
      renderedImage = paramIIOImage.getRenderedImage();
      sampleModel1 = renderedImage.getSampleModel();
      raster = renderedImage.getData();
    } 
    checkSampleModel(sampleModel1);
    if (rectangle1 == null) {
      rectangle1 = raster.getBounds();
    } else {
      rectangle1 = rectangle1.intersection(raster.getBounds());
    } 
    if (rectangle1.isEmpty())
      throw new RuntimeException(I18N.getString("WBMPImageWriter1")); 
    int i = paramImageWriteParam.getSourceXSubsampling();
    int j = paramImageWriteParam.getSourceYSubsampling();
    int k = paramImageWriteParam.getSubsamplingXOffset();
    int m = paramImageWriteParam.getSubsamplingYOffset();
    rectangle1.translate(k, m);
    rectangle1.width -= k;
    rectangle1.height -= m;
    int n = rectangle1.x / i;
    int i1 = rectangle1.y / j;
    int i2 = (rectangle1.width + i - 1) / i;
    int i3 = (rectangle1.height + j - 1) / j;
    Rectangle rectangle2 = new Rectangle(n, i1, i2, i3);
    sampleModel1 = sampleModel1.createCompatibleSampleModel(i2, i3);
    SampleModel sampleModel2 = sampleModel1;
    if (sampleModel1.getDataType() != 0 || !(sampleModel1 instanceof MultiPixelPackedSampleModel) || ((MultiPixelPackedSampleModel)sampleModel1).getDataBitOffset() != 0)
      sampleModel2 = new MultiPixelPackedSampleModel(0, i2, i3, 1, i2 + 7 >> 3, 0); 
    if (!rectangle2.equals(rectangle1))
      if (i == 1 && j == 1) {
        raster = raster.createChild(raster.getMinX(), raster.getMinY(), i2, i3, n, i1, null);
      } else {
        WritableRaster writableRaster = Raster.createWritableRaster(sampleModel2, new Point(n, i1));
        byte[] arrayOfByte1 = ((DataBufferByte)writableRaster.getDataBuffer()).getData();
        int i6 = i1;
        int i7 = rectangle1.y;
        int i8 = 0;
        while (i6 < i1 + i3) {
          byte b = 0;
          int i9;
          for (i9 = rectangle1.x; b < i2; i9 += i) {
            int i10 = raster.getSample(i9, i7, 0);
            arrayOfByte1[i8 + (b >> 3)] = (byte)(arrayOfByte1[i8 + (b >> 3)] | i10 << 7 - (b & 0x7));
            b++;
          } 
          i8 += (i2 + 7 >> 3);
          i6++;
          i7 += j;
        } 
        raster = writableRaster;
      }  
    if (!sampleModel2.equals(raster.getSampleModel())) {
      WritableRaster writableRaster = Raster.createWritableRaster(sampleModel2, new Point(raster.getMinX(), raster.getMinY()));
      writableRaster.setRect(raster);
      raster = writableRaster;
    } 
    boolean bool1 = false;
    if (!bool && renderedImage.getColorModel() instanceof IndexColorModel) {
      IndexColorModel indexColorModel = (IndexColorModel)renderedImage.getColorModel();
      bool1 = (indexColorModel.getRed(0) > indexColorModel.getRed(1)) ? 1 : 0;
    } 
    int i4 = ((MultiPixelPackedSampleModel)sampleModel2).getScanlineStride();
    int i5 = (i2 + 7) / 8;
    byte[] arrayOfByte = ((DataBufferByte)raster.getDataBuffer()).getData();
    this.stream.write(0);
    this.stream.write(0);
    this.stream.write(intToMultiByte(i2));
    this.stream.write(intToMultiByte(i3));
    if (!bool1 && i4 == i5) {
      this.stream.write(arrayOfByte, 0, i3 * i5);
      processImageProgress(100.0F);
    } else {
      int i6 = 0;
      if (!bool1) {
        for (byte b = 0; b < i3 && !abortRequested(); b++) {
          this.stream.write(arrayOfByte, i6, i5);
          i6 += i4;
          processImageProgress(100.0F * b / i3);
        } 
      } else {
        byte[] arrayOfByte1 = new byte[i5];
        for (byte b = 0; b < i3 && !abortRequested(); b++) {
          for (int i7 = 0; i7 < i5; i7++)
            arrayOfByte1[i7] = (byte)(arrayOfByte[i7 + i6] ^ 0xFFFFFFFF); 
          this.stream.write(arrayOfByte1, 0, i5);
          i6 += i4;
          processImageProgress(100.0F * b / i3);
        } 
      } 
    } 
    if (abortRequested()) {
      processWriteAborted();
    } else {
      processImageComplete();
      this.stream.flushBefore(this.stream.getStreamPosition());
    } 
  }
  
  public void reset() {
    super.reset();
    this.stream = null;
  }
  
  private void checkSampleModel(SampleModel paramSampleModel) {
    int i = paramSampleModel.getDataType();
    if (i < 0 || i > 3 || paramSampleModel.getNumBands() != 1 || paramSampleModel.getSampleSize(0) != 1)
      throw new IllegalArgumentException(I18N.getString("WBMPImageWriter2")); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\wbmp\WBMPImageWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */