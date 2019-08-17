package sun.nio.fs;

import sun.misc.Cleaner;
import sun.misc.Unsafe;

class NativeBuffer {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private final long address;
  
  private final int size;
  
  private final Cleaner cleaner;
  
  private Object owner;
  
  NativeBuffer(int paramInt) {
    this.address = unsafe.allocateMemory(paramInt);
    this.size = paramInt;
    this.cleaner = Cleaner.create(this, new Deallocator(this.address));
  }
  
  void release() { NativeBuffers.releaseNativeBuffer(this); }
  
  long address() { return this.address; }
  
  int size() { return this.size; }
  
  Cleaner cleaner() { return this.cleaner; }
  
  void setOwner(Object paramObject) { this.owner = paramObject; }
  
  Object owner() { return this.owner; }
  
  private static class Deallocator implements Runnable {
    private final long address;
    
    Deallocator(long param1Long) { this.address = param1Long; }
    
    public void run() { unsafe.freeMemory(this.address); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\NativeBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */