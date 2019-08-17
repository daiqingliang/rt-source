package com.sun.corba.se.pept.transport;

public interface InboundConnectionCache extends ConnectionCache {
  Connection get(Acceptor paramAcceptor);
  
  void put(Acceptor paramAcceptor, Connection paramConnection);
  
  void remove(Connection paramConnection);
  
  Acceptor getAcceptor();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\pept\transport\InboundConnectionCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */