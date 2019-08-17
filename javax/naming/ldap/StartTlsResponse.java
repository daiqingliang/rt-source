package javax.naming.ldap;

import java.io.IOException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

public abstract class StartTlsResponse implements ExtendedResponse {
  public static final String OID = "1.3.6.1.4.1.1466.20037";
  
  private static final long serialVersionUID = 8372842182579276418L;
  
  public String getID() { return "1.3.6.1.4.1.1466.20037"; }
  
  public byte[] getEncodedValue() { return null; }
  
  public abstract void setEnabledCipherSuites(String[] paramArrayOfString);
  
  public abstract void setHostnameVerifier(HostnameVerifier paramHostnameVerifier);
  
  public abstract SSLSession negotiate() throws IOException;
  
  public abstract SSLSession negotiate(SSLSocketFactory paramSSLSocketFactory) throws IOException;
  
  public abstract void close();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\StartTlsResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */