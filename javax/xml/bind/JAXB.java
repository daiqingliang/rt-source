package javax.xml.bind;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public final class JAXB {
  private static <T> JAXBContext getContext(Class<T> paramClass) throws JAXBException {
    WeakReference weakReference = cache;
    if (weakReference != null) {
      Cache cache2 = (Cache)weakReference.get();
      if (cache2 != null && cache2.type == paramClass)
        return cache2.context; 
    } 
    Cache cache1 = new Cache(paramClass);
    cache = new WeakReference(cache1);
    return cache1.context;
  }
  
  public static <T> T unmarshal(File paramFile, Class<T> paramClass) {
    try {
      JAXBElement jAXBElement = getContext(paramClass).createUnmarshaller().unmarshal(new StreamSource(paramFile), paramClass);
      return (T)jAXBElement.getValue();
    } catch (JAXBException jAXBException) {
      throw new DataBindingException(jAXBException);
    } 
  }
  
  public static <T> T unmarshal(URL paramURL, Class<T> paramClass) {
    try {
      JAXBElement jAXBElement = getContext(paramClass).createUnmarshaller().unmarshal(toSource(paramURL), paramClass);
      return (T)jAXBElement.getValue();
    } catch (JAXBException jAXBException) {
      throw new DataBindingException(jAXBException);
    } catch (IOException iOException) {
      throw new DataBindingException(iOException);
    } 
  }
  
  public static <T> T unmarshal(URI paramURI, Class<T> paramClass) {
    try {
      JAXBElement jAXBElement = getContext(paramClass).createUnmarshaller().unmarshal(toSource(paramURI), paramClass);
      return (T)jAXBElement.getValue();
    } catch (JAXBException jAXBException) {
      throw new DataBindingException(jAXBException);
    } catch (IOException iOException) {
      throw new DataBindingException(iOException);
    } 
  }
  
  public static <T> T unmarshal(String paramString, Class<T> paramClass) {
    try {
      JAXBElement jAXBElement = getContext(paramClass).createUnmarshaller().unmarshal(toSource(paramString), paramClass);
      return (T)jAXBElement.getValue();
    } catch (JAXBException jAXBException) {
      throw new DataBindingException(jAXBException);
    } catch (IOException iOException) {
      throw new DataBindingException(iOException);
    } 
  }
  
  public static <T> T unmarshal(InputStream paramInputStream, Class<T> paramClass) {
    try {
      JAXBElement jAXBElement = getContext(paramClass).createUnmarshaller().unmarshal(toSource(paramInputStream), paramClass);
      return (T)jAXBElement.getValue();
    } catch (JAXBException jAXBException) {
      throw new DataBindingException(jAXBException);
    } catch (IOException iOException) {
      throw new DataBindingException(iOException);
    } 
  }
  
  public static <T> T unmarshal(Reader paramReader, Class<T> paramClass) {
    try {
      JAXBElement jAXBElement = getContext(paramClass).createUnmarshaller().unmarshal(toSource(paramReader), paramClass);
      return (T)jAXBElement.getValue();
    } catch (JAXBException jAXBException) {
      throw new DataBindingException(jAXBException);
    } catch (IOException iOException) {
      throw new DataBindingException(iOException);
    } 
  }
  
  public static <T> T unmarshal(Source paramSource, Class<T> paramClass) {
    try {
      JAXBElement jAXBElement = getContext(paramClass).createUnmarshaller().unmarshal(toSource(paramSource), paramClass);
      return (T)jAXBElement.getValue();
    } catch (JAXBException jAXBException) {
      throw new DataBindingException(jAXBException);
    } catch (IOException iOException) {
      throw new DataBindingException(iOException);
    } 
  }
  
  private static Source toSource(Object paramObject) throws IOException {
    if (paramObject == null)
      throw new IllegalArgumentException("no XML is given"); 
    if (paramObject instanceof String)
      try {
        paramObject = new URI((String)paramObject);
      } catch (URISyntaxException uRISyntaxException) {
        paramObject = new File((String)paramObject);
      }  
    if (paramObject instanceof File) {
      File file = (File)paramObject;
      return new StreamSource(file);
    } 
    if (paramObject instanceof URI) {
      URI uRI = (URI)paramObject;
      paramObject = uRI.toURL();
    } 
    if (paramObject instanceof URL) {
      URL uRL = (URL)paramObject;
      return new StreamSource(uRL.toExternalForm());
    } 
    if (paramObject instanceof InputStream) {
      InputStream inputStream = (InputStream)paramObject;
      return new StreamSource(inputStream);
    } 
    if (paramObject instanceof Reader) {
      Reader reader = (Reader)paramObject;
      return new StreamSource(reader);
    } 
    if (paramObject instanceof Source)
      return (Source)paramObject; 
    throw new IllegalArgumentException("I don't understand how to handle " + paramObject.getClass());
  }
  
  public static void marshal(Object paramObject, File paramFile) { _marshal(paramObject, paramFile); }
  
  public static void marshal(Object paramObject, URL paramURL) { _marshal(paramObject, paramURL); }
  
  public static void marshal(Object paramObject, URI paramURI) { _marshal(paramObject, paramURI); }
  
  public static void marshal(Object paramObject, String paramString) { _marshal(paramObject, paramString); }
  
  public static void marshal(Object paramObject, OutputStream paramOutputStream) { _marshal(paramObject, paramOutputStream); }
  
  public static void marshal(Object paramObject, Writer paramWriter) { _marshal(paramObject, paramWriter); }
  
  public static void marshal(Object paramObject, Result paramResult) { _marshal(paramObject, paramResult); }
  
  private static void _marshal(Object paramObject1, Object paramObject2) {
    try {
      JAXBContext jAXBContext;
      if (paramObject1 instanceof JAXBElement) {
        jAXBContext = getContext(((JAXBElement)paramObject1).getDeclaredType());
      } else {
        Class clazz = paramObject1.getClass();
        XmlRootElement xmlRootElement = (XmlRootElement)clazz.getAnnotation(XmlRootElement.class);
        jAXBContext = getContext(clazz);
        if (xmlRootElement == null)
          paramObject1 = new JAXBElement(new QName(inferName(clazz)), clazz, paramObject1); 
      } 
      Marshaller marshaller = jAXBContext.createMarshaller();
      marshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(true));
      marshaller.marshal(paramObject1, toResult(paramObject2));
    } catch (JAXBException jAXBException) {
      throw new DataBindingException(jAXBException);
    } catch (IOException iOException) {
      throw new DataBindingException(iOException);
    } 
  }
  
  private static String inferName(Class paramClass) { return Introspector.decapitalize(paramClass.getSimpleName()); }
  
  private static Result toResult(Object paramObject) throws IOException {
    if (paramObject == null)
      throw new IllegalArgumentException("no XML is given"); 
    if (paramObject instanceof String)
      try {
        paramObject = new URI((String)paramObject);
      } catch (URISyntaxException uRISyntaxException) {
        paramObject = new File((String)paramObject);
      }  
    if (paramObject instanceof File) {
      File file = (File)paramObject;
      return new StreamResult(file);
    } 
    if (paramObject instanceof URI) {
      URI uRI = (URI)paramObject;
      paramObject = uRI.toURL();
    } 
    if (paramObject instanceof URL) {
      URL uRL = (URL)paramObject;
      URLConnection uRLConnection = uRL.openConnection();
      uRLConnection.setDoOutput(true);
      uRLConnection.setDoInput(false);
      uRLConnection.connect();
      return new StreamResult(uRLConnection.getOutputStream());
    } 
    if (paramObject instanceof OutputStream) {
      OutputStream outputStream = (OutputStream)paramObject;
      return new StreamResult(outputStream);
    } 
    if (paramObject instanceof Writer) {
      Writer writer = (Writer)paramObject;
      return new StreamResult(writer);
    } 
    if (paramObject instanceof Result)
      return (Result)paramObject; 
    throw new IllegalArgumentException("I don't understand how to handle " + paramObject.getClass());
  }
  
  private static final class Cache {
    final Class type;
    
    final JAXBContext context;
    
    public Cache(Class param1Class) throws JAXBException {
      this.type = param1Class;
      this.context = JAXBContext.newInstance(new Class[] { param1Class });
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\JAXB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */