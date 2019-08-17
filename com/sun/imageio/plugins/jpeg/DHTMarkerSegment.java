package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class DHTMarkerSegment extends MarkerSegment {
  List tables = new ArrayList();
  
  DHTMarkerSegment(boolean paramBoolean) {
    super(196);
    this.tables.add(new Htable(JPEGHuffmanTable.StdDCLuminance, true, 0));
    if (paramBoolean)
      this.tables.add(new Htable(JPEGHuffmanTable.StdDCChrominance, true, 1)); 
    this.tables.add(new Htable(JPEGHuffmanTable.StdACLuminance, false, 0));
    if (paramBoolean)
      this.tables.add(new Htable(JPEGHuffmanTable.StdACChrominance, false, 1)); 
  }
  
  DHTMarkerSegment(JPEGBuffer paramJPEGBuffer) throws IOException {
    super(paramJPEGBuffer);
    for (int i = this.length; i > 0; i -= 17 + htable.values.length) {
      Htable htable = new Htable(paramJPEGBuffer);
      this.tables.add(htable);
    } 
    paramJPEGBuffer.bufAvail -= this.length;
  }
  
  DHTMarkerSegment(JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable2) {
    super(196);
    byte b;
    for (b = 0; b < paramArrayOfJPEGHuffmanTable1.length; b++)
      this.tables.add(new Htable(paramArrayOfJPEGHuffmanTable1[b], true, b)); 
    for (b = 0; b < paramArrayOfJPEGHuffmanTable2.length; b++)
      this.tables.add(new Htable(paramArrayOfJPEGHuffmanTable2[b], false, b)); 
  }
  
  DHTMarkerSegment(Node paramNode) throws IIOInvalidTreeException {
    super(196);
    NodeList nodeList = paramNode.getChildNodes();
    int i = nodeList.getLength();
    if (i < 1 || i > 4)
      throw new IIOInvalidTreeException("Invalid DHT node", paramNode); 
    for (byte b = 0; b < i; b++)
      this.tables.add(new Htable(nodeList.item(b))); 
  }
  
  protected Object clone() {
    DHTMarkerSegment dHTMarkerSegment = (DHTMarkerSegment)super.clone();
    dHTMarkerSegment.tables = new ArrayList(this.tables.size());
    for (Htable htable : this.tables)
      dHTMarkerSegment.tables.add(htable.clone()); 
    return dHTMarkerSegment;
  }
  
  IIOMetadataNode getNativeNode() {
    IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("dht");
    for (byte b = 0; b < this.tables.size(); b++) {
      Htable htable = (Htable)this.tables.get(b);
      iIOMetadataNode.appendChild(htable.getNativeNode());
    } 
    return iIOMetadataNode;
  }
  
  void write(ImageOutputStream paramImageOutputStream) throws IOException {}
  
  void print() {
    printTag("DHT");
    System.out.println("Num tables: " + Integer.toString(this.tables.size()));
    for (byte b = 0; b < this.tables.size(); b++) {
      Htable htable = (Htable)this.tables.get(b);
      htable.print();
    } 
    System.out.println();
  }
  
  Htable getHtableFromNode(Node paramNode) throws IIOInvalidTreeException { return new Htable(paramNode); }
  
  void addHtable(JPEGHuffmanTable paramJPEGHuffmanTable, boolean paramBoolean, int paramInt) { this.tables.add(new Htable(paramJPEGHuffmanTable, paramBoolean, paramInt)); }
  
  class Htable implements Cloneable {
    int tableClass;
    
    int tableID;
    
    private static final int NUM_LENGTHS = 16;
    
    short[] numCodes = new short[16];
    
    short[] values;
    
    Htable(JPEGBuffer param1JPEGBuffer) {
      this.tableClass = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr] >>> 4;
      this.tableID = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr++] & 0xF;
      short s;
      for (s = 0; s < 16; s++)
        this.numCodes[s] = (short)(param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr++] & 0xFF); 
      s = 0;
      byte b;
      for (b = 0; b < 16; b++)
        s += this.numCodes[b]; 
      this.values = new short[s];
      for (b = 0; b < s; b++)
        this.values[b] = (short)(param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr++] & 0xFF); 
    }
    
    Htable(JPEGHuffmanTable param1JPEGHuffmanTable, boolean param1Boolean, int param1Int) {
      this.tableClass = param1Boolean ? 0 : 1;
      this.tableID = param1Int;
      this.numCodes = param1JPEGHuffmanTable.getLengths();
      this.values = param1JPEGHuffmanTable.getValues();
    }
    
    Htable(Node param1Node) throws IIOInvalidTreeException {
      if (param1Node.getNodeName().equals("dhtable")) {
        NamedNodeMap namedNodeMap = param1Node.getAttributes();
        int i = namedNodeMap.getLength();
        if (i != 2)
          throw new IIOInvalidTreeException("dhtable node must have 2 attributes", param1Node); 
        this.tableClass = MarkerSegment.getAttributeValue(param1Node, namedNodeMap, "class", 0, 1, true);
        this.tableID = MarkerSegment.getAttributeValue(param1Node, namedNodeMap, "htableId", 0, 3, true);
        if (param1Node instanceof IIOMetadataNode) {
          IIOMetadataNode iIOMetadataNode = (IIOMetadataNode)param1Node;
          JPEGHuffmanTable jPEGHuffmanTable = (JPEGHuffmanTable)iIOMetadataNode.getUserObject();
          if (jPEGHuffmanTable == null)
            throw new IIOInvalidTreeException("dhtable node must have user object", param1Node); 
          this.numCodes = jPEGHuffmanTable.getLengths();
          this.values = jPEGHuffmanTable.getValues();
        } else {
          throw new IIOInvalidTreeException("dhtable node must have user object", param1Node);
        } 
      } else {
        throw new IIOInvalidTreeException("Invalid node, expected dqtable", param1Node);
      } 
    }
    
    protected Object clone() {
      Htable htable = null;
      try {
        htable = (Htable)super.clone();
      } catch (CloneNotSupportedException cloneNotSupportedException) {}
      if (this.numCodes != null)
        htable.numCodes = (short[])this.numCodes.clone(); 
      if (this.values != null)
        htable.values = (short[])this.values.clone(); 
      return htable;
    }
    
    IIOMetadataNode getNativeNode() {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("dhtable");
      iIOMetadataNode.setAttribute("class", Integer.toString(this.tableClass));
      iIOMetadataNode.setAttribute("htableId", Integer.toString(this.tableID));
      iIOMetadataNode.setUserObject(new JPEGHuffmanTable(this.numCodes, this.values));
      return iIOMetadataNode;
    }
    
    void print() {
      System.out.println("Huffman Table");
      System.out.println("table class: " + ((this.tableClass == 0) ? "DC" : "AC"));
      System.out.println("table id: " + Integer.toString(this.tableID));
      (new JPEGHuffmanTable(this.numCodes, this.values)).toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\DHTMarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */