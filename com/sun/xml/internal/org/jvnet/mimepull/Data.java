package com.sun.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;

interface Data {
  int size();
  
  byte[] read();
  
  long writeTo(DataFile paramDataFile);
  
  Data createNext(DataHead paramDataHead, ByteBuffer paramByteBuffer);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\Data.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */