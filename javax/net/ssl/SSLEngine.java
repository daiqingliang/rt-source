package javax.net.ssl;

import java.nio.ByteBuffer;

public abstract class SSLEngine {
  private String peerHost = null;
  
  private int peerPort = -1;
  
  protected SSLEngine() {}
  
  protected SSLEngine(String paramString, int paramInt) {
    this.peerHost = paramString;
    this.peerPort = paramInt;
  }
  
  public String getPeerHost() { return this.peerHost; }
  
  public int getPeerPort() { return this.peerPort; }
  
  public SSLEngineResult wrap(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2) throws SSLException { return wrap(new ByteBuffer[] { paramByteBuffer1 }, 0, 1, paramByteBuffer2); }
  
  public SSLEngineResult wrap(ByteBuffer[] paramArrayOfByteBuffer, ByteBuffer paramByteBuffer) throws SSLException {
    if (paramArrayOfByteBuffer == null)
      throw new IllegalArgumentException("src == null"); 
    return wrap(paramArrayOfByteBuffer, 0, paramArrayOfByteBuffer.length, paramByteBuffer);
  }
  
  public abstract SSLEngineResult wrap(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2, ByteBuffer paramByteBuffer) throws SSLException;
  
  public SSLEngineResult unwrap(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2) throws SSLException { return unwrap(paramByteBuffer1, new ByteBuffer[] { paramByteBuffer2 }, 0, 1); }
  
  public SSLEngineResult unwrap(ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer) throws SSLException {
    if (paramArrayOfByteBuffer == null)
      throw new IllegalArgumentException("dsts == null"); 
    return unwrap(paramByteBuffer, paramArrayOfByteBuffer, 0, paramArrayOfByteBuffer.length);
  }
  
  public abstract SSLEngineResult unwrap(ByteBuffer paramByteBuffer, ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws SSLException;
  
  public abstract Runnable getDelegatedTask();
  
  public abstract void closeInbound();
  
  public abstract boolean isInboundDone();
  
  public abstract void closeOutbound();
  
  public abstract boolean isOutboundDone();
  
  public abstract String[] getSupportedCipherSuites();
  
  public abstract String[] getEnabledCipherSuites();
  
  public abstract void setEnabledCipherSuites(String[] paramArrayOfString);
  
  public abstract String[] getSupportedProtocols();
  
  public abstract String[] getEnabledProtocols();
  
  public abstract void setEnabledProtocols(String[] paramArrayOfString);
  
  public abstract SSLSession getSession();
  
  public SSLSession getHandshakeSession() { throw new UnsupportedOperationException(); }
  
  public abstract void beginHandshake();
  
  public abstract SSLEngineResult.HandshakeStatus getHandshakeStatus();
  
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\SSLEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */