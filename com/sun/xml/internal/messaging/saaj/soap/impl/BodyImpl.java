package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class BodyImpl extends ElementImpl implements SOAPBody {
  private SOAPFault fault;
  
  protected BodyImpl(SOAPDocumentImpl paramSOAPDocumentImpl, NameImpl paramNameImpl) { super(paramSOAPDocumentImpl, paramNameImpl); }
  
  protected abstract NameImpl getFaultName(String paramString);
  
  protected abstract boolean isFault(SOAPElement paramSOAPElement);
  
  protected abstract SOAPBodyElement createBodyElement(Name paramName);
  
  protected abstract SOAPBodyElement createBodyElement(QName paramQName);
  
  protected abstract SOAPFault createFaultElement();
  
  protected abstract QName getDefaultFaultCode();
  
  public SOAPFault addFault() {
    if (hasFault()) {
      log.severe("SAAJ0110.impl.fault.already.exists");
      throw new SOAPExceptionImpl("Error: Fault already exists");
    } 
    this.fault = createFaultElement();
    addNode(this.fault);
    this.fault.setFaultCode(getDefaultFaultCode());
    this.fault.setFaultString("Fault string, and possibly fault code, not set");
    return this.fault;
  }
  
  public SOAPFault addFault(Name paramName, String paramString, Locale paramLocale) throws SOAPException {
    SOAPFault sOAPFault = addFault();
    sOAPFault.setFaultCode(paramName);
    sOAPFault.setFaultString(paramString, paramLocale);
    return sOAPFault;
  }
  
  public SOAPFault addFault(QName paramQName, String paramString, Locale paramLocale) throws SOAPException {
    SOAPFault sOAPFault = addFault();
    sOAPFault.setFaultCode(paramQName);
    sOAPFault.setFaultString(paramString, paramLocale);
    return sOAPFault;
  }
  
  public SOAPFault addFault(Name paramName, String paramString) throws SOAPException {
    SOAPFault sOAPFault = addFault();
    sOAPFault.setFaultCode(paramName);
    sOAPFault.setFaultString(paramString);
    return sOAPFault;
  }
  
  public SOAPFault addFault(QName paramQName, String paramString) throws SOAPException {
    SOAPFault sOAPFault = addFault();
    sOAPFault.setFaultCode(paramQName);
    sOAPFault.setFaultString(paramString);
    return sOAPFault;
  }
  
  void initializeFault() {
    FaultImpl faultImpl = (FaultImpl)findFault();
    this.fault = faultImpl;
  }
  
  protected SOAPElement findFault() {
    Iterator iterator = getChildElementNodes();
    while (iterator.hasNext()) {
      SOAPElement sOAPElement = (SOAPElement)iterator.next();
      if (isFault(sOAPElement))
        return sOAPElement; 
    } 
    return null;
  }
  
  public boolean hasFault() {
    initializeFault();
    return (this.fault != null);
  }
  
  public SOAPFault getFault() { return hasFault() ? this.fault : null; }
  
  public SOAPBodyElement addBodyElement(Name paramName) {
    SOAPBodyElement sOAPBodyElement = (SOAPBodyElement)ElementFactory.createNamedElement(((SOAPDocument)getOwnerDocument()).getDocument(), paramName.getLocalName(), paramName.getPrefix(), paramName.getURI());
    if (sOAPBodyElement == null)
      sOAPBodyElement = createBodyElement(paramName); 
    addNode(sOAPBodyElement);
    return sOAPBodyElement;
  }
  
  public SOAPBodyElement addBodyElement(QName paramQName) {
    SOAPBodyElement sOAPBodyElement = (SOAPBodyElement)ElementFactory.createNamedElement(((SOAPDocument)getOwnerDocument()).getDocument(), paramQName.getLocalPart(), paramQName.getPrefix(), paramQName.getNamespaceURI());
    if (sOAPBodyElement == null)
      sOAPBodyElement = createBodyElement(paramQName); 
    addNode(sOAPBodyElement);
    return sOAPBodyElement;
  }
  
  public void setParentElement(SOAPElement paramSOAPElement) throws SOAPException {
    if (!(paramSOAPElement instanceof javax.xml.soap.SOAPEnvelope)) {
      log.severe("SAAJ0111.impl.body.parent.must.be.envelope");
      throw new SOAPException("Parent of SOAPBody has to be a SOAPEnvelope");
    } 
    super.setParentElement(paramSOAPElement);
  }
  
  protected SOAPElement addElement(Name paramName) throws SOAPException { return addBodyElement(paramName); }
  
  protected SOAPElement addElement(QName paramQName) throws SOAPException { return addBodyElement(paramQName); }
  
  public SOAPBodyElement addDocument(Document paramDocument) throws SOAPException {
    SOAPBodyElement sOAPBodyElement = null;
    DocumentFragment documentFragment = paramDocument.createDocumentFragment();
    Element element = paramDocument.getDocumentElement();
    if (element != null) {
      documentFragment.appendChild(element);
      Document document = getOwnerDocument();
      Node node = document.importNode(documentFragment, true);
      addNode(node);
      Iterator iterator = getChildElements(NameImpl.copyElementName(element));
      while (iterator.hasNext())
        sOAPBodyElement = (SOAPBodyElement)iterator.next(); 
    } 
    return sOAPBodyElement;
  }
  
  protected SOAPElement convertToSoapElement(Element paramElement) { return (paramElement instanceof SOAPBodyElement && !paramElement.getClass().equals(ElementImpl.class)) ? (SOAPElement)paramElement : replaceElementWithSOAPElement(paramElement, (ElementImpl)createBodyElement(NameImpl.copyElementName(paramElement))); }
  
  public SOAPElement setElementQName(QName paramQName) throws SOAPException {
    log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[] { this.elementQName.getLocalPart(), paramQName.getLocalPart() });
    throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + paramQName.getLocalPart());
  }
  
  public Document extractContentAsDocument() throws SOAPException {
    Iterator iterator = getChildElements();
    Node node;
    for (node = null; iterator.hasNext() && !(node instanceof SOAPElement); node = (Node)iterator.next());
    boolean bool = true;
    if (node == null) {
      bool = false;
    } else {
      for (Node node1 = node.getNextSibling(); node1 != null; node1 = node1.getNextSibling()) {
        if (node1 instanceof Element) {
          bool = false;
          break;
        } 
      } 
    } 
    if (!bool) {
      log.log(Level.SEVERE, "SAAJ0250.impl.body.should.have.exactly.one.child");
      throw new SOAPException("Cannot extract Document from body");
    } 
    Document document = null;
    try {
      DocumentBuilderFactoryImpl documentBuilderFactoryImpl = new DocumentBuilderFactoryImpl();
      documentBuilderFactoryImpl.setNamespaceAware(true);
      DocumentBuilder documentBuilder = documentBuilderFactoryImpl.newDocumentBuilder();
      document = documentBuilder.newDocument();
      Element element = (Element)document.importNode(node, true);
      document.appendChild(element);
    } catch (Exception exception) {
      log.log(Level.SEVERE, "SAAJ0251.impl.cannot.extract.document.from.body");
      throw new SOAPExceptionImpl("Unable to extract Document from body", exception);
    } 
    node.detachNode();
    return document;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\impl\BodyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */