package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.bind.util.Which;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.annotation.ClassLocatable;
import com.sun.xml.internal.bind.v2.model.annotation.Locatable;
import com.sun.xml.internal.bind.v2.model.core.ErrorHandler;
import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.model.core.PropertyInfo;
import com.sun.xml.internal.bind.v2.model.core.PropertyKind;
import com.sun.xml.internal.bind.v2.model.core.Ref;
import com.sun.xml.internal.bind.v2.model.core.RegistryInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.core.TypeInfoSet;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.namespace.QName;

public class ModelBuilder<T, C, F, M> extends Object implements ModelBuilderI<T, C, F, M> {
  private static final Logger logger;
  
  final TypeInfoSetImpl<T, C, F, M> typeInfoSet;
  
  public final AnnotationReader<T, C, F, M> reader;
  
  public final Navigator<T, C, F, M> nav;
  
  private final Map<QName, TypeInfo> typeNames = new HashMap();
  
  public final String defaultNsUri;
  
  final Map<String, RegistryInfoImpl<T, C, F, M>> registries = new HashMap();
  
  private final Map<C, C> subclassReplacements;
  
  private ErrorHandler errorHandler;
  
  private boolean hadError;
  
  public boolean hasSwaRef;
  
  private final ErrorHandler proxyErrorHandler = new ErrorHandler() {
      public void error(IllegalAnnotationException param1IllegalAnnotationException) { ModelBuilder.this.reportError(param1IllegalAnnotationException); }
    };
  
  private boolean linked;
  
  public ModelBuilder(AnnotationReader<T, C, F, M> paramAnnotationReader, Navigator<T, C, F, M> paramNavigator, Map<C, C> paramMap, String paramString) {
    this.reader = paramAnnotationReader;
    this.nav = paramNavigator;
    this.subclassReplacements = paramMap;
    if (paramString == null)
      paramString = ""; 
    this.defaultNsUri = paramString;
    paramAnnotationReader.setErrorHandler(this.proxyErrorHandler);
    this.typeInfoSet = createTypeInfoSet();
  }
  
  protected TypeInfoSetImpl<T, C, F, M> createTypeInfoSet() { return new TypeInfoSetImpl(this.nav, this.reader, BuiltinLeafInfoImpl.createLeaves(this.nav)); }
  
  public NonElement<T, C> getClassInfo(C paramC, Locatable paramLocatable) { return getClassInfo(paramC, false, paramLocatable); }
  
  public NonElement<T, C> getClassInfo(C paramC, boolean paramBoolean, Locatable paramLocatable) {
    assert paramC != null;
    NonElement nonElement = this.typeInfoSet.getClassInfo(paramC);
    if (nonElement != null)
      return nonElement; 
    if (this.nav.isEnum(paramC)) {
      EnumLeafInfoImpl enumLeafInfoImpl = createEnumLeafInfo(paramC, paramLocatable);
      this.typeInfoSet.add(enumLeafInfoImpl);
      nonElement = enumLeafInfoImpl;
      addTypeName(nonElement);
    } else {
      boolean bool = this.subclassReplacements.containsKey(paramC);
      if (bool && !paramBoolean) {
        nonElement = getClassInfo(this.subclassReplacements.get(paramC), paramLocatable);
      } else if (this.reader.hasClassAnnotation(paramC, javax.xml.bind.annotation.XmlTransient.class) || bool) {
        nonElement = getClassInfo(this.nav.getSuperClass(paramC), paramBoolean, new ClassLocatable(paramLocatable, paramC, this.nav));
      } else {
        ClassInfoImpl classInfoImpl = createClassInfo(paramC, paramLocatable);
        this.typeInfoSet.add(classInfoImpl);
        for (PropertyInfo propertyInfo : classInfoImpl.getProperties()) {
          if (propertyInfo.kind() == PropertyKind.REFERENCE) {
            addToRegistry(paramC, (Locatable)propertyInfo);
            Class[] arrayOfClass = getParametrizedTypes(propertyInfo);
            if (arrayOfClass != null)
              for (Class clazz : arrayOfClass) {
                if (clazz != paramC)
                  addToRegistry(clazz, (Locatable)propertyInfo); 
              }  
          } 
          for (TypeInfo typeInfo : propertyInfo.ref());
        } 
        classInfoImpl.getBaseClass();
        nonElement = classInfoImpl;
        addTypeName(nonElement);
      } 
    } 
    XmlSeeAlso xmlSeeAlso = (XmlSeeAlso)this.reader.getClassAnnotation(XmlSeeAlso.class, paramC, paramLocatable);
    if (xmlSeeAlso != null)
      for (Object object : this.reader.getClassArrayValue(xmlSeeAlso, "value"))
        getTypeInfo(object, (Locatable)xmlSeeAlso);  
    return nonElement;
  }
  
  private void addToRegistry(C paramC, Locatable paramLocatable) {
    String str = this.nav.getPackageName(paramC);
    if (!this.registries.containsKey(str)) {
      Object object = this.nav.loadObjectFactory(paramC, str);
      if (object != null)
        addRegistry(object, paramLocatable); 
    } 
  }
  
  private Class[] getParametrizedTypes(PropertyInfo paramPropertyInfo) {
    try {
      Type type = ((RuntimePropertyInfo)paramPropertyInfo).getIndividualType();
      if (type instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType)type;
        if (parameterizedType.getRawType() == javax.xml.bind.JAXBElement.class) {
          Type[] arrayOfType = parameterizedType.getActualTypeArguments();
          Class[] arrayOfClass = new Class[arrayOfType.length];
          for (byte b = 0; b < arrayOfType.length; b++)
            arrayOfClass[b] = (Class)arrayOfType[b]; 
          return arrayOfClass;
        } 
      } 
    } catch (Exception exception) {
      logger.log(Level.FINE, "Error in ModelBuilder.getParametrizedTypes. " + exception.getMessage());
    } 
    return null;
  }
  
  private void addTypeName(NonElement<T, C> paramNonElement) {
    QName qName = paramNonElement.getTypeName();
    if (qName == null)
      return; 
    TypeInfo typeInfo = (TypeInfo)this.typeNames.put(qName, paramNonElement);
    if (typeInfo != null)
      reportError(new IllegalAnnotationException(Messages.CONFLICTING_XML_TYPE_MAPPING.format(new Object[] { paramNonElement.getTypeName() }, ), typeInfo, paramNonElement)); 
  }
  
  public NonElement<T, C> getTypeInfo(T paramT, Locatable paramLocatable) {
    NonElement nonElement = this.typeInfoSet.getTypeInfo(paramT);
    if (nonElement != null)
      return nonElement; 
    if (this.nav.isArray(paramT)) {
      ArrayInfoImpl arrayInfoImpl = createArrayInfo(paramLocatable, paramT);
      addTypeName(arrayInfoImpl);
      this.typeInfoSet.add(arrayInfoImpl);
      return arrayInfoImpl;
    } 
    Object object = this.nav.asDecl(paramT);
    assert object != null : paramT.toString() + " must be a leaf, but we failed to recognize it.";
    return getClassInfo(object, paramLocatable);
  }
  
  public NonElement<T, C> getTypeInfo(Ref<T, C> paramRef) {
    assert !paramRef.valueList;
    Object object = this.nav.asDecl(paramRef.type);
    if (object != null && this.reader.getClassAnnotation(javax.xml.bind.annotation.XmlRegistry.class, object, null) != null) {
      if (!this.registries.containsKey(this.nav.getPackageName(object)))
        addRegistry(object, null); 
      return null;
    } 
    return getTypeInfo(paramRef.type, null);
  }
  
  protected EnumLeafInfoImpl<T, C, F, M> createEnumLeafInfo(C paramC, Locatable paramLocatable) { return new EnumLeafInfoImpl(this, paramLocatable, paramC, this.nav.use(paramC)); }
  
  protected ClassInfoImpl<T, C, F, M> createClassInfo(C paramC, Locatable paramLocatable) { return new ClassInfoImpl(this, paramLocatable, paramC); }
  
  protected ElementInfoImpl<T, C, F, M> createElementInfo(RegistryInfoImpl<T, C, F, M> paramRegistryInfoImpl, M paramM) throws IllegalAnnotationException { return new ElementInfoImpl(this, paramRegistryInfoImpl, paramM); }
  
  protected ArrayInfoImpl<T, C, F, M> createArrayInfo(Locatable paramLocatable, T paramT) { return new ArrayInfoImpl(this, paramLocatable, paramT); }
  
  public RegistryInfo<T, C> addRegistry(C paramC, Locatable paramLocatable) { return new RegistryInfoImpl(this, paramLocatable, paramC); }
  
  public RegistryInfo<T, C> getRegistry(String paramString) { return (RegistryInfo)this.registries.get(paramString); }
  
  public TypeInfoSet<T, C, F, M> link() {
    assert !this.linked;
    this.linked = true;
    for (ElementInfoImpl elementInfoImpl : this.typeInfoSet.getAllElements())
      elementInfoImpl.link(); 
    for (ClassInfoImpl classInfoImpl : this.typeInfoSet.beans().values())
      classInfoImpl.link(); 
    for (EnumLeafInfoImpl enumLeafInfoImpl : this.typeInfoSet.enums().values())
      enumLeafInfoImpl.link(); 
    return this.hadError ? null : this.typeInfoSet;
  }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler) { this.errorHandler = paramErrorHandler; }
  
  public final void reportError(IllegalAnnotationException paramIllegalAnnotationException) {
    this.hadError = true;
    if (this.errorHandler != null)
      this.errorHandler.error(paramIllegalAnnotationException); 
  }
  
  public boolean isReplaced(C paramC) { return this.subclassReplacements.containsKey(paramC); }
  
  public Navigator<T, C, F, M> getNavigator() { return this.nav; }
  
  public AnnotationReader<T, C, F, M> getReader() { return this.reader; }
  
  static  {
    try {
      Object object = null;
      object.location();
    } catch (NullPointerException nullPointerException) {
    
    } catch (NoSuchMethodError noSuchMethodError) {
      Messages messages;
      if (SecureLoader.getClassClassLoader(javax.xml.bind.annotation.XmlSchema.class) == null) {
        messages = Messages.INCOMPATIBLE_API_VERSION_MUSTANG;
      } else {
        messages = Messages.INCOMPATIBLE_API_VERSION;
      } 
      throw new LinkageError(messages.format(new Object[] { Which.which(javax.xml.bind.annotation.XmlSchema.class), Which.which(ModelBuilder.class) }));
    } 
    try {
      WhiteSpaceProcessor.isWhiteSpace("xyz");
    } catch (NoSuchMethodError noSuchMethodError) {
      throw new LinkageError(Messages.RUNNING_WITH_1_0_RUNTIME.format(new Object[] { Which.which(WhiteSpaceProcessor.class), Which.which(ModelBuilder.class) }));
    } 
    logger = Logger.getLogger(ModelBuilder.class.getName());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\ModelBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */