package sun.net;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TelnetInputStream extends FilterInputStream {
  boolean stickyCRLF = false;
  
  boolean seenCR = false;
  
  public boolean binaryMode = false;
  
  public TelnetInputStream(InputStream paramInputStream, boolean paramBoolean) {
    super(paramInputStream);
    this.binaryMode = paramBoolean;
  }
  
  public void setStickyCRLF(boolean paramBoolean) { this.stickyCRLF = paramBoolean; }
  
  public int read() throws IOException {
    if (this.binaryMode)
      return super.read(); 
    if (this.seenCR) {
      this.seenCR = false;
      return 10;
    } 
    int i;
    if ((i = super.read()) == 13) {
      switch (i = super.read()) {
        default:
          throw new TelnetProtocolException("misplaced CR in input");
        case 0:
          return 13;
        case 10:
          break;
      } 
      if (this.stickyCRLF) {
        this.seenCR = true;
        return 13;
      } 
      return 10;
    } 
    return i;
  }
  
  public int read(byte[] paramArrayOfByte) throws IOException { return read(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (this.binaryMode)
      return super.read(paramArrayOfByte, paramInt1, paramInt2); 
    int i = paramInt1;
    while (--paramInt2 >= 0) {
      int j = read();
      if (j == -1)
        break; 
      paramArrayOfByte[paramInt1++] = (byte)j;
    } 
    return (paramInt1 > i) ? (paramInt1 - i) : -1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\TelnetInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */