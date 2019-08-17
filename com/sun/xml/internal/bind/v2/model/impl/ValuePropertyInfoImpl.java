package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.ValuePropertyInfo;

class ValuePropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> extends SingleTypePropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> implements ValuePropertyInfo<TypeT, ClassDeclT> {
  ValuePropertyInfoImpl(ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> paramClassInfoImpl, PropertySeed<TypeT, ClassDeclT, FieldT, MethodT> paramPropertySeed) { super(paramClassInfoImpl, paramPropertySeed); }
  
  public PropertyKind kind() { return PropertyKind.VALUE; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\ValuePropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */