package com.sun.org.apache.xerces.internal.impl.xs.identity;

import com.sun.org.apache.xerces.internal.xs.XSIDCDefinition;

public class KeyRef extends IdentityConstraint {
  protected UniqueOrKey fKey;
  
  public KeyRef(String paramString1, String paramString2, String paramString3, UniqueOrKey paramUniqueOrKey) {
    super(paramString1, paramString2, paramString3);
    this.fKey = paramUniqueOrKey;
    this.type = 2;
  }
  
  public UniqueOrKey getKey() { return this.fKey; }
  
  public XSIDCDefinition getRefKey() { return this.fKey; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\identity\KeyRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */