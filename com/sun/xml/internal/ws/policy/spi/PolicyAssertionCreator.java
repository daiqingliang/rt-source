package com.sun.xml.internal.ws.policy.spi;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;

public interface PolicyAssertionCreator {
  String[] getSupportedDomainNamespaceURIs();
  
  PolicyAssertion createAssertion(AssertionData paramAssertionData, Collection<PolicyAssertion> paramCollection, AssertionSet paramAssertionSet, PolicyAssertionCreator paramPolicyAssertionCreator) throws AssertionCreationException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\spi\PolicyAssertionCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */