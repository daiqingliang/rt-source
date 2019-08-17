package com.sun.xml.internal.ws.client.sei;

import java.lang.reflect.Method;
import java.util.concurrent.Future;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.WebServiceException;

final class CallbackMethodHandler extends AsyncMethodHandler {
  private final int handlerPos;
  
  CallbackMethodHandler(SEIStub paramSEIStub, Method paramMethod, int paramInt) {
    super(paramSEIStub, paramMethod);
    this.handlerPos = paramInt;
  }
  
  Future<?> invoke(Object paramObject, Object[] paramArrayOfObject) throws WebServiceException {
    AsyncHandler asyncHandler = (AsyncHandler)paramArrayOfObject[this.handlerPos];
    return doInvoke(paramObject, paramArrayOfObject, asyncHandler);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\sei\CallbackMethodHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */