package com.sun.corba.se.impl.legacy.connection;

import com.sun.corba.se.spi.legacy.connection.LegacyServerSocketEndPointInfo;
import com.sun.corba.se.spi.transport.SocketInfo;

public class EndPointInfoImpl implements SocketInfo, LegacyServerSocketEndPointInfo {
  protected String type;
  
  protected String hostname;
  
  protected int port;
  
  protected int locatorPort;
  
  protected String name;
  
  public EndPointInfoImpl(String paramString1, int paramInt, String paramString2) {
    this.type = paramString1;
    this.port = paramInt;
    this.hostname = paramString2;
    this.locatorPort = -1;
    this.name = "NO_NAME";
  }
  
  public String getType() { return this.type; }
  
  public String getHost() { return this.hostname; }
  
  public String getHostName() { return this.hostname; }
  
  public int getPort() { return this.port; }
  
  public int getLocatorPort() { return this.locatorPort; }
  
  public void setLocatorPort(int paramInt) { this.locatorPort = paramInt; }
  
  public String getName() { return this.name; }
  
  public int hashCode() { return this.type.hashCode() ^ this.hostname.hashCode() ^ this.port; }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof EndPointInfoImpl))
      return false; 
    EndPointInfoImpl endPointInfoImpl = (EndPointInfoImpl)paramObject;
    if (this.type == null) {
      if (endPointInfoImpl.type != null)
        return false; 
    } else if (!this.type.equals(endPointInfoImpl.type)) {
      return false;
    } 
    return (this.port != endPointInfoImpl.port) ? false : (!!this.hostname.equals(endPointInfoImpl.hostname));
  }
  
  public String toString() { return this.type + " " + this.name + " " + this.hostname + " " + this.port; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\legacy\connection\EndPointInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */