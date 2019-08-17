package sun.security.provider.certpath;

import java.io.IOException;
import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import sun.security.action.GetBooleanAction;
import sun.security.util.Debug;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.GeneralNames;
import sun.security.x509.GeneralSubtrees;
import sun.security.x509.NameConstraintsExtension;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;

public abstract class Builder {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private Set<String> matchingPolicies;
  
  final PKIX.BuilderParams buildParams;
  
  final X509CertSelector targetCertConstraints;
  
  static final boolean USE_AIA = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("com.sun.security.enableAIAcaIssuers"))).booleanValue();
  
  Builder(PKIX.BuilderParams paramBuilderParams) {
    this.buildParams = paramBuilderParams;
    this.targetCertConstraints = (X509CertSelector)paramBuilderParams.targetCertConstraints();
  }
  
  abstract Collection<X509Certificate> getMatchingCerts(State paramState, List<CertStore> paramList) throws CertStoreException, CertificateException, IOException;
  
  abstract void verifyCert(X509Certificate paramX509Certificate, State paramState, List<X509Certificate> paramList) throws GeneralSecurityException;
  
  abstract boolean isPathCompleted(X509Certificate paramX509Certificate);
  
  abstract void addCertToPath(X509Certificate paramX509Certificate, LinkedList<X509Certificate> paramLinkedList);
  
  abstract void removeFinalCertFromPath(LinkedList<X509Certificate> paramLinkedList);
  
  static int distance(GeneralNameInterface paramGeneralNameInterface1, GeneralNameInterface paramGeneralNameInterface2, int paramInt) {
    switch (paramGeneralNameInterface1.constrains(paramGeneralNameInterface2)) {
      case -1:
        if (debug != null)
          debug.println("Builder.distance(): Names are different types"); 
        return paramInt;
      case 3:
        if (debug != null)
          debug.println("Builder.distance(): Names are same type but in different subtrees"); 
        return paramInt;
      case 0:
        return 0;
      case 2:
      case 1:
        return paramGeneralNameInterface2.subtreeDepth() - paramGeneralNameInterface1.subtreeDepth();
    } 
    return paramInt;
  }
  
  static int hops(GeneralNameInterface paramGeneralNameInterface1, GeneralNameInterface paramGeneralNameInterface2, int paramInt) {
    int i = paramGeneralNameInterface1.constrains(paramGeneralNameInterface2);
    switch (i) {
      case -1:
        if (debug != null)
          debug.println("Builder.hops(): Names are different types"); 
        return paramInt;
      case 3:
        break;
      case 0:
        return 0;
      case 2:
        return paramGeneralNameInterface2.subtreeDepth() - paramGeneralNameInterface1.subtreeDepth();
      case 1:
        return paramGeneralNameInterface2.subtreeDepth() - paramGeneralNameInterface1.subtreeDepth();
      default:
        return paramInt;
    } 
    if (paramGeneralNameInterface1.getType() != 4) {
      if (debug != null)
        debug.println("Builder.hops(): hopDistance not implemented for this name type"); 
      return paramInt;
    } 
    X500Name x500Name1 = (X500Name)paramGeneralNameInterface1;
    X500Name x500Name2 = (X500Name)paramGeneralNameInterface2;
    X500Name x500Name3 = x500Name1.commonAncestor(x500Name2);
    if (x500Name3 == null) {
      if (debug != null)
        debug.println("Builder.hops(): Names are in different namespaces"); 
      return paramInt;
    } 
    int j = x500Name3.subtreeDepth();
    int k = x500Name1.subtreeDepth();
    int m = x500Name2.subtreeDepth();
    return k + m - 2 * j;
  }
  
  static int targetDistance(NameConstraintsExtension paramNameConstraintsExtension, X509Certificate paramX509Certificate, GeneralNameInterface paramGeneralNameInterface) throws IOException {
    X509CertImpl x509CertImpl;
    if (paramNameConstraintsExtension != null && !paramNameConstraintsExtension.verify(paramX509Certificate))
      throw new IOException("certificate does not satisfy existing name constraints"); 
    try {
      x509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
    } catch (CertificateException certificateException) {
      throw new IOException("Invalid certificate", certificateException);
    } 
    X500Name x500Name = X500Name.asX500Name(x509CertImpl.getSubjectX500Principal());
    if (x500Name.equals(paramGeneralNameInterface))
      return 0; 
    SubjectAlternativeNameExtension subjectAlternativeNameExtension = x509CertImpl.getSubjectAlternativeNameExtension();
    if (subjectAlternativeNameExtension != null) {
      GeneralNames generalNames = subjectAlternativeNameExtension.get("subject_name");
      if (generalNames != null) {
        byte b1 = 0;
        int j = generalNames.size();
        while (b1 < j) {
          GeneralNameInterface generalNameInterface = generalNames.get(b1).getName();
          if (generalNameInterface.equals(paramGeneralNameInterface))
            return 0; 
          b1++;
        } 
      } 
    } 
    NameConstraintsExtension nameConstraintsExtension = x509CertImpl.getNameConstraintsExtension();
    if (nameConstraintsExtension == null)
      return -1; 
    if (paramNameConstraintsExtension != null) {
      paramNameConstraintsExtension.merge(nameConstraintsExtension);
    } else {
      paramNameConstraintsExtension = (NameConstraintsExtension)nameConstraintsExtension.clone();
    } 
    if (debug != null)
      debug.println("Builder.targetDistance() merged constraints: " + String.valueOf(paramNameConstraintsExtension)); 
    GeneralSubtrees generalSubtrees1 = paramNameConstraintsExtension.get("permitted_subtrees");
    GeneralSubtrees generalSubtrees2 = paramNameConstraintsExtension.get("excluded_subtrees");
    if (generalSubtrees1 != null)
      generalSubtrees1.reduce(generalSubtrees2); 
    if (debug != null)
      debug.println("Builder.targetDistance() reduced constraints: " + generalSubtrees1); 
    if (!paramNameConstraintsExtension.verify(paramGeneralNameInterface))
      throw new IOException("New certificate not allowed to sign certificate for target"); 
    if (generalSubtrees1 == null)
      return -1; 
    byte b = 0;
    int i = generalSubtrees1.size();
    while (b < i) {
      GeneralNameInterface generalNameInterface = generalSubtrees1.get(b).getName().getName();
      int j = distance(generalNameInterface, paramGeneralNameInterface, -1);
      if (j >= 0)
        return j + 1; 
      b++;
    } 
    return -1;
  }
  
  Set<String> getMatchingPolicies() {
    if (this.matchingPolicies != null) {
      Set set = this.buildParams.initialPolicies();
      if (!set.isEmpty() && !set.contains("2.5.29.32.0") && this.buildParams.policyMappingInhibited()) {
        this.matchingPolicies = new HashSet(set);
        this.matchingPolicies.add("2.5.29.32.0");
      } else {
        this.matchingPolicies = Collections.emptySet();
      } 
    } 
    return this.matchingPolicies;
  }
  
  boolean addMatchingCerts(X509CertSelector paramX509CertSelector, Collection<CertStore> paramCollection1, Collection<X509Certificate> paramCollection2, boolean paramBoolean) {
    X509Certificate x509Certificate = paramX509CertSelector.getCertificate();
    if (x509Certificate != null) {
      if (paramX509CertSelector.match(x509Certificate) && !X509CertImpl.isSelfSigned(x509Certificate, this.buildParams.sigProvider())) {
        if (debug != null)
          debug.println("Builder.addMatchingCerts: adding target cert\n  SN: " + Debug.toHexString(x509Certificate.getSerialNumber()) + "\n  Subject: " + x509Certificate.getSubjectX500Principal() + "\n  Issuer: " + x509Certificate.getIssuerX500Principal()); 
        return paramCollection2.add(x509Certificate);
      } 
      return false;
    } 
    boolean bool = false;
    for (CertStore certStore : paramCollection1) {
      try {
        Collection collection = certStore.getCertificates(paramX509CertSelector);
        for (Certificate certificate : collection) {
          if (!X509CertImpl.isSelfSigned((X509Certificate)certificate, this.buildParams.sigProvider()) && paramCollection2.add((X509Certificate)certificate))
            bool = true; 
        } 
        if (!paramBoolean && bool)
          return true; 
      } catch (CertStoreException certStoreException) {
        if (debug != null) {
          debug.println("Builder.addMatchingCerts, non-fatal exception retrieving certs: " + certStoreException);
          certStoreException.printStackTrace();
        } 
      } 
    } 
    return bool;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\Builder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */