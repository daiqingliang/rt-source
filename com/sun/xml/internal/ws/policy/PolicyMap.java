package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;

public final class PolicyMap extends Object implements Iterable<Policy> {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMap.class);
  
  private static final PolicyMapKeyHandler serviceKeyHandler = new PolicyMapKeyHandler() {
      public boolean areEqual(PolicyMapKey param1PolicyMapKey1, PolicyMapKey param1PolicyMapKey2) { return param1PolicyMapKey1.getService().equals(param1PolicyMapKey2.getService()); }
      
      public int generateHashCode(PolicyMapKey param1PolicyMapKey) {
        null = 17;
        return 37 * null + param1PolicyMapKey.getService().hashCode();
      }
    };
  
  private static final PolicyMapKeyHandler endpointKeyHandler = new PolicyMapKeyHandler() {
      public boolean areEqual(PolicyMapKey param1PolicyMapKey1, PolicyMapKey param1PolicyMapKey2) {
        null = true;
        null = (null && param1PolicyMapKey1.getService().equals(param1PolicyMapKey2.getService()));
        return (null && ((param1PolicyMapKey1.getPort() == null) ? (param1PolicyMapKey2.getPort() == null) : param1PolicyMapKey1.getPort().equals(param1PolicyMapKey2.getPort())));
      }
      
      public int generateHashCode(PolicyMapKey param1PolicyMapKey) {
        null = 17;
        null = 37 * null + param1PolicyMapKey.getService().hashCode();
        return 37 * null + ((param1PolicyMapKey.getPort() == null) ? 0 : param1PolicyMapKey.getPort().hashCode());
      }
    };
  
  private static final PolicyMapKeyHandler operationAndInputOutputMessageKeyHandler = new PolicyMapKeyHandler() {
      public boolean areEqual(PolicyMapKey param1PolicyMapKey1, PolicyMapKey param1PolicyMapKey2) {
        null = true;
        null = (null && param1PolicyMapKey1.getService().equals(param1PolicyMapKey2.getService()));
        null = (null && ((param1PolicyMapKey1.getPort() == null) ? (param1PolicyMapKey2.getPort() == null) : param1PolicyMapKey1.getPort().equals(param1PolicyMapKey2.getPort())));
        return (null && ((param1PolicyMapKey1.getOperation() == null) ? (param1PolicyMapKey2.getOperation() == null) : param1PolicyMapKey1.getOperation().equals(param1PolicyMapKey2.getOperation())));
      }
      
      public int generateHashCode(PolicyMapKey param1PolicyMapKey) {
        null = 17;
        null = 37 * null + param1PolicyMapKey.getService().hashCode();
        null = 37 * null + ((param1PolicyMapKey.getPort() == null) ? 0 : param1PolicyMapKey.getPort().hashCode());
        return 37 * null + ((param1PolicyMapKey.getOperation() == null) ? 0 : param1PolicyMapKey.getOperation().hashCode());
      }
    };
  
  private static final PolicyMapKeyHandler faultMessageHandler = new PolicyMapKeyHandler() {
      public boolean areEqual(PolicyMapKey param1PolicyMapKey1, PolicyMapKey param1PolicyMapKey2) {
        null = true;
        null = (null && param1PolicyMapKey1.getService().equals(param1PolicyMapKey2.getService()));
        null = (null && ((param1PolicyMapKey1.getPort() == null) ? (param1PolicyMapKey2.getPort() == null) : param1PolicyMapKey1.getPort().equals(param1PolicyMapKey2.getPort())));
        null = (null && ((param1PolicyMapKey1.getOperation() == null) ? (param1PolicyMapKey2.getOperation() == null) : param1PolicyMapKey1.getOperation().equals(param1PolicyMapKey2.getOperation())));
        return (null && ((param1PolicyMapKey1.getFaultMessage() == null) ? (param1PolicyMapKey2.getFaultMessage() == null) : param1PolicyMapKey1.getFaultMessage().equals(param1PolicyMapKey2.getFaultMessage())));
      }
      
      public int generateHashCode(PolicyMapKey param1PolicyMapKey) {
        null = 17;
        null = 37 * null + param1PolicyMapKey.getService().hashCode();
        null = 37 * null + ((param1PolicyMapKey.getPort() == null) ? 0 : param1PolicyMapKey.getPort().hashCode());
        null = 37 * null + ((param1PolicyMapKey.getOperation() == null) ? 0 : param1PolicyMapKey.getOperation().hashCode());
        return 37 * null + ((param1PolicyMapKey.getFaultMessage() == null) ? 0 : param1PolicyMapKey.getFaultMessage().hashCode());
      }
    };
  
  private static final PolicyMerger merger = PolicyMerger.getMerger();
  
  private final ScopeMap serviceMap = new ScopeMap(merger, serviceKeyHandler);
  
  private final ScopeMap endpointMap = new ScopeMap(merger, endpointKeyHandler);
  
  private final ScopeMap operationMap = new ScopeMap(merger, operationAndInputOutputMessageKeyHandler);
  
  private final ScopeMap inputMessageMap = new ScopeMap(merger, operationAndInputOutputMessageKeyHandler);
  
  private final ScopeMap outputMessageMap = new ScopeMap(merger, operationAndInputOutputMessageKeyHandler);
  
  private final ScopeMap faultMessageMap = new ScopeMap(merger, faultMessageHandler);
  
  public static PolicyMap createPolicyMap(Collection<? extends PolicyMapMutator> paramCollection) {
    PolicyMap policyMap = new PolicyMap();
    if (paramCollection != null && !paramCollection.isEmpty())
      for (PolicyMapMutator policyMapMutator : paramCollection)
        policyMapMutator.connect(policyMap);  
    return policyMap;
  }
  
  public Policy getServiceEffectivePolicy(PolicyMapKey paramPolicyMapKey) throws PolicyException { return this.serviceMap.getEffectivePolicy(paramPolicyMapKey); }
  
  public Policy getEndpointEffectivePolicy(PolicyMapKey paramPolicyMapKey) throws PolicyException { return this.endpointMap.getEffectivePolicy(paramPolicyMapKey); }
  
  public Policy getOperationEffectivePolicy(PolicyMapKey paramPolicyMapKey) throws PolicyException { return this.operationMap.getEffectivePolicy(paramPolicyMapKey); }
  
  public Policy getInputMessageEffectivePolicy(PolicyMapKey paramPolicyMapKey) throws PolicyException { return this.inputMessageMap.getEffectivePolicy(paramPolicyMapKey); }
  
  public Policy getOutputMessageEffectivePolicy(PolicyMapKey paramPolicyMapKey) throws PolicyException { return this.outputMessageMap.getEffectivePolicy(paramPolicyMapKey); }
  
  public Policy getFaultMessageEffectivePolicy(PolicyMapKey paramPolicyMapKey) throws PolicyException { return this.faultMessageMap.getEffectivePolicy(paramPolicyMapKey); }
  
  public Collection<PolicyMapKey> getAllServiceScopeKeys() { return this.serviceMap.getAllKeys(); }
  
  public Collection<PolicyMapKey> getAllEndpointScopeKeys() { return this.endpointMap.getAllKeys(); }
  
  public Collection<PolicyMapKey> getAllOperationScopeKeys() { return this.operationMap.getAllKeys(); }
  
  public Collection<PolicyMapKey> getAllInputMessageScopeKeys() { return this.inputMessageMap.getAllKeys(); }
  
  public Collection<PolicyMapKey> getAllOutputMessageScopeKeys() { return this.outputMessageMap.getAllKeys(); }
  
  public Collection<PolicyMapKey> getAllFaultMessageScopeKeys() { return this.faultMessageMap.getAllKeys(); }
  
  void putSubject(ScopeType paramScopeType, PolicyMapKey paramPolicyMapKey, PolicySubject paramPolicySubject) {
    switch (paramScopeType) {
      case SERVICE:
        this.serviceMap.putSubject(paramPolicyMapKey, paramPolicySubject);
        return;
      case ENDPOINT:
        this.endpointMap.putSubject(paramPolicyMapKey, paramPolicySubject);
        return;
      case OPERATION:
        this.operationMap.putSubject(paramPolicyMapKey, paramPolicySubject);
        return;
      case INPUT_MESSAGE:
        this.inputMessageMap.putSubject(paramPolicyMapKey, paramPolicySubject);
        return;
      case OUTPUT_MESSAGE:
        this.outputMessageMap.putSubject(paramPolicyMapKey, paramPolicySubject);
        return;
      case FAULT_MESSAGE:
        this.faultMessageMap.putSubject(paramPolicyMapKey, paramPolicySubject);
        return;
    } 
    throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0002_UNRECOGNIZED_SCOPE_TYPE(paramScopeType)));
  }
  
  void setNewEffectivePolicyForScope(ScopeType paramScopeType, PolicyMapKey paramPolicyMapKey, Policy paramPolicy) throws IllegalArgumentException {
    if (paramScopeType == null || paramPolicyMapKey == null || paramPolicy == null)
      throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0062_INPUT_PARAMS_MUST_NOT_BE_NULL())); 
    switch (paramScopeType) {
      case SERVICE:
        this.serviceMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
        return;
      case ENDPOINT:
        this.endpointMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
        return;
      case OPERATION:
        this.operationMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
        return;
      case INPUT_MESSAGE:
        this.inputMessageMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
        return;
      case OUTPUT_MESSAGE:
        this.outputMessageMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
        return;
      case FAULT_MESSAGE:
        this.faultMessageMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
        return;
    } 
    throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0002_UNRECOGNIZED_SCOPE_TYPE(paramScopeType)));
  }
  
  public Collection<PolicySubject> getPolicySubjects() {
    LinkedList linkedList = new LinkedList();
    addSubjects(linkedList, this.serviceMap);
    addSubjects(linkedList, this.endpointMap);
    addSubjects(linkedList, this.operationMap);
    addSubjects(linkedList, this.inputMessageMap);
    addSubjects(linkedList, this.outputMessageMap);
    addSubjects(linkedList, this.faultMessageMap);
    return linkedList;
  }
  
  public boolean isInputMessageSubject(PolicySubject paramPolicySubject) {
    for (PolicyScope policyScope : this.inputMessageMap.getStoredScopes()) {
      if (policyScope.getPolicySubjects().contains(paramPolicySubject))
        return true; 
    } 
    return false;
  }
  
  public boolean isOutputMessageSubject(PolicySubject paramPolicySubject) {
    for (PolicyScope policyScope : this.outputMessageMap.getStoredScopes()) {
      if (policyScope.getPolicySubjects().contains(paramPolicySubject))
        return true; 
    } 
    return false;
  }
  
  public boolean isFaultMessageSubject(PolicySubject paramPolicySubject) {
    for (PolicyScope policyScope : this.faultMessageMap.getStoredScopes()) {
      if (policyScope.getPolicySubjects().contains(paramPolicySubject))
        return true; 
    } 
    return false;
  }
  
  public boolean isEmpty() { return (this.serviceMap.isEmpty() && this.endpointMap.isEmpty() && this.operationMap.isEmpty() && this.inputMessageMap.isEmpty() && this.outputMessageMap.isEmpty() && this.faultMessageMap.isEmpty()); }
  
  private void addSubjects(Collection<PolicySubject> paramCollection, ScopeMap paramScopeMap) {
    for (PolicyScope policyScope : paramScopeMap.getStoredScopes()) {
      Collection collection = policyScope.getPolicySubjects();
      paramCollection.addAll(collection);
    } 
  }
  
  public static PolicyMapKey createWsdlServiceScopeKey(QName paramQName) throws IllegalArgumentException {
    if (paramQName == null)
      throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0031_SERVICE_PARAM_MUST_NOT_BE_NULL())); 
    return new PolicyMapKey(paramQName, null, null, serviceKeyHandler);
  }
  
  public static PolicyMapKey createWsdlEndpointScopeKey(QName paramQName1, QName paramQName2) throws IllegalArgumentException {
    if (paramQName1 == null || paramQName2 == null)
      throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0033_SERVICE_AND_PORT_PARAM_MUST_NOT_BE_NULL(paramQName1, paramQName2))); 
    return new PolicyMapKey(paramQName1, paramQName2, null, endpointKeyHandler);
  }
  
  public static PolicyMapKey createWsdlOperationScopeKey(QName paramQName1, QName paramQName2, QName paramQName3) throws IllegalArgumentException { return createOperationOrInputOutputMessageKey(paramQName1, paramQName2, paramQName3); }
  
  public static PolicyMapKey createWsdlMessageScopeKey(QName paramQName1, QName paramQName2, QName paramQName3) throws IllegalArgumentException { return createOperationOrInputOutputMessageKey(paramQName1, paramQName2, paramQName3); }
  
  public static PolicyMapKey createWsdlFaultMessageScopeKey(QName paramQName1, QName paramQName2, QName paramQName3, QName paramQName4) throws IllegalArgumentException {
    if (paramQName1 == null || paramQName2 == null || paramQName3 == null || paramQName4 == null)
      throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0030_SERVICE_PORT_OPERATION_FAULT_MSG_PARAM_MUST_NOT_BE_NULL(paramQName1, paramQName2, paramQName3, paramQName4))); 
    return new PolicyMapKey(paramQName1, paramQName2, paramQName3, paramQName4, faultMessageHandler);
  }
  
  private static PolicyMapKey createOperationOrInputOutputMessageKey(QName paramQName1, QName paramQName2, QName paramQName3) throws IllegalArgumentException {
    if (paramQName1 == null || paramQName2 == null || paramQName3 == null)
      throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0029_SERVICE_PORT_OPERATION_PARAM_MUST_NOT_BE_NULL(paramQName1, paramQName2, paramQName3))); 
    return new PolicyMapKey(paramQName1, paramQName2, paramQName3, operationAndInputOutputMessageKeyHandler);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    if (null != this.serviceMap)
      stringBuffer.append("\nServiceMap=").append(this.serviceMap); 
    if (null != this.endpointMap)
      stringBuffer.append("\nEndpointMap=").append(this.endpointMap); 
    if (null != this.operationMap)
      stringBuffer.append("\nOperationMap=").append(this.operationMap); 
    if (null != this.inputMessageMap)
      stringBuffer.append("\nInputMessageMap=").append(this.inputMessageMap); 
    if (null != this.outputMessageMap)
      stringBuffer.append("\nOutputMessageMap=").append(this.outputMessageMap); 
    if (null != this.faultMessageMap)
      stringBuffer.append("\nFaultMessageMap=").append(this.faultMessageMap); 
    return stringBuffer.toString();
  }
  
  public Iterator<Policy> iterator() { return new Iterator<Policy>() {
        private final Iterator<Iterator<Policy>> mainIterator;
        
        private Iterator<Policy> currentScopeIterator;
        
        public boolean hasNext() {
          while (!this.currentScopeIterator.hasNext()) {
            if (this.mainIterator.hasNext()) {
              this.currentScopeIterator = (Iterator)this.mainIterator.next();
              continue;
            } 
            return false;
          } 
          return true;
        }
        
        public Policy next() {
          if (hasNext())
            return (Policy)this.currentScopeIterator.next(); 
          throw (NoSuchElementException)LOGGER.logSevereException(new NoSuchElementException(LocalizationMessages.WSP_0054_NO_MORE_ELEMS_IN_POLICY_MAP()));
        }
        
        public void remove() { throw (UnsupportedOperationException)LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0034_REMOVE_OPERATION_NOT_SUPPORTED())); }
      }; }
  
  private static final class ScopeMap extends Object implements Iterable<Policy> {
    private final Map<PolicyMapKey, PolicyScope> internalMap = new HashMap();
    
    private final PolicyMapKeyHandler scopeKeyHandler;
    
    private final PolicyMerger merger;
    
    ScopeMap(PolicyMerger param1PolicyMerger, PolicyMapKeyHandler param1PolicyMapKeyHandler) {
      this.merger = param1PolicyMerger;
      this.scopeKeyHandler = param1PolicyMapKeyHandler;
    }
    
    Policy getEffectivePolicy(PolicyMapKey param1PolicyMapKey) throws PolicyException {
      PolicyScope policyScope = (PolicyScope)this.internalMap.get(createLocalCopy(param1PolicyMapKey));
      return (policyScope == null) ? null : policyScope.getEffectivePolicy(this.merger);
    }
    
    void putSubject(PolicyMapKey param1PolicyMapKey, PolicySubject param1PolicySubject) {
      PolicyMapKey policyMapKey = createLocalCopy(param1PolicyMapKey);
      PolicyScope policyScope = (PolicyScope)this.internalMap.get(policyMapKey);
      if (policyScope == null) {
        LinkedList linkedList = new LinkedList();
        linkedList.add(param1PolicySubject);
        this.internalMap.put(policyMapKey, new PolicyScope(linkedList));
      } else {
        policyScope.attach(param1PolicySubject);
      } 
    }
    
    void setNewEffectivePolicy(PolicyMapKey param1PolicyMapKey, Policy param1Policy) {
      PolicySubject policySubject = new PolicySubject(param1PolicyMapKey, param1Policy);
      PolicyMapKey policyMapKey = createLocalCopy(param1PolicyMapKey);
      PolicyScope policyScope = (PolicyScope)this.internalMap.get(policyMapKey);
      if (policyScope == null) {
        LinkedList linkedList = new LinkedList();
        linkedList.add(policySubject);
        this.internalMap.put(policyMapKey, new PolicyScope(linkedList));
      } else {
        policyScope.dettachAllSubjects();
        policyScope.attach(policySubject);
      } 
    }
    
    Collection<PolicyScope> getStoredScopes() { return this.internalMap.values(); }
    
    Set<PolicyMapKey> getAllKeys() { return this.internalMap.keySet(); }
    
    private PolicyMapKey createLocalCopy(PolicyMapKey param1PolicyMapKey) {
      if (param1PolicyMapKey == null)
        throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0045_POLICY_MAP_KEY_MUST_NOT_BE_NULL())); 
      PolicyMapKey policyMapKey = new PolicyMapKey(param1PolicyMapKey);
      policyMapKey.setHandler(this.scopeKeyHandler);
      return policyMapKey;
    }
    
    public Iterator<Policy> iterator() { return new Iterator<Policy>() {
          private final Iterator<PolicyMapKey> keysIterator = PolicyMap.ScopeMap.this.internalMap.keySet().iterator();
          
          public boolean hasNext() { return this.keysIterator.hasNext(); }
          
          public Policy next() {
            PolicyMapKey policyMapKey = (PolicyMapKey)this.keysIterator.next();
            try {
              return PolicyMap.ScopeMap.this.getEffectivePolicy(policyMapKey);
            } catch (PolicyException policyException) {
              throw (IllegalStateException)LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0069_EXCEPTION_WHILE_RETRIEVING_EFFECTIVE_POLICY_FOR_KEY(policyMapKey), policyException));
            } 
          }
          
          public void remove() { throw (UnsupportedOperationException)LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0034_REMOVE_OPERATION_NOT_SUPPORTED())); }
        }; }
    
    public boolean isEmpty() { return this.internalMap.isEmpty(); }
    
    public String toString() { return this.internalMap.toString(); }
  }
  
  enum ScopeType {
    SERVICE, ENDPOINT, OPERATION, INPUT_MESSAGE, OUTPUT_MESSAGE, FAULT_MESSAGE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\PolicyMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */