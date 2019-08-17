package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.NestedPolicy;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import java.util.Iterator;

public abstract class PolicyModelGenerator {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyModelGenerator.class);
  
  public static PolicyModelGenerator getGenerator() { return getNormalizedGenerator(new PolicySourceModelCreator()); }
  
  protected static PolicyModelGenerator getCompactGenerator(PolicySourceModelCreator paramPolicySourceModelCreator) { return new CompactModelGenerator(paramPolicySourceModelCreator); }
  
  protected static PolicyModelGenerator getNormalizedGenerator(PolicySourceModelCreator paramPolicySourceModelCreator) { return new NormalizedModelGenerator(paramPolicySourceModelCreator); }
  
  public abstract PolicySourceModel translate(Policy paramPolicy) throws PolicyException;
  
  protected abstract ModelNode translate(ModelNode paramModelNode, NestedPolicy paramNestedPolicy);
  
  protected void translate(ModelNode paramModelNode, AssertionSet paramAssertionSet) {
    for (PolicyAssertion policyAssertion : paramAssertionSet) {
      AssertionData assertionData = AssertionData.createAssertionData(policyAssertion.getName(), policyAssertion.getValue(), policyAssertion.getAttributes(), policyAssertion.isOptional(), policyAssertion.isIgnorable());
      ModelNode modelNode = paramModelNode.createChildAssertionNode(assertionData);
      if (policyAssertion.hasNestedPolicy())
        translate(modelNode, policyAssertion.getNestedPolicy()); 
      if (policyAssertion.hasParameters())
        translate(modelNode, policyAssertion.getParametersIterator()); 
    } 
  }
  
  protected void translate(ModelNode paramModelNode, Iterator<PolicyAssertion> paramIterator) {
    while (paramIterator.hasNext()) {
      PolicyAssertion policyAssertion = (PolicyAssertion)paramIterator.next();
      AssertionData assertionData = AssertionData.createAssertionParameterData(policyAssertion.getName(), policyAssertion.getValue(), policyAssertion.getAttributes());
      ModelNode modelNode = paramModelNode.createChildAssertionParameterNode(assertionData);
      if (policyAssertion.hasNestedPolicy())
        throw (IllegalStateException)LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0005_UNEXPECTED_POLICY_ELEMENT_FOUND_IN_ASSERTION_PARAM(policyAssertion))); 
      if (policyAssertion.hasNestedAssertions())
        translate(modelNode, policyAssertion.getNestedAssertionsIterator()); 
    } 
  }
  
  protected static class PolicySourceModelCreator {
    protected PolicySourceModel create(Policy param1Policy) throws PolicyException { return PolicySourceModel.createPolicySourceModel(param1Policy.getNamespaceVersion(), param1Policy.getId(), param1Policy.getName()); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\PolicyModelGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */