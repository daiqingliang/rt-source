package jdk.management.resource.internal;

import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;

public final class ResourceNatives {
  public static final int SYSTEM_RESOURCE_CONTEXT_ID = 0;
  
  public static final int FEATURE_ENABLED = 1;
  
  public static final int FEATURE_RETAINED_MEMORY = 2;
  
  private static int enabledFeatures;
  
  private static native int featuresEnabled0();
  
  private static native int sampleInterval0();
  
  private static native void getThreadStats0(long[] paramArrayOfLong1, long[] paramArrayOfLong2, long[] paramArrayOfLong3);
  
  private static native long getCurrentThreadCPUTime0();
  
  private static native long getCurrentThreadAllocatedHeap0();
  
  public static boolean isEnabled() { return ((featuresEnabled() & true) == 1); }
  
  public static boolean isHeapRetainedEnabled() { return ((featuresEnabled() & 0x2) == 2); }
  
  public static int featuresEnabled() { return enabledFeatures; }
  
  public static int sampleInterval() { return sampleInterval0(); }
  
  public static void getThreadStats(long[] paramArrayOfLong1, long[] paramArrayOfLong2, long[] paramArrayOfLong3) { getThreadStats0(paramArrayOfLong1, paramArrayOfLong2, paramArrayOfLong3); }
  
  public static long getCurrentThreadCPUTime() { return getCurrentThreadCPUTime0(); }
  
  public static long getCurrentThreadAllocatedHeap() { return getCurrentThreadAllocatedHeap0(); }
  
  private static native int createResourceContext0(String paramString);
  
  private static native void destroyResourceContext0(int paramInt1, int paramInt2);
  
  public static native int setThreadResourceContext0(long paramLong, int paramInt);
  
  public static native int getThreadResourceContext0(long paramLong);
  
  public static int createResourceContext(String paramString) { return createResourceContext0(paramString); }
  
  public static void destroyResourceContext(int paramInt1, int paramInt2) { destroyResourceContext0(paramInt1, paramInt2); }
  
  public static int setThreadResourceContext(int paramInt) { return setThreadResourceContext0(0L, paramInt); }
  
  public static int setThreadResourceContext(long paramLong, int paramInt) { return setThreadResourceContext0(paramLong, paramInt); }
  
  public static int getThreadResourceContext() { return getThreadResourceContext0(0L); }
  
  private static native boolean getContextsRetainedMemory0(int[] paramArrayOfInt, long[] paramArrayOfLong, byte[] paramArrayOfByte);
  
  private static native void setRetainedMemoryNotificationEnabled0(Object paramObject);
  
  private static native void computeRetainedMemory0(int[] paramArrayOfInt, byte paramByte);
  
  public static boolean getContextsRetainedMemory(int[] paramArrayOfInt, long[] paramArrayOfLong, byte[] paramArrayOfByte) { return getContextsRetainedMemory0(paramArrayOfInt, paramArrayOfLong, paramArrayOfByte); }
  
  public static void setRetainedMemoryNotificationEnabled(Object paramObject) { setRetainedMemoryNotificationEnabled0(paramObject); }
  
  public static void computeRetainedMemory(int[] paramArrayOfInt, int paramInt) { computeRetainedMemory0(paramArrayOfInt, (byte)paramInt); }
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("resource");
            return null;
          }
        },  null, new Permission[] { new RuntimePermission("loadLibrary.resource") });
    enabledFeatures = featuresEnabled0();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\ResourceNatives.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */