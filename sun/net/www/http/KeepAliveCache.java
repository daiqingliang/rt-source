package sun.net.www.http;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import sun.security.action.GetIntegerAction;

public class KeepAliveCache extends HashMap<KeepAliveKey, ClientVector> implements Runnable {
  private static final long serialVersionUID = -2937172892064557949L;
  
  static final int MAX_CONNECTIONS = 5;
  
  static int result = -1;
  
  static final int LIFETIME = 5000;
  
  private Thread keepAliveTimer = null;
  
  static int getMaxConnections() {
    if (result == -1) {
      result = ((Integer)AccessController.doPrivileged(new GetIntegerAction("http.maxConnections", 5))).intValue();
      if (result <= 0)
        result = 5; 
    } 
    return result;
  }
  
  public void put(URL paramURL, Object paramObject, HttpClient paramHttpClient) {
    boolean bool = (this.keepAliveTimer == null) ? 1 : 0;
    if (!bool && !this.keepAliveTimer.isAlive())
      bool = true; 
    if (bool) {
      clear();
      final KeepAliveCache cache = this;
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              ThreadGroup threadGroup1 = Thread.currentThread().getThreadGroup();
              ThreadGroup threadGroup2 = null;
              while ((threadGroup2 = threadGroup1.getParent()) != null)
                threadGroup1 = threadGroup2; 
              KeepAliveCache.this.keepAliveTimer = new Thread(threadGroup1, cache, "Keep-Alive-Timer");
              KeepAliveCache.this.keepAliveTimer.setDaemon(true);
              KeepAliveCache.this.keepAliveTimer.setPriority(8);
              KeepAliveCache.this.keepAliveTimer.setContextClassLoader(null);
              KeepAliveCache.this.keepAliveTimer.start();
              return null;
            }
          });
    } 
    KeepAliveKey keepAliveKey = new KeepAliveKey(paramURL, paramObject);
    ClientVector clientVector = (ClientVector)get(keepAliveKey);
    if (clientVector == null) {
      int i = paramHttpClient.getKeepAliveTimeout();
      clientVector = new ClientVector((i > 0) ? (i * 1000) : 5000);
      clientVector.put(paramHttpClient);
      put(keepAliveKey, clientVector);
    } else {
      clientVector.put(paramHttpClient);
    } 
  }
  
  public void remove(HttpClient paramHttpClient, Object paramObject) {
    KeepAliveKey keepAliveKey = new KeepAliveKey(paramHttpClient.url, paramObject);
    ClientVector clientVector = (ClientVector)get(keepAliveKey);
    if (clientVector != null) {
      clientVector.remove(paramHttpClient);
      if (clientVector.empty())
        removeVector(keepAliveKey); 
    } 
  }
  
  void removeVector(KeepAliveKey paramKeepAliveKey) { remove(paramKeepAliveKey); }
  
  public HttpClient get(URL paramURL, Object paramObject) {
    KeepAliveKey keepAliveKey = new KeepAliveKey(paramURL, paramObject);
    ClientVector clientVector = (ClientVector)get(keepAliveKey);
    return (clientVector == null) ? null : clientVector.get();
  }
  
  public void run() {
    do {
      try {
        Thread.sleep(5000L);
      } catch (InterruptedException interruptedException) {}
      synchronized (this) {
        long l = System.currentTimeMillis();
        ArrayList arrayList = new ArrayList();
        for (KeepAliveKey keepAliveKey : keySet()) {
          ClientVector clientVector = (ClientVector)get(keepAliveKey);
          synchronized (clientVector) {
            byte b = 0;
            while (b < clientVector.size()) {
              KeepAliveEntry keepAliveEntry = (KeepAliveEntry)clientVector.elementAt(b);
              if (l - keepAliveEntry.idleStartTime > clientVector.nap) {
                HttpClient httpClient = keepAliveEntry.hc;
                httpClient.closeServer();
                b++;
              } 
            } 
            clientVector.subList(0, b).clear();
            if (clientVector.size() == 0)
              arrayList.add(keepAliveKey); 
          } 
        } 
        for (KeepAliveKey keepAliveKey : arrayList)
          removeVector(keepAliveKey); 
      } 
    } while (size() > 0);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException { throw new NotSerializableException(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException { throw new NotSerializableException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\http\KeepAliveCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */