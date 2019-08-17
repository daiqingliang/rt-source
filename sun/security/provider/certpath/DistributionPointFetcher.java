package sun.security.provider.certpath;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.PublicKey;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertificateException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.CRLDistributionPointsExtension;
import sun.security.x509.DistributionPoint;
import sun.security.x509.DistributionPointName;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.GeneralNames;
import sun.security.x509.IssuingDistributionPointExtension;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.RDN;
import sun.security.x509.ReasonFlags;
import sun.security.x509.SerialNumber;
import sun.security.x509.URIName;
import sun.security.x509.X500Name;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;

public class DistributionPointFetcher {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private static final boolean[] ALL_REASONS = { true, true, true, true, true, true, true, true, true };
  
  public static Collection<X509CRL> getCRLs(X509CRLSelector paramX509CRLSelector, boolean paramBoolean, PublicKey paramPublicKey, String paramString1, List<CertStore> paramList, boolean[] paramArrayOfBoolean, Set<TrustAnchor> paramSet, Date paramDate, String paramString2) throws CertStoreException { return getCRLs(paramX509CRLSelector, paramBoolean, paramPublicKey, null, paramString1, paramList, paramArrayOfBoolean, paramSet, paramDate, paramString2); }
  
  public static Collection<X509CRL> getCRLs(X509CRLSelector paramX509CRLSelector, boolean paramBoolean, PublicKey paramPublicKey, String paramString, List<CertStore> paramList, boolean[] paramArrayOfBoolean, Set<TrustAnchor> paramSet, Date paramDate) throws CertStoreException { return getCRLs(paramX509CRLSelector, paramBoolean, paramPublicKey, null, paramString, paramList, paramArrayOfBoolean, paramSet, paramDate, "generic"); }
  
  public static Collection<X509CRL> getCRLs(X509CRLSelector paramX509CRLSelector, boolean paramBoolean, PublicKey paramPublicKey, X509Certificate paramX509Certificate, String paramString1, List<CertStore> paramList, boolean[] paramArrayOfBoolean, Set<TrustAnchor> paramSet, Date paramDate, String paramString2) throws CertStoreException {
    X509Certificate x509Certificate = paramX509CRLSelector.getCertificateChecking();
    if (x509Certificate == null)
      return Collections.emptySet(); 
    try {
      X509CertImpl x509CertImpl = X509CertImpl.toImpl(x509Certificate);
      if (debug != null)
        debug.println("DistributionPointFetcher.getCRLs: Checking CRLDPs for " + x509CertImpl.getSubjectX500Principal()); 
      CRLDistributionPointsExtension cRLDistributionPointsExtension = x509CertImpl.getCRLDistributionPointsExtension();
      if (cRLDistributionPointsExtension == null) {
        if (debug != null)
          debug.println("No CRLDP ext"); 
        return Collections.emptySet();
      } 
      List list = cRLDistributionPointsExtension.get("points");
      HashSet hashSet = new HashSet();
      Iterator iterator = list.iterator();
      while (iterator.hasNext() && !Arrays.equals(paramArrayOfBoolean, ALL_REASONS)) {
        DistributionPoint distributionPoint = (DistributionPoint)iterator.next();
        Collection collection = getCRLs(paramX509CRLSelector, x509CertImpl, distributionPoint, paramArrayOfBoolean, paramBoolean, paramPublicKey, paramX509Certificate, paramString1, paramList, paramSet, paramDate, paramString2);
        hashSet.addAll(collection);
      } 
      if (debug != null)
        debug.println("Returning " + hashSet.size() + " CRLs"); 
      return hashSet;
    } catch (CertificateException|IOException certificateException) {
      return Collections.emptySet();
    } 
  }
  
  private static Collection<X509CRL> getCRLs(X509CRLSelector paramX509CRLSelector, X509CertImpl paramX509CertImpl, DistributionPoint paramDistributionPoint, boolean[] paramArrayOfBoolean, boolean paramBoolean, PublicKey paramPublicKey, X509Certificate paramX509Certificate, String paramString1, List<CertStore> paramList, Set<TrustAnchor> paramSet, Date paramDate, String paramString2) throws CertStoreException {
    GeneralNames generalNames = paramDistributionPoint.getFullName();
    if (generalNames == null) {
      RDN rDN = paramDistributionPoint.getRelativeName();
      if (rDN == null)
        return Collections.emptySet(); 
      try {
        GeneralNames generalNames1 = paramDistributionPoint.getCRLIssuer();
        if (generalNames1 == null) {
          generalNames = getFullNames((X500Name)paramX509CertImpl.getIssuerDN(), rDN);
        } else {
          if (generalNames1.size() != 1)
            return Collections.emptySet(); 
          generalNames = getFullNames((X500Name)generalNames1.get(0).getName(), rDN);
        } 
      } catch (IOException iOException) {
        return Collections.emptySet();
      } 
    } 
    ArrayList arrayList1 = new ArrayList();
    CertStoreException certStoreException = null;
    Iterator iterator = generalNames.iterator();
    while (iterator.hasNext()) {
      try {
        GeneralName generalName = (GeneralName)iterator.next();
        if (generalName.getType() == 4) {
          X500Name x500Name = (X500Name)generalName.getName();
          arrayList1.addAll(getCRLs(x500Name, paramX509CertImpl.getIssuerX500Principal(), paramList));
          continue;
        } 
        if (generalName.getType() == 6) {
          URIName uRIName = (URIName)generalName.getName();
          X509CRL x509CRL = getCRL(uRIName);
          if (x509CRL != null)
            arrayList1.add(x509CRL); 
        } 
      } catch (CertStoreException certStoreException1) {
        certStoreException = certStoreException1;
      } 
    } 
    if (arrayList1.isEmpty() && certStoreException != null)
      throw certStoreException; 
    ArrayList arrayList2 = new ArrayList(2);
    for (X509CRL x509CRL : arrayList1) {
      try {
        paramX509CRLSelector.setIssuerNames(null);
        if (paramX509CRLSelector.match(x509CRL) && verifyCRL(paramX509CertImpl, paramDistributionPoint, x509CRL, paramArrayOfBoolean, paramBoolean, paramPublicKey, paramX509Certificate, paramString1, paramSet, paramList, paramDate, paramString2))
          arrayList2.add(x509CRL); 
      } catch (IOException|CRLException iOException) {
        if (debug != null) {
          debug.println("Exception verifying CRL: " + iOException.getMessage());
          iOException.printStackTrace();
        } 
      } 
    } 
    return arrayList2;
  }
  
  private static X509CRL getCRL(URIName paramURIName) throws CertStoreException {
    URI uRI = paramURIName.getURI();
    if (debug != null)
      debug.println("Trying to fetch CRL from DP " + uRI); 
    CertStore certStore = null;
    try {
      certStore = URICertStore.getInstance(new URICertStore.URICertStoreParameters(uRI));
    } catch (InvalidAlgorithmParameterException|java.security.NoSuchAlgorithmException invalidAlgorithmParameterException) {
      if (debug != null)
        debug.println("Can't create URICertStore: " + invalidAlgorithmParameterException.getMessage()); 
      return null;
    } 
    Collection collection = certStore.getCRLs(null);
    return collection.isEmpty() ? null : (X509CRL)collection.iterator().next();
  }
  
  private static Collection<X509CRL> getCRLs(X500Name paramX500Name, X500Principal paramX500Principal, List<CertStore> paramList) throws CertStoreException {
    if (debug != null)
      debug.println("Trying to fetch CRL from DP " + paramX500Name); 
    X509CRLSelector x509CRLSelector = new X509CRLSelector();
    x509CRLSelector.addIssuer(paramX500Name.asX500Principal());
    x509CRLSelector.addIssuer(paramX500Principal);
    ArrayList arrayList = new ArrayList();
    PKIX.CertStoreTypeException certStoreTypeException = null;
    for (CertStore certStore : paramList) {
      try {
        for (CRL cRL : certStore.getCRLs(x509CRLSelector))
          arrayList.add((X509CRL)cRL); 
      } catch (CertStoreException certStoreException) {
        if (debug != null) {
          debug.println("Exception while retrieving CRLs: " + certStoreException);
          certStoreException.printStackTrace();
        } 
        certStoreTypeException = new PKIX.CertStoreTypeException(certStore.getType(), certStoreException);
      } 
    } 
    if (arrayList.isEmpty() && certStoreTypeException != null)
      throw certStoreTypeException; 
    return arrayList;
  }
  
  static boolean verifyCRL(X509CertImpl paramX509CertImpl, DistributionPoint paramDistributionPoint, X509CRL paramX509CRL, boolean[] paramArrayOfBoolean, boolean paramBoolean, PublicKey paramPublicKey, X509Certificate paramX509Certificate, String paramString1, Set<TrustAnchor> paramSet, List<CertStore> paramList, Date paramDate, String paramString2) throws CRLException, IOException {
    if (debug != null)
      debug.println("DistributionPointFetcher.verifyCRL: checking revocation status for\n  SN: " + Debug.toHexString(paramX509CertImpl.getSerialNumber()) + "\n  Subject: " + paramX509CertImpl.getSubjectX500Principal() + "\n  Issuer: " + paramX509CertImpl.getIssuerX500Principal()); 
    boolean bool1 = false;
    X509CRLImpl x509CRLImpl = X509CRLImpl.toImpl(paramX509CRL);
    IssuingDistributionPointExtension issuingDistributionPointExtension = x509CRLImpl.getIssuingDistributionPointExtension();
    X500Name x500Name1 = (X500Name)paramX509CertImpl.getIssuerDN();
    X500Name x500Name2 = (X500Name)x509CRLImpl.getIssuerDN();
    GeneralNames generalNames = paramDistributionPoint.getCRLIssuer();
    X500Name x500Name3 = null;
    if (generalNames != null) {
      if (issuingDistributionPointExtension == null || ((Boolean)issuingDistributionPointExtension.get("indirect_crl")).equals(Boolean.FALSE))
        return false; 
      boolean bool = false;
      Iterator iterator = generalNames.iterator();
      while (!bool && iterator.hasNext()) {
        GeneralNameInterface generalNameInterface = ((GeneralName)iterator.next()).getName();
        if (x500Name2.equals(generalNameInterface) == true) {
          x500Name3 = (X500Name)generalNameInterface;
          bool = true;
        } 
      } 
      if (!bool)
        return false; 
      if (issues(paramX509CertImpl, x509CRLImpl, paramString1)) {
        paramPublicKey = paramX509CertImpl.getPublicKey();
      } else {
        bool1 = true;
      } 
    } else {
      if (!x500Name2.equals(x500Name1)) {
        if (debug != null)
          debug.println("crl issuer does not equal cert issuer.\ncrl issuer: " + x500Name2 + "\ncert issuer: " + x500Name1); 
        return false;
      } 
      KeyIdentifier keyIdentifier1 = paramX509CertImpl.getAuthKeyId();
      KeyIdentifier keyIdentifier2 = x509CRLImpl.getAuthKeyId();
      if (keyIdentifier1 == null || keyIdentifier2 == null) {
        if (issues(paramX509CertImpl, x509CRLImpl, paramString1))
          paramPublicKey = paramX509CertImpl.getPublicKey(); 
      } else if (!keyIdentifier1.equals(keyIdentifier2)) {
        if (issues(paramX509CertImpl, x509CRLImpl, paramString1)) {
          paramPublicKey = paramX509CertImpl.getPublicKey();
        } else {
          bool1 = true;
        } 
      } 
    } 
    if (!bool1 && !paramBoolean)
      return false; 
    if (issuingDistributionPointExtension != null) {
      DistributionPointName distributionPointName = (DistributionPointName)issuingDistributionPointExtension.get("point");
      if (distributionPointName != null) {
        GeneralNames generalNames1 = distributionPointName.getFullName();
        if (generalNames1 == null) {
          RDN rDN = distributionPointName.getRelativeName();
          if (rDN == null) {
            if (debug != null)
              debug.println("IDP must be relative or full DN"); 
            return false;
          } 
          if (debug != null)
            debug.println("IDP relativeName:" + rDN); 
          generalNames1 = getFullNames(x500Name2, rDN);
        } 
        if (paramDistributionPoint.getFullName() != null || paramDistributionPoint.getRelativeName() != null) {
          GeneralNames generalNames2 = paramDistributionPoint.getFullName();
          if (generalNames2 == null) {
            RDN rDN = paramDistributionPoint.getRelativeName();
            if (rDN == null) {
              if (debug != null)
                debug.println("DP must be relative or full DN"); 
              return false;
            } 
            if (debug != null)
              debug.println("DP relativeName:" + rDN); 
            if (bool1) {
              if (generalNames.size() != 1) {
                if (debug != null)
                  debug.println("must only be one CRL issuer when relative name present"); 
                return false;
              } 
              generalNames2 = getFullNames(x500Name3, rDN);
            } else {
              generalNames2 = getFullNames(x500Name1, rDN);
            } 
          } 
          boolean bool3 = false;
          Iterator iterator = generalNames1.iterator();
          while (!bool3 && iterator.hasNext()) {
            GeneralNameInterface generalNameInterface = ((GeneralName)iterator.next()).getName();
            if (debug != null)
              debug.println("idpName: " + generalNameInterface); 
            Iterator iterator1 = generalNames2.iterator();
            while (!bool3 && iterator1.hasNext()) {
              GeneralNameInterface generalNameInterface1 = ((GeneralName)iterator1.next()).getName();
              if (debug != null)
                debug.println("pointName: " + generalNameInterface1); 
              bool3 = generalNameInterface.equals(generalNameInterface1);
            } 
          } 
          if (!bool3) {
            if (debug != null)
              debug.println("IDP name does not match DP name"); 
            return false;
          } 
        } else {
          boolean bool3 = false;
          Iterator iterator = generalNames.iterator();
          while (!bool3 && iterator.hasNext()) {
            GeneralNameInterface generalNameInterface = ((GeneralName)iterator.next()).getName();
            Iterator iterator1 = generalNames1.iterator();
            while (!bool3 && iterator1.hasNext()) {
              GeneralNameInterface generalNameInterface1 = ((GeneralName)iterator1.next()).getName();
              bool3 = generalNameInterface.equals(generalNameInterface1);
            } 
          } 
          if (!bool3)
            return false; 
        } 
      } 
      Boolean bool = (Boolean)issuingDistributionPointExtension.get("only_user_certs");
      if (bool.equals(Boolean.TRUE) && paramX509CertImpl.getBasicConstraints() != -1) {
        if (debug != null)
          debug.println("cert must be a EE cert"); 
        return false;
      } 
      bool = (Boolean)issuingDistributionPointExtension.get("only_ca_certs");
      if (bool.equals(Boolean.TRUE) && paramX509CertImpl.getBasicConstraints() == -1) {
        if (debug != null)
          debug.println("cert must be a CA cert"); 
        return false;
      } 
      bool = (Boolean)issuingDistributionPointExtension.get("only_attribute_certs");
      if (bool.equals(Boolean.TRUE)) {
        if (debug != null)
          debug.println("cert must not be an AA cert"); 
        return false;
      } 
    } 
    boolean[] arrayOfBoolean1 = new boolean[9];
    ReasonFlags reasonFlags = null;
    if (issuingDistributionPointExtension != null)
      reasonFlags = (ReasonFlags)issuingDistributionPointExtension.get("reasons"); 
    boolean[] arrayOfBoolean2 = paramDistributionPoint.getReasonFlags();
    if (reasonFlags != null) {
      if (arrayOfBoolean2 != null) {
        boolean[] arrayOfBoolean = reasonFlags.getFlags();
        for (byte b = 0; b < arrayOfBoolean1.length; b++)
          arrayOfBoolean1[b] = (b < arrayOfBoolean.length && arrayOfBoolean[b] && b < arrayOfBoolean2.length && arrayOfBoolean2[b]); 
      } else {
        arrayOfBoolean1 = (boolean[])reasonFlags.getFlags().clone();
      } 
    } else if (issuingDistributionPointExtension == null || reasonFlags == null) {
      if (arrayOfBoolean2 != null) {
        arrayOfBoolean1 = (boolean[])arrayOfBoolean2.clone();
      } else {
        Arrays.fill(arrayOfBoolean1, true);
      } 
    } 
    boolean bool2 = false;
    for (b1 = 0; b1 < arrayOfBoolean1.length && !bool2; b1++) {
      if (arrayOfBoolean1[b1] && (b1 >= paramArrayOfBoolean.length || !paramArrayOfBoolean[b1]))
        bool2 = true; 
    } 
    if (!bool2)
      return false; 
    if (bool1) {
      X509CertSelector x509CertSelector = new X509CertSelector();
      x509CertSelector.setSubject(x500Name2.asX500Principal());
      boolean[] arrayOfBoolean = { false, false, false, false, false, false, true };
      x509CertSelector.setKeyUsage(arrayOfBoolean);
      AuthorityKeyIdentifierExtension authorityKeyIdentifierExtension = x509CRLImpl.getAuthKeyIdExtension();
      if (authorityKeyIdentifierExtension != null) {
        byte[] arrayOfByte = authorityKeyIdentifierExtension.getEncodedKeyIdentifier();
        if (arrayOfByte != null)
          x509CertSelector.setSubjectKeyIdentifier(arrayOfByte); 
        SerialNumber serialNumber = (SerialNumber)authorityKeyIdentifierExtension.get("serial_number");
        if (serialNumber != null)
          x509CertSelector.setSerialNumber(serialNumber.getNumber()); 
      } 
      HashSet hashSet = new HashSet(paramSet);
      if (paramPublicKey != null) {
        TrustAnchor trustAnchor;
        if (paramX509Certificate != null) {
          trustAnchor = new TrustAnchor(paramX509Certificate, null);
        } else {
          X500Principal x500Principal = paramX509CertImpl.getIssuerX500Principal();
          trustAnchor = new TrustAnchor(x500Principal, paramPublicKey, null);
        } 
        hashSet.add(trustAnchor);
      } 
      PKIXBuilderParameters pKIXBuilderParameters = null;
      try {
        pKIXBuilderParameters = new PKIXBuilderParameters(hashSet, x509CertSelector);
      } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
        throw new CRLException(invalidAlgorithmParameterException);
      } 
      pKIXBuilderParameters.setCertStores(paramList);
      pKIXBuilderParameters.setSigProvider(paramString1);
      pKIXBuilderParameters.setDate(paramDate);
      try {
        CertPathBuilder certPathBuilder = CertPathBuilder.getInstance("PKIX");
        PKIXCertPathBuilderResult pKIXCertPathBuilderResult = (PKIXCertPathBuilderResult)certPathBuilder.build(pKIXBuilderParameters);
        paramPublicKey = pKIXCertPathBuilderResult.getPublicKey();
      } catch (GeneralSecurityException generalSecurityException) {
        throw new CRLException(generalSecurityException);
      } 
    } 
    try {
      AlgorithmChecker.check(paramPublicKey, paramX509CRL, paramString2);
    } catch (CertPathValidatorException b1) {
      CertPathValidatorException certPathValidatorException;
      if (debug != null)
        debug.println("CRL signature algorithm check failed: " + certPathValidatorException); 
      return false;
    } 
    try {
      paramX509CRL.verify(paramPublicKey, paramString1);
    } catch (GeneralSecurityException b1) {
      GeneralSecurityException generalSecurityException;
      if (debug != null)
        debug.println("CRL signature failed to verify"); 
      return false;
    } 
    Set set = paramX509CRL.getCriticalExtensionOIDs();
    if (set != null) {
      set.remove(PKIXExtensions.IssuingDistributionPoint_Id.toString());
      if (!set.isEmpty()) {
        if (debug != null) {
          debug.println("Unrecognized critical extension(s) in CRL: " + set);
          for (String str : set)
            debug.println(str); 
        } 
        return false;
      } 
    } 
    for (byte b2 = 0; b2 < paramArrayOfBoolean.length; b2++)
      paramArrayOfBoolean[b2] = (paramArrayOfBoolean[b2] || (b2 < arrayOfBoolean1.length && arrayOfBoolean1[b2])); 
    return true;
  }
  
  private static GeneralNames getFullNames(X500Name paramX500Name, RDN paramRDN) throws IOException {
    ArrayList arrayList = new ArrayList(paramX500Name.rdns());
    arrayList.add(paramRDN);
    X500Name x500Name = new X500Name((RDN[])arrayList.toArray(new RDN[0]));
    GeneralNames generalNames = new GeneralNames();
    generalNames.add(new GeneralName(x500Name));
    return generalNames;
  }
  
  private static boolean issues(X509CertImpl paramX509CertImpl, X509CRLImpl paramX509CRLImpl, String paramString) throws IOException {
    boolean bool = false;
    AdaptableX509CertSelector adaptableX509CertSelector = new AdaptableX509CertSelector();
    boolean[] arrayOfBoolean = paramX509CertImpl.getKeyUsage();
    if (arrayOfBoolean != null) {
      arrayOfBoolean[6] = true;
      adaptableX509CertSelector.setKeyUsage(arrayOfBoolean);
    } 
    X500Principal x500Principal = paramX509CRLImpl.getIssuerX500Principal();
    adaptableX509CertSelector.setSubject(x500Principal);
    AuthorityKeyIdentifierExtension authorityKeyIdentifierExtension = paramX509CRLImpl.getAuthKeyIdExtension();
    adaptableX509CertSelector.setSkiAndSerialNumber(authorityKeyIdentifierExtension);
    bool = adaptableX509CertSelector.match(paramX509CertImpl);
    if (bool && (authorityKeyIdentifierExtension == null || paramX509CertImpl.getAuthorityKeyIdentifierExtension() == null))
      try {
        paramX509CRLImpl.verify(paramX509CertImpl.getPublicKey(), paramString);
        bool = true;
      } catch (GeneralSecurityException generalSecurityException) {
        bool = false;
      }  
    return bool;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\DistributionPointFetcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */