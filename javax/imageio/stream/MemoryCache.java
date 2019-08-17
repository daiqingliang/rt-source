package javax.imageio.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

class MemoryCache {
  private static final int BUFFER_LENGTH = 8192;
  
  private ArrayList cache = new ArrayList();
  
  private long cacheStart = 0L;
  
  private long length = 0L;
  
  private byte[] getCacheBlock(long paramLong) throws IOException {
    long l = paramLong - this.cacheStart;
    if (l > 2147483647L)
      throw new IOException("Cache addressing limit exceeded!"); 
    return (byte[])this.cache.get((int)l);
  }
  
  public long loadFromStream(InputStream paramInputStream, long paramLong) throws IOException {
    if (paramLong < this.length)
      return paramLong; 
    int i = (int)(this.length % 8192L);
    byte[] arrayOfByte = null;
    long l = paramLong - this.length;
    if (i != 0)
      arrayOfByte = getCacheBlock(this.length / 8192L); 
    while (l > 0L) {
      if (arrayOfByte == null) {
        try {
          arrayOfByte = new byte[8192];
        } catch (OutOfMemoryError outOfMemoryError) {
          throw new IOException("No memory left for cache!");
        } 
        i = 0;
      } 
      int j = 8192 - i;
      int k = (int)Math.min(l, j);
      k = paramInputStream.read(arrayOfByte, i, k);
      if (k == -1)
        return this.length; 
      if (i == 0)
        this.cache.add(arrayOfByte); 
      l -= k;
      this.length += k;
      i += k;
      if (i >= 8192)
        arrayOfByte = null; 
    } 
    return paramLong;
  }
  
  public void writeToStream(OutputStream paramOutputStream, long paramLong1, long paramLong2) throws IOException {
    if (paramLong1 + paramLong2 > this.length)
      throw new IndexOutOfBoundsException("Argument out of cache"); 
    if (paramLong1 < 0L || paramLong2 < 0L)
      throw new IndexOutOfBoundsException("Negative pos or len"); 
    if (paramLong2 == 0L)
      return; 
    long l = paramLong1 / 8192L;
    if (l < this.cacheStart)
      throw new IndexOutOfBoundsException("pos already disposed"); 
    int i = (int)(paramLong1 % 8192L);
    byte[] arrayOfByte = getCacheBlock(l++);
    while (paramLong2 > 0L) {
      if (arrayOfByte == null) {
        arrayOfByte = getCacheBlock(l++);
        i = 0;
      } 
      int j = (int)Math.min(paramLong2, (8192 - i));
      paramOutputStream.write(arrayOfByte, i, j);
      arrayOfByte = null;
      paramLong2 -= j;
    } 
  }
  
  private void pad(long paramLong) throws IOException {
    long l1 = this.cacheStart + this.cache.size() - 1L;
    long l2 = paramLong / 8192L;
    long l3 = l2 - l1;
    long l4;
    for (l4 = 0L; l4 < l3; l4++) {
      try {
        this.cache.add(new byte[8192]);
      } catch (OutOfMemoryError outOfMemoryError) {
        throw new IOException("No memory left for cache!");
      } 
    } 
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2, long paramLong) throws IOException {
    if (paramArrayOfByte == null)
      throw new NullPointerException("b == null!"); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramLong < 0L || paramInt1 + paramInt2 > paramArrayOfByte.length || paramInt1 + paramInt2 < 0)
      throw new IndexOutOfBoundsException(); 
    long l = paramLong + paramInt2 - 1L;
    if (l >= this.length) {
      pad(l);
      this.length = l + 1L;
    } 
    int i;
    for (i = (int)(paramLong % 8192L); paramInt2 > 0; i = 0) {
      byte[] arrayOfByte = getCacheBlock(paramLong / 8192L);
      int j = Math.min(paramInt2, 8192 - i);
      System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, i, j);
      paramLong += j;
      paramInt1 += j;
      paramInt2 -= j;
    } 
  }
  
  public void write(int paramInt, long paramLong) throws IOException {
    if (paramLong < 0L)
      throw new ArrayIndexOutOfBoundsException("pos < 0"); 
    if (paramLong >= this.length) {
      pad(paramLong);
      this.length = paramLong + 1L;
    } 
    byte[] arrayOfByte = getCacheBlock(paramLong / 8192L);
    int i = (int)(paramLong % 8192L);
    arrayOfByte[i] = (byte)paramInt;
  }
  
  public long getLength() { return this.length; }
  
  public int read(long paramLong) throws IOException {
    if (paramLong >= this.length)
      return -1; 
    byte[] arrayOfByte = getCacheBlock(paramLong / 8192L);
    return (arrayOfByte == null) ? -1 : (arrayOfByte[(int)(paramLong % 8192L)] & 0xFF);
  }
  
  public void read(byte[] paramArrayOfByte, int paramInt1, int paramInt2, long paramLong) throws IOException { // Byte code:
    //   0: aload_1
    //   1: ifnonnull -> 14
    //   4: new java/lang/NullPointerException
    //   7: dup
    //   8: ldc 'b == null!'
    //   10: invokespecial <init> : (Ljava/lang/String;)V
    //   13: athrow
    //   14: iload_2
    //   15: iflt -> 43
    //   18: iload_3
    //   19: iflt -> 43
    //   22: lload #4
    //   24: lconst_0
    //   25: lcmp
    //   26: iflt -> 43
    //   29: iload_2
    //   30: iload_3
    //   31: iadd
    //   32: aload_1
    //   33: arraylength
    //   34: if_icmpgt -> 43
    //   37: iload_2
    //   38: iload_3
    //   39: iadd
    //   40: ifge -> 51
    //   43: new java/lang/IndexOutOfBoundsException
    //   46: dup
    //   47: invokespecial <init> : ()V
    //   50: athrow
    //   51: lload #4
    //   53: iload_3
    //   54: i2l
    //   55: ladd
    //   56: aload_0
    //   57: getfield length : J
    //   60: lcmp
    //   61: ifle -> 72
    //   64: new java/lang/IndexOutOfBoundsException
    //   67: dup
    //   68: invokespecial <init> : ()V
    //   71: athrow
    //   72: lload #4
    //   74: ldc2_w 8192
    //   77: ldiv
    //   78: lstore #6
    //   80: lload #4
    //   82: l2i
    //   83: sipush #8192
    //   86: irem
    //   87: istore #8
    //   89: iload_3
    //   90: ifle -> 145
    //   93: iload_3
    //   94: sipush #8192
    //   97: iload #8
    //   99: isub
    //   100: invokestatic min : (II)I
    //   103: istore #9
    //   105: aload_0
    //   106: lload #6
    //   108: dup2
    //   109: lconst_1
    //   110: ladd
    //   111: lstore #6
    //   113: invokespecial getCacheBlock : (J)[B
    //   116: astore #10
    //   118: aload #10
    //   120: iload #8
    //   122: aload_1
    //   123: iload_2
    //   124: iload #9
    //   126: invokestatic arraycopy : (Ljava/lang/Object;ILjava/lang/Object;II)V
    //   129: iload_3
    //   130: iload #9
    //   132: isub
    //   133: istore_3
    //   134: iload_2
    //   135: iload #9
    //   137: iadd
    //   138: istore_2
    //   139: iconst_0
    //   140: istore #8
    //   142: goto -> 89
    //   145: return }
  
  public void disposeBefore(long paramLong) throws IOException {
    long l1 = paramLong / 8192L;
    if (l1 < this.cacheStart)
      throw new IndexOutOfBoundsException("pos already disposed"); 
    long l2 = Math.min(l1 - this.cacheStart, this.cache.size());
    long l3;
    for (l3 = 0L; l3 < l2; l3++)
      this.cache.remove(0); 
    this.cacheStart = l1;
  }
  
  public void reset() {
    this.cache.clear();
    this.cacheStart = 0L;
    this.length = 0L;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\stream\MemoryCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */