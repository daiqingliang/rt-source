package com.sun.jndi.ldap.pool;

public final class PoolCleaner extends Thread {
  private final Pool[] pools;
  
  private final long period;
  
  public PoolCleaner(long paramLong, Pool[] paramArrayOfPool) {
    this.period = paramLong;
    this.pools = (Pool[])paramArrayOfPool.clone();
    setDaemon(true);
  }
  
  public void run() {
    while (true) {
      synchronized (this) {
        try {
          wait(this.period);
        } catch (InterruptedException interruptedException) {}
        long l = System.currentTimeMillis() - this.period;
        for (byte b = 0; b < this.pools.length; b++) {
          if (this.pools[b] != null)
            this.pools[b].expire(l); 
        } 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\pool\PoolCleaner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */