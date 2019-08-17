package com.sun.xml.internal.ws.client.sei;

import java.lang.reflect.Method;
import javax.xml.ws.WebServiceException;

public abstract class MethodHandler {
  protected final SEIStub owner;
  
  protected Method method;
  
  protected MethodHandler(SEIStub paramSEIStub, Method paramMethod) {
    this.owner = paramSEIStub;
    this.method = paramMethod;
  }
  
  abstract Object invoke(Object paramObject, Object[] paramArrayOfObject) throws WebServiceException, Throwable;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\sei\MethodHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */