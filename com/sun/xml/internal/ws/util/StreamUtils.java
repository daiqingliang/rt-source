package com.sun.xml.internal.ws.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
  public static InputStream hasSomeData(InputStream paramInputStream) {
    if (paramInputStream != null)
      try {
        if (paramInputStream.available() < 1) {
          if (!paramInputStream.markSupported())
            paramInputStream = new BufferedInputStream(paramInputStream); 
          paramInputStream.mark(1);
          if (paramInputStream.read() != -1) {
            paramInputStream.reset();
          } else {
            paramInputStream = null;
          } 
        } 
      } catch (IOException iOException) {
        paramInputStream = null;
      }  
    return paramInputStream;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\StreamUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */