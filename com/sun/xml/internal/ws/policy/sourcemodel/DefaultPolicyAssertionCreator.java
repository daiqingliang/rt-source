package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionCreator;
import java.util.Collection;

class DefaultPolicyAssertionCreator implements PolicyAssertionCreator {
  public String[] getSupportedDomainNamespaceURIs() { return null; }
  
  public PolicyAssertion createAssertion(AssertionData paramAssertionData, Collection<PolicyAssertion> paramCollection, AssertionSet paramAssertionSet, PolicyAssertionCreator paramPolicyAssertionCreator) throws AssertionCreationException { return new DefaultPolicyAssertion(paramAssertionData, paramCollection, paramAssertionSet); }
  
  private static final class DefaultPolicyAssertion extends PolicyAssertion {
    DefaultPolicyAssertion(AssertionData param1AssertionData, Collection<PolicyAssertion> param1Collection, AssertionSet param1AssertionSet) { super(param1AssertionData, param1Collection, param1AssertionSet); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\DefaultPolicyAssertionCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */