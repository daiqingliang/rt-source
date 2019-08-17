package sun.security.provider.certpath;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.AccessDescription;
import sun.security.x509.AuthorityInfoAccessExtension;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;

class ForwardBuilder extends Builder {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private final Set<X509Certificate> trustedCerts;
  
  private final Set<X500Principal> trustedSubjectDNs;
  
  private final Set<TrustAnchor> trustAnchors;
  
  private X509CertSelector eeSelector;
  
  private AdaptableX509CertSelector caSelector;
  
  private X509CertSelector caTargetSelector;
  
  TrustAnchor trustAnchor;
  
  private boolean searchAllCertStores = true;
  
  ForwardBuilder(PKIX.BuilderParams paramBuilderParams, boolean paramBoolean) {
    super(paramBuilderParams);
    this.trustAnchors = paramBuilderParams.trustAnchors();
    this.trustedCerts = new HashSet(this.trustAnchors.size());
    this.trustedSubjectDNs = new HashSet(this.trustAnchors.size());
    for (TrustAnchor trustAnchor1 : this.trustAnchors) {
      X509Certificate x509Certificate = trustAnchor1.getTrustedCert();
      if (x509Certificate != null) {
        this.trustedCerts.add(x509Certificate);
        this.trustedSubjectDNs.add(x509Certificate.getSubjectX500Principal());
        continue;
      } 
      this.trustedSubjectDNs.add(trustAnchor1.getCA());
    } 
    this.searchAllCertStores = paramBoolean;
  }
  
  Collection<X509Certificate> getMatchingCerts(State paramState, List<CertStore> paramList) throws CertStoreException, CertificateException, IOException {
    if (debug != null)
      debug.println("ForwardBuilder.getMatchingCerts()..."); 
    ForwardState forwardState = (ForwardState)paramState;
    PKIXCertComparator pKIXCertComparator = new PKIXCertComparator(this.trustedSubjectDNs, forwardState.cert);
    TreeSet treeSet = new TreeSet(pKIXCertComparator);
    if (forwardState.isInitial())
      getMatchingEECerts(forwardState, paramList, treeSet); 
    getMatchingCACerts(forwardState, paramList, treeSet);
    return treeSet;
  }
  
  private void getMatchingEECerts(ForwardState paramForwardState, List<CertStore> paramList, Collection<X509Certificate> paramCollection) throws IOException {
    if (debug != null)
      debug.println("ForwardBuilder.getMatchingEECerts()..."); 
    if (this.eeSelector == null) {
      this.eeSelector = (X509CertSelector)this.targetCertConstraints.clone();
      this.eeSelector.setCertificateValid(this.buildParams.date());
      if (this.buildParams.explicitPolicyRequired())
        this.eeSelector.setPolicy(getMatchingPolicies()); 
      this.eeSelector.setBasicConstraints(-2);
    } 
    addMatchingCerts(this.eeSelector, paramList, paramCollection, this.searchAllCertStores);
  }
  
  private void getMatchingCACerts(ForwardState paramForwardState, List<CertStore> paramList, Collection<X509Certificate> paramCollection) throws IOException {
    if (debug != null)
      debug.println("ForwardBuilder.getMatchingCACerts()..."); 
    int i = paramCollection.size();
    X509CertSelector x509CertSelector = null;
    if (paramForwardState.isInitial()) {
      if (this.targetCertConstraints.getBasicConstraints() == -2)
        return; 
      if (debug != null)
        debug.println("ForwardBuilder.getMatchingCACerts(): the target is a CA"); 
      if (this.caTargetSelector == null) {
        this.caTargetSelector = (X509CertSelector)this.targetCertConstraints.clone();
        if (this.buildParams.explicitPolicyRequired())
          this.caTargetSelector.setPolicy(getMatchingPolicies()); 
      } 
      x509CertSelector = this.caTargetSelector;
    } else {
      if (this.caSelector == null) {
        this.caSelector = new AdaptableX509CertSelector();
        if (this.buildParams.explicitPolicyRequired())
          this.caSelector.setPolicy(getMatchingPolicies()); 
      } 
      this.caSelector.setSubject(paramForwardState.issuerDN);
      CertPathHelper.setPathToNames(this.caSelector, paramForwardState.subjectNamesTraversed);
      this.caSelector.setValidityPeriod(paramForwardState.cert.getNotBefore(), paramForwardState.cert.getNotAfter());
      x509CertSelector = this.caSelector;
    } 
    x509CertSelector.setBasicConstraints(-1);
    for (X509Certificate x509Certificate : this.trustedCerts) {
      if (x509CertSelector.match(x509Certificate)) {
        if (debug != null)
          debug.println("ForwardBuilder.getMatchingCACerts: found matching trust anchor.\n  SN: " + Debug.toHexString(x509Certificate.getSerialNumber()) + "\n  Subject: " + x509Certificate.getSubjectX500Principal() + "\n  Issuer: " + x509Certificate.getIssuerX500Principal()); 
        if (paramCollection.add(x509Certificate) && !this.searchAllCertStores)
          return; 
      } 
    } 
    x509CertSelector.setCertificateValid(this.buildParams.date());
    x509CertSelector.setBasicConstraints(paramForwardState.traversedCACerts);
    if ((paramForwardState.isInitial() || this.buildParams.maxPathLength() == -1 || this.buildParams.maxPathLength() > paramForwardState.traversedCACerts) && addMatchingCerts(x509CertSelector, paramList, paramCollection, this.searchAllCertStores) && !this.searchAllCertStores)
      return; 
    if (!paramForwardState.isInitial() && Builder.USE_AIA) {
      AuthorityInfoAccessExtension authorityInfoAccessExtension = paramForwardState.cert.getAuthorityInfoAccessExtension();
      if (authorityInfoAccessExtension != null)
        getCerts(authorityInfoAccessExtension, paramCollection); 
    } 
    if (debug != null) {
      int j = paramCollection.size() - i;
      debug.println("ForwardBuilder.getMatchingCACerts: found " + j + " CA certs");
    } 
  }
  
  private boolean getCerts(AuthorityInfoAccessExtension paramAuthorityInfoAccessExtension, Collection<X509Certificate> paramCollection) {
    if (!Builder.USE_AIA)
      return false; 
    List list = paramAuthorityInfoAccessExtension.getAccessDescriptions();
    if (list == null || list.isEmpty())
      return false; 
    boolean bool = false;
    for (AccessDescription accessDescription : list) {
      CertStore certStore = URICertStore.getInstance(accessDescription);
      if (certStore != null)
        try {
          if (paramCollection.addAll(certStore.getCertificates(this.caSelector))) {
            bool = true;
            if (!this.searchAllCertStores)
              return true; 
          } 
        } catch (CertStoreException certStoreException) {
          if (debug != null) {
            debug.println("exception getting certs from CertStore:");
            certStoreException.printStackTrace();
          } 
        }  
    } 
    return bool;
  }
  
  void verifyCert(X509Certificate paramX509Certificate, State paramState, List<X509Certificate> paramList) throws GeneralSecurityException {
    if (debug != null)
      debug.println("ForwardBuilder.verifyCert(SN: " + Debug.toHexString(paramX509Certificate.getSerialNumber()) + "\n  Issuer: " + paramX509Certificate.getIssuerX500Principal() + ")\n  Subject: " + paramX509Certificate.getSubjectX500Principal() + ")"); 
    ForwardState forwardState = (ForwardState)paramState;
    forwardState.untrustedChecker.check(paramX509Certificate, Collections.emptySet());
    if (paramList != null)
      for (X509Certificate x509Certificate : paramList) {
        if (paramX509Certificate.equals(x509Certificate)) {
          if (debug != null)
            debug.println("loop detected!!"); 
          throw new CertPathValidatorException("loop detected");
        } 
      }  
    boolean bool = this.trustedCerts.contains(paramX509Certificate);
    if (!bool) {
      Set set = paramX509Certificate.getCriticalExtensionOIDs();
      if (set == null)
        set = Collections.emptySet(); 
      for (PKIXCertPathChecker pKIXCertPathChecker : forwardState.forwardCheckers)
        pKIXCertPathChecker.check(paramX509Certificate, set); 
      for (PKIXCertPathChecker pKIXCertPathChecker : this.buildParams.certPathCheckers()) {
        if (!pKIXCertPathChecker.isForwardCheckingSupported()) {
          Set set1 = pKIXCertPathChecker.getSupportedExtensions();
          if (set1 != null)
            set.removeAll(set1); 
        } 
      } 
      if (!set.isEmpty()) {
        set.remove(PKIXExtensions.BasicConstraints_Id.toString());
        set.remove(PKIXExtensions.NameConstraints_Id.toString());
        set.remove(PKIXExtensions.CertificatePolicies_Id.toString());
        set.remove(PKIXExtensions.PolicyMappings_Id.toString());
        set.remove(PKIXExtensions.PolicyConstraints_Id.toString());
        set.remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
        set.remove(PKIXExtensions.SubjectAlternativeName_Id.toString());
        set.remove(PKIXExtensions.KeyUsage_Id.toString());
        set.remove(PKIXExtensions.ExtendedKeyUsage_Id.toString());
        if (!set.isEmpty())
          throw new CertPathValidatorException("Unrecognized critical extension(s)", null, null, -1, PKIXReason.UNRECOGNIZED_CRIT_EXT); 
      } 
    } 
    if (forwardState.isInitial())
      return; 
    if (!bool) {
      if (paramX509Certificate.getBasicConstraints() == -1)
        throw new CertificateException("cert is NOT a CA cert"); 
      KeyChecker.verifyCAKeyUsage(paramX509Certificate);
    } 
    if (!forwardState.keyParamsNeeded())
      forwardState.cert.verify(paramX509Certificate.getPublicKey(), this.buildParams.sigProvider()); 
  }
  
  boolean isPathCompleted(X509Certificate paramX509Certificate) {
    ArrayList arrayList = new ArrayList();
    for (TrustAnchor trustAnchor1 : this.trustAnchors) {
      if (trustAnchor1.getTrustedCert() != null) {
        if (paramX509Certificate.equals(trustAnchor1.getTrustedCert())) {
          this.trustAnchor = trustAnchor1;
          return true;
        } 
        continue;
      } 
      X500Principal x500Principal = trustAnchor1.getCA();
      PublicKey publicKey = trustAnchor1.getCAPublicKey();
      if (x500Principal != null && publicKey != null && x500Principal.equals(paramX509Certificate.getSubjectX500Principal()) && publicKey.equals(paramX509Certificate.getPublicKey())) {
        this.trustAnchor = trustAnchor1;
        return true;
      } 
      arrayList.add(trustAnchor1);
    } 
    for (TrustAnchor trustAnchor1 : arrayList) {
      X500Principal x500Principal = trustAnchor1.getCA();
      PublicKey publicKey = trustAnchor1.getCAPublicKey();
      if (x500Principal == null || !x500Principal.equals(paramX509Certificate.getIssuerX500Principal()) || PKIX.isDSAPublicKeyWithoutParams(publicKey))
        continue; 
      try {
        paramX509Certificate.verify(publicKey, this.buildParams.sigProvider());
      } catch (InvalidKeyException invalidKeyException) {
        if (debug != null)
          debug.println("ForwardBuilder.isPathCompleted() invalid DSA key found"); 
        continue;
      } catch (GeneralSecurityException generalSecurityException) {
        if (debug != null) {
          debug.println("ForwardBuilder.isPathCompleted() unexpected exception");
          generalSecurityException.printStackTrace();
        } 
        continue;
      } 
      this.trustAnchor = trustAnchor1;
      return true;
    } 
    return false;
  }
  
  void addCertToPath(X509Certificate paramX509Certificate, LinkedList<X509Certificate> paramLinkedList) { paramLinkedList.addFirst(paramX509Certificate); }
  
  void removeFinalCertFromPath(LinkedList<X509Certificate> paramLinkedList) { paramLinkedList.removeFirst(); }
  
  static class PKIXCertComparator extends Object implements Comparator<X509Certificate> {
    static final String METHOD_NME = "PKIXCertComparator.compare()";
    
    private final Set<X500Principal> trustedSubjectDNs;
    
    private final X509CertSelector certSkidSelector;
    
    PKIXCertComparator(Set<X500Principal> param1Set, X509CertImpl param1X509CertImpl) throws IOException {
      this.trustedSubjectDNs = param1Set;
      this.certSkidSelector = getSelector(param1X509CertImpl);
    }
    
    private X509CertSelector getSelector(X509CertImpl param1X509CertImpl) throws IOException {
      if (param1X509CertImpl != null) {
        AuthorityKeyIdentifierExtension authorityKeyIdentifierExtension = param1X509CertImpl.getAuthorityKeyIdentifierExtension();
        if (authorityKeyIdentifierExtension != null) {
          byte[] arrayOfByte = authorityKeyIdentifierExtension.getEncodedKeyIdentifier();
          if (arrayOfByte != null) {
            X509CertSelector x509CertSelector = new X509CertSelector();
            x509CertSelector.setSubjectKeyIdentifier(arrayOfByte);
            return x509CertSelector;
          } 
        } 
      } 
      return null;
    }
    
    public int compare(X509Certificate param1X509Certificate1, X509Certificate param1X509Certificate2) {
      if (param1X509Certificate1.equals(param1X509Certificate2))
        return 0; 
      if (this.certSkidSelector != null) {
        if (this.certSkidSelector.match(param1X509Certificate1))
          return -1; 
        if (this.certSkidSelector.match(param1X509Certificate2))
          return 1; 
      } 
      X500Principal x500Principal1 = param1X509Certificate1.getIssuerX500Principal();
      X500Principal x500Principal2 = param1X509Certificate2.getIssuerX500Principal();
      X500Name x500Name1;
      X500Name x500Name2 = (x500Name1 = X500Name.asX500Name(x500Principal1)).asX500Name(x500Principal2);
      if (debug != null) {
        debug.println("PKIXCertComparator.compare() o1 Issuer:  " + x500Principal1);
        debug.println("PKIXCertComparator.compare() o2 Issuer:  " + x500Principal2);
      } 
      if (debug != null)
        debug.println("PKIXCertComparator.compare() MATCH TRUSTED SUBJECT TEST..."); 
      boolean bool1 = this.trustedSubjectDNs.contains(x500Principal1);
      boolean bool2 = this.trustedSubjectDNs.contains(x500Principal2);
      if (debug != null) {
        debug.println("PKIXCertComparator.compare() m1: " + bool1);
        debug.println("PKIXCertComparator.compare() m2: " + bool2);
      } 
      if (bool1 && bool2)
        return -1; 
      if (bool1)
        return -1; 
      if (bool2)
        return 1; 
      if (debug != null)
        debug.println("PKIXCertComparator.compare() NAMING DESCENDANT TEST..."); 
      for (X500Principal x500Principal : this.trustedSubjectDNs) {
        X500Name x500Name = X500Name.asX500Name(x500Principal);
        int k = Builder.distance(x500Name, x500Name1, -1);
        int m = Builder.distance(x500Name, x500Name2, -1);
        if (debug != null) {
          debug.println("PKIXCertComparator.compare() distanceTto1: " + k);
          debug.println("PKIXCertComparator.compare() distanceTto2: " + m);
        } 
        if (k > 0 || m > 0)
          return (k == m) ? -1 : ((k > 0 && m <= 0) ? -1 : ((k <= 0 && m > 0) ? 1 : ((k < m) ? -1 : 1))); 
      } 
      if (debug != null)
        debug.println("PKIXCertComparator.compare() NAMING ANCESTOR TEST..."); 
      for (X500Principal x500Principal : this.trustedSubjectDNs) {
        X500Name x500Name = X500Name.asX500Name(x500Principal);
        int k = Builder.distance(x500Name, x500Name1, 2147483647);
        int m = Builder.distance(x500Name, x500Name2, 2147483647);
        if (debug != null) {
          debug.println("PKIXCertComparator.compare() distanceTto1: " + k);
          debug.println("PKIXCertComparator.compare() distanceTto2: " + m);
        } 
        if (k < 0 || m < 0)
          return (k == m) ? -1 : ((k < 0 && m >= 0) ? -1 : ((k >= 0 && m < 0) ? 1 : ((k > m) ? -1 : 1))); 
      } 
      if (debug != null)
        debug.println("PKIXCertComparator.compare() SAME NAMESPACE AS TRUSTED TEST..."); 
      for (X500Principal x500Principal : this.trustedSubjectDNs) {
        X500Name x500Name5 = X500Name.asX500Name(x500Principal);
        X500Name x500Name6 = x500Name5.commonAncestor(x500Name1);
        X500Name x500Name7 = x500Name5.commonAncestor(x500Name2);
        if (debug != null) {
          debug.println("PKIXCertComparator.compare() tAo1: " + String.valueOf(x500Name6));
          debug.println("PKIXCertComparator.compare() tAo2: " + String.valueOf(x500Name7));
        } 
        if (x500Name6 != null || x500Name7 != null) {
          if (x500Name6 != null && x500Name7 != null) {
            int k = Builder.hops(x500Name5, x500Name1, 2147483647);
            int m = Builder.hops(x500Name5, x500Name2, 2147483647);
            if (debug != null) {
              debug.println("PKIXCertComparator.compare() hopsTto1: " + k);
              debug.println("PKIXCertComparator.compare() hopsTto2: " + m);
            } 
            if (k == m)
              continue; 
            return (k > m) ? 1 : -1;
          } 
          return (x500Name6 == null) ? 1 : -1;
        } 
      } 
      if (debug != null)
        debug.println("PKIXCertComparator.compare() CERT ISSUER/SUBJECT COMPARISON TEST..."); 
      X500Principal x500Principal3 = param1X509Certificate1.getSubjectX500Principal();
      X500Principal x500Principal4 = param1X509Certificate2.getSubjectX500Principal();
      X500Name x500Name3;
      X500Name x500Name4 = (x500Name3 = X500Name.asX500Name(x500Principal3)).asX500Name(x500Principal4);
      if (debug != null) {
        debug.println("PKIXCertComparator.compare() o1 Subject: " + x500Principal3);
        debug.println("PKIXCertComparator.compare() o2 Subject: " + x500Principal4);
      } 
      int i = Builder.distance(x500Name3, x500Name1, 2147483647);
      int j = Builder.distance(x500Name4, x500Name2, 2147483647);
      if (debug != null) {
        debug.println("PKIXCertComparator.compare() distanceStoI1: " + i);
        debug.println("PKIXCertComparator.compare() distanceStoI2: " + j);
      } 
      if (j > i)
        return -1; 
      if (j < i)
        return 1; 
      if (debug != null)
        debug.println("PKIXCertComparator.compare() no tests matched; RETURN 0"); 
      return -1;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\ForwardBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */