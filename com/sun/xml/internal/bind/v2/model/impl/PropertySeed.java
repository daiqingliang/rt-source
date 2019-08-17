package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationSource;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;

interface PropertySeed<T, C, F, M> extends Locatable, AnnotationSource {
  String getName();
  
  T getRawType();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\PropertySeed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */