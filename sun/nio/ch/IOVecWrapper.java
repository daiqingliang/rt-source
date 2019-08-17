package sun.nio.ch;

import java.nio.ByteBuffer;
import sun.misc.Cleaner;

class IOVecWrapper {
  private static final int BASE_OFFSET = 0;
  
  private static final int LEN_OFFSET;
  
  private static final int SIZE_IOVEC;
  
  private final AllocatedNativeObject vecArray;
  
  private final int size;
  
  private final ByteBuffer[] buf;
  
  private final int[] position;
  
  private final int[] remaining;
  
  private final ByteBuffer[] shadow;
  
  final long address;
  
  static int addressSize;
  
  private static final ThreadLocal<IOVecWrapper> cached = new ThreadLocal();
  
  private IOVecWrapper(int paramInt) {
    this.size = paramInt;
    this.buf = new ByteBuffer[paramInt];
    this.position = new int[paramInt];
    this.remaining = new int[paramInt];
    this.shadow = new ByteBuffer[paramInt];
    this.vecArray = new AllocatedNativeObject(paramInt * SIZE_IOVEC, false);
    this.address = this.vecArray.address();
  }
  
  static IOVecWrapper get(int paramInt) {
    IOVecWrapper iOVecWrapper = (IOVecWrapper)cached.get();
    if (iOVecWrapper != null && iOVecWrapper.size < paramInt) {
      iOVecWrapper.vecArray.free();
      iOVecWrapper = null;
    } 
    if (iOVecWrapper == null) {
      iOVecWrapper = new IOVecWrapper(paramInt);
      Cleaner.create(iOVecWrapper, new Deallocator(iOVecWrapper.vecArray));
      cached.set(iOVecWrapper);
    } 
    return iOVecWrapper;
  }
  
  void setBuffer(int paramInt1, ByteBuffer paramByteBuffer, int paramInt2, int paramInt3) {
    this.buf[paramInt1] = paramByteBuffer;
    this.position[paramInt1] = paramInt2;
    this.remaining[paramInt1] = paramInt3;
  }
  
  void setShadow(int paramInt, ByteBuffer paramByteBuffer) { this.shadow[paramInt] = paramByteBuffer; }
  
  ByteBuffer getBuffer(int paramInt) { return this.buf[paramInt]; }
  
  int getPosition(int paramInt) { return this.position[paramInt]; }
  
  int getRemaining(int paramInt) { return this.remaining[paramInt]; }
  
  ByteBuffer getShadow(int paramInt) { return this.shadow[paramInt]; }
  
  void clearRefs(int paramInt) {
    this.buf[paramInt] = null;
    this.shadow[paramInt] = null;
  }
  
  void putBase(int paramInt, long paramLong) {
    int i = SIZE_IOVEC * paramInt + 0;
    if (addressSize == 4) {
      this.vecArray.putInt(i, (int)paramLong);
    } else {
      this.vecArray.putLong(i, paramLong);
    } 
  }
  
  void putLen(int paramInt, long paramLong) {
    int i = SIZE_IOVEC * paramInt + LEN_OFFSET;
    if (addressSize == 4) {
      this.vecArray.putInt(i, (int)paramLong);
    } else {
      this.vecArray.putLong(i, paramLong);
    } 
  }
  
  static  {
    addressSize = Util.unsafe().addressSize();
    LEN_OFFSET = addressSize;
    SIZE_IOVEC = (short)(addressSize * 2);
  }
  
  private static class Deallocator implements Runnable {
    private final AllocatedNativeObject obj;
    
    Deallocator(AllocatedNativeObject param1AllocatedNativeObject) { this.obj = param1AllocatedNativeObject; }
    
    public void run() { this.obj.free(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\IOVecWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */