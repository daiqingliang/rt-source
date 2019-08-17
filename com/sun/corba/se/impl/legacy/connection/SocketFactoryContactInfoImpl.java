package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.transport.SocketOrChannelContactInfoImpl;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.SocketInfo;

public class SocketFactoryContactInfoImpl extends SocketOrChannelContactInfoImpl {
  protected ORBUtilSystemException wrapper;
  
  protected SocketInfo socketInfo;
  
  public SocketFactoryContactInfoImpl() {}
  
  public SocketFactoryContactInfoImpl(ORB paramORB, CorbaContactInfoList paramCorbaContactInfoList, IOR paramIOR, short paramShort, SocketInfo paramSocketInfo) {
    super(paramORB, paramCorbaContactInfoList);
    this.effectiveTargetIOR = paramIOR;
    this.addressingDisposition = paramShort;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.transport");
    this.socketInfo = paramORB.getORBData().getLegacySocketFactory().getEndPointInfo(paramORB, paramIOR, paramSocketInfo);
    this.socketType = this.socketInfo.getType();
    this.hostname = this.socketInfo.getHost();
    this.port = this.socketInfo.getPort();
  }
  
  public Connection createConnection() { return new SocketFactoryConnectionImpl(this.orb, this, this.orb.getORBData().connectionSocketUseSelectThreadToWait(), this.orb.getORBData().connectionSocketUseWorkerThreadForEvent()); }
  
  public String toString() { return "SocketFactoryContactInfoImpl[" + this.socketType + " " + this.hostname + " " + this.port + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\legacy\connection\SocketFactoryContactInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */