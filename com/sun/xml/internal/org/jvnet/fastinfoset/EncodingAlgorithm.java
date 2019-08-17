package com.sun.xml.internal.org.jvnet.fastinfoset;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface EncodingAlgorithm {
  Object decodeFromBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws EncodingAlgorithmException;
  
  Object decodeFromInputStream(InputStream paramInputStream) throws EncodingAlgorithmException, IOException;
  
  void encodeToOutputStream(Object paramObject, OutputStream paramOutputStream) throws EncodingAlgorithmException, IOException;
  
  Object convertFromCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws EncodingAlgorithmException;
  
  void convertToCharacters(Object paramObject, StringBuffer paramStringBuffer) throws EncodingAlgorithmException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\EncodingAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */