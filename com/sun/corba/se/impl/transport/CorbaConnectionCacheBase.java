package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaConnectionCache;
import java.util.Collection;
import java.util.Iterator;

public abstract class CorbaConnectionCacheBase implements ConnectionCache, CorbaConnectionCache {
  protected ORB orb;
  
  protected long timestamp = 0L;
  
  protected String cacheType;
  
  protected String monitoringName;
  
  protected ORBUtilSystemException wrapper;
  
  protected CorbaConnectionCacheBase(ORB paramORB, String paramString1, String paramString2) {
    this.orb = paramORB;
    this.cacheType = paramString1;
    this.monitoringName = paramString2;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.transport");
    registerWithMonitoring();
    dprintCreation();
  }
  
  public String getCacheType() { return this.cacheType; }
  
  public void stampTime(Connection paramConnection) { paramConnection.setTimeStamp(this.timestamp++); }
  
  public long numberOfConnections() {
    synchronized (backingStore()) {
      return values().size();
    } 
  }
  
  public void close() {
    synchronized (backingStore()) {
      for (Object object : values())
        ((CorbaConnection)object).closeConnectionResources(); 
    } 
  }
  
  public long numberOfIdleConnections() {
    long l = 0L;
    synchronized (backingStore()) {
      Iterator iterator = values().iterator();
      while (iterator.hasNext()) {
        if (!((Connection)iterator.next()).isBusy())
          l++; 
      } 
    } 
    return l;
  }
  
  public long numberOfBusyConnections() {
    long l = 0L;
    synchronized (backingStore()) {
      Iterator iterator = values().iterator();
      while (iterator.hasNext()) {
        if (((Connection)iterator.next()).isBusy())
          l++; 
      } 
    } 
    return l;
  }
  
  public boolean reclaim() {
    try {
      long l = numberOfConnections();
      if (this.orb.transportDebugFlag)
        dprint(".reclaim->: " + l + " (" + this.orb.getORBData().getHighWaterMark() + "/" + this.orb.getORBData().getLowWaterMark() + "/" + this.orb.getORBData().getNumberToReclaim() + ")"); 
      if (l <= this.orb.getORBData().getHighWaterMark() || l < this.orb.getORBData().getLowWaterMark())
        return false; 
      Object object = backingStore();
      synchronized (object) {
        for (byte b = 0; b < this.orb.getORBData().getNumberToReclaim(); b++) {
          Connection connection = null;
          long l1 = Float.MAX_VALUE;
          for (Connection connection1 : values()) {
            if (!connection1.isBusy() && connection1.getTimeStamp() < l1) {
              connection = connection1;
              l1 = connection1.getTimeStamp();
            } 
          } 
          if (connection == null)
            return false; 
          try {
            if (this.orb.transportDebugFlag)
              dprint(".reclaim: closing: " + connection); 
            connection.close();
          } catch (Exception exception) {}
        } 
        if (this.orb.transportDebugFlag)
          dprint(".reclaim: connections reclaimed (" + (l - numberOfConnections()) + ")"); 
      } 
      return true;
    } finally {
      if (this.orb.transportDebugFlag)
        dprint(".reclaim<-: " + numberOfConnections()); 
    } 
  }
  
  public String getMonitoringName() { return this.monitoringName; }
  
  public abstract Collection values();
  
  protected abstract Object backingStore();
  
  protected abstract void registerWithMonitoring();
  
  protected void dprintCreation() {
    if (this.orb.transportDebugFlag)
      dprint(".constructor: cacheType: " + getCacheType() + " monitoringName: " + getMonitoringName()); 
  }
  
  protected void dprintStatistics() {
    if (this.orb.transportDebugFlag)
      dprint(".stats: " + numberOfConnections() + "/total " + numberOfBusyConnections() + "/busy " + numberOfIdleConnections() + "/idle (" + this.orb.getORBData().getHighWaterMark() + "/" + this.orb.getORBData().getLowWaterMark() + "/" + this.orb.getORBData().getNumberToReclaim() + ")"); 
  }
  
  protected void dprint(String paramString) { ORBUtility.dprint("CorbaConnectionCacheBase", paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\CorbaConnectionCacheBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */