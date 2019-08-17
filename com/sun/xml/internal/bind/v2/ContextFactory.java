package com.sun.xml.internal.bind.v2;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.util.TypeCast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class ContextFactory {
  public static final String USE_JAXB_PROPERTIES = "_useJAXBProperties";
  
  public static JAXBContext createContext(Class[] paramArrayOfClass, Map<String, Object> paramMap) throws JAXBException {
    Map map;
    if (paramMap == null) {
      paramMap = Collections.emptyMap();
    } else {
      paramMap = new HashMap<String, Object>(paramMap);
    } 
    String str = (String)getPropertyValue(paramMap, "com.sun.xml.internal.bind.defaultNamespaceRemap", String.class);
    Boolean bool1 = (Boolean)getPropertyValue(paramMap, "com.sun.xml.internal.bind.c14n", Boolean.class);
    if (bool1 == null)
      bool1 = Boolean.valueOf(false); 
    Boolean bool2 = (Boolean)getPropertyValue(paramMap, "com.sun.xml.internal.bind.disableXmlSecurity", Boolean.class);
    if (bool2 == null)
      bool2 = Boolean.valueOf(false); 
    Boolean bool3 = (Boolean)getPropertyValue(paramMap, "com.sun.xml.internal.bind.treatEverythingNillable", Boolean.class);
    if (bool3 == null)
      bool3 = Boolean.valueOf(false); 
    Boolean bool4 = (Boolean)getPropertyValue(paramMap, "retainReferenceToInfo", Boolean.class);
    if (bool4 == null)
      bool4 = Boolean.valueOf(false); 
    Boolean bool5 = (Boolean)getPropertyValue(paramMap, "supressAccessorWarnings", Boolean.class);
    if (bool5 == null)
      bool5 = Boolean.valueOf(false); 
    Boolean bool6 = (Boolean)getPropertyValue(paramMap, "com.sun.xml.internal.bind.improvedXsiTypeHandling", Boolean.class);
    if (bool6 == null) {
      String str1 = Util.getSystemProperty("com.sun.xml.internal.bind.improvedXsiTypeHandling");
      if (str1 == null) {
        bool6 = Boolean.valueOf(true);
      } else {
        bool6 = Boolean.valueOf(str1);
      } 
    } 
    Boolean bool7 = (Boolean)getPropertyValue(paramMap, "com.sun.xml.internal.bind.XmlAccessorFactory", Boolean.class);
    if (bool7 == null) {
      bool7 = Boolean.valueOf(false);
      Util.getClassLogger().log(Level.FINE, "Property com.sun.xml.internal.bind.XmlAccessorFactoryis not active.  Using JAXB's implementation");
    } 
    RuntimeAnnotationReader runtimeAnnotationReader = (RuntimeAnnotationReader)getPropertyValue(paramMap, JAXBRIContext.ANNOTATION_READER, RuntimeAnnotationReader.class);
    Collection collection = (Collection)getPropertyValue(paramMap, "com.sun.xml.internal.bind.typeReferences", Collection.class);
    if (collection == null)
      collection = Collections.emptyList(); 
    try {
      map = TypeCast.checkedCast((Map)getPropertyValue(paramMap, "com.sun.xml.internal.bind.subclassReplacements", Map.class), Class.class, Class.class);
    } catch (ClassCastException classCastException) {
      throw new JAXBException(Messages.INVALID_TYPE_IN_MAP.format(new Object[0]), classCastException);
    } 
    if (!paramMap.isEmpty())
      throw new JAXBException(Messages.UNSUPPORTED_PROPERTY.format(new Object[] { paramMap.keySet().iterator().next() })); 
    JAXBContextImpl.JAXBContextBuilder jAXBContextBuilder = new JAXBContextImpl.JAXBContextBuilder();
    jAXBContextBuilder.setClasses(paramArrayOfClass);
    jAXBContextBuilder.setTypeRefs(collection);
    jAXBContextBuilder.setSubclassReplacements(map);
    jAXBContextBuilder.setDefaultNsUri(str);
    jAXBContextBuilder.setC14NSupport(bool1.booleanValue());
    jAXBContextBuilder.setAnnotationReader(runtimeAnnotationReader);
    jAXBContextBuilder.setXmlAccessorFactorySupport(bool7.booleanValue());
    jAXBContextBuilder.setAllNillable(bool3.booleanValue());
    jAXBContextBuilder.setRetainPropertyInfo(bool4.booleanValue());
    jAXBContextBuilder.setSupressAccessorWarnings(bool5.booleanValue());
    jAXBContextBuilder.setImprovedXsiTypeHandling(bool6.booleanValue());
    jAXBContextBuilder.setDisableSecurityProcessing(bool2.booleanValue());
    return jAXBContextBuilder.build();
  }
  
  private static <T> T getPropertyValue(Map<String, Object> paramMap, String paramString, Class<T> paramClass) throws JAXBException {
    Object object = paramMap.get(paramString);
    if (object == null)
      return null; 
    paramMap.remove(paramString);
    if (!paramClass.isInstance(object))
      throw new JAXBException(Messages.INVALID_PROPERTY_VALUE.format(new Object[] { paramString, object })); 
    return (T)paramClass.cast(object);
  }
  
  @Deprecated
  public static JAXBRIContext createContext(Class[] paramArrayOfClass, Collection<TypeReference> paramCollection, Map<Class, Class> paramMap, String paramString, boolean paramBoolean1, RuntimeAnnotationReader paramRuntimeAnnotationReader, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4) throws JAXBException { return createContext(paramArrayOfClass, paramCollection, paramMap, paramString, paramBoolean1, paramRuntimeAnnotationReader, paramBoolean2, paramBoolean3, paramBoolean4, false); }
  
  @Deprecated
  public static JAXBRIContext createContext(Class[] paramArrayOfClass, Collection<TypeReference> paramCollection, Map<Class, Class> paramMap, String paramString, boolean paramBoolean1, RuntimeAnnotationReader paramRuntimeAnnotationReader, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5) throws JAXBException {
    JAXBContextImpl.JAXBContextBuilder jAXBContextBuilder = new JAXBContextImpl.JAXBContextBuilder();
    jAXBContextBuilder.setClasses(paramArrayOfClass);
    jAXBContextBuilder.setTypeRefs(paramCollection);
    jAXBContextBuilder.setSubclassReplacements(paramMap);
    jAXBContextBuilder.setDefaultNsUri(paramString);
    jAXBContextBuilder.setC14NSupport(paramBoolean1);
    jAXBContextBuilder.setAnnotationReader(paramRuntimeAnnotationReader);
    jAXBContextBuilder.setXmlAccessorFactorySupport(paramBoolean2);
    jAXBContextBuilder.setAllNillable(paramBoolean3);
    jAXBContextBuilder.setRetainPropertyInfo(paramBoolean4);
    jAXBContextBuilder.setImprovedXsiTypeHandling(paramBoolean5);
    return jAXBContextBuilder.build();
  }
  
  public static JAXBContext createContext(String paramString, ClassLoader paramClassLoader, Map<String, Object> paramMap) throws JAXBException {
    FinalArrayList finalArrayList = new FinalArrayList();
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ":");
    while (stringTokenizer.hasMoreTokens()) {
      List list;
      boolean bool2 = false;
      boolean bool1 = bool2;
      String str = stringTokenizer.nextToken();
      try {
        Class clazz = paramClassLoader.loadClass(str + ".ObjectFactory");
        finalArrayList.add(clazz);
        bool1 = true;
      } catch (ClassNotFoundException classNotFoundException) {}
      try {
        list = loadIndexedClasses(str, paramClassLoader);
      } catch (IOException iOException) {
        throw new JAXBException(iOException);
      } 
      if (list != null) {
        finalArrayList.addAll(list);
        bool2 = true;
      } 
      if (!bool1 && !bool2)
        throw new JAXBException(Messages.BROKEN_CONTEXTPATH.format(new Object[] { str })); 
    } 
    return createContext((Class[])finalArrayList.toArray(new Class[finalArrayList.size()]), paramMap);
  }
  
  private static List<Class> loadIndexedClasses(String paramString, ClassLoader paramClassLoader) throws IOException, JAXBException {
    String str = paramString.replace('.', '/') + "/jaxb.index";
    InputStream inputStream = paramClassLoader.getResourceAsStream(str);
    if (inputStream == null)
      return null; 
    bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
    try {
      FinalArrayList finalArrayList = new FinalArrayList();
      for (String str1 = bufferedReader.readLine(); str1 != null; str1 = bufferedReader.readLine()) {
        str1 = str1.trim();
        if (str1.startsWith("#") || str1.length() == 0) {
          str1 = bufferedReader.readLine();
          continue;
        } 
        if (str1.endsWith(".class"))
          throw new JAXBException(Messages.ILLEGAL_ENTRY.format(new Object[] { str1 })); 
        try {
          finalArrayList.add(paramClassLoader.loadClass(paramString + '.' + str1));
        } catch (ClassNotFoundException classNotFoundException) {
          throw new JAXBException(Messages.ERROR_LOADING_CLASS.format(new Object[] { str1, str }, ), classNotFoundException);
        } 
      } 
      return finalArrayList;
    } finally {
      bufferedReader.close();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\ContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */