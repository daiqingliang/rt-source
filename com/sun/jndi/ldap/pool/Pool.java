package com.sun.jndi.ldap.pool;

import com.sun.jndi.ldap.LdapPoolManager;
import java.io.PrintStream;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;
import javax.naming.NamingException;

public final class Pool {
  static final boolean debug = LdapPoolManager.debug;
  
  private static final ReferenceQueue<ConnectionsRef> queue = new ReferenceQueue();
  
  private static final Collection<Reference<ConnectionsRef>> weakRefs = Collections.synchronizedList(new LinkedList());
  
  private final int maxSize;
  
  private final int prefSize;
  
  private final int initSize;
  
  private final Map<Object, ConnectionsRef> map = new WeakHashMap();
  
  public Pool(int paramInt1, int paramInt2, int paramInt3) {
    this.prefSize = paramInt2;
    this.maxSize = paramInt3;
    this.initSize = paramInt1;
  }
  
  public PooledConnection getPooledConnection(Object paramObject, long paramLong, PooledConnectionFactory paramPooledConnectionFactory) throws NamingException {
    Connections connections;
    d("get(): ", paramObject);
    if (debug)
      synchronized (this.map) {
        d("size: ", this.map.size());
      }  
    expungeStaleConnections();
    synchronized (this.map) {
      connections = getConnections(paramObject);
      if (connections == null) {
        d("get(): creating new connections list for ", paramObject);
        connections = new Connections(paramObject, this.initSize, this.prefSize, this.maxSize, paramPooledConnectionFactory);
        ConnectionsRef connectionsRef = new ConnectionsRef(connections);
        this.map.put(paramObject, connectionsRef);
        ConnectionsWeakRef connectionsWeakRef = new ConnectionsWeakRef(connectionsRef, queue);
        weakRefs.add(connectionsWeakRef);
      } 
      d("get(): size after: ", this.map.size());
    } 
    return connections.get(paramLong, paramPooledConnectionFactory);
  }
  
  private Connections getConnections(Object paramObject) {
    ConnectionsRef connectionsRef = (ConnectionsRef)this.map.get(paramObject);
    return (connectionsRef != null) ? connectionsRef.getConnections() : null;
  }
  
  public void expire(long paramLong) {
    ArrayList arrayList1;
    synchronized (this.map) {
      arrayList1 = new ArrayList(this.map.values());
    } 
    ArrayList arrayList2 = new ArrayList();
    for (ConnectionsRef connectionsRef : arrayList1) {
      Connections connections = connectionsRef.getConnections();
      if (connections.expire(paramLong)) {
        d("expire(): removing ", connections);
        arrayList2.add(connectionsRef);
      } 
    } 
    synchronized (this.map) {
      this.map.values().removeAll(arrayList2);
    } 
    expungeStaleConnections();
  }
  
  private static void expungeStaleConnections() {
    ConnectionsWeakRef connectionsWeakRef = null;
    while ((connectionsWeakRef = (ConnectionsWeakRef)queue.poll()) != null) {
      Connections connections = connectionsWeakRef.getConnections();
      if (debug)
        System.err.println("weak reference cleanup: Closing Connections:" + connections); 
      connections.close();
      weakRefs.remove(connectionsWeakRef);
      connectionsWeakRef.clear();
    } 
  }
  
  public void showStats(PrintStream paramPrintStream) {
    paramPrintStream.println("===== Pool start ======================");
    paramPrintStream.println("maximum pool size: " + this.maxSize);
    paramPrintStream.println("preferred pool size: " + this.prefSize);
    paramPrintStream.println("initial pool size: " + this.initSize);
    synchronized (this.map) {
      paramPrintStream.println("current pool size: " + this.map.size());
      for (Map.Entry entry : this.map.entrySet()) {
        Object object = entry.getKey();
        Connections connections = ((ConnectionsRef)entry.getValue()).getConnections();
        paramPrintStream.println("   " + object + ":" + connections.getStats());
      } 
    } 
    paramPrintStream.println("====== Pool end =====================");
  }
  
  public String toString() {
    synchronized (this.map) {
      return super.toString() + " " + this.map.toString();
    } 
  }
  
  private void d(String paramString, int paramInt) {
    if (debug)
      System.err.println(this + "." + paramString + paramInt); 
  }
  
  private void d(String paramString, Object paramObject) {
    if (debug)
      System.err.println(this + "." + paramString + paramObject); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\pool\Pool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */