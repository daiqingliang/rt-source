package com.sun.net.httpserver;

import java.net.InetSocketAddress;
import javax.net.ssl.SSLParameters;
import jdk.Exported;

@Exported
public abstract class HttpsParameters {
  private String[] cipherSuites;
  
  private String[] protocols;
  
  private boolean wantClientAuth;
  
  private boolean needClientAuth;
  
  public abstract HttpsConfigurator getHttpsConfigurator();
  
  public abstract InetSocketAddress getClientAddress();
  
  public abstract void setSSLParameters(SSLParameters paramSSLParameters);
  
  public String[] getCipherSuites() { return (this.cipherSuites != null) ? (String[])this.cipherSuites.clone() : null; }
  
  public void setCipherSuites(String[] paramArrayOfString) { this.cipherSuites = (paramArrayOfString != null) ? (String[])paramArrayOfString.clone() : null; }
  
  public String[] getProtocols() { return (this.protocols != null) ? (String[])this.protocols.clone() : null; }
  
  public void setProtocols(String[] paramArrayOfString) { this.protocols = (paramArrayOfString != null) ? (String[])paramArrayOfString.clone() : null; }
  
  public boolean getWantClientAuth() { return this.wantClientAuth; }
  
  public void setWantClientAuth(boolean paramBoolean) { this.wantClientAuth = paramBoolean; }
  
  public boolean getNeedClientAuth() { return this.needClientAuth; }
  
  public void setNeedClientAuth(boolean paramBoolean) { this.needClientAuth = paramBoolean; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\httpserver\HttpsParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */