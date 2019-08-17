package com.sun.xml.internal.ws.policy.subject;

import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import javax.xml.namespace.QName;

public class PolicyMapKeyConverter {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMapKeyConverter.class);
  
  private final QName serviceName;
  
  private final QName portName;
  
  public PolicyMapKeyConverter(QName paramQName1, QName paramQName2) {
    this.serviceName = paramQName1;
    this.portName = paramQName2;
  }
  
  public PolicyMapKey getPolicyMapKey(WsdlBindingSubject paramWsdlBindingSubject) {
    LOGGER.entering(new Object[] { paramWsdlBindingSubject });
    PolicyMapKey policyMapKey = null;
    if (paramWsdlBindingSubject.isBindingSubject()) {
      policyMapKey = PolicyMap.createWsdlEndpointScopeKey(this.serviceName, this.portName);
    } else if (paramWsdlBindingSubject.isBindingOperationSubject()) {
      policyMapKey = PolicyMap.createWsdlOperationScopeKey(this.serviceName, this.portName, paramWsdlBindingSubject.getName());
    } else if (paramWsdlBindingSubject.isBindingMessageSubject()) {
      if (paramWsdlBindingSubject.getMessageType() == WsdlBindingSubject.WsdlMessageType.FAULT) {
        policyMapKey = PolicyMap.createWsdlFaultMessageScopeKey(this.serviceName, this.portName, paramWsdlBindingSubject.getParent().getName(), paramWsdlBindingSubject.getName());
      } else {
        policyMapKey = PolicyMap.createWsdlMessageScopeKey(this.serviceName, this.portName, paramWsdlBindingSubject.getParent().getName());
      } 
    } 
    LOGGER.exiting(policyMapKey);
    return policyMapKey;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\subject\PolicyMapKeyConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */