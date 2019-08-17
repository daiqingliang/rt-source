package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class SOSMarkerSegment extends MarkerSegment {
  int startSpectralSelection;
  
  int endSpectralSelection;
  
  int approxHigh;
  
  int approxLow;
  
  ScanComponentSpec[] componentSpecs;
  
  SOSMarkerSegment(boolean paramBoolean, byte[] paramArrayOfByte, int paramInt) {
    super(218);
    this.startSpectralSelection = 0;
    this.endSpectralSelection = 63;
    this.approxHigh = 0;
    this.approxLow = 0;
    this.componentSpecs = new ScanComponentSpec[paramInt];
    for (byte b = 0; b < paramInt; b++) {
      byte b1 = 0;
      if (paramBoolean && (b == 1 || b == 2))
        b1 = 1; 
      this.componentSpecs[b] = new ScanComponentSpec(paramArrayOfByte[b], b1);
    } 
  }
  
  SOSMarkerSegment(JPEGBuffer paramJPEGBuffer) throws IOException {
    super(paramJPEGBuffer);
    byte b = paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++];
    this.componentSpecs = new ScanComponentSpec[b];
    for (byte b1 = 0; b1 < b; b1++)
      this.componentSpecs[b1] = new ScanComponentSpec(paramJPEGBuffer); 
    this.startSpectralSelection = paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++];
    this.endSpectralSelection = paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++];
    this.approxHigh = paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr] >> 4;
    this.approxLow = paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xF;
    paramJPEGBuffer.bufAvail -= this.length;
  }
  
  SOSMarkerSegment(Node paramNode) throws IIOInvalidTreeException {
    super(218);
    this.startSpectralSelection = 0;
    this.endSpectralSelection = 63;
    this.approxHigh = 0;
    this.approxLow = 0;
    updateFromNativeNode(paramNode, true);
  }
  
  protected Object clone() {
    SOSMarkerSegment sOSMarkerSegment = (SOSMarkerSegment)super.clone();
    if (this.componentSpecs != null) {
      sOSMarkerSegment.componentSpecs = (ScanComponentSpec[])this.componentSpecs.clone();
      for (byte b = 0; b < this.componentSpecs.length; b++)
        sOSMarkerSegment.componentSpecs[b] = (ScanComponentSpec)this.componentSpecs[b].clone(); 
    } 
    return sOSMarkerSegment;
  }
  
  IIOMetadataNode getNativeNode() {
    IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("sos");
    iIOMetadataNode.setAttribute("numScanComponents", Integer.toString(this.componentSpecs.length));
    iIOMetadataNode.setAttribute("startSpectralSelection", Integer.toString(this.startSpectralSelection));
    iIOMetadataNode.setAttribute("endSpectralSelection", Integer.toString(this.endSpectralSelection));
    iIOMetadataNode.setAttribute("approxHigh", Integer.toString(this.approxHigh));
    iIOMetadataNode.setAttribute("approxLow", Integer.toString(this.approxLow));
    for (byte b = 0; b < this.componentSpecs.length; b++)
      iIOMetadataNode.appendChild(this.componentSpecs[b].getNativeNode()); 
    return iIOMetadataNode;
  }
  
  void updateFromNativeNode(Node paramNode, boolean paramBoolean) throws IIOInvalidTreeException {
    NamedNodeMap namedNodeMap = paramNode.getAttributes();
    int i = getAttributeValue(paramNode, namedNodeMap, "numScanComponents", 1, 4, true);
    int j = getAttributeValue(paramNode, namedNodeMap, "startSpectralSelection", 0, 63, false);
    this.startSpectralSelection = (j != -1) ? j : this.startSpectralSelection;
    j = getAttributeValue(paramNode, namedNodeMap, "endSpectralSelection", 0, 63, false);
    this.endSpectralSelection = (j != -1) ? j : this.endSpectralSelection;
    j = getAttributeValue(paramNode, namedNodeMap, "approxHigh", 0, 15, false);
    this.approxHigh = (j != -1) ? j : this.approxHigh;
    j = getAttributeValue(paramNode, namedNodeMap, "approxLow", 0, 15, false);
    this.approxLow = (j != -1) ? j : this.approxLow;
    NodeList nodeList = paramNode.getChildNodes();
    if (nodeList.getLength() != i)
      throw new IIOInvalidTreeException("numScanComponents must match the number of children", paramNode); 
    this.componentSpecs = new ScanComponentSpec[i];
    for (byte b = 0; b < i; b++)
      this.componentSpecs[b] = new ScanComponentSpec(nodeList.item(b)); 
  }
  
  void write(ImageOutputStream paramImageOutputStream) throws IOException {}
  
  void print() {
    printTag("SOS");
    System.out.print("Start spectral selection: ");
    System.out.println(this.startSpectralSelection);
    System.out.print("End spectral selection: ");
    System.out.println(this.endSpectralSelection);
    System.out.print("Approx high: ");
    System.out.println(this.approxHigh);
    System.out.print("Approx low: ");
    System.out.println(this.approxLow);
    System.out.print("Num scan components: ");
    System.out.println(this.componentSpecs.length);
    for (byte b = 0; b < this.componentSpecs.length; b++)
      this.componentSpecs[b].print(); 
  }
  
  ScanComponentSpec getScanComponentSpec(byte paramByte, int paramInt) { return new ScanComponentSpec(paramByte, paramInt); }
  
  class ScanComponentSpec implements Cloneable {
    int componentSelector;
    
    int dcHuffTable;
    
    int acHuffTable;
    
    ScanComponentSpec(byte param1Byte, int param1Int) {
      this.componentSelector = param1Byte;
      this.dcHuffTable = param1Int;
      this.acHuffTable = param1Int;
    }
    
    ScanComponentSpec(JPEGBuffer param1JPEGBuffer) {
      this.componentSelector = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr++];
      this.dcHuffTable = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr] >> 4;
      this.acHuffTable = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr++] & 0xF;
    }
    
    ScanComponentSpec(Node param1Node) throws IIOInvalidTreeException {
      NamedNodeMap namedNodeMap = param1Node.getAttributes();
      this.componentSelector = MarkerSegment.getAttributeValue(param1Node, namedNodeMap, "componentSelector", 0, 255, true);
      this.dcHuffTable = MarkerSegment.getAttributeValue(param1Node, namedNodeMap, "dcHuffTable", 0, 3, true);
      this.acHuffTable = MarkerSegment.getAttributeValue(param1Node, namedNodeMap, "acHuffTable", 0, 3, true);
    }
    
    protected Object clone() {
      try {
        return super.clone();
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        return null;
      } 
    }
    
    IIOMetadataNode getNativeNode() {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("scanComponentSpec");
      iIOMetadataNode.setAttribute("componentSelector", Integer.toString(this.componentSelector));
      iIOMetadataNode.setAttribute("dcHuffTable", Integer.toString(this.dcHuffTable));
      iIOMetadataNode.setAttribute("acHuffTable", Integer.toString(this.acHuffTable));
      return iIOMetadataNode;
    }
    
    void print() {
      System.out.print("Component Selector: ");
      System.out.println(this.componentSelector);
      System.out.print("DC huffman table: ");
      System.out.println(this.dcHuffTable);
      System.out.print("AC huffman table: ");
      System.out.println(this.acHuffTable);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\SOSMarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */