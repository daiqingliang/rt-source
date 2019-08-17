package sun.management;

import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GarbageCollectorMXBean;
import com.sun.management.GcInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.List;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;

class GarbageCollectorImpl extends MemoryManagerImpl implements GarbageCollectorMXBean {
  private String[] poolNames = null;
  
  private GcInfoBuilder gcInfoBuilder;
  
  private static final String notifName = "javax.management.Notification";
  
  private static final String[] gcNotifTypes = { "com.sun.management.gc.notification" };
  
  private static long seqNumber = 0L;
  
  GarbageCollectorImpl(String paramString) { super(paramString); }
  
  public native long getCollectionCount();
  
  public native long getCollectionTime();
  
  String[] getAllPoolNames() {
    if (this.poolNames == null) {
      List list = ManagementFactory.getMemoryPoolMXBeans();
      this.poolNames = new String[list.size()];
      byte b = 0;
      for (MemoryPoolMXBean memoryPoolMXBean : list)
        this.poolNames[b++] = memoryPoolMXBean.getName(); 
    } 
    return this.poolNames;
  }
  
  private GcInfoBuilder getGcInfoBuilder() {
    if (this.gcInfoBuilder == null)
      this.gcInfoBuilder = new GcInfoBuilder(this, getAllPoolNames()); 
    return this.gcInfoBuilder;
  }
  
  public GcInfo getLastGcInfo() { return getGcInfoBuilder().getLastGcInfo(); }
  
  public MBeanNotificationInfo[] getNotificationInfo() { return new MBeanNotificationInfo[] { new MBeanNotificationInfo(gcNotifTypes, "javax.management.Notification", "GC Notification") }; }
  
  private static long getNextSeqNumber() { return ++seqNumber; }
  
  void createGCNotification(long paramLong, String paramString1, String paramString2, String paramString3, GcInfo paramGcInfo) {
    if (!hasListeners())
      return; 
    Notification notification = new Notification("com.sun.management.gc.notification", getObjectName(), getNextSeqNumber(), paramLong, paramString1);
    GarbageCollectionNotificationInfo garbageCollectionNotificationInfo = new GarbageCollectionNotificationInfo(paramString1, paramString2, paramString3, paramGcInfo);
    CompositeData compositeData = GarbageCollectionNotifInfoCompositeData.toCompositeData(garbageCollectionNotificationInfo);
    notification.setUserData(compositeData);
    sendNotification(notification);
  }
  
  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) {
    boolean bool1 = hasListeners();
    super.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
    boolean bool2 = hasListeners();
    if (!bool1 && bool2)
      setNotificationEnabled(this, true); 
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener) throws ListenerNotFoundException {
    boolean bool1 = hasListeners();
    super.removeNotificationListener(paramNotificationListener);
    boolean bool2 = hasListeners();
    if (bool1 && !bool2)
      setNotificationEnabled(this, false); 
  }
  
  public void removeNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject) {
    boolean bool1 = hasListeners();
    super.removeNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
    boolean bool2 = hasListeners();
    if (bool1 && !bool2)
      setNotificationEnabled(this, false); 
  }
  
  public ObjectName getObjectName() { return Util.newObjectName("java.lang:type=GarbageCollector", getName()); }
  
  native void setNotificationEnabled(GarbageCollectorMXBean paramGarbageCollectorMXBean, boolean paramBoolean);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\GarbageCollectorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */