package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.xml.internal.bind.v2.model.core.AttributePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.namespace.QName;

class AttributePropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> extends SingleTypePropertyInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> implements AttributePropertyInfo<TypeT, ClassDeclT> {
  private final QName xmlName;
  
  private final boolean isRequired;
  
  AttributePropertyInfoImpl(ClassInfoImpl<TypeT, ClassDeclT, FieldT, MethodT> paramClassInfoImpl, PropertySeed<TypeT, ClassDeclT, FieldT, MethodT> paramPropertySeed) {
    super(paramClassInfoImpl, paramPropertySeed);
    XmlAttribute xmlAttribute = (XmlAttribute)paramPropertySeed.readAnnotation(XmlAttribute.class);
    assert xmlAttribute != null;
    if (xmlAttribute.required()) {
      this.isRequired = true;
    } else {
      this.isRequired = nav().isPrimitive(getIndividualType());
    } 
    this.xmlName = calcXmlName(xmlAttribute);
  }
  
  private QName calcXmlName(XmlAttribute paramXmlAttribute) {
    String str1 = paramXmlAttribute.namespace();
    String str2 = paramXmlAttribute.name();
    if (str2.equals("##default"))
      str2 = NameConverter.standard.toVariableName(getName()); 
    if (str1.equals("##default")) {
      XmlSchema xmlSchema = (XmlSchema)reader().getPackageAnnotation(XmlSchema.class, this.parent.getClazz(), this);
      if (xmlSchema != null) {
        switch (xmlSchema.attributeFormDefault()) {
          case QUALIFIED:
            str1 = this.parent.getTypeName().getNamespaceURI();
            if (str1.length() == 0)
              str1 = this.parent.builder.defaultNsUri; 
            break;
          case UNQUALIFIED:
          case UNSET:
            str1 = "";
            break;
        } 
      } else {
        str1 = "";
      } 
    } 
    return new QName(str1.intern(), str2.intern());
  }
  
  public boolean isRequired() { return this.isRequired; }
  
  public final QName getXmlName() { return this.xmlName; }
  
  public final PropertyKind kind() { return PropertyKind.ATTRIBUTE; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\AttributePropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */