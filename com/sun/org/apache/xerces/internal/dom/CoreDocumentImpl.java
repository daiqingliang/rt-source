package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.util.URI;
import com.sun.org.apache.xerces.internal.util.XML11Char;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
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
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

public class CoreDocumentImpl extends ParentNode implements Document {
  static final long serialVersionUID = 0L;
  
  protected DocumentTypeImpl docType;
  
  protected ElementImpl docElement;
  
  NodeListCache fFreeNLCache;
  
  protected String encoding;
  
  protected String actualEncoding;
  
  protected String version;
  
  protected boolean standalone;
  
  protected String fDocumentURI;
  
  private Map<Node, Map<String, ParentNode.UserDataRecord>> nodeUserData;
  
  protected Map<String, Node> identifiers;
  
  DOMNormalizer domNormalizer = null;
  
  DOMConfigurationImpl fConfiguration = null;
  
  Object fXPathEvaluator = null;
  
  private static final int[] kidOK = new int[13];
  
  protected int changes = 0;
  
  protected boolean allowGrammarAccess;
  
  protected boolean errorChecking = true;
  
  protected boolean ancestorChecking = true;
  
  protected boolean xmlVersionChanged = false;
  
  private int documentNumber = 0;
  
  private int nodeCounter = 0;
  
  private Map<Node, Integer> nodeTable;
  
  private boolean xml11Version = false;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  public CoreDocumentImpl() { this(false); }
  
  public CoreDocumentImpl(boolean paramBoolean) {
    super(null);
    this.allowGrammarAccess = paramBoolean;
    String str = SecuritySupport.getSystemProperty("http://java.sun.com/xml/dom/properties/ancestor-check");
    if (str != null && str.equalsIgnoreCase("false"))
      this.ancestorChecking = false; 
  }
  
  public CoreDocumentImpl(DocumentType paramDocumentType) { this(paramDocumentType, false); }
  
  public CoreDocumentImpl(DocumentType paramDocumentType, boolean paramBoolean) {
    this(paramBoolean);
    if (paramDocumentType != null) {
      DocumentTypeImpl documentTypeImpl;
      try {
        documentTypeImpl = (DocumentTypeImpl)paramDocumentType;
      } catch (ClassCastException classCastException) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
        throw new DOMException((short)4, str);
      } 
      documentTypeImpl.ownerDocument = this;
      appendChild(paramDocumentType);
    } 
  }
  
  public final Document getOwnerDocument() { return null; }
  
  public short getNodeType() { return 9; }
  
  public String getNodeName() { return "#document"; }
  
  public Node cloneNode(boolean paramBoolean) {
    CoreDocumentImpl coreDocumentImpl = new CoreDocumentImpl();
    callUserDataHandlers(this, coreDocumentImpl, (short)1);
    cloneNode(coreDocumentImpl, paramBoolean);
    return coreDocumentImpl;
  }
  
  protected void cloneNode(CoreDocumentImpl paramCoreDocumentImpl, boolean paramBoolean) {
    if (needsSyncChildren())
      synchronizeChildren(); 
    if (paramBoolean) {
      HashMap hashMap = null;
      if (this.identifiers != null) {
        hashMap = new HashMap(this.identifiers.size());
        for (String str : this.identifiers.keySet())
          hashMap.put(this.identifiers.get(str), str); 
      } 
      for (ChildNode childNode = this.firstChild; childNode != null; childNode = childNode.nextSibling)
        paramCoreDocumentImpl.appendChild(paramCoreDocumentImpl.importNode(childNode, true, true, hashMap)); 
    } 
    paramCoreDocumentImpl.allowGrammarAccess = this.allowGrammarAccess;
    paramCoreDocumentImpl.errorChecking = this.errorChecking;
  }
  
  public Node insertBefore(Node paramNode1, Node paramNode2) throws DOMException {
    short s = paramNode1.getNodeType();
    if (this.errorChecking && ((s == 1 && this.docElement != null) || (s == 10 && this.docType != null))) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
      throw new DOMException((short)3, str);
    } 
    if (paramNode1.getOwnerDocument() == null && paramNode1 instanceof DocumentTypeImpl)
      ((DocumentTypeImpl)paramNode1).ownerDocument = this; 
    super.insertBefore(paramNode1, paramNode2);
    if (s == 1) {
      this.docElement = (ElementImpl)paramNode1;
    } else if (s == 10) {
      this.docType = (DocumentTypeImpl)paramNode1;
    } 
    return paramNode1;
  }
  
  public Node removeChild(Node paramNode) throws DOMException {
    super.removeChild(paramNode);
    short s = paramNode.getNodeType();
    if (s == 1) {
      this.docElement = null;
    } else if (s == 10) {
      this.docType = null;
    } 
    return paramNode;
  }
  
  public Node replaceChild(Node paramNode1, Node paramNode2) throws DOMException {
    if (paramNode1.getOwnerDocument() == null && paramNode1 instanceof DocumentTypeImpl)
      ((DocumentTypeImpl)paramNode1).ownerDocument = this; 
    if (this.errorChecking && ((this.docType != null && paramNode2.getNodeType() != 10 && paramNode1.getNodeType() == 10) || (this.docElement != null && paramNode2.getNodeType() != 1 && paramNode1.getNodeType() == 1)))
      throw new DOMException((short)3, DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null)); 
    super.replaceChild(paramNode1, paramNode2);
    short s = paramNode2.getNodeType();
    if (s == 1) {
      this.docElement = (ElementImpl)paramNode1;
    } else if (s == 10) {
      this.docType = (DocumentTypeImpl)paramNode1;
    } 
    return paramNode2;
  }
  
  public String getTextContent() { return null; }
  
  public void setTextContent(String paramString) throws DOMException {}
  
  public Object getFeature(String paramString1, String paramString2) {
    boolean bool = (paramString2 == null || paramString2.length() == 0) ? 1 : 0;
    if (paramString1.equalsIgnoreCase("+XPath") && (bool || paramString2.equals("3.0"))) {
      if (this.fXPathEvaluator != null)
        return this.fXPathEvaluator; 
      try {
        Class clazz = ObjectFactory.findProviderClass("com.sun.org.apache.xpath.internal.domapi.XPathEvaluatorImpl", true);
        Constructor constructor = clazz.getConstructor(new Class[] { Document.class });
        Class[] arrayOfClass = clazz.getInterfaces();
        for (byte b = 0; b < arrayOfClass.length; b++) {
          if (arrayOfClass[b].getName().equals("org.w3c.dom.xpath.XPathEvaluator")) {
            this.fXPathEvaluator = constructor.newInstance(new Object[] { this });
            return this.fXPathEvaluator;
          } 
        } 
        return null;
      } catch (Exception exception) {
        return null;
      } 
    } 
    return super.getFeature(paramString1, paramString2);
  }
  
  public Attr createAttribute(String paramString) throws DOMException {
    if (this.errorChecking && !isXMLName(paramString, this.xml11Version)) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str);
    } 
    return new AttrImpl(this, paramString);
  }
  
  public CDATASection createCDATASection(String paramString) throws DOMException { return new CDATASectionImpl(this, paramString); }
  
  public Comment createComment(String paramString) { return new CommentImpl(this, paramString); }
  
  public DocumentFragment createDocumentFragment() { return new DocumentFragmentImpl(this); }
  
  public Element createElement(String paramString) throws DOMException {
    if (this.errorChecking && !isXMLName(paramString, this.xml11Version)) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str);
    } 
    return new ElementImpl(this, paramString);
  }
  
  public EntityReference createEntityReference(String paramString) throws DOMException {
    if (this.errorChecking && !isXMLName(paramString, this.xml11Version)) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str);
    } 
    return new EntityReferenceImpl(this, paramString);
  }
  
  public ProcessingInstruction createProcessingInstruction(String paramString1, String paramString2) throws DOMException {
    if (this.errorChecking && !isXMLName(paramString1, this.xml11Version)) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str);
    } 
    return new ProcessingInstructionImpl(this, paramString1, paramString2);
  }
  
  public Text createTextNode(String paramString) { return new TextImpl(this, paramString); }
  
  public DocumentType getDoctype() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return this.docType;
  }
  
  public Element getDocumentElement() {
    if (needsSyncChildren())
      synchronizeChildren(); 
    return this.docElement;
  }
  
  public NodeList getElementsByTagName(String paramString) { return new DeepNodeListImpl(this, paramString); }
  
  public DOMImplementation getImplementation() { return CoreDOMImplementationImpl.getDOMImplementation(); }
  
  public void setErrorChecking(boolean paramBoolean) { this.errorChecking = paramBoolean; }
  
  public void setStrictErrorChecking(boolean paramBoolean) { this.errorChecking = paramBoolean; }
  
  public boolean getErrorChecking() { return this.errorChecking; }
  
  public boolean getStrictErrorChecking() { return this.errorChecking; }
  
  public String getInputEncoding() { return this.actualEncoding; }
  
  public void setInputEncoding(String paramString) throws DOMException { this.actualEncoding = paramString; }
  
  public void setXmlEncoding(String paramString) throws DOMException { this.encoding = paramString; }
  
  public void setEncoding(String paramString) throws DOMException { setXmlEncoding(paramString); }
  
  public String getXmlEncoding() { return this.encoding; }
  
  public String getEncoding() { return getXmlEncoding(); }
  
  public void setXmlVersion(String paramString) throws DOMException {
    if (paramString.equals("1.0") || paramString.equals("1.1")) {
      if (!getXmlVersion().equals(paramString)) {
        this.xmlVersionChanged = true;
        isNormalized(false);
        this.version = paramString;
      } 
    } else {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
      throw new DOMException((short)9, str);
    } 
    if (getXmlVersion().equals("1.1")) {
      this.xml11Version = true;
    } else {
      this.xml11Version = false;
    } 
  }
  
  public void setVersion(String paramString) throws DOMException { setXmlVersion(paramString); }
  
  public String getXmlVersion() { return (this.version == null) ? "1.0" : this.version; }
  
  public String getVersion() { return getXmlVersion(); }
  
  public void setXmlStandalone(boolean paramBoolean) { this.standalone = paramBoolean; }
  
  public void setStandalone(boolean paramBoolean) { setXmlStandalone(paramBoolean); }
  
  public boolean getXmlStandalone() { return this.standalone; }
  
  public boolean getStandalone() { return getXmlStandalone(); }
  
  public String getDocumentURI() { return this.fDocumentURI; }
  
  public Node renameNode(Node paramNode, String paramString1, String paramString2) throws DOMException {
    Element element;
    AttrImpl attrImpl;
    ElementImpl elementImpl;
    if (this.errorChecking && paramNode.getOwnerDocument() != this && paramNode != this) {
      String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
      throw new DOMException((short)4, str1);
    } 
    switch (paramNode.getNodeType()) {
      case 1:
        elementImpl = (ElementImpl)paramNode;
        if (elementImpl instanceof ElementNSImpl) {
          ((ElementNSImpl)elementImpl).rename(paramString1, paramString2);
          callUserDataHandlers(elementImpl, null, (short)4);
        } else if (paramString1 == null) {
          if (this.errorChecking) {
            int i = paramString2.indexOf(':');
            if (i != -1) {
              String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
              throw new DOMException((short)14, str1);
            } 
            if (!isXMLName(paramString2, this.xml11Version)) {
              String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
              throw new DOMException((short)5, str1);
            } 
          } 
          elementImpl.rename(paramString2);
          callUserDataHandlers(elementImpl, null, (short)4);
        } else {
          ElementNSImpl elementNSImpl = new ElementNSImpl(this, paramString1, paramString2);
          copyEventListeners(elementImpl, elementNSImpl);
          Map map = removeUserDataTable(elementImpl);
          Node node1 = elementImpl.getParentNode();
          Node node2 = elementImpl.getNextSibling();
          if (node1 != null)
            node1.removeChild(elementImpl); 
          for (Node node3 = elementImpl.getFirstChild(); node3 != null; node3 = elementImpl.getFirstChild()) {
            elementImpl.removeChild(node3);
            elementNSImpl.appendChild(node3);
          } 
          elementNSImpl.moveSpecifiedAttributes(elementImpl);
          setUserDataTable(elementNSImpl, map);
          callUserDataHandlers(elementImpl, elementNSImpl, (short)4);
          if (node1 != null)
            node1.insertBefore(elementNSImpl, node2); 
          elementImpl = elementNSImpl;
        } 
        renamedElement((Element)paramNode, elementImpl);
        return elementImpl;
      case 2:
        attrImpl = (AttrImpl)paramNode;
        element = attrImpl.getOwnerElement();
        if (element != null)
          element.removeAttributeNode(attrImpl); 
        if (paramNode instanceof AttrNSImpl) {
          ((AttrNSImpl)attrImpl).rename(paramString1, paramString2);
          if (element != null)
            element.setAttributeNodeNS(attrImpl); 
          callUserDataHandlers(attrImpl, null, (short)4);
        } else if (paramString1 == null) {
          attrImpl.rename(paramString2);
          if (element != null)
            element.setAttributeNode(attrImpl); 
          callUserDataHandlers(attrImpl, null, (short)4);
        } else {
          AttrNSImpl attrNSImpl = new AttrNSImpl(this, paramString1, paramString2);
          copyEventListeners(attrImpl, attrNSImpl);
          Map map = removeUserDataTable(attrImpl);
          for (Node node = attrImpl.getFirstChild(); node != null; node = attrImpl.getFirstChild()) {
            attrImpl.removeChild(node);
            attrNSImpl.appendChild(node);
          } 
          setUserDataTable(attrNSImpl, map);
          callUserDataHandlers(attrImpl, attrNSImpl, (short)4);
          if (element != null)
            element.setAttributeNode(attrNSImpl); 
          attrImpl = attrNSImpl;
        } 
        renamedAttrNode((Attr)paramNode, attrImpl);
        return attrImpl;
    } 
    String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
    throw new DOMException((short)9, str);
  }
  
  public void normalizeDocument() {
    if (isNormalized() && !isNormalizeDocRequired())
      return; 
    if (needsSyncChildren())
      synchronizeChildren(); 
    if (this.domNormalizer == null)
      this.domNormalizer = new DOMNormalizer(); 
    if (this.fConfiguration == null) {
      this.fConfiguration = new DOMConfigurationImpl();
    } else {
      this.fConfiguration.reset();
    } 
    this.domNormalizer.normalizeDocument(this, this.fConfiguration);
    isNormalized(true);
    this.xmlVersionChanged = false;
  }
  
  public DOMConfiguration getDomConfig() {
    if (this.fConfiguration == null)
      this.fConfiguration = new DOMConfigurationImpl(); 
    return this.fConfiguration;
  }
  
  public String getBaseURI() {
    if (this.fDocumentURI != null && this.fDocumentURI.length() != 0)
      try {
        return (new URI(this.fDocumentURI)).toString();
      } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {
        return null;
      }  
    return this.fDocumentURI;
  }
  
  public void setDocumentURI(String paramString) throws DOMException { this.fDocumentURI = paramString; }
  
  public boolean getAsync() { return false; }
  
  public void setAsync(boolean paramBoolean) {
    if (paramBoolean) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
      throw new DOMException((short)9, str);
    } 
  }
  
  public void abort() {}
  
  public boolean load(String paramString) { return false; }
  
  public boolean loadXML(String paramString) { return false; }
  
  public String saveXML(Node paramNode) throws DOMException {
    if (this.errorChecking && paramNode != null && this != paramNode.getOwnerDocument()) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
      throw new DOMException((short)4, str);
    } 
    DOMImplementationLS dOMImplementationLS = (DOMImplementationLS)DOMImplementationImpl.getDOMImplementation();
    LSSerializer lSSerializer = dOMImplementationLS.createLSSerializer();
    if (paramNode == null)
      paramNode = this; 
    return lSSerializer.writeToString(paramNode);
  }
  
  void setMutationEvents(boolean paramBoolean) {}
  
  boolean getMutationEvents() { return false; }
  
  public DocumentType createDocumentType(String paramString1, String paramString2, String paramString3) throws DOMException { return new DocumentTypeImpl(this, paramString1, paramString2, paramString3); }
  
  public Entity createEntity(String paramString) throws DOMException {
    if (this.errorChecking && !isXMLName(paramString, this.xml11Version)) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str);
    } 
    return new EntityImpl(this, paramString);
  }
  
  public Notation createNotation(String paramString) throws DOMException {
    if (this.errorChecking && !isXMLName(paramString, this.xml11Version)) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str);
    } 
    return new NotationImpl(this, paramString);
  }
  
  public ElementDefinitionImpl createElementDefinition(String paramString) throws DOMException {
    if (this.errorChecking && !isXMLName(paramString, this.xml11Version)) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str);
    } 
    return new ElementDefinitionImpl(this, paramString);
  }
  
  protected int getNodeNumber() {
    if (this.documentNumber == 0) {
      CoreDOMImplementationImpl coreDOMImplementationImpl = (CoreDOMImplementationImpl)CoreDOMImplementationImpl.getDOMImplementation();
      this.documentNumber = coreDOMImplementationImpl.assignDocumentNumber();
    } 
    return this.documentNumber;
  }
  
  protected int getNodeNumber(Node paramNode) {
    int i;
    if (this.nodeTable == null) {
      this.nodeTable = new HashMap();
      i = --this.nodeCounter;
      this.nodeTable.put(paramNode, new Integer(i));
    } else {
      Integer integer = (Integer)this.nodeTable.get(paramNode);
      if (integer == null) {
        i = --this.nodeCounter;
        this.nodeTable.put(paramNode, Integer.valueOf(i));
      } else {
        i = integer.intValue();
      } 
    } 
    return i;
  }
  
  public Node importNode(Node paramNode, boolean paramBoolean) throws DOMException { return importNode(paramNode, paramBoolean, false, null); }
  
  private Node importNode(Node paramNode, boolean paramBoolean1, boolean paramBoolean2, Map<Node, String> paramMap) throws DOMException {
    NamedNodeMap namedNodeMap2;
    NamedNodeMap namedNodeMap1;
    EntityImpl entityImpl2;
    NotationImpl notationImpl2;
    DocumentTypeImpl documentTypeImpl2;
    boolean bool;
    DocumentType documentType;
    Element element;
    String str;
    Entity entity;
    Notation notation;
    EntityImpl entityImpl1;
    DocumentTypeImpl documentTypeImpl1;
    Comment comment;
    DocumentFragment documentFragment;
    Text text;
    EntityReference entityReference;
    ProcessingInstruction processingInstruction;
    NotationImpl notationImpl1;
    Attr attr = null;
    Map map = null;
    if (paramNode instanceof NodeImpl)
      map = ((NodeImpl)paramNode).getUserDataRecord(); 
    short s = paramNode.getNodeType();
    switch (s) {
      case 1:
        bool = paramNode.getOwnerDocument().getImplementation().hasFeature("XML", "2.0");
        if (!bool || paramNode.getLocalName() == null) {
          element = createElement(paramNode.getNodeName());
        } else {
          element = createElementNS(paramNode.getNamespaceURI(), paramNode.getNodeName());
        } 
        namedNodeMap1 = paramNode.getAttributes();
        if (namedNodeMap1 != null) {
          int i = namedNodeMap1.getLength();
          for (byte b = 0; b < i; b++) {
            Attr attr1 = (Attr)namedNodeMap1.item(b);
            if (attr1.getSpecified() || paramBoolean2) {
              Attr attr2 = (Attr)importNode(attr1, true, paramBoolean2, paramMap);
              if (!bool || attr1.getLocalName() == null) {
                element.setAttributeNode(attr2);
              } else {
                element.setAttributeNodeNS(attr2);
              } 
            } 
          } 
        } 
        if (paramMap != null) {
          String str1 = (String)paramMap.get(paramNode);
          if (str1 != null) {
            if (this.identifiers == null)
              this.identifiers = new HashMap(); 
            this.identifiers.put(str1, element);
          } 
        } 
        attr = element;
        break;
      case 2:
        if (paramNode.getOwnerDocument().getImplementation().hasFeature("XML", "2.0")) {
          if (paramNode.getLocalName() == null) {
            Attr attr1 = createAttribute(paramNode.getNodeName());
          } else {
            Attr attr1 = createAttributeNS(paramNode.getNamespaceURI(), paramNode.getNodeName());
          } 
        } else {
          attr = createAttribute(paramNode.getNodeName());
        } 
        if (paramNode instanceof AttrImpl) {
          AttrImpl attrImpl = (AttrImpl)paramNode;
          if (attrImpl.hasStringValue()) {
            AttrImpl attrImpl1 = (AttrImpl)attr;
            attrImpl1.setValue(attrImpl.getValue());
            paramBoolean1 = false;
            break;
          } 
          paramBoolean1 = true;
          break;
        } 
        if (paramNode.getFirstChild() == null) {
          attr.setNodeValue(paramNode.getNodeValue());
          paramBoolean1 = false;
          break;
        } 
        paramBoolean1 = true;
        break;
      case 3:
        text = createTextNode(paramNode.getNodeValue());
        break;
      case 4:
        text = createCDATASection(paramNode.getNodeValue());
        break;
      case 5:
        entityReference = createEntityReference(paramNode.getNodeName());
        paramBoolean1 = false;
        break;
      case 6:
        entity = (Entity)paramNode;
        entityImpl2 = (EntityImpl)createEntity(paramNode.getNodeName());
        entityImpl2.setPublicId(entity.getPublicId());
        entityImpl2.setSystemId(entity.getSystemId());
        entityImpl2.setNotationName(entity.getNotationName());
        entityImpl2.isReadOnly(false);
        entityImpl1 = entityImpl2;
        break;
      case 7:
        processingInstruction = createProcessingInstruction(paramNode.getNodeName(), paramNode.getNodeValue());
        break;
      case 8:
        comment = createComment(paramNode.getNodeValue());
        break;
      case 10:
        if (!paramBoolean2) {
          String str1 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
          throw new DOMException((short)9, str1);
        } 
        documentType = (DocumentType)paramNode;
        documentTypeImpl2 = (DocumentTypeImpl)createDocumentType(documentType.getNodeName(), documentType.getPublicId(), documentType.getSystemId());
        namedNodeMap1 = documentType.getEntities();
        namedNodeMap2 = documentTypeImpl2.getEntities();
        if (namedNodeMap1 != null)
          for (byte b = 0; b < namedNodeMap1.getLength(); b++)
            namedNodeMap2.setNamedItem(importNode(namedNodeMap1.item(b), true, true, paramMap));  
        namedNodeMap1 = documentType.getNotations();
        namedNodeMap2 = documentTypeImpl2.getNotations();
        if (namedNodeMap1 != null)
          for (byte b = 0; b < namedNodeMap1.getLength(); b++)
            namedNodeMap2.setNamedItem(importNode(namedNodeMap1.item(b), true, true, paramMap));  
        documentTypeImpl1 = documentTypeImpl2;
        break;
      case 11:
        documentFragment = createDocumentFragment();
        break;
      case 12:
        notation = (Notation)paramNode;
        notationImpl2 = (NotationImpl)createNotation(paramNode.getNodeName());
        notationImpl2.setPublicId(notation.getPublicId());
        notationImpl2.setSystemId(notation.getSystemId());
        notationImpl1 = notationImpl2;
        break;
      default:
        str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
        throw new DOMException((short)9, str);
    } 
    if (map != null)
      callUserDataHandlers(paramNode, notationImpl1, (short)2, map); 
    if (paramBoolean1)
      for (Node node = paramNode.getFirstChild(); node != null; node = node.getNextSibling())
        notationImpl1.appendChild(importNode(node, true, paramBoolean2, paramMap));  
    if (notationImpl1.getNodeType() == 6)
      ((NodeImpl)notationImpl1).setReadOnly(true, true); 
    return notationImpl1;
  }
  
  public Node adoptNode(Node paramNode) throws DOMException {
    Node node3;
    NamedNodeMap namedNodeMap;
    Node node2;
    String str;
    Node node1;
    AttrImpl attrImpl;
    Map map;
    NodeImpl nodeImpl;
    try {
      nodeImpl = (NodeImpl)paramNode;
    } catch (ClassCastException classCastException) {
      return null;
    } 
    if (paramNode == null)
      return null; 
    if (paramNode.getOwnerDocument() != null) {
      DOMImplementation dOMImplementation1 = getImplementation();
      DOMImplementation dOMImplementation2 = paramNode.getOwnerDocument().getImplementation();
      if (dOMImplementation1 != dOMImplementation2)
        if (dOMImplementation1 instanceof DOMImplementationImpl && dOMImplementation2 instanceof DeferredDOMImplementationImpl) {
          undeferChildren(nodeImpl);
        } else if (!(dOMImplementation1 instanceof DeferredDOMImplementationImpl) || !(dOMImplementation2 instanceof DOMImplementationImpl)) {
          return null;
        }  
    } 
    switch (nodeImpl.getNodeType()) {
      case 2:
        attrImpl = (AttrImpl)nodeImpl;
        if (attrImpl.getOwnerElement() != null)
          attrImpl.getOwnerElement().removeAttributeNode(attrImpl); 
        attrImpl.isSpecified(true);
        map = nodeImpl.getUserDataRecord();
        attrImpl.setOwnerDocument(this);
        if (map != null)
          setUserDataTable(nodeImpl, map); 
        break;
      case 6:
      case 12:
        str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
        throw new DOMException((short)7, str);
      case 9:
      case 10:
        str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
        throw new DOMException((short)9, str);
      case 5:
        map = nodeImpl.getUserDataRecord();
        node1 = nodeImpl.getParentNode();
        if (node1 != null)
          node1.removeChild(paramNode); 
        while ((node2 = nodeImpl.getFirstChild()) != null)
          nodeImpl.removeChild(node2); 
        nodeImpl.setOwnerDocument(this);
        if (map != null)
          setUserDataTable(nodeImpl, map); 
        if (this.docType == null)
          break; 
        namedNodeMap = this.docType.getEntities();
        node3 = namedNodeMap.getNamedItem(nodeImpl.getNodeName());
        if (node3 == null)
          break; 
        for (node2 = node3.getFirstChild(); node2 != null; node2 = node2.getNextSibling()) {
          Node node = node2.cloneNode(true);
          nodeImpl.appendChild(node);
        } 
        break;
      case 1:
        map = nodeImpl.getUserDataRecord();
        node1 = nodeImpl.getParentNode();
        if (node1 != null)
          node1.removeChild(paramNode); 
        nodeImpl.setOwnerDocument(this);
        if (map != null)
          setUserDataTable(nodeImpl, map); 
        ((ElementImpl)nodeImpl).reconcileDefaultAttributes();
        break;
      default:
        map = nodeImpl.getUserDataRecord();
        node1 = nodeImpl.getParentNode();
        if (node1 != null)
          node1.removeChild(paramNode); 
        nodeImpl.setOwnerDocument(this);
        if (map != null)
          setUserDataTable(nodeImpl, map); 
        break;
    } 
    if (map != null)
      callUserDataHandlers(paramNode, null, (short)5, map); 
    return nodeImpl;
  }
  
  protected void undeferChildren(Node paramNode) {
    Node node = paramNode;
    while (null != paramNode) {
      if (((NodeImpl)paramNode).needsSyncData())
        ((NodeImpl)paramNode).synchronizeData(); 
      NamedNodeMap namedNodeMap = paramNode.getAttributes();
      if (namedNodeMap != null) {
        int i = namedNodeMap.getLength();
        for (byte b = 0; b < i; b++)
          undeferChildren(namedNodeMap.item(b)); 
      } 
      Node node1 = null;
      node1 = paramNode.getFirstChild();
      while (null == node1 && !node.equals(paramNode)) {
        node1 = paramNode.getNextSibling();
        if (null == node1) {
          paramNode = paramNode.getParentNode();
          if (null == paramNode || node.equals(paramNode)) {
            node1 = null;
            break;
          } 
        } 
      } 
      paramNode = node1;
    } 
  }
  
  public Element getElementById(String paramString) throws DOMException { return getIdentifier(paramString); }
  
  protected final void clearIdentifiers() {
    if (this.identifiers != null)
      this.identifiers.clear(); 
  }
  
  public void putIdentifier(String paramString, Element paramElement) {
    if (paramElement == null) {
      removeIdentifier(paramString);
      return;
    } 
    if (needsSyncData())
      synchronizeData(); 
    if (this.identifiers == null)
      this.identifiers = new HashMap(); 
    this.identifiers.put(paramString, paramElement);
  }
  
  public Element getIdentifier(String paramString) throws DOMException {
    if (needsSyncData())
      synchronizeData(); 
    if (this.identifiers == null)
      return null; 
    Element element = (Element)this.identifiers.get(paramString);
    if (element != null)
      for (Node node = element.getParentNode(); node != null; node = node.getParentNode()) {
        if (node == this)
          return element; 
      }  
    return null;
  }
  
  public void removeIdentifier(String paramString) throws DOMException {
    if (needsSyncData())
      synchronizeData(); 
    if (this.identifiers == null)
      return; 
    this.identifiers.remove(paramString);
  }
  
  public Element createElementNS(String paramString1, String paramString2) throws DOMException { return new ElementNSImpl(this, paramString1, paramString2); }
  
  public Element createElementNS(String paramString1, String paramString2, String paramString3) throws DOMException { return new ElementNSImpl(this, paramString1, paramString2, paramString3); }
  
  public Attr createAttributeNS(String paramString1, String paramString2) throws DOMException { return new AttrNSImpl(this, paramString1, paramString2); }
  
  public Attr createAttributeNS(String paramString1, String paramString2, String paramString3) throws DOMException { return new AttrNSImpl(this, paramString1, paramString2, paramString3); }
  
  public NodeList getElementsByTagNameNS(String paramString1, String paramString2) { return new DeepNodeListImpl(this, paramString1, paramString2); }
  
  public Object clone() throws CloneNotSupportedException {
    CoreDocumentImpl coreDocumentImpl = (CoreDocumentImpl)super.clone();
    coreDocumentImpl.docType = null;
    coreDocumentImpl.docElement = null;
    return coreDocumentImpl;
  }
  
  public static final boolean isXMLName(String paramString, boolean paramBoolean) { return (paramString == null) ? false : (!paramBoolean ? XMLChar.isValidName(paramString) : XML11Char.isXML11ValidName(paramString)); }
  
  public static final boolean isValidQName(String paramString1, String paramString2, boolean paramBoolean) {
    if (paramString2 == null)
      return false; 
    boolean bool = false;
    if (!paramBoolean) {
      bool = ((paramString1 == null || XMLChar.isValidNCName(paramString1)) && XMLChar.isValidNCName(paramString2));
    } else {
      bool = ((paramString1 == null || XML11Char.isXML11ValidNCName(paramString1)) && XML11Char.isXML11ValidNCName(paramString2));
    } 
    return bool;
  }
  
  protected boolean isKidOK(Node paramNode1, Node paramNode2) { return (this.allowGrammarAccess && paramNode1.getNodeType() == 10) ? ((paramNode2.getNodeType() == 1)) : ((0 != (kidOK[paramNode1.getNodeType()] & 1 << paramNode2.getNodeType()))); }
  
  protected void changed() { this.changes++; }
  
  protected int changes() { return this.changes; }
  
  NodeListCache getNodeListCache(ParentNode paramParentNode) {
    if (this.fFreeNLCache == null)
      return new NodeListCache(paramParentNode); 
    NodeListCache nodeListCache = this.fFreeNLCache;
    this.fFreeNLCache = this.fFreeNLCache.next;
    nodeListCache.fChild = null;
    nodeListCache.fChildIndex = -1;
    nodeListCache.fLength = -1;
    if (nodeListCache.fOwner != null)
      nodeListCache.fOwner.fNodeListCache = null; 
    nodeListCache.fOwner = paramParentNode;
    return nodeListCache;
  }
  
  void freeNodeListCache(NodeListCache paramNodeListCache) {
    paramNodeListCache.next = this.fFreeNLCache;
    this.fFreeNLCache = paramNodeListCache;
  }
  
  public Object setUserData(Node paramNode, String paramString, Object paramObject, UserDataHandler paramUserDataHandler) {
    Map map;
    if (paramObject == null) {
      if (this.nodeUserData != null) {
        map = (Map)this.nodeUserData.get(paramNode);
        if (map != null) {
          ParentNode.UserDataRecord userDataRecord1 = (ParentNode.UserDataRecord)map.remove(paramString);
          if (userDataRecord1 != null)
            return userDataRecord1.fData; 
        } 
      } 
      return null;
    } 
    if (this.nodeUserData == null) {
      this.nodeUserData = new HashMap();
      map = new HashMap();
      this.nodeUserData.put(paramNode, map);
    } else {
      map = (Map)this.nodeUserData.get(paramNode);
      if (map == null) {
        map = new HashMap();
        this.nodeUserData.put(paramNode, map);
      } 
    } 
    ParentNode.UserDataRecord userDataRecord = (ParentNode.UserDataRecord)map.put(paramString, new ParentNode.UserDataRecord(this, paramObject, paramUserDataHandler));
    return (userDataRecord != null) ? userDataRecord.fData : null;
  }
  
  public Object getUserData(Node paramNode, String paramString) {
    if (this.nodeUserData == null)
      return null; 
    Map map = (Map)this.nodeUserData.get(paramNode);
    if (map == null)
      return null; 
    ParentNode.UserDataRecord userDataRecord = (ParentNode.UserDataRecord)map.get(paramString);
    return (userDataRecord != null) ? userDataRecord.fData : null;
  }
  
  protected Map<String, ParentNode.UserDataRecord> getUserDataRecord(Node paramNode) {
    if (this.nodeUserData == null)
      return null; 
    Map map = (Map)this.nodeUserData.get(paramNode);
    return (map == null) ? null : map;
  }
  
  Map<String, ParentNode.UserDataRecord> removeUserDataTable(Node paramNode) { return (this.nodeUserData == null) ? null : (Map)this.nodeUserData.get(paramNode); }
  
  void setUserDataTable(Node paramNode, Map<String, ParentNode.UserDataRecord> paramMap) {
    if (this.nodeUserData == null)
      this.nodeUserData = new HashMap(); 
    if (paramMap != null)
      this.nodeUserData.put(paramNode, paramMap); 
  }
  
  void callUserDataHandlers(Node paramNode1, Node paramNode2, short paramShort) {
    if (this.nodeUserData == null)
      return; 
    if (paramNode1 instanceof NodeImpl) {
      Map map = ((NodeImpl)paramNode1).getUserDataRecord();
      if (map == null || map.isEmpty())
        return; 
      callUserDataHandlers(paramNode1, paramNode2, paramShort, map);
    } 
  }
  
  void callUserDataHandlers(Node paramNode1, Node paramNode2, short paramShort, Map<String, ParentNode.UserDataRecord> paramMap) {
    if (paramMap == null || paramMap.isEmpty())
      return; 
    for (String str : paramMap.keySet()) {
      ParentNode.UserDataRecord userDataRecord = (ParentNode.UserDataRecord)paramMap.get(str);
      if (userDataRecord.fHandler != null)
        userDataRecord.fHandler.handle(paramShort, str, userDataRecord.fData, paramNode1, paramNode2); 
    } 
  }
  
  protected final void checkNamespaceWF(String paramString, int paramInt1, int paramInt2) {
    if (!this.errorChecking)
      return; 
    if (paramInt1 == 0 || paramInt1 == paramString.length() - 1 || paramInt2 != paramInt1) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
      throw new DOMException((short)14, str);
    } 
  }
  
  protected final void checkDOMNSErr(String paramString1, String paramString2) {
    if (this.errorChecking) {
      if (paramString2 == null) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
        throw new DOMException((short)14, str);
      } 
      if (paramString1.equals("xml") && !paramString2.equals(NamespaceContext.XML_URI)) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
        throw new DOMException((short)14, str);
      } 
      if ((paramString1.equals("xmlns") && !paramString2.equals(NamespaceContext.XMLNS_URI)) || (!paramString1.equals("xmlns") && paramString2.equals(NamespaceContext.XMLNS_URI))) {
        String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
        throw new DOMException((short)14, str);
      } 
    } 
  }
  
  protected final void checkQName(String paramString1, String paramString2) {
    if (!this.errorChecking)
      return; 
    boolean bool = false;
    if (!this.xml11Version) {
      bool = ((paramString1 == null || XMLChar.isValidNCName(paramString1)) && XMLChar.isValidNCName(paramString2)) ? 1 : 0;
    } else {
      bool = ((paramString1 == null || XML11Char.isXML11ValidNCName(paramString1)) && XML11Char.isXML11ValidNCName(paramString2)) ? 1 : 0;
    } 
    if (!bool) {
      String str = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
      throw new DOMException((short)5, str);
    } 
  }
  
  boolean isXML11Version() { return this.xml11Version; }
  
  boolean isNormalizeDocRequired() { return true; }
  
  boolean isXMLVersionChanged() { return this.xmlVersionChanged; }
  
  protected void setUserData(NodeImpl paramNodeImpl, Object paramObject) { setUserData(paramNodeImpl, "XERCES1DOMUSERDATA", paramObject, null); }
  
  protected Object getUserData(NodeImpl paramNodeImpl) { return getUserData(paramNodeImpl, "XERCES1DOMUSERDATA"); }
  
  protected void addEventListener(NodeImpl paramNodeImpl, String paramString, EventListener paramEventListener, boolean paramBoolean) {}
  
  protected void removeEventListener(NodeImpl paramNodeImpl, String paramString, EventListener paramEventListener, boolean paramBoolean) {}
  
  protected void copyEventListeners(NodeImpl paramNodeImpl1, NodeImpl paramNodeImpl2) {}
  
  protected boolean dispatchEvent(NodeImpl paramNodeImpl, Event paramEvent) { return false; }
  
  void replacedText(NodeImpl paramNodeImpl) {}
  
  void deletedText(NodeImpl paramNodeImpl, int paramInt1, int paramInt2) {}
  
  void insertedText(NodeImpl paramNodeImpl, int paramInt1, int paramInt2) {}
  
  void modifyingCharacterData(NodeImpl paramNodeImpl, boolean paramBoolean) {}
  
  void modifiedCharacterData(NodeImpl paramNodeImpl, String paramString1, String paramString2, boolean paramBoolean) {}
  
  void insertingNode(NodeImpl paramNodeImpl, boolean paramBoolean) {}
  
  void insertedNode(NodeImpl paramNodeImpl1, NodeImpl paramNodeImpl2, boolean paramBoolean) {}
  
  void removingNode(NodeImpl paramNodeImpl1, NodeImpl paramNodeImpl2, boolean paramBoolean) {}
  
  void removedNode(NodeImpl paramNodeImpl, boolean paramBoolean) {}
  
  void replacingNode(NodeImpl paramNodeImpl) {}
  
  void replacedNode(NodeImpl paramNodeImpl) {}
  
  void replacingData(NodeImpl paramNodeImpl) {}
  
  void replacedCharacterData(NodeImpl paramNodeImpl, String paramString1, String paramString2) {}
  
  void modifiedAttrValue(AttrImpl paramAttrImpl, String paramString) {}
  
  void setAttrNode(AttrImpl paramAttrImpl1, AttrImpl paramAttrImpl2) {}
  
  void removedAttrNode(AttrImpl paramAttrImpl, NodeImpl paramNodeImpl, String paramString) {}
  
  void renamedAttrNode(Attr paramAttr1, Attr paramAttr2) {}
  
  void renamedElement(Element paramElement1, Element paramElement2) {}
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Hashtable hashtable1 = null;
    if (this.nodeUserData != null) {
      hashtable1 = new Hashtable();
      for (Map.Entry entry : this.nodeUserData.entrySet())
        hashtable1.put(entry.getKey(), new Hashtable((Map)entry.getValue())); 
    } 
    Hashtable hashtable2 = (this.identifiers == null) ? null : new Hashtable(this.identifiers);
    Hashtable hashtable3 = (this.nodeTable == null) ? null : new Hashtable(this.nodeTable);
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("docType", this.docType);
    putField.put("docElement", this.docElement);
    putField.put("fFreeNLCache", this.fFreeNLCache);
    putField.put("encoding", this.encoding);
    putField.put("actualEncoding", this.actualEncoding);
    putField.put("version", this.version);
    putField.put("standalone", this.standalone);
    putField.put("fDocumentURI", this.fDocumentURI);
    putField.put("userData", hashtable1);
    putField.put("identifiers", hashtable2);
    putField.put("changes", this.changes);
    putField.put("allowGrammarAccess", this.allowGrammarAccess);
    putField.put("errorChecking", this.errorChecking);
    putField.put("ancestorChecking", this.ancestorChecking);
    putField.put("xmlVersionChanged", this.xmlVersionChanged);
    putField.put("documentNumber", this.documentNumber);
    putField.put("nodeCounter", this.nodeCounter);
    putField.put("nodeTable", hashtable3);
    putField.put("xml11Version", this.xml11Version);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    this.docType = (DocumentTypeImpl)getField.get("docType", null);
    this.docElement = (ElementImpl)getField.get("docElement", null);
    this.fFreeNLCache = (NodeListCache)getField.get("fFreeNLCache", null);
    this.encoding = (String)getField.get("encoding", null);
    this.actualEncoding = (String)getField.get("actualEncoding", null);
    this.version = (String)getField.get("version", null);
    this.standalone = getField.get("standalone", false);
    this.fDocumentURI = (String)getField.get("fDocumentURI", null);
    Hashtable hashtable1 = (Hashtable)getField.get("userData", null);
    Hashtable hashtable2 = (Hashtable)getField.get("identifiers", null);
    this.changes = getField.get("changes", 0);
    this.allowGrammarAccess = getField.get("allowGrammarAccess", false);
    this.errorChecking = getField.get("errorChecking", true);
    this.ancestorChecking = getField.get("ancestorChecking", true);
    this.xmlVersionChanged = getField.get("xmlVersionChanged", false);
    this.documentNumber = getField.get("documentNumber", 0);
    this.nodeCounter = getField.get("nodeCounter", 0);
    Hashtable hashtable3 = (Hashtable)getField.get("nodeTable", null);
    this.xml11Version = getField.get("xml11Version", false);
    if (hashtable1 != null) {
      this.nodeUserData = new HashMap();
      for (Map.Entry entry : hashtable1.entrySet())
        this.nodeUserData.put(entry.getKey(), new HashMap((Map)entry.getValue())); 
    } 
    if (hashtable2 != null)
      this.identifiers = new HashMap(hashtable2); 
    if (hashtable3 != null)
      this.nodeTable = new HashMap(hashtable3); 
  }
  
  static  {
    kidOK[9] = 1410;
    kidOK[1] = 442;
    kidOK[5] = 442;
    kidOK[6] = 442;
    kidOK[11] = 442;
    kidOK[2] = 40;
    kidOK[12] = 0;
    kidOK[4] = 0;
    kidOK[3] = 0;
    kidOK[8] = 0;
    kidOK[7] = 0;
    kidOK[10] = 0;
    serialPersistentFields = new ObjectStreamField[] { 
        new ObjectStreamField("docType", DocumentTypeImpl.class), new ObjectStreamField("docElement", ElementImpl.class), new ObjectStreamField("fFreeNLCache", NodeListCache.class), new ObjectStreamField("encoding", String.class), new ObjectStreamField("actualEncoding", String.class), new ObjectStreamField("version", String.class), new ObjectStreamField("standalone", boolean.class), new ObjectStreamField("fDocumentURI", String.class), new ObjectStreamField("userData", Hashtable.class), new ObjectStreamField("identifiers", Hashtable.class), 
        new ObjectStreamField("changes", int.class), new ObjectStreamField("allowGrammarAccess", boolean.class), new ObjectStreamField("errorChecking", boolean.class), new ObjectStreamField("ancestorChecking", boolean.class), new ObjectStreamField("xmlVersionChanged", boolean.class), new ObjectStreamField("documentNumber", int.class), new ObjectStreamField("nodeCounter", int.class), new ObjectStreamField("nodeTable", Hashtable.class), new ObjectStreamField("xml11Version", boolean.class) };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\CoreDocumentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */