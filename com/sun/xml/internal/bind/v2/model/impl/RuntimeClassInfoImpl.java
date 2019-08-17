package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.AccessorFactory;
import com.sun.xml.internal.bind.AccessorFactoryImpl;
import com.sun.xml.internal.bind.InternalAccessorFactory;
import com.sun.xml.internal.bind.XmlAccessorFactory;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.ClassInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeValuePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Location;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class RuntimeClassInfoImpl extends ClassInfoImpl<Type, Class, Field, Method> implements RuntimeClassInfo, RuntimeElement {
  private Accessor<?, Locator> xmlLocationAccessor;
  
  private AccessorFactory accessorFactory;
  
  private boolean supressAccessorWarnings = false;
  
  private Accessor<?, Map<QName, String>> attributeWildcardAccessor;
  
  private boolean computedTransducer = false;
  
  private Transducer xducer = null;
  
  public RuntimeClassInfoImpl(RuntimeModelBuilder paramRuntimeModelBuilder, Locatable paramLocatable, Class paramClass) {
    super(paramRuntimeModelBuilder, paramLocatable, paramClass);
    this.accessorFactory = createAccessorFactory(paramClass);
  }
  
  protected AccessorFactory createAccessorFactory(Class paramClass) {
    AccessorFactory accessorFactory1 = null;
    JAXBContextImpl jAXBContextImpl = ((RuntimeModelBuilder)this.builder).context;
    if (jAXBContextImpl != null) {
      this.supressAccessorWarnings = jAXBContextImpl.supressAccessorWarnings;
      if (jAXBContextImpl.xmlAccessorFactorySupport) {
        XmlAccessorFactory xmlAccessorFactory = findXmlAccessorFactoryAnnotation(paramClass);
        if (xmlAccessorFactory != null)
          try {
            accessorFactory1 = (AccessorFactory)xmlAccessorFactory.value().newInstance();
          } catch (InstantiationException instantiationException) {
            this.builder.reportError(new IllegalAnnotationException(Messages.ACCESSORFACTORY_INSTANTIATION_EXCEPTION.format(new Object[] { xmlAccessorFactory.getClass().getName(), nav().getClassName(paramClass) }, ), this));
          } catch (IllegalAccessException illegalAccessException) {
            this.builder.reportError(new IllegalAnnotationException(Messages.ACCESSORFACTORY_ACCESS_EXCEPTION.format(new Object[] { xmlAccessorFactory.getClass().getName(), nav().getClassName(paramClass) }, ), this));
          }  
      } 
    } 
    if (accessorFactory1 == null)
      accessorFactory1 = AccessorFactoryImpl.getInstance(); 
    return accessorFactory1;
  }
  
  protected XmlAccessorFactory findXmlAccessorFactoryAnnotation(Class paramClass) {
    XmlAccessorFactory xmlAccessorFactory = (XmlAccessorFactory)reader().getClassAnnotation(XmlAccessorFactory.class, paramClass, this);
    if (xmlAccessorFactory == null)
      xmlAccessorFactory = (XmlAccessorFactory)reader().getPackageAnnotation(XmlAccessorFactory.class, paramClass, this); 
    return xmlAccessorFactory;
  }
  
  public Method getFactoryMethod() { return super.getFactoryMethod(); }
  
  public final RuntimeClassInfoImpl getBaseClass() { return (RuntimeClassInfoImpl)super.getBaseClass(); }
  
  protected ReferencePropertyInfoImpl createReferenceProperty(PropertySeed<Type, Class, Field, Method> paramPropertySeed) { return new RuntimeReferencePropertyInfoImpl(this, paramPropertySeed); }
  
  protected AttributePropertyInfoImpl createAttributeProperty(PropertySeed<Type, Class, Field, Method> paramPropertySeed) { return new RuntimeAttributePropertyInfoImpl(this, paramPropertySeed); }
  
  protected ValuePropertyInfoImpl createValueProperty(PropertySeed<Type, Class, Field, Method> paramPropertySeed) { return new RuntimeValuePropertyInfoImpl(this, paramPropertySeed); }
  
  protected ElementPropertyInfoImpl createElementProperty(PropertySeed<Type, Class, Field, Method> paramPropertySeed) { return new RuntimeElementPropertyInfoImpl(this, paramPropertySeed); }
  
  protected MapPropertyInfoImpl createMapProperty(PropertySeed<Type, Class, Field, Method> paramPropertySeed) { return new RuntimeMapPropertyInfoImpl(this, paramPropertySeed); }
  
  public List<? extends RuntimePropertyInfo> getProperties() { return super.getProperties(); }
  
  public RuntimePropertyInfo getProperty(String paramString) { return (RuntimePropertyInfo)super.getProperty(paramString); }
  
  public void link() {
    getTransducer();
    super.link();
  }
  
  public <B> Accessor<B, Map<QName, String>> getAttributeWildcard() {
    for (RuntimeClassInfoImpl runtimeClassInfoImpl = this; runtimeClassInfoImpl != null; runtimeClassInfoImpl = runtimeClassInfoImpl.getBaseClass()) {
      if (runtimeClassInfoImpl.attributeWildcard != null) {
        if (runtimeClassInfoImpl.attributeWildcardAccessor == null)
          runtimeClassInfoImpl.attributeWildcardAccessor = runtimeClassInfoImpl.createAttributeWildcardAccessor(); 
        return runtimeClassInfoImpl.attributeWildcardAccessor;
      } 
    } 
    return null;
  }
  
  public Transducer getTransducer() {
    if (!this.computedTransducer) {
      this.computedTransducer = true;
      this.xducer = calcTransducer();
    } 
    return this.xducer;
  }
  
  private Transducer calcTransducer() {
    RuntimeValuePropertyInfo runtimeValuePropertyInfo = null;
    if (hasAttributeWildcard())
      return null; 
    for (RuntimeClassInfoImpl runtimeClassInfoImpl = this; runtimeClassInfoImpl != null; runtimeClassInfoImpl = runtimeClassInfoImpl.getBaseClass()) {
      for (RuntimePropertyInfo runtimePropertyInfo : runtimeClassInfoImpl.getProperties()) {
        if (runtimePropertyInfo.kind() == PropertyKind.VALUE) {
          runtimeValuePropertyInfo = (RuntimeValuePropertyInfo)runtimePropertyInfo;
          continue;
        } 
        return null;
      } 
    } 
    return (runtimeValuePropertyInfo == null) ? null : (!runtimeValuePropertyInfo.getTarget().isSimpleType() ? null : new TransducerImpl((Class)getClazz(), TransducedAccessor.get(((RuntimeModelBuilder)this.builder).context, runtimeValuePropertyInfo)));
  }
  
  private Accessor<?, Map<QName, String>> createAttributeWildcardAccessor() {
    assert this.attributeWildcard != null;
    return ((RuntimePropertySeed)this.attributeWildcard).getAccessor();
  }
  
  protected RuntimePropertySeed createFieldSeed(Field paramField) {
    Accessor accessor;
    boolean bool = Modifier.isStatic(paramField.getModifiers());
    try {
      if (this.supressAccessorWarnings) {
        accessor = ((InternalAccessorFactory)this.accessorFactory).createFieldAccessor((Class)this.clazz, paramField, bool, this.supressAccessorWarnings);
      } else {
        accessor = this.accessorFactory.createFieldAccessor((Class)this.clazz, paramField, bool);
      } 
    } catch (JAXBException jAXBException) {
      this.builder.reportError(new IllegalAnnotationException(Messages.CUSTOM_ACCESSORFACTORY_FIELD_ERROR.format(new Object[] { nav().getClassName(this.clazz), jAXBException.toString() }, ), this));
      accessor = Accessor.getErrorInstance();
    } 
    return new RuntimePropertySeed(super.createFieldSeed(paramField), accessor);
  }
  
  public RuntimePropertySeed createAccessorSeed(Method paramMethod1, Method paramMethod2) {
    Accessor accessor;
    try {
      accessor = this.accessorFactory.createPropertyAccessor((Class)this.clazz, paramMethod1, paramMethod2);
    } catch (JAXBException jAXBException) {
      this.builder.reportError(new IllegalAnnotationException(Messages.CUSTOM_ACCESSORFACTORY_PROPERTY_ERROR.format(new Object[] { nav().getClassName(this.clazz), jAXBException.toString() }, ), this));
      accessor = Accessor.getErrorInstance();
    } 
    return new RuntimePropertySeed(super.createAccessorSeed(paramMethod1, paramMethod2), accessor);
  }
  
  protected void checkFieldXmlLocation(Field paramField) {
    if (reader().hasFieldAnnotation(com.sun.xml.internal.bind.annotation.XmlLocation.class, paramField))
      this.xmlLocationAccessor = new Accessor.FieldReflection(paramField); 
  }
  
  public Accessor<?, Locator> getLocatorField() { return this.xmlLocationAccessor; }
  
  static final class RuntimePropertySeed extends Object implements PropertySeed<Type, Class, Field, Method> {
    private final Accessor acc;
    
    private final PropertySeed<Type, Class, Field, Method> core;
    
    public RuntimePropertySeed(PropertySeed<Type, Class, Field, Method> param1PropertySeed, Accessor param1Accessor) {
      this.core = param1PropertySeed;
      this.acc = param1Accessor;
    }
    
    public String getName() { return this.core.getName(); }
    
    public <A extends Annotation> A readAnnotation(Class<A> param1Class) { return (A)this.core.readAnnotation(param1Class); }
    
    public boolean hasAnnotation(Class<? extends Annotation> param1Class) { return this.core.hasAnnotation(param1Class); }
    
    public Type getRawType() { return (Type)this.core.getRawType(); }
    
    public Location getLocation() { return this.core.getLocation(); }
    
    public Locatable getUpstream() { return this.core.getUpstream(); }
    
    public Accessor getAccessor() { return this.acc; }
  }
  
  private static final class TransducerImpl<BeanT> extends Object implements Transducer<BeanT> {
    private final TransducedAccessor<BeanT> xacc;
    
    private final Class<BeanT> ownerClass;
    
    public TransducerImpl(Class<BeanT> param1Class, TransducedAccessor<BeanT> param1TransducedAccessor) {
      this.xacc = param1TransducedAccessor;
      this.ownerClass = param1Class;
    }
    
    public boolean useNamespace() { return this.xacc.useNamespace(); }
    
    public boolean isDefault() { return false; }
    
    public void declareNamespace(BeanT param1BeanT, XMLSerializer param1XMLSerializer) throws AccessorException {
      try {
        this.xacc.declareNamespace(param1BeanT, param1XMLSerializer);
      } catch (SAXException sAXException) {
        throw new AccessorException(sAXException);
      } 
    }
    
    @NotNull
    public CharSequence print(BeanT param1BeanT) throws AccessorException {
      try {
        CharSequence charSequence = this.xacc.print(param1BeanT);
        if (charSequence == null)
          throw new AccessorException(Messages.THERE_MUST_BE_VALUE_IN_XMLVALUE.format(new Object[] { param1BeanT })); 
        return charSequence;
      } catch (SAXException sAXException) {
        throw new AccessorException(sAXException);
      } 
    }
    
    public BeanT parse(CharSequence param1CharSequence) throws AccessorException, SAXException {
      Object object;
      UnmarshallingContext unmarshallingContext = UnmarshallingContext.getInstance();
      if (unmarshallingContext != null) {
        object = unmarshallingContext.createInstance(this.ownerClass);
      } else {
        object = ClassFactory.create(this.ownerClass);
      } 
      this.xacc.parse(object, param1CharSequence);
      return (BeanT)object;
    }
    
    public void writeText(XMLSerializer param1XMLSerializer, BeanT param1BeanT, String param1String) throws IOException, SAXException, XMLStreamException, AccessorException {
      if (!this.xacc.hasValue(param1BeanT))
        throw new AccessorException(Messages.THERE_MUST_BE_VALUE_IN_XMLVALUE.format(new Object[] { param1BeanT })); 
      this.xacc.writeText(param1XMLSerializer, param1BeanT, param1String);
    }
    
    public void writeLeafElement(XMLSerializer param1XMLSerializer, Name param1Name, BeanT param1BeanT, String param1String) throws IOException, SAXException, XMLStreamException, AccessorException {
      if (!this.xacc.hasValue(param1BeanT))
        throw new AccessorException(Messages.THERE_MUST_BE_VALUE_IN_XMLVALUE.format(new Object[] { param1BeanT })); 
      this.xacc.writeLeafElement(param1XMLSerializer, param1Name, param1BeanT, param1String);
    }
    
    public QName getTypeName(BeanT param1BeanT) { return null; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\RuntimeClassInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */