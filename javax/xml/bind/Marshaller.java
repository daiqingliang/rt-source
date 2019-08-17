package javax.xml.bind;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public interface Marshaller {
  public static final String JAXB_ENCODING = "jaxb.encoding";
  
  public static final String JAXB_FORMATTED_OUTPUT = "jaxb.formatted.output";
  
  public static final String JAXB_SCHEMA_LOCATION = "jaxb.schemaLocation";
  
  public static final String JAXB_NO_NAMESPACE_SCHEMA_LOCATION = "jaxb.noNamespaceSchemaLocation";
  
  public static final String JAXB_FRAGMENT = "jaxb.fragment";
  
  void marshal(Object paramObject, Result paramResult) throws JAXBException;
  
  void marshal(Object paramObject, OutputStream paramOutputStream) throws JAXBException;
  
  void marshal(Object paramObject, File paramFile) throws JAXBException;
  
  void marshal(Object paramObject, Writer paramWriter) throws JAXBException;
  
  void marshal(Object paramObject, ContentHandler paramContentHandler) throws JAXBException;
  
  void marshal(Object paramObject, Node paramNode) throws JAXBException;
  
  void marshal(Object paramObject, XMLStreamWriter paramXMLStreamWriter) throws JAXBException;
  
  void marshal(Object paramObject, XMLEventWriter paramXMLEventWriter) throws JAXBException;
  
  Node getNode(Object paramObject) throws JAXBException;
  
  void setProperty(String paramString, Object paramObject) throws PropertyException;
  
  Object getProperty(String paramString) throws PropertyException;
  
  void setEventHandler(ValidationEventHandler paramValidationEventHandler) throws JAXBException;
  
  ValidationEventHandler getEventHandler() throws JAXBException;
  
  void setAdapter(XmlAdapter paramXmlAdapter);
  
  <A extends XmlAdapter> void setAdapter(Class<A> paramClass, A paramA);
  
  <A extends XmlAdapter> A getAdapter(Class<A> paramClass);
  
  void setAttachmentMarshaller(AttachmentMarshaller paramAttachmentMarshaller);
  
  AttachmentMarshaller getAttachmentMarshaller();
  
  void setSchema(Schema paramSchema);
  
  Schema getSchema();
  
  void setListener(Listener paramListener);
  
  Listener getListener();
  
  public static abstract class Listener {
    public void beforeMarshal(Object param1Object) {}
    
    public void afterMarshal(Object param1Object) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\Marshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */