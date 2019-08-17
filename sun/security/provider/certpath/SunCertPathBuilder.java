package sun.security.provider.certpath;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.PublicKey;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathBuilderSpi;
import java.security.cert.CertPathChecker;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertSelector;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.PolicyNode;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.x509.PKIXExtensions;

public final class SunCertPathBuilder extends CertPathBuilderSpi {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private PKIX.BuilderParams buildParams;
  
  private CertificateFactory cf;
  
  private boolean pathCompleted = false;
  
  private PolicyNode policyTreeResult;
  
  private TrustAnchor trustAnchor;
  
  private PublicKey finalPublicKey;
  
  public SunCertPathBuilder() throws CertPathBuilderException {
    try {
      this.cf = CertificateFactory.getInstance("X.509");
    } catch (CertificateException certificateException) {
      throw new CertPathBuilderException(certificateException);
    } 
  }
  
  public CertPathChecker engineGetRevocationChecker() { return new RevocationChecker(); }
  
  public CertPathBuilderResult engineBuild(CertPathParameters paramCertPathParameters) throws CertPathBuilderException, InvalidAlgorithmParameterException {
    if (debug != null)
      debug.println("SunCertPathBuilder.engineBuild(" + paramCertPathParameters + ")"); 
    this.buildParams = PKIX.checkBuilderParams(paramCertPathParameters);
    return build();
  }
  
  private PKIXCertPathBuilderResult build() throws CertPathBuilderException {
    ArrayList arrayList = new ArrayList();
    PKIXCertPathBuilderResult pKIXCertPathBuilderResult = buildCertPath(false, arrayList);
    if (pKIXCertPathBuilderResult == null) {
      if (debug != null)
        debug.println("SunCertPathBuilder.engineBuild: 2nd pass; try building again searching all certstores"); 
      arrayList.clear();
      pKIXCertPathBuilderResult = buildCertPath(true, arrayList);
      if (pKIXCertPathBuilderResult == null)
        throw new SunCertPathBuilderException("unable to find valid certification path to requested target", new AdjacencyList(arrayList)); 
    } 
    return pKIXCertPathBuilderResult;
  }
  
  private PKIXCertPathBuilderResult buildCertPath(boolean paramBoolean, List<List<Vertex>> paramList) throws CertPathBuilderException {
    this.pathCompleted = false;
    this.trustAnchor = null;
    this.finalPublicKey = null;
    this.policyTreeResult = null;
    LinkedList linkedList = new LinkedList();
    try {
      buildForward(paramList, linkedList, paramBoolean);
    } catch (GeneralSecurityException|IOException generalSecurityException) {
      if (debug != null) {
        debug.println("SunCertPathBuilder.engineBuild() exception in build");
        generalSecurityException.printStackTrace();
      } 
      throw new SunCertPathBuilderException("unable to find valid certification path to requested target", generalSecurityException, new AdjacencyList(paramList));
    } 
    try {
      if (this.pathCompleted) {
        if (debug != null)
          debug.println("SunCertPathBuilder.engineBuild() pathCompleted"); 
        Collections.reverse(linkedList);
        return new SunCertPathBuilderResult(this.cf.generateCertPath(linkedList), this.trustAnchor, this.policyTreeResult, this.finalPublicKey, new AdjacencyList(paramList));
      } 
    } catch (CertificateException certificateException) {
      if (debug != null) {
        debug.println("SunCertPathBuilder.engineBuild() exception in wrap-up");
        certificateException.printStackTrace();
      } 
      throw new SunCertPathBuilderException("unable to find valid certification path to requested target", certificateException, new AdjacencyList(paramList));
    } 
    return null;
  }
  
  private void buildForward(List<List<Vertex>> paramList, LinkedList<X509Certificate> paramLinkedList, boolean paramBoolean) throws GeneralSecurityException, IOException {
    if (debug != null)
      debug.println("SunCertPathBuilder.buildForward()..."); 
    ForwardState forwardState = new ForwardState();
    forwardState.initState(this.buildParams.certPathCheckers());
    paramList.clear();
    paramList.add(new LinkedList());
    forwardState.untrustedChecker = new UntrustedChecker();
    depthFirstSearchForward(this.buildParams.targetSubject(), forwardState, new ForwardBuilder(this.buildParams, paramBoolean), paramList, paramLinkedList);
  }
  
  private void depthFirstSearchForward(X500Principal paramX500Principal, ForwardState paramForwardState, ForwardBuilder paramForwardBuilder, List<List<Vertex>> paramList, LinkedList<X509Certificate> paramLinkedList) throws GeneralSecurityException, IOException {
    if (debug != null)
      debug.println("SunCertPathBuilder.depthFirstSearchForward(" + paramX500Principal + ", " + paramForwardState.toString() + ")"); 
    Collection collection = paramForwardBuilder.getMatchingCerts(paramForwardState, this.buildParams.certStores());
    List list = addVertices(collection, paramList);
    if (debug != null)
      debug.println("SunCertPathBuilder.depthFirstSearchForward(): certs.size=" + list.size()); 
    label115: for (Vertex vertex : list) {
      ForwardState forwardState = (ForwardState)paramForwardState.clone();
      X509Certificate x509Certificate = vertex.getCertificate();
      try {
        paramForwardBuilder.verifyCert(x509Certificate, forwardState, paramLinkedList);
      } catch (GeneralSecurityException generalSecurityException) {
        if (debug != null) {
          debug.println("SunCertPathBuilder.depthFirstSearchForward(): validation failed: " + generalSecurityException);
          generalSecurityException.printStackTrace();
        } 
        vertex.setThrowable(generalSecurityException);
        continue;
      } 
      if (paramForwardBuilder.isPathCompleted(x509Certificate)) {
        if (debug != null)
          debug.println("SunCertPathBuilder.depthFirstSearchForward(): commencing final verification"); 
        ArrayList arrayList1 = new ArrayList(paramLinkedList);
        if (paramForwardBuilder.trustAnchor.getTrustedCert() == null)
          arrayList1.add(0, x509Certificate); 
        Set set = Collections.singleton("2.5.29.32.0");
        PolicyNodeImpl policyNodeImpl = new PolicyNodeImpl(null, "2.5.29.32.0", null, false, set, false);
        ArrayList arrayList2 = new ArrayList();
        PolicyChecker policyChecker = new PolicyChecker(this.buildParams.initialPolicies(), arrayList1.size(), this.buildParams.explicitPolicyRequired(), this.buildParams.policyMappingInhibited(), this.buildParams.anyPolicyInhibited(), this.buildParams.policyQualifiersRejected(), policyNodeImpl);
        arrayList2.add(policyChecker);
        arrayList2.add(new AlgorithmChecker(paramForwardBuilder.trustAnchor, this.buildParams.date(), this.buildParams.variant()));
        BasicChecker basicChecker = null;
        if (forwardState.keyParamsNeeded()) {
          PublicKey publicKey = x509Certificate.getPublicKey();
          if (paramForwardBuilder.trustAnchor.getTrustedCert() == null) {
            publicKey = paramForwardBuilder.trustAnchor.getCAPublicKey();
            if (debug != null)
              debug.println("SunCertPathBuilder.depthFirstSearchForward using buildParams public key: " + publicKey.toString()); 
          } 
          TrustAnchor trustAnchor1 = new TrustAnchor(x509Certificate.getSubjectX500Principal(), publicKey, null);
          basicChecker = new BasicChecker(trustAnchor1, this.buildParams.date(), this.buildParams.sigProvider(), true);
          arrayList2.add(basicChecker);
        } 
        this.buildParams.setCertPath(this.cf.generateCertPath(arrayList1));
        boolean bool = false;
        List list1 = this.buildParams.certPathCheckers();
        for (PKIXCertPathChecker pKIXCertPathChecker : list1) {
          if (pKIXCertPathChecker instanceof java.security.cert.PKIXRevocationChecker) {
            if (bool)
              throw new CertPathValidatorException("Only one PKIXRevocationChecker can be specified"); 
            bool = true;
            if (pKIXCertPathChecker instanceof RevocationChecker)
              ((RevocationChecker)pKIXCertPathChecker).init(paramForwardBuilder.trustAnchor, this.buildParams); 
          } 
        } 
        if (this.buildParams.revocationEnabled() && !bool)
          arrayList2.add(new RevocationChecker(paramForwardBuilder.trustAnchor, this.buildParams)); 
        arrayList2.addAll(list1);
        for (byte b = 0; b < arrayList1.size(); b++) {
          X509Certificate x509Certificate1 = (X509Certificate)arrayList1.get(b);
          if (debug != null)
            debug.println("current subject = " + x509Certificate1.getSubjectX500Principal()); 
          Set set1 = x509Certificate1.getCriticalExtensionOIDs();
          if (set1 == null)
            set1 = Collections.emptySet(); 
          for (PKIXCertPathChecker pKIXCertPathChecker : arrayList2) {
            if (!pKIXCertPathChecker.isForwardCheckingSupported()) {
              if (b == 0) {
                pKIXCertPathChecker.init(false);
                if (pKIXCertPathChecker instanceof AlgorithmChecker)
                  ((AlgorithmChecker)pKIXCertPathChecker).trySetTrustAnchor(paramForwardBuilder.trustAnchor); 
              } 
              try {
                pKIXCertPathChecker.check(x509Certificate1, set1);
              } catch (CertPathValidatorException certPathValidatorException) {
                if (debug != null)
                  debug.println("SunCertPathBuilder.depthFirstSearchForward(): final verification failed: " + certPathValidatorException); 
                if (this.buildParams.targetCertConstraints().match(x509Certificate1) && certPathValidatorException.getReason() == CertPathValidatorException.BasicReason.REVOKED)
                  throw certPathValidatorException; 
                vertex.setThrowable(certPathValidatorException);
                continue label115;
              } 
            } 
          } 
          for (PKIXCertPathChecker pKIXCertPathChecker : this.buildParams.certPathCheckers()) {
            if (pKIXCertPathChecker.isForwardCheckingSupported()) {
              Set set2 = pKIXCertPathChecker.getSupportedExtensions();
              if (set2 != null)
                set1.removeAll(set2); 
            } 
          } 
          if (!set1.isEmpty()) {
            set1.remove(PKIXExtensions.BasicConstraints_Id.toString());
            set1.remove(PKIXExtensions.NameConstraints_Id.toString());
            set1.remove(PKIXExtensions.CertificatePolicies_Id.toString());
            set1.remove(PKIXExtensions.PolicyMappings_Id.toString());
            set1.remove(PKIXExtensions.PolicyConstraints_Id.toString());
            set1.remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
            set1.remove(PKIXExtensions.SubjectAlternativeName_Id.toString());
            set1.remove(PKIXExtensions.KeyUsage_Id.toString());
            set1.remove(PKIXExtensions.ExtendedKeyUsage_Id.toString());
            if (!set1.isEmpty())
              throw new CertPathValidatorException("unrecognized critical extension(s)", null, null, -1, PKIXReason.UNRECOGNIZED_CRIT_EXT); 
          } 
        } 
        if (debug != null)
          debug.println("SunCertPathBuilder.depthFirstSearchForward(): final verification succeeded - path completed!"); 
        this.pathCompleted = true;
        if (paramForwardBuilder.trustAnchor.getTrustedCert() == null)
          paramForwardBuilder.addCertToPath(x509Certificate, paramLinkedList); 
        this.trustAnchor = paramForwardBuilder.trustAnchor;
        if (basicChecker != null) {
          this.finalPublicKey = basicChecker.getPublicKey();
        } else {
          Certificate certificate;
          if (paramLinkedList.isEmpty()) {
            certificate = paramForwardBuilder.trustAnchor.getTrustedCert();
          } else {
            certificate = (Certificate)paramLinkedList.getLast();
          } 
          this.finalPublicKey = certificate.getPublicKey();
        } 
        this.policyTreeResult = policyChecker.getPolicyTree();
        return;
      } 
      paramForwardBuilder.addCertToPath(x509Certificate, paramLinkedList);
      forwardState.updateState(x509Certificate);
      paramList.add(new LinkedList());
      vertex.setIndex(paramList.size() - 1);
      depthFirstSearchForward(x509Certificate.getIssuerX500Principal(), forwardState, paramForwardBuilder, paramList, paramLinkedList);
      if (this.pathCompleted)
        return; 
      if (debug != null)
        debug.println("SunCertPathBuilder.depthFirstSearchForward(): backtracking"); 
      paramForwardBuilder.removeFinalCertFromPath(paramLinkedList);
    } 
  }
  
  private static List<Vertex> addVertices(Collection<X509Certificate> paramCollection, List<List<Vertex>> paramList) {
    List list = (List)paramList.get(paramList.size() - 1);
    for (X509Certificate x509Certificate : paramCollection) {
      Vertex vertex = new Vertex(x509Certificate);
      list.add(vertex);
    } 
    return list;
  }
  
  private static boolean anchorIsTarget(TrustAnchor paramTrustAnchor, CertSelector paramCertSelector) {
    X509Certificate x509Certificate = paramTrustAnchor.getTrustedCert();
    return (x509Certificate != null) ? paramCertSelector.match(x509Certificate) : 0;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\SunCertPathBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */