package sun.awt.image.codec;

import com.sun.image.codec.jpeg.JPEGDecodeParam;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGHuffmanTable;
import com.sun.image.codec.jpeg.JPEGQTable;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.util.Enumeration;
import java.util.Vector;

public class JPEGParam implements JPEGEncodeParam, Cloneable {
  private static int[] defComponents = { 
      -1, 1, 3, 3, 4, 3, 4, 4, 4, 4, 
      4, 4 };
  
  private static int[][] stdCompMapping = { 
      { 0, 0, 0, 0 }, { 0 }, { 0, 0, 0 }, { 0, 1, 1 }, { 0, 0, 0, 0 }, { 0, 1, 1 }, { 0, 0, 0, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 0 }, { 0, 1, 1, 0 }, 
      { 0, 1, 1, 0 }, { 0, 1, 1, 0 } };
  
  private static int[][] stdSubsample = { 
      { 1, 1, 1, 1 }, { 1 }, { 1, 1, 1 }, { 1, 2, 2 }, { 1, 1, 1, 1 }, { 1, 2, 2 }, { 1, 1, 1, 1 }, { 1, 2, 2, 1 }, { 1, 1, 1, 1 }, { 1, 2, 2, 1 }, 
      { 1, 2, 2, 1 }, { 1, 2, 2, 1 } };
  
  private int width;
  
  private int height;
  
  private int encodedColorID;
  
  private int numComponents;
  
  private byte[][][] appMarkers;
  
  private byte[][] comMarker;
  
  private boolean imageInfoValid;
  
  private boolean tableInfoValid;
  
  private int[] horizontalSubsampling;
  
  private int[] verticalSubsampling;
  
  private JPEGQTable[] qTables;
  
  private int[] qTableMapping;
  
  private JPEGHuffmanTable[] dcHuffTables;
  
  private int[] dcHuffMapping;
  
  private JPEGHuffmanTable[] acHuffTables;
  
  private int[] acHuffMapping;
  
  private int restartInterval;
  
  private static final int app0Length = 14;
  
  public JPEGParam(int paramInt) { this(paramInt, defComponents[paramInt]); }
  
  public JPEGParam(JPEGDecodeParam paramJPEGDecodeParam) {
    this(paramJPEGDecodeParam.getEncodedColorID(), paramJPEGDecodeParam.getNumComponents());
    copy(paramJPEGDecodeParam);
  }
  
  public JPEGParam(JPEGEncodeParam paramJPEGEncodeParam) {
    this(paramJPEGEncodeParam.getEncodedColorID(), paramJPEGEncodeParam.getNumComponents());
    copy(paramJPEGEncodeParam);
  }
  
  public JPEGParam(int paramInt1, int paramInt2) {
    if (paramInt1 != 0 && paramInt2 != defComponents[paramInt1])
      throw new IllegalArgumentException("NumComponents not in sync with COLOR_ID"); 
    this.qTables = new JPEGQTable[4];
    this.acHuffTables = new JPEGHuffmanTable[4];
    this.dcHuffTables = new JPEGHuffmanTable[4];
    for (byte b = 0; b < 4; b++) {
      this.qTables[b] = null;
      this.dcHuffTables[b] = null;
      this.acHuffTables[b] = null;
    } 
    this.comMarker = (byte[][])null;
    this.appMarkers = new byte[16][][];
    this.numComponents = paramInt2;
    setDefaults(paramInt1);
  }
  
  private void copy(JPEGDecodeParam paramJPEGDecodeParam) {
    if (getEncodedColorID() != paramJPEGDecodeParam.getEncodedColorID())
      throw new IllegalArgumentException("Argument to copy must match current COLOR_ID"); 
    if (getNumComponents() != paramJPEGDecodeParam.getNumComponents())
      throw new IllegalArgumentException("Argument to copy must match in number of components"); 
    setWidth(paramJPEGDecodeParam.getWidth());
    setHeight(paramJPEGDecodeParam.getHeight());
    char c;
    for (c = 'à'; c < 'ï'; c++)
      setMarkerData(c, copyArrays(paramJPEGDecodeParam.getMarkerData(c))); 
    setMarkerData(254, copyArrays(paramJPEGDecodeParam.getMarkerData(254)));
    setTableInfoValid(paramJPEGDecodeParam.isTableInfoValid());
    setImageInfoValid(paramJPEGDecodeParam.isImageInfoValid());
    setRestartInterval(paramJPEGDecodeParam.getRestartInterval());
    for (c = Character.MIN_VALUE; c < '\004'; c++) {
      setDCHuffmanTable(c, paramJPEGDecodeParam.getDCHuffmanTable(c));
      setACHuffmanTable(c, paramJPEGDecodeParam.getACHuffmanTable(c));
      setQTable(c, paramJPEGDecodeParam.getQTable(c));
    } 
    for (c = Character.MIN_VALUE; c < paramJPEGDecodeParam.getNumComponents(); c++) {
      setDCHuffmanComponentMapping(c, paramJPEGDecodeParam.getDCHuffmanComponentMapping(c));
      setACHuffmanComponentMapping(c, paramJPEGDecodeParam.getACHuffmanComponentMapping(c));
      setQTableComponentMapping(c, paramJPEGDecodeParam.getQTableComponentMapping(c));
      setHorizontalSubsampling(c, paramJPEGDecodeParam.getHorizontalSubsampling(c));
      setVerticalSubsampling(c, paramJPEGDecodeParam.getVerticalSubsampling(c));
    } 
  }
  
  private void copy(JPEGEncodeParam paramJPEGEncodeParam) { copy(paramJPEGEncodeParam); }
  
  protected void setDefaults(int paramInt) {
    this.encodedColorID = paramInt;
    this.restartInterval = 0;
    boolean bool = false;
    switch (this.numComponents) {
      case 1:
        if (this.encodedColorID == 1 || this.encodedColorID == 0)
          bool = true; 
        break;
      case 3:
        if (this.encodedColorID == 3)
          bool = true; 
        break;
      case 4:
        if (this.encodedColorID == 4)
          bool = true; 
        break;
    } 
    if (bool)
      addMarkerData(224, createDefaultAPP0Marker()); 
    setTableInfoValid(true);
    setImageInfoValid(true);
    this.dcHuffTables[0] = JPEGHuffmanTable.StdDCLuminance;
    this.dcHuffTables[1] = JPEGHuffmanTable.StdDCChrominance;
    this.dcHuffMapping = new int[getNumComponents()];
    System.arraycopy(stdCompMapping[this.encodedColorID], 0, this.dcHuffMapping, 0, getNumComponents());
    this.acHuffTables[0] = JPEGHuffmanTable.StdACLuminance;
    this.acHuffTables[1] = JPEGHuffmanTable.StdACChrominance;
    this.acHuffMapping = new int[getNumComponents()];
    System.arraycopy(stdCompMapping[this.encodedColorID], 0, this.acHuffMapping, 0, getNumComponents());
    this.qTables[0] = JPEGQTable.StdLuminance.getScaledInstance(0.5F, true);
    this.qTables[1] = JPEGQTable.StdChrominance.getScaledInstance(0.5F, true);
    this.qTableMapping = new int[getNumComponents()];
    System.arraycopy(stdCompMapping[this.encodedColorID], 0, this.qTableMapping, 0, getNumComponents());
    this.horizontalSubsampling = new int[getNumComponents()];
    System.arraycopy(stdSubsample[this.encodedColorID], 0, this.horizontalSubsampling, 0, getNumComponents());
    this.verticalSubsampling = new int[getNumComponents()];
    System.arraycopy(stdSubsample[this.encodedColorID], 0, this.verticalSubsampling, 0, getNumComponents());
  }
  
  public Object clone() {
    JPEGParam jPEGParam = new JPEGParam(getEncodedColorID(), getNumComponents());
    jPEGParam.copy(this);
    return jPEGParam;
  }
  
  public int getWidth() { return this.width; }
  
  public int getHeight() { return this.height; }
  
  public void setWidth(int paramInt) { this.width = paramInt; }
  
  public void setHeight(int paramInt) { this.height = paramInt; }
  
  public int getHorizontalSubsampling(int paramInt) {
    if (paramInt < 0 || paramInt >= getNumComponents())
      throw new IllegalArgumentException("Component must be between 0 and number of components"); 
    return this.horizontalSubsampling[paramInt];
  }
  
  public int getVerticalSubsampling(int paramInt) {
    if (paramInt < 0 || paramInt >= getNumComponents())
      throw new IllegalArgumentException("Component must be between 0 and number of components"); 
    return this.verticalSubsampling[paramInt];
  }
  
  public void setHorizontalSubsampling(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 >= getNumComponents())
      throw new IllegalArgumentException("Component must be between 0 and number of components: " + paramInt1); 
    if (paramInt2 <= 0)
      throw new IllegalArgumentException("SubSample factor must be positive: " + paramInt2); 
    this.horizontalSubsampling[paramInt1] = paramInt2;
  }
  
  public void setVerticalSubsampling(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 >= getNumComponents())
      throw new IllegalArgumentException("Component must be between 0 and number of components"); 
    if (paramInt2 <= 0)
      throw new IllegalArgumentException("SubSample factor must be positive."); 
    this.verticalSubsampling[paramInt1] = paramInt2;
  }
  
  public JPEGQTable getQTable(int paramInt) {
    if (paramInt < 0 || paramInt >= 4)
      throw new IllegalArgumentException("tableNum must be between 0 and 3."); 
    return this.qTables[paramInt];
  }
  
  public JPEGQTable getQTableForComponent(int paramInt) {
    if (paramInt < 0 || paramInt >= this.qTableMapping.length)
      throw new IllegalArgumentException("Component must be between 0 and number of components"); 
    return getQTable(this.qTableMapping[paramInt]);
  }
  
  public JPEGHuffmanTable getDCHuffmanTable(int paramInt) {
    if (paramInt < 0 || paramInt >= 4)
      throw new IllegalArgumentException("tableNum must be 0-3."); 
    return this.dcHuffTables[paramInt];
  }
  
  public JPEGHuffmanTable getDCHuffmanTableForComponent(int paramInt) {
    if (paramInt < 0 || paramInt >= this.dcHuffMapping.length)
      throw new IllegalArgumentException("Component must be between 0 and number of components"); 
    return getDCHuffmanTable(this.dcHuffMapping[paramInt]);
  }
  
  public JPEGHuffmanTable getACHuffmanTable(int paramInt) {
    if (paramInt < 0 || paramInt >= 4)
      throw new IllegalArgumentException("tableNum must be 0-3."); 
    return this.acHuffTables[paramInt];
  }
  
  public JPEGHuffmanTable getACHuffmanTableForComponent(int paramInt) {
    if (paramInt < 0 || paramInt >= this.acHuffMapping.length)
      throw new IllegalArgumentException("Component must be between 0 and number of components"); 
    return getACHuffmanTable(this.acHuffMapping[paramInt]);
  }
  
  public void setQTable(int paramInt, JPEGQTable paramJPEGQTable) {
    if (paramInt < 0 || paramInt >= 4)
      throw new IllegalArgumentException("tableNum must be between 0 and 3."); 
    this.qTables[paramInt] = paramJPEGQTable;
  }
  
  public void setDCHuffmanTable(int paramInt, JPEGHuffmanTable paramJPEGHuffmanTable) {
    if (paramInt < 0 || paramInt >= 4)
      throw new IllegalArgumentException("tableNum must be 0, 1, 2, or 3."); 
    this.dcHuffTables[paramInt] = paramJPEGHuffmanTable;
  }
  
  public void setACHuffmanTable(int paramInt, JPEGHuffmanTable paramJPEGHuffmanTable) {
    if (paramInt < 0 || paramInt >= 4)
      throw new IllegalArgumentException("tableNum must be 0, 1, 2, or 3."); 
    this.acHuffTables[paramInt] = paramJPEGHuffmanTable;
  }
  
  public int getDCHuffmanComponentMapping(int paramInt) {
    if (paramInt < 0 || paramInt >= getNumComponents())
      throw new IllegalArgumentException("Requested Component doesn't exist."); 
    return this.dcHuffMapping[paramInt];
  }
  
  public int getACHuffmanComponentMapping(int paramInt) {
    if (paramInt < 0 || paramInt >= getNumComponents())
      throw new IllegalArgumentException("Requested Component doesn't exist."); 
    return this.acHuffMapping[paramInt];
  }
  
  public int getQTableComponentMapping(int paramInt) {
    if (paramInt < 0 || paramInt >= getNumComponents())
      throw new IllegalArgumentException("Requested Component doesn't exist."); 
    return this.qTableMapping[paramInt];
  }
  
  public void setDCHuffmanComponentMapping(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 >= getNumComponents())
      throw new IllegalArgumentException("Given Component doesn't exist."); 
    if (paramInt2 < 0 || paramInt2 >= 4)
      throw new IllegalArgumentException("Tables must be 0, 1, 2, or 3."); 
    this.dcHuffMapping[paramInt1] = paramInt2;
  }
  
  public void setACHuffmanComponentMapping(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 >= getNumComponents())
      throw new IllegalArgumentException("Given Component doesn't exist."); 
    if (paramInt2 < 0 || paramInt2 >= 4)
      throw new IllegalArgumentException("Tables must be 0, 1, 2, or 3."); 
    this.acHuffMapping[paramInt1] = paramInt2;
  }
  
  public void setQTableComponentMapping(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 >= getNumComponents())
      throw new IllegalArgumentException("Given Component doesn't exist."); 
    if (paramInt2 < 0 || paramInt2 >= 4)
      throw new IllegalArgumentException("Tables must be 0, 1, 2, or 3."); 
    this.qTableMapping[paramInt1] = paramInt2;
  }
  
  public boolean isImageInfoValid() { return this.imageInfoValid; }
  
  public void setImageInfoValid(boolean paramBoolean) { this.imageInfoValid = paramBoolean; }
  
  public boolean isTableInfoValid() { return this.tableInfoValid; }
  
  public void setTableInfoValid(boolean paramBoolean) { this.tableInfoValid = paramBoolean; }
  
  public boolean getMarker(int paramInt) {
    byte[][] arrayOfByte = (byte[][])null;
    if (paramInt == 254) {
      arrayOfByte = this.comMarker;
    } else if (paramInt >= 224 && paramInt <= 239) {
      arrayOfByte = this.appMarkers[paramInt - 224];
    } else {
      throw new IllegalArgumentException("Invalid Marker ID:" + paramInt);
    } 
    return (arrayOfByte == null) ? false : (!(arrayOfByte.length == 0));
  }
  
  public byte[][] getMarkerData(int paramInt) {
    if (paramInt == 254)
      return this.comMarker; 
    if (paramInt >= 224 && paramInt <= 239)
      return this.appMarkers[paramInt - 224]; 
    throw new IllegalArgumentException("Invalid Marker ID:" + paramInt);
  }
  
  public void setMarkerData(int paramInt, byte[][] paramArrayOfByte) {
    if (paramInt == 254) {
      this.comMarker = paramArrayOfByte;
    } else if (paramInt >= 224 && paramInt <= 239) {
      this.appMarkers[paramInt - 224] = paramArrayOfByte;
    } else {
      throw new IllegalArgumentException("Invalid Marker ID:" + paramInt);
    } 
  }
  
  public void addMarkerData(int paramInt, byte[] paramArrayOfByte) {
    if (paramArrayOfByte == null)
      return; 
    if (paramInt == 254) {
      this.comMarker = appendArray(this.comMarker, paramArrayOfByte);
    } else if (paramInt >= 224 && paramInt <= 239) {
      this.appMarkers[paramInt - 224] = appendArray(this.appMarkers[paramInt - 224], paramArrayOfByte);
    } else {
      throw new IllegalArgumentException("Invalid Marker ID:" + paramInt);
    } 
  }
  
  public int getEncodedColorID() { return this.encodedColorID; }
  
  public int getNumComponents() { return this.numComponents; }
  
  public static int getNumComponents(int paramInt) {
    if (paramInt < 0 || paramInt >= 12)
      throw new IllegalArgumentException("Invalid JPEGColorID."); 
    return defComponents[paramInt];
  }
  
  public int getRestartInterval() { return this.restartInterval; }
  
  public void setRestartInterval(int paramInt) { this.restartInterval = paramInt; }
  
  public int getDensityUnit() {
    if (!getMarker(224))
      throw new IllegalArgumentException("No APP0 marker present"); 
    byte[] arrayOfByte = findAPP0();
    if (arrayOfByte == null)
      throw new IllegalArgumentException("Can't understand APP0 marker that is present"); 
    return arrayOfByte[7];
  }
  
  public int getXDensity() {
    if (!getMarker(224))
      throw new IllegalArgumentException("No APP0 marker present"); 
    byte[] arrayOfByte = findAPP0();
    if (arrayOfByte == null)
      throw new IllegalArgumentException("Can't understand APP0 marker that is present"); 
    return arrayOfByte[8] << 8 | arrayOfByte[9] & 0xFF;
  }
  
  public int getYDensity() {
    if (!getMarker(224))
      throw new IllegalArgumentException("No APP0 marker present"); 
    byte[] arrayOfByte = findAPP0();
    if (arrayOfByte == null)
      throw new IllegalArgumentException("Can't understand APP0 marker that is present"); 
    return arrayOfByte[10] << 8 | arrayOfByte[11] & 0xFF;
  }
  
  public void setDensityUnit(int paramInt) {
    byte[] arrayOfByte = null;
    if (!getMarker(224)) {
      arrayOfByte = createDefaultAPP0Marker();
      addMarkerData(224, arrayOfByte);
    } else {
      arrayOfByte = findAPP0();
      if (arrayOfByte == null)
        throw new IllegalArgumentException("Can't understand APP0 marker that is present"); 
    } 
    arrayOfByte[7] = (byte)paramInt;
  }
  
  public void setXDensity(int paramInt) {
    byte[] arrayOfByte = null;
    if (!getMarker(224)) {
      arrayOfByte = createDefaultAPP0Marker();
      addMarkerData(224, arrayOfByte);
    } else {
      arrayOfByte = findAPP0();
      if (arrayOfByte == null)
        throw new IllegalArgumentException("Can't understand APP0 marker that is present"); 
    } 
    arrayOfByte[8] = (byte)(paramInt >>> 8 & 0xFF);
    arrayOfByte[9] = (byte)(paramInt & 0xFF);
  }
  
  public void setYDensity(int paramInt) {
    byte[] arrayOfByte = null;
    if (!getMarker(224)) {
      arrayOfByte = createDefaultAPP0Marker();
      addMarkerData(224, arrayOfByte);
    } else {
      arrayOfByte = findAPP0();
      if (arrayOfByte == null)
        throw new IllegalArgumentException("Can't understand APP0 marker that is present"); 
    } 
    arrayOfByte[10] = (byte)(paramInt >>> 8 & 0xFF);
    arrayOfByte[11] = (byte)(paramInt & 0xFF);
  }
  
  public void setQuality(float paramFloat, boolean paramBoolean) {
    double d = paramFloat;
    if (d <= 0.01D)
      d = 0.01D; 
    if (d > 1.0D)
      d = 1.0D; 
    if (d < 0.5D) {
      d = 0.5D / d;
    } else {
      d = 2.0D - d * 2.0D;
    } 
    this.qTableMapping = new int[getNumComponents()];
    System.arraycopy(stdCompMapping[this.encodedColorID], 0, this.qTableMapping, 0, getNumComponents());
    JPEGQTable jPEGQTable = JPEGQTable.StdLuminance;
    this.qTables[0] = jPEGQTable.getScaledInstance((float)d, paramBoolean);
    jPEGQTable = JPEGQTable.StdChrominance;
    this.qTables[1] = jPEGQTable.getScaledInstance((float)d, paramBoolean);
    this.qTables[2] = null;
    this.qTables[3] = null;
  }
  
  byte[] findAPP0() {
    byte[][] arrayOfByte = (byte[][])null;
    arrayOfByte = getMarkerData(224);
    if (arrayOfByte == null)
      return null; 
    for (byte b = 0; b < arrayOfByte.length; b++) {
      if (arrayOfByte[b] != null && checkAPP0(arrayOfByte[b]))
        return arrayOfByte[b]; 
    } 
    return null;
  }
  
  static boolean checkAPP0(byte[] paramArrayOfByte) { return (paramArrayOfByte.length < 14) ? false : ((paramArrayOfByte[0] != 74 || paramArrayOfByte[1] != 70 || paramArrayOfByte[2] != 73 || paramArrayOfByte[3] != 70 || paramArrayOfByte[4] != 0) ? false : (!(paramArrayOfByte[5] < 1))); }
  
  static byte[] createDefaultAPP0Marker() {
    byte[] arrayOfByte = new byte[14];
    arrayOfByte[0] = 74;
    arrayOfByte[1] = 70;
    arrayOfByte[2] = 73;
    arrayOfByte[3] = 70;
    arrayOfByte[4] = 0;
    arrayOfByte[5] = 1;
    arrayOfByte[6] = 1;
    arrayOfByte[7] = 0;
    arrayOfByte[8] = 0;
    arrayOfByte[9] = 1;
    arrayOfByte[10] = 0;
    arrayOfByte[11] = 1;
    arrayOfByte[12] = 0;
    arrayOfByte[13] = 0;
    return arrayOfByte;
  }
  
  static byte[] copyArray(byte[] paramArrayOfByte) {
    if (paramArrayOfByte == null)
      return null; 
    byte[] arrayOfByte = new byte[paramArrayOfByte.length];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramArrayOfByte.length);
    return arrayOfByte;
  }
  
  static byte[][] copyArrays(byte[][] paramArrayOfByte) {
    if (paramArrayOfByte == null)
      return (byte[][])null; 
    byte[][] arrayOfByte = new byte[paramArrayOfByte.length][];
    for (byte b = 0; b < paramArrayOfByte.length; b++) {
      if (paramArrayOfByte[b] != null)
        arrayOfByte[b] = copyArray(paramArrayOfByte[b]); 
    } 
    return arrayOfByte;
  }
  
  static byte[][] appendArray(byte[][] paramArrayOfByte, byte[] paramArrayOfByte1) {
    int i = 0;
    if (paramArrayOfByte != null)
      i = paramArrayOfByte.length; 
    byte[][] arrayOfByte = new byte[i + 1][];
    for (byte b = 0; b < i; b++)
      arrayOfByte[b] = paramArrayOfByte[b]; 
    if (paramArrayOfByte1 != null)
      arrayOfByte[i] = copyArray(paramArrayOfByte1); 
    return arrayOfByte;
  }
  
  static byte[][] buildArray(Vector paramVector) {
    if (paramVector == null)
      return (byte[][])null; 
    byte b = 0;
    byte[][] arrayOfByte = new byte[paramVector.size()][];
    Enumeration enumeration = paramVector.elements();
    while (enumeration.hasMoreElements()) {
      byte[] arrayOfByte1 = (byte[])enumeration.nextElement();
      if (arrayOfByte1 != null)
        arrayOfByte[b++] = copyArray(arrayOfByte1); 
    } 
    return arrayOfByte;
  }
  
  public static int getDefaultColorId(ColorModel paramColorModel) {
    boolean bool = paramColorModel.hasAlpha();
    ColorSpace colorSpace1 = paramColorModel.getColorSpace();
    ColorSpace colorSpace2 = null;
    switch (colorSpace1.getType()) {
      case 6:
        return 1;
      case 5:
        return bool ? 7 : 3;
      case 3:
        if (colorSpace2 == null)
          try {
            colorSpace2 = ColorSpace.getInstance(1002);
          } catch (IllegalArgumentException illegalArgumentException) {} 
        return (colorSpace1 == colorSpace2) ? (bool ? 10 : 5) : (bool ? 7 : 3);
      case 9:
        return 4;
    } 
    return 0;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\image\codec\JPEGParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */