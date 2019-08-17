package com.sun.xml.internal.ws.client;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.Cancelable;
import com.sun.xml.internal.ws.util.CompletedFuture;
import java.util.Map;
import java.util.concurrent.FutureTask;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.WebServiceException;

public final class AsyncResponseImpl<T> extends FutureTask<T> implements Response<T>, ResponseContextReceiver {
  private final AsyncHandler<T> handler;
  
  private ResponseContext responseContext;
  
  private final Runnable callable;
  
  private Cancelable cancelable;
  
  public AsyncResponseImpl(Runnable paramRunnable, @Nullable AsyncHandler<T> paramAsyncHandler) {
    super(paramRunnable, null);
    this.callable = paramRunnable;
    this.handler = paramAsyncHandler;
  }
  
  public void run() {
    try {
      this.callable.run();
    } catch (WebServiceException webServiceException) {
      set(null, webServiceException);
    } catch (Throwable throwable) {
      set(null, new WebServiceException(throwable));
    } 
  }
  
  public ResponseContext getContext() { return this.responseContext; }
  
  public void setResponseContext(ResponseContext paramResponseContext) { this.responseContext = paramResponseContext; }
  
  public void set(T paramT, Throwable paramThrowable) {
    if (this.handler != null)
      try {
        class CallbackFuture<T> extends CompletedFuture<T> implements Response<T> {
          public CallbackFuture(T param1T, Throwable param1Throwable) { super(param1T, param1Throwable); }
          
          public Map<String, Object> getContext() { return AsyncResponseImpl.this.getContext(); }
        };
        this.handler.handleResponse(new CallbackFuture(paramT, paramThrowable));
      } catch (Throwable throwable) {
        setException(throwable);
        return;
      }  
    if (paramThrowable != null) {
      setException(paramThrowable);
    } else {
      set(paramT);
    } 
  }
  
  public void setCancelable(Cancelable paramCancelable) { this.cancelable = paramCancelable; }
  
  public boolean cancel(boolean paramBoolean) {
    if (this.cancelable != null)
      this.cancelable.cancel(paramBoolean); 
    return super.cancel(paramBoolean);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\AsyncResponseImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */