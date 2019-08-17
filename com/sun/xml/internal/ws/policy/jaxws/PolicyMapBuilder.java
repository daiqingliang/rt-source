package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapExtender;
import com.sun.xml.internal.ws.policy.PolicyMapMutator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

class PolicyMapBuilder {
  private List<BuilderHandler> policyBuilders = new LinkedList();
  
  void registerHandler(BuilderHandler paramBuilderHandler) {
    if (null != paramBuilderHandler)
      this.policyBuilders.add(paramBuilderHandler); 
  }
  
  PolicyMap getPolicyMap(PolicyMapMutator... paramVarArgs) throws PolicyException { return getNewPolicyMap(paramVarArgs); }
  
  private PolicyMap getNewPolicyMap(PolicyMapMutator... paramVarArgs) throws PolicyException {
    HashSet hashSet = new HashSet();
    PolicyMapExtender policyMapExtender = PolicyMapExtender.createPolicyMapExtender();
    hashSet.add(policyMapExtender);
    if (null != paramVarArgs)
      hashSet.addAll(Arrays.asList(paramVarArgs)); 
    PolicyMap policyMap = PolicyMap.createPolicyMap(hashSet);
    for (BuilderHandler builderHandler : this.policyBuilders)
      builderHandler.populate(policyMapExtender); 
    return policyMap;
  }
  
  void unregisterAll() { this.policyBuilders = null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\PolicyMapBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */