package com.sun.imageio.plugins.gif;

import com.sun.imageio.plugins.common.ReaderUtil;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.WritableRaster;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class GIFImageReader extends ImageReader {
  ImageInputStream stream = null;
  
  boolean gotHeader = false;
  
  GIFStreamMetadata streamMetadata = null;
  
  int currIndex = -1;
  
  GIFImageMetadata imageMetadata = null;
  
  List imageStartPosition = new ArrayList();
  
  int imageMetadataLength;
  
  int numImages = -1;
  
  byte[] block = new byte[255];
  
  int blockLength = 0;
  
  int bitPos = 0;
  
  int nextByte = 0;
  
  int initCodeSize;
  
  int clearCode;
  
  int eofCode;
  
  int next32Bits = 0;
  
  boolean lastBlockFound = false;
  
  BufferedImage theImage = null;
  
  WritableRaster theTile = null;
  
  int width = -1;
  
  int height = -1;
  
  int streamX = -1;
  
  int streamY = -1;
  
  int rowsDone = 0;
  
  int interlacePass = 0;
  
  private byte[] fallbackColorTable = null;
  
  static final int[] interlaceIncrement = { 8, 8, 4, 2, -1 };
  
  static final int[] interlaceOffset = { 0, 4, 2, 1, -1 };
  
  Rectangle sourceRegion;
  
  int sourceXSubsampling;
  
  int sourceYSubsampling;
  
  int sourceMinProgressivePass;
  
  int sourceMaxProgressivePass;
  
  Point destinationOffset;
  
  Rectangle destinationRegion;
  
  int updateMinY;
  
  int updateYStep;
  
  boolean decodeThisRow = true;
  
  int destY = 0;
  
  byte[] rowBuf;
  
  private static byte[] defaultPalette = null;
  
  public GIFImageReader(ImageReaderSpi paramImageReaderSpi) { super(paramImageReaderSpi); }
  
  public void setInput(Object paramObject, boolean paramBoolean1, boolean paramBoolean2) {
    super.setInput(paramObject, paramBoolean1, paramBoolean2);
    if (paramObject != null) {
      if (!(paramObject instanceof ImageInputStream))
        throw new IllegalArgumentException("input not an ImageInputStream!"); 
      this.stream = (ImageInputStream)paramObject;
    } else {
      this.stream = null;
    } 
    resetStreamSettings();
  }
  
  public int getNumImages(boolean paramBoolean) throws IIOException {
    if (this.stream == null)
      throw new IllegalStateException("Input not set!"); 
    if (this.seekForwardOnly && paramBoolean)
      throw new IllegalStateException("seekForwardOnly and allowSearch can't both be true!"); 
    if (this.numImages > 0)
      return this.numImages; 
    if (paramBoolean)
      this.numImages = locateImage(2147483647) + 1; 
    return this.numImages;
  }
  
  private void checkIndex(int paramInt) {
    if (paramInt < this.minIndex)
      throw new IndexOutOfBoundsException("imageIndex < minIndex!"); 
    if (this.seekForwardOnly)
      this.minIndex = paramInt; 
  }
  
  public int getWidth(int paramInt) throws IIOException {
    checkIndex(paramInt);
    int i = locateImage(paramInt);
    if (i != paramInt)
      throw new IndexOutOfBoundsException(); 
    readMetadata();
    return this.imageMetadata.imageWidth;
  }
  
  public int getHeight(int paramInt) throws IIOException {
    checkIndex(paramInt);
    int i = locateImage(paramInt);
    if (i != paramInt)
      throw new IndexOutOfBoundsException(); 
    readMetadata();
    return this.imageMetadata.imageHeight;
  }
  
  private ImageTypeSpecifier createIndexed(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt) {
    MultiPixelPackedSampleModel multiPixelPackedSampleModel;
    IndexColorModel indexColorModel;
    if (this.imageMetadata.transparentColorFlag) {
      int i = Math.min(this.imageMetadata.transparentColorIndex, paramArrayOfByte1.length - 1);
      indexColorModel = new IndexColorModel(paramInt, paramArrayOfByte1.length, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3, i);
    } else {
      indexColorModel = new IndexColorModel(paramInt, paramArrayOfByte1.length, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3);
    } 
    if (paramInt == 8) {
      int[] arrayOfInt = { 0 };
      multiPixelPackedSampleModel = new PixelInterleavedSampleModel(0, 1, 1, 1, 1, arrayOfInt);
    } else {
      multiPixelPackedSampleModel = new MultiPixelPackedSampleModel(0, 1, 1, paramInt);
    } 
    return new ImageTypeSpecifier(indexColorModel, multiPixelPackedSampleModel);
  }
  
  public Iterator getImageTypes(int paramInt) throws IIOException {
    byte b1;
    byte[] arrayOfByte1;
    checkIndex(paramInt);
    int i = locateImage(paramInt);
    if (i != paramInt)
      throw new IndexOutOfBoundsException(); 
    readMetadata();
    ArrayList arrayList = new ArrayList(1);
    if (this.imageMetadata.localColorTable != null) {
      arrayOfByte1 = this.imageMetadata.localColorTable;
      this.fallbackColorTable = this.imageMetadata.localColorTable;
    } else {
      arrayOfByte1 = this.streamMetadata.globalColorTable;
    } 
    if (arrayOfByte1 == null) {
      if (this.fallbackColorTable == null) {
        processWarningOccurred("Use default color table.");
        this.fallbackColorTable = getDefaultPalette();
      } 
      arrayOfByte1 = this.fallbackColorTable;
    } 
    int j = arrayOfByte1.length / 3;
    if (j == 2) {
      b1 = 1;
    } else if (j == 4) {
      b1 = 2;
    } else if (j == 8 || j == 16) {
      b1 = 4;
    } else {
      b1 = 8;
    } 
    byte b2 = 1 << b1;
    byte[] arrayOfByte2 = new byte[b2];
    byte[] arrayOfByte3 = new byte[b2];
    byte[] arrayOfByte4 = new byte[b2];
    byte b3 = 0;
    for (byte b4 = 0; b4 < j; b4++) {
      arrayOfByte2[b4] = arrayOfByte1[b3++];
      arrayOfByte3[b4] = arrayOfByte1[b3++];
      arrayOfByte4[b4] = arrayOfByte1[b3++];
    } 
    arrayList.add(createIndexed(arrayOfByte2, arrayOfByte3, arrayOfByte4, b1));
    return arrayList.iterator();
  }
  
  public ImageReadParam getDefaultReadParam() { return new ImageReadParam(); }
  
  public IIOMetadata getStreamMetadata() throws IIOException {
    readHeader();
    return this.streamMetadata;
  }
  
  public IIOMetadata getImageMetadata(int paramInt) throws IIOException {
    checkIndex(paramInt);
    int i = locateImage(paramInt);
    if (i != paramInt)
      throw new IndexOutOfBoundsException("Bad image index!"); 
    readMetadata();
    return this.imageMetadata;
  }
  
  private void initNext32Bits() {
    this.next32Bits = this.block[0] & 0xFF;
    this.next32Bits |= (this.block[1] & 0xFF) << 8;
    this.next32Bits |= (this.block[2] & 0xFF) << 16;
    this.next32Bits |= this.block[3] << 24;
    this.nextByte = 4;
  }
  
  private int getCode(int paramInt1, int paramInt2) throws IOException {
    if (this.bitPos + paramInt1 > 32)
      return this.eofCode; 
    int i = this.next32Bits >> this.bitPos & paramInt2;
    this.bitPos += paramInt1;
    while (this.bitPos >= 8 && !this.lastBlockFound) {
      this.next32Bits >>>= 8;
      this.bitPos -= 8;
      if (this.nextByte >= this.blockLength) {
        this.blockLength = this.stream.readUnsignedByte();
        if (this.blockLength == 0) {
          this.lastBlockFound = true;
          return i;
        } 
        int j = this.blockLength;
        int k = 0;
        while (j > 0) {
          int m = this.stream.read(this.block, k, j);
          k += m;
          j -= m;
        } 
        this.nextByte = 0;
      } 
      this.next32Bits |= this.block[this.nextByte++] << 24;
    } 
    return i;
  }
  
  public void initializeStringTable(int[] paramArrayOfInt1, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int[] paramArrayOfInt2) {
    int i = 1 << this.initCodeSize;
    int j;
    for (j = 0; j < i; j++) {
      paramArrayOfInt1[j] = -1;
      paramArrayOfByte1[j] = (byte)j;
      paramArrayOfByte2[j] = (byte)j;
      paramArrayOfInt2[j] = 1;
    } 
    for (j = i; j < 4096; j++) {
      paramArrayOfInt1[j] = -1;
      paramArrayOfInt2[j] = 1;
    } 
  }
  
  private void outputRow() {
    int i = Math.min(this.sourceRegion.width, this.destinationRegion.width * this.sourceXSubsampling);
    int j = this.destinationRegion.x;
    if (this.sourceXSubsampling == 1) {
      this.theTile.setDataElements(j, this.destY, i, 1, this.rowBuf);
    } else {
      int k = 0;
      while (k < i) {
        this.theTile.setSample(j, this.destY, 0, this.rowBuf[k] & 0xFF);
        k += this.sourceXSubsampling;
        j++;
      } 
    } 
    if (this.updateListeners != null) {
      int[] arrayOfInt = { 0 };
      processImageUpdate(this.theImage, j, this.destY, i, 1, 1, this.updateYStep, arrayOfInt);
    } 
  }
  
  private void computeDecodeThisRow() { this.decodeThisRow = (this.destY < this.destinationRegion.y + this.destinationRegion.height && this.streamY >= this.sourceRegion.y && this.streamY < this.sourceRegion.y + this.sourceRegion.height && (this.streamY - this.sourceRegion.y) % this.sourceYSubsampling == 0); }
  
  private void outputPixels(byte[] paramArrayOfByte, int paramInt) {
    if (this.interlacePass < this.sourceMinProgressivePass || this.interlacePass > this.sourceMaxProgressivePass)
      return; 
    for (byte b = 0; b < paramInt; b++) {
      if (this.streamX >= this.sourceRegion.x)
        this.rowBuf[this.streamX - this.sourceRegion.x] = paramArrayOfByte[b]; 
      this.streamX++;
      if (this.streamX == this.width) {
        this.rowsDone++;
        processImageProgress(100.0F * this.rowsDone / this.height);
        if (this.decodeThisRow)
          outputRow(); 
        this.streamX = 0;
        if (this.imageMetadata.interlaceFlag) {
          this.streamY += interlaceIncrement[this.interlacePass];
          if (this.streamY >= this.height) {
            if (this.updateListeners != null)
              processPassComplete(this.theImage); 
            this.interlacePass++;
            if (this.interlacePass > this.sourceMaxProgressivePass)
              return; 
            this.streamY = interlaceOffset[this.interlacePass];
            startPass(this.interlacePass);
          } 
        } else {
          this.streamY++;
        } 
        this.destY = this.destinationRegion.y + (this.streamY - this.sourceRegion.y) / this.sourceYSubsampling;
        computeDecodeThisRow();
      } 
    } 
  }
  
  private void readHeader() {
    if (this.gotHeader)
      return; 
    if (this.stream == null)
      throw new IllegalStateException("Input not set!"); 
    this.streamMetadata = new GIFStreamMetadata();
    try {
      this.stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
      byte[] arrayOfByte = new byte[6];
      this.stream.readFully(arrayOfByte);
      StringBuffer stringBuffer = new StringBuffer(3);
      stringBuffer.append((char)arrayOfByte[3]);
      stringBuffer.append((char)arrayOfByte[4]);
      stringBuffer.append((char)arrayOfByte[5]);
      this.streamMetadata.version = stringBuffer.toString();
      this.streamMetadata.logicalScreenWidth = this.stream.readUnsignedShort();
      this.streamMetadata.logicalScreenHeight = this.stream.readUnsignedShort();
      int i = this.stream.readUnsignedByte();
      boolean bool = ((i & 0x80) != 0) ? 1 : 0;
      this.streamMetadata.colorResolution = (i >> 4 & 0x7) + 1;
      this.streamMetadata.sortFlag = ((i & 0x8) != 0);
      int j = 1 << (i & 0x7) + 1;
      this.streamMetadata.backgroundColorIndex = this.stream.readUnsignedByte();
      this.streamMetadata.pixelAspectRatio = this.stream.readUnsignedByte();
      if (bool) {
        this.streamMetadata.globalColorTable = new byte[3 * j];
        this.stream.readFully(this.streamMetadata.globalColorTable);
      } else {
        this.streamMetadata.globalColorTable = null;
      } 
      this.imageStartPosition.add(Long.valueOf(this.stream.getStreamPosition()));
    } catch (IOException iOException) {
      throw new IIOException("I/O error reading header!", iOException);
    } 
    this.gotHeader = true;
  }
  
  private boolean skipImage() throws IIOException {
    try {
      label31: while (true) {
        int i = this.stream.readUnsignedByte();
        if (i == 44) {
          this.stream.skipBytes(8);
          int k = this.stream.readUnsignedByte();
          if ((k & 0x80) != 0) {
            int n = (k & 0x7) + 1;
            this.stream.skipBytes(3 * (1 << n));
          } 
          this.stream.skipBytes(1);
          int m = 0;
          do {
            m = this.stream.readUnsignedByte();
            this.stream.skipBytes(m);
          } while (m > 0);
          return true;
        } 
        if (i == 59)
          return false; 
        if (i == 33) {
          int k = this.stream.readUnsignedByte();
          int m = 0;
          while (true) {
            m = this.stream.readUnsignedByte();
            this.stream.skipBytes(m);
            if (m <= 0)
              continue label31; 
          } 
          break;
        } 
        if (i == 0)
          return false; 
        int j = 0;
        while (true) {
          j = this.stream.readUnsignedByte();
          this.stream.skipBytes(j);
          if (j <= 0)
            continue label31; 
        } 
        break;
      } 
    } catch (EOFException eOFException) {
      return false;
    } catch (IOException iOException) {
      throw new IIOException("I/O error locating image!", iOException);
    } 
  }
  
  private int locateImage(int paramInt) throws IIOException {
    readHeader();
    try {
      int i = Math.min(paramInt, this.imageStartPosition.size() - 1);
      Long long = (Long)this.imageStartPosition.get(i);
      this.stream.seek(long.longValue());
      while (i < paramInt) {
        if (!skipImage())
          return --i; 
        Long long1 = new Long(this.stream.getStreamPosition());
        this.imageStartPosition.add(long1);
        i++;
      } 
    } catch (IOException iOException) {
      throw new IIOException("Couldn't seek!", iOException);
    } 
    if (this.currIndex != paramInt)
      this.imageMetadata = null; 
    this.currIndex = paramInt;
    return paramInt;
  }
  
  private byte[] concatenateBlocks() throws IOException {
    byte[] arrayOfByte;
    for (arrayOfByte = new byte[0];; arrayOfByte = arrayOfByte1) {
      int i = this.stream.readUnsignedByte();
      if (i == 0)
        break; 
      byte[] arrayOfByte1 = new byte[arrayOfByte.length + i];
      System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, arrayOfByte.length);
      this.stream.readFully(arrayOfByte1, arrayOfByte.length, i);
    } 
    return arrayOfByte;
  }
  
  private void readMetadata() {
    if (this.stream == null)
      throw new IllegalStateException("Input not set!"); 
    try {
      int i;
      this.imageMetadata = new GIFImageMetadata();
      long l = this.stream.getStreamPosition();
      label68: while (true) {
        i = this.stream.readUnsignedByte();
        if (i == 44) {
          this.imageMetadata.imageLeftPosition = this.stream.readUnsignedShort();
          this.imageMetadata.imageTopPosition = this.stream.readUnsignedShort();
          this.imageMetadata.imageWidth = this.stream.readUnsignedShort();
          this.imageMetadata.imageHeight = this.stream.readUnsignedShort();
          int j = this.stream.readUnsignedByte();
          boolean bool = ((j & 0x80) != 0) ? 1 : 0;
          this.imageMetadata.interlaceFlag = ((j & 0x40) != 0);
          this.imageMetadata.sortFlag = ((j & 0x20) != 0);
          int k = 1 << (j & 0x7) + 1;
          if (bool) {
            this.imageMetadata.localColorTable = new byte[3 * k];
            this.stream.readFully(this.imageMetadata.localColorTable);
          } else {
            this.imageMetadata.localColorTable = null;
          } 
          this.imageMetadataLength = (int)(this.stream.getStreamPosition() - l);
          return;
        } 
        if (i == 33) {
          int j = this.stream.readUnsignedByte();
          if (j == 249) {
            int m = this.stream.readUnsignedByte();
            int n = this.stream.readUnsignedByte();
            this.imageMetadata.disposalMethod = n >> 2 & 0x3;
            this.imageMetadata.userInputFlag = ((n & 0x2) != 0);
            this.imageMetadata.transparentColorFlag = ((n & true) != 0);
            this.imageMetadata.delayTime = this.stream.readUnsignedShort();
            this.imageMetadata.transparentColorIndex = this.stream.readUnsignedByte();
            int i1 = this.stream.readUnsignedByte();
            continue;
          } 
          if (j == 1) {
            int m = this.stream.readUnsignedByte();
            this.imageMetadata.hasPlainTextExtension = true;
            this.imageMetadata.textGridLeft = this.stream.readUnsignedShort();
            this.imageMetadata.textGridTop = this.stream.readUnsignedShort();
            this.imageMetadata.textGridWidth = this.stream.readUnsignedShort();
            this.imageMetadata.textGridHeight = this.stream.readUnsignedShort();
            this.imageMetadata.characterCellWidth = this.stream.readUnsignedByte();
            this.imageMetadata.characterCellHeight = this.stream.readUnsignedByte();
            this.imageMetadata.textForegroundColor = this.stream.readUnsignedByte();
            this.imageMetadata.textBackgroundColor = this.stream.readUnsignedByte();
            this.imageMetadata.text = concatenateBlocks();
            continue;
          } 
          if (j == 254) {
            byte[] arrayOfByte = concatenateBlocks();
            if (this.imageMetadata.comments == null)
              this.imageMetadata.comments = new ArrayList(); 
            this.imageMetadata.comments.add(arrayOfByte);
            continue;
          } 
          if (j == 255) {
            int m = this.stream.readUnsignedByte();
            byte[] arrayOfByte1 = new byte[8];
            byte[] arrayOfByte2 = new byte[3];
            byte[] arrayOfByte3 = new byte[m];
            this.stream.readFully(arrayOfByte3);
            int n = copyData(arrayOfByte3, 0, arrayOfByte1);
            n = copyData(arrayOfByte3, n, arrayOfByte2);
            byte[] arrayOfByte4 = concatenateBlocks();
            if (n < m) {
              int i1 = m - n;
              byte[] arrayOfByte = new byte[i1 + arrayOfByte4.length];
              System.arraycopy(arrayOfByte3, n, arrayOfByte, 0, i1);
              System.arraycopy(arrayOfByte4, 0, arrayOfByte, i1, arrayOfByte4.length);
              arrayOfByte4 = arrayOfByte;
            } 
            if (this.imageMetadata.applicationIDs == null) {
              this.imageMetadata.applicationIDs = new ArrayList();
              this.imageMetadata.authenticationCodes = new ArrayList();
              this.imageMetadata.applicationData = new ArrayList();
            } 
            this.imageMetadata.applicationIDs.add(arrayOfByte1);
            this.imageMetadata.authenticationCodes.add(arrayOfByte2);
            this.imageMetadata.applicationData.add(arrayOfByte4);
            continue;
          } 
          int k = 0;
          while (true) {
            k = this.stream.readUnsignedByte();
            this.stream.skipBytes(k);
            if (k <= 0)
              continue label68; 
          } 
        } 
        break;
      } 
      if (i == 59)
        throw new IndexOutOfBoundsException("Attempt to read past end of image sequence!"); 
      throw new IIOException("Unexpected block type " + i + "!");
    } catch (IIOException iIOException) {
      throw iIOException;
    } catch (IOException iOException) {
      throw new IIOException("I/O error reading image metadata!", iOException);
    } 
  }
  
  private int copyData(byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2) {
    int i = paramArrayOfByte2.length;
    int j = paramArrayOfByte1.length - paramInt;
    if (i > j)
      i = j; 
    System.arraycopy(paramArrayOfByte1, paramInt, paramArrayOfByte2, 0, i);
    return paramInt + i;
  }
  
  private void startPass(int paramInt) {
    if (this.updateListeners == null || !this.imageMetadata.interlaceFlag)
      return; 
    int i = interlaceOffset[this.interlacePass];
    int j = interlaceIncrement[this.interlacePass];
    int[] arrayOfInt1 = ReaderUtil.computeUpdatedPixels(this.sourceRegion, this.destinationOffset, this.destinationRegion.x, this.destinationRegion.y, this.destinationRegion.x + this.destinationRegion.width - 1, this.destinationRegion.y + this.destinationRegion.height - 1, this.sourceXSubsampling, this.sourceYSubsampling, 0, i, this.destinationRegion.width, (this.destinationRegion.height + j - 1) / j, 1, j);
    this.updateMinY = arrayOfInt1[1];
    this.updateYStep = arrayOfInt1[5];
    int[] arrayOfInt2 = { 0 };
    processPassStarted(this.theImage, this.interlacePass, this.sourceMinProgressivePass, this.sourceMaxProgressivePass, 0, this.updateMinY, 1, this.updateYStep, arrayOfInt2);
  }
  
  public BufferedImage read(int paramInt, ImageReadParam paramImageReadParam) throws IIOException {
    if (this.stream == null)
      throw new IllegalStateException("Input not set!"); 
    checkIndex(paramInt);
    int i = locateImage(paramInt);
    if (i != paramInt)
      throw new IndexOutOfBoundsException("imageIndex out of bounds!"); 
    clearAbortRequest();
    readMetadata();
    if (paramImageReadParam == null)
      paramImageReadParam = getDefaultReadParam(); 
    Iterator iterator = getImageTypes(paramInt);
    this.theImage = getDestination(paramImageReadParam, iterator, this.imageMetadata.imageWidth, this.imageMetadata.imageHeight);
    this.theTile = this.theImage.getWritableTile(0, 0);
    this.width = this.imageMetadata.imageWidth;
    this.height = this.imageMetadata.imageHeight;
    this.streamX = 0;
    this.streamY = 0;
    this.rowsDone = 0;
    this.interlacePass = 0;
    this.sourceRegion = new Rectangle(0, 0, 0, 0);
    this.destinationRegion = new Rectangle(0, 0, 0, 0);
    computeRegions(paramImageReadParam, this.width, this.height, this.theImage, this.sourceRegion, this.destinationRegion);
    this.destinationOffset = new Point(this.destinationRegion.x, this.destinationRegion.y);
    this.sourceXSubsampling = paramImageReadParam.getSourceXSubsampling();
    this.sourceYSubsampling = paramImageReadParam.getSourceYSubsampling();
    this.sourceMinProgressivePass = Math.max(paramImageReadParam.getSourceMinProgressivePass(), 0);
    this.sourceMaxProgressivePass = Math.min(paramImageReadParam.getSourceMaxProgressivePass(), 3);
    this.destY = this.destinationRegion.y + (this.streamY - this.sourceRegion.y) / this.sourceYSubsampling;
    computeDecodeThisRow();
    processImageStarted(paramInt);
    startPass(0);
    this.rowBuf = new byte[this.width];
    try {
      this.initCodeSize = this.stream.readUnsignedByte();
      this.blockLength = this.stream.readUnsignedByte();
      int j = this.blockLength;
      int k;
      for (k = 0; j > 0; k += i3) {
        int i3 = this.stream.read(this.block, k, j);
        j -= i3;
      } 
      this.bitPos = 0;
      this.nextByte = 0;
      this.lastBlockFound = false;
      this.interlacePass = 0;
      initNext32Bits();
      this.clearCode = 1 << this.initCodeSize;
      this.eofCode = this.clearCode + 1;
      int m = 0;
      int[] arrayOfInt1 = new int[4096];
      byte[] arrayOfByte1 = new byte[4096];
      byte[] arrayOfByte2 = new byte[4096];
      int[] arrayOfInt2 = new int[4096];
      byte[] arrayOfByte3 = new byte[4096];
      initializeStringTable(arrayOfInt1, arrayOfByte1, arrayOfByte2, arrayOfInt2);
      int n = (1 << this.initCodeSize) + 2;
      int i1 = this.initCodeSize + 1;
      int i2 = (1 << i1) - 1;
      while (!abortRequested()) {
        int i3 = getCode(i1, i2);
        if (i3 == this.clearCode) {
          initializeStringTable(arrayOfInt1, arrayOfByte1, arrayOfByte2, arrayOfInt2);
          n = (1 << this.initCodeSize) + 2;
          i1 = this.initCodeSize + 1;
          i2 = (1 << i1) - 1;
          i3 = getCode(i1, i2);
          if (i3 == this.eofCode) {
            processImageComplete();
            return this.theImage;
          } 
        } else {
          boolean bool1;
          if (i3 == this.eofCode) {
            processImageComplete();
            return this.theImage;
          } 
          if (i3 < n) {
            bool1 = i3;
          } else {
            bool1 = m;
            if (i3 != n)
              processWarningOccurred("Out-of-sequence code!"); 
          } 
          int i7 = n;
          boolean bool2 = m;
          arrayOfInt1[i7] = bool2;
          arrayOfByte1[i7] = arrayOfByte2[bool1];
          arrayOfByte2[i7] = arrayOfByte2[bool2];
          arrayOfInt2[i7] = arrayOfInt2[bool2] + 1;
          if (++n == 1 << i1 && n < 4096)
            i2 = (1 << ++i1) - 1; 
        } 
        int i4 = i3;
        int i5 = arrayOfInt2[i4];
        for (int i6 = i5 - 1; i6 >= 0; i6--) {
          arrayOfByte3[i6] = arrayOfByte1[i4];
          i4 = arrayOfInt1[i4];
        } 
        outputPixels(arrayOfByte3, i5);
        m = i3;
      } 
      processReadAborted();
      return this.theImage;
    } catch (IOException iOException) {
      iOException.printStackTrace();
      throw new IIOException("I/O error reading image!", iOException);
    } 
  }
  
  public void reset() {
    super.reset();
    resetStreamSettings();
  }
  
  private void resetStreamSettings() {
    this.gotHeader = false;
    this.streamMetadata = null;
    this.currIndex = -1;
    this.imageMetadata = null;
    this.imageStartPosition = new ArrayList();
    this.numImages = -1;
    this.blockLength = 0;
    this.bitPos = 0;
    this.nextByte = 0;
    this.next32Bits = 0;
    this.lastBlockFound = false;
    this.theImage = null;
    this.theTile = null;
    this.width = -1;
    this.height = -1;
    this.streamX = -1;
    this.streamY = -1;
    this.rowsDone = 0;
    this.interlacePass = 0;
    this.fallbackColorTable = null;
  }
  
  private static byte[] getDefaultPalette() throws IOException {
    if (defaultPalette == null) {
      BufferedImage bufferedImage = new BufferedImage(1, 1, 13);
      IndexColorModel indexColorModel = (IndexColorModel)bufferedImage.getColorModel();
      int i = indexColorModel.getMapSize();
      byte[] arrayOfByte1 = new byte[i];
      byte[] arrayOfByte2 = new byte[i];
      byte[] arrayOfByte3 = new byte[i];
      indexColorModel.getReds(arrayOfByte1);
      indexColorModel.getGreens(arrayOfByte2);
      indexColorModel.getBlues(arrayOfByte3);
      defaultPalette = new byte[i * 3];
      for (byte b = 0; b < i; b++) {
        defaultPalette[3 * b + 0] = arrayOfByte1[b];
        defaultPalette[3 * b + 1] = arrayOfByte2[b];
        defaultPalette[3 * b + 2] = arrayOfByte3[b];
      } 
    } 
    return defaultPalette;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\gif\GIFImageReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */