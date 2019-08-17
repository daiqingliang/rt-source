package com.sun.imageio.plugins.jpeg;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JPEGMetadata extends IIOMetadata implements Cloneable {
  private static final boolean debug = false;
  
  private List resetSequence = null;
  
  private boolean inThumb = false;
  
  private boolean hasAlpha;
  
  List markerSequence = new ArrayList();
  
  final boolean isStream;
  
  private boolean transparencyDone;
  
  JPEGMetadata(boolean paramBoolean1, boolean paramBoolean2) {
    super(true, "javax_imageio_jpeg_image_1.0", "com.sun.imageio.plugins.jpeg.JPEGImageMetadataFormat", null, null);
    this.inThumb = paramBoolean2;
    this.isStream = paramBoolean1;
    if (paramBoolean1) {
      this.nativeMetadataFormatName = "javax_imageio_jpeg_stream_1.0";
      this.nativeMetadataFormatClassName = "com.sun.imageio.plugins.jpeg.JPEGStreamMetadataFormat";
    } 
  }
  
  JPEGMetadata(boolean paramBoolean1, boolean paramBoolean2, ImageInputStream paramImageInputStream, JPEGImageReader paramJPEGImageReader) throws IOException {
    this(paramBoolean1, paramBoolean2);
    JPEGBuffer jPEGBuffer = new JPEGBuffer(paramImageInputStream);
    jPEGBuffer.loadBuf(0);
    if ((jPEGBuffer.buf[0] & 0xFF) != 255 || (jPEGBuffer.buf[1] & 0xFF) != 216 || (jPEGBuffer.buf[2] & 0xFF) != 255)
      throw new IIOException("Image format error"); 
    boolean bool = false;
    jPEGBuffer.bufAvail -= 2;
    jPEGBuffer.bufPtr = 2;
    SOFMarkerSegment sOFMarkerSegment = null;
    while (!bool) {
      int i;
      byte[] arrayOfByte;
      DRIMarkerSegment dRIMarkerSegment;
      MarkerSegment markerSegment;
      DHTMarkerSegment dHTMarkerSegment;
      DQTMarkerSegment dQTMarkerSegment;
      jPEGBuffer.loadBuf(1);
      jPEGBuffer.scanForFF(paramJPEGImageReader);
      switch (jPEGBuffer.buf[jPEGBuffer.bufPtr] & 0xFF) {
        case 0:
          jPEGBuffer.bufAvail--;
          jPEGBuffer.bufPtr++;
          break;
        case 192:
        case 193:
        case 194:
          if (paramBoolean1)
            throw new IIOException("SOF not permitted in stream metadata"); 
          sOFMarkerSegment = new SOFMarkerSegment(jPEGBuffer);
          break;
        case 219:
          dQTMarkerSegment = new DQTMarkerSegment(jPEGBuffer);
          break;
        case 196:
          dHTMarkerSegment = new DHTMarkerSegment(jPEGBuffer);
          break;
        case 221:
          dRIMarkerSegment = new DRIMarkerSegment(jPEGBuffer);
          break;
        case 224:
          jPEGBuffer.loadBuf(8);
          arrayOfByte = jPEGBuffer.buf;
          i = jPEGBuffer.bufPtr;
          if (arrayOfByte[i + 3] == 74 && arrayOfByte[i + 4] == 70 && arrayOfByte[i + 5] == 73 && arrayOfByte[i + 6] == 70 && arrayOfByte[i + 7] == 0) {
            if (this.inThumb) {
              paramJPEGImageReader.warningOccurred(1);
              JFIFMarkerSegment jFIFMarkerSegment1 = new JFIFMarkerSegment(jPEGBuffer);
              break;
            } 
            if (paramBoolean1)
              throw new IIOException("JFIF not permitted in stream metadata"); 
            if (!this.markerSequence.isEmpty())
              throw new IIOException("JFIF APP0 must be first marker after SOI"); 
            JFIFMarkerSegment jFIFMarkerSegment = new JFIFMarkerSegment(jPEGBuffer);
            break;
          } 
          if (arrayOfByte[i + 3] == 74 && arrayOfByte[i + 4] == 70 && arrayOfByte[i + 5] == 88 && arrayOfByte[i + 6] == 88 && arrayOfByte[i + 7] == 0) {
            if (paramBoolean1)
              throw new IIOException("JFXX not permitted in stream metadata"); 
            if (this.inThumb)
              throw new IIOException("JFXX markers not allowed in JFIF JPEG thumbnail"); 
            JFIFMarkerSegment jFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
            if (jFIFMarkerSegment == null)
              throw new IIOException("JFXX encountered without prior JFIF!"); 
            jFIFMarkerSegment.addJFXX(jPEGBuffer, paramJPEGImageReader);
            break;
          } 
          markerSegment = new MarkerSegment(jPEGBuffer);
          markerSegment.loadData(jPEGBuffer);
          break;
        case 226:
          jPEGBuffer.loadBuf(15);
          if (jPEGBuffer.buf[jPEGBuffer.bufPtr + 3] == 73 && jPEGBuffer.buf[jPEGBuffer.bufPtr + 4] == 67 && jPEGBuffer.buf[jPEGBuffer.bufPtr + 5] == 67 && jPEGBuffer.buf[jPEGBuffer.bufPtr + 6] == 95 && jPEGBuffer.buf[jPEGBuffer.bufPtr + 7] == 80 && jPEGBuffer.buf[jPEGBuffer.bufPtr + 8] == 82 && jPEGBuffer.buf[jPEGBuffer.bufPtr + 9] == 79 && jPEGBuffer.buf[jPEGBuffer.bufPtr + 10] == 70 && jPEGBuffer.buf[jPEGBuffer.bufPtr + 11] == 73 && jPEGBuffer.buf[jPEGBuffer.bufPtr + 12] == 76 && jPEGBuffer.buf[jPEGBuffer.bufPtr + 13] == 69 && jPEGBuffer.buf[jPEGBuffer.bufPtr + 14] == 0) {
            if (paramBoolean1)
              throw new IIOException("ICC profiles not permitted in stream metadata"); 
            JFIFMarkerSegment jFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
            if (jFIFMarkerSegment == null) {
              markerSegment = new MarkerSegment(jPEGBuffer);
              markerSegment.loadData(jPEGBuffer);
              break;
            } 
            jFIFMarkerSegment.addICC(jPEGBuffer);
            break;
          } 
          markerSegment = new MarkerSegment(jPEGBuffer);
          markerSegment.loadData(jPEGBuffer);
          break;
        case 238:
          jPEGBuffer.loadBuf(8);
          if (jPEGBuffer.buf[jPEGBuffer.bufPtr + 3] == 65 && jPEGBuffer.buf[jPEGBuffer.bufPtr + 4] == 100 && jPEGBuffer.buf[jPEGBuffer.bufPtr + 5] == 111 && jPEGBuffer.buf[jPEGBuffer.bufPtr + 6] == 98 && jPEGBuffer.buf[jPEGBuffer.bufPtr + 7] == 101) {
            if (paramBoolean1)
              throw new IIOException("Adobe APP14 markers not permitted in stream metadata"); 
            markerSegment = new AdobeMarkerSegment(jPEGBuffer);
            break;
          } 
          markerSegment = new MarkerSegment(jPEGBuffer);
          markerSegment.loadData(jPEGBuffer);
          break;
        case 254:
          markerSegment = new COMMarkerSegment(jPEGBuffer);
          break;
        case 218:
          if (paramBoolean1)
            throw new IIOException("SOS not permitted in stream metadata"); 
          markerSegment = new SOSMarkerSegment(jPEGBuffer);
          break;
        case 208:
        case 209:
        case 210:
        case 211:
        case 212:
        case 213:
        case 214:
        case 215:
          jPEGBuffer.bufPtr++;
          jPEGBuffer.bufAvail--;
          break;
        case 217:
          bool = true;
          jPEGBuffer.bufPtr++;
          jPEGBuffer.bufAvail--;
          break;
        default:
          markerSegment = new MarkerSegment(jPEGBuffer);
          markerSegment.loadData(jPEGBuffer);
          markerSegment.unknown = true;
          break;
      } 
      if (markerSegment != null) {
        this.markerSequence.add(markerSegment);
        markerSegment = null;
      } 
    } 
    jPEGBuffer.pushBack();
    if (!isConsistent())
      throw new IIOException("Inconsistent metadata read from stream"); 
  }
  
  JPEGMetadata(ImageWriteParam paramImageWriteParam, JPEGImageWriter paramJPEGImageWriter) {
    this(true, false);
    JPEGImageWriteParam jPEGImageWriteParam = null;
    if (paramImageWriteParam != null && paramImageWriteParam instanceof JPEGImageWriteParam) {
      jPEGImageWriteParam = (JPEGImageWriteParam)paramImageWriteParam;
      if (!jPEGImageWriteParam.areTablesSet())
        jPEGImageWriteParam = null; 
    } 
    if (jPEGImageWriteParam != null) {
      this.markerSequence.add(new DQTMarkerSegment(jPEGImageWriteParam.getQTables()));
      this.markerSequence.add(new DHTMarkerSegment(jPEGImageWriteParam.getDCHuffmanTables(), jPEGImageWriteParam.getACHuffmanTables()));
    } else {
      this.markerSequence.add(new DQTMarkerSegment(JPEG.getDefaultQTables()));
      this.markerSequence.add(new DHTMarkerSegment(JPEG.getDefaultHuffmanTables(true), JPEG.getDefaultHuffmanTables(false)));
    } 
    if (!isConsistent())
      throw new InternalError("Default stream metadata is inconsistent"); 
  }
  
  JPEGMetadata(ImageTypeSpecifier paramImageTypeSpecifier, ImageWriteParam paramImageWriteParam, JPEGImageWriter paramJPEGImageWriter) {
    this(false, false);
    boolean bool1 = true;
    boolean bool2 = false;
    byte b = 0;
    boolean bool3 = true;
    boolean bool4 = false;
    boolean bool5 = false;
    boolean bool6 = false;
    boolean bool7 = false;
    boolean bool8 = true;
    boolean bool9 = true;
    float f = 0.75F;
    byte[] arrayOfByte = { 1, 2, 3, 4 };
    int i = 0;
    ImageTypeSpecifier imageTypeSpecifier = null;
    if (paramImageWriteParam != null) {
      imageTypeSpecifier = paramImageWriteParam.getDestinationType();
      if (imageTypeSpecifier != null && paramImageTypeSpecifier != null) {
        paramJPEGImageWriter.warningOccurred(0);
        imageTypeSpecifier = null;
      } 
      if (paramImageWriteParam.canWriteProgressive() && paramImageWriteParam.getProgressiveMode() == 1) {
        bool5 = true;
        bool6 = true;
        bool9 = false;
      } 
      if (paramImageWriteParam instanceof JPEGImageWriteParam) {
        JPEGImageWriteParam jPEGImageWriteParam = (JPEGImageWriteParam)paramImageWriteParam;
        if (jPEGImageWriteParam.areTablesSet()) {
          bool8 = false;
          bool9 = false;
          if (jPEGImageWriteParam.getDCHuffmanTables().length > 2 || jPEGImageWriteParam.getACHuffmanTables().length > 2)
            bool7 = true; 
        } 
        if (!bool5) {
          bool6 = jPEGImageWriteParam.getOptimizeHuffmanTables();
          if (bool6)
            bool9 = false; 
        } 
      } 
      if (paramImageWriteParam.canWriteCompressed() && paramImageWriteParam.getCompressionMode() == 2)
        f = paramImageWriteParam.getCompressionQuality(); 
    } 
    ColorSpace colorSpace = null;
    if (imageTypeSpecifier != null) {
      ColorModel colorModel = imageTypeSpecifier.getColorModel();
      i = colorModel.getNumComponents();
      boolean bool = (colorModel.getNumColorComponents() != i) ? 1 : 0;
      boolean bool10 = colorModel.hasAlpha();
      colorSpace = colorModel.getColorSpace();
      int j = colorSpace.getType();
      switch (j) {
        case 6:
          bool3 = false;
          if (bool)
            bool1 = false; 
          break;
        case 13:
          if (colorSpace == JPEG.JCS.getYCC()) {
            bool1 = false;
            arrayOfByte[0] = 89;
            arrayOfByte[1] = 67;
            arrayOfByte[2] = 99;
            if (bool10)
              arrayOfByte[3] = 65; 
          } 
          break;
        case 3:
          if (bool) {
            bool1 = false;
            if (!bool10) {
              bool2 = true;
              b = 2;
            } 
          } 
          break;
        case 5:
          bool1 = false;
          bool2 = true;
          bool3 = false;
          arrayOfByte[0] = 82;
          arrayOfByte[1] = 71;
          arrayOfByte[2] = 66;
          if (bool10)
            arrayOfByte[3] = 65; 
          break;
        default:
          bool1 = false;
          bool3 = false;
          break;
      } 
    } else if (paramImageTypeSpecifier != null) {
      ColorModel colorModel = paramImageTypeSpecifier.getColorModel();
      i = colorModel.getNumComponents();
      boolean bool = (colorModel.getNumColorComponents() != i) ? 1 : 0;
      boolean bool10 = colorModel.hasAlpha();
      colorSpace = colorModel.getColorSpace();
      int j = colorSpace.getType();
      switch (j) {
        case 6:
          bool3 = false;
          if (bool)
            bool1 = false; 
          break;
        case 5:
          if (bool10)
            bool1 = false; 
          break;
        case 13:
          bool1 = false;
          bool3 = false;
          if (colorSpace.equals(ColorSpace.getInstance(1002))) {
            bool3 = true;
            bool2 = true;
            arrayOfByte[0] = 89;
            arrayOfByte[1] = 67;
            arrayOfByte[2] = 99;
            if (bool10)
              arrayOfByte[3] = 65; 
          } 
          break;
        case 3:
          if (bool) {
            bool1 = false;
            if (!bool10) {
              bool2 = true;
              b = 2;
            } 
          } 
          break;
        case 9:
          bool1 = false;
          bool2 = true;
          b = 2;
          break;
        default:
          bool1 = false;
          bool3 = false;
          break;
      } 
    } 
    if (bool1 && JPEG.isNonStandardICC(colorSpace))
      bool4 = true; 
    if (bool1) {
      JFIFMarkerSegment jFIFMarkerSegment = new JFIFMarkerSegment();
      this.markerSequence.add(jFIFMarkerSegment);
      if (bool4)
        try {
          jFIFMarkerSegment.addICC((ICC_ColorSpace)colorSpace);
        } catch (IOException iOException) {} 
    } 
    if (bool2)
      this.markerSequence.add(new AdobeMarkerSegment(b)); 
    if (bool8)
      this.markerSequence.add(new DQTMarkerSegment(f, bool3)); 
    if (bool9)
      this.markerSequence.add(new DHTMarkerSegment(bool3)); 
    this.markerSequence.add(new SOFMarkerSegment(bool5, bool7, bool3, arrayOfByte, i));
    if (!bool5)
      this.markerSequence.add(new SOSMarkerSegment(bool3, arrayOfByte, i)); 
    if (!isConsistent())
      throw new InternalError("Default image metadata is inconsistent"); 
  }
  
  MarkerSegment findMarkerSegment(int paramInt) {
    for (MarkerSegment markerSegment : this.markerSequence) {
      if (markerSegment.tag == paramInt)
        return markerSegment; 
    } 
    return null;
  }
  
  MarkerSegment findMarkerSegment(Class paramClass, boolean paramBoolean) {
    if (paramBoolean) {
      for (MarkerSegment markerSegment : this.markerSequence) {
        if (paramClass.isInstance(markerSegment))
          return markerSegment; 
      } 
    } else {
      ListIterator listIterator = this.markerSequence.listIterator(this.markerSequence.size());
      while (listIterator.hasPrevious()) {
        MarkerSegment markerSegment = (MarkerSegment)listIterator.previous();
        if (paramClass.isInstance(markerSegment))
          return markerSegment; 
      } 
    } 
    return null;
  }
  
  private int findMarkerSegmentPosition(Class paramClass, boolean paramBoolean) {
    if (paramBoolean) {
      ListIterator listIterator = this.markerSequence.listIterator();
      for (byte b = 0; listIterator.hasNext(); b++) {
        MarkerSegment markerSegment = (MarkerSegment)listIterator.next();
        if (paramClass.isInstance(markerSegment))
          return b; 
      } 
    } else {
      ListIterator listIterator = this.markerSequence.listIterator(this.markerSequence.size());
      for (int i = this.markerSequence.size() - 1; listIterator.hasPrevious(); i--) {
        MarkerSegment markerSegment = (MarkerSegment)listIterator.previous();
        if (paramClass.isInstance(markerSegment))
          return i; 
      } 
    } 
    return -1;
  }
  
  private int findLastUnknownMarkerSegmentPosition() {
    ListIterator listIterator = this.markerSequence.listIterator(this.markerSequence.size());
    for (int i = this.markerSequence.size() - 1; listIterator.hasPrevious(); i--) {
      MarkerSegment markerSegment = (MarkerSegment)listIterator.previous();
      if (markerSegment.unknown == true)
        return i; 
    } 
    return -1;
  }
  
  protected Object clone() {
    JPEGMetadata jPEGMetadata = null;
    try {
      jPEGMetadata = (JPEGMetadata)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {}
    if (this.markerSequence != null)
      jPEGMetadata.markerSequence = cloneSequence(); 
    jPEGMetadata.resetSequence = null;
    return jPEGMetadata;
  }
  
  private List cloneSequence() {
    if (this.markerSequence == null)
      return null; 
    ArrayList arrayList = new ArrayList(this.markerSequence.size());
    for (MarkerSegment markerSegment : this.markerSequence)
      arrayList.add(markerSegment.clone()); 
    return arrayList;
  }
  
  public Node getAsTree(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("null formatName!"); 
    if (this.isStream) {
      if (paramString.equals("javax_imageio_jpeg_stream_1.0"))
        return getNativeTree(); 
    } else {
      if (paramString.equals("javax_imageio_jpeg_image_1.0"))
        return getNativeTree(); 
      if (paramString.equals("javax_imageio_1.0"))
        return getStandardTree(); 
    } 
    throw new IllegalArgumentException("Unsupported format name: " + paramString);
  }
  
  IIOMetadataNode getNativeTree() {
    IIOMetadataNode iIOMetadataNode2;
    IIOMetadataNode iIOMetadataNode1;
    Iterator iterator = this.markerSequence.iterator();
    if (this.isStream) {
      iIOMetadataNode1 = new IIOMetadataNode("javax_imageio_jpeg_stream_1.0");
      iIOMetadataNode2 = iIOMetadataNode1;
    } else {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("markerSequence");
      if (!this.inThumb) {
        iIOMetadataNode1 = new IIOMetadataNode("javax_imageio_jpeg_image_1.0");
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("JPEGvariety");
        iIOMetadataNode1.appendChild(iIOMetadataNode3);
        JFIFMarkerSegment jFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
        if (jFIFMarkerSegment != null) {
          iterator.next();
          iIOMetadataNode3.appendChild(jFIFMarkerSegment.getNativeNode());
        } 
        iIOMetadataNode1.appendChild(iIOMetadataNode);
      } else {
        iIOMetadataNode1 = iIOMetadataNode;
      } 
      iIOMetadataNode2 = iIOMetadataNode;
    } 
    while (iterator.hasNext()) {
      MarkerSegment markerSegment = (MarkerSegment)iterator.next();
      iIOMetadataNode2.appendChild(markerSegment.getNativeNode());
    } 
    return iIOMetadataNode1;
  }
  
  protected IIOMetadataNode getStandardChromaNode() {
    this.hasAlpha = false;
    SOFMarkerSegment sOFMarkerSegment = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
    if (sOFMarkerSegment == null)
      return null; 
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Chroma");
    IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("ColorSpaceType");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    int i = sOFMarkerSegment.componentSpecs.length;
    IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("NumChannels");
    iIOMetadataNode1.appendChild(iIOMetadataNode3);
    iIOMetadataNode3.setAttribute("value", Integer.toString(i));
    if (findMarkerSegment(JFIFMarkerSegment.class, true) != null) {
      if (i == 1) {
        iIOMetadataNode2.setAttribute("name", "GRAY");
      } else {
        iIOMetadataNode2.setAttribute("name", "YCbCr");
      } 
      return iIOMetadataNode1;
    } 
    AdobeMarkerSegment adobeMarkerSegment = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
    if (adobeMarkerSegment != null) {
      switch (adobeMarkerSegment.transform) {
        case 2:
          iIOMetadataNode2.setAttribute("name", "YCCK");
          break;
        case 1:
          iIOMetadataNode2.setAttribute("name", "YCbCr");
          break;
        case 0:
          if (i == 3) {
            iIOMetadataNode2.setAttribute("name", "RGB");
            break;
          } 
          if (i == 4)
            iIOMetadataNode2.setAttribute("name", "CMYK"); 
          break;
      } 
      return iIOMetadataNode1;
    } 
    if (i < 3) {
      iIOMetadataNode2.setAttribute("name", "GRAY");
      if (i == 2)
        this.hasAlpha = true; 
      return iIOMetadataNode1;
    } 
    boolean bool = true;
    byte b1;
    for (b1 = 0; b1 < sOFMarkerSegment.componentSpecs.length; b1++) {
      int m = (sOFMarkerSegment.componentSpecs[b1]).componentId;
      if (m < 1 || m >= sOFMarkerSegment.componentSpecs.length)
        bool = false; 
    } 
    if (bool) {
      iIOMetadataNode2.setAttribute("name", "YCbCr");
      if (i == 4)
        this.hasAlpha = true; 
      return iIOMetadataNode1;
    } 
    if ((sOFMarkerSegment.componentSpecs[0]).componentId == 82 && (sOFMarkerSegment.componentSpecs[1]).componentId == 71 && (sOFMarkerSegment.componentSpecs[2]).componentId == 66) {
      iIOMetadataNode2.setAttribute("name", "RGB");
      if (i == 4 && (sOFMarkerSegment.componentSpecs[3]).componentId == 65)
        this.hasAlpha = true; 
      return iIOMetadataNode1;
    } 
    if ((sOFMarkerSegment.componentSpecs[0]).componentId == 89 && (sOFMarkerSegment.componentSpecs[1]).componentId == 67 && (sOFMarkerSegment.componentSpecs[2]).componentId == 99) {
      iIOMetadataNode2.setAttribute("name", "PhotoYCC");
      if (i == 4 && (sOFMarkerSegment.componentSpecs[3]).componentId == 65)
        this.hasAlpha = true; 
      return iIOMetadataNode1;
    } 
    b1 = 0;
    int j = (sOFMarkerSegment.componentSpecs[0]).HsamplingFactor;
    int k = (sOFMarkerSegment.componentSpecs[0]).VsamplingFactor;
    for (byte b2 = 1; b2 < sOFMarkerSegment.componentSpecs.length; b2++) {
      if ((sOFMarkerSegment.componentSpecs[b2]).HsamplingFactor != j || (sOFMarkerSegment.componentSpecs[b2]).VsamplingFactor != k) {
        b1 = 1;
        break;
      } 
    } 
    if (b1 != 0) {
      iIOMetadataNode2.setAttribute("name", "YCbCr");
      if (i == 4)
        this.hasAlpha = true; 
      return iIOMetadataNode1;
    } 
    if (i == 3) {
      iIOMetadataNode2.setAttribute("name", "RGB");
    } else {
      iIOMetadataNode2.setAttribute("name", "CMYK");
    } 
    return iIOMetadataNode1;
  }
  
  protected IIOMetadataNode getStandardCompressionNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Compression");
    IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("CompressionTypeName");
    iIOMetadataNode2.setAttribute("value", "JPEG");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("Lossless");
    iIOMetadataNode3.setAttribute("value", "FALSE");
    iIOMetadataNode1.appendChild(iIOMetadataNode3);
    byte b = 0;
    for (MarkerSegment markerSegment : this.markerSequence) {
      if (markerSegment.tag == 218)
        b++; 
    } 
    if (b != 0) {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("NumProgressiveScans");
      iIOMetadataNode.setAttribute("value", Integer.toString(b));
      iIOMetadataNode1.appendChild(iIOMetadataNode);
    } 
    return iIOMetadataNode1;
  }
  
  protected IIOMetadataNode getStandardDimensionNode() {
    IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Dimension");
    IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("ImageOrientation");
    iIOMetadataNode2.setAttribute("value", "normal");
    iIOMetadataNode1.appendChild(iIOMetadataNode2);
    JFIFMarkerSegment jFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
    if (jFIFMarkerSegment != null) {
      float f;
      if (jFIFMarkerSegment.resUnits == 0) {
        f = jFIFMarkerSegment.Xdensity / jFIFMarkerSegment.Ydensity;
      } else {
        f = jFIFMarkerSegment.Ydensity / jFIFMarkerSegment.Xdensity;
      } 
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("PixelAspectRatio");
      iIOMetadataNode.setAttribute("value", Float.toString(f));
      iIOMetadataNode1.insertBefore(iIOMetadataNode, iIOMetadataNode2);
      if (jFIFMarkerSegment.resUnits != 0) {
        float f1 = (jFIFMarkerSegment.resUnits == 1) ? 25.4F : 10.0F;
        IIOMetadataNode iIOMetadataNode3 = new IIOMetadataNode("HorizontalPixelSize");
        iIOMetadataNode3.setAttribute("value", Float.toString(f1 / jFIFMarkerSegment.Xdensity));
        iIOMetadataNode1.appendChild(iIOMetadataNode3);
        IIOMetadataNode iIOMetadataNode4 = new IIOMetadataNode("VerticalPixelSize");
        iIOMetadataNode4.setAttribute("value", Float.toString(f1 / jFIFMarkerSegment.Ydensity));
        iIOMetadataNode1.appendChild(iIOMetadataNode4);
      } 
    } 
    return iIOMetadataNode1;
  }
  
  protected IIOMetadataNode getStandardTextNode() {
    IIOMetadataNode iIOMetadataNode = null;
    if (findMarkerSegment('þ') != null) {
      iIOMetadataNode = new IIOMetadataNode("Text");
      for (MarkerSegment markerSegment : this.markerSequence) {
        if (markerSegment.tag == 254) {
          COMMarkerSegment cOMMarkerSegment = (COMMarkerSegment)markerSegment;
          IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("TextEntry");
          iIOMetadataNode1.setAttribute("keyword", "comment");
          iIOMetadataNode1.setAttribute("value", cOMMarkerSegment.getComment());
          iIOMetadataNode.appendChild(iIOMetadataNode1);
        } 
      } 
    } 
    return iIOMetadataNode;
  }
  
  protected IIOMetadataNode getStandardTransparencyNode() {
    IIOMetadataNode iIOMetadataNode = null;
    if (this.hasAlpha == true) {
      iIOMetadataNode = new IIOMetadataNode("Transparency");
      IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("Alpha");
      iIOMetadataNode1.setAttribute("value", "nonpremultiplied");
      iIOMetadataNode.appendChild(iIOMetadataNode1);
    } 
    return iIOMetadataNode;
  }
  
  public boolean isReadOnly() { return false; }
  
  public void mergeTree(String paramString, Node paramNode) throws IIOInvalidTreeException {
    if (paramString == null)
      throw new IllegalArgumentException("null formatName!"); 
    if (paramNode == null)
      throw new IllegalArgumentException("null root!"); 
    List list = null;
    if (this.resetSequence == null) {
      this.resetSequence = cloneSequence();
      list = this.resetSequence;
    } else {
      list = cloneSequence();
    } 
    if (this.isStream && paramString.equals("javax_imageio_jpeg_stream_1.0")) {
      mergeNativeTree(paramNode);
    } else if (!this.isStream && paramString.equals("javax_imageio_jpeg_image_1.0")) {
      mergeNativeTree(paramNode);
    } else if (!this.isStream && paramString.equals("javax_imageio_1.0")) {
      mergeStandardTree(paramNode);
    } else {
      throw new IllegalArgumentException("Unsupported format name: " + paramString);
    } 
    if (!isConsistent()) {
      this.markerSequence = list;
      throw new IIOInvalidTreeException("Merged tree is invalid; original restored", paramNode);
    } 
  }
  
  private void mergeNativeTree(Node paramNode) throws IIOInvalidTreeException {
    String str = paramNode.getNodeName();
    if (str != (this.isStream ? "javax_imageio_jpeg_stream_1.0" : "javax_imageio_jpeg_image_1.0"))
      throw new IIOInvalidTreeException("Invalid root node name: " + str, paramNode); 
    if (paramNode.getChildNodes().getLength() != 2)
      throw new IIOInvalidTreeException("JPEGvariety and markerSequence nodes must be present", paramNode); 
    mergeJFIFsubtree(paramNode.getFirstChild());
    mergeSequenceSubtree(paramNode.getLastChild());
  }
  
  private void mergeJFIFsubtree(Node paramNode) throws IIOInvalidTreeException {
    if (paramNode.getChildNodes().getLength() != 0) {
      Node node = paramNode.getFirstChild();
      JFIFMarkerSegment jFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
      if (jFIFMarkerSegment != null) {
        jFIFMarkerSegment.updateFromNativeNode(node, false);
      } else {
        this.markerSequence.add(0, new JFIFMarkerSegment(node));
      } 
    } 
  }
  
  private void mergeSequenceSubtree(Node paramNode) throws IIOInvalidTreeException {
    NodeList nodeList = paramNode.getChildNodes();
    for (byte b = 0; b < nodeList.getLength(); b++) {
      Node node = nodeList.item(b);
      String str = node.getNodeName();
      if (str.equals("dqt")) {
        mergeDQTNode(node);
      } else if (str.equals("dht")) {
        mergeDHTNode(node);
      } else if (str.equals("dri")) {
        mergeDRINode(node);
      } else if (str.equals("com")) {
        mergeCOMNode(node);
      } else if (str.equals("app14Adobe")) {
        mergeAdobeNode(node);
      } else if (str.equals("unknown")) {
        mergeUnknownNode(node);
      } else if (str.equals("sof")) {
        mergeSOFNode(node);
      } else if (str.equals("sos")) {
        mergeSOSNode(node);
      } else {
        throw new IIOInvalidTreeException("Invalid node: " + str, node);
      } 
    } 
  }
  
  private void mergeDQTNode(Node paramNode) throws IIOInvalidTreeException {
    ArrayList arrayList = new ArrayList();
    for (MarkerSegment markerSegment : this.markerSequence) {
      if (markerSegment instanceof DQTMarkerSegment)
        arrayList.add(markerSegment); 
    } 
    if (!arrayList.isEmpty()) {
      NodeList nodeList = paramNode.getChildNodes();
      for (byte b = 0; b < nodeList.getLength(); b++) {
        Node node = nodeList.item(b);
        int i = MarkerSegment.getAttributeValue(node, null, "qtableId", 0, 3, true);
        DQTMarkerSegment dQTMarkerSegment = null;
        byte b1 = -1;
        for (byte b2 = 0; b2 < arrayList.size(); b2++) {
          DQTMarkerSegment dQTMarkerSegment1 = (DQTMarkerSegment)arrayList.get(b2);
          for (byte b3 = 0; b3 < dQTMarkerSegment1.tables.size(); b3++) {
            DQTMarkerSegment.Qtable qtable = (DQTMarkerSegment.Qtable)dQTMarkerSegment1.tables.get(b3);
            if (i == qtable.tableID) {
              dQTMarkerSegment = dQTMarkerSegment1;
              b1 = b3;
              break;
            } 
          } 
          if (dQTMarkerSegment != null)
            break; 
        } 
        if (dQTMarkerSegment != null) {
          dQTMarkerSegment.tables.set(b1, dQTMarkerSegment.getQtableFromNode(node));
        } else {
          dQTMarkerSegment = (DQTMarkerSegment)arrayList.get(arrayList.size() - 1);
          dQTMarkerSegment.tables.add(dQTMarkerSegment.getQtableFromNode(node));
        } 
      } 
    } else {
      DQTMarkerSegment dQTMarkerSegment = new DQTMarkerSegment(paramNode);
      int i = findMarkerSegmentPosition(DHTMarkerSegment.class, true);
      int j = findMarkerSegmentPosition(SOFMarkerSegment.class, true);
      int k = findMarkerSegmentPosition(SOSMarkerSegment.class, true);
      if (i != -1) {
        this.markerSequence.add(i, dQTMarkerSegment);
      } else if (j != -1) {
        this.markerSequence.add(j, dQTMarkerSegment);
      } else if (k != -1) {
        this.markerSequence.add(k, dQTMarkerSegment);
      } else {
        this.markerSequence.add(dQTMarkerSegment);
      } 
    } 
  }
  
  private void mergeDHTNode(Node paramNode) throws IIOInvalidTreeException {
    ArrayList arrayList = new ArrayList();
    for (MarkerSegment markerSegment : this.markerSequence) {
      if (markerSegment instanceof DHTMarkerSegment)
        arrayList.add(markerSegment); 
    } 
    if (!arrayList.isEmpty()) {
      NodeList nodeList = paramNode.getChildNodes();
      for (byte b = 0; b < nodeList.getLength(); b++) {
        Node node = nodeList.item(b);
        NamedNodeMap namedNodeMap = node.getAttributes();
        int i = MarkerSegment.getAttributeValue(node, namedNodeMap, "htableId", 0, 3, true);
        int j = MarkerSegment.getAttributeValue(node, namedNodeMap, "class", 0, 1, true);
        DHTMarkerSegment dHTMarkerSegment = null;
        byte b1 = -1;
        for (byte b2 = 0; b2 < arrayList.size(); b2++) {
          DHTMarkerSegment dHTMarkerSegment1 = (DHTMarkerSegment)arrayList.get(b2);
          for (byte b3 = 0; b3 < dHTMarkerSegment1.tables.size(); b3++) {
            DHTMarkerSegment.Htable htable = (DHTMarkerSegment.Htable)dHTMarkerSegment1.tables.get(b3);
            if (i == htable.tableID && j == htable.tableClass) {
              dHTMarkerSegment = dHTMarkerSegment1;
              b1 = b3;
              break;
            } 
          } 
          if (dHTMarkerSegment != null)
            break; 
        } 
        if (dHTMarkerSegment != null) {
          dHTMarkerSegment.tables.set(b1, dHTMarkerSegment.getHtableFromNode(node));
        } else {
          dHTMarkerSegment = (DHTMarkerSegment)arrayList.get(arrayList.size() - 1);
          dHTMarkerSegment.tables.add(dHTMarkerSegment.getHtableFromNode(node));
        } 
      } 
    } else {
      DHTMarkerSegment dHTMarkerSegment = new DHTMarkerSegment(paramNode);
      int i = findMarkerSegmentPosition(DQTMarkerSegment.class, false);
      int j = findMarkerSegmentPosition(SOFMarkerSegment.class, true);
      int k = findMarkerSegmentPosition(SOSMarkerSegment.class, true);
      if (i != -1) {
        this.markerSequence.add(i + 1, dHTMarkerSegment);
      } else if (j != -1) {
        this.markerSequence.add(j, dHTMarkerSegment);
      } else if (k != -1) {
        this.markerSequence.add(k, dHTMarkerSegment);
      } else {
        this.markerSequence.add(dHTMarkerSegment);
      } 
    } 
  }
  
  private void mergeDRINode(Node paramNode) throws IIOInvalidTreeException {
    DRIMarkerSegment dRIMarkerSegment = (DRIMarkerSegment)findMarkerSegment(DRIMarkerSegment.class, true);
    if (dRIMarkerSegment != null) {
      dRIMarkerSegment.updateFromNativeNode(paramNode, false);
    } else {
      DRIMarkerSegment dRIMarkerSegment1 = new DRIMarkerSegment(paramNode);
      int i = findMarkerSegmentPosition(SOFMarkerSegment.class, true);
      int j = findMarkerSegmentPosition(SOSMarkerSegment.class, true);
      if (i != -1) {
        this.markerSequence.add(i, dRIMarkerSegment1);
      } else if (j != -1) {
        this.markerSequence.add(j, dRIMarkerSegment1);
      } else {
        this.markerSequence.add(dRIMarkerSegment1);
      } 
    } 
  }
  
  private void mergeCOMNode(Node paramNode) throws IIOInvalidTreeException {
    COMMarkerSegment cOMMarkerSegment = new COMMarkerSegment(paramNode);
    insertCOMMarkerSegment(cOMMarkerSegment);
  }
  
  private void insertCOMMarkerSegment(COMMarkerSegment paramCOMMarkerSegment) {
    int i = findMarkerSegmentPosition(COMMarkerSegment.class, false);
    boolean bool = (findMarkerSegment(JFIFMarkerSegment.class, true) != null) ? 1 : 0;
    int j = findMarkerSegmentPosition(AdobeMarkerSegment.class, true);
    if (i != -1) {
      this.markerSequence.add(i + 1, paramCOMMarkerSegment);
    } else if (bool) {
      this.markerSequence.add(1, paramCOMMarkerSegment);
    } else if (j != -1) {
      this.markerSequence.add(j + 1, paramCOMMarkerSegment);
    } else {
      this.markerSequence.add(0, paramCOMMarkerSegment);
    } 
  }
  
  private void mergeAdobeNode(Node paramNode) throws IIOInvalidTreeException {
    AdobeMarkerSegment adobeMarkerSegment = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
    if (adobeMarkerSegment != null) {
      adobeMarkerSegment.updateFromNativeNode(paramNode, false);
    } else {
      AdobeMarkerSegment adobeMarkerSegment1 = new AdobeMarkerSegment(paramNode);
      insertAdobeMarkerSegment(adobeMarkerSegment1);
    } 
  }
  
  private void insertAdobeMarkerSegment(AdobeMarkerSegment paramAdobeMarkerSegment) {
    boolean bool = (findMarkerSegment(JFIFMarkerSegment.class, true) != null) ? 1 : 0;
    int i = findLastUnknownMarkerSegmentPosition();
    if (bool) {
      this.markerSequence.add(1, paramAdobeMarkerSegment);
    } else if (i != -1) {
      this.markerSequence.add(i + 1, paramAdobeMarkerSegment);
    } else {
      this.markerSequence.add(0, paramAdobeMarkerSegment);
    } 
  }
  
  private void mergeUnknownNode(Node paramNode) throws IIOInvalidTreeException {
    MarkerSegment markerSegment = new MarkerSegment(paramNode);
    int i = findLastUnknownMarkerSegmentPosition();
    boolean bool = (findMarkerSegment(JFIFMarkerSegment.class, true) != null) ? 1 : 0;
    int j = findMarkerSegmentPosition(AdobeMarkerSegment.class, true);
    if (i != -1) {
      this.markerSequence.add(i + 1, markerSegment);
    } else if (bool) {
      this.markerSequence.add(1, markerSegment);
    } 
    if (j != -1) {
      this.markerSequence.add(j, markerSegment);
    } else {
      this.markerSequence.add(0, markerSegment);
    } 
  }
  
  private void mergeSOFNode(Node paramNode) throws IIOInvalidTreeException {
    SOFMarkerSegment sOFMarkerSegment = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
    if (sOFMarkerSegment != null) {
      sOFMarkerSegment.updateFromNativeNode(paramNode, false);
    } else {
      SOFMarkerSegment sOFMarkerSegment1 = new SOFMarkerSegment(paramNode);
      int i = findMarkerSegmentPosition(SOSMarkerSegment.class, true);
      if (i != -1) {
        this.markerSequence.add(i, sOFMarkerSegment1);
      } else {
        this.markerSequence.add(sOFMarkerSegment1);
      } 
    } 
  }
  
  private void mergeSOSNode(Node paramNode) throws IIOInvalidTreeException {
    SOSMarkerSegment sOSMarkerSegment1 = (SOSMarkerSegment)findMarkerSegment(SOSMarkerSegment.class, true);
    SOSMarkerSegment sOSMarkerSegment2 = (SOSMarkerSegment)findMarkerSegment(SOSMarkerSegment.class, false);
    if (sOSMarkerSegment1 != null) {
      if (sOSMarkerSegment1 != sOSMarkerSegment2)
        throw new IIOInvalidTreeException("Can't merge SOS node into a tree with > 1 SOS node", paramNode); 
      sOSMarkerSegment1.updateFromNativeNode(paramNode, false);
    } else {
      this.markerSequence.add(new SOSMarkerSegment(paramNode));
    } 
  }
  
  private void mergeStandardTree(Node paramNode) throws IIOInvalidTreeException {
    this.transparencyDone = false;
    NodeList nodeList = paramNode.getChildNodes();
    for (byte b = 0; b < nodeList.getLength(); b++) {
      Node node = nodeList.item(b);
      String str = node.getNodeName();
      if (str.equals("Chroma")) {
        mergeStandardChromaNode(node, nodeList);
      } else if (str.equals("Compression")) {
        mergeStandardCompressionNode(node);
      } else if (str.equals("Data")) {
        mergeStandardDataNode(node);
      } else if (str.equals("Dimension")) {
        mergeStandardDimensionNode(node);
      } else if (str.equals("Document")) {
        mergeStandardDocumentNode(node);
      } else if (str.equals("Text")) {
        mergeStandardTextNode(node);
      } else if (str.equals("Transparency")) {
        mergeStandardTransparencyNode(node);
      } else {
        throw new IIOInvalidTreeException("Invalid node: " + str, node);
      } 
    } 
  }
  
  private void mergeStandardChromaNode(Node paramNode, NodeList paramNodeList) throws IIOInvalidTreeException {
    if (this.transparencyDone)
      throw new IIOInvalidTreeException("Transparency node must follow Chroma node", paramNode); 
    Node node = paramNode.getFirstChild();
    if (node == null || !node.getNodeName().equals("ColorSpaceType"))
      return; 
    String str = node.getAttributes().getNamedItem("name").getNodeValue();
    byte b1 = 0;
    boolean bool1 = false;
    boolean bool2 = false;
    byte b2 = 0;
    boolean bool3 = false;
    byte[] arrayOfByte = { 1, 2, 3, 4 };
    if (str.equals("GRAY")) {
      b1 = 1;
      bool1 = true;
    } else if (str.equals("YCbCr")) {
      b1 = 3;
      bool1 = true;
      bool3 = true;
    } else if (str.equals("PhotoYCC")) {
      b1 = 3;
      bool2 = true;
      b2 = 1;
      arrayOfByte[0] = 89;
      arrayOfByte[1] = 67;
      arrayOfByte[2] = 99;
    } else if (str.equals("RGB")) {
      b1 = 3;
      bool2 = true;
      b2 = 0;
      arrayOfByte[0] = 82;
      arrayOfByte[1] = 71;
      arrayOfByte[2] = 66;
    } else if (str.equals("XYZ") || str.equals("Lab") || str.equals("Luv") || str.equals("YxY") || str.equals("HSV") || str.equals("HLS") || str.equals("CMY") || str.equals("3CLR")) {
      b1 = 3;
    } else if (str.equals("YCCK")) {
      b1 = 4;
      bool2 = true;
      b2 = 2;
      bool3 = true;
    } else if (str.equals("CMYK")) {
      b1 = 4;
      bool2 = true;
      b2 = 0;
    } else if (str.equals("4CLR")) {
      b1 = 4;
    } else {
      return;
    } 
    boolean bool4 = false;
    for (byte b3 = 0; b3 < paramNodeList.getLength(); b3++) {
      Node node1 = paramNodeList.item(b3);
      if (node1.getNodeName().equals("Transparency")) {
        bool4 = wantAlpha(node1);
        break;
      } 
    } 
    if (bool4) {
      b1++;
      bool1 = false;
      if (arrayOfByte[0] == 82) {
        arrayOfByte[3] = 65;
        bool2 = false;
      } 
    } 
    JFIFMarkerSegment jFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
    AdobeMarkerSegment adobeMarkerSegment = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
    SOFMarkerSegment sOFMarkerSegment = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
    SOSMarkerSegment sOSMarkerSegment = (SOSMarkerSegment)findMarkerSegment(SOSMarkerSegment.class, true);
    if (sOFMarkerSegment != null && sOFMarkerSegment.tag == 194 && sOFMarkerSegment.componentSpecs.length != b1 && sOSMarkerSegment != null)
      return; 
    if (!bool1 && jFIFMarkerSegment != null)
      this.markerSequence.remove(jFIFMarkerSegment); 
    if (bool1 && !this.isStream)
      this.markerSequence.add(0, new JFIFMarkerSegment()); 
    if (bool2) {
      if (adobeMarkerSegment == null && !this.isStream) {
        adobeMarkerSegment = new AdobeMarkerSegment(b2);
        insertAdobeMarkerSegment(adobeMarkerSegment);
      } else {
        adobeMarkerSegment.transform = b2;
      } 
    } else if (adobeMarkerSegment != null) {
      this.markerSequence.remove(adobeMarkerSegment);
    } 
    boolean bool5 = false;
    boolean bool6 = false;
    boolean bool7 = false;
    int[] arrayOfInt1 = { 0, 1, 1, 0 };
    int[] arrayOfInt2 = { 0, 0, 0, 0 };
    int[] arrayOfInt3 = bool3 ? arrayOfInt1 : arrayOfInt2;
    SOFMarkerSegment.ComponentSpec[] arrayOfComponentSpec = null;
    if (sOFMarkerSegment != null) {
      arrayOfComponentSpec = sOFMarkerSegment.componentSpecs;
      bool7 = (sOFMarkerSegment.tag == 194);
      this.markerSequence.set(this.markerSequence.indexOf(sOFMarkerSegment), new SOFMarkerSegment(bool7, false, bool3, arrayOfByte, b1));
      byte b;
      for (b = 0; b < arrayOfComponentSpec.length; b++) {
        if ((arrayOfComponentSpec[b]).QtableSelector != arrayOfInt3[b])
          bool5 = true; 
      } 
      if (bool7) {
        b = 0;
        for (byte b4 = 0; b4 < arrayOfComponentSpec.length; b4++) {
          if (arrayOfByte[b4] != (arrayOfComponentSpec[b4]).componentId)
            b = 1; 
        } 
        if (b != 0)
          for (MarkerSegment markerSegment : this.markerSequence) {
            if (markerSegment instanceof SOSMarkerSegment) {
              SOSMarkerSegment sOSMarkerSegment1 = (SOSMarkerSegment)markerSegment;
              for (byte b5 = 0; b5 < sOSMarkerSegment1.componentSpecs.length; b5++) {
                int i = (sOSMarkerSegment1.componentSpecs[b5]).componentSelector;
                for (byte b6 = 0; b6 < arrayOfComponentSpec.length; b6++) {
                  if ((arrayOfComponentSpec[b6]).componentId == i)
                    (sOSMarkerSegment1.componentSpecs[b5]).componentSelector = arrayOfByte[b6]; 
                } 
              } 
            } 
          }  
      } else if (sOSMarkerSegment != null) {
        for (b = 0; b < sOSMarkerSegment.componentSpecs.length; b++) {
          if ((sOSMarkerSegment.componentSpecs[b]).dcHuffTable != arrayOfInt3[b] || (sOSMarkerSegment.componentSpecs[b]).acHuffTable != arrayOfInt3[b])
            bool6 = true; 
        } 
        this.markerSequence.set(this.markerSequence.indexOf(sOSMarkerSegment), new SOSMarkerSegment(bool3, arrayOfByte, b1));
      } 
    } else if (this.isStream) {
      bool5 = true;
      bool6 = true;
    } 
    if (bool5) {
      ArrayList arrayList = new ArrayList();
      for (MarkerSegment markerSegment : this.markerSequence) {
        if (markerSegment instanceof DQTMarkerSegment)
          arrayList.add(markerSegment); 
      } 
      if (!arrayList.isEmpty() && bool3) {
        boolean bool = false;
        for (DQTMarkerSegment dQTMarkerSegment : arrayList) {
          for (DQTMarkerSegment.Qtable qtable : dQTMarkerSegment.tables) {
            if (qtable.tableID == 1)
              bool = true; 
          } 
        } 
        if (!bool) {
          DQTMarkerSegment.Qtable qtable = null;
          for (DQTMarkerSegment dQTMarkerSegment1 : arrayList) {
            for (DQTMarkerSegment.Qtable qtable1 : dQTMarkerSegment1.tables) {
              if (qtable1.tableID == 0)
                qtable = qtable1; 
            } 
          } 
          DQTMarkerSegment dQTMarkerSegment = (DQTMarkerSegment)arrayList.get(arrayList.size() - 1);
          dQTMarkerSegment.tables.add(dQTMarkerSegment.getChromaForLuma(qtable));
        } 
      } 
    } 
    if (bool6) {
      ArrayList arrayList = new ArrayList();
      for (MarkerSegment markerSegment : this.markerSequence) {
        if (markerSegment instanceof DHTMarkerSegment)
          arrayList.add(markerSegment); 
      } 
      if (!arrayList.isEmpty() && bool3) {
        boolean bool = false;
        for (DHTMarkerSegment dHTMarkerSegment : arrayList) {
          for (DHTMarkerSegment.Htable htable : dHTMarkerSegment.tables) {
            if (htable.tableID == 1)
              bool = true; 
          } 
        } 
        if (!bool) {
          DHTMarkerSegment dHTMarkerSegment = (DHTMarkerSegment)arrayList.get(arrayList.size() - 1);
          dHTMarkerSegment.addHtable(JPEGHuffmanTable.StdDCLuminance, true, 1);
          dHTMarkerSegment.addHtable(JPEGHuffmanTable.StdACLuminance, true, 1);
        } 
      } 
    } 
  }
  
  private boolean wantAlpha(Node paramNode) {
    boolean bool = false;
    Node node = paramNode.getFirstChild();
    if (node.getNodeName().equals("Alpha") && node.hasAttributes()) {
      String str = node.getAttributes().getNamedItem("value").getNodeValue();
      if (!str.equals("none"))
        bool = true; 
    } 
    this.transparencyDone = true;
    return bool;
  }
  
  private void mergeStandardCompressionNode(Node paramNode) throws IIOInvalidTreeException {}
  
  private void mergeStandardDataNode(Node paramNode) throws IIOInvalidTreeException {}
  
  private void mergeStandardDimensionNode(Node paramNode) throws IIOInvalidTreeException {
    JFIFMarkerSegment jFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
    if (jFIFMarkerSegment == null) {
      boolean bool = false;
      SOFMarkerSegment sOFMarkerSegment = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
      if (sOFMarkerSegment != null) {
        int i = sOFMarkerSegment.componentSpecs.length;
        if (i == 1 || i == 3) {
          bool = true;
          for (byte b = 0; b < sOFMarkerSegment.componentSpecs.length; b++) {
            if ((sOFMarkerSegment.componentSpecs[b]).componentId != b + true)
              bool = false; 
          } 
          AdobeMarkerSegment adobeMarkerSegment = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
          if (adobeMarkerSegment != null && adobeMarkerSegment.transform != ((i == 1) ? 0 : 1))
            bool = false; 
        } 
      } 
      if (bool) {
        jFIFMarkerSegment = new JFIFMarkerSegment();
        this.markerSequence.add(0, jFIFMarkerSegment);
      } 
    } 
    if (jFIFMarkerSegment != null) {
      NodeList nodeList = paramNode.getChildNodes();
      for (byte b = 0; b < nodeList.getLength(); b++) {
        Node node = nodeList.item(b);
        NamedNodeMap namedNodeMap = node.getAttributes();
        String str = node.getNodeName();
        if (str.equals("PixelAspectRatio")) {
          String str1 = namedNodeMap.getNamedItem("value").getNodeValue();
          float f = Float.parseFloat(str1);
          Point point = findIntegerRatio(f);
          jFIFMarkerSegment.resUnits = 0;
          jFIFMarkerSegment.Xdensity = point.x;
          jFIFMarkerSegment.Xdensity = point.y;
        } else if (str.equals("HorizontalPixelSize")) {
          String str1 = namedNodeMap.getNamedItem("value").getNodeValue();
          float f = Float.parseFloat(str1);
          int i = (int)Math.round(1.0D / f * 10.0D);
          jFIFMarkerSegment.resUnits = 2;
          jFIFMarkerSegment.Xdensity = i;
        } else if (str.equals("VerticalPixelSize")) {
          String str1 = namedNodeMap.getNamedItem("value").getNodeValue();
          float f = Float.parseFloat(str1);
          int i = (int)Math.round(1.0D / f * 10.0D);
          jFIFMarkerSegment.resUnits = 2;
          jFIFMarkerSegment.Ydensity = i;
        } 
      } 
    } 
  }
  
  private static Point findIntegerRatio(float paramFloat) {
    float f1 = 0.005F;
    paramFloat = Math.abs(paramFloat);
    if (paramFloat <= f1)
      return new Point(1, 255); 
    if (paramFloat >= 255.0F)
      return new Point(255, 1); 
    boolean bool = false;
    if (paramFloat < 1.0D) {
      paramFloat = 1.0F / paramFloat;
      bool = true;
    } 
    byte b = 1;
    int i = Math.round(paramFloat);
    float f2 = i;
    float f3;
    for (f3 = Math.abs(paramFloat - f2); f3 > f1; f3 = Math.abs(paramFloat - f2)) {
      i = Math.round(++b * paramFloat);
      f2 = i / b;
    } 
    return bool ? new Point(b, i) : new Point(i, b);
  }
  
  private void mergeStandardDocumentNode(Node paramNode) throws IIOInvalidTreeException {}
  
  private void mergeStandardTextNode(Node paramNode) throws IIOInvalidTreeException {
    NodeList nodeList = paramNode.getChildNodes();
    for (byte b = 0; b < nodeList.getLength(); b++) {
      Node node1 = nodeList.item(b);
      NamedNodeMap namedNodeMap = node1.getAttributes();
      Node node2 = namedNodeMap.getNamedItem("compression");
      boolean bool = true;
      if (node2 != null) {
        String str = node2.getNodeValue();
        if (!str.equals("none"))
          bool = false; 
      } 
      if (bool) {
        String str = namedNodeMap.getNamedItem("value").getNodeValue();
        COMMarkerSegment cOMMarkerSegment = new COMMarkerSegment(str);
        insertCOMMarkerSegment(cOMMarkerSegment);
      } 
    } 
  }
  
  private void mergeStandardTransparencyNode(Node paramNode) throws IIOInvalidTreeException {
    if (!this.transparencyDone && !this.isStream) {
      boolean bool = wantAlpha(paramNode);
      JFIFMarkerSegment jFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
      AdobeMarkerSegment adobeMarkerSegment = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
      SOFMarkerSegment sOFMarkerSegment = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
      SOSMarkerSegment sOSMarkerSegment = (SOSMarkerSegment)findMarkerSegment(SOSMarkerSegment.class, true);
      if (sOFMarkerSegment != null && sOFMarkerSegment.tag == 194)
        return; 
      if (sOFMarkerSegment != null) {
        int i = sOFMarkerSegment.componentSpecs.length;
        boolean bool1 = (i == 2 || i == 4) ? 1 : 0;
        if (bool1 != bool)
          if (bool) {
            i++;
            if (jFIFMarkerSegment != null)
              this.markerSequence.remove(jFIFMarkerSegment); 
            if (adobeMarkerSegment != null)
              adobeMarkerSegment.transform = 0; 
            SOFMarkerSegment.ComponentSpec[] arrayOfComponentSpec = new SOFMarkerSegment.ComponentSpec[i];
            byte b;
            for (b = 0; b < sOFMarkerSegment.componentSpecs.length; b++)
              arrayOfComponentSpec[b] = sOFMarkerSegment.componentSpecs[b]; 
            b = (byte)(sOFMarkerSegment.componentSpecs[0]).componentId;
            byte b1 = (byte)((b > 1) ? 65 : 4);
            arrayOfComponentSpec[i - 1] = sOFMarkerSegment.getComponentSpec(b1, (sOFMarkerSegment.componentSpecs[0]).HsamplingFactor, (sOFMarkerSegment.componentSpecs[0]).QtableSelector);
            sOFMarkerSegment.componentSpecs = arrayOfComponentSpec;
            SOSMarkerSegment.ScanComponentSpec[] arrayOfScanComponentSpec = new SOSMarkerSegment.ScanComponentSpec[i];
            for (byte b2 = 0; b2 < sOSMarkerSegment.componentSpecs.length; b2++)
              arrayOfScanComponentSpec[b2] = sOSMarkerSegment.componentSpecs[b2]; 
            arrayOfScanComponentSpec[i - 1] = sOSMarkerSegment.getScanComponentSpec(b1, 0);
            sOSMarkerSegment.componentSpecs = arrayOfScanComponentSpec;
          } else {
            SOFMarkerSegment.ComponentSpec[] arrayOfComponentSpec = new SOFMarkerSegment.ComponentSpec[--i];
            for (byte b1 = 0; b1 < i; b1++)
              arrayOfComponentSpec[b1] = sOFMarkerSegment.componentSpecs[b1]; 
            sOFMarkerSegment.componentSpecs = arrayOfComponentSpec;
            SOSMarkerSegment.ScanComponentSpec[] arrayOfScanComponentSpec = new SOSMarkerSegment.ScanComponentSpec[i];
            for (byte b2 = 0; b2 < i; b2++)
              arrayOfScanComponentSpec[b2] = sOSMarkerSegment.componentSpecs[b2]; 
            sOSMarkerSegment.componentSpecs = arrayOfScanComponentSpec;
          }  
      } 
    } 
  }
  
  public void setFromTree(String paramString, Node paramNode) throws IIOInvalidTreeException {
    if (paramString == null)
      throw new IllegalArgumentException("null formatName!"); 
    if (paramNode == null)
      throw new IllegalArgumentException("null root!"); 
    if (this.isStream && paramString.equals("javax_imageio_jpeg_stream_1.0")) {
      setFromNativeTree(paramNode);
    } else if (!this.isStream && paramString.equals("javax_imageio_jpeg_image_1.0")) {
      setFromNativeTree(paramNode);
    } else if (!this.isStream && paramString.equals("javax_imageio_1.0")) {
      super.setFromTree(paramString, paramNode);
    } else {
      throw new IllegalArgumentException("Unsupported format name: " + paramString);
    } 
  }
  
  private void setFromNativeTree(Node paramNode) throws IIOInvalidTreeException {
    if (this.resetSequence == null)
      this.resetSequence = this.markerSequence; 
    this.markerSequence = new ArrayList();
    String str = paramNode.getNodeName();
    if (str != (this.isStream ? "javax_imageio_jpeg_stream_1.0" : "javax_imageio_jpeg_image_1.0"))
      throw new IIOInvalidTreeException("Invalid root node name: " + str, paramNode); 
    if (!this.isStream) {
      if (paramNode.getChildNodes().getLength() != 2)
        throw new IIOInvalidTreeException("JPEGvariety and markerSequence nodes must be present", paramNode); 
      Node node1 = paramNode.getFirstChild();
      if (node1.getChildNodes().getLength() != 0)
        this.markerSequence.add(new JFIFMarkerSegment(node1.getFirstChild())); 
    } 
    Node node = this.isStream ? paramNode : paramNode.getLastChild();
    setFromMarkerSequenceNode(node);
  }
  
  void setFromMarkerSequenceNode(Node paramNode) throws IIOInvalidTreeException {
    NodeList nodeList = paramNode.getChildNodes();
    for (byte b = 0; b < nodeList.getLength(); b++) {
      Node node = nodeList.item(b);
      String str = node.getNodeName();
      if (str.equals("dqt")) {
        this.markerSequence.add(new DQTMarkerSegment(node));
      } else if (str.equals("dht")) {
        this.markerSequence.add(new DHTMarkerSegment(node));
      } else if (str.equals("dri")) {
        this.markerSequence.add(new DRIMarkerSegment(node));
      } else if (str.equals("com")) {
        this.markerSequence.add(new COMMarkerSegment(node));
      } else if (str.equals("app14Adobe")) {
        this.markerSequence.add(new AdobeMarkerSegment(node));
      } else if (str.equals("unknown")) {
        this.markerSequence.add(new MarkerSegment(node));
      } else if (str.equals("sof")) {
        this.markerSequence.add(new SOFMarkerSegment(node));
      } else if (str.equals("sos")) {
        this.markerSequence.add(new SOSMarkerSegment(node));
      } else {
        throw new IIOInvalidTreeException("Invalid " + (this.isStream ? "stream " : "image ") + "child: " + str, node);
      } 
    } 
  }
  
  private boolean isConsistent() {
    SOFMarkerSegment sOFMarkerSegment = (SOFMarkerSegment)findMarkerSegment(SOFMarkerSegment.class, true);
    JFIFMarkerSegment jFIFMarkerSegment = (JFIFMarkerSegment)findMarkerSegment(JFIFMarkerSegment.class, true);
    AdobeMarkerSegment adobeMarkerSegment = (AdobeMarkerSegment)findMarkerSegment(AdobeMarkerSegment.class, true);
    boolean bool = true;
    if (!this.isStream)
      if (sOFMarkerSegment != null) {
        int i = sOFMarkerSegment.componentSpecs.length;
        int j = countScanBands();
        if (j != 0 && j != i)
          bool = false; 
        if (jFIFMarkerSegment != null) {
          if (i != 1 && i != 3)
            bool = false; 
          for (byte b = 0; b < i; b++) {
            if ((sOFMarkerSegment.componentSpecs[b]).componentId != b + true)
              bool = false; 
          } 
          if (adobeMarkerSegment != null && ((i == 1 && adobeMarkerSegment.transform != 0) || (i == 3 && adobeMarkerSegment.transform != 1)))
            bool = false; 
        } 
      } else {
        SOSMarkerSegment sOSMarkerSegment = (SOSMarkerSegment)findMarkerSegment(SOSMarkerSegment.class, true);
        if (jFIFMarkerSegment != null || adobeMarkerSegment != null || sOFMarkerSegment != null || sOSMarkerSegment != null)
          bool = false; 
      }  
    return bool;
  }
  
  private int countScanBands() {
    ArrayList arrayList = new ArrayList();
    for (MarkerSegment markerSegment : this.markerSequence) {
      if (markerSegment instanceof SOSMarkerSegment) {
        SOSMarkerSegment sOSMarkerSegment = (SOSMarkerSegment)markerSegment;
        SOSMarkerSegment.ScanComponentSpec[] arrayOfScanComponentSpec = sOSMarkerSegment.componentSpecs;
        for (byte b = 0; b < arrayOfScanComponentSpec.length; b++) {
          Integer integer = new Integer((arrayOfScanComponentSpec[b]).componentSelector);
          if (!arrayList.contains(integer))
            arrayList.add(integer); 
        } 
      } 
    } 
    return arrayList.size();
  }
  
  void writeToStream(ImageOutputStream paramImageOutputStream, boolean paramBoolean1, boolean paramBoolean2, List paramList, ICC_Profile paramICC_Profile, boolean paramBoolean3, int paramInt, JPEGImageWriter paramJPEGImageWriter) throws IOException {
    if (paramBoolean2) {
      JFIFMarkerSegment.writeDefaultJFIF(paramImageOutputStream, paramList, paramICC_Profile, paramJPEGImageWriter);
      if (!paramBoolean3 && paramInt != -1 && paramInt != 0 && paramInt != 1) {
        paramBoolean3 = true;
        paramJPEGImageWriter.warningOccurred(13);
      } 
    } 
    for (MarkerSegment markerSegment : this.markerSequence) {
      if (markerSegment instanceof JFIFMarkerSegment) {
        if (!paramBoolean1) {
          JFIFMarkerSegment jFIFMarkerSegment = (JFIFMarkerSegment)markerSegment;
          jFIFMarkerSegment.writeWithThumbs(paramImageOutputStream, paramList, paramJPEGImageWriter);
          if (paramICC_Profile != null)
            JFIFMarkerSegment.writeICC(paramICC_Profile, paramImageOutputStream); 
        } 
        continue;
      } 
      if (markerSegment instanceof AdobeMarkerSegment) {
        if (!paramBoolean3) {
          if (paramInt != -1) {
            AdobeMarkerSegment adobeMarkerSegment = (AdobeMarkerSegment)markerSegment.clone();
            adobeMarkerSegment.transform = paramInt;
            adobeMarkerSegment.write(paramImageOutputStream);
            continue;
          } 
          if (paramBoolean2) {
            AdobeMarkerSegment adobeMarkerSegment = (AdobeMarkerSegment)markerSegment;
            if (adobeMarkerSegment.transform == 0 || adobeMarkerSegment.transform == 1) {
              adobeMarkerSegment.write(paramImageOutputStream);
              continue;
            } 
            paramJPEGImageWriter.warningOccurred(13);
            continue;
          } 
          markerSegment.write(paramImageOutputStream);
        } 
        continue;
      } 
      markerSegment.write(paramImageOutputStream);
    } 
  }
  
  public void reset() {
    if (this.resetSequence != null) {
      this.markerSequence = this.resetSequence;
      this.resetSequence = null;
    } 
  }
  
  public void print() {
    for (byte b = 0; b < this.markerSequence.size(); b++) {
      MarkerSegment markerSegment = (MarkerSegment)this.markerSequence.get(b);
      markerSegment.print();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\JPEGMetadata.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */