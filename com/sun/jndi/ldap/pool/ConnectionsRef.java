package com.sun.jndi.ldap.pool;

final class ConnectionsRef {
  private final Connections conns;
  
  ConnectionsRef(Connections paramConnections) { this.conns = paramConnections; }
  
  Connections getConnections() { return this.conns; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\pool\ConnectionsRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */