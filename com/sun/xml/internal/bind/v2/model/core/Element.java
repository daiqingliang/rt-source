package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public interface Element<T, C> extends TypeInfo<T, C> {
  QName getElementName();
  
  Element<T, C> getSubstitutionHead();
  
  ClassInfo<T, C> getScope();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\core\Element.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */