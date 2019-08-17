package com.sun.istack.internal.localization;

public class LocalizableMessageFactory {
  private final String _bundlename;
  
  public LocalizableMessageFactory(String paramString) { this._bundlename = paramString; }
  
  public Localizable getMessage(String paramString, Object... paramVarArgs) { return new LocalizableMessage(this._bundlename, paramString, paramVarArgs); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\istack\internal\localization\LocalizableMessageFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */