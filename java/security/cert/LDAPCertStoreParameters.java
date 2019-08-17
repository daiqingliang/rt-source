package java.security.cert;

public class LDAPCertStoreParameters implements CertStoreParameters {
  private static final int LDAP_DEFAULT_PORT = 389;
  
  private int port;
  
  private String serverName;
  
  public LDAPCertStoreParameters(String paramString, int paramInt) {
    if (paramString == null)
      throw new NullPointerException(); 
    this.serverName = paramString;
    this.port = paramInt;
  }
  
  public LDAPCertStoreParameters(String paramString) { this(paramString, 389); }
  
  public LDAPCertStoreParameters() { this("localhost", 389); }
  
  public String getServerName() { return this.serverName; }
  
  public int getPort() { return this.port; }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException.toString(), cloneNotSupportedException);
    } 
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("LDAPCertStoreParameters: [\n");
    stringBuffer.append("  serverName: " + this.serverName + "\n");
    stringBuffer.append("  port: " + this.port + "\n");
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\LDAPCertStoreParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */