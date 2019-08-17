package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.policy.jaxws.DefaultPolicyResolver;
import com.sun.xml.internal.ws.util.ServiceFinder;

public abstract class PolicyResolverFactory {
  public static final PolicyResolver DEFAULT_POLICY_RESOLVER = new DefaultPolicyResolver();
  
  public abstract PolicyResolver doCreate();
  
  public static PolicyResolver create() {
    for (PolicyResolverFactory policyResolverFactory : ServiceFinder.find(PolicyResolverFactory.class)) {
      PolicyResolver policyResolver = policyResolverFactory.doCreate();
      if (policyResolver != null)
        return policyResolver; 
    } 
    return DEFAULT_POLICY_RESOLVER;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\policy\PolicyResolverFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */