package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeArrayInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

final class RuntimeArrayInfoImpl extends ArrayInfoImpl<Type, Class, Field, Method> implements RuntimeArrayInfo {
  RuntimeArrayInfoImpl(RuntimeModelBuilder paramRuntimeModelBuilder, Locatable paramLocatable, Class paramClass) { super(paramRuntimeModelBuilder, paramLocatable, paramClass); }
  
  public Class getType() { return (Class)super.getType(); }
  
  public RuntimeNonElement getItemType() { return (RuntimeNonElement)super.getItemType(); }
  
  public <V> Transducer<V> getTransducer() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\RuntimeArrayInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */