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

final class BuilderHandlerMessageScope extends BuilderHandler {
  private final QName service;
  
  private final QName port;
  
  private final QName operation;
  
  private final QName message;
  
  private final Scope scope;
  
  BuilderHandlerMessageScope(Collection<String> paramCollection, Map<String, PolicySourceModel> paramMap, Object paramObject, Scope paramScope, QName paramQName1, QName paramQName2, QName paramQName3, QName paramQName4) {
    super(paramCollection, paramMap, paramObject);
    this.service = paramQName1;
    this.port = paramQName2;
    this.operation = paramQName3;
    this.scope = paramScope;
    this.message = paramQName4;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof BuilderHandlerMessageScope))
      return false; 
    BuilderHandlerMessageScope builderHandlerMessageScope = (BuilderHandlerMessageScope)paramObject;
    boolean bool = true;
    bool = (bool && ((this.policySubject == null) ? (builderHandlerMessageScope.policySubject == null) : this.policySubject.equals(builderHandlerMessageScope.policySubject)));
    bool = (bool && ((this.scope == null) ? (builderHandlerMessageScope.scope == null) : this.scope.equals(builderHandlerMessageScope.scope)));
    bool = (bool && ((this.message == null) ? (builderHandlerMessageScope.message == null) : this.message.equals(builderHandlerMessageScope.message)));
    if (this.scope != Scope.FaultMessageScope) {
      bool = (bool && ((this.service == null) ? (builderHandlerMessageScope.service == null) : this.service.equals(builderHandlerMessageScope.service)));
      bool = (bool && ((this.port == null) ? (builderHandlerMessageScope.port == null) : this.port.equals(builderHandlerMessageScope.port)));
      bool = (bool && ((this.operation == null) ? (builderHandlerMessageScope.operation == null) : this.operation.equals(builderHandlerMessageScope.operation)));
    } 
    return bool;
  }
  
  public int hashCode() {
    byte b = 19;
    b = 31 * b + ((this.policySubject == null) ? 0 : this.policySubject.hashCode());
    b = 31 * b + ((this.message == null) ? 0 : this.message.hashCode());
    b = 31 * b + ((this.scope == null) ? 0 : this.scope.hashCode());
    if (this.scope != Scope.FaultMessageScope) {
      b = 31 * b + ((this.service == null) ? 0 : this.service.hashCode());
      b = 31 * b + ((this.port == null) ? 0 : this.port.hashCode());
      b = 31 * b + ((this.operation == null) ? 0 : this.operation.hashCode());
    } 
    return b;
  }
  
  protected void doPopulate(PolicyMapExtender paramPolicyMapExtender) throws PolicyException {
    PolicyMapKey policyMapKey;
    if (Scope.FaultMessageScope == this.scope) {
      policyMapKey = PolicyMap.createWsdlFaultMessageScopeKey(this.service, this.port, this.operation, this.message);
    } else {
      policyMapKey = PolicyMap.createWsdlMessageScopeKey(this.service, this.port, this.operation);
    } 
    if (Scope.InputMessageScope == this.scope) {
      for (PolicySubject policySubject : getPolicySubjects())
        paramPolicyMapExtender.putInputMessageSubject(policyMapKey, policySubject); 
    } else if (Scope.OutputMessageScope == this.scope) {
      for (PolicySubject policySubject : getPolicySubjects())
        paramPolicyMapExtender.putOutputMessageSubject(policyMapKey, policySubject); 
    } else if (Scope.FaultMessageScope == this.scope) {
      for (PolicySubject policySubject : getPolicySubjects())
        paramPolicyMapExtender.putFaultMessageSubject(policyMapKey, policySubject); 
    } 
  }
  
  enum Scope {
    InputMessageScope, OutputMessageScope, FaultMessageScope;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\BuilderHandlerMessageScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */