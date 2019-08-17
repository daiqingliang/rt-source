package com.sun.jndi.ldap.pool;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

class ConnectionsWeakRef extends WeakReference<ConnectionsRef> {
  private final Connections conns;
  
  ConnectionsWeakRef(ConnectionsRef paramConnectionsRef, ReferenceQueue<? super ConnectionsRef> paramReferenceQueue) {
    super(paramConnectionsRef, paramReferenceQueue);
    this.conns = paramConnectionsRef.getConnections();
  }
  
  Connections getConnections() { return this.conns; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\pool\ConnectionsWeakRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */