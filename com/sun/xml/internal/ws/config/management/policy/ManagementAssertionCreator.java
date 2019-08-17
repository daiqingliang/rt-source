package com.sun.xml.internal.ws.config.management.policy;

import com.sun.xml.internal.ws.api.config.management.policy.ManagedClientAssertion;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedServiceAssertion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionCreator;
import java.util.Collection;
import javax.xml.namespace.QName;

public class ManagementAssertionCreator implements PolicyAssertionCreator {
  public String[] getSupportedDomainNamespaceURIs() { return new String[] { "http://java.sun.com/xml/ns/metro/management" }; }
  
  public PolicyAssertion createAssertion(AssertionData paramAssertionData, Collection<PolicyAssertion> paramCollection, AssertionSet paramAssertionSet, PolicyAssertionCreator paramPolicyAssertionCreator) throws AssertionCreationException {
    QName qName = paramAssertionData.getName();
    return ManagedServiceAssertion.MANAGED_SERVICE_QNAME.equals(qName) ? new ManagedServiceAssertion(paramAssertionData, paramCollection) : (ManagedClientAssertion.MANAGED_CLIENT_QNAME.equals(qName) ? new ManagedClientAssertion(paramAssertionData, paramCollection) : paramPolicyAssertionCreator.createAssertion(paramAssertionData, paramCollection, paramAssertionSet, null));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\config\management\policy\ManagementAssertionCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */