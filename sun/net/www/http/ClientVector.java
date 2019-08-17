package sun.net.www.http;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Stack;

class ClientVector extends Stack<KeepAliveEntry> {
  private static final long serialVersionUID = -8680532108106489459L;
  
  int nap;
  
  ClientVector(int paramInt) { this.nap = paramInt; }
  
  HttpClient get() {
    if (empty())
      return null; 
    HttpClient httpClient = null;
    long l = System.currentTimeMillis();
    do {
      KeepAliveEntry keepAliveEntry = (KeepAliveEntry)pop();
      if (l - keepAliveEntry.idleStartTime > this.nap) {
        keepAliveEntry.hc.closeServer();
      } else {
        httpClient = keepAliveEntry.hc;
      } 
    } while (httpClient == null && !empty());
    return httpClient;
  }
  
  void put(HttpClient paramHttpClient) {
    if (size() >= KeepAliveCache.getMaxConnections()) {
      paramHttpClient.closeServer();
    } else {
      push(new KeepAliveEntry(paramHttpClient, System.currentTimeMillis()));
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException { throw new NotSerializableException(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException { throw new NotSerializableException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\http\ClientVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */