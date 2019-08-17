package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.api.policy.AlternativeSelector;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.ValidationProcessor;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.EffectivePolicyModifier;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import javax.xml.ws.WebServiceException;

public class DefaultPolicyResolver implements PolicyResolver {
  public PolicyMap resolve(PolicyResolver.ServerContext paramServerContext) {
    PolicyMap policyMap = paramServerContext.getPolicyMap();
    if (policyMap != null)
      validateServerPolicyMap(policyMap); 
    return policyMap;
  }
  
  public PolicyMap resolve(PolicyResolver.ClientContext paramClientContext) {
    PolicyMap policyMap = paramClientContext.getPolicyMap();
    if (policyMap != null)
      policyMap = doAlternativeSelection(policyMap); 
    return policyMap;
  }
  
  private void validateServerPolicyMap(PolicyMap paramPolicyMap) {
    try {
      ValidationProcessor validationProcessor = ValidationProcessor.getInstance();
      for (Policy policy : paramPolicyMap) {
        for (AssertionSet assertionSet : policy) {
          for (PolicyAssertion policyAssertion : assertionSet) {
            PolicyAssertionValidator.Fitness fitness = validationProcessor.validateServerSide(policyAssertion);
            if (fitness != PolicyAssertionValidator.Fitness.SUPPORTED)
              throw new PolicyException(PolicyMessages.WSP_1015_SERVER_SIDE_ASSERTION_VALIDATION_FAILED(policyAssertion.getName(), fitness)); 
          } 
        } 
      } 
    } catch (PolicyException policyException) {
      throw new WebServiceException(policyException);
    } 
  }
  
  private PolicyMap doAlternativeSelection(PolicyMap paramPolicyMap) {
    EffectivePolicyModifier effectivePolicyModifier = EffectivePolicyModifier.createEffectivePolicyModifier();
    effectivePolicyModifier.connect(paramPolicyMap);
    try {
      AlternativeSelector.doSelection(effectivePolicyModifier);
    } catch (PolicyException policyException) {
      throw new WebServiceException(policyException);
    } 
    return paramPolicyMap;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\DefaultPolicyResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */