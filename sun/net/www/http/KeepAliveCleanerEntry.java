package sun.net.www.http;

class KeepAliveCleanerEntry {
  KeepAliveStream kas;
  
  HttpClient hc;
  
  public KeepAliveCleanerEntry(KeepAliveStream paramKeepAliveStream, HttpClient paramHttpClient) {
    this.kas = paramKeepAliveStream;
    this.hc = paramHttpClient;
  }
  
  protected KeepAliveStream getKeepAliveStream() { return this.kas; }
  
  protected HttpClient getHttpClient() { return this.hc; }
  
  protected void setQueuedForCleanup() { this.kas.queuedForCleanup = true; }
  
  protected boolean getQueuedForCleanup() { return this.kas.queuedForCleanup; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\http\KeepAliveCleanerEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */