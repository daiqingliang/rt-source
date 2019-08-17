package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

public interface RuntimeElementPropertyInfo extends ElementPropertyInfo<Type, Class>, RuntimePropertyInfo {
  Collection<? extends RuntimeTypeInfo> ref();
  
  List<? extends RuntimeTypeRef> getTypes();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\runtime\RuntimeElementPropertyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */