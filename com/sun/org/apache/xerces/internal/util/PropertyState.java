package com.sun.org.apache.xerces.internal.util;

public class PropertyState {
  public final Status status;
  
  public final Object state;
  
  public static final PropertyState UNKNOWN = new PropertyState(Status.UNKNOWN, null);
  
  public static final PropertyState RECOGNIZED = new PropertyState(Status.RECOGNIZED, null);
  
  public static final PropertyState NOT_SUPPORTED = new PropertyState(Status.NOT_SUPPORTED, null);
  
  public static final PropertyState NOT_RECOGNIZED = new PropertyState(Status.NOT_RECOGNIZED, null);
  
  public static final PropertyState NOT_ALLOWED = new PropertyState(Status.NOT_ALLOWED, null);
  
  public PropertyState(Status paramStatus, Object paramObject) {
    this.status = paramStatus;
    this.state = paramObject;
  }
  
  public static PropertyState of(Status paramStatus) { return new PropertyState(paramStatus, null); }
  
  public static PropertyState is(Object paramObject) { return new PropertyState(Status.SET, paramObject); }
  
  public boolean isExceptional() { return this.status.isExceptional(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\PropertyState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */