package com.sun.istack.internal.localization;

public final class NullLocalizable implements Localizable {
  private final String msg;
  
  public NullLocalizable(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException(); 
    this.msg = paramString;
  }
  
  public String getKey() { return "\000"; }
  
  public Object[] getArguments() { return new Object[] { this.msg }; }
  
  public String getResourceBundleName() { return ""; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\istack\internal\localization\NullLocalizable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */