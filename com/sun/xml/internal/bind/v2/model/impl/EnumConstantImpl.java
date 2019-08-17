package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.EnumConstant;
import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;

class EnumConstantImpl<T, C, F, M> extends Object implements EnumConstant<T, C> {
  protected final String lexical;
  
  protected final EnumLeafInfoImpl<T, C, F, M> owner;
  
  protected final String name;
  
  protected final EnumConstantImpl<T, C, F, M> next;
  
  public EnumConstantImpl(EnumLeafInfoImpl<T, C, F, M> paramEnumLeafInfoImpl, String paramString1, String paramString2, EnumConstantImpl<T, C, F, M> paramEnumConstantImpl) {
    this.lexical = paramString2;
    this.owner = paramEnumLeafInfoImpl;
    this.name = paramString1;
    this.next = paramEnumConstantImpl;
  }
  
  public EnumLeafInfo<T, C> getEnclosingClass() { return this.owner; }
  
  public final String getLexicalValue() { return this.lexical; }
  
  public final String getName() { return this.name; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\EnumConstantImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */