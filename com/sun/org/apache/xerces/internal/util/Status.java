package com.sun.org.apache.xerces.internal.util;

public static enum Status {
  SET((short)-3, false),
  UNKNOWN((short)-2, false),
  RECOGNIZED((short)-1, false),
  NOT_SUPPORTED((short)0, true),
  NOT_RECOGNIZED((short)1, true),
  NOT_ALLOWED((short)2, true);
  
  private final short type;
  
  private boolean isExceptional;
  
  Status(boolean paramBoolean1, boolean paramBoolean2) {
    this.type = paramBoolean1;
    this.isExceptional = paramBoolean2;
  }
  
  public short getType() { return this.type; }
  
  public boolean isExceptional() { return this.isExceptional; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\Status.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */