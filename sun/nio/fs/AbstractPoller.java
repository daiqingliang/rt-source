package sun.nio.fs;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

abstract class AbstractPoller implements Runnable {
  private final LinkedList<Request> requestList = new LinkedList();
  
  private boolean shutdown = false;
  
  public void start() {
    final AbstractPoller thisRunnable = this;
    AccessController.doPrivileged(new PrivilegedAction<Object>() {
          public Object run() {
            Thread thread = new Thread(thisRunnable);
            thread.setDaemon(true);
            thread.start();
            return null;
          }
        });
  }
  
  abstract void wakeup();
  
  abstract Object implRegister(Path paramPath, Set<? extends WatchEvent.Kind<?>> paramSet, WatchEvent.Modifier... paramVarArgs);
  
  abstract void implCancelKey(WatchKey paramWatchKey);
  
  abstract void implCloseAll();
  
  final WatchKey register(Path paramPath, WatchEvent.Kind<?>[] paramArrayOfKind, WatchEvent.Modifier... paramVarArgs) throws IOException {
    if (paramPath == null)
      throw new NullPointerException(); 
    HashSet hashSet = new HashSet(paramArrayOfKind.length);
    for (WatchEvent.Kind<?> kind : paramArrayOfKind) {
      if (kind == StandardWatchEventKinds.ENTRY_CREATE || kind == StandardWatchEventKinds.ENTRY_MODIFY || kind == StandardWatchEventKinds.ENTRY_DELETE) {
        hashSet.add(kind);
      } else if (kind != StandardWatchEventKinds.OVERFLOW) {
        if (kind == null)
          throw new NullPointerException("An element in event set is 'null'"); 
        throw new UnsupportedOperationException(kind.name());
      } 
    } 
    if (hashSet.isEmpty())
      throw new IllegalArgumentException("No events to register"); 
    return (WatchKey)invoke(RequestType.REGISTER, new Object[] { paramPath, hashSet, paramVarArgs });
  }
  
  final void cancel(WatchKey paramWatchKey) {
    try {
      invoke(RequestType.CANCEL, new Object[] { paramWatchKey });
    } catch (IOException iOException) {
      throw new AssertionError(iOException.getMessage());
    } 
  }
  
  final void close() { invoke(RequestType.CLOSE, new Object[0]); }
  
  private Object invoke(RequestType paramRequestType, Object... paramVarArgs) throws IOException {
    Request request = new Request(paramRequestType, paramVarArgs);
    synchronized (this.requestList) {
      if (this.shutdown)
        throw new ClosedWatchServiceException(); 
      this.requestList.add(request);
    } 
    wakeup();
    Object object = request.awaitResult();
    if (object instanceof RuntimeException)
      throw (RuntimeException)object; 
    if (object instanceof IOException)
      throw (IOException)object; 
    return object;
  }
  
  boolean processRequests() {
    synchronized (this.requestList) {
      Request request;
      while ((request = (Request)this.requestList.poll()) != null) {
        Modifier[] arrayOfModifier;
        Set set;
        Path path;
        WatchKey watchKey;
        Object[] arrayOfObject;
        if (this.shutdown)
          request.release(new ClosedWatchServiceException()); 
        switch (request.type()) {
          case REGISTER:
            arrayOfObject = request.parameters();
            path = (Path)arrayOfObject[0];
            set = (Set)arrayOfObject[1];
            arrayOfModifier = (Modifier[])arrayOfObject[2];
            request.release(implRegister(path, set, arrayOfModifier));
            continue;
          case CANCEL:
            arrayOfObject = request.parameters();
            watchKey = (WatchKey)arrayOfObject[0];
            implCancelKey(watchKey);
            request.release(null);
            continue;
          case CLOSE:
            implCloseAll();
            request.release(null);
            this.shutdown = true;
            continue;
        } 
        request.release(new IOException("request not recognized"));
      } 
    } 
    return this.shutdown;
  }
  
  private static class Request {
    private final AbstractPoller.RequestType type;
    
    private final Object[] params;
    
    private boolean completed = false;
    
    private Object result = null;
    
    Request(AbstractPoller.RequestType param1RequestType, Object... param1VarArgs) {
      this.type = param1RequestType;
      this.params = param1VarArgs;
    }
    
    AbstractPoller.RequestType type() { return this.type; }
    
    Object[] parameters() { return this.params; }
    
    void release(Object param1Object) {
      synchronized (this) {
        this.completed = true;
        this.result = param1Object;
        notifyAll();
      } 
    }
    
    Object awaitResult() {
      boolean bool = false;
      synchronized (this) {
        while (!this.completed) {
          try {
            wait();
          } catch (InterruptedException interruptedException) {
            bool = true;
          } 
        } 
        if (bool)
          Thread.currentThread().interrupt(); 
        return this.result;
      } 
    }
  }
  
  private enum RequestType {
    REGISTER, CANCEL, CLOSE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\AbstractPoller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */