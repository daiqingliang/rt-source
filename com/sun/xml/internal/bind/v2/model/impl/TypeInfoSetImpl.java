package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.core.BuiltinLeafInfo;
import com.sun.xml.internal.bind.v2.model.core.ElementInfo;
import com.sun.xml.internal.bind.v2.model.core.LeafInfo;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.Ref;
import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil;
import com.sun.xml.internal.bind.v2.util.FlattenIterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;

class TypeInfoSetImpl<T, C, F, M> extends Object implements TypeInfoSet<T, C, F, M> {
  @XmlTransient
  public final Navigator<T, C, F, M> nav;
  
  @XmlTransient
  public final AnnotationReader<T, C, F, M> reader;
  
  private final Map<T, BuiltinLeafInfo<T, C>> builtins = new LinkedHashMap();
  
  private final Map<C, EnumLeafInfoImpl<T, C, F, M>> enums = new LinkedHashMap();
  
  private final Map<T, ArrayInfoImpl<T, C, F, M>> arrays = new LinkedHashMap();
  
  @XmlJavaTypeAdapter(RuntimeUtil.ToStringAdapter.class)
  private final Map<C, ClassInfoImpl<T, C, F, M>> beans = new LinkedHashMap();
  
  @XmlTransient
  private final Map<C, ClassInfoImpl<T, C, F, M>> beansView = Collections.unmodifiableMap(this.beans);
  
  private final Map<C, Map<QName, ElementInfoImpl<T, C, F, M>>> elementMappings = new LinkedHashMap();
  
  private final Iterable<? extends ElementInfoImpl<T, C, F, M>> allElements = new Iterable<ElementInfoImpl<T, C, F, M>>() {
      public Iterator<ElementInfoImpl<T, C, F, M>> iterator() { return new FlattenIterator(TypeInfoSetImpl.this.elementMappings.values()); }
    };
  
  private final NonElement<T, C> anyType;
  
  private Map<String, Map<String, String>> xmlNsCache;
  
  public TypeInfoSetImpl(Navigator<T, C, F, M> paramNavigator, AnnotationReader<T, C, F, M> paramAnnotationReader, Map<T, ? extends BuiltinLeafInfoImpl<T, C>> paramMap) {
    this.nav = paramNavigator;
    this.reader = paramAnnotationReader;
    this.builtins.putAll(paramMap);
    this.anyType = createAnyType();
    for (Map.Entry entry : RuntimeUtil.primitiveToBox.entrySet())
      this.builtins.put(paramNavigator.getPrimitive((Class)entry.getKey()), paramMap.get(paramNavigator.ref((Class)entry.getValue()))); 
    this.elementMappings.put(null, new LinkedHashMap());
  }
  
  protected NonElement<T, C> createAnyType() { return new AnyTypeImpl(this.nav); }
  
  public Navigator<T, C, F, M> getNavigator() { return this.nav; }
  
  public void add(ClassInfoImpl<T, C, F, M> paramClassInfoImpl) { this.beans.put(paramClassInfoImpl.getClazz(), paramClassInfoImpl); }
  
  public void add(EnumLeafInfoImpl<T, C, F, M> paramEnumLeafInfoImpl) { this.enums.put(paramEnumLeafInfoImpl.clazz, paramEnumLeafInfoImpl); }
  
  public void add(ArrayInfoImpl<T, C, F, M> paramArrayInfoImpl) { this.arrays.put(paramArrayInfoImpl.getType(), paramArrayInfoImpl); }
  
  public NonElement<T, C> getTypeInfo(T paramT) {
    paramT = (T)this.nav.erasure(paramT);
    LeafInfo leafInfo = (LeafInfo)this.builtins.get(paramT);
    if (leafInfo != null)
      return leafInfo; 
    if (this.nav.isArray(paramT))
      return (NonElement)this.arrays.get(paramT); 
    Object object = this.nav.asDecl(paramT);
    return (object == null) ? null : getClassInfo(object);
  }
  
  public NonElement<T, C> getAnyTypeInfo() { return this.anyType; }
  
  public NonElement<T, C> getTypeInfo(Ref<T, C> paramRef) {
    assert !paramRef.valueList;
    Object object = this.nav.asDecl(paramRef.type);
    return (object != null && this.reader.getClassAnnotation(javax.xml.bind.annotation.XmlRegistry.class, object, null) != null) ? null : getTypeInfo(paramRef.type);
  }
  
  public Map<C, ? extends ClassInfoImpl<T, C, F, M>> beans() { return this.beansView; }
  
  public Map<T, ? extends BuiltinLeafInfo<T, C>> builtins() { return this.builtins; }
  
  public Map<C, ? extends EnumLeafInfoImpl<T, C, F, M>> enums() { return this.enums; }
  
  public Map<? extends T, ? extends ArrayInfoImpl<T, C, F, M>> arrays() { return this.arrays; }
  
  public NonElement<T, C> getClassInfo(C paramC) {
    LeafInfo leafInfo = (LeafInfo)this.builtins.get(this.nav.use(paramC));
    if (leafInfo != null)
      return leafInfo; 
    leafInfo = (LeafInfo)this.enums.get(paramC);
    return (leafInfo != null) ? leafInfo : (this.nav.asDecl(Object.class).equals(paramC) ? this.anyType : (NonElement)this.beans.get(paramC));
  }
  
  public ElementInfoImpl<T, C, F, M> getElementInfo(C paramC, QName paramQName) {
    while (paramC != null) {
      Map map = (Map)this.elementMappings.get(paramC);
      if (map != null) {
        ElementInfoImpl elementInfoImpl = (ElementInfoImpl)map.get(paramQName);
        if (elementInfoImpl != null)
          return elementInfoImpl; 
      } 
      paramC = (C)this.nav.getSuperClass(paramC);
    } 
    return (ElementInfoImpl)((Map)this.elementMappings.get(null)).get(paramQName);
  }
  
  public final void add(ElementInfoImpl<T, C, F, M> paramElementInfoImpl, ModelBuilder<T, C, F, M> paramModelBuilder) {
    Object object = null;
    if (paramElementInfoImpl.getScope() != null)
      object = paramElementInfoImpl.getScope().getClazz(); 
    Map map = (Map)this.elementMappings.get(object);
    if (map == null)
      this.elementMappings.put(object, map = new LinkedHashMap()); 
    ElementInfoImpl elementInfoImpl = (ElementInfoImpl)map.put(paramElementInfoImpl.getElementName(), paramElementInfoImpl);
    if (elementInfoImpl != null) {
      QName qName = paramElementInfoImpl.getElementName();
      paramModelBuilder.reportError(new IllegalAnnotationException(Messages.CONFLICTING_XML_ELEMENT_MAPPING.format(new Object[] { qName.getNamespaceURI(), qName.getLocalPart() }, ), paramElementInfoImpl, elementInfoImpl));
    } 
  }
  
  public Map<QName, ? extends ElementInfoImpl<T, C, F, M>> getElementMappings(C paramC) { return (Map)this.elementMappings.get(paramC); }
  
  public Iterable<? extends ElementInfoImpl<T, C, F, M>> getAllElements() { return this.allElements; }
  
  public Map<String, String> getXmlNs(String paramString) {
    if (this.xmlNsCache == null) {
      this.xmlNsCache = new HashMap();
      for (ClassInfoImpl classInfoImpl : beans().values()) {
        XmlSchema xmlSchema = (XmlSchema)this.reader.getPackageAnnotation(XmlSchema.class, classInfoImpl.getClazz(), null);
        if (xmlSchema == null)
          continue; 
        String str = xmlSchema.namespace();
        Map map1 = (Map)this.xmlNsCache.get(str);
        if (map1 == null)
          this.xmlNsCache.put(str, map1 = new HashMap()); 
        for (XmlNs xmlNs : xmlSchema.xmlns())
          map1.put(xmlNs.prefix(), xmlNs.namespaceURI()); 
      } 
    } 
    Map map = (Map)this.xmlNsCache.get(paramString);
    return (map != null) ? map : Collections.emptyMap();
  }
  
  public Map<String, String> getSchemaLocations() {
    HashMap hashMap = new HashMap();
    for (ClassInfoImpl classInfoImpl : beans().values()) {
      XmlSchema xmlSchema = (XmlSchema)this.reader.getPackageAnnotation(XmlSchema.class, classInfoImpl.getClazz(), null);
      if (xmlSchema == null)
        continue; 
      String str = xmlSchema.location();
      if (str.equals("##generate"))
        continue; 
      hashMap.put(xmlSchema.namespace(), str);
    } 
    return hashMap;
  }
  
  public final XmlNsForm getElementFormDefault(String paramString) {
    for (ClassInfoImpl classInfoImpl : beans().values()) {
      XmlSchema xmlSchema = (XmlSchema)this.reader.getPackageAnnotation(XmlSchema.class, classInfoImpl.getClazz(), null);
      if (xmlSchema == null || !xmlSchema.namespace().equals(paramString))
        continue; 
      XmlNsForm xmlNsForm = xmlSchema.elementFormDefault();
      if (xmlNsForm != XmlNsForm.UNSET)
        return xmlNsForm; 
    } 
    return XmlNsForm.UNSET;
  }
  
  public final XmlNsForm getAttributeFormDefault(String paramString) {
    for (ClassInfoImpl classInfoImpl : beans().values()) {
      XmlSchema xmlSchema = (XmlSchema)this.reader.getPackageAnnotation(XmlSchema.class, classInfoImpl.getClazz(), null);
      if (xmlSchema == null || !xmlSchema.namespace().equals(paramString))
        continue; 
      XmlNsForm xmlNsForm = xmlSchema.attributeFormDefault();
      if (xmlNsForm != XmlNsForm.UNSET)
        return xmlNsForm; 
    } 
    return XmlNsForm.UNSET;
  }
  
  public void dump(Result paramResult) throws JAXBException {
    JAXBContext jAXBContext = JAXBContext.newInstance(new Class[] { getClass() });
    Marshaller marshaller = jAXBContext.createMarshaller();
    marshaller.marshal(this, paramResult);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\TypeInfoSetImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */