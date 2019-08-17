package java.io;

import java.util.Arrays;

public class ByteArrayOutputStream extends OutputStream {
  protected byte[] buf;
  
  protected int count;
  
  private static final int MAX_ARRAY_SIZE = 2147483639;
  
  public ByteArrayOutputStream() { this(32); }
  
  public ByteArrayOutputStream(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Negative initial size: " + paramInt); 
    this.buf = new byte[paramInt];
  }
  
  private void ensureCapacity(int paramInt) {
    if (paramInt - this.buf.length > 0)
      grow(paramInt); 
  }
  
  private void grow(int paramInt) {
    int i = this.buf.length;
    int j = i << 1;
    if (j - paramInt < 0)
      j = paramInt; 
    if (j - 2147483639 > 0)
      j = hugeCapacity(paramInt); 
    this.buf = Arrays.copyOf(this.buf, j);
  }
  
  private static int hugeCapacity(int paramInt) {
    if (paramInt < 0)
      throw new OutOfMemoryError(); 
    return (paramInt > 2147483639) ? Integer.MAX_VALUE : 2147483639;
  }
  
  public void write(int paramInt) {
    ensureCapacity(this.count + 1);
    this.buf[this.count] = (byte)paramInt;
    this.count++;
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 > paramArrayOfByte.length || paramInt2 < 0 || paramInt1 + paramInt2 - paramArrayOfByte.length > 0)
      throw new IndexOutOfBoundsException(); 
    ensureCapacity(this.count + paramInt2);
    System.arraycopy(paramArrayOfByte, paramInt1, this.buf, this.count, paramInt2);
    this.count += paramInt2;
  }
  
  public void writeTo(OutputStream paramOutputStream) throws IOException { paramOutputStream.write(this.buf, 0, this.count); }
  
  public void reset() { this.count = 0; }
  
  public byte[] toByteArray() { return Arrays.copyOf(this.buf, this.count); }
  
  public int size() { return this.count; }
  
  public String toString() { return new String(this.buf, 0, this.count); }
  
  public String toString(String paramString) throws UnsupportedEncodingException { return new String(this.buf, 0, this.count, paramString); }
  
  @Deprecated
  public String toString(int paramInt) { return new String(this.buf, paramInt, 0, this.count); }
  
  public void close() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\ByteArrayOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */