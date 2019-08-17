package com.sun.xml.internal.ws.addressing.policy;

import com.sun.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.policy.NestedPolicy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import java.util.ArrayList;
import javax.xml.namespace.QName;

public class AddressingPolicyValidator implements PolicyAssertionValidator {
  private static final ArrayList<QName> supportedAssertions = new ArrayList();
  
  private static final PolicyLogger LOGGER;
  
  public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion paramPolicyAssertion) { return supportedAssertions.contains(paramPolicyAssertion.getName()) ? PolicyAssertionValidator.Fitness.SUPPORTED : PolicyAssertionValidator.Fitness.UNKNOWN; }
  
  public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion paramPolicyAssertion) {
    if (!supportedAssertions.contains(paramPolicyAssertion.getName()))
      return PolicyAssertionValidator.Fitness.UNKNOWN; 
    if (paramPolicyAssertion.getName().equals(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION)) {
      NestedPolicy nestedPolicy = paramPolicyAssertion.getNestedPolicy();
      if (nestedPolicy != null) {
        boolean bool1 = false;
        boolean bool2 = false;
        for (PolicyAssertion policyAssertion : nestedPolicy.getAssertionSet()) {
          if (policyAssertion.getName().equals(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION)) {
            bool1 = true;
            continue;
          } 
          if (policyAssertion.getName().equals(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION)) {
            bool2 = true;
            continue;
          } 
          LOGGER.warning("Found unsupported assertion:\n" + policyAssertion + "\nnested into assertion:\n" + paramPolicyAssertion);
          return PolicyAssertionValidator.Fitness.UNSUPPORTED;
        } 
        if (bool1 && bool2) {
          LOGGER.warning("Only one among AnonymousResponses and NonAnonymousResponses can be nested in an Addressing assertion");
          return PolicyAssertionValidator.Fitness.INVALID;
        } 
      } 
    } 
    return PolicyAssertionValidator.Fitness.SUPPORTED;
  }
  
  public String[] declareSupportedDomains() { return new String[] { AddressingVersion.MEMBER.policyNsUri, AddressingVersion.W3C.policyNsUri, "http://www.w3.org/2007/05/addressing/metadata" }; }
  
  static  {
    supportedAssertions.add(new QName(AddressingVersion.MEMBER.policyNsUri, "UsingAddressing"));
    supportedAssertions.add(W3CAddressingMetadataConstants.WSAM_ADDRESSING_ASSERTION);
    supportedAssertions.add(W3CAddressingMetadataConstants.WSAM_ANONYMOUS_NESTED_ASSERTION);
    supportedAssertions.add(W3CAddressingMetadataConstants.WSAM_NONANONYMOUS_NESTED_ASSERTION);
    LOGGER = PolicyLogger.getLogger(AddressingPolicyValidator.class);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\addressing\policy\AddressingPolicyValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */