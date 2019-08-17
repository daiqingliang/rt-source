package sun.net.httpserver;

import java.io.IOException;
import java.io.InputStream;

class ChunkedInputStream extends LeftOverInputStream {
  private int remaining;
  
  private boolean needToReadHeader = true;
  
  static final char CR = '\r';
  
  static final char LF = '\n';
  
  private static final int MAX_CHUNK_HEADER_SIZE = 2050;
  
  ChunkedInputStream(ExchangeImpl paramExchangeImpl, InputStream paramInputStream) { super(paramExchangeImpl, paramInputStream); }
  
  private int numeric(char[] paramArrayOfChar, int paramInt) throws IOException {
    assert paramArrayOfChar.length >= paramInt;
    char c = Character.MIN_VALUE;
    for (byte b = 0; b < paramInt; b++) {
      char c1 = paramArrayOfChar[b];
      char c2 = Character.MIN_VALUE;
      if (c1 >= '0' && c1 <= '9') {
        c2 = c1 - '0';
      } else if (c1 >= 'a' && c1 <= 'f') {
        c2 = c1 - 'a' + '\n';
      } else if (c1 >= 'A' && c1 <= 'F') {
        c2 = c1 - 'A' + '\n';
      } else {
        throw new IOException("invalid chunk length");
      } 
      c = c * 16 + c2;
    } 
    return c;
  }
  
  private int readChunkHeader() throws IOException {
    boolean bool1 = false;
    char[] arrayOfChar = new char[16];
    byte b1 = 0;
    boolean bool2 = false;
    byte b2 = 0;
    int i;
    while ((i = this.in.read()) != -1) {
      char c = (char)i;
      if (b1 == arrayOfChar.length - 1 || ++b2 > 'ࠂ')
        throw new IOException("invalid chunk header"); 
      if (bool1) {
        if (c == '\n')
          return numeric(arrayOfChar, b1); 
        bool1 = false;
        if (!bool2)
          arrayOfChar[b1++] = c; 
        continue;
      } 
      if (c == '\r') {
        bool1 = true;
        continue;
      } 
      if (c == ';') {
        bool2 = true;
        continue;
      } 
      if (!bool2)
        arrayOfChar[b1++] = c; 
    } 
    throw new IOException("end of stream reading chunk header");
  }
  
  protected int readImpl(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (this.eof)
      return -1; 
    if (this.needToReadHeader) {
      this.remaining = readChunkHeader();
      if (this.remaining == 0) {
        this.eof = true;
        consumeCRLF();
        this.t.getServerImpl().requestCompleted(this.t.getConnection());
        return -1;
      } 
      this.needToReadHeader = false;
    } 
    if (paramInt2 > this.remaining)
      paramInt2 = this.remaining; 
    int i = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i > -1)
      this.remaining -= i; 
    if (this.remaining == 0) {
      this.needToReadHeader = true;
      consumeCRLF();
    } 
    return i;
  }
  
  private void consumeCRLF() throws IOException {
    char c = (char)this.in.read();
    if (c != '\r')
      throw new IOException("invalid chunk end"); 
    c = (char)this.in.read();
    if (c != '\n')
      throw new IOException("invalid chunk end"); 
  }
  
  public int available() throws IOException {
    if (this.eof || this.closed)
      return 0; 
    int i = this.in.available();
    return (i > this.remaining) ? this.remaining : i;
  }
  
  public boolean isDataBuffered() throws IOException {
    assert this.eof;
    return (this.in.available() > 0);
  }
  
  public boolean markSupported() throws IOException { return false; }
  
  public void mark(int paramInt) {}
  
  public void reset() throws IOException { throw new IOException("mark/reset not supported"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\httpserver\ChunkedInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */