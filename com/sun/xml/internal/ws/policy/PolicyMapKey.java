package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import javax.xml.namespace.QName;

public final class PolicyMapKey {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMapKey.class);
  
  private final QName service;
  
  private final QName port;
  
  private final QName operation;
  
  private final QName faultMessage;
  
  private PolicyMapKeyHandler handler;
  
  PolicyMapKey(QName paramQName1, QName paramQName2, QName paramQName3, PolicyMapKeyHandler paramPolicyMapKeyHandler) { this(paramQName1, paramQName2, paramQName3, null, paramPolicyMapKeyHandler); }
  
  PolicyMapKey(QName paramQName1, QName paramQName2, QName paramQName3, QName paramQName4, PolicyMapKeyHandler paramPolicyMapKeyHandler) {
    if (paramPolicyMapKeyHandler == null)
      throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0046_POLICY_MAP_KEY_HANDLER_NOT_SET())); 
    this.service = paramQName1;
    this.port = paramQName2;
    this.operation = paramQName3;
    this.faultMessage = paramQName4;
    this.handler = paramPolicyMapKeyHandler;
  }
  
  PolicyMapKey(PolicyMapKey paramPolicyMapKey) {
    this.service = paramPolicyMapKey.service;
    this.port = paramPolicyMapKey.port;
    this.operation = paramPolicyMapKey.operation;
    this.faultMessage = paramPolicyMapKey.faultMessage;
    this.handler = paramPolicyMapKey.handler;
  }
  
  public QName getOperation() { return this.operation; }
  
  public QName getPort() { return this.port; }
  
  public QName getService() { return this.service; }
  
  void setHandler(PolicyMapKeyHandler paramPolicyMapKeyHandler) {
    if (paramPolicyMapKeyHandler == null)
      throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0046_POLICY_MAP_KEY_HANDLER_NOT_SET())); 
    this.handler = paramPolicyMapKeyHandler;
  }
  
  public QName getFaultMessage() { return this.faultMessage; }
  
  public boolean equals(Object paramObject) { return (this == paramObject) ? true : ((paramObject == null) ? false : ((paramObject instanceof PolicyMapKey) ? this.handler.areEqual(this, (PolicyMapKey)paramObject) : 0)); }
  
  public int hashCode() { return this.handler.generateHashCode(this); }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("PolicyMapKey(");
    stringBuffer.append(this.service).append(", ").append(this.port).append(", ").append(this.operation).append(", ").append(this.faultMessage);
    return stringBuffer.append(")").toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\PolicyMapKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */