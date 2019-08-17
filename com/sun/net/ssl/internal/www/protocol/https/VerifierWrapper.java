package com.sun.net.ssl.internal.www.protocol.https;

import com.sun.net.ssl.HostnameVerifier;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import sun.security.util.DerValue;
import sun.security.util.HostnameChecker;
import sun.security.x509.X500Name;

class VerifierWrapper implements HostnameVerifier {
  private HostnameVerifier verifier;
  
  VerifierWrapper(HostnameVerifier paramHostnameVerifier) { this.verifier = paramHostnameVerifier; }
  
  public boolean verify(String paramString, SSLSession paramSSLSession) {
    try {
      String str;
      if (paramSSLSession.getCipherSuite().startsWith("TLS_KRB5")) {
        str = HostnameChecker.getServerName(getPeerPrincipal(paramSSLSession));
      } else {
        Certificate[] arrayOfCertificate = paramSSLSession.getPeerCertificates();
        if (arrayOfCertificate == null || arrayOfCertificate.length == 0)
          return false; 
        if (!(arrayOfCertificate[0] instanceof X509Certificate))
          return false; 
        X509Certificate x509Certificate = (X509Certificate)arrayOfCertificate[0];
        str = getServername(x509Certificate);
      } 
      return (str == null) ? false : this.verifier.verify(paramString, str);
    } catch (SSLPeerUnverifiedException sSLPeerUnverifiedException) {
      return false;
    } 
  }
  
  private Principal getPeerPrincipal(SSLSession paramSSLSession) throws SSLPeerUnverifiedException {
    Principal principal;
    try {
      principal = paramSSLSession.getPeerPrincipal();
    } catch (AbstractMethodError abstractMethodError) {
      principal = null;
    } 
    return principal;
  }
  
  private static String getServername(X509Certificate paramX509Certificate) {
    try {
      Collection collection = paramX509Certificate.getSubjectAlternativeNames();
      if (collection != null)
        for (List list : collection) {
          if (((Integer)list.get(0)).intValue() == 2)
            return (String)list.get(1); 
        }  
      X500Name x500Name = HostnameChecker.getSubjectX500Name(paramX509Certificate);
      DerValue derValue = x500Name.findMostSpecificAttribute(X500Name.commonName_oid);
      if (derValue != null)
        try {
          return derValue.getAsString();
        } catch (IOException iOException) {} 
    } catch (CertificateException certificateException) {}
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\net\ssl\internal\www\protocol\https\VerifierWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */