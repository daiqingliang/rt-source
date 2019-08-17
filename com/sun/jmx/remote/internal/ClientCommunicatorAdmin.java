package com.sun.jmx.remote.internal;

import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.io.InterruptedIOException;

public abstract class ClientCommunicatorAdmin {
  private final Checker checker;
  
  private long period;
  
  private static final int CONNECTED = 0;
  
  private static final int RE_CONNECTING = 1;
  
  private static final int FAILED = 2;
  
  private static final int TERMINATED = 3;
  
  private int state = 0;
  
  private final int[] lock = new int[0];
  
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "ClientCommunicatorAdmin");
  
  public ClientCommunicatorAdmin(long paramLong) {
    this.period = paramLong;
    this.checker = new Checker(null);
    Thread thread = new Thread(this.checker, "JMX client heartbeat " + ++threadNo);
    thread.setDaemon(true);
    thread.start();
    this.checker = null;
  }
  
  public void gotIOException(IOException paramIOException) throws IOException { restart(paramIOException); }
  
  protected abstract void checkConnection() throws IOException;
  
  protected abstract void doStart() throws IOException;
  
  protected abstract void doStop() throws IOException;
  
  public void terminate() throws IOException {
    synchronized (this.lock) {
      if (this.state == 3)
        return; 
      this.state = 3;
      this.lock.notifyAll();
      if (this.checker != null)
        this.checker.stop(); 
    } 
  }
  
  private void restart(IOException paramIOException) throws IOException {
    synchronized (this.lock) {
      if (this.state == 3)
        throw new IOException("The client has been closed."); 
      if (this.state == 2)
        throw paramIOException; 
      if (this.state == 1) {
        while (this.state == 1) {
          try {
            this.lock.wait();
          } catch (InterruptedException interruptedException) {
            InterruptedIOException interruptedIOException = new InterruptedIOException(interruptedException.toString());
            EnvHelp.initCause(interruptedIOException, interruptedException);
            throw interruptedIOException;
          } 
        } 
        if (this.state == 3)
          throw new IOException("The client has been closed."); 
        if (this.state != 0)
          throw paramIOException; 
        return;
      } 
      this.state = 1;
      this.lock.notifyAll();
    } 
    try {
      doStart();
      synchronized (this.lock) {
        if (this.state == 3)
          throw new IOException("The client has been closed."); 
        this.state = 0;
        this.lock.notifyAll();
      } 
      return;
    } catch (Exception exception) {
      logger.warning("restart", "Failed to restart: " + exception);
      logger.debug("restart", exception);
      synchronized (this.lock) {
        if (this.state == 3)
          throw new IOException("The client has been closed."); 
        this.state = 2;
        this.lock.notifyAll();
      } 
      try {
        doStop();
      } catch (Exception exception1) {}
      terminate();
      throw paramIOException;
    } 
  }
  
  private class Checker implements Runnable {
    private Thread myThread;
    
    private Checker() {}
    
    public void run() throws IOException {
      this.myThread = Thread.currentThread();
      while (ClientCommunicatorAdmin.this.state != 3 && !this.myThread.isInterrupted()) {
        try {
          Thread.sleep(ClientCommunicatorAdmin.this.period);
        } catch (InterruptedException interruptedException) {}
        if (ClientCommunicatorAdmin.this.state == 3 || this.myThread.isInterrupted())
          break; 
        try {
          ClientCommunicatorAdmin.this.checkConnection();
        } catch (Exception exception) {
          synchronized (ClientCommunicatorAdmin.this.lock) {
            if (ClientCommunicatorAdmin.this.state == 3 || this.myThread.isInterrupted())
              break; 
          } 
          exception = (Exception)EnvHelp.getCause(exception);
          if (exception instanceof IOException && !(exception instanceof InterruptedIOException))
            try {
              ClientCommunicatorAdmin.this.gotIOException((IOException)exception);
              continue;
            } catch (Exception exception1) {
              logger.warning("Checker-run", "Failed to check connection: " + exception);
              logger.warning("Checker-run", "stopping");
              logger.debug("Checker-run", exception);
              break;
            }  
          logger.warning("Checker-run", "Failed to check the connection: " + exception);
          logger.debug("Checker-run", exception);
          break;
        } 
      } 
      if (logger.traceOn())
        logger.trace("Checker-run", "Finished."); 
    }
    
    private void stop() throws IOException {
      if (this.myThread != null && this.myThread != Thread.currentThread())
        this.myThread.interrupt(); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\internal\ClientCommunicatorAdmin.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */