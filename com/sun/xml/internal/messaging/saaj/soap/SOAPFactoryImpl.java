package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.soap.impl.ElementFactory;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class SOAPFactoryImpl extends SOAPFactory {
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
  
  protected abstract SOAPDocumentImpl createDocument();
  
  public SOAPElement createElement(String paramString) throws SOAPException {
    if (paramString == null) {
      log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[] { "tagName", "SOAPFactory.createElement" });
      throw new SOAPException("Null tagName argument passed to createElement");
    } 
    return ElementFactory.createElement(createDocument(), NameImpl.createFromTagName(paramString));
  }
  
  public SOAPElement createElement(Name paramName) throws SOAPException {
    if (paramName == null) {
      log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[] { "name", "SOAPFactory.createElement" });
      throw new SOAPException("Null name argument passed to createElement");
    } 
    return ElementFactory.createElement(createDocument(), paramName);
  }
  
  public SOAPElement createElement(QName paramQName) throws SOAPException {
    if (paramQName == null) {
      log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[] { "qname", "SOAPFactory.createElement" });
      throw new SOAPException("Null qname argument passed to createElement");
    } 
    return ElementFactory.createElement(createDocument(), paramQName);
  }
  
  public SOAPElement createElement(String paramString1, String paramString2, String paramString3) throws SOAPException {
    if (paramString1 == null) {
      log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[] { "localName", "SOAPFactory.createElement" });
      throw new SOAPException("Null localName argument passed to createElement");
    } 
    return ElementFactory.createElement(createDocument(), paramString1, paramString2, paramString3);
  }
  
  public Name createName(String paramString1, String paramString2, String paramString3) throws SOAPException {
    if (paramString1 == null) {
      log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[] { "localName", "SOAPFactory.createName" });
      throw new SOAPException("Null localName argument passed to createName");
    } 
    return NameImpl.create(paramString1, paramString2, paramString3);
  }
  
  public Name createName(String paramString) throws SOAPException {
    if (paramString == null) {
      log.log(Level.SEVERE, "SAAJ0567.soap.null.input", new Object[] { "localName", "SOAPFactory.createName" });
      throw new SOAPException("Null localName argument passed to createName");
    } 
    return NameImpl.createFromUnqualifiedName(paramString);
  }
  
  public SOAPElement createElement(Element paramElement) throws SOAPException { return (paramElement == null) ? null : convertToSoapElement(paramElement); }
  
  private SOAPElement convertToSoapElement(Element paramElement) throws SOAPException {
    if (paramElement instanceof SOAPElement)
      return (SOAPElement)paramElement; 
    SOAPElement sOAPElement = createElement(paramElement.getLocalName(), paramElement.getPrefix(), paramElement.getNamespaceURI());
    Document document = sOAPElement.getOwnerDocument();
    NamedNodeMap namedNodeMap = paramElement.getAttributes();
    for (byte b1 = 0; b1 < namedNodeMap.getLength(); b1++) {
      Attr attr1 = (Attr)namedNodeMap.item(b1);
      Attr attr2 = (Attr)document.importNode(attr1, true);
      sOAPElement.setAttributeNodeNS(attr2);
    } 
    NodeList nodeList = paramElement.getChildNodes();
    for (byte b2 = 0; b2 < nodeList.getLength(); b2++) {
      Node node1 = nodeList.item(b2);
      Node node2 = document.importNode(node1, true);
      sOAPElement.appendChild(node2);
    } 
    return sOAPElement;
  }
  
  public Detail createDetail() throws SOAPException { throw new UnsupportedOperationException(); }
  
  public SOAPFault createFault(String paramString, QName paramQName) throws SOAPException { throw new UnsupportedOperationException(); }
  
  public SOAPFault createFault() throws SOAPException { throw new UnsupportedOperationException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\SOAPFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */