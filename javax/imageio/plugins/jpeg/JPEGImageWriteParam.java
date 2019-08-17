package javax.imageio.plugins.jpeg;

import java.util.Locale;
import javax.imageio.ImageWriteParam;

public class JPEGImageWriteParam extends ImageWriteParam {
  private JPEGQTable[] qTables = null;
  
  private JPEGHuffmanTable[] DCHuffmanTables = null;
  
  private JPEGHuffmanTable[] ACHuffmanTables = null;
  
  private boolean optimizeHuffman = false;
  
  private String[] compressionNames = { "JPEG" };
  
  private float[] qualityVals = { 0.0F, 0.3F, 0.75F, 1.0F };
  
  private String[] qualityDescs = { "Low quality", "Medium quality", "Visually lossless" };
  
  public JPEGImageWriteParam(Locale paramLocale) { super(paramLocale); }
  
  public void unsetCompression() {
    if (getCompressionMode() != 2)
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!"); 
    this.compressionQuality = 0.75F;
  }
  
  public boolean isCompressionLossless() {
    if (getCompressionMode() != 2)
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!"); 
    return false;
  }
  
  public String[] getCompressionQualityDescriptions() {
    if (getCompressionMode() != 2)
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!"); 
    if (getCompressionTypes() != null && getCompressionType() == null)
      throw new IllegalStateException("No compression type set!"); 
    return (String[])this.qualityDescs.clone();
  }
  
  public float[] getCompressionQualityValues() {
    if (getCompressionMode() != 2)
      throw new IllegalStateException("Compression mode not MODE_EXPLICIT!"); 
    if (getCompressionTypes() != null && getCompressionType() == null)
      throw new IllegalStateException("No compression type set!"); 
    return (float[])this.qualityVals.clone();
  }
  
  public boolean areTablesSet() { return (this.qTables != null); }
  
  public void setEncodeTables(JPEGQTable[] paramArrayOfJPEGQTable, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable1, JPEGHuffmanTable[] paramArrayOfJPEGHuffmanTable2) {
    if (paramArrayOfJPEGQTable == null || paramArrayOfJPEGHuffmanTable1 == null || paramArrayOfJPEGHuffmanTable2 == null || paramArrayOfJPEGQTable.length > 4 || paramArrayOfJPEGHuffmanTable1.length > 4 || paramArrayOfJPEGHuffmanTable2.length > 4 || paramArrayOfJPEGHuffmanTable1.length != paramArrayOfJPEGHuffmanTable2.length)
      throw new IllegalArgumentException("Invalid JPEG table arrays"); 
    this.qTables = (JPEGQTable[])paramArrayOfJPEGQTable.clone();
    this.DCHuffmanTables = (JPEGHuffmanTable[])paramArrayOfJPEGHuffmanTable1.clone();
    this.ACHuffmanTables = (JPEGHuffmanTable[])paramArrayOfJPEGHuffmanTable2.clone();
  }
  
  public void unsetEncodeTables() {
    this.qTables = null;
    this.DCHuffmanTables = null;
    this.ACHuffmanTables = null;
  }
  
  public JPEGQTable[] getQTables() { return (this.qTables != null) ? (JPEGQTable[])this.qTables.clone() : null; }
  
  public JPEGHuffmanTable[] getDCHuffmanTables() { return (this.DCHuffmanTables != null) ? (JPEGHuffmanTable[])this.DCHuffmanTables.clone() : null; }
  
  public JPEGHuffmanTable[] getACHuffmanTables() { return (this.ACHuffmanTables != null) ? (JPEGHuffmanTable[])this.ACHuffmanTables.clone() : null; }
  
  public void setOptimizeHuffmanTables(boolean paramBoolean) { this.optimizeHuffman = paramBoolean; }
  
  public boolean getOptimizeHuffmanTables() { return this.optimizeHuffman; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\plugins\jpeg\JPEGImageWriteParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */