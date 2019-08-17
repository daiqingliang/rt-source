package com.sun.xml.internal.bind.marshaller;

import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.util.Which;
import com.sun.xml.internal.bind.v2.util.XmlFactory;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SAX2DOMEx implements ContentHandler {
  private Node node = null;
  
  private boolean isConsolidate;
  
  protected final Stack<Node> nodeStack = new Stack();
  
  private final FinalArrayList<String> unprocessedNamespaces = new FinalArrayList();
  
  protected final Document document;
  
  public SAX2DOMEx(Node paramNode) { this(paramNode, false); }
  
  public SAX2DOMEx(Node paramNode, boolean paramBoolean) {
    this.node = paramNode;
    this.isConsolidate = paramBoolean;
    this.nodeStack.push(this.node);
    if (paramNode instanceof Document) {
      this.document = (Document)paramNode;
    } else {
      this.document = paramNode.getOwnerDocument();
    } 
  }
  
  public SAX2DOMEx(DocumentBuilderFactory paramDocumentBuilderFactory) throws ParserConfigurationException {
    paramDocumentBuilderFactory.setValidating(false);
    this.document = paramDocumentBuilderFactory.newDocumentBuilder().newDocument();
    this.node = this.document;
    this.nodeStack.push(this.document);
  }
  
  public SAX2DOMEx() throws ParserConfigurationException {
    DocumentBuilderFactory documentBuilderFactory = XmlFactory.createDocumentBuilderFactory(false);
    documentBuilderFactory.setValidating(false);
    this.document = documentBuilderFactory.newDocumentBuilder().newDocument();
    this.node = this.document;
    this.nodeStack.push(this.document);
  }
  
  public final Element getCurrentElement() { return (Element)this.nodeStack.peek(); }
  
  public Node getDOM() { return this.node; }
  
  public void startDocument() throws ParserConfigurationException {}
  
  public void endDocument() throws ParserConfigurationException {}
  
  protected void namespace(Element paramElement, String paramString1, String paramString2) {
    String str;
    if ("".equals(paramString1) || paramString1 == null) {
      str = "xmlns";
    } else {
      str = "xmlns:" + paramString1;
    } 
    if (paramElement.hasAttributeNS("http://www.w3.org/2000/xmlns/", str))
      paramElement.removeAttributeNS("http://www.w3.org/2000/xmlns/", str); 
    paramElement.setAttributeNS("http://www.w3.org/2000/xmlns/", str, paramString2);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) {
    Node node1 = (Node)this.nodeStack.peek();
    Element element = this.document.createElementNS(paramString1, paramString3);
    if (element == null)
      throw new AssertionError(Messages.format("SAX2DOMEx.DomImplDoesntSupportCreateElementNs", this.document.getClass().getName(), Which.which(this.document.getClass()))); 
    int i;
    for (i = 0; i < this.unprocessedNamespaces.size(); i += 2) {
      String str1 = (String)this.unprocessedNamespaces.get(i);
      String str2 = (String)this.unprocessedNamespaces.get(i + 1);
      namespace(element, str1, str2);
    } 
    this.unprocessedNamespaces.clear();
    if (paramAttributes != null) {
      i = paramAttributes.getLength();
      for (byte b = 0; b < i; b++) {
        String str1 = paramAttributes.getURI(b);
        String str2 = paramAttributes.getValue(b);
        String str3 = paramAttributes.getQName(b);
        element.setAttributeNS(str1, str3, str2);
      } 
    } 
    node1.appendChild(element);
    this.nodeStack.push(element);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) { this.nodeStack.pop(); }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) { characters(new String(paramArrayOfChar, paramInt1, paramInt2)); }
  
  protected Text characters(String paramString) {
    Text text;
    Node node1 = (Node)this.nodeStack.peek();
    Node node2 = node1.getLastChild();
    if (this.isConsolidate && node2 != null && node2.getNodeType() == 3) {
      text = (Text)node2;
      text.appendData(paramString);
    } else {
      text = this.document.createTextNode(paramString);
      node1.appendChild(text);
    } 
    return text;
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) {}
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    Node node1 = (Node)this.nodeStack.peek();
    ProcessingInstruction processingInstruction = this.document.createProcessingInstruction(paramString1, paramString2);
    node1.appendChild(processingInstruction);
  }
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void skippedEntity(String paramString) {}
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    this.unprocessedNamespaces.add(paramString1);
    this.unprocessedNamespaces.add(paramString2);
  }
  
  public void endPrefixMapping(String paramString) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\marshaller\SAX2DOMEx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */