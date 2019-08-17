package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import javax.xml.namespace.QName;

final class RuntimeTypeInfoSetImpl extends TypeInfoSetImpl<Type, Class, Field, Method> implements RuntimeTypeInfoSet {
  public RuntimeTypeInfoSetImpl(AnnotationReader<Type, Class, Field, Method> paramAnnotationReader) { super(Utils.REFLECTION_NAVIGATOR, paramAnnotationReader, RuntimeBuiltinLeafInfoImpl.LEAVES); }
  
  protected RuntimeNonElement createAnyType() { return RuntimeAnyTypeImpl.theInstance; }
  
  public RuntimeNonElement getTypeInfo(Type paramType) { return (RuntimeNonElement)super.getTypeInfo(paramType); }
  
  public RuntimeNonElement getAnyTypeInfo() { return (RuntimeNonElement)super.getAnyTypeInfo(); }
  
  public RuntimeNonElement getClassInfo(Class paramClass) { return (RuntimeNonElement)super.getClassInfo(paramClass); }
  
  public Map<Class, RuntimeClassInfoImpl> beans() { return super.beans(); }
  
  public Map<Type, RuntimeBuiltinLeafInfoImpl<?>> builtins() { return super.builtins(); }
  
  public Map<Class, RuntimeEnumLeafInfoImpl<?, ?>> enums() { return super.enums(); }
  
  public Map<Class, RuntimeArrayInfoImpl> arrays() { return super.arrays(); }
  
  public RuntimeElementInfoImpl getElementInfo(Class paramClass, QName paramQName) { return (RuntimeElementInfoImpl)super.getElementInfo(paramClass, paramQName); }
  
  public Map<QName, RuntimeElementInfoImpl> getElementMappings(Class paramClass) { return super.getElementMappings(paramClass); }
  
  public Iterable<RuntimeElementInfoImpl> getAllElements() { return super.getAllElements(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\RuntimeTypeInfoSetImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */