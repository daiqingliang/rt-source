package com.sun.xml.internal.ws.encoding.policy;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import java.util.Collection;
import java.util.LinkedList;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;

public class MtomFeatureConfigurator implements PolicyFeatureConfigurator {
  public Collection<WebServiceFeature> getFeatures(PolicyMapKey paramPolicyMapKey, PolicyMap paramPolicyMap) throws PolicyException {
    LinkedList linkedList = new LinkedList();
    if (paramPolicyMapKey != null && paramPolicyMap != null) {
      Policy policy = paramPolicyMap.getEndpointEffectivePolicy(paramPolicyMapKey);
      if (null != policy && policy.contains(EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION))
        for (AssertionSet assertionSet : policy) {
          for (PolicyAssertion policyAssertion : assertionSet) {
            if (EncodingConstants.OPTIMIZED_MIME_SERIALIZATION_ASSERTION.equals(policyAssertion.getName()))
              linkedList.add(new MTOMFeature(true)); 
          } 
        }  
    } 
    return linkedList;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\policy\MtomFeatureConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */