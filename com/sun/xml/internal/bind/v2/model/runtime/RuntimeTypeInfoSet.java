package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import javax.xml.namespace.QName;

public interface RuntimeTypeInfoSet extends TypeInfoSet<Type, Class, Field, Method> {
  Map<Class, ? extends RuntimeArrayInfo> arrays();
  
  Map<Class, ? extends RuntimeClassInfo> beans();
  
  Map<Type, ? extends RuntimeBuiltinLeafInfo> builtins();
  
  Map<Class, ? extends RuntimeEnumLeafInfo> enums();
  
  RuntimeNonElement getTypeInfo(Type paramType);
  
  RuntimeNonElement getAnyTypeInfo();
  
  RuntimeNonElement getClassInfo(Class paramClass);
  
  RuntimeElementInfo getElementInfo(Class paramClass, QName paramQName);
  
  Map<QName, ? extends RuntimeElementInfo> getElementMappings(Class paramClass);
  
  Iterable<? extends RuntimeElementInfo> getAllElements();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\runtime\RuntimeTypeInfoSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */