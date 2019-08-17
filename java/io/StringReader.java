package java.io;

public class StringReader extends Reader {
  private String str;
  
  private int length;
  
  private int next = 0;
  
  private int mark = 0;
  
  public StringReader(String paramString) {
    this.str = paramString;
    this.length = paramString.length();
  }
  
  private void ensureOpen() throws IOException {
    if (this.str == null)
      throw new IOException("Stream closed"); 
  }
  
  public int read() throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      if (this.next >= this.length)
        return -1; 
      return this.str.charAt(this.next++);
    } 
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      if (paramInt1 < 0 || paramInt1 > paramArrayOfChar.length || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfChar.length || paramInt1 + paramInt2 < 0)
        throw new IndexOutOfBoundsException(); 
      if (paramInt2 == 0)
        return 0; 
      if (this.next >= this.length)
        return -1; 
      int i = Math.min(this.length - this.next, paramInt2);
      this.str.getChars(this.next, this.next + i, paramArrayOfChar, paramInt1);
      this.next += i;
      return i;
    } 
  }
  
  public long skip(long paramLong) throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      if (this.next >= this.length)
        return 0L; 
      long l = Math.min((this.length - this.next), paramLong);
      l = Math.max(-this.next, l);
      this.next = (int)(this.next + l);
      return l;
    } 
  }
  
  public boolean ready() throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      return true;
    } 
  }
  
  public boolean markSupported() throws IOException { return true; }
  
  public void mark(int paramInt) throws IOException {
    if (paramInt < 0)
      throw new IllegalArgumentException("Read-ahead limit < 0"); 
    synchronized (this.lock) {
      ensureOpen();
      this.mark = this.next;
    } 
  }
  
  public void reset() throws IOException {
    synchronized (this.lock) {
      ensureOpen();
      this.next = this.mark;
    } 
  }
  
  public void close() throws IOException { this.str = null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\StringReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */