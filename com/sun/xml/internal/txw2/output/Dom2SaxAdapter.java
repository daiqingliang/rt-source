package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TxwException;
import java.util.ArrayList;
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
import org.xml.sax.ext.LexicalHandler;

class Dom2SaxAdapter implements ContentHandler, LexicalHandler {
  private final Node _node;
  
  private final Stack _nodeStk = new Stack();
  
  private boolean inCDATA;
  
  private final Document _document;
  
  private ArrayList unprocessedNamespaces = new ArrayList();
  
  public final Element getCurrentElement() { return (Element)this._nodeStk.peek(); }
  
  public Dom2SaxAdapter(Node paramNode) {
    this._node = paramNode;
    this._nodeStk.push(this._node);
    if (paramNode instanceof Document) {
      this._document = (Document)paramNode;
    } else {
      this._document = paramNode.getOwnerDocument();
    } 
  }
  
  public Dom2SaxAdapter() throws ParserConfigurationException {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setNamespaceAware(true);
    documentBuilderFactory.setValidating(false);
    this._document = documentBuilderFactory.newDocumentBuilder().newDocument();
    this._node = this._document;
    this._nodeStk.push(this._document);
  }
  
  public Node getDOM() { return this._node; }
  
  public void startDocument() throws ParserConfigurationException {}
  
  public void endDocument() throws ParserConfigurationException {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) {
    Element element = this._document.createElementNS(paramString1, paramString3);
    if (element == null)
      throw new TxwException("Your DOM provider doesn't support the createElementNS method properly"); 
    int i;
    for (i = 0; i < this.unprocessedNamespaces.size(); i += true) {
      String str3;
      String str1 = (String)this.unprocessedNamespaces.get(i + false);
      String str2 = (String)this.unprocessedNamespaces.get(i + true);
      if ("".equals(str1) || str1 == null) {
        str3 = "xmlns";
      } else {
        str3 = "xmlns:" + str1;
      } 
      if (element.hasAttributeNS("http://www.w3.org/2000/xmlns/", str3))
        element.removeAttributeNS("http://www.w3.org/2000/xmlns/", str3); 
      element.setAttributeNS("http://www.w3.org/2000/xmlns/", str3, str2);
    } 
    this.unprocessedNamespaces.clear();
    i = paramAttributes.getLength();
    for (byte b = 0; b < i; b++) {
      String str1 = paramAttributes.getURI(b);
      String str2 = paramAttributes.getValue(b);
      String str3 = paramAttributes.getQName(b);
      element.setAttributeNS(str1, str3, str2);
    } 
    getParent().appendChild(element);
    this._nodeStk.push(element);
  }
  
  private final Node getParent() { return (Node)this._nodeStk.peek(); }
  
  public void endElement(String paramString1, String paramString2, String paramString3) { this._nodeStk.pop(); }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    Text text;
    if (this.inCDATA) {
      text = this._document.createCDATASection(new String(paramArrayOfChar, paramInt1, paramInt2));
    } else {
      text = this._document.createTextNode(new String(paramArrayOfChar, paramInt1, paramInt2));
    } 
    getParent().appendChild(text);
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) { getParent().appendChild(this._document.createComment(new String(paramArrayOfChar, paramInt1, paramInt2))); }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) {}
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    ProcessingInstruction processingInstruction = this._document.createProcessingInstruction(paramString1, paramString2);
    getParent().appendChild(processingInstruction);
  }
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void skippedEntity(String paramString) {}
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    this.unprocessedNamespaces.add(paramString1);
    this.unprocessedNamespaces.add(paramString2);
  }
  
  public void endPrefixMapping(String paramString) {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3) {}
  
  public void endDTD() throws ParserConfigurationException {}
  
  public void startEntity(String paramString) {}
  
  public void endEntity(String paramString) {}
  
  public void startCDATA() throws ParserConfigurationException { this.inCDATA = true; }
  
  public void endCDATA() throws ParserConfigurationException { this.inCDATA = false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\output\Dom2SaxAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */