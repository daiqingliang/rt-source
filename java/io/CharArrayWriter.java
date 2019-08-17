package java.io;

import java.util.Arrays;

public class CharArrayWriter extends Writer {
  protected char[] buf;
  
  protected int count;
  
  public CharArrayWriter() { this(32); }
  
  public CharArrayWriter(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Negative initial size: " + paramInt); 
    this.buf = new char[paramInt];
  }
  
  public void write(int paramInt) {
    synchronized (this.lock) {
      int i = this.count + 1;
      if (i > this.buf.length)
        this.buf = Arrays.copyOf(this.buf, Math.max(this.buf.length << 1, i)); 
      this.buf[this.count] = (char)paramInt;
      this.count = i;
    } 
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 > paramArrayOfChar.length || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfChar.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return; 
    synchronized (this.lock) {
      int i = this.count + paramInt2;
      if (i > this.buf.length)
        this.buf = Arrays.copyOf(this.buf, Math.max(this.buf.length << 1, i)); 
      System.arraycopy(paramArrayOfChar, paramInt1, this.buf, this.count, paramInt2);
      this.count = i;
    } 
  }
  
  public void write(String paramString, int paramInt1, int paramInt2) {
    synchronized (this.lock) {
      int i = this.count + paramInt2;
      if (i > this.buf.length)
        this.buf = Arrays.copyOf(this.buf, Math.max(this.buf.length << 1, i)); 
      paramString.getChars(paramInt1, paramInt1 + paramInt2, this.buf, this.count);
      this.count = i;
    } 
  }
  
  public void writeTo(Writer paramWriter) throws IOException {
    synchronized (this.lock) {
      paramWriter.write(this.buf, 0, this.count);
    } 
  }
  
  public CharArrayWriter append(CharSequence paramCharSequence) {
    String str = (paramCharSequence == null) ? "null" : paramCharSequence.toString();
    write(str, 0, str.length());
    return this;
  }
  
  public CharArrayWriter append(CharSequence paramCharSequence, int paramInt1, int paramInt2) {
    String str = ((paramCharSequence == null) ? "null" : paramCharSequence).subSequence(paramInt1, paramInt2).toString();
    write(str, 0, str.length());
    return this;
  }
  
  public CharArrayWriter append(char paramChar) {
    write(paramChar);
    return this;
  }
  
  public void reset() { this.count = 0; }
  
  public char[] toCharArray() {
    synchronized (this.lock) {
      return Arrays.copyOf(this.buf, this.count);
    } 
  }
  
  public int size() { return this.count; }
  
  public String toString() {
    synchronized (this.lock) {
      return new String(this.buf, 0, this.count);
    } 
  }
  
  public void flush() {}
  
  public void close() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\CharArrayWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */