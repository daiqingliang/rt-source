package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.property.AttributeProperty;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.property.PropertyFactory;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.StructureLoader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiTypeLoader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

public final class ClassBeanInfoImpl<BeanT> extends JaxBeanInfo<BeanT> implements AttributeAccessor<BeanT> {
  public final Property<BeanT>[] properties;
  
  private Property<? super BeanT> idProperty;
  
  private Loader loader;
  
  private Loader loaderWithTypeSubst;
  
  private RuntimeClassInfo ci;
  
  private final Accessor<? super BeanT, Map<QName, String>> inheritedAttWildcard;
  
  private final Transducer<BeanT> xducer;
  
  public final ClassBeanInfoImpl<? super BeanT> superClazz;
  
  private final Accessor<? super BeanT, Locator> xmlLocatorField;
  
  private final Name tagName;
  
  private boolean retainPropertyInfo = false;
  
  private AttributeProperty<BeanT>[] attributeProperties;
  
  private Property<BeanT>[] uriProperties;
  
  private final Method factoryMethod;
  
  private static final AttributeProperty[] EMPTY_PROPERTIES = new AttributeProperty[0];
  
  private static final Logger logger = Util.getClassLogger();
  
  ClassBeanInfoImpl(JAXBContextImpl paramJAXBContextImpl, RuntimeClassInfo paramRuntimeClassInfo) {
    super(paramJAXBContextImpl, paramRuntimeClassInfo, (Class)paramRuntimeClassInfo.getClazz(), paramRuntimeClassInfo.getTypeName(), paramRuntimeClassInfo.isElement(), false, true);
    this.ci = paramRuntimeClassInfo;
    this.inheritedAttWildcard = paramRuntimeClassInfo.getAttributeWildcard();
    this.xducer = paramRuntimeClassInfo.getTransducer();
    this.factoryMethod = paramRuntimeClassInfo.getFactoryMethod();
    this.retainPropertyInfo = paramJAXBContextImpl.retainPropertyInfo;
    if (this.factoryMethod != null) {
      int i = this.factoryMethod.getDeclaringClass().getModifiers();
      if (!Modifier.isPublic(i) || !Modifier.isPublic(this.factoryMethod.getModifiers()))
        try {
          this.factoryMethod.setAccessible(true);
        } catch (SecurityException securityException) {
          logger.log(Level.FINE, "Unable to make the method of " + this.factoryMethod + " accessible", securityException);
          throw securityException;
        }  
    } 
    if (paramRuntimeClassInfo.getBaseClass() == null) {
      this.superClazz = null;
    } else {
      this.superClazz = paramJAXBContextImpl.getOrCreate(paramRuntimeClassInfo.getBaseClass());
    } 
    if (this.superClazz != null && this.superClazz.xmlLocatorField != null) {
      this.xmlLocatorField = this.superClazz.xmlLocatorField;
    } else {
      this.xmlLocatorField = paramRuntimeClassInfo.getLocatorField();
    } 
    List list = paramRuntimeClassInfo.getProperties();
    this.properties = new Property[list.size()];
    byte b = 0;
    boolean bool = true;
    for (RuntimePropertyInfo runtimePropertyInfo : list) {
      Property property = PropertyFactory.create(paramJAXBContextImpl, runtimePropertyInfo);
      if (runtimePropertyInfo.id() == ID.ID)
        this.idProperty = property; 
      this.properties[b++] = property;
      bool &= runtimePropertyInfo.elementOnlyContent();
      checkOverrideProperties(property);
    } 
    hasElementOnlyContentModel(bool);
    if (paramRuntimeClassInfo.isElement()) {
      this.tagName = paramJAXBContextImpl.nameBuilder.createElementName(paramRuntimeClassInfo.getElementName());
    } else {
      this.tagName = null;
    } 
    setLifecycleFlags();
  }
  
  private void checkOverrideProperties(Property paramProperty) {
    ClassBeanInfoImpl classBeanInfoImpl = this;
    while ((classBeanInfoImpl = classBeanInfoImpl.superClazz) != null) {
      Property[] arrayOfProperty = classBeanInfoImpl.properties;
      if (arrayOfProperty == null)
        break; 
      for (Property property : arrayOfProperty) {
        if (property != null) {
          String str = property.getFieldName();
          if (str != null && str.equals(paramProperty.getFieldName()))
            property.setHiddenByOverride(true); 
        } 
      } 
    } 
  }
  
  protected void link(JAXBContextImpl paramJAXBContextImpl) {
    if (this.uriProperties != null)
      return; 
    super.link(paramJAXBContextImpl);
    if (this.superClazz != null)
      this.superClazz.link(paramJAXBContextImpl); 
    getLoader(paramJAXBContextImpl, true);
    if (this.superClazz != null) {
      if (this.idProperty == null)
        this.idProperty = this.superClazz.idProperty; 
      if (!this.superClazz.hasElementOnlyContentModel())
        hasElementOnlyContentModel(false); 
    } 
    FinalArrayList finalArrayList1 = new FinalArrayList();
    FinalArrayList finalArrayList2 = new FinalArrayList();
    for (ClassBeanInfoImpl classBeanInfoImpl = this; classBeanInfoImpl != null; classBeanInfoImpl = classBeanInfoImpl.superClazz) {
      for (byte b = 0; b < classBeanInfoImpl.properties.length; b++) {
        Property property = classBeanInfoImpl.properties[b];
        if (property instanceof AttributeProperty)
          finalArrayList1.add((AttributeProperty)property); 
        if (property.hasSerializeURIAction())
          finalArrayList2.add(property); 
      } 
    } 
    if (paramJAXBContextImpl.c14nSupport)
      Collections.sort(finalArrayList1); 
    if (finalArrayList1.isEmpty()) {
      this.attributeProperties = EMPTY_PROPERTIES;
    } else {
      this.attributeProperties = (AttributeProperty[])finalArrayList1.toArray(new AttributeProperty[finalArrayList1.size()]);
    } 
    if (finalArrayList2.isEmpty()) {
      this.uriProperties = EMPTY_PROPERTIES;
    } else {
      this.uriProperties = (Property[])finalArrayList2.toArray(new Property[finalArrayList2.size()]);
    } 
  }
  
  public void wrapUp() {
    for (Property property : this.properties)
      property.wrapUp(); 
    this.ci = null;
    super.wrapUp();
  }
  
  public String getElementNamespaceURI(BeanT paramBeanT) { return this.tagName.nsUri; }
  
  public String getElementLocalName(BeanT paramBeanT) { return this.tagName.localName; }
  
  public BeanT createInstance(UnmarshallingContext paramUnmarshallingContext) throws IllegalAccessException, InvocationTargetException, InstantiationException, SAXException {
    Object object = null;
    if (this.factoryMethod == null) {
      object = ClassFactory.create0(this.jaxbType);
    } else {
      Object object1 = ClassFactory.create(this.factoryMethod);
      if (this.jaxbType.isInstance(object1)) {
        object = object1;
      } else {
        throw new InstantiationException("The factory method didn't return a correct object");
      } 
    } 
    if (this.xmlLocatorField != null)
      try {
        this.xmlLocatorField.set(object, new LocatorImpl(paramUnmarshallingContext.getLocator()));
      } catch (AccessorException accessorException) {
        paramUnmarshallingContext.handleError(accessorException);
      }  
    return (BeanT)object;
  }
  
  public boolean reset(BeanT paramBeanT, UnmarshallingContext paramUnmarshallingContext) throws SAXException {
    try {
      if (this.superClazz != null)
        this.superClazz.reset(paramBeanT, paramUnmarshallingContext); 
      for (Property property : this.properties)
        property.reset(paramBeanT); 
      return true;
    } catch (AccessorException accessorException) {
      paramUnmarshallingContext.handleError(accessorException);
      return false;
    } 
  }
  
  public String getId(BeanT paramBeanT, XMLSerializer paramXMLSerializer) throws SAXException {
    if (this.idProperty != null)
      try {
        return this.idProperty.getIdValue(paramBeanT);
      } catch (AccessorException accessorException) {
        paramXMLSerializer.reportError(null, accessorException);
      }  
    return null;
  }
  
  public void serializeRoot(BeanT paramBeanT, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {
    if (this.tagName == null) {
      String str;
      Class clazz = paramBeanT.getClass();
      if (clazz.isAnnotationPresent(javax.xml.bind.annotation.XmlRootElement.class)) {
        str = Messages.UNABLE_TO_MARSHAL_UNBOUND_CLASS.format(new Object[] { clazz.getName() });
      } else {
        str = Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(new Object[] { clazz.getName() });
      } 
      paramXMLSerializer.reportError(new ValidationEventImpl(1, str, null, null));
    } else {
      paramXMLSerializer.startElement(this.tagName, paramBeanT);
      paramXMLSerializer.childAsSoleContent(paramBeanT, null);
      paramXMLSerializer.endElement();
      if (this.retainPropertyInfo)
        paramXMLSerializer.currentProperty.remove(); 
    } 
  }
  
  public void serializeBody(BeanT paramBeanT, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {
    if (this.superClazz != null)
      this.superClazz.serializeBody(paramBeanT, paramXMLSerializer); 
    try {
      for (Property property : this.properties) {
        if (this.retainPropertyInfo)
          paramXMLSerializer.currentProperty.set(property); 
        boolean bool = property.isHiddenByOverride();
        if (!bool || paramBeanT.getClass().equals(this.jaxbType)) {
          property.serializeBody(paramBeanT, paramXMLSerializer, null);
        } else if (bool) {
          Class clazz = paramBeanT.getClass();
          if (Utils.REFLECTION_NAVIGATOR.getDeclaredField(clazz, property.getFieldName()) == null)
            property.serializeBody(paramBeanT, paramXMLSerializer, null); 
        } 
      } 
    } catch (AccessorException accessorException) {
      paramXMLSerializer.reportError(null, accessorException);
    } 
  }
  
  public void serializeAttributes(BeanT paramBeanT, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {
    for (AttributeProperty attributeProperty : this.attributeProperties) {
      try {
        if (this.retainPropertyInfo) {
          Property property = paramXMLSerializer.getCurrentProperty();
          paramXMLSerializer.currentProperty.set(attributeProperty);
          attributeProperty.serializeAttributes(paramBeanT, paramXMLSerializer);
          paramXMLSerializer.currentProperty.set(property);
        } else {
          attributeProperty.serializeAttributes(paramBeanT, paramXMLSerializer);
        } 
        if (attributeProperty.attName.equals("http://www.w3.org/2001/XMLSchema-instance", "nil"))
          this.isNilIncluded = true; 
      } catch (AccessorException accessorException) {
        paramXMLSerializer.reportError(null, accessorException);
      } 
    } 
    try {
      if (this.inheritedAttWildcard != null) {
        Map map = (Map)this.inheritedAttWildcard.get(paramBeanT);
        paramXMLSerializer.attWildcardAsAttributes(map, null);
      } 
    } catch (AccessorException accessorException) {
      paramXMLSerializer.reportError(null, accessorException);
    } 
  }
  
  public void serializeURIs(BeanT paramBeanT, XMLSerializer paramXMLSerializer) throws SAXException, IOException, XMLStreamException {
    try {
      if (this.retainPropertyInfo) {
        Property property = paramXMLSerializer.getCurrentProperty();
        for (Property property1 : this.uriProperties) {
          paramXMLSerializer.currentProperty.set(property1);
          property1.serializeURIs(paramBeanT, paramXMLSerializer);
        } 
        paramXMLSerializer.currentProperty.set(property);
      } else {
        for (Property property : this.uriProperties)
          property.serializeURIs(paramBeanT, paramXMLSerializer); 
      } 
      if (this.inheritedAttWildcard != null) {
        Map map = (Map)this.inheritedAttWildcard.get(paramBeanT);
        paramXMLSerializer.attWildcardAsURIs(map, null);
      } 
    } catch (AccessorException accessorException) {
      paramXMLSerializer.reportError(null, accessorException);
    } 
  }
  
  public Loader getLoader(JAXBContextImpl paramJAXBContextImpl, boolean paramBoolean) {
    if (this.loader == null) {
      StructureLoader structureLoader = new StructureLoader(this);
      this.loader = structureLoader;
      if (this.ci.hasSubClasses()) {
        this.loaderWithTypeSubst = new XsiTypeLoader(this);
      } else {
        this.loaderWithTypeSubst = this.loader;
      } 
      structureLoader.init(paramJAXBContextImpl, this, this.ci.getAttributeWildcard());
    } 
    return paramBoolean ? this.loaderWithTypeSubst : this.loader;
  }
  
  public Transducer<BeanT> getTransducer() { return this.xducer; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\ClassBeanInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */