package java.io;

@Deprecated
public class StringBufferInputStream extends InputStream {
  protected String buffer;
  
  protected int pos;
  
  protected int count;
  
  public StringBufferInputStream(String paramString) {
    this.buffer = paramString;
    this.count = paramString.length();
  }
  
  public int read() { return (this.pos < this.count) ? (this.buffer.charAt(this.pos++) & 0xFF) : -1; }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt1 > paramArrayOfByte.length || paramInt2 < 0 || paramInt1 + paramInt2 > paramArrayOfByte.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException(); 
    if (this.pos >= this.count)
      return -1; 
    int i = this.count - this.pos;
    if (paramInt2 > i)
      paramInt2 = i; 
    if (paramInt2 <= 0)
      return 0; 
    String str = this.buffer;
    int j = paramInt2;
    while (--j >= 0)
      paramArrayOfByte[paramInt1++] = (byte)str.charAt(this.pos++); 
    return paramInt2;
  }
  
  public long skip(long paramLong) {
    if (paramLong < 0L)
      return 0L; 
    if (paramLong > (this.count - this.pos))
      paramLong = (this.count - this.pos); 
    this.pos = (int)(this.pos + paramLong);
    return paramLong;
  }
  
  public int available() { return this.count - this.pos; }
  
  public void reset() { this.pos = 0; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\StringBufferInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */