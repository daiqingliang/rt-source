package javax.rmi.ssl;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.util.StringTokenizer;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SslRMIClientSocketFactory implements RMIClientSocketFactory, Serializable {
  private static SocketFactory defaultSocketFactory = null;
  
  private static final long serialVersionUID = -8310631444933958385L;
  
  public Socket createSocket(String paramString, int paramInt) throws IOException {
    SocketFactory socketFactory = getDefaultClientSocketFactory();
    SSLSocket sSLSocket = (SSLSocket)socketFactory.createSocket(paramString, paramInt);
    String str1 = System.getProperty("javax.rmi.ssl.client.enabledCipherSuites");
    if (str1 != null) {
      StringTokenizer stringTokenizer = new StringTokenizer(str1, ",");
      int i = stringTokenizer.countTokens();
      String[] arrayOfString = new String[i];
      for (b = 0; b < i; b++)
        arrayOfString[b] = stringTokenizer.nextToken(); 
      try {
        sSLSocket.setEnabledCipherSuites(arrayOfString);
      } catch (IllegalArgumentException b) {
        IllegalArgumentException illegalArgumentException;
        throw (IOException)(new IOException(illegalArgumentException.getMessage())).initCause(illegalArgumentException);
      } 
    } 
    String str2 = System.getProperty("javax.rmi.ssl.client.enabledProtocols");
    if (str2 != null) {
      StringTokenizer stringTokenizer = new StringTokenizer(str2, ",");
      int i = stringTokenizer.countTokens();
      String[] arrayOfString = new String[i];
      for (b = 0; b < i; b++)
        arrayOfString[b] = stringTokenizer.nextToken(); 
      try {
        sSLSocket.setEnabledProtocols(arrayOfString);
      } catch (IllegalArgumentException b) {
        IllegalArgumentException illegalArgumentException;
        throw (IOException)(new IOException(illegalArgumentException.getMessage())).initCause(illegalArgumentException);
      } 
    } 
    return sSLSocket;
  }
  
  public boolean equals(Object paramObject) { return (paramObject == null) ? false : ((paramObject == this) ? true : getClass().equals(paramObject.getClass())); }
  
  public int hashCode() { return getClass().hashCode(); }
  
  private static SocketFactory getDefaultClientSocketFactory() {
    if (defaultSocketFactory == null)
      defaultSocketFactory = SSLSocketFactory.getDefault(); 
    return defaultSocketFactory;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\rmi\ssl\SslRMIClientSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */