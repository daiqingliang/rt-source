package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import java.util.Collection;
import java.util.Hashtable;

public class CorbaOutboundConnectionCacheImpl extends CorbaConnectionCacheBase implements OutboundConnectionCache {
  protected Hashtable connectionCache = new Hashtable();
  
  public CorbaOutboundConnectionCacheImpl(ORB paramORB, ContactInfo paramContactInfo) { super(paramORB, paramContactInfo.getConnectionCacheType(), ((CorbaContactInfo)paramContactInfo).getMonitoringName()); }
  
  public Connection get(ContactInfo paramContactInfo) {
    if (this.orb.transportDebugFlag)
      dprint(".get: " + paramContactInfo + " " + paramContactInfo.hashCode()); 
    synchronized (backingStore()) {
      dprintStatistics();
      return (Connection)this.connectionCache.get(paramContactInfo);
    } 
  }
  
  public void put(ContactInfo paramContactInfo, Connection paramConnection) {
    if (this.orb.transportDebugFlag)
      dprint(".put: " + paramContactInfo + " " + paramContactInfo.hashCode() + " " + paramConnection); 
    synchronized (backingStore()) {
      this.connectionCache.put(paramContactInfo, paramConnection);
      paramConnection.setConnectionCache(this);
      dprintStatistics();
    } 
  }
  
  public void remove(ContactInfo paramContactInfo) {
    if (this.orb.transportDebugFlag)
      dprint(".remove: " + paramContactInfo + " " + paramContactInfo.hashCode()); 
    synchronized (backingStore()) {
      if (paramContactInfo != null)
        this.connectionCache.remove(paramContactInfo); 
      dprintStatistics();
    } 
  }
  
  public Collection values() { return this.connectionCache.values(); }
  
  protected Object backingStore() { return this.connectionCache; }
  
  protected void registerWithMonitoring() {
    MonitoredObject monitoredObject1 = this.orb.getMonitoringManager().getRootMonitoredObject();
    MonitoredObject monitoredObject2 = monitoredObject1.getChild("Connections");
    if (monitoredObject2 == null) {
      monitoredObject2 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("Connections", "Statistics on inbound/outbound connections");
      monitoredObject1.addChild(monitoredObject2);
    } 
    MonitoredObject monitoredObject3 = monitoredObject2.getChild("Outbound");
    if (monitoredObject3 == null) {
      monitoredObject3 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("Outbound", "Statistics on outbound connections");
      monitoredObject2.addChild(monitoredObject3);
    } 
    MonitoredObject monitoredObject4 = monitoredObject3.getChild(getMonitoringName());
    if (monitoredObject4 == null) {
      monitoredObject4 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject(getMonitoringName(), "Connection statistics");
      monitoredObject3.addChild(monitoredObject4);
    } 
    LongMonitoredAttributeBase longMonitoredAttributeBase = new LongMonitoredAttributeBase("NumberOfConnections", "The total number of connections") {
        public Object getValue() { return new Long(CorbaOutboundConnectionCacheImpl.this.numberOfConnections()); }
      };
    monitoredObject4.addAttribute(longMonitoredAttributeBase);
    longMonitoredAttributeBase = new LongMonitoredAttributeBase("NumberOfIdleConnections", "The number of idle connections") {
        public Object getValue() { return new Long(CorbaOutboundConnectionCacheImpl.this.numberOfIdleConnections()); }
      };
    monitoredObject4.addAttribute(longMonitoredAttributeBase);
    longMonitoredAttributeBase = new LongMonitoredAttributeBase("NumberOfBusyConnections", "The number of busy connections") {
        public Object getValue() { return new Long(CorbaOutboundConnectionCacheImpl.this.numberOfBusyConnections()); }
      };
    monitoredObject4.addAttribute(longMonitoredAttributeBase);
  }
  
  protected void dprint(String paramString) { ORBUtility.dprint("CorbaOutboundConnectionCacheImpl", paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\CorbaOutboundConnectionCacheImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */