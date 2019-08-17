package com.sun.org.apache.xml.internal.dtm.ref.sax2dtm;

import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators;
import com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault;
import com.sun.org.apache.xml.internal.dtm.ref.DTMStringPool;
import com.sun.org.apache.xml.internal.dtm.ref.DTMTreeWalker;
import com.sun.org.apache.xml.internal.dtm.ref.IncrementalSAXSource;
import com.sun.org.apache.xml.internal.dtm.ref.NodeLocator;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.IntStack;
import com.sun.org.apache.xml.internal.utils.IntVector;
import com.sun.org.apache.xml.internal.utils.StringVector;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public class SAX2DTM extends DTMDefaultBaseIterators implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler, DeclHandler, LexicalHandler {
  private static final boolean DEBUG = false;
  
  private IncrementalSAXSource m_incrementalSAXSource = null;
  
  protected FastStringBuffer m_chars;
  
  protected SuballocatedIntVector m_data;
  
  protected IntStack m_parents;
  
  protected int m_previous = 0;
  
  protected Vector m_prefixMappings = new Vector();
  
  protected IntStack m_contextIndexes;
  
  protected int m_textType = 3;
  
  protected int m_coalescedTextType = 3;
  
  protected Locator m_locator = null;
  
  private String m_systemId = null;
  
  protected boolean m_insideDTD = false;
  
  protected DTMTreeWalker m_walker = new DTMTreeWalker();
  
  protected DTMStringPool m_valuesOrPrefixes;
  
  protected boolean m_endDocumentOccured = false;
  
  protected SuballocatedIntVector m_dataOrQName;
  
  protected Map<String, Integer> m_idAttributes = new HashMap();
  
  private static final String[] m_fixednames = { 
      null, null, null, "#text", "#cdata_section", null, null, null, "#comment", "#document", 
      null, "#document-fragment", null };
  
  private Vector m_entities = null;
  
  private static final int ENTITY_FIELD_PUBLICID = 0;
  
  private static final int ENTITY_FIELD_SYSTEMID = 1;
  
  private static final int ENTITY_FIELD_NOTATIONNAME = 2;
  
  private static final int ENTITY_FIELD_NAME = 3;
  
  private static final int ENTITY_FIELDS_PER = 4;
  
  protected int m_textPendingStart = -1;
  
  protected boolean m_useSourceLocationProperty = false;
  
  protected StringVector m_sourceSystemId;
  
  protected IntVector m_sourceLine;
  
  protected IntVector m_sourceColumn;
  
  boolean m_pastFirstElement = false;
  
  public SAX2DTM(DTMManager paramDTMManager, Source paramSource, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean) { this(paramDTMManager, paramSource, paramInt, paramDTMWSFilter, paramXMLStringFactory, paramBoolean, 512, true, false); }
  
  public SAX2DTM(DTMManager paramDTMManager, Source paramSource, int paramInt1, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, boolean paramBoolean3) {
    super(paramDTMManager, paramSource, paramInt1, paramDTMWSFilter, paramXMLStringFactory, paramBoolean1, paramInt2, paramBoolean2, paramBoolean3);
    if (paramInt2 <= 64) {
      this.m_data = new SuballocatedIntVector(paramInt2, 4);
      this.m_dataOrQName = new SuballocatedIntVector(paramInt2, 4);
      this.m_valuesOrPrefixes = new DTMStringPool(16);
      this.m_chars = new FastStringBuffer(7, 10);
      this.m_contextIndexes = new IntStack(4);
      this.m_parents = new IntStack(4);
    } else {
      this.m_data = new SuballocatedIntVector(paramInt2, 32);
      this.m_dataOrQName = new SuballocatedIntVector(paramInt2, 32);
      this.m_valuesOrPrefixes = new DTMStringPool();
      this.m_chars = new FastStringBuffer(10, 13);
      this.m_contextIndexes = new IntStack();
      this.m_parents = new IntStack();
    } 
    this.m_data.addElement(0);
    this.m_useSourceLocationProperty = paramDTMManager.getSource_location();
    this.m_sourceSystemId = this.m_useSourceLocationProperty ? new StringVector() : null;
    this.m_sourceLine = this.m_useSourceLocationProperty ? new IntVector() : null;
    this.m_sourceColumn = this.m_useSourceLocationProperty ? new IntVector() : null;
  }
  
  public void setUseSourceLocation(boolean paramBoolean) { this.m_useSourceLocationProperty = paramBoolean; }
  
  protected int _dataOrQName(int paramInt) {
    if (paramInt < this.m_size)
      return this.m_dataOrQName.elementAt(paramInt); 
    do {
      boolean bool = nextNode();
      if (!bool)
        return -1; 
    } while (paramInt >= this.m_size);
    return this.m_dataOrQName.elementAt(paramInt);
  }
  
  public void clearCoRoutine() { clearCoRoutine(true); }
  
  public void clearCoRoutine(boolean paramBoolean) {
    if (null != this.m_incrementalSAXSource) {
      if (paramBoolean)
        this.m_incrementalSAXSource.deliverMoreNodes(false); 
      this.m_incrementalSAXSource = null;
    } 
  }
  
  public void setIncrementalSAXSource(IncrementalSAXSource paramIncrementalSAXSource) {
    this.m_incrementalSAXSource = paramIncrementalSAXSource;
    paramIncrementalSAXSource.setContentHandler(this);
    paramIncrementalSAXSource.setLexicalHandler(this);
    paramIncrementalSAXSource.setDTDHandler(this);
  }
  
  public ContentHandler getContentHandler() { return this.m_incrementalSAXSource.getClass().getName().equals("com.sun.org.apache.xml.internal.dtm.ref.IncrementalSAXSource_Filter") ? (ContentHandler)this.m_incrementalSAXSource : this; }
  
  public LexicalHandler getLexicalHandler() { return this.m_incrementalSAXSource.getClass().getName().equals("com.sun.org.apache.xml.internal.dtm.ref.IncrementalSAXSource_Filter") ? (LexicalHandler)this.m_incrementalSAXSource : this; }
  
  public EntityResolver getEntityResolver() { return this; }
  
  public DTDHandler getDTDHandler() { return this; }
  
  public ErrorHandler getErrorHandler() { return this; }
  
  public DeclHandler getDeclHandler() { return this; }
  
  public boolean needsTwoThreads() { return (null != this.m_incrementalSAXSource); }
  
  public void dispatchCharactersEvents(int paramInt, ContentHandler paramContentHandler, boolean paramBoolean) throws SAXException {
    int i = makeNodeIdentity(paramInt);
    if (i == -1)
      return; 
    short s = _type(i);
    if (isTextType(s)) {
      int j = this.m_dataOrQName.elementAt(i);
      int k = this.m_data.elementAt(j);
      int m = this.m_data.elementAt(j + 1);
      if (paramBoolean) {
        this.m_chars.sendNormalizedSAXcharacters(paramContentHandler, k, m);
      } else {
        this.m_chars.sendSAXcharacters(paramContentHandler, k, m);
      } 
    } else {
      int j = _firstch(i);
      if (-1 != j) {
        int k = -1;
        int m = 0;
        int n = i;
        i = j;
        do {
          s = _type(i);
          if (isTextType(s)) {
            int i1 = _dataOrQName(i);
            if (-1 == k)
              k = this.m_data.elementAt(i1); 
            m += this.m_data.elementAt(i1 + 1);
          } 
          i = getNextNodeIdentity(i);
        } while (-1 != i && _parent(i) >= n);
        if (m > 0)
          if (paramBoolean) {
            this.m_chars.sendNormalizedSAXcharacters(paramContentHandler, k, m);
          } else {
            this.m_chars.sendSAXcharacters(paramContentHandler, k, m);
          }  
      } else if (s != 1) {
        int k = _dataOrQName(i);
        if (k < 0) {
          k = -k;
          k = this.m_data.elementAt(k + 1);
        } 
        String str = this.m_valuesOrPrefixes.indexToString(k);
        if (paramBoolean) {
          FastStringBuffer.sendNormalizedSAXcharacters(str.toCharArray(), 0, str.length(), paramContentHandler);
        } else {
          paramContentHandler.characters(str.toCharArray(), 0, str.length());
        } 
      } 
    } 
  }
  
  public String getNodeName(int paramInt) {
    int i = getExpandedTypeID(paramInt);
    int j = this.m_expandedNameTable.getNamespaceID(i);
    if (0 == j) {
      short s = getNodeType(paramInt);
      return (s == 13) ? ((null == this.m_expandedNameTable.getLocalName(i)) ? "xmlns" : ("xmlns:" + this.m_expandedNameTable.getLocalName(i))) : ((0 == this.m_expandedNameTable.getLocalNameID(i)) ? m_fixednames[s] : this.m_expandedNameTable.getLocalName(i));
    } 
    int k = this.m_dataOrQName.elementAt(makeNodeIdentity(paramInt));
    if (k < 0) {
      k = -k;
      k = this.m_data.elementAt(k);
    } 
    return this.m_valuesOrPrefixes.indexToString(k);
  }
  
  public String getNodeNameX(int paramInt) {
    int i = getExpandedTypeID(paramInt);
    int j = this.m_expandedNameTable.getNamespaceID(i);
    if (0 == j) {
      String str = this.m_expandedNameTable.getLocalName(i);
      return (str == null) ? "" : str;
    } 
    int k = this.m_dataOrQName.elementAt(makeNodeIdentity(paramInt));
    if (k < 0) {
      k = -k;
      k = this.m_data.elementAt(k);
    } 
    return this.m_valuesOrPrefixes.indexToString(k);
  }
  
  public boolean isAttributeSpecified(int paramInt) { return true; }
  
  public String getDocumentTypeDeclarationSystemIdentifier() {
    error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
    return null;
  }
  
  protected int getNextNodeIdentity(int paramInt) {
    while (++paramInt >= this.m_size) {
      if (null == this.m_incrementalSAXSource)
        return -1; 
      nextNode();
    } 
    return paramInt;
  }
  
  public void dispatchToEvents(int paramInt, ContentHandler paramContentHandler) throws SAXException {
    dTMTreeWalker = this.m_walker;
    ContentHandler contentHandler = dTMTreeWalker.getcontentHandler();
    if (null != contentHandler)
      dTMTreeWalker = new DTMTreeWalker(); 
    dTMTreeWalker.setcontentHandler(paramContentHandler);
    dTMTreeWalker.setDTM(this);
    try {
      dTMTreeWalker.traverse(paramInt);
    } finally {
      dTMTreeWalker.setcontentHandler(null);
    } 
  }
  
  public int getNumberOfNodes() { return this.m_size; }
  
  protected boolean nextNode() {
    if (null == this.m_incrementalSAXSource)
      return false; 
    if (this.m_endDocumentOccured) {
      clearCoRoutine();
      return false;
    } 
    Object object = this.m_incrementalSAXSource.deliverMoreNodes(true);
    if (!(object instanceof Boolean)) {
      if (object instanceof RuntimeException)
        throw (RuntimeException)object; 
      if (object instanceof Exception)
        throw new WrappedRuntimeException((Exception)object); 
      clearCoRoutine();
      return false;
    } 
    if (object != Boolean.TRUE)
      clearCoRoutine(); 
    return true;
  }
  
  private final boolean isTextType(int paramInt) { return (3 == paramInt || 4 == paramInt); }
  
  protected int addNode(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean) {
    int i = this.m_size++;
    if (this.m_dtmIdent.size() == i >>> 16)
      addNewDTMID(i); 
    this.m_firstch.addElement(paramBoolean ? -2 : -1);
    this.m_nextsib.addElement(-2);
    this.m_parent.addElement(paramInt3);
    this.m_exptype.addElement(paramInt2);
    this.m_dataOrQName.addElement(paramInt5);
    if (this.m_prevsib != null)
      this.m_prevsib.addElement(paramInt4); 
    if (-1 != paramInt4)
      this.m_nextsib.setElementAt(i, paramInt4); 
    if (this.m_locator != null && this.m_useSourceLocationProperty)
      setSourceLocation(); 
    switch (paramInt1) {
      case 13:
        declareNamespaceInContext(paramInt3, i);
      case 2:
        return i;
    } 
    if (-1 == paramInt4 && -1 != paramInt3)
      this.m_firstch.setElementAt(i, paramInt3); 
  }
  
  protected void addNewDTMID(int paramInt) {
    try {
      if (this.m_mgr == null)
        throw new ClassCastException(); 
      DTMManagerDefault dTMManagerDefault = (DTMManagerDefault)this.m_mgr;
      int i = dTMManagerDefault.getFirstFreeDTMID();
      dTMManagerDefault.addDTM(this, i, paramInt);
      this.m_dtmIdent.addElement(i << 16);
    } catch (ClassCastException classCastException) {
      error(XMLMessages.createXMLMessage("ER_NO_DTMIDS_AVAIL", null));
    } 
  }
  
  public void migrateTo(DTMManager paramDTMManager) {
    super.migrateTo(paramDTMManager);
    int i = this.m_dtmIdent.size();
    int j = this.m_mgrDefault.getFirstFreeDTMID();
    int k = 0;
    for (byte b = 0; b < i; b++) {
      this.m_dtmIdent.setElementAt(j << 16, b);
      this.m_mgrDefault.addDTM(this, j, k);
      j++;
      k += 65536;
    } 
  }
  
  protected void setSourceLocation() {
    this.m_sourceSystemId.addElement(this.m_locator.getSystemId());
    this.m_sourceLine.addElement(this.m_locator.getLineNumber());
    this.m_sourceColumn.addElement(this.m_locator.getColumnNumber());
    if (this.m_sourceSystemId.size() != this.m_size) {
      String str = "CODING ERROR in Source Location: " + this.m_size + " != " + this.m_sourceSystemId.size();
      System.err.println(str);
      throw new RuntimeException(str);
    } 
  }
  
  public String getNodeValue(int paramInt) {
    int i = makeNodeIdentity(paramInt);
    short s = _type(i);
    if (isTextType(s)) {
      int k = _dataOrQName(i);
      int m = this.m_data.elementAt(k);
      int n = this.m_data.elementAt(k + 1);
      return this.m_chars.getString(m, n);
    } 
    if (1 == s || 11 == s || 9 == s)
      return null; 
    int j = _dataOrQName(i);
    if (j < 0) {
      j = -j;
      j = this.m_data.elementAt(j + 1);
    } 
    return this.m_valuesOrPrefixes.indexToString(j);
  }
  
  public String getLocalName(int paramInt) { return this.m_expandedNameTable.getLocalName(_exptype(makeNodeIdentity(paramInt))); }
  
  public String getUnparsedEntityURI(String paramString) {
    String str = "";
    if (null == this.m_entities)
      return str; 
    int i = this.m_entities.size();
    for (boolean bool = false; bool < i; bool += true) {
      String str1 = (String)this.m_entities.elementAt(bool + 3);
      if (null != str1 && str1.equals(paramString)) {
        String str2 = (String)this.m_entities.elementAt(bool + 2);
        if (null != str2) {
          str = (String)this.m_entities.elementAt(bool + true);
          if (null == str)
            str = (String)this.m_entities.elementAt(bool + false); 
        } 
        break;
      } 
    } 
    return str;
  }
  
  public String getPrefix(int paramInt) {
    int i = makeNodeIdentity(paramInt);
    short s = _type(i);
    if (1 == s) {
      int j = _dataOrQName(i);
      if (0 == j)
        return ""; 
      String str = this.m_valuesOrPrefixes.indexToString(j);
      return getPrefix(str, null);
    } 
    if (2 == s) {
      int j = _dataOrQName(i);
      if (j < 0) {
        j = this.m_data.elementAt(-j);
        String str = this.m_valuesOrPrefixes.indexToString(j);
        return getPrefix(str, null);
      } 
    } 
    return "";
  }
  
  public int getAttributeNode(int paramInt, String paramString1, String paramString2) {
    int i;
    for (i = getFirstAttribute(paramInt); -1 != i; i = getNextAttribute(i)) {
      String str1 = getNamespaceURI(i);
      String str2 = getLocalName(i);
      boolean bool = (paramString1 == str1 || (paramString1 != null && paramString1.equals(str1))) ? 1 : 0;
      if (bool && paramString2.equals(str2))
        return i; 
    } 
    return -1;
  }
  
  public String getDocumentTypeDeclarationPublicIdentifier() {
    error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
    return null;
  }
  
  public String getNamespaceURI(int paramInt) { return this.m_expandedNameTable.getNamespace(_exptype(makeNodeIdentity(paramInt))); }
  
  public XMLString getStringValue(int paramInt) {
    short s;
    int i = makeNodeIdentity(paramInt);
    if (i == -1) {
      s = -1;
    } else {
      s = _type(i);
    } 
    if (isTextType(s)) {
      int k = _dataOrQName(i);
      int m = this.m_data.elementAt(k);
      int n = this.m_data.elementAt(k + 1);
      return this.m_xstrf.newstr(this.m_chars, m, n);
    } 
    int j = _firstch(i);
    if (-1 != j) {
      int k = -1;
      int m = 0;
      int n = i;
      i = j;
      do {
        s = _type(i);
        if (isTextType(s)) {
          int i1 = _dataOrQName(i);
          if (-1 == k)
            k = this.m_data.elementAt(i1); 
          m += this.m_data.elementAt(i1 + 1);
        } 
        i = getNextNodeIdentity(i);
      } while (-1 != i && _parent(i) >= n);
      if (m > 0)
        return this.m_xstrf.newstr(this.m_chars, k, m); 
    } else if (s != 1) {
      int k = _dataOrQName(i);
      if (k < 0) {
        k = -k;
        k = this.m_data.elementAt(k + 1);
      } 
      return this.m_xstrf.newstr(this.m_valuesOrPrefixes.indexToString(k));
    } 
    return this.m_xstrf.emptystr();
  }
  
  public boolean isWhitespace(int paramInt) {
    short s;
    int i = makeNodeIdentity(paramInt);
    if (i == -1) {
      s = -1;
    } else {
      s = _type(i);
    } 
    if (isTextType(s)) {
      int j = _dataOrQName(i);
      int k = this.m_data.elementAt(j);
      int m = this.m_data.elementAt(j + 1);
      return this.m_chars.isWhitespace(k, m);
    } 
    return false;
  }
  
  public int getElementById(String paramString) {
    Integer integer;
    boolean bool = true;
    do {
      integer = (Integer)this.m_idAttributes.get(paramString);
      if (null != integer)
        return makeNodeHandle(integer.intValue()); 
      if (!bool || this.m_endDocumentOccured)
        break; 
      bool = nextNode();
    } while (null == integer);
    return -1;
  }
  
  public String getPrefix(String paramString1, String paramString2) {
    String str;
    int i = -1;
    if (null != paramString2 && paramString2.length() > 0) {
      do {
        i = this.m_prefixMappings.indexOf(paramString2, ++i);
      } while ((i & true) == 0);
      if (i >= 0) {
        str = (String)this.m_prefixMappings.elementAt(i - 1);
      } else if (null != paramString1) {
        int j = paramString1.indexOf(':');
        if (paramString1.equals("xmlns")) {
          str = "";
        } else if (paramString1.startsWith("xmlns:")) {
          str = paramString1.substring(j + 1);
        } else {
          str = (j > 0) ? paramString1.substring(0, j) : null;
        } 
      } else {
        str = null;
      } 
    } else if (null != paramString1) {
      int j = paramString1.indexOf(':');
      if (j > 0) {
        if (paramString1.startsWith("xmlns:")) {
          str = paramString1.substring(j + 1);
        } else {
          str = paramString1.substring(0, j);
        } 
      } else if (paramString1.equals("xmlns")) {
        str = "";
      } else {
        str = null;
      } 
    } else {
      str = null;
    } 
    return str;
  }
  
  public int getIdForNamespace(String paramString) { return this.m_valuesOrPrefixes.stringToIndex(paramString); }
  
  public String getNamespaceURI(String paramString) {
    String str = "";
    int i = this.m_contextIndexes.peek() - 1;
    if (null == paramString)
      paramString = ""; 
    do {
      i = this.m_prefixMappings.indexOf(paramString, ++i);
    } while (i >= 0 && (i & true) == 1);
    if (i > -1)
      str = (String)this.m_prefixMappings.elementAt(i + 1); 
    return str;
  }
  
  public void setIDAttribute(String paramString, int paramInt) { this.m_idAttributes.put(paramString, Integer.valueOf(paramInt)); }
  
  protected void charactersFlush() {
    if (this.m_textPendingStart >= 0) {
      int i = this.m_chars.size() - this.m_textPendingStart;
      boolean bool = false;
      if (getShouldStripWhitespace())
        bool = this.m_chars.isWhitespace(this.m_textPendingStart, i); 
      if (bool) {
        this.m_chars.setLength(this.m_textPendingStart);
      } else if (i > 0) {
        int j = this.m_expandedNameTable.getExpandedTypeID(3);
        int k = this.m_data.size();
        this.m_previous = addNode(this.m_coalescedTextType, j, this.m_parents.peek(), this.m_previous, k, false);
        this.m_data.addElement(this.m_textPendingStart);
        this.m_data.addElement(i);
      } 
      this.m_textPendingStart = -1;
      this.m_textType = this.m_coalescedTextType = 3;
    } 
  }
  
  public InputSource resolveEntity(String paramString1, String paramString2) throws SAXException { return null; }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4) throws SAXException {
    if (null == this.m_entities)
      this.m_entities = new Vector(); 
    try {
      paramString3 = SystemIDResolver.getAbsoluteURI(paramString3, getDocumentBaseURI());
    } catch (Exception exception) {
      throw new SAXException(exception);
    } 
    this.m_entities.addElement(paramString2);
    this.m_entities.addElement(paramString3);
    this.m_entities.addElement(paramString4);
    this.m_entities.addElement(paramString1);
  }
  
  public void setDocumentLocator(Locator paramLocator) {
    this.m_locator = paramLocator;
    this.m_systemId = paramLocator.getSystemId();
  }
  
  public void startDocument() {
    int i = addNode(9, this.m_expandedNameTable.getExpandedTypeID(9), -1, -1, 0, true);
    this.m_parents.push(i);
    this.m_previous = -1;
    this.m_contextIndexes.push(this.m_prefixMappings.size());
  }
  
  public void endDocument() {
    charactersFlush();
    this.m_nextsib.setElementAt(-1, 0);
    if (this.m_firstch.elementAt(0) == -2)
      this.m_firstch.setElementAt(-1, 0); 
    if (-1 != this.m_previous)
      this.m_nextsib.setElementAt(-1, this.m_previous); 
    this.m_parents = null;
    this.m_prefixMappings = null;
    this.m_contextIndexes = null;
    this.m_endDocumentOccured = true;
    this.m_locator = null;
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    if (null == paramString1)
      paramString1 = ""; 
    this.m_prefixMappings.addElement(paramString1);
    this.m_prefixMappings.addElement(paramString2);
  }
  
  public void endPrefixMapping(String paramString) throws SAXException {
    if (null == paramString)
      paramString = ""; 
    int i = this.m_contextIndexes.peek() - 1;
    do {
      i = this.m_prefixMappings.indexOf(paramString, ++i);
    } while (i >= 0 && (i & true) == 1);
    if (i > -1) {
      this.m_prefixMappings.setElementAt("%@$#^@#", i);
      this.m_prefixMappings.setElementAt("%@$#^@#", i + 1);
    } 
  }
  
  protected boolean declAlreadyDeclared(String paramString) {
    int i = this.m_contextIndexes.peek();
    Vector vector = this.m_prefixMappings;
    int j = vector.size();
    for (int k = i; k < j; k += 2) {
      String str = (String)vector.elementAt(k);
      if (str != null && str.equals(paramString))
        return true; 
    } 
    return false;
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    charactersFlush();
    int i = this.m_expandedNameTable.getExpandedTypeID(paramString1, paramString2, 1);
    String str = getPrefix(paramString3, paramString1);
    int j = (null != str) ? this.m_valuesOrPrefixes.stringToIndex(paramString3) : 0;
    int k = addNode(1, i, this.m_parents.peek(), this.m_previous, j, true);
    if (this.m_indexing)
      indexNode(i, k); 
    this.m_parents.push(k);
    int m = this.m_contextIndexes.peek();
    int n = this.m_prefixMappings.size();
    int i1 = -1;
    if (!this.m_pastFirstElement) {
      str = "xml";
      String str1 = "http://www.w3.org/XML/1998/namespace";
      i = this.m_expandedNameTable.getExpandedTypeID(null, str, 13);
      int i3 = this.m_valuesOrPrefixes.stringToIndex(str1);
      i1 = addNode(13, i, k, i1, i3, false);
      this.m_pastFirstElement = true;
    } 
    int i2;
    for (i2 = m; i2 < n; i2 += 2) {
      str = (String)this.m_prefixMappings.elementAt(i2);
      if (str != null) {
        String str1 = (String)this.m_prefixMappings.elementAt(i2 + 1);
        i = this.m_expandedNameTable.getExpandedTypeID(null, str, 13);
        int i3 = this.m_valuesOrPrefixes.stringToIndex(str1);
        i1 = addNode(13, i, k, i1, i3, false);
      } 
    } 
    i2 = paramAttributes.getLength();
    short s;
    for (s = 0; s < i2; s++) {
      byte b;
      String str1 = paramAttributes.getURI(s);
      String str2 = paramAttributes.getQName(s);
      String str3 = paramAttributes.getValue(s);
      str = getPrefix(str2, str1);
      String str4 = paramAttributes.getLocalName(s);
      if (null != str2 && (str2.equals("xmlns") || str2.startsWith("xmlns:"))) {
        if (declAlreadyDeclared(str))
          continue; 
        b = 13;
      } else {
        b = 2;
        if (paramAttributes.getType(s).equalsIgnoreCase("ID"))
          setIDAttribute(str3, k); 
      } 
      if (null == str3)
        str3 = ""; 
      int i3 = this.m_valuesOrPrefixes.stringToIndex(str3);
      if (null != str) {
        j = this.m_valuesOrPrefixes.stringToIndex(str2);
        int i4 = this.m_data.size();
        this.m_data.addElement(j);
        this.m_data.addElement(i3);
        i3 = -i4;
      } 
      i = this.m_expandedNameTable.getExpandedTypeID(str1, str4, b);
      i1 = addNode(b, i, k, i1, i3, false);
      continue;
    } 
    if (-1 != i1)
      this.m_nextsib.setElementAt(-1, i1); 
    if (null != this.m_wsfilter) {
      s = this.m_wsfilter.getShouldStripSpace(makeNodeHandle(k), this);
      boolean bool = (3 == s) ? getShouldStripWhitespace() : ((2 == s) ? 1 : 0);
      pushShouldStripWhitespace(bool);
    } 
    this.m_previous = -1;
    this.m_contextIndexes.push(this.m_prefixMappings.size());
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    charactersFlush();
    this.m_contextIndexes.quickPop(1);
    int i = this.m_contextIndexes.peek();
    if (i != this.m_prefixMappings.size())
      this.m_prefixMappings.setSize(i); 
    int j = this.m_previous;
    this.m_previous = this.m_parents.pop();
    if (-1 == j) {
      this.m_firstch.setElementAt(-1, this.m_previous);
    } else {
      this.m_nextsib.setElementAt(-1, j);
    } 
    popShouldStripWhitespace();
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.m_textPendingStart == -1) {
      this.m_textPendingStart = this.m_chars.size();
      this.m_coalescedTextType = this.m_textType;
    } else if (this.m_textType == 3) {
      this.m_coalescedTextType = 3;
    } 
    this.m_chars.append(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException { characters(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    charactersFlush();
    int i = this.m_expandedNameTable.getExpandedTypeID(null, paramString1, 7);
    int j = this.m_valuesOrPrefixes.stringToIndex(paramString2);
    this.m_previous = addNode(7, i, this.m_parents.peek(), this.m_previous, j, false);
  }
  
  public void skippedEntity(String paramString) throws SAXException {}
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException { System.err.println(paramSAXParseException.getMessage()); }
  
  public void error(SAXParseException paramSAXParseException) throws SAXException { throw paramSAXParseException; }
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException { throw paramSAXParseException; }
  
  public void elementDecl(String paramString1, String paramString2) throws SAXException {}
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws SAXException {}
  
  public void internalEntityDecl(String paramString1, String paramString2) throws SAXException {}
  
  public void externalEntityDecl(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException { this.m_insideDTD = true; }
  
  public void endDTD() { this.m_insideDTD = false; }
  
  public void startEntity(String paramString) throws SAXException {}
  
  public void endEntity(String paramString) throws SAXException {}
  
  public void startCDATA() { this.m_textType = 4; }
  
  public void endCDATA() { this.m_textType = 3; }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.m_insideDTD)
      return; 
    charactersFlush();
    int i = this.m_expandedNameTable.getExpandedTypeID(8);
    int j = this.m_valuesOrPrefixes.stringToIndex(new String(paramArrayOfChar, paramInt1, paramInt2));
    this.m_previous = addNode(8, i, this.m_parents.peek(), this.m_previous, j, false);
  }
  
  public void setProperty(String paramString, Object paramObject) {}
  
  public SourceLocator getSourceLocatorFor(int paramInt) {
    if (this.m_useSourceLocationProperty) {
      paramInt = makeNodeIdentity(paramInt);
      return new NodeLocator(null, this.m_sourceSystemId.elementAt(paramInt), this.m_sourceLine.elementAt(paramInt), this.m_sourceColumn.elementAt(paramInt));
    } 
    return (this.m_locator != null) ? new NodeLocator(null, this.m_locator.getSystemId(), -1, -1) : ((this.m_systemId != null) ? new NodeLocator(null, this.m_systemId, -1, -1) : null);
  }
  
  public String getFixedNames(int paramInt) { return m_fixednames[paramInt]; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\sax2dtm\SAX2DTM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */