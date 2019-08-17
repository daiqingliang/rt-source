package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Type;
import java.util.Collection;

public interface RuntimePropertyInfo extends PropertyInfo<Type, Class> {
  Collection<? extends RuntimeTypeInfo> ref();
  
  Accessor getAccessor();
  
  boolean elementOnlyContent();
  
  Type getRawType();
  
  Type getIndividualType();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\runtime\RuntimePropertyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */