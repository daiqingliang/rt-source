package javax.xml.stream;

import java.io.OutputStream;
import java.io.Writer;
import javax.xml.transform.Result;

public abstract class XMLOutputFactory {
  public static final String IS_REPAIRING_NAMESPACES = "javax.xml.stream.isRepairingNamespaces";
  
  static final String DEFAULIMPL = "com.sun.xml.internal.stream.XMLOutputFactoryImpl";
  
  public static XMLOutputFactory newInstance() throws FactoryConfigurationError { return (XMLOutputFactory)FactoryFinder.find(XMLOutputFactory.class, "com.sun.xml.internal.stream.XMLOutputFactoryImpl"); }
  
  public static XMLOutputFactory newFactory() throws FactoryConfigurationError { return (XMLOutputFactory)FactoryFinder.find(XMLOutputFactory.class, "com.sun.xml.internal.stream.XMLOutputFactoryImpl"); }
  
  public static XMLInputFactory newInstance(String paramString, ClassLoader paramClassLoader) throws FactoryConfigurationError { return (XMLInputFactory)FactoryFinder.find(XMLInputFactory.class, paramString, paramClassLoader, null); }
  
  public static XMLOutputFactory newFactory(String paramString, ClassLoader paramClassLoader) throws FactoryConfigurationError { return (XMLOutputFactory)FactoryFinder.find(XMLOutputFactory.class, paramString, paramClassLoader, null); }
  
  public abstract XMLStreamWriter createXMLStreamWriter(Writer paramWriter) throws XMLStreamException;
  
  public abstract XMLStreamWriter createXMLStreamWriter(OutputStream paramOutputStream) throws XMLStreamException;
  
  public abstract XMLStreamWriter createXMLStreamWriter(OutputStream paramOutputStream, String paramString) throws XMLStreamException;
  
  public abstract XMLStreamWriter createXMLStreamWriter(Result paramResult) throws XMLStreamException;
  
  public abstract XMLEventWriter createXMLEventWriter(Result paramResult) throws XMLStreamException;
  
  public abstract XMLEventWriter createXMLEventWriter(OutputStream paramOutputStream) throws XMLStreamException;
  
  public abstract XMLEventWriter createXMLEventWriter(OutputStream paramOutputStream, String paramString) throws XMLStreamException;
  
  public abstract XMLEventWriter createXMLEventWriter(Writer paramWriter) throws XMLStreamException;
  
  public abstract void setProperty(String paramString, Object paramObject) throws IllegalArgumentException;
  
  public abstract Object getProperty(String paramString) throws IllegalArgumentException;
  
  public abstract boolean isPropertySupported(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\stream\XMLOutputFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */