package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.IOException;
import java.io.InputStream;

public class QDecoderStream extends QPDecoderStream {
  public QDecoderStream(InputStream paramInputStream) { super(paramInputStream); }
  
  public int read() throws IOException {
    int i = this.in.read();
    if (i == 95)
      return 32; 
    if (i == 61) {
      this.ba[0] = (byte)this.in.read();
      this.ba[1] = (byte)this.in.read();
      try {
        return ASCIIUtility.parseInt(this.ba, 0, 2, 16);
      } catch (NumberFormatException numberFormatException) {
        throw new IOException("Error in QP stream " + numberFormatException.getMessage());
      } 
    } 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mim\\util\QDecoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */