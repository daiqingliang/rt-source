package java.util.concurrent.locks;

import java.util.concurrent.ThreadLocalRandom;
import sun.misc.Unsafe;

public class LockSupport {
  private static final Unsafe UNSAFE;
  
  private static final long parkBlockerOffset;
  
  private static final long SEED;
  
  private static final long PROBE;
  
  private static final long SECONDARY;
  
  private static void setBlocker(Thread paramThread, Object paramObject) { UNSAFE.putObject(paramThread, parkBlockerOffset, paramObject); }
  
  public static void unpark(Thread paramThread) {
    if (paramThread != null)
      UNSAFE.unpark(paramThread); 
  }
  
  public static void park(Object paramObject) {
    Thread thread = Thread.currentThread();
    setBlocker(thread, paramObject);
    UNSAFE.park(false, 0L);
    setBlocker(thread, null);
  }
  
  public static void parkNanos(Object paramObject, long paramLong) {
    if (paramLong > 0L) {
      Thread thread = Thread.currentThread();
      setBlocker(thread, paramObject);
      UNSAFE.park(false, paramLong);
      setBlocker(thread, null);
    } 
  }
  
  public static void parkUntil(Object paramObject, long paramLong) {
    Thread thread = Thread.currentThread();
    setBlocker(thread, paramObject);
    UNSAFE.park(true, paramLong);
    setBlocker(thread, null);
  }
  
  public static Object getBlocker(Thread paramThread) {
    if (paramThread == null)
      throw new NullPointerException(); 
    return UNSAFE.getObjectVolatile(paramThread, parkBlockerOffset);
  }
  
  public static void park() { UNSAFE.park(false, 0L); }
  
  public static void parkNanos(long paramLong) {
    if (paramLong > 0L)
      UNSAFE.park(false, paramLong); 
  }
  
  public static void parkUntil(long paramLong) { UNSAFE.park(true, paramLong); }
  
  static final int nextSecondarySeed() {
    Thread thread = Thread.currentThread();
    int i;
    if ((i = UNSAFE.getInt(thread, SECONDARY)) != 0) {
      i ^= i << 13;
      i ^= i >>> 17;
      i ^= i << 5;
    } else if ((i = ThreadLocalRandom.current().nextInt()) == 0) {
      i = 1;
    } 
    UNSAFE.putInt(thread, SECONDARY, i);
    return i;
  }
  
  static  {
    try {
      UNSAFE = Unsafe.getUnsafe();
      Class clazz = Thread.class;
      parkBlockerOffset = UNSAFE.objectFieldOffset(clazz.getDeclaredField("parkBlocker"));
      SEED = UNSAFE.objectFieldOffset(clazz.getDeclaredField("threadLocalRandomSeed"));
      PROBE = UNSAFE.objectFieldOffset(clazz.getDeclaredField("threadLocalRandomProbe"));
      SECONDARY = UNSAFE.objectFieldOffset(clazz.getDeclaredField("threadLocalRandomSecondarySeed"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\locks\LockSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */