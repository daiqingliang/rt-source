package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;
import java.util.Vector;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public final class ToUnknownStream extends SerializerBase {
  private SerializationHandler m_handler = new ToXMLStream();
  
  private static final String EMPTYSTRING = "";
  
  private boolean m_wrapped_handler_not_initialized = false;
  
  private String m_firstElementPrefix;
  
  private String m_firstElementName;
  
  private String m_firstElementURI;
  
  private String m_firstElementLocalName = null;
  
  private boolean m_firstTagNotEmitted = true;
  
  private Vector m_namespaceURI = null;
  
  private Vector m_namespacePrefix = null;
  
  private boolean m_needToCallStartDocument = false;
  
  private boolean m_setVersion_called = false;
  
  private boolean m_setDoctypeSystem_called = false;
  
  private boolean m_setDoctypePublic_called = false;
  
  private boolean m_setMediaType_called = false;
  
  public ContentHandler asContentHandler() throws IOException { return this; }
  
  public void close() { this.m_handler.close(); }
  
  public Properties getOutputFormat() { return this.m_handler.getOutputFormat(); }
  
  public OutputStream getOutputStream() { return this.m_handler.getOutputStream(); }
  
  public Writer getWriter() { return this.m_handler.getWriter(); }
  
  public boolean reset() { return this.m_handler.reset(); }
  
  public void serialize(Node paramNode) throws IOException {
    if (this.m_firstTagNotEmitted)
      flush(); 
    this.m_handler.serialize(paramNode);
  }
  
  public boolean setEscaping(boolean paramBoolean) throws SAXException { return this.m_handler.setEscaping(paramBoolean); }
  
  public void setOutputFormat(Properties paramProperties) { this.m_handler.setOutputFormat(paramProperties); }
  
  public void setOutputStream(OutputStream paramOutputStream) { this.m_handler.setOutputStream(paramOutputStream); }
  
  public void setWriter(Writer paramWriter) { this.m_handler.setWriter(paramWriter); }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws SAXException { addAttribute(paramString1, paramString2, paramString3, paramString4, paramString5, false); }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean) throws SAXException {
    if (this.m_firstTagNotEmitted)
      flush(); 
    this.m_handler.addAttribute(paramString1, paramString2, paramString3, paramString4, paramString5, paramBoolean);
  }
  
  public void addAttribute(String paramString1, String paramString2) {
    if (this.m_firstTagNotEmitted)
      flush(); 
    this.m_handler.addAttribute(paramString1, paramString2);
  }
  
  public void addUniqueAttribute(String paramString1, String paramString2, int paramInt) throws SAXException {
    if (this.m_firstTagNotEmitted)
      flush(); 
    this.m_handler.addUniqueAttribute(paramString1, paramString2, paramInt);
  }
  
  public void characters(String paramString) throws SAXException {
    int i = paramString.length();
    if (i > this.m_charsBuff.length)
      this.m_charsBuff = new char[i * 2 + 1]; 
    paramString.getChars(0, i, this.m_charsBuff, 0);
    characters(this.m_charsBuff, 0, i);
  }
  
  public void endElement(String paramString) throws SAXException {
    if (this.m_firstTagNotEmitted)
      flush(); 
    this.m_handler.endElement(paramString);
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) { startPrefixMapping(paramString1, paramString2, true); }
  
  public void namespaceAfterStartElement(String paramString1, String paramString2) {
    if (this.m_firstTagNotEmitted && this.m_firstElementURI == null && this.m_firstElementName != null) {
      String str = getPrefixPart(this.m_firstElementName);
      if (str == null && "".equals(paramString1))
        this.m_firstElementURI = paramString2; 
    } 
    startPrefixMapping(paramString1, paramString2, false);
  }
  
  public boolean startPrefixMapping(String paramString1, String paramString2, boolean paramBoolean) throws SAXException {
    boolean bool = false;
    if (this.m_firstTagNotEmitted) {
      if (this.m_firstElementName != null && paramBoolean) {
        flush();
        bool = this.m_handler.startPrefixMapping(paramString1, paramString2, paramBoolean);
      } else {
        if (this.m_namespacePrefix == null) {
          this.m_namespacePrefix = new Vector();
          this.m_namespaceURI = new Vector();
        } 
        this.m_namespacePrefix.addElement(paramString1);
        this.m_namespaceURI.addElement(paramString2);
        if (this.m_firstElementURI == null && paramString1.equals(this.m_firstElementPrefix))
          this.m_firstElementURI = paramString2; 
      } 
    } else {
      bool = this.m_handler.startPrefixMapping(paramString1, paramString2, paramBoolean);
    } 
    return bool;
  }
  
  public void setVersion(String paramString) throws SAXException {
    this.m_handler.setVersion(paramString);
    this.m_setVersion_called = true;
  }
  
  public void startDocument() { this.m_needToCallStartDocument = true; }
  
  public void startElement(String paramString) throws SAXException { startElement(null, null, paramString, null); }
  
  public void startElement(String paramString1, String paramString2, String paramString3) throws SAXException { startElement(paramString1, paramString2, paramString3, null); }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    if (this.m_needToCallSetDocumentInfo) {
      setDocumentInfo();
      this.m_needToCallSetDocumentInfo = false;
    } 
    if (this.m_firstTagNotEmitted) {
      if (this.m_firstElementName != null) {
        flush();
        this.m_handler.startElement(paramString1, paramString2, paramString3, paramAttributes);
      } else {
        this.m_wrapped_handler_not_initialized = true;
        this.m_firstElementName = paramString3;
        this.m_firstElementPrefix = getPrefixPartUnknown(paramString3);
        this.m_firstElementURI = paramString1;
        this.m_firstElementLocalName = paramString2;
        if (this.m_tracer != null)
          firePseudoElement(paramString3); 
        if (paramAttributes != null)
          super.addAttributes(paramAttributes); 
        if (paramAttributes != null)
          flush(); 
      } 
    } else {
      this.m_handler.startElement(paramString1, paramString2, paramString3, paramAttributes);
    } 
  }
  
  public void comment(String paramString) throws SAXException {
    if (this.m_firstTagNotEmitted && this.m_firstElementName != null) {
      emitFirstTag();
    } else if (this.m_needToCallStartDocument) {
      this.m_handler.startDocument();
      this.m_needToCallStartDocument = false;
    } 
    this.m_handler.comment(paramString);
  }
  
  public String getDoctypePublic() { return this.m_handler.getDoctypePublic(); }
  
  public String getDoctypeSystem() { return this.m_handler.getDoctypeSystem(); }
  
  public String getEncoding() { return this.m_handler.getEncoding(); }
  
  public boolean getIndent() { return this.m_handler.getIndent(); }
  
  public int getIndentAmount() { return this.m_handler.getIndentAmount(); }
  
  public String getMediaType() { return this.m_handler.getMediaType(); }
  
  public boolean getOmitXMLDeclaration() { return this.m_handler.getOmitXMLDeclaration(); }
  
  public String getStandalone() { return this.m_handler.getStandalone(); }
  
  public String getVersion() { return this.m_handler.getVersion(); }
  
  public void setDoctype(String paramString1, String paramString2) {
    this.m_handler.setDoctypePublic(paramString2);
    this.m_handler.setDoctypeSystem(paramString1);
  }
  
  public void setDoctypePublic(String paramString) throws SAXException {
    this.m_handler.setDoctypePublic(paramString);
    this.m_setDoctypePublic_called = true;
  }
  
  public void setDoctypeSystem(String paramString) throws SAXException {
    this.m_handler.setDoctypeSystem(paramString);
    this.m_setDoctypeSystem_called = true;
  }
  
  public void setEncoding(String paramString) throws SAXException { this.m_handler.setEncoding(paramString); }
  
  public void setIndent(boolean paramBoolean) { this.m_handler.setIndent(paramBoolean); }
  
  public void setIndentAmount(int paramInt) { this.m_handler.setIndentAmount(paramInt); }
  
  public void setMediaType(String paramString) throws SAXException {
    this.m_handler.setMediaType(paramString);
    this.m_setMediaType_called = true;
  }
  
  public void setOmitXMLDeclaration(boolean paramBoolean) { this.m_handler.setOmitXMLDeclaration(paramBoolean); }
  
  public void setStandalone(String paramString) throws SAXException { this.m_handler.setStandalone(paramString); }
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws SAXException { this.m_handler.attributeDecl(paramString1, paramString2, paramString3, paramString4, paramString5); }
  
  public void elementDecl(String paramString1, String paramString2) {
    if (this.m_firstTagNotEmitted)
      emitFirstTag(); 
    this.m_handler.elementDecl(paramString1, paramString2);
  }
  
  public void externalEntityDecl(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (this.m_firstTagNotEmitted)
      flush(); 
    this.m_handler.externalEntityDecl(paramString1, paramString2, paramString3);
  }
  
  public void internalEntityDecl(String paramString1, String paramString2) {
    if (this.m_firstTagNotEmitted)
      flush(); 
    this.m_handler.internalEntityDecl(paramString1, paramString2);
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.m_firstTagNotEmitted)
      flush(); 
    this.m_handler.characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void endDocument() {
    if (this.m_firstTagNotEmitted)
      flush(); 
    this.m_handler.endDocument();
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (this.m_firstTagNotEmitted) {
      flush();
      if (paramString1 == null && this.m_firstElementURI != null)
        paramString1 = this.m_firstElementURI; 
      if (paramString2 == null && this.m_firstElementLocalName != null)
        paramString2 = this.m_firstElementLocalName; 
    } 
    this.m_handler.endElement(paramString1, paramString2, paramString3);
  }
  
  public void endPrefixMapping(String paramString) throws SAXException { this.m_handler.endPrefixMapping(paramString); }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.m_firstTagNotEmitted)
      flush(); 
    this.m_handler.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void processingInstruction(String paramString1, String paramString2) {
    if (this.m_firstTagNotEmitted)
      flush(); 
    this.m_handler.processingInstruction(paramString1, paramString2);
  }
  
  public void setDocumentLocator(Locator paramLocator) {
    super.setDocumentLocator(paramLocator);
    this.m_handler.setDocumentLocator(paramLocator);
  }
  
  public void skippedEntity(String paramString) throws SAXException { this.m_handler.skippedEntity(paramString); }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.m_firstTagNotEmitted)
      flush(); 
    this.m_handler.comment(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void endCDATA() { this.m_handler.endCDATA(); }
  
  public void endDTD() { this.m_handler.endDTD(); }
  
  public void endEntity(String paramString) throws SAXException {
    if (this.m_firstTagNotEmitted)
      emitFirstTag(); 
    this.m_handler.endEntity(paramString);
  }
  
  public void startCDATA() { this.m_handler.startCDATA(); }
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException { this.m_handler.startDTD(paramString1, paramString2, paramString3); }
  
  public void startEntity(String paramString) throws SAXException { this.m_handler.startEntity(paramString); }
  
  private void initStreamOutput() {
    boolean bool = isFirstElemHTML();
    if (bool) {
      SerializationHandler serializationHandler = this.m_handler;
      Properties properties = OutputPropertiesFactory.getDefaultMethodProperties("html");
      Serializer serializer = SerializerFactory.getSerializer(properties);
      this.m_handler = (SerializationHandler)serializer;
      Writer writer = serializationHandler.getWriter();
      if (null != writer) {
        this.m_handler.setWriter(writer);
      } else {
        OutputStream outputStream = serializationHandler.getOutputStream();
        if (null != outputStream)
          this.m_handler.setOutputStream(outputStream); 
      } 
      this.m_handler.setVersion(serializationHandler.getVersion());
      this.m_handler.setDoctypeSystem(serializationHandler.getDoctypeSystem());
      this.m_handler.setDoctypePublic(serializationHandler.getDoctypePublic());
      this.m_handler.setMediaType(serializationHandler.getMediaType());
      this.m_handler.setTransformer(serializationHandler.getTransformer());
    } 
    if (this.m_needToCallStartDocument) {
      this.m_handler.startDocument();
      this.m_needToCallStartDocument = false;
    } 
    this.m_wrapped_handler_not_initialized = false;
  }
  
  private void emitFirstTag() {
    if (this.m_firstElementName != null) {
      if (this.m_wrapped_handler_not_initialized) {
        initStreamOutput();
        this.m_wrapped_handler_not_initialized = false;
      } 
      this.m_handler.startElement(this.m_firstElementURI, null, this.m_firstElementName, this.m_attributes);
      this.m_attributes = null;
      if (this.m_namespacePrefix != null) {
        int i = this.m_namespacePrefix.size();
        for (byte b = 0; b < i; b++) {
          String str1 = (String)this.m_namespacePrefix.elementAt(b);
          String str2 = (String)this.m_namespaceURI.elementAt(b);
          this.m_handler.startPrefixMapping(str1, str2, false);
        } 
        this.m_namespacePrefix = null;
        this.m_namespaceURI = null;
      } 
      this.m_firstTagNotEmitted = false;
    } 
  }
  
  private String getLocalNameUnknown(String paramString) {
    int i = paramString.lastIndexOf(':');
    if (i >= 0)
      paramString = paramString.substring(i + 1); 
    i = paramString.lastIndexOf('@');
    if (i >= 0)
      paramString = paramString.substring(i + 1); 
    return paramString;
  }
  
  private String getPrefixPartUnknown(String paramString) {
    int i = paramString.indexOf(':');
    return (i > 0) ? paramString.substring(0, i) : "";
  }
  
  private boolean isFirstElemHTML() {
    boolean bool = getLocalNameUnknown(this.m_firstElementName).equalsIgnoreCase("html");
    if (bool && this.m_firstElementURI != null && !"".equals(this.m_firstElementURI))
      bool = false; 
    if (bool && this.m_namespacePrefix != null) {
      int i = this.m_namespacePrefix.size();
      for (byte b = 0; b < i; b++) {
        String str1 = (String)this.m_namespacePrefix.elementAt(b);
        String str2 = (String)this.m_namespaceURI.elementAt(b);
        if (this.m_firstElementPrefix != null && this.m_firstElementPrefix.equals(str1) && !"".equals(str2)) {
          bool = false;
          break;
        } 
      } 
    } 
    return bool;
  }
  
  public DOMSerializer asDOMSerializer() throws IOException { return this.m_handler.asDOMSerializer(); }
  
  public void setCdataSectionElements(Vector paramVector) { this.m_handler.setCdataSectionElements(paramVector); }
  
  public void addAttributes(Attributes paramAttributes) throws SAXException { this.m_handler.addAttributes(paramAttributes); }
  
  public NamespaceMappings getNamespaceMappings() {
    NamespaceMappings namespaceMappings = null;
    if (this.m_handler != null)
      namespaceMappings = this.m_handler.getNamespaceMappings(); 
    return namespaceMappings;
  }
  
  public void flushPending() {
    flush();
    this.m_handler.flushPending();
  }
  
  private void flush() {
    try {
      if (this.m_firstTagNotEmitted)
        emitFirstTag(); 
      if (this.m_needToCallStartDocument) {
        this.m_handler.startDocument();
        this.m_needToCallStartDocument = false;
      } 
    } catch (SAXException sAXException) {
      throw new RuntimeException(sAXException.toString());
    } 
  }
  
  public String getPrefix(String paramString) { return this.m_handler.getPrefix(paramString); }
  
  public void entityReference(String paramString) throws SAXException { this.m_handler.entityReference(paramString); }
  
  public String getNamespaceURI(String paramString, boolean paramBoolean) { return this.m_handler.getNamespaceURI(paramString, paramBoolean); }
  
  public String getNamespaceURIFromPrefix(String paramString) { return this.m_handler.getNamespaceURIFromPrefix(paramString); }
  
  public void setTransformer(Transformer paramTransformer) {
    this.m_handler.setTransformer(paramTransformer);
    if (paramTransformer instanceof SerializerTrace && ((SerializerTrace)paramTransformer).hasTraceListeners()) {
      this.m_tracer = (SerializerTrace)paramTransformer;
    } else {
      this.m_tracer = null;
    } 
  }
  
  public Transformer getTransformer() { return this.m_handler.getTransformer(); }
  
  public void setContentHandler(ContentHandler paramContentHandler) { this.m_handler.setContentHandler(paramContentHandler); }
  
  public void setSourceLocator(SourceLocator paramSourceLocator) { this.m_handler.setSourceLocator(paramSourceLocator); }
  
  protected void firePseudoElement(String paramString) throws SAXException {
    if (this.m_tracer != null) {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append('<');
      stringBuffer.append(paramString);
      char[] arrayOfChar = stringBuffer.toString().toCharArray();
      this.m_tracer.fireGenerateEvent(11, arrayOfChar, 0, arrayOfChar.length);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\ToUnknownStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */