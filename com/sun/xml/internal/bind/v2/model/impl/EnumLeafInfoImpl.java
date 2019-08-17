package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.EnumLeafInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.runtime.Location;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.namespace.QName;

class EnumLeafInfoImpl<T, C, F, M> extends TypeInfoImpl<T, C, F, M> implements EnumLeafInfo<T, C>, Element<T, C>, Iterable<EnumConstantImpl<T, C, F, M>> {
  final C clazz;
  
  NonElement<T, C> baseType;
  
  private final T type;
  
  private final QName typeName;
  
  private EnumConstantImpl<T, C, F, M> firstConstant;
  
  private QName elementName;
  
  protected boolean tokenStringType;
  
  public EnumLeafInfoImpl(ModelBuilder<T, C, F, M> paramModelBuilder, Locatable paramLocatable, C paramC, T paramT) {
    super(paramModelBuilder, paramLocatable);
    this.clazz = paramC;
    this.type = paramT;
    this.elementName = parseElementName(paramC);
    this.typeName = parseTypeName(paramC);
    XmlEnum xmlEnum = (XmlEnum)paramModelBuilder.reader.getClassAnnotation(XmlEnum.class, paramC, this);
    if (xmlEnum != null) {
      Object object = paramModelBuilder.reader.getClassValue(xmlEnum, "value");
      this.baseType = paramModelBuilder.getTypeInfo(object, this);
    } else {
      this.baseType = paramModelBuilder.getTypeInfo(paramModelBuilder.nav.ref(String.class), this);
    } 
  }
  
  protected void calcConstants() {
    EnumConstantImpl enumConstantImpl = null;
    Collection collection = nav().getDeclaredFields(this.clazz);
    for (Object object : collection) {
      if (nav().isSameType(nav().getFieldType(object), nav().ref(String.class))) {
        XmlSchemaType xmlSchemaType = (XmlSchemaType)this.builder.reader.getFieldAnnotation(XmlSchemaType.class, object, this);
        if (xmlSchemaType != null && "token".equals(xmlSchemaType.name())) {
          this.tokenStringType = true;
          break;
        } 
      } 
    } 
    Object[] arrayOfObject = nav().getEnumConstants(this.clazz);
    for (int i = arrayOfObject.length - 1; i >= 0; i--) {
      String str2;
      Object object = arrayOfObject[i];
      String str1 = nav().getFieldName(object);
      XmlEnumValue xmlEnumValue = (XmlEnumValue)this.builder.reader.getFieldAnnotation(XmlEnumValue.class, object, this);
      if (xmlEnumValue == null) {
        str2 = str1;
      } else {
        str2 = xmlEnumValue.value();
      } 
      enumConstantImpl = createEnumConstant(str1, str2, object, enumConstantImpl);
    } 
    this.firstConstant = enumConstantImpl;
  }
  
  protected EnumConstantImpl<T, C, F, M> createEnumConstant(String paramString1, String paramString2, F paramF, EnumConstantImpl<T, C, F, M> paramEnumConstantImpl) { return new EnumConstantImpl(this, paramString1, paramString2, paramEnumConstantImpl); }
  
  public T getType() { return (T)this.type; }
  
  public boolean isToken() { return this.tokenStringType; }
  
  public final boolean canBeReferencedByIDREF() { return false; }
  
  public QName getTypeName() { return this.typeName; }
  
  public C getClazz() { return (C)this.clazz; }
  
  public NonElement<T, C> getBaseType() { return this.baseType; }
  
  public boolean isSimpleType() { return true; }
  
  public Location getLocation() { return nav().getClassLocation(this.clazz); }
  
  public Iterable<? extends EnumConstantImpl<T, C, F, M>> getConstants() {
    if (this.firstConstant == null)
      calcConstants(); 
    return this;
  }
  
  public void link() {
    getConstants();
    super.link();
  }
  
  public Element<T, C> getSubstitutionHead() { return null; }
  
  public QName getElementName() { return this.elementName; }
  
  public boolean isElement() { return (this.elementName != null); }
  
  public Element<T, C> asElement() { return isElement() ? this : null; }
  
  public ClassInfo<T, C> getScope() { return null; }
  
  public Iterator<EnumConstantImpl<T, C, F, M>> iterator() { return new Iterator<EnumConstantImpl<T, C, F, M>>() {
        private EnumConstantImpl<T, C, F, M> next = EnumLeafInfoImpl.this.firstConstant;
        
        public boolean hasNext() { return (this.next != null); }
        
        public EnumConstantImpl<T, C, F, M> next() {
          EnumConstantImpl enumConstantImpl = this.next;
          this.next = this.next.next;
          return enumConstantImpl;
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\EnumLeafInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */