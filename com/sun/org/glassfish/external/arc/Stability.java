package com.sun.org.glassfish.external.arc;

public static enum Stability {
  COMMITTED("Committed"),
  UNCOMMITTED("Uncommitted"),
  VOLATILE("Volatile"),
  NOT_AN_INTERFACE("Not-An-Interface"),
  PRIVATE("Private"),
  EXPERIMENTAL("Experimental"),
  UNSPECIFIED("Unspecified");
  
  private final String mName;
  
  Stability(String paramString1) { this.mName = paramString1; }
  
  public String toString() { return this.mName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\glassfish\external\arc\Stability.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */