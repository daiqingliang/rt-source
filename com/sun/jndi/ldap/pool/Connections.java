package com.sun.jndi.ldap.pool;

import com.sun.jndi.ldap.LdapPoolManager;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import javax.naming.CommunicationException;
import javax.naming.InterruptedNamingException;
import javax.naming.NamingException;

final class Connections implements PoolCallback {
  private static final boolean debug = Pool.debug;
  
  private static final boolean trace = LdapPoolManager.trace;
  
  private static final int DEFAULT_SIZE = 10;
  
  private final int maxSize;
  
  private final int prefSize;
  
  private final List<ConnectionDesc> conns;
  
  private boolean closed = false;
  
  private Reference<Object> ref;
  
  Connections(Object paramObject, int paramInt1, int paramInt2, int paramInt3, PooledConnectionFactory paramPooledConnectionFactory) throws NamingException {
    this.maxSize = paramInt3;
    if (paramInt3 > 0) {
      this.prefSize = Math.min(paramInt2, paramInt3);
      paramInt1 = Math.min(paramInt1, paramInt3);
    } else {
      this.prefSize = paramInt2;
    } 
    this.conns = new ArrayList((paramInt3 > 0) ? paramInt3 : 10);
    this.ref = new SoftReference(paramObject);
    d("init size=", paramInt1);
    d("max size=", paramInt3);
    d("preferred size=", paramInt2);
    for (byte b = 0; b < paramInt1; b++) {
      PooledConnection pooledConnection = paramPooledConnectionFactory.createPooledConnection(this);
      td("Create ", pooledConnection, paramPooledConnectionFactory);
      this.conns.add(new ConnectionDesc(pooledConnection));
    } 
  }
  
  PooledConnection get(long paramLong, PooledConnectionFactory paramPooledConnectionFactory) throws NamingException {
    long l1 = (paramLong > 0L) ? System.currentTimeMillis() : 0L;
    long l2 = paramLong;
    d("get(): before");
    PooledConnection pooledConnection;
    while ((pooledConnection = getOrCreateConnection(paramPooledConnectionFactory)) == null) {
      if (paramLong > 0L && l2 <= 0L)
        throw new CommunicationException("Timeout exceeded while waiting for a connection: " + paramLong + "ms"); 
      try {
        d("get(): waiting");
        if (l2 > 0L) {
          wait(l2);
        } else {
          wait();
        } 
      } catch (InterruptedException interruptedException) {
        throw new InterruptedNamingException("Interrupted while waiting for a connection");
      } 
      if (paramLong > 0L) {
        long l = System.currentTimeMillis();
        l2 = paramLong - l - l1;
      } 
    } 
    d("get(): after");
    return pooledConnection;
  }
  
  private PooledConnection getOrCreateConnection(PooledConnectionFactory paramPooledConnectionFactory) throws NamingException {
    int i = this.conns.size();
    PooledConnection pooledConnection = null;
    if (this.prefSize <= 0 || i >= this.prefSize)
      for (byte b = 0; b < i; b++) {
        ConnectionDesc connectionDesc = (ConnectionDesc)this.conns.get(b);
        if ((pooledConnection = connectionDesc.tryUse()) != null) {
          d("get(): use ", pooledConnection);
          td("Use ", pooledConnection);
          return pooledConnection;
        } 
      }  
    if (this.maxSize > 0 && i >= this.maxSize)
      return null; 
    pooledConnection = paramPooledConnectionFactory.createPooledConnection(this);
    td("Create and use ", pooledConnection, paramPooledConnectionFactory);
    this.conns.add(new ConnectionDesc(pooledConnection, true));
    return pooledConnection;
  }
  
  public boolean releasePooledConnection(PooledConnection paramPooledConnection) {
    ConnectionDesc connectionDesc;
    int i = this.conns.indexOf(connectionDesc = new ConnectionDesc(paramPooledConnection));
    d("release(): ", paramPooledConnection);
    if (i >= 0) {
      if (this.closed || (this.prefSize > 0 && this.conns.size() > this.prefSize)) {
        d("release(): closing ", paramPooledConnection);
        td("Close ", paramPooledConnection);
        this.conns.remove(connectionDesc);
        paramPooledConnection.closeConnection();
      } else {
        d("release(): release ", paramPooledConnection);
        td("Release ", paramPooledConnection);
        connectionDesc = (ConnectionDesc)this.conns.get(i);
        connectionDesc.release();
      } 
      notifyAll();
      d("release(): notify");
      return true;
    } 
    return false;
  }
  
  public boolean removePooledConnection(PooledConnection paramPooledConnection) {
    if (this.conns.remove(new ConnectionDesc(paramPooledConnection))) {
      d("remove(): ", paramPooledConnection);
      notifyAll();
      d("remove(): notify");
      td("Remove ", paramPooledConnection);
      if (this.conns.isEmpty())
        this.ref = null; 
      return true;
    } 
    d("remove(): not found ", paramPooledConnection);
    return false;
  }
  
  boolean expire(long paramLong) {
    ArrayList arrayList1;
    synchronized (this) {
      arrayList1 = new ArrayList(this.conns);
    } 
    ArrayList arrayList2 = new ArrayList();
    for (ConnectionDesc connectionDesc : arrayList1) {
      d("expire(): ", connectionDesc);
      if (connectionDesc.expire(paramLong)) {
        arrayList2.add(connectionDesc);
        td("expire(): Expired ", connectionDesc);
      } 
    } 
    synchronized (this) {
      this.conns.removeAll(arrayList2);
      return this.conns.isEmpty();
    } 
  }
  
  void close() {
    expire(System.currentTimeMillis());
    this.closed = true;
  }
  
  String getStats() {
    int i;
    byte b1 = 0;
    byte b2 = 0;
    byte b3 = 0;
    long l = 0L;
    synchronized (this) {
      i = this.conns.size();
      for (byte b = 0; b < i; b++) {
        ConnectionDesc connectionDesc = (ConnectionDesc)this.conns.get(b);
        l += connectionDesc.getUseCount();
        switch (connectionDesc.getState()) {
          case 0:
            b2++;
            break;
          case 1:
            b1++;
            break;
          case 2:
            b3++;
            break;
        } 
      } 
    } 
    return "size=" + i + "; use=" + l + "; busy=" + b2 + "; idle=" + b1 + "; expired=" + b3;
  }
  
  private void d(String paramString, Object paramObject) {
    if (debug)
      d(paramString + paramObject); 
  }
  
  private void d(String paramString, int paramInt) {
    if (debug)
      d(paramString + paramInt); 
  }
  
  private void d(String paramString) {
    if (debug)
      System.err.println(this + "." + paramString + "; size: " + this.conns.size()); 
  }
  
  private void td(String paramString, Object paramObject1, Object paramObject2) {
    if (trace)
      td(paramString + paramObject1 + "[" + paramObject2 + "]"); 
  }
  
  private void td(String paramString, Object paramObject) {
    if (trace)
      td(paramString + paramObject); 
  }
  
  private void td(String paramString) {
    if (trace)
      System.err.println(paramString); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\pool\Connections.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */