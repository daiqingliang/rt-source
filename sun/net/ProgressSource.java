package sun.net;

import java.net.URL;

public class ProgressSource {
  private URL url;
  
  private String method;
  
  private String contentType;
  
  private long progress = 0L;
  
  private long lastProgress = 0L;
  
  private long expected = -1L;
  
  private State state;
  
  private boolean connected = false;
  
  private int threshold = 8192;
  
  private ProgressMonitor progressMonitor;
  
  public ProgressSource(URL paramURL, String paramString) { this(paramURL, paramString, -1L); }
  
  public ProgressSource(URL paramURL, String paramString, long paramLong) {
    this.url = paramURL;
    this.method = paramString;
    this.contentType = "content/unknown";
    this.progress = 0L;
    this.lastProgress = 0L;
    this.expected = paramLong;
    this.state = State.NEW;
    this.progressMonitor = ProgressMonitor.getDefault();
    this.threshold = this.progressMonitor.getProgressUpdateThreshold();
  }
  
  public boolean connected() {
    if (!this.connected) {
      this.connected = true;
      this.state = State.CONNECTED;
      return false;
    } 
    return true;
  }
  
  public void close() { this.state = State.DELETE; }
  
  public URL getURL() { return this.url; }
  
  public String getMethod() { return this.method; }
  
  public String getContentType() { return this.contentType; }
  
  public void setContentType(String paramString) { this.contentType = paramString; }
  
  public long getProgress() { return this.progress; }
  
  public long getExpected() { return this.expected; }
  
  public State getState() { return this.state; }
  
  public void beginTracking() { this.progressMonitor.registerSource(this); }
  
  public void finishTracking() { this.progressMonitor.unregisterSource(this); }
  
  public void updateProgress(long paramLong1, long paramLong2) {
    this.lastProgress = this.progress;
    this.progress = paramLong1;
    this.expected = paramLong2;
    if (!connected()) {
      this.state = State.CONNECTED;
    } else {
      this.state = State.UPDATE;
    } 
    if (this.lastProgress / this.threshold != this.progress / this.threshold)
      this.progressMonitor.updateProgress(this); 
    if (this.expected != -1L && this.progress >= this.expected && this.progress != 0L)
      close(); 
  }
  
  public Object clone() throws CloneNotSupportedException { return super.clone(); }
  
  public String toString() { return getClass().getName() + "[url=" + this.url + ", method=" + this.method + ", state=" + this.state + ", content-type=" + this.contentType + ", progress=" + this.progress + ", expected=" + this.expected + "]"; }
  
  public enum State {
    NEW, CONNECTED, UPDATE, DELETE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\ProgressSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */