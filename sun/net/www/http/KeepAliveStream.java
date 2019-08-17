package sun.net.www.http;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.ProgressSource;
import sun.net.www.MeteredStream;

public class KeepAliveStream extends MeteredStream implements Hurryable {
  HttpClient hc;
  
  boolean hurried;
  
  protected boolean queuedForCleanup = false;
  
  private static final KeepAliveStreamCleaner queue = new KeepAliveStreamCleaner();
  
  private static Thread cleanerThread;
  
  public KeepAliveStream(InputStream paramInputStream, ProgressSource paramProgressSource, long paramLong, HttpClient paramHttpClient) {
    super(paramInputStream, paramProgressSource, paramLong);
    this.hc = paramHttpClient;
  }
  
  public void close() throws IOException {
    if (this.closed)
      return; 
    if (this.queuedForCleanup)
      return; 
    try {
      if (this.expected > this.count) {
        long l = this.expected - this.count;
        if (l <= available()) {
          do {
          
          } while ((l = this.expected - this.count) > 0L && skip(Math.min(l, available())) > 0L);
        } else if (this.expected <= KeepAliveStreamCleaner.MAX_DATA_REMAINING && !this.hurried) {
          queueForCleanup(new KeepAliveCleanerEntry(this, this.hc));
        } else {
          this.hc.closeServer();
        } 
      } 
      if (!this.closed && !this.hurried && !this.queuedForCleanup)
        this.hc.finished(); 
    } finally {
      if (this.pi != null)
        this.pi.finishTracking(); 
      if (!this.queuedForCleanup) {
        this.in = null;
        this.hc = null;
        this.closed = true;
      } 
    } 
  }
  
  public boolean markSupported() { return false; }
  
  public void mark(int paramInt) {}
  
  public void reset() throws IOException { throw new IOException("mark/reset not supported"); }
  
  public boolean hurry() {
    try {
      if (this.closed || this.count >= this.expected)
        return false; 
      if (this.in.available() < this.expected - this.count)
        return false; 
      int i = (int)(this.expected - this.count);
      byte[] arrayOfByte = new byte[i];
      DataInputStream dataInputStream = new DataInputStream(this.in);
      dataInputStream.readFully(arrayOfByte);
      this.in = new ByteArrayInputStream(arrayOfByte);
      this.hurried = true;
      return true;
    } catch (IOException iOException) {
      return false;
    } 
  }
  
  private static void queueForCleanup(KeepAliveCleanerEntry paramKeepAliveCleanerEntry) {
    synchronized (queue) {
      if (!paramKeepAliveCleanerEntry.getQueuedForCleanup()) {
        if (!queue.offer(paramKeepAliveCleanerEntry)) {
          paramKeepAliveCleanerEntry.getHttpClient().closeServer();
          return;
        } 
        paramKeepAliveCleanerEntry.setQueuedForCleanup();
        queue.notifyAll();
      } 
      boolean bool = (cleanerThread == null) ? 1 : 0;
      if (!bool && !cleanerThread.isAlive())
        bool = true; 
      if (bool)
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                ThreadGroup threadGroup1 = Thread.currentThread().getThreadGroup();
                ThreadGroup threadGroup2 = null;
                while ((threadGroup2 = threadGroup1.getParent()) != null)
                  threadGroup1 = threadGroup2; 
                cleanerThread = new Thread(threadGroup1, queue, "Keep-Alive-SocketCleaner");
                cleanerThread.setDaemon(true);
                cleanerThread.setPriority(8);
                cleanerThread.setContextClassLoader(null);
                cleanerThread.start();
                return null;
              }
            }); 
    } 
  }
  
  protected long remainingToRead() { return this.expected - this.count; }
  
  protected void setClosed() throws IOException {
    this.in = null;
    this.hc = null;
    this.closed = true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\http\KeepAliveStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */