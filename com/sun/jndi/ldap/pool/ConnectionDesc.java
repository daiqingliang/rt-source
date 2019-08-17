package com.sun.jndi.ldap.pool;

final class ConnectionDesc {
  private static final boolean debug = Pool.debug;
  
  static final byte BUSY = 0;
  
  static final byte IDLE = 1;
  
  static final byte EXPIRED = 2;
  
  private final PooledConnection conn;
  
  private byte state = 1;
  
  private long idleSince;
  
  private long useCount = 0L;
  
  ConnectionDesc(PooledConnection paramPooledConnection) { this.conn = paramPooledConnection; }
  
  ConnectionDesc(PooledConnection paramPooledConnection, boolean paramBoolean) {
    this.conn = paramPooledConnection;
    if (paramBoolean) {
      this.state = 0;
      this.useCount++;
    } 
  }
  
  public boolean equals(Object paramObject) { return (paramObject != null && paramObject instanceof ConnectionDesc && ((ConnectionDesc)paramObject).conn == this.conn); }
  
  public int hashCode() { return this.conn.hashCode(); }
  
  boolean release() {
    d("release()");
    if (this.state == 0) {
      this.state = 1;
      this.idleSince = System.currentTimeMillis();
      return true;
    } 
    return false;
  }
  
  PooledConnection tryUse() {
    d("tryUse()");
    if (this.state == 1) {
      this.state = 0;
      this.useCount++;
      return this.conn;
    } 
    return null;
  }
  
  boolean expire(long paramLong) {
    if (this.state == 1 && this.idleSince < paramLong) {
      d("expire(): expired");
      this.state = 2;
      this.conn.closeConnection();
      return true;
    } 
    d("expire(): not expired");
    return false;
  }
  
  public String toString() { return this.conn.toString() + " " + ((this.state == 0) ? "busy" : ((this.state == 1) ? "idle" : "expired")); }
  
  int getState() { return this.state; }
  
  long getUseCount() { return this.useCount; }
  
  private void d(String paramString) {
    if (debug)
      System.err.println("ConnectionDesc." + paramString + " " + toString()); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\pool\ConnectionDesc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */