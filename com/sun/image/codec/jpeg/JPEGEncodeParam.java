package com.sun.image.codec.jpeg;

public interface JPEGEncodeParam extends Cloneable, JPEGDecodeParam {
  Object clone();
  
  void setHorizontalSubsampling(int paramInt1, int paramInt2);
  
  void setVerticalSubsampling(int paramInt1, int paramInt2);
  
  void setQTable(int paramInt, JPEGQTable paramJPEGQTable);
  
  void setDCHuffmanTable(int paramInt, JPEGHuffmanTable paramJPEGHuffmanTable);
  
  void setACHuffmanTable(int paramInt, JPEGHuffmanTable paramJPEGHuffmanTable);
  
  void setDCHuffmanComponentMapping(int paramInt1, int paramInt2);
  
  void setACHuffmanComponentMapping(int paramInt1, int paramInt2);
  
  void setQTableComponentMapping(int paramInt1, int paramInt2);
  
  void setImageInfoValid(boolean paramBoolean);
  
  void setTableInfoValid(boolean paramBoolean);
  
  void setMarkerData(int paramInt, byte[][] paramArrayOfByte);
  
  void addMarkerData(int paramInt, byte[] paramArrayOfByte);
  
  void setRestartInterval(int paramInt);
  
  void setDensityUnit(int paramInt);
  
  void setXDensity(int paramInt);
  
  void setYDensity(int paramInt);
  
  void setQuality(float paramFloat, boolean paramBoolean);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\image\codec\jpeg\JPEGEncodeParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */