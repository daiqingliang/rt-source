package com.sun.xml.internal.ws.api.policy.subject;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.resources.BindingApiMessages;
import javax.xml.namespace.QName;

public class BindingSubject {
  private static final Logger LOGGER = Logger.getLogger(BindingSubject.class);
  
  private final QName name;
  
  private final WsdlMessageType messageType;
  
  private final WsdlNameScope nameScope;
  
  private final BindingSubject parent;
  
  BindingSubject(QName paramQName, WsdlNameScope paramWsdlNameScope, BindingSubject paramBindingSubject) { this(paramQName, WsdlMessageType.NO_MESSAGE, paramWsdlNameScope, paramBindingSubject); }
  
  BindingSubject(QName paramQName, WsdlMessageType paramWsdlMessageType, WsdlNameScope paramWsdlNameScope, BindingSubject paramBindingSubject) {
    this.name = paramQName;
    this.messageType = paramWsdlMessageType;
    this.nameScope = paramWsdlNameScope;
    this.parent = paramBindingSubject;
  }
  
  public static BindingSubject createBindingSubject(QName paramQName) { return new BindingSubject(paramQName, WsdlNameScope.ENDPOINT, null); }
  
  public static BindingSubject createOperationSubject(QName paramQName1, QName paramQName2) {
    BindingSubject bindingSubject = createBindingSubject(paramQName1);
    return new BindingSubject(paramQName2, WsdlNameScope.OPERATION, bindingSubject);
  }
  
  public static BindingSubject createInputMessageSubject(QName paramQName1, QName paramQName2, QName paramQName3) {
    BindingSubject bindingSubject = createOperationSubject(paramQName1, paramQName2);
    return new BindingSubject(paramQName3, WsdlMessageType.INPUT, WsdlNameScope.MESSAGE, bindingSubject);
  }
  
  public static BindingSubject createOutputMessageSubject(QName paramQName1, QName paramQName2, QName paramQName3) {
    BindingSubject bindingSubject = createOperationSubject(paramQName1, paramQName2);
    return new BindingSubject(paramQName3, WsdlMessageType.OUTPUT, WsdlNameScope.MESSAGE, bindingSubject);
  }
  
  public static BindingSubject createFaultMessageSubject(QName paramQName1, QName paramQName2, QName paramQName3) {
    if (paramQName3 == null)
      throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(BindingApiMessages.BINDING_API_NO_FAULT_MESSAGE_NAME())); 
    BindingSubject bindingSubject = createOperationSubject(paramQName1, paramQName2);
    return new BindingSubject(paramQName3, WsdlMessageType.FAULT, WsdlNameScope.MESSAGE, bindingSubject);
  }
  
  public QName getName() { return this.name; }
  
  public BindingSubject getParent() { return this.parent; }
  
  public boolean isBindingSubject() { return (this.nameScope == WsdlNameScope.ENDPOINT) ? ((this.parent == null)) : false; }
  
  public boolean isOperationSubject() { return (this.nameScope == WsdlNameScope.OPERATION && this.parent != null) ? this.parent.isBindingSubject() : 0; }
  
  public boolean isMessageSubject() { return (this.nameScope == WsdlNameScope.MESSAGE && this.parent != null) ? this.parent.isOperationSubject() : 0; }
  
  public boolean isInputMessageSubject() { return (isMessageSubject() && this.messageType == WsdlMessageType.INPUT); }
  
  public boolean isOutputMessageSubject() { return (isMessageSubject() && this.messageType == WsdlMessageType.OUTPUT); }
  
  public boolean isFaultMessageSubject() { return (isMessageSubject() && this.messageType == WsdlMessageType.FAULT); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || !(paramObject instanceof BindingSubject))
      return false; 
    BindingSubject bindingSubject = (BindingSubject)paramObject;
    null = true;
    null = (null && ((this.name == null) ? (bindingSubject.name == null) : this.name.equals(bindingSubject.name)));
    null = (null && this.messageType.equals(bindingSubject.messageType));
    null = (null && this.nameScope.equals(bindingSubject.nameScope));
    return (null && ((this.parent == null) ? (bindingSubject.parent == null) : this.parent.equals(bindingSubject.parent)));
  }
  
  public int hashCode() {
    null = 23;
    null = 29 * null + ((this.name == null) ? 0 : this.name.hashCode());
    null = 29 * null + this.messageType.hashCode();
    null = 29 * null + this.nameScope.hashCode();
    return 29 * null + ((this.parent == null) ? 0 : this.parent.hashCode());
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder("BindingSubject[");
    stringBuilder.append(this.name).append(", ").append(this.messageType);
    stringBuilder.append(", ").append(this.nameScope).append(", ").append(this.parent);
    return stringBuilder.append("]").toString();
  }
  
  private enum WsdlMessageType {
    NO_MESSAGE, INPUT, OUTPUT, FAULT;
  }
  
  private enum WsdlNameScope {
    SERVICE, ENDPOINT, OPERATION, MESSAGE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\policy\subject\BindingSubject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */