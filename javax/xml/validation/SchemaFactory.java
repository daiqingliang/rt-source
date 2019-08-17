package javax.xml.validation;

import java.io.File;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public abstract class SchemaFactory {
  private static SecuritySupport ss = new SecuritySupport();
  
  public static SchemaFactory newInstance(String paramString) {
    ClassLoader classLoader = ss.getContextClassLoader();
    if (classLoader == null)
      classLoader = SchemaFactory.class.getClassLoader(); 
    SchemaFactory schemaFactory = (new SchemaFactoryFinder(classLoader)).newFactory(paramString);
    if (schemaFactory == null)
      throw new IllegalArgumentException("No SchemaFactory that implements the schema language specified by: " + paramString + " could be loaded"); 
    return schemaFactory;
  }
  
  public static SchemaFactory newInstance(String paramString1, String paramString2, ClassLoader paramClassLoader) {
    ClassLoader classLoader = paramClassLoader;
    if (classLoader == null)
      classLoader = ss.getContextClassLoader(); 
    SchemaFactory schemaFactory = (new SchemaFactoryFinder(classLoader)).createInstance(paramString2);
    if (schemaFactory == null)
      throw new IllegalArgumentException("Factory " + paramString2 + " could not be loaded to implement the schema language specified by: " + paramString1); 
    if (schemaFactory.isSchemaLanguageSupported(paramString1))
      return schemaFactory; 
    throw new IllegalArgumentException("Factory " + schemaFactory.getClass().getName() + " does not implement the schema language specified by: " + paramString1);
  }
  
  public abstract boolean isSchemaLanguageSupported(String paramString);
  
  public boolean getFeature(String paramString) {
    if (paramString == null)
      throw new NullPointerException("the name parameter is null"); 
    throw new SAXNotRecognizedException(paramString);
  }
  
  public void setFeature(String paramString, boolean paramBoolean) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException("the name parameter is null"); 
    throw new SAXNotRecognizedException(paramString);
  }
  
  public void setProperty(String paramString, Object paramObject) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException("the name parameter is null"); 
    throw new SAXNotRecognizedException(paramString);
  }
  
  public Object getProperty(String paramString) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (paramString == null)
      throw new NullPointerException("the name parameter is null"); 
    throw new SAXNotRecognizedException(paramString);
  }
  
  public abstract void setErrorHandler(ErrorHandler paramErrorHandler);
  
  public abstract ErrorHandler getErrorHandler();
  
  public abstract void setResourceResolver(LSResourceResolver paramLSResourceResolver);
  
  public abstract LSResourceResolver getResourceResolver();
  
  public Schema newSchema(Source paramSource) throws SAXException { return newSchema(new Source[] { paramSource }); }
  
  public Schema newSchema(File paramFile) throws SAXException { return newSchema(new StreamSource(paramFile)); }
  
  public Schema newSchema(URL paramURL) throws SAXException { return newSchema(new StreamSource(paramURL.toExternalForm())); }
  
  public abstract Schema newSchema(Source[] paramArrayOfSource) throws SAXException;
  
  public abstract Schema newSchema() throws SAXException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\validation\SchemaFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */