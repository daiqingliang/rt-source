package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public interface TypeRef<T, C> extends NonElementRef<T, C> {
  QName getTagName();
  
  boolean isNillable();
  
  String getDefaultValue();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\core\TypeRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */