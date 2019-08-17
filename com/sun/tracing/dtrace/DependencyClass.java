package com.sun.tracing.dtrace;

public static enum DependencyClass {
  UNKNOWN(0),
  CPU(1),
  PLATFORM(2),
  GROUP(3),
  ISA(4),
  COMMON(5);
  
  private int encoding;
  
  public String toDisplayString() { return toString().substring(0, 1) + toString().substring(1).toLowerCase(); }
  
  public int getEncoding() { return this.encoding; }
  
  DependencyClass(int paramInt1) { this.encoding = paramInt1; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\tracing\dtrace\DependencyClass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */