package com.sun.xml.internal.stream.writers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.helpers.NamespaceSupport;

public class XMLDOMWriterImpl implements XMLStreamWriter {
  private Document ownerDoc = null;
  
  private Node currentNode = null;
  
  private Node node = null;
  
  private NamespaceSupport namespaceContext = null;
  
  private Method mXmlVersion = null;
  
  private boolean[] needContextPop = null;
  
  private StringBuffer stringBuffer = null;
  
  private int resizeValue = 20;
  
  private int depth = 0;
  
  public XMLDOMWriterImpl(DOMResult paramDOMResult) {
    this.node = paramDOMResult.getNode();
    if (this.node.getNodeType() == 9) {
      this.ownerDoc = (Document)this.node;
      this.currentNode = this.ownerDoc;
    } else {
      this.ownerDoc = this.node.getOwnerDocument();
      this.currentNode = this.node;
    } 
    getDLThreeMethods();
    this.stringBuffer = new StringBuffer();
    this.needContextPop = new boolean[this.resizeValue];
    this.namespaceContext = new NamespaceSupport();
  }
  
  private void getDLThreeMethods() {
    try {
      this.mXmlVersion = this.ownerDoc.getClass().getMethod("setXmlVersion", new Class[] { String.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      this.mXmlVersion = null;
    } catch (SecurityException securityException) {
      this.mXmlVersion = null;
    } 
  }
  
  public void close() {}
  
  public void flush() {}
  
  public NamespaceContext getNamespaceContext() { return null; }
  
  public String getPrefix(String paramString) throws XMLStreamException {
    String str = null;
    if (this.namespaceContext != null)
      str = this.namespaceContext.getPrefix(paramString); 
    return str;
  }
  
  public Object getProperty(String paramString) throws IllegalArgumentException { throw new UnsupportedOperationException(); }
  
  public void setDefaultNamespace(String paramString) throws XMLStreamException {
    this.namespaceContext.declarePrefix("", paramString);
    if (!this.needContextPop[this.depth])
      this.needContextPop[this.depth] = true; 
  }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext) throws XMLStreamException { throw new UnsupportedOperationException(); }
  
  public void setPrefix(String paramString1, String paramString2) throws XMLStreamException {
    if (paramString1 == null)
      throw new XMLStreamException("Prefix cannot be null"); 
    this.namespaceContext.declarePrefix(paramString1, paramString2);
    if (!this.needContextPop[this.depth])
      this.needContextPop[this.depth] = true; 
  }
  
  public void writeAttribute(String paramString1, String paramString2) throws XMLStreamException {
    if (this.currentNode.getNodeType() == 1) {
      Attr attr = this.ownerDoc.createAttribute(paramString1);
      attr.setValue(paramString2);
      ((Element)this.currentNode).setAttributeNode(attr);
    } else {
      throw new IllegalStateException("Current DOM Node type  is " + this.currentNode.getNodeType() + "and does not allow attributes to be set ");
    } 
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
    if (this.currentNode.getNodeType() == 1) {
      String str1 = null;
      if (paramString1 == null)
        throw new XMLStreamException("NamespaceURI cannot be null"); 
      if (paramString2 == null)
        throw new XMLStreamException("Local name cannot be null"); 
      if (this.namespaceContext != null)
        str1 = this.namespaceContext.getPrefix(paramString1); 
      if (str1 == null)
        throw new XMLStreamException("Namespace URI " + paramString1 + "is not bound to any prefix"); 
      String str2 = null;
      if (str1.equals("")) {
        str2 = paramString2;
      } else {
        str2 = getQName(str1, paramString2);
      } 
      Attr attr = this.ownerDoc.createAttributeNS(paramString1, str2);
      attr.setValue(paramString3);
      ((Element)this.currentNode).setAttributeNode(attr);
    } else {
      throw new IllegalStateException("Current DOM Node type  is " + this.currentNode.getNodeType() + "and does not allow attributes to be set ");
    } 
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, String paramString4) throws XMLStreamException {
    if (this.currentNode.getNodeType() == 1) {
      if (paramString2 == null)
        throw new XMLStreamException("NamespaceURI cannot be null"); 
      if (paramString3 == null)
        throw new XMLStreamException("Local name cannot be null"); 
      if (paramString1 == null)
        throw new XMLStreamException("prefix cannot be null"); 
      String str = null;
      if (paramString1.equals("")) {
        str = paramString3;
      } else {
        str = getQName(paramString1, paramString3);
      } 
      Attr attr = this.ownerDoc.createAttributeNS(paramString2, str);
      attr.setValue(paramString4);
      ((Element)this.currentNode).setAttributeNodeNS(attr);
    } else {
      throw new IllegalStateException("Current DOM Node type  is " + this.currentNode.getNodeType() + "and does not allow attributes to be set ");
    } 
  }
  
  public void writeCData(String paramString) throws XMLStreamException {
    if (paramString == null)
      throw new XMLStreamException("CDATA cannot be null"); 
    CDATASection cDATASection = this.ownerDoc.createCDATASection(paramString);
    getNode().appendChild(cDATASection);
  }
  
  public void writeCharacters(String paramString) throws XMLStreamException {
    Text text = this.ownerDoc.createTextNode(paramString);
    this.currentNode.appendChild(text);
  }
  
  public void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws XMLStreamException {
    Text text = this.ownerDoc.createTextNode(new String(paramArrayOfChar, paramInt1, paramInt2));
    this.currentNode.appendChild(text);
  }
  
  public void writeComment(String paramString) throws XMLStreamException {
    Comment comment = this.ownerDoc.createComment(paramString);
    getNode().appendChild(comment);
  }
  
  public void writeDTD(String paramString) throws XMLStreamException { throw new UnsupportedOperationException(); }
  
  public void writeDefaultNamespace(String paramString) throws XMLStreamException {
    if (this.currentNode.getNodeType() == 1) {
      String str = "xmlns";
      ((Element)this.currentNode).setAttributeNS("http://www.w3.org/2000/xmlns/", str, paramString);
    } else {
      throw new IllegalStateException("Current DOM Node type  is " + this.currentNode.getNodeType() + "and does not allow attributes to be set ");
    } 
  }
  
  public void writeEmptyElement(String paramString) throws XMLStreamException {
    if (this.ownerDoc != null) {
      Element element = this.ownerDoc.createElement(paramString);
      if (this.currentNode != null) {
        this.currentNode.appendChild(element);
      } else {
        this.ownerDoc.appendChild(element);
      } 
    } 
  }
  
  public void writeEmptyElement(String paramString1, String paramString2) throws XMLStreamException {
    if (this.ownerDoc != null) {
      String str1 = null;
      String str2 = null;
      if (paramString1 == null)
        throw new XMLStreamException("NamespaceURI cannot be null"); 
      if (paramString2 == null)
        throw new XMLStreamException("Local name cannot be null"); 
      if (this.namespaceContext != null)
        str2 = this.namespaceContext.getPrefix(paramString1); 
      if (str2 == null)
        throw new XMLStreamException("Namespace URI " + paramString1 + "is not bound to any prefix"); 
      if ("".equals(str2)) {
        str1 = paramString2;
      } else {
        str1 = getQName(str2, paramString2);
      } 
      Element element = this.ownerDoc.createElementNS(paramString1, str1);
      if (this.currentNode != null) {
        this.currentNode.appendChild(element);
      } else {
        this.ownerDoc.appendChild(element);
      } 
    } 
  }
  
  public void writeEmptyElement(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
    if (this.ownerDoc != null) {
      if (paramString3 == null)
        throw new XMLStreamException("NamespaceURI cannot be null"); 
      if (paramString2 == null)
        throw new XMLStreamException("Local name cannot be null"); 
      if (paramString1 == null)
        throw new XMLStreamException("Prefix cannot be null"); 
      String str = null;
      if ("".equals(paramString1)) {
        str = paramString2;
      } else {
        str = getQName(paramString1, paramString2);
      } 
      Element element = this.ownerDoc.createElementNS(paramString3, str);
      if (this.currentNode != null) {
        this.currentNode.appendChild(element);
      } else {
        this.ownerDoc.appendChild(element);
      } 
    } 
  }
  
  public void writeEndDocument() {
    this.currentNode = null;
    for (byte b = 0; b < this.depth; b++) {
      if (this.needContextPop[this.depth]) {
        this.needContextPop[this.depth] = false;
        this.namespaceContext.popContext();
      } 
      this.depth--;
    } 
    this.depth = 0;
  }
  
  public void writeEndElement() {
    Node node1 = this.currentNode.getParentNode();
    if (this.currentNode.getNodeType() == 9) {
      this.currentNode = null;
    } else {
      this.currentNode = node1;
    } 
    if (this.needContextPop[this.depth]) {
      this.needContextPop[this.depth] = false;
      this.namespaceContext.popContext();
    } 
    this.depth--;
  }
  
  public void writeEntityRef(String paramString) throws XMLStreamException {
    EntityReference entityReference = this.ownerDoc.createEntityReference(paramString);
    this.currentNode.appendChild(entityReference);
  }
  
  public void writeNamespace(String paramString1, String paramString2) throws XMLStreamException {
    if (paramString1 == null)
      throw new XMLStreamException("prefix cannot be null"); 
    if (paramString2 == null)
      throw new XMLStreamException("NamespaceURI cannot be null"); 
    String str = null;
    if (paramString1.equals("")) {
      str = "xmlns";
    } else {
      str = getQName("xmlns", paramString1);
    } 
    ((Element)this.currentNode).setAttributeNS("http://www.w3.org/2000/xmlns/", str, paramString2);
  }
  
  public void writeProcessingInstruction(String paramString) throws XMLStreamException {
    if (paramString == null)
      throw new XMLStreamException("Target cannot be null"); 
    ProcessingInstruction processingInstruction = this.ownerDoc.createProcessingInstruction(paramString, "");
    this.currentNode.appendChild(processingInstruction);
  }
  
  public void writeProcessingInstruction(String paramString1, String paramString2) throws XMLStreamException {
    if (paramString1 == null)
      throw new XMLStreamException("Target cannot be null"); 
    ProcessingInstruction processingInstruction = this.ownerDoc.createProcessingInstruction(paramString1, paramString2);
    this.currentNode.appendChild(processingInstruction);
  }
  
  public void writeStartDocument() {
    try {
      if (this.mXmlVersion != null)
        this.mXmlVersion.invoke(this.ownerDoc, new Object[] { "1.0" }); 
    } catch (IllegalAccessException illegalAccessException) {
      throw new XMLStreamException(illegalAccessException);
    } catch (InvocationTargetException invocationTargetException) {
      throw new XMLStreamException(invocationTargetException);
    } 
  }
  
  public void writeStartDocument(String paramString) throws XMLStreamException {
    try {
      if (this.mXmlVersion != null)
        this.mXmlVersion.invoke(this.ownerDoc, new Object[] { paramString }); 
    } catch (IllegalAccessException illegalAccessException) {
      throw new XMLStreamException(illegalAccessException);
    } catch (InvocationTargetException invocationTargetException) {
      throw new XMLStreamException(invocationTargetException);
    } 
  }
  
  public void writeStartDocument(String paramString1, String paramString2) throws XMLStreamException {
    try {
      if (this.mXmlVersion != null)
        this.mXmlVersion.invoke(this.ownerDoc, new Object[] { paramString2 }); 
    } catch (IllegalAccessException illegalAccessException) {
      throw new XMLStreamException(illegalAccessException);
    } catch (InvocationTargetException invocationTargetException) {
      throw new XMLStreamException(invocationTargetException);
    } 
  }
  
  public void writeStartElement(String paramString) throws XMLStreamException {
    if (this.ownerDoc != null) {
      Element element = this.ownerDoc.createElement(paramString);
      if (this.currentNode != null) {
        this.currentNode.appendChild(element);
      } else {
        this.ownerDoc.appendChild(element);
      } 
      this.currentNode = element;
    } 
    if (this.needContextPop[this.depth])
      this.namespaceContext.pushContext(); 
    incDepth();
  }
  
  public void writeStartElement(String paramString1, String paramString2) throws XMLStreamException {
    if (this.ownerDoc != null) {
      String str1 = null;
      String str2 = null;
      if (paramString1 == null)
        throw new XMLStreamException("NamespaceURI cannot be null"); 
      if (paramString2 == null)
        throw new XMLStreamException("Local name cannot be null"); 
      if (this.namespaceContext != null)
        str2 = this.namespaceContext.getPrefix(paramString1); 
      if (str2 == null)
        throw new XMLStreamException("Namespace URI " + paramString1 + "is not bound to any prefix"); 
      if ("".equals(str2)) {
        str1 = paramString2;
      } else {
        str1 = getQName(str2, paramString2);
      } 
      Element element = this.ownerDoc.createElementNS(paramString1, str1);
      if (this.currentNode != null) {
        this.currentNode.appendChild(element);
      } else {
        this.ownerDoc.appendChild(element);
      } 
      this.currentNode = element;
    } 
    if (this.needContextPop[this.depth])
      this.namespaceContext.pushContext(); 
    incDepth();
  }
  
  public void writeStartElement(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
    if (this.ownerDoc != null) {
      String str = null;
      if (paramString3 == null)
        throw new XMLStreamException("NamespaceURI cannot be null"); 
      if (paramString2 == null)
        throw new XMLStreamException("Local name cannot be null"); 
      if (paramString1 == null)
        throw new XMLStreamException("Prefix cannot be null"); 
      if (paramString1.equals("")) {
        str = paramString2;
      } else {
        str = getQName(paramString1, paramString2);
      } 
      Element element = this.ownerDoc.createElementNS(paramString3, str);
      if (this.currentNode != null) {
        this.currentNode.appendChild(element);
      } else {
        this.ownerDoc.appendChild(element);
      } 
      this.currentNode = element;
      if (this.needContextPop[this.depth])
        this.namespaceContext.pushContext(); 
      incDepth();
    } 
  }
  
  private String getQName(String paramString1, String paramString2) {
    this.stringBuffer.setLength(0);
    this.stringBuffer.append(paramString1);
    this.stringBuffer.append(":");
    this.stringBuffer.append(paramString2);
    return this.stringBuffer.toString();
  }
  
  private Node getNode() { return (this.currentNode == null) ? this.ownerDoc : this.currentNode; }
  
  private void incDepth() {
    this.depth++;
    if (this.depth == this.needContextPop.length) {
      boolean[] arrayOfBoolean = new boolean[this.depth + this.resizeValue];
      System.arraycopy(this.needContextPop, 0, arrayOfBoolean, 0, this.depth);
      this.needContextPop = arrayOfBoolean;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\writers\XMLDOMWriterImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */