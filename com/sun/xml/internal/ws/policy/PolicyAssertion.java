package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.sourcemodel.ModelNode;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

public abstract class PolicyAssertion {
  private final AssertionData data;
  
  private AssertionSet parameters;
  
  private NestedPolicy nestedPolicy;
  
  protected PolicyAssertion() {
    this.data = AssertionData.createAssertionData(null);
    this.parameters = AssertionSet.createAssertionSet(null);
  }
  
  @Deprecated
  protected PolicyAssertion(AssertionData paramAssertionData, Collection<? extends PolicyAssertion> paramCollection, AssertionSet paramAssertionSet) {
    this.data = paramAssertionData;
    if (paramAssertionSet != null)
      this.nestedPolicy = NestedPolicy.createNestedPolicy(paramAssertionSet); 
    this.parameters = AssertionSet.createAssertionSet(paramCollection);
  }
  
  protected PolicyAssertion(AssertionData paramAssertionData, Collection<? extends PolicyAssertion> paramCollection) {
    if (paramAssertionData == null) {
      this.data = AssertionData.createAssertionData(null);
    } else {
      this.data = paramAssertionData;
    } 
    this.parameters = AssertionSet.createAssertionSet(paramCollection);
  }
  
  public final QName getName() { return this.data.getName(); }
  
  public final String getValue() { return this.data.getValue(); }
  
  public boolean isOptional() { return this.data.isOptionalAttributeSet(); }
  
  public boolean isIgnorable() { return this.data.isIgnorableAttributeSet(); }
  
  public final boolean isPrivate() { return this.data.isPrivateAttributeSet(); }
  
  public final Set<Map.Entry<QName, String>> getAttributesSet() { return this.data.getAttributesSet(); }
  
  public final Map<QName, String> getAttributes() { return this.data.getAttributes(); }
  
  public final String getAttributeValue(QName paramQName) { return this.data.getAttributeValue(paramQName); }
  
  @Deprecated
  public final boolean hasNestedAssertions() { return !this.parameters.isEmpty(); }
  
  public final boolean hasParameters() { return !this.parameters.isEmpty(); }
  
  @Deprecated
  public final Iterator<PolicyAssertion> getNestedAssertionsIterator() { return this.parameters.iterator(); }
  
  public final Iterator<PolicyAssertion> getParametersIterator() { return this.parameters.iterator(); }
  
  boolean isParameter() { return (this.data.getNodeType() == ModelNode.Type.ASSERTION_PARAMETER_NODE); }
  
  public boolean hasNestedPolicy() { return (getNestedPolicy() != null); }
  
  public NestedPolicy getNestedPolicy() { return this.nestedPolicy; }
  
  public <T extends PolicyAssertion> T getImplementation(Class<T> paramClass) { return paramClass.isAssignableFrom(getClass()) ? (T)(PolicyAssertion)paramClass.cast(this) : null; }
  
  public String toString() { return toString(0, new StringBuffer()).toString(); }
  
  protected StringBuffer toString(int paramInt, StringBuffer paramStringBuffer) {
    String str1 = PolicyUtils.Text.createIndent(paramInt);
    String str2 = PolicyUtils.Text.createIndent(paramInt + 1);
    paramStringBuffer.append(str1).append("Assertion[").append(getClass().getName()).append("] {").append(PolicyUtils.Text.NEW_LINE);
    this.data.toString(paramInt + 1, paramStringBuffer);
    paramStringBuffer.append(PolicyUtils.Text.NEW_LINE);
    if (hasParameters()) {
      paramStringBuffer.append(str2).append("parameters {").append(PolicyUtils.Text.NEW_LINE);
      for (PolicyAssertion policyAssertion : this.parameters)
        policyAssertion.toString(paramInt + 2, paramStringBuffer).append(PolicyUtils.Text.NEW_LINE); 
      paramStringBuffer.append(str2).append('}').append(PolicyUtils.Text.NEW_LINE);
    } else {
      paramStringBuffer.append(str2).append("no parameters").append(PolicyUtils.Text.NEW_LINE);
    } 
    if (hasNestedPolicy()) {
      getNestedPolicy().toString(paramInt + 1, paramStringBuffer).append(PolicyUtils.Text.NEW_LINE);
    } else {
      paramStringBuffer.append(str2).append("no nested policy").append(PolicyUtils.Text.NEW_LINE);
    } 
    paramStringBuffer.append(str1).append('}');
    return paramStringBuffer;
  }
  
  boolean isCompatibleWith(PolicyAssertion paramPolicyAssertion, PolicyIntersector.CompatibilityMode paramCompatibilityMode) {
    boolean bool = (this.data.getName().equals(paramPolicyAssertion.data.getName()) && hasNestedPolicy() == paramPolicyAssertion.hasNestedPolicy());
    if (bool && hasNestedPolicy())
      bool = getNestedPolicy().getAssertionSet().isCompatibleWith(paramPolicyAssertion.getNestedPolicy().getAssertionSet(), paramCompatibilityMode); 
    return bool;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof PolicyAssertion))
      return false; 
    PolicyAssertion policyAssertion = (PolicyAssertion)paramObject;
    null = true;
    null = (null && this.data.equals(policyAssertion.data));
    null = (null && this.parameters.equals(policyAssertion.parameters));
    return (null && ((getNestedPolicy() == null) ? (policyAssertion.getNestedPolicy() == null) : getNestedPolicy().equals(policyAssertion.getNestedPolicy())));
  }
  
  public int hashCode() {
    null = 17;
    null = 37 * null + this.data.hashCode();
    null = 37 * null + (hasParameters() ? 17 : 0);
    return 37 * null + (hasNestedPolicy() ? 17 : 0);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\PolicyAssertion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */