package java.io;

public class PushbackReader extends FilterReader {
  private char[] buf;
  
  private int pos;
  
  public PushbackReader(Reader paramReader, int paramInt) {
    super(paramReader);
    if (paramInt <= 0)
      throw new IllegalArgumentException("size <= 0"); 
    this.buf = new char[paramInt];
    this.pos = paramInt;
  }
  
  public PushbackReader(Reader paramReader) { this(paramReader, 1); }
  
  private void ensureOpen() throws IOException {
    if (this.buf == null)
      throw new IOException("Stream closed"); 
  }
  
  public int read() throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      if (this.pos < this.buf.length)
        return this.buf[this.pos++]; 
      return super.read();
    } 
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      try {
        if (paramInt2 <= 0) {
          if (paramInt2 < 0)
            throw new IndexOutOfBoundsException(); 
          if (paramInt1 < 0 || paramInt1 > paramArrayOfChar.length)
            throw new IndexOutOfBoundsException(); 
          return 0;
        } 
        int i = this.buf.length - this.pos;
        if (i > 0) {
          if (paramInt2 < i)
            i = paramInt2; 
          System.arraycopy(this.buf, this.pos, paramArrayOfChar, paramInt1, i);
          this.pos += i;
          paramInt1 += i;
          paramInt2 -= i;
        } 
        if (paramInt2 > 0) {
          paramInt2 = super.read(paramArrayOfChar, paramInt1, paramInt2);
          if (paramInt2 == -1)
            return (i == 0) ? -1 : i; 
          return i + paramInt2;
        } 
        return i;
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
        throw new IndexOutOfBoundsException();
      } 
    } 
  }
  
  public void unread(int paramInt) throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      if (this.pos == 0)
        throw new IOException("Pushback buffer overflow"); 
      this.buf[--this.pos] = (char)paramInt;
    } 
  }
  
  public void unread(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      if (paramInt2 > this.pos)
        throw new IOException("Pushback buffer overflow"); 
      this.pos -= paramInt2;
      System.arraycopy(paramArrayOfChar, paramInt1, this.buf, this.pos, paramInt2);
    } 
  }
  
  public void unread(char[] paramArrayOfChar) throws IOException { unread(paramArrayOfChar, 0, paramArrayOfChar.length); }
  
  public boolean ready() throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      return (this.pos < this.buf.length || super.ready());
    } 
  }
  
  public void mark(int paramInt) throws IOException { throw new IOException("mark/reset not supported"); }
  
  public void reset() throws IOException { throw new IOException("mark/reset not supported"); }
  
  public boolean markSupported() throws IOException { return false; }
  
  public void close() throws IOException {
    super.close();
    this.buf = null;
  }
  
  public long skip(long paramLong) throws IOException {
    if (paramLong < 0L)
      throw new IllegalArgumentException("skip value is negative"); 
    synchronized (this.lock) {
      ensureOpen();
      int i = this.buf.length - this.pos;
      if (i > 0) {
        if (paramLong <= i) {
          this.pos = (int)(this.pos + paramLong);
          return paramLong;
        } 
        this.pos = this.buf.length;
        paramLong -= i;
      } 
      return i + super.skip(paramLong);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\PushbackReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */