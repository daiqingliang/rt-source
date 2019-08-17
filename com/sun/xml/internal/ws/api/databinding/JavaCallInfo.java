package com.sun.xml.internal.ws.api.databinding;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import java.lang.reflect.Method;

public class JavaCallInfo implements JavaCallInfo {
  private Method method;
  
  private Object[] parameters;
  
  private Object returnValue;
  
  private Throwable exception;
  
  public JavaCallInfo() {}
  
  public JavaCallInfo(Method paramMethod, Object[] paramArrayOfObject) {
    this.method = paramMethod;
    this.parameters = paramArrayOfObject;
  }
  
  public Method getMethod() { return this.method; }
  
  public void setMethod(Method paramMethod) { this.method = paramMethod; }
  
  public Object[] getParameters() { return this.parameters; }
  
  public void setParameters(Object[] paramArrayOfObject) { this.parameters = paramArrayOfObject; }
  
  public Object getReturnValue() { return this.returnValue; }
  
  public void setReturnValue(Object paramObject) { this.returnValue = paramObject; }
  
  public Throwable getException() { return this.exception; }
  
  public void setException(Throwable paramThrowable) { this.exception = paramThrowable; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\databinding\JavaCallInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */