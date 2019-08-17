package com.sun.imageio.plugins.gif;

import com.sun.imageio.plugins.common.LZWCompressor;
import com.sun.imageio.plugins.common.PaletteBuilder;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.awt.image.ByteComponentRaster;

public class GIFImageWriter extends ImageWriter {
  private static final boolean DEBUG = false;
  
  static final String STANDARD_METADATA_NAME = "javax_imageio_1.0";
  
  static final String STREAM_METADATA_NAME = "javax_imageio_gif_stream_1.0";
  
  static final String IMAGE_METADATA_NAME = "javax_imageio_gif_image_1.0";
  
  private ImageOutputStream stream = null;
  
  private boolean isWritingSequence = false;
  
  private boolean wroteSequenceHeader = false;
  
  private GIFWritableStreamMetadata theStreamMetadata = null;
  
  private int imageIndex = 0;
  
  private static int getNumBits(int paramInt) throws IOException {
    switch (paramInt) {
      case 2:
        return 1;
      case 4:
        return 2;
      case 8:
        return 3;
      case 16:
        return 4;
      case 32:
        return 5;
      case 64:
        return 6;
      case 128:
        return 7;
      case 256:
        return 8;
    } 
    throw new IOException("Bad palette length: " + paramInt + "!");
  }
  
  private static void computeRegions(Rectangle paramRectangle, Dimension paramDimension, ImageWriteParam paramImageWriteParam) {
    int i = 1;
    int j = 1;
    if (paramImageWriteParam != null) {
      int[] arrayOfInt = paramImageWriteParam.getSourceBands();
      if (arrayOfInt != null && (arrayOfInt.length != 1 || arrayOfInt[0] != 0))
        throw new IllegalArgumentException("Cannot sub-band image!"); 
      Rectangle rectangle = paramImageWriteParam.getSourceRegion();
      if (rectangle != null) {
        rectangle = rectangle.intersection(paramRectangle);
        paramRectangle.setBounds(rectangle);
      } 
      int k = paramImageWriteParam.getSubsamplingXOffset();
      int m = paramImageWriteParam.getSubsamplingYOffset();
      paramRectangle.x += k;
      paramRectangle.y += m;
      paramRectangle.width -= k;
      paramRectangle.height -= m;
      i = paramImageWriteParam.getSourceXSubsampling();
      j = paramImageWriteParam.getSourceYSubsampling();
    } 
    paramDimension.setSize((paramRectangle.width + i - 1) / i, (paramRectangle.height + j - 1) / j);
    if (paramDimension.width <= 0 || paramDimension.height <= 0)
      throw new IllegalArgumentException("Empty source region!"); 
  }
  
  private static byte[] createColorTable(ColorModel paramColorModel, SampleModel paramSampleModel) {
    byte[] arrayOfByte;
    if (paramColorModel instanceof IndexColorModel) {
      IndexColorModel indexColorModel = (IndexColorModel)paramColorModel;
      int i = indexColorModel.getMapSize();
      int j = getGifPaletteSize(i);
      byte[] arrayOfByte1 = new byte[j];
      byte[] arrayOfByte2 = new byte[j];
      byte[] arrayOfByte3 = new byte[j];
      indexColorModel.getReds(arrayOfByte1);
      indexColorModel.getGreens(arrayOfByte2);
      indexColorModel.getBlues(arrayOfByte3);
      int k;
      for (k = i; k < j; k++) {
        arrayOfByte1[k] = arrayOfByte1[0];
        arrayOfByte2[k] = arrayOfByte2[0];
        arrayOfByte3[k] = arrayOfByte3[0];
      } 
      arrayOfByte = new byte[3 * j];
      k = 0;
      for (byte b = 0; b < j; b++) {
        arrayOfByte[k++] = arrayOfByte1[b];
        arrayOfByte[k++] = arrayOfByte2[b];
        arrayOfByte[k++] = arrayOfByte3[b];
      } 
    } else if (paramSampleModel.getNumBands() == 1) {
      int i = paramSampleModel.getSampleSize()[0];
      if (i > 8)
        i = 8; 
      int j = 3 * (1 << i);
      arrayOfByte = new byte[j];
      for (byte b = 0; b < j; b++)
        arrayOfByte[b] = (byte)(b / 3); 
    } else {
      arrayOfByte = null;
    } 
    return arrayOfByte;
  }
  
  private static int getGifPaletteSize(int paramInt) throws IOException {
    if (paramInt <= 2)
      return 2; 
    paramInt = --paramInt | paramInt >> 1;
    paramInt |= paramInt >> 2;
    paramInt |= paramInt >> 4;
    paramInt |= paramInt >> 8;
    paramInt |= paramInt >> 16;
    return paramInt + 1;
  }
  
  public GIFImageWriter(GIFImageWriterSpi paramGIFImageWriterSpi) { super(paramGIFImageWriterSpi); }
  
  public boolean canWriteSequence() { return true; }
  
  private void convertMetadata(String paramString, IIOMetadata paramIIOMetadata1, IIOMetadata paramIIOMetadata2) {
    String str1 = null;
    String str2 = paramIIOMetadata1.getNativeMetadataFormatName();
    if (str2 != null && str2.equals(paramString)) {
      str1 = paramString;
    } else {
      String[] arrayOfString = paramIIOMetadata1.getExtraMetadataFormatNames();
      if (arrayOfString != null)
        for (byte b = 0; b < arrayOfString.length; b++) {
          if (arrayOfString[b].equals(paramString)) {
            str1 = paramString;
            break;
          } 
        }  
    } 
    if (str1 == null && paramIIOMetadata1.isStandardMetadataFormatSupported())
      str1 = "javax_imageio_1.0"; 
    if (str1 != null)
      try {
        Node node = paramIIOMetadata1.getAsTree(str1);
        paramIIOMetadata2.mergeTree(str1, node);
      } catch (IIOInvalidTreeException iIOInvalidTreeException) {} 
  }
  
  public IIOMetadata convertStreamMetadata(IIOMetadata paramIIOMetadata, ImageWriteParam paramImageWriteParam) {
    if (paramIIOMetadata == null)
      throw new IllegalArgumentException("inData == null!"); 
    IIOMetadata iIOMetadata = getDefaultStreamMetadata(paramImageWriteParam);
    convertMetadata("javax_imageio_gif_stream_1.0", paramIIOMetadata, iIOMetadata);
    return iIOMetadata;
  }
  
  public IIOMetadata convertImageMetadata(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam) {
    if (paramIIOMetadata == null)
      throw new IllegalArgumentException("inData == null!"); 
    if (paramImageTypeSpecifier == null)
      throw new IllegalArgumentException("imageType == null!"); 
    GIFWritableImageMetadata gIFWritableImageMetadata = (GIFWritableImageMetadata)getDefaultImageMetadata(paramImageTypeSpecifier, paramImageWriteParam);
    boolean bool = gIFWritableImageMetadata.interlaceFlag;
    convertMetadata("javax_imageio_gif_image_1.0", paramIIOMetadata, gIFWritableImageMetadata);
    paramImageWriteParam;
    if (paramImageWriteParam != null && paramImageWriteParam.canWriteProgressive() && paramImageWriteParam.getProgressiveMode() != 3)
      gIFWritableImageMetadata.interlaceFlag = bool; 
    return gIFWritableImageMetadata;
  }
  
  public void endWriteSequence() throws IOException {
    if (this.stream == null)
      throw new IllegalStateException("output == null!"); 
    if (!this.isWritingSequence)
      throw new IllegalStateException("prepareWriteSequence() was not invoked!"); 
    writeTrailer();
    resetLocal();
  }
  
  public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam) {
    GIFWritableImageMetadata gIFWritableImageMetadata = new GIFWritableImageMetadata();
    SampleModel sampleModel = paramImageTypeSpecifier.getSampleModel();
    Rectangle rectangle = new Rectangle(sampleModel.getWidth(), sampleModel.getHeight());
    Dimension dimension = new Dimension();
    computeRegions(rectangle, dimension, paramImageWriteParam);
    gIFWritableImageMetadata.imageWidth = dimension.width;
    gIFWritableImageMetadata.imageHeight = dimension.height;
    if (paramImageWriteParam != null && paramImageWriteParam.canWriteProgressive() && paramImageWriteParam.getProgressiveMode() == 0) {
      gIFWritableImageMetadata.interlaceFlag = false;
    } else {
      gIFWritableImageMetadata.interlaceFlag = true;
    } 
    ColorModel colorModel = paramImageTypeSpecifier.getColorModel();
    gIFWritableImageMetadata.localColorTable = createColorTable(colorModel, sampleModel);
    if (colorModel instanceof IndexColorModel) {
      int i = ((IndexColorModel)colorModel).getTransparentPixel();
      if (i != -1) {
        gIFWritableImageMetadata.transparentColorFlag = true;
        gIFWritableImageMetadata.transparentColorIndex = i;
      } 
    } 
    return gIFWritableImageMetadata;
  }
  
  public IIOMetadata getDefaultStreamMetadata(ImageWriteParam paramImageWriteParam) {
    GIFWritableStreamMetadata gIFWritableStreamMetadata = new GIFWritableStreamMetadata();
    gIFWritableStreamMetadata.version = "89a";
    return gIFWritableStreamMetadata;
  }
  
  public ImageWriteParam getDefaultWriteParam() { return new GIFImageWriteParam(getLocale()); }
  
  public void prepareWriteSequence(IIOMetadata paramIIOMetadata) throws IOException {
    if (this.stream == null)
      throw new IllegalStateException("Output is not set."); 
    resetLocal();
    if (paramIIOMetadata == null) {
      this.theStreamMetadata = (GIFWritableStreamMetadata)getDefaultStreamMetadata(null);
    } else {
      this.theStreamMetadata = new GIFWritableStreamMetadata();
      convertMetadata("javax_imageio_gif_stream_1.0", paramIIOMetadata, this.theStreamMetadata);
    } 
    this.isWritingSequence = true;
  }
  
  public void reset() throws IOException {
    super.reset();
    resetLocal();
  }
  
  private void resetLocal() throws IOException {
    this.isWritingSequence = false;
    this.wroteSequenceHeader = false;
    this.theStreamMetadata = null;
    this.imageIndex = 0;
  }
  
  public void setOutput(Object paramObject) {
    super.setOutput(paramObject);
    if (paramObject != null) {
      if (!(paramObject instanceof ImageOutputStream))
        throw new IllegalArgumentException("output is not an ImageOutputStream"); 
      this.stream = (ImageOutputStream)paramObject;
      this.stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
    } else {
      this.stream = null;
    } 
  }
  
  public void write(IIOMetadata paramIIOMetadata, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam) throws IOException {
    GIFWritableStreamMetadata gIFWritableStreamMetadata;
    if (this.stream == null)
      throw new IllegalStateException("output == null!"); 
    if (paramIIOImage == null)
      throw new IllegalArgumentException("iioimage == null!"); 
    if (paramIIOImage.hasRaster())
      throw new UnsupportedOperationException("canWriteRasters() == false!"); 
    resetLocal();
    if (paramIIOMetadata == null) {
      gIFWritableStreamMetadata = (GIFWritableStreamMetadata)getDefaultStreamMetadata(paramImageWriteParam);
    } else {
      gIFWritableStreamMetadata = (GIFWritableStreamMetadata)convertStreamMetadata(paramIIOMetadata, paramImageWriteParam);
    } 
    write(true, true, gIFWritableStreamMetadata, paramIIOImage, paramImageWriteParam);
  }
  
  public void writeToSequence(IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam) throws IOException {
    if (this.stream == null)
      throw new IllegalStateException("output == null!"); 
    if (paramIIOImage == null)
      throw new IllegalArgumentException("image == null!"); 
    if (paramIIOImage.hasRaster())
      throw new UnsupportedOperationException("canWriteRasters() == false!"); 
    if (!this.isWritingSequence)
      throw new IllegalStateException("prepareWriteSequence() was not invoked!"); 
    write(!this.wroteSequenceHeader, false, this.theStreamMetadata, paramIIOImage, paramImageWriteParam);
    if (!this.wroteSequenceHeader)
      this.wroteSequenceHeader = true; 
    this.imageIndex++;
  }
  
  private boolean needToCreateIndex(RenderedImage paramRenderedImage) {
    SampleModel sampleModel = paramRenderedImage.getSampleModel();
    ColorModel colorModel = paramRenderedImage.getColorModel();
    return (sampleModel.getNumBands() != 1 || sampleModel.getSampleSize()[0] > 8 || colorModel.getComponentSize()[0] > 8);
  }
  
  private void write(boolean paramBoolean1, boolean paramBoolean2, IIOMetadata paramIIOMetadata, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam) throws IOException {
    clearAbortRequest();
    RenderedImage renderedImage = paramIIOImage.getRenderedImage();
    if (needToCreateIndex(renderedImage)) {
      renderedImage = PaletteBuilder.createIndexedImage(renderedImage);
      paramIIOImage.setRenderedImage(renderedImage);
    } 
    ColorModel colorModel = renderedImage.getColorModel();
    SampleModel sampleModel = renderedImage.getSampleModel();
    Rectangle rectangle = new Rectangle(renderedImage.getMinX(), renderedImage.getMinY(), renderedImage.getWidth(), renderedImage.getHeight());
    Dimension dimension = new Dimension();
    computeRegions(rectangle, dimension, paramImageWriteParam);
    GIFWritableImageMetadata gIFWritableImageMetadata = null;
    if (paramIIOImage.getMetadata() != null) {
      gIFWritableImageMetadata = new GIFWritableImageMetadata();
      convertMetadata("javax_imageio_gif_image_1.0", paramIIOImage.getMetadata(), gIFWritableImageMetadata);
      if (gIFWritableImageMetadata.localColorTable == null) {
        gIFWritableImageMetadata.localColorTable = createColorTable(colorModel, sampleModel);
        if (colorModel instanceof IndexColorModel) {
          IndexColorModel indexColorModel = (IndexColorModel)colorModel;
          int i = indexColorModel.getTransparentPixel();
          gIFWritableImageMetadata.transparentColorFlag = (i != -1);
          if (gIFWritableImageMetadata.transparentColorFlag)
            gIFWritableImageMetadata.transparentColorIndex = i; 
        } 
      } 
    } 
    byte[] arrayOfByte = null;
    if (paramBoolean1) {
      int i;
      if (paramIIOMetadata == null)
        throw new IllegalArgumentException("Cannot write null header!"); 
      GIFWritableStreamMetadata gIFWritableStreamMetadata = (GIFWritableStreamMetadata)paramIIOMetadata;
      if (gIFWritableStreamMetadata.version == null)
        gIFWritableStreamMetadata.version = "89a"; 
      if (gIFWritableStreamMetadata.logicalScreenWidth == -1)
        gIFWritableStreamMetadata.logicalScreenWidth = dimension.width; 
      if (gIFWritableStreamMetadata.logicalScreenHeight == -1)
        gIFWritableStreamMetadata.logicalScreenHeight = dimension.height; 
      if (gIFWritableStreamMetadata.colorResolution == -1)
        gIFWritableStreamMetadata.colorResolution = (colorModel != null) ? colorModel.getComponentSize()[0] : sampleModel.getSampleSize()[0]; 
      if (gIFWritableStreamMetadata.globalColorTable == null)
        if (this.isWritingSequence && gIFWritableImageMetadata != null && gIFWritableImageMetadata.localColorTable != null) {
          gIFWritableStreamMetadata.globalColorTable = gIFWritableImageMetadata.localColorTable;
        } else if (gIFWritableImageMetadata == null || gIFWritableImageMetadata.localColorTable == null) {
          gIFWritableStreamMetadata.globalColorTable = createColorTable(colorModel, sampleModel);
        }  
      arrayOfByte = gIFWritableStreamMetadata.globalColorTable;
      if (arrayOfByte != null) {
        i = getNumBits(arrayOfByte.length / 3);
      } else if (gIFWritableImageMetadata != null && gIFWritableImageMetadata.localColorTable != null) {
        i = getNumBits(gIFWritableImageMetadata.localColorTable.length / 3);
      } else {
        i = sampleModel.getSampleSize(0);
      } 
      writeHeader(gIFWritableStreamMetadata, i);
    } else if (this.isWritingSequence) {
      arrayOfByte = this.theStreamMetadata.globalColorTable;
    } else {
      throw new IllegalArgumentException("Must write header for single image!");
    } 
    writeImage(paramIIOImage.getRenderedImage(), gIFWritableImageMetadata, paramImageWriteParam, arrayOfByte, rectangle, dimension);
    if (paramBoolean2)
      writeTrailer(); 
  }
  
  private void writeImage(RenderedImage paramRenderedImage, GIFWritableImageMetadata paramGIFWritableImageMetadata, ImageWriteParam paramImageWriteParam, byte[] paramArrayOfByte, Rectangle paramRectangle, Dimension paramDimension) throws IOException {
    boolean bool;
    ColorModel colorModel = paramRenderedImage.getColorModel();
    SampleModel sampleModel = paramRenderedImage.getSampleModel();
    if (paramGIFWritableImageMetadata == null) {
      paramGIFWritableImageMetadata = (GIFWritableImageMetadata)getDefaultImageMetadata(new ImageTypeSpecifier(paramRenderedImage), paramImageWriteParam);
      bool = paramGIFWritableImageMetadata.transparentColorFlag;
    } else {
      NodeList nodeList = null;
      try {
        IIOMetadataNode iIOMetadataNode = (IIOMetadataNode)paramGIFWritableImageMetadata.getAsTree("javax_imageio_gif_image_1.0");
        nodeList = iIOMetadataNode.getElementsByTagName("GraphicControlExtension");
      } catch (IllegalArgumentException illegalArgumentException) {}
      bool = (nodeList != null && nodeList.getLength() > 0) ? 1 : 0;
      if (paramImageWriteParam != null && paramImageWriteParam.canWriteProgressive())
        if (paramImageWriteParam.getProgressiveMode() == 0) {
          paramGIFWritableImageMetadata.interlaceFlag = false;
        } else if (paramImageWriteParam.getProgressiveMode() == 1) {
          paramGIFWritableImageMetadata.interlaceFlag = true;
        }  
    } 
    if (Arrays.equals(paramArrayOfByte, paramGIFWritableImageMetadata.localColorTable))
      paramGIFWritableImageMetadata.localColorTable = null; 
    paramGIFWritableImageMetadata.imageWidth = paramDimension.width;
    paramGIFWritableImageMetadata.imageHeight = paramDimension.height;
    if (bool)
      writeGraphicControlExtension(paramGIFWritableImageMetadata); 
    writePlainTextExtension(paramGIFWritableImageMetadata);
    writeApplicationExtension(paramGIFWritableImageMetadata);
    writeCommentExtension(paramGIFWritableImageMetadata);
    int i = getNumBits((paramGIFWritableImageMetadata.localColorTable == null) ? ((paramArrayOfByte == null) ? sampleModel.getSampleSize(0) : (paramArrayOfByte.length / 3)) : (paramGIFWritableImageMetadata.localColorTable.length / 3));
    writeImageDescriptor(paramGIFWritableImageMetadata, i);
    writeRasterData(paramRenderedImage, paramRectangle, paramDimension, paramImageWriteParam, paramGIFWritableImageMetadata.interlaceFlag);
  }
  
  private void writeRows(RenderedImage paramRenderedImage, LZWCompressor paramLZWCompressor, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11) throws IOException {
    int[] arrayOfInt = new int[paramInt5];
    byte[] arrayOfByte = new byte[paramInt8];
    Raster raster = (paramRenderedImage.getNumXTiles() == 1 && paramRenderedImage.getNumYTiles() == 1) ? paramRenderedImage.getTile(0, 0) : paramRenderedImage.getData();
    int i;
    for (i = paramInt6; i < paramInt9; i += paramInt7) {
      if (paramInt10 % paramInt11 == 0) {
        if (abortRequested()) {
          processWriteAborted();
          return;
        } 
        processImageProgress(paramInt10 * 100.0F / paramInt9);
      } 
      raster.getSamples(paramInt1, paramInt3, paramInt5, 1, 0, arrayOfInt);
      byte b = 0;
      int j;
      for (j = 0; b < paramInt8; j += paramInt2) {
        arrayOfByte[b] = (byte)arrayOfInt[j];
        b++;
      } 
      paramLZWCompressor.compress(arrayOfByte, 0, paramInt8);
      paramInt10++;
      paramInt3 += paramInt4;
    } 
  }
  
  private void writeRowsOpt(byte[] paramArrayOfByte, int paramInt1, int paramInt2, LZWCompressor paramLZWCompressor, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) throws IOException {
    paramInt1 += paramInt3 * paramInt2;
    paramInt2 *= paramInt4;
    int i;
    for (i = paramInt3; i < paramInt6; i += paramInt4) {
      if (paramInt7 % paramInt8 == 0) {
        if (abortRequested()) {
          processWriteAborted();
          return;
        } 
        processImageProgress(paramInt7 * 100.0F / paramInt6);
      } 
      paramLZWCompressor.compress(paramArrayOfByte, paramInt1, paramInt5);
      paramInt7++;
      paramInt1 += paramInt2;
    } 
  }
  
  private void writeRasterData(RenderedImage paramRenderedImage, Rectangle paramRectangle, Dimension paramDimension, ImageWriteParam paramImageWriteParam, boolean paramBoolean) throws IOException {
    int i3;
    int i2;
    int i = paramRectangle.x;
    int j = paramRectangle.y;
    int k = paramRectangle.width;
    int m = paramRectangle.height;
    int n = paramDimension.width;
    int i1 = paramDimension.height;
    if (paramImageWriteParam == null) {
      i2 = 1;
      i3 = 1;
    } else {
      i2 = paramImageWriteParam.getSourceXSubsampling();
      i3 = paramImageWriteParam.getSourceYSubsampling();
    } 
    SampleModel sampleModel = paramRenderedImage.getSampleModel();
    int i4 = sampleModel.getSampleSize()[0];
    int i5 = i4;
    if (i5 == 1)
      i5++; 
    this.stream.write(i5);
    LZWCompressor lZWCompressor = new LZWCompressor(this.stream, i5, false);
    boolean bool = (i2 == 1 && i3 == 1 && paramRenderedImage.getNumXTiles() == 1 && paramRenderedImage.getNumYTiles() == 1 && sampleModel instanceof ComponentSampleModel && paramRenderedImage.getTile(0, 0) instanceof ByteComponentRaster && paramRenderedImage.getTile(0, 0).getDataBuffer() instanceof DataBufferByte) ? 1 : 0;
    int i6 = 0;
    int i7 = Math.max(i1 / 20, 1);
    processImageStarted(this.imageIndex);
    if (paramBoolean) {
      if (bool) {
        ByteComponentRaster byteComponentRaster = (ByteComponentRaster)paramRenderedImage.getTile(0, 0);
        byte[] arrayOfByte = ((DataBufferByte)byteComponentRaster.getDataBuffer()).getData();
        ComponentSampleModel componentSampleModel = (ComponentSampleModel)byteComponentRaster.getSampleModel();
        int i8 = componentSampleModel.getOffset(i, j, 0);
        i8 += byteComponentRaster.getDataOffset(0);
        int i9 = componentSampleModel.getScanlineStride();
        writeRowsOpt(arrayOfByte, i8, i9, lZWCompressor, 0, 8, n, i1, i6, i7);
        if (abortRequested())
          return; 
        i6 += i1 / 8;
        writeRowsOpt(arrayOfByte, i8, i9, lZWCompressor, 4, 8, n, i1, i6, i7);
        if (abortRequested())
          return; 
        i6 += (i1 - 4) / 8;
        writeRowsOpt(arrayOfByte, i8, i9, lZWCompressor, 2, 4, n, i1, i6, i7);
        if (abortRequested())
          return; 
        i6 += (i1 - 2) / 4;
        writeRowsOpt(arrayOfByte, i8, i9, lZWCompressor, 1, 2, n, i1, i6, i7);
      } else {
        writeRows(paramRenderedImage, lZWCompressor, i, i2, j, 8 * i3, k, 0, 8, n, i1, i6, i7);
        if (abortRequested())
          return; 
        i6 += i1 / 8;
        writeRows(paramRenderedImage, lZWCompressor, i, i2, j + 4 * i3, 8 * i3, k, 4, 8, n, i1, i6, i7);
        if (abortRequested())
          return; 
        i6 += (i1 - 4) / 8;
        writeRows(paramRenderedImage, lZWCompressor, i, i2, j + 2 * i3, 4 * i3, k, 2, 4, n, i1, i6, i7);
        if (abortRequested())
          return; 
        i6 += (i1 - 2) / 4;
        writeRows(paramRenderedImage, lZWCompressor, i, i2, j + i3, 2 * i3, k, 1, 2, n, i1, i6, i7);
      } 
    } else if (bool) {
      Raster raster = paramRenderedImage.getTile(0, 0);
      byte[] arrayOfByte = ((DataBufferByte)raster.getDataBuffer()).getData();
      ComponentSampleModel componentSampleModel = (ComponentSampleModel)raster.getSampleModel();
      int i8 = componentSampleModel.getOffset(i, j, 0);
      int i9 = componentSampleModel.getScanlineStride();
      writeRowsOpt(arrayOfByte, i8, i9, lZWCompressor, 0, 1, n, i1, i6, i7);
    } else {
      writeRows(paramRenderedImage, lZWCompressor, i, i2, j, i3, k, 0, 1, n, i1, i6, i7);
    } 
    if (abortRequested())
      return; 
    processImageProgress(100.0F);
    lZWCompressor.flush();
    this.stream.write(0);
    processImageComplete();
  }
  
  private void writeHeader(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, int paramInt6, byte[] paramArrayOfByte) throws IOException {
    try {
      this.stream.writeBytes("GIF" + paramString);
      this.stream.writeShort((short)paramInt1);
      this.stream.writeShort((short)paramInt2);
      int i = (paramArrayOfByte != null) ? 128 : 0;
      i |= (paramInt3 - 1 & 0x7) << 4;
      if (paramBoolean)
        i |= 0x8; 
      i |= paramInt6 - 1;
      this.stream.write(i);
      this.stream.write(paramInt5);
      this.stream.write(paramInt4);
      if (paramArrayOfByte != null)
        this.stream.write(paramArrayOfByte); 
    } catch (IOException iOException) {
      throw new IIOException("I/O error writing header!", iOException);
    } 
  }
  
  private void writeHeader(IIOMetadata paramIIOMetadata, int paramInt) throws IOException {
    GIFWritableStreamMetadata gIFWritableStreamMetadata;
    if (paramIIOMetadata instanceof GIFWritableStreamMetadata) {
      gIFWritableStreamMetadata = (GIFWritableStreamMetadata)paramIIOMetadata;
    } else {
      gIFWritableStreamMetadata = new GIFWritableStreamMetadata();
      Node node = paramIIOMetadata.getAsTree("javax_imageio_gif_stream_1.0");
      gIFWritableStreamMetadata.setFromTree("javax_imageio_gif_stream_1.0", node);
    } 
    writeHeader(gIFWritableStreamMetadata.version, gIFWritableStreamMetadata.logicalScreenWidth, gIFWritableStreamMetadata.logicalScreenHeight, gIFWritableStreamMetadata.colorResolution, gIFWritableStreamMetadata.pixelAspectRatio, gIFWritableStreamMetadata.backgroundColorIndex, gIFWritableStreamMetadata.sortFlag, paramInt, gIFWritableStreamMetadata.globalColorTable);
  }
  
  private void writeGraphicControlExtension(int paramInt1, boolean paramBoolean1, boolean paramBoolean2, int paramInt2, int paramInt3) throws IOException {
    try {
      this.stream.write(33);
      this.stream.write(249);
      this.stream.write(4);
      int i = (paramInt1 & 0x3) << 2;
      if (paramBoolean1)
        i |= 0x2; 
      if (paramBoolean2)
        i |= 0x1; 
      this.stream.write(i);
      this.stream.writeShort((short)paramInt2);
      this.stream.write(paramInt3);
      this.stream.write(0);
    } catch (IOException iOException) {
      throw new IIOException("I/O error writing Graphic Control Extension!", iOException);
    } 
  }
  
  private void writeGraphicControlExtension(GIFWritableImageMetadata paramGIFWritableImageMetadata) throws IOException { writeGraphicControlExtension(paramGIFWritableImageMetadata.disposalMethod, paramGIFWritableImageMetadata.userInputFlag, paramGIFWritableImageMetadata.transparentColorFlag, paramGIFWritableImageMetadata.delayTime, paramGIFWritableImageMetadata.transparentColorIndex); }
  
  private void writeBlocks(byte[] paramArrayOfByte) throws IOException {
    if (paramArrayOfByte != null && paramArrayOfByte.length > 0)
      for (int i = 0; i < paramArrayOfByte.length; i += j) {
        int j = Math.min(paramArrayOfByte.length - i, 255);
        this.stream.write(j);
        this.stream.write(paramArrayOfByte, i, j);
      }  
  }
  
  private void writePlainTextExtension(GIFWritableImageMetadata paramGIFWritableImageMetadata) throws IOException {
    if (paramGIFWritableImageMetadata.hasPlainTextExtension)
      try {
        this.stream.write(33);
        this.stream.write(1);
        this.stream.write(12);
        this.stream.writeShort(paramGIFWritableImageMetadata.textGridLeft);
        this.stream.writeShort(paramGIFWritableImageMetadata.textGridTop);
        this.stream.writeShort(paramGIFWritableImageMetadata.textGridWidth);
        this.stream.writeShort(paramGIFWritableImageMetadata.textGridHeight);
        this.stream.write(paramGIFWritableImageMetadata.characterCellWidth);
        this.stream.write(paramGIFWritableImageMetadata.characterCellHeight);
        this.stream.write(paramGIFWritableImageMetadata.textForegroundColor);
        this.stream.write(paramGIFWritableImageMetadata.textBackgroundColor);
        writeBlocks(paramGIFWritableImageMetadata.text);
        this.stream.write(0);
      } catch (IOException iOException) {
        throw new IIOException("I/O error writing Plain Text Extension!", iOException);
      }  
  }
  
  private void writeApplicationExtension(GIFWritableImageMetadata paramGIFWritableImageMetadata) throws IOException {
    if (paramGIFWritableImageMetadata.applicationIDs != null) {
      Iterator iterator1 = paramGIFWritableImageMetadata.applicationIDs.iterator();
      Iterator iterator2 = paramGIFWritableImageMetadata.authenticationCodes.iterator();
      Iterator iterator3 = paramGIFWritableImageMetadata.applicationData.iterator();
      while (iterator1.hasNext()) {
        try {
          this.stream.write(33);
          this.stream.write(255);
          this.stream.write(11);
          this.stream.write((byte[])iterator1.next(), 0, 8);
          this.stream.write((byte[])iterator2.next(), 0, 3);
          writeBlocks((byte[])iterator3.next());
          this.stream.write(0);
        } catch (IOException iOException) {
          throw new IIOException("I/O error writing Application Extension!", iOException);
        } 
      } 
    } 
  }
  
  private void writeCommentExtension(GIFWritableImageMetadata paramGIFWritableImageMetadata) throws IOException {
    if (paramGIFWritableImageMetadata.comments != null)
      try {
        Iterator iterator = paramGIFWritableImageMetadata.comments.iterator();
        while (iterator.hasNext()) {
          this.stream.write(33);
          this.stream.write(254);
          writeBlocks((byte[])iterator.next());
          this.stream.write(0);
        } 
      } catch (IOException iOException) {
        throw new IIOException("I/O error writing Comment Extension!", iOException);
      }  
  }
  
  private void writeImageDescriptor(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, int paramInt5, byte[] paramArrayOfByte) throws IOException {
    try {
      this.stream.write(44);
      this.stream.writeShort((short)paramInt1);
      this.stream.writeShort((short)paramInt2);
      this.stream.writeShort((short)paramInt3);
      this.stream.writeShort((short)paramInt4);
      int i = (paramArrayOfByte != null) ? 128 : 0;
      if (paramBoolean1)
        i |= 0x40; 
      if (paramBoolean2)
        i |= 0x8; 
      i |= paramInt5 - 1;
      this.stream.write(i);
      if (paramArrayOfByte != null)
        this.stream.write(paramArrayOfByte); 
    } catch (IOException iOException) {
      throw new IIOException("I/O error writing Image Descriptor!", iOException);
    } 
  }
  
  private void writeImageDescriptor(GIFWritableImageMetadata paramGIFWritableImageMetadata, int paramInt) throws IOException { writeImageDescriptor(paramGIFWritableImageMetadata.imageLeftPosition, paramGIFWritableImageMetadata.imageTopPosition, paramGIFWritableImageMetadata.imageWidth, paramGIFWritableImageMetadata.imageHeight, paramGIFWritableImageMetadata.interlaceFlag, paramGIFWritableImageMetadata.sortFlag, paramInt, paramGIFWritableImageMetadata.localColorTable); }
  
  private void writeTrailer() throws IOException { this.stream.write(59); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\gif\GIFImageWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */