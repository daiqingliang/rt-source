package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.ArrayInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.util.ArrayInfoUtil;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Location;
import javax.xml.namespace.QName;

public class ArrayInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> extends TypeInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> implements ArrayInfo<TypeT, ClassDeclT>, Location {
  private final NonElement<TypeT, ClassDeclT> itemType;
  
  private final QName typeName;
  
  private final TypeT arrayType;
  
  public ArrayInfoImpl(ModelBuilder<TypeT, ClassDeclT, FieldT, MethodT> paramModelBuilder, Locatable paramLocatable, TypeT paramTypeT) {
    super(paramModelBuilder, paramLocatable);
    this.arrayType = paramTypeT;
    Object object = nav().getComponentType(paramTypeT);
    this.itemType = paramModelBuilder.getTypeInfo(object, this);
    QName qName = this.itemType.getTypeName();
    if (qName == null) {
      paramModelBuilder.reportError(new IllegalAnnotationException(Messages.ANONYMOUS_ARRAY_ITEM.format(new Object[] { nav().getTypeName(object) }, ), this));
      qName = new QName("#dummy");
    } 
    this.typeName = ArrayInfoUtil.calcArrayTypeName(qName);
  }
  
  public NonElement<TypeT, ClassDeclT> getItemType() { return this.itemType; }
  
  public QName getTypeName() { return this.typeName; }
  
  public boolean isSimpleType() { return false; }
  
  public TypeT getType() { return (TypeT)this.arrayType; }
  
  public final boolean canBeReferencedByIDREF() { return false; }
  
  public Location getLocation() { return this; }
  
  public String toString() { return nav().getTypeName(this.arrayType); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\ArrayInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */