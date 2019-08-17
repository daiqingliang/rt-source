package com.sun.xml.internal.bind.v2.model.core;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;

public interface TypeInfo<T, C> extends Locatable {
  T getType();
  
  boolean canBeReferencedByIDREF();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\core\TypeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */