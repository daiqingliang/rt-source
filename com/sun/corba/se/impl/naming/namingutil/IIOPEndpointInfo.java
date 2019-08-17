package com.sun.corba.se.impl.naming.namingutil;

public class IIOPEndpointInfo {
  private int major = 1;
  
  private int minor = 0;
  
  private String host = "localhost";
  
  private int port = 2089;
  
  public void setHost(String paramString) { this.host = paramString; }
  
  public String getHost() { return this.host; }
  
  public void setPort(int paramInt) { this.port = paramInt; }
  
  public int getPort() { return this.port; }
  
  public void setVersion(int paramInt1, int paramInt2) {
    this.major = paramInt1;
    this.minor = paramInt2;
  }
  
  public int getMajor() { return this.major; }
  
  public int getMinor() { return this.minor; }
  
  public void dump() {
    System.out.println(" Major -> " + this.major + " Minor -> " + this.minor);
    System.out.println("host -> " + this.host);
    System.out.println("port -> " + this.port);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\namingutil\IIOPEndpointInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */