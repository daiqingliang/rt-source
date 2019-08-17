package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import java.io.InputStream;
import java.io.OutputStream;

public interface SharedInputStream {
  long getPosition();
  
  InputStream newStream(long paramLong1, long paramLong2);
  
  void writeTo(long paramLong1, long paramLong2, OutputStream paramOutputStream);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\SharedInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */