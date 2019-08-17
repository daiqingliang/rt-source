package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.internal.bind.marshaller.DataWriter;
import com.sun.xml.internal.bind.marshaller.DumbEscapeHandler;
import com.sun.xml.internal.bind.marshaller.MinimumEscapeHandler;
import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
import com.sun.xml.internal.bind.marshaller.NioEscapeHandler;
import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import com.sun.xml.internal.bind.marshaller.XMLWriter;
import com.sun.xml.internal.bind.v2.runtime.output.C14nXmlOutput;
import com.sun.xml.internal.bind.v2.runtime.output.Encoded;
import com.sun.xml.internal.bind.v2.runtime.output.ForkXmlOutput;
import com.sun.xml.internal.bind.v2.runtime.output.IndentingUTF8XmlOutput;
import com.sun.xml.internal.bind.v2.runtime.output.NamespaceContextImpl;
import com.sun.xml.internal.bind.v2.runtime.output.SAXOutput;
import com.sun.xml.internal.bind.v2.runtime.output.UTF8XmlOutput;
import com.sun.xml.internal.bind.v2.runtime.output.XMLEventWriterOutput;
import com.sun.xml.internal.bind.v2.runtime.output.XMLStreamWriterOutput;
import com.sun.xml.internal.bind.v2.runtime.output.XmlOutput;
import com.sun.xml.internal.bind.v2.util.FatalAdapter;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.helpers.AbstractMarshallerImpl;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public final class MarshallerImpl extends AbstractMarshallerImpl implements ValidationEventHandler {
  private String indent = "    ";
  
  private NamespacePrefixMapper prefixMapper = null;
  
  private CharacterEscapeHandler escapeHandler = null;
  
  private String header = null;
  
  final JAXBContextImpl context;
  
  protected final XMLSerializer serializer;
  
  private Schema schema;
  
  private Marshaller.Listener externalListener = null;
  
  private boolean c14nSupport;
  
  private Flushable toBeFlushed;
  
  private Closeable toBeClosed;
  
  protected static final String INDENT_STRING = "com.sun.xml.internal.bind.indentString";
  
  protected static final String PREFIX_MAPPER = "com.sun.xml.internal.bind.namespacePrefixMapper";
  
  protected static final String ENCODING_HANDLER = "com.sun.xml.internal.bind.characterEscapeHandler";
  
  protected static final String ENCODING_HANDLER2 = "com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler";
  
  protected static final String XMLDECLARATION = "com.sun.xml.internal.bind.xmlDeclaration";
  
  protected static final String XML_HEADERS = "com.sun.xml.internal.bind.xmlHeaders";
  
  protected static final String C14N = "com.sun.xml.internal.bind.c14n";
  
  protected static final String OBJECT_IDENTITY_CYCLE_DETECTION = "com.sun.xml.internal.bind.objectIdentitityCycleDetection";
  
  public MarshallerImpl(JAXBContextImpl paramJAXBContextImpl, AssociationMap paramAssociationMap) {
    this.context = paramJAXBContextImpl;
    this.serializer = new XMLSerializer(this);
    this.c14nSupport = this.context.c14nSupport;
    try {
      setEventHandler(this);
    } catch (JAXBException jAXBException) {
      throw new AssertionError(jAXBException);
    } 
  }
  
  public JAXBContextImpl getContext() { return this.context; }
  
  public void marshal(Object paramObject, OutputStream paramOutputStream, NamespaceContext paramNamespaceContext) throws JAXBException { write(paramObject, createWriter(paramOutputStream), new StAXPostInitAction(paramNamespaceContext, this.serializer)); }
  
  public void marshal(Object paramObject, XMLStreamWriter paramXMLStreamWriter) throws JAXBException { write(paramObject, XMLStreamWriterOutput.create(paramXMLStreamWriter, this.context, this.escapeHandler), new StAXPostInitAction(paramXMLStreamWriter, this.serializer)); }
  
  public void marshal(Object paramObject, XMLEventWriter paramXMLEventWriter) throws JAXBException { write(paramObject, new XMLEventWriterOutput(paramXMLEventWriter), new StAXPostInitAction(paramXMLEventWriter, this.serializer)); }
  
  public void marshal(Object paramObject, XmlOutput paramXmlOutput) throws JAXBException { write(paramObject, paramXmlOutput, null); }
  
  final XmlOutput createXmlOutput(Result paramResult) throws JAXBException {
    if (paramResult instanceof SAXResult)
      return new SAXOutput(((SAXResult)paramResult).getHandler()); 
    if (paramResult instanceof DOMResult) {
      Node node = ((DOMResult)paramResult).getNode();
      if (node == null) {
        Document document = JAXBContextImpl.createDom((getContext()).disableSecurityProcessing);
        ((DOMResult)paramResult).setNode(document);
        return new SAXOutput(new SAX2DOMEx(document));
      } 
      return new SAXOutput(new SAX2DOMEx(node));
    } 
    if (paramResult instanceof StreamResult) {
      StreamResult streamResult = (StreamResult)paramResult;
      if (streamResult.getWriter() != null)
        return createWriter(streamResult.getWriter()); 
      if (streamResult.getOutputStream() != null)
        return createWriter(streamResult.getOutputStream()); 
      if (streamResult.getSystemId() != null) {
        String str = streamResult.getSystemId();
        try {
          str = (new URI(str)).getPath();
        } catch (URISyntaxException uRISyntaxException) {}
        try {
          FileOutputStream fileOutputStream = new FileOutputStream(str);
          assert this.toBeClosed == null;
          this.toBeClosed = fileOutputStream;
          return createWriter(fileOutputStream);
        } catch (IOException iOException) {
          throw new MarshalException(iOException);
        } 
      } 
    } 
    throw new MarshalException(Messages.UNSUPPORTED_RESULT.format(new Object[0]));
  }
  
  final Runnable createPostInitAction(Result paramResult) {
    if (paramResult instanceof DOMResult) {
      Node node = ((DOMResult)paramResult).getNode();
      return new DomPostInitAction(node, this.serializer);
    } 
    return null;
  }
  
  public void marshal(Object paramObject, Result paramResult) throws JAXBException { write(paramObject, createXmlOutput(paramResult), createPostInitAction(paramResult)); }
  
  protected final <T> void write(Name paramName, JaxBeanInfo<T> paramJaxBeanInfo, T paramT, XmlOutput paramXmlOutput, Runnable paramRunnable) throws JAXBException {
    try {
      try {
        prewrite(paramXmlOutput, true, paramRunnable);
        this.serializer.startElement(paramName, null);
        if (paramJaxBeanInfo.jaxbType == Void.class || paramJaxBeanInfo.jaxbType == void.class) {
          this.serializer.endNamespaceDecls(null);
          this.serializer.endAttributes();
        } else if (paramT == null) {
          this.serializer.writeXsiNilTrue();
        } else {
          this.serializer.childAsXsiType(paramT, "root", paramJaxBeanInfo, false);
        } 
        this.serializer.endElement();
        postwrite();
      } catch (SAXException sAXException) {
        throw new MarshalException(sAXException);
      } catch (IOException iOException) {
        throw new MarshalException(iOException);
      } catch (XMLStreamException xMLStreamException) {
        throw new MarshalException(xMLStreamException);
      } finally {
        this.serializer.close();
      } 
    } finally {
      cleanUp();
    } 
  }
  
  private void write(Object paramObject, XmlOutput paramXmlOutput, Runnable paramRunnable) throws JAXBException {
    try {
      if (paramObject == null)
        throw new IllegalArgumentException(Messages.NOT_MARSHALLABLE.format(new Object[0])); 
      if (this.schema != null) {
        ValidatorHandler validatorHandler = this.schema.newValidatorHandler();
        validatorHandler.setErrorHandler(new FatalAdapter(this.serializer));
        XMLFilterImpl xMLFilterImpl = new XMLFilterImpl() {
            public void startPrefixMapping(String param1String1, String param1String2) throws SAXException { super.startPrefixMapping(param1String1.intern(), param1String2.intern()); }
          };
        xMLFilterImpl.setContentHandler(validatorHandler);
        paramXmlOutput = new ForkXmlOutput(new SAXOutput(this, xMLFilterImpl) {
              public void startDocument(XMLSerializer param1XMLSerializer, boolean param1Boolean, int[] param1ArrayOfInt, NamespaceContextImpl param1NamespaceContextImpl) throws SAXException, IOException, XMLStreamException { super.startDocument(param1XMLSerializer, false, param1ArrayOfInt, param1NamespaceContextImpl); }
              
              public void endDocument(boolean param1Boolean) throws SAXException, IOException, XMLStreamException { super.endDocument(false); }
            }paramXmlOutput);
      } 
      try {
        prewrite(paramXmlOutput, isFragment(), paramRunnable);
        this.serializer.childAsRoot(paramObject);
        postwrite();
      } catch (SAXException sAXException) {
        throw new MarshalException(sAXException);
      } catch (IOException iOException) {
        throw new MarshalException(iOException);
      } catch (XMLStreamException xMLStreamException) {
        throw new MarshalException(xMLStreamException);
      } finally {
        this.serializer.close();
      } 
    } finally {
      cleanUp();
    } 
  }
  
  private void cleanUp() {
    if (this.toBeFlushed != null)
      try {
        this.toBeFlushed.flush();
      } catch (IOException iOException) {} 
    if (this.toBeClosed != null)
      try {
        this.toBeClosed.close();
      } catch (IOException iOException) {} 
    this.toBeFlushed = null;
    this.toBeClosed = null;
  }
  
  private void prewrite(XmlOutput paramXmlOutput, boolean paramBoolean, Runnable paramRunnable) throws IOException, SAXException, XMLStreamException {
    this.serializer.startDocument(paramXmlOutput, paramBoolean, getSchemaLocation(), getNoNSSchemaLocation());
    if (paramRunnable != null)
      paramRunnable.run(); 
    if (this.prefixMapper != null) {
      String[] arrayOfString = this.prefixMapper.getContextualNamespaceDecls();
      if (arrayOfString != null)
        for (boolean bool = false; bool < arrayOfString.length; bool += true) {
          String str1 = arrayOfString[bool];
          String str2 = arrayOfString[bool + true];
          if (str2 != null && str1 != null)
            this.serializer.addInscopeBinding(str2, str1); 
        }  
    } 
    this.serializer.setPrefixMapper(this.prefixMapper);
  }
  
  private void postwrite() {
    this.serializer.endDocument();
    this.serializer.reconcileID();
  }
  
  CharacterEscapeHandler getEscapeHandler() { return this.escapeHandler; }
  
  protected CharacterEscapeHandler createEscapeHandler(String paramString) {
    if (this.escapeHandler != null)
      return this.escapeHandler; 
    if (paramString.startsWith("UTF"))
      return MinimumEscapeHandler.theInstance; 
    try {
      return new NioEscapeHandler(getJavaEncoding(paramString));
    } catch (Throwable throwable) {
      return DumbEscapeHandler.theInstance;
    } 
  }
  
  public XmlOutput createWriter(Writer paramWriter, String paramString) {
    XMLWriter xMLWriter;
    if (!(paramWriter instanceof BufferedWriter))
      paramWriter = new BufferedWriter(paramWriter); 
    assert this.toBeFlushed == null;
    this.toBeFlushed = paramWriter;
    CharacterEscapeHandler characterEscapeHandler = createEscapeHandler(paramString);
    if (isFormattedOutput()) {
      DataWriter dataWriter = new DataWriter(paramWriter, paramString, characterEscapeHandler);
      dataWriter.setIndentStep(this.indent);
      xMLWriter = dataWriter;
    } else {
      xMLWriter = new XMLWriter(paramWriter, paramString, characterEscapeHandler);
    } 
    xMLWriter.setXmlDecl(!isFragment());
    xMLWriter.setHeader(this.header);
    return new SAXOutput(xMLWriter);
  }
  
  public XmlOutput createWriter(Writer paramWriter) { return createWriter(paramWriter, getEncoding()); }
  
  public XmlOutput createWriter(OutputStream paramOutputStream) throws JAXBException { return createWriter(paramOutputStream, getEncoding()); }
  
  public XmlOutput createWriter(OutputStream paramOutputStream, String paramString) throws JAXBException {
    if (paramString.equals("UTF-8")) {
      UTF8XmlOutput uTF8XmlOutput;
      Encoded[] arrayOfEncoded = this.context.getUTF8NameTable();
      if (isFormattedOutput()) {
        uTF8XmlOutput = new IndentingUTF8XmlOutput(paramOutputStream, this.indent, arrayOfEncoded, this.escapeHandler);
      } else if (this.c14nSupport) {
        uTF8XmlOutput = new C14nXmlOutput(paramOutputStream, arrayOfEncoded, this.context.c14nSupport, this.escapeHandler);
      } else {
        uTF8XmlOutput = new UTF8XmlOutput(paramOutputStream, arrayOfEncoded, this.escapeHandler);
      } 
      if (this.header != null)
        uTF8XmlOutput.setHeader(this.header); 
      return uTF8XmlOutput;
    } 
    try {
      return createWriter(new OutputStreamWriter(paramOutputStream, getJavaEncoding(paramString)), paramString);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new MarshalException(Messages.UNSUPPORTED_ENCODING.format(new Object[] { paramString }, ), unsupportedEncodingException);
    } 
  }
  
  public Object getProperty(String paramString) throws PropertyException { return "com.sun.xml.internal.bind.indentString".equals(paramString) ? this.indent : (("com.sun.xml.internal.bind.characterEscapeHandler".equals(paramString) || "com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler".equals(paramString)) ? this.escapeHandler : ("com.sun.xml.internal.bind.namespacePrefixMapper".equals(paramString) ? this.prefixMapper : ("com.sun.xml.internal.bind.xmlDeclaration".equals(paramString) ? Boolean.valueOf(!isFragment()) : ("com.sun.xml.internal.bind.xmlHeaders".equals(paramString) ? this.header : ("com.sun.xml.internal.bind.c14n".equals(paramString) ? Boolean.valueOf(this.c14nSupport) : ("com.sun.xml.internal.bind.objectIdentitityCycleDetection".equals(paramString) ? Boolean.valueOf(this.serializer.getObjectIdentityCycleDetection()) : super.getProperty(paramString))))))); }
  
  public void setProperty(String paramString, Object paramObject) throws PropertyException {
    if ("com.sun.xml.internal.bind.indentString".equals(paramString)) {
      checkString(paramString, paramObject);
      this.indent = (String)paramObject;
      return;
    } 
    if ("com.sun.xml.internal.bind.characterEscapeHandler".equals(paramString) || "com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler".equals(paramString)) {
      if (!(paramObject instanceof CharacterEscapeHandler))
        throw new PropertyException(Messages.MUST_BE_X.format(new Object[] { paramString, CharacterEscapeHandler.class.getName(), paramObject.getClass().getName() })); 
      this.escapeHandler = (CharacterEscapeHandler)paramObject;
      return;
    } 
    if ("com.sun.xml.internal.bind.namespacePrefixMapper".equals(paramString)) {
      if (!(paramObject instanceof NamespacePrefixMapper))
        throw new PropertyException(Messages.MUST_BE_X.format(new Object[] { paramString, NamespacePrefixMapper.class.getName(), paramObject.getClass().getName() })); 
      this.prefixMapper = (NamespacePrefixMapper)paramObject;
      return;
    } 
    if ("com.sun.xml.internal.bind.xmlDeclaration".equals(paramString)) {
      checkBoolean(paramString, paramObject);
      super.setProperty("jaxb.fragment", Boolean.valueOf(!((Boolean)paramObject).booleanValue()));
      return;
    } 
    if ("com.sun.xml.internal.bind.xmlHeaders".equals(paramString)) {
      checkString(paramString, paramObject);
      this.header = (String)paramObject;
      return;
    } 
    if ("com.sun.xml.internal.bind.c14n".equals(paramString)) {
      checkBoolean(paramString, paramObject);
      this.c14nSupport = ((Boolean)paramObject).booleanValue();
      return;
    } 
    if ("com.sun.xml.internal.bind.objectIdentitityCycleDetection".equals(paramString)) {
      checkBoolean(paramString, paramObject);
      this.serializer.setObjectIdentityCycleDetection(((Boolean)paramObject).booleanValue());
      return;
    } 
    super.setProperty(paramString, paramObject);
  }
  
  private void checkBoolean(String paramString, Object paramObject) throws PropertyException {
    if (!(paramObject instanceof Boolean))
      throw new PropertyException(Messages.MUST_BE_X.format(new Object[] { paramString, Boolean.class.getName(), paramObject.getClass().getName() })); 
  }
  
  private void checkString(String paramString, Object paramObject) throws PropertyException {
    if (!(paramObject instanceof String))
      throw new PropertyException(Messages.MUST_BE_X.format(new Object[] { paramString, String.class.getName(), paramObject.getClass().getName() })); 
  }
  
  public <A extends javax.xml.bind.annotation.adapters.XmlAdapter> void setAdapter(Class<A> paramClass, A paramA) {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    this.serializer.putAdapter(paramClass, paramA);
  }
  
  public <A extends javax.xml.bind.annotation.adapters.XmlAdapter> A getAdapter(Class<A> paramClass) {
    if (paramClass == null)
      throw new IllegalArgumentException(); 
    return this.serializer.containsAdapter(paramClass) ? (A)this.serializer.getAdapter(paramClass) : null;
  }
  
  public void setAttachmentMarshaller(AttachmentMarshaller paramAttachmentMarshaller) { this.serializer.attachmentMarshaller = paramAttachmentMarshaller; }
  
  public AttachmentMarshaller getAttachmentMarshaller() { return this.serializer.attachmentMarshaller; }
  
  public Schema getSchema() { return this.schema; }
  
  public void setSchema(Schema paramSchema) { this.schema = paramSchema; }
  
  public boolean handleEvent(ValidationEvent paramValidationEvent) { return false; }
  
  public Marshaller.Listener getListener() { return this.externalListener; }
  
  public void setListener(Marshaller.Listener paramListener) { this.externalListener = paramListener; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\MarshallerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */