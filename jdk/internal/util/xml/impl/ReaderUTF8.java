package jdk.internal.util.xml.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class ReaderUTF8 extends Reader {
  private InputStream is;
  
  public ReaderUTF8(InputStream paramInputStream) { this.is = paramInputStream; }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    byte b;
    for (b = 0; b < paramInt2; b++) {
      int i;
      if ((i = this.is.read()) < 0)
        return b ? b : -1; 
      switch (i & 0xF0) {
        case 192:
        case 208:
          paramArrayOfChar[paramInt1++] = (char)((i & 0x1F) << 6 | this.is.read() & 0x3F);
          break;
        case 224:
          paramArrayOfChar[paramInt1++] = (char)((i & 0xF) << 12 | (this.is.read() & 0x3F) << 6 | this.is.read() & 0x3F);
          break;
        case 240:
          throw new UnsupportedEncodingException("UTF-32 (or UCS-4) encoding not supported.");
        default:
          paramArrayOfChar[paramInt1++] = (char)i;
          break;
      } 
    } 
    return b;
  }
  
  public int read() throws IOException {
    int i;
    if ((i = this.is.read()) < 0)
      return -1; 
    switch (i & 0xF0) {
      case 192:
      case 208:
        i = (i & 0x1F) << 6 | this.is.read() & 0x3F;
        break;
      case 224:
        i = (i & 0xF) << 12 | (this.is.read() & 0x3F) << 6 | this.is.read() & 0x3F;
        break;
      case 240:
        throw new UnsupportedEncodingException();
    } 
    return i;
  }
  
  public void close() throws IOException { this.is.close(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\interna\\util\xml\impl\ReaderUTF8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */