package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.subject.PolicyMapKeyConverter;
import com.sun.xml.internal.ws.policy.subject.WsdlBindingSubject;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import javax.xml.namespace.QName;

public class PolicyMapUtil {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMapUtil.class);
  
  private static final PolicyMerger MERGER = PolicyMerger.getMerger();
  
  public static void rejectAlternatives(PolicyMap paramPolicyMap) throws PolicyException {
    for (Policy policy : paramPolicyMap) {
      if (policy.getNumberOfAssertionSets() > 1)
        throw (PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0035_RECONFIGURE_ALTERNATIVES(policy.getIdOrName()))); 
    } 
  }
  
  public static void insertPolicies(PolicyMap paramPolicyMap, Collection<PolicySubject> paramCollection, QName paramQName1, QName paramQName2) throws PolicyException {
    LOGGER.entering(new Object[] { paramPolicyMap, paramCollection, paramQName1, paramQName2 });
    HashMap hashMap = new HashMap();
    for (PolicySubject policySubject : paramCollection) {
      Object object = policySubject.getSubject();
      if (object instanceof WsdlBindingSubject) {
        WsdlBindingSubject wsdlBindingSubject = (WsdlBindingSubject)object;
        LinkedList linkedList = new LinkedList();
        linkedList.add(policySubject.getEffectivePolicy(MERGER));
        Collection collection = (Collection)hashMap.put(wsdlBindingSubject, linkedList);
        if (collection != null)
          linkedList.addAll(collection); 
      } 
    } 
    PolicyMapKeyConverter policyMapKeyConverter = new PolicyMapKeyConverter(paramQName1, paramQName2);
    for (WsdlBindingSubject wsdlBindingSubject : hashMap.keySet()) {
      PolicySubject policySubject = new PolicySubject(wsdlBindingSubject, (Collection)hashMap.get(wsdlBindingSubject));
      PolicyMapKey policyMapKey = policyMapKeyConverter.getPolicyMapKey(wsdlBindingSubject);
      if (wsdlBindingSubject.isBindingSubject()) {
        paramPolicyMap.putSubject(PolicyMap.ScopeType.ENDPOINT, policyMapKey, policySubject);
        continue;
      } 
      if (wsdlBindingSubject.isBindingOperationSubject()) {
        paramPolicyMap.putSubject(PolicyMap.ScopeType.OPERATION, policyMapKey, policySubject);
        continue;
      } 
      if (wsdlBindingSubject.isBindingMessageSubject())
        switch (wsdlBindingSubject.getMessageType()) {
          case INPUT:
            paramPolicyMap.putSubject(PolicyMap.ScopeType.INPUT_MESSAGE, policyMapKey, policySubject);
          case OUTPUT:
            paramPolicyMap.putSubject(PolicyMap.ScopeType.OUTPUT_MESSAGE, policyMapKey, policySubject);
          case FAULT:
            paramPolicyMap.putSubject(PolicyMap.ScopeType.FAULT_MESSAGE, policyMapKey, policySubject);
        }  
    } 
    LOGGER.exiting();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\PolicyMapUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */