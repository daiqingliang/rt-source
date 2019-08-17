package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.namespace.QName;

class ElementPropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> extends ERPropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> implements ElementPropertyInfo<TypeT, ClassDeclT> {
  private List<TypeRefImpl<TypeT, ClassDeclT>> types;
  
  private final List<TypeInfo<TypeT, ClassDeclT>> ref = new AbstractList<TypeInfo<TypeT, ClassDeclT>>() {
      public TypeInfo<TypeT, ClassDeclT> get(int param1Int) { return ((TypeRefImpl)ElementPropertyInfoImpl.this.getTypes().get(param1Int)).getTarget(); }
      
      public int size() { return ElementPropertyInfoImpl.this.getTypes().size(); }
    };
  
  private Boolean isRequired;
  
  private final boolean isValueList = this.seed.hasAnnotation(javax.xml.bind.annotation.XmlList.class);
  
  ElementPropertyInfoImpl(ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> paramClassInfoImpl, PropertySeed<TypeT, ClassDeclT, FieldT, MethodT> paramPropertySeed) { super(paramClassInfoImpl, paramPropertySeed); }
  
  public List<? extends TypeRefImpl<TypeT, ClassDeclT>> getTypes() {
    if (this.types == null) {
      this.types = new FinalArrayList();
      XmlElement[] arrayOfXmlElement = null;
      XmlElement xmlElement = (XmlElement)this.seed.readAnnotation(XmlElement.class);
      XmlElements xmlElements = (XmlElements)this.seed.readAnnotation(XmlElements.class);
      if (xmlElement != null && xmlElements != null)
        this.parent.builder.reportError(new IllegalAnnotationException(Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(new Object[] { nav().getClassName(this.parent.getClazz()) + '#' + this.seed.getName(), xmlElement.annotationType().getName(), xmlElements.annotationType().getName() }, ), xmlElement, xmlElements)); 
      this.isRequired = Boolean.valueOf(true);
      if (xmlElement != null) {
        arrayOfXmlElement = new XmlElement[] { xmlElement };
      } else if (xmlElements != null) {
        arrayOfXmlElement = xmlElements.value();
      } 
      if (arrayOfXmlElement == null) {
        Object object = getIndividualType();
        if (!nav().isPrimitive(object) || isCollection())
          this.isRequired = Boolean.valueOf(false); 
        this.types.add(createTypeRef(calcXmlName((XmlElement)null), object, isCollection(), null));
      } else {
        for (XmlElement xmlElement1 : arrayOfXmlElement) {
          QName qName = calcXmlName(xmlElement1);
          Object object = reader().getClassValue(xmlElement1, "type");
          if (nav().isSameType(object, nav().ref(XmlElement.DEFAULT.class)))
            object = getIndividualType(); 
          if ((!nav().isPrimitive(object) || isCollection()) && !xmlElement1.required())
            this.isRequired = Boolean.valueOf(false); 
          this.types.add(createTypeRef(qName, object, xmlElement1.nillable(), getDefaultValue(xmlElement1.defaultValue())));
        } 
      } 
      this.types = Collections.unmodifiableList(this.types);
      assert !this.types.contains(null);
    } 
    return this.types;
  }
  
  private String getDefaultValue(String paramString) { return paramString.equals("\000") ? null : paramString; }
  
  protected TypeRefImpl<TypeT, ClassDeclT> createTypeRef(QName paramQName, TypeT paramTypeT, boolean paramBoolean, String paramString) { return new TypeRefImpl(this, paramQName, paramTypeT, paramBoolean, paramString); }
  
  public boolean isValueList() { return this.isValueList; }
  
  public boolean isRequired() {
    if (this.isRequired == null)
      getTypes(); 
    return this.isRequired.booleanValue();
  }
  
  public List<? extends TypeInfo<TypeT, ClassDeclT>> ref() { return this.ref; }
  
  public final PropertyKind kind() { return PropertyKind.ELEMENT; }
  
  protected void link() {
    super.link();
    for (TypeRefImpl typeRefImpl : getTypes())
      typeRefImpl.link(); 
    if (isValueList()) {
      if (id() != ID.IDREF)
        for (TypeRefImpl typeRefImpl : this.types) {
          if (!typeRefImpl.getTarget().isSimpleType()) {
            this.parent.builder.reportError(new IllegalAnnotationException(Messages.XMLLIST_NEEDS_SIMPLETYPE.format(new Object[] { nav().getTypeName(typeRefImpl.getTarget().getType()) }, ), this));
            break;
          } 
        }  
      if (!isCollection())
        this.parent.builder.reportError(new IllegalAnnotationException(Messages.XMLLIST_ON_SINGLE_PROPERTY.format(new Object[0]), this)); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\ElementPropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */