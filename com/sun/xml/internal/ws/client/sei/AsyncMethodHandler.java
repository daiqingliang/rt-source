package com.sun.xml.internal.ws.client.sei;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.client.AsyncInvoker;
import com.sun.xml.internal.ws.client.AsyncResponseImpl;
import com.sun.xml.internal.ws.client.RequestContext;
import com.sun.xml.internal.ws.client.ResponseContext;
import java.lang.reflect.Method;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

abstract class AsyncMethodHandler extends MethodHandler {
  AsyncMethodHandler(SEIStub paramSEIStub, Method paramMethod) { super(paramSEIStub, paramMethod); }
  
  protected final Response<Object> doInvoke(Object paramObject, Object[] paramArrayOfObject, AsyncHandler paramAsyncHandler) {
    SEIAsyncInvoker sEIAsyncInvoker = new SEIAsyncInvoker(paramObject, paramArrayOfObject);
    sEIAsyncInvoker.setNonNullAsyncHandlerGiven((paramAsyncHandler != null));
    AsyncResponseImpl asyncResponseImpl = new AsyncResponseImpl(sEIAsyncInvoker, paramAsyncHandler);
    sEIAsyncInvoker.setReceiver(asyncResponseImpl);
    asyncResponseImpl.run();
    return asyncResponseImpl;
  }
  
  ValueGetterFactory getValueGetterFactory() { return ValueGetterFactory.ASYNC; }
  
  private class SEIAsyncInvoker extends AsyncInvoker {
    private final RequestContext rc = this.this$0.owner.requestContext.copy();
    
    private final Object[] args;
    
    SEIAsyncInvoker(Object param1Object, Object[] param1ArrayOfObject) { this.args = param1ArrayOfObject; }
    
    public void do_run() {
      JavaCallInfo javaCallInfo = this.this$0.owner.databinding.createJavaCallInfo(AsyncMethodHandler.this.method, this.args);
      Packet packet = (Packet)this.this$0.owner.databinding.serializeRequest(javaCallInfo);
      Fiber.CompletionCallback completionCallback = new Fiber.CompletionCallback() {
          public void onCompletion(@NotNull Packet param2Packet) {
            AsyncMethodHandler.SEIAsyncInvoker.this.responseImpl.setResponseContext(new ResponseContext(param2Packet));
            Message message = param2Packet.getMessage();
            if (message == null)
              return; 
            try {
              Object[] arrayOfObject = new Object[1];
              JavaCallInfo javaCallInfo = this.this$1.this$0.owner.databinding.createJavaCallInfo(AsyncMethodHandler.this.method, arrayOfObject);
              javaCallInfo = this.this$1.this$0.owner.databinding.deserializeResponse(param2Packet, javaCallInfo);
              if (javaCallInfo.getException() != null)
                throw javaCallInfo.getException(); 
              AsyncMethodHandler.SEIAsyncInvoker.this.responseImpl.set(arrayOfObject[0], null);
            } catch (Throwable throwable) {
              if (throwable instanceof RuntimeException) {
                if (throwable instanceof WebServiceException) {
                  AsyncMethodHandler.SEIAsyncInvoker.this.responseImpl.set(null, throwable);
                  return;
                } 
              } else if (throwable instanceof Exception) {
                AsyncMethodHandler.SEIAsyncInvoker.this.responseImpl.set(null, throwable);
                return;
              } 
              AsyncMethodHandler.SEIAsyncInvoker.this.responseImpl.set(null, new WebServiceException(throwable));
            } 
          }
          
          public void onCompletion(@NotNull Throwable param2Throwable) {
            if (param2Throwable instanceof WebServiceException) {
              AsyncMethodHandler.SEIAsyncInvoker.this.responseImpl.set(null, param2Throwable);
            } else {
              AsyncMethodHandler.SEIAsyncInvoker.this.responseImpl.set(null, new WebServiceException(param2Throwable));
            } 
          }
        };
      AsyncMethodHandler.this.owner.doProcessAsync(this.responseImpl, packet, this.rc, completionCallback);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\sei\AsyncMethodHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */