package jdk.internal.util.xml.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class ReaderUTF16 extends Reader {
  private InputStream is;
  
  private char bo;
  
  public ReaderUTF16(InputStream paramInputStream, char paramChar) {
    switch (paramChar) {
      case 'l':
      case 'b':
        break;
      default:
        throw new IllegalArgumentException("");
    } 
    this.bo = paramChar;
    this.is = paramInputStream;
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    byte b = 0;
    if (this.bo == 'b') {
      while (b < paramInt2) {
        int i;
        if ((i = this.is.read()) < 0)
          return b ? b : -1; 
        paramArrayOfChar[paramInt1++] = (char)(i << 8 | this.is.read() & 0xFF);
        b++;
      } 
    } else {
      while (b < paramInt2) {
        int i;
        if ((i = this.is.read()) < 0)
          return (b != 0) ? b : -1; 
        paramArrayOfChar[paramInt1++] = (char)(this.is.read() << 8 | i & 0xFF);
        b++;
      } 
    } 
    return b;
  }
  
  public int read() throws IOException {
    int i;
    if ((i = this.is.read()) < 0)
      return -1; 
    if (this.bo == 'b') {
      i = (char)(i << 8 | this.is.read() & 0xFF);
    } else {
      i = (char)(this.is.read() << 8 | i & 0xFF);
    } 
    return i;
  }
  
  public void close() throws IOException { this.is.close(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\interna\\util\xml\impl\ReaderUTF16.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */