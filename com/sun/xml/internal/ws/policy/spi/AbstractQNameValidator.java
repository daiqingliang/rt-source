package com.sun.xml.internal.ws.policy.spi;

import com.sun.xml.internal.ws.policy.PolicyAssertion;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;

public abstract class AbstractQNameValidator implements PolicyAssertionValidator {
  private final Set<String> supportedDomains = new HashSet();
  
  private final Collection<QName> serverAssertions;
  
  private final Collection<QName> clientAssertions;
  
  protected AbstractQNameValidator(Collection<QName> paramCollection1, Collection<QName> paramCollection2) {
    if (paramCollection1 != null) {
      this.serverAssertions = new HashSet(paramCollection1);
      for (QName qName : this.serverAssertions)
        this.supportedDomains.add(qName.getNamespaceURI()); 
    } else {
      this.serverAssertions = new HashSet(0);
    } 
    if (paramCollection2 != null) {
      this.clientAssertions = new HashSet(paramCollection2);
      for (QName qName : this.clientAssertions)
        this.supportedDomains.add(qName.getNamespaceURI()); 
    } else {
      this.clientAssertions = new HashSet(0);
    } 
  }
  
  public String[] declareSupportedDomains() { return (String[])this.supportedDomains.toArray(new String[this.supportedDomains.size()]); }
  
  public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion paramPolicyAssertion) { return validateAssertion(paramPolicyAssertion, this.clientAssertions, this.serverAssertions); }
  
  public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion paramPolicyAssertion) { return validateAssertion(paramPolicyAssertion, this.serverAssertions, this.clientAssertions); }
  
  private PolicyAssertionValidator.Fitness validateAssertion(PolicyAssertion paramPolicyAssertion, Collection<QName> paramCollection1, Collection<QName> paramCollection2) {
    QName qName = paramPolicyAssertion.getName();
    return paramCollection1.contains(qName) ? PolicyAssertionValidator.Fitness.SUPPORTED : (paramCollection2.contains(qName) ? PolicyAssertionValidator.Fitness.UNSUPPORTED : PolicyAssertionValidator.Fitness.UNKNOWN);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\spi\AbstractQNameValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */