package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;

public abstract class HeaderElementImpl extends ElementImpl implements SOAPHeaderElement {
  protected static Name RELAY_ATTRIBUTE_LOCAL_NAME = NameImpl.createFromTagName("relay");
  
  protected static Name MUST_UNDERSTAND_ATTRIBUTE_LOCAL_NAME = NameImpl.createFromTagName("mustUnderstand");
  
  Name actorAttNameWithoutNS = NameImpl.createFromTagName("actor");
  
  Name roleAttNameWithoutNS = NameImpl.createFromTagName("role");
  
  public HeaderElementImpl(SOAPDocumentImpl paramSOAPDocumentImpl, Name paramName) { super(paramSOAPDocumentImpl, paramName); }
  
  public HeaderElementImpl(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName) { super(paramSOAPDocumentImpl, paramQName); }
  
  protected abstract NameImpl getActorAttributeName();
  
  protected abstract NameImpl getRoleAttributeName();
  
  protected abstract NameImpl getMustunderstandAttributeName();
  
  protected abstract boolean getMustunderstandAttributeValue(String paramString);
  
  protected abstract String getMustunderstandLiteralValue(boolean paramBoolean);
  
  protected abstract NameImpl getRelayAttributeName();
  
  protected abstract boolean getRelayAttributeValue(String paramString);
  
  protected abstract String getRelayLiteralValue(boolean paramBoolean);
  
  protected abstract String getActorOrRole();
  
  public void setParentElement(SOAPElement paramSOAPElement) throws SOAPException {
    if (!(paramSOAPElement instanceof javax.xml.soap.SOAPHeader)) {
      log.severe("SAAJ0130.impl.header.elem.parent.mustbe.header");
      throw new SOAPException("Parent of a SOAPHeaderElement has to be a SOAPHeader");
    } 
    super.setParentElement(paramSOAPElement);
  }
  
  public void setActor(String paramString) {
    try {
      removeAttribute(getActorAttributeName());
      addAttribute(getActorAttributeName(), paramString);
    } catch (SOAPException sOAPException) {}
  }
  
  public void setRole(String paramString) {
    removeAttribute(getRoleAttributeName());
    addAttribute(getRoleAttributeName(), paramString);
  }
  
  public String getActor() { return getAttributeValue(getActorAttributeName()); }
  
  public String getRole() { return getAttributeValue(getRoleAttributeName()); }
  
  public void setMustUnderstand(boolean paramBoolean) {
    try {
      removeAttribute(getMustunderstandAttributeName());
      addAttribute(getMustunderstandAttributeName(), getMustunderstandLiteralValue(paramBoolean));
    } catch (SOAPException sOAPException) {}
  }
  
  public boolean getMustUnderstand() {
    String str = getAttributeValue(getMustunderstandAttributeName());
    return (str != null) ? getMustunderstandAttributeValue(str) : 0;
  }
  
  public void setRelay(boolean paramBoolean) {
    removeAttribute(getRelayAttributeName());
    addAttribute(getRelayAttributeName(), getRelayLiteralValue(paramBoolean));
  }
  
  public boolean getRelay() {
    String str = getAttributeValue(getRelayAttributeName());
    return (str != null) ? getRelayAttributeValue(str) : 0;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\impl\HeaderElementImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */