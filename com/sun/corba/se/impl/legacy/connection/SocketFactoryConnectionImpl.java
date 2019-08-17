package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.transport.SocketOrChannelConnectionImpl;
import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.SocketInfo;

public class SocketFactoryConnectionImpl extends SocketOrChannelConnectionImpl {
  public SocketFactoryConnectionImpl(ORB paramORB, CorbaContactInfo paramCorbaContactInfo, boolean paramBoolean1, boolean paramBoolean2) {
    super(paramORB, paramBoolean1, paramBoolean2);
    this.contactInfo = paramCorbaContactInfo;
    boolean bool = !paramBoolean1;
    SocketInfo socketInfo = ((SocketFactoryContactInfoImpl)paramCorbaContactInfo).socketInfo;
    try {
      this.socket = paramORB.getORBData().getLegacySocketFactory().createSocket(socketInfo);
      this.socketChannel = this.socket.getChannel();
      if (this.socketChannel != null) {
        this.socketChannel.configureBlocking(bool);
      } else {
        setUseSelectThreadToWait(false);
      } 
      if (paramORB.transportDebugFlag)
        dprint(".initialize: connection created: " + this.socket); 
    } catch (GetEndPointInfoAgainException getEndPointInfoAgainException) {
      throw this.wrapper.connectFailure(getEndPointInfoAgainException, socketInfo.getType(), socketInfo.getHost(), Integer.toString(socketInfo.getPort()));
    } catch (Exception exception) {
      throw this.wrapper.connectFailure(exception, socketInfo.getType(), socketInfo.getHost(), Integer.toString(socketInfo.getPort()));
    } 
    this.state = 1;
  }
  
  public String toString() {
    synchronized (this.stateEvent) {
      return "SocketFactoryConnectionImpl[ " + ((this.socketChannel == null) ? this.socket.toString() : this.socketChannel.toString()) + " " + getStateString(this.state) + " " + shouldUseSelectThreadToWait() + " " + shouldUseWorkerThreadForEvent() + "]";
    } 
  }
  
  public void dprint(String paramString) { ORBUtility.dprint("SocketFactoryConnectionImpl", paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\legacy\connection\SocketFactoryConnectionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */