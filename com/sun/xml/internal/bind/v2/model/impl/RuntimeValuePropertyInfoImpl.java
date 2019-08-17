package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeValuePropertyInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

final class RuntimeValuePropertyInfoImpl extends ValuePropertyInfoImpl<Type, Class, Field, Method> implements RuntimeValuePropertyInfo {
  RuntimeValuePropertyInfoImpl(RuntimeClassInfoImpl paramRuntimeClassInfoImpl, PropertySeed<Type, Class, Field, Method> paramPropertySeed) { super(paramRuntimeClassInfoImpl, paramPropertySeed); }
  
  public boolean elementOnlyContent() { return false; }
  
  public RuntimePropertyInfo getSource() { return (RuntimePropertyInfo)super.getSource(); }
  
  public RuntimeNonElement getTarget() { return (RuntimeNonElement)super.getTarget(); }
  
  public List<? extends RuntimeNonElement> ref() { return super.ref(); }
  
  public void link() {
    getTransducer();
    super.link();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\RuntimeValuePropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */