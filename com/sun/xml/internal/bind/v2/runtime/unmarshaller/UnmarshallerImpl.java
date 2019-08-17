package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.IDResolver;
import com.sun.xml.internal.bind.api.ClassResolver;
import com.sun.xml.internal.bind.unmarshaller.DOMScanner;
import com.sun.xml.internal.bind.unmarshaller.InfosetScanner;
import com.sun.xml.internal.bind.unmarshaller.Messages;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.runtime.AssociationMap;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.bind.helpers.AbstractUnmarshallerImpl;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public final class UnmarshallerImpl extends AbstractUnmarshallerImpl implements ValidationEventHandler, Closeable {
  protected final JAXBContextImpl context;
  
  private Schema schema;
  
  public final UnmarshallingContext coordinator;
  
  private Unmarshaller.Listener externalListener;
  
  private AttachmentUnmarshaller attachmentUnmarshaller;
  
  private IDResolver idResolver = new DefaultIDResolver();
  
  private XMLReader reader = null;
  
  private static final DefaultHandler dummyHandler = new DefaultHandler();
  
  public static final String FACTORY = "com.sun.xml.internal.bind.ObjectFactory";
  
  public UnmarshallerImpl(JAXBContextImpl paramJAXBContextImpl, AssociationMap paramAssociationMap) {
    this.context = paramJAXBContextImpl;
    this.coordinator = new UnmarshallingContext(this, paramAssociationMap);
    try {
      setEventHandler(this);
    } catch (JAXBException jAXBException) {
      throw new AssertionError(jAXBException);
    } 
  }
  
  public UnmarshallerHandler getUnmarshallerHandler() { return getUnmarshallerHandler(true, null); }
  
  protected XMLReader getXMLReader() throws JAXBException {
    if (this.reader == null)
      try {
        SAXParserFactory sAXParserFactory = XmlFactory.createParserFactory(this.context.disableSecurityProcessing);
        sAXParserFactory.setValidating(false);
        this.reader = sAXParserFactory.newSAXParser().getXMLReader();
      } catch (ParserConfigurationException parserConfigurationException) {
        throw new JAXBException(parserConfigurationException);
      } catch (SAXException sAXException) {
        throw new JAXBException(sAXException);
      }  
    return this.reader;
  }
  
  private SAXConnector getUnmarshallerHandler(boolean paramBoolean, JaxBeanInfo paramJaxBeanInfo) {
    XmlVisitor xmlVisitor = createUnmarshallerHandler(null, false, paramJaxBeanInfo);
    if (paramBoolean)
      xmlVisitor = new InterningXmlVisitor(xmlVisitor); 
    return new SAXConnector(xmlVisitor, null);
  }
  
  public final XmlVisitor createUnmarshallerHandler(InfosetScanner paramInfosetScanner, boolean paramBoolean, JaxBeanInfo paramJaxBeanInfo) {
    this.coordinator.reset(paramInfosetScanner, paramBoolean, paramJaxBeanInfo, this.idResolver);
    MTOMDecorator mTOMDecorator = this.coordinator;
    if (this.schema != null)
      mTOMDecorator = new ValidatingUnmarshaller(this.schema, mTOMDecorator); 
    if (this.attachmentUnmarshaller != null && this.attachmentUnmarshaller.isXOPPackage())
      mTOMDecorator = new MTOMDecorator(this, mTOMDecorator, this.attachmentUnmarshaller); 
    return mTOMDecorator;
  }
  
  public static boolean needsInterning(XMLReader paramXMLReader) {
    try {
      paramXMLReader.setFeature("http://xml.org/sax/features/string-interning", true);
    } catch (SAXException sAXException) {}
    try {
      if (paramXMLReader.getFeature("http://xml.org/sax/features/string-interning"))
        return false; 
    } catch (SAXException sAXException) {}
    return true;
  }
  
  protected Object unmarshal(XMLReader paramXMLReader, InputSource paramInputSource) throws JAXBException { return unmarshal0(paramXMLReader, paramInputSource, null); }
  
  protected <T> JAXBElement<T> unmarshal(XMLReader paramXMLReader, InputSource paramInputSource, Class<T> paramClass) throws JAXBException {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    return (JAXBElement)unmarshal0(paramXMLReader, paramInputSource, getBeanInfo(paramClass));
  }
  
  private Object unmarshal0(XMLReader paramXMLReader, InputSource paramInputSource, JaxBeanInfo paramJaxBeanInfo) throws JAXBException {
    SAXConnector sAXConnector = getUnmarshallerHandler(needsInterning(paramXMLReader), paramJaxBeanInfo);
    paramXMLReader.setContentHandler(sAXConnector);
    paramXMLReader.setErrorHandler(this.coordinator);
    try {
      paramXMLReader.parse(paramInputSource);
    } catch (IOException iOException) {
      this.coordinator.clearStates();
      throw new UnmarshalException(iOException);
    } catch (SAXException sAXException) {
      this.coordinator.clearStates();
      throw createUnmarshalException(sAXException);
    } 
    Object object = sAXConnector.getResult();
    paramXMLReader.setContentHandler(dummyHandler);
    paramXMLReader.setErrorHandler(dummyHandler);
    return object;
  }
  
  public <T> JAXBElement<T> unmarshal(Source paramSource, Class<T> paramClass) throws JAXBException {
    if (paramSource instanceof SAXSource) {
      SAXSource sAXSource = (SAXSource)paramSource;
      XMLReader xMLReader = sAXSource.getXMLReader();
      if (xMLReader == null)
        xMLReader = getXMLReader(); 
      return unmarshal(xMLReader, sAXSource.getInputSource(), paramClass);
    } 
    if (paramSource instanceof StreamSource)
      return unmarshal(getXMLReader(), streamSourceToInputSource((StreamSource)paramSource), paramClass); 
    if (paramSource instanceof DOMSource)
      return unmarshal(((DOMSource)paramSource).getNode(), paramClass); 
    throw new IllegalArgumentException();
  }
  
  public Object unmarshal0(Source paramSource, JaxBeanInfo paramJaxBeanInfo) throws JAXBException {
    if (paramSource instanceof SAXSource) {
      SAXSource sAXSource = (SAXSource)paramSource;
      XMLReader xMLReader = sAXSource.getXMLReader();
      if (xMLReader == null)
        xMLReader = getXMLReader(); 
      return unmarshal0(xMLReader, sAXSource.getInputSource(), paramJaxBeanInfo);
    } 
    if (paramSource instanceof StreamSource)
      return unmarshal0(getXMLReader(), streamSourceToInputSource((StreamSource)paramSource), paramJaxBeanInfo); 
    if (paramSource instanceof DOMSource)
      return unmarshal0(((DOMSource)paramSource).getNode(), paramJaxBeanInfo); 
    throw new IllegalArgumentException();
  }
  
  public final ValidationEventHandler getEventHandler() {
    try {
      return super.getEventHandler();
    } catch (JAXBException jAXBException) {
      throw new AssertionError();
    } 
  }
  
  public final boolean hasEventHandler() { return (getEventHandler() != this); }
  
  public <T> JAXBElement<T> unmarshal(Node paramNode, Class<T> paramClass) throws JAXBException {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    return (JAXBElement)unmarshal0(paramNode, getBeanInfo(paramClass));
  }
  
  public final Object unmarshal(Node paramNode) throws JAXBException { return unmarshal0(paramNode, null); }
  
  @Deprecated
  public final Object unmarshal(SAXSource paramSAXSource) throws JAXBException { return unmarshal(paramSAXSource); }
  
  public final Object unmarshal0(Node paramNode, JaxBeanInfo paramJaxBeanInfo) throws JAXBException {
    try {
      DOMScanner dOMScanner = new DOMScanner();
      InterningXmlVisitor interningXmlVisitor = new InterningXmlVisitor(createUnmarshallerHandler(null, false, paramJaxBeanInfo));
      dOMScanner.setContentHandler(new SAXConnector(interningXmlVisitor, dOMScanner));
      if (paramNode.getNodeType() == 1) {
        dOMScanner.scan((Element)paramNode);
      } else if (paramNode.getNodeType() == 9) {
        dOMScanner.scan((Document)paramNode);
      } else {
        throw new IllegalArgumentException("Unexpected node type: " + paramNode);
      } 
      Object object = interningXmlVisitor.getContext().getResult();
      interningXmlVisitor.getContext().clearResult();
      return object;
    } catch (SAXException sAXException) {
      throw createUnmarshalException(sAXException);
    } 
  }
  
  public Object unmarshal(XMLStreamReader paramXMLStreamReader) throws JAXBException { return unmarshal0(paramXMLStreamReader, null); }
  
  public <T> JAXBElement<T> unmarshal(XMLStreamReader paramXMLStreamReader, Class<T> paramClass) throws JAXBException {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    return (JAXBElement)unmarshal0(paramXMLStreamReader, getBeanInfo(paramClass));
  }
  
  public Object unmarshal0(XMLStreamReader paramXMLStreamReader, JaxBeanInfo paramJaxBeanInfo) throws JAXBException {
    if (paramXMLStreamReader == null)
      throw new IllegalArgumentException(Messages.format("Unmarshaller.NullReader")); 
    int i = paramXMLStreamReader.getEventType();
    if (i != 1 && i != 7)
      throw new IllegalStateException(Messages.format("Unmarshaller.IllegalReaderState", Integer.valueOf(i))); 
    XmlVisitor xmlVisitor = createUnmarshallerHandler(null, false, paramJaxBeanInfo);
    StAXConnector stAXConnector = StAXStreamConnector.create(paramXMLStreamReader, xmlVisitor);
    try {
      stAXConnector.bridge();
    } catch (XMLStreamException xMLStreamException) {
      throw handleStreamException(xMLStreamException);
    } 
    Object object = xmlVisitor.getContext().getResult();
    xmlVisitor.getContext().clearResult();
    return object;
  }
  
  public <T> JAXBElement<T> unmarshal(XMLEventReader paramXMLEventReader, Class<T> paramClass) throws JAXBException {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    return (JAXBElement)unmarshal0(paramXMLEventReader, getBeanInfo(paramClass));
  }
  
  public Object unmarshal(XMLEventReader paramXMLEventReader) throws JAXBException { return unmarshal0(paramXMLEventReader, null); }
  
  private Object unmarshal0(XMLEventReader paramXMLEventReader, JaxBeanInfo paramJaxBeanInfo) throws JAXBException {
    if (paramXMLEventReader == null)
      throw new IllegalArgumentException(Messages.format("Unmarshaller.NullReader")); 
    try {
      XMLEvent xMLEvent = paramXMLEventReader.peek();
      if (!xMLEvent.isStartElement() && !xMLEvent.isStartDocument())
        throw new IllegalStateException(Messages.format("Unmarshaller.IllegalReaderState", Integer.valueOf(xMLEvent.getEventType()))); 
      boolean bool = paramXMLEventReader.getClass().getName().equals("com.sun.xml.internal.stream.XMLReaderImpl");
      XmlVisitor xmlVisitor = createUnmarshallerHandler(null, false, paramJaxBeanInfo);
      if (!bool)
        xmlVisitor = new InterningXmlVisitor(xmlVisitor); 
      (new StAXEventConnector(paramXMLEventReader, xmlVisitor)).bridge();
      return xmlVisitor.getContext().getResult();
    } catch (XMLStreamException xMLStreamException) {
      throw handleStreamException(xMLStreamException);
    } 
  }
  
  public Object unmarshal0(InputStream paramInputStream, JaxBeanInfo paramJaxBeanInfo) throws JAXBException { return unmarshal0(getXMLReader(), new InputSource(paramInputStream), paramJaxBeanInfo); }
  
  private static JAXBException handleStreamException(XMLStreamException paramXMLStreamException) {
    Throwable throwable = paramXMLStreamException.getNestedException();
    return (throwable instanceof JAXBException) ? (JAXBException)throwable : ((throwable instanceof SAXException) ? new UnmarshalException(throwable) : new UnmarshalException(paramXMLStreamException));
  }
  
  public Object getProperty(String paramString) throws PropertyException { return paramString.equals(IDResolver.class.getName()) ? this.idResolver : super.getProperty(paramString); }
  
  public void setProperty(String paramString, Object paramObject) throws PropertyException {
    if (paramString.equals("com.sun.xml.internal.bind.ObjectFactory")) {
      this.coordinator.setFactories(paramObject);
      return;
    } 
    if (paramString.equals(IDResolver.class.getName())) {
      this.idResolver = (IDResolver)paramObject;
      return;
    } 
    if (paramString.equals(ClassResolver.class.getName())) {
      this.coordinator.classResolver = (ClassResolver)paramObject;
      return;
    } 
    if (paramString.equals(ClassLoader.class.getName())) {
      this.coordinator.classLoader = (ClassLoader)paramObject;
      return;
    } 
    super.setProperty(paramString, paramObject);
  }
  
  public void setSchema(Schema paramSchema) { this.schema = paramSchema; }
  
  public Schema getSchema() { return this.schema; }
  
  public AttachmentUnmarshaller getAttachmentUnmarshaller() { return this.attachmentUnmarshaller; }
  
  public void setAttachmentUnmarshaller(AttachmentUnmarshaller paramAttachmentUnmarshaller) { this.attachmentUnmarshaller = paramAttachmentUnmarshaller; }
  
  public boolean isValidating() { throw new UnsupportedOperationException(); }
  
  public void setValidating(boolean paramBoolean) { throw new UnsupportedOperationException(); }
  
  public <A extends javax.xml.bind.annotation.adapters.XmlAdapter> void setAdapter(Class<A> paramClass, A paramA) {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    this.coordinator.putAdapter(paramClass, paramA);
  }
  
  public <A extends javax.xml.bind.annotation.adapters.XmlAdapter> A getAdapter(Class<A> paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    return this.coordinator.containsAdapter(paramClass) ? (A)this.coordinator.getAdapter(paramClass) : null;
  }
  
  public UnmarshalException createUnmarshalException(SAXException paramSAXException) { return super.createUnmarshalException(paramSAXException); }
  
  public boolean handleEvent(ValidationEvent paramValidationEvent) { return (paramValidationEvent.getSeverity() != 2); }
  
  private static InputSource streamSourceToInputSource(StreamSource paramStreamSource) {
    InputSource inputSource = new InputSource();
    inputSource.setSystemId(paramStreamSource.getSystemId());
    inputSource.setByteStream(paramStreamSource.getInputStream());
    inputSource.setCharacterStream(paramStreamSource.getReader());
    return inputSource;
  }
  
  public <T> JaxBeanInfo<T> getBeanInfo(Class<T> paramClass) throws JAXBException { return this.context.getBeanInfo(paramClass, true); }
  
  public Unmarshaller.Listener getListener() { return this.externalListener; }
  
  public void setListener(Unmarshaller.Listener paramListener) { this.externalListener = paramListener; }
  
  public UnmarshallingContext getContext() { return this.coordinator; }
  
  protected void finalize() throws Throwable {
    try {
      ClassFactory.cleanCache();
    } finally {
      super.finalize();
    } 
  }
  
  public void close() throws Throwable { ClassFactory.cleanCache(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\UnmarshallerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */