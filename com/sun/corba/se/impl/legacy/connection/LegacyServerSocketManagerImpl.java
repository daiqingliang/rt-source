package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketManager;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Collection;
import java.util.Iterator;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;

public class LegacyServerSocketManagerImpl implements LegacyServerSocketManager {
  protected ORB orb;
  
  private ORBUtilSystemException wrapper;
  
  public LegacyServerSocketManagerImpl(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.transport");
  }
  
  public int legacyGetTransientServerPort(String paramString) { return legacyGetServerPort(paramString, false); }
  
  public int legacyGetPersistentServerPort(String paramString) {
    if (this.orb.getORBData().getServerIsORBActivated())
      return legacyGetServerPort(paramString, true); 
    if (this.orb.getORBData().getPersistentPortInitialized())
      return this.orb.getORBData().getPersistentServerPort(); 
    throw this.wrapper.persistentServerportNotSet(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public int legacyGetTransientOrPersistentServerPort(String paramString) { return legacyGetServerPort(paramString, this.orb.getORBData().getServerIsORBActivated()); }
  
  public LegacyServerSocketEndPointInfo legacyGetEndpoint(String paramString) {
    Iterator iterator = getAcceptorIterator();
    while (iterator.hasNext()) {
      LegacyServerSocketEndPointInfo legacyServerSocketEndPointInfo = cast(iterator.next());
      if (legacyServerSocketEndPointInfo != null && paramString.equals(legacyServerSocketEndPointInfo.getName()))
        return legacyServerSocketEndPointInfo; 
    } 
    throw new INTERNAL("No acceptor for: " + paramString);
  }
  
  public boolean legacyIsLocalServerPort(int paramInt) {
    Iterator iterator = getAcceptorIterator();
    while (iterator.hasNext()) {
      LegacyServerSocketEndPointInfo legacyServerSocketEndPointInfo = cast(iterator.next());
      if (legacyServerSocketEndPointInfo != null && legacyServerSocketEndPointInfo.getPort() == paramInt)
        return true; 
    } 
    return false;
  }
  
  private int legacyGetServerPort(String paramString, boolean paramBoolean) {
    Iterator iterator = getAcceptorIterator();
    while (iterator.hasNext()) {
      LegacyServerSocketEndPointInfo legacyServerSocketEndPointInfo = cast(iterator.next());
      if (legacyServerSocketEndPointInfo != null && legacyServerSocketEndPointInfo.getType().equals(paramString))
        return paramBoolean ? legacyServerSocketEndPointInfo.getLocatorPort() : legacyServerSocketEndPointInfo.getPort(); 
    } 
    return -1;
  }
  
  private Iterator getAcceptorIterator() {
    Collection collection = this.orb.getCorbaTransportManager().getAcceptors(null, null);
    if (collection != null)
      return collection.iterator(); 
    throw this.wrapper.getServerPortCalledBeforeEndpointsInitialized();
  }
  
  private LegacyServerSocketEndPointInfo cast(Object paramObject) { return (paramObject instanceof LegacyServerSocketEndPointInfo) ? (LegacyServerSocketEndPointInfo)paramObject : null; }
  
  protected void dprint(String paramString) { ORBUtility.dprint("LegacyServerSocketManagerImpl", paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\legacy\connection\LegacyServerSocketManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */