package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

interface CodingMethod {
  void readArrayFrom(InputStream paramInputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2) throws IOException;
  
  void writeArrayTo(OutputStream paramOutputStream, int[] paramArrayOfInt, int paramInt1, int paramInt2) throws IOException;
  
  byte[] getMetaCoding(Coding paramCoding);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\CodingMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */