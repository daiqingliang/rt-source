package com.sun.xml.internal.bind.v2.model.core;

public static enum WildcardMode {
  STRICT(false, true),
  SKIP(true, false),
  LAX(true, true);
  
  public final boolean allowDom;
  
  public final boolean allowTypedObject;
  
  WildcardMode(boolean paramBoolean1, boolean paramBoolean2) {
    this.allowDom = paramBoolean1;
    this.allowTypedObject = paramBoolean2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\core\WildcardMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */