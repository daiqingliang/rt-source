package com.sun.org.apache.xml.internal.utils;

import com.sun.org.apache.xml.internal.dtm.ref.dom2dtm.DOM2DTM;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.LocatorImpl;

public class TreeWalker {
  private ContentHandler m_contentHandler = null;
  
  private LocatorImpl m_locator = new LocatorImpl();
  
  boolean nextIsRaw = false;
  
  public ContentHandler getContentHandler() { return this.m_contentHandler; }
  
  public void setContentHandler(ContentHandler paramContentHandler) { this.m_contentHandler = paramContentHandler; }
  
  public TreeWalker(ContentHandler paramContentHandler, String paramString) {
    this.m_contentHandler = paramContentHandler;
    if (this.m_contentHandler != null)
      this.m_contentHandler.setDocumentLocator(this.m_locator); 
    if (paramString != null)
      this.m_locator.setSystemId(paramString); 
  }
  
  public TreeWalker(ContentHandler paramContentHandler) { this(paramContentHandler, null); }
  
  public void traverse(Node paramNode) throws SAXException {
    this.m_contentHandler.startDocument();
    traverseFragment(paramNode);
    this.m_contentHandler.endDocument();
  }
  
  public void traverseFragment(Node paramNode) throws SAXException {
    Node node = paramNode;
    while (null != paramNode) {
      startNode(paramNode);
      Node node1 = paramNode.getFirstChild();
      while (null == node1) {
        endNode(paramNode);
        if (node.equals(paramNode))
          break; 
        node1 = paramNode.getNextSibling();
        if (null == node1) {
          paramNode = paramNode.getParentNode();
          if (null == paramNode || node.equals(paramNode)) {
            if (null != paramNode)
              endNode(paramNode); 
            node1 = null;
            break;
          } 
        } 
      } 
      paramNode = node1;
    } 
  }
  
  public void traverse(Node paramNode1, Node paramNode2) throws SAXException {
    this.m_contentHandler.startDocument();
    while (null != paramNode1) {
      startNode(paramNode1);
      Node node = paramNode1.getFirstChild();
      while (null == node) {
        endNode(paramNode1);
        if (null != paramNode2 && paramNode2.equals(paramNode1))
          break; 
        node = paramNode1.getNextSibling();
        if (null == node) {
          paramNode1 = paramNode1.getParentNode();
          if (null == paramNode1 || (null != paramNode2 && paramNode2.equals(paramNode1))) {
            node = null;
            break;
          } 
        } 
      } 
      paramNode1 = node;
    } 
    this.m_contentHandler.endDocument();
  }
  
  private final void dispatachChars(Node paramNode) throws SAXException {
    if (this.m_contentHandler instanceof DOM2DTM.CharacterNodeHandler) {
      ((DOM2DTM.CharacterNodeHandler)this.m_contentHandler).characters(paramNode);
    } else {
      String str = ((Text)paramNode).getData();
      this.m_contentHandler.characters(str.toCharArray(), 0, str.length());
    } 
  }
  
  protected void startNode(Node paramNode) throws SAXException {
    LexicalHandler lexicalHandler;
    String str3;
    EntityReference entityReference;
    ProcessingInstruction processingInstruction;
    boolean bool;
    byte b;
    String str2;
    int i;
    NamedNodeMap namedNodeMap;
    String str1;
    if (this.m_contentHandler instanceof NodeConsumer)
      ((NodeConsumer)this.m_contentHandler).setOriginatingNode(paramNode); 
    if (paramNode instanceof Locator) {
      Locator locator = (Locator)paramNode;
      this.m_locator.setColumnNumber(locator.getColumnNumber());
      this.m_locator.setLineNumber(locator.getLineNumber());
      this.m_locator.setPublicId(locator.getPublicId());
      this.m_locator.setSystemId(locator.getSystemId());
    } else {
      this.m_locator.setColumnNumber(0);
      this.m_locator.setLineNumber(0);
    } 
    switch (paramNode.getNodeType()) {
      case 8:
        str1 = ((Comment)paramNode).getData();
        if (this.m_contentHandler instanceof LexicalHandler) {
          LexicalHandler lexicalHandler1 = (LexicalHandler)this.m_contentHandler;
          lexicalHandler1.comment(str1.toCharArray(), 0, str1.length());
        } 
        break;
      case 1:
        namedNodeMap = ((Element)paramNode).getAttributes();
        i = namedNodeMap.getLength();
        for (b = 0; b < i; b++) {
          Node node = namedNodeMap.item(b);
          String str = node.getNodeName();
          if (str.equals("xmlns") || str.startsWith("xmlns:")) {
            int j;
            String str4 = ((j = str.indexOf(":")) < 0) ? "" : str.substring(j + 1);
            this.m_contentHandler.startPrefixMapping(str4, node.getNodeValue());
          } 
        } 
        str2 = DOM2Helper.getNamespaceOfNode(paramNode);
        if (null == str2)
          str2 = ""; 
        this.m_contentHandler.startElement(str2, DOM2Helper.getLocalNameOfNode(paramNode), paramNode.getNodeName(), new AttList(namedNodeMap));
        break;
      case 7:
        processingInstruction = (ProcessingInstruction)paramNode;
        str3 = processingInstruction.getNodeName();
        if (str3.equals("xslt-next-is-raw")) {
          this.nextIsRaw = true;
          break;
        } 
        this.m_contentHandler.processingInstruction(processingInstruction.getNodeName(), processingInstruction.getData());
        break;
      case 4:
        bool = this.m_contentHandler instanceof LexicalHandler;
        lexicalHandler = bool ? (LexicalHandler)this.m_contentHandler : null;
        if (bool)
          lexicalHandler.startCDATA(); 
        dispatachChars(paramNode);
        if (bool)
          lexicalHandler.endCDATA(); 
        break;
      case 3:
        if (this.nextIsRaw) {
          this.nextIsRaw = false;
          this.m_contentHandler.processingInstruction("javax.xml.transform.disable-output-escaping", "");
          dispatachChars(paramNode);
          this.m_contentHandler.processingInstruction("javax.xml.transform.enable-output-escaping", "");
          break;
        } 
        dispatachChars(paramNode);
        break;
      case 5:
        entityReference = (EntityReference)paramNode;
        if (this.m_contentHandler instanceof LexicalHandler)
          ((LexicalHandler)this.m_contentHandler).startEntity(entityReference.getNodeName()); 
        break;
    } 
  }
  
  protected void endNode(Node paramNode) throws SAXException {
    byte b;
    EntityReference entityReference;
    int i;
    NamedNodeMap namedNodeMap;
    String str;
    switch (paramNode.getNodeType()) {
      case 1:
        str = DOM2Helper.getNamespaceOfNode(paramNode);
        if (null == str)
          str = ""; 
        this.m_contentHandler.endElement(str, DOM2Helper.getLocalNameOfNode(paramNode), paramNode.getNodeName());
        namedNodeMap = ((Element)paramNode).getAttributes();
        i = namedNodeMap.getLength();
        for (b = 0; b < i; b++) {
          Node node = namedNodeMap.item(b);
          String str1 = node.getNodeName();
          if (str1.equals("xmlns") || str1.startsWith("xmlns:")) {
            int j;
            String str2 = ((j = str1.indexOf(":")) < 0) ? "" : str1.substring(j + 1);
            this.m_contentHandler.endPrefixMapping(str2);
          } 
        } 
        break;
      case 5:
        entityReference = (EntityReference)paramNode;
        if (this.m_contentHandler instanceof LexicalHandler) {
          LexicalHandler lexicalHandler = (LexicalHandler)this.m_contentHandler;
          lexicalHandler.endEntity(entityReference.getNodeName());
        } 
        break;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\TreeWalker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */