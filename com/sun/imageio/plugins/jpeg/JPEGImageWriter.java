package com.sun.imageio.plugins.jpeg;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.Node;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

public class JPEGImageWriter extends ImageWriter {
  private boolean debug = false;
  
  private long structPointer = 0L;
  
  private ImageOutputStream ios = null;
  
  private Raster srcRas = null;
  
  private WritableRaster raster = null;
  
  private boolean indexed = false;
  
  private IndexColorModel indexCM = null;
  
  private boolean convertTosRGB = false;
  
  private WritableRaster converted = null;
  
  private boolean isAlphaPremultiplied = false;
  
  private ColorModel srcCM = null;
  
  private List thumbnails = null;
  
  private ICC_Profile iccProfile = null;
  
  private int sourceXOffset = 0;
  
  private int sourceYOffset = 0;
  
  private int sourceWidth = 0;
  
  private int[] srcBands = null;
  
  private int sourceHeight = 0;
  
  private int currentImage = 0;
  
  private ColorConvertOp convertOp = null;
  
  private JPEGQTable[] streamQTables = null;
  
  private JPEGHuffmanTable[] streamDCHuffmanTables = null;
  
  private JPEGHuffmanTable[] streamACHuffmanTables = null;
  
  private boolean ignoreJFIF = false;
  
  private boolean forceJFIF = false;
  
  private boolean ignoreAdobe = false;
  
  private int newAdobeTransform = -1;
  
  private boolean writeDefaultJFIF = false;
  
  private boolean writeAdobe = false;
  
  private JPEGMetadata metadata = null;
  
  private boolean sequencePrepared = false;
  
  private int numScans = 0;
  
  private Object disposerReferent = new Object();
  
  private DisposerRecord disposerRecord;
  
  protected static final int WARNING_DEST_IGNORED = 0;
  
  protected static final int WARNING_STREAM_METADATA_IGNORED = 1;
  
  protected static final int WARNING_DEST_METADATA_COMP_MISMATCH = 2;
  
  protected static final int WARNING_DEST_METADATA_JFIF_MISMATCH = 3;
  
  protected static final int WARNING_DEST_METADATA_ADOBE_MISMATCH = 4;
  
  protected static final int WARNING_IMAGE_METADATA_JFIF_MISMATCH = 5;
  
  protected static final int WARNING_IMAGE_METADATA_ADOBE_MISMATCH = 6;
  
  protected static final int WARNING_METADATA_NOT_JPEG_FOR_RASTER = 7;
  
  protected static final int WARNING_NO_BANDS_ON_INDEXED = 8;
  
  protected static final int WARNING_ILLEGAL_THUMBNAIL = 9;
  
  protected static final int WARNING_IGNORING_THUMBS = 10;
  
  protected static final int WARNING_FORCING_JFIF = 11;
  
  protected static final int WARNING_THUMB_CLIPPED = 12;
  
  protected static final int WARNING_METADATA_ADJUSTED_FOR_THUMB = 13;
  
  protected static final int WARNING_NO_RGB_THUMB_AS_INDEXED = 14;
  
  protected static final int WARNING_NO_GRAY_THUMB_AS_INDEXED = 15;
  
  private static final int MAX_WARNING = 15;
  
  static final Dimension[] preferredThumbSizes;
  
  private Thread theThread = null;
  
  private int theLockCount = 0;
  
  private CallBackLock cbLock = new CallBackLock();
  
  public JPEGImageWriter(ImageWriterSpi paramImageWriterSpi) {
    super(paramImageWriterSpi);
    this.structPointer = initJPEGImageWriter();
    this.disposerRecord = new JPEGWriterDisposerRecord(this.structPointer);
    Disposer.addRecord(this.disposerReferent, this.disposerRecord);
  }
  
  public void setOutput(Object paramObject) {
    setThreadLock();
    try {
      this.cbLock.check();
      super.setOutput(paramObject);
      resetInternalState();
      this.ios = (ImageOutputStream)paramObject;
      setDest(this.structPointer);
    } finally {
      clearThreadLock();
    } 
  }
  
  public ImageWriteParam getDefaultWriteParam() { return new JPEGImageWriteParam(null); }
  
  public IIOMetadata getDefaultStreamMetadata(ImageWriteParam paramImageWriteParam) {
    setThreadLock();
    try {
      return new JPEGMetadata(paramImageWriteParam, this);
    } finally {
      clearThreadLock();
    } 
  }
  
  public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam) {
    setThreadLock();
    try {
      return new JPEGMetadata(paramImageTypeSpecifier, paramImageWriteParam, this);
    } finally {
      clearThreadLock();
    } 
  }
  
  public IIOMetadata convertStreamMetadata(IIOMetadata paramIIOMetadata, ImageWriteParam paramImageWriteParam) {
    if (paramIIOMetadata instanceof JPEGMetadata) {
      JPEGMetadata jPEGMetadata = (JPEGMetadata)paramIIOMetadata;
      if (jPEGMetadata.isStream)
        return paramIIOMetadata; 
    } 
    return null;
  }
  
  public IIOMetadata convertImageMetadata(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam) {
    setThreadLock();
    try {
      return convertImageMetadataOnThread(paramIIOMetadata, paramImageTypeSpecifier, paramImageWriteParam);
    } finally {
      clearThreadLock();
    } 
  }
  
  private IIOMetadata convertImageMetadataOnThread(IIOMetadata paramIIOMetadata, ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam) {
    if (paramIIOMetadata instanceof JPEGMetadata) {
      JPEGMetadata jPEGMetadata = (JPEGMetadata)paramIIOMetadata;
      return !jPEGMetadata.isStream ? paramIIOMetadata : null;
    } 
    if (paramIIOMetadata.isStandardMetadataFormatSupported()) {
      String str = "javax_imageio_1.0";
      Node node = paramIIOMetadata.getAsTree(str);
      if (node != null) {
        JPEGMetadata jPEGMetadata = new JPEGMetadata(paramImageTypeSpecifier, paramImageWriteParam, this);
        try {
          jPEGMetadata.setFromTree(str, node);
        } catch (IIOInvalidTreeException iIOInvalidTreeException) {
          return null;
        } 
        return jPEGMetadata;
      } 
    } 
    return null;
  }
  
  public int getNumThumbnailsSupported(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam, IIOMetadata paramIIOMetadata1, IIOMetadata paramIIOMetadata2) { return jfifOK(paramImageTypeSpecifier, paramImageWriteParam, paramIIOMetadata1, paramIIOMetadata2) ? Integer.MAX_VALUE : 0; }
  
  public Dimension[] getPreferredThumbnailSizes(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam, IIOMetadata paramIIOMetadata1, IIOMetadata paramIIOMetadata2) { return jfifOK(paramImageTypeSpecifier, paramImageWriteParam, paramIIOMetadata1, paramIIOMetadata2) ? (Dimension[])preferredThumbSizes.clone() : null; }
  
  private boolean jfifOK(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam, IIOMetadata paramIIOMetadata1, IIOMetadata paramIIOMetadata2) {
    if (paramImageTypeSpecifier != null && !JPEG.isJFIFcompliant(paramImageTypeSpecifier, true))
      return false; 
    if (paramIIOMetadata2 != null) {
      JPEGMetadata jPEGMetadata = null;
      if (paramIIOMetadata2 instanceof JPEGMetadata) {
        jPEGMetadata = (JPEGMetadata)paramIIOMetadata2;
      } else {
        jPEGMetadata = (JPEGMetadata)convertImageMetadata(paramIIOMetadata2, paramImageTypeSpecifier, paramImageWriteParam);
      } 
      if (jPEGMetadata.findMarkerSegment(JFIFMarkerSegment.class, true) == null)
        return false; 
    } 
    return true;
  }
  
  public boolean canWriteRasters() { return true; }
  
  public void write(IIOMetadata paramIIOMetadata, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam) throws IOException {
    setThreadLock();
    try {
      this.cbLock.check();
      writeOnThread(paramIIOMetadata, paramIIOImage, paramImageWriteParam);
    } finally {
      clearThreadLock();
    } 
  }
  
  private void writeOnThread(IIOMetadata paramIIOMetadata, IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam) throws IOException {
    if (this.ios == null)
      throw new IllegalStateException("Output has not been set!"); 
    if (paramIIOImage == null)
      throw new IllegalArgumentException("image is null!"); 
    if (paramIIOMetadata != null)
      warningOccurred(1); 
    boolean bool1 = paramIIOImage.hasRaster();
    RenderedImage renderedImage = null;
    if (bool1) {
      this.srcRas = paramIIOImage.getRaster();
    } else {
      renderedImage = paramIIOImage.getRenderedImage();
      if (renderedImage instanceof BufferedImage) {
        this.srcRas = ((BufferedImage)renderedImage).getRaster();
      } else if (renderedImage.getNumXTiles() == 1 && renderedImage.getNumYTiles() == 1) {
        this.srcRas = renderedImage.getTile(renderedImage.getMinTileX(), renderedImage.getMinTileY());
        if (this.srcRas.getWidth() != renderedImage.getWidth() || this.srcRas.getHeight() != renderedImage.getHeight())
          this.srcRas = this.srcRas.createChild(this.srcRas.getMinX(), this.srcRas.getMinY(), renderedImage.getWidth(), renderedImage.getHeight(), this.srcRas.getMinX(), this.srcRas.getMinY(), null); 
      } else {
        this.srcRas = renderedImage.getData();
      } 
    } 
    int i = this.srcRas.getNumBands();
    this.indexed = false;
    this.indexCM = null;
    ColorModel colorModel = null;
    ColorSpace colorSpace = null;
    this.isAlphaPremultiplied = false;
    this.srcCM = null;
    if (!bool1) {
      colorModel = renderedImage.getColorModel();
      if (colorModel != null) {
        colorSpace = colorModel.getColorSpace();
        if (colorModel instanceof IndexColorModel) {
          this.indexed = true;
          this.indexCM = (IndexColorModel)colorModel;
          i = colorModel.getNumComponents();
        } 
        if (colorModel.isAlphaPremultiplied()) {
          this.isAlphaPremultiplied = true;
          this.srcCM = colorModel;
        } 
      } 
    } 
    this.srcBands = JPEG.bandOffsets[i - 1];
    int j = i;
    if (paramImageWriteParam != null) {
      int[] arrayOfInt = paramImageWriteParam.getSourceBands();
      if (arrayOfInt != null)
        if (this.indexed) {
          warningOccurred(8);
        } else {
          this.srcBands = arrayOfInt;
          j = this.srcBands.length;
          if (j > i)
            throw new IIOException("ImageWriteParam specifies too many source bands"); 
        }  
    } 
    boolean bool2 = (j != i);
    boolean bool3 = (!bool1 && !bool2);
    int[] arrayOfInt1 = null;
    if (!this.indexed) {
      arrayOfInt1 = this.srcRas.getSampleModel().getSampleSize();
      if (bool2) {
        int[] arrayOfInt = new int[j];
        for (byte b1 = 0; b1 < j; b1++)
          arrayOfInt[b1] = arrayOfInt1[this.srcBands[b1]]; 
        arrayOfInt1 = arrayOfInt;
      } 
    } else {
      int[] arrayOfInt = this.srcRas.getSampleModel().getSampleSize();
      arrayOfInt1 = new int[i];
      for (byte b1 = 0; b1 < i; b1++)
        arrayOfInt1[b1] = arrayOfInt[0]; 
    } 
    byte b;
    for (b = 0; b < arrayOfInt1.length; b++) {
      if (arrayOfInt1[b] <= 0 || arrayOfInt1[b] > 8)
        throw new IIOException("Illegal band size: should be 0 < size <= 8"); 
      if (this.indexed)
        arrayOfInt1[b] = 8; 
    } 
    if (this.debug) {
      System.out.println("numSrcBands is " + i);
      System.out.println("numBandsUsed is " + j);
      System.out.println("usingBandSubset is " + bool2);
      System.out.println("fullImage is " + bool3);
      System.out.print("Band sizes:");
      for (b = 0; b < arrayOfInt1.length; b++)
        System.out.print(" " + arrayOfInt1[b]); 
      System.out.println();
    } 
    ImageTypeSpecifier imageTypeSpecifier = null;
    if (paramImageWriteParam != null) {
      imageTypeSpecifier = paramImageWriteParam.getDestinationType();
      if (bool3 && imageTypeSpecifier != null) {
        warningOccurred(0);
        imageTypeSpecifier = null;
      } 
    } 
    this.sourceXOffset = this.srcRas.getMinX();
    this.sourceYOffset = this.srcRas.getMinY();
    int k = this.srcRas.getWidth();
    int m = this.srcRas.getHeight();
    this.sourceWidth = k;
    this.sourceHeight = m;
    int n = 1;
    int i1 = 1;
    int i2 = 0;
    int i3 = 0;
    JPEGQTable[] arrayOfJPEGQTable = null;
    JPEGHuffmanTable[] arrayOfJPEGHuffmanTable1 = null;
    JPEGHuffmanTable[] arrayOfJPEGHuffmanTable2 = null;
    boolean bool4 = false;
    JPEGImageWriteParam jPEGImageWriteParam = null;
    int i4 = 0;
    if (paramImageWriteParam != null) {
      float f;
      Rectangle rectangle = paramImageWriteParam.getSourceRegion();
      if (rectangle != null) {
        Rectangle rectangle1 = new Rectangle(this.sourceXOffset, this.sourceYOffset, this.sourceWidth, this.sourceHeight);
        rectangle = rectangle.intersection(rectangle1);
        this.sourceXOffset = rectangle.x;
        this.sourceYOffset = rectangle.y;
        this.sourceWidth = rectangle.width;
        this.sourceHeight = rectangle.height;
      } 
      if (this.sourceWidth + this.sourceXOffset > k)
        this.sourceWidth = k - this.sourceXOffset; 
      if (this.sourceHeight + this.sourceYOffset > m)
        this.sourceHeight = m - this.sourceYOffset; 
      n = paramImageWriteParam.getSourceXSubsampling();
      i1 = paramImageWriteParam.getSourceYSubsampling();
      i2 = paramImageWriteParam.getSubsamplingXOffset();
      i3 = paramImageWriteParam.getSubsamplingYOffset();
      switch (paramImageWriteParam.getCompressionMode()) {
        case 0:
          throw new IIOException("JPEG compression cannot be disabled");
        case 2:
          f = paramImageWriteParam.getCompressionQuality();
          f = JPEG.convertToLinearQuality(f);
          arrayOfJPEGQTable = new JPEGQTable[2];
          arrayOfJPEGQTable[0] = JPEGQTable.K1Luminance.getScaledInstance(f, true);
          arrayOfJPEGQTable[1] = JPEGQTable.K2Chrominance.getScaledInstance(f, true);
          break;
        case 1:
          arrayOfJPEGQTable = new JPEGQTable[2];
          arrayOfJPEGQTable[0] = JPEGQTable.K1Div2Luminance;
          arrayOfJPEGQTable[1] = JPEGQTable.K2Div2Chrominance;
          break;
      } 
      i4 = paramImageWriteParam.getProgressiveMode();
      if (paramImageWriteParam instanceof JPEGImageWriteParam) {
        jPEGImageWriteParam = (JPEGImageWriteParam)paramImageWriteParam;
        bool4 = jPEGImageWriteParam.getOptimizeHuffmanTables();
      } 
    } 
    IIOMetadata iIOMetadata = paramIIOImage.getMetadata();
    if (iIOMetadata != null)
      if (iIOMetadata instanceof JPEGMetadata) {
        this.metadata = (JPEGMetadata)iIOMetadata;
        if (this.debug)
          System.out.println("We have metadata, and it's JPEG metadata"); 
      } else if (!bool1) {
        ImageTypeSpecifier imageTypeSpecifier1 = imageTypeSpecifier;
        if (imageTypeSpecifier1 == null)
          imageTypeSpecifier1 = new ImageTypeSpecifier(renderedImage); 
        this.metadata = (JPEGMetadata)convertImageMetadata(iIOMetadata, imageTypeSpecifier1, paramImageWriteParam);
      } else {
        warningOccurred(7);
      }  
    this.ignoreJFIF = false;
    this.ignoreAdobe = false;
    this.newAdobeTransform = -1;
    this.writeDefaultJFIF = false;
    this.writeAdobe = false;
    int i5 = 0;
    int i6 = 0;
    JFIFMarkerSegment jFIFMarkerSegment = null;
    AdobeMarkerSegment adobeMarkerSegment = null;
    SOFMarkerSegment sOFMarkerSegment = null;
    if (this.metadata != null) {
      jFIFMarkerSegment = (JFIFMarkerSegment)this.metadata.findMarkerSegment(JFIFMarkerSegment.class, true);
      adobeMarkerSegment = (AdobeMarkerSegment)this.metadata.findMarkerSegment(AdobeMarkerSegment.class, true);
      sOFMarkerSegment = (SOFMarkerSegment)this.metadata.findMarkerSegment(SOFMarkerSegment.class, true);
    } 
    this.iccProfile = null;
    this.convertTosRGB = false;
    this.converted = null;
    if (imageTypeSpecifier != null) {
      if (j != imageTypeSpecifier.getNumBands())
        throw new IIOException("Number of source bands != number of destination bands"); 
      colorSpace = imageTypeSpecifier.getColorModel().getColorSpace();
      if (this.metadata != null) {
        checkSOFBands(sOFMarkerSegment, j);
        checkJFIF(jFIFMarkerSegment, imageTypeSpecifier, false);
        if (jFIFMarkerSegment != null && !this.ignoreJFIF && JPEG.isNonStandardICC(colorSpace))
          this.iccProfile = ((ICC_ColorSpace)colorSpace).getProfile(); 
        checkAdobe(adobeMarkerSegment, imageTypeSpecifier, false);
      } else {
        if (JPEG.isJFIFcompliant(imageTypeSpecifier, false)) {
          this.writeDefaultJFIF = true;
          if (JPEG.isNonStandardICC(colorSpace))
            this.iccProfile = ((ICC_ColorSpace)colorSpace).getProfile(); 
        } else {
          int i12 = JPEG.transformForType(imageTypeSpecifier, false);
          if (i12 != -1) {
            this.writeAdobe = true;
            this.newAdobeTransform = i12;
          } 
        } 
        this.metadata = new JPEGMetadata(imageTypeSpecifier, null, this);
      } 
      i5 = getSrcCSType(imageTypeSpecifier);
      i6 = getDefaultDestCSType(imageTypeSpecifier);
    } else if (this.metadata == null) {
      if (bool3) {
        this.metadata = new JPEGMetadata(new ImageTypeSpecifier(renderedImage), paramImageWriteParam, this);
        if (this.metadata.findMarkerSegment(JFIFMarkerSegment.class, true) != null) {
          colorSpace = renderedImage.getColorModel().getColorSpace();
          if (JPEG.isNonStandardICC(colorSpace))
            this.iccProfile = ((ICC_ColorSpace)colorSpace).getProfile(); 
        } 
        i5 = getSrcCSType(renderedImage);
        i6 = getDefaultDestCSType(renderedImage);
      } 
    } else {
      checkSOFBands(sOFMarkerSegment, j);
      if (bool3) {
        ImageTypeSpecifier imageTypeSpecifier1 = new ImageTypeSpecifier(renderedImage);
        i5 = getSrcCSType(renderedImage);
        if (colorModel != null) {
          boolean bool10;
          int i12;
          boolean bool9 = colorModel.hasAlpha();
          switch (colorSpace.getType()) {
            case 6:
              if (!bool9) {
                i6 = 1;
              } else if (jFIFMarkerSegment != null) {
                this.ignoreJFIF = true;
                warningOccurred(5);
              } 
              if (adobeMarkerSegment != null && adobeMarkerSegment.transform != 0) {
                this.newAdobeTransform = 0;
                warningOccurred(6);
              } 
              break;
            case 5:
              if (!bool9) {
                if (jFIFMarkerSegment != null) {
                  i6 = 3;
                  if (JPEG.isNonStandardICC(colorSpace) || (colorSpace instanceof ICC_ColorSpace && jFIFMarkerSegment.iccSegment != null))
                    this.iccProfile = ((ICC_ColorSpace)colorSpace).getProfile(); 
                  break;
                } 
                if (adobeMarkerSegment != null) {
                  switch (adobeMarkerSegment.transform) {
                    case 0:
                      i6 = 2;
                      break;
                    case 1:
                      i6 = 3;
                      break;
                  } 
                  warningOccurred(6);
                  this.newAdobeTransform = 0;
                  i6 = 2;
                  break;
                } 
                int i13 = sOFMarkerSegment.getIDencodedCSType();
                if (i13 != 0) {
                  i6 = i13;
                  break;
                } 
                boolean bool11 = isSubsampled(sOFMarkerSegment.componentSpecs);
                if (bool11) {
                  i6 = 3;
                  break;
                } 
                i6 = 2;
                break;
              } 
              if (jFIFMarkerSegment != null) {
                this.ignoreJFIF = true;
                warningOccurred(5);
              } 
              if (adobeMarkerSegment != null) {
                if (adobeMarkerSegment.transform != 0) {
                  this.newAdobeTransform = 0;
                  warningOccurred(6);
                } 
                i6 = 6;
                break;
              } 
              i12 = sOFMarkerSegment.getIDencodedCSType();
              if (i12 != 0) {
                i6 = i12;
                break;
              } 
              bool10 = isSubsampled(sOFMarkerSegment.componentSpecs);
              i6 = bool10 ? 7 : 6;
              break;
            case 13:
              if (colorSpace == JPEG.JCS.getYCC()) {
                if (!bool9) {
                  if (jFIFMarkerSegment != null) {
                    this.convertTosRGB = true;
                    this.convertOp = new ColorConvertOp(colorSpace, JPEG.JCS.sRGB, null);
                    i6 = 3;
                    break;
                  } 
                  if (adobeMarkerSegment != null) {
                    if (adobeMarkerSegment.transform != 1) {
                      this.newAdobeTransform = 1;
                      warningOccurred(6);
                    } 
                    i6 = 5;
                    break;
                  } 
                  i6 = 5;
                  break;
                } 
                if (jFIFMarkerSegment != null) {
                  this.ignoreJFIF = true;
                  warningOccurred(5);
                } else if (adobeMarkerSegment != null && adobeMarkerSegment.transform != 0) {
                  this.newAdobeTransform = 0;
                  warningOccurred(6);
                } 
                i6 = 10;
              } 
              break;
          } 
        } 
      } 
    } 
    boolean bool = false;
    int[] arrayOfInt2 = null;
    if (this.metadata != null) {
      if (sOFMarkerSegment == null)
        sOFMarkerSegment = (SOFMarkerSegment)this.metadata.findMarkerSegment(SOFMarkerSegment.class, true); 
      if (sOFMarkerSegment != null && sOFMarkerSegment.tag == 194) {
        bool = true;
        if (i4 == 3) {
          arrayOfInt2 = collectScans(this.metadata, sOFMarkerSegment);
        } else {
          this.numScans = 0;
        } 
      } 
      if (jFIFMarkerSegment == null)
        jFIFMarkerSegment = (JFIFMarkerSegment)this.metadata.findMarkerSegment(JFIFMarkerSegment.class, true); 
    } 
    this.thumbnails = paramIIOImage.getThumbnails();
    int i7 = paramIIOImage.getNumThumbnails();
    this.forceJFIF = false;
    if (!this.writeDefaultJFIF)
      if (this.metadata == null) {
        this.thumbnails = null;
        if (i7 != 0)
          warningOccurred(10); 
      } else if (!bool3) {
        if (jFIFMarkerSegment == null) {
          this.thumbnails = null;
          if (i7 != 0)
            warningOccurred(10); 
        } 
      } else if (jFIFMarkerSegment == null) {
        if (i6 == 1 || i6 == 3) {
          if (i7 != 0) {
            this.forceJFIF = true;
            warningOccurred(11);
          } 
        } else {
          this.thumbnails = null;
          if (i7 != 0)
            warningOccurred(10); 
        } 
      }  
    boolean bool5 = (this.metadata != null || this.writeDefaultJFIF || this.writeAdobe);
    boolean bool6 = true;
    boolean bool7 = true;
    DQTMarkerSegment dQTMarkerSegment = null;
    DHTMarkerSegment dHTMarkerSegment = null;
    int i8 = 0;
    if (this.metadata != null) {
      dQTMarkerSegment = (DQTMarkerSegment)this.metadata.findMarkerSegment(DQTMarkerSegment.class, true);
      dHTMarkerSegment = (DHTMarkerSegment)this.metadata.findMarkerSegment(DHTMarkerSegment.class, true);
      DRIMarkerSegment dRIMarkerSegment = (DRIMarkerSegment)this.metadata.findMarkerSegment(DRIMarkerSegment.class, true);
      if (dRIMarkerSegment != null)
        i8 = dRIMarkerSegment.restartInterval; 
      if (dQTMarkerSegment == null)
        bool6 = false; 
      if (dHTMarkerSegment == null)
        bool7 = false; 
    } 
    if (arrayOfJPEGQTable == null)
      if (dQTMarkerSegment != null) {
        arrayOfJPEGQTable = collectQTablesFromMetadata(this.metadata);
      } else if (this.streamQTables != null) {
        arrayOfJPEGQTable = this.streamQTables;
      } else if (jPEGImageWriteParam != null && jPEGImageWriteParam.areTablesSet()) {
        arrayOfJPEGQTable = jPEGImageWriteParam.getQTables();
      } else {
        arrayOfJPEGQTable = JPEG.getDefaultQTables();
      }  
    if (!bool4)
      if (dHTMarkerSegment != null && !bool) {
        arrayOfJPEGHuffmanTable1 = collectHTablesFromMetadata(this.metadata, true);
        arrayOfJPEGHuffmanTable2 = collectHTablesFromMetadata(this.metadata, false);
      } else if (this.streamDCHuffmanTables != null) {
        arrayOfJPEGHuffmanTable1 = this.streamDCHuffmanTables;
        arrayOfJPEGHuffmanTable2 = this.streamACHuffmanTables;
      } else if (jPEGImageWriteParam != null && jPEGImageWriteParam.areTablesSet()) {
        arrayOfJPEGHuffmanTable1 = jPEGImageWriteParam.getDCHuffmanTables();
        arrayOfJPEGHuffmanTable2 = jPEGImageWriteParam.getACHuffmanTables();
      } else {
        arrayOfJPEGHuffmanTable1 = JPEG.getDefaultHuffmanTables(true);
        arrayOfJPEGHuffmanTable2 = JPEG.getDefaultHuffmanTables(false);
      }  
    int[] arrayOfInt3 = new int[j];
    int[] arrayOfInt4 = new int[j];
    int[] arrayOfInt5 = new int[j];
    int[] arrayOfInt6 = new int[j];
    int i9;
    for (i9 = 0; i9 < j; i9++) {
      arrayOfInt3[i9] = i9 + true;
      arrayOfInt4[i9] = 1;
      arrayOfInt5[i9] = 1;
      arrayOfInt6[i9] = 0;
    } 
    if (sOFMarkerSegment != null)
      for (i9 = 0; i9 < j; i9++) {
        if (!this.forceJFIF)
          arrayOfInt3[i9] = (sOFMarkerSegment.componentSpecs[i9]).componentId; 
        arrayOfInt4[i9] = (sOFMarkerSegment.componentSpecs[i9]).HsamplingFactor;
        arrayOfInt5[i9] = (sOFMarkerSegment.componentSpecs[i9]).VsamplingFactor;
        arrayOfInt6[i9] = (sOFMarkerSegment.componentSpecs[i9]).QtableSelector;
      }  
    this.sourceXOffset += i2;
    this.sourceWidth -= i2;
    this.sourceYOffset += i3;
    this.sourceHeight -= i3;
    i9 = (this.sourceWidth + n - 1) / n;
    int i10 = (this.sourceHeight + i1 - 1) / i1;
    int i11 = this.sourceWidth * j;
    DataBufferByte dataBufferByte = new DataBufferByte(i11);
    int[] arrayOfInt7 = JPEG.bandOffsets[j - 1];
    this.raster = Raster.createInterleavedRaster(dataBufferByte, this.sourceWidth, 1, i11, j, arrayOfInt7, null);
    clearAbortRequest();
    this.cbLock.lock();
    try {
      processImageStarted(this.currentImage);
    } finally {
      this.cbLock.unlock();
    } 
    boolean bool8 = false;
    if (this.debug) {
      System.out.println("inCsType: " + i5);
      System.out.println("outCsType: " + i6);
    } 
    bool8 = writeImage(this.structPointer, dataBufferByte.getData(), i5, i6, j, arrayOfInt1, this.sourceWidth, i9, i10, n, i1, arrayOfJPEGQTable, bool6, arrayOfJPEGHuffmanTable1, arrayOfJPEGHuffmanTable2, bool7, bool4, (i4 != 0), this.numScans, arrayOfInt2, arrayOfInt3, arrayOfInt4, arrayOfInt5, arrayOfInt6, bool5, i8);
    this.cbLock.lock();
    try {
      if (bool8) {
        processWriteAborted();
      } else {
        processImageComplete();
      } 
      this.ios.flush();
    } finally {
      this.cbLock.unlock();
    } 
    this.currentImage++;
  }
  
  public boolean canWriteSequence() { return true; }
  
  public void prepareWriteSequence(IIOMetadata paramIIOMetadata) throws IOException {
    setThreadLock();
    try {
      this.cbLock.check();
      prepareWriteSequenceOnThread(paramIIOMetadata);
    } finally {
      clearThreadLock();
    } 
  }
  
  private void prepareWriteSequenceOnThread(IIOMetadata paramIIOMetadata) throws IOException {
    if (this.ios == null)
      throw new IllegalStateException("Output has not been set!"); 
    if (paramIIOMetadata != null)
      if (paramIIOMetadata instanceof JPEGMetadata) {
        JPEGMetadata jPEGMetadata = (JPEGMetadata)paramIIOMetadata;
        if (!jPEGMetadata.isStream)
          throw new IllegalArgumentException("Invalid stream metadata object."); 
        if (this.currentImage != 0)
          throw new IIOException("JPEG Stream metadata must precede all images"); 
        if (this.sequencePrepared == true)
          throw new IIOException("Stream metadata already written!"); 
        this.streamQTables = collectQTablesFromMetadata(jPEGMetadata);
        if (this.debug)
          System.out.println("after collecting from stream metadata, streamQTables.length is " + this.streamQTables.length); 
        if (this.streamQTables == null)
          this.streamQTables = JPEG.getDefaultQTables(); 
        this.streamDCHuffmanTables = collectHTablesFromMetadata(jPEGMetadata, true);
        if (this.streamDCHuffmanTables == null)
          this.streamDCHuffmanTables = JPEG.getDefaultHuffmanTables(true); 
        this.streamACHuffmanTables = collectHTablesFromMetadata(jPEGMetadata, false);
        if (this.streamACHuffmanTables == null)
          this.streamACHuffmanTables = JPEG.getDefaultHuffmanTables(false); 
        writeTables(this.structPointer, this.streamQTables, this.streamDCHuffmanTables, this.streamACHuffmanTables);
      } else {
        throw new IIOException("Stream metadata must be JPEG metadata");
      }  
    this.sequencePrepared = true;
  }
  
  public void writeToSequence(IIOImage paramIIOImage, ImageWriteParam paramImageWriteParam) throws IOException {
    setThreadLock();
    try {
      this.cbLock.check();
      if (!this.sequencePrepared)
        throw new IllegalStateException("sequencePrepared not called!"); 
      write(null, paramIIOImage, paramImageWriteParam);
    } finally {
      clearThreadLock();
    } 
  }
  
  public void endWriteSequence() throws IOException {
    setThreadLock();
    try {
      this.cbLock.check();
      if (!this.sequencePrepared)
        throw new IllegalStateException("sequencePrepared not called!"); 
      this.sequencePrepared = false;
    } finally {
      clearThreadLock();
    } 
  }
  
  public void abort() throws IOException {
    setThreadLock();
    try {
      super.abort();
      abortWrite(this.structPointer);
    } finally {
      clearThreadLock();
    } 
  }
  
  protected void clearAbortRequest() throws IOException {
    setThreadLock();
    try {
      this.cbLock.check();
      if (abortRequested()) {
        super.clearAbortRequest();
        resetWriter(this.structPointer);
        setDest(this.structPointer);
      } 
    } finally {
      clearThreadLock();
    } 
  }
  
  private void resetInternalState() throws IOException {
    resetWriter(this.structPointer);
    this.srcRas = null;
    this.raster = null;
    this.convertTosRGB = false;
    this.currentImage = 0;
    this.numScans = 0;
    this.metadata = null;
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
  
  void warningOccurred(int paramInt) {
    this.cbLock.lock();
    try {
      if (paramInt < 0 || paramInt > 15)
        throw new InternalError("Invalid warning index"); 
      processWarningOccurred(this.currentImage, "com.sun.imageio.plugins.jpeg.JPEGImageWriterResources", Integer.toString(paramInt));
    } finally {
      this.cbLock.unlock();
    } 
  }
  
  void warningWithMessage(String paramString) {
    this.cbLock.lock();
    try {
      processWarningOccurred(this.currentImage, paramString);
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
  
  private void checkSOFBands(SOFMarkerSegment paramSOFMarkerSegment, int paramInt) throws IIOException {
    if (paramSOFMarkerSegment != null && paramSOFMarkerSegment.componentSpecs.length != paramInt)
      throw new IIOException("Metadata components != number of destination bands"); 
  }
  
  private void checkJFIF(JFIFMarkerSegment paramJFIFMarkerSegment, ImageTypeSpecifier paramImageTypeSpecifier, boolean paramBoolean) {
    if (paramJFIFMarkerSegment != null && !JPEG.isJFIFcompliant(paramImageTypeSpecifier, paramBoolean)) {
      this.ignoreJFIF = true;
      warningOccurred(paramBoolean ? 5 : 3);
    } 
  }
  
  private void checkAdobe(AdobeMarkerSegment paramAdobeMarkerSegment, ImageTypeSpecifier paramImageTypeSpecifier, boolean paramBoolean) {
    if (paramAdobeMarkerSegment != null) {
      int i = JPEG.transformForType(paramImageTypeSpecifier, paramBoolean);
      if (paramAdobeMarkerSegment.transform != i) {
        warningOccurred(paramBoolean ? 6 : 4);
        if (i == -1) {
          this.ignoreAdobe = true;
        } else {
          this.newAdobeTransform = i;
        } 
      } 
    } 
  }
  
  private int[] collectScans(JPEGMetadata paramJPEGMetadata, SOFMarkerSegment paramSOFMarkerSegment) {
    ArrayList arrayList = new ArrayList();
    int i = 9;
    byte b = 4;
    for (MarkerSegment markerSegment : paramJPEGMetadata.markerSequence) {
      if (markerSegment instanceof SOSMarkerSegment)
        arrayList.add(markerSegment); 
    } 
    int[] arrayOfInt = null;
    this.numScans = 0;
    if (!arrayList.isEmpty()) {
      this.numScans = arrayList.size();
      arrayOfInt = new int[this.numScans * i];
      byte b1 = 0;
      for (byte b2 = 0; b2 < this.numScans; b2++) {
        SOSMarkerSegment sOSMarkerSegment = (SOSMarkerSegment)arrayList.get(b2);
        arrayOfInt[b1++] = sOSMarkerSegment.componentSpecs.length;
        for (byte b3 = 0; b3 < b; b3++) {
          if (b3 < sOSMarkerSegment.componentSpecs.length) {
            int j = (sOSMarkerSegment.componentSpecs[b3]).componentSelector;
            for (byte b4 = 0; b4 < paramSOFMarkerSegment.componentSpecs.length; b4++) {
              if (j == (paramSOFMarkerSegment.componentSpecs[b4]).componentId) {
                arrayOfInt[b1++] = b4;
                break;
              } 
            } 
          } else {
            arrayOfInt[b1++] = 0;
          } 
        } 
        arrayOfInt[b1++] = sOSMarkerSegment.startSpectralSelection;
        arrayOfInt[b1++] = sOSMarkerSegment.endSpectralSelection;
        arrayOfInt[b1++] = sOSMarkerSegment.approxHigh;
        arrayOfInt[b1++] = sOSMarkerSegment.approxLow;
      } 
    } 
    return arrayOfInt;
  }
  
  private JPEGQTable[] collectQTablesFromMetadata(JPEGMetadata paramJPEGMetadata) {
    ArrayList arrayList = new ArrayList();
    for (MarkerSegment markerSegment : paramJPEGMetadata.markerSequence) {
      if (markerSegment instanceof DQTMarkerSegment) {
        DQTMarkerSegment dQTMarkerSegment = (DQTMarkerSegment)markerSegment;
        arrayList.addAll(dQTMarkerSegment.tables);
      } 
    } 
    JPEGQTable[] arrayOfJPEGQTable = null;
    if (arrayList.size() != 0) {
      arrayOfJPEGQTable = new JPEGQTable[arrayList.size()];
      for (byte b = 0; b < arrayOfJPEGQTable.length; b++)
        arrayOfJPEGQTable[b] = new JPEGQTable(((DQTMarkerSegment.Qtable)arrayList.get(b)).data); 
    } 
    return arrayOfJPEGQTable;
  }
  
  private JPEGHuffmanTable[] collectHTablesFromMetadata(JPEGMetadata paramJPEGMetadata, boolean paramBoolean) throws IIOException {
    ArrayList arrayList = new ArrayList();
    for (MarkerSegment markerSegment : paramJPEGMetadata.markerSequence) {
      if (markerSegment instanceof DHTMarkerSegment) {
        DHTMarkerSegment dHTMarkerSegment = (DHTMarkerSegment)markerSegment;
        for (byte b = 0; b < dHTMarkerSegment.tables.size(); b++) {
          DHTMarkerSegment.Htable htable = (DHTMarkerSegment.Htable)dHTMarkerSegment.tables.get(b);
          if (htable.tableClass == (paramBoolean ? 0 : 1))
            arrayList.add(htable); 
        } 
      } 
    } 
    JPEGHuffmanTable[] arrayOfJPEGHuffmanTable = null;
    if (arrayList.size() != 0) {
      DHTMarkerSegment.Htable[] arrayOfHtable = new DHTMarkerSegment.Htable[arrayList.size()];
      arrayList.toArray(arrayOfHtable);
      arrayOfJPEGHuffmanTable = new JPEGHuffmanTable[arrayList.size()];
      for (byte b = 0; b < arrayOfJPEGHuffmanTable.length; b++) {
        arrayOfJPEGHuffmanTable[b] = null;
        for (byte b1 = 0; b1 < arrayList.size(); b1++) {
          if ((arrayOfHtable[b1]).tableID == b) {
            if (arrayOfJPEGHuffmanTable[b] != null)
              throw new IIOException("Metadata has duplicate Htables!"); 
            arrayOfJPEGHuffmanTable[b] = new JPEGHuffmanTable((arrayOfHtable[b1]).numCodes, (arrayOfHtable[b1]).values);
          } 
        } 
      } 
    } 
    return arrayOfJPEGHuffmanTable;
  }
  
  private int getSrcCSType(ImageTypeSpecifier paramImageTypeSpecifier) { return getSrcCSType(paramImageTypeSpecifier.getColorModel()); }
  
  private int getSrcCSType(RenderedImage paramRenderedImage) { return getSrcCSType(paramRenderedImage.getColorModel()); }
  
  private int getSrcCSType(ColorModel paramColorModel) {
    byte b = 0;
    if (paramColorModel != null) {
      boolean bool = paramColorModel.hasAlpha();
      ColorSpace colorSpace = paramColorModel.getColorSpace();
      switch (colorSpace.getType()) {
        case 6:
          b = 1;
          break;
        case 5:
          if (bool) {
            b = 6;
            break;
          } 
          b = 2;
          break;
        case 3:
          if (bool) {
            b = 7;
            break;
          } 
          b = 3;
          break;
        case 13:
          if (colorSpace == JPEG.JCS.getYCC())
            if (bool) {
              b = 10;
            } else {
              b = 5;
            }  
        case 9:
          b = 4;
          break;
      } 
    } 
    return b;
  }
  
  private int getDestCSType(ImageTypeSpecifier paramImageTypeSpecifier) {
    ColorModel colorModel = paramImageTypeSpecifier.getColorModel();
    boolean bool = colorModel.hasAlpha();
    ColorSpace colorSpace = colorModel.getColorSpace();
    byte b = 0;
    switch (colorSpace.getType()) {
      case 6:
        b = 1;
        break;
      case 5:
        if (bool) {
          b = 6;
          break;
        } 
        b = 2;
        break;
      case 3:
        if (bool) {
          b = 7;
          break;
        } 
        b = 3;
        break;
      case 13:
        if (colorSpace == JPEG.JCS.getYCC())
          if (bool) {
            b = 10;
          } else {
            b = 5;
          }  
      case 9:
        b = 4;
        break;
    } 
    return b;
  }
  
  private int getDefaultDestCSType(ImageTypeSpecifier paramImageTypeSpecifier) { return getDefaultDestCSType(paramImageTypeSpecifier.getColorModel()); }
  
  private int getDefaultDestCSType(RenderedImage paramRenderedImage) { return getDefaultDestCSType(paramRenderedImage.getColorModel()); }
  
  private int getDefaultDestCSType(ColorModel paramColorModel) {
    byte b = 0;
    if (paramColorModel != null) {
      boolean bool = paramColorModel.hasAlpha();
      ColorSpace colorSpace = paramColorModel.getColorSpace();
      switch (colorSpace.getType()) {
        case 6:
          b = 1;
          break;
        case 5:
          if (bool) {
            b = 7;
            break;
          } 
          b = 3;
          break;
        case 3:
          if (bool) {
            b = 7;
            break;
          } 
          b = 3;
          break;
        case 13:
          if (colorSpace == JPEG.JCS.getYCC())
            if (bool) {
              b = 10;
            } else {
              b = 5;
            }  
        case 9:
          b = 11;
          break;
      } 
    } 
    return b;
  }
  
  private boolean isSubsampled(SOFMarkerSegment.ComponentSpec[] paramArrayOfComponentSpec) {
    int i = (paramArrayOfComponentSpec[0]).HsamplingFactor;
    int j = (paramArrayOfComponentSpec[0]).VsamplingFactor;
    for (byte b = 1; b < paramArrayOfComponentSpec.length; b++) {
      if ((paramArrayOfComponentSpec[b]).HsamplingFactor != i || (paramArrayOfComponentSpec[b]).HsamplingFactor != i)
        return true; 
    } 
    return false;
  }
  
  private static native void initWriterIDs(Class paramClass1, Class paramClass2);
  
  private native long initJPEGImageWriter();
  
  private native void setDest(long paramLong);
  
  private native boolean writeImage(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt1, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, JPEGQTable[] paramArrayOfJPEGQTable, boolean paramBoolean1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable2, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, int paramInt9, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int[] paramArrayOfInt4, int[] paramArrayOfInt5, int[] paramArrayOfInt6, boolean paramBoolean5, int paramInt10);
  
  private void writeMetadata() throws IOException {
    if (this.metadata == null) {
      if (this.writeDefaultJFIF)
        JFIFMarkerSegment.writeDefaultJFIF(this.ios, this.thumbnails, this.iccProfile, this); 
      if (this.writeAdobe)
        AdobeMarkerSegment.writeAdobeSegment(this.ios, this.newAdobeTransform); 
    } else {
      this.metadata.writeToStream(this.ios, this.ignoreJFIF, this.forceJFIF, this.thumbnails, this.iccProfile, this.ignoreAdobe, this.newAdobeTransform, this);
    } 
  }
  
  private native void writeTables(long paramLong, JPEGQTable[] paramArrayOfJPEGQTable, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable2);
  
  private void grabPixels(int paramInt) {
    Raster raster1 = null;
    if (this.indexed) {
      raster1 = this.srcRas.createChild(this.sourceXOffset, this.sourceYOffset + paramInt, this.sourceWidth, 1, 0, 0, new int[] { 0 });
      boolean bool = (this.indexCM.getTransparency() != 1);
      BufferedImage bufferedImage = this.indexCM.convertToIntDiscrete(raster1, bool);
      raster1 = bufferedImage.getRaster();
    } else {
      raster1 = this.srcRas.createChild(this.sourceXOffset, this.sourceYOffset + paramInt, this.sourceWidth, 1, 0, 0, this.srcBands);
    } 
    if (this.convertTosRGB) {
      if (this.debug)
        System.out.println("Converting to sRGB"); 
      this.converted = this.convertOp.filter(raster1, this.converted);
      raster1 = this.converted;
    } 
    if (this.isAlphaPremultiplied) {
      WritableRaster writableRaster = raster1.createCompatibleWritableRaster();
      int[] arrayOfInt = null;
      arrayOfInt = raster1.getPixels(raster1.getMinX(), raster1.getMinY(), raster1.getWidth(), raster1.getHeight(), arrayOfInt);
      writableRaster.setPixels(raster1.getMinX(), raster1.getMinY(), raster1.getWidth(), raster1.getHeight(), arrayOfInt);
      this.srcCM.coerceData(writableRaster, false);
      raster1 = writableRaster.createChild(writableRaster.getMinX(), writableRaster.getMinY(), writableRaster.getWidth(), writableRaster.getHeight(), 0, 0, this.srcBands);
    } 
    this.raster.setRect(raster1);
    if (paramInt > 7 && paramInt % 8 == 0) {
      this.cbLock.lock();
      try {
        processImageProgress(paramInt / this.sourceHeight * 100.0F);
      } finally {
        this.cbLock.unlock();
      } 
    } 
  }
  
  private native void abortWrite(long paramLong);
  
  private native void resetWriter(long paramLong);
  
  private static native void disposeWriter(long paramLong);
  
  private void writeOutputData(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    this.cbLock.lock();
    try {
      this.ios.write(paramArrayOfByte, paramInt1, paramInt2);
    } finally {
      this.cbLock.unlock();
    } 
  }
  
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
      throw new IllegalStateException("Attempt to clear thread lock form wrong thread. Locked thread: " + this.theThread + "; current thread: " + thread); 
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
    initWriterIDs(JPEGQTable.class, JPEGHuffmanTable.class);
    preferredThumbSizes = new Dimension[] { new Dimension(1, 1), new Dimension(255, 255) };
  }
  
  private static class CallBackLock {
    private State lockState = State.Unlocked;
    
    void check() throws IOException {
      if (this.lockState != State.Unlocked)
        throw new IllegalStateException("Access to the writer is not allowed"); 
    }
    
    private void lock() throws IOException { this.lockState = State.Locked; }
    
    private void unlock() throws IOException { this.lockState = State.Unlocked; }
    
    private enum State {
      Unlocked, Locked;
    }
  }
  
  private static class JPEGWriterDisposerRecord implements DisposerRecord {
    private long pData;
    
    public JPEGWriterDisposerRecord(long param1Long) { this.pData = param1Long; }
    
    public void dispose() throws IOException {
      if (this.pData != 0L) {
        JPEGImageWriter.disposeWriter(this.pData);
        this.pData = 0L;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGImageWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */