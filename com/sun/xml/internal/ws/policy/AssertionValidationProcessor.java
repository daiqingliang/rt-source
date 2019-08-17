package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import java.util.Collection;
import java.util.LinkedList;

public class AssertionValidationProcessor {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AssertionValidationProcessor.class);
  
  private final Collection<PolicyAssertionValidator> validators = new LinkedList();
  
  private AssertionValidationProcessor() throws PolicyException { this(null); }
  
  protected AssertionValidationProcessor(Collection<PolicyAssertionValidator> paramCollection) throws PolicyException {
    for (PolicyAssertionValidator policyAssertionValidator : (PolicyAssertionValidator[])PolicyUtils.ServiceProvider.load(PolicyAssertionValidator.class))
      this.validators.add(policyAssertionValidator); 
    if (paramCollection != null)
      for (PolicyAssertionValidator policyAssertionValidator : paramCollection)
        this.validators.add(policyAssertionValidator);  
    if (this.validators.size() == 0)
      throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0076_NO_SERVICE_PROVIDERS_FOUND(PolicyAssertionValidator.class.getName()))); 
  }
  
  public static AssertionValidationProcessor getInstance() throws PolicyException { return new AssertionValidationProcessor(); }
  
  public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion paramPolicyAssertion) throws PolicyException {
    PolicyAssertionValidator.Fitness fitness = PolicyAssertionValidator.Fitness.UNKNOWN;
    for (PolicyAssertionValidator policyAssertionValidator : this.validators) {
      fitness = fitness.combine(policyAssertionValidator.validateClientSide(paramPolicyAssertion));
      if (fitness == PolicyAssertionValidator.Fitness.SUPPORTED)
        break; 
    } 
    return fitness;
  }
  
  public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion paramPolicyAssertion) throws PolicyException {
    PolicyAssertionValidator.Fitness fitness = PolicyAssertionValidator.Fitness.UNKNOWN;
    for (PolicyAssertionValidator policyAssertionValidator : this.validators) {
      fitness = fitness.combine(policyAssertionValidator.validateServerSide(paramPolicyAssertion));
      if (fitness == PolicyAssertionValidator.Fitness.SUPPORTED)
        break; 
    } 
    return fitness;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\AssertionValidationProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */