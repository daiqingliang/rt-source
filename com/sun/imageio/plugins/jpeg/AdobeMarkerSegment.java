package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class AdobeMarkerSegment extends MarkerSegment {
  int version;
  
  int flags0;
  
  int flags1;
  
  int transform;
  
  private static final int ID_SIZE = 5;
  
  AdobeMarkerSegment(int paramInt) {
    super(238);
    this.version = 101;
    this.flags0 = 0;
    this.flags1 = 0;
    this.transform = paramInt;
  }
  
  AdobeMarkerSegment(JPEGBuffer paramJPEGBuffer) throws IOException {
    super(paramJPEGBuffer);
    paramJPEGBuffer.bufPtr += 5;
    this.version = (paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF) << 8;
    this.version |= paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF;
    this.flags0 = (paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF) << 8;
    this.flags0 |= paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF;
    this.flags1 = (paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF) << 8;
    this.flags1 |= paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF;
    this.transform = paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF;
    paramJPEGBuffer.bufAvail -= this.length;
  }
  
  AdobeMarkerSegment(Node paramNode) throws IIOInvalidTreeException {
    this(0);
    updateFromNativeNode(paramNode, true);
  }
  
  IIOMetadataNode getNativeNode() {
    IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("app14Adobe");
    iIOMetadataNode.setAttribute("version", Integer.toString(this.version));
    iIOMetadataNode.setAttribute("flags0", Integer.toString(this.flags0));
    iIOMetadataNode.setAttribute("flags1", Integer.toString(this.flags1));
    iIOMetadataNode.setAttribute("transform", Integer.toString(this.transform));
    return iIOMetadataNode;
  }
  
  void updateFromNativeNode(Node paramNode, boolean paramBoolean) throws IIOInvalidTreeException {
    NamedNodeMap namedNodeMap = paramNode.getAttributes();
    this.transform = getAttributeValue(paramNode, namedNodeMap, "transform", 0, 2, true);
    int i = namedNodeMap.getLength();
    if (i > 4)
      throw new IIOInvalidTreeException("Adobe APP14 node cannot have > 4 attributes", paramNode); 
    if (i > 1) {
      int j = getAttributeValue(paramNode, namedNodeMap, "version", 100, 255, false);
      this.version = (j != -1) ? j : this.version;
      j = getAttributeValue(paramNode, namedNodeMap, "flags0", 0, 65535, false);
      this.flags0 = (j != -1) ? j : this.flags0;
      j = getAttributeValue(paramNode, namedNodeMap, "flags1", 0, 65535, false);
      this.flags1 = (j != -1) ? j : this.flags1;
    } 
  }
  
  void write(ImageOutputStream paramImageOutputStream) throws IOException {
    this.length = 14;
    writeTag(paramImageOutputStream);
    byte[] arrayOfByte = { 65, 100, 111, 98, 101 };
    paramImageOutputStream.write(arrayOfByte);
    write2bytes(paramImageOutputStream, this.version);
    write2bytes(paramImageOutputStream, this.flags0);
    write2bytes(paramImageOutputStream, this.flags1);
    paramImageOutputStream.write(this.transform);
  }
  
  static void writeAdobeSegment(ImageOutputStream paramImageOutputStream, int paramInt) throws IOException { (new AdobeMarkerSegment(paramInt)).write(paramImageOutputStream); }
  
  void print() {
    printTag("Adobe APP14");
    System.out.print("Version: ");
    System.out.println(this.version);
    System.out.print("Flags0: 0x");
    System.out.println(Integer.toHexString(this.flags0));
    System.out.print("Flags1: 0x");
    System.out.println(Integer.toHexString(this.flags1));
    System.out.print("Transform: ");
    System.out.println(this.transform);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\AdobeMarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */