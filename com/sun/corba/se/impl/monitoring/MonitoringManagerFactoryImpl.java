package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoringManager;
import com.sun.corba.se.spi.monitoring.MonitoringManagerFactory;
import java.util.HashMap;

public class MonitoringManagerFactoryImpl implements MonitoringManagerFactory {
  private HashMap monitoringManagerTable = new HashMap();
  
  public MonitoringManager createMonitoringManager(String paramString1, String paramString2) {
    MonitoringManagerImpl monitoringManagerImpl = null;
    monitoringManagerImpl = (MonitoringManagerImpl)this.monitoringManagerTable.get(paramString1);
    if (monitoringManagerImpl == null) {
      monitoringManagerImpl = new MonitoringManagerImpl(paramString1, paramString2);
      this.monitoringManagerTable.put(paramString1, monitoringManagerImpl);
    } 
    return monitoringManagerImpl;
  }
  
  public void remove(String paramString) { this.monitoringManagerTable.remove(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\monitoring\MonitoringManagerFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */