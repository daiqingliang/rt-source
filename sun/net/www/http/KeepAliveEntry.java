package sun.net.www.http;

class KeepAliveEntry {
  HttpClient hc;
  
  long idleStartTime;
  
  KeepAliveEntry(HttpClient paramHttpClient, long paramLong) {
    this.hc = paramHttpClient;
    this.idleStartTime = paramLong;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\http\KeepAliveEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */