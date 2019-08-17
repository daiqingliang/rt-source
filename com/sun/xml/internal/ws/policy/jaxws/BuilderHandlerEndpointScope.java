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

final class BuilderHandlerEndpointScope extends BuilderHandler {
  private final QName service;
  
  private final QName port;
  
  BuilderHandlerEndpointScope(Collection<String> paramCollection, Map<String, PolicySourceModel> paramMap, Object paramObject, QName paramQName1, QName paramQName2) {
    super(paramCollection, paramMap, paramObject);
    this.service = paramQName1;
    this.port = paramQName2;
  }
  
  protected void doPopulate(PolicyMapExtender paramPolicyMapExtender) throws PolicyException {
    PolicyMapKey policyMapKey = PolicyMap.createWsdlEndpointScopeKey(this.service, this.port);
    for (PolicySubject policySubject : getPolicySubjects())
      paramPolicyMapExtender.putEndpointSubject(policyMapKey, policySubject); 
  }
  
  public String toString() { return this.service.toString() + ":" + this.port.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\BuilderHandlerEndpointScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */