package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.IOException;
import java.io.OutputStream;

public class QEncoderStream extends QPEncoderStream {
  private String specials;
  
  private static String WORD_SPECIALS = "=_?\"#$%&'(),.:;<>@[\\]^`{|}~";
  
  private static String TEXT_SPECIALS = "=_?";
  
  public QEncoderStream(OutputStream paramOutputStream, boolean paramBoolean) {
    super(paramOutputStream, 2147483647);
    this.specials = paramBoolean ? WORD_SPECIALS : TEXT_SPECIALS;
  }
  
  public void write(int paramInt) throws IOException {
    paramInt &= 0xFF;
    if (paramInt == 32) {
      output(95, false);
    } else if (paramInt < 32 || paramInt >= 127 || this.specials.indexOf(paramInt) >= 0) {
      output(paramInt, true);
    } else {
      output(paramInt, false);
    } 
  }
  
  public static int encodedLength(byte[] paramArrayOfByte, boolean paramBoolean) {
    byte b1 = 0;
    String str = paramBoolean ? WORD_SPECIALS : TEXT_SPECIALS;
    for (byte b2 = 0; b2 < paramArrayOfByte.length; b2++) {
      byte b = paramArrayOfByte[b2] & 0xFF;
      if (b < 32 || b >= Byte.MAX_VALUE || str.indexOf(b) >= 0) {
        b1 += true;
      } else {
        b1++;
      } 
    } 
    return b1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mim\\util\QEncoderStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */