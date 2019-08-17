package javax.xml.bind;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import org.w3c.dom.Node;

public abstract class JAXBContext {
  public static final String JAXB_CONTEXT_FACTORY = "javax.xml.bind.context.factory";
  
  public static JAXBContext newInstance(String paramString) throws JAXBException { return newInstance(paramString, getContextClassLoader()); }
  
  public static JAXBContext newInstance(String paramString, ClassLoader paramClassLoader) throws JAXBException { return newInstance(paramString, paramClassLoader, Collections.emptyMap()); }
  
  public static JAXBContext newInstance(String paramString, ClassLoader paramClassLoader, Map<String, ?> paramMap) throws JAXBException { return ContextFinder.find("javax.xml.bind.context.factory", paramString, paramClassLoader, paramMap); }
  
  public static JAXBContext newInstance(Class... paramVarArgs) throws JAXBException { return newInstance(paramVarArgs, Collections.emptyMap()); }
  
  public static JAXBContext newInstance(Class[] paramArrayOfClass, Map<String, ?> paramMap) throws JAXBException {
    if (paramArrayOfClass == null)
      throw new IllegalArgumentException(); 
    for (int i = paramArrayOfClass.length - 1; i >= 0; i--) {
      if (paramArrayOfClass[i] == null)
        throw new IllegalArgumentException(); 
    } 
    return ContextFinder.find(paramArrayOfClass, paramMap);
  }
  
  public abstract Unmarshaller createUnmarshaller() throws JAXBException;
  
  public abstract Marshaller createMarshaller() throws JAXBException;
  
  public abstract Validator createValidator() throws JAXBException;
  
  public <T> Binder<T> createBinder(Class<T> paramClass) { throw new UnsupportedOperationException(); }
  
  public Binder<Node> createBinder() { return createBinder(Node.class); }
  
  public JAXBIntrospector createJAXBIntrospector() { throw new UnsupportedOperationException(); }
  
  public void generateSchema(SchemaOutputResolver paramSchemaOutputResolver) throws IOException { throw new UnsupportedOperationException(); }
  
  private static ClassLoader getContextClassLoader() { return (System.getSecurityManager() == null) ? Thread.currentThread().getContextClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return Thread.currentThread().getContextClassLoader(); }
        }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\JAXBContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */