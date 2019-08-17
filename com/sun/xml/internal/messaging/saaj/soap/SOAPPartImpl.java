package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import com.sun.xml.internal.messaging.saaj.util.FastInfosetReflection;
import com.sun.xml.internal.messaging.saaj.util.JAXMStreamSource;
import com.sun.xml.internal.messaging.saaj.util.MimeHeadersUtil;
import com.sun.xml.internal.messaging.saaj.util.SAAJUtil;
import com.sun.xml.internal.messaging.saaj.util.XMLDeclarationParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

public abstract class SOAPPartImpl extends SOAPPart implements SOAPDocument {
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
  
  protected MimeHeaders headers = new MimeHeaders();
  
  protected Envelope envelope;
  
  protected Source source;
  
  protected SOAPDocumentImpl document = new SOAPDocumentImpl(this);
  
  private boolean sourceWasSet = false;
  
  protected boolean omitXmlDecl = true;
  
  protected String sourceCharsetEncoding = null;
  
  protected MessageImpl message;
  
  static final boolean lazyContentLength = SAAJUtil.getSystemBoolean("saaj.lazy.contentlength");
  
  protected SOAPPartImpl() { this(null); }
  
  protected SOAPPartImpl(MessageImpl paramMessageImpl) {
    this.message = paramMessageImpl;
    this.headers.setHeader("Content-Type", getContentType());
  }
  
  protected abstract String getContentType();
  
  protected abstract Envelope createEnvelopeFromSource() throws SOAPException;
  
  protected abstract Envelope createEmptyEnvelope(String paramString) throws SOAPException;
  
  protected abstract SOAPPartImpl duplicateType();
  
  protected String getContentTypeString() { return getContentType(); }
  
  public boolean isFastInfoset() { return (this.message != null) ? this.message.isFastInfoset() : 0; }
  
  public SOAPEnvelope getEnvelope() throws SOAPException {
    if (this.sourceWasSet)
      this.sourceWasSet = false; 
    lookForEnvelope();
    if (this.envelope != null) {
      if (this.source != null) {
        this.document.removeChild(this.envelope);
        this.envelope = createEnvelopeFromSource();
      } 
    } else if (this.source != null) {
      this.envelope = createEnvelopeFromSource();
    } else {
      this.envelope = createEmptyEnvelope(null);
      this.document.insertBefore(this.envelope, null);
    } 
    return this.envelope;
  }
  
  protected void lookForEnvelope() {
    Element element = this.document.doGetDocumentElement();
    if (element == null || element instanceof Envelope) {
      this.envelope = (EnvelopeImpl)element;
    } else {
      if (!(element instanceof ElementImpl)) {
        log.severe("SAAJ0512.soap.incorrect.factory.used");
        throw new SOAPExceptionImpl("Unable to create envelope: incorrect factory used during tree construction");
      } 
      ElementImpl elementImpl = (ElementImpl)element;
      if (elementImpl.getLocalName().equalsIgnoreCase("Envelope")) {
        String str1 = elementImpl.getPrefix();
        String str2 = (str1 == null) ? elementImpl.getNamespaceURI() : elementImpl.getNamespaceURI(str1);
        if (!str2.equals("http://schemas.xmlsoap.org/soap/envelope/") && !str2.equals("http://www.w3.org/2003/05/soap-envelope")) {
          log.severe("SAAJ0513.soap.unknown.ns");
          throw new SOAPVersionMismatchException("Unable to create envelope from given source because the namespace was not recognized");
        } 
      } else {
        log.severe("SAAJ0514.soap.root.elem.not.named.envelope");
        throw new SOAPExceptionImpl("Unable to create envelope from given source because the root element is not named \"Envelope\"");
      } 
    } 
  }
  
  public void removeAllMimeHeaders() { this.headers.removeAllHeaders(); }
  
  public void removeMimeHeader(String paramString) { this.headers.removeHeader(paramString); }
  
  public String[] getMimeHeader(String paramString) { return this.headers.getHeader(paramString); }
  
  public void setMimeHeader(String paramString1, String paramString2) { this.headers.setHeader(paramString1, paramString2); }
  
  public void addMimeHeader(String paramString1, String paramString2) { this.headers.addHeader(paramString1, paramString2); }
  
  public Iterator getAllMimeHeaders() { return this.headers.getAllHeaders(); }
  
  public Iterator getMatchingMimeHeaders(String[] paramArrayOfString) { return this.headers.getMatchingHeaders(paramArrayOfString); }
  
  public Iterator getNonMatchingMimeHeaders(String[] paramArrayOfString) { return this.headers.getNonMatchingHeaders(paramArrayOfString); }
  
  public Source getContent() throws SOAPException {
    if (this.source != null) {
      InputStream inputStream = null;
      if (this.source instanceof JAXMStreamSource) {
        StreamSource streamSource = (StreamSource)this.source;
        inputStream = streamSource.getInputStream();
      } else if (FastInfosetReflection.isFastInfosetSource(this.source)) {
        SAXSource sAXSource = (SAXSource)this.source;
        inputStream = sAXSource.getInputSource().getByteStream();
      } 
      if (inputStream != null)
        try {
          inputStream.reset();
        } catch (IOException iOException) {} 
      return this.source;
    } 
    return ((Envelope)getEnvelope()).getContent();
  }
  
  public void setContent(Source paramSource) throws SOAPException {
    try {
      if (paramSource instanceof StreamSource) {
        InputStream inputStream = ((StreamSource)paramSource).getInputStream();
        Reader reader = ((StreamSource)paramSource).getReader();
        if (inputStream != null) {
          this.source = new JAXMStreamSource(inputStream);
        } else if (reader != null) {
          this.source = new JAXMStreamSource(reader);
        } else {
          log.severe("SAAJ0544.soap.no.valid.reader.for.src");
          throw new SOAPExceptionImpl("Source does not have a valid Reader or InputStream");
        } 
      } else if (FastInfosetReflection.isFastInfosetSource(paramSource)) {
        InputStream inputStream = FastInfosetReflection.FastInfosetSource_getInputStream(paramSource);
        if (!(inputStream instanceof ByteInputStream)) {
          ByteOutputStream byteOutputStream = new ByteOutputStream();
          byteOutputStream.write(inputStream);
          FastInfosetReflection.FastInfosetSource_setInputStream(paramSource, byteOutputStream.newInputStream());
        } 
        this.source = paramSource;
      } else {
        this.source = paramSource;
      } 
      this.sourceWasSet = true;
    } catch (Exception exception) {
      exception.printStackTrace();
      log.severe("SAAJ0545.soap.cannot.set.src.for.part");
      throw new SOAPExceptionImpl("Error setting the source for SOAPPart: " + exception.getMessage());
    } 
  }
  
  public InputStream getContentAsStream() throws IOException {
    if (this.source != null) {
      InputStream inputStream = null;
      if (this.source instanceof StreamSource && !isFastInfoset()) {
        inputStream = ((StreamSource)this.source).getInputStream();
      } else if (FastInfosetReflection.isFastInfosetSource(this.source) && isFastInfoset()) {
        try {
          inputStream = FastInfosetReflection.FastInfosetSource_getInputStream(this.source);
        } catch (Exception exception) {
          throw new IOException(exception.toString());
        } 
      } 
      if (inputStream != null) {
        if (lazyContentLength)
          return inputStream; 
        if (!(inputStream instanceof ByteInputStream)) {
          log.severe("SAAJ0546.soap.stream.incorrect.type");
          throw new IOException("Internal error: stream not of the right type");
        } 
        return (ByteInputStream)inputStream;
      } 
    } 
    ByteOutputStream byteOutputStream = new ByteOutputStream();
    Envelope envelope1 = null;
    try {
      envelope1 = (Envelope)getEnvelope();
      envelope1.output(byteOutputStream, isFastInfoset());
    } catch (SOAPException sOAPException) {
      log.severe("SAAJ0547.soap.cannot.externalize");
      throw new SOAPIOException("SOAP exception while trying to externalize: ", sOAPException);
    } 
    return byteOutputStream.newInputStream();
  }
  
  MimeBodyPart getMimePart() throws SOAPException {
    try {
      MimeBodyPart mimeBodyPart = new MimeBodyPart();
      mimeBodyPart.setDataHandler(getDataHandler());
      AttachmentPartImpl.copyMimeHeaders(this.headers, mimeBodyPart);
      return mimeBodyPart;
    } catch (SOAPException sOAPException) {
      throw sOAPException;
    } catch (Exception exception) {
      log.severe("SAAJ0548.soap.cannot.externalize.hdr");
      throw new SOAPExceptionImpl("Unable to externalize header", exception);
    } 
  }
  
  MimeHeaders getMimeHeaders() { return this.headers; }
  
  DataHandler getDataHandler() {
    DataSource dataSource = new DataSource() {
        public OutputStream getOutputStream() throws IOException { throw new IOException("Illegal Operation"); }
        
        public String getContentType() { return SOAPPartImpl.this.getContentTypeString(); }
        
        public String getName() { return SOAPPartImpl.this.getContentId(); }
        
        public InputStream getInputStream() throws IOException { return SOAPPartImpl.this.getContentAsStream(); }
      };
    return new DataHandler(dataSource);
  }
  
  public SOAPDocumentImpl getDocument() {
    handleNewSource();
    return this.document;
  }
  
  public SOAPPartImpl getSOAPPart() { return this; }
  
  public DocumentType getDoctype() { return this.document.getDoctype(); }
  
  public DOMImplementation getImplementation() { return this.document.getImplementation(); }
  
  public Element getDocumentElement() {
    try {
      getEnvelope();
    } catch (SOAPException sOAPException) {}
    return this.document.getDocumentElement();
  }
  
  protected void doGetDocumentElement() {
    handleNewSource();
    try {
      lookForEnvelope();
    } catch (SOAPException sOAPException) {}
  }
  
  public Element createElement(String paramString) throws DOMException { return this.document.createElement(paramString); }
  
  public DocumentFragment createDocumentFragment() { return this.document.createDocumentFragment(); }
  
  public Text createTextNode(String paramString) { return this.document.createTextNode(paramString); }
  
  public Comment createComment(String paramString) { return this.document.createComment(paramString); }
  
  public CDATASection createCDATASection(String paramString) throws DOMException { return this.document.createCDATASection(paramString); }
  
  public ProcessingInstruction createProcessingInstruction(String paramString1, String paramString2) throws DOMException { return this.document.createProcessingInstruction(paramString1, paramString2); }
  
  public Attr createAttribute(String paramString) throws DOMException { return this.document.createAttribute(paramString); }
  
  public EntityReference createEntityReference(String paramString) throws DOMException { return this.document.createEntityReference(paramString); }
  
  public NodeList getElementsByTagName(String paramString) {
    handleNewSource();
    return this.document.getElementsByTagName(paramString);
  }
  
  public Node importNode(Node paramNode, boolean paramBoolean) throws DOMException {
    handleNewSource();
    return this.document.importNode(paramNode, paramBoolean);
  }
  
  public Element createElementNS(String paramString1, String paramString2) throws DOMException { return this.document.createElementNS(paramString1, paramString2); }
  
  public Attr createAttributeNS(String paramString1, String paramString2) throws DOMException { return this.document.createAttributeNS(paramString1, paramString2); }
  
  public NodeList getElementsByTagNameNS(String paramString1, String paramString2) {
    handleNewSource();
    return this.document.getElementsByTagNameNS(paramString1, paramString2);
  }
  
  public Element getElementById(String paramString) throws DOMException {
    handleNewSource();
    return this.document.getElementById(paramString);
  }
  
  public Node appendChild(Node paramNode) throws DOMException {
    handleNewSource();
    return this.document.appendChild(paramNode);
  }
  
  public Node cloneNode(boolean paramBoolean) {
    handleNewSource();
    return this.document.cloneNode(paramBoolean);
  }
  
  protected SOAPPartImpl doCloneNode() {
    handleNewSource();
    SOAPPartImpl sOAPPartImpl = duplicateType();
    sOAPPartImpl.headers = MimeHeadersUtil.copy(this.headers);
    sOAPPartImpl.source = this.source;
    return sOAPPartImpl;
  }
  
  public NamedNodeMap getAttributes() { return this.document.getAttributes(); }
  
  public NodeList getChildNodes() {
    handleNewSource();
    return this.document.getChildNodes();
  }
  
  public Node getFirstChild() {
    handleNewSource();
    return this.document.getFirstChild();
  }
  
  public Node getLastChild() {
    handleNewSource();
    return this.document.getLastChild();
  }
  
  public String getLocalName() { return this.document.getLocalName(); }
  
  public String getNamespaceURI() { return this.document.getNamespaceURI(); }
  
  public Node getNextSibling() {
    handleNewSource();
    return this.document.getNextSibling();
  }
  
  public String getNodeName() { return this.document.getNodeName(); }
  
  public short getNodeType() { return this.document.getNodeType(); }
  
  public String getNodeValue() { return this.document.getNodeValue(); }
  
  public Document getOwnerDocument() { return this.document.getOwnerDocument(); }
  
  public Node getParentNode() { return this.document.getParentNode(); }
  
  public String getPrefix() { return this.document.getPrefix(); }
  
  public Node getPreviousSibling() { return this.document.getPreviousSibling(); }
  
  public boolean hasAttributes() { return this.document.hasAttributes(); }
  
  public boolean hasChildNodes() {
    handleNewSource();
    return this.document.hasChildNodes();
  }
  
  public Node insertBefore(Node paramNode1, Node paramNode2) throws DOMException {
    handleNewSource();
    return this.document.insertBefore(paramNode1, paramNode2);
  }
  
  public boolean isSupported(String paramString1, String paramString2) { return this.document.isSupported(paramString1, paramString2); }
  
  public void normalize() {
    handleNewSource();
    this.document.normalize();
  }
  
  public Node removeChild(Node paramNode) throws DOMException {
    handleNewSource();
    return this.document.removeChild(paramNode);
  }
  
  public Node replaceChild(Node paramNode1, Node paramNode2) throws DOMException {
    handleNewSource();
    return this.document.replaceChild(paramNode1, paramNode2);
  }
  
  public void setNodeValue(String paramString) { this.document.setNodeValue(paramString); }
  
  public void setPrefix(String paramString) { this.document.setPrefix(paramString); }
  
  private void handleNewSource() {
    if (this.sourceWasSet)
      try {
        getEnvelope();
      } catch (SOAPException sOAPException) {} 
  }
  
  protected XMLDeclarationParser lookForXmlDecl() throws SOAPException {
    if (this.source != null && this.source instanceof StreamSource) {
      Reader reader = null;
      InputStream inputStream = ((StreamSource)this.source).getInputStream();
      if (inputStream != null) {
        if (getSourceCharsetEncoding() == null) {
          reader = new InputStreamReader(inputStream);
        } else {
          try {
            reader = new InputStreamReader(inputStream, getSourceCharsetEncoding());
          } catch (UnsupportedEncodingException unsupportedEncodingException) {
            log.log(Level.SEVERE, "SAAJ0551.soap.unsupported.encoding", new Object[] { getSourceCharsetEncoding() });
            throw new SOAPExceptionImpl("Unsupported encoding " + getSourceCharsetEncoding(), unsupportedEncodingException);
          } 
        } 
      } else {
        reader = ((StreamSource)this.source).getReader();
      } 
      if (reader != null) {
        PushbackReader pushbackReader = new PushbackReader(reader, 4096);
        XMLDeclarationParser xMLDeclarationParser = new XMLDeclarationParser(pushbackReader);
        try {
          xMLDeclarationParser.parse();
        } catch (Exception exception) {
          log.log(Level.SEVERE, "SAAJ0552.soap.xml.decl.parsing.failed");
          throw new SOAPExceptionImpl("XML declaration parsing failed", exception);
        } 
        String str = xMLDeclarationParser.getXmlDeclaration();
        if (str != null && str.length() > 0)
          this.omitXmlDecl = false; 
        if (lazyContentLength)
          this.source = new StreamSource(pushbackReader); 
        return xMLDeclarationParser;
      } 
    } else if (this.source == null || this.source instanceof javax.xml.transform.dom.DOMSource) {
    
    } 
    return null;
  }
  
  public void setSourceCharsetEncoding(String paramString) { this.sourceCharsetEncoding = paramString; }
  
  public Node renameNode(Node paramNode, String paramString1, String paramString2) throws DOMException {
    handleNewSource();
    return this.document.renameNode(paramNode, paramString1, paramString2);
  }
  
  public void normalizeDocument() { this.document.normalizeDocument(); }
  
  public DOMConfiguration getDomConfig() { return this.document.getDomConfig(); }
  
  public Node adoptNode(Node paramNode) throws DOMException {
    handleNewSource();
    return this.document.adoptNode(paramNode);
  }
  
  public void setDocumentURI(String paramString) { this.document.setDocumentURI(paramString); }
  
  public String getDocumentURI() { return this.document.getDocumentURI(); }
  
  public void setStrictErrorChecking(boolean paramBoolean) { this.document.setStrictErrorChecking(paramBoolean); }
  
  public String getInputEncoding() { return this.document.getInputEncoding(); }
  
  public String getXmlEncoding() { return this.document.getXmlEncoding(); }
  
  public boolean getXmlStandalone() { return this.document.getXmlStandalone(); }
  
  public void setXmlStandalone(boolean paramBoolean) { this.document.setXmlStandalone(paramBoolean); }
  
  public String getXmlVersion() { return this.document.getXmlVersion(); }
  
  public void setXmlVersion(String paramString) { this.document.setXmlVersion(paramString); }
  
  public boolean getStrictErrorChecking() { return this.document.getStrictErrorChecking(); }
  
  public String getBaseURI() { return this.document.getBaseURI(); }
  
  public short compareDocumentPosition(Node paramNode) throws DOMException { return this.document.compareDocumentPosition(paramNode); }
  
  public String getTextContent() { return this.document.getTextContent(); }
  
  public void setTextContent(String paramString) { this.document.setTextContent(paramString); }
  
  public boolean isSameNode(Node paramNode) { return this.document.isSameNode(paramNode); }
  
  public String lookupPrefix(String paramString) { return this.document.lookupPrefix(paramString); }
  
  public boolean isDefaultNamespace(String paramString) { return this.document.isDefaultNamespace(paramString); }
  
  public String lookupNamespaceURI(String paramString) { return this.document.lookupNamespaceURI(paramString); }
  
  public boolean isEqualNode(Node paramNode) { return this.document.isEqualNode(paramNode); }
  
  public Object getFeature(String paramString1, String paramString2) { return this.document.getFeature(paramString1, paramString2); }
  
  public Object setUserData(String paramString, Object paramObject, UserDataHandler paramUserDataHandler) { return this.document.setUserData(paramString, paramObject, paramUserDataHandler); }
  
  public Object getUserData(String paramString) { return this.document.getUserData(paramString); }
  
  public void recycleNode() {}
  
  public String getValue() { return null; }
  
  public void setValue(String paramString) {
    log.severe("SAAJ0571.soappart.setValue.not.defined");
    throw new IllegalStateException("Setting value of a soap part is not defined");
  }
  
  public void setParentElement(SOAPElement paramSOAPElement) throws SOAPException {
    log.severe("SAAJ0570.soappart.parent.element.not.defined");
    throw new SOAPExceptionImpl("The parent element of a soap part is not defined");
  }
  
  public SOAPElement getParentElement() { return null; }
  
  public void detachNode() {}
  
  public String getSourceCharsetEncoding() { return this.sourceCharsetEncoding; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\SOAPPartImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */