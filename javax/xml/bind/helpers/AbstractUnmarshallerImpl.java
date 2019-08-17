package javax.xml.bind.helpers;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public abstract class AbstractUnmarshallerImpl implements Unmarshaller {
  private ValidationEventHandler eventHandler = new DefaultValidationEventHandler();
  
  protected boolean validating = false;
  
  private XMLReader reader = null;
  
  protected XMLReader getXMLReader() throws JAXBException {
    if (this.reader == null)
      try {
        SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
        sAXParserFactory.setNamespaceAware(true);
        sAXParserFactory.setValidating(false);
        this.reader = sAXParserFactory.newSAXParser().getXMLReader();
      } catch (ParserConfigurationException parserConfigurationException) {
        throw new JAXBException(parserConfigurationException);
      } catch (SAXException sAXException) {
        throw new JAXBException(sAXException);
      }  
    return this.reader;
  }
  
  public Object unmarshal(Source paramSource) throws JAXBException {
    if (paramSource == null)
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "source")); 
    if (paramSource instanceof SAXSource)
      return unmarshal((SAXSource)paramSource); 
    if (paramSource instanceof StreamSource)
      return unmarshal(streamSourceToInputSource((StreamSource)paramSource)); 
    if (paramSource instanceof DOMSource)
      return unmarshal(((DOMSource)paramSource).getNode()); 
    throw new IllegalArgumentException();
  }
  
  private Object unmarshal(SAXSource paramSAXSource) throws JAXBException {
    XMLReader xMLReader = paramSAXSource.getXMLReader();
    if (xMLReader == null)
      xMLReader = getXMLReader(); 
    return unmarshal(xMLReader, paramSAXSource.getInputSource());
  }
  
  protected abstract Object unmarshal(XMLReader paramXMLReader, InputSource paramInputSource) throws JAXBException;
  
  public final Object unmarshal(InputSource paramInputSource) throws JAXBException {
    if (paramInputSource == null)
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "source")); 
    return unmarshal(getXMLReader(), paramInputSource);
  }
  
  private Object unmarshal(String paramString) throws JAXBException { return unmarshal(new InputSource(paramString)); }
  
  public final Object unmarshal(URL paramURL) throws JAXBException {
    if (paramURL == null)
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "url")); 
    return unmarshal(paramURL.toExternalForm());
  }
  
  public final Object unmarshal(File paramFile) throws JAXBException {
    if (paramFile == null)
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "file")); 
    try {
      String str = paramFile.getAbsolutePath();
      if (File.separatorChar != '/')
        str = str.replace(File.separatorChar, '/'); 
      if (!str.startsWith("/"))
        str = "/" + str; 
      if (!str.endsWith("/") && paramFile.isDirectory())
        str = str + "/"; 
      return unmarshal(new URL("file", "", str));
    } catch (MalformedURLException malformedURLException) {
      throw new IllegalArgumentException(malformedURLException.getMessage());
    } 
  }
  
  public final Object unmarshal(InputStream paramInputStream) throws JAXBException {
    if (paramInputStream == null)
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "is")); 
    InputSource inputSource = new InputSource(paramInputStream);
    return unmarshal(inputSource);
  }
  
  public final Object unmarshal(Reader paramReader) throws JAXBException {
    if (paramReader == null)
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "reader")); 
    InputSource inputSource = new InputSource(paramReader);
    return unmarshal(inputSource);
  }
  
  private static InputSource streamSourceToInputSource(StreamSource paramStreamSource) {
    InputSource inputSource = new InputSource();
    inputSource.setSystemId(paramStreamSource.getSystemId());
    inputSource.setByteStream(paramStreamSource.getInputStream());
    inputSource.setCharacterStream(paramStreamSource.getReader());
    return inputSource;
  }
  
  public boolean isValidating() throws JAXBException { return this.validating; }
  
  public void setEventHandler(ValidationEventHandler paramValidationEventHandler) throws JAXBException {
    if (paramValidationEventHandler == null) {
      this.eventHandler = new DefaultValidationEventHandler();
    } else {
      this.eventHandler = paramValidationEventHandler;
    } 
  }
  
  public void setValidating(boolean paramBoolean) throws JAXBException { this.validating = paramBoolean; }
  
  public ValidationEventHandler getEventHandler() throws JAXBException { return this.eventHandler; }
  
  protected UnmarshalException createUnmarshalException(SAXException paramSAXException) {
    Exception exception = paramSAXException.getException();
    if (exception instanceof UnmarshalException)
      return (UnmarshalException)exception; 
    if (exception instanceof RuntimeException)
      throw (RuntimeException)exception; 
    return (exception != null) ? new UnmarshalException(exception) : new UnmarshalException(paramSAXException);
  }
  
  public void setProperty(String paramString, Object paramObject) throws PropertyException {
    if (paramString == null)
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "name")); 
    throw new PropertyException(paramString, paramObject);
  }
  
  public Object getProperty(String paramString) throws JAXBException {
    if (paramString == null)
      throw new IllegalArgumentException(Messages.format("Shared.MustNotBeNull", "name")); 
    throw new PropertyException(paramString);
  }
  
  public Object unmarshal(XMLEventReader paramXMLEventReader) throws JAXBException { throw new UnsupportedOperationException(); }
  
  public Object unmarshal(XMLStreamReader paramXMLStreamReader) throws JAXBException { throw new UnsupportedOperationException(); }
  
  public <T> JAXBElement<T> unmarshal(Node paramNode, Class<T> paramClass) throws JAXBException { throw new UnsupportedOperationException(); }
  
  public <T> JAXBElement<T> unmarshal(Source paramSource, Class<T> paramClass) throws JAXBException { throw new UnsupportedOperationException(); }
  
  public <T> JAXBElement<T> unmarshal(XMLStreamReader paramXMLStreamReader, Class<T> paramClass) throws JAXBException { throw new UnsupportedOperationException(); }
  
  public <T> JAXBElement<T> unmarshal(XMLEventReader paramXMLEventReader, Class<T> paramClass) throws JAXBException { throw new UnsupportedOperationException(); }
  
  public void setSchema(Schema paramSchema) { throw new UnsupportedOperationException(); }
  
  public Schema getSchema() { throw new UnsupportedOperationException(); }
  
  public void setAdapter(XmlAdapter paramXmlAdapter) {
    if (paramXmlAdapter == null)
      throw new IllegalArgumentException(); 
    setAdapter(paramXmlAdapter.getClass(), paramXmlAdapter);
  }
  
  public <A extends XmlAdapter> void setAdapter(Class<A> paramClass, A paramA) { throw new UnsupportedOperationException(); }
  
  public <A extends XmlAdapter> A getAdapter(Class<A> paramClass) { throw new UnsupportedOperationException(); }
  
  public void setAttachmentUnmarshaller(AttachmentUnmarshaller paramAttachmentUnmarshaller) { throw new UnsupportedOperationException(); }
  
  public AttachmentUnmarshaller getAttachmentUnmarshaller() { throw new UnsupportedOperationException(); }
  
  public void setListener(Unmarshaller.Listener paramListener) { throw new UnsupportedOperationException(); }
  
  public Unmarshaller.Listener getListener() { throw new UnsupportedOperationException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\helpers\AbstractUnmarshallerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */