package com.sun.org.apache.xerces.internal.util;

public class FeatureState {
  public final Status status;
  
  public final boolean state;
  
  public static final FeatureState SET_ENABLED = new FeatureState(Status.SET, true);
  
  public static final FeatureState SET_DISABLED = new FeatureState(Status.SET, false);
  
  public static final FeatureState UNKNOWN = new FeatureState(Status.UNKNOWN, false);
  
  public static final FeatureState RECOGNIZED = new FeatureState(Status.RECOGNIZED, false);
  
  public static final FeatureState NOT_SUPPORTED = new FeatureState(Status.NOT_SUPPORTED, false);
  
  public static final FeatureState NOT_RECOGNIZED = new FeatureState(Status.NOT_RECOGNIZED, false);
  
  public static final FeatureState NOT_ALLOWED = new FeatureState(Status.NOT_ALLOWED, false);
  
  public FeatureState(Status paramStatus, boolean paramBoolean) {
    this.status = paramStatus;
    this.state = paramBoolean;
  }
  
  public static FeatureState of(Status paramStatus) { return new FeatureState(paramStatus, false); }
  
  public static FeatureState is(boolean paramBoolean) { return new FeatureState(Status.SET, paramBoolean); }
  
  public boolean isExceptional() { return this.status.isExceptional(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\FeatureState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */