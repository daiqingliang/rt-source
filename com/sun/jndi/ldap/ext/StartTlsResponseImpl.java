package com.sun.jndi.ldap.ext;

import com.sun.jndi.ldap.Connection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.naming.ldap.StartTlsResponse;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import sun.security.util.HostnameChecker;

public final class StartTlsResponseImpl extends StartTlsResponse {
  private static final boolean debug = false;
  
  private static final int DNSNAME_TYPE = 2;
  
  private String hostname = null;
  
  private Connection ldapConnection = null;
  
  private InputStream originalInputStream = null;
  
  private OutputStream originalOutputStream = null;
  
  private SSLSocket sslSocket = null;
  
  private SSLSocketFactory defaultFactory = null;
  
  private SSLSocketFactory currentFactory = null;
  
  private String[] suites = null;
  
  private HostnameVerifier verifier = null;
  
  private boolean isClosed = true;
  
  private static final long serialVersionUID = -1126624615143411328L;
  
  public void setEnabledCipherSuites(String[] paramArrayOfString) { this.suites = (paramArrayOfString == null) ? null : (String[])paramArrayOfString.clone(); }
  
  public void setHostnameVerifier(HostnameVerifier paramHostnameVerifier) { this.verifier = paramHostnameVerifier; }
  
  public SSLSession negotiate() throws IOException { return negotiate(null); }
  
  public SSLSession negotiate(SSLSocketFactory paramSSLSocketFactory) throws IOException {
    if (this.isClosed && this.sslSocket != null)
      throw new IOException("TLS connection is closed."); 
    if (paramSSLSocketFactory == null)
      paramSSLSocketFactory = getDefaultFactory(); 
    SSLSession sSLSession = startHandshake(paramSSLSocketFactory).getSession();
    SSLPeerUnverifiedException sSLPeerUnverifiedException = null;
    try {
      if (verify(this.hostname, sSLSession)) {
        this.isClosed = false;
        return sSLSession;
      } 
    } catch (SSLPeerUnverifiedException sSLPeerUnverifiedException1) {
      sSLPeerUnverifiedException = sSLPeerUnverifiedException1;
    } 
    if (this.verifier != null && this.verifier.verify(this.hostname, sSLSession)) {
      this.isClosed = false;
      return sSLSession;
    } 
    close();
    sSLSession.invalidate();
    if (sSLPeerUnverifiedException == null)
      sSLPeerUnverifiedException = new SSLPeerUnverifiedException("hostname of the server '" + this.hostname + "' does not match the hostname in the server's certificate."); 
    throw sSLPeerUnverifiedException;
  }
  
  public void close() {
    if (this.isClosed)
      return; 
    this.ldapConnection.replaceStreams(this.originalInputStream, this.originalOutputStream);
    this.sslSocket.close();
    this.isClosed = true;
  }
  
  public void setConnection(Connection paramConnection, String paramString) {
    this.ldapConnection = paramConnection;
    this.hostname = (paramString != null) ? paramString : paramConnection.host;
    this.originalInputStream = paramConnection.inStream;
    this.originalOutputStream = paramConnection.outStream;
  }
  
  private SSLSocketFactory getDefaultFactory() throws IOException { return (this.defaultFactory != null) ? this.defaultFactory : (this.defaultFactory = (SSLSocketFactory)SSLSocketFactory.getDefault()); }
  
  private SSLSocket startHandshake(SSLSocketFactory paramSSLSocketFactory) throws IOException {
    if (this.ldapConnection == null)
      throw new IllegalStateException("LDAP connection has not been set. TLS requires an existing LDAP connection."); 
    if (paramSSLSocketFactory != this.currentFactory) {
      this.sslSocket = (SSLSocket)paramSSLSocketFactory.createSocket(this.ldapConnection.sock, this.ldapConnection.host, this.ldapConnection.port, false);
      this.currentFactory = paramSSLSocketFactory;
    } 
    if (this.suites != null)
      this.sslSocket.setEnabledCipherSuites(this.suites); 
    try {
      this.sslSocket.startHandshake();
      this.ldapConnection.replaceStreams(this.sslSocket.getInputStream(), this.sslSocket.getOutputStream());
    } catch (IOException iOException) {
      this.sslSocket.close();
      this.isClosed = true;
      throw iOException;
    } 
    return this.sslSocket;
  }
  
  private boolean verify(String paramString, SSLSession paramSSLSession) throws SSLPeerUnverifiedException {
    Certificate[] arrayOfCertificate = null;
    if (paramString != null && paramString.startsWith("[") && paramString.endsWith("]"))
      paramString = paramString.substring(1, paramString.length() - 1); 
    try {
      HostnameChecker hostnameChecker = HostnameChecker.getInstance((byte)2);
      if (paramSSLSession.getCipherSuite().startsWith("TLS_KRB5")) {
        Principal principal = getPeerPrincipal(paramSSLSession);
        if (!HostnameChecker.match(paramString, principal))
          throw new SSLPeerUnverifiedException("hostname of the kerberos principal:" + principal + " does not match the hostname:" + paramString); 
      } else {
        X509Certificate x509Certificate;
        arrayOfCertificate = paramSSLSession.getPeerCertificates();
        if (arrayOfCertificate[0] instanceof X509Certificate) {
          x509Certificate = (X509Certificate)arrayOfCertificate[0];
        } else {
          throw new SSLPeerUnverifiedException("Received a non X509Certificate from the server");
        } 
        hostnameChecker.match(paramString, x509Certificate);
      } 
      return true;
    } catch (SSLPeerUnverifiedException sSLPeerUnverifiedException) {
      String str = paramSSLSession.getCipherSuite();
      if (str != null && str.indexOf("_anon_") != -1)
        return true; 
      throw sSLPeerUnverifiedException;
    } catch (CertificateException certificateException) {
      throw (SSLPeerUnverifiedException)(new SSLPeerUnverifiedException("hostname of the server '" + paramString + "' does not match the hostname in the server's certificate.")).initCause(certificateException);
    } 
  }
  
  private static Principal getPeerPrincipal(SSLSession paramSSLSession) throws SSLPeerUnverifiedException {
    Principal principal;
    try {
      principal = paramSSLSession.getPeerPrincipal();
    } catch (AbstractMethodError abstractMethodError) {
      principal = null;
    } 
    return principal;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\ext\StartTlsResponseImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */