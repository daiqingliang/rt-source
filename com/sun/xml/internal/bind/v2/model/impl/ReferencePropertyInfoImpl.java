package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.ReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.WildcardMode;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.namespace.QName;

class ReferencePropertyInfoImpl<T, C, F, M> extends ERPropertyInfoImpl<T, C, F, M> implements ReferencePropertyInfo<T, C>, DummyPropertyInfo<T, C, F, M> {
  private Set<Element<T, C>> types;
  
  private Set<ReferencePropertyInfoImpl<T, C, F, M>> subTypes = new LinkedHashSet();
  
  private final boolean isMixed;
  
  private final WildcardMode wildcard;
  
  private final C domHandler;
  
  private Boolean isRequired;
  
  private static boolean is2_2 = true;
  
  public ReferencePropertyInfoImpl(ClassInfoImpl<T, C, F, M> paramClassInfoImpl, PropertySeed<T, C, F, M> paramPropertySeed) {
    super(paramClassInfoImpl, paramPropertySeed);
    this.isMixed = (paramPropertySeed.readAnnotation(javax.xml.bind.annotation.XmlMixed.class) != null);
    XmlAnyElement xmlAnyElement = (XmlAnyElement)paramPropertySeed.readAnnotation(XmlAnyElement.class);
    if (xmlAnyElement == null) {
      this.wildcard = null;
      this.domHandler = null;
    } else {
      this.wildcard = xmlAnyElement.lax() ? WildcardMode.LAX : WildcardMode.SKIP;
      this.domHandler = nav().asDecl(reader().getClassValue(xmlAnyElement, "value"));
    } 
  }
  
  public Set<? extends Element<T, C>> ref() { return getElements(); }
  
  public PropertyKind kind() { return PropertyKind.REFERENCE; }
  
  public Set<? extends Element<T, C>> getElements() {
    if (this.types == null)
      calcTypes(false); 
    assert this.types != null;
    return this.types;
  }
  
  private void calcTypes(boolean paramBoolean) {
    XmlElementRef[] arrayOfXmlElementRef;
    this.types = new LinkedHashSet();
    XmlElementRefs xmlElementRefs = (XmlElementRefs)this.seed.readAnnotation(XmlElementRefs.class);
    XmlElementRef xmlElementRef = (XmlElementRef)this.seed.readAnnotation(XmlElementRef.class);
    if (xmlElementRefs != null && xmlElementRef != null)
      this.parent.builder.reportError(new IllegalAnnotationException(Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(new Object[] { nav().getClassName(this.parent.getClazz()) + '#' + this.seed.getName(), xmlElementRef.annotationType().getName(), xmlElementRefs.annotationType().getName() }, ), xmlElementRef, xmlElementRefs)); 
    if (xmlElementRefs != null) {
      arrayOfXmlElementRef = xmlElementRefs.value();
    } else if (xmlElementRef != null) {
      arrayOfXmlElementRef = new XmlElementRef[] { xmlElementRef };
    } else {
      arrayOfXmlElementRef = null;
    } 
    this.isRequired = Boolean.valueOf(!isCollection());
    if (arrayOfXmlElementRef != null) {
      Navigator navigator = nav();
      AnnotationReader annotationReader = reader();
      Object object1 = navigator.ref(XmlElementRef.DEFAULT.class);
      Object object2 = navigator.asDecl(javax.xml.bind.JAXBElement.class);
      for (Annotation annotation : arrayOfXmlElementRef) {
        boolean bool;
        Object object = annotationReader.getClassValue(annotation, "type");
        if (nav().isSameType(object, object1))
          object = navigator.erasure(getIndividualType()); 
        if (navigator.getBaseClass(object, object2) != null) {
          bool = addGenericElement(annotation);
        } else {
          bool = addAllSubtypes(object);
        } 
        if (this.isRequired.booleanValue() && !isRequired(annotation))
          this.isRequired = Boolean.valueOf(false); 
        if (paramBoolean && !bool) {
          if (nav().isSameType(object, navigator.ref(javax.xml.bind.JAXBElement.class))) {
            this.parent.builder.reportError(new IllegalAnnotationException(Messages.NO_XML_ELEMENT_DECL.format(new Object[] { getEffectiveNamespaceFor(annotation), annotation.name() }, ), this));
          } else {
            this.parent.builder.reportError(new IllegalAnnotationException(Messages.INVALID_XML_ELEMENT_REF.format(new Object[] { object }, ), this));
          } 
          return;
        } 
      } 
    } 
    for (ReferencePropertyInfoImpl referencePropertyInfoImpl : this.subTypes) {
      PropertySeed propertySeed = referencePropertyInfoImpl.seed;
      xmlElementRefs = (XmlElementRefs)propertySeed.readAnnotation(XmlElementRefs.class);
      xmlElementRef = (XmlElementRef)propertySeed.readAnnotation(XmlElementRef.class);
      if (xmlElementRefs != null && xmlElementRef != null)
        this.parent.builder.reportError(new IllegalAnnotationException(Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(new Object[] { nav().getClassName(this.parent.getClazz()) + '#' + this.seed.getName(), xmlElementRef.annotationType().getName(), xmlElementRefs.annotationType().getName() }, ), xmlElementRef, xmlElementRefs)); 
      if (xmlElementRefs != null) {
        arrayOfXmlElementRef = xmlElementRefs.value();
      } else if (xmlElementRef != null) {
        arrayOfXmlElementRef = new XmlElementRef[] { xmlElementRef };
      } else {
        arrayOfXmlElementRef = null;
      } 
      if (arrayOfXmlElementRef != null) {
        Navigator navigator = nav();
        AnnotationReader annotationReader = reader();
        Object object1 = navigator.ref(XmlElementRef.DEFAULT.class);
        Object object2 = navigator.asDecl(javax.xml.bind.JAXBElement.class);
        for (XmlElementRef xmlElementRef1 : arrayOfXmlElementRef) {
          boolean bool;
          Object object = annotationReader.getClassValue(xmlElementRef1, "type");
          if (nav().isSameType(object, object1))
            object = navigator.erasure(getIndividualType()); 
          if (navigator.getBaseClass(object, object2) != null) {
            bool = addGenericElement(xmlElementRef1, referencePropertyInfoImpl);
          } else {
            bool = addAllSubtypes(object);
          } 
          if (paramBoolean && !bool) {
            if (nav().isSameType(object, navigator.ref(javax.xml.bind.JAXBElement.class))) {
              this.parent.builder.reportError(new IllegalAnnotationException(Messages.NO_XML_ELEMENT_DECL.format(new Object[] { getEffectiveNamespaceFor(xmlElementRef1), xmlElementRef1.name() }, ), this));
            } else {
              this.parent.builder.reportError(new IllegalAnnotationException(Messages.INVALID_XML_ELEMENT_REF.format(new Object[0]), this));
            } 
            return;
          } 
        } 
      } 
    } 
    this.types = Collections.unmodifiableSet(this.types);
  }
  
  public boolean isRequired() {
    if (this.isRequired == null)
      calcTypes(false); 
    return this.isRequired.booleanValue();
  }
  
  private boolean isRequired(XmlElementRef paramXmlElementRef) {
    if (!is2_2)
      return true; 
    try {
      return paramXmlElementRef.required();
    } catch (LinkageError linkageError) {
      is2_2 = false;
      return true;
    } 
  }
  
  private boolean addGenericElement(XmlElementRef paramXmlElementRef) {
    String str = getEffectiveNamespaceFor(paramXmlElementRef);
    return addGenericElement(this.parent.owner.getElementInfo(this.parent.getClazz(), new QName(str, paramXmlElementRef.name())));
  }
  
  private boolean addGenericElement(XmlElementRef paramXmlElementRef, ReferencePropertyInfoImpl<T, C, F, M> paramReferencePropertyInfoImpl) {
    String str = paramReferencePropertyInfoImpl.getEffectiveNamespaceFor(paramXmlElementRef);
    ElementInfoImpl elementInfoImpl = this.parent.owner.getElementInfo(paramReferencePropertyInfoImpl.parent.getClazz(), new QName(str, paramXmlElementRef.name()));
    this.types.add(elementInfoImpl);
    return true;
  }
  
  private String getEffectiveNamespaceFor(XmlElementRef paramXmlElementRef) {
    String str = paramXmlElementRef.namespace();
    XmlSchema xmlSchema = (XmlSchema)reader().getPackageAnnotation(XmlSchema.class, this.parent.getClazz(), this);
    if (xmlSchema != null && xmlSchema.attributeFormDefault() == XmlNsForm.QUALIFIED && str.length() == 0)
      str = this.parent.builder.defaultNsUri; 
    return str;
  }
  
  private boolean addGenericElement(ElementInfo<T, C> paramElementInfo) {
    if (paramElementInfo == null)
      return false; 
    this.types.add(paramElementInfo);
    for (ElementInfo elementInfo : paramElementInfo.getSubstitutionMembers())
      addGenericElement(elementInfo); 
    return true;
  }
  
  private boolean addAllSubtypes(T paramT) {
    Navigator navigator = nav();
    NonElement nonElement = this.parent.builder.getClassInfo(navigator.asDecl(paramT), this);
    if (!(nonElement instanceof ClassInfo))
      return false; 
    boolean bool = false;
    ClassInfo classInfo = (ClassInfo)nonElement;
    if (classInfo.isElement()) {
      this.types.add(classInfo.asElement());
      bool = true;
    } 
    for (ClassInfo classInfo1 : this.parent.owner.beans().values()) {
      if (classInfo1.isElement() && navigator.isSubClassOf(classInfo1.getType(), paramT)) {
        this.types.add(classInfo1.asElement());
        bool = true;
      } 
    } 
    for (ElementInfo elementInfo : this.parent.owner.getElementMappings(null).values()) {
      if (navigator.isSubClassOf(elementInfo.getType(), paramT)) {
        this.types.add(elementInfo);
        bool = true;
      } 
    } 
    return bool;
  }
  
  protected void link() {
    super.link();
    calcTypes(true);
  }
  
  public final void addType(PropertyInfoImpl<T, C, F, M> paramPropertyInfoImpl) { this.subTypes.add((ReferencePropertyInfoImpl)paramPropertyInfoImpl); }
  
  public final boolean isMixed() { return this.isMixed; }
  
  public final WildcardMode getWildcard() { return this.wildcard; }
  
  public final C getDOMHandler() { return (C)this.domHandler; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\ReferencePropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */