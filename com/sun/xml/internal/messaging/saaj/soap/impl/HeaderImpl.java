package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import org.w3c.dom.Element;

public abstract class HeaderImpl extends ElementImpl implements SOAPHeader {
  protected static final boolean MUST_UNDERSTAND_ONLY = false;
  
  protected HeaderImpl(SOAPDocumentImpl paramSOAPDocumentImpl, NameImpl paramNameImpl) { super(paramSOAPDocumentImpl, paramNameImpl); }
  
  protected abstract SOAPHeaderElement createHeaderElement(Name paramName) throws SOAPException;
  
  protected abstract SOAPHeaderElement createHeaderElement(QName paramQName) throws SOAPException;
  
  protected abstract NameImpl getNotUnderstoodName();
  
  protected abstract NameImpl getUpgradeName();
  
  protected abstract NameImpl getSupportedEnvelopeName();
  
  public SOAPHeaderElement addHeaderElement(Name paramName) throws SOAPException {
    SOAPElement sOAPElement = ElementFactory.createNamedElement(((SOAPDocument)getOwnerDocument()).getDocument(), paramName.getLocalName(), paramName.getPrefix(), paramName.getURI());
    if (sOAPElement == null || !(sOAPElement instanceof SOAPHeaderElement))
      sOAPElement = createHeaderElement(paramName); 
    String str = sOAPElement.getElementQName().getNamespaceURI();
    if (str == null || "".equals(str)) {
      log.severe("SAAJ0131.impl.header.elems.ns.qualified");
      throw new SOAPExceptionImpl("HeaderElements must be namespace qualified");
    } 
    addNode(sOAPElement);
    return (SOAPHeaderElement)sOAPElement;
  }
  
  public SOAPHeaderElement addHeaderElement(QName paramQName) throws SOAPException {
    SOAPElement sOAPElement = ElementFactory.createNamedElement(((SOAPDocument)getOwnerDocument()).getDocument(), paramQName.getLocalPart(), paramQName.getPrefix(), paramQName.getNamespaceURI());
    if (sOAPElement == null || !(sOAPElement instanceof SOAPHeaderElement))
      sOAPElement = createHeaderElement(paramQName); 
    String str = sOAPElement.getElementQName().getNamespaceURI();
    if (str == null || "".equals(str)) {
      log.severe("SAAJ0131.impl.header.elems.ns.qualified");
      throw new SOAPExceptionImpl("HeaderElements must be namespace qualified");
    } 
    addNode(sOAPElement);
    return (SOAPHeaderElement)sOAPElement;
  }
  
  protected SOAPElement addElement(Name paramName) throws SOAPException { return addHeaderElement(paramName); }
  
  protected SOAPElement addElement(QName paramQName) throws SOAPException { return addHeaderElement(paramQName); }
  
  public Iterator examineHeaderElements(String paramString) { return getHeaderElementsForActor(paramString, false, false); }
  
  public Iterator extractHeaderElements(String paramString) { return getHeaderElementsForActor(paramString, true, false); }
  
  protected Iterator getHeaderElementsForActor(String paramString, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramString == null || paramString.equals("")) {
      log.severe("SAAJ0132.impl.invalid.value.for.actor.or.role");
      throw new IllegalArgumentException("Invalid value for actor or role");
    } 
    return getHeaderElements(paramString, paramBoolean1, paramBoolean2);
  }
  
  protected Iterator getHeaderElements(String paramString, boolean paramBoolean1, boolean paramBoolean2) {
    ArrayList arrayList = new ArrayList();
    Iterator iterator = getChildElements();
    Object object = iterate(iterator);
    while (object != null) {
      if (!(object instanceof SOAPHeaderElement)) {
        object = iterate(iterator);
        continue;
      } 
      HeaderElementImpl headerElementImpl = (HeaderElementImpl)object;
      object = iterate(iterator);
      boolean bool1 = (!paramBoolean2 || headerElementImpl.getMustUnderstand()) ? 1 : 0;
      boolean bool2 = false;
      if (paramString == null && bool1) {
        bool2 = true;
      } else {
        String str = headerElementImpl.getActorOrRole();
        if (str == null)
          str = ""; 
        if (str.equalsIgnoreCase(paramString) && bool1)
          bool2 = true; 
      } 
      if (bool2) {
        arrayList.add(headerElementImpl);
        if (paramBoolean1)
          headerElementImpl.detachNode(); 
      } 
    } 
    return arrayList.listIterator();
  }
  
  private Object iterate(Iterator paramIterator) { return paramIterator.hasNext() ? paramIterator.next() : null; }
  
  public void setParentElement(SOAPElement paramSOAPElement) throws SOAPException {
    if (!(paramSOAPElement instanceof javax.xml.soap.SOAPEnvelope)) {
      log.severe("SAAJ0133.impl.header.parent.mustbe.envelope");
      throw new SOAPException("Parent of SOAPHeader has to be a SOAPEnvelope");
    } 
    super.setParentElement(paramSOAPElement);
  }
  
  public SOAPElement addChildElement(String paramString) throws SOAPException {
    SOAPElement sOAPElement = super.addChildElement(paramString);
    String str = sOAPElement.getElementName().getURI();
    if (str == null || "".equals(str)) {
      log.severe("SAAJ0134.impl.header.elems.ns.qualified");
      throw new SOAPExceptionImpl("HeaderElements must be namespace qualified");
    } 
    return sOAPElement;
  }
  
  public Iterator examineAllHeaderElements() { return getHeaderElements(null, false, false); }
  
  public Iterator examineMustUnderstandHeaderElements(String paramString) { return getHeaderElements(paramString, false, true); }
  
  public Iterator extractAllHeaderElements() { return getHeaderElements(null, true, false); }
  
  public SOAPHeaderElement addUpgradeHeaderElement(Iterator paramIterator) throws SOAPException {
    if (paramIterator == null) {
      log.severe("SAAJ0411.ver1_2.no.null.supportedURIs");
      throw new SOAPException("Argument cannot be null; iterator of supportedURIs cannot be null");
    } 
    if (!paramIterator.hasNext()) {
      log.severe("SAAJ0412.ver1_2.no.empty.list.of.supportedURIs");
      throw new SOAPException("List of supported URIs cannot be empty");
    } 
    NameImpl nameImpl1 = getUpgradeName();
    SOAPHeaderElement sOAPHeaderElement = (SOAPHeaderElement)addChildElement(nameImpl1);
    NameImpl nameImpl2 = getSupportedEnvelopeName();
    for (byte b = 0; paramIterator.hasNext(); b++) {
      SOAPElement sOAPElement = sOAPHeaderElement.addChildElement(nameImpl2);
      String str = "ns" + Integer.toString(b);
      sOAPElement.addAttribute(NameImpl.createFromUnqualifiedName("qname"), str + ":Envelope");
      sOAPElement.addNamespaceDeclaration(str, (String)paramIterator.next());
    } 
    return sOAPHeaderElement;
  }
  
  public SOAPHeaderElement addUpgradeHeaderElement(String paramString) throws SOAPException { return addUpgradeHeaderElement(new String[] { paramString }); }
  
  public SOAPHeaderElement addUpgradeHeaderElement(String[] paramArrayOfString) throws SOAPException {
    if (paramArrayOfString == null) {
      log.severe("SAAJ0411.ver1_2.no.null.supportedURIs");
      throw new SOAPException("Argument cannot be null; array of supportedURIs cannot be null");
    } 
    if (paramArrayOfString.length == 0) {
      log.severe("SAAJ0412.ver1_2.no.empty.list.of.supportedURIs");
      throw new SOAPException("List of supported URIs cannot be empty");
    } 
    NameImpl nameImpl1 = getUpgradeName();
    SOAPHeaderElement sOAPHeaderElement = (SOAPHeaderElement)addChildElement(nameImpl1);
    NameImpl nameImpl2 = getSupportedEnvelopeName();
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      SOAPElement sOAPElement = sOAPHeaderElement.addChildElement(nameImpl2);
      String str = "ns" + Integer.toString(b);
      sOAPElement.addAttribute(NameImpl.createFromUnqualifiedName("qname"), str + ":Envelope");
      sOAPElement.addNamespaceDeclaration(str, paramArrayOfString[b]);
    } 
    return sOAPHeaderElement;
  }
  
  protected SOAPElement convertToSoapElement(Element paramElement) {
    SOAPHeaderElement sOAPHeaderElement;
    if (paramElement instanceof SOAPHeaderElement)
      return (SOAPElement)paramElement; 
    try {
      sOAPHeaderElement = createHeaderElement(NameImpl.copyElementName(paramElement));
    } catch (SOAPException sOAPException) {
      throw new ClassCastException("Could not convert Element to SOAPHeaderElement: " + sOAPException.getMessage());
    } 
    return replaceElementWithSOAPElement(paramElement, (ElementImpl)sOAPHeaderElement);
  }
  
  public SOAPElement setElementQName(QName paramQName) throws SOAPException {
    log.log(Level.SEVERE, "SAAJ0146.impl.invalid.name.change.requested", new Object[] { this.elementQName.getLocalPart(), paramQName.getLocalPart() });
    throw new SOAPException("Cannot change name for " + this.elementQName.getLocalPart() + " to " + paramQName.getLocalPart());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\impl\HeaderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */