package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.OutputStream;

public class BEncoderStream extends BASE64EncoderStream {
  public BEncoderStream(OutputStream paramOutputStream) { super(paramOutputStream, 2147483647); }
  
  public static int encodedLength(byte[] paramArrayOfByte) { return (paramArrayOfByte.length + 2) / 3 * 4; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mim\\util\BEncoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */