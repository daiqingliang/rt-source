package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class MarkerSegment implements Cloneable {
  protected static final int LENGTH_SIZE = 2;
  
  int tag;
  
  int length;
  
  byte[] data = null;
  
  boolean unknown = false;
  
  MarkerSegment(JPEGBuffer paramJPEGBuffer) throws IOException {
    paramJPEGBuffer.loadBuf(3);
    this.tag = paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF;
    this.length = (paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF) << 8;
    this.length |= paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF;
    this.length -= 2;
    if (this.length < 0)
      throw new IIOException("Invalid segment length: " + this.length); 
    paramJPEGBuffer.bufAvail -= 3;
    paramJPEGBuffer.loadBuf(this.length);
  }
  
  MarkerSegment(int paramInt) {
    this.tag = paramInt;
    this.length = 0;
  }
  
  MarkerSegment(Node paramNode) throws IIOInvalidTreeException {
    this.tag = getAttributeValue(paramNode, null, "MarkerTag", 0, 255, true);
    this.length = 0;
    if (paramNode instanceof IIOMetadataNode) {
      IIOMetadataNode iIOMetadataNode = (IIOMetadataNode)paramNode;
      try {
        this.data = (byte[])iIOMetadataNode.getUserObject();
      } catch (Exception exception) {
        IIOInvalidTreeException iIOInvalidTreeException = new IIOInvalidTreeException("Can't get User Object", paramNode);
        iIOInvalidTreeException.initCause(exception);
        throw iIOInvalidTreeException;
      } 
    } else {
      throw new IIOInvalidTreeException("Node must have User Object", paramNode);
    } 
  }
  
  protected Object clone() {
    MarkerSegment markerSegment = null;
    try {
      markerSegment = (MarkerSegment)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {}
    if (this.data != null)
      markerSegment.data = (byte[])this.data.clone(); 
    return markerSegment;
  }
  
  void loadData(JPEGBuffer paramJPEGBuffer) throws IOException {
    this.data = new byte[this.length];
    paramJPEGBuffer.readData(this.data);
  }
  
  IIOMetadataNode getNativeNode() {
    IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("unknown");
    iIOMetadataNode.setAttribute("MarkerTag", Integer.toString(this.tag));
    iIOMetadataNode.setUserObject(this.data);
    return iIOMetadataNode;
  }
  
  static int getAttributeValue(Node paramNode, NamedNodeMap paramNamedNodeMap, String paramString, int paramInt1, int paramInt2, boolean paramBoolean) throws IIOInvalidTreeException {
    if (paramNamedNodeMap == null)
      paramNamedNodeMap = paramNode.getAttributes(); 
    String str = paramNamedNodeMap.getNamedItem(paramString).getNodeValue();
    int i = -1;
    if (str == null) {
      if (paramBoolean)
        throw new IIOInvalidTreeException(paramString + " attribute not found", paramNode); 
    } else {
      i = Integer.parseInt(str);
      if (i < paramInt1 || i > paramInt2)
        throw new IIOInvalidTreeException(paramString + " attribute out of range", paramNode); 
    } 
    return i;
  }
  
  void writeTag(ImageOutputStream paramImageOutputStream) throws IOException {
    paramImageOutputStream.write(255);
    paramImageOutputStream.write(this.tag);
    write2bytes(paramImageOutputStream, this.length);
  }
  
  void write(ImageOutputStream paramImageOutputStream) throws IOException {
    this.length = 2 + ((this.data != null) ? this.data.length : 0);
    writeTag(paramImageOutputStream);
    if (this.data != null)
      paramImageOutputStream.write(this.data); 
  }
  
  static void write2bytes(ImageOutputStream paramImageOutputStream, int paramInt) throws IOException {
    paramImageOutputStream.write(paramInt >> 8 & 0xFF);
    paramImageOutputStream.write(paramInt & 0xFF);
  }
  
  void printTag(String paramString) {
    System.out.println(paramString + " marker segment - marker = 0x" + Integer.toHexString(this.tag));
    System.out.println("length: " + this.length);
  }
  
  void print() {
    printTag("Unknown");
    if (this.length > 10) {
      System.out.print("First 5 bytes:");
      int i;
      for (i = 0; i < 5; i++)
        System.out.print(" Ox" + Integer.toHexString(this.data[i])); 
      System.out.print("\nLast 5 bytes:");
      for (i = this.data.length - 5; i < this.data.length; i++)
        System.out.print(" Ox" + Integer.toHexString(this.data[i])); 
    } else {
      System.out.print("Data:");
      for (byte b = 0; b < this.data.length; b++)
        System.out.print(" Ox" + Integer.toHexString(this.data[b])); 
    } 
    System.out.println();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\MarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */