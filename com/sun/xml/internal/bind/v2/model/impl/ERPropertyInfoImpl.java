package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;

abstract class ERPropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> extends PropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> {
  private final QName xmlName;
  
  private final boolean wrapperNillable;
  
  private final boolean wrapperRequired;
  
  public ERPropertyInfoImpl(ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> paramClassInfoImpl, PropertySeed<TypeT, ClassDeclT, FieldT, MethodT> paramPropertySeed) {
    super(paramClassInfoImpl, paramPropertySeed);
    XmlElementWrapper xmlElementWrapper = (XmlElementWrapper)this.seed.readAnnotation(XmlElementWrapper.class);
    boolean bool1 = false;
    boolean bool2 = false;
    if (!isCollection()) {
      this.xmlName = null;
      if (xmlElementWrapper != null)
        paramClassInfoImpl.builder.reportError(new IllegalAnnotationException(Messages.XML_ELEMENT_WRAPPER_ON_NON_COLLECTION.format(new Object[] { nav().getClassName(this.parent.getClazz()) + '.' + this.seed.getName() }, ), xmlElementWrapper)); 
    } else if (xmlElementWrapper != null) {
      this.xmlName = calcXmlName(xmlElementWrapper);
      bool1 = xmlElementWrapper.nillable();
      bool2 = xmlElementWrapper.required();
    } else {
      this.xmlName = null;
    } 
    this.wrapperNillable = bool1;
    this.wrapperRequired = bool2;
  }
  
  public final QName getXmlName() { return this.xmlName; }
  
  public final boolean isCollectionNillable() { return this.wrapperNillable; }
  
  public final boolean isCollectionRequired() { return this.wrapperRequired; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\ERPropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */