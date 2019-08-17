package sun.management;

import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import javax.management.MBeanNotificationInfo;
import javax.management.ObjectName;

class MemoryManagerImpl extends NotificationEmitterSupport implements MemoryManagerMXBean {
  private final String name;
  
  private final boolean isValid;
  
  private MemoryPoolMXBean[] pools;
  
  private MBeanNotificationInfo[] notifInfo = null;
  
  MemoryManagerImpl(String paramString) {
    this.name = paramString;
    this.isValid = true;
    this.pools = null;
  }
  
  public String getName() { return this.name; }
  
  public boolean isValid() { return this.isValid; }
  
  public String[] getMemoryPoolNames() {
    MemoryPoolMXBean[] arrayOfMemoryPoolMXBean = getMemoryPools();
    String[] arrayOfString = new String[arrayOfMemoryPoolMXBean.length];
    for (byte b = 0; b < arrayOfMemoryPoolMXBean.length; b++)
      arrayOfString[b] = arrayOfMemoryPoolMXBean[b].getName(); 
    return arrayOfString;
  }
  
  MemoryPoolMXBean[] getMemoryPools() {
    if (this.pools == null)
      this.pools = getMemoryPools0(); 
    return this.pools;
  }
  
  private native MemoryPoolMXBean[] getMemoryPools0();
  
  public MBeanNotificationInfo[] getNotificationInfo() {
    synchronized (this) {
      if (this.notifInfo == null)
        this.notifInfo = new MBeanNotificationInfo[0]; 
    } 
    return this.notifInfo;
  }
  
  public ObjectName getObjectName() { return Util.newObjectName("java.lang:type=MemoryManager", getName()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\MemoryManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */