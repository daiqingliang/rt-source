package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.lang.annotation.Annotation;

class FieldPropertySeed<TypeT, ClassDeclT, FieldT, MethodT> extends Object implements PropertySeed<TypeT, ClassDeclT, FieldT, MethodT> {
  protected final FieldT field;
  
  private ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> parent;
  
  FieldPropertySeed(ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> paramClassInfoImpl, FieldT paramFieldT) {
    this.parent = paramClassInfoImpl;
    this.field = paramFieldT;
  }
  
  public <A extends Annotation> A readAnnotation(Class<A> paramClass) { return (A)this.parent.reader().getFieldAnnotation(paramClass, this.field, this); }
  
  public boolean hasAnnotation(Class<? extends Annotation> paramClass) { return this.parent.reader().hasFieldAnnotation(paramClass, this.field); }
  
  public String getName() { return this.parent.nav().getFieldName(this.field); }
  
  public TypeT getRawType() { return (TypeT)this.parent.nav().getFieldType(this.field); }
  
  public Locatable getUpstream() { return this.parent; }
  
  public Location getLocation() { return this.parent.nav().getFieldLocation(this.field); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\FieldPropertySeed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */