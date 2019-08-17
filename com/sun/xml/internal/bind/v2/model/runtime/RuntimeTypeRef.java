package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import java.lang.reflect.Type;

public interface RuntimeTypeRef extends TypeRef<Type, Class>, RuntimeNonElementRef {
  RuntimeNonElement getTarget();
  
  RuntimePropertyInfo getSource();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\runtime\RuntimeTypeRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */