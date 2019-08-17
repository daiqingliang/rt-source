package sun.nio.ch;

import java.io.FileDescriptor;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import sun.misc.Unsafe;
import sun.misc.VM;
import sun.security.action.GetPropertyAction;

public class Util {
  private static final int TEMP_BUF_POOL_SIZE = IOUtil.IOV_MAX;
  
  private static final long MAX_CACHED_BUFFER_SIZE = getMaxCachedBufferSize();
  
  private static ThreadLocal<BufferCache> bufferCache = new ThreadLocal<BufferCache>() {
      protected Util.BufferCache initialValue() { return new Util.BufferCache(); }
    };
  
  private static Unsafe unsafe = Unsafe.getUnsafe();
  
  private static int pageSize = -1;
  
  private static long getMaxCachedBufferSize() {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return System.getProperty("jdk.nio.maxCachedBufferSize"); }
        });
    if (str != null)
      try {
        long l = Long.parseLong(str);
        if (l >= 0L)
          return l; 
      } catch (NumberFormatException numberFormatException) {} 
    return Float.MAX_VALUE;
  }
  
  private static boolean isBufferTooLarge(int paramInt) { return (paramInt > MAX_CACHED_BUFFER_SIZE); }
  
  private static boolean isBufferTooLarge(ByteBuffer paramByteBuffer) { return isBufferTooLarge(paramByteBuffer.capacity()); }
  
  public static ByteBuffer getTemporaryDirectBuffer(int paramInt) {
    if (isBufferTooLarge(paramInt))
      return ByteBuffer.allocateDirect(paramInt); 
    BufferCache bufferCache1 = (BufferCache)bufferCache.get();
    ByteBuffer byteBuffer = bufferCache1.get(paramInt);
    if (byteBuffer != null)
      return byteBuffer; 
    if (!bufferCache1.isEmpty()) {
      byteBuffer = bufferCache1.removeFirst();
      free(byteBuffer);
    } 
    return ByteBuffer.allocateDirect(paramInt);
  }
  
  public static void releaseTemporaryDirectBuffer(ByteBuffer paramByteBuffer) { offerFirstTemporaryDirectBuffer(paramByteBuffer); }
  
  static void offerFirstTemporaryDirectBuffer(ByteBuffer paramByteBuffer) {
    if (isBufferTooLarge(paramByteBuffer)) {
      free(paramByteBuffer);
      return;
    } 
    assert paramByteBuffer != null;
    BufferCache bufferCache1 = (BufferCache)bufferCache.get();
    if (!bufferCache1.offerFirst(paramByteBuffer))
      free(paramByteBuffer); 
  }
  
  static void offerLastTemporaryDirectBuffer(ByteBuffer paramByteBuffer) {
    if (isBufferTooLarge(paramByteBuffer)) {
      free(paramByteBuffer);
      return;
    } 
    assert paramByteBuffer != null;
    BufferCache bufferCache1 = (BufferCache)bufferCache.get();
    if (!bufferCache1.offerLast(paramByteBuffer))
      free(paramByteBuffer); 
  }
  
  private static void free(ByteBuffer paramByteBuffer) { ((DirectBuffer)paramByteBuffer).cleaner().clean(); }
  
  static ByteBuffer[] subsequence(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) {
    if (paramInt1 == 0 && paramInt2 == paramArrayOfByteBuffer.length)
      return paramArrayOfByteBuffer; 
    int i = paramInt2;
    ByteBuffer[] arrayOfByteBuffer = new ByteBuffer[i];
    for (int j = 0; j < i; j++)
      arrayOfByteBuffer[j] = paramArrayOfByteBuffer[paramInt1 + j]; 
    return arrayOfByteBuffer;
  }
  
  static <E> Set<E> ungrowableSet(final Set<E> s) { return new Set<E>() {
        public int size() { return s.size(); }
        
        public boolean isEmpty() { return s.isEmpty(); }
        
        public boolean contains(Object param1Object) { return s.contains(param1Object); }
        
        public Object[] toArray() { return s.toArray(); }
        
        public <T> T[] toArray(T[] param1ArrayOfT) { return (T[])s.toArray(param1ArrayOfT); }
        
        public String toString() { return s.toString(); }
        
        public Iterator<E> iterator() { return s.iterator(); }
        
        public boolean equals(Object param1Object) { return s.equals(param1Object); }
        
        public int hashCode() { return s.hashCode(); }
        
        public void clear() { s.clear(); }
        
        public boolean remove(Object param1Object) { return s.remove(param1Object); }
        
        public boolean containsAll(Collection<?> param1Collection) { return s.containsAll(param1Collection); }
        
        public boolean removeAll(Collection<?> param1Collection) { return s.removeAll(param1Collection); }
        
        public boolean retainAll(Collection<?> param1Collection) { return s.retainAll(param1Collection); }
        
        public boolean add(E param1E) { throw new UnsupportedOperationException(); }
        
        public boolean addAll(Collection<? extends E> param1Collection) { throw new UnsupportedOperationException(); }
      }; }
  
  private static byte _get(long paramLong) { return unsafe.getByte(paramLong); }
  
  private static void _put(long paramLong, byte paramByte) { unsafe.putByte(paramLong, paramByte); }
  
  static void erase(ByteBuffer paramByteBuffer) { unsafe.setMemory(((DirectBuffer)paramByteBuffer).address(), paramByteBuffer.capacity(), (byte)0); }
  
  static Unsafe unsafe() { return unsafe; }
  
  static int pageSize() {
    if (pageSize == -1)
      pageSize = unsafe().pageSize(); 
    return pageSize;
  }
  
  private static void initDBBConstructor() { AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            try {
              Class clazz = Class.forName("java.nio.DirectByteBuffer");
              Constructor constructor = clazz.getDeclaredConstructor(new Class[] { int.class, long.class, FileDescriptor.class, Runnable.class });
              constructor.setAccessible(true);
              directByteBufferConstructor = constructor;
            } catch (ClassNotFoundException|NoSuchMethodException|IllegalArgumentException|ClassCastException classNotFoundException) {
              throw new InternalError(classNotFoundException);
            } 
            return null;
          }
        }); }
  
  static MappedByteBuffer newMappedByteBuffer(int paramInt, long paramLong, FileDescriptor paramFileDescriptor, Runnable paramRunnable) {
    MappedByteBuffer mappedByteBuffer;
    if (directByteBufferConstructor == null)
      initDBBConstructor(); 
    try {
      mappedByteBuffer = (MappedByteBuffer)directByteBufferConstructor.newInstance(new Object[] { new Integer(paramInt), new Long(paramLong), paramFileDescriptor, paramRunnable });
    } catch (InstantiationException|IllegalAccessException|java.lang.reflect.InvocationTargetException instantiationException) {
      throw new InternalError(instantiationException);
    } 
    return mappedByteBuffer;
  }
  
  private static void initDBBRConstructor() { AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            try {
              Class clazz = Class.forName("java.nio.DirectByteBufferR");
              Constructor constructor = clazz.getDeclaredConstructor(new Class[] { int.class, long.class, FileDescriptor.class, Runnable.class });
              constructor.setAccessible(true);
              directByteBufferRConstructor = constructor;
            } catch (ClassNotFoundException|NoSuchMethodException|IllegalArgumentException|ClassCastException classNotFoundException) {
              throw new InternalError(classNotFoundException);
            } 
            return null;
          }
        }); }
  
  static MappedByteBuffer newMappedByteBufferR(int paramInt, long paramLong, FileDescriptor paramFileDescriptor, Runnable paramRunnable) {
    MappedByteBuffer mappedByteBuffer;
    if (directByteBufferRConstructor == null)
      initDBBRConstructor(); 
    try {
      mappedByteBuffer = (MappedByteBuffer)directByteBufferRConstructor.newInstance(new Object[] { new Integer(paramInt), new Long(paramLong), paramFileDescriptor, paramRunnable });
    } catch (InstantiationException|IllegalAccessException|java.lang.reflect.InvocationTargetException instantiationException) {
      throw new InternalError(instantiationException);
    } 
    return mappedByteBuffer;
  }
  
  static boolean atBugLevel(String paramString) {
    if (bugLevel == null) {
      if (!VM.isBooted())
        return false; 
      String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.nio.ch.bugLevel"));
      bugLevel = (str != null) ? str : "";
    } 
    return bugLevel.equals(paramString);
  }
  
  private static class BufferCache {
    private ByteBuffer[] buffers = new ByteBuffer[TEMP_BUF_POOL_SIZE];
    
    private int count;
    
    private int start;
    
    private int next(int param1Int) { return (param1Int + 1) % TEMP_BUF_POOL_SIZE; }
    
    ByteBuffer get(int param1Int) {
      assert !Util.isBufferTooLarge(param1Int);
      if (this.count == 0)
        return null; 
      ByteBuffer[] arrayOfByteBuffer = this.buffers;
      ByteBuffer byteBuffer = arrayOfByteBuffer[this.start];
      if (byteBuffer.capacity() < param1Int) {
        byteBuffer = null;
        int i = this.start;
        while ((i = next(i)) != this.start) {
          ByteBuffer byteBuffer1 = arrayOfByteBuffer[i];
          if (byteBuffer1 == null)
            break; 
          if (byteBuffer1.capacity() >= param1Int) {
            byteBuffer = byteBuffer1;
            break;
          } 
        } 
        if (byteBuffer == null)
          return null; 
        arrayOfByteBuffer[i] = arrayOfByteBuffer[this.start];
      } 
      arrayOfByteBuffer[this.start] = null;
      this.start = next(this.start);
      this.count--;
      byteBuffer.rewind();
      byteBuffer.limit(param1Int);
      return byteBuffer;
    }
    
    boolean offerFirst(ByteBuffer param1ByteBuffer) {
      assert !Util.isBufferTooLarge(param1ByteBuffer);
      if (this.count >= TEMP_BUF_POOL_SIZE)
        return false; 
      this.start = (this.start + TEMP_BUF_POOL_SIZE - 1) % TEMP_BUF_POOL_SIZE;
      this.buffers[this.start] = param1ByteBuffer;
      this.count++;
      return true;
    }
    
    boolean offerLast(ByteBuffer param1ByteBuffer) {
      assert !Util.isBufferTooLarge(param1ByteBuffer);
      if (this.count >= TEMP_BUF_POOL_SIZE)
        return false; 
      int i = (this.start + this.count) % TEMP_BUF_POOL_SIZE;
      this.buffers[i] = param1ByteBuffer;
      this.count++;
      return true;
    }
    
    boolean isEmpty() { return (this.count == 0); }
    
    ByteBuffer removeFirst() {
      assert this.count > 0;
      ByteBuffer byteBuffer = this.buffers[this.start];
      this.buffers[this.start] = null;
      this.start = next(this.start);
      this.count--;
      return byteBuffer;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */