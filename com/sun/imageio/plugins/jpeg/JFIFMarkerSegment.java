package com.sun.imageio.plugins.jpeg;

import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class JFIFMarkerSegment extends MarkerSegment {
  int majorVersion;
  
  int minorVersion;
  
  int resUnits;
  
  int Xdensity;
  
  int Ydensity;
  
  int thumbWidth;
  
  int thumbHeight;
  
  JFIFThumbRGB thumb = null;
  
  ArrayList extSegments = new ArrayList();
  
  ICCMarkerSegment iccSegment = null;
  
  private static final int THUMB_JPEG = 16;
  
  private static final int THUMB_PALETTE = 17;
  
  private static final int THUMB_UNASSIGNED = 18;
  
  private static final int THUMB_RGB = 19;
  
  private static final int DATA_SIZE = 14;
  
  private static final int ID_SIZE = 5;
  
  private final int MAX_THUMB_WIDTH = 255;
  
  private final int MAX_THUMB_HEIGHT = 255;
  
  private final boolean debug = false;
  
  private boolean inICC = false;
  
  private ICCMarkerSegment tempICCSegment = null;
  
  JFIFMarkerSegment() {
    super(224);
    this.majorVersion = 1;
    this.minorVersion = 2;
    this.resUnits = 0;
    this.Xdensity = 1;
    this.Ydensity = 1;
    this.thumbWidth = 0;
    this.thumbHeight = 0;
  }
  
  JFIFMarkerSegment(JPEGBuffer paramJPEGBuffer) throws IOException {
    super(paramJPEGBuffer);
    paramJPEGBuffer.bufPtr += 5;
    this.majorVersion = paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++];
    this.minorVersion = paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++];
    this.resUnits = paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++];
    this.Xdensity = (paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF) << 8;
    this.Xdensity |= paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF;
    this.Ydensity = (paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF) << 8;
    this.Ydensity |= paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF;
    this.thumbWidth = paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF;
    this.thumbHeight = paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF;
    paramJPEGBuffer.bufAvail -= 14;
    if (this.thumbWidth > 0)
      this.thumb = new JFIFThumbRGB(paramJPEGBuffer, this.thumbWidth, this.thumbHeight); 
  }
  
  JFIFMarkerSegment(Node paramNode) throws IIOInvalidTreeException {
    this();
    updateFromNativeNode(paramNode, true);
  }
  
  protected Object clone() {
    JFIFMarkerSegment jFIFMarkerSegment = (JFIFMarkerSegment)super.clone();
    if (!this.extSegments.isEmpty()) {
      jFIFMarkerSegment.extSegments = new ArrayList();
      for (JFIFExtensionMarkerSegment jFIFExtensionMarkerSegment : this.extSegments)
        jFIFMarkerSegment.extSegments.add(jFIFExtensionMarkerSegment.clone()); 
    } 
    if (this.iccSegment != null)
      jFIFMarkerSegment.iccSegment = (ICCMarkerSegment)this.iccSegment.clone(); 
    return jFIFMarkerSegment;
  }
  
  void addJFXX(JPEGBuffer paramJPEGBuffer, JPEGImageReader paramJPEGImageReader) throws IOException { this.extSegments.add(new JFIFExtensionMarkerSegment(paramJPEGBuffer, paramJPEGImageReader)); }
  
  void addICC(JPEGBuffer paramJPEGBuffer) throws IOException {
    if (!this.inICC) {
      if (this.iccSegment != null)
        throw new IIOException("> 1 ICC APP2 Marker Segment not supported"); 
      this.tempICCSegment = new ICCMarkerSegment(paramJPEGBuffer);
      if (!this.inICC) {
        this.iccSegment = this.tempICCSegment;
        this.tempICCSegment = null;
      } 
    } else if (this.tempICCSegment.addData(paramJPEGBuffer) == true) {
      this.iccSegment = this.tempICCSegment;
      this.tempICCSegment = null;
    } 
  }
  
  void addICC(ICC_ColorSpace paramICC_ColorSpace) throws IOException {
    if (this.iccSegment != null)
      throw new IIOException("> 1 ICC APP2 Marker Segment not supported"); 
    this.iccSegment = new ICCMarkerSegment(paramICC_ColorSpace);
  }
  
  IIOMetadataNode getNativeNode() {
    IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("app0JFIF");
    iIOMetadataNode.setAttribute("majorVersion", Integer.toString(this.majorVersion));
    iIOMetadataNode.setAttribute("minorVersion", Integer.toString(this.minorVersion));
    iIOMetadataNode.setAttribute("resUnits", Integer.toString(this.resUnits));
    iIOMetadataNode.setAttribute("Xdensity", Integer.toString(this.Xdensity));
    iIOMetadataNode.setAttribute("Ydensity", Integer.toString(this.Ydensity));
    iIOMetadataNode.setAttribute("thumbWidth", Integer.toString(this.thumbWidth));
    iIOMetadataNode.setAttribute("thumbHeight", Integer.toString(this.thumbHeight));
    if (!this.extSegments.isEmpty()) {
      IIOMetadataNode iIOMetadataNode1 = new IIOMetadataNode("JFXX");
      iIOMetadataNode.appendChild(iIOMetadataNode1);
      for (JFIFExtensionMarkerSegment jFIFExtensionMarkerSegment : this.extSegments)
        iIOMetadataNode1.appendChild(jFIFExtensionMarkerSegment.getNativeNode()); 
    } 
    if (this.iccSegment != null)
      iIOMetadataNode.appendChild(this.iccSegment.getNativeNode()); 
    return iIOMetadataNode;
  }
  
  void updateFromNativeNode(Node paramNode, boolean paramBoolean) throws IIOInvalidTreeException {
    NamedNodeMap namedNodeMap = paramNode.getAttributes();
    if (namedNodeMap.getLength() > 0) {
      int i = getAttributeValue(paramNode, namedNodeMap, "majorVersion", 0, 255, false);
      this.majorVersion = (i != -1) ? i : this.majorVersion;
      i = getAttributeValue(paramNode, namedNodeMap, "minorVersion", 0, 255, false);
      this.minorVersion = (i != -1) ? i : this.minorVersion;
      i = getAttributeValue(paramNode, namedNodeMap, "resUnits", 0, 2, false);
      this.resUnits = (i != -1) ? i : this.resUnits;
      i = getAttributeValue(paramNode, namedNodeMap, "Xdensity", 1, 65535, false);
      this.Xdensity = (i != -1) ? i : this.Xdensity;
      i = getAttributeValue(paramNode, namedNodeMap, "Ydensity", 1, 65535, false);
      this.Ydensity = (i != -1) ? i : this.Ydensity;
      i = getAttributeValue(paramNode, namedNodeMap, "thumbWidth", 0, 255, false);
      this.thumbWidth = (i != -1) ? i : this.thumbWidth;
      i = getAttributeValue(paramNode, namedNodeMap, "thumbHeight", 0, 255, false);
      this.thumbHeight = (i != -1) ? i : this.thumbHeight;
    } 
    if (paramNode.hasChildNodes()) {
      NodeList nodeList = paramNode.getChildNodes();
      int i = nodeList.getLength();
      if (i > 2)
        throw new IIOInvalidTreeException("app0JFIF node cannot have > 2 children", paramNode); 
      for (byte b = 0; b < i; b++) {
        Node node = nodeList.item(b);
        String str = node.getNodeName();
        if (str.equals("JFXX")) {
          if (!this.extSegments.isEmpty() && paramBoolean)
            throw new IIOInvalidTreeException("app0JFIF node cannot have > 1 JFXX node", paramNode); 
          NodeList nodeList1 = node.getChildNodes();
          int j = nodeList1.getLength();
          for (byte b1 = 0; b1 < j; b1++) {
            Node node1 = nodeList1.item(b1);
            this.extSegments.add(new JFIFExtensionMarkerSegment(node1));
          } 
        } 
        if (str.equals("app2ICC")) {
          if (this.iccSegment != null && paramBoolean)
            throw new IIOInvalidTreeException("> 1 ICC APP2 Marker Segment not supported", paramNode); 
          this.iccSegment = new ICCMarkerSegment(node);
        } 
      } 
    } 
  }
  
  int getThumbnailWidth(int paramInt) {
    if (this.thumb != null) {
      if (paramInt == 0)
        return this.thumb.getWidth(); 
      paramInt--;
    } 
    JFIFExtensionMarkerSegment jFIFExtensionMarkerSegment = (JFIFExtensionMarkerSegment)this.extSegments.get(paramInt);
    return jFIFExtensionMarkerSegment.thumb.getWidth();
  }
  
  int getThumbnailHeight(int paramInt) {
    if (this.thumb != null) {
      if (paramInt == 0)
        return this.thumb.getHeight(); 
      paramInt--;
    } 
    JFIFExtensionMarkerSegment jFIFExtensionMarkerSegment = (JFIFExtensionMarkerSegment)this.extSegments.get(paramInt);
    return jFIFExtensionMarkerSegment.thumb.getHeight();
  }
  
  BufferedImage getThumbnail(ImageInputStream paramImageInputStream, int paramInt, JPEGImageReader paramJPEGImageReader) throws IOException {
    paramJPEGImageReader.thumbnailStarted(paramInt);
    BufferedImage bufferedImage = null;
    if (this.thumb != null && paramInt == 0) {
      bufferedImage = this.thumb.getThumbnail(paramImageInputStream, paramJPEGImageReader);
    } else {
      if (this.thumb != null)
        paramInt--; 
      JFIFExtensionMarkerSegment jFIFExtensionMarkerSegment = (JFIFExtensionMarkerSegment)this.extSegments.get(paramInt);
      bufferedImage = jFIFExtensionMarkerSegment.thumb.getThumbnail(paramImageInputStream, paramJPEGImageReader);
    } 
    paramJPEGImageReader.thumbnailComplete();
    return bufferedImage;
  }
  
  void write(ImageOutputStream paramImageOutputStream, JPEGImageWriter paramJPEGImageWriter) throws IOException { write(paramImageOutputStream, null, paramJPEGImageWriter); }
  
  void write(ImageOutputStream paramImageOutputStream, BufferedImage paramBufferedImage, JPEGImageWriter paramJPEGImageWriter) throws IOException {
    int i = 0;
    int j = 0;
    int k = 0;
    int[] arrayOfInt = null;
    if (paramBufferedImage != null) {
      i = paramBufferedImage.getWidth();
      j = paramBufferedImage.getHeight();
      if (i > 255 || j > 255)
        paramJPEGImageWriter.warningOccurred(12); 
      i = Math.min(i, 255);
      j = Math.min(j, 255);
      arrayOfInt = paramBufferedImage.getRaster().getPixels(0, 0, i, j, (int[])null);
      k = arrayOfInt.length;
    } 
    this.length = 16 + k;
    writeTag(paramImageOutputStream);
    byte[] arrayOfByte = { 74, 70, 73, 70, 0 };
    paramImageOutputStream.write(arrayOfByte);
    paramImageOutputStream.write(this.majorVersion);
    paramImageOutputStream.write(this.minorVersion);
    paramImageOutputStream.write(this.resUnits);
    write2bytes(paramImageOutputStream, this.Xdensity);
    write2bytes(paramImageOutputStream, this.Ydensity);
    paramImageOutputStream.write(i);
    paramImageOutputStream.write(j);
    if (arrayOfInt != null) {
      paramJPEGImageWriter.thumbnailStarted(0);
      writeThumbnailData(paramImageOutputStream, arrayOfInt, paramJPEGImageWriter);
      paramJPEGImageWriter.thumbnailComplete();
    } 
  }
  
  void writeThumbnailData(ImageOutputStream paramImageOutputStream, int[] paramArrayOfInt, JPEGImageWriter paramJPEGImageWriter) throws IOException {
    int i = paramArrayOfInt.length / 20;
    if (i == 0)
      i = 1; 
    for (int j = 0; j < paramArrayOfInt.length; j++) {
      paramImageOutputStream.write(paramArrayOfInt[j]);
      if (j > i && j % i == 0)
        paramJPEGImageWriter.thumbnailProgress(j * 100.0F / paramArrayOfInt.length); 
    } 
  }
  
  void writeWithThumbs(ImageOutputStream paramImageOutputStream, List paramList, JPEGImageWriter paramJPEGImageWriter) throws IOException {
    if (paramList != null) {
      JFIFExtensionMarkerSegment jFIFExtensionMarkerSegment = null;
      if (paramList.size() == 1) {
        if (!this.extSegments.isEmpty())
          jFIFExtensionMarkerSegment = (JFIFExtensionMarkerSegment)this.extSegments.get(0); 
        writeThumb(paramImageOutputStream, (BufferedImage)paramList.get(0), jFIFExtensionMarkerSegment, 0, true, paramJPEGImageWriter);
      } else {
        write(paramImageOutputStream, paramJPEGImageWriter);
        for (byte b = 0; b < paramList.size(); b++) {
          jFIFExtensionMarkerSegment = null;
          if (b < this.extSegments.size())
            jFIFExtensionMarkerSegment = (JFIFExtensionMarkerSegment)this.extSegments.get(b); 
          writeThumb(paramImageOutputStream, (BufferedImage)paramList.get(b), jFIFExtensionMarkerSegment, b, false, paramJPEGImageWriter);
        } 
      } 
    } else {
      write(paramImageOutputStream, paramJPEGImageWriter);
    } 
  }
  
  private void writeThumb(ImageOutputStream paramImageOutputStream, BufferedImage paramBufferedImage, JFIFExtensionMarkerSegment paramJFIFExtensionMarkerSegment, int paramInt, boolean paramBoolean, JPEGImageWriter paramJPEGImageWriter) throws IOException {
    ColorModel colorModel = paramBufferedImage.getColorModel();
    ColorSpace colorSpace = colorModel.getColorSpace();
    if (colorModel instanceof IndexColorModel) {
      if (paramBoolean)
        write(paramImageOutputStream, paramJPEGImageWriter); 
      if (paramJFIFExtensionMarkerSegment == null || paramJFIFExtensionMarkerSegment.code == 17) {
        writeJFXXSegment(paramInt, paramBufferedImage, paramImageOutputStream, paramJPEGImageWriter);
      } else {
        BufferedImage bufferedImage = ((IndexColorModel)colorModel).convertToIntDiscrete(paramBufferedImage.getRaster(), false);
        paramJFIFExtensionMarkerSegment.setThumbnail(bufferedImage);
        paramJPEGImageWriter.thumbnailStarted(paramInt);
        paramJFIFExtensionMarkerSegment.write(paramImageOutputStream, paramJPEGImageWriter);
        paramJPEGImageWriter.thumbnailComplete();
      } 
    } else if (colorSpace.getType() == 5) {
      if (paramJFIFExtensionMarkerSegment == null) {
        if (paramBoolean) {
          write(paramImageOutputStream, paramBufferedImage, paramJPEGImageWriter);
        } else {
          writeJFXXSegment(paramInt, paramBufferedImage, paramImageOutputStream, paramJPEGImageWriter);
        } 
      } else {
        if (paramBoolean)
          write(paramImageOutputStream, paramJPEGImageWriter); 
        if (paramJFIFExtensionMarkerSegment.code == 17) {
          writeJFXXSegment(paramInt, paramBufferedImage, paramImageOutputStream, paramJPEGImageWriter);
          paramJPEGImageWriter.warningOccurred(14);
        } else {
          paramJFIFExtensionMarkerSegment.setThumbnail(paramBufferedImage);
          paramJPEGImageWriter.thumbnailStarted(paramInt);
          paramJFIFExtensionMarkerSegment.write(paramImageOutputStream, paramJPEGImageWriter);
          paramJPEGImageWriter.thumbnailComplete();
        } 
      } 
    } else if (colorSpace.getType() == 6) {
      if (paramJFIFExtensionMarkerSegment == null) {
        if (paramBoolean) {
          BufferedImage bufferedImage = expandGrayThumb(paramBufferedImage);
          write(paramImageOutputStream, bufferedImage, paramJPEGImageWriter);
        } else {
          writeJFXXSegment(paramInt, paramBufferedImage, paramImageOutputStream, paramJPEGImageWriter);
        } 
      } else {
        if (paramBoolean)
          write(paramImageOutputStream, paramJPEGImageWriter); 
        if (paramJFIFExtensionMarkerSegment.code == 19) {
          BufferedImage bufferedImage = expandGrayThumb(paramBufferedImage);
          writeJFXXSegment(paramInt, bufferedImage, paramImageOutputStream, paramJPEGImageWriter);
        } else if (paramJFIFExtensionMarkerSegment.code == 16) {
          paramJFIFExtensionMarkerSegment.setThumbnail(paramBufferedImage);
          paramJPEGImageWriter.thumbnailStarted(paramInt);
          paramJFIFExtensionMarkerSegment.write(paramImageOutputStream, paramJPEGImageWriter);
          paramJPEGImageWriter.thumbnailComplete();
        } else if (paramJFIFExtensionMarkerSegment.code == 17) {
          writeJFXXSegment(paramInt, paramBufferedImage, paramImageOutputStream, paramJPEGImageWriter);
          paramJPEGImageWriter.warningOccurred(15);
        } 
      } 
    } else {
      paramJPEGImageWriter.warningOccurred(9);
    } 
  }
  
  private void writeJFXXSegment(int paramInt, BufferedImage paramBufferedImage, ImageOutputStream paramImageOutputStream, JPEGImageWriter paramJPEGImageWriter) throws IOException {
    JFIFExtensionMarkerSegment jFIFExtensionMarkerSegment = null;
    try {
      jFIFExtensionMarkerSegment = new JFIFExtensionMarkerSegment(paramBufferedImage);
    } catch (IllegalThumbException illegalThumbException) {
      paramJPEGImageWriter.warningOccurred(9);
      return;
    } 
    paramJPEGImageWriter.thumbnailStarted(paramInt);
    jFIFExtensionMarkerSegment.write(paramImageOutputStream, paramJPEGImageWriter);
    paramJPEGImageWriter.thumbnailComplete();
  }
  
  private static BufferedImage expandGrayThumb(BufferedImage paramBufferedImage) {
    BufferedImage bufferedImage = new BufferedImage(paramBufferedImage.getWidth(), paramBufferedImage.getHeight(), 1);
    Graphics graphics = bufferedImage.getGraphics();
    graphics.drawImage(paramBufferedImage, 0, 0, null);
    return bufferedImage;
  }
  
  static void writeDefaultJFIF(ImageOutputStream paramImageOutputStream, List paramList, ICC_Profile paramICC_Profile, JPEGImageWriter paramJPEGImageWriter) throws IOException {
    JFIFMarkerSegment jFIFMarkerSegment = new JFIFMarkerSegment();
    jFIFMarkerSegment.writeWithThumbs(paramImageOutputStream, paramList, paramJPEGImageWriter);
    if (paramICC_Profile != null)
      writeICC(paramICC_Profile, paramImageOutputStream); 
  }
  
  void print() {
    printTag("JFIF");
    System.out.print("Version ");
    System.out.print(this.majorVersion);
    System.out.println(".0" + Integer.toString(this.minorVersion));
    System.out.print("Resolution units: ");
    System.out.println(this.resUnits);
    System.out.print("X density: ");
    System.out.println(this.Xdensity);
    System.out.print("Y density: ");
    System.out.println(this.Ydensity);
    System.out.print("Thumbnail Width: ");
    System.out.println(this.thumbWidth);
    System.out.print("Thumbnail Height: ");
    System.out.println(this.thumbHeight);
    if (!this.extSegments.isEmpty())
      for (JFIFExtensionMarkerSegment jFIFExtensionMarkerSegment : this.extSegments)
        jFIFExtensionMarkerSegment.print();  
    if (this.iccSegment != null)
      this.iccSegment.print(); 
  }
  
  static void writeICC(ICC_Profile paramICC_Profile, ImageOutputStream paramImageOutputStream) throws IOException {
    int i = 2;
    int j = "ICC_PROFILE".length() + 1;
    int k = 2;
    int m = '￿' - i - j - k;
    byte[] arrayOfByte = paramICC_Profile.getData();
    int n = arrayOfByte.length / m;
    if (arrayOfByte.length % m != 0)
      n++; 
    byte b1 = 1;
    int i1 = 0;
    for (byte b2 = 0; b2 < n; b2++) {
      int i2 = Math.min(arrayOfByte.length - i1, m);
      int i3 = i2 + k + j + i;
      paramImageOutputStream.write(255);
      paramImageOutputStream.write(226);
      MarkerSegment.write2bytes(paramImageOutputStream, i3);
      byte[] arrayOfByte1 = "ICC_PROFILE".getBytes("US-ASCII");
      paramImageOutputStream.write(arrayOfByte1);
      paramImageOutputStream.write(0);
      paramImageOutputStream.write(b1++);
      paramImageOutputStream.write(n);
      paramImageOutputStream.write(arrayOfByte, i1, i2);
      i1 += i2;
    } 
  }
  
  class ICCMarkerSegment extends MarkerSegment {
    ArrayList chunks = null;
    
    byte[] profile = null;
    
    private static final int ID_SIZE = 12;
    
    int chunksRead;
    
    int numChunks;
    
    ICCMarkerSegment(ICC_ColorSpace param1ICC_ColorSpace) {
      super(226);
      this.chunks = null;
      this.chunksRead = 0;
      this.numChunks = 0;
      this.profile = param1ICC_ColorSpace.getProfile().getData();
    }
    
    ICCMarkerSegment(JPEGBuffer param1JPEGBuffer) throws IOException {
      super(param1JPEGBuffer);
      param1JPEGBuffer.bufPtr += 12;
      param1JPEGBuffer.bufAvail -= 12;
      this.length -= 12;
      byte b = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr] & 0xFF;
      this.numChunks = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr + 1] & 0xFF;
      if (b > this.numChunks)
        throw new IIOException("Image format Error; chunk num > num chunks"); 
      if (this.numChunks == 1) {
        this.length -= 2;
        this.profile = new byte[this.length];
        param1JPEGBuffer.bufPtr += 2;
        param1JPEGBuffer.bufAvail -= 2;
        param1JPEGBuffer.readData(this.profile);
        this$0.inICC = false;
      } else {
        byte[] arrayOfByte = new byte[this.length];
        this.length -= 2;
        param1JPEGBuffer.readData(arrayOfByte);
        this.chunks = new ArrayList();
        this.chunks.add(arrayOfByte);
        this.chunksRead = 1;
        this$0.inICC = true;
      } 
    }
    
    ICCMarkerSegment(Node param1Node) throws IIOInvalidTreeException {
      super(226);
      if (param1Node instanceof IIOMetadataNode) {
        IIOMetadataNode iIOMetadataNode = (IIOMetadataNode)param1Node;
        ICC_Profile iCC_Profile = (ICC_Profile)iIOMetadataNode.getUserObject();
        if (iCC_Profile != null)
          this.profile = iCC_Profile.getData(); 
      } 
    }
    
    protected Object clone() {
      ICCMarkerSegment iCCMarkerSegment = (ICCMarkerSegment)super.clone();
      if (this.profile != null)
        iCCMarkerSegment.profile = (byte[])this.profile.clone(); 
      return iCCMarkerSegment;
    }
    
    boolean addData(JPEGBuffer param1JPEGBuffer) throws IOException {
      param1JPEGBuffer.bufPtr++;
      param1JPEGBuffer.bufAvail--;
      byte b1 = (param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr++] & 0xFF) << 8;
      b1 |= param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr++] & 0xFF;
      param1JPEGBuffer.bufAvail -= 2;
      b1 -= 2;
      param1JPEGBuffer.bufPtr += 12;
      param1JPEGBuffer.bufAvail -= 12;
      b1 -= 12;
      byte b2 = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr] & 0xFF;
      if (b2 > this.numChunks)
        throw new IIOException("Image format Error; chunk num > num chunks"); 
      byte b3 = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr + 1] & 0xFF;
      if (this.numChunks != b3)
        throw new IIOException("Image format Error; icc num chunks mismatch"); 
      b1 -= 2;
      boolean bool = false;
      byte[] arrayOfByte = new byte[b1];
      param1JPEGBuffer.readData(arrayOfByte);
      this.chunks.add(arrayOfByte);
      this.length += b1;
      this.chunksRead++;
      if (this.chunksRead < this.numChunks) {
        JFIFMarkerSegment.this.inICC = true;
      } else {
        this.profile = new byte[this.length];
        int i = 0;
        for (byte b = 1; b <= this.numChunks; b++) {
          boolean bool1 = false;
          for (byte b4 = 0; b4 < this.chunks.size(); b4++) {
            byte[] arrayOfByte1 = (byte[])this.chunks.get(b4);
            if (arrayOfByte1[0] == b) {
              System.arraycopy(arrayOfByte1, 2, this.profile, i, arrayOfByte1.length - 2);
              i += arrayOfByte1.length - 2;
              bool1 = true;
            } 
          } 
          if (!bool1)
            throw new IIOException("Image Format Error: Missing ICC chunk num " + b); 
        } 
        this.chunks = null;
        this.chunksRead = 0;
        this.numChunks = 0;
        JFIFMarkerSegment.this.inICC = false;
        bool = true;
      } 
      return bool;
    }
    
    IIOMetadataNode getNativeNode() {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("app2ICC");
      if (this.profile != null)
        iIOMetadataNode.setUserObject(ICC_Profile.getInstance(this.profile)); 
      return iIOMetadataNode;
    }
    
    void write(ImageOutputStream param1ImageOutputStream) throws IOException {}
    
    void print() { printTag("ICC Profile APP2"); }
  }
  
  private class IllegalThumbException extends Exception {
    private IllegalThumbException() {}
  }
  
  class JFIFExtensionMarkerSegment extends MarkerSegment {
    int code;
    
    JFIFMarkerSegment.JFIFThumb thumb;
    
    private static final int DATA_SIZE = 6;
    
    private static final int ID_SIZE = 5;
    
    JFIFExtensionMarkerSegment(JPEGBuffer param1JPEGBuffer, JPEGImageReader param1JPEGImageReader) throws IOException {
      super(param1JPEGBuffer);
      param1JPEGBuffer.bufPtr += 5;
      this.code = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr++] & 0xFF;
      param1JPEGBuffer.bufAvail -= 6;
      if (this.code == 16) {
        this.thumb = new JFIFMarkerSegment.JFIFThumbJPEG(this$0, param1JPEGBuffer, this.length, param1JPEGImageReader);
      } else {
        param1JPEGBuffer.loadBuf(2);
        byte b1 = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr++] & 0xFF;
        byte b2 = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr++] & 0xFF;
        param1JPEGBuffer.bufAvail -= 2;
        if (this.code == 17) {
          this.thumb = new JFIFMarkerSegment.JFIFThumbPalette(this$0, param1JPEGBuffer, b1, b2);
        } else {
          this.thumb = new JFIFMarkerSegment.JFIFThumbRGB(this$0, param1JPEGBuffer, b1, b2);
        } 
      } 
    }
    
    JFIFExtensionMarkerSegment(Node param1Node) throws IIOInvalidTreeException {
      super(224);
      NamedNodeMap namedNodeMap = param1Node.getAttributes();
      if (namedNodeMap.getLength() > 0) {
        this.code = getAttributeValue(param1Node, namedNodeMap, "extensionCode", 16, 19, false);
        if (this.code == 18)
          throw new IIOInvalidTreeException("invalid extensionCode attribute value", param1Node); 
      } else {
        this.code = 18;
      } 
      if (param1Node.getChildNodes().getLength() != 1)
        throw new IIOInvalidTreeException("app0JFXX node must have exactly 1 child", param1Node); 
      Node node = param1Node.getFirstChild();
      String str = node.getNodeName();
      if (str.equals("JFIFthumbJPEG")) {
        if (this.code == 18)
          this.code = 16; 
        this.thumb = new JFIFMarkerSegment.JFIFThumbJPEG(this$0, node);
      } else if (str.equals("JFIFthumbPalette")) {
        if (this.code == 18)
          this.code = 17; 
        this.thumb = new JFIFMarkerSegment.JFIFThumbPalette(this$0, node);
      } else if (str.equals("JFIFthumbRGB")) {
        if (this.code == 18)
          this.code = 19; 
        this.thumb = new JFIFMarkerSegment.JFIFThumbRGB(this$0, node);
      } else {
        throw new IIOInvalidTreeException("unrecognized app0JFXX child node", param1Node);
      } 
    }
    
    JFIFExtensionMarkerSegment(BufferedImage param1BufferedImage) throws JFIFMarkerSegment.IllegalThumbException {
      super(224);
      ColorModel colorModel = param1BufferedImage.getColorModel();
      int i = colorModel.getColorSpace().getType();
      if (colorModel.hasAlpha())
        throw new JFIFMarkerSegment.IllegalThumbException(this$0, null); 
      if (colorModel instanceof IndexColorModel) {
        this.code = 17;
        this.thumb = new JFIFMarkerSegment.JFIFThumbPalette(this$0, param1BufferedImage);
      } else if (i == 5) {
        this.code = 19;
        this.thumb = new JFIFMarkerSegment.JFIFThumbRGB(this$0, param1BufferedImage);
      } else if (i == 6) {
        this.code = 16;
        this.thumb = new JFIFMarkerSegment.JFIFThumbJPEG(this$0, param1BufferedImage);
      } else {
        throw new JFIFMarkerSegment.IllegalThumbException(this$0, null);
      } 
    }
    
    void setThumbnail(BufferedImage param1BufferedImage) {
      try {
        switch (this.code) {
          case 17:
            this.thumb = new JFIFMarkerSegment.JFIFThumbPalette(JFIFMarkerSegment.this, param1BufferedImage);
            break;
          case 19:
            this.thumb = new JFIFMarkerSegment.JFIFThumbRGB(JFIFMarkerSegment.this, param1BufferedImage);
            break;
          case 16:
            this.thumb = new JFIFMarkerSegment.JFIFThumbJPEG(JFIFMarkerSegment.this, param1BufferedImage);
            break;
        } 
      } catch (IllegalThumbException illegalThumbException) {
        throw new InternalError("Illegal thumb in setThumbnail!", illegalThumbException);
      } 
    }
    
    protected Object clone() {
      JFIFExtensionMarkerSegment jFIFExtensionMarkerSegment = (JFIFExtensionMarkerSegment)super.clone();
      if (this.thumb != null)
        jFIFExtensionMarkerSegment.thumb = (JFIFMarkerSegment.JFIFThumb)this.thumb.clone(); 
      return jFIFExtensionMarkerSegment;
    }
    
    IIOMetadataNode getNativeNode() {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("app0JFXX");
      iIOMetadataNode.setAttribute("extensionCode", Integer.toString(this.code));
      iIOMetadataNode.appendChild(this.thumb.getNativeNode());
      return iIOMetadataNode;
    }
    
    void write(ImageOutputStream param1ImageOutputStream, JPEGImageWriter param1JPEGImageWriter) throws IOException {
      this.length = 8 + this.thumb.getLength();
      writeTag(param1ImageOutputStream);
      byte[] arrayOfByte = { 74, 70, 88, 88, 0 };
      param1ImageOutputStream.write(arrayOfByte);
      param1ImageOutputStream.write(this.code);
      this.thumb.write(param1ImageOutputStream, param1JPEGImageWriter);
    }
    
    void print() {
      printTag("JFXX");
      this.thumb.print();
    }
  }
  
  abstract class JFIFThumb implements Cloneable {
    long streamPos = -1L;
    
    abstract int getLength();
    
    abstract int getWidth();
    
    abstract int getHeight();
    
    abstract BufferedImage getThumbnail(ImageInputStream param1ImageInputStream, JPEGImageReader param1JPEGImageReader) throws IOException;
    
    protected JFIFThumb() {}
    
    protected JFIFThumb(JPEGBuffer param1JPEGBuffer) throws IOException { this.streamPos = param1JPEGBuffer.getStreamPosition(); }
    
    abstract void print();
    
    abstract IIOMetadataNode getNativeNode();
    
    abstract void write(ImageOutputStream param1ImageOutputStream, JPEGImageWriter param1JPEGImageWriter) throws IOException;
    
    protected Object clone() {
      try {
        return super.clone();
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        return null;
      } 
    }
  }
  
  class JFIFThumbJPEG extends JFIFThumb {
    JPEGMetadata thumbMetadata = null;
    
    byte[] data = null;
    
    private static final int PREAMBLE_SIZE = 6;
    
    JFIFThumbJPEG(JPEGBuffer param1JPEGBuffer, int param1Int, JPEGImageReader param1JPEGImageReader) throws IOException {
      super(JFIFMarkerSegment.this, param1JPEGBuffer);
      long l = this.streamPos + (param1Int - 6);
      param1JPEGBuffer.iis.seek(this.streamPos);
      this.thumbMetadata = new JPEGMetadata(false, true, param1JPEGBuffer.iis, param1JPEGImageReader);
      param1JPEGBuffer.iis.seek(l);
      param1JPEGBuffer.bufAvail = 0;
      param1JPEGBuffer.bufPtr = 0;
    }
    
    JFIFThumbJPEG(Node param1Node) throws IIOInvalidTreeException {
      super(JFIFMarkerSegment.this);
      if (param1Node.getChildNodes().getLength() > 1)
        throw new IIOInvalidTreeException("JFIFThumbJPEG node must have 0 or 1 child", param1Node); 
      Node node = param1Node.getFirstChild();
      if (node != null) {
        String str = node.getNodeName();
        if (!str.equals("markerSequence"))
          throw new IIOInvalidTreeException("JFIFThumbJPEG child must be a markerSequence node", param1Node); 
        this.thumbMetadata = new JPEGMetadata(false, true);
        this.thumbMetadata.setFromMarkerSequenceNode(node);
      } 
    }
    
    JFIFThumbJPEG(BufferedImage param1BufferedImage) throws JFIFMarkerSegment.IllegalThumbException {
      super(JFIFMarkerSegment.this);
      char c = 'က';
      char c1 = '￷';
      try {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(c);
        MemoryCacheImageOutputStream memoryCacheImageOutputStream = new MemoryCacheImageOutputStream(byteArrayOutputStream);
        JPEGImageWriter jPEGImageWriter = new JPEGImageWriter(null);
        jPEGImageWriter.setOutput(memoryCacheImageOutputStream);
        JPEGMetadata jPEGMetadata = (JPEGMetadata)jPEGImageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(param1BufferedImage), null);
        MarkerSegment markerSegment = jPEGMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
        if (markerSegment == null)
          throw new JFIFMarkerSegment.IllegalThumbException(null); 
        jPEGMetadata.markerSequence.remove(markerSegment);
        jPEGImageWriter.write(new IIOImage(param1BufferedImage, null, jPEGMetadata));
        jPEGImageWriter.dispose();
        if (byteArrayOutputStream.size() > c1)
          throw new JFIFMarkerSegment.IllegalThumbException(null); 
        this.data = byteArrayOutputStream.toByteArray();
      } catch (IOException iOException) {
        throw new JFIFMarkerSegment.IllegalThumbException(null);
      } 
    }
    
    int getWidth() {
      int i = 0;
      SOFMarkerSegment sOFMarkerSegment = (SOFMarkerSegment)this.thumbMetadata.findMarkerSegment(SOFMarkerSegment.class, true);
      if (sOFMarkerSegment != null)
        i = sOFMarkerSegment.samplesPerLine; 
      return i;
    }
    
    int getHeight() {
      int i = 0;
      SOFMarkerSegment sOFMarkerSegment = (SOFMarkerSegment)this.thumbMetadata.findMarkerSegment(SOFMarkerSegment.class, true);
      if (sOFMarkerSegment != null)
        i = sOFMarkerSegment.numLines; 
      return i;
    }
    
    BufferedImage getThumbnail(ImageInputStream param1ImageInputStream, JPEGImageReader param1JPEGImageReader) throws IOException {
      param1ImageInputStream.mark();
      param1ImageInputStream.seek(this.streamPos);
      JPEGImageReader jPEGImageReader = new JPEGImageReader(null);
      jPEGImageReader.setInput(param1ImageInputStream);
      jPEGImageReader.addIIOReadProgressListener(new ThumbnailReadListener(param1JPEGImageReader));
      BufferedImage bufferedImage = jPEGImageReader.read(0, null);
      jPEGImageReader.dispose();
      param1ImageInputStream.reset();
      return bufferedImage;
    }
    
    protected Object clone() {
      JFIFThumbJPEG jFIFThumbJPEG = (JFIFThumbJPEG)super.clone();
      if (this.thumbMetadata != null)
        jFIFThumbJPEG.thumbMetadata = (JPEGMetadata)this.thumbMetadata.clone(); 
      return jFIFThumbJPEG;
    }
    
    IIOMetadataNode getNativeNode() {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("JFIFthumbJPEG");
      if (this.thumbMetadata != null)
        iIOMetadataNode.appendChild(this.thumbMetadata.getNativeTree()); 
      return iIOMetadataNode;
    }
    
    int getLength() { return (this.data == null) ? 0 : this.data.length; }
    
    void write(ImageOutputStream param1ImageOutputStream, JPEGImageWriter param1JPEGImageWriter) throws IOException {
      int i = this.data.length / 20;
      if (i == 0)
        i = 1; 
      int j = 0;
      while (j < this.data.length) {
        int k = Math.min(i, this.data.length - j);
        param1ImageOutputStream.write(this.data, j, k);
        j += i;
        float f = j * 100.0F / this.data.length;
        if (f > 100.0F)
          f = 100.0F; 
        param1JPEGImageWriter.thumbnailProgress(f);
      } 
    }
    
    void print() { System.out.println("JFIF thumbnail stored as JPEG"); }
    
    private class ThumbnailReadListener implements IIOReadProgressListener {
      JPEGImageReader reader = null;
      
      ThumbnailReadListener(JPEGImageReader param2JPEGImageReader) { this.reader = param2JPEGImageReader; }
      
      public void sequenceStarted(ImageReader param2ImageReader, int param2Int) {}
      
      public void sequenceComplete(ImageReader param2ImageReader) {}
      
      public void imageStarted(ImageReader param2ImageReader, int param2Int) {}
      
      public void imageProgress(ImageReader param2ImageReader, float param2Float) { this.reader.thumbnailProgress(param2Float); }
      
      public void imageComplete(ImageReader param2ImageReader) {}
      
      public void thumbnailStarted(ImageReader param2ImageReader, int param2Int1, int param2Int2) {}
      
      public void thumbnailProgress(ImageReader param2ImageReader, float param2Float) {}
      
      public void thumbnailComplete(ImageReader param2ImageReader) {}
      
      public void readAborted(ImageReader param2ImageReader) {}
    }
  }
  
  class JFIFThumbPalette extends JFIFThumbUncompressed {
    private static final int PALETTE_SIZE = 768;
    
    JFIFThumbPalette(JPEGBuffer param1JPEGBuffer, int param1Int1, int param1Int2) throws IOException { super(JFIFMarkerSegment.this, param1JPEGBuffer, param1Int1, param1Int2, 768 + param1Int1 * param1Int2, "JFIFThumbPalette"); }
    
    JFIFThumbPalette(Node param1Node) throws IIOInvalidTreeException { super(JFIFMarkerSegment.this, param1Node, "JFIFThumbPalette"); }
    
    JFIFThumbPalette(BufferedImage param1BufferedImage) throws JFIFMarkerSegment.IllegalThumbException {
      super(JFIFMarkerSegment.this, param1BufferedImage);
      IndexColorModel indexColorModel = (IndexColorModel)this.thumbnail.getColorModel();
      if (indexColorModel.getMapSize() > 256)
        throw new JFIFMarkerSegment.IllegalThumbException(this$0, null); 
    }
    
    int getLength() { return this.thumbWidth * this.thumbHeight + 768; }
    
    BufferedImage getThumbnail(ImageInputStream param1ImageInputStream, JPEGImageReader param1JPEGImageReader) throws IOException {
      param1ImageInputStream.mark();
      param1ImageInputStream.seek(this.streamPos);
      byte[] arrayOfByte = new byte[768];
      float f = 768.0F / getLength();
      readByteBuffer(param1ImageInputStream, arrayOfByte, param1JPEGImageReader, f, 0.0F);
      DataBufferByte dataBufferByte = new DataBufferByte(this.thumbWidth * this.thumbHeight);
      readByteBuffer(param1ImageInputStream, dataBufferByte.getData(), param1JPEGImageReader, 1.0F - f, f);
      param1ImageInputStream.read();
      param1ImageInputStream.reset();
      IndexColorModel indexColorModel = new IndexColorModel(8, 256, arrayOfByte, 0, false);
      SampleModel sampleModel = indexColorModel.createCompatibleSampleModel(this.thumbWidth, this.thumbHeight);
      WritableRaster writableRaster = Raster.createWritableRaster(sampleModel, dataBufferByte, null);
      return new BufferedImage(indexColorModel, writableRaster, false, null);
    }
    
    void write(ImageOutputStream param1ImageOutputStream, JPEGImageWriter param1JPEGImageWriter) throws IOException {
      super.write(param1ImageOutputStream, param1JPEGImageWriter);
      byte[] arrayOfByte1 = new byte[768];
      IndexColorModel indexColorModel = (IndexColorModel)this.thumbnail.getColorModel();
      byte[] arrayOfByte2 = new byte[256];
      byte[] arrayOfByte3 = new byte[256];
      byte[] arrayOfByte4 = new byte[256];
      indexColorModel.getReds(arrayOfByte2);
      indexColorModel.getGreens(arrayOfByte3);
      indexColorModel.getBlues(arrayOfByte4);
      for (byte b = 0; b < 'Ā'; b++) {
        arrayOfByte1[b * 3] = arrayOfByte2[b];
        arrayOfByte1[b * 3 + 1] = arrayOfByte3[b];
        arrayOfByte1[b * 3 + 2] = arrayOfByte4[b];
      } 
      param1ImageOutputStream.write(arrayOfByte1);
      writePixels(param1ImageOutputStream, param1JPEGImageWriter);
    }
  }
  
  class JFIFThumbRGB extends JFIFThumbUncompressed {
    JFIFThumbRGB(JPEGBuffer param1JPEGBuffer, int param1Int1, int param1Int2) throws IOException { super(JFIFMarkerSegment.this, param1JPEGBuffer, param1Int1, param1Int2, param1Int1 * param1Int2 * 3, "JFIFthumbRGB"); }
    
    JFIFThumbRGB(Node param1Node) throws IIOInvalidTreeException { super(JFIFMarkerSegment.this, param1Node, "JFIFthumbRGB"); }
    
    JFIFThumbRGB(BufferedImage param1BufferedImage) throws JFIFMarkerSegment.IllegalThumbException { super(JFIFMarkerSegment.this, param1BufferedImage); }
    
    int getLength() { return this.thumbWidth * this.thumbHeight * 3; }
    
    BufferedImage getThumbnail(ImageInputStream param1ImageInputStream, JPEGImageReader param1JPEGImageReader) throws IOException {
      param1ImageInputStream.mark();
      param1ImageInputStream.seek(this.streamPos);
      DataBufferByte dataBufferByte = new DataBufferByte(getLength());
      readByteBuffer(param1ImageInputStream, dataBufferByte.getData(), param1JPEGImageReader, 1.0F, 0.0F);
      param1ImageInputStream.reset();
      WritableRaster writableRaster = Raster.createInterleavedRaster(dataBufferByte, this.thumbWidth, this.thumbHeight, this.thumbWidth * 3, 3, new int[] { 0, 1, 2 }, null);
      ComponentColorModel componentColorModel = new ComponentColorModel(JPEG.JCS.sRGB, false, false, 1, 0);
      return new BufferedImage(componentColorModel, writableRaster, false, null);
    }
    
    void write(ImageOutputStream param1ImageOutputStream, JPEGImageWriter param1JPEGImageWriter) throws IOException {
      super.write(param1ImageOutputStream, param1JPEGImageWriter);
      writePixels(param1ImageOutputStream, param1JPEGImageWriter);
    }
  }
  
  abstract class JFIFThumbUncompressed extends JFIFThumb {
    BufferedImage thumbnail = null;
    
    int thumbWidth;
    
    int thumbHeight;
    
    String name;
    
    JFIFThumbUncompressed(JPEGBuffer param1JPEGBuffer, int param1Int1, int param1Int2, int param1Int3, String param1String) throws IOException {
      super(JFIFMarkerSegment.this, param1JPEGBuffer);
      this.thumbWidth = param1Int1;
      this.thumbHeight = param1Int2;
      param1JPEGBuffer.skipData(param1Int3);
      this.name = param1String;
    }
    
    JFIFThumbUncompressed(Node param1Node, String param1String) throws IIOInvalidTreeException {
      super(JFIFMarkerSegment.this);
      this.thumbWidth = 0;
      this.thumbHeight = 0;
      this.name = param1String;
      NamedNodeMap namedNodeMap = param1Node.getAttributes();
      int i = namedNodeMap.getLength();
      if (i > 2)
        throw new IIOInvalidTreeException(param1String + " node cannot have > 2 attributes", param1Node); 
      if (i != 0) {
        int j = MarkerSegment.getAttributeValue(param1Node, namedNodeMap, "thumbWidth", 0, 255, false);
        this.thumbWidth = (j != -1) ? j : this.thumbWidth;
        j = MarkerSegment.getAttributeValue(param1Node, namedNodeMap, "thumbHeight", 0, 255, false);
        this.thumbHeight = (j != -1) ? j : this.thumbHeight;
      } 
    }
    
    JFIFThumbUncompressed(BufferedImage param1BufferedImage) throws JFIFMarkerSegment.IllegalThumbException {
      super(JFIFMarkerSegment.this);
      this.thumbnail = param1BufferedImage;
      this.thumbWidth = param1BufferedImage.getWidth();
      this.thumbHeight = param1BufferedImage.getHeight();
      this.name = null;
    }
    
    void readByteBuffer(ImageInputStream param1ImageInputStream, byte[] param1ArrayOfByte, JPEGImageReader param1JPEGImageReader, float param1Float1, float param1Float2) throws IOException {
      int i = Math.max((int)((param1ArrayOfByte.length / 20) / param1Float1), 1);
      int j = 0;
      while (j < param1ArrayOfByte.length) {
        int k = Math.min(i, param1ArrayOfByte.length - j);
        param1ImageInputStream.read(param1ArrayOfByte, j, k);
        j += i;
        float f = j * 100.0F / param1ArrayOfByte.length * param1Float1 + param1Float2;
        if (f > 100.0F)
          f = 100.0F; 
        param1JPEGImageReader.thumbnailProgress(f);
      } 
    }
    
    int getWidth() { return this.thumbWidth; }
    
    int getHeight() { return this.thumbHeight; }
    
    IIOMetadataNode getNativeNode() {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode(this.name);
      iIOMetadataNode.setAttribute("thumbWidth", Integer.toString(this.thumbWidth));
      iIOMetadataNode.setAttribute("thumbHeight", Integer.toString(this.thumbHeight));
      return iIOMetadataNode;
    }
    
    void write(ImageOutputStream param1ImageOutputStream, JPEGImageWriter param1JPEGImageWriter) throws IOException {
      if (this.thumbWidth > 255 || this.thumbHeight > 255)
        param1JPEGImageWriter.warningOccurred(12); 
      this.thumbWidth = Math.min(this.thumbWidth, 255);
      this.thumbHeight = Math.min(this.thumbHeight, 255);
      param1ImageOutputStream.write(this.thumbWidth);
      param1ImageOutputStream.write(this.thumbHeight);
    }
    
    void writePixels(ImageOutputStream param1ImageOutputStream, JPEGImageWriter param1JPEGImageWriter) throws IOException {
      if (this.thumbWidth > 255 || this.thumbHeight > 255)
        param1JPEGImageWriter.warningOccurred(12); 
      this.thumbWidth = Math.min(this.thumbWidth, 255);
      this.thumbHeight = Math.min(this.thumbHeight, 255);
      int[] arrayOfInt = this.thumbnail.getRaster().getPixels(0, 0, this.thumbWidth, this.thumbHeight, (int[])null);
      JFIFMarkerSegment.this.writeThumbnailData(param1ImageOutputStream, arrayOfInt, param1JPEGImageWriter);
    }
    
    void print() {
      System.out.print(this.name + " width: ");
      System.out.println(this.thumbWidth);
      System.out.print(this.name + " height: ");
      System.out.println(this.thumbHeight);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\JFIFMarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */