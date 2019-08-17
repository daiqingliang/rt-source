package sun.management;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import sun.misc.VM;

class MemoryImpl extends NotificationEmitterSupport implements MemoryMXBean {
  private final VMManagement jvm;
  
  private static MemoryPoolMXBean[] pools = null;
  
  private static MemoryManagerMXBean[] mgrs = null;
  
  private static final String notifName = "javax.management.Notification";
  
  private static final String[] notifTypes = { "java.management.memory.threshold.exceeded", "java.management.memory.collection.threshold.exceeded" };
  
  private static final String[] notifMsgs = { "Memory usage exceeds usage threshold", "Memory usage exceeds collection usage threshold" };
  
  private static long seqNumber = 0L;
  
  MemoryImpl(VMManagement paramVMManagement) { this.jvm = paramVMManagement; }
  
  public int getObjectPendingFinalizationCount() { return VM.getFinalRefCount(); }
  
  public void gc() { Runtime.getRuntime().gc(); }
  
  public MemoryUsage getHeapMemoryUsage() { return getMemoryUsage0(true); }
  
  public MemoryUsage getNonHeapMemoryUsage() { return getMemoryUsage0(false); }
  
  public boolean isVerbose() { return this.jvm.getVerboseGC(); }
  
  public void setVerbose(boolean paramBoolean) {
    Util.checkControlAccess();
    setVerboseGC(paramBoolean);
  }
  
  static MemoryPoolMXBean[] getMemoryPools() {
    if (pools == null)
      pools = getMemoryPools0(); 
    return pools;
  }
  
  static MemoryManagerMXBean[] getMemoryManagers() {
    if (mgrs == null)
      mgrs = getMemoryManagers0(); 
    return mgrs;
  }
  
  private static native MemoryPoolMXBean[] getMemoryPools0();
  
  private static native MemoryManagerMXBean[] getMemoryManagers0();
  
  private native MemoryUsage getMemoryUsage0(boolean paramBoolean);
  
  private native void setVerboseGC(boolean paramBoolean);
  
  public MBeanNotificationInfo[] getNotificationInfo() { return new MBeanNotificationInfo[] { new MBeanNotificationInfo(notifTypes, "javax.management.Notification", "Memory Notification") }; }
  
  private static String getNotifMsg(String paramString) {
    for (byte b = 0; b < notifTypes.length; b++) {
      if (paramString == notifTypes[b])
        return notifMsgs[b]; 
    } 
    return "Unknown message";
  }
  
  private static long getNextSeqNumber() { return ++seqNumber; }
  
  static void createNotification(String paramString1, String paramString2, MemoryUsage paramMemoryUsage, long paramLong) {
    MemoryImpl memoryImpl = (MemoryImpl)ManagementFactory.getMemoryMXBean();
    if (!memoryImpl.hasListeners())
      return; 
    long l = System.currentTimeMillis();
    String str = getNotifMsg(paramString1);
    Notification notification = new Notification(paramString1, memoryImpl.getObjectName(), getNextSeqNumber(), l, str);
    MemoryNotificationInfo memoryNotificationInfo = new MemoryNotificationInfo(paramString2, paramMemoryUsage, paramLong);
    CompositeData compositeData = MemoryNotifInfoCompositeData.toCompositeData(memoryNotificationInfo);
    notification.setUserData(compositeData);
    memoryImpl.sendNotification(notification);
  }
  
  public ObjectName getObjectName() { return Util.newObjectName("java.lang:type=Memory"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\MemoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */