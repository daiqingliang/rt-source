package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.SocketInfo;

public class SocketOrChannelContactInfoImpl extends CorbaContactInfoBase implements SocketInfo {
  protected boolean isHashCodeCached = false;
  
  protected int cachedHashCode;
  
  protected String socketType;
  
  protected String hostname;
  
  protected int port;
  
  protected SocketOrChannelContactInfoImpl() {}
  
  protected SocketOrChannelContactInfoImpl(ORB paramORB, CorbaContactInfoList paramCorbaContactInfoList) {
    this.orb = paramORB;
    this.contactInfoList = paramCorbaContactInfoList;
  }
  
  public SocketOrChannelContactInfoImpl(ORB paramORB, CorbaContactInfoList paramCorbaContactInfoList, String paramString1, String paramString2, int paramInt) {
    this(paramORB, paramCorbaContactInfoList);
    this.socketType = paramString1;
    this.hostname = paramString2;
    this.port = paramInt;
  }
  
  public SocketOrChannelContactInfoImpl(ORB paramORB, CorbaContactInfoList paramCorbaContactInfoList, IOR paramIOR, short paramShort, String paramString1, String paramString2, int paramInt) {
    this(paramORB, paramCorbaContactInfoList, paramString1, paramString2, paramInt);
    this.effectiveTargetIOR = paramIOR;
    this.addressingDisposition = paramShort;
  }
  
  public boolean isConnectionBased() { return true; }
  
  public boolean shouldCacheConnection() { return true; }
  
  public String getConnectionCacheType() { return "SocketOrChannelConnectionCache"; }
  
  public Connection createConnection() { return new SocketOrChannelConnectionImpl(this.orb, this, this.socketType, this.hostname, this.port); }
  
  public String getMonitoringName() { return "SocketConnections"; }
  
  public String getType() { return this.socketType; }
  
  public String getHost() { return this.hostname; }
  
  public int getPort() { return this.port; }
  
  public int hashCode() {
    if (!this.isHashCodeCached) {
      this.cachedHashCode = this.socketType.hashCode() ^ this.hostname.hashCode() ^ this.port;
      this.isHashCodeCached = true;
    } 
    return this.cachedHashCode;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (!(paramObject instanceof SocketOrChannelContactInfoImpl))
      return false; 
    SocketOrChannelContactInfoImpl socketOrChannelContactInfoImpl = (SocketOrChannelContactInfoImpl)paramObject;
    if (this.port != socketOrChannelContactInfoImpl.port)
      return false; 
    if (!this.hostname.equals(socketOrChannelContactInfoImpl.hostname))
      return false; 
    if (this.socketType == null) {
      if (socketOrChannelContactInfoImpl.socketType != null)
        return false; 
    } else if (!this.socketType.equals(socketOrChannelContactInfoImpl.socketType)) {
      return false;
    } 
    return true;
  }
  
  public String toString() { return "SocketOrChannelContactInfoImpl[" + this.socketType + " " + this.hostname + " " + this.port + "]"; }
  
  protected void dprint(String paramString) { ORBUtility.dprint("SocketOrChannelContactInfoImpl", paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\SocketOrChannelContactInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */