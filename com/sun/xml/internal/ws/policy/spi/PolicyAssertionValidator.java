package com.sun.xml.internal.ws.policy.spi;

import com.sun.xml.internal.ws.policy.PolicyAssertion;

public interface PolicyAssertionValidator {
  Fitness validateClientSide(PolicyAssertion paramPolicyAssertion);
  
  Fitness validateServerSide(PolicyAssertion paramPolicyAssertion);
  
  String[] declareSupportedDomains();
  
  public enum Fitness {
    UNKNOWN, INVALID, UNSUPPORTED, SUPPORTED;
    
    public Fitness combine(Fitness param1Fitness) { return (compareTo(param1Fitness) < 0) ? param1Fitness : this; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\spi\PolicyAssertionValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */