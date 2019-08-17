package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import javax.xml.namespace.QName;

class RuntimeElementPropertyInfoImpl extends ElementPropertyInfoImpl<Type, Class, Field, Method> implements RuntimeElementPropertyInfo {
  private final Accessor acc;
  
  RuntimeElementPropertyInfoImpl(RuntimeClassInfoImpl paramRuntimeClassInfoImpl, PropertySeed<Type, Class, Field, Method> paramPropertySeed) {
    super(paramRuntimeClassInfoImpl, paramPropertySeed);
    Accessor accessor = ((RuntimeClassInfoImpl.RuntimePropertySeed)paramPropertySeed).getAccessor();
    if (getAdapter() != null && !isCollection())
      accessor = accessor.adapt(getAdapter()); 
    this.acc = accessor;
  }
  
  public Accessor getAccessor() { return this.acc; }
  
  public boolean elementOnlyContent() { return true; }
  
  public List<? extends RuntimeTypeInfo> ref() { return super.ref(); }
  
  protected RuntimeTypeRefImpl createTypeRef(QName paramQName, Type paramType, boolean paramBoolean, String paramString) { return new RuntimeTypeRefImpl(this, paramQName, paramType, paramBoolean, paramString); }
  
  public List<RuntimeTypeRefImpl> getTypes() { return super.getTypes(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\RuntimeElementPropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */