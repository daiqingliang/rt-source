package com.sun.xml.internal.ws.api.model;

public static enum ExceptionType {
  WSDLException(0),
  UserDefined(1);
  
  private final int exceptionType;
  
  ExceptionType(int paramInt1) { this.exceptionType = paramInt1; }
  
  public int value() { return this.exceptionType; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\ExceptionType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */