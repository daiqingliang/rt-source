package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.NestedPolicy;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

class CompactModelGenerator extends PolicyModelGenerator {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(CompactModelGenerator.class);
  
  private final PolicyModelGenerator.PolicySourceModelCreator sourceModelCreator;
  
  CompactModelGenerator(PolicyModelGenerator.PolicySourceModelCreator paramPolicySourceModelCreator) { this.sourceModelCreator = paramPolicySourceModelCreator; }
  
  public PolicySourceModel translate(Policy paramPolicy) throws PolicyException {
    LOGGER.entering(new Object[] { paramPolicy });
    PolicySourceModel policySourceModel = null;
    if (paramPolicy == null) {
      LOGGER.fine(LocalizationMessages.WSP_0047_POLICY_IS_NULL_RETURNING());
    } else {
      policySourceModel = this.sourceModelCreator.create(paramPolicy);
      ModelNode modelNode1 = policySourceModel.getRootNode();
      int i = paramPolicy.getNumberOfAssertionSets();
      if (i > 1)
        modelNode1 = modelNode1.createChildExactlyOneNode(); 
      ModelNode modelNode2 = modelNode1;
      for (AssertionSet assertionSet : paramPolicy) {
        if (i > 1)
          modelNode2 = modelNode1.createChildAllNode(); 
        for (PolicyAssertion policyAssertion : assertionSet) {
          AssertionData assertionData = AssertionData.createAssertionData(policyAssertion.getName(), policyAssertion.getValue(), policyAssertion.getAttributes(), policyAssertion.isOptional(), policyAssertion.isIgnorable());
          ModelNode modelNode = modelNode2.createChildAssertionNode(assertionData);
          if (policyAssertion.hasNestedPolicy())
            translate(modelNode, policyAssertion.getNestedPolicy()); 
          if (policyAssertion.hasParameters())
            translate(modelNode, policyAssertion.getParametersIterator()); 
        } 
      } 
    } 
    LOGGER.exiting(policySourceModel);
    return policySourceModel;
  }
  
  protected ModelNode translate(ModelNode paramModelNode, NestedPolicy paramNestedPolicy) {
    ModelNode modelNode = paramModelNode.createChildPolicyNode();
    AssertionSet assertionSet = paramNestedPolicy.getAssertionSet();
    translate(modelNode, assertionSet);
    return modelNode;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\CompactModelGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */