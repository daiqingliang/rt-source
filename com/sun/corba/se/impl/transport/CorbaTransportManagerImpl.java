package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.oa.poa.Policies;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.pept.transport.ConnectionCache;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.InboundConnectionCache;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaAcceptor;
import com.sun.corba.se.spi.transport.CorbaTransportManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CorbaTransportManagerImpl implements CorbaTransportManager {
  protected ORB orb;
  
  protected List acceptors;
  
  protected Map outboundConnectionCaches;
  
  protected Map inboundConnectionCaches;
  
  protected Selector selector;
  
  public CorbaTransportManagerImpl(ORB paramORB) {
    this.orb = paramORB;
    this.acceptors = new ArrayList();
    this.outboundConnectionCaches = new HashMap();
    this.inboundConnectionCaches = new HashMap();
    this.selector = new SelectorImpl(paramORB);
  }
  
  public ByteBufferPool getByteBufferPool(int paramInt) { throw new RuntimeException(); }
  
  public OutboundConnectionCache getOutboundConnectionCache(ContactInfo paramContactInfo) {
    synchronized (paramContactInfo) {
      if (paramContactInfo.getConnectionCache() == null) {
        OutboundConnectionCache outboundConnectionCache = null;
        synchronized (this.outboundConnectionCaches) {
          outboundConnectionCache = (OutboundConnectionCache)this.outboundConnectionCaches.get(paramContactInfo.getConnectionCacheType());
          if (outboundConnectionCache == null) {
            outboundConnectionCache = new CorbaOutboundConnectionCacheImpl(this.orb, paramContactInfo);
            this.outboundConnectionCaches.put(paramContactInfo.getConnectionCacheType(), outboundConnectionCache);
          } 
        } 
        paramContactInfo.setConnectionCache(outboundConnectionCache);
      } 
      return paramContactInfo.getConnectionCache();
    } 
  }
  
  public Collection getOutboundConnectionCaches() { return this.outboundConnectionCaches.values(); }
  
  public InboundConnectionCache getInboundConnectionCache(Acceptor paramAcceptor) {
    synchronized (paramAcceptor) {
      if (paramAcceptor.getConnectionCache() == null) {
        InboundConnectionCache inboundConnectionCache = null;
        synchronized (this.inboundConnectionCaches) {
          inboundConnectionCache = (InboundConnectionCache)this.inboundConnectionCaches.get(paramAcceptor.getConnectionCacheType());
          if (inboundConnectionCache == null) {
            inboundConnectionCache = new CorbaInboundConnectionCacheImpl(this.orb, paramAcceptor);
            this.inboundConnectionCaches.put(paramAcceptor.getConnectionCacheType(), inboundConnectionCache);
          } 
        } 
        paramAcceptor.setConnectionCache(inboundConnectionCache);
      } 
      return paramAcceptor.getConnectionCache();
    } 
  }
  
  public Collection getInboundConnectionCaches() { return this.inboundConnectionCaches.values(); }
  
  public Selector getSelector(int paramInt) { return this.selector; }
  
  public void registerAcceptor(Acceptor paramAcceptor) {
    if (this.orb.transportDebugFlag)
      dprint(".registerAcceptor->: " + paramAcceptor); 
    this.acceptors.add(paramAcceptor);
    if (this.orb.transportDebugFlag)
      dprint(".registerAcceptor<-: " + paramAcceptor); 
  }
  
  public Collection getAcceptors() { return getAcceptors(null, null); }
  
  public void unregisterAcceptor(Acceptor paramAcceptor) { this.acceptors.remove(paramAcceptor); }
  
  public void close() {
    try {
      if (this.orb.transportDebugFlag)
        dprint(".close->"); 
      for (Object object : this.outboundConnectionCaches.values())
        ((ConnectionCache)object).close(); 
      for (Object object : this.inboundConnectionCaches.values()) {
        ((ConnectionCache)object).close();
        unregisterAcceptor(((InboundConnectionCache)object).getAcceptor());
      } 
      getSelector(0).close();
    } finally {
      if (this.orb.transportDebugFlag)
        dprint(".close<-"); 
    } 
  }
  
  public Collection getAcceptors(String paramString, ObjectAdapterId paramObjectAdapterId) {
    for (Acceptor acceptor : this.acceptors) {
      if (acceptor.initialize() && acceptor.shouldRegisterAcceptEvent())
        this.orb.getTransportManager().getSelector(0).registerForEvent(acceptor.getEventHandler()); 
    } 
    return this.acceptors;
  }
  
  public void addToIORTemplate(IORTemplate paramIORTemplate, Policies paramPolicies, String paramString1, String paramString2, ObjectAdapterId paramObjectAdapterId) {
    for (CorbaAcceptor corbaAcceptor : getAcceptors(paramString2, paramObjectAdapterId))
      corbaAcceptor.addToIORTemplate(paramIORTemplate, paramPolicies, paramString1); 
  }
  
  protected void dprint(String paramString) { ORBUtility.dprint("CorbaTransportManagerImpl", paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\CorbaTransportManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */