package com.sun.xml.internal.bind.v2.model.core;

public interface EnumConstant<T, C> {
  EnumLeafInfo<T, C> getEnclosingClass();
  
  String getLexicalValue();
  
  String getName();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\core\EnumConstant.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */