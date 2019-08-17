package sun.security.provider.certpath;

import java.io.IOException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXReason;
import java.security.cert.PolicyNode;
import java.security.cert.PolicyQualifierInfo;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import sun.security.util.Debug;
import sun.security.x509.CertificatePoliciesExtension;
import sun.security.x509.CertificatePolicyMap;
import sun.security.x509.InhibitAnyPolicyExtension;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.PolicyConstraintsExtension;
import sun.security.x509.PolicyInformation;
import sun.security.x509.PolicyMappingsExtension;
import sun.security.x509.X509CertImpl;

class PolicyChecker extends PKIXCertPathChecker {
  private final Set<String> initPolicies;
  
  private final int certPathLen;
  
  private final boolean expPolicyRequired;
  
  private final boolean polMappingInhibited;
  
  private final boolean anyPolicyInhibited;
  
  private final boolean rejectPolicyQualifiers;
  
  private PolicyNodeImpl rootNode;
  
  private int explicitPolicy;
  
  private int policyMapping;
  
  private int inhibitAnyPolicy;
  
  private int certIndex;
  
  private Set<String> supportedExts;
  
  private static final Debug debug = Debug.getInstance("certpath");
  
  static final String ANY_POLICY = "2.5.29.32.0";
  
  PolicyChecker(Set<String> paramSet, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, PolicyNodeImpl paramPolicyNodeImpl) {
    if (paramSet.isEmpty()) {
      this.initPolicies = new HashSet(1);
      this.initPolicies.add("2.5.29.32.0");
    } else {
      this.initPolicies = new HashSet(paramSet);
    } 
    this.certPathLen = paramInt;
    this.expPolicyRequired = paramBoolean1;
    this.polMappingInhibited = paramBoolean2;
    this.anyPolicyInhibited = paramBoolean3;
    this.rejectPolicyQualifiers = paramBoolean4;
    this.rootNode = paramPolicyNodeImpl;
  }
  
  public void init(boolean paramBoolean) throws CertPathValidatorException {
    if (paramBoolean)
      throw new CertPathValidatorException("forward checking not supported"); 
    this.certIndex = 1;
    this.explicitPolicy = this.expPolicyRequired ? 0 : (this.certPathLen + 1);
    this.policyMapping = this.polMappingInhibited ? 0 : (this.certPathLen + 1);
    this.inhibitAnyPolicy = this.anyPolicyInhibited ? 0 : (this.certPathLen + 1);
  }
  
  public boolean isForwardCheckingSupported() { return false; }
  
  public Set<String> getSupportedExtensions() {
    if (this.supportedExts == null) {
      this.supportedExts = new HashSet(4);
      this.supportedExts.add(PKIXExtensions.CertificatePolicies_Id.toString());
      this.supportedExts.add(PKIXExtensions.PolicyMappings_Id.toString());
      this.supportedExts.add(PKIXExtensions.PolicyConstraints_Id.toString());
      this.supportedExts.add(PKIXExtensions.InhibitAnyPolicy_Id.toString());
      this.supportedExts = Collections.unmodifiableSet(this.supportedExts);
    } 
    return this.supportedExts;
  }
  
  public void check(Certificate paramCertificate, Collection<String> paramCollection) throws CertPathValidatorException {
    checkPolicy((X509Certificate)paramCertificate);
    if (paramCollection != null && !paramCollection.isEmpty()) {
      paramCollection.remove(PKIXExtensions.CertificatePolicies_Id.toString());
      paramCollection.remove(PKIXExtensions.PolicyMappings_Id.toString());
      paramCollection.remove(PKIXExtensions.PolicyConstraints_Id.toString());
      paramCollection.remove(PKIXExtensions.InhibitAnyPolicy_Id.toString());
    } 
  }
  
  private void checkPolicy(X509Certificate paramX509Certificate) throws CertPathValidatorException {
    String str = "certificate policies";
    if (debug != null) {
      debug.println("PolicyChecker.checkPolicy() ---checking " + str + "...");
      debug.println("PolicyChecker.checkPolicy() certIndex = " + this.certIndex);
      debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: explicitPolicy = " + this.explicitPolicy);
      debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: policyMapping = " + this.policyMapping);
      debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: inhibitAnyPolicy = " + this.inhibitAnyPolicy);
      debug.println("PolicyChecker.checkPolicy() BEFORE PROCESSING: policyTree = " + this.rootNode);
    } 
    X509CertImpl x509CertImpl = null;
    try {
      x509CertImpl = X509CertImpl.toImpl(paramX509Certificate);
    } catch (CertificateException certificateException) {
      throw new CertPathValidatorException(certificateException);
    } 
    boolean bool = (this.certIndex == this.certPathLen);
    this.rootNode = processPolicies(this.certIndex, this.initPolicies, this.explicitPolicy, this.policyMapping, this.inhibitAnyPolicy, this.rejectPolicyQualifiers, this.rootNode, x509CertImpl, bool);
    if (!bool) {
      this.explicitPolicy = mergeExplicitPolicy(this.explicitPolicy, x509CertImpl, bool);
      this.policyMapping = mergePolicyMapping(this.policyMapping, x509CertImpl);
      this.inhibitAnyPolicy = mergeInhibitAnyPolicy(this.inhibitAnyPolicy, x509CertImpl);
    } 
    this.certIndex++;
    if (debug != null) {
      debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: explicitPolicy = " + this.explicitPolicy);
      debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: policyMapping = " + this.policyMapping);
      debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: inhibitAnyPolicy = " + this.inhibitAnyPolicy);
      debug.println("PolicyChecker.checkPolicy() AFTER PROCESSING: policyTree = " + this.rootNode);
      debug.println("PolicyChecker.checkPolicy() " + str + " verified");
    } 
  }
  
  static int mergeExplicitPolicy(int paramInt, X509CertImpl paramX509CertImpl, boolean paramBoolean) throws CertPathValidatorException {
    if (paramInt > 0 && !X509CertImpl.isSelfIssued(paramX509CertImpl))
      paramInt--; 
    try {
      PolicyConstraintsExtension policyConstraintsExtension = paramX509CertImpl.getPolicyConstraintsExtension();
      if (policyConstraintsExtension == null)
        return paramInt; 
      int i = policyConstraintsExtension.get("require").intValue();
      if (debug != null)
        debug.println("PolicyChecker.mergeExplicitPolicy() require Index from cert = " + i); 
      if (!paramBoolean) {
        if (i != -1 && (paramInt == -1 || i < paramInt))
          paramInt = i; 
      } else if (i == 0) {
        paramInt = i;
      } 
    } catch (IOException iOException) {
      if (debug != null) {
        debug.println("PolicyChecker.mergeExplicitPolicy unexpected exception");
        iOException.printStackTrace();
      } 
      throw new CertPathValidatorException(iOException);
    } 
    return paramInt;
  }
  
  static int mergePolicyMapping(int paramInt, X509CertImpl paramX509CertImpl) throws CertPathValidatorException {
    if (paramInt > 0 && !X509CertImpl.isSelfIssued(paramX509CertImpl))
      paramInt--; 
    try {
      PolicyConstraintsExtension policyConstraintsExtension = paramX509CertImpl.getPolicyConstraintsExtension();
      if (policyConstraintsExtension == null)
        return paramInt; 
      int i = policyConstraintsExtension.get("inhibit").intValue();
      if (debug != null)
        debug.println("PolicyChecker.mergePolicyMapping() inhibit Index from cert = " + i); 
      if (i != -1 && (paramInt == -1 || i < paramInt))
        paramInt = i; 
    } catch (IOException iOException) {
      if (debug != null) {
        debug.println("PolicyChecker.mergePolicyMapping unexpected exception");
        iOException.printStackTrace();
      } 
      throw new CertPathValidatorException(iOException);
    } 
    return paramInt;
  }
  
  static int mergeInhibitAnyPolicy(int paramInt, X509CertImpl paramX509CertImpl) throws CertPathValidatorException {
    if (paramInt > 0 && !X509CertImpl.isSelfIssued(paramX509CertImpl))
      paramInt--; 
    try {
      InhibitAnyPolicyExtension inhibitAnyPolicyExtension = (InhibitAnyPolicyExtension)paramX509CertImpl.getExtension(PKIXExtensions.InhibitAnyPolicy_Id);
      if (inhibitAnyPolicyExtension == null)
        return paramInt; 
      int i = inhibitAnyPolicyExtension.get("skip_certs").intValue();
      if (debug != null)
        debug.println("PolicyChecker.mergeInhibitAnyPolicy() skipCerts Index from cert = " + i); 
      if (i != -1 && i < paramInt)
        paramInt = i; 
    } catch (IOException iOException) {
      if (debug != null) {
        debug.println("PolicyChecker.mergeInhibitAnyPolicy unexpected exception");
        iOException.printStackTrace();
      } 
      throw new CertPathValidatorException(iOException);
    } 
    return paramInt;
  }
  
  static PolicyNodeImpl processPolicies(int paramInt1, Set<String> paramSet, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, PolicyNodeImpl paramPolicyNodeImpl, X509CertImpl paramX509CertImpl, boolean paramBoolean2) throws CertPathValidatorException {
    boolean bool = false;
    PolicyNodeImpl policyNodeImpl = null;
    Set set = new HashSet();
    if (paramPolicyNodeImpl == null) {
      policyNodeImpl = null;
    } else {
      policyNodeImpl = paramPolicyNodeImpl.copyTree();
    } 
    CertificatePoliciesExtension certificatePoliciesExtension = paramX509CertImpl.getCertificatePoliciesExtension();
    if (certificatePoliciesExtension != null && policyNodeImpl != null) {
      List list;
      bool = certificatePoliciesExtension.isCritical();
      if (debug != null)
        debug.println("PolicyChecker.processPolicies() policiesCritical = " + bool); 
      try {
        list = certificatePoliciesExtension.get("policies");
      } catch (IOException iOException) {
        throw new CertPathValidatorException("Exception while retrieving policyOIDs", iOException);
      } 
      if (debug != null)
        debug.println("PolicyChecker.processPolicies() rejectPolicyQualifiers = " + paramBoolean1); 
      boolean bool1 = false;
      for (PolicyInformation policyInformation : list) {
        String str = policyInformation.getPolicyIdentifier().getIdentifier().toString();
        if (str.equals("2.5.29.32.0")) {
          bool1 = true;
          set = policyInformation.getPolicyQualifiers();
          continue;
        } 
        if (debug != null)
          debug.println("PolicyChecker.processPolicies() processing policy: " + str); 
        Set set1 = policyInformation.getPolicyQualifiers();
        if (!set1.isEmpty() && paramBoolean1 && bool)
          throw new CertPathValidatorException("critical policy qualifiers present in certificate", null, null, -1, PKIXReason.INVALID_POLICY); 
        boolean bool2 = processParents(paramInt1, bool, paramBoolean1, policyNodeImpl, str, set1, false);
        if (!bool2)
          processParents(paramInt1, bool, paramBoolean1, policyNodeImpl, str, set1, true); 
      } 
      if (bool1 && (paramInt4 > 0 || (!paramBoolean2 && X509CertImpl.isSelfIssued(paramX509CertImpl)))) {
        if (debug != null)
          debug.println("PolicyChecker.processPolicies() processing policy: 2.5.29.32.0"); 
        processParents(paramInt1, bool, paramBoolean1, policyNodeImpl, "2.5.29.32.0", set, true);
      } 
      policyNodeImpl.prune(paramInt1);
      if (!policyNodeImpl.getChildren().hasNext())
        policyNodeImpl = null; 
    } else if (certificatePoliciesExtension == null) {
      if (debug != null)
        debug.println("PolicyChecker.processPolicies() no policies present in cert"); 
      policyNodeImpl = null;
    } 
    if (policyNodeImpl != null && !paramBoolean2)
      policyNodeImpl = processPolicyMappings(paramX509CertImpl, paramInt1, paramInt3, policyNodeImpl, bool, set); 
    if (policyNodeImpl != null && !paramSet.contains("2.5.29.32.0") && certificatePoliciesExtension != null) {
      policyNodeImpl = removeInvalidNodes(policyNodeImpl, paramInt1, paramSet, certificatePoliciesExtension);
      if (policyNodeImpl != null && paramBoolean2)
        policyNodeImpl = rewriteLeafNodes(paramInt1, paramSet, policyNodeImpl); 
    } 
    if (paramBoolean2)
      paramInt2 = mergeExplicitPolicy(paramInt2, paramX509CertImpl, paramBoolean2); 
    if (paramInt2 == 0 && policyNodeImpl == null)
      throw new CertPathValidatorException("non-null policy tree required and policy tree is null", null, null, -1, PKIXReason.INVALID_POLICY); 
    return policyNodeImpl;
  }
  
  private static PolicyNodeImpl rewriteLeafNodes(int paramInt, Set<String> paramSet, PolicyNodeImpl paramPolicyNodeImpl) {
    Set set = paramPolicyNodeImpl.getPolicyNodesValid(paramInt, "2.5.29.32.0");
    if (set.isEmpty())
      return paramPolicyNodeImpl; 
    PolicyNodeImpl policyNodeImpl1 = (PolicyNodeImpl)set.iterator().next();
    PolicyNodeImpl policyNodeImpl2 = (PolicyNodeImpl)policyNodeImpl1.getParent();
    policyNodeImpl2.deleteChild(policyNodeImpl1);
    HashSet hashSet = new HashSet(paramSet);
    for (PolicyNodeImpl policyNodeImpl : paramPolicyNodeImpl.getPolicyNodes(paramInt))
      hashSet.remove(policyNodeImpl.getValidPolicy()); 
    if (hashSet.isEmpty()) {
      paramPolicyNodeImpl.prune(paramInt);
      if (!paramPolicyNodeImpl.getChildren().hasNext())
        paramPolicyNodeImpl = null; 
    } else {
      boolean bool = policyNodeImpl1.isCritical();
      Set set1 = policyNodeImpl1.getPolicyQualifiers();
      for (String str : hashSet) {
        Set set2 = Collections.singleton(str);
        PolicyNodeImpl policyNodeImpl = new PolicyNodeImpl(policyNodeImpl2, str, set1, bool, set2, false);
      } 
    } 
    return paramPolicyNodeImpl;
  }
  
  private static boolean processParents(int paramInt, boolean paramBoolean1, boolean paramBoolean2, PolicyNodeImpl paramPolicyNodeImpl, String paramString, Set<PolicyQualifierInfo> paramSet, boolean paramBoolean3) throws CertPathValidatorException {
    boolean bool = false;
    if (debug != null)
      debug.println("PolicyChecker.processParents(): matchAny = " + paramBoolean3); 
    Set set = paramPolicyNodeImpl.getPolicyNodesExpected(paramInt - 1, paramString, paramBoolean3);
    for (PolicyNodeImpl policyNodeImpl1 : set) {
      if (debug != null)
        debug.println("PolicyChecker.processParents() found parent:\n" + policyNodeImpl1.asString()); 
      bool = true;
      String str = policyNodeImpl1.getValidPolicy();
      PolicyNodeImpl policyNodeImpl2 = null;
      HashSet hashSet = null;
      if (paramString.equals("2.5.29.32.0")) {
        Set set1 = policyNodeImpl1.getExpectedPolicies();
        for (String str1 : set1) {
          Iterator iterator = policyNodeImpl1.getChildren();
          while (iterator.hasNext()) {
            PolicyNodeImpl policyNodeImpl = (PolicyNodeImpl)iterator.next();
            String str2 = policyNodeImpl.getValidPolicy();
            if (str1.equals(str2) && debug != null)
              debug.println(str2 + " in parent's expected policy set already appears in child node"); 
          } 
          HashSet hashSet1 = new HashSet();
          hashSet1.add(str1);
          policyNodeImpl2 = new PolicyNodeImpl(policyNodeImpl1, str1, paramSet, paramBoolean1, hashSet1, false);
        } 
        continue;
      } 
      hashSet = new HashSet();
      hashSet.add(paramString);
      policyNodeImpl2 = new PolicyNodeImpl(policyNodeImpl1, paramString, paramSet, paramBoolean1, hashSet, false);
    } 
    return bool;
  }
  
  private static PolicyNodeImpl processPolicyMappings(X509CertImpl paramX509CertImpl, int paramInt1, int paramInt2, PolicyNodeImpl paramPolicyNodeImpl, boolean paramBoolean, Set<PolicyQualifierInfo> paramSet) throws CertPathValidatorException {
    PolicyMappingsExtension policyMappingsExtension = paramX509CertImpl.getPolicyMappingsExtension();
    if (policyMappingsExtension == null)
      return paramPolicyNodeImpl; 
    if (debug != null)
      debug.println("PolicyChecker.processPolicyMappings() inside policyMapping check"); 
    List list = null;
    try {
      list = policyMappingsExtension.get("map");
    } catch (IOException iOException) {
      if (debug != null) {
        debug.println("PolicyChecker.processPolicyMappings() mapping exception");
        iOException.printStackTrace();
      } 
      throw new CertPathValidatorException("Exception while checking mapping", iOException);
    } 
    boolean bool = false;
    for (CertificatePolicyMap certificatePolicyMap : list) {
      String str1 = certificatePolicyMap.getIssuerIdentifier().getIdentifier().toString();
      String str2 = certificatePolicyMap.getSubjectIdentifier().getIdentifier().toString();
      if (debug != null) {
        debug.println("PolicyChecker.processPolicyMappings() issuerDomain = " + str1);
        debug.println("PolicyChecker.processPolicyMappings() subjectDomain = " + str2);
      } 
      if (str1.equals("2.5.29.32.0"))
        throw new CertPathValidatorException("encountered an issuerDomainPolicy of ANY_POLICY", null, null, -1, PKIXReason.INVALID_POLICY); 
      if (str2.equals("2.5.29.32.0"))
        throw new CertPathValidatorException("encountered a subjectDomainPolicy of ANY_POLICY", null, null, -1, PKIXReason.INVALID_POLICY); 
      Set set = paramPolicyNodeImpl.getPolicyNodesValid(paramInt1, str1);
      if (!set.isEmpty()) {
        for (PolicyNodeImpl policyNodeImpl : set) {
          if (paramInt2 > 0 || paramInt2 == -1) {
            policyNodeImpl.addExpectedPolicy(str2);
            continue;
          } 
          if (paramInt2 == 0) {
            PolicyNodeImpl policyNodeImpl1 = (PolicyNodeImpl)policyNodeImpl.getParent();
            if (debug != null)
              debug.println("PolicyChecker.processPolicyMappings() before deleting: policy tree = " + paramPolicyNodeImpl); 
            policyNodeImpl1.deleteChild(policyNodeImpl);
            bool = true;
            if (debug != null)
              debug.println("PolicyChecker.processPolicyMappings() after deleting: policy tree = " + paramPolicyNodeImpl); 
          } 
        } 
        continue;
      } 
      if (paramInt2 > 0 || paramInt2 == -1) {
        Set set1 = paramPolicyNodeImpl.getPolicyNodesValid(paramInt1, "2.5.29.32.0");
        for (PolicyNodeImpl policyNodeImpl1 : set1) {
          PolicyNodeImpl policyNodeImpl2 = (PolicyNodeImpl)policyNodeImpl1.getParent();
          HashSet hashSet = new HashSet();
          hashSet.add(str2);
          PolicyNodeImpl policyNodeImpl3 = new PolicyNodeImpl(policyNodeImpl2, str1, paramSet, paramBoolean, hashSet, true);
        } 
      } 
    } 
    if (bool) {
      paramPolicyNodeImpl.prune(paramInt1);
      if (!paramPolicyNodeImpl.getChildren().hasNext()) {
        if (debug != null)
          debug.println("setting rootNode to null"); 
        paramPolicyNodeImpl = null;
      } 
    } 
    return paramPolicyNodeImpl;
  }
  
  private static PolicyNodeImpl removeInvalidNodes(PolicyNodeImpl paramPolicyNodeImpl, int paramInt, Set<String> paramSet, CertificatePoliciesExtension paramCertificatePoliciesExtension) throws CertPathValidatorException {
    List list = null;
    try {
      list = paramCertificatePoliciesExtension.get("policies");
    } catch (IOException iOException) {
      throw new CertPathValidatorException("Exception while retrieving policyOIDs", iOException);
    } 
    boolean bool = false;
    for (PolicyInformation policyInformation : list) {
      String str = policyInformation.getPolicyIdentifier().getIdentifier().toString();
      if (debug != null)
        debug.println("PolicyChecker.processPolicies() processing policy second time: " + str); 
      Set set = paramPolicyNodeImpl.getPolicyNodesValid(paramInt, str);
      for (PolicyNodeImpl policyNodeImpl1 : set) {
        PolicyNodeImpl policyNodeImpl2 = (PolicyNodeImpl)policyNodeImpl1.getParent();
        if (policyNodeImpl2.getValidPolicy().equals("2.5.29.32.0") && !paramSet.contains(str) && !str.equals("2.5.29.32.0")) {
          if (debug != null)
            debug.println("PolicyChecker.processPolicies() before deleting: policy tree = " + paramPolicyNodeImpl); 
          policyNodeImpl2.deleteChild(policyNodeImpl1);
          bool = true;
          if (debug != null)
            debug.println("PolicyChecker.processPolicies() after deleting: policy tree = " + paramPolicyNodeImpl); 
        } 
      } 
    } 
    if (bool) {
      paramPolicyNodeImpl.prune(paramInt);
      if (!paramPolicyNodeImpl.getChildren().hasNext())
        paramPolicyNodeImpl = null; 
    } 
    return paramPolicyNodeImpl;
  }
  
  PolicyNode getPolicyTree() {
    if (this.rootNode == null)
      return null; 
    PolicyNodeImpl policyNodeImpl = this.rootNode.copyTree();
    policyNodeImpl.setImmutable();
    return policyNodeImpl;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\PolicyChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */