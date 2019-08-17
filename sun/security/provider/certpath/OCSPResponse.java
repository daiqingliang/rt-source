package sun.security.provider.certpath;

import java.io.IOException;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CRLReason;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.Extension;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.action.GetIntegerAction;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;
import sun.security.x509.Extension;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.X509CertImpl;

public final class OCSPResponse {
  private static final ResponseStatus[] rsvalues = ResponseStatus.values();
  
  private static final Debug debug;
  
  private static final boolean dump = (debug != null && (debug = Debug.getInstance("certpath")).isOn("ocsp"));
  
  private static final ObjectIdentifier OCSP_BASIC_RESPONSE_OID = ObjectIdentifier.newInternal(new int[] { 1, 3, 6, 1, 5, 5, 7, 48, 1, 1 });
  
  private static final int CERT_STATUS_GOOD = 0;
  
  private static final int CERT_STATUS_REVOKED = 1;
  
  private static final int CERT_STATUS_UNKNOWN = 2;
  
  private static final int NAME_TAG = 1;
  
  private static final int KEY_TAG = 2;
  
  private static final String KP_OCSP_SIGNING_OID = "1.3.6.1.5.5.7.3.9";
  
  private static final int DEFAULT_MAX_CLOCK_SKEW = 900000;
  
  private static final int MAX_CLOCK_SKEW = initializeClockSkew();
  
  private static final CRLReason[] values = CRLReason.values();
  
  private final ResponseStatus responseStatus;
  
  private final Map<CertId, SingleResponse> singleResponseMap;
  
  private final AlgorithmId sigAlgId;
  
  private final byte[] signature;
  
  private final byte[] tbsResponseData;
  
  private final byte[] responseNonce;
  
  private List<X509CertImpl> certs;
  
  private X509CertImpl signerCert = null;
  
  private final ResponderId respId;
  
  private Date producedAtDate = null;
  
  private final Map<String, Extension> responseExtensions;
  
  private static int initializeClockSkew() {
    Integer integer = (Integer)AccessController.doPrivileged(new GetIntegerAction("com.sun.security.ocsp.clockSkew"));
    return (integer == null || integer.intValue() < 0) ? 900000 : (integer.intValue() * 1000);
  }
  
  public OCSPResponse(byte[] paramArrayOfByte) throws IOException {
    if (dump) {
      HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
      debug.println("OCSPResponse bytes...\n\n" + hexDumpEncoder.encode(paramArrayOfByte) + "\n");
    } 
    DerValue derValue1 = new DerValue(paramArrayOfByte);
    if (derValue1.tag != 48)
      throw new IOException("Bad encoding in OCSP response: expected ASN.1 SEQUENCE tag."); 
    DerInputStream derInputStream1 = derValue1.getData();
    int i = derInputStream1.getEnumerated();
    if (i >= 0 && i < rsvalues.length) {
      this.responseStatus = rsvalues[i];
    } else {
      throw new IOException("Unknown OCSPResponse status: " + i);
    } 
    if (debug != null)
      debug.println("OCSP response status: " + this.responseStatus); 
    if (this.responseStatus != ResponseStatus.SUCCESSFUL) {
      this.singleResponseMap = Collections.emptyMap();
      this.certs = new ArrayList();
      this.sigAlgId = null;
      this.signature = null;
      this.tbsResponseData = null;
      this.responseNonce = null;
      this.responseExtensions = Collections.emptyMap();
      this.respId = null;
      return;
    } 
    derValue1 = derInputStream1.getDerValue();
    if (!derValue1.isContextSpecific((byte)0))
      throw new IOException("Bad encoding in responseBytes element of OCSP response: expected ASN.1 context specific tag 0."); 
    DerValue derValue2 = derValue1.data.getDerValue();
    if (derValue2.tag != 48)
      throw new IOException("Bad encoding in responseBytes element of OCSP response: expected ASN.1 SEQUENCE tag."); 
    derInputStream1 = derValue2.data;
    ObjectIdentifier objectIdentifier = derInputStream1.getOID();
    if (objectIdentifier.equals(OCSP_BASIC_RESPONSE_OID)) {
      if (debug != null)
        debug.println("OCSP response type: basic"); 
    } else {
      if (debug != null)
        debug.println("OCSP response type: " + objectIdentifier); 
      throw new IOException("Unsupported OCSP response type: " + objectIdentifier);
    } 
    DerInputStream derInputStream2 = new DerInputStream(derInputStream1.getOctetString());
    DerValue[] arrayOfDerValue1 = derInputStream2.getSequence(2);
    if (arrayOfDerValue1.length < 3)
      throw new IOException("Unexpected BasicOCSPResponse value"); 
    DerValue derValue3 = arrayOfDerValue1[0];
    this.tbsResponseData = arrayOfDerValue1[0].toByteArray();
    if (derValue3.tag != 48)
      throw new IOException("Bad encoding in tbsResponseData element of OCSP response: expected ASN.1 SEQUENCE tag."); 
    DerInputStream derInputStream3 = derValue3.data;
    DerValue derValue4 = derInputStream3.getDerValue();
    if (derValue4.isContextSpecific((byte)0) && derValue4.isConstructed() && derValue4.isContextSpecific()) {
      derValue4 = derValue4.data.getDerValue();
      int j = derValue4.getInteger();
      if (derValue4.data.available() != 0)
        throw new IOException("Bad encoding in version  element of OCSP response: bad format"); 
      derValue4 = derInputStream3.getDerValue();
    } 
    this.respId = new ResponderId(derValue4.toByteArray());
    if (debug != null)
      debug.println("Responder ID: " + this.respId); 
    derValue4 = derInputStream3.getDerValue();
    this.producedAtDate = derValue4.getGeneralizedTime();
    if (debug != null)
      debug.println("OCSP response produced at: " + this.producedAtDate); 
    DerValue[] arrayOfDerValue2 = derInputStream3.getSequence(1);
    this.singleResponseMap = new HashMap(arrayOfDerValue2.length);
    if (debug != null)
      debug.println("OCSP number of SingleResponses: " + arrayOfDerValue2.length); 
    for (DerValue derValue : arrayOfDerValue2) {
      SingleResponse singleResponse = new SingleResponse(derValue, null);
      this.singleResponseMap.put(singleResponse.getCertId(), singleResponse);
    } 
    Map map = new HashMap();
    if (derInputStream3.available() > 0) {
      derValue4 = derInputStream3.getDerValue();
      if (derValue4.isContextSpecific((byte)1))
        map = parseExtensions(derValue4); 
    } 
    this.responseExtensions = map;
    Extension extension = (Extension)map.get(PKIXExtensions.OCSPNonce_Id.toString());
    this.responseNonce = (extension != null) ? extension.getExtensionValue() : null;
    if (debug != null && this.responseNonce != null)
      debug.println("Response nonce: " + Arrays.toString(this.responseNonce)); 
    this.sigAlgId = AlgorithmId.parse(arrayOfDerValue1[1]);
    this.signature = arrayOfDerValue1[2].getBitString();
    if (arrayOfDerValue1.length > 3) {
      DerValue derValue = arrayOfDerValue1[3];
      if (!derValue.isContextSpecific((byte)0))
        throw new IOException("Bad encoding in certs element of OCSP response: expected ASN.1 context specific tag 0."); 
      DerValue[] arrayOfDerValue = derValue.getData().getSequence(3);
      this.certs = new ArrayList(arrayOfDerValue.length);
      try {
        for (byte b = 0; b < arrayOfDerValue.length; b++) {
          X509CertImpl x509CertImpl = new X509CertImpl(arrayOfDerValue[b].toByteArray());
          this.certs.add(x509CertImpl);
          if (debug != null)
            debug.println("OCSP response cert #" + (b + true) + ": " + x509CertImpl.getSubjectX500Principal()); 
        } 
      } catch (CertificateException certificateException) {
        throw new IOException("Bad encoding in X509 Certificate", certificateException);
      } 
    } else {
      this.certs = new ArrayList();
    } 
  }
  
  void verify(List<CertId> paramList, IssuerInfo paramIssuerInfo, X509Certificate paramX509Certificate, Date paramDate, byte[] paramArrayOfByte, String paramString) throws CertPathValidatorException {
    switch (this.responseStatus) {
      case SUCCESSFUL:
        break;
      case TRY_LATER:
      case INTERNAL_ERROR:
        throw new CertPathValidatorException("OCSP response error: " + this.responseStatus, null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
      default:
        throw new CertPathValidatorException("OCSP response error: " + this.responseStatus);
    } 
    for (CertId certId : paramList) {
      SingleResponse singleResponse = getSingleResponse(certId);
      if (singleResponse == null) {
        if (debug != null)
          debug.println("No response found for CertId: " + certId); 
        throw new CertPathValidatorException("OCSP response does not include a response for a certificate supplied in the OCSP request");
      } 
      if (debug != null)
        debug.println("Status of certificate (with serial number " + certId.getSerialNumber() + ") is: " + singleResponse.getCertStatus()); 
    } 
    if (this.signerCert == null) {
      try {
        if (paramIssuerInfo.getCertificate() != null)
          this.certs.add(X509CertImpl.toImpl(paramIssuerInfo.getCertificate())); 
        if (paramX509Certificate != null)
          this.certs.add(X509CertImpl.toImpl(paramX509Certificate)); 
      } catch (CertificateException certificateException) {
        throw new CertPathValidatorException("Invalid issuer or trusted responder certificate", certificateException);
      } 
      if (this.respId.getType() == ResponderId.Type.BY_NAME) {
        X500Principal x500Principal = this.respId.getResponderName();
        for (X509CertImpl x509CertImpl : this.certs) {
          if (x509CertImpl.getSubjectX500Principal().equals(x500Principal)) {
            this.signerCert = x509CertImpl;
            break;
          } 
        } 
      } else if (this.respId.getType() == ResponderId.Type.BY_KEY) {
        KeyIdentifier keyIdentifier = this.respId.getKeyIdentifier();
        for (X509CertImpl x509CertImpl : this.certs) {
          KeyIdentifier keyIdentifier1 = x509CertImpl.getSubjectKeyId();
          if (keyIdentifier1 != null && keyIdentifier.equals(keyIdentifier1)) {
            this.signerCert = x509CertImpl;
            break;
          } 
          try {
            keyIdentifier1 = new KeyIdentifier(x509CertImpl.getPublicKey());
          } catch (IOException iOException) {}
          if (keyIdentifier.equals(keyIdentifier1)) {
            this.signerCert = x509CertImpl;
            break;
          } 
        } 
      } 
    } 
    if (this.signerCert != null)
      if (this.signerCert.getSubjectX500Principal().equals(paramIssuerInfo.getName()) && this.signerCert.getPublicKey().equals(paramIssuerInfo.getPublicKey())) {
        if (debug != null)
          debug.println("OCSP response is signed by the target's Issuing CA"); 
      } else if (this.signerCert.equals(paramX509Certificate)) {
        if (debug != null)
          debug.println("OCSP response is signed by a Trusted Responder"); 
      } else if (this.signerCert.getIssuerX500Principal().equals(paramIssuerInfo.getName())) {
        try {
          List list = this.signerCert.getExtendedKeyUsage();
          if (list == null || !list.contains("1.3.6.1.5.5.7.3.9"))
            throw new CertPathValidatorException("Responder's certificate not valid for signing OCSP responses"); 
        } catch (CertificateParsingException certificateParsingException) {
          throw new CertPathValidatorException("Responder's certificate not valid for signing OCSP responses", certificateParsingException);
        } 
        AlgorithmChecker algorithmChecker = new AlgorithmChecker(paramIssuerInfo.getAnchor(), paramDate, paramString);
        algorithmChecker.init(false);
        algorithmChecker.check(this.signerCert, Collections.emptySet());
        try {
          if (paramDate == null) {
            this.signerCert.checkValidity();
          } else {
            this.signerCert.checkValidity(paramDate);
          } 
        } catch (CertificateException certificateException) {
          throw new CertPathValidatorException("Responder's certificate not within the validity period", certificateException);
        } 
        Extension extension = this.signerCert.getExtension(PKIXExtensions.OCSPNoCheck_Id);
        if (extension != null && debug != null)
          debug.println("Responder's certificate includes the extension id-pkix-ocsp-nocheck."); 
        try {
          this.signerCert.verify(paramIssuerInfo.getPublicKey());
          if (debug != null)
            debug.println("OCSP response is signed by an Authorized Responder"); 
        } catch (GeneralSecurityException generalSecurityException) {
          this.signerCert = null;
        } 
      } else {
        throw new CertPathValidatorException("Responder's certificate is not authorized to sign OCSP responses");
      }  
    if (this.signerCert != null) {
      AlgorithmChecker.check(this.signerCert.getPublicKey(), this.sigAlgId, paramString);
      if (!verifySignature(this.signerCert))
        throw new CertPathValidatorException("Error verifying OCSP Response's signature"); 
    } else {
      throw new CertPathValidatorException("Unable to verify OCSP Response's signature");
    } 
    if (paramArrayOfByte != null && this.responseNonce != null && !Arrays.equals(paramArrayOfByte, this.responseNonce))
      throw new CertPathValidatorException("Nonces don't match"); 
    long l = (paramDate == null) ? System.currentTimeMillis() : paramDate.getTime();
    Date date1 = new Date(l + MAX_CLOCK_SKEW);
    Date date2 = new Date(l - MAX_CLOCK_SKEW);
    for (SingleResponse singleResponse : this.singleResponseMap.values()) {
      if (debug != null) {
        String str = "";
        if (singleResponse.nextUpdate != null)
          str = " until " + singleResponse.nextUpdate; 
        debug.println("OCSP response validity interval is from " + singleResponse.thisUpdate + str);
        debug.println("Checking validity of OCSP response on: " + new Date(l));
      } 
      if (date1.before(singleResponse.thisUpdate) || date2.after((singleResponse.nextUpdate != null) ? singleResponse.nextUpdate : singleResponse.thisUpdate))
        throw new CertPathValidatorException("Response is unreliable: its validity interval is out-of-date"); 
    } 
  }
  
  public ResponseStatus getResponseStatus() { return this.responseStatus; }
  
  private boolean verifySignature(X509Certificate paramX509Certificate) throws CertPathValidatorException {
    try {
      Signature signature1 = Signature.getInstance(this.sigAlgId.getName());
      signature1.initVerify(paramX509Certificate.getPublicKey());
      signature1.update(this.tbsResponseData);
      if (signature1.verify(this.signature)) {
        if (debug != null)
          debug.println("Verified signature of OCSP Response"); 
        return true;
      } 
      if (debug != null)
        debug.println("Error verifying signature of OCSP Response"); 
      return false;
    } catch (InvalidKeyException|java.security.NoSuchAlgorithmException|java.security.SignatureException invalidKeyException) {
      throw new CertPathValidatorException(invalidKeyException);
    } 
  }
  
  public SingleResponse getSingleResponse(CertId paramCertId) { return (SingleResponse)this.singleResponseMap.get(paramCertId); }
  
  public Set<CertId> getCertIds() { return Collections.unmodifiableSet(this.singleResponseMap.keySet()); }
  
  X509Certificate getSignerCertificate() { return this.signerCert; }
  
  public ResponderId getResponderId() { return this.respId; }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("OCSP Response:\n");
    stringBuilder.append("Response Status: ").append(this.responseStatus).append("\n");
    stringBuilder.append("Responder ID: ").append(this.respId).append("\n");
    stringBuilder.append("Produced at: ").append(this.producedAtDate).append("\n");
    int i = this.singleResponseMap.size();
    stringBuilder.append(i).append((i == 1) ? " response:\n" : " responses:\n");
    for (SingleResponse singleResponse : this.singleResponseMap.values())
      stringBuilder.append(singleResponse).append("\n"); 
    if (this.responseExtensions != null && this.responseExtensions.size() > 0) {
      i = this.responseExtensions.size();
      stringBuilder.append(i).append((i == 1) ? " extension:\n" : " extensions:\n");
      for (String str : this.responseExtensions.keySet())
        stringBuilder.append(this.responseExtensions.get(str)).append("\n"); 
    } 
    return stringBuilder.toString();
  }
  
  private static Map<String, Extension> parseExtensions(DerValue paramDerValue) throws IOException {
    DerValue[] arrayOfDerValue = paramDerValue.data.getSequence(3);
    HashMap hashMap = new HashMap(arrayOfDerValue.length);
    for (DerValue derValue : arrayOfDerValue) {
      Extension extension = new Extension(derValue);
      if (debug != null)
        debug.println("Extension: " + extension); 
      if (extension.isCritical())
        throw new IOException("Unsupported OCSP critical extension: " + extension.getExtensionId()); 
      hashMap.put(extension.getId(), extension);
    } 
    return hashMap;
  }
  
  static final class IssuerInfo {
    private final TrustAnchor anchor;
    
    private final X509Certificate certificate;
    
    private final X500Principal name;
    
    private final PublicKey pubKey;
    
    IssuerInfo(TrustAnchor param1TrustAnchor) { this(param1TrustAnchor, (param1TrustAnchor != null) ? param1TrustAnchor.getTrustedCert() : null); }
    
    IssuerInfo(X509Certificate param1X509Certificate) { this(null, param1X509Certificate); }
    
    IssuerInfo(TrustAnchor param1TrustAnchor, X509Certificate param1X509Certificate) {
      if (param1TrustAnchor == null && param1X509Certificate == null)
        throw new NullPointerException("TrustAnchor and issuerCert cannot be null"); 
      this.anchor = param1TrustAnchor;
      if (param1X509Certificate != null) {
        this.name = param1X509Certificate.getSubjectX500Principal();
        this.pubKey = param1X509Certificate.getPublicKey();
        this.certificate = param1X509Certificate;
      } else {
        this.name = param1TrustAnchor.getCA();
        this.pubKey = param1TrustAnchor.getCAPublicKey();
        this.certificate = param1TrustAnchor.getTrustedCert();
      } 
    }
    
    X509Certificate getCertificate() { return this.certificate; }
    
    X500Principal getName() { return this.name; }
    
    PublicKey getPublicKey() { return this.pubKey; }
    
    TrustAnchor getAnchor() { return this.anchor; }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Issuer Info:\n");
      stringBuilder.append("Name: ").append(this.name.toString()).append("\n");
      stringBuilder.append("Public Key:\n").append(this.pubKey.toString()).append("\n");
      return stringBuilder.toString();
    }
  }
  
  public enum ResponseStatus {
    SUCCESSFUL, MALFORMED_REQUEST, INTERNAL_ERROR, TRY_LATER, UNUSED, SIG_REQUIRED, UNAUTHORIZED;
  }
  
  public static final class SingleResponse implements OCSP.RevocationStatus {
    private final CertId certId;
    
    private final OCSP.RevocationStatus.CertStatus certStatus;
    
    private final Date thisUpdate;
    
    private final Date nextUpdate;
    
    private final Date revocationTime;
    
    private final CRLReason revocationReason;
    
    private final Map<String, Extension> singleExtensions;
    
    private SingleResponse(DerValue param1DerValue) throws IOException {
      if (param1DerValue.tag != 48)
        throw new IOException("Bad ASN.1 encoding in SingleResponse"); 
      DerInputStream derInputStream = param1DerValue.data;
      this.certId = new CertId((derInputStream.getDerValue()).data);
      DerValue derValue = derInputStream.getDerValue();
      short s = (short)(byte)(derValue.tag & 0x1F);
      if (s == 1) {
        this.certStatus = OCSP.RevocationStatus.CertStatus.REVOKED;
        this.revocationTime = derValue.data.getGeneralizedTime();
        if (derValue.data.available() != 0) {
          DerValue derValue1 = derValue.data.getDerValue();
          s = (short)(byte)(derValue1.tag & 0x1F);
          if (s == 0) {
            int i = derValue1.data.getEnumerated();
            if (i >= 0 && i < values.length) {
              this.revocationReason = values[i];
            } else {
              this.revocationReason = CRLReason.UNSPECIFIED;
            } 
          } else {
            this.revocationReason = CRLReason.UNSPECIFIED;
          } 
        } else {
          this.revocationReason = CRLReason.UNSPECIFIED;
        } 
        if (debug != null) {
          debug.println("Revocation time: " + this.revocationTime);
          debug.println("Revocation reason: " + this.revocationReason);
        } 
      } else {
        this.revocationTime = null;
        this.revocationReason = null;
        if (s == 0) {
          this.certStatus = OCSP.RevocationStatus.CertStatus.GOOD;
        } else if (s == 2) {
          this.certStatus = OCSP.RevocationStatus.CertStatus.UNKNOWN;
        } else {
          throw new IOException("Invalid certificate status");
        } 
      } 
      this.thisUpdate = derInputStream.getGeneralizedTime();
      if (debug != null)
        debug.println("thisUpdate: " + this.thisUpdate); 
      Date date = null;
      Map map = null;
      if (derInputStream.available() > 0) {
        derValue = derInputStream.getDerValue();
        if (derValue.isContextSpecific((byte)0)) {
          date = derValue.data.getGeneralizedTime();
          if (debug != null)
            debug.println("nextUpdate: " + date); 
          derValue = (derInputStream.available() > 0) ? derInputStream.getDerValue() : null;
        } 
        if (derValue != null)
          if (derValue.isContextSpecific((byte)1)) {
            map = OCSPResponse.parseExtensions(derValue);
            if (derInputStream.available() > 0)
              throw new IOException(derInputStream.available() + " bytes of additional data in singleResponse"); 
          } else {
            throw new IOException("Unsupported singleResponse item, tag = " + String.format("%02X", new Object[] { Byte.valueOf(derValue.tag) }));
          }  
      } 
      this.nextUpdate = date;
      this.singleExtensions = (map != null) ? map : Collections.emptyMap();
      if (debug != null)
        for (Extension extension : this.singleExtensions.values())
          debug.println("singleExtension: " + extension);  
    }
    
    public OCSP.RevocationStatus.CertStatus getCertStatus() { return this.certStatus; }
    
    public CertId getCertId() { return this.certId; }
    
    public Date getThisUpdate() { return (this.thisUpdate != null) ? (Date)this.thisUpdate.clone() : null; }
    
    public Date getNextUpdate() { return (this.nextUpdate != null) ? (Date)this.nextUpdate.clone() : null; }
    
    public Date getRevocationTime() { return (this.revocationTime != null) ? (Date)this.revocationTime.clone() : null; }
    
    public CRLReason getRevocationReason() { return this.revocationReason; }
    
    public Map<String, Extension> getSingleExtensions() { return Collections.unmodifiableMap(this.singleExtensions); }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("SingleResponse:\n");
      stringBuilder.append(this.certId);
      stringBuilder.append("\nCertStatus: ").append(this.certStatus).append("\n");
      if (this.certStatus == OCSP.RevocationStatus.CertStatus.REVOKED) {
        stringBuilder.append("revocationTime is ");
        stringBuilder.append(this.revocationTime).append("\n");
        stringBuilder.append("revocationReason is ");
        stringBuilder.append(this.revocationReason).append("\n");
      } 
      stringBuilder.append("thisUpdate is ").append(this.thisUpdate).append("\n");
      if (this.nextUpdate != null)
        stringBuilder.append("nextUpdate is ").append(this.nextUpdate).append("\n"); 
      for (Extension extension : this.singleExtensions.values()) {
        stringBuilder.append("singleExtension: ");
        stringBuilder.append(extension.toString()).append("\n");
      } 
      return stringBuilder.toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\OCSPResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */