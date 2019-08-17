package com.sun.jndi.ldap;

import com.sun.jndi.ldap.pool.PoolCallback;
import com.sun.jndi.ldap.pool.PooledConnection;
import com.sun.jndi.ldap.pool.PooledConnectionFactory;
import java.io.OutputStream;
import javax.naming.NamingException;

final class LdapClientFactory implements PooledConnectionFactory {
  private final String host;
  
  private final int port;
  
  private final String socketFactory;
  
  private final int connTimeout;
  
  private final int readTimeout;
  
  private final OutputStream trace;
  
  LdapClientFactory(String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3, OutputStream paramOutputStream) {
    this.host = paramString1;
    this.port = paramInt1;
    this.socketFactory = paramString2;
    this.connTimeout = paramInt2;
    this.readTimeout = paramInt3;
    this.trace = paramOutputStream;
  }
  
  public PooledConnection createPooledConnection(PoolCallback paramPoolCallback) throws NamingException { return new LdapClient(this.host, this.port, this.socketFactory, this.connTimeout, this.readTimeout, this.trace, paramPoolCallback); }
  
  public String toString() { return this.host + ":" + this.port; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapClientFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */