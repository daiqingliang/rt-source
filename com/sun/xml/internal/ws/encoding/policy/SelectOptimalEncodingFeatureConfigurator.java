package com.sun.xml.internal.ws.encoding.policy;

import com.sun.xml.internal.ws.api.client.SelectOptimalEncodingFeature;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import java.util.Collection;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceFeature;

public class SelectOptimalEncodingFeatureConfigurator implements PolicyFeatureConfigurator {
  public static final QName enabled = new QName("enabled");
  
  public Collection<WebServiceFeature> getFeatures(PolicyMapKey paramPolicyMapKey, PolicyMap paramPolicyMap) throws PolicyException {
    LinkedList linkedList = new LinkedList();
    if (paramPolicyMapKey != null && paramPolicyMap != null) {
      Policy policy = paramPolicyMap.getEndpointEffectivePolicy(paramPolicyMapKey);
      if (null != policy && policy.contains(EncodingConstants.SELECT_OPTIMAL_ENCODING_ASSERTION))
        for (AssertionSet assertionSet : policy) {
          for (PolicyAssertion policyAssertion : assertionSet) {
            if (EncodingConstants.SELECT_OPTIMAL_ENCODING_ASSERTION.equals(policyAssertion.getName())) {
              String str = policyAssertion.getAttributeValue(enabled);
              boolean bool = (str == null || Boolean.valueOf(str.trim()).booleanValue());
              linkedList.add(new SelectOptimalEncodingFeature(bool));
            } 
          } 
        }  
    } 
    return linkedList;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\policy\SelectOptimalEncodingFeatureConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */