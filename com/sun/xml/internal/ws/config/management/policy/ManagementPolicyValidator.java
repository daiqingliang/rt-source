package com.sun.xml.internal.ws.config.management.policy;

import com.sun.xml.internal.ws.api.config.management.policy.ManagedClientAssertion;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedServiceAssertion;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import javax.xml.namespace.QName;

public class ManagementPolicyValidator implements PolicyAssertionValidator {
  public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion paramPolicyAssertion) {
    QName qName = paramPolicyAssertion.getName();
    return ManagedClientAssertion.MANAGED_CLIENT_QNAME.equals(qName) ? PolicyAssertionValidator.Fitness.SUPPORTED : (ManagedServiceAssertion.MANAGED_SERVICE_QNAME.equals(qName) ? PolicyAssertionValidator.Fitness.UNSUPPORTED : PolicyAssertionValidator.Fitness.UNKNOWN);
  }
  
  public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion paramPolicyAssertion) {
    QName qName = paramPolicyAssertion.getName();
    return ManagedServiceAssertion.MANAGED_SERVICE_QNAME.equals(qName) ? PolicyAssertionValidator.Fitness.SUPPORTED : (ManagedClientAssertion.MANAGED_CLIENT_QNAME.equals(qName) ? PolicyAssertionValidator.Fitness.UNSUPPORTED : PolicyAssertionValidator.Fitness.UNKNOWN);
  }
  
  public String[] declareSupportedDomains() { return new String[] { "http://java.sun.com/xml/ns/metro/management" }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\config\management\policy\ManagementPolicyValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */