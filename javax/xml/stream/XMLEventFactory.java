package javax.xml.stream;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;

public abstract class XMLEventFactory {
  static final String JAXPFACTORYID = "javax.xml.stream.XMLEventFactory";
  
  static final String DEFAULIMPL = "com.sun.xml.internal.stream.events.XMLEventFactoryImpl";
  
  public static XMLEventFactory newInstance() throws FactoryConfigurationError { return (XMLEventFactory)FactoryFinder.find(XMLEventFactory.class, "com.sun.xml.internal.stream.events.XMLEventFactoryImpl"); }
  
  public static XMLEventFactory newFactory() throws FactoryConfigurationError { return (XMLEventFactory)FactoryFinder.find(XMLEventFactory.class, "com.sun.xml.internal.stream.events.XMLEventFactoryImpl"); }
  
  public static XMLEventFactory newInstance(String paramString, ClassLoader paramClassLoader) throws FactoryConfigurationError { return (XMLEventFactory)FactoryFinder.find(XMLEventFactory.class, paramString, paramClassLoader, null); }
  
  public static XMLEventFactory newFactory(String paramString, ClassLoader paramClassLoader) throws FactoryConfigurationError { return (XMLEventFactory)FactoryFinder.find(XMLEventFactory.class, paramString, paramClassLoader, null); }
  
  public abstract void setLocation(Location paramLocation);
  
  public abstract Attribute createAttribute(String paramString1, String paramString2, String paramString3, String paramString4);
  
  public abstract Attribute createAttribute(String paramString1, String paramString2);
  
  public abstract Attribute createAttribute(QName paramQName, String paramString);
  
  public abstract Namespace createNamespace(String paramString);
  
  public abstract Namespace createNamespace(String paramString1, String paramString2);
  
  public abstract StartElement createStartElement(QName paramQName, Iterator paramIterator1, Iterator paramIterator2);
  
  public abstract StartElement createStartElement(String paramString1, String paramString2, String paramString3);
  
  public abstract StartElement createStartElement(String paramString1, String paramString2, String paramString3, Iterator paramIterator1, Iterator paramIterator2);
  
  public abstract StartElement createStartElement(String paramString1, String paramString2, String paramString3, Iterator paramIterator1, Iterator paramIterator2, NamespaceContext paramNamespaceContext);
  
  public abstract EndElement createEndElement(QName paramQName, Iterator paramIterator);
  
  public abstract EndElement createEndElement(String paramString1, String paramString2, String paramString3);
  
  public abstract EndElement createEndElement(String paramString1, String paramString2, String paramString3, Iterator paramIterator);
  
  public abstract Characters createCharacters(String paramString);
  
  public abstract Characters createCData(String paramString);
  
  public abstract Characters createSpace(String paramString);
  
  public abstract Characters createIgnorableSpace(String paramString);
  
  public abstract StartDocument createStartDocument();
  
  public abstract StartDocument createStartDocument(String paramString1, String paramString2, boolean paramBoolean);
  
  public abstract StartDocument createStartDocument(String paramString1, String paramString2);
  
  public abstract StartDocument createStartDocument(String paramString);
  
  public abstract EndDocument createEndDocument();
  
  public abstract EntityReference createEntityReference(String paramString, EntityDeclaration paramEntityDeclaration);
  
  public abstract Comment createComment(String paramString);
  
  public abstract ProcessingInstruction createProcessingInstruction(String paramString1, String paramString2);
  
  public abstract DTD createDTD(String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\stream\XMLEventFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */