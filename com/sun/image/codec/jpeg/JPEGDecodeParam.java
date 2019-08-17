package com.sun.image.codec.jpeg;

public interface JPEGDecodeParam extends Cloneable {
  public static final int COLOR_ID_UNKNOWN = 0;
  
  public static final int COLOR_ID_GRAY = 1;
  
  public static final int COLOR_ID_RGB = 2;
  
  public static final int COLOR_ID_YCbCr = 3;
  
  public static final int COLOR_ID_CMYK = 4;
  
  public static final int COLOR_ID_PYCC = 5;
  
  public static final int COLOR_ID_RGBA = 6;
  
  public static final int COLOR_ID_YCbCrA = 7;
  
  public static final int COLOR_ID_RGBA_INVERTED = 8;
  
  public static final int COLOR_ID_YCbCrA_INVERTED = 9;
  
  public static final int COLOR_ID_PYCCA = 10;
  
  public static final int COLOR_ID_YCCK = 11;
  
  public static final int NUM_COLOR_ID = 12;
  
  public static final int NUM_TABLES = 4;
  
  public static final int DENSITY_UNIT_ASPECT_RATIO = 0;
  
  public static final int DENSITY_UNIT_DOTS_INCH = 1;
  
  public static final int DENSITY_UNIT_DOTS_CM = 2;
  
  public static final int NUM_DENSITY_UNIT = 3;
  
  public static final int APP0_MARKER = 224;
  
  public static final int APP1_MARKER = 225;
  
  public static final int APP2_MARKER = 226;
  
  public static final int APP3_MARKER = 227;
  
  public static final int APP4_MARKER = 228;
  
  public static final int APP5_MARKER = 229;
  
  public static final int APP6_MARKER = 230;
  
  public static final int APP7_MARKER = 231;
  
  public static final int APP8_MARKER = 232;
  
  public static final int APP9_MARKER = 233;
  
  public static final int APPA_MARKER = 234;
  
  public static final int APPB_MARKER = 235;
  
  public static final int APPC_MARKER = 236;
  
  public static final int APPD_MARKER = 237;
  
  public static final int APPE_MARKER = 238;
  
  public static final int APPF_MARKER = 239;
  
  public static final int COMMENT_MARKER = 254;
  
  Object clone();
  
  int getWidth();
  
  int getHeight();
  
  int getHorizontalSubsampling(int paramInt);
  
  int getVerticalSubsampling(int paramInt);
  
  JPEGQTable getQTable(int paramInt);
  
  JPEGQTable getQTableForComponent(int paramInt);
  
  JPEGHuffmanTable getDCHuffmanTable(int paramInt);
  
  JPEGHuffmanTable getDCHuffmanTableForComponent(int paramInt);
  
  JPEGHuffmanTable getACHuffmanTable(int paramInt);
  
  JPEGHuffmanTable getACHuffmanTableForComponent(int paramInt);
  
  int getDCHuffmanComponentMapping(int paramInt);
  
  int getACHuffmanComponentMapping(int paramInt);
  
  int getQTableComponentMapping(int paramInt);
  
  boolean isImageInfoValid();
  
  boolean isTableInfoValid();
  
  boolean getMarker(int paramInt);
  
  byte[][] getMarkerData(int paramInt);
  
  int getEncodedColorID();
  
  int getNumComponents();
  
  int getRestartInterval();
  
  int getDensityUnit();
  
  int getXDensity();
  
  int getYDensity();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\image\codec\jpeg\JPEGDecodeParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */