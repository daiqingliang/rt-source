package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaAcceptor;
import java.util.ArrayList;
import java.util.Collection;

public class CorbaInboundConnectionCacheImpl extends CorbaConnectionCacheBase implements InboundConnectionCache {
  protected Collection connectionCache = new ArrayList();
  
  private Acceptor acceptor;
  
  public CorbaInboundConnectionCacheImpl(ORB paramORB, Acceptor paramAcceptor) {
    super(paramORB, paramAcceptor.getConnectionCacheType(), ((CorbaAcceptor)paramAcceptor).getMonitoringName());
    this.acceptor = paramAcceptor;
    if (paramORB.transportDebugFlag)
      dprint(": " + paramAcceptor); 
  }
  
  public void close() {
    super.close();
    if (this.orb.transportDebugFlag)
      dprint(".close: " + this.acceptor); 
    this.acceptor.close();
  }
  
  public Connection get(Acceptor paramAcceptor) { throw this.wrapper.methodShouldNotBeCalled(); }
  
  public Acceptor getAcceptor() { return this.acceptor; }
  
  public void put(Acceptor paramAcceptor, Connection paramConnection) {
    if (this.orb.transportDebugFlag)
      dprint(".put: " + paramAcceptor + " " + paramConnection); 
    synchronized (backingStore()) {
      this.connectionCache.add(paramConnection);
      paramConnection.setConnectionCache(this);
      dprintStatistics();
    } 
  }
  
  public void remove(Connection paramConnection) {
    if (this.orb.transportDebugFlag)
      dprint(".remove: " + paramConnection); 
    synchronized (backingStore()) {
      this.connectionCache.remove(paramConnection);
      dprintStatistics();
    } 
  }
  
  public Collection values() { return this.connectionCache; }
  
  protected Object backingStore() { return this.connectionCache; }
  
  protected void registerWithMonitoring() {
    MonitoredObject monitoredObject1 = this.orb.getMonitoringManager().getRootMonitoredObject();
    MonitoredObject monitoredObject2 = monitoredObject1.getChild("Connections");
    if (monitoredObject2 == null) {
      monitoredObject2 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("Connections", "Statistics on inbound/outbound connections");
      monitoredObject1.addChild(monitoredObject2);
    } 
    MonitoredObject monitoredObject3 = monitoredObject2.getChild("Inbound");
    if (monitoredObject3 == null) {
      monitoredObject3 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("Inbound", "Statistics on inbound connections");
      monitoredObject2.addChild(monitoredObject3);
    } 
    MonitoredObject monitoredObject4 = monitoredObject3.getChild(getMonitoringName());
    if (monitoredObject4 == null) {
      monitoredObject4 = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject(getMonitoringName(), "Connection statistics");
      monitoredObject3.addChild(monitoredObject4);
    } 
    LongMonitoredAttributeBase longMonitoredAttributeBase = new LongMonitoredAttributeBase("NumberOfConnections", "The total number of connections") {
        public Object getValue() { return new Long(CorbaInboundConnectionCacheImpl.this.numberOfConnections()); }
      };
    monitoredObject4.addAttribute(longMonitoredAttributeBase);
    longMonitoredAttributeBase = new LongMonitoredAttributeBase("NumberOfIdleConnections", "The number of idle connections") {
        public Object getValue() { return new Long(CorbaInboundConnectionCacheImpl.this.numberOfIdleConnections()); }
      };
    monitoredObject4.addAttribute(longMonitoredAttributeBase);
    longMonitoredAttributeBase = new LongMonitoredAttributeBase("NumberOfBusyConnections", "The number of busy connections") {
        public Object getValue() { return new Long(CorbaInboundConnectionCacheImpl.this.numberOfBusyConnections()); }
      };
    monitoredObject4.addAttribute(longMonitoredAttributeBase);
  }
  
  protected void dprint(String paramString) { ORBUtility.dprint("CorbaInboundConnectionCacheImpl", paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\CorbaInboundConnectionCacheImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */