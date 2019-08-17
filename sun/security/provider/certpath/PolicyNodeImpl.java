package sun.security.provider.certpath;

import java.security.cert.PolicyNode;
import java.security.cert.PolicyQualifierInfo;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

final class PolicyNodeImpl implements PolicyNode {
  private static final String ANY_POLICY = "2.5.29.32.0";
  
  private PolicyNodeImpl mParent;
  
  private HashSet<PolicyNodeImpl> mChildren;
  
  private String mValidPolicy;
  
  private HashSet<PolicyQualifierInfo> mQualifierSet;
  
  private boolean mCriticalityIndicator;
  
  private HashSet<String> mExpectedPolicySet;
  
  private boolean mOriginalExpectedPolicySet;
  
  private int mDepth;
  
  private boolean isImmutable = false;
  
  PolicyNodeImpl(PolicyNodeImpl paramPolicyNodeImpl, String paramString, Set<PolicyQualifierInfo> paramSet1, boolean paramBoolean1, Set<String> paramSet2, boolean paramBoolean2) {
    this.mParent = paramPolicyNodeImpl;
    this.mChildren = new HashSet();
    if (paramString != null) {
      this.mValidPolicy = paramString;
    } else {
      this.mValidPolicy = "";
    } 
    if (paramSet1 != null) {
      this.mQualifierSet = new HashSet(paramSet1);
    } else {
      this.mQualifierSet = new HashSet();
    } 
    this.mCriticalityIndicator = paramBoolean1;
    if (paramSet2 != null) {
      this.mExpectedPolicySet = new HashSet(paramSet2);
    } else {
      this.mExpectedPolicySet = new HashSet();
    } 
    this.mOriginalExpectedPolicySet = !paramBoolean2;
    if (this.mParent != null) {
      this.mDepth = this.mParent.getDepth() + 1;
      this.mParent.addChild(this);
    } else {
      this.mDepth = 0;
    } 
  }
  
  PolicyNodeImpl(PolicyNodeImpl paramPolicyNodeImpl1, PolicyNodeImpl paramPolicyNodeImpl2) { this(paramPolicyNodeImpl1, paramPolicyNodeImpl2.mValidPolicy, paramPolicyNodeImpl2.mQualifierSet, paramPolicyNodeImpl2.mCriticalityIndicator, paramPolicyNodeImpl2.mExpectedPolicySet, false); }
  
  public PolicyNode getParent() { return this.mParent; }
  
  public Iterator<PolicyNodeImpl> getChildren() { return Collections.unmodifiableSet(this.mChildren).iterator(); }
  
  public int getDepth() { return this.mDepth; }
  
  public String getValidPolicy() { return this.mValidPolicy; }
  
  public Set<PolicyQualifierInfo> getPolicyQualifiers() { return Collections.unmodifiableSet(this.mQualifierSet); }
  
  public Set<String> getExpectedPolicies() { return Collections.unmodifiableSet(this.mExpectedPolicySet); }
  
  public boolean isCritical() { return this.mCriticalityIndicator; }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(asString());
    for (PolicyNodeImpl policyNodeImpl : this.mChildren)
      stringBuilder.append(policyNodeImpl); 
    return stringBuilder.toString();
  }
  
  boolean isImmutable() { return this.isImmutable; }
  
  void setImmutable() {
    if (this.isImmutable)
      return; 
    for (PolicyNodeImpl policyNodeImpl : this.mChildren)
      policyNodeImpl.setImmutable(); 
    this.isImmutable = true;
  }
  
  private void addChild(PolicyNodeImpl paramPolicyNodeImpl) {
    if (this.isImmutable)
      throw new IllegalStateException("PolicyNode is immutable"); 
    this.mChildren.add(paramPolicyNodeImpl);
  }
  
  void addExpectedPolicy(String paramString) {
    if (this.isImmutable)
      throw new IllegalStateException("PolicyNode is immutable"); 
    if (this.mOriginalExpectedPolicySet) {
      this.mExpectedPolicySet.clear();
      this.mOriginalExpectedPolicySet = false;
    } 
    this.mExpectedPolicySet.add(paramString);
  }
  
  void prune(int paramInt) {
    if (this.isImmutable)
      throw new IllegalStateException("PolicyNode is immutable"); 
    if (this.mChildren.size() == 0)
      return; 
    Iterator iterator = this.mChildren.iterator();
    while (iterator.hasNext()) {
      PolicyNodeImpl policyNodeImpl = (PolicyNodeImpl)iterator.next();
      policyNodeImpl.prune(paramInt);
      if (policyNodeImpl.mChildren.size() == 0 && paramInt > this.mDepth + 1)
        iterator.remove(); 
    } 
  }
  
  void deleteChild(PolicyNode paramPolicyNode) {
    if (this.isImmutable)
      throw new IllegalStateException("PolicyNode is immutable"); 
    this.mChildren.remove(paramPolicyNode);
  }
  
  PolicyNodeImpl copyTree() { return copyTree(null); }
  
  private PolicyNodeImpl copyTree(PolicyNodeImpl paramPolicyNodeImpl) {
    PolicyNodeImpl policyNodeImpl = new PolicyNodeImpl(paramPolicyNodeImpl, this);
    for (PolicyNodeImpl policyNodeImpl1 : this.mChildren)
      policyNodeImpl1.copyTree(policyNodeImpl); 
    return policyNodeImpl;
  }
  
  Set<PolicyNodeImpl> getPolicyNodes(int paramInt) {
    HashSet hashSet = new HashSet();
    getPolicyNodes(paramInt, hashSet);
    return hashSet;
  }
  
  private void getPolicyNodes(int paramInt, Set<PolicyNodeImpl> paramSet) {
    if (this.mDepth == paramInt) {
      paramSet.add(this);
    } else {
      for (PolicyNodeImpl policyNodeImpl : this.mChildren)
        policyNodeImpl.getPolicyNodes(paramInt, paramSet); 
    } 
  }
  
  Set<PolicyNodeImpl> getPolicyNodesExpected(int paramInt, String paramString, boolean paramBoolean) { return paramString.equals("2.5.29.32.0") ? getPolicyNodes(paramInt) : getPolicyNodesExpectedHelper(paramInt, paramString, paramBoolean); }
  
  private Set<PolicyNodeImpl> getPolicyNodesExpectedHelper(int paramInt, String paramString, boolean paramBoolean) {
    HashSet hashSet = new HashSet();
    if (this.mDepth < paramInt) {
      for (PolicyNodeImpl policyNodeImpl : this.mChildren)
        hashSet.addAll(policyNodeImpl.getPolicyNodesExpectedHelper(paramInt, paramString, paramBoolean)); 
    } else if (paramBoolean) {
      if (this.mExpectedPolicySet.contains("2.5.29.32.0"))
        hashSet.add(this); 
    } else if (this.mExpectedPolicySet.contains(paramString)) {
      hashSet.add(this);
    } 
    return hashSet;
  }
  
  Set<PolicyNodeImpl> getPolicyNodesValid(int paramInt, String paramString) {
    HashSet hashSet = new HashSet();
    if (this.mDepth < paramInt) {
      for (PolicyNodeImpl policyNodeImpl : this.mChildren)
        hashSet.addAll(policyNodeImpl.getPolicyNodesValid(paramInt, paramString)); 
    } else if (this.mValidPolicy.equals(paramString)) {
      hashSet.add(this);
    } 
    return hashSet;
  }
  
  private static String policyToString(String paramString) { return paramString.equals("2.5.29.32.0") ? "anyPolicy" : paramString; }
  
  String asString() {
    if (this.mParent == null)
      return "anyPolicy  ROOT\n"; 
    StringBuilder stringBuilder = new StringBuilder();
    byte b = 0;
    int i = getDepth();
    while (b < i) {
      stringBuilder.append("  ");
      b++;
    } 
    stringBuilder.append(policyToString(getValidPolicy()));
    stringBuilder.append("  CRIT: ");
    stringBuilder.append(isCritical());
    stringBuilder.append("  EP: ");
    for (String str : getExpectedPolicies()) {
      stringBuilder.append(policyToString(str));
      stringBuilder.append(" ");
    } 
    stringBuilder.append(" (");
    stringBuilder.append(getDepth());
    stringBuilder.append(")\n");
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\PolicyNodeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */