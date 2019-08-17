package com.sun.xml.internal.ws.api.model;

public static enum MEP {
  REQUEST_RESPONSE(false),
  ONE_WAY(false),
  ASYNC_POLL(true),
  ASYNC_CALLBACK(true);
  
  public final boolean isAsync;
  
  MEP(boolean paramBoolean1) { this.isAsync = paramBoolean1; }
  
  public final boolean isOneWay() { return (this == ONE_WAY); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\MEP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */