package com.sun.org.apache.xalan.internal.xsltc.trax;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Constants;
import java.util.Stack;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;

public class SAX2DOM implements ContentHandler, LexicalHandler, Constants {
  private Node _root = null;
  
  private Document _document = null;
  
  private Node _nextSibling = null;
  
  private Stack _nodeStk = new Stack();
  
  private Vector _namespaceDecls = null;
  
  private Node _lastSibling = null;
  
  private Locator locator = null;
  
  private boolean needToSetDocumentInfo = true;
  
  private StringBuilder _textBuffer = new StringBuilder();
  
  private Node _nextSiblingCache = null;
  
  private DocumentBuilderFactory _factory;
  
  private boolean _internal = true;
  
  public SAX2DOM(boolean paramBoolean) throws ParserConfigurationException {
    this._document = createDocument(paramBoolean);
    this._root = this._document;
  }
  
  public SAX2DOM(Node paramNode1, Node paramNode2, boolean paramBoolean) throws ParserConfigurationException {
    this._root = paramNode1;
    if (paramNode1 instanceof Document) {
      this._document = (Document)paramNode1;
    } else if (paramNode1 != null) {
      this._document = paramNode1.getOwnerDocument();
    } else {
      this._document = createDocument(paramBoolean);
      this._root = this._document;
    } 
    this._nextSibling = paramNode2;
  }
  
  public SAX2DOM(Node paramNode, boolean paramBoolean) throws ParserConfigurationException { this(paramNode, null, paramBoolean); }
  
  public Node getDOM() { return this._root; }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (paramInt2 == 0)
      return; 
    Node node = (Node)this._nodeStk.peek();
    if (node != this._document) {
      this._nextSiblingCache = this._nextSibling;
      this._textBuffer.append(paramArrayOfChar, paramInt1, paramInt2);
    } 
  }
  
  private void appendTextNode() {
    if (this._textBuffer.length() > 0) {
      Node node = (Node)this._nodeStk.peek();
      if (node == this._root && this._nextSiblingCache != null) {
        this._lastSibling = node.insertBefore(this._document.createTextNode(this._textBuffer.toString()), this._nextSiblingCache);
      } else {
        this._lastSibling = node.appendChild(this._document.createTextNode(this._textBuffer.toString()));
      } 
      this._textBuffer.setLength(0);
    } 
  }
  
  public void startDocument() { this._nodeStk.push(this._root); }
  
  public void endDocument() { this._nodeStk.pop(); }
  
  private void setDocumentInfo() {
    if (this.locator == null)
      return; 
    try {
      this._document.setXmlVersion(((Locator2)this.locator).getXMLVersion());
    } catch (ClassCastException classCastException) {}
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) {
    appendTextNode();
    if (this.needToSetDocumentInfo) {
      setDocumentInfo();
      this.needToSetDocumentInfo = false;
    } 
    Element element = this._document.createElementNS(paramString1, paramString3);
    if (this._namespaceDecls != null) {
      int j = this._namespaceDecls.size();
      for (byte b1 = 0; b1 < j; b1++) {
        String str = (String)this._namespaceDecls.elementAt(b1++);
        if (str == null || str.equals("")) {
          element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", (String)this._namespaceDecls.elementAt(b1));
        } else {
          element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + str, (String)this._namespaceDecls.elementAt(b1));
        } 
      } 
      this._namespaceDecls.clear();
    } 
    int i = paramAttributes.getLength();
    for (byte b = 0; b < i; b++) {
      String str1 = paramAttributes.getQName(b);
      String str2 = paramAttributes.getURI(b);
      if (paramAttributes.getLocalName(b).equals("")) {
        element.setAttribute(str1, paramAttributes.getValue(b));
        if (paramAttributes.getType(b).equals("ID"))
          element.setIdAttribute(str1, true); 
      } else {
        element.setAttributeNS(str2, str1, paramAttributes.getValue(b));
        if (paramAttributes.getType(b).equals("ID"))
          element.setIdAttributeNS(str2, paramAttributes.getLocalName(b), true); 
      } 
    } 
    Node node = (Node)this._nodeStk.peek();
    if (node == this._root && this._nextSibling != null) {
      node.insertBefore(element, this._nextSibling);
    } else {
      node.appendChild(element);
    } 
    this._nodeStk.push(element);
    this._lastSibling = null;
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) {
    appendTextNode();
    this._nodeStk.pop();
    this._lastSibling = null;
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) {
    if (this._namespaceDecls == null)
      this._namespaceDecls = new Vector(2); 
    this._namespaceDecls.addElement(paramString1);
    this._namespaceDecls.addElement(paramString2);
  }
  
  public void endPrefixMapping(String paramString) {}
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) {}
  
  public void processingInstruction(String paramString1, String paramString2) {
    appendTextNode();
    Node node = (Node)this._nodeStk.peek();
    ProcessingInstruction processingInstruction = this._document.createProcessingInstruction(paramString1, paramString2);
    if (processingInstruction != null) {
      if (node == this._root && this._nextSibling != null) {
        node.insertBefore(processingInstruction, this._nextSibling);
      } else {
        node.appendChild(processingInstruction);
      } 
      this._lastSibling = processingInstruction;
    } 
  }
  
  public void setDocumentLocator(Locator paramLocator) { this.locator = paramLocator; }
  
  public void skippedEntity(String paramString) {}
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    appendTextNode();
    Node node = (Node)this._nodeStk.peek();
    Comment comment = this._document.createComment(new String(paramArrayOfChar, paramInt1, paramInt2));
    if (comment != null) {
      if (node == this._root && this._nextSibling != null) {
        node.insertBefore(comment, this._nextSibling);
      } else {
        node.appendChild(comment);
      } 
      this._lastSibling = comment;
    } 
  }
  
  public void startCDATA() {}
  
  public void endCDATA() {}
  
  public void startEntity(String paramString) {}
  
  public void endDTD() {}
  
  public void endEntity(String paramString) {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3) {}
  
  private Document createDocument(boolean paramBoolean) throws ParserConfigurationException {
    Document document;
    if (this._factory == null) {
      this._factory = JdkXmlUtils.getDOMFactory(paramBoolean);
      this._internal = true;
      if (!(this._factory instanceof com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl))
        this._internal = false; 
    } 
    if (this._internal) {
      document = this._factory.newDocumentBuilder().newDocument();
    } else {
      synchronized (SAX2DOM.class) {
        document = this._factory.newDocumentBuilder().newDocument();
      } 
    } 
    return document;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\trax\SAX2DOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */