package com.sun.imageio.plugins.jpeg;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public class JPEGImageReader extends ImageReader {
  private boolean debug = false;
  
  private long structPointer = 0L;
  
  private ImageInputStream iis = null;
  
  private List imagePositions = null;
  
  private int numImages = 0;
  
  protected static final int WARNING_NO_EOI = 0;
  
  protected static final int WARNING_NO_JFIF_IN_THUMB = 1;
  
  protected static final int WARNING_IGNORE_INVALID_ICC = 2;
  
  private static final int MAX_WARNING = 2;
  
  private int currentImage = -1;
  
  private int width;
  
  private int height;
  
  private int colorSpaceCode;
  
  private int outColorSpaceCode;
  
  private int numComponents;
  
  private ColorSpace iccCS = null;
  
  private ColorConvertOp convert = null;
  
  private BufferedImage image = null;
  
  private WritableRaster raster = null;
  
  private WritableRaster target = null;
  
  private DataBufferByte buffer = null;
  
  private Rectangle destROI = null;
  
  private int[] destinationBands = null;
  
  private JPEGMetadata streamMetadata = null;
  
  private JPEGMetadata imageMetadata = null;
  
  private int imageMetadataIndex = -1;
  
  private boolean haveSeeked = false;
  
  private JPEGQTable[] abbrevQTables = null;
  
  private JPEGHuffmanTable[] abbrevDCHuffmanTables = null;
  
  private JPEGHuffmanTable[] abbrevACHuffmanTables = null;
  
  private int minProgressivePass = 0;
  
  private int maxProgressivePass = Integer.MAX_VALUE;
  
  private static final int UNKNOWN = -1;
  
  private static final int MIN_ESTIMATED_PASSES = 10;
  
  private int knownPassCount = -1;
  
  private int pass = 0;
  
  private float percentToDate = 0.0F;
  
  private float previousPassPercentage = 0.0F;
  
  private int progInterval = 0;
  
  private boolean tablesOnlyChecked = false;
  
  private Object disposerReferent = new Object();
  
  private DisposerRecord disposerRecord;
  
  private Thread theThread = null;
  
  private int theLockCount = 0;
  
  private CallBackLock cbLock = new CallBackLock();
  
  private static native void initReaderIDs(Class paramClass1, Class paramClass2, Class paramClass3);
  
  public JPEGImageReader(ImageReaderSpi paramImageReaderSpi) {
    super(paramImageReaderSpi);
    this.structPointer = initJPEGImageReader();
    this.disposerRecord = new JPEGReaderDisposerRecord(this.structPointer);
    Disposer.addRecord(this.disposerReferent, this.disposerRecord);
  }
  
  private native long initJPEGImageReader();
  
  protected void warningOccurred(int paramInt) {
    this.cbLock.lock();
    try {
      if (paramInt < 0 || paramInt > 2)
        throw new InternalError("Invalid warning index"); 
      processWarningOccurred("com.sun.imageio.plugins.jpeg.JPEGImageReaderResources", Integer.toString(paramInt));
    } finally {
      this.cbLock.unlock();
    } 
  }
  
  protected void warningWithMessage(String paramString) {
    this.cbLock.lock();
    try {
      processWarningOccurred(paramString);
    } finally {
      this.cbLock.unlock();
    } 
  }
  
  public void setInput(Object paramObject, boolean paramBoolean1, boolean paramBoolean2) {
    setThreadLock();
    try {
      this.cbLock.check();
      super.setInput(paramObject, paramBoolean1, paramBoolean2);
      this.ignoreMetadata = paramBoolean2;
      resetInternalState();
      this.iis = (ImageInputStream)paramObject;
      setSource(this.structPointer);
    } finally {
      clearThreadLock();
    } 
  }
  
  private int readInputData(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    this.cbLock.lock();
    try {
      return this.iis.read(paramArrayOfByte, paramInt1, paramInt2);
    } finally {
      this.cbLock.unlock();
    } 
  }
  
  private long skipInputBytes(long paramLong) throws IOException {
    this.cbLock.lock();
    try {
      return this.iis.skipBytes(paramLong);
    } finally {
      this.cbLock.unlock();
    } 
  }
  
  private native void setSource(long paramLong);
  
  private void checkTablesOnly() throws IOException {
    if (this.debug)
      System.out.println("Checking for tables-only image"); 
    long l = this.iis.getStreamPosition();
    if (this.debug) {
      System.out.println("saved pos is " + l);
      System.out.println("length is " + this.iis.length());
    } 
    boolean bool = readNativeHeader(true);
    if (bool) {
      if (this.debug) {
        System.out.println("tables-only image found");
        long l1 = this.iis.getStreamPosition();
        System.out.println("pos after return from native is " + l1);
      } 
      if (!this.ignoreMetadata) {
        this.iis.seek(l);
        this.haveSeeked = true;
        this.streamMetadata = new JPEGMetadata(true, false, this.iis, this);
        long l1 = this.iis.getStreamPosition();
        if (this.debug)
          System.out.println("pos after constructing stream metadata is " + l1); 
      } 
      if (hasNextImage())
        this.imagePositions.add(new Long(this.iis.getStreamPosition())); 
    } else {
      this.imagePositions.add(new Long(l));
      this.currentImage = 0;
    } 
    if (this.seekForwardOnly) {
      Long long = (Long)this.imagePositions.get(this.imagePositions.size() - 1);
      this.iis.flushBefore(long.longValue());
    } 
    this.tablesOnlyChecked = true;
  }
  
  public int getNumImages(boolean paramBoolean) throws IOException {
    setThreadLock();
    try {
      this.cbLock.check();
      return getNumImagesOnThread(paramBoolean);
    } finally {
      clearThreadLock();
    } 
  }
  
  private void skipPastImage(int paramInt) {
    this.cbLock.lock();
    try {
      gotoImage(paramInt);
      skipImage();
    } catch (IOException|IndexOutOfBoundsException iOException) {
    
    } finally {
      this.cbLock.unlock();
    } 
  }
  
  private int getNumImagesOnThread(boolean paramBoolean) throws IOException {
    if (this.numImages != 0)
      return this.numImages; 
    if (this.iis == null)
      throw new IllegalStateException("Input not set"); 
    if (paramBoolean == true) {
      if (this.seekForwardOnly)
        throw new IllegalStateException("seekForwardOnly and allowSearch can't both be true!"); 
      if (!this.tablesOnlyChecked)
        checkTablesOnly(); 
      this.iis.mark();
      gotoImage(0);
      JPEGBuffer jPEGBuffer = new JPEGBuffer(this.iis);
      jPEGBuffer.loadBuf(0);
      boolean bool = false;
      while (!bool) {
        bool = jPEGBuffer.scanForFF(this);
        switch (jPEGBuffer.buf[jPEGBuffer.bufPtr] & 0xFF) {
          case 216:
            this.numImages++;
          case 0:
          case 208:
          case 209:
          case 210:
          case 211:
          case 212:
          case 213:
          case 214:
          case 215:
          case 217:
            jPEGBuffer.bufAvail--;
            jPEGBuffer.bufPtr++;
            continue;
        } 
        jPEGBuffer.bufAvail--;
        jPEGBuffer.bufPtr++;
        jPEGBuffer.loadBuf(2);
        byte b = (jPEGBuffer.buf[jPEGBuffer.bufPtr++] & 0xFF) << 8 | jPEGBuffer.buf[jPEGBuffer.bufPtr++] & 0xFF;
        jPEGBuffer.bufAvail -= 2;
        b -= 2;
        jPEGBuffer.skipData(b);
      } 
      this.iis.reset();
      return this.numImages;
    } 
    return -1;
  }
  
  private void gotoImage(int paramInt) {
    if (this.iis == null)
      throw new IllegalStateException("Input not set"); 
    if (paramInt < this.minIndex)
      throw new IndexOutOfBoundsException(); 
    if (!this.tablesOnlyChecked)
      checkTablesOnly(); 
    if (paramInt < this.imagePositions.size()) {
      this.iis.seek(((Long)this.imagePositions.get(paramInt)).longValue());
    } else {
      Long long = (Long)this.imagePositions.get(this.imagePositions.size() - 1);
      this.iis.seek(long.longValue());
      skipImage();
      for (int i = this.imagePositions.size(); i <= paramInt; i++) {
        if (!hasNextImage())
          throw new IndexOutOfBoundsException(); 
        long = new Long(this.iis.getStreamPosition());
        this.imagePositions.add(long);
        if (this.seekForwardOnly)
          this.iis.flushBefore(long.longValue()); 
        if (i < paramInt)
          skipImage(); 
      } 
    } 
    if (this.seekForwardOnly)
      this.minIndex = paramInt; 
    this.haveSeeked = true;
  }
  
  private void skipImage() throws IOException {
    if (this.debug)
      System.out.println("skipImage called"); 
    boolean bool = false;
    for (int i = this.iis.read(); i != -1; i = this.iis.read()) {
      if (bool == true && i == 217)
        return; 
      bool = (i == 255) ? 1 : 0;
    } 
    throw new IndexOutOfBoundsException();
  }
  
  private boolean hasNextImage() throws IOException {
    if (this.debug)
      System.out.print("hasNextImage called; returning "); 
    this.iis.mark();
    boolean bool = false;
    for (int i = this.iis.read(); i != -1; i = this.iis.read()) {
      if (bool == true && i == 216) {
        this.iis.reset();
        if (this.debug)
          System.out.println("true"); 
        return true;
      } 
      bool = (i == 255) ? 1 : 0;
    } 
    this.iis.reset();
    if (this.debug)
      System.out.println("false"); 
    return false;
  }
  
  private void pushBack(int paramInt) {
    if (this.debug)
      System.out.println("pushing back " + paramInt + " bytes"); 
    this.cbLock.lock();
    try {
      this.iis.seek(this.iis.getStreamPosition() - paramInt);
    } finally {
      this.cbLock.unlock();
    } 
  }
  
  private void readHeader(int paramInt, boolean paramBoolean) throws IOException {
    gotoImage(paramInt);
    readNativeHeader(paramBoolean);
    this.currentImage = paramInt;
  }
  
  private boolean readNativeHeader(boolean paramBoolean) throws IOException {
    boolean bool = false;
    bool = readImageHeader(this.structPointer, this.haveSeeked, paramBoolean);
    this.haveSeeked = false;
    return bool;
  }
  
  private native boolean readImageHeader(long paramLong, boolean paramBoolean1, boolean paramBoolean2) throws IOException;
  
  private void setImageData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, byte[] paramArrayOfByte) {
    this.width = paramInt1;
    this.height = paramInt2;
    this.colorSpaceCode = paramInt3;
    this.outColorSpaceCode = paramInt4;
    this.numComponents = paramInt5;
    if (paramArrayOfByte == null) {
      this.iccCS = null;
      return;
    } 
    ICC_Profile iCC_Profile1 = null;
    try {
      iCC_Profile1 = ICC_Profile.getInstance(paramArrayOfByte);
    } catch (IllegalArgumentException illegalArgumentException) {
      this.iccCS = null;
      warningOccurred(2);
      return;
    } 
    byte[] arrayOfByte1 = iCC_Profile1.getData();
    ICC_Profile iCC_Profile2 = null;
    if (this.iccCS instanceof ICC_ColorSpace)
      iCC_Profile2 = ((ICC_ColorSpace)this.iccCS).getProfile(); 
    byte[] arrayOfByte2 = null;
    if (iCC_Profile2 != null)
      arrayOfByte2 = iCC_Profile2.getData(); 
    if (arrayOfByte2 == null || !Arrays.equals(arrayOfByte2, arrayOfByte1)) {
      this.iccCS = new ICC_ColorSpace(iCC_Profile1);
      try {
        float[] arrayOfFloat = this.iccCS.fromRGB(new float[] { 1.0F, 0.0F, 0.0F });
      } catch (CMMException cMMException) {
        this.iccCS = null;
        this.cbLock.lock();
        try {
          warningOccurred(2);
        } finally {
          this.cbLock.unlock();
        } 
      } 
    } 
  }
  
  public int getWidth(int paramInt) throws IOException {
    setThreadLock();
    try {
      if (this.currentImage != paramInt) {
        this.cbLock.check();
        readHeader(paramInt, true);
      } 
      return this.width;
    } finally {
      clearThreadLock();
    } 
  }
  
  public int getHeight(int paramInt) throws IOException {
    setThreadLock();
    try {
      if (this.currentImage != paramInt) {
        this.cbLock.check();
        readHeader(paramInt, true);
      } 
      return this.height;
    } finally {
      clearThreadLock();
    } 
  }
  
  private ImageTypeProducer getImageType(int paramInt) {
    ImageTypeProducer imageTypeProducer = null;
    if (paramInt > 0 && paramInt < 12)
      imageTypeProducer = ImageTypeProducer.getTypeProducer(paramInt); 
    return imageTypeProducer;
  }
  
  public ImageTypeSpecifier getRawImageType(int paramInt) throws IOException {
    setThreadLock();
    try {
      if (this.currentImage != paramInt) {
        this.cbLock.check();
        readHeader(paramInt, true);
      } 
      return getImageType(this.colorSpaceCode).getType();
    } finally {
      clearThreadLock();
    } 
  }
  
  public Iterator getImageTypes(int paramInt) throws IOException {
    setThreadLock();
    try {
      return getImageTypesOnThread(paramInt);
    } finally {
      clearThreadLock();
    } 
  }
  
  private Iterator getImageTypesOnThread(int paramInt) throws IOException {
    if (this.currentImage != paramInt) {
      this.cbLock.check();
      readHeader(paramInt, true);
    } 
    ImageTypeProducer imageTypeProducer = getImageType(this.colorSpaceCode);
    ArrayList arrayList = new ArrayList(1);
    switch (this.colorSpaceCode) {
      case 1:
        arrayList.add(imageTypeProducer);
        arrayList.add(getImageType(2));
        break;
      case 2:
        arrayList.add(imageTypeProducer);
        arrayList.add(getImageType(1));
        arrayList.add(getImageType(5));
        break;
      case 6:
        arrayList.add(imageTypeProducer);
        break;
      case 5:
        if (imageTypeProducer != null) {
          arrayList.add(imageTypeProducer);
          arrayList.add(getImageType(2));
        } 
        break;
      case 10:
        if (imageTypeProducer != null)
          arrayList.add(imageTypeProducer); 
        break;
      case 3:
        arrayList.add(getImageType(2));
        if (this.iccCS != null)
          arrayList.add(new ImageTypeProducer() {
                protected ImageTypeSpecifier produce() { return ImageTypeSpecifier.createInterleaved(JPEGImageReader.this.iccCS, JPEG.bOffsRGB, 0, false, false); }
              }); 
        arrayList.add(getImageType(1));
        arrayList.add(getImageType(5));
        break;
      case 7:
        arrayList.add(getImageType(6));
        break;
    } 
    return new ImageTypeIterator(arrayList.iterator());
  }
  
  private void checkColorConversion(BufferedImage paramBufferedImage, ImageReadParam paramImageReadParam) throws IIOException {
    ColorSpace colorSpace2;
    if (paramImageReadParam != null && (paramImageReadParam.getSourceBands() != null || paramImageReadParam.getDestinationBands() != null))
      return; 
    ColorModel colorModel = paramBufferedImage.getColorModel();
    if (colorModel instanceof java.awt.image.IndexColorModel)
      throw new IIOException("IndexColorModel not supported"); 
    ColorSpace colorSpace1 = colorModel.getColorSpace();
    int i = colorSpace1.getType();
    this.convert = null;
    switch (this.outColorSpaceCode) {
      case 1:
        if (i == 5) {
          setOutColorSpace(this.structPointer, 2);
          this.outColorSpaceCode = 2;
          this.numComponents = 3;
        } else if (i != 6) {
          throw new IIOException("Incompatible color conversion");
        } 
        return;
      case 2:
        if (i == 6) {
          if (this.colorSpaceCode == 3) {
            setOutColorSpace(this.structPointer, 1);
            this.outColorSpaceCode = 1;
            this.numComponents = 1;
          } 
        } else if (this.iccCS != null && colorModel.getNumComponents() == this.numComponents && colorSpace1 != this.iccCS) {
          this.convert = new ColorConvertOp(this.iccCS, colorSpace1, null);
        } else if (this.iccCS == null && !colorSpace1.isCS_sRGB() && colorModel.getNumComponents() == this.numComponents) {
          this.convert = new ColorConvertOp(JPEG.JCS.sRGB, colorSpace1, null);
        } else if (i != 5) {
          throw new IIOException("Incompatible color conversion");
        } 
        return;
      case 6:
        if (i != 5 || colorModel.getNumComponents() != this.numComponents)
          throw new IIOException("Incompatible color conversion"); 
        return;
      case 5:
        colorSpace2 = JPEG.JCS.getYCC();
        if (colorSpace2 == null)
          throw new IIOException("Incompatible color conversion"); 
        if (colorSpace1 != colorSpace2 && colorModel.getNumComponents() == this.numComponents)
          this.convert = new ColorConvertOp(colorSpace2, colorSpace1, null); 
        return;
      case 10:
        colorSpace2 = JPEG.JCS.getYCC();
        if (colorSpace2 == null || colorSpace1 != colorSpace2 || colorModel.getNumComponents() != this.numComponents)
          throw new IIOException("Incompatible color conversion"); 
        return;
    } 
    throw new IIOException("Incompatible color conversion");
  }
  
  private native void setOutColorSpace(long paramLong, int paramInt);
  
  public ImageReadParam getDefaultReadParam() { return new JPEGImageReadParam(); }
  
  public IIOMetadata getStreamMetadata() throws IOException {
    setThreadLock();
    try {
      if (!this.tablesOnlyChecked) {
        this.cbLock.check();
        checkTablesOnly();
      } 
      return this.streamMetadata;
    } finally {
      clearThreadLock();
    } 
  }
  
  public IIOMetadata getImageMetadata(int paramInt) throws IOException {
    setThreadLock();
    try {
      if (this.imageMetadataIndex == paramInt && this.imageMetadata != null)
        return this.imageMetadata; 
      this.cbLock.check();
      gotoImage(paramInt);
      this.imageMetadata = new JPEGMetadata(false, false, this.iis, this);
      this.imageMetadataIndex = paramInt;
      return this.imageMetadata;
    } finally {
      clearThreadLock();
    } 
  }
  
  public BufferedImage read(int paramInt, ImageReadParam paramImageReadParam) throws IOException {
    setThreadLock();
    try {
      this.cbLock.check();
      try {
        readInternal(paramInt, paramImageReadParam, false);
      } catch (RuntimeException runtimeException) {
        resetLibraryState(this.structPointer);
        throw runtimeException;
      } catch (IOException iOException) {
        resetLibraryState(this.structPointer);
        throw iOException;
      } 
      BufferedImage bufferedImage = this.image;
      this.image = null;
      return bufferedImage;
    } finally {
      clearThreadLock();
    } 
  }
  
  private Raster readInternal(int paramInt, ImageReadParam paramImageReadParam, boolean paramBoolean) throws IOException {
    readHeader(paramInt, false);
    WritableRaster writableRaster = null;
    int i = 0;
    if (!paramBoolean) {
      Iterator iterator = getImageTypes(paramInt);
      if (!iterator.hasNext())
        throw new IIOException("Unsupported Image Type"); 
      this.image = getDestination(paramImageReadParam, iterator, this.width, this.height);
      writableRaster = this.image.getRaster();
      i = this.image.getSampleModel().getNumBands();
      checkColorConversion(this.image, paramImageReadParam);
      checkReadParamBandSettings(paramImageReadParam, this.numComponents, i);
    } else {
      setOutColorSpace(this.structPointer, this.colorSpaceCode);
      this.image = null;
    } 
    int[] arrayOfInt1 = JPEG.bandOffsets[this.numComponents - 1];
    int j = paramBoolean ? this.numComponents : i;
    this.destinationBands = null;
    Rectangle rectangle = new Rectangle(0, 0, 0, 0);
    this.destROI = new Rectangle(0, 0, 0, 0);
    computeRegions(paramImageReadParam, this.width, this.height, this.image, rectangle, this.destROI);
    int k = 1;
    int m = 1;
    this.minProgressivePass = 0;
    this.maxProgressivePass = Integer.MAX_VALUE;
    if (paramImageReadParam != null) {
      k = paramImageReadParam.getSourceXSubsampling();
      m = paramImageReadParam.getSourceYSubsampling();
      int[] arrayOfInt = paramImageReadParam.getSourceBands();
      if (arrayOfInt != null) {
        arrayOfInt1 = arrayOfInt;
        j = arrayOfInt1.length;
      } 
      if (!paramBoolean)
        this.destinationBands = paramImageReadParam.getDestinationBands(); 
      this.minProgressivePass = paramImageReadParam.getSourceMinProgressivePass();
      this.maxProgressivePass = paramImageReadParam.getSourceMaxProgressivePass();
      if (paramImageReadParam instanceof JPEGImageReadParam) {
        JPEGImageReadParam jPEGImageReadParam = (JPEGImageReadParam)paramImageReadParam;
        if (jPEGImageReadParam.areTablesSet()) {
          this.abbrevQTables = jPEGImageReadParam.getQTables();
          this.abbrevDCHuffmanTables = jPEGImageReadParam.getDCHuffmanTables();
          this.abbrevACHuffmanTables = jPEGImageReadParam.getACHuffmanTables();
        } 
      } 
    } 
    int n = this.destROI.width * j;
    this.buffer = new DataBufferByte(n);
    int[] arrayOfInt2 = JPEG.bandOffsets[j - 1];
    this.raster = Raster.createInterleavedRaster(this.buffer, this.destROI.width, 1, n, j, arrayOfInt2, null);
    if (paramBoolean) {
      this.target = Raster.createInterleavedRaster(0, this.destROI.width, this.destROI.height, n, j, arrayOfInt2, null);
    } else {
      this.target = writableRaster;
    } 
    int[] arrayOfInt3 = this.target.getSampleModel().getSampleSize();
    int i1;
    for (i1 = 0; i1 < arrayOfInt3.length; i1++) {
      if (arrayOfInt3[i1] <= 0 || arrayOfInt3[i1] > 8)
        throw new IIOException("Illegal band size: should be 0 < size <= 8"); 
    } 
    i1 = (this.updateListeners != null || this.progressListeners != null) ? 1 : 0;
    initProgressData();
    if (paramInt == this.imageMetadataIndex) {
      this.knownPassCount = 0;
      Iterator iterator = this.imageMetadata.markerSequence.iterator();
      while (iterator.hasNext()) {
        if (iterator.next() instanceof SOSMarkerSegment)
          this.knownPassCount++; 
      } 
    } 
    this.progInterval = Math.max((this.target.getHeight() - 1) / 20, 1);
    if (this.knownPassCount > 0) {
      this.progInterval *= this.knownPassCount;
    } else if (this.maxProgressivePass != Integer.MAX_VALUE) {
      this.progInterval *= (this.maxProgressivePass - this.minProgressivePass + 1);
    } 
    if (this.debug) {
      System.out.println("**** Read Data *****");
      System.out.println("numRasterBands is " + j);
      System.out.print("srcBands:");
      byte b;
      for (b = 0; b < arrayOfInt1.length; b++)
        System.out.print(" " + arrayOfInt1[b]); 
      System.out.println();
      System.out.println("destination bands is " + this.destinationBands);
      if (this.destinationBands != null) {
        for (b = 0; b < this.destinationBands.length; b++)
          System.out.print(" " + this.destinationBands[b]); 
        System.out.println();
      } 
      System.out.println("sourceROI is " + rectangle);
      System.out.println("destROI is " + this.destROI);
      System.out.println("periodX is " + k);
      System.out.println("periodY is " + m);
      System.out.println("minProgressivePass is " + this.minProgressivePass);
      System.out.println("maxProgressivePass is " + this.maxProgressivePass);
      System.out.println("callbackUpdates is " + i1);
    } 
    processImageStarted(this.currentImage);
    boolean bool = false;
    bool = readImage(paramInt, this.structPointer, this.buffer.getData(), j, arrayOfInt1, arrayOfInt3, rectangle.x, rectangle.y, rectangle.width, rectangle.height, k, m, this.abbrevQTables, this.abbrevDCHuffmanTables, this.abbrevACHuffmanTables, this.minProgressivePass, this.maxProgressivePass, i1);
    if (bool) {
      processReadAborted();
    } else {
      processImageComplete();
    } 
    return this.target;
  }
  
  private void acceptPixels(int paramInt, boolean paramBoolean) throws IOException {
    if (this.convert != null)
      this.convert.filter(this.raster, this.raster); 
    this.target.setRect(this.destROI.x, this.destROI.y + paramInt, this.raster);
    this.cbLock.lock();
    try {
      processImageUpdate(this.image, this.destROI.x, this.destROI.y + paramInt, this.raster.getWidth(), 1, 1, 1, this.destinationBands);
      if (paramInt > 0 && paramInt % this.progInterval == 0) {
        int i = this.target.getHeight() - 1;
        float f = paramInt / i;
        if (paramBoolean) {
          if (this.knownPassCount != -1) {
            processImageProgress((this.pass + f) * 100.0F / this.knownPassCount);
          } else if (this.maxProgressivePass != Integer.MAX_VALUE) {
            processImageProgress((this.pass + f) * 100.0F / (this.maxProgressivePass - this.minProgressivePass + 1));
          } else {
            int j = Math.max(2, 10 - this.pass);
            int k = this.pass + j - 1;
            this.progInterval = Math.max(i / 20 * k, k);
            if (paramInt % this.progInterval == 0) {
              this.percentToDate = this.previousPassPercentage + (1.0F - this.previousPassPercentage) * f / j;
              if (this.debug) {
                System.out.print("pass= " + this.pass);
                System.out.print(", y= " + paramInt);
                System.out.print(", progInt= " + this.progInterval);
                System.out.print(", % of pass: " + f);
                System.out.print(", rem. passes: " + j);
                System.out.print(", prev%: " + this.previousPassPercentage);
                System.out.print(", %ToDate: " + this.percentToDate);
                System.out.print(" ");
              } 
              processImageProgress(this.percentToDate * 100.0F);
            } 
          } 
        } else {
          processImageProgress(f * 100.0F);
        } 
      } 
    } finally {
      this.cbLock.unlock();
    } 
  }
  
  private void initProgressData() throws IOException {
    this.knownPassCount = -1;
    this.pass = 0;
    this.percentToDate = 0.0F;
    this.previousPassPercentage = 0.0F;
    this.progInterval = 0;
  }
  
  private void passStarted(int paramInt) {
    this.cbLock.lock();
    try {
      this.pass = paramInt;
      this.previousPassPercentage = this.percentToDate;
      processPassStarted(this.image, paramInt, this.minProgressivePass, this.maxProgressivePass, 0, 0, 1, 1, this.destinationBands);
    } finally {
      this.cbLock.unlock();
    } 
  }
  
  private void passComplete() throws IOException {
    this.cbLock.lock();
    try {
      processPassComplete(this.image);
    } finally {
      this.cbLock.unlock();
    } 
  }
  
  void thumbnailStarted(int paramInt) {
    this.cbLock.lock();
    try {
      processThumbnailStarted(this.currentImage, paramInt);
    } finally {
      this.cbLock.unlock();
    } 
  }
  
  void thumbnailProgress(float paramFloat) {
    this.cbLock.lock();
    try {
      processThumbnailProgress(paramFloat);
    } finally {
      this.cbLock.unlock();
    } 
  }
  
  void thumbnailComplete() throws IOException {
    this.cbLock.lock();
    try {
      processThumbnailComplete();
    } finally {
      this.cbLock.unlock();
    } 
  }
  
  private native boolean readImage(int paramInt1, long paramLong, byte[] paramArrayOfByte, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, JPEGQTable[] paramArrayOfJPEGQTable, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable2, int paramInt9, int paramInt10, boolean paramBoolean);
  
  public void abort() throws IOException {
    setThreadLock();
    try {
      super.abort();
      abortRead(this.structPointer);
    } finally {
      clearThreadLock();
    } 
  }
  
  private native void abortRead(long paramLong);
  
  private native void resetLibraryState(long paramLong);
  
  public boolean canReadRaster() throws IOException { return true; }
  
  public Raster readRaster(int paramInt, ImageReadParam paramImageReadParam) throws IOException {
    setThreadLock();
    Raster raster1 = null;
    try {
      this.cbLock.check();
      Point point = null;
      if (paramImageReadParam != null) {
        point = paramImageReadParam.getDestinationOffset();
        paramImageReadParam.setDestinationOffset(new Point(0, 0));
      } 
      raster1 = readInternal(paramInt, paramImageReadParam, true);
      if (point != null)
        this.target = this.target.createWritableTranslatedChild(point.x, point.y); 
    } catch (RuntimeException runtimeException) {
      resetLibraryState(this.structPointer);
      throw runtimeException;
    } catch (IOException iOException) {
      resetLibraryState(this.structPointer);
      throw iOException;
    } finally {
      clearThreadLock();
    } 
    return raster1;
  }
  
  public boolean readerSupportsThumbnails() throws IOException { return true; }
  
  public int getNumThumbnails(int paramInt) throws IOException {
    setThreadLock();
    try {
      this.cbLock.check();
      getImageMetadata(paramInt);
      JFIFMarkerSegment jFIFMarkerSegment = (JFIFMarkerSegment)this.imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
      int i = 0;
      if (jFIFMarkerSegment != null) {
        i = (jFIFMarkerSegment.thumb == null) ? 0 : 1;
        i += jFIFMarkerSegment.extSegments.size();
      } 
      return i;
    } finally {
      clearThreadLock();
    } 
  }
  
  public int getThumbnailWidth(int paramInt1, int paramInt2) throws IOException {
    setThreadLock();
    try {
      this.cbLock.check();
      if (paramInt2 < 0 || paramInt2 >= getNumThumbnails(paramInt1))
        throw new IndexOutOfBoundsException("No such thumbnail"); 
      JFIFMarkerSegment jFIFMarkerSegment = (JFIFMarkerSegment)this.imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
      return jFIFMarkerSegment.getThumbnailWidth(paramInt2);
    } finally {
      clearThreadLock();
    } 
  }
  
  public int getThumbnailHeight(int paramInt1, int paramInt2) throws IOException {
    setThreadLock();
    try {
      this.cbLock.check();
      if (paramInt2 < 0 || paramInt2 >= getNumThumbnails(paramInt1))
        throw new IndexOutOfBoundsException("No such thumbnail"); 
      JFIFMarkerSegment jFIFMarkerSegment = (JFIFMarkerSegment)this.imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
      return jFIFMarkerSegment.getThumbnailHeight(paramInt2);
    } finally {
      clearThreadLock();
    } 
  }
  
  public BufferedImage readThumbnail(int paramInt1, int paramInt2) throws IOException {
    setThreadLock();
    try {
      this.cbLock.check();
      if (paramInt2 < 0 || paramInt2 >= getNumThumbnails(paramInt1))
        throw new IndexOutOfBoundsException("No such thumbnail"); 
      JFIFMarkerSegment jFIFMarkerSegment = (JFIFMarkerSegment)this.imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
      return jFIFMarkerSegment.getThumbnail(this.iis, paramInt2, this);
    } finally {
      clearThreadLock();
    } 
  }
  
  private void resetInternalState() throws IOException {
    resetReader(this.structPointer);
    this.numImages = 0;
    this.imagePositions = new ArrayList();
    this.currentImage = -1;
    this.image = null;
    this.raster = null;
    this.target = null;
    this.buffer = null;
    this.destROI = null;
    this.destinationBands = null;
    this.streamMetadata = null;
    this.imageMetadata = null;
    this.imageMetadataIndex = -1;
    this.haveSeeked = false;
    this.tablesOnlyChecked = false;
    this.iccCS = null;
    initProgressData();
  }
  
  public void reset() throws IOException {
    setThreadLock();
    try {
      this.cbLock.check();
      super.reset();
    } finally {
      clearThreadLock();
    } 
  }
  
  private native void resetReader(long paramLong);
  
  public void dispose() throws IOException {
    setThreadLock();
    try {
      this.cbLock.check();
      if (this.structPointer != 0L) {
        this.disposerRecord.dispose();
        this.structPointer = 0L;
      } 
    } finally {
      clearThreadLock();
    } 
  }
  
  private static native void disposeReader(long paramLong);
  
  private void setThreadLock() throws IOException {
    Thread thread = Thread.currentThread();
    if (this.theThread != null) {
      if (this.theThread != thread)
        throw new IllegalStateException("Attempt to use instance of " + this + " locked on thread " + this.theThread + " from thread " + thread); 
      this.theLockCount++;
    } else {
      this.theThread = thread;
      this.theLockCount = 1;
    } 
  }
  
  private void clearThreadLock() throws IOException {
    Thread thread = Thread.currentThread();
    if (this.theThread == null || this.theThread != thread)
      throw new IllegalStateException("Attempt to clear thread lock  form wrong thread. Locked thread: " + this.theThread + "; current thread: " + thread); 
    this.theLockCount--;
    if (this.theLockCount == 0)
      this.theThread = null; 
  }
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("jpeg");
            return null;
          }
        });
    initReaderIDs(ImageInputStream.class, JPEGQTable.class, JPEGHuffmanTable.class);
  }
  
  private static class CallBackLock {
    private State lockState = State.Unlocked;
    
    void check() throws IOException {
      if (this.lockState != State.Unlocked)
        throw new IllegalStateException("Access to the reader is not allowed"); 
    }
    
    private void lock() throws IOException { this.lockState = State.Locked; }
    
    private void unlock() throws IOException { this.lockState = State.Unlocked; }
    
    private enum State {
      Unlocked, Locked;
    }
  }
  
  private static class JPEGReaderDisposerRecord implements DisposerRecord {
    private long pData;
    
    public JPEGReaderDisposerRecord(long param1Long) { this.pData = param1Long; }
    
    public void dispose() throws IOException {
      if (this.pData != 0L) {
        JPEGImageReader.disposeReader(this.pData);
        this.pData = 0L;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGImageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */