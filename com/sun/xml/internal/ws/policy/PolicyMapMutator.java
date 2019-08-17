package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public abstract class PolicyMapMutator {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMapMutator.class);
  
  private PolicyMap map = null;
  
  public void connect(PolicyMap paramPolicyMap) {
    if (isConnected())
      throw (IllegalStateException)LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0044_POLICY_MAP_MUTATOR_ALREADY_CONNECTED())); 
    this.map = paramPolicyMap;
  }
  
  public PolicyMap getMap() { return this.map; }
  
  public void disconnect() { this.map = null; }
  
  public boolean isConnected() { return (this.map != null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\PolicyMapMutator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */