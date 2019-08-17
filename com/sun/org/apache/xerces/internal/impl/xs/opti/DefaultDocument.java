package com.sun.org.apache.xerces.internal.impl.xs.opti;

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
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class DefaultDocument extends NodeImpl implements Document {
  private String fDocumentURI = null;
  
  public DocumentType getDoctype() { return null; }
  
  public DOMImplementation getImplementation() { return null; }
  
  public Element getDocumentElement() { return null; }
  
  public NodeList getElementsByTagName(String paramString) { return null; }
  
  public NodeList getElementsByTagNameNS(String paramString1, String paramString2) { return null; }
  
  public Element getElementById(String paramString) { return null; }
  
  public Node importNode(Node paramNode, boolean paramBoolean) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public Element createElement(String paramString) { throw new DOMException((short)9, "Method not supported"); }
  
  public DocumentFragment createDocumentFragment() { return null; }
  
  public Text createTextNode(String paramString) { return null; }
  
  public Comment createComment(String paramString) { return null; }
  
  public CDATASection createCDATASection(String paramString) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public ProcessingInstruction createProcessingInstruction(String paramString1, String paramString2) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public Attr createAttribute(String paramString) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public EntityReference createEntityReference(String paramString) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public Element createElementNS(String paramString1, String paramString2) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public Attr createAttributeNS(String paramString1, String paramString2) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public String getInputEncoding() { return null; }
  
  public String getXmlEncoding() { return null; }
  
  public boolean getXmlStandalone() { throw new DOMException((short)9, "Method not supported"); }
  
  public void setXmlStandalone(boolean paramBoolean) { throw new DOMException((short)9, "Method not supported"); }
  
  public String getXmlVersion() { return null; }
  
  public void setXmlVersion(String paramString) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public boolean getStrictErrorChecking() { return false; }
  
  public void setStrictErrorChecking(boolean paramBoolean) { throw new DOMException((short)9, "Method not supported"); }
  
  public String getDocumentURI() { return this.fDocumentURI; }
  
  public void setDocumentURI(String paramString) throws DOMException { this.fDocumentURI = paramString; }
  
  public Node adoptNode(Node paramNode) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
  
  public void normalizeDocument() { throw new DOMException((short)9, "Method not supported"); }
  
  public DOMConfiguration getDomConfig() { throw new DOMException((short)9, "Method not supported"); }
  
  public Node renameNode(Node paramNode, String paramString1, String paramString2) throws DOMException { throw new DOMException((short)9, "Method not supported"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\opti\DefaultDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */