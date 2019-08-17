package com.sun.jmx.remote.internal;

import com.sun.jmx.remote.util.ClassLogger;

public abstract class ServerCommunicatorAdmin {
  private long timestamp;
  
  private final int[] lock = new int[0];
  
  private int currentJobs = 0;
  
  private long timeout;
  
  private boolean terminated = false;
  
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "ServerCommunicatorAdmin");
  
  private static final ClassLogger timelogger = new ClassLogger("javax.management.remote.timeout", "ServerCommunicatorAdmin");
  
  public ServerCommunicatorAdmin(long paramLong) {
    if (logger.traceOn())
      logger.trace("Constructor", "Creates a new ServerCommunicatorAdmin object with the timeout " + paramLong); 
    this.timeout = paramLong;
    this.timestamp = 0L;
    if (paramLong < Float.MAX_VALUE) {
      Timeout timeout1 = new Timeout(null);
      Thread thread = new Thread(timeout1);
      thread.setName("JMX server connection timeout " + thread.getId());
      thread.setDaemon(true);
      thread.start();
    } 
  }
  
  public boolean reqIncoming() {
    if (logger.traceOn())
      logger.trace("reqIncoming", "Receive a new request."); 
    synchronized (this.lock) {
      if (this.terminated)
        logger.warning("reqIncoming", "The server has decided to close this client connection."); 
      this.currentJobs++;
      return this.terminated;
    } 
  }
  
  public boolean rspOutgoing() {
    if (logger.traceOn())
      logger.trace("reqIncoming", "Finish a request."); 
    synchronized (this.lock) {
      if (--this.currentJobs == 0) {
        this.timestamp = System.currentTimeMillis();
        logtime("Admin: Timestamp=", this.timestamp);
        this.lock.notify();
      } 
      return this.terminated;
    } 
  }
  
  protected abstract void doStop();
  
  public void terminate() {
    if (logger.traceOn())
      logger.trace("terminate", "terminate the ServerCommunicatorAdmin object."); 
    synchronized (this.lock) {
      if (this.terminated)
        return; 
      this.terminated = true;
      this.lock.notify();
    } 
  }
  
  private void logtime(String paramString, long paramLong) { timelogger.trace("synchro", paramString + paramLong); }
  
  private class Timeout implements Runnable {
    private Timeout() {}
    
    public void run() {
      boolean bool = false;
      synchronized (ServerCommunicatorAdmin.this.lock) {
        if (ServerCommunicatorAdmin.this.timestamp == 0L)
          ServerCommunicatorAdmin.this.timestamp = System.currentTimeMillis(); 
        ServerCommunicatorAdmin.this.logtime("Admin: timeout=", ServerCommunicatorAdmin.this.timeout);
        ServerCommunicatorAdmin.this.logtime("Admin: Timestamp=", ServerCommunicatorAdmin.this.timestamp);
        while (!ServerCommunicatorAdmin.this.terminated) {
          try {
            while (!ServerCommunicatorAdmin.this.terminated && ServerCommunicatorAdmin.this.currentJobs != 0) {
              if (logger.traceOn())
                logger.trace("Timeout-run", "Waiting without timeout."); 
              ServerCommunicatorAdmin.this.lock.wait();
            } 
            if (ServerCommunicatorAdmin.this.terminated)
              return; 
            long l1 = ServerCommunicatorAdmin.this.timeout - System.currentTimeMillis() - ServerCommunicatorAdmin.this.timestamp;
            ServerCommunicatorAdmin.this.logtime("Admin: remaining timeout=", l1);
            if (l1 > 0L) {
              if (logger.traceOn())
                logger.trace("Timeout-run", "Waiting with timeout: " + l1 + " ms remaining"); 
              ServerCommunicatorAdmin.this.lock.wait(l1);
            } 
            if (ServerCommunicatorAdmin.this.currentJobs > 0)
              continue; 
            long l2 = System.currentTimeMillis() - ServerCommunicatorAdmin.this.timestamp;
            ServerCommunicatorAdmin.this.logtime("Admin: elapsed=", l2);
            if (!ServerCommunicatorAdmin.this.terminated && l2 > ServerCommunicatorAdmin.this.timeout) {
              if (logger.traceOn())
                logger.trace("Timeout-run", "timeout elapsed"); 
              ServerCommunicatorAdmin.this.logtime("Admin: timeout elapsed! " + l2 + ">", ServerCommunicatorAdmin.this.timeout);
              ServerCommunicatorAdmin.this.terminated = true;
              bool = true;
              break;
            } 
          } catch (InterruptedException interruptedException) {
            logger.warning("Timeout-run", "Unexpected Exception: " + interruptedException);
            logger.debug("Timeout-run", interruptedException);
            return;
          } 
        } 
      } 
      if (bool) {
        if (logger.traceOn())
          logger.trace("Timeout-run", "Call the doStop."); 
        ServerCommunicatorAdmin.this.doStop();
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\internal\ServerCommunicatorAdmin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */