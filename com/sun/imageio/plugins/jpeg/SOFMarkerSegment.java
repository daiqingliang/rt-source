package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class SOFMarkerSegment extends MarkerSegment {
  int samplePrecision = 8;
  
  int numLines = 0;
  
  int samplesPerLine;
  
  ComponentSpec[] componentSpecs;
  
  SOFMarkerSegment(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, byte[] paramArrayOfByte, int paramInt) {
    super(paramBoolean1 ? 194 : (paramBoolean2 ? 193 : 192));
    this.samplesPerLine = 0;
    this.componentSpecs = new ComponentSpec[paramInt];
    for (byte b = 0; b < paramInt; b++) {
      byte b1 = 1;
      byte b2 = 0;
      if (paramBoolean3) {
        b1 = 2;
        if (b == 1 || b == 2) {
          b1 = 1;
          b2 = 1;
        } 
      } 
      this.componentSpecs[b] = new ComponentSpec(paramArrayOfByte[b], b1, b2);
    } 
  }
  
  SOFMarkerSegment(JPEGBuffer paramJPEGBuffer) throws IOException {
    super(paramJPEGBuffer);
    this.numLines |= paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF;
    this.samplesPerLine = (paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF) << 8;
    this.samplesPerLine |= paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF;
    byte b = paramJPEGBuffer.buf[paramJPEGBuffer.bufPtr++] & 0xFF;
    this.componentSpecs = new ComponentSpec[b];
    for (byte b1 = 0; b1 < b; b1++)
      this.componentSpecs[b1] = new ComponentSpec(paramJPEGBuffer); 
    paramJPEGBuffer.bufAvail -= this.length;
  }
  
  SOFMarkerSegment(Node paramNode) throws IIOInvalidTreeException {
    super(192);
    this.samplesPerLine = 0;
    updateFromNativeNode(paramNode, true);
  }
  
  protected Object clone() {
    SOFMarkerSegment sOFMarkerSegment = (SOFMarkerSegment)super.clone();
    if (this.componentSpecs != null) {
      sOFMarkerSegment.componentSpecs = (ComponentSpec[])this.componentSpecs.clone();
      for (byte b = 0; b < this.componentSpecs.length; b++)
        sOFMarkerSegment.componentSpecs[b] = (ComponentSpec)this.componentSpecs[b].clone(); 
    } 
    return sOFMarkerSegment;
  }
  
  IIOMetadataNode getNativeNode() {
    IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("sof");
    iIOMetadataNode.setAttribute("process", Integer.toString(this.tag - 192));
    iIOMetadataNode.setAttribute("samplePrecision", Integer.toString(this.samplePrecision));
    iIOMetadataNode.setAttribute("numLines", Integer.toString(this.numLines));
    iIOMetadataNode.setAttribute("samplesPerLine", Integer.toString(this.samplesPerLine));
    iIOMetadataNode.setAttribute("numFrameComponents", Integer.toString(this.componentSpecs.length));
    for (byte b = 0; b < this.componentSpecs.length; b++)
      iIOMetadataNode.appendChild(this.componentSpecs[b].getNativeNode()); 
    return iIOMetadataNode;
  }
  
  void updateFromNativeNode(Node paramNode, boolean paramBoolean) throws IIOInvalidTreeException {
    NamedNodeMap namedNodeMap = paramNode.getAttributes();
    int i = getAttributeValue(paramNode, namedNodeMap, "process", 0, 2, false);
    this.tag = (i != -1) ? (i + 192) : this.tag;
    i = getAttributeValue(paramNode, namedNodeMap, "samplePrecision", 8, 8, false);
    i = getAttributeValue(paramNode, namedNodeMap, "numLines", 0, 65535, false);
    this.numLines = (i != -1) ? i : this.numLines;
    i = getAttributeValue(paramNode, namedNodeMap, "samplesPerLine", 0, 65535, false);
    this.samplesPerLine = (i != -1) ? i : this.samplesPerLine;
    int j = getAttributeValue(paramNode, namedNodeMap, "numFrameComponents", 1, 4, false);
    NodeList nodeList = paramNode.getChildNodes();
    if (nodeList.getLength() != j)
      throw new IIOInvalidTreeException("numFrameComponents must match number of children", paramNode); 
    this.componentSpecs = new ComponentSpec[j];
    for (byte b = 0; b < j; b++)
      this.componentSpecs[b] = new ComponentSpec(nodeList.item(b)); 
  }
  
  void write(ImageOutputStream paramImageOutputStream) throws IOException {}
  
  void print() {
    printTag("SOF");
    System.out.print("Sample precision: ");
    System.out.println(this.samplePrecision);
    System.out.print("Number of lines: ");
    System.out.println(this.numLines);
    System.out.print("Samples per line: ");
    System.out.println(this.samplesPerLine);
    System.out.print("Number of components: ");
    System.out.println(this.componentSpecs.length);
    for (byte b = 0; b < this.componentSpecs.length; b++)
      this.componentSpecs[b].print(); 
  }
  
  int getIDencodedCSType() {
    for (byte b = 0; b < this.componentSpecs.length; b++) {
      if ((this.componentSpecs[b]).componentId < 65)
        return 0; 
    } 
    switch (this.componentSpecs.length) {
      case 3:
        if ((this.componentSpecs[0]).componentId == 82 && (this.componentSpecs[0]).componentId == 71 && (this.componentSpecs[0]).componentId == 66)
          return 2; 
        if ((this.componentSpecs[0]).componentId == 89 && (this.componentSpecs[0]).componentId == 67 && (this.componentSpecs[0]).componentId == 99)
          return 5; 
        break;
      case 4:
        if ((this.componentSpecs[0]).componentId == 82 && (this.componentSpecs[0]).componentId == 71 && (this.componentSpecs[0]).componentId == 66 && (this.componentSpecs[0]).componentId == 65)
          return 6; 
        if ((this.componentSpecs[0]).componentId == 89 && (this.componentSpecs[0]).componentId == 67 && (this.componentSpecs[0]).componentId == 99 && (this.componentSpecs[0]).componentId == 65)
          return 10; 
        break;
    } 
    return 0;
  }
  
  ComponentSpec getComponentSpec(byte paramByte, int paramInt1, int paramInt2) { return new ComponentSpec(paramByte, paramInt1, paramInt2); }
  
  class ComponentSpec implements Cloneable {
    int componentId;
    
    int HsamplingFactor;
    
    int VsamplingFactor;
    
    int QtableSelector;
    
    ComponentSpec(byte param1Byte, int param1Int1, int param1Int2) {
      this.componentId = param1Byte;
      this.HsamplingFactor = param1Int1;
      this.VsamplingFactor = param1Int1;
      this.QtableSelector = param1Int2;
    }
    
    ComponentSpec(JPEGBuffer param1JPEGBuffer) {
      this.componentId = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr++];
      this.HsamplingFactor = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr] >>> 4;
      this.VsamplingFactor = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr++] & 0xF;
      this.QtableSelector = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr++];
    }
    
    ComponentSpec(Node param1Node) throws IIOInvalidTreeException {
      NamedNodeMap namedNodeMap = param1Node.getAttributes();
      this.componentId = MarkerSegment.getAttributeValue(param1Node, namedNodeMap, "componentId", 0, 255, true);
      this.HsamplingFactor = MarkerSegment.getAttributeValue(param1Node, namedNodeMap, "HsamplingFactor", 1, 255, true);
      this.VsamplingFactor = MarkerSegment.getAttributeValue(param1Node, namedNodeMap, "VsamplingFactor", 1, 255, true);
      this.QtableSelector = MarkerSegment.getAttributeValue(param1Node, namedNodeMap, "QtableSelector", 0, 3, true);
    }
    
    protected Object clone() {
      try {
        return super.clone();
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        return null;
      } 
    }
    
    IIOMetadataNode getNativeNode() {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("componentSpec");
      iIOMetadataNode.setAttribute("componentId", Integer.toString(this.componentId));
      iIOMetadataNode.setAttribute("HsamplingFactor", Integer.toString(this.HsamplingFactor));
      iIOMetadataNode.setAttribute("VsamplingFactor", Integer.toString(this.VsamplingFactor));
      iIOMetadataNode.setAttribute("QtableSelector", Integer.toString(this.QtableSelector));
      return iIOMetadataNode;
    }
    
    void print() {
      System.out.print("Component ID: ");
      System.out.println(this.componentId);
      System.out.print("H sampling factor: ");
      System.out.println(this.HsamplingFactor);
      System.out.print("V sampling factor: ");
      System.out.println(this.VsamplingFactor);
      System.out.print("Q table selector: ");
      System.out.println(this.QtableSelector);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\SOFMarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */