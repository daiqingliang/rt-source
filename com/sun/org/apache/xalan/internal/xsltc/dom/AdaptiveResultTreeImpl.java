package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.utils.XMLString;
import java.util.Map;
import javax.xml.transform.SourceLocator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class AdaptiveResultTreeImpl extends SimpleResultTreeImpl {
  private static int _documentURIIndex = 0;
  
  private static final String EMPTY_STRING = "".intern();
  
  private SAXImpl _dom;
  
  private DTMWSFilter _wsfilter;
  
  private int _initSize;
  
  private boolean _buildIdIndex;
  
  private final AttributesImpl _attributes = new AttributesImpl();
  
  private String _openElementName;
  
  public AdaptiveResultTreeImpl(XSLTCDTMManager paramXSLTCDTMManager, int paramInt1, DTMWSFilter paramDTMWSFilter, int paramInt2, boolean paramBoolean) {
    super(paramXSLTCDTMManager, paramInt1);
    this._wsfilter = paramDTMWSFilter;
    this._initSize = paramInt2;
    this._buildIdIndex = paramBoolean;
  }
  
  public DOM getNestedDOM() { return this._dom; }
  
  public int getDocument() { return (this._dom != null) ? this._dom.getDocument() : super.getDocument(); }
  
  public String getStringValue() { return (this._dom != null) ? this._dom.getStringValue() : super.getStringValue(); }
  
  public DTMAxisIterator getIterator() { return (this._dom != null) ? this._dom.getIterator() : super.getIterator(); }
  
  public DTMAxisIterator getChildren(int paramInt) { return (this._dom != null) ? this._dom.getChildren(paramInt) : super.getChildren(paramInt); }
  
  public DTMAxisIterator getTypedChildren(int paramInt) { return (this._dom != null) ? this._dom.getTypedChildren(paramInt) : super.getTypedChildren(paramInt); }
  
  public DTMAxisIterator getAxisIterator(int paramInt) { return (this._dom != null) ? this._dom.getAxisIterator(paramInt) : super.getAxisIterator(paramInt); }
  
  public DTMAxisIterator getTypedAxisIterator(int paramInt1, int paramInt2) { return (this._dom != null) ? this._dom.getTypedAxisIterator(paramInt1, paramInt2) : super.getTypedAxisIterator(paramInt1, paramInt2); }
  
  public DTMAxisIterator getNthDescendant(int paramInt1, int paramInt2, boolean paramBoolean) { return (this._dom != null) ? this._dom.getNthDescendant(paramInt1, paramInt2, paramBoolean) : super.getNthDescendant(paramInt1, paramInt2, paramBoolean); }
  
  public DTMAxisIterator getNamespaceAxisIterator(int paramInt1, int paramInt2) { return (this._dom != null) ? this._dom.getNamespaceAxisIterator(paramInt1, paramInt2) : super.getNamespaceAxisIterator(paramInt1, paramInt2); }
  
  public DTMAxisIterator getNodeValueIterator(DTMAxisIterator paramDTMAxisIterator, int paramInt, String paramString, boolean paramBoolean) { return (this._dom != null) ? this._dom.getNodeValueIterator(paramDTMAxisIterator, paramInt, paramString, paramBoolean) : super.getNodeValueIterator(paramDTMAxisIterator, paramInt, paramString, paramBoolean); }
  
  public DTMAxisIterator orderNodes(DTMAxisIterator paramDTMAxisIterator, int paramInt) { return (this._dom != null) ? this._dom.orderNodes(paramDTMAxisIterator, paramInt) : super.orderNodes(paramDTMAxisIterator, paramInt); }
  
  public String getNodeName(int paramInt) { return (this._dom != null) ? this._dom.getNodeName(paramInt) : super.getNodeName(paramInt); }
  
  public String getNodeNameX(int paramInt) { return (this._dom != null) ? this._dom.getNodeNameX(paramInt) : super.getNodeNameX(paramInt); }
  
  public String getNamespaceName(int paramInt) { return (this._dom != null) ? this._dom.getNamespaceName(paramInt) : super.getNamespaceName(paramInt); }
  
  public int getExpandedTypeID(int paramInt) { return (this._dom != null) ? this._dom.getExpandedTypeID(paramInt) : super.getExpandedTypeID(paramInt); }
  
  public int getNamespaceType(int paramInt) { return (this._dom != null) ? this._dom.getNamespaceType(paramInt) : super.getNamespaceType(paramInt); }
  
  public int getParent(int paramInt) { return (this._dom != null) ? this._dom.getParent(paramInt) : super.getParent(paramInt); }
  
  public int getAttributeNode(int paramInt1, int paramInt2) { return (this._dom != null) ? this._dom.getAttributeNode(paramInt1, paramInt2) : super.getAttributeNode(paramInt1, paramInt2); }
  
  public String getStringValueX(int paramInt) { return (this._dom != null) ? this._dom.getStringValueX(paramInt) : super.getStringValueX(paramInt); }
  
  public void copy(int paramInt, SerializationHandler paramSerializationHandler) throws TransletException {
    if (this._dom != null) {
      this._dom.copy(paramInt, paramSerializationHandler);
    } else {
      super.copy(paramInt, paramSerializationHandler);
    } 
  }
  
  public void copy(DTMAxisIterator paramDTMAxisIterator, SerializationHandler paramSerializationHandler) throws TransletException {
    if (this._dom != null) {
      this._dom.copy(paramDTMAxisIterator, paramSerializationHandler);
    } else {
      super.copy(paramDTMAxisIterator, paramSerializationHandler);
    } 
  }
  
  public String shallowCopy(int paramInt, SerializationHandler paramSerializationHandler) throws TransletException { return (this._dom != null) ? this._dom.shallowCopy(paramInt, paramSerializationHandler) : super.shallowCopy(paramInt, paramSerializationHandler); }
  
  public boolean lessThan(int paramInt1, int paramInt2) { return (this._dom != null) ? this._dom.lessThan(paramInt1, paramInt2) : super.lessThan(paramInt1, paramInt2); }
  
  public void characters(int paramInt, SerializationHandler paramSerializationHandler) throws TransletException {
    if (this._dom != null) {
      this._dom.characters(paramInt, paramSerializationHandler);
    } else {
      super.characters(paramInt, paramSerializationHandler);
    } 
  }
  
  public Node makeNode(int paramInt) { return (this._dom != null) ? this._dom.makeNode(paramInt) : super.makeNode(paramInt); }
  
  public Node makeNode(DTMAxisIterator paramDTMAxisIterator) { return (this._dom != null) ? this._dom.makeNode(paramDTMAxisIterator) : super.makeNode(paramDTMAxisIterator); }
  
  public NodeList makeNodeList(int paramInt) { return (this._dom != null) ? this._dom.makeNodeList(paramInt) : super.makeNodeList(paramInt); }
  
  public NodeList makeNodeList(DTMAxisIterator paramDTMAxisIterator) { return (this._dom != null) ? this._dom.makeNodeList(paramDTMAxisIterator) : super.makeNodeList(paramDTMAxisIterator); }
  
  public String getLanguage(int paramInt) { return (this._dom != null) ? this._dom.getLanguage(paramInt) : super.getLanguage(paramInt); }
  
  public int getSize() { return (this._dom != null) ? this._dom.getSize() : super.getSize(); }
  
  public String getDocumentURI(int paramInt) { return (this._dom != null) ? this._dom.getDocumentURI(paramInt) : ("adaptive_rtf" + _documentURIIndex++); }
  
  public void setFilter(StripFilter paramStripFilter) {
    if (this._dom != null) {
      this._dom.setFilter(paramStripFilter);
    } else {
      super.setFilter(paramStripFilter);
    } 
  }
  
  public void setupMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt, String[] paramArrayOfString3) {
    if (this._dom != null) {
      this._dom.setupMapping(paramArrayOfString1, paramArrayOfString2, paramArrayOfInt, paramArrayOfString3);
    } else {
      super.setupMapping(paramArrayOfString1, paramArrayOfString2, paramArrayOfInt, paramArrayOfString3);
    } 
  }
  
  public boolean isElement(int paramInt) { return (this._dom != null) ? this._dom.isElement(paramInt) : super.isElement(paramInt); }
  
  public boolean isAttribute(int paramInt) { return (this._dom != null) ? this._dom.isAttribute(paramInt) : super.isAttribute(paramInt); }
  
  public String lookupNamespace(int paramInt, String paramString) throws TransletException { return (this._dom != null) ? this._dom.lookupNamespace(paramInt, paramString) : super.lookupNamespace(paramInt, paramString); }
  
  public final int getNodeIdent(int paramInt) { return (this._dom != null) ? this._dom.getNodeIdent(paramInt) : super.getNodeIdent(paramInt); }
  
  public final int getNodeHandle(int paramInt) { return (this._dom != null) ? this._dom.getNodeHandle(paramInt) : super.getNodeHandle(paramInt); }
  
  public DOM getResultTreeFrag(int paramInt1, int paramInt2) { return (this._dom != null) ? this._dom.getResultTreeFrag(paramInt1, paramInt2) : super.getResultTreeFrag(paramInt1, paramInt2); }
  
  public SerializationHandler getOutputDomBuilder() { return this; }
  
  public int getNSType(int paramInt) { return (this._dom != null) ? this._dom.getNSType(paramInt) : super.getNSType(paramInt); }
  
  public String getUnparsedEntityURI(String paramString) { return (this._dom != null) ? this._dom.getUnparsedEntityURI(paramString) : super.getUnparsedEntityURI(paramString); }
  
  public Map<String, Integer> getElementsWithIDs() { return (this._dom != null) ? this._dom.getElementsWithIDs() : super.getElementsWithIDs(); }
  
  private void maybeEmitStartElement() throws SAXException {
    if (this._openElementName != null) {
      int i;
      if ((i = this._openElementName.indexOf(":")) < 0) {
        this._dom.startElement(null, this._openElementName, this._openElementName, this._attributes);
      } else {
        String str = this._dom.getNamespaceURI(this._openElementName.substring(0, i));
        this._dom.startElement(str, this._openElementName.substring(i + 1), this._openElementName, this._attributes);
      } 
      this._openElementName = null;
    } 
  }
  
  private void prepareNewDOM() throws SAXException {
    this._dom = (SAXImpl)this._dtmManager.getDTM(null, true, this._wsfilter, true, false, false, this._initSize, this._buildIdIndex);
    this._dom.startDocument();
    for (byte b = 0; b < this._size; b++) {
      String str = this._textArray[b];
      this._dom.characters(str.toCharArray(), 0, str.length());
    } 
    this._size = 0;
  }
  
  public void startDocument() throws SAXException {}
  
  public void endDocument() throws SAXException {
    if (this._dom != null) {
      this._dom.endDocument();
    } else {
      super.endDocument();
    } 
  }
  
  public void characters(String paramString) throws SAXException {
    if (this._dom != null) {
      characters(paramString.toCharArray(), 0, paramString.length());
    } else {
      super.characters(paramString);
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this._dom != null) {
      maybeEmitStartElement();
      this._dom.characters(paramArrayOfChar, paramInt1, paramInt2);
    } else {
      super.characters(paramArrayOfChar, paramInt1, paramInt2);
    } 
  }
  
  public boolean setEscaping(boolean paramBoolean) throws SAXException { return (this._dom != null) ? this._dom.setEscaping(paramBoolean) : super.setEscaping(paramBoolean); }
  
  public void startElement(String paramString) throws SAXException {
    if (this._dom == null)
      prepareNewDOM(); 
    maybeEmitStartElement();
    this._openElementName = paramString;
    this._attributes.clear();
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3) throws SAXException { startElement(paramString3); }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException { startElement(paramString3); }
  
  public void endElement(String paramString) throws SAXException {
    maybeEmitStartElement();
    this._dom.endElement(null, null, paramString);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException { endElement(paramString3); }
  
  public void addAttribute(String paramString1, String paramString2) {
    int i = paramString1.indexOf(":");
    String str1 = EMPTY_STRING;
    String str2 = paramString1;
    if (i > 0) {
      String str = paramString1.substring(0, i);
      str2 = paramString1.substring(i + 1);
      str1 = this._dom.getNamespaceURI(str);
    } 
    addAttribute(str1, str2, paramString1, "CDATA", paramString2);
  }
  
  public void addUniqueAttribute(String paramString1, String paramString2, int paramInt) throws SAXException { addAttribute(paramString1, paramString2); }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    if (this._openElementName != null) {
      this._attributes.addAttribute(paramString1, paramString2, paramString3, paramString4, paramString5);
    } else {
      BasisLibrary.runTimeError("STRAY_ATTRIBUTE_ERR", paramString3);
    } 
  }
  
  public void namespaceAfterStartElement(String paramString1, String paramString2) {
    if (this._dom == null)
      prepareNewDOM(); 
    this._dom.startPrefixMapping(paramString1, paramString2);
  }
  
  public void comment(String paramString) throws SAXException {
    if (this._dom == null)
      prepareNewDOM(); 
    maybeEmitStartElement();
    char[] arrayOfChar = paramString.toCharArray();
    this._dom.comment(arrayOfChar, 0, arrayOfChar.length);
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this._dom == null)
      prepareNewDOM(); 
    maybeEmitStartElement();
    this._dom.comment(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void processingInstruction(String paramString1, String paramString2) {
    if (this._dom == null)
      prepareNewDOM(); 
    maybeEmitStartElement();
    this._dom.processingInstruction(paramString1, paramString2);
  }
  
  public void setFeature(String paramString, boolean paramBoolean) {
    if (this._dom != null)
      this._dom.setFeature(paramString, paramBoolean); 
  }
  
  public void setProperty(String paramString, Object paramObject) {
    if (this._dom != null)
      this._dom.setProperty(paramString, paramObject); 
  }
  
  public DTMAxisTraverser getAxisTraverser(int paramInt) { return (this._dom != null) ? this._dom.getAxisTraverser(paramInt) : super.getAxisTraverser(paramInt); }
  
  public boolean hasChildNodes(int paramInt) { return (this._dom != null) ? this._dom.hasChildNodes(paramInt) : super.hasChildNodes(paramInt); }
  
  public int getFirstChild(int paramInt) { return (this._dom != null) ? this._dom.getFirstChild(paramInt) : super.getFirstChild(paramInt); }
  
  public int getLastChild(int paramInt) { return (this._dom != null) ? this._dom.getLastChild(paramInt) : super.getLastChild(paramInt); }
  
  public int getAttributeNode(int paramInt, String paramString1, String paramString2) { return (this._dom != null) ? this._dom.getAttributeNode(paramInt, paramString1, paramString2) : super.getAttributeNode(paramInt, paramString1, paramString2); }
  
  public int getFirstAttribute(int paramInt) { return (this._dom != null) ? this._dom.getFirstAttribute(paramInt) : super.getFirstAttribute(paramInt); }
  
  public int getFirstNamespaceNode(int paramInt, boolean paramBoolean) { return (this._dom != null) ? this._dom.getFirstNamespaceNode(paramInt, paramBoolean) : super.getFirstNamespaceNode(paramInt, paramBoolean); }
  
  public int getNextSibling(int paramInt) { return (this._dom != null) ? this._dom.getNextSibling(paramInt) : super.getNextSibling(paramInt); }
  
  public int getPreviousSibling(int paramInt) { return (this._dom != null) ? this._dom.getPreviousSibling(paramInt) : super.getPreviousSibling(paramInt); }
  
  public int getNextAttribute(int paramInt) { return (this._dom != null) ? this._dom.getNextAttribute(paramInt) : super.getNextAttribute(paramInt); }
  
  public int getNextNamespaceNode(int paramInt1, int paramInt2, boolean paramBoolean) { return (this._dom != null) ? this._dom.getNextNamespaceNode(paramInt1, paramInt2, paramBoolean) : super.getNextNamespaceNode(paramInt1, paramInt2, paramBoolean); }
  
  public int getOwnerDocument(int paramInt) { return (this._dom != null) ? this._dom.getOwnerDocument(paramInt) : super.getOwnerDocument(paramInt); }
  
  public int getDocumentRoot(int paramInt) { return (this._dom != null) ? this._dom.getDocumentRoot(paramInt) : super.getDocumentRoot(paramInt); }
  
  public XMLString getStringValue(int paramInt) { return (this._dom != null) ? this._dom.getStringValue(paramInt) : super.getStringValue(paramInt); }
  
  public int getStringValueChunkCount(int paramInt) { return (this._dom != null) ? this._dom.getStringValueChunkCount(paramInt) : super.getStringValueChunkCount(paramInt); }
  
  public char[] getStringValueChunk(int paramInt1, int paramInt2, int[] paramArrayOfInt) { return (this._dom != null) ? this._dom.getStringValueChunk(paramInt1, paramInt2, paramArrayOfInt) : super.getStringValueChunk(paramInt1, paramInt2, paramArrayOfInt); }
  
  public int getExpandedTypeID(String paramString1, String paramString2, int paramInt) { return (this._dom != null) ? this._dom.getExpandedTypeID(paramString1, paramString2, paramInt) : super.getExpandedTypeID(paramString1, paramString2, paramInt); }
  
  public String getLocalNameFromExpandedNameID(int paramInt) { return (this._dom != null) ? this._dom.getLocalNameFromExpandedNameID(paramInt) : super.getLocalNameFromExpandedNameID(paramInt); }
  
  public String getNamespaceFromExpandedNameID(int paramInt) { return (this._dom != null) ? this._dom.getNamespaceFromExpandedNameID(paramInt) : super.getNamespaceFromExpandedNameID(paramInt); }
  
  public String getLocalName(int paramInt) { return (this._dom != null) ? this._dom.getLocalName(paramInt) : super.getLocalName(paramInt); }
  
  public String getPrefix(int paramInt) { return (this._dom != null) ? this._dom.getPrefix(paramInt) : super.getPrefix(paramInt); }
  
  public String getNamespaceURI(int paramInt) { return (this._dom != null) ? this._dom.getNamespaceURI(paramInt) : super.getNamespaceURI(paramInt); }
  
  public String getNodeValue(int paramInt) { return (this._dom != null) ? this._dom.getNodeValue(paramInt) : super.getNodeValue(paramInt); }
  
  public short getNodeType(int paramInt) { return (this._dom != null) ? this._dom.getNodeType(paramInt) : super.getNodeType(paramInt); }
  
  public short getLevel(int paramInt) { return (this._dom != null) ? this._dom.getLevel(paramInt) : super.getLevel(paramInt); }
  
  public boolean isSupported(String paramString1, String paramString2) { return (this._dom != null) ? this._dom.isSupported(paramString1, paramString2) : super.isSupported(paramString1, paramString2); }
  
  public String getDocumentBaseURI() { return (this._dom != null) ? this._dom.getDocumentBaseURI() : super.getDocumentBaseURI(); }
  
  public void setDocumentBaseURI(String paramString) throws SAXException {
    if (this._dom != null) {
      this._dom.setDocumentBaseURI(paramString);
    } else {
      super.setDocumentBaseURI(paramString);
    } 
  }
  
  public String getDocumentSystemIdentifier(int paramInt) { return (this._dom != null) ? this._dom.getDocumentSystemIdentifier(paramInt) : super.getDocumentSystemIdentifier(paramInt); }
  
  public String getDocumentEncoding(int paramInt) { return (this._dom != null) ? this._dom.getDocumentEncoding(paramInt) : super.getDocumentEncoding(paramInt); }
  
  public String getDocumentStandalone(int paramInt) { return (this._dom != null) ? this._dom.getDocumentStandalone(paramInt) : super.getDocumentStandalone(paramInt); }
  
  public String getDocumentVersion(int paramInt) { return (this._dom != null) ? this._dom.getDocumentVersion(paramInt) : super.getDocumentVersion(paramInt); }
  
  public boolean getDocumentAllDeclarationsProcessed() { return (this._dom != null) ? this._dom.getDocumentAllDeclarationsProcessed() : super.getDocumentAllDeclarationsProcessed(); }
  
  public String getDocumentTypeDeclarationSystemIdentifier() { return (this._dom != null) ? this._dom.getDocumentTypeDeclarationSystemIdentifier() : super.getDocumentTypeDeclarationSystemIdentifier(); }
  
  public String getDocumentTypeDeclarationPublicIdentifier() { return (this._dom != null) ? this._dom.getDocumentTypeDeclarationPublicIdentifier() : super.getDocumentTypeDeclarationPublicIdentifier(); }
  
  public int getElementById(String paramString) { return (this._dom != null) ? this._dom.getElementById(paramString) : super.getElementById(paramString); }
  
  public boolean supportsPreStripping() { return (this._dom != null) ? this._dom.supportsPreStripping() : super.supportsPreStripping(); }
  
  public boolean isNodeAfter(int paramInt1, int paramInt2) { return (this._dom != null) ? this._dom.isNodeAfter(paramInt1, paramInt2) : super.isNodeAfter(paramInt1, paramInt2); }
  
  public boolean isCharacterElementContentWhitespace(int paramInt) { return (this._dom != null) ? this._dom.isCharacterElementContentWhitespace(paramInt) : super.isCharacterElementContentWhitespace(paramInt); }
  
  public boolean isDocumentAllDeclarationsProcessed(int paramInt) { return (this._dom != null) ? this._dom.isDocumentAllDeclarationsProcessed(paramInt) : super.isDocumentAllDeclarationsProcessed(paramInt); }
  
  public boolean isAttributeSpecified(int paramInt) { return (this._dom != null) ? this._dom.isAttributeSpecified(paramInt) : super.isAttributeSpecified(paramInt); }
  
  public void dispatchCharactersEvents(int paramInt, ContentHandler paramContentHandler, boolean paramBoolean) throws SAXException {
    if (this._dom != null) {
      this._dom.dispatchCharactersEvents(paramInt, paramContentHandler, paramBoolean);
    } else {
      super.dispatchCharactersEvents(paramInt, paramContentHandler, paramBoolean);
    } 
  }
  
  public void dispatchToEvents(int paramInt, ContentHandler paramContentHandler) throws SAXException {
    if (this._dom != null) {
      this._dom.dispatchToEvents(paramInt, paramContentHandler);
    } else {
      super.dispatchToEvents(paramInt, paramContentHandler);
    } 
  }
  
  public Node getNode(int paramInt) { return (this._dom != null) ? this._dom.getNode(paramInt) : super.getNode(paramInt); }
  
  public boolean needsTwoThreads() { return (this._dom != null) ? this._dom.needsTwoThreads() : super.needsTwoThreads(); }
  
  public ContentHandler getContentHandler() { return (this._dom != null) ? this._dom.getContentHandler() : super.getContentHandler(); }
  
  public LexicalHandler getLexicalHandler() { return (this._dom != null) ? this._dom.getLexicalHandler() : super.getLexicalHandler(); }
  
  public EntityResolver getEntityResolver() { return (this._dom != null) ? this._dom.getEntityResolver() : super.getEntityResolver(); }
  
  public DTDHandler getDTDHandler() { return (this._dom != null) ? this._dom.getDTDHandler() : super.getDTDHandler(); }
  
  public ErrorHandler getErrorHandler() { return (this._dom != null) ? this._dom.getErrorHandler() : super.getErrorHandler(); }
  
  public DeclHandler getDeclHandler() { return (this._dom != null) ? this._dom.getDeclHandler() : super.getDeclHandler(); }
  
  public void appendChild(int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
    if (this._dom != null) {
      this._dom.appendChild(paramInt, paramBoolean1, paramBoolean2);
    } else {
      super.appendChild(paramInt, paramBoolean1, paramBoolean2);
    } 
  }
  
  public void appendTextChild(String paramString) throws SAXException {
    if (this._dom != null) {
      this._dom.appendTextChild(paramString);
    } else {
      super.appendTextChild(paramString);
    } 
  }
  
  public SourceLocator getSourceLocatorFor(int paramInt) { return (this._dom != null) ? this._dom.getSourceLocatorFor(paramInt) : super.getSourceLocatorFor(paramInt); }
  
  public void documentRegistration() throws SAXException {
    if (this._dom != null) {
      this._dom.documentRegistration();
    } else {
      super.documentRegistration();
    } 
  }
  
  public void documentRelease() throws SAXException {
    if (this._dom != null) {
      this._dom.documentRelease();
    } else {
      super.documentRelease();
    } 
  }
  
  public void release() throws SAXException {
    if (this._dom != null) {
      this._dom.release();
      this._dom = null;
    } 
    super.release();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\AdaptiveResultTreeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */