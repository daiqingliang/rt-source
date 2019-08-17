package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.transport.SocketOrChannelAcceptorImpl;
import com.sun.corba.se.spi.orb.ORB;

public class SocketFactoryAcceptorImpl extends SocketOrChannelAcceptorImpl {
  public SocketFactoryAcceptorImpl(ORB paramORB, int paramInt, String paramString1, String paramString2) { super(paramORB, paramInt, paramString1, paramString2); }
  
  public boolean initialize() {
    if (this.initialized)
      return false; 
    if (this.orb.transportDebugFlag)
      dprint("initialize: " + this); 
    try {
      this.serverSocket = this.orb.getORBData().getLegacySocketFactory().createServerSocket(this.type, this.port);
      internalInitialize();
    } catch (Throwable throwable) {
      throw this.wrapper.createListenerFailed(throwable, Integer.toString(this.port));
    } 
    this.initialized = true;
    return true;
  }
  
  protected String toStringName() { return "SocketFactoryAcceptorImpl"; }
  
  protected void dprint(String paramString) { ORBUtility.dprint(toStringName(), paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\legacy\connection\SocketFactoryAcceptorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */