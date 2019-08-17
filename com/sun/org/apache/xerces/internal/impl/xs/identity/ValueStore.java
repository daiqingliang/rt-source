package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.xs.ShortList;

public interface ValueStore {
  void addValue(Field paramField, Object paramObject, short paramShort, ShortList paramShortList);
  
  void reportError(String paramString, Object[] paramArrayOfObject);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\identity\ValueStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */