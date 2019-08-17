package java.io;

@Deprecated
public class LineNumberInputStream extends FilterInputStream {
  int pushBack = -1;
  
  int lineNumber;
  
  int markLineNumber;
  
  int markPushBack = -1;
  
  public LineNumberInputStream(InputStream paramInputStream) { super(paramInputStream); }
  
  public int read() throws IOException {
    int i = this.pushBack;
    if (i != -1) {
      this.pushBack = -1;
    } else {
      i = this.in.read();
    } 
    switch (i) {
      case 13:
        this.pushBack = this.in.read();
        if (this.pushBack == 10)
          this.pushBack = -1; 
      case 10:
        this.lineNumber++;
        return 10;
    } 
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt1 > paramArrayOfByte.length || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfByte.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return 0; 
    int i = read();
    if (i == -1)
      return -1; 
    paramArrayOfByte[paramInt1] = (byte)i;
    int j = 1;
    try {
      while (j < paramInt2) {
        i = read();
        if (i == -1)
          break; 
        if (paramArrayOfByte != null)
          paramArrayOfByte[paramInt1 + j] = (byte)i; 
        j++;
      } 
    } catch (IOException iOException) {}
    return j;
  }
  
  public long skip(long paramLong) throws IOException {
    char c = 'ࠀ';
    long l = paramLong;
    if (paramLong <= 0L)
      return 0L; 
    byte[] arrayOfByte = new byte[c];
    while (l > 0L) {
      int i = read(arrayOfByte, 0, (int)Math.min(c, l));
      if (i < 0)
        break; 
      l -= i;
    } 
    return paramLong - l;
  }
  
  public void setLineNumber(int paramInt) { this.lineNumber = paramInt; }
  
  public int getLineNumber() throws IOException { return this.lineNumber; }
  
  public int available() throws IOException { return (this.pushBack == -1) ? (super.available() / 2) : (super.available() / 2 + 1); }
  
  public void mark(int paramInt) {
    this.markLineNumber = this.lineNumber;
    this.markPushBack = this.pushBack;
    this.in.mark(paramInt);
  }
  
  public void reset() throws IOException {
    this.lineNumber = this.markLineNumber;
    this.pushBack = this.markPushBack;
    this.in.reset();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\LineNumberInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */