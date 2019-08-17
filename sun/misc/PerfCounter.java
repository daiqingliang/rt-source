package sun.misc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.security.AccessController;

public class PerfCounter {
  private static final Perf perf = (Perf)AccessController.doPrivileged(new Perf.GetPerfAction());
  
  private static final int V_Constant = 1;
  
  private static final int V_Monotonic = 2;
  
  private static final int V_Variable = 3;
  
  private static final int U_None = 1;
  
  private final String name;
  
  private final LongBuffer lb;
  
  private PerfCounter(String paramString, int paramInt) {
    this.name = paramString;
    ByteBuffer byteBuffer = perf.createLong(paramString, paramInt, 1, 0L);
    byteBuffer.order(ByteOrder.nativeOrder());
    this.lb = byteBuffer.asLongBuffer();
  }
  
  static PerfCounter newPerfCounter(String paramString) { return new PerfCounter(paramString, 3); }
  
  static PerfCounter newConstantPerfCounter(String paramString) { return new PerfCounter(paramString, 1); }
  
  public long get() { return this.lb.get(0); }
  
  public void set(long paramLong) { this.lb.put(0, paramLong); }
  
  public void add(long paramLong) {
    long l = get() + paramLong;
    this.lb.put(0, l);
  }
  
  public void increment() { add(1L); }
  
  public void addTime(long paramLong) { add(paramLong); }
  
  public void addElapsedTimeFrom(long paramLong) { add(System.nanoTime() - paramLong); }
  
  public String toString() { return this.name + " = " + get(); }
  
  public static PerfCounter getFindClasses() { return CoreCounters.lc; }
  
  public static PerfCounter getFindClassTime() { return CoreCounters.lct; }
  
  public static PerfCounter getReadClassBytesTime() { return CoreCounters.rcbt; }
  
  public static PerfCounter getParentDelegationTime() { return CoreCounters.pdt; }
  
  public static PerfCounter getZipFileCount() { return CoreCounters.zfc; }
  
  public static PerfCounter getZipFileOpenTime() { return CoreCounters.zfot; }
  
  public static PerfCounter getD3DAvailable() { return WindowsClientCounters.d3dAvailable; }
  
  static class CoreCounters {
    static final PerfCounter pdt;
    
    static final PerfCounter lc;
    
    static final PerfCounter lct;
    
    static final PerfCounter rcbt;
    
    static final PerfCounter zfc;
    
    static final PerfCounter zfot = (zfc = (rcbt = (lct = (lc = (pdt = PerfCounter.newPerfCounter("sun.classloader.parentDelegationTime")).newPerfCounter("sun.classloader.findClasses")).newPerfCounter("sun.classloader.findClassTime")).newPerfCounter("sun.urlClassLoader.readClassBytesTime")).newPerfCounter("sun.zip.zipFiles")).newPerfCounter("sun.zip.zipFile.openTime");
  }
  
  static class WindowsClientCounters {
    static final PerfCounter d3dAvailable = PerfCounter.newConstantPerfCounter("sun.java2d.d3d.available");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\PerfCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */