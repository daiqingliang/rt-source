package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocument;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.DetailImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultElementImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.FaultImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFaultElement;

public class Fault1_2Impl extends FaultImpl {
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap.ver1_2", "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");
  
  private static final QName textName = new QName("http://www.w3.org/2003/05/soap-envelope", "Text");
  
  private final QName valueName = new QName("http://www.w3.org/2003/05/soap-envelope", "Value", getPrefix());
  
  private final QName subcodeName = new QName("http://www.w3.org/2003/05/soap-envelope", "Subcode", getPrefix());
  
  private SOAPElement innermostSubCodeElement = null;
  
  public Fault1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString1, String paramString2) { super(paramSOAPDocumentImpl, NameImpl.createFault1_2Name(paramString1, paramString2)); }
  
  public Fault1_2Impl(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString) { super(paramSOAPDocumentImpl, NameImpl.createFault1_2Name(null, paramString)); }
  
  protected NameImpl getDetailName() { return NameImpl.createSOAP12Name("Detail", getPrefix()); }
  
  protected NameImpl getFaultCodeName() { return NameImpl.createSOAP12Name("Code", getPrefix()); }
  
  protected NameImpl getFaultStringName() { return getFaultReasonName(); }
  
  protected NameImpl getFaultActorName() { return getFaultRoleName(); }
  
  private NameImpl getFaultRoleName() { return NameImpl.createSOAP12Name("Role", getPrefix()); }
  
  private NameImpl getFaultReasonName() { return NameImpl.createSOAP12Name("Reason", getPrefix()); }
  
  private NameImpl getFaultReasonTextName() { return NameImpl.createSOAP12Name("Text", getPrefix()); }
  
  private NameImpl getFaultNodeName() { return NameImpl.createSOAP12Name("Node", getPrefix()); }
  
  private static NameImpl getXmlLangName() { return NameImpl.createXmlName("lang"); }
  
  protected DetailImpl createDetail() { return new Detail1_2Impl(((SOAPDocument)getOwnerDocument()).getDocument()); }
  
  protected FaultElementImpl createSOAPFaultElement(String paramString) { return new FaultElement1_2Impl(((SOAPDocument)getOwnerDocument()).getDocument(), paramString); }
  
  protected void checkIfStandardFaultCode(String paramString1, String paramString2) throws SOAPException {
    QName qName = new QName(paramString2, paramString1);
    if (SOAPConstants.SOAP_DATAENCODINGUNKNOWN_FAULT.equals(qName) || SOAPConstants.SOAP_MUSTUNDERSTAND_FAULT.equals(qName) || SOAPConstants.SOAP_RECEIVER_FAULT.equals(qName) || SOAPConstants.SOAP_SENDER_FAULT.equals(qName) || SOAPConstants.SOAP_VERSIONMISMATCH_FAULT.equals(qName))
      return; 
    log.log(Level.SEVERE, "SAAJ0435.ver1_2.code.not.standard", qName);
    throw new SOAPExceptionImpl(qName + " is not a standard Code value");
  }
  
  protected void finallySetFaultCode(String paramString) throws SOAPException {
    SOAPElement sOAPElement = this.faultCodeElement.addChildElement(this.valueName);
    sOAPElement.addTextNode(paramString);
  }
  
  private void findReasonElement() { findFaultStringElement(); }
  
  public Iterator getFaultReasonTexts() throws SOAPException {
    if (this.faultStringElement == null)
      findReasonElement(); 
    Iterator iterator = this.faultStringElement.getChildElements(textName);
    ArrayList arrayList = new ArrayList();
    while (iterator.hasNext()) {
      SOAPElement sOAPElement = (SOAPElement)iterator.next();
      Locale locale = getLocale(sOAPElement);
      if (locale == null) {
        log.severe("SAAJ0431.ver1_2.xml.lang.missing");
        throw new SOAPExceptionImpl("\"xml:lang\" attribute is not present on the Text element");
      } 
      arrayList.add(sOAPElement.getValue());
    } 
    if (arrayList.isEmpty()) {
      log.severe("SAAJ0434.ver1_2.text.element.not.present");
      throw new SOAPExceptionImpl("env:Text must be present inside env:Reason");
    } 
    return arrayList.iterator();
  }
  
  public void addFaultReasonText(String paramString, Locale paramLocale) throws SOAPException {
    SOAPElement sOAPElement;
    if (paramLocale == null) {
      log.severe("SAAJ0430.ver1_2.locale.required");
      throw new SOAPException("locale is required and must not be null");
    } 
    if (this.faultStringElement == null)
      findReasonElement(); 
    if (this.faultStringElement == null) {
      this.faultStringElement = addSOAPFaultElement("Reason");
      sOAPElement = this.faultStringElement.addChildElement(getFaultReasonTextName());
    } else {
      removeDefaultFaultString();
      sOAPElement = getFaultReasonTextElement(paramLocale);
      if (sOAPElement != null) {
        sOAPElement.removeContents();
      } else {
        sOAPElement = this.faultStringElement.addChildElement(getFaultReasonTextName());
      } 
    } 
    String str = localeToXmlLang(paramLocale);
    sOAPElement.addAttribute(getXmlLangName(), str);
    sOAPElement.addTextNode(paramString);
  }
  
  private void removeDefaultFaultString() {
    SOAPElement sOAPElement = getFaultReasonTextElement(Locale.getDefault());
    if (sOAPElement != null) {
      String str = "Fault string, and possibly fault code, not set";
      if (str.equals(sOAPElement.getValue()))
        sOAPElement.detachNode(); 
    } 
  }
  
  public String getFaultReasonText(Locale paramLocale) throws SOAPException {
    if (paramLocale == null)
      return null; 
    if (this.faultStringElement == null)
      findReasonElement(); 
    if (this.faultStringElement != null) {
      SOAPElement sOAPElement = getFaultReasonTextElement(paramLocale);
      if (sOAPElement != null) {
        sOAPElement.normalize();
        return sOAPElement.getFirstChild().getNodeValue();
      } 
    } 
    return null;
  }
  
  public Iterator getFaultReasonLocales() throws SOAPException {
    if (this.faultStringElement == null)
      findReasonElement(); 
    Iterator iterator = this.faultStringElement.getChildElements(textName);
    ArrayList arrayList = new ArrayList();
    while (iterator.hasNext()) {
      SOAPElement sOAPElement = (SOAPElement)iterator.next();
      Locale locale = getLocale(sOAPElement);
      if (locale == null) {
        log.severe("SAAJ0431.ver1_2.xml.lang.missing");
        throw new SOAPExceptionImpl("\"xml:lang\" attribute is not present on the Text element");
      } 
      arrayList.add(locale);
    } 
    if (arrayList.isEmpty()) {
      log.severe("SAAJ0434.ver1_2.text.element.not.present");
      throw new SOAPExceptionImpl("env:Text elements with mandatory xml:lang attributes must be present inside env:Reason");
    } 
    return arrayList.iterator();
  }
  
  public Locale getFaultStringLocale() {
    Locale locale = null;
    try {
      locale = (Locale)getFaultReasonLocales().next();
    } catch (SOAPException sOAPException) {}
    return locale;
  }
  
  private SOAPElement getFaultReasonTextElement(Locale paramLocale) throws SOAPException {
    Iterator iterator = this.faultStringElement.getChildElements(textName);
    while (iterator.hasNext()) {
      SOAPElement sOAPElement = (SOAPElement)iterator.next();
      Locale locale = getLocale(sOAPElement);
      if (locale == null) {
        log.severe("SAAJ0431.ver1_2.xml.lang.missing");
        throw new SOAPExceptionImpl("\"xml:lang\" attribute is not present on the Text element");
      } 
      if (locale.equals(paramLocale))
        return sOAPElement; 
    } 
    return null;
  }
  
  public String getFaultNode() {
    SOAPElement sOAPElement = findChild(getFaultNodeName());
    return (sOAPElement == null) ? null : sOAPElement.getValue();
  }
  
  public void setFaultNode(String paramString) throws SOAPException {
    SOAPElement sOAPElement = findChild(getFaultNodeName());
    if (sOAPElement != null)
      sOAPElement.detachNode(); 
    sOAPElement = createSOAPFaultElement(getFaultNodeName());
    sOAPElement = sOAPElement.addTextNode(paramString);
    if (getFaultRole() != null) {
      insertBefore(sOAPElement, this.faultActorElement);
      return;
    } 
    if (hasDetail()) {
      insertBefore(sOAPElement, this.detail);
      return;
    } 
    addNode(sOAPElement);
  }
  
  public String getFaultRole() { return getFaultActor(); }
  
  public void setFaultRole(String paramString) throws SOAPException {
    if (this.faultActorElement == null)
      findFaultActorElement(); 
    if (this.faultActorElement != null)
      this.faultActorElement.detachNode(); 
    this.faultActorElement = createSOAPFaultElement(getFaultActorName());
    this.faultActorElement.addTextNode(paramString);
    if (hasDetail()) {
      insertBefore(this.faultActorElement, this.detail);
      return;
    } 
    addNode(this.faultActorElement);
  }
  
  public String getFaultCode() {
    if (this.faultCodeElement == null)
      findFaultCodeElement(); 
    Iterator iterator = this.faultCodeElement.getChildElements(this.valueName);
    return ((SOAPElement)iterator.next()).getValue();
  }
  
  public QName getFaultCodeAsQName() {
    String str = getFaultCode();
    if (str == null)
      return null; 
    if (this.faultCodeElement == null)
      findFaultCodeElement(); 
    Iterator iterator = this.faultCodeElement.getChildElements(this.valueName);
    return convertCodeToQName(str, (SOAPElement)iterator.next());
  }
  
  public Name getFaultCodeAsName() {
    String str = getFaultCode();
    if (str == null)
      return null; 
    if (this.faultCodeElement == null)
      findFaultCodeElement(); 
    Iterator iterator = this.faultCodeElement.getChildElements(this.valueName);
    return NameImpl.convertToName(convertCodeToQName(str, (SOAPElement)iterator.next()));
  }
  
  public String getFaultString() {
    String str = null;
    try {
      str = (String)getFaultReasonTexts().next();
    } catch (SOAPException sOAPException) {}
    return str;
  }
  
  public void setFaultString(String paramString) throws SOAPException { addFaultReasonText(paramString, Locale.getDefault()); }
  
  public void setFaultString(String paramString, Locale paramLocale) throws SOAPException { addFaultReasonText(paramString, paramLocale); }
  
  public void appendFaultSubcode(QName paramQName) throws SOAPException {
    if (paramQName == null)
      return; 
    if (paramQName.getNamespaceURI() == null || "".equals(paramQName.getNamespaceURI())) {
      log.severe("SAAJ0432.ver1_2.subcode.not.ns.qualified");
      throw new SOAPExceptionImpl("A Subcode must be namespace-qualified");
    } 
    if (this.innermostSubCodeElement == null) {
      if (this.faultCodeElement == null)
        findFaultCodeElement(); 
      this.innermostSubCodeElement = this.faultCodeElement;
    } 
    String str = null;
    if (paramQName.getPrefix() == null || "".equals(paramQName.getPrefix())) {
      str = ((ElementImpl)this.innermostSubCodeElement).getNamespacePrefix(paramQName.getNamespaceURI());
    } else {
      str = paramQName.getPrefix();
    } 
    if (str == null || "".equals(str))
      str = "ns1"; 
    this.innermostSubCodeElement = this.innermostSubCodeElement.addChildElement(this.subcodeName);
    SOAPElement sOAPElement = this.innermostSubCodeElement.addChildElement(this.valueName);
    ((ElementImpl)sOAPElement).ensureNamespaceIsDeclared(str, paramQName.getNamespaceURI());
    sOAPElement.addTextNode(str + ":" + paramQName.getLocalPart());
  }
  
  public void removeAllFaultSubcodes() {
    if (this.faultCodeElement == null)
      findFaultCodeElement(); 
    Iterator iterator = this.faultCodeElement.getChildElements(this.subcodeName);
    if (iterator.hasNext()) {
      SOAPElement sOAPElement = (SOAPElement)iterator.next();
      sOAPElement.detachNode();
    } 
  }
  
  public Iterator getFaultSubcodes() throws SOAPException {
    if (this.faultCodeElement == null)
      findFaultCodeElement(); 
    final ArrayList subcodeList = new ArrayList();
    SOAPFaultElement sOAPFaultElement = this.faultCodeElement;
    for (Iterator iterator = sOAPFaultElement.getChildElements(this.subcodeName); iterator.hasNext(); iterator = elementImpl.getChildElements(this.subcodeName)) {
      ElementImpl elementImpl = (ElementImpl)iterator.next();
      Iterator iterator1 = elementImpl.getChildElements(this.valueName);
      SOAPElement sOAPElement = (SOAPElement)iterator1.next();
      String str = sOAPElement.getValue();
      arrayList.add(convertCodeToQName(str, sOAPElement));
    } 
    return new Iterator() {
        Iterator subCodeIter = subcodeList.iterator();
        
        public boolean hasNext() { return this.subCodeIter.hasNext(); }
        
        public Object next() { return this.subCodeIter.next(); }
        
        public void remove() { throw new UnsupportedOperationException("Method remove() not supported on SubCodes Iterator"); }
      };
  }
  
  private static Locale getLocale(SOAPElement paramSOAPElement) { return xmlLangToLocale(paramSOAPElement.getAttributeValue(getXmlLangName())); }
  
  public void setEncodingStyle(String paramString) throws SOAPException {
    log.severe("SAAJ0407.ver1_2.no.encodingStyle.in.fault");
    throw new SOAPExceptionImpl("encodingStyle attribute cannot appear on Fault");
  }
  
  public SOAPElement addAttribute(Name paramName, String paramString) throws SOAPException {
    if (paramName.getLocalName().equals("encodingStyle") && paramName.getURI().equals("http://www.w3.org/2003/05/soap-envelope"))
      setEncodingStyle(paramString); 
    return super.addAttribute(paramName, paramString);
  }
  
  public SOAPElement addAttribute(QName paramQName, String paramString) throws SOAPException {
    if (paramQName.getLocalPart().equals("encodingStyle") && paramQName.getNamespaceURI().equals("http://www.w3.org/2003/05/soap-envelope"))
      setEncodingStyle(paramString); 
    return super.addAttribute(paramQName, paramString);
  }
  
  public SOAPElement addTextNode(String paramString) throws SOAPException {
    log.log(Level.SEVERE, "SAAJ0416.ver1_2.adding.text.not.legal", getElementQName());
    throw new SOAPExceptionImpl("Adding text to SOAP 1.2 Fault is not legal");
  }
  
  public SOAPElement addChildElement(SOAPElement paramSOAPElement) throws SOAPException {
    String str = paramSOAPElement.getLocalName();
    if ("Detail".equalsIgnoreCase(str)) {
      if (hasDetail()) {
        log.severe("SAAJ0436.ver1_2.detail.exists.error");
        throw new SOAPExceptionImpl("Cannot add Detail, Detail already exists");
      } 
      String str1 = paramSOAPElement.getElementQName().getNamespaceURI();
      if (!str1.equals("http://www.w3.org/2003/05/soap-envelope")) {
        log.severe("SAAJ0437.ver1_2.version.mismatch.error");
        throw new SOAPExceptionImpl("Cannot add Detail, Incorrect SOAP version specified for Detail element");
      } 
    } 
    if (paramSOAPElement instanceof Detail1_2Impl) {
      ElementImpl elementImpl = (ElementImpl)importElement(paramSOAPElement);
      addNode(elementImpl);
      return convertToSoapElement(elementImpl);
    } 
    return super.addChildElement(paramSOAPElement);
  }
  
  protected boolean isStandardFaultElement(String paramString) { return (paramString.equalsIgnoreCase("code") || paramString.equalsIgnoreCase("reason") || paramString.equalsIgnoreCase("node") || paramString.equalsIgnoreCase("role") || paramString.equalsIgnoreCase("detail")); }
  
  protected QName getDefaultFaultCode() { return SOAPConstants.SOAP_SENDER_FAULT; }
  
  protected FaultElementImpl createSOAPFaultElement(QName paramQName) { return new FaultElement1_2Impl(((SOAPDocument)getOwnerDocument()).getDocument(), paramQName); }
  
  protected FaultElementImpl createSOAPFaultElement(Name paramName) { return new FaultElement1_2Impl(((SOAPDocument)getOwnerDocument()).getDocument(), (NameImpl)paramName); }
  
  public void setFaultActor(String paramString) throws SOAPException { setFaultRole(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_2\Fault1_2Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */