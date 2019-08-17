package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

final class LineInputStream extends FilterInputStream {
  private char[] lineBuffer = null;
  
  private static int MAX_INCR = 1048576;
  
  public LineInputStream(InputStream paramInputStream) { super(paramInputStream); }
  
  public String readLine() throws IOException {
    char[] arrayOfChar = this.lineBuffer;
    if (arrayOfChar == null)
      arrayOfChar = this.lineBuffer = new char[128]; 
    int j = arrayOfChar.length;
    int k = 0;
    int i;
    while ((i = this.in.read()) != -1 && i != 10) {
      if (i == 13) {
        boolean bool = false;
        if (this.in.markSupported())
          this.in.mark(2); 
        int m = this.in.read();
        if (m == 13) {
          bool = true;
          m = this.in.read();
        } 
        if (m != 10) {
          if (this.in.markSupported()) {
            this.in.reset();
            break;
          } 
          if (!(this.in instanceof PushbackInputStream))
            this.in = new PushbackInputStream(this.in, 2); 
          if (m != -1)
            ((PushbackInputStream)this.in).unread(m); 
          if (bool)
            ((PushbackInputStream)this.in).unread(13); 
        } 
        break;
      } 
      if (--j < 0) {
        if (arrayOfChar.length < MAX_INCR) {
          arrayOfChar = new char[arrayOfChar.length * 2];
        } else {
          arrayOfChar = new char[arrayOfChar.length + MAX_INCR];
        } 
        j = arrayOfChar.length - k - 1;
        System.arraycopy(this.lineBuffer, 0, arrayOfChar, 0, k);
        this.lineBuffer = arrayOfChar;
      } 
      arrayOfChar[k++] = (char)i;
    } 
    return (i == -1 && k == 0) ? null : String.copyValueOf(arrayOfChar, 0, k);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\LineInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */