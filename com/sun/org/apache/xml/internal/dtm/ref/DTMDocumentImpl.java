package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import javax.xml.transform.SourceLocator;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public class DTMDocumentImpl implements DTM, ContentHandler, LexicalHandler {
  protected static final byte DOCHANDLE_SHIFT = 22;
  
  protected static final int NODEHANDLE_MASK = 8388607;
  
  protected static final int DOCHANDLE_MASK = -8388608;
  
  int m_docHandle = -1;
  
  int m_docElement = -1;
  
  int currentParent = 0;
  
  int previousSibling = 0;
  
  protected int m_currentNode = -1;
  
  private boolean previousSiblingWasParent = false;
  
  int[] gotslot = new int[4];
  
  private boolean done = false;
  
  boolean m_isError = false;
  
  private final boolean DEBUG = false;
  
  protected String m_documentBaseURI;
  
  private IncrementalSAXSource m_incrSAXSource = null;
  
  ChunkedIntArray nodes = new ChunkedIntArray(4);
  
  private FastStringBuffer m_char = new FastStringBuffer();
  
  private int m_char_current_start = 0;
  
  private DTMStringPool m_localNames = new DTMStringPool();
  
  private DTMStringPool m_nsNames = new DTMStringPool();
  
  private DTMStringPool m_prefixNames = new DTMStringPool();
  
  private ExpandedNameTable m_expandedNames = new ExpandedNameTable();
  
  private XMLStringFactory m_xsf;
  
  private static final String[] fixednames = { 
      null, null, null, "#text", "#cdata_section", null, null, null, "#comment", "#document", 
      null, "#document-fragment", null };
  
  public DTMDocumentImpl(DTMManager paramDTMManager, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory) {
    initDocument(paramInt);
    this.m_xsf = paramXMLStringFactory;
  }
  
  public void setIncrementalSAXSource(IncrementalSAXSource paramIncrementalSAXSource) {
    this.m_incrSAXSource = paramIncrementalSAXSource;
    paramIncrementalSAXSource.setContentHandler(this);
    paramIncrementalSAXSource.setLexicalHandler(this);
  }
  
  private final int appendNode(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = this.nodes.appendSlot(paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.previousSiblingWasParent)
      this.nodes.writeEntry(this.previousSibling, 2, i); 
    this.previousSiblingWasParent = false;
    return i;
  }
  
  public void setFeature(String paramString, boolean paramBoolean) {}
  
  public void setLocalNameTable(DTMStringPool paramDTMStringPool) { this.m_localNames = paramDTMStringPool; }
  
  public DTMStringPool getLocalNameTable() { return this.m_localNames; }
  
  public void setNsNameTable(DTMStringPool paramDTMStringPool) { this.m_nsNames = paramDTMStringPool; }
  
  public DTMStringPool getNsNameTable() { return this.m_nsNames; }
  
  public void setPrefixNameTable(DTMStringPool paramDTMStringPool) { this.m_prefixNames = paramDTMStringPool; }
  
  public DTMStringPool getPrefixNameTable() { return this.m_prefixNames; }
  
  void setContentBuffer(FastStringBuffer paramFastStringBuffer) { this.m_char = paramFastStringBuffer; }
  
  FastStringBuffer getContentBuffer() { return this.m_char; }
  
  public ContentHandler getContentHandler() { return (this.m_incrSAXSource instanceof IncrementalSAXSource_Filter) ? (ContentHandler)this.m_incrSAXSource : this; }
  
  public LexicalHandler getLexicalHandler() { return (this.m_incrSAXSource instanceof IncrementalSAXSource_Filter) ? (LexicalHandler)this.m_incrSAXSource : this; }
  
  public EntityResolver getEntityResolver() { return null; }
  
  public DTDHandler getDTDHandler() { return null; }
  
  public ErrorHandler getErrorHandler() { return null; }
  
  public DeclHandler getDeclHandler() { return null; }
  
  public boolean needsTwoThreads() { return (null != this.m_incrSAXSource); }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException { this.m_char.append(paramArrayOfChar, paramInt1, paramInt2); }
  
  private void processAccumulatedText() {
    int i = this.m_char.length();
    if (i != this.m_char_current_start) {
      appendTextChild(this.m_char_current_start, i - this.m_char_current_start);
      this.m_char_current_start = i;
    } 
  }
  
  public void endDocument() { appendEndDocument(); }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    processAccumulatedText();
    appendEndElement();
  }
  
  public void endPrefixMapping(String paramString) throws SAXException {}
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {}
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException { processAccumulatedText(); }
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void skippedEntity(String paramString) throws SAXException { processAccumulatedText(); }
  
  public void startDocument() { appendStartDocument(); }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    processAccumulatedText();
    String str = null;
    int i = paramString3.indexOf(':');
    if (i > 0)
      str = paramString3.substring(0, i); 
    System.out.println("Prefix=" + str + " index=" + this.m_prefixNames.stringToIndex(str));
    appendStartElement(this.m_nsNames.stringToIndex(paramString1), this.m_localNames.stringToIndex(paramString2), this.m_prefixNames.stringToIndex(str));
    boolean bool = (paramAttributes == null) ? 0 : paramAttributes.getLength();
    byte b;
    for (b = bool - true; b; b--) {
      paramString3 = paramAttributes.getQName(b);
      if (paramString3.startsWith("xmlns:") || "xmlns".equals(paramString3)) {
        str = null;
        i = paramString3.indexOf(':');
        if (i > 0) {
          str = paramString3.substring(0, i);
        } else {
          str = null;
        } 
        appendNSDeclaration(this.m_prefixNames.stringToIndex(str), this.m_nsNames.stringToIndex(paramAttributes.getValue(b)), paramAttributes.getType(b).equalsIgnoreCase("ID"));
      } 
    } 
    for (b = bool - true; b >= 0; b--) {
      paramString3 = paramAttributes.getQName(b);
      if (!paramString3.startsWith("xmlns:") && !"xmlns".equals(paramString3)) {
        str = null;
        i = paramString3.indexOf(':');
        if (i > 0) {
          str = paramString3.substring(0, i);
          paramString2 = paramString3.substring(i + 1);
        } else {
          str = "";
          paramString2 = paramString3;
        } 
        this.m_char.append(paramAttributes.getValue(b));
        int j = this.m_char.length();
        if (!"xmlns".equals(str) && !"xmlns".equals(paramString3))
          appendAttribute(this.m_nsNames.stringToIndex(paramAttributes.getURI(b)), this.m_localNames.stringToIndex(paramString2), this.m_prefixNames.stringToIndex(str), paramAttributes.getType(b).equalsIgnoreCase("ID"), this.m_char_current_start, j - this.m_char_current_start); 
        this.m_char_current_start = j;
      } 
    } 
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {}
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    processAccumulatedText();
    this.m_char.append(paramArrayOfChar, paramInt1, paramInt2);
    appendComment(this.m_char_current_start, paramInt2);
    this.m_char_current_start += paramInt2;
  }
  
  public void endCDATA() {}
  
  public void endDTD() {}
  
  public void endEntity(String paramString) throws SAXException {}
  
  public void startCDATA() {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void startEntity(String paramString) throws SAXException {}
  
  final void initDocument(int paramInt) {
    this.m_docHandle = paramInt << 22;
    this.nodes.writeSlot(0, 9, -1, -1, 0);
    this.done = false;
  }
  
  public boolean hasChildNodes(int paramInt) { return (getFirstChild(paramInt) != -1); }
  
  public int getFirstChild(int paramInt) {
    paramInt &= 0x7FFFFF;
    this.nodes.readSlot(paramInt, this.gotslot);
    short s = (short)(this.gotslot[0] & 0xFFFF);
    if (s == 1 || s == 9 || s == 5) {
      int i = paramInt + 1;
      this.nodes.readSlot(i, this.gotslot);
      while (2 == (this.gotslot[0] & 0xFFFF)) {
        i = this.gotslot[2];
        if (i == -1)
          return -1; 
        this.nodes.readSlot(i, this.gotslot);
      } 
      if (this.gotslot[1] == paramInt)
        return i | this.m_docHandle; 
    } 
    return -1;
  }
  
  public int getLastChild(int paramInt) {
    paramInt &= 0x7FFFFF;
    int i = -1;
    for (int j = getFirstChild(paramInt); j != -1; j = getNextSibling(j))
      i = j; 
    return i | this.m_docHandle;
  }
  
  public int getAttributeNode(int paramInt, String paramString1, String paramString2) {
    int i = this.m_nsNames.stringToIndex(paramString1);
    int j = this.m_localNames.stringToIndex(paramString2);
    paramInt &= 0x7FFFFF;
    this.nodes.readSlot(paramInt, this.gotslot);
    short s = (short)(this.gotslot[0] & 0xFFFF);
    if (s == 1)
      paramInt++; 
    while (s == 2) {
      if (i == this.gotslot[0] << 16 && this.gotslot[3] == j)
        return paramInt | this.m_docHandle; 
      paramInt = this.gotslot[2];
      this.nodes.readSlot(paramInt, this.gotslot);
    } 
    return -1;
  }
  
  public int getFirstAttribute(int paramInt) {
    paramInt &= 0x7FFFFF;
    return (1 != (this.nodes.readEntry(paramInt, 0) & 0xFFFF)) ? -1 : ((2 == (this.nodes.readEntry(++paramInt, 0) & 0xFFFF)) ? (paramInt | this.m_docHandle) : -1);
  }
  
  public int getFirstNamespaceNode(int paramInt, boolean paramBoolean) { return -1; }
  
  public int getNextSibling(int paramInt) {
    paramInt &= 0x7FFFFF;
    if (paramInt == 0)
      return -1; 
    short s = (short)(this.nodes.readEntry(paramInt, 0) & 0xFFFF);
    if (s == 1 || s == 2 || s == 5) {
      int j = this.nodes.readEntry(paramInt, 2);
      if (j == -1)
        return -1; 
      if (j != 0)
        return this.m_docHandle | j; 
    } 
    int i = this.nodes.readEntry(paramInt, 1);
    return (this.nodes.readEntry(++paramInt, 1) == i) ? (this.m_docHandle | paramInt) : -1;
  }
  
  public int getPreviousSibling(int paramInt) {
    paramInt &= 0x7FFFFF;
    if (paramInt == 0)
      return -1; 
    int i = this.nodes.readEntry(paramInt, 1);
    int j = -1;
    int k;
    for (k = getFirstChild(i); k != paramInt; k = getNextSibling(k))
      j = k; 
    return j | this.m_docHandle;
  }
  
  public int getNextAttribute(int paramInt) {
    paramInt &= 0x7FFFFF;
    this.nodes.readSlot(paramInt, this.gotslot);
    short s = (short)(this.gotslot[0] & 0xFFFF);
    return (s == 1) ? getFirstAttribute(paramInt) : ((s == 2 && this.gotslot[2] != -1) ? (this.m_docHandle | this.gotslot[2]) : -1);
  }
  
  public int getNextNamespaceNode(int paramInt1, int paramInt2, boolean paramBoolean) { return -1; }
  
  public int getNextDescendant(int paramInt1, int paramInt2) {
    paramInt1 &= 0x7FFFFF;
    paramInt2 &= 0x7FFFFF;
    if (paramInt2 == 0)
      return -1; 
    while (!this.m_isError && (!this.done || paramInt2 <= this.nodes.slotsUsed())) {
      if (paramInt2 > paramInt1) {
        this.nodes.readSlot(paramInt2 + 1, this.gotslot);
        if (this.gotslot[2] != 0) {
          short s = (short)(this.gotslot[0] & 0xFFFF);
          if (s == 2) {
            paramInt2 += 2;
            continue;
          } 
          int i = this.gotslot[1];
          if (i >= paramInt1)
            return this.m_docHandle | paramInt2 + 1; 
          break;
        } 
        if (!this.done)
          continue; 
        break;
      } 
      paramInt2++;
    } 
    return -1;
  }
  
  public int getNextFollowing(int paramInt1, int paramInt2) { return -1; }
  
  public int getNextPreceding(int paramInt1, int paramInt2) {
    paramInt2 &= 0x7FFFFF;
    while (paramInt2 > 1) {
      if (2 == (this.nodes.readEntry(--paramInt2, 0) & 0xFFFF))
        continue; 
      return this.m_docHandle | this.nodes.specialFind(paramInt1, paramInt2);
    } 
    return -1;
  }
  
  public int getParent(int paramInt) { return this.m_docHandle | this.nodes.readEntry(paramInt, 1); }
  
  public int getDocumentRoot() { return this.m_docHandle | this.m_docElement; }
  
  public int getDocument() { return this.m_docHandle; }
  
  public int getOwnerDocument(int paramInt) { return ((paramInt & 0x7FFFFF) == 0) ? -1 : (paramInt & 0xFF800000); }
  
  public int getDocumentRoot(int paramInt) { return ((paramInt & 0x7FFFFF) == 0) ? -1 : (paramInt & 0xFF800000); }
  
  public XMLString getStringValue(int paramInt) {
    this.nodes.readSlot(paramInt, this.gotslot);
    int i = this.gotslot[0] & 0xFF;
    String str = null;
    switch (i) {
      case 3:
      case 4:
      case 8:
        str = this.m_char.getString(this.gotslot[2], this.gotslot[3]);
        break;
    } 
    return this.m_xsf.newstr(str);
  }
  
  public int getStringValueChunkCount(int paramInt) { return 0; }
  
  public char[] getStringValueChunk(int paramInt1, int paramInt2, int[] paramArrayOfInt) { return new char[0]; }
  
  public int getExpandedTypeID(int paramInt) {
    this.nodes.readSlot(paramInt, this.gotslot);
    String str1 = this.m_localNames.indexToString(this.gotslot[3]);
    int i = str1.indexOf(":");
    String str2 = str1.substring(i + 1);
    String str3 = this.m_nsNames.indexToString(this.gotslot[0] << 16);
    String str4 = str3 + ":" + str2;
    return this.m_nsNames.stringToIndex(str4);
  }
  
  public int getExpandedTypeID(String paramString1, String paramString2, int paramInt) {
    String str = paramString1 + ":" + paramString2;
    return this.m_nsNames.stringToIndex(str);
  }
  
  public String getLocalNameFromExpandedNameID(int paramInt) {
    String str = this.m_localNames.indexToString(paramInt);
    int i = str.indexOf(":");
    return str.substring(i + 1);
  }
  
  public String getNamespaceFromExpandedNameID(int paramInt) {
    String str = this.m_localNames.indexToString(paramInt);
    int i = str.indexOf(":");
    return str.substring(0, i);
  }
  
  public String getNodeName(int paramInt) {
    this.nodes.readSlot(paramInt, this.gotslot);
    short s = (short)(this.gotslot[0] & 0xFFFF);
    String str = fixednames[s];
    if (null == str) {
      int i = this.gotslot[3];
      System.out.println("got i=" + i + " " + (i >> 16) + "/" + (i & 0xFFFF));
      str = this.m_localNames.indexToString(i & 0xFFFF);
      String str1 = this.m_prefixNames.indexToString(i >> 16);
      if (str1 != null && str1.length() > 0)
        str = str1 + ":" + str; 
    } 
    return str;
  }
  
  public String getNodeNameX(int paramInt) { return null; }
  
  public String getLocalName(int paramInt) {
    this.nodes.readSlot(paramInt, this.gotslot);
    short s = (short)(this.gotslot[0] & 0xFFFF);
    String str = "";
    if (s == 1 || s == 2) {
      int i = this.gotslot[3];
      str = this.m_localNames.indexToString(i & 0xFFFF);
      if (str == null)
        str = ""; 
    } 
    return str;
  }
  
  public String getPrefix(int paramInt) {
    this.nodes.readSlot(paramInt, this.gotslot);
    short s = (short)(this.gotslot[0] & 0xFFFF);
    String str = "";
    if (s == 1 || s == 2) {
      int i = this.gotslot[3];
      str = this.m_prefixNames.indexToString(i >> 16);
      if (str == null)
        str = ""; 
    } 
    return str;
  }
  
  public String getNamespaceURI(int paramInt) { return null; }
  
  public String getNodeValue(int paramInt) {
    this.nodes.readSlot(paramInt, this.gotslot);
    int i = this.gotslot[0] & 0xFF;
    String str = null;
    switch (i) {
      case 2:
        this.nodes.readSlot(paramInt + 1, this.gotslot);
      case 3:
      case 4:
      case 8:
        str = this.m_char.getString(this.gotslot[2], this.gotslot[3]);
        break;
    } 
    return str;
  }
  
  public short getNodeType(int paramInt) { return (short)(this.nodes.readEntry(paramInt, 0) & 0xFFFF); }
  
  public short getLevel(int paramInt) {
    short s = 0;
    while (paramInt != 0) {
      s = (short)(s + true);
      paramInt = this.nodes.readEntry(paramInt, 1);
    } 
    return s;
  }
  
  public boolean isSupported(String paramString1, String paramString2) { return false; }
  
  public String getDocumentBaseURI() { return this.m_documentBaseURI; }
  
  public void setDocumentBaseURI(String paramString) throws SAXException { this.m_documentBaseURI = paramString; }
  
  public String getDocumentSystemIdentifier(int paramInt) { return null; }
  
  public String getDocumentEncoding(int paramInt) { return null; }
  
  public String getDocumentStandalone(int paramInt) { return null; }
  
  public String getDocumentVersion(int paramInt) { return null; }
  
  public boolean getDocumentAllDeclarationsProcessed() { return false; }
  
  public String getDocumentTypeDeclarationSystemIdentifier() { return null; }
  
  public String getDocumentTypeDeclarationPublicIdentifier() { return null; }
  
  public int getElementById(String paramString) { return 0; }
  
  public String getUnparsedEntityURI(String paramString) { return null; }
  
  public boolean supportsPreStripping() { return false; }
  
  public boolean isNodeAfter(int paramInt1, int paramInt2) { return false; }
  
  public boolean isCharacterElementContentWhitespace(int paramInt) { return false; }
  
  public boolean isDocumentAllDeclarationsProcessed(int paramInt) { return false; }
  
  public boolean isAttributeSpecified(int paramInt) { return false; }
  
  public void dispatchCharactersEvents(int paramInt, ContentHandler paramContentHandler, boolean paramBoolean) throws SAXException {}
  
  public void dispatchToEvents(int paramInt, ContentHandler paramContentHandler) throws SAXException {}
  
  public Node getNode(int paramInt) { return null; }
  
  public void appendChild(int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
    boolean bool = ((paramInt & 0xFF800000) == this.m_docHandle) ? 1 : 0;
    if (paramBoolean1 || !bool);
  }
  
  public void appendTextChild(String paramString) throws SAXException {}
  
  void appendTextChild(int paramInt1, int paramInt2) {
    byte b = 3;
    int i = this.currentParent;
    int j = paramInt1;
    int k = paramInt2;
    int m = appendNode(b, i, j, k);
    this.previousSibling = m;
  }
  
  void appendComment(int paramInt1, int paramInt2) {
    byte b = 8;
    int i = this.currentParent;
    int j = paramInt1;
    int k = paramInt2;
    int m = appendNode(b, i, j, k);
    this.previousSibling = m;
  }
  
  void appendStartElement(int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt1 << 16 | true;
    int j = this.currentParent;
    byte b = 0;
    int k = paramInt2 | paramInt3 << 16;
    System.out.println("set w3=" + k + " " + (k >> 16) + "/" + (k & 0xFFFF));
    int m = appendNode(i, j, b, k);
    this.currentParent = m;
    this.previousSibling = 0;
    if (this.m_docElement == -1)
      this.m_docElement = m; 
  }
  
  void appendNSDeclaration(int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = this.m_nsNames.stringToIndex("http://www.w3.org/2000/xmlns/");
    int j = 0xD | this.m_nsNames.stringToIndex("http://www.w3.org/2000/xmlns/") << 16;
    int k = this.currentParent;
    byte b = 0;
    int m = paramInt2;
    int n = appendNode(j, k, b, m);
    this.previousSibling = n;
    this.previousSiblingWasParent = false;
  }
  
  void appendAttribute(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, int paramInt4, int paramInt5) {
    int i = 0x2 | paramInt1 << 16;
    int j = this.currentParent;
    int k = 0;
    int m = paramInt2 | paramInt3 << 16;
    System.out.println("set w3=" + m + " " + (m >> 16) + "/" + (m & 0xFFFF));
    int n = appendNode(i, j, k, m);
    this.previousSibling = n;
    i = 3;
    j = n;
    k = paramInt4;
    m = paramInt5;
    appendNode(i, j, k, m);
    this.previousSiblingWasParent = true;
  }
  
  public DTMAxisTraverser getAxisTraverser(int paramInt) { return null; }
  
  public DTMAxisIterator getAxisIterator(int paramInt) { return null; }
  
  public DTMAxisIterator getTypedAxisIterator(int paramInt1, int paramInt2) { return null; }
  
  void appendEndElement() {
    if (this.previousSiblingWasParent)
      this.nodes.writeEntry(this.previousSibling, 2, -1); 
    this.previousSibling = this.currentParent;
    this.nodes.readSlot(this.currentParent, this.gotslot);
    this.currentParent = this.gotslot[1] & 0xFFFF;
    this.previousSiblingWasParent = true;
  }
  
  void appendStartDocument() {
    this.m_docElement = -1;
    initDocument(0);
  }
  
  void appendEndDocument() { this.done = true; }
  
  public void setProperty(String paramString, Object paramObject) {}
  
  public SourceLocator getSourceLocatorFor(int paramInt) { return null; }
  
  public void documentRegistration() {}
  
  public void documentRelease() {}
  
  public void migrateTo(DTMManager paramDTMManager) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMDocumentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */