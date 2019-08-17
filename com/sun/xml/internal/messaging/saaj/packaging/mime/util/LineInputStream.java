package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public final class LineInputStream extends FilterInputStream {
  private char[] lineBuffer = null;
  
  public LineInputStream(InputStream paramInputStream) { super(paramInputStream); }
  
  public String readLine() throws IOException {
    InputStream inputStream = this.in;
    char[] arrayOfChar = this.lineBuffer;
    if (arrayOfChar == null)
      arrayOfChar = this.lineBuffer = new char[128]; 
    int j = arrayOfChar.length;
    int k = 0;
    int i;
    while ((i = inputStream.read()) != -1 && i != 10) {
      if (i == 13) {
        int m = inputStream.read();
        if (m == 13)
          m = inputStream.read(); 
        if (m != 10) {
          if (!(inputStream instanceof PushbackInputStream))
            inputStream = this.in = new PushbackInputStream(inputStream); 
          ((PushbackInputStream)inputStream).unread(m);
        } 
        break;
      } 
      if (--j < 0) {
        arrayOfChar = new char[k + ''];
        j = arrayOfChar.length - k - 1;
        System.arraycopy(this.lineBuffer, 0, arrayOfChar, 0, k);
        this.lineBuffer = arrayOfChar;
      } 
      arrayOfChar[k++] = (char)i;
    } 
    return (i == -1 && k == 0) ? null : String.copyValueOf(arrayOfChar, 0, k);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mim\\util\LineInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */