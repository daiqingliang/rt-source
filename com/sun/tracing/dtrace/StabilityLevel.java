package com.sun.tracing.dtrace;

public static enum StabilityLevel {
  INTERNAL(0),
  PRIVATE(1),
  OBSOLETE(2),
  EXTERNAL(3),
  UNSTABLE(4),
  EVOLVING(5),
  STABLE(6),
  STANDARD(7);
  
  private int encoding;
  
  String toDisplayString() { return toString().substring(0, 1) + toString().substring(1).toLowerCase(); }
  
  public int getEncoding() { return this.encoding; }
  
  StabilityLevel(int paramInt1) { this.encoding = paramInt1; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\tracing\dtrace\StabilityLevel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */