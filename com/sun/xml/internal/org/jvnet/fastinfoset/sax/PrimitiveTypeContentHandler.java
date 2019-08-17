package com.sun.xml.internal.org.jvnet.fastinfoset.sax;

import org.xml.sax.SAXException;

public interface PrimitiveTypeContentHandler {
  void booleans(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2) throws SAXException;
  
  void bytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SAXException;
  
  void shorts(short[] paramArrayOfShort, int paramInt1, int paramInt2) throws SAXException;
  
  void ints(int[] paramArrayOfInt, int paramInt1, int paramInt2) throws SAXException;
  
  void longs(long[] paramArrayOfLong, int paramInt1, int paramInt2) throws SAXException;
  
  void floats(float[] paramArrayOfFloat, int paramInt1, int paramInt2) throws SAXException;
  
  void doubles(double[] paramArrayOfDouble, int paramInt1, int paramInt2) throws SAXException;
  
  void uuids(long[] paramArrayOfLong, int paramInt1, int paramInt2) throws SAXException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\sax\PrimitiveTypeContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */