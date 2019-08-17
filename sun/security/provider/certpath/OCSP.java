package sun.security.provider.certpath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.cert.CRLReason;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.Extension;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import sun.security.action.GetIntegerAction;
import sun.security.util.Debug;
import sun.security.x509.AccessDescription;
import sun.security.x509.AuthorityInfoAccessExtension;
import sun.security.x509.GeneralName;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.URIName;
import sun.security.x509.X509CertImpl;

public final class OCSP {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private static final int DEFAULT_CONNECT_TIMEOUT = 15000;
  
  private static final int CONNECT_TIMEOUT = initializeTimeout();
  
  private static int initializeTimeout() {
    Integer integer = (Integer)AccessController.doPrivileged(new GetIntegerAction("com.sun.security.ocsp.timeout"));
    return (integer == null || integer.intValue() < 0) ? 15000 : (integer.intValue() * 1000);
  }
  
  public static RevocationStatus check(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2, URI paramURI, X509Certificate paramX509Certificate3, Date paramDate) throws IOException, CertPathValidatorException { return check(paramX509Certificate1, paramX509Certificate2, paramURI, paramX509Certificate3, paramDate, Collections.emptyList(), "generic"); }
  
  public static RevocationStatus check(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2, URI paramURI, X509Certificate paramX509Certificate3, Date paramDate, List<Extension> paramList, String paramString) throws IOException, CertPathValidatorException { return check(paramX509Certificate1, paramURI, null, paramX509Certificate2, paramX509Certificate3, paramDate, paramList, paramString); }
  
  public static RevocationStatus check(X509Certificate paramX509Certificate1, URI paramURI, TrustAnchor paramTrustAnchor, X509Certificate paramX509Certificate2, X509Certificate paramX509Certificate3, Date paramDate, List<Extension> paramList, String paramString) throws IOException, CertPathValidatorException {
    CertId certId;
    try {
      X509CertImpl x509CertImpl = X509CertImpl.toImpl(paramX509Certificate1);
      certId = new CertId(paramX509Certificate2, x509CertImpl.getSerialNumberObject());
    } catch (CertificateException|IOException certificateException) {
      throw new CertPathValidatorException("Exception while encoding OCSPRequest", certificateException);
    } 
    OCSPResponse oCSPResponse = check(Collections.singletonList(certId), paramURI, new OCSPResponse.IssuerInfo(paramTrustAnchor, paramX509Certificate2), paramX509Certificate3, paramDate, paramList, paramString);
    return oCSPResponse.getSingleResponse(certId);
  }
  
  static OCSPResponse check(List<CertId> paramList1, URI paramURI, OCSPResponse.IssuerInfo paramIssuerInfo, X509Certificate paramX509Certificate, Date paramDate, List<Extension> paramList2, String paramString) throws IOException, CertPathValidatorException {
    byte[] arrayOfByte = null;
    for (Extension extension : paramList2) {
      if (extension.getId().equals(PKIXExtensions.OCSPNonce_Id.toString()))
        arrayOfByte = extension.getValue(); 
    } 
    OCSPResponse oCSPResponse = null;
    try {
      byte[] arrayOfByte1 = getOCSPBytes(paramList1, paramURI, paramList2);
      oCSPResponse = new OCSPResponse(arrayOfByte1);
      oCSPResponse.verify(paramList1, paramIssuerInfo, paramX509Certificate, paramDate, arrayOfByte, paramString);
    } catch (IOException iOException) {
      throw new CertPathValidatorException("Unable to determine revocation status due to network error", iOException, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
    } 
    return oCSPResponse;
  }
  
  public static byte[] getOCSPBytes(List<CertId> paramList1, URI paramURI, List<Extension> paramList2) throws IOException {
    OCSPRequest oCSPRequest = new OCSPRequest(paramList1, paramList2);
    byte[] arrayOfByte1 = oCSPRequest.encodeBytes();
    inputStream = null;
    outputStream = null;
    byte[] arrayOfByte2 = null;
    try {
      uRL = paramURI.toURL();
      if (debug != null)
        debug.println("connecting to OCSP service at: " + uRL); 
      HttpURLConnection httpURLConnection = (HttpURLConnection)uRL.openConnection();
      httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
      httpURLConnection.setReadTimeout(CONNECT_TIMEOUT);
      httpURLConnection.setDoOutput(true);
      httpURLConnection.setDoInput(true);
      httpURLConnection.setRequestMethod("POST");
      httpURLConnection.setRequestProperty("Content-type", "application/ocsp-request");
      httpURLConnection.setRequestProperty("Content-length", String.valueOf(arrayOfByte1.length));
      outputStream = httpURLConnection.getOutputStream();
      outputStream.write(arrayOfByte1);
      outputStream.flush();
      if (debug != null && httpURLConnection.getResponseCode() != 200)
        debug.println("Received HTTP error: " + httpURLConnection.getResponseCode() + " - " + httpURLConnection.getResponseMessage()); 
      inputStream = httpURLConnection.getInputStream();
      int i = httpURLConnection.getContentLength();
      if (i == -1)
        i = Integer.MAX_VALUE; 
      arrayOfByte2 = new byte[(i > 2048) ? 2048 : i];
      int j = 0;
      while (j < i) {
        int k = inputStream.read(arrayOfByte2, j, arrayOfByte2.length - j);
        if (k < 0)
          break; 
        j += k;
        if (j >= arrayOfByte2.length && j < i)
          arrayOfByte2 = Arrays.copyOf(arrayOfByte2, j * 2); 
      } 
      arrayOfByte2 = Arrays.copyOf(arrayOfByte2, j);
    } finally {
      if (inputStream != null)
        try {
          inputStream.close();
        } catch (IOException iOException) {
          throw iOException;
        }  
      if (outputStream != null)
        try {
          outputStream.close();
        } catch (IOException iOException) {
          throw iOException;
        }  
    } 
    return arrayOfByte2;
  }
  
  public static URI getResponderURI(X509Certificate paramX509Certificate) {
    try {
      return getResponderURI(X509CertImpl.toImpl(paramX509Certificate));
    } catch (CertificateException certificateException) {
      return null;
    } 
  }
  
  static URI getResponderURI(X509CertImpl paramX509CertImpl) {
    AuthorityInfoAccessExtension authorityInfoAccessExtension = paramX509CertImpl.getAuthorityInfoAccessExtension();
    if (authorityInfoAccessExtension == null)
      return null; 
    List list = authorityInfoAccessExtension.getAccessDescriptions();
    for (AccessDescription accessDescription : list) {
      if (accessDescription.getAccessMethod().equals(AccessDescription.Ad_OCSP_Id)) {
        GeneralName generalName = accessDescription.getAccessLocation();
        if (generalName.getType() == 6) {
          URIName uRIName = (URIName)generalName.getName();
          return uRIName.getURI();
        } 
      } 
    } 
    return null;
  }
  
  public static interface RevocationStatus {
    CertStatus getCertStatus();
    
    Date getRevocationTime();
    
    CRLReason getRevocationReason();
    
    Map<String, Extension> getSingleExtensions();
    
    public enum CertStatus {
      GOOD, REVOKED, UNKNOWN;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\OCSP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */