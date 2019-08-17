package sun.security.provider.certpath;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CRLReason;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateRevokedException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.Extension;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.AccessDescription;
import sun.security.x509.AuthorityInfoAccessExtension;
import sun.security.x509.CRLDistributionPointsExtension;
import sun.security.x509.DistributionPoint;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNames;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.X500Name;
import sun.security.x509.X509CRLEntryImpl;
import sun.security.x509.X509CertImpl;

class RevocationChecker extends PKIXRevocationChecker {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private TrustAnchor anchor;
  
  private PKIX.ValidatorParams params;
  
  private boolean onlyEE;
  
  private boolean softFail;
  
  private boolean crlDP;
  
  private URI responderURI;
  
  private X509Certificate responderCert;
  
  private List<CertStore> certStores;
  
  private Map<X509Certificate, byte[]> ocspResponses;
  
  private List<Extension> ocspExtensions;
  
  private final boolean legacy = false;
  
  private LinkedList<CertPathValidatorException> softFailExceptions = new LinkedList();
  
  private OCSPResponse.IssuerInfo issuerInfo;
  
  private PublicKey prevPubKey;
  
  private boolean crlSignFlag;
  
  private int certIndex;
  
  private Mode mode = Mode.PREFER_OCSP;
  
  private static final long MAX_CLOCK_SKEW = 900000L;
  
  private static final String HEX_DIGITS = "0123456789ABCDEFabcdef";
  
  private static final boolean[] ALL_REASONS = { true, true, true, true, true, true, true, true, true };
  
  private static final boolean[] CRL_SIGN_USAGE = { false, false, false, false, false, false, true };
  
  RevocationChecker() {}
  
  RevocationChecker(TrustAnchor paramTrustAnchor, PKIX.ValidatorParams paramValidatorParams) throws CertPathValidatorException { init(paramTrustAnchor, paramValidatorParams); }
  
  void init(TrustAnchor paramTrustAnchor, PKIX.ValidatorParams paramValidatorParams) throws CertPathValidatorException {
    RevocationProperties revocationProperties = getRevocationProperties();
    URI uRI = getOcspResponder();
    this.responderURI = (uRI == null) ? toURI(revocationProperties.ocspUrl) : uRI;
    X509Certificate x509Certificate = getOcspResponderCert();
    this.responderCert = (x509Certificate == null) ? getResponderCert(revocationProperties, paramValidatorParams.trustAnchors(), paramValidatorParams.certStores()) : x509Certificate;
    Set set = getOptions();
    for (PKIXRevocationChecker.Option option : set) {
      switch (option) {
        case PREFER_OCSP:
        case ONLY_OCSP:
        case PREFER_CRLS:
        case ONLY_CRLS:
          continue;
      } 
      throw new CertPathValidatorException("Unrecognized revocation parameter option: " + option);
    } 
    this.softFail = set.contains(PKIXRevocationChecker.Option.SOFT_FAIL);
    if (this.legacy) {
      this.mode = revocationProperties.ocspEnabled ? Mode.PREFER_OCSP : Mode.ONLY_CRLS;
      this.onlyEE = revocationProperties.onlyEE;
    } else {
      if (set.contains(PKIXRevocationChecker.Option.NO_FALLBACK)) {
        if (set.contains(PKIXRevocationChecker.Option.PREFER_CRLS)) {
          this.mode = Mode.ONLY_CRLS;
        } else {
          this.mode = Mode.ONLY_OCSP;
        } 
      } else if (set.contains(PKIXRevocationChecker.Option.PREFER_CRLS)) {
        this.mode = Mode.PREFER_CRLS;
      } 
      this.onlyEE = set.contains(PKIXRevocationChecker.Option.ONLY_END_ENTITY);
    } 
    if (this.legacy) {
      this.crlDP = revocationProperties.crlDPEnabled;
    } else {
      this.crlDP = true;
    } 
    this.ocspResponses = getOcspResponses();
    this.ocspExtensions = getOcspExtensions();
    this.anchor = paramTrustAnchor;
    this.params = paramValidatorParams;
    this.certStores = new ArrayList(paramValidatorParams.certStores());
    try {
      this.certStores.add(CertStore.getInstance("Collection", new CollectionCertStoreParameters(paramValidatorParams.certificates())));
    } catch (InvalidAlgorithmParameterException|NoSuchAlgorithmException invalidAlgorithmParameterException) {
      if (debug != null)
        debug.println("RevocationChecker: error creating Collection CertStore: " + invalidAlgorithmParameterException); 
    } 
  }
  
  private static URI toURI(String paramString) throws CertPathValidatorException {
    try {
      return (paramString != null) ? new URI(paramString) : null;
    } catch (URISyntaxException uRISyntaxException) {
      throw new CertPathValidatorException("cannot parse ocsp.responderURL property", uRISyntaxException);
    } 
  }
  
  private static RevocationProperties getRevocationProperties() { return (RevocationProperties)AccessController.doPrivileged(new PrivilegedAction<RevocationProperties>() {
          public RevocationChecker.RevocationProperties run() {
            RevocationChecker.RevocationProperties revocationProperties = new RevocationChecker.RevocationProperties(null);
            String str1 = Security.getProperty("com.sun.security.onlyCheckRevocationOfEECert");
            revocationProperties.onlyEE = (str1 != null && str1.equalsIgnoreCase("true"));
            String str2 = Security.getProperty("ocsp.enable");
            revocationProperties.ocspEnabled = (str2 != null && str2.equalsIgnoreCase("true"));
            revocationProperties.ocspUrl = Security.getProperty("ocsp.responderURL");
            revocationProperties.ocspSubject = Security.getProperty("ocsp.responderCertSubjectName");
            revocationProperties.ocspIssuer = Security.getProperty("ocsp.responderCertIssuerName");
            revocationProperties.ocspSerial = Security.getProperty("ocsp.responderCertSerialNumber");
            revocationProperties.crlDPEnabled = Boolean.getBoolean("com.sun.security.enableCRLDP");
            return revocationProperties;
          }
        }); }
  
  private static X509Certificate getResponderCert(RevocationProperties paramRevocationProperties, Set<TrustAnchor> paramSet, List<CertStore> paramList) throws CertPathValidatorException {
    if (paramRevocationProperties.ocspSubject != null)
      return getResponderCert(paramRevocationProperties.ocspSubject, paramSet, paramList); 
    if (paramRevocationProperties.ocspIssuer != null && paramRevocationProperties.ocspSerial != null)
      return getResponderCert(paramRevocationProperties.ocspIssuer, paramRevocationProperties.ocspSerial, paramSet, paramList); 
    if (paramRevocationProperties.ocspIssuer != null || paramRevocationProperties.ocspSerial != null)
      throw new CertPathValidatorException("Must specify both ocsp.responderCertIssuerName and ocsp.responderCertSerialNumber properties"); 
    return null;
  }
  
  private static X509Certificate getResponderCert(String paramString, Set<TrustAnchor> paramSet, List<CertStore> paramList) throws CertPathValidatorException {
    X509CertSelector x509CertSelector = new X509CertSelector();
    try {
      x509CertSelector.setSubject(new X500Principal(paramString));
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new CertPathValidatorException("cannot parse ocsp.responderCertSubjectName property", illegalArgumentException);
    } 
    return getResponderCert(x509CertSelector, paramSet, paramList);
  }
  
  private static X509Certificate getResponderCert(String paramString1, String paramString2, Set<TrustAnchor> paramSet, List<CertStore> paramList) throws CertPathValidatorException {
    X509CertSelector x509CertSelector = new X509CertSelector();
    try {
      x509CertSelector.setIssuer(new X500Principal(paramString1));
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new CertPathValidatorException("cannot parse ocsp.responderCertIssuerName property", illegalArgumentException);
    } 
    try {
      x509CertSelector.setSerialNumber(new BigInteger(stripOutSeparators(paramString2), 16));
    } catch (NumberFormatException numberFormatException) {
      throw new CertPathValidatorException("cannot parse ocsp.responderCertSerialNumber property", numberFormatException);
    } 
    return getResponderCert(x509CertSelector, paramSet, paramList);
  }
  
  private static X509Certificate getResponderCert(X509CertSelector paramX509CertSelector, Set<TrustAnchor> paramSet, List<CertStore> paramList) throws CertPathValidatorException {
    for (TrustAnchor trustAnchor : paramSet) {
      X509Certificate x509Certificate = trustAnchor.getTrustedCert();
      if (x509Certificate != null && paramX509CertSelector.match(x509Certificate))
        return x509Certificate; 
    } 
    for (CertStore certStore : paramList) {
      try {
        Collection collection = certStore.getCertificates(paramX509CertSelector);
        if (!collection.isEmpty())
          return (X509Certificate)collection.iterator().next(); 
      } catch (CertStoreException certStoreException) {
        if (debug != null)
          debug.println("CertStore exception:" + certStoreException); 
      } 
    } 
    throw new CertPathValidatorException("Cannot find the responder's certificate (set using the OCSP security properties).");
  }
  
  public void init(boolean paramBoolean) throws CertPathValidatorException {
    if (paramBoolean)
      throw new CertPathValidatorException("forward checking not supported"); 
    if (this.anchor != null) {
      this.issuerInfo = new OCSPResponse.IssuerInfo(this.anchor);
      this.prevPubKey = this.issuerInfo.getPublicKey();
    } 
    this.crlSignFlag = true;
    if (this.params != null && this.params.certPath() != null) {
      this.certIndex = this.params.certPath().getCertificates().size() - 1;
    } else {
      this.certIndex = -1;
    } 
    this.softFailExceptions.clear();
  }
  
  public boolean isForwardCheckingSupported() { return false; }
  
  public Set<String> getSupportedExtensions() { return null; }
  
  public List<CertPathValidatorException> getSoftFailExceptions() { return Collections.unmodifiableList(this.softFailExceptions); }
  
  public void check(Certificate paramCertificate, Collection<String> paramCollection) throws CertPathValidatorException { check((X509Certificate)paramCertificate, paramCollection, this.prevPubKey, this.crlSignFlag); }
  
  private void check(X509Certificate paramX509Certificate, Collection<String> paramCollection, PublicKey paramPublicKey, boolean paramBoolean) throws CertPathValidatorException {
    if (debug != null)
      debug.println("RevocationChecker.check: checking cert\n  SN: " + Debug.toHexString(paramX509Certificate.getSerialNumber()) + "\n  Subject: " + paramX509Certificate.getSubjectX500Principal() + "\n  Issuer: " + paramX509Certificate.getIssuerX500Principal()); 
    try {
      if (this.onlyEE && paramX509Certificate.getBasicConstraints() != -1) {
        if (debug != null)
          debug.println("Skipping revocation check; cert is not an end entity cert"); 
        return;
      } 
      switch (this.mode) {
        case PREFER_OCSP:
        case ONLY_OCSP:
          checkOCSP(paramX509Certificate, paramCollection);
          break;
        case PREFER_CRLS:
        case ONLY_CRLS:
          checkCRLs(paramX509Certificate, paramCollection, null, paramPublicKey, paramBoolean);
          break;
      } 
    } catch (CertPathValidatorException certPathValidatorException1) {
      if (certPathValidatorException1.getReason() == CertPathValidatorException.BasicReason.REVOKED)
        throw certPathValidatorException1; 
      boolean bool = isSoftFailException(certPathValidatorException1);
      if (bool) {
        if (this.mode == Mode.ONLY_OCSP || this.mode == Mode.ONLY_CRLS)
          return; 
      } else if (this.mode == Mode.ONLY_OCSP || this.mode == Mode.ONLY_CRLS) {
        throw certPathValidatorException1;
      } 
      CertPathValidatorException certPathValidatorException2 = certPathValidatorException1;
      if (debug != null) {
        debug.println("RevocationChecker.check() " + certPathValidatorException1.getMessage());
        debug.println("RevocationChecker.check() preparing to failover");
      } 
      try {
        switch (this.mode) {
          case PREFER_OCSP:
            checkCRLs(paramX509Certificate, paramCollection, null, paramPublicKey, paramBoolean);
            break;
          case PREFER_CRLS:
            checkOCSP(paramX509Certificate, paramCollection);
            break;
        } 
      } catch (CertPathValidatorException certPathValidatorException) {
        if (debug != null) {
          debug.println("RevocationChecker.check() failover failed");
          debug.println("RevocationChecker.check() " + certPathValidatorException.getMessage());
        } 
        if (certPathValidatorException.getReason() == CertPathValidatorException.BasicReason.REVOKED)
          throw certPathValidatorException; 
        if (!isSoftFailException(certPathValidatorException)) {
          certPathValidatorException2.addSuppressed(certPathValidatorException);
          throw certPathValidatorException2;
        } 
        if (!bool)
          throw certPathValidatorException2; 
      } 
    } finally {
      updateState(paramX509Certificate);
    } 
  }
  
  private boolean isSoftFailException(CertPathValidatorException paramCertPathValidatorException) {
    if (this.softFail && paramCertPathValidatorException.getReason() == CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS) {
      CertPathValidatorException certPathValidatorException = new CertPathValidatorException(paramCertPathValidatorException.getMessage(), paramCertPathValidatorException.getCause(), this.params.certPath(), this.certIndex, paramCertPathValidatorException.getReason());
      this.softFailExceptions.addFirst(certPathValidatorException);
      return true;
    } 
    return false;
  }
  
  private void updateState(X509Certificate paramX509Certificate) throws CertPathValidatorException {
    this.issuerInfo = new OCSPResponse.IssuerInfo(this.anchor, paramX509Certificate);
    PublicKey publicKey = paramX509Certificate.getPublicKey();
    if (PKIX.isDSAPublicKeyWithoutParams(publicKey))
      publicKey = BasicChecker.makeInheritedParamsKey(publicKey, this.prevPubKey); 
    this.prevPubKey = publicKey;
    this.crlSignFlag = certCanSignCrl(paramX509Certificate);
    if (this.certIndex > 0)
      this.certIndex--; 
  }
  
  private void checkCRLs(X509Certificate paramX509Certificate, Collection<String> paramCollection, Set<X509Certificate> paramSet, PublicKey paramPublicKey, boolean paramBoolean) throws CertPathValidatorException { checkCRLs(paramX509Certificate, paramPublicKey, null, paramBoolean, true, paramSet, this.params.trustAnchors()); }
  
  static boolean isCausedByNetworkIssue(String paramString, CertStoreException paramCertStoreException) {
    Throwable throwable = paramCertStoreException.getCause();
    switch (paramString) {
      case "LDAP":
        if (throwable != null) {
          String str = throwable.getClass().getName();
          null = (str.equals("javax.naming.ServiceUnavailableException") || str.equals("javax.naming.CommunicationException"));
        } else {
          null = false;
        } 
        return null;
      case "SSLServer":
        return (throwable != null && throwable instanceof IOException);
      case "URI":
        return (throwable != null && throwable instanceof IOException);
    } 
    return false;
  }
  
  private void checkCRLs(X509Certificate paramX509Certificate1, PublicKey paramPublicKey, X509Certificate paramX509Certificate2, boolean paramBoolean1, boolean paramBoolean2, Set<X509Certificate> paramSet1, Set<TrustAnchor> paramSet2) throws CertPathValidatorException {
    if (debug != null)
      debug.println("RevocationChecker.checkCRLs() ---checking revocation status ..."); 
    if (paramSet1 != null && paramSet1.contains(paramX509Certificate1)) {
      if (debug != null)
        debug.println("RevocationChecker.checkCRLs() circular dependency"); 
      throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
    } 
    HashSet hashSet1 = new HashSet();
    HashSet hashSet2 = new HashSet();
    X509CRLSelector x509CRLSelector = new X509CRLSelector();
    x509CRLSelector.setCertificateChecking(paramX509Certificate1);
    CertPathHelper.setDateAndTime(x509CRLSelector, this.params.date(), 900000L);
    CertPathValidatorException certPathValidatorException = null;
    for (CertStore certStore : this.certStores) {
      try {
        for (CRL cRL : certStore.getCRLs(x509CRLSelector))
          hashSet1.add((X509CRL)cRL); 
      } catch (CertStoreException certStoreException) {
        if (debug != null)
          debug.println("RevocationChecker.checkCRLs() CertStoreException: " + certStoreException.getMessage()); 
        if (certPathValidatorException == null && isCausedByNetworkIssue(certStore.getType(), certStoreException))
          certPathValidatorException = new CertPathValidatorException("Unable to determine revocation status due to network error", certStoreException, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS); 
      } 
    } 
    if (debug != null)
      debug.println("RevocationChecker.checkCRLs() possible crls.size() = " + hashSet1.size()); 
    boolean[] arrayOfBoolean = new boolean[9];
    if (!hashSet1.isEmpty())
      hashSet2.addAll(verifyPossibleCRLs(hashSet1, paramX509Certificate1, paramPublicKey, paramBoolean1, arrayOfBoolean, paramSet2)); 
    if (debug != null)
      debug.println("RevocationChecker.checkCRLs() approved crls.size() = " + hashSet2.size()); 
    if (!hashSet2.isEmpty() && Arrays.equals(arrayOfBoolean, ALL_REASONS)) {
      checkApprovedCRLs(paramX509Certificate1, hashSet2);
    } else {
      try {
        if (this.crlDP)
          hashSet2.addAll(DistributionPointFetcher.getCRLs(x509CRLSelector, paramBoolean1, paramPublicKey, paramX509Certificate2, this.params.sigProvider(), this.certStores, arrayOfBoolean, paramSet2, null, this.params.variant())); 
      } catch (CertStoreException certStoreException) {
        if (certStoreException instanceof PKIX.CertStoreTypeException) {
          PKIX.CertStoreTypeException certStoreTypeException = (PKIX.CertStoreTypeException)certStoreException;
          if (isCausedByNetworkIssue(certStoreTypeException.getType(), certStoreException))
            throw new CertPathValidatorException("Unable to determine revocation status due to network error", certStoreException, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS); 
        } 
        throw new CertPathValidatorException(certStoreException);
      } 
      if (!hashSet2.isEmpty() && Arrays.equals(arrayOfBoolean, ALL_REASONS)) {
        checkApprovedCRLs(paramX509Certificate1, hashSet2);
      } else {
        if (paramBoolean2)
          try {
            verifyWithSeparateSigningKey(paramX509Certificate1, paramPublicKey, paramBoolean1, paramSet1);
            return;
          } catch (CertPathValidatorException certPathValidatorException1) {
            if (certPathValidatorException != null)
              throw certPathValidatorException; 
            throw certPathValidatorException1;
          }  
        if (certPathValidatorException != null)
          throw certPathValidatorException; 
        throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
      } 
    } 
  }
  
  private void checkApprovedCRLs(X509Certificate paramX509Certificate, Set<X509CRL> paramSet) throws CertPathValidatorException {
    if (debug != null) {
      BigInteger bigInteger = paramX509Certificate.getSerialNumber();
      debug.println("RevocationChecker.checkApprovedCRLs() starting the final sweep...");
      debug.println("RevocationChecker.checkApprovedCRLs() cert SN: " + bigInteger.toString());
    } 
    CRLReason cRLReason = CRLReason.UNSPECIFIED;
    X509CRLEntryImpl x509CRLEntryImpl = null;
    for (X509CRL x509CRL : paramSet) {
      X509CRLEntry x509CRLEntry = x509CRL.getRevokedCertificate(paramX509Certificate);
      if (x509CRLEntry != null) {
        try {
          x509CRLEntryImpl = X509CRLEntryImpl.toImpl(x509CRLEntry);
        } catch (CRLException cRLException) {
          throw new CertPathValidatorException(cRLException);
        } 
        if (debug != null)
          debug.println("RevocationChecker.checkApprovedCRLs() CRL entry: " + x509CRLEntryImpl.toString()); 
        Set set = x509CRLEntryImpl.getCriticalExtensionOIDs();
        if (set != null && !set.isEmpty()) {
          set.remove(PKIXExtensions.ReasonCode_Id.toString());
          set.remove(PKIXExtensions.CertificateIssuer_Id.toString());
          if (!set.isEmpty())
            throw new CertPathValidatorException("Unrecognized critical extension(s) in revoked CRL entry"); 
        } 
        cRLReason = x509CRLEntryImpl.getRevocationReason();
        if (cRLReason == null)
          cRLReason = CRLReason.UNSPECIFIED; 
        Date date = x509CRLEntryImpl.getRevocationDate();
        if (date.before(this.params.date())) {
          CertificateRevokedException certificateRevokedException = new CertificateRevokedException(date, cRLReason, x509CRL.getIssuerX500Principal(), x509CRLEntryImpl.getExtensions());
          throw new CertPathValidatorException(certificateRevokedException.getMessage(), certificateRevokedException, null, -1, CertPathValidatorException.BasicReason.REVOKED);
        } 
      } 
    } 
  }
  
  private void checkOCSP(X509Certificate paramX509Certificate, Collection<String> paramCollection) throws CertPathValidatorException {
    X509CertImpl x509CertImpl = null;
    try {
      x509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
    } catch (CertificateException certificateException) {
      throw new CertPathValidatorException(certificateException);
    } 
    OCSPResponse oCSPResponse = null;
    CertId certId = null;
    try {
      certId = new CertId(this.issuerInfo.getName(), this.issuerInfo.getPublicKey(), x509CertImpl.getSerialNumberObject());
      byte[] arrayOfByte = (byte[])this.ocspResponses.get(paramX509Certificate);
      if (arrayOfByte != null) {
        if (debug != null)
          debug.println("Found cached OCSP response"); 
        oCSPResponse = new OCSPResponse(arrayOfByte);
        byte[] arrayOfByte1 = null;
        for (Extension extension : this.ocspExtensions) {
          if (extension.getId().equals("1.3.6.1.5.5.7.48.1.2"))
            arrayOfByte1 = extension.getValue(); 
        } 
        oCSPResponse.verify(Collections.singletonList(certId), this.issuerInfo, this.responderCert, this.params.date(), arrayOfByte1, this.params.variant());
      } else {
        URI uRI = (this.responderURI != null) ? this.responderURI : OCSP.getResponderURI(x509CertImpl);
        if (uRI == null)
          throw new CertPathValidatorException("Certificate does not specify OCSP responder", null, null, -1); 
        oCSPResponse = OCSP.check(Collections.singletonList(certId), uRI, this.issuerInfo, this.responderCert, null, this.ocspExtensions, this.params.variant());
      } 
    } catch (IOException iOException) {
      throw new CertPathValidatorException("Unable to determine revocation status due to network error", iOException, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
    } 
    OCSPResponse.SingleResponse singleResponse = oCSPResponse.getSingleResponse(certId);
    OCSP.RevocationStatus.CertStatus certStatus = singleResponse.getCertStatus();
    if (certStatus == OCSP.RevocationStatus.CertStatus.REVOKED) {
      Date date = singleResponse.getRevocationTime();
      if (date.before(this.params.date())) {
        CertificateRevokedException certificateRevokedException = new CertificateRevokedException(date, singleResponse.getRevocationReason(), oCSPResponse.getSignerCertificate().getSubjectX500Principal(), singleResponse.getSingleExtensions());
        throw new CertPathValidatorException(certificateRevokedException.getMessage(), certificateRevokedException, null, -1, CertPathValidatorException.BasicReason.REVOKED);
      } 
    } else if (certStatus == OCSP.RevocationStatus.CertStatus.UNKNOWN) {
      throw new CertPathValidatorException("Certificate's revocation status is unknown", null, this.params.certPath(), -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
    } 
  }
  
  private static String stripOutSeparators(String paramString) {
    char[] arrayOfChar = paramString.toCharArray();
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < arrayOfChar.length; b++) {
      if ("0123456789ABCDEFabcdef".indexOf(arrayOfChar[b]) != -1)
        stringBuilder.append(arrayOfChar[b]); 
    } 
    return stringBuilder.toString();
  }
  
  static boolean certCanSignCrl(X509Certificate paramX509Certificate) {
    boolean[] arrayOfBoolean = paramX509Certificate.getKeyUsage();
    return (arrayOfBoolean != null) ? arrayOfBoolean[6] : false;
  }
  
  private Collection<X509CRL> verifyPossibleCRLs(Set<X509CRL> paramSet1, X509Certificate paramX509Certificate, PublicKey paramPublicKey, boolean paramBoolean, boolean[] paramArrayOfBoolean, Set<TrustAnchor> paramSet2) throws CertPathValidatorException {
    try {
      X509CertImpl x509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
      if (debug != null)
        debug.println("RevocationChecker.verifyPossibleCRLs: Checking CRLDPs for " + x509CertImpl.getSubjectX500Principal()); 
      CRLDistributionPointsExtension cRLDistributionPointsExtension = x509CertImpl.getCRLDistributionPointsExtension();
      List list = null;
      if (cRLDistributionPointsExtension == null) {
        X500Name x500Name = (X500Name)x509CertImpl.getIssuerDN();
        DistributionPoint distributionPoint = new DistributionPoint((new GeneralNames()).add(new GeneralName(x500Name)), null, null);
        list = Collections.singletonList(distributionPoint);
      } else {
        list = cRLDistributionPointsExtension.get("points");
      } 
      HashSet hashSet = new HashSet();
      for (DistributionPoint distributionPoint : list) {
        for (X509CRL x509CRL : paramSet1) {
          if (DistributionPointFetcher.verifyCRL(x509CertImpl, distributionPoint, x509CRL, paramArrayOfBoolean, paramBoolean, paramPublicKey, null, this.params.sigProvider(), paramSet2, this.certStores, this.params.date(), this.params.variant()))
            hashSet.add(x509CRL); 
        } 
        if (Arrays.equals(paramArrayOfBoolean, ALL_REASONS))
          break; 
      } 
      return hashSet;
    } catch (CertificateException|CRLException|IOException certificateException) {
      if (debug != null) {
        debug.println("Exception while verifying CRL: " + certificateException.getMessage());
        certificateException.printStackTrace();
      } 
      return Collections.emptySet();
    } 
  }
  
  private void verifyWithSeparateSigningKey(X509Certificate paramX509Certificate, PublicKey paramPublicKey, boolean paramBoolean, Set<X509Certificate> paramSet) throws CertPathValidatorException {
    String str = "revocation status";
    if (debug != null)
      debug.println("RevocationChecker.verifyWithSeparateSigningKey() ---checking " + str + "..."); 
    if (paramSet != null && paramSet.contains(paramX509Certificate)) {
      if (debug != null)
        debug.println("RevocationChecker.verifyWithSeparateSigningKey() circular dependency"); 
      throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
    } 
    if (!paramBoolean) {
      buildToNewKey(paramX509Certificate, null, paramSet);
    } else {
      buildToNewKey(paramX509Certificate, paramPublicKey, paramSet);
    } 
  }
  
  private void buildToNewKey(X509Certificate paramX509Certificate, PublicKey paramPublicKey, Set<X509Certificate> paramSet) throws CertPathValidatorException {
    PKIXBuilderParameters pKIXBuilderParameters;
    if (debug != null)
      debug.println("RevocationChecker.buildToNewKey() starting work"); 
    HashSet hashSet = new HashSet();
    if (paramPublicKey != null)
      hashSet.add(paramPublicKey); 
    RejectKeySelector rejectKeySelector = new RejectKeySelector(hashSet);
    rejectKeySelector.setSubject(paramX509Certificate.getIssuerX500Principal());
    rejectKeySelector.setKeyUsage(CRL_SIGN_USAGE);
    Set set = (this.anchor == null) ? this.params.trustAnchors() : Collections.singleton(this.anchor);
    try {
      pKIXBuilderParameters = new PKIXBuilderParameters(set, rejectKeySelector);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new RuntimeException(invalidAlgorithmParameterException);
    } 
    pKIXBuilderParameters.setInitialPolicies(this.params.initialPolicies());
    pKIXBuilderParameters.setCertStores(this.certStores);
    pKIXBuilderParameters.setExplicitPolicyRequired(this.params.explicitPolicyRequired());
    pKIXBuilderParameters.setPolicyMappingInhibited(this.params.policyMappingInhibited());
    pKIXBuilderParameters.setAnyPolicyInhibited(this.params.anyPolicyInhibited());
    pKIXBuilderParameters.setDate(this.params.date());
    pKIXBuilderParameters.setCertPathCheckers(this.params.getPKIXParameters().getCertPathCheckers());
    pKIXBuilderParameters.setSigProvider(this.params.sigProvider());
    pKIXBuilderParameters.setRevocationEnabled(false);
    if (Builder.USE_AIA == true) {
      X509CertImpl x509CertImpl = null;
      try {
        x509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
      } catch (CertificateException certificateException) {
        if (debug != null)
          debug.println("RevocationChecker.buildToNewKey: error decoding cert: " + certificateException); 
      } 
      AuthorityInfoAccessExtension authorityInfoAccessExtension = null;
      if (x509CertImpl != null)
        authorityInfoAccessExtension = x509CertImpl.getAuthorityInfoAccessExtension(); 
      if (authorityInfoAccessExtension != null) {
        List list = authorityInfoAccessExtension.getAccessDescriptions();
        if (list != null)
          for (AccessDescription accessDescription : list) {
            CertStore certStore = URICertStore.getInstance(accessDescription);
            if (certStore != null) {
              if (debug != null)
                debug.println("adding AIAext CertStore"); 
              pKIXBuilderParameters.addCertStore(certStore);
            } 
          }  
      } 
    } 
    CertPathBuilder certPathBuilder = null;
    try {
      certPathBuilder = CertPathBuilder.getInstance("PKIX");
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new CertPathValidatorException(noSuchAlgorithmException);
    } 
    try {
      while (true) {
        if (debug != null)
          debug.println("RevocationChecker.buildToNewKey() about to try build ..."); 
        PKIXCertPathBuilderResult pKIXCertPathBuilderResult = (PKIXCertPathBuilderResult)certPathBuilder.build(pKIXBuilderParameters);
        if (debug != null)
          debug.println("RevocationChecker.buildToNewKey() about to check revocation ..."); 
        if (paramSet == null)
          paramSet = new HashSet<X509Certificate>(); 
        paramSet.add(paramX509Certificate);
        TrustAnchor trustAnchor = pKIXCertPathBuilderResult.getTrustAnchor();
        PublicKey publicKey1 = trustAnchor.getCAPublicKey();
        if (publicKey1 == null)
          publicKey1 = trustAnchor.getTrustedCert().getPublicKey(); 
        boolean bool = true;
        List list = pKIXCertPathBuilderResult.getCertPath().getCertificates();
        try {
          for (int i = list.size() - 1; i >= 0; i--) {
            X509Certificate x509Certificate1 = (X509Certificate)list.get(i);
            if (debug != null)
              debug.println("RevocationChecker.buildToNewKey() index " + i + " checking " + x509Certificate1); 
            checkCRLs(x509Certificate1, publicKey1, null, bool, true, paramSet, set);
            bool = certCanSignCrl(x509Certificate1);
            publicKey1 = x509Certificate1.getPublicKey();
          } 
        } catch (CertPathValidatorException certPathValidatorException) {
          hashSet.add(pKIXCertPathBuilderResult.getPublicKey());
          continue;
        } 
        if (debug != null)
          debug.println("RevocationChecker.buildToNewKey() got key " + pKIXCertPathBuilderResult.getPublicKey()); 
        PublicKey publicKey2 = pKIXCertPathBuilderResult.getPublicKey();
        X509Certificate x509Certificate = list.isEmpty() ? null : (X509Certificate)list.get(0);
        try {
          checkCRLs(paramX509Certificate, publicKey2, x509Certificate, true, false, null, this.params.trustAnchors());
          return;
        } catch (CertPathValidatorException certPathValidatorException) {
          if (certPathValidatorException.getReason() == CertPathValidatorException.BasicReason.REVOKED)
            throw certPathValidatorException; 
          hashSet.add(publicKey2);
        } 
      } 
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new CertPathValidatorException(invalidAlgorithmParameterException);
    } catch (CertPathBuilderException certPathBuilderException) {
      throw new CertPathValidatorException("Could not determine revocation status", null, null, -1, CertPathValidatorException.BasicReason.UNDETERMINED_REVOCATION_STATUS);
    } 
  }
  
  public RevocationChecker clone() {
    RevocationChecker revocationChecker = (RevocationChecker)super.clone();
    revocationChecker.softFailExceptions = new LinkedList(this.softFailExceptions);
    return revocationChecker;
  }
  
  private enum Mode {
    PREFER_OCSP, PREFER_CRLS, ONLY_CRLS, ONLY_OCSP;
  }
  
  private static class RejectKeySelector extends X509CertSelector {
    private final Set<PublicKey> badKeySet;
    
    RejectKeySelector(Set<PublicKey> param1Set) { this.badKeySet = param1Set; }
    
    public boolean match(Certificate param1Certificate) {
      if (!super.match(param1Certificate))
        return false; 
      if (this.badKeySet.contains(param1Certificate.getPublicKey())) {
        if (debug != null)
          debug.println("RejectKeySelector.match: bad key"); 
        return false;
      } 
      if (debug != null)
        debug.println("RejectKeySelector.match: returning true"); 
      return true;
    }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("RejectKeySelector: [\n");
      stringBuilder.append(super.toString());
      stringBuilder.append(this.badKeySet);
      stringBuilder.append("]");
      return stringBuilder.toString();
    }
  }
  
  private static class RevocationProperties {
    boolean onlyEE;
    
    boolean ocspEnabled;
    
    boolean crlDPEnabled;
    
    String ocspUrl;
    
    String ocspSubject;
    
    String ocspIssuer;
    
    String ocspSerial;
    
    private RevocationProperties() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\RevocationChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */