package com.sun.xml.internal.bind.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx;
import java.util.Enumeration;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;

public class DOMScanner implements LocatorEx, InfosetScanner {
  private Node currentNode = null;
  
  private final AttributesImpl atts = new AttributesImpl();
  
  private ContentHandler receiver = null;
  
  private Locator locator = this;
  
  public void setLocator(Locator paramLocator) { this.locator = paramLocator; }
  
  public void scan(Object paramObject) throws SAXException {
    if (paramObject instanceof Document) {
      scan((Document)paramObject);
    } else {
      scan((Element)paramObject);
    } 
  }
  
  public void scan(Document paramDocument) throws SAXException { scan(paramDocument.getDocumentElement()); }
  
  public void scan(Element paramElement) throws SAXException {
    setCurrentLocation(paramElement);
    this.receiver.setDocumentLocator(this.locator);
    this.receiver.startDocument();
    NamespaceSupport namespaceSupport = new NamespaceSupport();
    buildNamespaceSupport(namespaceSupport, paramElement.getParentNode());
    Enumeration enumeration = namespaceSupport.getPrefixes();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      this.receiver.startPrefixMapping(str, namespaceSupport.getURI(str));
    } 
    visit(paramElement);
    enumeration = namespaceSupport.getPrefixes();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      this.receiver.endPrefixMapping(str);
    } 
    setCurrentLocation(paramElement);
    this.receiver.endDocument();
  }
  
  public void parse(Element paramElement, ContentHandler paramContentHandler) throws SAXException {
    this.receiver = paramContentHandler;
    setCurrentLocation(paramElement);
    this.receiver.startDocument();
    this.receiver.setDocumentLocator(this.locator);
    visit(paramElement);
    setCurrentLocation(paramElement);
    this.receiver.endDocument();
  }
  
  public void parseWithContext(Element paramElement, ContentHandler paramContentHandler) throws SAXException {
    setContentHandler(paramContentHandler);
    scan(paramElement);
  }
  
  private void buildNamespaceSupport(NamespaceSupport paramNamespaceSupport, Node paramNode) {
    if (paramNode == null || paramNode.getNodeType() != 1)
      return; 
    buildNamespaceSupport(paramNamespaceSupport, paramNode.getParentNode());
    paramNamespaceSupport.pushContext();
    NamedNodeMap namedNodeMap = paramNode.getAttributes();
    for (byte b = 0; b < namedNodeMap.getLength(); b++) {
      Attr attr = (Attr)namedNodeMap.item(b);
      if ("xmlns".equals(attr.getPrefix())) {
        paramNamespaceSupport.declarePrefix(attr.getLocalName(), attr.getValue());
      } else if ("xmlns".equals(attr.getName())) {
        paramNamespaceSupport.declarePrefix("", attr.getValue());
      } 
    } 
  }
  
  public void visit(Element paramElement) throws SAXException {
    setCurrentLocation(paramElement);
    NamedNodeMap namedNodeMap = paramElement.getAttributes();
    this.atts.clear();
    boolean bool = (namedNodeMap == null) ? 0 : namedNodeMap.getLength();
    for (byte b1 = bool - true; b1; b1--) {
      Attr attr = (Attr)namedNodeMap.item(b1);
      String str = attr.getName();
      if (str.startsWith("xmlns")) {
        if (str.length() == 5) {
          this.receiver.startPrefixMapping("", attr.getValue());
        } else {
          String str4 = attr.getLocalName();
          if (str4 == null)
            str4 = str.substring(6); 
          this.receiver.startPrefixMapping(str4, attr.getValue());
        } 
      } else {
        String str4 = attr.getNamespaceURI();
        if (str4 == null)
          str4 = ""; 
        String str5 = attr.getLocalName();
        if (str5 == null)
          str5 = attr.getName(); 
        this.atts.addAttribute(str4, str5, attr.getName(), "CDATA", attr.getValue());
      } 
    } 
    String str1 = paramElement.getNamespaceURI();
    if (str1 == null)
      str1 = ""; 
    String str2 = paramElement.getLocalName();
    String str3 = paramElement.getTagName();
    if (str2 == null)
      str2 = str3; 
    this.receiver.startElement(str1, str2, str3, this.atts);
    NodeList nodeList = paramElement.getChildNodes();
    int i = nodeList.getLength();
    byte b2;
    for (b2 = 0; b2 < i; b2++)
      visit(nodeList.item(b2)); 
    setCurrentLocation(paramElement);
    this.receiver.endElement(str1, str2, str3);
    for (b2 = bool - true; b2 >= 0; b2--) {
      Attr attr = (Attr)namedNodeMap.item(b2);
      String str = attr.getName();
      if (str.startsWith("xmlns"))
        if (str.length() == 5) {
          this.receiver.endPrefixMapping("");
        } else {
          this.receiver.endPrefixMapping(attr.getLocalName());
        }  
    } 
  }
  
  private void visit(Node paramNode) throws SAXException {
    ProcessingInstruction processingInstruction;
    String str;
    setCurrentLocation(paramNode);
    switch (paramNode.getNodeType()) {
      case 3:
      case 4:
        str = paramNode.getNodeValue();
        this.receiver.characters(str.toCharArray(), 0, str.length());
        break;
      case 1:
        visit((Element)paramNode);
        break;
      case 5:
        this.receiver.skippedEntity(paramNode.getNodeName());
        break;
      case 7:
        processingInstruction = (ProcessingInstruction)paramNode;
        this.receiver.processingInstruction(processingInstruction.getTarget(), processingInstruction.getData());
        break;
    } 
  }
  
  private void setCurrentLocation(Node paramNode) throws SAXException { this.currentNode = paramNode; }
  
  public Node getCurrentLocation() { return this.currentNode; }
  
  public Object getCurrentElement() { return this.currentNode; }
  
  public LocatorEx getLocator() { return this; }
  
  public void setContentHandler(ContentHandler paramContentHandler) { this.receiver = paramContentHandler; }
  
  public ContentHandler getContentHandler() { return this.receiver; }
  
  public String getPublicId() { return null; }
  
  public String getSystemId() { return null; }
  
  public int getLineNumber() { return -1; }
  
  public int getColumnNumber() { return -1; }
  
  public ValidationEventLocator getLocation() { return new ValidationEventLocatorImpl(getCurrentLocation()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bin\\unmarshaller\DOMScanner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */