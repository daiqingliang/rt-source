package com.sun.xml.internal.ws.policy.subject;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import javax.xml.namespace.QName;

public class WsdlBindingSubject {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(WsdlBindingSubject.class);
  
  private final QName name;
  
  private final WsdlMessageType messageType;
  
  private final WsdlNameScope nameScope;
  
  private final WsdlBindingSubject parent;
  
  WsdlBindingSubject(QName paramQName, WsdlNameScope paramWsdlNameScope, WsdlBindingSubject paramWsdlBindingSubject) { this(paramQName, WsdlMessageType.NO_MESSAGE, paramWsdlNameScope, paramWsdlBindingSubject); }
  
  WsdlBindingSubject(QName paramQName, WsdlMessageType paramWsdlMessageType, WsdlNameScope paramWsdlNameScope, WsdlBindingSubject paramWsdlBindingSubject) {
    this.name = paramQName;
    this.messageType = paramWsdlMessageType;
    this.nameScope = paramWsdlNameScope;
    this.parent = paramWsdlBindingSubject;
  }
  
  public static WsdlBindingSubject createBindingSubject(QName paramQName) { return new WsdlBindingSubject(paramQName, WsdlNameScope.ENDPOINT, null); }
  
  public static WsdlBindingSubject createBindingOperationSubject(QName paramQName1, QName paramQName2) {
    WsdlBindingSubject wsdlBindingSubject = createBindingSubject(paramQName1);
    return new WsdlBindingSubject(paramQName2, WsdlNameScope.OPERATION, wsdlBindingSubject);
  }
  
  public static WsdlBindingSubject createBindingMessageSubject(QName paramQName1, QName paramQName2, QName paramQName3, WsdlMessageType paramWsdlMessageType) {
    if (paramWsdlMessageType == null)
      throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0083_MESSAGE_TYPE_NULL())); 
    if (paramWsdlMessageType == WsdlMessageType.NO_MESSAGE)
      throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0084_MESSAGE_TYPE_NO_MESSAGE())); 
    if (paramWsdlMessageType == WsdlMessageType.FAULT && paramQName3 == null)
      throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0085_MESSAGE_FAULT_NO_NAME())); 
    WsdlBindingSubject wsdlBindingSubject = createBindingOperationSubject(paramQName1, paramQName2);
    return new WsdlBindingSubject(paramQName3, paramWsdlMessageType, WsdlNameScope.MESSAGE, wsdlBindingSubject);
  }
  
  public QName getName() { return this.name; }
  
  public WsdlMessageType getMessageType() { return this.messageType; }
  
  public WsdlBindingSubject getParent() { return this.parent; }
  
  public boolean isBindingSubject() { return (this.nameScope == WsdlNameScope.ENDPOINT) ? ((this.parent == null)) : false; }
  
  public boolean isBindingOperationSubject() { return (this.nameScope == WsdlNameScope.OPERATION && this.parent != null) ? this.parent.isBindingSubject() : 0; }
  
  public boolean isBindingMessageSubject() { return (this.nameScope == WsdlNameScope.MESSAGE && this.parent != null) ? this.parent.isBindingOperationSubject() : 0; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || !(paramObject instanceof WsdlBindingSubject))
      return false; 
    WsdlBindingSubject wsdlBindingSubject = (WsdlBindingSubject)paramObject;
    null = true;
    null = (null && ((this.name == null) ? (wsdlBindingSubject.name == null) : this.name.equals(wsdlBindingSubject.name)));
    null = (null && this.messageType.equals(wsdlBindingSubject.messageType));
    null = (null && this.nameScope.equals(wsdlBindingSubject.nameScope));
    return (null && ((this.parent == null) ? (wsdlBindingSubject.parent == null) : this.parent.equals(wsdlBindingSubject.parent)));
  }
  
  public int hashCode() {
    null = 23;
    null = 31 * null + ((this.name == null) ? 0 : this.name.hashCode());
    null = 31 * null + this.messageType.hashCode();
    null = 31 * null + this.nameScope.hashCode();
    return 31 * null + ((this.parent == null) ? 0 : this.parent.hashCode());
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder("WsdlBindingSubject[");
    stringBuilder.append(this.name).append(", ").append(this.messageType);
    stringBuilder.append(", ").append(this.nameScope).append(", ").append(this.parent);
    return stringBuilder.append("]").toString();
  }
  
  public enum WsdlMessageType {
    NO_MESSAGE, INPUT, OUTPUT, FAULT;
  }
  
  public enum WsdlNameScope {
    SERVICE, ENDPOINT, OPERATION, MESSAGE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\subject\WsdlBindingSubject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */