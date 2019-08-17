package com.sun.org.apache.xerces.internal.parsers;

import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMMessageFormatter;
import com.sun.org.apache.xerces.internal.dom.DeferredDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.org.apache.xerces.internal.dom.DocumentTypeImpl;
import com.sun.org.apache.xerces.internal.dom.ElementDefinitionImpl;
import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.org.apache.xerces.internal.dom.EntityImpl;
import com.sun.org.apache.xerces.internal.dom.EntityReferenceImpl;
import com.sun.org.apache.xerces.internal.dom.NodeImpl;
import com.sun.org.apache.xerces.internal.dom.NotationImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.PSVIElementNSImpl;
import com.sun.org.apache.xerces.internal.dom.TextImpl;
import com.sun.org.apache.xerces.internal.impl.dv.XSSimpleType;
import com.sun.org.apache.xerces.internal.util.DOMErrorHandlerWrapper;
import com.sun.org.apache.xerces.internal.utils.ObjectFactory;
import com.sun.org.apache.xerces.internal.xni.Augmentations;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParserConfiguration;
import com.sun.org.apache.xerces.internal.xs.AttributePSVI;
import com.sun.org.apache.xerces.internal.xs.ElementPSVI;
import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;
import com.sun.org.apache.xerces.internal.xs.XSTypeDefinition;
import java.util.Locale;
import java.util.Stack;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.ls.LSParserFilter;

public class AbstractDOMParser extends AbstractXMLDocumentParser {
  protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
  
  protected static final String CREATE_ENTITY_REF_NODES = "http://apache.org/xml/features/dom/create-entity-ref-nodes";
  
  protected static final String INCLUDE_COMMENTS_FEATURE = "http://apache.org/xml/features/include-comments";
  
  protected static final String CREATE_CDATA_NODES_FEATURE = "http://apache.org/xml/features/create-cdata-nodes";
  
  protected static final String INCLUDE_IGNORABLE_WHITESPACE = "http://apache.org/xml/features/dom/include-ignorable-whitespace";
  
  protected static final String DEFER_NODE_EXPANSION = "http://apache.org/xml/features/dom/defer-node-expansion";
  
  private static final String[] RECOGNIZED_FEATURES = { "http://xml.org/sax/features/namespaces", "http://apache.org/xml/features/dom/create-entity-ref-nodes", "http://apache.org/xml/features/include-comments", "http://apache.org/xml/features/create-cdata-nodes", "http://apache.org/xml/features/dom/include-ignorable-whitespace", "http://apache.org/xml/features/dom/defer-node-expansion" };
  
  protected static final String DOCUMENT_CLASS_NAME = "http://apache.org/xml/properties/dom/document-class-name";
  
  protected static final String CURRENT_ELEMENT_NODE = "http://apache.org/xml/properties/dom/current-element-node";
  
  private static final String[] RECOGNIZED_PROPERTIES = { "http://apache.org/xml/properties/dom/document-class-name", "http://apache.org/xml/properties/dom/current-element-node" };
  
  protected static final String DEFAULT_DOCUMENT_CLASS_NAME = "com.sun.org.apache.xerces.internal.dom.DocumentImpl";
  
  protected static final String CORE_DOCUMENT_CLASS_NAME = "com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl";
  
  protected static final String PSVI_DOCUMENT_CLASS_NAME = "com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl";
  
  private static final boolean DEBUG_EVENTS = false;
  
  private static final boolean DEBUG_BASEURI = false;
  
  protected DOMErrorHandlerWrapper fErrorHandler = null;
  
  protected boolean fInDTD;
  
  protected boolean fCreateEntityRefNodes;
  
  protected boolean fIncludeIgnorableWhitespace;
  
  protected boolean fIncludeComments;
  
  protected boolean fCreateCDATANodes;
  
  protected Document fDocument;
  
  protected CoreDocumentImpl fDocumentImpl;
  
  protected boolean fStorePSVI;
  
  protected String fDocumentClassName;
  
  protected DocumentType fDocumentType;
  
  protected Node fCurrentNode;
  
  protected CDATASection fCurrentCDATASection;
  
  protected EntityImpl fCurrentEntityDecl;
  
  protected int fDeferredEntityDecl;
  
  protected final StringBuilder fStringBuilder = new StringBuilder(50);
  
  protected StringBuilder fInternalSubset;
  
  protected boolean fDeferNodeExpansion;
  
  protected boolean fNamespaceAware;
  
  protected DeferredDocumentImpl fDeferredDocumentImpl;
  
  protected int fDocumentIndex;
  
  protected int fDocumentTypeIndex;
  
  protected int fCurrentNodeIndex;
  
  protected int fCurrentCDATASectionIndex;
  
  protected boolean fInDTDExternalSubset;
  
  protected Node fRoot;
  
  protected boolean fInCDATASection;
  
  protected boolean fFirstChunk = false;
  
  protected boolean fFilterReject = false;
  
  protected final Stack fBaseURIStack = new Stack();
  
  protected int fRejectedElementDepth = 0;
  
  protected Stack fSkippedElemStack = null;
  
  protected boolean fInEntityRef = false;
  
  private final QName fAttrQName = new QName();
  
  private XMLLocator fLocator;
  
  protected LSParserFilter fDOMFilter = null;
  
  protected AbstractDOMParser(XMLParserConfiguration paramXMLParserConfiguration) {
    super(paramXMLParserConfiguration);
    this.fConfiguration.addRecognizedFeatures(RECOGNIZED_FEATURES);
    this.fConfiguration.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", true);
    this.fConfiguration.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", true);
    this.fConfiguration.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
    this.fConfiguration.setFeature("http://apache.org/xml/features/include-comments", true);
    this.fConfiguration.setFeature("http://apache.org/xml/features/create-cdata-nodes", true);
    this.fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
    this.fConfiguration.setProperty("http://apache.org/xml/properties/dom/document-class-name", "com.sun.org.apache.xerces.internal.dom.DocumentImpl");
  }
  
  protected String getDocumentClassName() { return this.fDocumentClassName; }
  
  protected void setDocumentClassName(String paramString) {
    if (paramString == null)
      paramString = "com.sun.org.apache.xerces.internal.dom.DocumentImpl"; 
    if (!paramString.equals("com.sun.org.apache.xerces.internal.dom.DocumentImpl") && !paramString.equals("com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl"))
      try {
        Class clazz = ObjectFactory.findProviderClass(paramString, true);
        if (!Document.class.isAssignableFrom(clazz))
          throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "InvalidDocumentClassName", new Object[] { paramString })); 
      } catch (ClassNotFoundException classNotFoundException) {
        throw new IllegalArgumentException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "MissingDocumentClassName", new Object[] { paramString }));
      }  
    this.fDocumentClassName = paramString;
    if (!paramString.equals("com.sun.org.apache.xerces.internal.dom.DocumentImpl"))
      this.fDeferNodeExpansion = false; 
  }
  
  public Document getDocument() { return this.fDocument; }
  
  public final void dropDocumentReferences() {
    this.fDocument = null;
    this.fDocumentImpl = null;
    this.fDeferredDocumentImpl = null;
    this.fDocumentType = null;
    this.fCurrentNode = null;
    this.fCurrentCDATASection = null;
    this.fCurrentEntityDecl = null;
    this.fRoot = null;
  }
  
  public void reset() {
    super.reset();
    this.fCreateEntityRefNodes = this.fConfiguration.getFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes");
    this.fIncludeIgnorableWhitespace = this.fConfiguration.getFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace");
    this.fDeferNodeExpansion = this.fConfiguration.getFeature("http://apache.org/xml/features/dom/defer-node-expansion");
    this.fNamespaceAware = this.fConfiguration.getFeature("http://xml.org/sax/features/namespaces");
    this.fIncludeComments = this.fConfiguration.getFeature("http://apache.org/xml/features/include-comments");
    this.fCreateCDATANodes = this.fConfiguration.getFeature("http://apache.org/xml/features/create-cdata-nodes");
    setDocumentClassName((String)this.fConfiguration.getProperty("http://apache.org/xml/properties/dom/document-class-name"));
    this.fDocument = null;
    this.fDocumentImpl = null;
    this.fStorePSVI = false;
    this.fDocumentType = null;
    this.fDocumentTypeIndex = -1;
    this.fDeferredDocumentImpl = null;
    this.fCurrentNode = null;
    this.fStringBuilder.setLength(0);
    this.fRoot = null;
    this.fInDTD = false;
    this.fInDTDExternalSubset = false;
    this.fInCDATASection = false;
    this.fFirstChunk = false;
    this.fCurrentCDATASection = null;
    this.fCurrentCDATASectionIndex = -1;
    this.fBaseURIStack.removeAllElements();
  }
  
  public void setLocale(Locale paramLocale) { this.fConfiguration.setLocale(paramLocale); }
  
  public void startGeneralEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {
    if (!this.fDeferNodeExpansion) {
      if (this.fFilterReject)
        return; 
      setCharacterData(true);
      EntityReference entityReference = this.fDocument.createEntityReference(paramString1);
      if (this.fDocumentImpl != null) {
        EntityReferenceImpl entityReferenceImpl = (EntityReferenceImpl)entityReference;
        entityReferenceImpl.setBaseURI(paramXMLResourceIdentifier.getExpandedSystemId());
        if (this.fDocumentType != null) {
          NamedNodeMap namedNodeMap = this.fDocumentType.getEntities();
          this.fCurrentEntityDecl = (EntityImpl)namedNodeMap.getNamedItem(paramString1);
          if (this.fCurrentEntityDecl != null)
            this.fCurrentEntityDecl.setInputEncoding(paramString2); 
        } 
        entityReferenceImpl.needsSyncChildren(false);
      } 
      this.fInEntityRef = true;
      this.fCurrentNode.appendChild(entityReference);
      this.fCurrentNode = entityReference;
    } else {
      int i = this.fDeferredDocumentImpl.createDeferredEntityReference(paramString1, paramXMLResourceIdentifier.getExpandedSystemId());
      if (this.fDocumentTypeIndex != -1) {
        int j;
        for (j = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); j != -1; j = this.fDeferredDocumentImpl.getRealPrevSibling(j, false)) {
          short s = this.fDeferredDocumentImpl.getNodeType(j, false);
          if (s == 6) {
            String str = this.fDeferredDocumentImpl.getNodeName(j, false);
            if (str.equals(paramString1)) {
              this.fDeferredEntityDecl = j;
              this.fDeferredDocumentImpl.setInputEncoding(j, paramString2);
              break;
            } 
          } 
        } 
      } 
      this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, i);
      this.fCurrentNodeIndex = i;
    } 
  }
  
  public void textDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException {
    if (this.fInDTD)
      return; 
    if (!this.fDeferNodeExpansion) {
      if (this.fCurrentEntityDecl != null && !this.fFilterReject) {
        this.fCurrentEntityDecl.setXmlEncoding(paramString2);
        if (paramString1 != null)
          this.fCurrentEntityDecl.setXmlVersion(paramString1); 
      } 
    } else if (this.fDeferredEntityDecl != -1) {
      this.fDeferredDocumentImpl.setEntityInfo(this.fDeferredEntityDecl, paramString1, paramString2);
    } 
  }
  
  public void comment(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fInDTD) {
      if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
        this.fInternalSubset.append("<!--");
        if (paramXMLString.length > 0)
          this.fInternalSubset.append(paramXMLString.ch, paramXMLString.offset, paramXMLString.length); 
        this.fInternalSubset.append("-->");
      } 
      return;
    } 
    if (!this.fIncludeComments || this.fFilterReject)
      return; 
    if (!this.fDeferNodeExpansion) {
      Comment comment = this.fDocument.createComment(paramXMLString.toString());
      setCharacterData(false);
      this.fCurrentNode.appendChild(comment);
      if (this.fDOMFilter != null && !this.fInEntityRef && (this.fDOMFilter.getWhatToShow() & 0x80) != 0) {
        short s = this.fDOMFilter.acceptNode(comment);
        switch (s) {
          case 4:
            throw Abort.INSTANCE;
          case 2:
          case 3:
            this.fCurrentNode.removeChild(comment);
            this.fFirstChunk = true;
            return;
        } 
      } 
    } else {
      int i = this.fDeferredDocumentImpl.createDeferredComment(paramXMLString.toString());
      this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, i);
    } 
  }
  
  public void processingInstruction(String paramString, XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (this.fInDTD) {
      if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
        this.fInternalSubset.append("<?");
        this.fInternalSubset.append(paramString);
        if (paramXMLString.length > 0)
          this.fInternalSubset.append(' ').append(paramXMLString.ch, paramXMLString.offset, paramXMLString.length); 
        this.fInternalSubset.append("?>");
      } 
      return;
    } 
    if (!this.fDeferNodeExpansion) {
      if (this.fFilterReject)
        return; 
      ProcessingInstruction processingInstruction = this.fDocument.createProcessingInstruction(paramString, paramXMLString.toString());
      setCharacterData(false);
      this.fCurrentNode.appendChild(processingInstruction);
      if (this.fDOMFilter != null && !this.fInEntityRef && (this.fDOMFilter.getWhatToShow() & 0x40) != 0) {
        short s = this.fDOMFilter.acceptNode(processingInstruction);
        switch (s) {
          case 4:
            throw Abort.INSTANCE;
          case 2:
          case 3:
            this.fCurrentNode.removeChild(processingInstruction);
            this.fFirstChunk = true;
            return;
        } 
      } 
    } else {
      int i = this.fDeferredDocumentImpl.createDeferredProcessingInstruction(paramString, paramXMLString.toString());
      this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, i);
    } 
  }
  
  public void startDocument(XMLLocator paramXMLLocator, String paramString, NamespaceContext paramNamespaceContext, Augmentations paramAugmentations) throws XNIException {
    this.fLocator = paramXMLLocator;
    if (!this.fDeferNodeExpansion) {
      if (this.fDocumentClassName.equals("com.sun.org.apache.xerces.internal.dom.DocumentImpl")) {
        this.fDocument = new DocumentImpl();
        this.fDocumentImpl = (CoreDocumentImpl)this.fDocument;
        this.fDocumentImpl.setStrictErrorChecking(false);
        this.fDocumentImpl.setInputEncoding(paramString);
        this.fDocumentImpl.setDocumentURI(paramXMLLocator.getExpandedSystemId());
      } else if (this.fDocumentClassName.equals("com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl")) {
        this.fDocument = new PSVIDocumentImpl();
        this.fDocumentImpl = (CoreDocumentImpl)this.fDocument;
        this.fStorePSVI = true;
        this.fDocumentImpl.setStrictErrorChecking(false);
        this.fDocumentImpl.setInputEncoding(paramString);
        this.fDocumentImpl.setDocumentURI(paramXMLLocator.getExpandedSystemId());
      } else {
        try {
          Class clazz1 = ObjectFactory.findProviderClass(this.fDocumentClassName, true);
          this.fDocument = (Document)clazz1.newInstance();
          Class clazz2 = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl", true);
          if (clazz2.isAssignableFrom(clazz1)) {
            this.fDocumentImpl = (CoreDocumentImpl)this.fDocument;
            Class clazz = ObjectFactory.findProviderClass("com.sun.org.apache.xerces.internal.dom.PSVIDocumentImpl", true);
            if (clazz.isAssignableFrom(clazz1))
              this.fStorePSVI = true; 
            this.fDocumentImpl.setStrictErrorChecking(false);
            this.fDocumentImpl.setInputEncoding(paramString);
            if (paramXMLLocator != null)
              this.fDocumentImpl.setDocumentURI(paramXMLLocator.getExpandedSystemId()); 
          } 
        } catch (ClassNotFoundException classNotFoundException) {
        
        } catch (Exception exception) {
          throw new RuntimeException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "CannotCreateDocumentClass", new Object[] { this.fDocumentClassName }));
        } 
      } 
      this.fCurrentNode = this.fDocument;
    } else {
      this.fDeferredDocumentImpl = new DeferredDocumentImpl(this.fNamespaceAware);
      this.fDocument = this.fDeferredDocumentImpl;
      this.fDocumentIndex = this.fDeferredDocumentImpl.createDeferredDocument();
      this.fDeferredDocumentImpl.setInputEncoding(paramString);
      this.fDeferredDocumentImpl.setDocumentURI(paramXMLLocator.getExpandedSystemId());
      this.fCurrentNodeIndex = this.fDocumentIndex;
    } 
  }
  
  public void xmlDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {
    if (!this.fDeferNodeExpansion) {
      if (this.fDocumentImpl != null) {
        if (paramString1 != null)
          this.fDocumentImpl.setXmlVersion(paramString1); 
        this.fDocumentImpl.setXmlEncoding(paramString2);
        this.fDocumentImpl.setXmlStandalone("yes".equals(paramString3));
      } 
    } else {
      if (paramString1 != null)
        this.fDeferredDocumentImpl.setXmlVersion(paramString1); 
      this.fDeferredDocumentImpl.setXmlEncoding(paramString2);
      this.fDeferredDocumentImpl.setXmlStandalone("yes".equals(paramString3));
    } 
  }
  
  public void doctypeDecl(String paramString1, String paramString2, String paramString3, Augmentations paramAugmentations) throws XNIException {
    if (!this.fDeferNodeExpansion) {
      if (this.fDocumentImpl != null) {
        this.fDocumentType = this.fDocumentImpl.createDocumentType(paramString1, paramString2, paramString3);
        this.fCurrentNode.appendChild(this.fDocumentType);
      } 
    } else {
      this.fDocumentTypeIndex = this.fDeferredDocumentImpl.createDeferredDocumentType(paramString1, paramString2, paramString3);
      this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, this.fDocumentTypeIndex);
    } 
  }
  
  public void startElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    if (!this.fDeferNodeExpansion) {
      if (this.fFilterReject) {
        this.fRejectedElementDepth++;
        return;
      } 
      Element element = createElementNode(paramQName);
      int i = paramXMLAttributes.getLength();
      boolean bool = false;
      short s;
      for (s = 0; s < i; s++) {
        paramXMLAttributes.getName(s, this.fAttrQName);
        Attr attr = createAttrNode(this.fAttrQName);
        String str = paramXMLAttributes.getValue(s);
        AttributePSVI attributePSVI = (AttributePSVI)paramXMLAttributes.getAugmentations(s).getItem("ATTRIBUTE_PSVI");
        if (this.fStorePSVI && attributePSVI != null)
          ((PSVIAttrNSImpl)attr).setPSVI(attributePSVI); 
        attr.setValue(str);
        boolean bool1 = paramXMLAttributes.isSpecified(s);
        if (!bool1 && (bool || (this.fAttrQName.uri != null && this.fAttrQName.uri != NamespaceContext.XMLNS_URI && this.fAttrQName.prefix == null))) {
          element.setAttributeNodeNS(attr);
          bool = true;
        } else {
          element.setAttributeNode(attr);
        } 
        if (this.fDocumentImpl != null) {
          AttrImpl attrImpl = (AttrImpl)attr;
          XSSimpleTypeDefinition xSSimpleTypeDefinition = null;
          boolean bool2 = false;
          if (attributePSVI != null && this.fNamespaceAware) {
            XSTypeDefinition xSTypeDefinition;
            xSSimpleTypeDefinition = attributePSVI.getMemberTypeDefinition();
            if (xSSimpleTypeDefinition == null) {
              xSTypeDefinition = attributePSVI.getTypeDefinition();
              if (xSTypeDefinition != null) {
                bool2 = ((XSSimpleType)xSTypeDefinition).isIDType();
                attrImpl.setType(xSTypeDefinition);
              } 
            } else {
              bool2 = ((XSSimpleType)xSTypeDefinition).isIDType();
              attrImpl.setType(xSTypeDefinition);
            } 
          } else {
            String str1;
            boolean bool3 = Boolean.TRUE.equals(paramXMLAttributes.getAugmentations(s).getItem("ATTRIBUTE_DECLARED"));
            if (bool3) {
              str1 = paramXMLAttributes.getType(s);
              bool2 = "ID".equals(str1);
            } 
            attrImpl.setType(str1);
          } 
          if (bool2)
            ((ElementImpl)element).setIdAttributeNode(attr, true); 
          attrImpl.setSpecified(bool1);
        } 
      } 
      setCharacterData(false);
      if (paramAugmentations != null) {
        ElementPSVI elementPSVI = (ElementPSVI)paramAugmentations.getItem("ELEMENT_PSVI");
        if (elementPSVI != null && this.fNamespaceAware) {
          XSTypeDefinition xSTypeDefinition = elementPSVI.getMemberTypeDefinition();
          if (xSTypeDefinition == null)
            xSTypeDefinition = elementPSVI.getTypeDefinition(); 
          ((ElementNSImpl)element).setType(xSTypeDefinition);
        } 
      } 
      if (this.fDOMFilter != null && !this.fInEntityRef)
        if (this.fRoot == null) {
          this.fRoot = element;
        } else {
          s = this.fDOMFilter.startElement(element);
          switch (s) {
            case 4:
              throw Abort.INSTANCE;
            case 2:
              this.fFilterReject = true;
              this.fRejectedElementDepth = 0;
              return;
            case 3:
              this.fFirstChunk = true;
              this.fSkippedElemStack.push(Boolean.TRUE);
              return;
          } 
          if (!this.fSkippedElemStack.isEmpty())
            this.fSkippedElemStack.push(Boolean.FALSE); 
        }  
      this.fCurrentNode.appendChild(element);
      this.fCurrentNode = element;
    } else {
      int i = this.fDeferredDocumentImpl.createDeferredElement(this.fNamespaceAware ? paramQName.uri : null, paramQName.rawname);
      XSSimpleTypeDefinition xSSimpleTypeDefinition = null;
      int j = paramXMLAttributes.getLength();
      for (int k = j - 1; k >= 0; k--) {
        String str;
        AttributePSVI attributePSVI = (AttributePSVI)paramXMLAttributes.getAugmentations(k).getItem("ATTRIBUTE_PSVI");
        boolean bool = false;
        if (attributePSVI != null && this.fNamespaceAware) {
          xSSimpleTypeDefinition = attributePSVI.getMemberTypeDefinition();
          if (xSSimpleTypeDefinition == null) {
            str = attributePSVI.getTypeDefinition();
            if (str != null)
              bool = ((XSSimpleType)str).isIDType(); 
          } else {
            bool = ((XSSimpleType)str).isIDType();
          } 
        } else {
          boolean bool1 = Boolean.TRUE.equals(paramXMLAttributes.getAugmentations(k).getItem("ATTRIBUTE_DECLARED"));
          if (bool1) {
            str = paramXMLAttributes.getType(k);
            bool = "ID".equals(str);
          } 
        } 
        this.fDeferredDocumentImpl.setDeferredAttribute(i, paramXMLAttributes.getQName(k), paramXMLAttributes.getURI(k), paramXMLAttributes.getValue(k), paramXMLAttributes.isSpecified(k), bool, str);
      } 
      this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, i);
      this.fCurrentNodeIndex = i;
    } 
  }
  
  public void emptyElement(QName paramQName, XMLAttributes paramXMLAttributes, Augmentations paramAugmentations) throws XNIException {
    startElement(paramQName, paramXMLAttributes, paramAugmentations);
    endElement(paramQName, paramAugmentations);
  }
  
  public void characters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (!this.fDeferNodeExpansion) {
      if (this.fFilterReject)
        return; 
      if (this.fInCDATASection && this.fCreateCDATANodes) {
        if (this.fCurrentCDATASection == null) {
          this.fCurrentCDATASection = this.fDocument.createCDATASection(paramXMLString.toString());
          this.fCurrentNode.appendChild(this.fCurrentCDATASection);
          this.fCurrentNode = this.fCurrentCDATASection;
        } else {
          this.fCurrentCDATASection.appendData(paramXMLString.toString());
        } 
      } else if (!this.fInDTD) {
        if (paramXMLString.length == 0)
          return; 
        Node node = this.fCurrentNode.getLastChild();
        if (node != null && node.getNodeType() == 3) {
          if (this.fFirstChunk) {
            if (this.fDocumentImpl != null) {
              this.fStringBuilder.append(((TextImpl)node).removeData());
            } else {
              this.fStringBuilder.append(((Text)node).getData());
              ((Text)node).setNodeValue(null);
            } 
            this.fFirstChunk = false;
          } 
          if (paramXMLString.length > 0)
            this.fStringBuilder.append(paramXMLString.ch, paramXMLString.offset, paramXMLString.length); 
        } else {
          this.fFirstChunk = true;
          Text text = this.fDocument.createTextNode(paramXMLString.toString());
          this.fCurrentNode.appendChild(text);
        } 
      } 
    } else if (this.fInCDATASection && this.fCreateCDATANodes) {
      if (this.fCurrentCDATASectionIndex == -1) {
        int i = this.fDeferredDocumentImpl.createDeferredCDATASection(paramXMLString.toString());
        this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, i);
        this.fCurrentCDATASectionIndex = i;
        this.fCurrentNodeIndex = i;
      } else {
        int i = this.fDeferredDocumentImpl.createDeferredTextNode(paramXMLString.toString(), false);
        this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, i);
      } 
    } else if (!this.fInDTD) {
      if (paramXMLString.length == 0)
        return; 
      String str = paramXMLString.toString();
      int i = this.fDeferredDocumentImpl.createDeferredTextNode(str, false);
      this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, i);
    } 
  }
  
  public void ignorableWhitespace(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {
    if (!this.fIncludeIgnorableWhitespace || this.fFilterReject)
      return; 
    if (!this.fDeferNodeExpansion) {
      Node node = this.fCurrentNode.getLastChild();
      if (node != null && node.getNodeType() == 3) {
        Text text = (Text)node;
        text.appendData(paramXMLString.toString());
      } else {
        Text text = this.fDocument.createTextNode(paramXMLString.toString());
        if (this.fDocumentImpl != null) {
          TextImpl textImpl = (TextImpl)text;
          textImpl.setIgnorableWhitespace(true);
        } 
        this.fCurrentNode.appendChild(text);
      } 
    } else {
      int i = this.fDeferredDocumentImpl.createDeferredTextNode(paramXMLString.toString(), true);
      this.fDeferredDocumentImpl.appendChild(this.fCurrentNodeIndex, i);
    } 
  }
  
  public void endElement(QName paramQName, Augmentations paramAugmentations) throws XNIException {
    if (!this.fDeferNodeExpansion) {
      if (paramAugmentations != null && this.fDocumentImpl != null && (this.fNamespaceAware || this.fStorePSVI)) {
        ElementPSVI elementPSVI = (ElementPSVI)paramAugmentations.getItem("ELEMENT_PSVI");
        if (elementPSVI != null) {
          if (this.fNamespaceAware) {
            XSTypeDefinition xSTypeDefinition = elementPSVI.getMemberTypeDefinition();
            if (xSTypeDefinition == null)
              xSTypeDefinition = elementPSVI.getTypeDefinition(); 
            ((ElementNSImpl)this.fCurrentNode).setType(xSTypeDefinition);
          } 
          if (this.fStorePSVI)
            ((PSVIElementNSImpl)this.fCurrentNode).setPSVI(elementPSVI); 
        } 
      } 
      if (this.fDOMFilter != null) {
        if (this.fFilterReject) {
          if (this.fRejectedElementDepth-- == 0)
            this.fFilterReject = false; 
          return;
        } 
        if (!this.fSkippedElemStack.isEmpty() && this.fSkippedElemStack.pop() == Boolean.TRUE)
          return; 
        setCharacterData(false);
        if (this.fCurrentNode != this.fRoot && !this.fInEntityRef && (this.fDOMFilter.getWhatToShow() & true) != 0) {
          byte b;
          int i;
          NodeList nodeList;
          Node node;
          short s = this.fDOMFilter.acceptNode(this.fCurrentNode);
          switch (s) {
            case 4:
              throw Abort.INSTANCE;
            case 2:
              node = this.fCurrentNode.getParentNode();
              node.removeChild(this.fCurrentNode);
              this.fCurrentNode = node;
              return;
            case 3:
              this.fFirstChunk = true;
              node = this.fCurrentNode.getParentNode();
              nodeList = this.fCurrentNode.getChildNodes();
              i = nodeList.getLength();
              for (b = 0; b < i; b++)
                node.appendChild(nodeList.item(0)); 
              node.removeChild(this.fCurrentNode);
              this.fCurrentNode = node;
              return;
          } 
        } 
        this.fCurrentNode = this.fCurrentNode.getParentNode();
      } else {
        setCharacterData(false);
        this.fCurrentNode = this.fCurrentNode.getParentNode();
      } 
    } else {
      if (paramAugmentations != null) {
        ElementPSVI elementPSVI = (ElementPSVI)paramAugmentations.getItem("ELEMENT_PSVI");
        if (elementPSVI != null) {
          XSTypeDefinition xSTypeDefinition = elementPSVI.getMemberTypeDefinition();
          if (xSTypeDefinition == null)
            xSTypeDefinition = elementPSVI.getTypeDefinition(); 
          this.fDeferredDocumentImpl.setTypeInfo(this.fCurrentNodeIndex, xSTypeDefinition);
        } 
      } 
      this.fCurrentNodeIndex = this.fDeferredDocumentImpl.getParentNode(this.fCurrentNodeIndex, false);
    } 
  }
  
  public void startCDATA(Augmentations paramAugmentations) throws XNIException {
    this.fInCDATASection = true;
    if (!this.fDeferNodeExpansion) {
      if (this.fFilterReject)
        return; 
      if (this.fCreateCDATANodes)
        setCharacterData(false); 
    } 
  }
  
  public void endCDATA(Augmentations paramAugmentations) throws XNIException {
    this.fInCDATASection = false;
    if (!this.fDeferNodeExpansion) {
      if (this.fFilterReject)
        return; 
      if (this.fCurrentCDATASection != null) {
        if (this.fDOMFilter != null && !this.fInEntityRef && (this.fDOMFilter.getWhatToShow() & 0x8) != 0) {
          Node node;
          short s = this.fDOMFilter.acceptNode(this.fCurrentCDATASection);
          switch (s) {
            case 4:
              throw Abort.INSTANCE;
            case 2:
            case 3:
              node = this.fCurrentNode.getParentNode();
              node.removeChild(this.fCurrentCDATASection);
              this.fCurrentNode = node;
              return;
          } 
        } 
        this.fCurrentNode = this.fCurrentNode.getParentNode();
        this.fCurrentCDATASection = null;
      } 
    } else if (this.fCurrentCDATASectionIndex != -1) {
      this.fCurrentNodeIndex = this.fDeferredDocumentImpl.getParentNode(this.fCurrentNodeIndex, false);
      this.fCurrentCDATASectionIndex = -1;
    } 
  }
  
  public void endDocument(Augmentations paramAugmentations) throws XNIException {
    if (!this.fDeferNodeExpansion) {
      if (this.fDocumentImpl != null) {
        if (this.fLocator != null && this.fLocator.getEncoding() != null)
          this.fDocumentImpl.setInputEncoding(this.fLocator.getEncoding()); 
        this.fDocumentImpl.setStrictErrorChecking(true);
      } 
      this.fCurrentNode = null;
    } else {
      if (this.fLocator != null && this.fLocator.getEncoding() != null)
        this.fDeferredDocumentImpl.setInputEncoding(this.fLocator.getEncoding()); 
      this.fCurrentNodeIndex = -1;
    } 
  }
  
  public void endGeneralEntity(String paramString, Augmentations paramAugmentations) throws XNIException {
    if (!this.fDeferNodeExpansion) {
      if (this.fFilterReject)
        return; 
      setCharacterData(true);
      if (this.fDocumentType != null) {
        NamedNodeMap namedNodeMap = this.fDocumentType.getEntities();
        this.fCurrentEntityDecl = (EntityImpl)namedNodeMap.getNamedItem(paramString);
        if (this.fCurrentEntityDecl != null) {
          if (this.fCurrentEntityDecl != null && this.fCurrentEntityDecl.getFirstChild() == null) {
            this.fCurrentEntityDecl.setReadOnly(false, true);
            for (Node node = this.fCurrentNode.getFirstChild(); node != null; node = node.getNextSibling()) {
              Node node1 = node.cloneNode(true);
              this.fCurrentEntityDecl.appendChild(node1);
            } 
            this.fCurrentEntityDecl.setReadOnly(true, true);
          } 
          this.fCurrentEntityDecl = null;
        } 
      } 
      this.fInEntityRef = false;
      boolean bool = false;
      if (this.fCreateEntityRefNodes) {
        if (this.fDocumentImpl != null)
          ((NodeImpl)this.fCurrentNode).setReadOnly(true, true); 
        if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 0x10) != 0) {
          Node node;
          short s = this.fDOMFilter.acceptNode(this.fCurrentNode);
          switch (s) {
            case 4:
              throw Abort.INSTANCE;
            case 2:
              node = this.fCurrentNode.getParentNode();
              node.removeChild(this.fCurrentNode);
              this.fCurrentNode = node;
              return;
            case 3:
              this.fFirstChunk = true;
              bool = true;
              break;
            default:
              this.fCurrentNode = this.fCurrentNode.getParentNode();
              break;
          } 
        } else {
          this.fCurrentNode = this.fCurrentNode.getParentNode();
        } 
      } 
      if (!this.fCreateEntityRefNodes || bool) {
        NodeList nodeList = this.fCurrentNode.getChildNodes();
        Node node = this.fCurrentNode.getParentNode();
        int i = nodeList.getLength();
        if (i > 0) {
          Node node1 = this.fCurrentNode.getPreviousSibling();
          Node node2 = nodeList.item(0);
          if (node1 != null && node1.getNodeType() == 3 && node2.getNodeType() == 3) {
            ((Text)node1).appendData(node2.getNodeValue());
            this.fCurrentNode.removeChild(node2);
          } else {
            node1 = node.insertBefore(node2, this.fCurrentNode);
            handleBaseURI(node1);
          } 
          for (byte b = 1; b < i; b++) {
            node1 = node.insertBefore(nodeList.item(0), this.fCurrentNode);
            handleBaseURI(node1);
          } 
        } 
        node.removeChild(this.fCurrentNode);
        this.fCurrentNode = node;
      } 
    } else {
      if (this.fDocumentTypeIndex != -1)
        for (int i = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); i != -1; i = this.fDeferredDocumentImpl.getRealPrevSibling(i, false)) {
          short s = this.fDeferredDocumentImpl.getNodeType(i, false);
          if (s == 6) {
            String str = this.fDeferredDocumentImpl.getNodeName(i, false);
            if (str.equals(paramString)) {
              this.fDeferredEntityDecl = i;
              break;
            } 
          } 
        }  
      if (this.fDeferredEntityDecl != -1 && this.fDeferredDocumentImpl.getLastChild(this.fDeferredEntityDecl, false) == -1) {
        int i = -1;
        int j;
        for (j = this.fDeferredDocumentImpl.getLastChild(this.fCurrentNodeIndex, false); j != -1; j = this.fDeferredDocumentImpl.getRealPrevSibling(j, false)) {
          int k = this.fDeferredDocumentImpl.cloneNode(j, true);
          this.fDeferredDocumentImpl.insertBefore(this.fDeferredEntityDecl, k, i);
          i = k;
        } 
      } 
      if (this.fCreateEntityRefNodes) {
        this.fCurrentNodeIndex = this.fDeferredDocumentImpl.getParentNode(this.fCurrentNodeIndex, false);
      } else {
        int i = this.fDeferredDocumentImpl.getLastChild(this.fCurrentNodeIndex, false);
        int j = this.fDeferredDocumentImpl.getParentNode(this.fCurrentNodeIndex, false);
        int k = this.fCurrentNodeIndex;
        int m = i;
        int n = -1;
        while (i != -1) {
          handleBaseURI(i);
          n = this.fDeferredDocumentImpl.getRealPrevSibling(i, false);
          this.fDeferredDocumentImpl.insertBefore(j, i, k);
          k = i;
          i = n;
        } 
        if (m != -1) {
          this.fDeferredDocumentImpl.setAsLastChild(j, m);
        } else {
          n = this.fDeferredDocumentImpl.getRealPrevSibling(k, false);
          this.fDeferredDocumentImpl.setAsLastChild(j, n);
        } 
        this.fCurrentNodeIndex = j;
      } 
      this.fDeferredEntityDecl = -1;
    } 
  }
  
  protected final void handleBaseURI(Node paramNode) {
    if (this.fDocumentImpl != null) {
      String str = null;
      short s = paramNode.getNodeType();
      if (s == 1) {
        if (this.fNamespaceAware) {
          if (((Element)paramNode).getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "base") != null)
            return; 
        } else if (((Element)paramNode).getAttributeNode("xml:base") != null) {
          return;
        } 
        str = ((EntityReferenceImpl)this.fCurrentNode).getBaseURI();
        if (str != null && !str.equals(this.fDocumentImpl.getDocumentURI()))
          if (this.fNamespaceAware) {
            ((Element)paramNode).setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:base", str);
          } else {
            ((Element)paramNode).setAttribute("xml:base", str);
          }  
      } else if (s == 7) {
        str = ((EntityReferenceImpl)this.fCurrentNode).getBaseURI();
        if (str != null && this.fErrorHandler != null) {
          DOMErrorImpl dOMErrorImpl = new DOMErrorImpl();
          dOMErrorImpl.fType = "pi-base-uri-not-preserved";
          dOMErrorImpl.fRelatedData = str;
          dOMErrorImpl.fSeverity = 1;
          this.fErrorHandler.getErrorHandler().handleError(dOMErrorImpl);
        } 
      } 
    } 
  }
  
  protected final void handleBaseURI(int paramInt) {
    short s = this.fDeferredDocumentImpl.getNodeType(paramInt, false);
    if (s == 1) {
      String str = this.fDeferredDocumentImpl.getNodeValueString(this.fCurrentNodeIndex, false);
      if (str == null)
        str = this.fDeferredDocumentImpl.getDeferredEntityBaseURI(this.fDeferredEntityDecl); 
      if (str != null && !str.equals(this.fDeferredDocumentImpl.getDocumentURI()))
        this.fDeferredDocumentImpl.setDeferredAttribute(paramInt, "xml:base", "http://www.w3.org/XML/1998/namespace", str, true); 
    } else if (s == 7) {
      String str = this.fDeferredDocumentImpl.getNodeValueString(this.fCurrentNodeIndex, false);
      if (str == null)
        str = this.fDeferredDocumentImpl.getDeferredEntityBaseURI(this.fDeferredEntityDecl); 
      if (str != null && this.fErrorHandler != null) {
        DOMErrorImpl dOMErrorImpl = new DOMErrorImpl();
        dOMErrorImpl.fType = "pi-base-uri-not-preserved";
        dOMErrorImpl.fRelatedData = str;
        dOMErrorImpl.fSeverity = 1;
        this.fErrorHandler.getErrorHandler().handleError(dOMErrorImpl);
      } 
    } 
  }
  
  public void startDTD(XMLLocator paramXMLLocator, Augmentations paramAugmentations) throws XNIException {
    this.fInDTD = true;
    if (paramXMLLocator != null)
      this.fBaseURIStack.push(paramXMLLocator.getBaseSystemId()); 
    if (this.fDeferNodeExpansion || this.fDocumentImpl != null)
      this.fInternalSubset = new StringBuilder(1024); 
  }
  
  public void endDTD(Augmentations paramAugmentations) throws XNIException {
    this.fInDTD = false;
    if (!this.fBaseURIStack.isEmpty())
      this.fBaseURIStack.pop(); 
    String str = (this.fInternalSubset != null && this.fInternalSubset.length() > 0) ? this.fInternalSubset.toString() : null;
    if (this.fDeferNodeExpansion) {
      if (str != null)
        this.fDeferredDocumentImpl.setInternalSubset(this.fDocumentTypeIndex, str); 
    } else if (this.fDocumentImpl != null && str != null) {
      ((DocumentTypeImpl)this.fDocumentType).setInternalSubset(str);
    } 
  }
  
  public void startConditional(short paramShort, Augmentations paramAugmentations) throws XNIException {}
  
  public void endConditional(Augmentations paramAugmentations) throws XNIException {}
  
  public void startExternalSubset(XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations) throws XNIException {
    this.fBaseURIStack.push(paramXMLResourceIdentifier.getBaseSystemId());
    this.fInDTDExternalSubset = true;
  }
  
  public void endExternalSubset(Augmentations paramAugmentations) throws XNIException {
    this.fInDTDExternalSubset = false;
    this.fBaseURIStack.pop();
  }
  
  public void internalEntityDecl(String paramString, XMLString paramXMLString1, XMLString paramXMLString2, Augmentations paramAugmentations) throws XNIException {
    if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
      this.fInternalSubset.append("<!ENTITY ");
      if (paramString.startsWith("%")) {
        this.fInternalSubset.append("% ");
        this.fInternalSubset.append(paramString.substring(1));
      } else {
        this.fInternalSubset.append(paramString);
      } 
      this.fInternalSubset.append(' ');
      String str = paramXMLString2.toString();
      boolean bool = (str.indexOf('\'') == -1) ? 1 : 0;
      this.fInternalSubset.append(bool ? 39 : 34);
      this.fInternalSubset.append(str);
      this.fInternalSubset.append(bool ? 39 : 34);
      this.fInternalSubset.append(">\n");
    } 
    if (paramString.startsWith("%"))
      return; 
    if (this.fDocumentType != null) {
      NamedNodeMap namedNodeMap = this.fDocumentType.getEntities();
      EntityImpl entityImpl = (EntityImpl)namedNodeMap.getNamedItem(paramString);
      if (entityImpl == null) {
        entityImpl = (EntityImpl)this.fDocumentImpl.createEntity(paramString);
        entityImpl.setBaseURI((String)this.fBaseURIStack.peek());
        namedNodeMap.setNamedItem(entityImpl);
      } 
    } 
    if (this.fDocumentTypeIndex != -1) {
      boolean bool = false;
      int i;
      for (i = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); i != -1; i = this.fDeferredDocumentImpl.getRealPrevSibling(i, false)) {
        short s = this.fDeferredDocumentImpl.getNodeType(i, false);
        if (s == 6) {
          String str = this.fDeferredDocumentImpl.getNodeName(i, false);
          if (str.equals(paramString)) {
            bool = true;
            break;
          } 
        } 
      } 
      if (!bool) {
        int j = this.fDeferredDocumentImpl.createDeferredEntity(paramString, null, null, null, (String)this.fBaseURIStack.peek());
        this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, j);
      } 
    } 
  }
  
  public void externalEntityDecl(String paramString, XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations) throws XNIException {
    String str1 = paramXMLResourceIdentifier.getPublicId();
    String str2 = paramXMLResourceIdentifier.getLiteralSystemId();
    if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
      this.fInternalSubset.append("<!ENTITY ");
      if (paramString.startsWith("%")) {
        this.fInternalSubset.append("% ");
        this.fInternalSubset.append(paramString.substring(1));
      } else {
        this.fInternalSubset.append(paramString);
      } 
      this.fInternalSubset.append(' ');
      if (str1 != null) {
        this.fInternalSubset.append("PUBLIC '");
        this.fInternalSubset.append(str1);
        this.fInternalSubset.append("' '");
      } else {
        this.fInternalSubset.append("SYSTEM '");
      } 
      this.fInternalSubset.append(str2);
      this.fInternalSubset.append("'>\n");
    } 
    if (paramString.startsWith("%"))
      return; 
    if (this.fDocumentType != null) {
      NamedNodeMap namedNodeMap = this.fDocumentType.getEntities();
      EntityImpl entityImpl = (EntityImpl)namedNodeMap.getNamedItem(paramString);
      if (entityImpl == null) {
        entityImpl = (EntityImpl)this.fDocumentImpl.createEntity(paramString);
        entityImpl.setPublicId(str1);
        entityImpl.setSystemId(str2);
        entityImpl.setBaseURI(paramXMLResourceIdentifier.getBaseSystemId());
        namedNodeMap.setNamedItem(entityImpl);
      } 
    } 
    if (this.fDocumentTypeIndex != -1) {
      boolean bool = false;
      int i;
      for (i = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); i != -1; i = this.fDeferredDocumentImpl.getRealPrevSibling(i, false)) {
        short s = this.fDeferredDocumentImpl.getNodeType(i, false);
        if (s == 6) {
          String str = this.fDeferredDocumentImpl.getNodeName(i, false);
          if (str.equals(paramString)) {
            bool = true;
            break;
          } 
        } 
      } 
      if (!bool) {
        int j = this.fDeferredDocumentImpl.createDeferredEntity(paramString, str1, str2, null, paramXMLResourceIdentifier.getBaseSystemId());
        this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, j);
      } 
    } 
  }
  
  public void startParameterEntity(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {
    if (paramAugmentations != null && this.fInternalSubset != null && !this.fInDTDExternalSubset && Boolean.TRUE.equals(paramAugmentations.getItem("ENTITY_SKIPPED")))
      this.fInternalSubset.append(paramString1).append(";\n"); 
    this.fBaseURIStack.push(paramXMLResourceIdentifier.getExpandedSystemId());
  }
  
  public void endParameterEntity(String paramString, Augmentations paramAugmentations) throws XNIException { this.fBaseURIStack.pop(); }
  
  public void unparsedEntityDecl(String paramString1, XMLResourceIdentifier paramXMLResourceIdentifier, String paramString2, Augmentations paramAugmentations) throws XNIException {
    String str1 = paramXMLResourceIdentifier.getPublicId();
    String str2 = paramXMLResourceIdentifier.getLiteralSystemId();
    if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
      this.fInternalSubset.append("<!ENTITY ");
      this.fInternalSubset.append(paramString1);
      this.fInternalSubset.append(' ');
      if (str1 != null) {
        this.fInternalSubset.append("PUBLIC '");
        this.fInternalSubset.append(str1);
        if (str2 != null) {
          this.fInternalSubset.append("' '");
          this.fInternalSubset.append(str2);
        } 
      } else {
        this.fInternalSubset.append("SYSTEM '");
        this.fInternalSubset.append(str2);
      } 
      this.fInternalSubset.append("' NDATA ");
      this.fInternalSubset.append(paramString2);
      this.fInternalSubset.append(">\n");
    } 
    if (this.fDocumentType != null) {
      NamedNodeMap namedNodeMap = this.fDocumentType.getEntities();
      EntityImpl entityImpl = (EntityImpl)namedNodeMap.getNamedItem(paramString1);
      if (entityImpl == null) {
        entityImpl = (EntityImpl)this.fDocumentImpl.createEntity(paramString1);
        entityImpl.setPublicId(str1);
        entityImpl.setSystemId(str2);
        entityImpl.setNotationName(paramString2);
        entityImpl.setBaseURI(paramXMLResourceIdentifier.getBaseSystemId());
        namedNodeMap.setNamedItem(entityImpl);
      } 
    } 
    if (this.fDocumentTypeIndex != -1) {
      boolean bool = false;
      int i;
      for (i = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); i != -1; i = this.fDeferredDocumentImpl.getRealPrevSibling(i, false)) {
        short s = this.fDeferredDocumentImpl.getNodeType(i, false);
        if (s == 6) {
          String str = this.fDeferredDocumentImpl.getNodeName(i, false);
          if (str.equals(paramString1)) {
            bool = true;
            break;
          } 
        } 
      } 
      if (!bool) {
        int j = this.fDeferredDocumentImpl.createDeferredEntity(paramString1, str1, str2, paramString2, paramXMLResourceIdentifier.getBaseSystemId());
        this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, j);
      } 
    } 
  }
  
  public void notationDecl(String paramString, XMLResourceIdentifier paramXMLResourceIdentifier, Augmentations paramAugmentations) throws XNIException {
    String str1 = paramXMLResourceIdentifier.getPublicId();
    String str2 = paramXMLResourceIdentifier.getLiteralSystemId();
    if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
      this.fInternalSubset.append("<!NOTATION ");
      this.fInternalSubset.append(paramString);
      if (str1 != null) {
        this.fInternalSubset.append(" PUBLIC '");
        this.fInternalSubset.append(str1);
        if (str2 != null) {
          this.fInternalSubset.append("' '");
          this.fInternalSubset.append(str2);
        } 
      } else {
        this.fInternalSubset.append(" SYSTEM '");
        this.fInternalSubset.append(str2);
      } 
      this.fInternalSubset.append("'>\n");
    } 
    if (this.fDocumentImpl != null && this.fDocumentType != null) {
      NamedNodeMap namedNodeMap = this.fDocumentType.getNotations();
      if (namedNodeMap.getNamedItem(paramString) == null) {
        NotationImpl notationImpl = (NotationImpl)this.fDocumentImpl.createNotation(paramString);
        notationImpl.setPublicId(str1);
        notationImpl.setSystemId(str2);
        notationImpl.setBaseURI(paramXMLResourceIdentifier.getBaseSystemId());
        namedNodeMap.setNamedItem(notationImpl);
      } 
    } 
    if (this.fDocumentTypeIndex != -1) {
      boolean bool = false;
      int i;
      for (i = this.fDeferredDocumentImpl.getLastChild(this.fDocumentTypeIndex, false); i != -1; i = this.fDeferredDocumentImpl.getPrevSibling(i, false)) {
        short s = this.fDeferredDocumentImpl.getNodeType(i, false);
        if (s == 12) {
          String str = this.fDeferredDocumentImpl.getNodeName(i, false);
          if (str.equals(paramString)) {
            bool = true;
            break;
          } 
        } 
      } 
      if (!bool) {
        int j = this.fDeferredDocumentImpl.createDeferredNotation(paramString, str1, str2, paramXMLResourceIdentifier.getBaseSystemId());
        this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, j);
      } 
    } 
  }
  
  public void ignoredCharacters(XMLString paramXMLString, Augmentations paramAugmentations) throws XNIException {}
  
  public void elementDecl(String paramString1, String paramString2, Augmentations paramAugmentations) throws XNIException {
    if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
      this.fInternalSubset.append("<!ELEMENT ");
      this.fInternalSubset.append(paramString1);
      this.fInternalSubset.append(' ');
      this.fInternalSubset.append(paramString2);
      this.fInternalSubset.append(">\n");
    } 
  }
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString, String paramString4, XMLString paramXMLString1, XMLString paramXMLString2, Augmentations paramAugmentations) throws XNIException {
    if (this.fInternalSubset != null && !this.fInDTDExternalSubset) {
      this.fInternalSubset.append("<!ATTLIST ");
      this.fInternalSubset.append(paramString1);
      this.fInternalSubset.append(' ');
      this.fInternalSubset.append(paramString2);
      this.fInternalSubset.append(' ');
      if (paramString3.equals("ENUMERATION")) {
        this.fInternalSubset.append('(');
        for (byte b = 0; b < paramArrayOfString.length; b++) {
          if (b)
            this.fInternalSubset.append('|'); 
          this.fInternalSubset.append(paramArrayOfString[b]);
        } 
        this.fInternalSubset.append(')');
      } else {
        this.fInternalSubset.append(paramString3);
      } 
      if (paramString4 != null) {
        this.fInternalSubset.append(' ');
        this.fInternalSubset.append(paramString4);
      } 
      if (paramXMLString1 != null) {
        this.fInternalSubset.append(" '");
        for (int i = 0; i < paramXMLString1.length; i++) {
          char c = paramXMLString1.ch[paramXMLString1.offset + i];
          if (c == '\'') {
            this.fInternalSubset.append("&apos;");
          } else {
            this.fInternalSubset.append(c);
          } 
        } 
        this.fInternalSubset.append('\'');
      } 
      this.fInternalSubset.append(">\n");
    } 
    if (this.fDeferredDocumentImpl != null) {
      if (paramXMLString1 != null) {
        int i = this.fDeferredDocumentImpl.lookupElementDefinition(paramString1);
        if (i == -1) {
          i = this.fDeferredDocumentImpl.createDeferredElementDefinition(paramString1);
          this.fDeferredDocumentImpl.appendChild(this.fDocumentTypeIndex, i);
        } 
        boolean bool = this.fNamespaceAware;
        String str = null;
        if (bool)
          if (paramString2.startsWith("xmlns:") || paramString2.equals("xmlns")) {
            str = NamespaceContext.XMLNS_URI;
          } else if (paramString2.startsWith("xml:")) {
            str = NamespaceContext.XML_URI;
          }  
        int j = this.fDeferredDocumentImpl.createDeferredAttribute(paramString2, str, paramXMLString1.toString(), false);
        if ("ID".equals(paramString3))
          this.fDeferredDocumentImpl.setIdAttribute(j); 
        this.fDeferredDocumentImpl.appendChild(i, j);
      } 
    } else if (this.fDocumentImpl != null && paramXMLString1 != null) {
      AttrImpl attrImpl;
      NamedNodeMap namedNodeMap = ((DocumentTypeImpl)this.fDocumentType).getElements();
      ElementDefinitionImpl elementDefinitionImpl = (ElementDefinitionImpl)namedNodeMap.getNamedItem(paramString1);
      if (elementDefinitionImpl == null) {
        elementDefinitionImpl = this.fDocumentImpl.createElementDefinition(paramString1);
        ((DocumentTypeImpl)this.fDocumentType).getElements().setNamedItem(elementDefinitionImpl);
      } 
      boolean bool = this.fNamespaceAware;
      if (bool) {
        String str = null;
        if (paramString2.startsWith("xmlns:") || paramString2.equals("xmlns")) {
          str = NamespaceContext.XMLNS_URI;
        } else if (paramString2.startsWith("xml:")) {
          str = NamespaceContext.XML_URI;
        } 
        attrImpl = (AttrImpl)this.fDocumentImpl.createAttributeNS(str, paramString2);
      } else {
        attrImpl = (AttrImpl)this.fDocumentImpl.createAttribute(paramString2);
      } 
      attrImpl.setValue(paramXMLString1.toString());
      attrImpl.setSpecified(false);
      attrImpl.setIdAttribute("ID".equals(paramString3));
      if (bool) {
        elementDefinitionImpl.getAttributes().setNamedItemNS(attrImpl);
      } else {
        elementDefinitionImpl.getAttributes().setNamedItem(attrImpl);
      } 
    } 
  }
  
  public void startAttlist(String paramString, Augmentations paramAugmentations) throws XNIException {}
  
  public void endAttlist(Augmentations paramAugmentations) throws XNIException {}
  
  protected Element createElementNode(QName paramQName) {
    Element element = null;
    if (this.fNamespaceAware) {
      if (this.fDocumentImpl != null) {
        element = this.fDocumentImpl.createElementNS(paramQName.uri, paramQName.rawname, paramQName.localpart);
      } else {
        element = this.fDocument.createElementNS(paramQName.uri, paramQName.rawname);
      } 
    } else {
      element = this.fDocument.createElement(paramQName.rawname);
    } 
    return element;
  }
  
  protected Attr createAttrNode(QName paramQName) {
    Attr attr = null;
    if (this.fNamespaceAware) {
      if (this.fDocumentImpl != null) {
        attr = this.fDocumentImpl.createAttributeNS(paramQName.uri, paramQName.rawname, paramQName.localpart);
      } else {
        attr = this.fDocument.createAttributeNS(paramQName.uri, paramQName.rawname);
      } 
    } else {
      attr = this.fDocument.createAttribute(paramQName.rawname);
    } 
    return attr;
  }
  
  protected void setCharacterData(boolean paramBoolean) {
    this.fFirstChunk = paramBoolean;
    Node node = this.fCurrentNode.getLastChild();
    if (node != null) {
      if (this.fStringBuilder.length() > 0) {
        if (node.getNodeType() == 3)
          if (this.fDocumentImpl != null) {
            ((TextImpl)node).replaceData(this.fStringBuilder.toString());
          } else {
            ((Text)node).setData(this.fStringBuilder.toString());
          }  
        this.fStringBuilder.setLength(0);
      } 
      if (this.fDOMFilter != null && !this.fInEntityRef && node.getNodeType() == 3 && (this.fDOMFilter.getWhatToShow() & 0x4) != 0) {
        short s = this.fDOMFilter.acceptNode(node);
        switch (s) {
          case 4:
            throw Abort.INSTANCE;
          case 2:
          case 3:
            this.fCurrentNode.removeChild(node);
            return;
        } 
      } 
    } 
  }
  
  public void abort() { throw Abort.INSTANCE; }
  
  static final class Abort extends RuntimeException {
    private static final long serialVersionUID = 1687848994976808490L;
    
    static final Abort INSTANCE = new Abort();
    
    public Throwable fillInStackTrace() { return this; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\parsers\AbstractDOMParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */