package javax.net.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class SSLSocket extends Socket {
  protected SSLSocket() {}
  
  protected SSLSocket(String paramString, int paramInt) throws IOException, UnknownHostException { super(paramString, paramInt); }
  
  protected SSLSocket(InetAddress paramInetAddress, int paramInt) throws IOException { super(paramInetAddress, paramInt); }
  
  protected SSLSocket(String paramString, int paramInt1, InetAddress paramInetAddress, int paramInt2) throws IOException, UnknownHostException { super(paramString, paramInt1, paramInetAddress, paramInt2); }
  
  protected SSLSocket(InetAddress paramInetAddress1, int paramInt1, InetAddress paramInetAddress2, int paramInt2) throws IOException { super(paramInetAddress1, paramInt1, paramInetAddress2, paramInt2); }
  
  public abstract String[] getSupportedCipherSuites();
  
  public abstract String[] getEnabledCipherSuites();
  
  public abstract void setEnabledCipherSuites(String[] paramArrayOfString);
  
  public abstract String[] getSupportedProtocols();
  
  public abstract String[] getEnabledProtocols();
  
  public abstract void setEnabledProtocols(String[] paramArrayOfString);
  
  public abstract SSLSession getSession();
  
  public SSLSession getHandshakeSession() { throw new UnsupportedOperationException(); }
  
  public abstract void addHandshakeCompletedListener(HandshakeCompletedListener paramHandshakeCompletedListener);
  
  public abstract void removeHandshakeCompletedListener(HandshakeCompletedListener paramHandshakeCompletedListener);
  
  public abstract void startHandshake();
  
  public abstract void setUseClientMode(boolean paramBoolean);
  
  public abstract boolean getUseClientMode();
  
  public abstract void setNeedClientAuth(boolean paramBoolean);
  
  public abstract boolean getNeedClientAuth();
  
  public abstract void setWantClientAuth(boolean paramBoolean);
  
  public abstract boolean getWantClientAuth();
  
  public abstract void setEnableSessionCreation(boolean paramBoolean);
  
  public abstract boolean getEnableSessionCreation();
  
  public SSLParameters getSSLParameters() {
    SSLParameters sSLParameters = new SSLParameters();
    sSLParameters.setCipherSuites(getEnabledCipherSuites());
    sSLParameters.setProtocols(getEnabledProtocols());
    if (getNeedClientAuth()) {
      sSLParameters.setNeedClientAuth(true);
    } else if (getWantClientAuth()) {
      sSLParameters.setWantClientAuth(true);
    } 
    return sSLParameters;
  }
  
  public void setSSLParameters(SSLParameters paramSSLParameters) {
    String[] arrayOfString = paramSSLParameters.getCipherSuites();
    if (arrayOfString != null)
      setEnabledCipherSuites(arrayOfString); 
    arrayOfString = paramSSLParameters.getProtocols();
    if (arrayOfString != null)
      setEnabledProtocols(arrayOfString); 
    if (paramSSLParameters.getNeedClientAuth()) {
      setNeedClientAuth(true);
    } else if (paramSSLParameters.getWantClientAuth()) {
      setWantClientAuth(true);
    } else {
      setWantClientAuth(false);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\SSLSocket.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */