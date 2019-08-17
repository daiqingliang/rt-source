package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Pool;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.BridgeContext;
import com.sun.xml.internal.bind.api.ErrorListener;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.RawAccessor;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import com.sun.xml.internal.bind.util.Which;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeInlineAnnotationReader;
import com.sun.xml.internal.bind.v2.model.core.Adapter;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.Ref;
import com.sun.xml.internal.bind.v2.model.impl.RuntimeBuiltinLeafInfoImpl;
import com.sun.xml.internal.bind.v2.model.impl.RuntimeModelBuilder;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeArrayInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeBuiltinLeafInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElementInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeEnumLeafInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
import com.sun.xml.internal.bind.v2.runtime.output.Encoded;
import com.sun.xml.internal.bind.v2.runtime.property.AttributeProperty;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallerImpl;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import com.sun.xml.internal.bind.v2.schemagen.XmlSchemaGenerator;
import com.sun.xml.internal.bind.v2.util.EditDistance;
import com.sun.xml.internal.bind.v2.util.QNameMap;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import com.sun.xml.internal.txw2.output.ResultFactory;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
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
import javax.xml.bind.Binder;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public final class JAXBContextImpl extends JAXBRIContext {
  private final Map<TypeReference, Bridge> bridges = new LinkedHashMap();
  
  private static DocumentBuilder db;
  
  private final QNameMap<JaxBeanInfo> rootMap = new QNameMap();
  
  private final HashMap<QName, JaxBeanInfo> typeMap = new HashMap();
  
  private final Map<Class, JaxBeanInfo> beanInfoMap = new LinkedHashMap();
  
  protected Map<RuntimeTypeInfo, JaxBeanInfo> beanInfos = new LinkedHashMap();
  
  private final Map<Class, Map<QName, ElementBeanInfoImpl>> elements = new LinkedHashMap();
  
  public final Pool<Marshaller> marshallerPool = new Pool.Impl<Marshaller>() {
      @NotNull
      protected Marshaller create() throws JAXBException { return JAXBContextImpl.this.createMarshaller(); }
    };
  
  public final Pool<Unmarshaller> unmarshallerPool = new Pool.Impl<Unmarshaller>() {
      @NotNull
      protected Unmarshaller create() throws JAXBException { return JAXBContextImpl.this.createUnmarshaller(); }
    };
  
  public NameBuilder nameBuilder = new NameBuilder();
  
  public final NameList nameList;
  
  private final String defaultNsUri;
  
  private final Class[] classes;
  
  protected final boolean c14nSupport;
  
  public final boolean xmlAccessorFactorySupport;
  
  public final boolean allNillable;
  
  public final boolean retainPropertyInfo;
  
  public final boolean supressAccessorWarnings;
  
  public final boolean improvedXsiTypeHandling;
  
  public final boolean disableSecurityProcessing;
  
  private WeakReference<RuntimeTypeInfoSet> typeInfoSetCache;
  
  @NotNull
  private RuntimeAnnotationReader annotationReader;
  
  private boolean hasSwaRef;
  
  @NotNull
  private final Map<Class, Class> subclassReplacements;
  
  public final boolean fastBoot;
  
  private Set<XmlNs> xmlNsSet = null;
  
  private Encoded[] utf8nameTable;
  
  private static final Comparator<QName> QNAME_COMPARATOR = new Comparator<QName>() {
      public int compare(QName param1QName1, QName param1QName2) {
        int i = param1QName1.getLocalPart().compareTo(param1QName2.getLocalPart());
        return (i != 0) ? i : param1QName1.getNamespaceURI().compareTo(param1QName2.getNamespaceURI());
      }
    };
  
  public Set<XmlNs> getXmlNsSet() { return this.xmlNsSet; }
  
  private JAXBContextImpl(JAXBContextBuilder paramJAXBContextBuilder) throws JAXBException {
    this.defaultNsUri = paramJAXBContextBuilder.defaultNsUri;
    this.retainPropertyInfo = paramJAXBContextBuilder.retainPropertyInfo;
    this.annotationReader = paramJAXBContextBuilder.annotationReader;
    this.subclassReplacements = paramJAXBContextBuilder.subclassReplacements;
    this.c14nSupport = paramJAXBContextBuilder.c14nSupport;
    this.classes = paramJAXBContextBuilder.classes;
    this.xmlAccessorFactorySupport = paramJAXBContextBuilder.xmlAccessorFactorySupport;
    this.allNillable = paramJAXBContextBuilder.allNillable;
    this.supressAccessorWarnings = paramJAXBContextBuilder.supressAccessorWarnings;
    this.improvedXsiTypeHandling = paramJAXBContextBuilder.improvedXsiTypeHandling;
    this.disableSecurityProcessing = paramJAXBContextBuilder.disableSecurityProcessing;
    Collection collection = paramJAXBContextBuilder.typeRefs;
    try {
      bool = Boolean.getBoolean(JAXBContextImpl.class.getName() + ".fastBoot");
    } catch (SecurityException securityException) {
      bool = false;
    } 
    this.fastBoot = bool;
    RuntimeTypeInfoSet runtimeTypeInfoSet = getTypeInfoSet();
    this.elements.put(null, new LinkedHashMap());
    for (RuntimeBuiltinLeafInfo runtimeBuiltinLeafInfo : RuntimeBuiltinLeafInfoImpl.builtinBeanInfos) {
      LeafBeanInfoImpl leafBeanInfoImpl = new LeafBeanInfoImpl(this, runtimeBuiltinLeafInfo);
      this.beanInfoMap.put(runtimeBuiltinLeafInfo.getClazz(), leafBeanInfoImpl);
      for (QName qName : leafBeanInfoImpl.getTypeNames())
        this.typeMap.put(qName, leafBeanInfoImpl); 
    } 
    for (RuntimeEnumLeafInfo runtimeEnumLeafInfo : runtimeTypeInfoSet.enums().values()) {
      JaxBeanInfo jaxBeanInfo = getOrCreate(runtimeEnumLeafInfo);
      for (QName qName : jaxBeanInfo.getTypeNames())
        this.typeMap.put(qName, jaxBeanInfo); 
      if (runtimeEnumLeafInfo.isElement())
        this.rootMap.put(runtimeEnumLeafInfo.getElementName(), jaxBeanInfo); 
    } 
    for (RuntimeArrayInfo runtimeArrayInfo : runtimeTypeInfoSet.arrays().values()) {
      JaxBeanInfo jaxBeanInfo = getOrCreate(runtimeArrayInfo);
      for (QName qName : jaxBeanInfo.getTypeNames())
        this.typeMap.put(qName, jaxBeanInfo); 
    } 
    for (Map.Entry entry : runtimeTypeInfoSet.beans().entrySet()) {
      ClassBeanInfoImpl classBeanInfoImpl = getOrCreate((RuntimeClassInfo)entry.getValue());
      XmlSchema xmlSchema = (XmlSchema)this.annotationReader.getPackageAnnotation(XmlSchema.class, entry.getKey(), null);
      if (xmlSchema != null && xmlSchema.xmlns() != null && xmlSchema.xmlns().length > 0) {
        if (this.xmlNsSet == null)
          this.xmlNsSet = new HashSet(); 
        this.xmlNsSet.addAll(Arrays.asList(xmlSchema.xmlns()));
      } 
      if (classBeanInfoImpl.isElement())
        this.rootMap.put(((RuntimeClassInfo)entry.getValue()).getElementName(), classBeanInfoImpl); 
      for (QName qName : classBeanInfoImpl.getTypeNames())
        this.typeMap.put(qName, classBeanInfoImpl); 
    } 
    for (RuntimeElementInfo runtimeElementInfo : runtimeTypeInfoSet.getAllElements()) {
      ElementBeanInfoImpl elementBeanInfoImpl = getOrCreate(runtimeElementInfo);
      if (runtimeElementInfo.getScope() == null)
        this.rootMap.put(runtimeElementInfo.getElementName(), elementBeanInfoImpl); 
      RuntimeClassInfo runtimeClassInfo = runtimeElementInfo.getScope();
      Class clazz = (runtimeClassInfo == null) ? null : (Class)runtimeClassInfo.getClazz();
      Map map = (Map)this.elements.get(clazz);
      if (map == null) {
        map = new LinkedHashMap();
        this.elements.put(clazz, map);
      } 
      map.put(runtimeElementInfo.getElementName(), elementBeanInfoImpl);
    } 
    this.beanInfoMap.put(javax.xml.bind.JAXBElement.class, new ElementBeanInfoImpl(this));
    this.beanInfoMap.put(com.sun.xml.internal.bind.api.CompositeStructure.class, new CompositeStructureBeanInfo(this));
    getOrCreate(runtimeTypeInfoSet.getAnyTypeInfo());
    for (JaxBeanInfo jaxBeanInfo : this.beanInfos.values())
      jaxBeanInfo.link(this); 
    for (Map.Entry entry : RuntimeUtil.primitiveToBox.entrySet())
      this.beanInfoMap.put(entry.getKey(), this.beanInfoMap.get(entry.getValue())); 
    Navigator navigator = runtimeTypeInfoSet.getNavigator();
    for (TypeReference typeReference : collection) {
      BridgeAdapter bridgeAdapter;
      XmlJavaTypeAdapter xmlJavaTypeAdapter = (XmlJavaTypeAdapter)typeReference.get(XmlJavaTypeAdapter.class);
      Adapter adapter = null;
      XmlList xmlList = (XmlList)typeReference.get(XmlList.class);
      Class clazz = (Class)navigator.erasure(typeReference.type);
      if (xmlJavaTypeAdapter != null)
        adapter = new Adapter(xmlJavaTypeAdapter.value(), navigator); 
      if (typeReference.get(javax.xml.bind.annotation.XmlAttachmentRef.class) != null) {
        adapter = new Adapter(SwaRefAdapter.class, navigator);
        this.hasSwaRef = true;
      } 
      if (adapter != null)
        clazz = (Class)navigator.erasure(adapter.defaultType); 
      Name name = this.nameBuilder.createElementName(typeReference.tagName);
      if (xmlList == null) {
        bridgeAdapter = new BridgeImpl(this, name, getBeanInfo(clazz, true), typeReference);
      } else {
        bridgeAdapter = new BridgeImpl(this, name, new ValueListBeanInfoImpl(this, clazz), typeReference);
      } 
      if (adapter != null)
        bridgeAdapter = new BridgeAdapter(bridgeAdapter, (Class)adapter.adapterType); 
      this.bridges.put(typeReference, bridgeAdapter);
    } 
    this.nameList = this.nameBuilder.conclude();
    for (JaxBeanInfo jaxBeanInfo : this.beanInfos.values())
      jaxBeanInfo.wrapUp(); 
    this.nameBuilder = null;
    this.beanInfos = null;
  }
  
  public boolean hasSwaRef() { return this.hasSwaRef; }
  
  public RuntimeTypeInfoSet getRuntimeTypeInfoSet() {
    try {
      return getTypeInfoSet();
    } catch (IllegalAnnotationsException illegalAnnotationsException) {
      throw new AssertionError(illegalAnnotationsException);
    } 
  }
  
  public RuntimeTypeInfoSet getTypeInfoSet() {
    if (this.typeInfoSetCache != null) {
      RuntimeTypeInfoSet runtimeTypeInfoSet1 = (RuntimeTypeInfoSet)this.typeInfoSetCache.get();
      if (runtimeTypeInfoSet1 != null)
        return runtimeTypeInfoSet1; 
    } 
    RuntimeModelBuilder runtimeModelBuilder = new RuntimeModelBuilder(this, this.annotationReader, this.subclassReplacements, this.defaultNsUri);
    IllegalAnnotationsException.Builder builder = new IllegalAnnotationsException.Builder();
    runtimeModelBuilder.setErrorHandler(builder);
    for (Class clazz : this.classes) {
      if (clazz != com.sun.xml.internal.bind.api.CompositeStructure.class)
        runtimeModelBuilder.getTypeInfo(new Ref(clazz)); 
    } 
    this.hasSwaRef |= runtimeModelBuilder.hasSwaRef;
    RuntimeTypeInfoSet runtimeTypeInfoSet = runtimeModelBuilder.link();
    builder.check();
    assert runtimeTypeInfoSet != null : "if no error was reported, the link must be a success";
    this.typeInfoSetCache = new WeakReference(runtimeTypeInfoSet);
    return runtimeTypeInfoSet;
  }
  
  public ElementBeanInfoImpl getElement(Class paramClass, QName paramQName) {
    Map map = (Map)this.elements.get(paramClass);
    if (map != null) {
      ElementBeanInfoImpl elementBeanInfoImpl = (ElementBeanInfoImpl)map.get(paramQName);
      if (elementBeanInfoImpl != null)
        return elementBeanInfoImpl; 
    } 
    map = (Map)this.elements.get(null);
    return (ElementBeanInfoImpl)map.get(paramQName);
  }
  
  private ElementBeanInfoImpl getOrCreate(RuntimeElementInfo paramRuntimeElementInfo) {
    JaxBeanInfo jaxBeanInfo = (JaxBeanInfo)this.beanInfos.get(paramRuntimeElementInfo);
    return (jaxBeanInfo != null) ? (ElementBeanInfoImpl)jaxBeanInfo : new ElementBeanInfoImpl(this, paramRuntimeElementInfo);
  }
  
  protected JaxBeanInfo getOrCreate(RuntimeEnumLeafInfo paramRuntimeEnumLeafInfo) {
    JaxBeanInfo jaxBeanInfo = (JaxBeanInfo)this.beanInfos.get(paramRuntimeEnumLeafInfo);
    if (jaxBeanInfo != null)
      return jaxBeanInfo; 
    jaxBeanInfo = new LeafBeanInfoImpl(this, paramRuntimeEnumLeafInfo);
    this.beanInfoMap.put(jaxBeanInfo.jaxbType, jaxBeanInfo);
    return jaxBeanInfo;
  }
  
  protected ClassBeanInfoImpl getOrCreate(RuntimeClassInfo paramRuntimeClassInfo) {
    ClassBeanInfoImpl classBeanInfoImpl = (ClassBeanInfoImpl)this.beanInfos.get(paramRuntimeClassInfo);
    if (classBeanInfoImpl != null)
      return classBeanInfoImpl; 
    classBeanInfoImpl = new ClassBeanInfoImpl(this, paramRuntimeClassInfo);
    this.beanInfoMap.put(classBeanInfoImpl.jaxbType, classBeanInfoImpl);
    return classBeanInfoImpl;
  }
  
  protected JaxBeanInfo getOrCreate(RuntimeArrayInfo paramRuntimeArrayInfo) {
    JaxBeanInfo jaxBeanInfo = (JaxBeanInfo)this.beanInfos.get(paramRuntimeArrayInfo);
    if (jaxBeanInfo != null)
      return jaxBeanInfo; 
    jaxBeanInfo = new ArrayBeanInfoImpl(this, paramRuntimeArrayInfo);
    this.beanInfoMap.put(paramRuntimeArrayInfo.getType(), jaxBeanInfo);
    return jaxBeanInfo;
  }
  
  public JaxBeanInfo getOrCreate(RuntimeTypeInfo paramRuntimeTypeInfo) {
    if (paramRuntimeTypeInfo instanceof RuntimeElementInfo)
      return getOrCreate((RuntimeElementInfo)paramRuntimeTypeInfo); 
    if (paramRuntimeTypeInfo instanceof RuntimeClassInfo)
      return getOrCreate((RuntimeClassInfo)paramRuntimeTypeInfo); 
    if (paramRuntimeTypeInfo instanceof com.sun.xml.internal.bind.v2.model.runtime.RuntimeLeafInfo) {
      JaxBeanInfo jaxBeanInfo = (JaxBeanInfo)this.beanInfos.get(paramRuntimeTypeInfo);
      assert jaxBeanInfo != null;
      return jaxBeanInfo;
    } 
    if (paramRuntimeTypeInfo instanceof RuntimeArrayInfo)
      return getOrCreate((RuntimeArrayInfo)paramRuntimeTypeInfo); 
    if (paramRuntimeTypeInfo.getType() == Object.class) {
      JaxBeanInfo jaxBeanInfo = (JaxBeanInfo)this.beanInfoMap.get(Object.class);
      if (jaxBeanInfo == null) {
        jaxBeanInfo = new AnyTypeBeanInfo(this, paramRuntimeTypeInfo);
        this.beanInfoMap.put(Object.class, jaxBeanInfo);
      } 
      return jaxBeanInfo;
    } 
    throw new IllegalArgumentException();
  }
  
  public final JaxBeanInfo getBeanInfo(Object paramObject) {
    for (Class clazz = paramObject.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
      JaxBeanInfo jaxBeanInfo = (JaxBeanInfo)this.beanInfoMap.get(clazz);
      if (jaxBeanInfo != null)
        return jaxBeanInfo; 
    } 
    if (paramObject instanceof org.w3c.dom.Element)
      return (JaxBeanInfo)this.beanInfoMap.get(Object.class); 
    for (Class clazz1 : paramObject.getClass().getInterfaces()) {
      JaxBeanInfo jaxBeanInfo = (JaxBeanInfo)this.beanInfoMap.get(clazz1);
      if (jaxBeanInfo != null)
        return jaxBeanInfo; 
    } 
    return null;
  }
  
  public final JaxBeanInfo getBeanInfo(Object paramObject, boolean paramBoolean) throws JAXBException {
    JaxBeanInfo jaxBeanInfo = getBeanInfo(paramObject);
    if (jaxBeanInfo != null)
      return jaxBeanInfo; 
    if (paramBoolean) {
      if (paramObject instanceof Document)
        throw new JAXBException(Messages.ELEMENT_NEEDED_BUT_FOUND_DOCUMENT.format(new Object[] { paramObject.getClass() })); 
      throw new JAXBException(Messages.UNKNOWN_CLASS.format(new Object[] { paramObject.getClass() }));
    } 
    return null;
  }
  
  public final <T> JaxBeanInfo<T> getBeanInfo(Class<T> paramClass) { return (JaxBeanInfo)this.beanInfoMap.get(paramClass); }
  
  public final <T> JaxBeanInfo<T> getBeanInfo(Class<T> paramClass, boolean paramBoolean) throws JAXBException {
    JaxBeanInfo jaxBeanInfo = getBeanInfo(paramClass);
    if (jaxBeanInfo != null)
      return jaxBeanInfo; 
    if (paramBoolean)
      throw new JAXBException(paramClass.getName() + " is not known to this context"); 
    return null;
  }
  
  public final Loader selectRootLoader(UnmarshallingContext.State paramState, TagName paramTagName) {
    JaxBeanInfo jaxBeanInfo = (JaxBeanInfo)this.rootMap.get(paramTagName.uri, paramTagName.local);
    return (jaxBeanInfo == null) ? null : jaxBeanInfo.getLoader(this, true);
  }
  
  public JaxBeanInfo getGlobalType(QName paramQName) { return (JaxBeanInfo)this.typeMap.get(paramQName); }
  
  public String getNearestTypeName(QName paramQName) {
    String[] arrayOfString = new String[this.typeMap.size()];
    byte b = 0;
    for (QName qName : this.typeMap.keySet()) {
      if (qName.getLocalPart().equals(paramQName.getLocalPart()))
        return qName.toString(); 
      arrayOfString[b++] = qName.toString();
    } 
    String str = EditDistance.findNearest(paramQName.toString(), arrayOfString);
    return (EditDistance.editDistance(str, paramQName.toString()) > 10) ? null : str;
  }
  
  public Set<QName> getValidRootNames() {
    TreeSet treeSet = new TreeSet(QNAME_COMPARATOR);
    for (QNameMap.Entry entry : this.rootMap.entrySet())
      treeSet.add(entry.createQName()); 
    return treeSet;
  }
  
  public Encoded[] getUTF8NameTable() {
    if (this.utf8nameTable == null) {
      Encoded[] arrayOfEncoded = new Encoded[this.nameList.localNames.length];
      for (byte b = 0; b < arrayOfEncoded.length; b++) {
        Encoded encoded = new Encoded(this.nameList.localNames[b]);
        encoded.compact();
        arrayOfEncoded[b] = encoded;
      } 
      this.utf8nameTable = arrayOfEncoded;
    } 
    return this.utf8nameTable;
  }
  
  public int getNumberOfLocalNames() { return this.nameList.localNames.length; }
  
  public int getNumberOfElementNames() { return this.nameList.numberOfElementNames; }
  
  public int getNumberOfAttributeNames() { return this.nameList.numberOfAttributeNames; }
  
  static Transformer createTransformer(boolean paramBoolean) {
    try {
      SAXTransformerFactory sAXTransformerFactory = (SAXTransformerFactory)XmlFactory.createTransformerFactory(paramBoolean);
      return sAXTransformerFactory.newTransformer();
    } catch (TransformerConfigurationException transformerConfigurationException) {
      throw new Error(transformerConfigurationException);
    } 
  }
  
  public static TransformerHandler createTransformerHandler(boolean paramBoolean) {
    try {
      SAXTransformerFactory sAXTransformerFactory = (SAXTransformerFactory)XmlFactory.createTransformerFactory(paramBoolean);
      return sAXTransformerFactory.newTransformerHandler();
    } catch (TransformerConfigurationException transformerConfigurationException) {
      throw new Error(transformerConfigurationException);
    } 
  }
  
  static Document createDom(boolean paramBoolean) {
    synchronized (JAXBContextImpl.class) {
      if (db == null)
        try {
          DocumentBuilderFactory documentBuilderFactory = XmlFactory.createDocumentBuilderFactory(paramBoolean);
          db = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException parserConfigurationException) {
          throw new FactoryConfigurationError(parserConfigurationException);
        }  
      return db.newDocument();
    } 
  }
  
  public MarshallerImpl createMarshaller() { return new MarshallerImpl(this, null); }
  
  public UnmarshallerImpl createUnmarshaller() { return new UnmarshallerImpl(this, null); }
  
  public Validator createValidator() { throw new UnsupportedOperationException(Messages.NOT_IMPLEMENTED_IN_2_0.format(new Object[0])); }
  
  public JAXBIntrospector createJAXBIntrospector() { return new JAXBIntrospector() {
        public boolean isElement(Object param1Object) { return (getElementName(param1Object) != null); }
        
        public QName getElementName(Object param1Object) throws JAXBException {
          try {
            return JAXBContextImpl.this.getElementName(param1Object);
          } catch (JAXBException jAXBException) {
            return null;
          } 
        }
      }; }
  
  private NonElement<Type, Class> getXmlType(RuntimeTypeInfoSet paramRuntimeTypeInfoSet, TypeReference paramTypeReference) {
    if (paramTypeReference == null)
      throw new IllegalArgumentException(); 
    XmlJavaTypeAdapter xmlJavaTypeAdapter = (XmlJavaTypeAdapter)paramTypeReference.get(XmlJavaTypeAdapter.class);
    XmlList xmlList = (XmlList)paramTypeReference.get(XmlList.class);
    Ref ref = new Ref(this.annotationReader, paramRuntimeTypeInfoSet.getNavigator(), paramTypeReference.type, xmlJavaTypeAdapter, xmlList);
    return paramRuntimeTypeInfoSet.getTypeInfo(ref);
  }
  
  public void generateEpisode(Result paramResult) {
    if (paramResult == null)
      throw new IllegalArgumentException(); 
    createSchemaGenerator().writeEpisodeFile(ResultFactory.createSerializer(paramResult));
  }
  
  public void generateSchema(SchemaOutputResolver paramSchemaOutputResolver) throws IOException {
    if (paramSchemaOutputResolver == null)
      throw new IOException(Messages.NULL_OUTPUT_RESOLVER.format(new Object[0])); 
    final SAXParseException[] e = new SAXParseException[1];
    final SAXParseException[] w = new SAXParseException[1];
    createSchemaGenerator().write(paramSchemaOutputResolver, new ErrorListener() {
          public void error(SAXParseException param1SAXParseException) { e[0] = param1SAXParseException; }
          
          public void fatalError(SAXParseException param1SAXParseException) { e[0] = param1SAXParseException; }
          
          public void warning(SAXParseException param1SAXParseException) { w[0] = param1SAXParseException; }
          
          public void info(SAXParseException param1SAXParseException) {}
        });
    if (arrayOfSAXParseException1[false] != null) {
      IOException iOException = new IOException(Messages.FAILED_TO_GENERATE_SCHEMA.format(new Object[0]));
      iOException.initCause(arrayOfSAXParseException1[0]);
      throw iOException;
    } 
    if (arrayOfSAXParseException2[false] != null) {
      IOException iOException = new IOException(Messages.ERROR_PROCESSING_SCHEMA.format(new Object[0]));
      iOException.initCause(arrayOfSAXParseException2[0]);
      throw iOException;
    } 
  }
  
  private XmlSchemaGenerator<Type, Class, Field, Method> createSchemaGenerator() {
    RuntimeTypeInfoSet runtimeTypeInfoSet;
    try {
      runtimeTypeInfoSet = getTypeInfoSet();
    } catch (IllegalAnnotationsException illegalAnnotationsException) {
      throw new AssertionError(illegalAnnotationsException);
    } 
    XmlSchemaGenerator xmlSchemaGenerator = new XmlSchemaGenerator(runtimeTypeInfoSet.getNavigator(), runtimeTypeInfoSet);
    HashSet hashSet = new HashSet();
    for (RuntimeElementInfo runtimeElementInfo : runtimeTypeInfoSet.getAllElements())
      hashSet.add(runtimeElementInfo.getElementName()); 
    for (RuntimeClassInfo runtimeClassInfo : runtimeTypeInfoSet.beans().values()) {
      if (runtimeClassInfo.isElement())
        hashSet.add(runtimeClassInfo.asElement().getElementName()); 
    } 
    for (TypeReference typeReference : this.bridges.keySet()) {
      if (hashSet.contains(typeReference.tagName))
        continue; 
      if (typeReference.type == void.class || typeReference.type == Void.class) {
        xmlSchemaGenerator.add(typeReference.tagName, false, null);
        continue;
      } 
      if (typeReference.type == com.sun.xml.internal.bind.api.CompositeStructure.class)
        continue; 
      NonElement nonElement = getXmlType(runtimeTypeInfoSet, typeReference);
      xmlSchemaGenerator.add(typeReference.tagName, !runtimeTypeInfoSet.getNavigator().isPrimitive(typeReference.type), nonElement);
    } 
    return xmlSchemaGenerator;
  }
  
  public QName getTypeName(TypeReference paramTypeReference) {
    try {
      NonElement nonElement = getXmlType(getTypeInfoSet(), paramTypeReference);
      if (nonElement == null)
        throw new IllegalArgumentException(); 
      return nonElement.getTypeName();
    } catch (IllegalAnnotationsException illegalAnnotationsException) {
      throw new AssertionError(illegalAnnotationsException);
    } 
  }
  
  public <T> Binder<T> createBinder(Class<T> paramClass) { return (paramClass == Node.class) ? createBinder() : super.createBinder(paramClass); }
  
  public Binder<Node> createBinder() { return new BinderImpl(this, new DOMScanner()); }
  
  public QName getElementName(Object paramObject) throws JAXBException {
    JaxBeanInfo jaxBeanInfo = getBeanInfo(paramObject, true);
    return !jaxBeanInfo.isElement() ? null : new QName(jaxBeanInfo.getElementNamespaceURI(paramObject), jaxBeanInfo.getElementLocalName(paramObject));
  }
  
  public QName getElementName(Class paramClass) throws JAXBException {
    JaxBeanInfo jaxBeanInfo = getBeanInfo(paramClass, true);
    return !jaxBeanInfo.isElement() ? null : new QName(jaxBeanInfo.getElementNamespaceURI(paramClass), jaxBeanInfo.getElementLocalName(paramClass));
  }
  
  public Bridge createBridge(TypeReference paramTypeReference) { return (Bridge)this.bridges.get(paramTypeReference); }
  
  @NotNull
  public BridgeContext createBridgeContext() { return new BridgeContextImpl(this); }
  
  public RawAccessor getElementPropertyAccessor(Class paramClass, String paramString1, String paramString2) throws JAXBException {
    JaxBeanInfo jaxBeanInfo = getBeanInfo(paramClass, true);
    if (!(jaxBeanInfo instanceof ClassBeanInfoImpl))
      throw new JAXBException(paramClass + " is not a bean"); 
    for (ClassBeanInfoImpl classBeanInfoImpl = (ClassBeanInfoImpl)jaxBeanInfo; classBeanInfoImpl != null; classBeanInfoImpl = classBeanInfoImpl.superClazz) {
      for (Property property : classBeanInfoImpl.properties) {
        final Accessor acc = property.getElementPropertyAccessor(paramString1, paramString2);
        if (accessor != null)
          return new RawAccessor() {
              public Object get(Object param1Object) throws AccessorException { return acc.getUnadapted(param1Object); }
              
              public void set(Object param1Object1, Object param1Object2) throws AccessorException { acc.setUnadapted(param1Object1, param1Object2); }
            }; 
      } 
    } 
    throw new JAXBException(new QName(paramString1, paramString2) + " is not a valid property on " + paramClass);
  }
  
  public List<String> getKnownNamespaceURIs() { return Arrays.asList(this.nameList.namespaceURIs); }
  
  public String getBuildId() {
    Package package = getClass().getPackage();
    return (package == null) ? null : package.getImplementationVersion();
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(Which.which(getClass()) + " Build-Id: " + getBuildId());
    stringBuilder.append("\nClasses known to this context:\n");
    TreeSet treeSet = new TreeSet();
    for (Class clazz : this.beanInfoMap.keySet())
      treeSet.add(clazz.getName()); 
    for (String str : treeSet)
      stringBuilder.append("  ").append(str).append('\n'); 
    return stringBuilder.toString();
  }
  
  public String getXMIMEContentType(Object paramObject) {
    JaxBeanInfo jaxBeanInfo = getBeanInfo(paramObject);
    if (!(jaxBeanInfo instanceof ClassBeanInfoImpl))
      return null; 
    ClassBeanInfoImpl classBeanInfoImpl = (ClassBeanInfoImpl)jaxBeanInfo;
    for (Property property : classBeanInfoImpl.properties) {
      if (property instanceof AttributeProperty) {
        AttributeProperty attributeProperty = (AttributeProperty)property;
        if (attributeProperty.attName.equals("http://www.w3.org/2005/05/xmlmime", "contentType"))
          try {
            return (String)attributeProperty.xacc.print(paramObject);
          } catch (AccessorException accessorException) {
            return null;
          } catch (SAXException sAXException) {
            return null;
          } catch (ClassCastException classCastException) {
            return null;
          }  
      } 
    } 
    return null;
  }
  
  public JAXBContextImpl createAugmented(Class<?> paramClass) throws JAXBException {
    Class[] arrayOfClass = new Class[this.classes.length + 1];
    System.arraycopy(this.classes, 0, arrayOfClass, 0, this.classes.length);
    arrayOfClass[this.classes.length] = paramClass;
    JAXBContextBuilder jAXBContextBuilder = new JAXBContextBuilder(this);
    jAXBContextBuilder.setClasses(arrayOfClass);
    return jAXBContextBuilder.build();
  }
  
  public static class JAXBContextBuilder {
    private boolean retainPropertyInfo = false;
    
    private boolean supressAccessorWarnings = false;
    
    private String defaultNsUri = "";
    
    @NotNull
    private RuntimeAnnotationReader annotationReader = new RuntimeInlineAnnotationReader();
    
    @NotNull
    private Map<Class, Class> subclassReplacements = Collections.emptyMap();
    
    private boolean c14nSupport = false;
    
    private Class[] classes;
    
    private Collection<TypeReference> typeRefs;
    
    private boolean xmlAccessorFactorySupport = false;
    
    private boolean allNillable;
    
    private boolean improvedXsiTypeHandling = true;
    
    private boolean disableSecurityProcessing = true;
    
    public JAXBContextBuilder() {}
    
    public JAXBContextBuilder(JAXBContextImpl param1JAXBContextImpl) {
      this.supressAccessorWarnings = param1JAXBContextImpl.supressAccessorWarnings;
      this.retainPropertyInfo = param1JAXBContextImpl.retainPropertyInfo;
      this.defaultNsUri = param1JAXBContextImpl.defaultNsUri;
      this.annotationReader = param1JAXBContextImpl.annotationReader;
      this.subclassReplacements = param1JAXBContextImpl.subclassReplacements;
      this.c14nSupport = param1JAXBContextImpl.c14nSupport;
      this.classes = param1JAXBContextImpl.classes;
      this.typeRefs = param1JAXBContextImpl.bridges.keySet();
      this.xmlAccessorFactorySupport = param1JAXBContextImpl.xmlAccessorFactorySupport;
      this.allNillable = param1JAXBContextImpl.allNillable;
      this.disableSecurityProcessing = param1JAXBContextImpl.disableSecurityProcessing;
    }
    
    public JAXBContextBuilder setRetainPropertyInfo(boolean param1Boolean) {
      this.retainPropertyInfo = param1Boolean;
      return this;
    }
    
    public JAXBContextBuilder setSupressAccessorWarnings(boolean param1Boolean) {
      this.supressAccessorWarnings = param1Boolean;
      return this;
    }
    
    public JAXBContextBuilder setC14NSupport(boolean param1Boolean) {
      this.c14nSupport = param1Boolean;
      return this;
    }
    
    public JAXBContextBuilder setXmlAccessorFactorySupport(boolean param1Boolean) {
      this.xmlAccessorFactorySupport = param1Boolean;
      return this;
    }
    
    public JAXBContextBuilder setDefaultNsUri(String param1String) {
      this.defaultNsUri = param1String;
      return this;
    }
    
    public JAXBContextBuilder setAllNillable(boolean param1Boolean) {
      this.allNillable = param1Boolean;
      return this;
    }
    
    public JAXBContextBuilder setClasses(Class[] param1ArrayOfClass) {
      this.classes = param1ArrayOfClass;
      return this;
    }
    
    public JAXBContextBuilder setAnnotationReader(RuntimeAnnotationReader param1RuntimeAnnotationReader) {
      this.annotationReader = param1RuntimeAnnotationReader;
      return this;
    }
    
    public JAXBContextBuilder setSubclassReplacements(Map<Class, Class> param1Map) {
      this.subclassReplacements = param1Map;
      return this;
    }
    
    public JAXBContextBuilder setTypeRefs(Collection<TypeReference> param1Collection) {
      this.typeRefs = param1Collection;
      return this;
    }
    
    public JAXBContextBuilder setImprovedXsiTypeHandling(boolean param1Boolean) {
      this.improvedXsiTypeHandling = param1Boolean;
      return this;
    }
    
    public JAXBContextBuilder setDisableSecurityProcessing(boolean param1Boolean) {
      this.disableSecurityProcessing = param1Boolean;
      return this;
    }
    
    public JAXBContextImpl build() throws JAXBException {
      if (this.defaultNsUri == null)
        this.defaultNsUri = ""; 
      if (this.subclassReplacements == null)
        this.subclassReplacements = Collections.emptyMap(); 
      if (this.annotationReader == null)
        this.annotationReader = new RuntimeInlineAnnotationReader(); 
      if (this.typeRefs == null)
        this.typeRefs = Collections.emptyList(); 
      return new JAXBContextImpl(this, null);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\JAXBContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */