package sun.management;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class HotspotInternal implements HotspotInternalMBean, MBeanRegistration {
  private static final String HOTSPOT_INTERNAL_MBEAN_NAME = "sun.management:type=HotspotInternal";
  
  private static ObjectName objName = Util.newObjectName("sun.management:type=HotspotInternal");
  
  private MBeanServer server = null;
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName) throws Exception {
    ManagementFactoryHelper.registerInternalMBeans(paramMBeanServer);
    this.server = paramMBeanServer;
    return objName;
  }
  
  public void postRegister(Boolean paramBoolean) {}
  
  public void preDeregister() { ManagementFactoryHelper.unregisterInternalMBeans(this.server); }
  
  public void postDeregister() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\management\HotspotInternal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */