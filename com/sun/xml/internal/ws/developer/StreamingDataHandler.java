package com.sun.xml.internal.ws.developer;

import com.sun.xml.internal.org.jvnet.staxex.StreamingDataHandler;
import java.net.URL;
import javax.activation.DataSource;

public abstract class StreamingDataHandler extends StreamingDataHandler {
  private String hrefCid;
  
  public StreamingDataHandler(Object paramObject, String paramString) { super(paramObject, paramString); }
  
  public StreamingDataHandler(URL paramURL) { super(paramURL); }
  
  public StreamingDataHandler(DataSource paramDataSource) { super(paramDataSource); }
  
  public String getHrefCid() { return this.hrefCid; }
  
  public void setHrefCid(String paramString) { this.hrefCid = paramString; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\developer\StreamingDataHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */