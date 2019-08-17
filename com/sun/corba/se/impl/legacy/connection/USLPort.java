package com.sun.corba.se.impl.legacy.connection;

public class USLPort {
  private String type;
  
  private int port;
  
  public USLPort(String paramString, int paramInt) {
    this.type = paramString;
    this.port = paramInt;
  }
  
  public String getType() { return this.type; }
  
  public int getPort() { return this.port; }
  
  public String toString() { return this.type + ":" + this.port; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\legacy\connection\USLPort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */