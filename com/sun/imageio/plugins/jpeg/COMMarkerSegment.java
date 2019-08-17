package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.Node;

class COMMarkerSegment extends MarkerSegment {
  private static final String ENCODING = "ISO-8859-1";
  
  COMMarkerSegment(JPEGBuffer paramJPEGBuffer) throws IOException {
    super(paramJPEGBuffer);
    loadData(paramJPEGBuffer);
  }
  
  COMMarkerSegment(String paramString) {
    super(254);
    this.data = paramString.getBytes();
  }
  
  COMMarkerSegment(Node paramNode) throws IIOInvalidTreeException {
    super(254);
    if (paramNode instanceof IIOMetadataNode) {
      IIOMetadataNode iIOMetadataNode = (IIOMetadataNode)paramNode;
      this.data = (byte[])iIOMetadataNode.getUserObject();
    } 
    if (this.data == null) {
      String str = paramNode.getAttributes().getNamedItem("comment").getNodeValue();
      if (str != null) {
        this.data = str.getBytes();
      } else {
        throw new IIOInvalidTreeException("Empty comment node!", paramNode);
      } 
    } 
  }
  
  String getComment() {
    try {
      return new String(this.data, "ISO-8859-1");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      return null;
    } 
  }
  
  IIOMetadataNode getNativeNode() {
    IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("com");
    iIOMetadataNode.setAttribute("comment", getComment());
    if (this.data != null)
      iIOMetadataNode.setUserObject(this.data.clone()); 
    return iIOMetadataNode;
  }
  
  void write(ImageOutputStream paramImageOutputStream) throws IOException {
    this.length = 2 + this.data.length;
    writeTag(paramImageOutputStream);
    paramImageOutputStream.write(this.data);
  }
  
  void print() {
    printTag("COM");
    System.out.println("<" + getComment() + ">");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\COMMarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */