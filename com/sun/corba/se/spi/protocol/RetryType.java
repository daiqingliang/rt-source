package com.sun.corba.se.spi.protocol;

public static enum RetryType {
  NONE(false),
  BEFORE_RESPONSE(true),
  AFTER_RESPONSE(true);
  
  private final boolean isRetry;
  
  RetryType(boolean paramBoolean1) { this.isRetry = paramBoolean1; }
  
  public boolean isRetry() { return this.isRetry; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\protocol\RetryType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */