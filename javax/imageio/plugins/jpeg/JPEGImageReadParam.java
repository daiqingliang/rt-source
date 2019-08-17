package javax.imageio.plugins.jpeg;

import javax.imageio.ImageReadParam;

public class JPEGImageReadParam extends ImageReadParam {
  private JPEGQTable[] qTables = null;
  
  private JPEGHuffmanTable[] DCHuffmanTables = null;
  
  private JPEGHuffmanTable[] ACHuffmanTables = null;
  
  public boolean areTablesSet() { return (this.qTables != null); }
  
  public void setDecodeTables(JPEGQTable[] paramArrayOfJPEGQTable, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable2) {
    if (paramArrayOfJPEGQTable == null || paramArrayOfJPEGHuffmanTable1 == null || paramArrayOfJPEGHuffmanTable2 == null || paramArrayOfJPEGQTable.length > 4 || paramArrayOfJPEGHuffmanTable1.length > 4 || paramArrayOfJPEGHuffmanTable2.length > 4 || paramArrayOfJPEGHuffmanTable1.length != paramArrayOfJPEGHuffmanTable2.length)
      throw new IllegalArgumentException("Invalid JPEG table arrays"); 
    this.qTables = (JPEGQTable[])paramArrayOfJPEGQTable.clone();
    this.DCHuffmanTables = (JPEGHuffmanTable[])paramArrayOfJPEGHuffmanTable1.clone();
    this.ACHuffmanTables = (JPEGHuffmanTable[])paramArrayOfJPEGHuffmanTable2.clone();
  }
  
  public void unsetDecodeTables() {
    this.qTables = null;
    this.DCHuffmanTables = null;
    this.ACHuffmanTables = null;
  }
  
  public JPEGQTable[] getQTables() { return (this.qTables != null) ? (JPEGQTable[])this.qTables.clone() : null; }
  
  public JPEGHuffmanTable[] getDCHuffmanTables() { return (this.DCHuffmanTables != null) ? (JPEGHuffmanTable[])this.DCHuffmanTables.clone() : null; }
  
  public JPEGHuffmanTable[] getACHuffmanTables() { return (this.ACHuffmanTables != null) ? (JPEGHuffmanTable[])this.ACHuffmanTables.clone() : null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\plugins\jpeg\JPEGImageReadParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */