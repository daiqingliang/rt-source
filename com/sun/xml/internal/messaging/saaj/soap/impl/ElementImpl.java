package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.util.NamespaceContextIterator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class ElementImpl extends ElementNSImpl implements SOAPElement, SOAPBodyElement {
  public static final String DSIG_NS = "http://www.w3.org/2000/09/xmldsig#".intern();
  
  public static final String XENC_NS = "http://www.w3.org/2001/04/xmlenc#".intern();
  
  public static final String WSU_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd".intern();
  
  private AttributeManager encodingStyleAttribute = new AttributeManager();
  
  protected QName elementQName;
  
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.impl", "com.sun.xml.internal.messaging.saaj.soap.impl.LocalStrings");
  
  public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/".intern();
  
  public static final String XML_URI = "http://www.w3.org/XML/1998/namespace".intern();
  
  public ElementImpl(SOAPDocumentImpl paramSOAPDocumentImpl, Name paramName) {
    super(paramSOAPDocumentImpl, paramName.getURI(), paramName.getQualifiedName(), paramName.getLocalName());
    this.elementQName = NameImpl.convertToQName(paramName);
  }
  
  public ElementImpl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName) {
    super(paramSOAPDocumentImpl, paramQName.getNamespaceURI(), getQualifiedName(paramQName), paramQName.getLocalPart());
    this.elementQName = paramQName;
  }
  
  public ElementImpl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString1, String paramString2) {
    super(paramSOAPDocumentImpl, paramString1, paramString2);
    this.elementQName = new QName(paramString1, getLocalPart(paramString2), getPrefix(paramString2));
  }
  
  public void ensureNamespaceIsDeclared(String paramString1, String paramString2) {
    String str = getNamespaceURI(paramString1);
    if (str == null || !str.equals(paramString2))
      try {
        addNamespaceDeclaration(paramString1, paramString2);
      } catch (SOAPException sOAPException) {} 
  }
  
  public Document getOwnerDocument() {
    Document document = super.getOwnerDocument();
    return (document instanceof SOAPDocument) ? ((SOAPDocument)document).getDocument() : document;
  }
  
  public SOAPElement addChildElement(Name paramName) throws SOAPException { return addElement(paramName); }
  
  public SOAPElement addChildElement(QName paramQName) throws SOAPException { return addElement(paramQName); }
  
  public SOAPElement addChildElement(String paramString) throws SOAPException {
    String str = getNamespaceURI("");
    NameImpl nameImpl = (str == null || str.isEmpty()) ? NameImpl.createFromUnqualifiedName(paramString) : NameImpl.createFromQualifiedName(paramString, str);
    return addChildElement(nameImpl);
  }
  
  public SOAPElement addChildElement(String paramString1, String paramString2) throws SOAPException {
    String str = getNamespaceURI(paramString2);
    if (str == null) {
      log.log(Level.SEVERE, "SAAJ0101.impl.parent.of.body.elem.mustbe.body", new String[] { paramString2 });
      throw new SOAPExceptionImpl("Unable to locate namespace for prefix " + paramString2);
    } 
    return addChildElement(paramString1, paramString2, str);
  }
  
  public String getNamespaceURI(String paramString) {
    if ("xmlns".equals(paramString))
      return XMLNS_URI; 
    if ("xml".equals(paramString))
      return XML_URI; 
    if ("".equals(paramString)) {
      ElementImpl elementImpl = this;
      while (elementImpl != null && !(elementImpl instanceof Document)) {
        if (elementImpl instanceof ElementImpl) {
          QName qName = ((ElementImpl)elementImpl).getElementQName();
          if (((Element)elementImpl).hasAttributeNS(XMLNS_URI, "xmlns")) {
            String str = ((Element)elementImpl).getAttributeNS(XMLNS_URI, "xmlns");
            return "".equals(str) ? null : str;
          } 
        } 
        Node node = elementImpl.getParentNode();
      } 
    } else if (paramString != null) {
      ElementImpl elementImpl = this;
      while (elementImpl != null && !(elementImpl instanceof Document)) {
        if (((Element)elementImpl).hasAttributeNS(XMLNS_URI, paramString))
          return ((Element)elementImpl).getAttributeNS(XMLNS_URI, paramString); 
        Node node = elementImpl.getParentNode();
      } 
    } 
    return null;
  }
  
  public SOAPElement setElementQName(QName paramQName) throws SOAPException {
    ElementImpl elementImpl;
    return (elementImpl = new ElementImpl((SOAPDocumentImpl)getOwnerDocument(), paramQName)).replaceElementWithSOAPElement(this, elementImpl);
  }
  
  public QName createQName(String paramString1, String paramString2) throws SOAPException {
    String str = getNamespaceURI(paramString2);
    if (str == null) {
      log.log(Level.SEVERE, "SAAJ0102.impl.cannot.locate.ns", new Object[] { paramString2 });
      throw new SOAPException("Unable to locate namespace for prefix " + paramString2);
    } 
    return new QName(str, paramString1, paramString2);
  }
  
  public String getNamespacePrefix(String paramString) {
    NamespaceContextIterator namespaceContextIterator = getNamespaceContextNodes();
    while (namespaceContextIterator.hasNext()) {
      Attr attr = namespaceContextIterator.nextNamespaceAttr();
      if (attr.getNodeValue().equals(paramString)) {
        String str = attr.getLocalName();
        return "xmlns".equals(str) ? "" : str;
      } 
    } 
    ElementImpl elementImpl = this;
    while (elementImpl != null && !(elementImpl instanceof Document)) {
      if (paramString.equals(elementImpl.getNamespaceURI()))
        return elementImpl.getPrefix(); 
      Node node = elementImpl.getParentNode();
    } 
    return null;
  }
  
  protected Attr getNamespaceAttr(String paramString) {
    NamespaceContextIterator namespaceContextIterator = getNamespaceContextNodes();
    if (!"".equals(paramString))
      paramString = ":" + paramString; 
    while (namespaceContextIterator.hasNext()) {
      Attr attr = namespaceContextIterator.nextNamespaceAttr();
      if (!"".equals(paramString)) {
        if (attr.getNodeName().endsWith(paramString))
          return attr; 
        continue;
      } 
      if (attr.getNodeName().equals("xmlns"))
        return attr; 
    } 
    return null;
  }
  
  public NamespaceContextIterator getNamespaceContextNodes() { return getNamespaceContextNodes(true); }
  
  public NamespaceContextIterator getNamespaceContextNodes(boolean paramBoolean) { return new NamespaceContextIterator(this, paramBoolean); }
  
  public SOAPElement addChildElement(String paramString1, String paramString2, String paramString3) throws SOAPException {
    SOAPElement sOAPElement = createElement(NameImpl.create(paramString1, paramString2, paramString3));
    addNode(sOAPElement);
    return convertToSoapElement(sOAPElement);
  }
  
  public SOAPElement addChildElement(SOAPElement paramSOAPElement) throws SOAPException {
    String str1 = paramSOAPElement.getElementName().getURI();
    String str2 = paramSOAPElement.getLocalName();
    if ("http://schemas.xmlsoap.org/soap/envelope/".equals(str1) || "http://www.w3.org/2003/05/soap-envelope".equals(str1)) {
      if ("Envelope".equalsIgnoreCase(str2) || "Header".equalsIgnoreCase(str2) || "Body".equalsIgnoreCase(str2)) {
        log.severe("SAAJ0103.impl.cannot.add.fragements");
        throw new SOAPExceptionImpl("Cannot add fragments which contain elements which are in the SOAP namespace");
      } 
      if ("Fault".equalsIgnoreCase(str2) && !"Body".equalsIgnoreCase(getLocalName())) {
        log.severe("SAAJ0154.impl.adding.fault.to.nonbody");
        throw new SOAPExceptionImpl("Cannot add a SOAPFault as a child of " + getLocalName());
      } 
      if ("Detail".equalsIgnoreCase(str2) && !"Fault".equalsIgnoreCase(getLocalName())) {
        log.severe("SAAJ0155.impl.adding.detail.nonfault");
        throw new SOAPExceptionImpl("Cannot add a Detail as a child of " + getLocalName());
      } 
      if ("Fault".equalsIgnoreCase(str2)) {
        if (!str1.equals(getElementName().getURI())) {
          log.severe("SAAJ0158.impl.version.mismatch.fault");
          throw new SOAPExceptionImpl("SOAP Version mismatch encountered when trying to add SOAPFault to SOAPBody");
        } 
        Iterator iterator = getChildElements();
        if (iterator.hasNext()) {
          log.severe("SAAJ0156.impl.adding.fault.error");
          throw new SOAPExceptionImpl("Cannot add SOAPFault as a child of a non-Empty SOAPBody");
        } 
      } 
    } 
    String str3 = paramSOAPElement.getEncodingStyle();
    ElementImpl elementImpl = (ElementImpl)importElement(paramSOAPElement);
    addNode(elementImpl);
    if (str3 != null)
      elementImpl.setEncodingStyle(str3); 
    return convertToSoapElement(elementImpl);
  }
  
  protected Element importElement(Element paramElement) {
    Document document1 = getOwnerDocument();
    Document document2 = paramElement.getOwnerDocument();
    return !document2.equals(document1) ? (Element)document1.importNode(paramElement, true) : paramElement;
  }
  
  protected SOAPElement addElement(Name paramName) throws SOAPException {
    SOAPElement sOAPElement = createElement(paramName);
    addNode(sOAPElement);
    return sOAPElement;
  }
  
  protected SOAPElement addElement(QName paramQName) throws SOAPException {
    SOAPElement sOAPElement = createElement(paramQName);
    addNode(sOAPElement);
    return sOAPElement;
  }
  
  protected SOAPElement createElement(Name paramName) throws SOAPException { return isNamespaceQualified(paramName) ? (SOAPElement)getOwnerDocument().createElementNS(paramName.getURI(), paramName.getQualifiedName()) : (SOAPElement)getOwnerDocument().createElement(paramName.getQualifiedName()); }
  
  protected SOAPElement createElement(QName paramQName) throws SOAPException { return isNamespaceQualified(paramQName) ? (SOAPElement)getOwnerDocument().createElementNS(paramQName.getNamespaceURI(), getQualifiedName(paramQName)) : (SOAPElement)getOwnerDocument().createElement(getQualifiedName(paramQName)); }
  
  protected void addNode(Node paramNode) throws SOAPException {
    insertBefore(paramNode, null);
    if (getOwnerDocument() instanceof org.w3c.dom.DocumentFragment)
      return; 
    if (paramNode instanceof ElementImpl) {
      ElementImpl elementImpl = (ElementImpl)paramNode;
      QName qName = elementImpl.getElementQName();
      if (!"".equals(qName.getNamespaceURI()))
        elementImpl.ensureNamespaceIsDeclared(qName.getPrefix(), qName.getNamespaceURI()); 
    } 
  }
  
  protected SOAPElement findChild(NameImpl paramNameImpl) {
    Iterator iterator = getChildElementNodes();
    while (iterator.hasNext()) {
      SOAPElement sOAPElement = (SOAPElement)iterator.next();
      if (sOAPElement.getElementName().equals(paramNameImpl))
        return sOAPElement; 
    } 
    return null;
  }
  
  public SOAPElement addTextNode(String paramString) throws SOAPException { return (paramString.startsWith("<![CDATA[") || paramString.startsWith("<![cdata[")) ? addCDATA(paramString.substring("<![CDATA[".length(), paramString.length() - 3)) : addText(paramString); }
  
  protected SOAPElement addCDATA(String paramString) throws SOAPException {
    CDATASection cDATASection = getOwnerDocument().createCDATASection(paramString);
    addNode(cDATASection);
    return this;
  }
  
  protected SOAPElement addText(String paramString) throws SOAPException {
    Text text = getOwnerDocument().createTextNode(paramString);
    addNode(text);
    return this;
  }
  
  public SOAPElement addAttribute(Name paramName, String paramString) throws SOAPException {
    addAttributeBare(paramName, paramString);
    if (!"".equals(paramName.getURI()))
      ensureNamespaceIsDeclared(paramName.getPrefix(), paramName.getURI()); 
    return this;
  }
  
  public SOAPElement addAttribute(QName paramQName, String paramString) throws SOAPException {
    addAttributeBare(paramQName, paramString);
    if (!"".equals(paramQName.getNamespaceURI()))
      ensureNamespaceIsDeclared(paramQName.getPrefix(), paramQName.getNamespaceURI()); 
    return this;
  }
  
  private void addAttributeBare(Name paramName, String paramString) { addAttributeBare(paramName.getURI(), paramName.getPrefix(), paramName.getQualifiedName(), paramString); }
  
  private void addAttributeBare(QName paramQName, String paramString) { addAttributeBare(paramQName.getNamespaceURI(), paramQName.getPrefix(), getQualifiedName(paramQName), paramString); }
  
  private void addAttributeBare(String paramString1, String paramString2, String paramString3, String paramString4) {
    paramString1 = (paramString1.length() == 0) ? null : paramString1;
    if (paramString3.equals("xmlns"))
      paramString1 = XMLNS_URI; 
    if (paramString1 == null) {
      setAttribute(paramString3, paramString4);
    } else {
      setAttributeNS(paramString1, paramString3, paramString4);
    } 
  }
  
  public SOAPElement addNamespaceDeclaration(String paramString1, String paramString2) throws SOAPException {
    if (paramString1.length() > 0) {
      setAttributeNS(XMLNS_URI, "xmlns:" + paramString1, paramString2);
    } else {
      setAttributeNS(XMLNS_URI, "xmlns", paramString2);
    } 
    return this;
  }
  
  public String getAttributeValue(Name paramName) { return getAttributeValueFrom(this, paramName); }
  
  public String getAttributeValue(QName paramQName) { return getAttributeValueFrom(this, paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramQName.getPrefix(), getQualifiedName(paramQName)); }
  
  public Iterator getAllAttributes() {
    Iterator iterator = getAllAttributesFrom(this);
    ArrayList arrayList = new ArrayList();
    while (iterator.hasNext()) {
      Name name = (Name)iterator.next();
      if (!"xmlns".equalsIgnoreCase(name.getPrefix()))
        arrayList.add(name); 
    } 
    return arrayList.iterator();
  }
  
  public Iterator getAllAttributesAsQNames() {
    Iterator iterator = getAllAttributesFrom(this);
    ArrayList arrayList = new ArrayList();
    while (iterator.hasNext()) {
      Name name = (Name)iterator.next();
      if (!"xmlns".equalsIgnoreCase(name.getPrefix()))
        arrayList.add(NameImpl.convertToQName(name)); 
    } 
    return arrayList.iterator();
  }
  
  public Iterator getNamespacePrefixes() { return doGetNamespacePrefixes(false); }
  
  public Iterator getVisibleNamespacePrefixes() { return doGetNamespacePrefixes(true); }
  
  protected Iterator doGetNamespacePrefixes(final boolean deep) { return new Iterator() {
        String next = null;
        
        String last = null;
        
        NamespaceContextIterator eachNamespace = ElementImpl.this.getNamespaceContextNodes(deep);
        
        void findNext() {
          while (this.next == null && this.eachNamespace.hasNext()) {
            String str = this.eachNamespace.nextNamespaceAttr().getNodeName();
            if (str.startsWith("xmlns:"))
              this.next = str.substring("xmlns:".length()); 
          } 
        }
        
        public boolean hasNext() {
          findNext();
          return (this.next != null);
        }
        
        public Object next() {
          findNext();
          if (this.next == null)
            throw new NoSuchElementException(); 
          this.last = this.next;
          this.next = null;
          return this.last;
        }
        
        public void remove() {
          if (this.last == null)
            throw new IllegalStateException(); 
          this.eachNamespace.remove();
          this.next = null;
          this.last = null;
        }
      }; }
  
  public Name getElementName() { return NameImpl.convertToName(this.elementQName); }
  
  public QName getElementQName() { return this.elementQName; }
  
  public boolean removeAttribute(Name paramName) { return removeAttribute(paramName.getURI(), paramName.getLocalName()); }
  
  public boolean removeAttribute(QName paramQName) { return removeAttribute(paramQName.getNamespaceURI(), paramQName.getLocalPart()); }
  
  private boolean removeAttribute(String paramString1, String paramString2) {
    String str = (paramString1 == null || paramString1.length() == 0) ? null : paramString1;
    Attr attr = getAttributeNodeNS(str, paramString2);
    if (attr == null)
      return false; 
    removeAttributeNode(attr);
    return true;
  }
  
  public boolean removeNamespaceDeclaration(String paramString) {
    Attr attr = getNamespaceAttr(paramString);
    if (attr == null)
      return false; 
    try {
      removeAttributeNode(attr);
    } catch (DOMException dOMException) {}
    return true;
  }
  
  public Iterator getChildElements() { return getChildElementsFrom(this); }
  
  protected SOAPElement convertToSoapElement(Element paramElement) { return (paramElement instanceof SOAPElement) ? (SOAPElement)paramElement : replaceElementWithSOAPElement(paramElement, (ElementImpl)createElement(NameImpl.copyElementName(paramElement))); }
  
  protected static SOAPElement replaceElementWithSOAPElement(Element paramElement, ElementImpl paramElementImpl) {
    Iterator iterator1 = getAllAttributesFrom(paramElement);
    while (iterator1.hasNext()) {
      Name name = (Name)iterator1.next();
      paramElementImpl.addAttributeBare(name, getAttributeValueFrom(paramElement, name));
    } 
    Iterator iterator2 = getChildElementsFrom(paramElement);
    while (iterator2.hasNext()) {
      Node node1 = (Node)iterator2.next();
      paramElementImpl.insertBefore(node1, null);
    } 
    Node node = paramElement.getParentNode();
    if (node != null)
      node.replaceChild(paramElementImpl, paramElement); 
    return paramElementImpl;
  }
  
  protected Iterator getChildElementNodes() { return new Iterator() {
        Iterator eachNode = ElementImpl.this.getChildElements();
        
        Node next = null;
        
        Node last = null;
        
        public boolean hasNext() {
          if (this.next == null)
            while (this.eachNode.hasNext()) {
              Node node = (Node)this.eachNode.next();
              if (node instanceof SOAPElement) {
                this.next = node;
                break;
              } 
            }  
          return (this.next != null);
        }
        
        public Object next() {
          if (hasNext()) {
            this.last = this.next;
            this.next = null;
            return this.last;
          } 
          throw new NoSuchElementException();
        }
        
        public void remove() {
          if (this.last == null)
            throw new IllegalStateException(); 
          Node node = this.last;
          this.last = null;
          ElementImpl.this.removeChild(node);
        }
      }; }
  
  public Iterator getChildElements(Name paramName) { return getChildElements(paramName.getURI(), paramName.getLocalName()); }
  
  public Iterator getChildElements(QName paramQName) { return getChildElements(paramQName.getNamespaceURI(), paramQName.getLocalPart()); }
  
  private Iterator getChildElements(final String nameUri, final String nameLocal) { return new Iterator() {
        Iterator eachElement = ElementImpl.this.getChildElementNodes();
        
        Node next = null;
        
        Node last = null;
        
        public boolean hasNext() {
          if (this.next == null)
            while (this.eachElement.hasNext()) {
              Node node = (Node)this.eachElement.next();
              String str1 = node.getNamespaceURI();
              str1 = (str1 == null) ? "" : str1;
              String str2 = node.getLocalName();
              if (str1.equals(nameUri) && str2.equals(nameLocal)) {
                this.next = node;
                break;
              } 
            }  
          return (this.next != null);
        }
        
        public Object next() {
          if (!hasNext())
            throw new NoSuchElementException(); 
          this.last = this.next;
          this.next = null;
          return this.last;
        }
        
        public void remove() {
          if (this.last == null)
            throw new IllegalStateException(); 
          Node node = this.last;
          this.last = null;
          ElementImpl.this.removeChild(node);
        }
      }; }
  
  public void removeContents() {
    for (Node node = getFirstChild(); node != null; node = node1) {
      Node node1 = node.getNextSibling();
      if (node instanceof Node) {
        ((Node)node).detachNode();
      } else {
        Node node2 = node.getParentNode();
        if (node2 != null)
          node2.removeChild(node); 
      } 
    } 
  }
  
  public void setEncodingStyle(String paramString) throws SOAPException {
    if (!"".equals(paramString))
      try {
        new URI(paramString);
      } catch (URISyntaxException uRISyntaxException) {
        log.log(Level.SEVERE, "SAAJ0105.impl.encoding.style.mustbe.valid.URI", new String[] { paramString });
        throw new IllegalArgumentException("Encoding style (" + paramString + ") should be a valid URI");
      }  
    this.encodingStyleAttribute.setValue(paramString);
    tryToFindEncodingStyleAttributeName();
  }
  
  public String getEncodingStyle() {
    String str1 = this.encodingStyleAttribute.getValue();
    if (str1 != null)
      return str1; 
    String str2 = getSOAPNamespace();
    if (str2 != null) {
      Attr attr = getAttributeNodeNS(str2, "encodingStyle");
      if (attr != null) {
        str1 = attr.getValue();
        try {
          setEncodingStyle(str1);
        } catch (SOAPException sOAPException) {}
        return str1;
      } 
    } 
    return null;
  }
  
  public String getValue() {
    Node node = getValueNode();
    return (node == null) ? null : node.getValue();
  }
  
  public void setValue(String paramString) throws SOAPException {
    Node node = getValueNodeStrict();
    if (node != null) {
      node.setNodeValue(paramString);
    } else {
      try {
        addTextNode(paramString);
      } catch (SOAPException sOAPException) {
        throw new RuntimeException(sOAPException.getMessage());
      } 
    } 
  }
  
  protected Node getValueNodeStrict() {
    Node node = getFirstChild();
    if (node != null) {
      if (node.getNextSibling() == null && node.getNodeType() == 3)
        return node; 
      log.severe("SAAJ0107.impl.elem.child.not.single.text");
      throw new IllegalStateException();
    } 
    return null;
  }
  
  protected Node getValueNode() {
    Iterator iterator = getChildElements();
    while (iterator.hasNext()) {
      Node node = (Node)iterator.next();
      if (node.getNodeType() == 3 || node.getNodeType() == 4) {
        normalize();
        return node;
      } 
    } 
    return null;
  }
  
  public void setParentElement(SOAPElement paramSOAPElement) throws SOAPException {
    if (paramSOAPElement == null) {
      log.severe("SAAJ0106.impl.no.null.to.parent.elem");
      throw new SOAPException("Cannot pass NULL to setParentElement");
    } 
    paramSOAPElement.addChildElement(this);
    findEncodingStyleAttributeName();
  }
  
  protected void findEncodingStyleAttributeName() {
    String str = getSOAPNamespace();
    if (str != null) {
      String str1 = getNamespacePrefix(str);
      if (str1 != null)
        setEncodingStyleNamespace(str, str1); 
    } 
  }
  
  protected void setEncodingStyleNamespace(String paramString1, String paramString2) {
    NameImpl nameImpl = NameImpl.create("encodingStyle", paramString2, paramString1);
    this.encodingStyleAttribute.setName(nameImpl);
  }
  
  public SOAPElement getParentElement() {
    Node node = getParentNode();
    return (node instanceof SOAPDocument) ? null : (SOAPElement)node;
  }
  
  protected String getSOAPNamespace() {
    String str = null;
    ElementImpl elementImpl = this;
    while (elementImpl != null) {
      Name name = elementImpl.getElementName();
      String str1 = name.getURI();
      if ("http://schemas.xmlsoap.org/soap/envelope/".equals(str1) || "http://www.w3.org/2003/05/soap-envelope".equals(str1)) {
        str = str1;
        break;
      } 
      SOAPElement sOAPElement = elementImpl.getParentElement();
    } 
    return str;
  }
  
  public void detachNode() {
    Node node = getParentNode();
    if (node != null)
      node.removeChild(this); 
    this.encodingStyleAttribute.clearNameAndValue();
  }
  
  public void tryToFindEncodingStyleAttributeName() {
    try {
      findEncodingStyleAttributeName();
    } catch (SOAPException sOAPException) {}
  }
  
  public void recycleNode() { detachNode(); }
  
  protected static Attr getNamespaceAttrFrom(Element paramElement, String paramString) {
    NamespaceContextIterator namespaceContextIterator = new NamespaceContextIterator(paramElement);
    while (namespaceContextIterator.hasNext()) {
      Attr attr = namespaceContextIterator.nextNamespaceAttr();
      String str = NameImpl.getLocalNameFromTagName(attr.getNodeName());
      if (str.equals(paramString))
        return attr; 
    } 
    return null;
  }
  
  protected static Iterator getAllAttributesFrom(Element paramElement) {
    final NamedNodeMap attributes = paramElement.getAttributes();
    return new Iterator() {
        int attributesLength = attributes.getLength();
        
        int attributeIndex = 0;
        
        String currentName;
        
        public boolean hasNext() { return (this.attributeIndex < this.attributesLength); }
        
        public Object next() {
          if (!hasNext())
            throw new NoSuchElementException(); 
          Node node = attributes.item(this.attributeIndex++);
          this.currentName = node.getNodeName();
          String str = NameImpl.getPrefixFromTagName(this.currentName);
          return (str.length() == 0) ? NameImpl.createFromUnqualifiedName(this.currentName) : NameImpl.createFromQualifiedName(this.currentName, node.getNamespaceURI());
        }
        
        public void remove() {
          if (this.currentName == null)
            throw new IllegalStateException(); 
          attributes.removeNamedItem(this.currentName);
        }
      };
  }
  
  protected static String getAttributeValueFrom(Element paramElement, Name paramName) { return getAttributeValueFrom(paramElement, paramName.getURI(), paramName.getLocalName(), paramName.getPrefix(), paramName.getQualifiedName()); }
  
  private static String getAttributeValueFrom(Element paramElement, String paramString1, String paramString2, String paramString3, String paramString4) {
    String str = (paramString1 == null || paramString1.length() == 0) ? null : paramString1;
    boolean bool = (str != null) ? 1 : 0;
    if (bool)
      return !paramElement.hasAttributeNS(paramString1, paramString2) ? null : paramElement.getAttributeNS(str, paramString2); 
    Attr attr = null;
    attr = paramElement.getAttributeNode(paramString4);
    return (attr == null) ? null : attr.getValue();
  }
  
  protected static Iterator getChildElementsFrom(final Element element) { return new Iterator() {
        Node next = element.getFirstChild();
        
        Node nextNext = null;
        
        Node last = null;
        
        public boolean hasNext() {
          if (this.next != null)
            return true; 
          if (this.next == null && this.nextNext != null)
            this.next = this.nextNext; 
          return (this.next != null);
        }
        
        public Object next() {
          if (hasNext()) {
            this.last = this.next;
            this.next = null;
            if (element instanceof ElementImpl && this.last instanceof Element)
              this.last = ((ElementImpl)element).convertToSoapElement((Element)this.last); 
            this.nextNext = this.last.getNextSibling();
            return this.last;
          } 
          throw new NoSuchElementException();
        }
        
        public void remove() {
          if (this.last == null)
            throw new IllegalStateException(); 
          Node node = this.last;
          this.last = null;
          element.removeChild(node);
        }
      }; }
  
  public static String getQualifiedName(QName paramQName) {
    String str1 = paramQName.getPrefix();
    String str2 = paramQName.getLocalPart();
    String str3 = null;
    if (str1 != null && str1.length() > 0) {
      str3 = str1 + ":" + str2;
    } else {
      str3 = str2;
    } 
    return str3;
  }
  
  public static String getLocalPart(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("Cannot get local name for a \"null\" qualified name"); 
    int i = paramString.indexOf(':');
    return (i < 0) ? paramString : paramString.substring(i + 1);
  }
  
  public static String getPrefix(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("Cannot get prefix for a  \"null\" qualified name"); 
    int i = paramString.indexOf(':');
    return (i < 0) ? "" : paramString.substring(0, i);
  }
  
  protected boolean isNamespaceQualified(Name paramName) { return !"".equals(paramName.getURI()); }
  
  protected boolean isNamespaceQualified(QName paramQName) { return !"".equals(paramQName.getNamespaceURI()); }
  
  public void setAttributeNS(String paramString1, String paramString2, String paramString3) {
    String str1;
    int i = paramString2.indexOf(':');
    if (i < 0) {
      str1 = paramString2;
    } else {
      str1 = paramString2.substring(i + 1);
    } 
    super.setAttributeNS(paramString1, paramString2, paramString3);
    String str2 = getNamespaceURI();
    boolean bool = false;
    if (str2 != null && (str2.equals(DSIG_NS) || str2.equals(XENC_NS)))
      bool = true; 
    if (str1.equals("Id"))
      if (paramString1 == null || paramString1.equals("")) {
        setIdAttribute(str1, true);
      } else if (bool || WSU_NS.equals(paramString1)) {
        setIdAttributeNS(paramString1, str1, true);
      }  
  }
  
  class AttributeManager {
    Name attributeName = null;
    
    String attributeValue = null;
    
    public void setName(Name param1Name) throws SOAPException {
      clearAttribute();
      this.attributeName = param1Name;
      reconcileAttribute();
    }
    
    public void clearName() {
      clearAttribute();
      this.attributeName = null;
    }
    
    public void setValue(String param1String) throws SOAPException {
      this.attributeValue = param1String;
      reconcileAttribute();
    }
    
    public Name getName() { return this.attributeName; }
    
    public String getValue() { return this.attributeValue; }
    
    public void clearNameAndValue() {
      this.attributeName = null;
      this.attributeValue = null;
    }
    
    private void reconcileAttribute() {
      if (this.attributeName != null) {
        ElementImpl.this.removeAttribute(this.attributeName);
        if (this.attributeValue != null)
          ElementImpl.this.addAttribute(this.attributeName, this.attributeValue); 
      } 
    }
    
    private void clearAttribute() {
      if (this.attributeName != null)
        ElementImpl.this.removeAttribute(this.attributeName); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\impl\ElementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */