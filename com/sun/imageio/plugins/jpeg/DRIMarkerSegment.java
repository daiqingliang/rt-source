package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.Node;

class DRIMarkerSegment extends MarkerSegment {
  int restartInterval = 0;
  
  DRIMarkerSegment(JPEGBuffer paramJPEGBuffer) throws IOException {
    super(paramJPEGBuffer);
    this.restartInterval = (paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF) << 8;
    this.restartInterval |= paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF;
    paramJPEGBuffer.bufAvail -= this.length;
  }
  
  DRIMarkerSegment(Node paramNode) throws IIOInvalidTreeException {
    super(221);
    updateFromNativeNode(paramNode, true);
  }
  
  IIOMetadataNode getNativeNode() {
    IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("dri");
    iIOMetadataNode.setAttribute("interval", Integer.toString(this.restartInterval));
    return iIOMetadataNode;
  }
  
  void updateFromNativeNode(Node paramNode, boolean paramBoolean) throws IIOInvalidTreeException { this.restartInterval = getAttributeValue(paramNode, null, "interval", 0, 65535, true); }
  
  void write(ImageOutputStream paramImageOutputStream) throws IOException {}
  
  void print() {
    printTag("DRI");
    System.out.println("Interval: " + Integer.toString(this.restartInterval));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\DRIMarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */