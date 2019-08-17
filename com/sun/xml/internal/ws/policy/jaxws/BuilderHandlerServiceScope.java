package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapExtender;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.PolicySubject;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import java.util.Collection;
import java.util.Map;
import javax.xml.namespace.QName;

final class BuilderHandlerServiceScope extends BuilderHandler {
  private final QName service;
  
  BuilderHandlerServiceScope(Collection<String> paramCollection, Map<String, PolicySourceModel> paramMap, Object paramObject, QName paramQName) {
    super(paramCollection, paramMap, paramObject);
    this.service = paramQName;
  }
  
  protected void doPopulate(PolicyMapExtender paramPolicyMapExtender) throws PolicyException {
    PolicyMapKey policyMapKey = PolicyMap.createWsdlServiceScopeKey(this.service);
    for (PolicySubject policySubject : getPolicySubjects())
      paramPolicyMapExtender.putServiceSubject(policyMapKey, policySubject); 
  }
  
  public String toString() { return this.service.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\BuilderHandlerServiceScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */