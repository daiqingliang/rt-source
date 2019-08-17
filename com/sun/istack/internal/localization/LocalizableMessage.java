package com.sun.istack.internal.localization;

import java.util.Arrays;

public final class LocalizableMessage implements Localizable {
  private final String _bundlename;
  
  private final String _key;
  
  private final Object[] _args;
  
  public LocalizableMessage(String paramString1, String paramString2, Object... paramVarArgs) {
    this._bundlename = paramString1;
    this._key = paramString2;
    if (paramVarArgs == null)
      paramVarArgs = new Object[0]; 
    this._args = paramVarArgs;
  }
  
  public String getKey() { return this._key; }
  
  public Object[] getArguments() { return Arrays.copyOf(this._args, this._args.length); }
  
  public String getResourceBundleName() { return this._bundlename; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\istack\internal\localization\LocalizableMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */