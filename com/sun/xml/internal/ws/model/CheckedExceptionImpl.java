package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.addressing.WsaActionUtil;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.ExceptionType;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.XMLBridge;

public final class CheckedExceptionImpl implements CheckedException {
  private final Class exceptionClass;
  
  private final TypeInfo detail;
  
  private final ExceptionType exceptionType;
  
  private final JavaMethodImpl javaMethod;
  
  private String messageName;
  
  private String faultAction = "";
  
  public CheckedExceptionImpl(JavaMethodImpl paramJavaMethodImpl, Class paramClass, TypeInfo paramTypeInfo, ExceptionType paramExceptionType) {
    this.detail = paramTypeInfo;
    this.exceptionType = paramExceptionType;
    this.exceptionClass = paramClass;
    this.javaMethod = paramJavaMethodImpl;
  }
  
  public AbstractSEIModelImpl getOwner() { return this.javaMethod.owner; }
  
  public JavaMethod getParent() { return this.javaMethod; }
  
  public Class getExceptionClass() { return this.exceptionClass; }
  
  public Class getDetailBean() { return (Class)this.detail.type; }
  
  public Bridge getBridge() { return null; }
  
  public XMLBridge getBond() { return getOwner().getXMLBridge(this.detail); }
  
  public TypeInfo getDetailType() { return this.detail; }
  
  public ExceptionType getExceptionType() { return this.exceptionType; }
  
  public String getMessageName() { return this.messageName; }
  
  public void setMessageName(String paramString) { this.messageName = paramString; }
  
  public String getFaultAction() { return this.faultAction; }
  
  public void setFaultAction(String paramString) { this.faultAction = paramString; }
  
  public String getDefaultFaultAction() { return WsaActionUtil.getDefaultFaultAction(this.javaMethod, this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\CheckedExceptionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */