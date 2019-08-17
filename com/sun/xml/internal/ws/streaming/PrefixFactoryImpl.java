package com.sun.xml.internal.ws.streaming;

import java.util.HashMap;
import java.util.Map;

public class PrefixFactoryImpl implements PrefixFactory {
  private String _base;
  
  private int _next;
  
  private Map _cachedUriToPrefixMap;
  
  public PrefixFactoryImpl(String paramString) {
    this._base = paramString;
    this._next = 1;
  }
  
  public String getPrefix(String paramString) {
    String str = null;
    if (this._cachedUriToPrefixMap == null) {
      this._cachedUriToPrefixMap = new HashMap();
    } else {
      str = (String)this._cachedUriToPrefixMap.get(paramString);
    } 
    if (str == null) {
      str = this._base + Integer.toString(this._next++);
      this._cachedUriToPrefixMap.put(paramString, str);
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\streaming\PrefixFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */