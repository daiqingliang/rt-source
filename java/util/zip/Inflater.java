package java.util.zip;

public class Inflater {
  private final ZStreamRef zsRef;
  
  private byte[] buf = defaultBuf;
  
  private int off;
  
  private int len;
  
  private boolean finished;
  
  private boolean needDict;
  
  private long bytesRead;
  
  private long bytesWritten;
  
  private static final byte[] defaultBuf = new byte[0];
  
  public Inflater(boolean paramBoolean) { this.zsRef = new ZStreamRef(init(paramBoolean)); }
  
  public Inflater() { this(false); }
  
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
      this.needDict = false;
    } 
  }
  
  public void setDictionary(byte[] paramArrayOfByte) { setDictionary(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public int getRemaining() {
    synchronized (this.zsRef) {
      return this.len;
    } 
  }
  
  public boolean needsInput() {
    synchronized (this.zsRef) {
      return (this.len <= 0);
    } 
  }
  
  public boolean needsDictionary() {
    synchronized (this.zsRef) {
      return this.needDict;
    } 
  }
  
  public boolean finished() {
    synchronized (this.zsRef) {
      return this.finished;
    } 
  }
  
  public int inflate(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws DataFormatException {
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByte.length - paramInt2)
      throw new ArrayIndexOutOfBoundsException(); 
    synchronized (this.zsRef) {
      ensureOpen();
      int i = this.len;
      int j = inflateBytes(this.zsRef.address(), paramArrayOfByte, paramInt1, paramInt2);
      this.bytesWritten += j;
      this.bytesRead += (i - this.len);
      return j;
    } 
  }
  
  public int inflate(byte[] paramArrayOfByte) throws DataFormatException { return inflate(paramArrayOfByte, 0, paramArrayOfByte.length); }
  
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
      this.buf = defaultBuf;
      this.finished = false;
      this.needDict = false;
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
      throw new NullPointerException("Inflater has been closed"); 
  }
  
  boolean ended() {
    synchronized (this.zsRef) {
      return (this.zsRef.address() == 0L);
    } 
  }
  
  private static native void initIDs();
  
  private static native long init(boolean paramBoolean);
  
  private static native void setDictionary(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private native int inflateBytes(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws DataFormatException;
  
  private static native int getAdler(long paramLong);
  
  private static native void reset(long paramLong);
  
  private static native void end(long paramLong);
  
  static  {
    initIDs();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\zip\Inflater.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */