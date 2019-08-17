package com.sun.corba.se.spi.activation.RepositoryPackage;

import org.omg.CORBA.portable.IDLEntity;

public final class ServerDef implements IDLEntity {
  public String applicationName = null;
  
  public String serverName = null;
  
  public String serverClassPath = null;
  
  public String serverArgs = null;
  
  public String serverVmArgs = null;
  
  public ServerDef() {}
  
  public ServerDef(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    this.applicationName = paramString1;
    this.serverName = paramString2;
    this.serverClassPath = paramString3;
    this.serverArgs = paramString4;
    this.serverVmArgs = paramString5;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\RepositoryPackage\ServerDef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */