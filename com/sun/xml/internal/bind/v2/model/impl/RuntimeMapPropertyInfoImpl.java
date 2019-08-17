package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeMapPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

class RuntimeMapPropertyInfoImpl extends MapPropertyInfoImpl<Type, Class, Field, Method> implements RuntimeMapPropertyInfo {
  private final Accessor acc;
  
  RuntimeMapPropertyInfoImpl(RuntimeClassInfoImpl paramRuntimeClassInfoImpl, PropertySeed<Type, Class, Field, Method> paramPropertySeed) {
    super(paramRuntimeClassInfoImpl, paramPropertySeed);
    this.acc = ((RuntimeClassInfoImpl.RuntimePropertySeed)paramPropertySeed).getAccessor();
  }
  
  public Accessor getAccessor() { return this.acc; }
  
  public boolean elementOnlyContent() { return true; }
  
  public RuntimeNonElement getKeyType() { return (RuntimeNonElement)super.getKeyType(); }
  
  public RuntimeNonElement getValueType() { return (RuntimeNonElement)super.getValueType(); }
  
  public List<? extends RuntimeTypeInfo> ref() { return (List)super.ref(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\RuntimeMapPropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */