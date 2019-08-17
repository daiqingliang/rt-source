package com.sun.xml.internal.ws.api.config.management.policy;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.resources.ManagementMessages;
import java.util.Collection;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class ManagedClientAssertion extends ManagementAssertion {
  public static final QName MANAGED_CLIENT_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ManagedClient");
  
  private static final Logger LOGGER = Logger.getLogger(ManagedClientAssertion.class);
  
  public static ManagedClientAssertion getAssertion(WSPortInfo paramWSPortInfo) throws WebServiceException {
    if (paramWSPortInfo == null)
      return null; 
    LOGGER.entering(new Object[] { paramWSPortInfo });
    PolicyMap policyMap = paramWSPortInfo.getPolicyMap();
    ManagedClientAssertion managedClientAssertion = (ManagedClientAssertion)ManagementAssertion.getAssertion(MANAGED_CLIENT_QNAME, policyMap, paramWSPortInfo.getServiceName(), paramWSPortInfo.getPortName(), ManagedClientAssertion.class);
    LOGGER.exiting(managedClientAssertion);
    return managedClientAssertion;
  }
  
  public ManagedClientAssertion(AssertionData paramAssertionData, Collection<PolicyAssertion> paramCollection) throws AssertionCreationException { super(MANAGED_CLIENT_QNAME, paramAssertionData, paramCollection); }
  
  public boolean isManagementEnabled() {
    String str = getAttributeValue(MANAGEMENT_ATTRIBUTE_QNAME);
    if (str != null && (str.trim().toLowerCase().equals("on") || Boolean.parseBoolean(str)))
      LOGGER.warning(ManagementMessages.WSM_1006_CLIENT_MANAGEMENT_ENABLED()); 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\config\management\policy\ManagedClientAssertion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */