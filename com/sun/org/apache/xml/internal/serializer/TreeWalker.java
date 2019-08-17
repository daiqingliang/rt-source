package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xml.internal.utils.AttList;
import com.sun.org.apache.xml.internal.utils.DOM2Helper;
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

public final class TreeWalker {
  private final ContentHandler m_contentHandler;
  
  private final SerializationHandler m_Serializer;
  
  private final LocatorImpl m_locator = new LocatorImpl();
  
  boolean nextIsRaw = false;
  
  public ContentHandler getContentHandler() { return this.m_contentHandler; }
  
  public TreeWalker(ContentHandler paramContentHandler) { this(paramContentHandler, null); }
  
  public TreeWalker(ContentHandler paramContentHandler, String paramString) {
    this.m_contentHandler = paramContentHandler;
    if (this.m_contentHandler instanceof SerializationHandler) {
      this.m_Serializer = (SerializationHandler)this.m_contentHandler;
    } else {
      this.m_Serializer = null;
    } 
    this.m_contentHandler.setDocumentLocator(this.m_locator);
    if (paramString != null)
      this.m_locator.setSystemId(paramString); 
  }
  
  public void traverse(Node paramNode) throws SAXException {
    this.m_contentHandler.startDocument();
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
    this.m_contentHandler.endDocument();
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
    if (this.m_Serializer != null) {
      this.m_Serializer.characters(paramNode);
    } else {
      String str = ((Text)paramNode).getData();
      this.m_contentHandler.characters(str.toCharArray(), 0, str.length());
    } 
  }
  
  protected void startNode(Node paramNode) throws SAXException {
    String str4;
    LexicalHandler lexicalHandler;
    ProcessingInstruction processingInstruction;
    boolean bool;
    EntityReference entityReference;
    String str3;
    byte b;
    int i;
    String str2;
    NamedNodeMap namedNodeMap;
    Element element;
    String str1;
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
        element = (Element)paramNode;
        str2 = element.getNamespaceURI();
        if (str2 != null) {
          String str = element.getPrefix();
          if (str == null)
            str = ""; 
          this.m_contentHandler.startPrefixMapping(str, str2);
        } 
        namedNodeMap = element.getAttributes();
        i = namedNodeMap.getLength();
        for (b = 0; b < i; b++) {
          Node node = namedNodeMap.item(b);
          String str = node.getNodeName();
          int j = str.indexOf(':');
          if (str.equals("xmlns") || str.startsWith("xmlns:")) {
            String str5;
            if (j < 0) {
              str5 = "";
            } else {
              str5 = str.substring(j + 1);
            } 
            this.m_contentHandler.startPrefixMapping(str5, node.getNodeValue());
          } else if (j > 0) {
            String str5 = str.substring(0, j);
            String str6 = node.getNamespaceURI();
            if (str6 != null)
              this.m_contentHandler.startPrefixMapping(str5, str6); 
          } 
        } 
        str3 = DOM2Helper.getNamespaceOfNode(paramNode);
        if (null == str3)
          str3 = ""; 
        this.m_contentHandler.startElement(str3, DOM2Helper.getLocalNameOfNode(paramNode), paramNode.getNodeName(), new AttList(namedNodeMap));
        break;
      case 7:
        processingInstruction = (ProcessingInstruction)paramNode;
        str4 = processingInstruction.getNodeName();
        if (str4.equals("xslt-next-is-raw")) {
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
    EntityReference entityReference;
    String str;
    switch (paramNode.getNodeType()) {
      case 1:
        str = DOM2Helper.getNamespaceOfNode(paramNode);
        if (null == str)
          str = ""; 
        this.m_contentHandler.endElement(str, DOM2Helper.getLocalNameOfNode(paramNode), paramNode.getNodeName());
        if (this.m_Serializer == null) {
          Element element = (Element)paramNode;
          NamedNodeMap namedNodeMap = element.getAttributes();
          int i = namedNodeMap.getLength();
          for (int j = i - 1; 0 <= j; j--) {
            Node node = namedNodeMap.item(j);
            String str2 = node.getNodeName();
            int k = str2.indexOf(':');
            if (str2.equals("xmlns") || str2.startsWith("xmlns:")) {
              String str3;
              if (k < 0) {
                str3 = "";
              } else {
                str3 = str2.substring(k + 1);
              } 
              this.m_contentHandler.endPrefixMapping(str3);
            } else if (k > 0) {
              String str3 = str2.substring(0, k);
              this.m_contentHandler.endPrefixMapping(str3);
            } 
          } 
          String str1 = element.getNamespaceURI();
          if (str1 != null) {
            String str2 = element.getPrefix();
            if (str2 == null)
              str2 = ""; 
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\TreeWalker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */