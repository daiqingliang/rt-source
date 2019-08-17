package sun.nio.fs;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

abstract class AbstractWatchService implements WatchService {
  private final LinkedBlockingDeque<WatchKey> pendingKeys = new LinkedBlockingDeque();
  
  private final WatchKey CLOSE_KEY = new AbstractWatchKey(null, null) {
      public boolean isValid() { return true; }
      
      public void cancel() {}
    };
  
  private final Object closeLock = new Object();
  
  abstract WatchKey register(Path paramPath, WatchEvent.Kind<?>[] paramArrayOfKind, WatchEvent.Modifier... paramVarArgs) throws IOException;
  
  final void enqueueKey(WatchKey paramWatchKey) { this.pendingKeys.offer(paramWatchKey); }
  
  private void checkOpen() {
    if (this.closed)
      throw new ClosedWatchServiceException(); 
  }
  
  private void checkKey(WatchKey paramWatchKey) {
    if (paramWatchKey == this.CLOSE_KEY)
      enqueueKey(paramWatchKey); 
    checkOpen();
  }
  
  public final WatchKey poll() {
    checkOpen();
    WatchKey watchKey = (WatchKey)this.pendingKeys.poll();
    checkKey(watchKey);
    return watchKey;
  }
  
  public final WatchKey poll(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    checkOpen();
    WatchKey watchKey = (WatchKey)this.pendingKeys.poll(paramLong, paramTimeUnit);
    checkKey(watchKey);
    return watchKey;
  }
  
  public final WatchKey take() {
    checkOpen();
    WatchKey watchKey = (WatchKey)this.pendingKeys.take();
    checkKey(watchKey);
    return watchKey;
  }
  
  final boolean isOpen() { return !this.closed; }
  
  final Object closeLock() { return this.closeLock; }
  
  abstract void implClose();
  
  public final void close() {
    synchronized (this.closeLock) {
      if (this.closed)
        return; 
      this.closed = true;
      implClose();
      this.pendingKeys.clear();
      this.pendingKeys.offer(this.CLOSE_KEY);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\AbstractWatchService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */