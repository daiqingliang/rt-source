package com.sun.jndi.ldap.pool;

import javax.naming.NamingException;

public interface PooledConnectionFactory {
  PooledConnection createPooledConnection(PoolCallback paramPoolCallback) throws NamingException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\pool\PooledConnectionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */