package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.CDATAImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.CommentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementFactory;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.TextImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class SOAPDocumentImpl extends DocumentImpl implements SOAPDocument {
  private static final String XMLNS = "xmlns".intern();
  
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
  
  SOAPPartImpl enclosingSOAPPart;
  
  public SOAPDocumentImpl(SOAPPartImpl paramSOAPPartImpl) { this.enclosingSOAPPart = paramSOAPPartImpl; }
  
  public SOAPPartImpl getSOAPPart() {
    if (this.enclosingSOAPPart == null) {
      log.severe("SAAJ0541.soap.fragment.not.bound.to.part");
      throw new RuntimeException("Could not complete operation. Fragment not bound to SOAP part.");
    } 
    return this.enclosingSOAPPart;
  }
  
  public SOAPDocumentImpl getDocument() { return this; }
  
  public DocumentType getDoctype() { return null; }
  
  public DOMImplementation getImplementation() { return super.getImplementation(); }
  
  public Element getDocumentElement() {
    getSOAPPart().doGetDocumentElement();
    return doGetDocumentElement();
  }
  
  protected Element doGetDocumentElement() { return super.getDocumentElement(); }
  
  public Element createElement(String paramString) throws DOMException { return ElementFactory.createElement(this, NameImpl.getLocalNameFromTagName(paramString), NameImpl.getPrefixFromTagName(paramString), null); }
  
  public DocumentFragment createDocumentFragment() { return new SOAPDocumentFragment(this); }
  
  public Text createTextNode(String paramString) { return new TextImpl(this, paramString); }
  
  public Comment createComment(String paramString) { return new CommentImpl(this, paramString); }
  
  public CDATASection createCDATASection(String paramString) throws DOMException { return new CDATAImpl(this, paramString); }
  
  public ProcessingInstruction createProcessingInstruction(String paramString1, String paramString2) throws DOMException {
    log.severe("SAAJ0542.soap.proc.instructions.not.allowed.in.docs");
    throw new UnsupportedOperationException("Processing Instructions are not allowed in SOAP documents");
  }
  
  public Attr createAttribute(String paramString) throws DOMException {
    boolean bool = (paramString.indexOf(":") > 0) ? 1 : 0;
    if (bool) {
      String str1 = null;
      String str2 = paramString.substring(0, paramString.indexOf(":"));
      if (XMLNS.equals(str2)) {
        str1 = ElementImpl.XMLNS_URI;
        return createAttributeNS(str1, paramString);
      } 
    } 
    return super.createAttribute(paramString);
  }
  
  public EntityReference createEntityReference(String paramString) throws DOMException {
    log.severe("SAAJ0543.soap.entity.refs.not.allowed.in.docs");
    throw new UnsupportedOperationException("Entity References are not allowed in SOAP documents");
  }
  
  public NodeList getElementsByTagName(String paramString) { return super.getElementsByTagName(paramString); }
  
  public Node importNode(Node paramNode, boolean paramBoolean) throws DOMException { return super.importNode(paramNode, paramBoolean); }
  
  public Element createElementNS(String paramString1, String paramString2) throws DOMException { return ElementFactory.createElement(this, NameImpl.getLocalNameFromTagName(paramString2), NameImpl.getPrefixFromTagName(paramString2), paramString1); }
  
  public Attr createAttributeNS(String paramString1, String paramString2) throws DOMException { return super.createAttributeNS(paramString1, paramString2); }
  
  public NodeList getElementsByTagNameNS(String paramString1, String paramString2) { return super.getElementsByTagNameNS(paramString1, paramString2); }
  
  public Element getElementById(String paramString) throws DOMException { return super.getElementById(paramString); }
  
  public Node cloneNode(boolean paramBoolean) {
    SOAPPartImpl sOAPPartImpl = getSOAPPart().doCloneNode();
    cloneNode(sOAPPartImpl.getDocument(), paramBoolean);
    return sOAPPartImpl;
  }
  
  public void cloneNode(SOAPDocumentImpl paramSOAPDocumentImpl, boolean paramBoolean) { cloneNode(paramSOAPDocumentImpl, paramBoolean); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\SOAPDocumentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */