package com.sun.imageio.plugins.jpeg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class DQTMarkerSegment extends MarkerSegment {
  List tables = new ArrayList();
  
  DQTMarkerSegment(float paramFloat, boolean paramBoolean) {
    super(219);
    this.tables.add(new Qtable(true, paramFloat));
    if (paramBoolean)
      this.tables.add(new Qtable(false, paramFloat)); 
  }
  
  DQTMarkerSegment(JPEGBuffer paramJPEGBuffer) throws IOException {
    super(paramJPEGBuffer);
    for (int i = this.length; i > 0; i -= qtable.data.length + 1) {
      Qtable qtable = new Qtable(paramJPEGBuffer);
      this.tables.add(qtable);
    } 
    paramJPEGBuffer.bufAvail -= this.length;
  }
  
  DQTMarkerSegment(JPEGQTable[] paramArrayOfJPEGQTable) {
    super(219);
    for (byte b = 0; b < paramArrayOfJPEGQTable.length; b++)
      this.tables.add(new Qtable(paramArrayOfJPEGQTable[b], b)); 
  }
  
  DQTMarkerSegment(Node paramNode) throws IIOInvalidTreeException {
    super(219);
    NodeList nodeList = paramNode.getChildNodes();
    int i = nodeList.getLength();
    if (i < 1 || i > 4)
      throw new IIOInvalidTreeException("Invalid DQT node", paramNode); 
    for (byte b = 0; b < i; b++)
      this.tables.add(new Qtable(nodeList.item(b))); 
  }
  
  protected Object clone() {
    DQTMarkerSegment dQTMarkerSegment = (DQTMarkerSegment)super.clone();
    dQTMarkerSegment.tables = new ArrayList(this.tables.size());
    for (Qtable qtable : this.tables)
      dQTMarkerSegment.tables.add(qtable.clone()); 
    return dQTMarkerSegment;
  }
  
  IIOMetadataNode getNativeNode() {
    IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("dqt");
    for (byte b = 0; b < this.tables.size(); b++) {
      Qtable qtable = (Qtable)this.tables.get(b);
      iIOMetadataNode.appendChild(qtable.getNativeNode());
    } 
    return iIOMetadataNode;
  }
  
  void write(ImageOutputStream paramImageOutputStream) throws IOException {}
  
  void print() {
    printTag("DQT");
    System.out.println("Num tables: " + Integer.toString(this.tables.size()));
    for (byte b = 0; b < this.tables.size(); b++) {
      Qtable qtable = (Qtable)this.tables.get(b);
      qtable.print();
    } 
    System.out.println();
  }
  
  Qtable getChromaForLuma(Qtable paramQtable) {
    Qtable qtable = null;
    boolean bool = true;
    byte b = 1;
    paramQtable.getClass();
    while (b < 64) {
      if (paramQtable.data[b] != paramQtable.data[b - true]) {
        bool = false;
        break;
      } 
      b++;
    } 
    if (bool) {
      qtable = (Qtable)paramQtable.clone();
      qtable.tableID = 1;
    } else {
      b = 0;
      byte b1 = 1;
      paramQtable.getClass();
      while (b1 < 64) {
        if (paramQtable.data[b1] > paramQtable.data[b])
          b = b1; 
        b1++;
      } 
      float f = paramQtable.data[b] / JPEGQTable.K1Div2Luminance.getTable()[b];
      JPEGQTable jPEGQTable = JPEGQTable.K2Div2Chrominance.getScaledInstance(f, true);
      qtable = new Qtable(jPEGQTable, 1);
    } 
    return qtable;
  }
  
  Qtable getQtableFromNode(Node paramNode) throws IIOInvalidTreeException { return new Qtable(paramNode); }
  
  class Qtable implements Cloneable {
    int elementPrecision;
    
    int tableID;
    
    final int QTABLE_SIZE = 64;
    
    int[] data;
    
    private final int[] zigzag = { 
        0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 
        7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 
        25, 30, 41, 43, 9, 11, 18, 24, 31, 40, 
        44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 
        20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 
        37, 47, 50, 56, 59, 61, 35, 36, 48, 49, 
        57, 58, 62, 63 };
    
    Qtable(boolean param1Boolean, float param1Float) {
      this.elementPrecision = 0;
      JPEGQTable jPEGQTable = null;
      if (param1Boolean) {
        this.tableID = 0;
        jPEGQTable = JPEGQTable.K1Div2Luminance;
      } else {
        this.tableID = 1;
        jPEGQTable = JPEGQTable.K2Div2Chrominance;
      } 
      if (param1Float != 0.75F) {
        param1Float = JPEG.convertToLinearQuality(param1Float);
        if (param1Boolean) {
          jPEGQTable = JPEGQTable.K1Luminance.getScaledInstance(param1Float, true);
        } else {
          jPEGQTable = JPEGQTable.K2Div2Chrominance.getScaledInstance(param1Float, true);
        } 
      } 
      this.data = jPEGQTable.getTable();
    }
    
    Qtable(JPEGBuffer param1JPEGBuffer) throws IIOException {
      this.elementPrecision = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr] >>> 4;
      this.tableID = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr++] & 0xF;
      if (this.elementPrecision != 0)
        throw new IIOException("Unsupported element precision"); 
      this.data = new int[64];
      for (byte b = 0; b < 64; b++)
        this.data[b] = param1JPEGBuffer.buf[param1JPEGBuffer.bufPtr + this.zigzag[b]] & 0xFF; 
      param1JPEGBuffer.bufPtr += 64;
    }
    
    Qtable(JPEGQTable param1JPEGQTable, int param1Int) {
      this.elementPrecision = 0;
      this.tableID = param1Int;
      this.data = param1JPEGQTable.getTable();
    }
    
    Qtable(Node param1Node) throws IIOInvalidTreeException {
      if (param1Node.getNodeName().equals("dqtable")) {
        NamedNodeMap namedNodeMap = param1Node.getAttributes();
        int i = namedNodeMap.getLength();
        if (i < 1 || i > 2)
          throw new IIOInvalidTreeException("dqtable node must have 1 or 2 attributes", param1Node); 
        this.elementPrecision = 0;
        this.tableID = MarkerSegment.getAttributeValue(param1Node, namedNodeMap, "qtableId", 0, 3, true);
        if (param1Node instanceof IIOMetadataNode) {
          IIOMetadataNode iIOMetadataNode = (IIOMetadataNode)param1Node;
          JPEGQTable jPEGQTable = (JPEGQTable)iIOMetadataNode.getUserObject();
          if (jPEGQTable == null)
            throw new IIOInvalidTreeException("dqtable node must have user object", param1Node); 
          this.data = jPEGQTable.getTable();
        } else {
          throw new IIOInvalidTreeException("dqtable node must have user object", param1Node);
        } 
      } else {
        throw new IIOInvalidTreeException("Invalid node, expected dqtable", param1Node);
      } 
    }
    
    protected Object clone() {
      Qtable qtable = null;
      try {
        qtable = (Qtable)super.clone();
      } catch (CloneNotSupportedException cloneNotSupportedException) {}
      if (this.data != null)
        qtable.data = (int[])this.data.clone(); 
      return qtable;
    }
    
    IIOMetadataNode getNativeNode() {
      IIOMetadataNode iIOMetadataNode = new IIOMetadataNode("dqtable");
      iIOMetadataNode.setAttribute("elementPrecision", Integer.toString(this.elementPrecision));
      iIOMetadataNode.setAttribute("qtableId", Integer.toString(this.tableID));
      iIOMetadataNode.setUserObject(new JPEGQTable(this.data));
      return iIOMetadataNode;
    }
    
    void print() {
      System.out.println("Table id: " + Integer.toString(this.tableID));
      System.out.println("Element precision: " + Integer.toString(this.elementPrecision));
      (new JPEGQTable(this.data)).toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\imageio\plugins\jpeg\DQTMarkerSegment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */