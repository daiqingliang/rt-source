package java.io;

public class CharArrayReader extends Reader {
  protected char[] buf;
  
  protected int pos;
  
  protected int markedPos = 0;
  
  protected int count;
  
  public CharArrayReader(char[] paramArrayOfChar) {
    this.buf = paramArrayOfChar;
    this.pos = 0;
    this.count = paramArrayOfChar.length;
  }
  
  public CharArrayReader(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 > paramArrayOfChar.length || paramInt2 < 0 || paramInt1 + paramInt2 < 0)
      throw new IllegalArgumentException(); 
    this.buf = paramArrayOfChar;
    this.pos = paramInt1;
    this.count = Math.min(paramInt1 + paramInt2, paramArrayOfChar.length);
    this.markedPos = paramInt1;
  }
  
  private void ensureOpen() throws IOException {
    if (this.buf == null)
      throw new IOException("Stream closed"); 
  }
  
  public int read() throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      if (this.pos >= this.count)
        return -1; 
      return this.buf[this.pos++];
    } 
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      if (paramInt1 < 0 || paramInt1 > paramArrayOfChar.length || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfChar.length || paramInt1 + paramInt2 < 0)
        throw new IndexOutOfBoundsException(); 
      if (paramInt2 == 0)
        return 0; 
      if (this.pos >= this.count)
        return -1; 
      int i = this.count - this.pos;
      if (paramInt2 > i)
        paramInt2 = i; 
      if (paramInt2 <= 0)
        return 0; 
      System.arraycopy(this.buf, this.pos, paramArrayOfChar, paramInt1, paramInt2);
      this.pos += paramInt2;
      return paramInt2;
    } 
  }
  
  public long skip(long paramLong) throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      long l = (this.count - this.pos);
      if (paramLong > l)
        paramLong = l; 
      if (paramLong < 0L)
        return 0L; 
      this.pos = (int)(this.pos + paramLong);
      return paramLong;
    } 
  }
  
  public boolean ready() throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      return (this.count - this.pos > 0);
    } 
  }
  
  public boolean markSupported() throws IOException { return true; }
  
  public void mark(int paramInt) throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      this.markedPos = this.pos;
    } 
  }
  
  public void reset() throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      this.pos = this.markedPos;
    } 
  }
  
  public void close() throws IOException { this.buf = null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\CharArrayReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */