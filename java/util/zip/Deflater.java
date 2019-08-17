package java.util.zip;

public class Deflater {
  private final ZStreamRef zsRef;
  
  private byte[] buf = new byte[0];
  
  private int off;
  
  private int len;
  
  private int level;
  
  private int strategy;
  
  private boolean setParams;
  
  private boolean finish;
  
  private boolean finished;
  
  private long bytesRead;
  
  private long bytesWritten;
  
  public static final int DEFLATED = 8;
  
  public static final int NO_COMPRESSION = 0;
  
  public static final int BEST_SPEED = 1;
  
  public static final int BEST_COMPRESSION = 9;
  
  public static final int DEFAULT_COMPRESSION = -1;
  
  public static final int FILTERED = 1;
  
  public static final int HUFFMAN_ONLY = 2;
  
  public static final int DEFAULT_STRATEGY = 0;
  
  public static final int NO_FLUSH = 0;
  
  public static final int SYNC_FLUSH = 2;
  
  public static final int FULL_FLUSH = 3;
  
  public Deflater(int paramInt, boolean paramBoolean) {
    this.level = paramInt;
    this.strategy = 0;
    this.zsRef = new ZStreamRef(init(paramInt, 0, paramBoolean));
  }
  
  public Deflater(int paramInt) { this(paramInt, false); }
  
  public Deflater() { this(-1, false); }
  
  public void setInput(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByte.length - paramInt2)
      throw new ArrayIndexOutOfBoundsException(); 
    synchronized (this.zsRef) {
      this.buf = paramArrayOfByte;
      this.off = paramInt1;
      this.len = paramInt2;
    } 
  }
  
  public void setInput(byte[] paramArrayOfByte) { setInput(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public void setDictionary(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByte.length - paramInt2)
      throw new ArrayIndexOutOfBoundsException(); 
    synchronized (this.zsRef) {
      ensureOpen();
      setDictionary(this.zsRef.address(), paramArrayOfByte, paramInt1, paramInt2);
    } 
  }
  
  public void setDictionary(byte[] paramArrayOfByte) { setDictionary(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public void setStrategy(int paramInt) {
    switch (paramInt) {
      case 0:
      case 1:
      case 2:
        break;
      default:
        throw new IllegalArgumentException();
    } 
    synchronized (this.zsRef) {
      if (this.strategy != paramInt) {
        this.strategy = paramInt;
        this.setParams = true;
      } 
    } 
  }
  
  public void setLevel(int paramInt) {
    if ((paramInt < 0 || paramInt > 9) && paramInt != -1)
      throw new IllegalArgumentException("invalid compression level"); 
    synchronized (this.zsRef) {
      if (this.level != paramInt) {
        this.level = paramInt;
        this.setParams = true;
      } 
    } 
  }
  
  public boolean needsInput() {
    synchronized (this.zsRef) {
      return (this.len <= 0);
    } 
  }
  
  public void finish() {
    synchronized (this.zsRef) {
      this.finish = true;
    } 
  }
  
  public boolean finished() {
    synchronized (this.zsRef) {
      return this.finished;
    } 
  }
  
  public int deflate(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { return deflate(paramArrayOfByte, paramInt1, paramInt2, 0); }
  
  public int deflate(byte[] paramArrayOfByte) { return deflate(paramArrayOfByte, 0, paramArrayOfByte.length, 0); }
  
  public int deflate(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3) {
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByte.length - paramInt2)
      throw new ArrayIndexOutOfBoundsException(); 
    synchronized (this.zsRef) {
      ensureOpen();
      if (paramInt3 == 0 || paramInt3 == 2 || paramInt3 == 3) {
        int i = this.len;
        int j = deflateBytes(this.zsRef.address(), paramArrayOfByte, paramInt1, paramInt2, paramInt3);
        this.bytesWritten += j;
        this.bytesRead += (i - this.len);
        return j;
      } 
      throw new IllegalArgumentException();
    } 
  }
  
  public int getAdler() {
    synchronized (this.zsRef) {
      ensureOpen();
      return getAdler(this.zsRef.address());
    } 
  }
  
  public int getTotalIn() { return (int)getBytesRead(); }
  
  public long getBytesRead() {
    synchronized (this.zsRef) {
      ensureOpen();
      return this.bytesRead;
    } 
  }
  
  public int getTotalOut() { return (int)getBytesWritten(); }
  
  public long getBytesWritten() {
    synchronized (this.zsRef) {
      ensureOpen();
      return this.bytesWritten;
    } 
  }
  
  public void reset() {
    synchronized (this.zsRef) {
      ensureOpen();
      reset(this.zsRef.address());
      this.finish = false;
      this.finished = false;
      this.off = this.len = 0;
      this.bytesRead = this.bytesWritten = 0L;
    } 
  }
  
  public void end() {
    synchronized (this.zsRef) {
      long l = this.zsRef.address();
      this.zsRef.clear();
      if (l != 0L) {
        end(l);
        this.buf = null;
      } 
    } 
  }
  
  protected void finalize() { end(); }
  
  private void ensureOpen() {
    assert Thread.holdsLock(this.zsRef);
    if (this.zsRef.address() == 0L)
      throw new NullPointerException("Deflater has been closed"); 
  }
  
  private static native void initIDs();
  
  private static native long init(int paramInt1, int paramInt2, boolean paramBoolean);
  
  private static native void setDictionary(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private native int deflateBytes(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3);
  
  private static native int getAdler(long paramLong);
  
  private static native void reset(long paramLong);
  
  private static native void end(long paramLong);
  
  static  {
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\Deflater.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */