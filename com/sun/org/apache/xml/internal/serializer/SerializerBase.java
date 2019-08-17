package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import java.io.IOException;
import java.io.Writer;
import java.util.Vector;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.Locator2;

public abstract class SerializerBase implements SerializationHandler, SerializerConstants {
  protected boolean m_needToCallStartDocument = true;
  
  protected boolean m_cdataTagOpen = false;
  
  protected AttributesImplSerializer m_attributes = new AttributesImplSerializer();
  
  protected boolean m_inEntityRef = false;
  
  protected boolean m_inExternalDTD = false;
  
  private String m_doctypeSystem;
  
  private String m_doctypePublic;
  
  boolean m_needToOutputDocTypeDecl = true;
  
  private String m_encoding = null;
  
  private boolean m_shouldNotWriteXMLHeader = false;
  
  private String m_standalone;
  
  protected boolean m_standaloneWasSpecified = false;
  
  protected boolean m_isStandalone = false;
  
  protected boolean m_doIndent = false;
  
  protected int m_indentAmount = 0;
  
  private String m_version = null;
  
  private String m_mediatype;
  
  private Transformer m_transformer;
  
  protected Vector m_cdataSectionElements = null;
  
  protected NamespaceMappings m_prefixMap;
  
  protected SerializerTrace m_tracer;
  
  protected SourceLocator m_sourceLocator;
  
  protected Writer m_writer = null;
  
  protected ElemContext m_elemContext = new ElemContext();
  
  protected char[] m_charsBuff = new char[60];
  
  protected char[] m_attrBuff = new char[30];
  
  private Locator m_locator = null;
  
  protected boolean m_needToCallSetDocumentInfo = true;
  
  protected void fireEndElem(String paramString) throws SAXException {
    if (this.m_tracer != null) {
      flushMyWriter();
      this.m_tracer.fireGenerateEvent(4, paramString, (Attributes)null);
    } 
  }
  
  protected void fireCharEvent(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.m_tracer != null) {
      flushMyWriter();
      this.m_tracer.fireGenerateEvent(5, paramArrayOfChar, paramInt1, paramInt2);
    } 
  }
  
  public void comment(String paramString) throws SAXException {
    int i = paramString.length();
    if (i > this.m_charsBuff.length)
      this.m_charsBuff = new char[i * 2 + 1]; 
    paramString.getChars(0, i, this.m_charsBuff, 0);
    comment(this.m_charsBuff, 0, i);
  }
  
  protected String patchName(String paramString) {
    int i = paramString.lastIndexOf(':');
    if (i > 0) {
      int j = paramString.indexOf(':');
      String str1 = paramString.substring(0, j);
      String str2 = paramString.substring(i + 1);
      String str3 = this.m_prefixMap.lookupNamespace(str1);
      if (str3 != null && str3.length() == 0)
        return str2; 
      if (j != i)
        return str1 + ':' + str2; 
    } 
    return paramString;
  }
  
  protected static String getLocalName(String paramString) {
    int i = paramString.lastIndexOf(':');
    return (i > 0) ? paramString.substring(i + 1) : paramString;
  }
  
  public void setDocumentLocator(Locator paramLocator) { this.m_locator = paramLocator; }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean) throws SAXException {
    if (this.m_elemContext.m_startTagOpen)
      addAttributeAlways(paramString1, paramString2, paramString3, paramString4, paramString5, paramBoolean); 
  }
  
  public boolean addAttributeAlways(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean) {
    int i;
    boolean bool;
    if (paramString2 == null || paramString1 == null || paramString1.length() == 0) {
      i = this.m_attributes.getIndex(paramString3);
    } else {
      i = this.m_attributes.getIndex(paramString1, paramString2);
    } 
    if (i >= 0) {
      this.m_attributes.setValue(i, paramString5);
      bool = false;
    } else {
      this.m_attributes.addAttribute(paramString1, paramString2, paramString3, paramString4, paramString5);
      bool = true;
    } 
    return bool;
  }
  
  public void addAttribute(String paramString1, String paramString2) {
    if (this.m_elemContext.m_startTagOpen) {
      String str1 = patchName(paramString1);
      String str2 = getLocalName(str1);
      String str3 = getNamespaceURI(str1, false);
      addAttributeAlways(str3, str2, str1, "CDATA", paramString2, false);
    } 
  }
  
  public void addXSLAttribute(String paramString1, String paramString2, String paramString3) {
    if (this.m_elemContext.m_startTagOpen) {
      String str1 = patchName(paramString1);
      String str2 = getLocalName(str1);
      addAttributeAlways(paramString3, str2, str1, "CDATA", paramString2, true);
    } 
  }
  
  public void addAttributes(Attributes paramAttributes) throws SAXException {
    int i = paramAttributes.getLength();
    for (byte b = 0; b < i; b++) {
      String str = paramAttributes.getURI(b);
      if (null == str)
        str = ""; 
      addAttributeAlways(str, paramAttributes.getLocalName(b), paramAttributes.getQName(b), paramAttributes.getType(b), paramAttributes.getValue(b), false);
    } 
  }
  
  public ContentHandler asContentHandler() throws IOException { return this; }
  
  public void endEntity(String paramString) throws SAXException {
    if (paramString.equals("[dtd]"))
      this.m_inExternalDTD = false; 
    this.m_inEntityRef = false;
    if (this.m_tracer != null)
      fireEndEntity(paramString); 
  }
  
  public void close() {}
  
  protected void initCDATA() {}
  
  public String getEncoding() { return this.m_encoding; }
  
  public void setEncoding(String paramString) throws SAXException { this.m_encoding = paramString; }
  
  public void setOmitXMLDeclaration(boolean paramBoolean) { this.m_shouldNotWriteXMLHeader = paramBoolean; }
  
  public boolean getOmitXMLDeclaration() { return this.m_shouldNotWriteXMLHeader; }
  
  public String getDoctypePublic() { return this.m_doctypePublic; }
  
  public void setDoctypePublic(String paramString) throws SAXException { this.m_doctypePublic = paramString; }
  
  public String getDoctypeSystem() { return this.m_doctypeSystem; }
  
  public void setDoctypeSystem(String paramString) throws SAXException { this.m_doctypeSystem = paramString; }
  
  public void setDoctype(String paramString1, String paramString2) {
    this.m_doctypeSystem = paramString1;
    this.m_doctypePublic = paramString2;
  }
  
  public void setStandalone(String paramString) throws SAXException {
    if (paramString != null) {
      this.m_standaloneWasSpecified = true;
      setStandaloneInternal(paramString);
    } 
  }
  
  protected void setStandaloneInternal(String paramString) throws SAXException {
    if ("yes".equals(paramString)) {
      this.m_standalone = "yes";
    } else {
      this.m_standalone = "no";
    } 
  }
  
  public String getStandalone() { return this.m_standalone; }
  
  public boolean getIndent() { return this.m_doIndent; }
  
  public String getMediaType() { return this.m_mediatype; }
  
  public String getVersion() { return this.m_version; }
  
  public void setVersion(String paramString) throws SAXException { this.m_version = paramString; }
  
  public void setMediaType(String paramString) throws SAXException { this.m_mediatype = paramString; }
  
  public int getIndentAmount() { return this.m_indentAmount; }
  
  public void setIndentAmount(int paramInt) { this.m_indentAmount = paramInt; }
  
  public void setIndent(boolean paramBoolean) { this.m_doIndent = paramBoolean; }
  
  public void setIsStandalone(boolean paramBoolean) { this.m_isStandalone = paramBoolean; }
  
  public void namespaceAfterStartElement(String paramString1, String paramString2) {}
  
  public DOMSerializer asDOMSerializer() throws IOException { return this; }
  
  protected boolean isCdataSection() {
    boolean bool = false;
    if (null != this.m_cdataSectionElements) {
      if (this.m_elemContext.m_elementLocalName == null)
        this.m_elemContext.m_elementLocalName = getLocalName(this.m_elemContext.m_elementName); 
      if (this.m_elemContext.m_elementURI == null) {
        String str = getPrefixPart(this.m_elemContext.m_elementName);
        if (str != null)
          this.m_elemContext.m_elementURI = this.m_prefixMap.lookupNamespace(str); 
      } 
      if (null != this.m_elemContext.m_elementURI && this.m_elemContext.m_elementURI.length() == 0)
        this.m_elemContext.m_elementURI = null; 
      int i = this.m_cdataSectionElements.size();
      for (byte b = 0; b < i; b += 2) {
        String str1 = (String)this.m_cdataSectionElements.elementAt(b);
        String str2 = (String)this.m_cdataSectionElements.elementAt(b + 1);
        if (str2.equals(this.m_elemContext.m_elementLocalName) && subPartMatch(this.m_elemContext.m_elementURI, str1)) {
          bool = true;
          break;
        } 
      } 
    } 
    return bool;
  }
  
  private static final boolean subPartMatch(String paramString1, String paramString2) { return (paramString1 == paramString2 || (null != paramString1 && paramString1.equals(paramString2))); }
  
  protected static final String getPrefixPart(String paramString) {
    int i = paramString.indexOf(':');
    return (i > 0) ? paramString.substring(0, i) : null;
  }
  
  public NamespaceMappings getNamespaceMappings() { return this.m_prefixMap; }
  
  public String getPrefix(String paramString) { return this.m_prefixMap.lookupPrefix(paramString); }
  
  public String getNamespaceURI(String paramString, boolean paramBoolean) {
    String str1 = "";
    int i = paramString.lastIndexOf(':');
    String str2 = (i > 0) ? paramString.substring(0, i) : "";
    if ((!"".equals(str2) || paramBoolean) && this.m_prefixMap != null) {
      str1 = this.m_prefixMap.lookupNamespace(str2);
      if (str1 == null && !str2.equals("xmlns"))
        throw new RuntimeException(Utils.messages.createMessage("ER_NAMESPACE_PREFIX", new Object[] { paramString.substring(0, i) })); 
    } 
    return str1;
  }
  
  public String getNamespaceURIFromPrefix(String paramString) {
    String str = null;
    if (this.m_prefixMap != null)
      str = this.m_prefixMap.lookupNamespace(paramString); 
    return str;
  }
  
  public void entityReference(String paramString) throws SAXException {
    flushPending();
    startEntity(paramString);
    endEntity(paramString);
    if (this.m_tracer != null)
      fireEntityReference(paramString); 
  }
  
  public void setTransformer(Transformer paramTransformer) {
    this.m_transformer = paramTransformer;
    if (this.m_transformer instanceof SerializerTrace && ((SerializerTrace)this.m_transformer).hasTraceListeners()) {
      this.m_tracer = (SerializerTrace)this.m_transformer;
    } else {
      this.m_tracer = null;
    } 
  }
  
  public Transformer getTransformer() { return this.m_transformer; }
  
  public void characters(Node paramNode) throws SAXException {
    flushPending();
    String str = paramNode.getNodeValue();
    if (str != null) {
      int i = str.length();
      if (i > this.m_charsBuff.length)
        this.m_charsBuff = new char[i * 2 + 1]; 
      str.getChars(0, i, this.m_charsBuff, 0);
      characters(this.m_charsBuff, 0, i);
    } 
  }
  
  public void error(SAXParseException paramSAXParseException) throws SAXException {}
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException { this.m_elemContext.m_startTagOpen = false; }
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException {}
  
  protected void fireStartEntity(String paramString) throws SAXException {
    if (this.m_tracer != null) {
      flushMyWriter();
      this.m_tracer.fireGenerateEvent(9, paramString);
    } 
  }
  
  private void flushMyWriter() {
    if (this.m_writer != null)
      try {
        this.m_writer.flush();
      } catch (IOException iOException) {} 
  }
  
  protected void fireCDATAEvent(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.m_tracer != null) {
      flushMyWriter();
      this.m_tracer.fireGenerateEvent(10, paramArrayOfChar, paramInt1, paramInt2);
    } 
  }
  
  protected void fireCommentEvent(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.m_tracer != null) {
      flushMyWriter();
      this.m_tracer.fireGenerateEvent(8, new String(paramArrayOfChar, paramInt1, paramInt2));
    } 
  }
  
  public void fireEndEntity(String paramString) throws SAXException {
    if (this.m_tracer != null)
      flushMyWriter(); 
  }
  
  protected void fireStartDoc() {
    if (this.m_tracer != null) {
      flushMyWriter();
      this.m_tracer.fireGenerateEvent(1);
    } 
  }
  
  protected void fireEndDoc() {
    if (this.m_tracer != null) {
      flushMyWriter();
      this.m_tracer.fireGenerateEvent(2);
    } 
  }
  
  protected void fireStartElem(String paramString) throws SAXException {
    if (this.m_tracer != null) {
      flushMyWriter();
      this.m_tracer.fireGenerateEvent(3, paramString, this.m_attributes);
    } 
  }
  
  protected void fireEscapingEvent(String paramString1, String paramString2) {
    if (this.m_tracer != null) {
      flushMyWriter();
      this.m_tracer.fireGenerateEvent(7, paramString1, paramString2);
    } 
  }
  
  protected void fireEntityReference(String paramString) throws SAXException {
    if (this.m_tracer != null) {
      flushMyWriter();
      this.m_tracer.fireGenerateEvent(9, paramString, (Attributes)null);
    } 
  }
  
  public void startDocument() {
    startDocumentInternal();
    this.m_needToCallStartDocument = false;
  }
  
  protected void startDocumentInternal() {
    if (this.m_tracer != null)
      fireStartDoc(); 
  }
  
  protected void setDocumentInfo() {
    if (this.m_locator == null)
      return; 
    try {
      String str = ((Locator2)this.m_locator).getXMLVersion();
      if (str != null)
        setVersion(str); 
    } catch (ClassCastException classCastException) {}
  }
  
  public void setSourceLocator(SourceLocator paramSourceLocator) { this.m_sourceLocator = paramSourceLocator; }
  
  public void setNamespaceMappings(NamespaceMappings paramNamespaceMappings) { this.m_prefixMap = paramNamespaceMappings; }
  
  public boolean reset() {
    resetSerializerBase();
    return true;
  }
  
  private void resetSerializerBase() {
    this.m_attributes.clear();
    this.m_cdataSectionElements = null;
    this.m_elemContext = new ElemContext();
    this.m_doctypePublic = null;
    this.m_doctypeSystem = null;
    this.m_doIndent = false;
    this.m_encoding = null;
    this.m_indentAmount = 0;
    this.m_inEntityRef = false;
    this.m_inExternalDTD = false;
    this.m_mediatype = null;
    this.m_needToCallStartDocument = true;
    this.m_needToOutputDocTypeDecl = false;
    if (this.m_prefixMap != null)
      this.m_prefixMap.reset(); 
    this.m_shouldNotWriteXMLHeader = false;
    this.m_sourceLocator = null;
    this.m_standalone = null;
    this.m_standaloneWasSpecified = false;
    this.m_tracer = null;
    this.m_transformer = null;
    this.m_version = null;
  }
  
  final boolean inTemporaryOutputState() { return (getEncoding() == null); }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws SAXException {
    if (this.m_elemContext.m_startTagOpen)
      addAttributeAlways(paramString1, paramString2, paramString3, paramString4, paramString5, false); 
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3) {}
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4) throws SAXException {}
  
  public void setDTDEntityExpansion(boolean paramBoolean) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\SerializerBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */