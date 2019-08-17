package sun.net;

import java.net.URL;
import java.util.EventObject;

public class ProgressEvent extends EventObject {
  private URL url;
  
  private String contentType;
  
  private String method;
  
  private long progress;
  
  private long expected;
  
  private ProgressSource.State state;
  
  public ProgressEvent(ProgressSource paramProgressSource, URL paramURL, String paramString1, String paramString2, ProgressSource.State paramState, long paramLong1, long paramLong2) {
    super(paramProgressSource);
    this.url = paramURL;
    this.method = paramString1;
    this.contentType = paramString2;
    this.progress = paramLong1;
    this.expected = paramLong2;
    this.state = paramState;
  }
  
  public URL getURL() { return this.url; }
  
  public String getMethod() { return this.method; }
  
  public String getContentType() { return this.contentType; }
  
  public long getProgress() { return this.progress; }
  
  public long getExpected() { return this.expected; }
  
  public ProgressSource.State getState() { return this.state; }
  
  public String toString() { return getClass().getName() + "[url=" + this.url + ", method=" + this.method + ", state=" + this.state + ", content-type=" + this.contentType + ", progress=" + this.progress + ", expected=" + this.expected + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\ProgressEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */