package com.sun.xml.internal.ws.model;

import com.oracle.xmlns.internal.webservices.jaxws_databinding.ExistingAnnotationsType;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.JavaMethod;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.JavaParam;
import com.oracle.xmlns.internal.webservices.jaxws_databinding.JavaWsdlMappingType;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBResult;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ExternalMetadataReader extends ReflectAnnotationReader {
  private static final String NAMESPACE_WEBLOGIC_WSEE_DATABINDING = "http://xmlns.oracle.com/weblogic/weblogic-wsee-databinding";
  
  private static final String NAMESPACE_JAXWS_RI_EXTERNAL_METADATA = "http://xmlns.oracle.com/webservices/jaxws-databinding";
  
  private Map<String, JavaWsdlMappingType> readers = new HashMap();
  
  public ExternalMetadataReader(Collection<File> paramCollection1, Collection<String> paramCollection2, ClassLoader paramClassLoader, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramCollection1 != null)
      for (File file : paramCollection1) {
        try {
          String str = Util.documentRootNamespace(newSource(file), paramBoolean2);
          JavaWsdlMappingType javaWsdlMappingType = parseMetadata(paramBoolean1, newSource(file), str, paramBoolean2);
          this.readers.put(javaWsdlMappingType.getJavaTypeName(), javaWsdlMappingType);
        } catch (Exception exception) {
          throw new RuntimeModelerException("runtime.modeler.external.metadata.unable.to.read", new Object[] { file.getAbsolutePath() });
        } 
      }  
    if (paramCollection2 != null)
      for (String str : paramCollection2) {
        try {
          String str1 = Util.documentRootNamespace(newSource(str, paramClassLoader), paramBoolean2);
          JavaWsdlMappingType javaWsdlMappingType = parseMetadata(paramBoolean1, newSource(str, paramClassLoader), str1, paramBoolean2);
          this.readers.put(javaWsdlMappingType.getJavaTypeName(), javaWsdlMappingType);
        } catch (Exception exception) {
          throw new RuntimeModelerException("runtime.modeler.external.metadata.unable.to.read", new Object[] { str });
        } 
      }  
  }
  
  private StreamSource newSource(String paramString, ClassLoader paramClassLoader) {
    InputStream inputStream = paramClassLoader.getResourceAsStream(paramString);
    return new StreamSource(inputStream);
  }
  
  private JavaWsdlMappingType parseMetadata(boolean paramBoolean1, StreamSource paramStreamSource, String paramString, boolean paramBoolean2) throws JAXBException, IOException, TransformerException {
    if ("http://xmlns.oracle.com/weblogic/weblogic-wsee-databinding".equals(paramString))
      return Util.transformAndRead(paramStreamSource, paramBoolean2); 
    if ("http://xmlns.oracle.com/webservices/jaxws-databinding".equals(paramString))
      return Util.read(paramStreamSource, paramBoolean1, paramBoolean2); 
    throw new RuntimeModelerException("runtime.modeler.external.metadata.unsupported.schema", new Object[] { paramString, Arrays.asList(new String[] { "http://xmlns.oracle.com/weblogic/weblogic-wsee-databinding", "http://xmlns.oracle.com/webservices/jaxws-databinding" }).toString() });
  }
  
  private StreamSource newSource(File paramFile) {
    try {
      return new StreamSource(new FileInputStream(paramFile));
    } catch (FileNotFoundException fileNotFoundException) {
      throw new RuntimeModelerException("runtime.modeler.external.metadata.unable.to.read", new Object[] { paramFile.getAbsolutePath() });
    } 
  }
  
  public <A extends Annotation> A getAnnotation(Class<A> paramClass1, Class<?> paramClass2) {
    JavaWsdlMappingType javaWsdlMappingType = reader(paramClass2);
    return (A)((javaWsdlMappingType == null) ? super.getAnnotation(paramClass1, paramClass2) : (Annotation)Util.annotation(javaWsdlMappingType, paramClass1));
  }
  
  private JavaWsdlMappingType reader(Class<?> paramClass) { return (JavaWsdlMappingType)this.readers.get(paramClass.getName()); }
  
  Annotation[] getAnnotations(List<Object> paramList) {
    ArrayList arrayList = new ArrayList();
    for (Object object : paramList) {
      if (Annotation.class.isInstance(object))
        arrayList.add(Annotation.class.cast(object)); 
    } 
    return (Annotation[])arrayList.toArray(new Annotation[arrayList.size()]);
  }
  
  public Annotation[] getAnnotations(final Class<?> c) {
    Merger<Annotation[]> merger = new Merger<Annotation[]>(reader(paramClass)) {
        Annotation[] reflection() { return ExternalMetadataReader.this.getAnnotations(c); }
        
        Annotation[] external() { return ExternalMetadataReader.this.getAnnotations(this.reader.getClassAnnotation()); }
      };
    return (Annotation[])merger.merge();
  }
  
  public Annotation[] getAnnotations(final Method m) {
    Merger<Annotation[]> merger = new Merger<Annotation[]>(reader(paramMethod.getDeclaringClass())) {
        Annotation[] reflection() { return ExternalMetadataReader.this.getAnnotations(m); }
        
        Annotation[] external() {
          JavaMethod javaMethod = ExternalMetadataReader.this.getJavaMethod(m, this.reader);
          return (javaMethod == null) ? new Annotation[0] : ExternalMetadataReader.this.getAnnotations(javaMethod.getMethodAnnotation());
        }
      };
    return (Annotation[])merger.merge();
  }
  
  public <A extends Annotation> A getAnnotation(final Class<A> annType, final Method m) {
    Merger<Annotation> merger = new Merger<Annotation>(reader(paramMethod.getDeclaringClass())) {
        Annotation reflection() { return ExternalMetadataReader.this.getAnnotation(annType, m); }
        
        Annotation external() {
          JavaMethod javaMethod = ExternalMetadataReader.this.getJavaMethod(m, this.reader);
          return (Annotation)ExternalMetadataReader.Util.annotation(javaMethod, annType);
        }
      };
    return (A)(Annotation)merger.merge();
  }
  
  public Annotation[][] getParameterAnnotations(final Method m) {
    Merger<Annotation[][]> merger = new Merger<Annotation[][]>(reader(paramMethod.getDeclaringClass())) {
        Annotation[][] reflection() { return ExternalMetadataReader.this.getParameterAnnotations(m); }
        
        Annotation[][] external() {
          JavaMethod javaMethod = ExternalMetadataReader.this.getJavaMethod(m, this.reader);
          Annotation[][] arrayOfAnnotation = m.getParameterAnnotations();
          for (byte b = 0; b < m.getParameterTypes().length; b++) {
            if (javaMethod != null) {
              JavaParam javaParam = (JavaParam)javaMethod.getJavaParams().getJavaParam().get(b);
              arrayOfAnnotation[b] = ExternalMetadataReader.this.getAnnotations(javaParam.getParamAnnotation());
            } 
          } 
          return arrayOfAnnotation;
        }
      };
    return (Annotation[][])merger.merge();
  }
  
  public void getProperties(Map<String, Object> paramMap, Class<?> paramClass) {
    JavaWsdlMappingType javaWsdlMappingType = reader(paramClass);
    if (javaWsdlMappingType == null || ExistingAnnotationsType.MERGE.equals(javaWsdlMappingType.getExistingAnnotations()))
      super.getProperties(paramMap, paramClass); 
  }
  
  public void getProperties(Map<String, Object> paramMap, Method paramMethod) {
    JavaWsdlMappingType javaWsdlMappingType = reader(paramMethod.getDeclaringClass());
    if (javaWsdlMappingType == null || ExistingAnnotationsType.MERGE.equals(javaWsdlMappingType.getExistingAnnotations()))
      super.getProperties(paramMap, paramMethod); 
    if (javaWsdlMappingType != null) {
      JavaMethod javaMethod = getJavaMethod(paramMethod, javaWsdlMappingType);
      Element[] arrayOfElement = Util.annotation(javaMethod);
      paramMap.put("eclipselink-oxm-xml.xml-element", findXmlElement(arrayOfElement));
    } 
  }
  
  public void getProperties(Map<String, Object> paramMap, Method paramMethod, int paramInt) {
    JavaWsdlMappingType javaWsdlMappingType = reader(paramMethod.getDeclaringClass());
    if (javaWsdlMappingType == null || ExistingAnnotationsType.MERGE.equals(javaWsdlMappingType.getExistingAnnotations()))
      super.getProperties(paramMap, paramMethod, paramInt); 
    if (javaWsdlMappingType != null) {
      JavaMethod javaMethod = getJavaMethod(paramMethod, javaWsdlMappingType);
      if (javaMethod == null)
        return; 
      JavaParam javaParam = (JavaParam)javaMethod.getJavaParams().getJavaParam().get(paramInt);
      Element[] arrayOfElement = Util.annotation(javaParam);
      paramMap.put("eclipselink-oxm-xml.xml-element", findXmlElement(arrayOfElement));
    } 
  }
  
  JavaMethod getJavaMethod(Method paramMethod, JavaWsdlMappingType paramJavaWsdlMappingType) {
    JavaWsdlMappingType.JavaMethods javaMethods = paramJavaWsdlMappingType.getJavaMethods();
    if (javaMethods == null)
      return null; 
    ArrayList arrayList = new ArrayList();
    for (JavaMethod javaMethod : javaMethods.getJavaMethod()) {
      if (paramMethod.getName().equals(javaMethod.getName()))
        arrayList.add(javaMethod); 
    } 
    if (arrayList.isEmpty())
      return null; 
    if (arrayList.size() == 1)
      return (JavaMethod)arrayList.get(0); 
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    for (JavaMethod javaMethod : arrayList) {
      JavaMethod.JavaParams javaParams = javaMethod.getJavaParams();
      if (javaParams != null && javaParams.getJavaParam() != null && javaParams.getJavaParam().size() == arrayOfClass.length) {
        byte b1 = 0;
        for (byte b2 = 0; b2 < arrayOfClass.length; b2++) {
          JavaParam javaParam = (JavaParam)javaParams.getJavaParam().get(b2);
          if (arrayOfClass[b2].getName().equals(javaParam.getJavaType()))
            b1++; 
        } 
        if (b1 == arrayOfClass.length)
          return javaMethod; 
      } 
    } 
    return null;
  }
  
  Element findXmlElement(Element[] paramArrayOfElement) {
    if (paramArrayOfElement == null)
      return null; 
    for (Element element : paramArrayOfElement) {
      if (element.getLocalName().equals("java-type"))
        return element; 
      if (element.getLocalName().equals("xml-element"))
        return element; 
    } 
    return null;
  }
  
  static abstract class Merger<T> extends Object {
    JavaWsdlMappingType reader;
    
    Merger(JavaWsdlMappingType param1JavaWsdlMappingType) { this.reader = param1JavaWsdlMappingType; }
    
    abstract T reflection();
    
    abstract T external();
    
    T merge() {
      Object object1 = reflection();
      if (this.reader == null)
        return (T)object1; 
      Object object2 = external();
      return !ExistingAnnotationsType.MERGE.equals(this.reader.getExistingAnnotations()) ? (T)object2 : ((object1 instanceof Annotation) ? (T)doMerge((Annotation)object1, (Annotation)object2) : ((object1 instanceof Annotation[][]) ? (T)doMerge((Annotation[][])object1, (Annotation[][])object2) : (T)doMerge((Annotation[])object1, (Annotation[])object2)));
    }
    
    private Annotation doMerge(Annotation param1Annotation1, Annotation param1Annotation2) { return (param1Annotation2 != null) ? param1Annotation2 : param1Annotation1; }
    
    private Annotation[][] doMerge(Annotation[][] param1ArrayOfAnnotation1, Annotation[][] param1ArrayOfAnnotation2) {
      for (byte b = 0; b < param1ArrayOfAnnotation1.length; b++)
        param1ArrayOfAnnotation1[b] = doMerge(param1ArrayOfAnnotation1[b], (param1ArrayOfAnnotation2.length > b) ? param1ArrayOfAnnotation2[b] : null); 
      return param1ArrayOfAnnotation1;
    }
    
    private Annotation[] doMerge(Annotation[] param1ArrayOfAnnotation1, Annotation[] param1ArrayOfAnnotation2) {
      HashMap hashMap = new HashMap();
      if (param1ArrayOfAnnotation1 != null)
        for (Annotation annotation : param1ArrayOfAnnotation1)
          hashMap.put(annotation.annotationType().getName(), annotation);  
      if (param1ArrayOfAnnotation2 != null)
        for (Annotation annotation : param1ArrayOfAnnotation2)
          hashMap.put(annotation.annotationType().getName(), annotation);  
      Collection collection = hashMap.values();
      int i = collection.size();
      return (i == 0) ? null : (Annotation[])collection.toArray(new Annotation[i]);
    }
  }
  
  static class Util {
    private static final String DATABINDING_XSD = "jaxws-databinding.xsd";
    
    private static final String TRANSLATE_NAMESPACES_XSL = "jaxws-databinding-translate-namespaces.xml";
    
    static Schema schema;
    
    static JAXBContext jaxbContext;
    
    private static URL getResource() {
      ClassLoader classLoader;
      return (classLoader != null) ? classLoader.getResource("jaxws-databinding.xsd") : (classLoader = Util.class.getClassLoader()).getSystemResource("jaxws-databinding.xsd");
    }
    
    private static JAXBContext createJaxbContext(boolean param1Boolean) {
      Class[] arrayOfClass = { com.oracle.xmlns.internal.webservices.jaxws_databinding.ObjectFactory.class };
      try {
        if (param1Boolean) {
          HashMap hashMap = new HashMap();
          hashMap.put("com.sun.xml.internal.bind.disableXmlSecurity", Boolean.valueOf(param1Boolean));
          return JAXBContext.newInstance(arrayOfClass, hashMap);
        } 
        return JAXBContext.newInstance(arrayOfClass);
      } catch (JAXBException jAXBException) {
        jAXBException.printStackTrace();
        return null;
      } 
    }
    
    public static JavaWsdlMappingType read(Source param1Source, boolean param1Boolean1, boolean param1Boolean2) throws IOException, JAXBException {
      JAXBContext jAXBContext = jaxbContext(param1Boolean2);
      try {
        Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
        if (param1Boolean1) {
          if (schema == null);
          unmarshaller.setSchema(schema);
        } 
        Object object = unmarshaller.unmarshal(param1Source);
        return getJavaWsdlMapping(object);
      } catch (JAXBException jAXBException) {
        URL uRL = new URL(param1Source.getSystemId());
        StreamSource streamSource = new StreamSource(uRL.openStream());
        Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
        if (param1Boolean1) {
          if (schema == null);
          unmarshaller.setSchema(schema);
        } 
        Object object = unmarshaller.unmarshal(streamSource);
        return getJavaWsdlMapping(object);
      } 
    }
    
    private static JAXBContext jaxbContext(boolean param1Boolean) { return param1Boolean ? createJaxbContext(true) : jaxbContext; }
    
    public static JavaWsdlMappingType transformAndRead(Source param1Source, boolean param1Boolean) throws TransformerException, JAXBException {
      StreamSource streamSource = new StreamSource(Util.class.getResourceAsStream("jaxws-databinding-translate-namespaces.xml"));
      JAXBResult jAXBResult = new JAXBResult(jaxbContext(param1Boolean));
      TransformerFactory transformerFactory = XmlUtil.newTransformerFactory(!param1Boolean);
      Transformer transformer = transformerFactory.newTemplates(streamSource).newTransformer();
      transformer.transform(param1Source, jAXBResult);
      return getJavaWsdlMapping(jAXBResult.getResult());
    }
    
    static JavaWsdlMappingType getJavaWsdlMapping(Object param1Object) {
      Object object = (param1Object instanceof JAXBElement) ? ((JAXBElement)param1Object).getValue() : param1Object;
      return (object instanceof JavaWsdlMappingType) ? (JavaWsdlMappingType)object : null;
    }
    
    static <T> T findInstanceOf(Class<T> param1Class, List<Object> param1List) {
      for (Object object : param1List) {
        if (param1Class.isInstance(object))
          return (T)param1Class.cast(object); 
      } 
      return null;
    }
    
    public static <T> T annotation(JavaWsdlMappingType param1JavaWsdlMappingType, Class<T> param1Class) { return (param1JavaWsdlMappingType == null || param1JavaWsdlMappingType.getClassAnnotation() == null) ? null : (T)findInstanceOf(param1Class, param1JavaWsdlMappingType.getClassAnnotation()); }
    
    public static <T> T annotation(JavaMethod param1JavaMethod, Class<T> param1Class) { return (param1JavaMethod == null || param1JavaMethod.getMethodAnnotation() == null) ? null : (T)findInstanceOf(param1Class, param1JavaMethod.getMethodAnnotation()); }
    
    public static <T> T annotation(JavaParam param1JavaParam, Class<T> param1Class) { return (param1JavaParam == null || param1JavaParam.getParamAnnotation() == null) ? null : (T)findInstanceOf(param1Class, param1JavaParam.getParamAnnotation()); }
    
    public static Element[] annotation(JavaMethod param1JavaMethod) { return (param1JavaMethod == null || param1JavaMethod.getMethodAnnotation() == null) ? null : findElements(param1JavaMethod.getMethodAnnotation()); }
    
    public static Element[] annotation(JavaParam param1JavaParam) { return (param1JavaParam == null || param1JavaParam.getParamAnnotation() == null) ? null : findElements(param1JavaParam.getParamAnnotation()); }
    
    private static Element[] findElements(List<Object> param1List) {
      ArrayList arrayList = new ArrayList();
      for (Object object : param1List) {
        if (object instanceof Element)
          arrayList.add((Element)object); 
      } 
      return (Element[])arrayList.toArray(new Element[arrayList.size()]);
    }
    
    static String documentRootNamespace(Source param1Source, boolean param1Boolean) throws XMLStreamException {
      XMLInputFactory xMLInputFactory = XmlUtil.newXMLInputFactory(!param1Boolean);
      XMLStreamReader xMLStreamReader = xMLInputFactory.createXMLStreamReader(param1Source);
      XMLStreamReaderUtil.nextElementContent(xMLStreamReader);
      String str = xMLStreamReader.getName().getNamespaceURI();
      XMLStreamReaderUtil.close(xMLStreamReader);
      return str;
    }
    
    static  {
      SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
      try {
        URL uRL = getResource();
        if (uRL != null)
          schema = schemaFactory.newSchema(uRL); 
      } catch (SAXException sAXException) {}
      jaxbContext = createJaxbContext(false);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\ExternalMetadataReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */