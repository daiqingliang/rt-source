package javax.xml.bind.helpers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

public abstract class AbstractMarshallerImpl implements Marshaller {
  private ValidationEventHandler eventHandler = new DefaultValidationEventHandler();
  
  private String encoding = "UTF-8";
  
  private String schemaLocation = null;
  
  private String noNSSchemaLocation = null;
  
  private boolean formattedOutput = false;
  
  private boolean fragment = false;
  
  static String[] aliases = { 
      "UTF-8", "UTF8", "UTF-16", "Unicode", "UTF-16BE", "UnicodeBigUnmarked", "UTF-16LE", "UnicodeLittleUnmarked", "US-ASCII", "ASCII", 
      "TIS-620", "TIS620", "ISO-10646-UCS-2", "Unicode", "EBCDIC-CP-US", "cp037", "EBCDIC-CP-CA", "cp037", "EBCDIC-CP-NL", "cp037", 
      "EBCDIC-CP-WT", "cp037", "EBCDIC-CP-DK", "cp277", "EBCDIC-CP-NO", "cp277", "EBCDIC-CP-FI", "cp278", "EBCDIC-CP-SE", "cp278", 
      "EBCDIC-CP-IT", "cp280", "EBCDIC-CP-ES", "cp284", "EBCDIC-CP-GB", "cp285", "EBCDIC-CP-FR", "cp297", "EBCDIC-CP-AR1", "cp420", 
      "EBCDIC-CP-HE", "cp424", "EBCDIC-CP-BE", "cp500", "EBCDIC-CP-CH", "cp500", "EBCDIC-CP-ROECE", "cp870", "EBCDIC-CP-YU", "cp870", 
      "EBCDIC-CP-IS", "cp871", "EBCDIC-CP-AR2", "cp918" };
  
  public final void marshal(Object paramObject, OutputStream paramOutputStream) throws JAXBException {
    checkNotNull(paramObject, "obj", paramOutputStream, "os");
    marshal(paramObject, new StreamResult(paramOutputStream));
  }
  
  public void marshal(Object paramObject, File paramFile) throws JAXBException {
    checkNotNull(paramObject, "jaxbElement", paramFile, "output");
    try {
      bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(paramFile));
      try {
        marshal(paramObject, new StreamResult(bufferedOutputStream));
      } finally {
        bufferedOutputStream.close();
      } 
    } catch (IOException iOException) {
      throw new JAXBException(iOException);
    } 
  }
  
  public final void marshal(Object paramObject, Writer paramWriter) throws JAXBException {
    checkNotNull(paramObject, "obj", paramWriter, "writer");
    marshal(paramObject, new StreamResult(paramWriter));
  }
  
  public final void marshal(Object paramObject, ContentHandler paramContentHandler) throws JAXBException {
    checkNotNull(paramObject, "obj", paramContentHandler, "handler");
    marshal(paramObject, new SAXResult(paramContentHandler));
  }
  
  public final void marshal(Object paramObject, Node paramNode) throws JAXBException {
    checkNotNull(paramObject, "obj", paramNode, "node");
    marshal(paramObject, new DOMResult(paramNode));
  }
  
  public Node getNode(Object paramObject) throws JAXBException {
    checkNotNull(paramObject, "obj", Boolean.TRUE, "foo");
    throw new UnsupportedOperationException();
  }
  
  protected String getEncoding() { return this.encoding; }
  
  protected void setEncoding(String paramString) { this.encoding = paramString; }
  
  protected String getSchemaLocation() { return this.schemaLocation; }
  
  protected void setSchemaLocation(String paramString) { this.schemaLocation = paramString; }
  
  protected String getNoNSSchemaLocation() { return this.noNSSchemaLocation; }
  
  protected void setNoNSSchemaLocation(String paramString) { this.noNSSchemaLocation = paramString; }
  
  protected boolean isFormattedOutput() { return this.formattedOutput; }
  
  protected void setFormattedOutput(boolean paramBoolean) { this.formattedOutput = paramBoolean; }
  
  protected boolean isFragment() { return this.fragment; }
  
  protected void setFragment(boolean paramBoolean) { this.fragment = paramBoolean; }
  
  protected String getJavaEncoding(String paramString) throws UnsupportedEncodingException {
    try {
      "1".getBytes(paramString);
      return paramString;
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      for (boolean bool = false; bool < aliases.length; bool += true) {
        if (paramString.equals(aliases[bool])) {
          "1".getBytes(aliases[bool + true]);
          return aliases[bool + true];
        } 
      } 
      throw new UnsupportedEncodingException(paramString);
    } 
  }
  
  public void setProperty(String paramString, Object paramObject) throws PropertyException {
    if (paramString == null)
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "name")); 
    if ("jaxb.encoding".equals(paramString)) {
      checkString(paramString, paramObject);
      setEncoding((String)paramObject);
      return;
    } 
    if ("jaxb.formatted.output".equals(paramString)) {
      checkBoolean(paramString, paramObject);
      setFormattedOutput(((Boolean)paramObject).booleanValue());
      return;
    } 
    if ("jaxb.noNamespaceSchemaLocation".equals(paramString)) {
      checkString(paramString, paramObject);
      setNoNSSchemaLocation((String)paramObject);
      return;
    } 
    if ("jaxb.schemaLocation".equals(paramString)) {
      checkString(paramString, paramObject);
      setSchemaLocation((String)paramObject);
      return;
    } 
    if ("jaxb.fragment".equals(paramString)) {
      checkBoolean(paramString, paramObject);
      setFragment(((Boolean)paramObject).booleanValue());
      return;
    } 
    throw new PropertyException(paramString, paramObject);
  }
  
  public Object getProperty(String paramString) throws PropertyException {
    if (paramString == null)
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "name")); 
    if ("jaxb.encoding".equals(paramString))
      return getEncoding(); 
    if ("jaxb.formatted.output".equals(paramString))
      return isFormattedOutput() ? Boolean.TRUE : Boolean.FALSE; 
    if ("jaxb.noNamespaceSchemaLocation".equals(paramString))
      return getNoNSSchemaLocation(); 
    if ("jaxb.schemaLocation".equals(paramString))
      return getSchemaLocation(); 
    if ("jaxb.fragment".equals(paramString))
      return isFragment() ? Boolean.TRUE : Boolean.FALSE; 
    throw new PropertyException(paramString);
  }
  
  public ValidationEventHandler getEventHandler() throws JAXBException { return this.eventHandler; }
  
  public void setEventHandler(ValidationEventHandler paramValidationEventHandler) throws JAXBException {
    if (paramValidationEventHandler == null) {
      this.eventHandler = new DefaultValidationEventHandler();
    } else {
      this.eventHandler = paramValidationEventHandler;
    } 
  }
  
  private void checkBoolean(String paramString, Object paramObject) throws PropertyException {
    if (!(paramObject instanceof Boolean))
      throw new PropertyException(Messages.format("AbstractMarshallerImpl.MustBeBoolean", paramString)); 
  }
  
  private void checkString(String paramString, Object paramObject) throws PropertyException {
    if (!(paramObject instanceof String))
      throw new PropertyException(Messages.format("AbstractMarshallerImpl.MustBeString", paramString)); 
  }
  
  private void checkNotNull(Object paramObject1, String paramString1, Object paramObject2, String paramString2) {
    if (paramObject1 == null)
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", paramString1)); 
    if (paramObject2 == null)
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", paramString2)); 
  }
  
  public void marshal(Object paramObject, XMLEventWriter paramXMLEventWriter) throws JAXBException { throw new UnsupportedOperationException(); }
  
  public void marshal(Object paramObject, XMLStreamWriter paramXMLStreamWriter) throws JAXBException { throw new UnsupportedOperationException(); }
  
  public void setSchema(Schema paramSchema) { throw new UnsupportedOperationException(); }
  
  public Schema getSchema() { throw new UnsupportedOperationException(); }
  
  public void setAdapter(XmlAdapter paramXmlAdapter) {
    if (paramXmlAdapter == null)
      throw new IllegalArgumentException(); 
    setAdapter(paramXmlAdapter.getClass(), paramXmlAdapter);
  }
  
  public <A extends XmlAdapter> void setAdapter(Class<A> paramClass, A paramA) { throw new UnsupportedOperationException(); }
  
  public <A extends XmlAdapter> A getAdapter(Class<A> paramClass) { throw new UnsupportedOperationException(); }
  
  public void setAttachmentMarshaller(AttachmentMarshaller paramAttachmentMarshaller) { throw new UnsupportedOperationException(); }
  
  public AttachmentMarshaller getAttachmentMarshaller() { throw new UnsupportedOperationException(); }
  
  public void setListener(Marshaller.Listener paramListener) { throw new UnsupportedOperationException(); }
  
  public Marshaller.Listener getListener() { throw new UnsupportedOperationException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\helpers\AbstractMarshallerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */