package com.sun.xml.internal.ws.client;

import javax.xml.ws.WebServiceException;

public abstract class AsyncInvoker implements Runnable {
  protected AsyncResponseImpl responseImpl;
  
  protected boolean nonNullAsyncHandlerGiven;
  
  public void setReceiver(AsyncResponseImpl paramAsyncResponseImpl) { this.responseImpl = paramAsyncResponseImpl; }
  
  public AsyncResponseImpl getResponseImpl() { return this.responseImpl; }
  
  public void setResponseImpl(AsyncResponseImpl paramAsyncResponseImpl) { this.responseImpl = paramAsyncResponseImpl; }
  
  public boolean isNonNullAsyncHandlerGiven() { return this.nonNullAsyncHandlerGiven; }
  
  public void setNonNullAsyncHandlerGiven(boolean paramBoolean) { this.nonNullAsyncHandlerGiven = paramBoolean; }
  
  public void run() {
    try {
      do_run();
    } catch (WebServiceException webServiceException) {
      throw webServiceException;
    } catch (Throwable throwable) {
      throw new WebServiceException(throwable);
    } 
  }
  
  public abstract void do_run();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\AsyncInvoker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */