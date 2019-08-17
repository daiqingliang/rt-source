package com.sun.jndi.ldap.pool;

public interface PoolCallback {
  boolean releasePooledConnection(PooledConnection paramPooledConnection);
  
  boolean removePooledConnection(PooledConnection paramPooledConnection);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\pool\PoolCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */