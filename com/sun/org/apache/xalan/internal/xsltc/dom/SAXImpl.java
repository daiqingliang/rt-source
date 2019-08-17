package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIterNodeList;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeProxy;
import com.sun.org.apache.xml.internal.dtm.ref.EmptyIterator;
import com.sun.org.apache.xml.internal.dtm.ref.ExpandedNameTable;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM2;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.serializer.ToXMLSAXHandler;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public final class SAXImpl extends SAX2DTM2 implements DOMEnhancedForDTM, DOMBuilder {
  private int _uriCount = 0;
  
  private int[] _xmlSpaceStack;
  
  private int _idx = 1;
  
  private boolean _preserve = false;
  
  private static final String XML_PREFIX = "xml";
  
  private static final String XMLSPACE_STRING = "xml:space";
  
  private static final String PRESERVE_STRING = "preserve";
  
  private static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
  
  private boolean _escaping = true;
  
  private boolean _disableEscaping = false;
  
  private int _textNodeToProcess = -1;
  
  private static final String EMPTYSTRING = "";
  
  private static final DTMAxisIterator EMPTYITERATOR = EmptyIterator.getInstance();
  
  private int _namesSize = -1;
  
  private Map<Integer, Integer> _nsIndex = new HashMap();
  
  private int _size = 0;
  
  private BitArray _dontEscape = null;
  
  private static int _documentURIIndex = 0;
  
  private Document _document;
  
  private Map<Node, Integer> _node2Ids = null;
  
  private boolean _hasDOMSource = false;
  
  private XSLTCDTMManager _dtmManager;
  
  private Node[] _nodes;
  
  private NodeList[] _nodeLists;
  
  public void setDocumentURI(String paramString) {
    if (paramString != null)
      setDocumentBaseURI(SystemIDResolver.getAbsoluteURI(paramString)); 
  }
  
  public String getDocumentURI() {
    String str = getDocumentBaseURI();
    return (str != null) ? str : ("rtf" + _documentURIIndex++);
  }
  
  public String getDocumentURI(int paramInt) { return getDocumentURI(); }
  
  public void setupMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt, String[] paramArrayOfString3) {}
  
  public String lookupNamespace(int paramInt, String paramString) throws TransletException {
    SAX2DTM2.AncestorIterator ancestorIterator = new SAX2DTM2.AncestorIterator(this);
    if (isElement(paramInt))
      ancestorIterator.includeSelf(); 
    ancestorIterator.setStartNode(paramInt);
    int i;
    while ((i = ancestorIterator.next()) != -1) {
      DTMDefaultBaseIterators.NamespaceIterator namespaceIterator = new DTMDefaultBaseIterators.NamespaceIterator(this);
      namespaceIterator.setStartNode(i);
      int j;
      while ((j = namespaceIterator.next()) != -1) {
        if (getLocalName(j).equals(paramString))
          return getNodeValue(j); 
      } 
    } 
    BasisLibrary.runTimeError("NAMESPACE_PREFIX_ERR", paramString);
    return null;
  }
  
  public boolean isElement(int paramInt) { return (getNodeType(paramInt) == 1); }
  
  public boolean isAttribute(int paramInt) { return (getNodeType(paramInt) == 2); }
  
  public int getSize() { return getNumberOfNodes(); }
  
  public void setFilter(StripFilter paramStripFilter) {}
  
  public boolean lessThan(int paramInt1, int paramInt2) { return (paramInt1 == -1) ? false : ((paramInt2 == -1) ? true : ((paramInt1 < paramInt2))); }
  
  public Node makeNode(int paramInt) {
    if (this._nodes == null)
      this._nodes = new Node[this._namesSize]; 
    int i = makeNodeIdentity(paramInt);
    return (i < 0) ? null : ((i < this._nodes.length) ? ((this._nodes[i] != null) ? this._nodes[i] : (this._nodes[i] = new DTMNodeProxy(this, paramInt))) : new DTMNodeProxy(this, paramInt));
  }
  
  public Node makeNode(DTMAxisIterator paramDTMAxisIterator) { return makeNode(paramDTMAxisIterator.next()); }
  
  public NodeList makeNodeList(int paramInt) {
    if (this._nodeLists == null)
      this._nodeLists = new NodeList[this._namesSize]; 
    int i = makeNodeIdentity(paramInt);
    return (i < 0) ? null : ((i < this._nodeLists.length) ? ((this._nodeLists[i] != null) ? this._nodeLists[i] : (this._nodeLists[i] = new DTMAxisIterNodeList(this, new DTMDefaultBaseIterators.SingletonIterator(this, paramInt)))) : new DTMAxisIterNodeList(this, new DTMDefaultBaseIterators.SingletonIterator(this, paramInt)));
  }
  
  public NodeList makeNodeList(DTMAxisIterator paramDTMAxisIterator) { return new DTMAxisIterNodeList(this, paramDTMAxisIterator); }
  
  public DTMAxisIterator getNodeValueIterator(DTMAxisIterator paramDTMAxisIterator, int paramInt, String paramString, boolean paramBoolean) { return new NodeValueIterator(paramDTMAxisIterator, paramInt, paramString, paramBoolean); }
  
  public DTMAxisIterator orderNodes(DTMAxisIterator paramDTMAxisIterator, int paramInt) { return new DupFilterIterator(paramDTMAxisIterator); }
  
  public DTMAxisIterator getIterator() { return new DTMDefaultBaseIterators.SingletonIterator(this, getDocument(), true); }
  
  public int getNSType(int paramInt) {
    String str = getNamespaceURI(paramInt);
    if (str == null)
      return 0; 
    int i = getIdForNamespace(str);
    return ((Integer)this._nsIndex.get(new Integer(i))).intValue();
  }
  
  public int getNamespaceType(int paramInt) { return super.getNamespaceType(paramInt); }
  
  public int getGeneralizedType(String paramString) { return getGeneralizedType(paramString, true); }
  
  public int getGeneralizedType(String paramString, boolean paramBoolean) {
    byte b;
    String str2 = null;
    int i = -1;
    if ((i = paramString.lastIndexOf(":")) > -1)
      str2 = paramString.substring(0, i); 
    int j = i + 1;
    if (paramString.charAt(j) == '@') {
      b = 2;
      j++;
    } else {
      b = 1;
    } 
    String str1 = (j == 0) ? paramString : paramString.substring(j);
    return this.m_expandedNameTable.getExpandedTypeID(str2, str1, b, paramBoolean);
  }
  
  public short[] getMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt) {
    if (this._namesSize < 0)
      return getMapping2(paramArrayOfString1, paramArrayOfString2, paramArrayOfInt); 
    int i = paramArrayOfString1.length;
    int j = this.m_expandedNameTable.getSize();
    short[] arrayOfShort = new short[j];
    byte b;
    for (b = 0; b < 14; b++)
      arrayOfShort[b] = (short)b; 
    for (b = 14; b < j; b++)
      arrayOfShort[b] = this.m_expandedNameTable.getType(b); 
    for (b = 0; b < i; b++) {
      int k = this.m_expandedNameTable.getExpandedTypeID(paramArrayOfString2[b], paramArrayOfString1[b], paramArrayOfInt[b], true);
      if (k >= 0 && k < j)
        arrayOfShort[k] = (short)(b + 14); 
    } 
    return arrayOfShort;
  }
  
  public int[] getReverseMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt) {
    int[] arrayOfInt = new int[paramArrayOfString1.length + 14];
    byte b;
    for (b = 0; b < 14; b++)
      arrayOfInt[b] = b; 
    for (b = 0; b < paramArrayOfString1.length; b++) {
      int i = this.m_expandedNameTable.getExpandedTypeID(paramArrayOfString2[b], paramArrayOfString1[b], paramArrayOfInt[b], true);
      arrayOfInt[b + 14] = i;
    } 
    return arrayOfInt;
  }
  
  private short[] getMapping2(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt) {
    int i = paramArrayOfString1.length;
    int j = this.m_expandedNameTable.getSize();
    int[] arrayOfInt = null;
    if (i > 0)
      arrayOfInt = new int[i]; 
    int k = j;
    byte b;
    for (b = 0; b < i; b++) {
      arrayOfInt[b] = this.m_expandedNameTable.getExpandedTypeID(paramArrayOfString2[b], paramArrayOfString1[b], paramArrayOfInt[b], false);
      if (this._namesSize < 0 && arrayOfInt[b] >= k)
        k = arrayOfInt[b] + 1; 
    } 
    short[] arrayOfShort = new short[k];
    for (b = 0; b < 14; b++)
      arrayOfShort[b] = (short)b; 
    for (b = 14; b < j; b++)
      arrayOfShort[b] = this.m_expandedNameTable.getType(b); 
    for (b = 0; b < i; b++) {
      int m = arrayOfInt[b];
      if (m >= 0 && m < k)
        arrayOfShort[m] = (short)(b + 14); 
    } 
    return arrayOfShort;
  }
  
  public short[] getNamespaceMapping(String[] paramArrayOfString) {
    int i = paramArrayOfString.length;
    int j = this._uriCount;
    short[] arrayOfShort = new short[j];
    byte b;
    for (b = 0; b < j; b++)
      arrayOfShort[b] = -1; 
    for (b = 0; b < i; b++) {
      int k = getIdForNamespace(paramArrayOfString[b]);
      Integer integer = (Integer)this._nsIndex.get(Integer.valueOf(k));
      if (integer != null)
        arrayOfShort[integer.intValue()] = (short)b; 
    } 
    return arrayOfShort;
  }
  
  public short[] getReverseNamespaceMapping(String[] paramArrayOfString) {
    int i = paramArrayOfString.length;
    short[] arrayOfShort = new short[i];
    for (byte b = 0; b < i; b++) {
      int j = getIdForNamespace(paramArrayOfString[b]);
      Integer integer = (Integer)this._nsIndex.get(Integer.valueOf(j));
      arrayOfShort[b] = (integer == null) ? -1 : integer.shortValue();
    } 
    return arrayOfShort;
  }
  
  public SAXImpl(XSLTCDTMManager paramXSLTCDTMManager, Source paramSource, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean1, boolean paramBoolean2) { this(paramXSLTCDTMManager, paramSource, paramInt, paramDTMWSFilter, paramXMLStringFactory, paramBoolean1, 512, paramBoolean2, false); }
  
  public SAXImpl(XSLTCDTMManager paramXSLTCDTMManager, Source paramSource, int paramInt1, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, boolean paramBoolean3) {
    super(paramXSLTCDTMManager, paramSource, paramInt1, paramDTMWSFilter, paramXMLStringFactory, paramBoolean1, paramInt2, false, paramBoolean2, paramBoolean3);
    this._dtmManager = paramXSLTCDTMManager;
    this._size = paramInt2;
    this._xmlSpaceStack = new int[(paramInt2 <= 64) ? 4 : 64];
    this._xmlSpaceStack[0] = 0;
    if (paramSource instanceof DOMSource) {
      this._hasDOMSource = true;
      DOMSource dOMSource = (DOMSource)paramSource;
      Node node = dOMSource.getNode();
      if (node instanceof Document) {
        this._document = (Document)node;
      } else {
        this._document = node.getOwnerDocument();
      } 
      this._node2Ids = new HashMap();
    } 
  }
  
  public void migrateTo(DTMManager paramDTMManager) {
    super.migrateTo(paramDTMManager);
    if (paramDTMManager instanceof XSLTCDTMManager)
      this._dtmManager = (XSLTCDTMManager)paramDTMManager; 
  }
  
  public int getElementById(String paramString) {
    Element element = this._document.getElementById(paramString);
    if (element != null) {
      Integer integer = (Integer)this._node2Ids.get(element);
      return (integer != null) ? integer.intValue() : -1;
    } 
    return -1;
  }
  
  public boolean hasDOMSource() { return this._hasDOMSource; }
  
  private void xmlSpaceDefine(String paramString, int paramInt) {
    boolean bool = paramString.equals("preserve");
    if (bool != this._preserve) {
      this._xmlSpaceStack[this._idx++] = paramInt;
      this._preserve = bool;
    } 
  }
  
  private void xmlSpaceRevert(int paramInt) {
    if (paramInt == this._xmlSpaceStack[this._idx - 1]) {
      this._idx--;
      this._preserve = !this._preserve;
    } 
  }
  
  protected boolean getShouldStripWhitespace() { return this._preserve ? false : super.getShouldStripWhitespace(); }
  
  private void handleTextEscaping() {
    if (this._disableEscaping && this._textNodeToProcess != -1 && _type(this._textNodeToProcess) == 3) {
      if (this._dontEscape == null)
        this._dontEscape = new BitArray(this._size); 
      if (this._textNodeToProcess >= this._dontEscape.size())
        this._dontEscape.resize(this._dontEscape.size() * 2); 
      this._dontEscape.setBit(this._textNodeToProcess);
      this._disableEscaping = false;
    } 
    this._textNodeToProcess = -1;
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    super.characters(paramArrayOfChar, paramInt1, paramInt2);
    this._disableEscaping = !this._escaping;
    this._textNodeToProcess = getNumberOfNodes();
  }
  
  public void startDocument() {
    super.startDocument();
    this._nsIndex.put(Integer.valueOf(0), Integer.valueOf(this._uriCount++));
    definePrefixAndUri("xml", "http://www.w3.org/XML/1998/namespace");
  }
  
  public void endDocument() {
    super.endDocument();
    handleTextEscaping();
    this._namesSize = this.m_expandedNameTable.getSize();
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes, Node paramNode) throws SAXException {
    startElement(paramString1, paramString2, paramString3, paramAttributes);
    if (this.m_buildIdIndex)
      this._node2Ids.put(paramNode, new Integer(this.m_parents.peek())); 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    super.startElement(paramString1, paramString2, paramString3, paramAttributes);
    handleTextEscaping();
    if (this.m_wsfilter != null) {
      int i = paramAttributes.getIndex("xml:space");
      if (i >= 0)
        xmlSpaceDefine(paramAttributes.getValue(i), this.m_parents.peek()); 
    } 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    super.endElement(paramString1, paramString2, paramString3);
    handleTextEscaping();
    if (this.m_wsfilter != null)
      xmlSpaceRevert(this.m_previous); 
  }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    super.processingInstruction(paramString1, paramString2);
    handleTextEscaping();
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    super.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
    this._textNodeToProcess = getNumberOfNodes();
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    super.startPrefixMapping(paramString1, paramString2);
    handleTextEscaping();
    definePrefixAndUri(paramString1, paramString2);
  }
  
  private void definePrefixAndUri(String paramString1, String paramString2) throws SAXException {
    Integer integer = new Integer(getIdForNamespace(paramString2));
    if (this._nsIndex.get(integer) == null)
      this._nsIndex.put(integer, Integer.valueOf(this._uriCount++)); 
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    super.comment(paramArrayOfChar, paramInt1, paramInt2);
    handleTextEscaping();
  }
  
  public boolean setEscaping(boolean paramBoolean) {
    boolean bool = this._escaping;
    this._escaping = paramBoolean;
    return bool;
  }
  
  public void print(int paramInt1, int paramInt2) {
    switch (getNodeType(paramInt1)) {
      case 0:
      case 9:
        print(getFirstChild(paramInt1), paramInt2);
        return;
      case 3:
      case 7:
      case 8:
        System.out.print(getStringValueX(paramInt1));
        return;
    } 
    String str = getNodeName(paramInt1);
    System.out.print("<" + str);
    int i;
    for (i = getFirstAttribute(paramInt1); i != -1; i = getNextAttribute(i))
      System.out.print("\n" + getNodeName(i) + "=\"" + getStringValueX(i) + "\""); 
    System.out.print('>');
    for (i = getFirstChild(paramInt1); i != -1; i = getNextSibling(i))
      print(i, paramInt2 + 1); 
    System.out.println("</" + str + '>');
  }
  
  public String getNodeName(int paramInt) {
    int i = paramInt;
    short s = getNodeType(i);
    switch (s) {
      case 0:
      case 3:
      case 8:
      case 9:
        return "";
      case 13:
        return getLocalName(i);
    } 
    return super.getNodeName(i);
  }
  
  public String getNamespaceName(int paramInt) {
    String str;
    return (paramInt == -1) ? "" : (((str = getNamespaceURI(paramInt)) == null) ? "" : str);
  }
  
  public int getAttributeNode(int paramInt1, int paramInt2) {
    for (int i = getFirstAttribute(paramInt2); i != -1; i = getNextAttribute(i)) {
      if (getExpandedTypeID(i) == paramInt1)
        return i; 
    } 
    return -1;
  }
  
  public String getAttributeValue(int paramInt1, int paramInt2) {
    int i = getAttributeNode(paramInt1, paramInt2);
    return (i != -1) ? getStringValueX(i) : "";
  }
  
  public String getAttributeValue(String paramString, int paramInt) { return getAttributeValue(getGeneralizedType(paramString), paramInt); }
  
  public DTMAxisIterator getChildren(int paramInt) { return (new SAX2DTM2.ChildrenIterator(this)).setStartNode(paramInt); }
  
  public DTMAxisIterator getTypedChildren(int paramInt) { return new SAX2DTM2.TypedChildrenIterator(this, paramInt); }
  
  public DTMAxisIterator getAxisIterator(int paramInt) {
    switch (paramInt) {
      case 13:
        return new DTMDefaultBaseIterators.SingletonIterator(this);
      case 3:
        return new SAX2DTM2.ChildrenIterator(this);
      case 10:
        return new SAX2DTM2.ParentIterator(this);
      case 0:
        return new SAX2DTM2.AncestorIterator(this);
      case 1:
        return (new SAX2DTM2.AncestorIterator(this)).includeSelf();
      case 2:
        return new SAX2DTM2.AttributeIterator(this);
      case 4:
        return new SAX2DTM2.DescendantIterator(this);
      case 5:
        return (new SAX2DTM2.DescendantIterator(this)).includeSelf();
      case 6:
        return new SAX2DTM2.FollowingIterator(this);
      case 11:
        return new SAX2DTM2.PrecedingIterator(this);
      case 7:
        return new SAX2DTM2.FollowingSiblingIterator(this);
      case 12:
        return new SAX2DTM2.PrecedingSiblingIterator(this);
      case 9:
        return new DTMDefaultBaseIterators.NamespaceIterator(this);
      case 19:
        return new DTMDefaultBaseIterators.RootIterator(this);
    } 
    BasisLibrary.runTimeError("AXIS_SUPPORT_ERR", Axis.getNames(paramInt));
    return null;
  }
  
  public DTMAxisIterator getTypedAxisIterator(int paramInt1, int paramInt2) {
    if (paramInt1 == 3)
      return new SAX2DTM2.TypedChildrenIterator(this, paramInt2); 
    if (paramInt2 == -1)
      return EMPTYITERATOR; 
    switch (paramInt1) {
      case 13:
        return new SAX2DTM2.TypedSingletonIterator(this, paramInt2);
      case 3:
        return new SAX2DTM2.TypedChildrenIterator(this, paramInt2);
      case 10:
        return (new SAX2DTM2.ParentIterator(this)).setNodeType(paramInt2);
      case 0:
        return new SAX2DTM2.TypedAncestorIterator(this, paramInt2);
      case 1:
        return (new SAX2DTM2.TypedAncestorIterator(this, paramInt2)).includeSelf();
      case 2:
        return new SAX2DTM2.TypedAttributeIterator(this, paramInt2);
      case 4:
        return new SAX2DTM2.TypedDescendantIterator(this, paramInt2);
      case 5:
        return (new SAX2DTM2.TypedDescendantIterator(this, paramInt2)).includeSelf();
      case 6:
        return new SAX2DTM2.TypedFollowingIterator(this, paramInt2);
      case 11:
        return new SAX2DTM2.TypedPrecedingIterator(this, paramInt2);
      case 7:
        return new SAX2DTM2.TypedFollowingSiblingIterator(this, paramInt2);
      case 12:
        return new SAX2DTM2.TypedPrecedingSiblingIterator(this, paramInt2);
      case 9:
        return new TypedNamespaceIterator(paramInt2);
      case 19:
        return new SAX2DTM2.TypedRootIterator(this, paramInt2);
    } 
    BasisLibrary.runTimeError("TYPED_AXIS_SUPPORT_ERR", Axis.getNames(paramInt1));
    return null;
  }
  
  public DTMAxisIterator getNamespaceAxisIterator(int paramInt1, int paramInt2) {
    if (paramInt2 == -1)
      return EMPTYITERATOR; 
    switch (paramInt1) {
      case 3:
        return new NamespaceChildrenIterator(paramInt2);
      case 2:
        return new NamespaceAttributeIterator(paramInt2);
    } 
    return new NamespaceWildcardIterator(paramInt1, paramInt2);
  }
  
  public DTMAxisIterator getTypedDescendantIterator(int paramInt) { return new SAX2DTM2.TypedDescendantIterator(this, paramInt); }
  
  public DTMAxisIterator getNthDescendant(int paramInt1, int paramInt2, boolean paramBoolean) { return new DTMDefaultBaseIterators.NthDescendantIterator(this, paramInt2); }
  
  public void characters(int paramInt, SerializationHandler paramSerializationHandler) throws TransletException {
    if (paramInt != -1)
      try {
        dispatchCharactersEvents(paramInt, paramSerializationHandler, false);
      } catch (SAXException sAXException) {
        throw new TransletException(sAXException);
      }  
  }
  
  public void copy(DTMAxisIterator paramDTMAxisIterator, SerializationHandler paramSerializationHandler) throws TransletException {
    int i;
    while ((i = paramDTMAxisIterator.next()) != -1)
      copy(i, paramSerializationHandler); 
  }
  
  public void copy(SerializationHandler paramSerializationHandler) throws TransletException { copy(getDocument(), paramSerializationHandler); }
  
  public void copy(int paramInt, SerializationHandler paramSerializationHandler) throws TransletException { copy(paramInt, paramSerializationHandler, false); }
  
  private final void copy(int paramInt, SerializationHandler paramSerializationHandler, boolean paramBoolean) throws TransletException {
    int i = makeNodeIdentity(paramInt);
    int j = _exptype2(i);
    int k = _exptype2Type(j);
    try {
      boolean bool2;
      boolean bool1;
      switch (k) {
        case 0:
        case 9:
          for (bool1 = _firstch2(i); bool1 != -1; bool1 = _nextsib2(bool1))
            copy(makeNodeHandle(bool1), paramSerializationHandler, true); 
          return;
        case 7:
          copyPI(paramInt, paramSerializationHandler);
          return;
        case 8:
          paramSerializationHandler.comment(getStringValueX(paramInt));
          return;
        case 3:
          bool1 = false;
          bool2 = false;
          if (this._dontEscape != null) {
            bool2 = this._dontEscape.getBit(getNodeIdent(paramInt));
            if (bool2)
              bool1 = paramSerializationHandler.setEscaping(false); 
          } 
          copyTextNode(i, paramSerializationHandler);
          if (bool2)
            paramSerializationHandler.setEscaping(bool1); 
          return;
        case 2:
          copyAttribute(i, j, paramSerializationHandler);
          return;
        case 13:
          paramSerializationHandler.namespaceAfterStartElement(getNodeNameX(paramInt), getNodeValue(paramInt));
          return;
      } 
      if (k == 1) {
        String str = copyElement(i, j, paramSerializationHandler);
        copyNS(i, paramSerializationHandler, !paramBoolean);
        copyAttributes(i, paramSerializationHandler);
        int m;
        for (m = _firstch2(i); m != -1; m = _nextsib2(m))
          copy(makeNodeHandle(m), paramSerializationHandler, true); 
        paramSerializationHandler.endElement(str);
      } else {
        String str = getNamespaceName(paramInt);
        if (str.length() != 0) {
          String str1 = getPrefix(paramInt);
          paramSerializationHandler.namespaceAfterStartElement(str1, str);
        } 
        paramSerializationHandler.addAttribute(getNodeName(paramInt), getNodeValue(paramInt));
      } 
    } catch (Exception exception) {
      throw new TransletException(exception);
    } 
  }
  
  private void copyPI(int paramInt, SerializationHandler paramSerializationHandler) throws TransletException {
    String str1 = getNodeName(paramInt);
    String str2 = getStringValueX(paramInt);
    try {
      paramSerializationHandler.processingInstruction(str1, str2);
    } catch (Exception exception) {
      throw new TransletException(exception);
    } 
  }
  
  public String shallowCopy(int paramInt, SerializationHandler paramSerializationHandler) throws TransletException {
    int i = makeNodeIdentity(paramInt);
    int j = _exptype2(i);
    int k = _exptype2Type(j);
    try {
      String str1;
      switch (k) {
        case 1:
          str1 = copyElement(i, j, paramSerializationHandler);
          copyNS(i, paramSerializationHandler, true);
          return str1;
        case 0:
        case 9:
          return "";
        case 3:
          copyTextNode(i, paramSerializationHandler);
          return null;
        case 7:
          copyPI(paramInt, paramSerializationHandler);
          return null;
        case 8:
          paramSerializationHandler.comment(getStringValueX(paramInt));
          return null;
        case 13:
          paramSerializationHandler.namespaceAfterStartElement(getNodeNameX(paramInt), getNodeValue(paramInt));
          return null;
        case 2:
          copyAttribute(i, j, paramSerializationHandler);
          return null;
      } 
      String str2 = getNamespaceName(paramInt);
      if (str2.length() != 0) {
        String str = getPrefix(paramInt);
        paramSerializationHandler.namespaceAfterStartElement(str, str2);
      } 
      paramSerializationHandler.addAttribute(getNodeName(paramInt), getNodeValue(paramInt));
      return null;
    } catch (Exception exception) {
      throw new TransletException(exception);
    } 
  }
  
  public String getLanguage(int paramInt) {
    for (int i = paramInt; -1 != i; i = getParent(i)) {
      if (1 == getNodeType(i)) {
        int j = getAttributeNode(i, "http://www.w3.org/XML/1998/namespace", "lang");
        if (-1 != j)
          return getNodeValue(j); 
      } 
    } 
    return null;
  }
  
  public DOMBuilder getBuilder() { return this; }
  
  public SerializationHandler getOutputDomBuilder() { return new ToXMLSAXHandler(this, "UTF-8"); }
  
  public DOM getResultTreeFrag(int paramInt1, int paramInt2) { return getResultTreeFrag(paramInt1, paramInt2, true); }
  
  public DOM getResultTreeFrag(int paramInt1, int paramInt2, boolean paramBoolean) {
    if (paramInt2 == 0) {
      if (paramBoolean) {
        int i = this._dtmManager.getFirstFreeDTMID();
        SimpleResultTreeImpl simpleResultTreeImpl = new SimpleResultTreeImpl(this._dtmManager, i << 16);
        this._dtmManager.addDTM(simpleResultTreeImpl, i, 0);
        return simpleResultTreeImpl;
      } 
      return new SimpleResultTreeImpl(this._dtmManager, 0);
    } 
    if (paramInt2 == 1) {
      if (paramBoolean) {
        int i = this._dtmManager.getFirstFreeDTMID();
        AdaptiveResultTreeImpl adaptiveResultTreeImpl = new AdaptiveResultTreeImpl(this._dtmManager, i << 16, this.m_wsfilter, paramInt1, this.m_buildIdIndex);
        this._dtmManager.addDTM(adaptiveResultTreeImpl, i, 0);
        return adaptiveResultTreeImpl;
      } 
      return new AdaptiveResultTreeImpl(this._dtmManager, 0, this.m_wsfilter, paramInt1, this.m_buildIdIndex);
    } 
    return (DOM)this._dtmManager.getDTM(null, true, this.m_wsfilter, true, false, false, paramInt1, this.m_buildIdIndex);
  }
  
  public Map<String, Integer> getElementsWithIDs() { return this.m_idAttributes; }
  
  public String getUnparsedEntityURI(String paramString) {
    if (this._document != null) {
      String str = "";
      DocumentType documentType = this._document.getDoctype();
      if (documentType != null) {
        NamedNodeMap namedNodeMap = documentType.getEntities();
        if (namedNodeMap == null)
          return str; 
        Entity entity = (Entity)namedNodeMap.getNamedItem(paramString);
        if (entity == null)
          return str; 
        String str1 = entity.getNotationName();
        if (str1 != null) {
          str = entity.getSystemId();
          if (str == null)
            str = entity.getPublicId(); 
        } 
      } 
      return str;
    } 
    return super.getUnparsedEntityURI(paramString);
  }
  
  public void release() { this._dtmManager.release(this, true); }
  
  public final class NamespaceAttributeIterator extends DTMDefaultBaseIterators.InternalAxisIteratorBase {
    private final int _nsType;
    
    public NamespaceAttributeIterator(int param1Int) {
      super(SAXImpl.this);
      this._nsType = param1Int;
    }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == 0)
        param1Int = SAXImpl.this.getDocument(); 
      if (this._isRestartable) {
        int i = this._nsType;
        this._startNode = param1Int;
        for (param1Int = SAXImpl.this.getFirstAttribute(param1Int); param1Int != -1 && SAXImpl.this.getNSType(param1Int) != i; param1Int = SAXImpl.this.getNextAttribute(param1Int));
        this._currentNode = param1Int;
        return resetPosition();
      } 
      return this;
    }
    
    public int next() {
      int i = this._currentNode;
      int j = this._nsType;
      if (i == -1)
        return -1; 
      int k;
      for (k = SAXImpl.this.getNextAttribute(i); k != -1 && SAXImpl.this.getNSType(k) != j; k = SAXImpl.this.getNextAttribute(k));
      this._currentNode = k;
      return returnNode(i);
    }
  }
  
  public final class NamespaceChildrenIterator extends DTMDefaultBaseIterators.InternalAxisIteratorBase {
    private final int _nsType;
    
    public NamespaceChildrenIterator(int param1Int) {
      super(SAXImpl.this);
      this._nsType = param1Int;
    }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == 0)
        param1Int = SAXImpl.this.getDocument(); 
      if (this._isRestartable) {
        this._startNode = param1Int;
        this._currentNode = (param1Int == -1) ? -1 : -2;
        return resetPosition();
      } 
      return this;
    }
    
    public int next() {
      if (this._currentNode != -1)
        for (int i = (-2 == this._currentNode) ? SAXImpl.this._firstch(SAXImpl.this.makeNodeIdentity(this._startNode)) : SAXImpl.this._nextsib(this._currentNode); i != -1; i = SAXImpl.this._nextsib(i)) {
          int j = SAXImpl.this.makeNodeHandle(i);
          if (SAXImpl.this.getNSType(j) == this._nsType) {
            this._currentNode = i;
            return returnNode(j);
          } 
        }  
      return -1;
    }
  }
  
  public final class NamespaceWildcardIterator extends DTMDefaultBaseIterators.InternalAxisIteratorBase {
    protected int m_nsType;
    
    protected DTMAxisIterator m_baseIterator;
    
    public NamespaceWildcardIterator(int param1Int1, int param1Int2) {
      super(SAXImpl.this);
      this.m_nsType = param1Int2;
      switch (param1Int1) {
        case 2:
          this.m_baseIterator = this$0.getAxisIterator(param1Int1);
        case 9:
          this.m_baseIterator = this$0.getAxisIterator(param1Int1);
          break;
      } 
      this.m_baseIterator = this$0.getTypedAxisIterator(param1Int1, 1);
    }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (this._isRestartable) {
        this._startNode = param1Int;
        this.m_baseIterator.setStartNode(param1Int);
        resetPosition();
      } 
      return this;
    }
    
    public int next() {
      int i;
      while ((i = this.m_baseIterator.next()) != -1) {
        if (SAXImpl.this.getNSType(i) == this.m_nsType)
          return returnNode(i); 
      } 
      return -1;
    }
    
    public DTMAxisIterator cloneIterator() {
      try {
        DTMAxisIterator dTMAxisIterator = this.m_baseIterator.cloneIterator();
        NamespaceWildcardIterator namespaceWildcardIterator = (NamespaceWildcardIterator)clone();
        namespaceWildcardIterator.m_baseIterator = dTMAxisIterator;
        namespaceWildcardIterator.m_nsType = this.m_nsType;
        namespaceWildcardIterator._isRestartable = false;
        return namespaceWildcardIterator;
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", cloneNotSupportedException.toString());
        return null;
      } 
    }
    
    public boolean isReverse() { return this.m_baseIterator.isReverse(); }
    
    public void setMark() { this.m_baseIterator.setMark(); }
    
    public void gotoMark() { this.m_baseIterator.gotoMark(); }
  }
  
  private final class NodeValueIterator extends DTMDefaultBaseIterators.InternalAxisIteratorBase {
    private DTMAxisIterator _source;
    
    private String _value;
    
    private boolean _op;
    
    private final boolean _isReverse;
    
    private int _returnType = 1;
    
    public NodeValueIterator(DTMAxisIterator param1DTMAxisIterator, int param1Int, String param1String, boolean param1Boolean) {
      super(SAXImpl.this);
      this._source = param1DTMAxisIterator;
      this._returnType = param1Int;
      this._value = param1String;
      this._op = param1Boolean;
      this._isReverse = param1DTMAxisIterator.isReverse();
    }
    
    public boolean isReverse() { return this._isReverse; }
    
    public DTMAxisIterator cloneIterator() {
      try {
        NodeValueIterator nodeValueIterator = (NodeValueIterator)clone();
        nodeValueIterator._isRestartable = false;
        nodeValueIterator._source = this._source.cloneIterator();
        nodeValueIterator._value = this._value;
        nodeValueIterator._op = this._op;
        return nodeValueIterator.reset();
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", cloneNotSupportedException.toString());
        return null;
      } 
    }
    
    public void setRestartable(boolean param1Boolean) {
      this._isRestartable = param1Boolean;
      this._source.setRestartable(param1Boolean);
    }
    
    public DTMAxisIterator reset() {
      this._source.reset();
      return resetPosition();
    }
    
    public int next() {
      int i;
      while ((i = this._source.next()) != -1) {
        String str = SAXImpl.this.getStringValueX(i);
        if (this._value.equals(str) == this._op)
          return (this._returnType == 0) ? returnNode(i) : returnNode(SAXImpl.this.getParent(i)); 
      } 
      return -1;
    }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (this._isRestartable) {
        this._source.setStartNode(this._startNode = param1Int);
        return resetPosition();
      } 
      return this;
    }
    
    public void setMark() { this._source.setMark(); }
    
    public void gotoMark() { this._source.gotoMark(); }
  }
  
  public class TypedNamespaceIterator extends DTMDefaultBaseIterators.NamespaceIterator {
    private String _nsPrefix;
    
    public TypedNamespaceIterator(int param1Int) {
      super(SAXImpl.this);
      if (this$0.m_expandedNameTable != null)
        this._nsPrefix = this$0.m_expandedNameTable.getLocalName(param1Int); 
    }
    
    public int next() {
      if (this._nsPrefix == null || this._nsPrefix.length() == 0)
        return -1; 
      int i = -1;
      for (i = super.next(); i != -1; i = super.next()) {
        if (this._nsPrefix.compareTo(SAXImpl.this.getLocalName(i)) == 0)
          return returnNode(i); 
      } 
      return -1;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\SAXImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */