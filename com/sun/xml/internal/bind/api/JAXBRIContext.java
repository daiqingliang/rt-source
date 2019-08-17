package com.sun.xml.internal.bind.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.impl.NameConverter;
import com.sun.xml.internal.bind.v2.ContextFactory;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;

public abstract class JAXBRIContext extends JAXBContext {
  public static final String DEFAULT_NAMESPACE_REMAP = "com.sun.xml.internal.bind.defaultNamespaceRemap";
  
  public static final String TYPE_REFERENCES = "com.sun.xml.internal.bind.typeReferences";
  
  public static final String CANONICALIZATION_SUPPORT = "com.sun.xml.internal.bind.c14n";
  
  public static final String TREAT_EVERYTHING_NILLABLE = "com.sun.xml.internal.bind.treatEverythingNillable";
  
  public static final String ANNOTATION_READER = RuntimeAnnotationReader.class.getName();
  
  public static final String ENABLE_XOP = "com.sun.xml.internal.bind.XOP";
  
  public static final String SUBCLASS_REPLACEMENTS = "com.sun.xml.internal.bind.subclassReplacements";
  
  public static final String XMLACCESSORFACTORY_SUPPORT = "com.sun.xml.internal.bind.XmlAccessorFactory";
  
  public static final String RETAIN_REFERENCE_TO_INFO = "retainReferenceToInfo";
  
  public static final String SUPRESS_ACCESSOR_WARNINGS = "supressAccessorWarnings";
  
  public static final String IMPROVED_XSI_TYPE_HANDLING = "com.sun.xml.internal.bind.improvedXsiTypeHandling";
  
  public static final String DISABLE_XML_SECURITY = "com.sun.xml.internal.bind.disableXmlSecurity";
  
  public static JAXBRIContext newInstance(@NotNull Class[] paramArrayOfClass, @Nullable Collection<TypeReference> paramCollection, @Nullable Map<Class, Class> paramMap, @Nullable String paramString, boolean paramBoolean, @Nullable RuntimeAnnotationReader paramRuntimeAnnotationReader) throws JAXBException { return newInstance(paramArrayOfClass, paramCollection, paramMap, paramString, paramBoolean, paramRuntimeAnnotationReader, false, false, false, false); }
  
  public static JAXBRIContext newInstance(@NotNull Class[] paramArrayOfClass, @Nullable Collection<TypeReference> paramCollection, @Nullable Map<Class, Class> paramMap, @Nullable String paramString, boolean paramBoolean1, @Nullable RuntimeAnnotationReader paramRuntimeAnnotationReader, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5) throws JAXBException {
    HashMap hashMap = new HashMap();
    if (paramCollection != null)
      hashMap.put("com.sun.xml.internal.bind.typeReferences", paramCollection); 
    if (paramMap != null)
      hashMap.put("com.sun.xml.internal.bind.subclassReplacements", paramMap); 
    if (paramString != null)
      hashMap.put("com.sun.xml.internal.bind.defaultNamespaceRemap", paramString); 
    if (paramRuntimeAnnotationReader != null)
      hashMap.put(ANNOTATION_READER, paramRuntimeAnnotationReader); 
    hashMap.put("com.sun.xml.internal.bind.c14n", Boolean.valueOf(paramBoolean1));
    hashMap.put("com.sun.xml.internal.bind.XmlAccessorFactory", Boolean.valueOf(paramBoolean2));
    hashMap.put("com.sun.xml.internal.bind.treatEverythingNillable", Boolean.valueOf(paramBoolean3));
    hashMap.put("retainReferenceToInfo", Boolean.valueOf(paramBoolean4));
    hashMap.put("supressAccessorWarnings", Boolean.valueOf(paramBoolean5));
    return (JAXBRIContext)ContextFactory.createContext(paramArrayOfClass, hashMap);
  }
  
  public static JAXBRIContext newInstance(@NotNull Class[] paramArrayOfClass, @Nullable Collection<TypeReference> paramCollection, @Nullable String paramString, boolean paramBoolean) throws JAXBException { return newInstance(paramArrayOfClass, paramCollection, Collections.emptyMap(), paramString, paramBoolean, null); }
  
  public abstract boolean hasSwaRef();
  
  @Nullable
  public abstract QName getElementName(@NotNull Object paramObject) throws JAXBException;
  
  @Nullable
  public abstract QName getElementName(@NotNull Class paramClass) throws JAXBException;
  
  public abstract Bridge createBridge(@NotNull TypeReference paramTypeReference);
  
  @NotNull
  public abstract BridgeContext createBridgeContext();
  
  public abstract <B, V> RawAccessor<B, V> getElementPropertyAccessor(Class<B> paramClass, String paramString1, String paramString2) throws JAXBException;
  
  @NotNull
  public abstract List<String> getKnownNamespaceURIs();
  
  public abstract void generateSchema(@NotNull SchemaOutputResolver paramSchemaOutputResolver) throws IOException;
  
  public abstract QName getTypeName(@NotNull TypeReference paramTypeReference);
  
  @NotNull
  public abstract String getBuildId();
  
  public abstract void generateEpisode(Result paramResult);
  
  public abstract RuntimeTypeInfoSet getRuntimeTypeInfoSet();
  
  @NotNull
  public static String mangleNameToVariableName(@NotNull String paramString) { return NameConverter.standard.toVariableName(paramString); }
  
  @NotNull
  public static String mangleNameToClassName(@NotNull String paramString) { return NameConverter.standard.toClassName(paramString); }
  
  @NotNull
  public static String mangleNameToPropertyName(@NotNull String paramString) { return NameConverter.standard.toPropertyName(paramString); }
  
  @Nullable
  public static Type getBaseType(@NotNull Type paramType, @NotNull Class paramClass) { return (Type)Utils.REFLECTION_NAVIGATOR.getBaseClass(paramType, paramClass); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\api\JAXBRIContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */