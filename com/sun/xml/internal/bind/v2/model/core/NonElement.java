package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public interface NonElement<T, C> extends TypeInfo<T, C> {
  public static final QName ANYTYPE_NAME = new QName("http://www.w3.org/2001/XMLSchema", "anyType");
  
  QName getTypeName();
  
  boolean isSimpleType();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\core\NonElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */