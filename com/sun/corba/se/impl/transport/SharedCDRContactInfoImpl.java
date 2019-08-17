package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.protocol.CorbaMessageMediatorImpl;
import com.sun.corba.se.impl.protocol.SharedCDRClientRequestDispatcherImpl;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import sun.corba.OutputStreamFactory;

public class SharedCDRContactInfoImpl extends CorbaContactInfoBase {
  private static int requestId = 0;
  
  protected ORBUtilSystemException wrapper;
  
  public SharedCDRContactInfoImpl(ORB paramORB, CorbaContactInfoList paramCorbaContactInfoList, IOR paramIOR, short paramShort) {
    this.orb = paramORB;
    this.contactInfoList = paramCorbaContactInfoList;
    this.effectiveTargetIOR = paramIOR;
    this.addressingDisposition = paramShort;
  }
  
  public ClientRequestDispatcher getClientRequestDispatcher() { return new SharedCDRClientRequestDispatcherImpl(); }
  
  public boolean isConnectionBased() { return false; }
  
  public boolean shouldCacheConnection() { return false; }
  
  public String getConnectionCacheType() { throw getWrapper().methodShouldNotBeCalled(); }
  
  public Connection createConnection() { throw getWrapper().methodShouldNotBeCalled(); }
  
  public MessageMediator createMessageMediator(Broker paramBroker, ContactInfo paramContactInfo, Connection paramConnection, String paramString, boolean paramBoolean) {
    if (paramConnection != null)
      throw new RuntimeException("connection is not null"); 
    return new CorbaMessageMediatorImpl((ORB)paramBroker, paramContactInfo, null, GIOPVersion.chooseRequestVersion((ORB)paramBroker, this.effectiveTargetIOR), this.effectiveTargetIOR, requestId++, getAddressingDisposition(), paramString, paramBoolean);
  }
  
  public OutputObject createOutputObject(MessageMediator paramMessageMediator) {
    CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    CDROutputObject cDROutputObject = OutputStreamFactory.newCDROutputObject(this.orb, paramMessageMediator, corbaMessageMediator.getRequestHeader(), corbaMessageMediator.getStreamFormatVersion(), 0);
    paramMessageMediator.setOutputObject(cDROutputObject);
    return cDROutputObject;
  }
  
  public String getMonitoringName() { throw getWrapper().methodShouldNotBeCalled(); }
  
  public String toString() { return "SharedCDRContactInfoImpl[]"; }
  
  protected ORBUtilSystemException getWrapper() {
    if (this.wrapper == null)
      this.wrapper = ORBUtilSystemException.get(this.orb, "rpc.transport"); 
    return this.wrapper;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\SharedCDRContactInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */