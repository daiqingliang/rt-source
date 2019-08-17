package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.annotation.OverrideAnnotationOf;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.annotation.MethodLocatable;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.Element;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.bind.v2.util.EditDistance;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;

public class ClassInfoImpl<T, C, F, M> extends TypeInfoImpl<T, C, F, M> implements ClassInfo<T, C>, Element<T, C> {
  protected final C clazz;
  
  private final QName elementName;
  
  private final QName typeName;
  
  private FinalArrayList<PropertyInfoImpl<T, C, F, M>> properties;
  
  private String[] propOrder;
  
  private ClassInfoImpl<T, C, F, M> baseClass;
  
  private boolean baseClassComputed = false;
  
  private boolean hasSubClasses = false;
  
  protected PropertySeed<T, C, F, M> attributeWildcard;
  
  private M factoryMethod = null;
  
  private static final SecondaryAnnotation[] SECONDARY_ANNOTATIONS = SecondaryAnnotation.values();
  
  private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];
  
  private static final HashMap<Class, Integer> ANNOTATION_NUMBER_MAP = new HashMap();
  
  private static final String[] DEFAULT_ORDER;
  
  ClassInfoImpl(ModelBuilder<T, C, F, M> paramModelBuilder, Locatable paramLocatable, C paramC) {
    super(paramModelBuilder, paramLocatable);
    this.clazz = paramC;
    assert paramC != null;
    this.elementName = parseElementName(paramC);
    XmlType xmlType = (XmlType)reader().getClassAnnotation(XmlType.class, paramC, this);
    this.typeName = parseTypeName(paramC, xmlType);
    if (xmlType != null) {
      String[] arrayOfString = xmlType.propOrder();
      if (arrayOfString.length == 0) {
        this.propOrder = null;
      } else if (arrayOfString[0].length() == 0) {
        this.propOrder = DEFAULT_ORDER;
      } else {
        this.propOrder = arrayOfString;
      } 
    } else {
      this.propOrder = DEFAULT_ORDER;
    } 
    XmlAccessorOrder xmlAccessorOrder = (XmlAccessorOrder)reader().getPackageAnnotation(XmlAccessorOrder.class, paramC, this);
    if (xmlAccessorOrder != null && xmlAccessorOrder.value() == XmlAccessOrder.UNDEFINED)
      this.propOrder = null; 
    xmlAccessorOrder = (XmlAccessorOrder)reader().getClassAnnotation(XmlAccessorOrder.class, paramC, this);
    if (xmlAccessorOrder != null && xmlAccessorOrder.value() == XmlAccessOrder.UNDEFINED)
      this.propOrder = null; 
    if (nav().isInterface(paramC))
      paramModelBuilder.reportError(new IllegalAnnotationException(Messages.CANT_HANDLE_INTERFACE.format(new Object[] { nav().getClassName(paramC) }, ), this)); 
    if (!hasFactoryConstructor(xmlType) && !nav().hasDefaultConstructor(paramC))
      if (nav().isInnerClass(paramC)) {
        paramModelBuilder.reportError(new IllegalAnnotationException(Messages.CANT_HANDLE_INNER_CLASS.format(new Object[] { nav().getClassName(paramC) }, ), this));
      } else if (this.elementName != null) {
        paramModelBuilder.reportError(new IllegalAnnotationException(Messages.NO_DEFAULT_CONSTRUCTOR.format(new Object[] { nav().getClassName(paramC) }, ), this));
      }  
  }
  
  public ClassInfoImpl<T, C, F, M> getBaseClass() {
    if (!this.baseClassComputed) {
      Object object = nav().getSuperClass(this.clazz);
      if (object == null || object == nav().asDecl(Object.class)) {
        this.baseClass = null;
      } else {
        NonElement nonElement = this.builder.getClassInfo(object, true, this);
        if (nonElement instanceof ClassInfoImpl) {
          this.baseClass = (ClassInfoImpl)nonElement;
          this.baseClass.hasSubClasses = true;
        } else {
          this.baseClass = null;
        } 
      } 
      this.baseClassComputed = true;
    } 
    return this.baseClass;
  }
  
  public final Element<T, C> getSubstitutionHead() {
    ClassInfoImpl classInfoImpl;
    for (classInfoImpl = getBaseClass(); classInfoImpl != null && !classInfoImpl.isElement(); classInfoImpl = classInfoImpl.getBaseClass());
    return classInfoImpl;
  }
  
  public final C getClazz() { return (C)this.clazz; }
  
  public ClassInfoImpl<T, C, F, M> getScope() { return null; }
  
  public final T getType() { return (T)nav().use(this.clazz); }
  
  public boolean canBeReferencedByIDREF() {
    for (PropertyInfo propertyInfo : getProperties()) {
      if (propertyInfo.id() == ID.ID)
        return true; 
    } 
    ClassInfoImpl classInfoImpl = getBaseClass();
    return (classInfoImpl != null) ? classInfoImpl.canBeReferencedByIDREF() : 0;
  }
  
  public final String getName() { return nav().getClassName(this.clazz); }
  
  public <A extends Annotation> A readAnnotation(Class<A> paramClass) { return (A)reader().getClassAnnotation(paramClass, this.clazz, this); }
  
  public Element<T, C> asElement() { return isElement() ? this : null; }
  
  public List<? extends PropertyInfo<T, C>> getProperties() {
    if (this.properties != null)
      return this.properties; 
    XmlAccessType xmlAccessType = getAccessType();
    this.properties = new FinalArrayList();
    findFieldProperties(this.clazz, xmlAccessType);
    findGetterSetterProperties(xmlAccessType);
    if (this.propOrder == DEFAULT_ORDER || this.propOrder == null) {
      XmlAccessOrder xmlAccessOrder = getAccessorOrder();
      if (xmlAccessOrder == XmlAccessOrder.ALPHABETICAL)
        Collections.sort(this.properties); 
    } else {
      PropertySorter propertySorter = new PropertySorter(this);
      for (PropertyInfoImpl propertyInfoImpl1 : this.properties)
        propertySorter.checkedGet(propertyInfoImpl1); 
      Collections.sort(this.properties, propertySorter);
      propertySorter.checkUnusedProperties();
    } 
    Locatable locatable = null;
    PropertyInfoImpl propertyInfoImpl = null;
    for (PropertyInfoImpl propertyInfoImpl1 : this.properties) {
      switch (propertyInfoImpl1.kind()) {
        case TRANSIENT:
        case ANY_ATTRIBUTE:
        case ATTRIBUTE:
          propertyInfoImpl = propertyInfoImpl1;
          continue;
        case VALUE:
          if (locatable != null)
            this.builder.reportError(new IllegalAnnotationException(Messages.MULTIPLE_VALUE_PROPERTY.format(new Object[0]), locatable, propertyInfoImpl1)); 
          if (getBaseClass() != null)
            this.builder.reportError(new IllegalAnnotationException(Messages.XMLVALUE_IN_DERIVED_TYPE.format(new Object[0]), propertyInfoImpl1)); 
          locatable = propertyInfoImpl1;
          continue;
        case ELEMENT:
          continue;
      } 
      assert false;
    } 
    if (propertyInfoImpl != null && locatable != null)
      this.builder.reportError(new IllegalAnnotationException(Messages.ELEMENT_AND_VALUE_PROPERTY.format(new Object[0]), locatable, propertyInfoImpl)); 
    return this.properties;
  }
  
  private void findFieldProperties(C paramC, XmlAccessType paramXmlAccessType) {
    Object object = nav().getSuperClass(paramC);
    if (shouldRecurseSuperClass(object))
      findFieldProperties(object, paramXmlAccessType); 
    for (Object object1 : nav().getDeclaredFields(paramC)) {
      Annotation[] arrayOfAnnotation = reader().getAllFieldAnnotations(object1, this);
      boolean bool = reader().hasFieldAnnotation(OverrideAnnotationOf.class, object1);
      if (nav().isTransient(object1)) {
        if (hasJAXBAnnotation(arrayOfAnnotation))
          this.builder.reportError(new IllegalAnnotationException(Messages.TRANSIENT_FIELD_NOT_BINDABLE.format(new Object[] { nav().getFieldName(object1) }, ), getSomeJAXBAnnotation(arrayOfAnnotation))); 
        continue;
      } 
      if (nav().isStaticField(object1)) {
        if (hasJAXBAnnotation(arrayOfAnnotation))
          addProperty(createFieldSeed(object1), arrayOfAnnotation, false); 
        continue;
      } 
      if (paramXmlAccessType == XmlAccessType.FIELD || (paramXmlAccessType == XmlAccessType.PUBLIC_MEMBER && nav().isPublicField(object1)) || hasJAXBAnnotation(arrayOfAnnotation))
        if (bool) {
          ClassInfo classInfo = getBaseClass();
          while (classInfo != null && classInfo.getProperty("content") == null)
            classInfo = classInfo.getBaseClass(); 
          DummyPropertyInfo dummyPropertyInfo = (DummyPropertyInfo)classInfo.getProperty("content");
          PropertySeed propertySeed = createFieldSeed(object1);
          dummyPropertyInfo.addType(createReferenceProperty(propertySeed));
        } else {
          addProperty(createFieldSeed(object1), arrayOfAnnotation, false);
        }  
      checkFieldXmlLocation(object1);
    } 
  }
  
  public final boolean hasValueProperty() {
    ClassInfoImpl classInfoImpl = getBaseClass();
    if (classInfoImpl != null && classInfoImpl.hasValueProperty())
      return true; 
    for (PropertyInfo propertyInfo : getProperties()) {
      if (propertyInfo instanceof com.sun.xml.internal.bind.v2.model.core.ValuePropertyInfo)
        return true; 
    } 
    return false;
  }
  
  public PropertyInfo<T, C> getProperty(String paramString) {
    for (PropertyInfo propertyInfo : getProperties()) {
      if (propertyInfo.getName().equals(paramString))
        return propertyInfo; 
    } 
    return null;
  }
  
  protected void checkFieldXmlLocation(F paramF) {}
  
  private <T extends Annotation> T getClassOrPackageAnnotation(Class<T> paramClass) {
    Annotation annotation = reader().getClassAnnotation(paramClass, this.clazz, this);
    return (annotation != null) ? (T)annotation : (T)reader().getPackageAnnotation(paramClass, this.clazz, this);
  }
  
  private XmlAccessType getAccessType() {
    XmlAccessorType xmlAccessorType = (XmlAccessorType)getClassOrPackageAnnotation(XmlAccessorType.class);
    return (xmlAccessorType != null) ? xmlAccessorType.value() : XmlAccessType.PUBLIC_MEMBER;
  }
  
  private XmlAccessOrder getAccessorOrder() {
    XmlAccessorOrder xmlAccessorOrder = (XmlAccessorOrder)getClassOrPackageAnnotation(XmlAccessorOrder.class);
    return (xmlAccessorOrder != null) ? xmlAccessorOrder.value() : XmlAccessOrder.UNDEFINED;
  }
  
  public boolean hasProperties() { return !this.properties.isEmpty(); }
  
  private static <T> T pickOne(T... paramVarArgs) {
    for (T t : paramVarArgs) {
      if (t != null)
        return t; 
    } 
    return null;
  }
  
  private static <T> List<T> makeSet(T... paramVarArgs) {
    FinalArrayList finalArrayList = new FinalArrayList();
    for (T t : paramVarArgs) {
      if (t != null)
        finalArrayList.add(t); 
    } 
    return finalArrayList;
  }
  
  private void checkConflict(Annotation paramAnnotation1, Annotation paramAnnotation2) throws DuplicateException {
    assert paramAnnotation2 != null;
    if (paramAnnotation1 != null)
      throw new DuplicateException(paramAnnotation1, paramAnnotation2); 
  }
  
  private void addProperty(PropertySeed<T, C, F, M> paramPropertySeed, Annotation[] paramArrayOfAnnotation, boolean paramBoolean) {
    Annotation annotation1 = null;
    Annotation annotation2 = null;
    Annotation annotation3 = null;
    Annotation annotation4 = null;
    Annotation annotation5 = null;
    Annotation annotation6 = null;
    Annotation annotation7 = null;
    Annotation annotation8 = null;
    Annotation annotation9 = null;
    Annotation annotation10 = null;
    Annotation annotation11 = null;
    int i = 0;
    try {
      for (Annotation annotation : paramArrayOfAnnotation) {
        Integer integer = (Integer)ANNOTATION_NUMBER_MAP.get(annotation.annotationType());
        if (integer != null)
          switch (integer.intValue()) {
            case 0:
              checkConflict(annotation1, annotation);
              annotation1 = (XmlTransient)annotation;
              break;
            case 1:
              checkConflict(annotation2, annotation);
              annotation2 = (XmlAnyAttribute)annotation;
              break;
            case 2:
              checkConflict(annotation3, annotation);
              annotation3 = (XmlAttribute)annotation;
              break;
            case 3:
              checkConflict(annotation4, annotation);
              annotation4 = (XmlValue)annotation;
              break;
            case 4:
              checkConflict(annotation5, annotation);
              annotation5 = (XmlElement)annotation;
              break;
            case 5:
              checkConflict(annotation6, annotation);
              annotation6 = (XmlElements)annotation;
              break;
            case 6:
              checkConflict(annotation7, annotation);
              annotation7 = (XmlElementRef)annotation;
              break;
            case 7:
              checkConflict(annotation8, annotation);
              annotation8 = (XmlElementRefs)annotation;
              break;
            case 8:
              checkConflict(annotation9, annotation);
              annotation9 = (XmlAnyElement)annotation;
              break;
            case 9:
              checkConflict(annotation10, annotation);
              annotation10 = (XmlMixed)annotation;
              break;
            case 10:
              checkConflict(annotation11, annotation);
              annotation11 = (OverrideAnnotationOf)annotation;
              break;
            default:
              i |= 1 << integer.intValue() - 20;
              break;
          }  
      } 
      PropertyGroup propertyGroup = null;
      byte b = 0;
      if (annotation1 != null) {
        propertyGroup = PropertyGroup.TRANSIENT;
        b++;
      } 
      if (annotation2 != null) {
        propertyGroup = PropertyGroup.ANY_ATTRIBUTE;
        b++;
      } 
      if (annotation3 != null) {
        propertyGroup = PropertyGroup.ATTRIBUTE;
        b++;
      } 
      if (annotation4 != null) {
        propertyGroup = PropertyGroup.VALUE;
        b++;
      } 
      if (annotation5 != null || annotation6 != null) {
        propertyGroup = PropertyGroup.ELEMENT;
        b++;
      } 
      if (annotation7 != null || annotation8 != null || annotation9 != null || annotation10 != null || annotation11 != null) {
        propertyGroup = PropertyGroup.ELEMENT_REF;
        b++;
      } 
      if (b > 1) {
        List list = makeSet(new Annotation[] { annotation1, annotation2, annotation3, annotation4, (Annotation)pickOne(new Annotation[] { annotation5, annotation6 }), (Annotation)pickOne(new Annotation[] { annotation7, annotation8, annotation9 }) });
        throw new ConflictException(list);
      } 
      if (propertyGroup == null) {
        assert b == 0;
        if (nav().isSubClassOf(paramPropertySeed.getRawType(), nav().ref(Map.class)) && !paramPropertySeed.hasAnnotation(javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.class)) {
          propertyGroup = PropertyGroup.MAP;
        } else {
          propertyGroup = PropertyGroup.ELEMENT;
        } 
      } else if (propertyGroup.equals(PropertyGroup.ELEMENT) && nav().isSubClassOf(paramPropertySeed.getRawType(), nav().ref(Map.class)) && !paramPropertySeed.hasAnnotation(javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.class)) {
        propertyGroup = PropertyGroup.MAP;
      } 
      if ((i & propertyGroup.allowedsecondaryAnnotations) != 0) {
        for (SecondaryAnnotation secondaryAnnotation : SECONDARY_ANNOTATIONS) {
          if (!propertyGroup.allows(secondaryAnnotation))
            for (Class clazz1 : secondaryAnnotation.members) {
              Annotation annotation = paramPropertySeed.readAnnotation(clazz1);
              if (annotation != null) {
                this.builder.reportError(new IllegalAnnotationException(Messages.ANNOTATION_NOT_ALLOWED.format(new Object[] { clazz1.getSimpleName() }, ), annotation));
                return;
              } 
            }  
        } 
        assert false;
      } 
      switch (propertyGroup) {
        case TRANSIENT:
          return;
        case ANY_ATTRIBUTE:
          if (this.attributeWildcard != null) {
            this.builder.reportError(new IllegalAnnotationException(Messages.TWO_ATTRIBUTE_WILDCARDS.format(new Object[] { nav().getClassName(getClazz()) }, ), annotation2, this.attributeWildcard));
            return;
          } 
          this.attributeWildcard = paramPropertySeed;
          if (inheritsAttributeWildcard()) {
            this.builder.reportError(new IllegalAnnotationException(Messages.SUPER_CLASS_HAS_WILDCARD.format(new Object[0]), annotation2, getInheritedAttributeWildcard()));
            return;
          } 
          if (!nav().isSubClassOf(paramPropertySeed.getRawType(), nav().ref(Map.class))) {
            this.builder.reportError(new IllegalAnnotationException(Messages.INVALID_ATTRIBUTE_WILDCARD_TYPE.format(new Object[] { nav().getTypeName(paramPropertySeed.getRawType()) }, ), annotation2, getInheritedAttributeWildcard()));
            return;
          } 
          return;
        case ATTRIBUTE:
          this.properties.add(createAttributeProperty(paramPropertySeed));
          return;
        case VALUE:
          this.properties.add(createValueProperty(paramPropertySeed));
          return;
        case ELEMENT:
          this.properties.add(createElementProperty(paramPropertySeed));
          return;
        case ELEMENT_REF:
          this.properties.add(createReferenceProperty(paramPropertySeed));
          return;
        case MAP:
          this.properties.add(createMapProperty(paramPropertySeed));
          return;
      } 
      assert false;
    } catch (ConflictException conflictException) {
      List list = conflictException.annotations;
      this.builder.reportError(new IllegalAnnotationException(Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(new Object[] { nav().getClassName(getClazz()) + '#' + paramPropertySeed.getName(), ((Annotation)list.get(0)).annotationType().getName(), ((Annotation)list.get(1)).annotationType().getName() }, ), (Annotation)list.get(0), (Annotation)list.get(1)));
    } catch (DuplicateException duplicateException) {
      this.builder.reportError(new IllegalAnnotationException(Messages.DUPLICATE_ANNOTATIONS.format(new Object[] { duplicateException.a1.annotationType().getName() }, ), duplicateException.a1, duplicateException.a2));
    } 
  }
  
  protected ReferencePropertyInfoImpl<T, C, F, M> createReferenceProperty(PropertySeed<T, C, F, M> paramPropertySeed) { return new ReferencePropertyInfoImpl(this, paramPropertySeed); }
  
  protected AttributePropertyInfoImpl<T, C, F, M> createAttributeProperty(PropertySeed<T, C, F, M> paramPropertySeed) { return new AttributePropertyInfoImpl(this, paramPropertySeed); }
  
  protected ValuePropertyInfoImpl<T, C, F, M> createValueProperty(PropertySeed<T, C, F, M> paramPropertySeed) { return new ValuePropertyInfoImpl(this, paramPropertySeed); }
  
  protected ElementPropertyInfoImpl<T, C, F, M> createElementProperty(PropertySeed<T, C, F, M> paramPropertySeed) { return new ElementPropertyInfoImpl(this, paramPropertySeed); }
  
  protected MapPropertyInfoImpl<T, C, F, M> createMapProperty(PropertySeed<T, C, F, M> paramPropertySeed) { return new MapPropertyInfoImpl(this, paramPropertySeed); }
  
  private void findGetterSetterProperties(XmlAccessType paramXmlAccessType) {
    LinkedHashMap linkedHashMap1 = new LinkedHashMap();
    LinkedHashMap linkedHashMap2 = new LinkedHashMap();
    Object object = this.clazz;
    do {
      collectGetterSetters(this.clazz, linkedHashMap1, linkedHashMap2);
      object = nav().getSuperClass(object);
    } while (shouldRecurseSuperClass(object));
    TreeSet treeSet = new TreeSet(linkedHashMap1.keySet());
    treeSet.retainAll(linkedHashMap2.keySet());
    resurrect(linkedHashMap1, treeSet);
    resurrect(linkedHashMap2, treeSet);
    for (String str : treeSet) {
      Object object1 = linkedHashMap1.get(str);
      Object object2 = linkedHashMap2.get(str);
      Annotation[] arrayOfAnnotation1 = (object1 != null) ? reader().getAllMethodAnnotations(object1, new MethodLocatable(this, object1, nav())) : EMPTY_ANNOTATIONS;
      Annotation[] arrayOfAnnotation2 = (object2 != null) ? reader().getAllMethodAnnotations(object2, new MethodLocatable(this, object2, nav())) : EMPTY_ANNOTATIONS;
      boolean bool1 = (hasJAXBAnnotation(arrayOfAnnotation1) || hasJAXBAnnotation(arrayOfAnnotation2)) ? 1 : 0;
      boolean bool2 = false;
      if (!bool1)
        bool2 = (object1 != null && nav().isOverriding(object1, object) && object2 != null && nav().isOverriding(object2, object)) ? 1 : 0; 
      if ((paramXmlAccessType == XmlAccessType.PROPERTY && !bool2) || (paramXmlAccessType == XmlAccessType.PUBLIC_MEMBER && isConsideredPublic(object1) && isConsideredPublic(object2) && !bool2) || bool1) {
        Annotation[] arrayOfAnnotation;
        if (object1 != null && object2 != null && !nav().isSameType(nav().getReturnType(object1), nav().getMethodParameters(object2)[0])) {
          this.builder.reportError(new IllegalAnnotationException(Messages.GETTER_SETTER_INCOMPATIBLE_TYPE.format(new Object[] { nav().getTypeName(nav().getReturnType(object1)), nav().getTypeName(nav().getMethodParameters(object2)[0]) }, ), new MethodLocatable(this, object1, nav()), new MethodLocatable(this, object2, nav())));
          continue;
        } 
        if (arrayOfAnnotation1.length == 0) {
          arrayOfAnnotation = arrayOfAnnotation2;
        } else if (arrayOfAnnotation2.length == 0) {
          arrayOfAnnotation = arrayOfAnnotation1;
        } else {
          arrayOfAnnotation = new Annotation[arrayOfAnnotation1.length + arrayOfAnnotation2.length];
          System.arraycopy(arrayOfAnnotation1, 0, arrayOfAnnotation, 0, arrayOfAnnotation1.length);
          System.arraycopy(arrayOfAnnotation2, 0, arrayOfAnnotation, arrayOfAnnotation1.length, arrayOfAnnotation2.length);
        } 
        addProperty(createAccessorSeed(object1, object2), arrayOfAnnotation, false);
      } 
    } 
    linkedHashMap1.keySet().removeAll(treeSet);
    linkedHashMap2.keySet().removeAll(treeSet);
  }
  
  private void collectGetterSetters(C paramC, Map<String, M> paramMap1, Map<String, M> paramMap2) {
    Object object = nav().getSuperClass(paramC);
    if (shouldRecurseSuperClass(object))
      collectGetterSetters(object, paramMap1, paramMap2); 
    Collection collection = nav().getDeclaredMethods(paramC);
    LinkedHashMap linkedHashMap = new LinkedHashMap();
    for (Object object1 : collection) {
      boolean bool = false;
      if (nav().isBridgeMethod(object1))
        continue; 
      String str1 = nav().getMethodName(object1);
      int i = nav().getMethodParameters(object1).length;
      if (nav().isStaticMethod(object1)) {
        ensureNoAnnotation(object1);
        continue;
      } 
      String str2 = getPropertyNameFromGetMethod(str1);
      if (str2 != null && i == 0) {
        paramMap1.put(str2, object1);
        bool = true;
      } 
      str2 = getPropertyNameFromSetMethod(str1);
      if (str2 != null && i == 1) {
        List list = (List)linkedHashMap.get(str2);
        if (null == list) {
          list = new ArrayList();
          linkedHashMap.put(str2, list);
        } 
        list.add(object1);
        bool = true;
      } 
      if (!bool)
        ensureNoAnnotation(object1); 
    } 
    for (Map.Entry entry : paramMap1.entrySet()) {
      String str = (String)entry.getKey();
      Object object1 = entry.getValue();
      List list = (List)linkedHashMap.remove(str);
      if (null == list)
        continue; 
      Object object2 = nav().getReturnType(object1);
      for (Object object3 : list) {
        Object object4 = nav().getMethodParameters(object3)[0];
        if (nav().isSameType(object4, object2))
          paramMap2.put(str, object3); 
      } 
    } 
    for (Map.Entry entry : linkedHashMap.entrySet())
      paramMap2.put(entry.getKey(), ((List)entry.getValue()).get(0)); 
  }
  
  private boolean shouldRecurseSuperClass(C paramC) { return (paramC != null && (this.builder.isReplaced(paramC) || reader().hasClassAnnotation(paramC, XmlTransient.class))); }
  
  private boolean isConsideredPublic(M paramM) { return (paramM == null || nav().isPublicMethod(paramM)); }
  
  private void resurrect(Map<String, M> paramMap, Set<String> paramSet) {
    for (Map.Entry entry : paramMap.entrySet()) {
      if (!paramSet.contains(entry.getKey()) && hasJAXBAnnotation(reader().getAllMethodAnnotations(entry.getValue(), this)))
        paramSet.add(entry.getKey()); 
    } 
  }
  
  private void ensureNoAnnotation(M paramM) {
    Annotation[] arrayOfAnnotation = reader().getAllMethodAnnotations(paramM, this);
    for (Annotation annotation : arrayOfAnnotation) {
      if (isJAXBAnnotation(annotation)) {
        this.builder.reportError(new IllegalAnnotationException(Messages.ANNOTATION_ON_WRONG_METHOD.format(new Object[0]), annotation));
        return;
      } 
    } 
  }
  
  private static boolean isJAXBAnnotation(Annotation paramAnnotation) { return ANNOTATION_NUMBER_MAP.containsKey(paramAnnotation.annotationType()); }
  
  private static boolean hasJAXBAnnotation(Annotation[] paramArrayOfAnnotation) { return (getSomeJAXBAnnotation(paramArrayOfAnnotation) != null); }
  
  private static Annotation getSomeJAXBAnnotation(Annotation[] paramArrayOfAnnotation) {
    for (Annotation annotation : paramArrayOfAnnotation) {
      if (isJAXBAnnotation(annotation))
        return annotation; 
    } 
    return null;
  }
  
  private static String getPropertyNameFromGetMethod(String paramString) { return (paramString.startsWith("get") && paramString.length() > 3) ? paramString.substring(3) : ((paramString.startsWith("is") && paramString.length() > 2) ? paramString.substring(2) : null); }
  
  private static String getPropertyNameFromSetMethod(String paramString) { return (paramString.startsWith("set") && paramString.length() > 3) ? paramString.substring(3) : null; }
  
  protected PropertySeed<T, C, F, M> createFieldSeed(F paramF) { return new FieldPropertySeed(this, paramF); }
  
  protected PropertySeed<T, C, F, M> createAccessorSeed(M paramM1, M paramM2) { return new GetterSetterPropertySeed(this, paramM1, paramM2); }
  
  public final boolean isElement() { return (this.elementName != null); }
  
  public boolean isAbstract() { return nav().isAbstract(this.clazz); }
  
  public boolean isOrdered() { return (this.propOrder != null); }
  
  public final boolean isFinal() { return nav().isFinal(this.clazz); }
  
  public final boolean hasSubClasses() { return this.hasSubClasses; }
  
  public final boolean hasAttributeWildcard() { return (declaresAttributeWildcard() || inheritsAttributeWildcard()); }
  
  public final boolean inheritsAttributeWildcard() { return (getInheritedAttributeWildcard() != null); }
  
  public final boolean declaresAttributeWildcard() { return (this.attributeWildcard != null); }
  
  private PropertySeed<T, C, F, M> getInheritedAttributeWildcard() {
    for (ClassInfoImpl classInfoImpl = getBaseClass(); classInfoImpl != null; classInfoImpl = classInfoImpl.getBaseClass()) {
      if (classInfoImpl.attributeWildcard != null)
        return classInfoImpl.attributeWildcard; 
    } 
    return null;
  }
  
  public final QName getElementName() { return this.elementName; }
  
  public final QName getTypeName() { return this.typeName; }
  
  public final boolean isSimpleType() {
    List list = getProperties();
    return (list.size() != 1) ? false : ((((PropertyInfo)list.get(false)).kind() == PropertyKind.VALUE));
  }
  
  void link() {
    getProperties();
    HashMap hashMap = new HashMap();
    for (PropertyInfoImpl propertyInfoImpl1 : this.properties) {
      propertyInfoImpl1.link();
      PropertyInfoImpl propertyInfoImpl2 = (PropertyInfoImpl)hashMap.put(propertyInfoImpl1.getName(), propertyInfoImpl1);
      if (propertyInfoImpl2 != null)
        this.builder.reportError(new IllegalAnnotationException(Messages.PROPERTY_COLLISION.format(new Object[] { propertyInfoImpl1.getName() }, ), propertyInfoImpl1, propertyInfoImpl2)); 
    } 
    super.link();
  }
  
  public Location getLocation() { return nav().getClassLocation(this.clazz); }
  
  private boolean hasFactoryConstructor(XmlType paramXmlType) {
    if (paramXmlType == null)
      return false; 
    String str = paramXmlType.factoryMethod();
    Object object = reader().getClassValue(paramXmlType, "factoryClass");
    if (str.length() > 0) {
      if (nav().isSameType(object, nav().ref(XmlType.DEFAULT.class)))
        object = nav().use(this.clazz); 
      for (Object object1 : nav().getDeclaredMethods(nav().asDecl(object))) {
        if (nav().getMethodName(object1).equals(str) && nav().isSameType(nav().getReturnType(object1), nav().use(this.clazz)) && nav().getMethodParameters(object1).length == 0 && nav().isStaticMethod(object1)) {
          this.factoryMethod = object1;
          break;
        } 
      } 
      if (this.factoryMethod == null)
        this.builder.reportError(new IllegalAnnotationException(Messages.NO_FACTORY_METHOD.format(new Object[] { nav().getClassName(nav().asDecl(object)), str }, ), this)); 
    } else if (!nav().isSameType(object, nav().ref(XmlType.DEFAULT.class))) {
      this.builder.reportError(new IllegalAnnotationException(Messages.FACTORY_CLASS_NEEDS_FACTORY_METHOD.format(new Object[] { nav().getClassName(nav().asDecl(object)) }, ), this));
    } 
    return (this.factoryMethod != null);
  }
  
  public Method getFactoryMethod() { return (Method)this.factoryMethod; }
  
  public String toString() { return "ClassInfo(" + this.clazz + ')'; }
  
  static  {
    Class[] arrayOfClass = { 
        XmlTransient.class, XmlAnyAttribute.class, XmlAttribute.class, XmlValue.class, XmlElement.class, XmlElements.class, XmlElementRef.class, XmlElementRefs.class, XmlAnyElement.class, XmlMixed.class, 
        OverrideAnnotationOf.class };
    HashMap hashMap = ANNOTATION_NUMBER_MAP;
    for (Class clazz1 : arrayOfClass)
      hashMap.put(clazz1, Integer.valueOf(hashMap.size())); 
    byte b = 20;
    for (SecondaryAnnotation secondaryAnnotation : SECONDARY_ANNOTATIONS) {
      for (Class clazz1 : secondaryAnnotation.members)
        hashMap.put(clazz1, Integer.valueOf(b)); 
      b++;
    } 
    DEFAULT_ORDER = new String[0];
  }
  
  private static final class ConflictException extends Exception {
    final List<Annotation> annotations;
    
    public ConflictException(List<Annotation> param1List) { this.annotations = param1List; }
  }
  
  private static final class DuplicateException extends Exception {
    final Annotation a1;
    
    final Annotation a2;
    
    public DuplicateException(Annotation param1Annotation1, Annotation param1Annotation2) throws DuplicateException {
      this.a1 = param1Annotation1;
      this.a2 = param1Annotation2;
    }
  }
  
  private enum PropertyGroup {
    TRANSIENT(new boolean[] { false, false, false, false, false, false }),
    ANY_ATTRIBUTE(new boolean[] { true, false, false, false, false, false }),
    ATTRIBUTE(new boolean[] { true, true, true, false, true, true }),
    VALUE(new boolean[] { true, true, true, false, true, true }),
    ELEMENT(new boolean[] { true, true, true, true, true, true }),
    ELEMENT_REF(new boolean[] { true, false, false, true, false, false }),
    MAP(new boolean[] { false, false, false, true, false, false });
    
    final int allowedsecondaryAnnotations;
    
    PropertyGroup(boolean... param1VarArgs1) {
      int i = 0;
      assert param1VarArgs1.length == SECONDARY_ANNOTATIONS.length;
      for (byte b = 0; b < param1VarArgs1.length; b++) {
        if (param1VarArgs1[b])
          i |= (SECONDARY_ANNOTATIONS[b]).bitMask; 
      } 
      this.allowedsecondaryAnnotations = i ^ 0xFFFFFFFF;
    }
    
    boolean allows(ClassInfoImpl.SecondaryAnnotation param1SecondaryAnnotation) { return ((this.allowedsecondaryAnnotations & param1SecondaryAnnotation.bitMask) == 0); }
  }
  
  private final class PropertySorter extends HashMap<String, Integer> implements Comparator<PropertyInfoImpl> {
    PropertyInfoImpl[] used = new PropertyInfoImpl[this.this$0.propOrder.length];
    
    private Set<String> collidedNames;
    
    PropertySorter(ClassInfoImpl this$0) {
      super(this$0.propOrder.length);
      for (String str : this$0.propOrder) {
        if (put(str, Integer.valueOf(size())) != null)
          this$0.builder.reportError(new IllegalAnnotationException(Messages.DUPLICATE_ENTRY_IN_PROP_ORDER.format(new Object[] { str }, ), this$0)); 
      } 
    }
    
    public int compare(PropertyInfoImpl param1PropertyInfoImpl1, PropertyInfoImpl param1PropertyInfoImpl2) {
      int i = checkedGet(param1PropertyInfoImpl1);
      int j = checkedGet(param1PropertyInfoImpl2);
      return i - j;
    }
    
    private int checkedGet(PropertyInfoImpl param1PropertyInfoImpl) {
      Integer integer = (Integer)get(param1PropertyInfoImpl.getName());
      if (integer == null) {
        if ((param1PropertyInfoImpl.kind()).isOrdered)
          this.this$0.builder.reportError(new IllegalAnnotationException(Messages.PROPERTY_MISSING_FROM_ORDER.format(new Object[] { param1PropertyInfoImpl.getName() }, ), param1PropertyInfoImpl)); 
        integer = Integer.valueOf(size());
        put(param1PropertyInfoImpl.getName(), integer);
      } 
      int i = integer.intValue();
      if (i < this.used.length) {
        if (this.used[i] != null && this.used[i] != param1PropertyInfoImpl) {
          if (this.collidedNames == null)
            this.collidedNames = new HashSet(); 
          if (this.collidedNames.add(param1PropertyInfoImpl.getName()))
            this.this$0.builder.reportError(new IllegalAnnotationException(Messages.DUPLICATE_PROPERTIES.format(new Object[] { param1PropertyInfoImpl.getName() }, ), param1PropertyInfoImpl, this.used[i])); 
        } 
        this.used[i] = param1PropertyInfoImpl;
      } 
      return integer.intValue();
    }
    
    public void checkUnusedProperties() {
      for (byte b = 0; b < this.used.length; b++) {
        if (this.used[b] == null) {
          String str1 = this.this$0.propOrder[b];
          String str2 = EditDistance.findNearest(str1, new AbstractList<String>() {
                public String get(int param2Int) { return ((PropertyInfoImpl)ClassInfoImpl.PropertySorter.this.this$0.properties.get(param2Int)).getName(); }
                
                public int size() { return ClassInfoImpl.PropertySorter.this.this$0.properties.size(); }
              });
          boolean bool = (b > this.this$0.properties.size() - 1) ? 0 : ((PropertyInfoImpl)this.this$0.properties.get(b)).hasAnnotation(OverrideAnnotationOf.class);
          if (!bool)
            this.this$0.builder.reportError(new IllegalAnnotationException(Messages.PROPERTY_ORDER_CONTAINS_UNUSED_ENTRY.format(new Object[] { str1, str2 }, ), this.this$0)); 
        } 
      } 
    }
  }
  
  private enum SecondaryAnnotation {
    JAVA_TYPE(1, new Class[] { javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.class }),
    ID_IDREF(2, new Class[] { javax.xml.bind.annotation.XmlID.class, javax.xml.bind.annotation.XmlIDREF.class }),
    BINARY(4, new Class[] { javax.xml.bind.annotation.XmlInlineBinaryData.class, javax.xml.bind.annotation.XmlMimeType.class, javax.xml.bind.annotation.XmlAttachmentRef.class }),
    ELEMENT_WRAPPER(8, new Class[] { javax.xml.bind.annotation.XmlElementWrapper.class }),
    LIST(16, new Class[] { javax.xml.bind.annotation.XmlList.class }),
    SCHEMA_TYPE(32, new Class[] { javax.xml.bind.annotation.XmlSchemaType.class });
    
    final int bitMask;
    
    final Class<? extends Annotation>[] members;
    
    SecondaryAnnotation(Class<? extends Annotation>[] param1ArrayOfClass1, Class... param1VarArgs1) {
      this.bitMask = param1ArrayOfClass1;
      this.members = param1VarArgs1;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\ClassInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */