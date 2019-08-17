package com.sun.xml.internal.ws.policy.jaxws.spi;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import java.util.Collection;
import javax.xml.ws.WebServiceFeature;

public interface PolicyFeatureConfigurator {
  Collection<WebServiceFeature> getFeatures(PolicyMapKey paramPolicyMapKey, PolicyMap paramPolicyMap) throws PolicyException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\spi\PolicyFeatureConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */