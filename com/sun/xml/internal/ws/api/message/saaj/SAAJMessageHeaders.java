package com.sun.xml.internal.ws.api.message.saaj;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.message.saaj.SAAJHeader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

public class SAAJMessageHeaders implements MessageHeaders {
  SOAPMessage sm;
  
  Map<SOAPHeaderElement, Header> nonSAAJHeaders;
  
  Map<QName, Integer> notUnderstoodCount;
  
  SOAPVersion soapVersion;
  
  private Set<QName> understoodHeaders;
  
  public SAAJMessageHeaders(SOAPMessage paramSOAPMessage, SOAPVersion paramSOAPVersion) {
    this.sm = paramSOAPMessage;
    this.soapVersion = paramSOAPVersion;
    initHeaderUnderstanding();
  }
  
  private void initHeaderUnderstanding() {
    SOAPHeader sOAPHeader = ensureSOAPHeader();
    if (sOAPHeader == null)
      return; 
    Iterator iterator = sOAPHeader.examineAllHeaderElements();
    while (iterator.hasNext()) {
      SOAPHeaderElement sOAPHeaderElement = (SOAPHeaderElement)iterator.next();
      if (sOAPHeaderElement != null && sOAPHeaderElement.getMustUnderstand())
        notUnderstood(sOAPHeaderElement.getElementQName()); 
    } 
  }
  
  public void understood(Header paramHeader) { understood(paramHeader.getNamespaceURI(), paramHeader.getLocalPart()); }
  
  public void understood(String paramString1, String paramString2) { understood(new QName(paramString1, paramString2)); }
  
  public void understood(QName paramQName) {
    if (this.notUnderstoodCount == null)
      this.notUnderstoodCount = new HashMap(); 
    Integer integer = (Integer)this.notUnderstoodCount.get(paramQName);
    if (integer != null && integer.intValue() > 0) {
      integer = Integer.valueOf(integer.intValue() - 1);
      if (integer.intValue() <= 0) {
        this.notUnderstoodCount.remove(paramQName);
      } else {
        this.notUnderstoodCount.put(paramQName, integer);
      } 
    } 
    if (this.understoodHeaders == null)
      this.understoodHeaders = new HashSet(); 
    this.understoodHeaders.add(paramQName);
  }
  
  public boolean isUnderstood(Header paramHeader) { return isUnderstood(paramHeader.getNamespaceURI(), paramHeader.getLocalPart()); }
  
  public boolean isUnderstood(String paramString1, String paramString2) { return isUnderstood(new QName(paramString1, paramString2)); }
  
  public boolean isUnderstood(QName paramQName) { return (this.understoodHeaders == null) ? false : this.understoodHeaders.contains(paramQName); }
  
  public boolean isUnderstood(int paramInt) { return false; }
  
  public Header get(String paramString1, String paramString2, boolean paramBoolean) {
    SOAPHeaderElement sOAPHeaderElement = find(paramString1, paramString2);
    if (sOAPHeaderElement != null) {
      if (paramBoolean)
        understood(paramString1, paramString2); 
      return new SAAJHeader(sOAPHeaderElement);
    } 
    return null;
  }
  
  public Header get(QName paramQName, boolean paramBoolean) { return get(paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramBoolean); }
  
  public Iterator<Header> getHeaders(QName paramQName, boolean paramBoolean) { return getHeaders(paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramBoolean); }
  
  public Iterator<Header> getHeaders(String paramString1, String paramString2, boolean paramBoolean) {
    SOAPHeader sOAPHeader = ensureSOAPHeader();
    if (sOAPHeader == null)
      return null; 
    Iterator iterator = sOAPHeader.examineAllHeaderElements();
    if (paramBoolean) {
      ArrayList arrayList = new ArrayList();
      while (iterator.hasNext()) {
        SOAPHeaderElement sOAPHeaderElement = (SOAPHeaderElement)iterator.next();
        if (sOAPHeaderElement != null && sOAPHeaderElement.getNamespaceURI().equals(paramString1) && (paramString2 == null || sOAPHeaderElement.getLocalName().equals(paramString2))) {
          understood(sOAPHeaderElement.getNamespaceURI(), sOAPHeaderElement.getLocalName());
          arrayList.add(new SAAJHeader(sOAPHeaderElement));
        } 
      } 
      return arrayList.iterator();
    } 
    return new HeaderReadIterator(iterator, paramString1, paramString2);
  }
  
  public Iterator<Header> getHeaders(String paramString, boolean paramBoolean) { return getHeaders(paramString, null, paramBoolean); }
  
  public boolean add(Header paramHeader) {
    try {
      paramHeader.writeTo(this.sm);
    } catch (SOAPException sOAPException) {
      return false;
    } 
    notUnderstood(new QName(paramHeader.getNamespaceURI(), paramHeader.getLocalPart()));
    if (isNonSAAJHeader(paramHeader))
      addNonSAAJHeader(find(paramHeader.getNamespaceURI(), paramHeader.getLocalPart()), paramHeader); 
    return true;
  }
  
  public Header remove(QName paramQName) { return remove(paramQName.getNamespaceURI(), paramQName.getLocalPart()); }
  
  public Header remove(String paramString1, String paramString2) {
    SOAPHeader sOAPHeader = ensureSOAPHeader();
    if (sOAPHeader == null)
      return null; 
    SOAPHeaderElement sOAPHeaderElement = find(paramString1, paramString2);
    if (sOAPHeaderElement == null)
      return null; 
    sOAPHeaderElement = (SOAPHeaderElement)sOAPHeader.removeChild(sOAPHeaderElement);
    removeNonSAAJHeader(sOAPHeaderElement);
    QName qName = (paramString1 == null) ? new QName(paramString2) : new QName(paramString1, paramString2);
    if (this.understoodHeaders != null)
      this.understoodHeaders.remove(qName); 
    removeNotUnderstood(qName);
    return new SAAJHeader(sOAPHeaderElement);
  }
  
  private void removeNotUnderstood(QName paramQName) {
    if (this.notUnderstoodCount == null)
      return; 
    Integer integer = (Integer)this.notUnderstoodCount.get(paramQName);
    if (integer != null) {
      int i = integer.intValue();
      if (--i <= 0)
        this.notUnderstoodCount.remove(paramQName); 
    } 
  }
  
  private SOAPHeaderElement find(QName paramQName) { return find(paramQName.getNamespaceURI(), paramQName.getLocalPart()); }
  
  private SOAPHeaderElement find(String paramString1, String paramString2) {
    SOAPHeader sOAPHeader = ensureSOAPHeader();
    if (sOAPHeader == null)
      return null; 
    Iterator iterator = sOAPHeader.examineAllHeaderElements();
    while (iterator.hasNext()) {
      SOAPHeaderElement sOAPHeaderElement = (SOAPHeaderElement)iterator.next();
      if (sOAPHeaderElement.getNamespaceURI().equals(paramString1) && sOAPHeaderElement.getLocalName().equals(paramString2))
        return sOAPHeaderElement; 
    } 
    return null;
  }
  
  private void notUnderstood(QName paramQName) {
    if (this.notUnderstoodCount == null)
      this.notUnderstoodCount = new HashMap(); 
    Integer integer = (Integer)this.notUnderstoodCount.get(paramQName);
    if (integer == null) {
      this.notUnderstoodCount.put(paramQName, Integer.valueOf(1));
    } else {
      this.notUnderstoodCount.put(paramQName, Integer.valueOf(integer.intValue() + 1));
    } 
    if (this.understoodHeaders != null)
      this.understoodHeaders.remove(paramQName); 
  }
  
  private SOAPHeader ensureSOAPHeader() {
    try {
      SOAPHeader sOAPHeader = this.sm.getSOAPPart().getEnvelope().getHeader();
      return (sOAPHeader != null) ? sOAPHeader : this.sm.getSOAPPart().getEnvelope().addHeader();
    } catch (Exception exception) {
      return null;
    } 
  }
  
  private boolean isNonSAAJHeader(Header paramHeader) { return !(paramHeader instanceof SAAJHeader); }
  
  private void addNonSAAJHeader(SOAPHeaderElement paramSOAPHeaderElement, Header paramHeader) {
    if (this.nonSAAJHeaders == null)
      this.nonSAAJHeaders = new HashMap(); 
    this.nonSAAJHeaders.put(paramSOAPHeaderElement, paramHeader);
  }
  
  private void removeNonSAAJHeader(SOAPHeaderElement paramSOAPHeaderElement) {
    if (this.nonSAAJHeaders != null)
      this.nonSAAJHeaders.remove(paramSOAPHeaderElement); 
  }
  
  public boolean addOrReplace(Header paramHeader) {
    remove(paramHeader.getNamespaceURI(), paramHeader.getLocalPart());
    return add(paramHeader);
  }
  
  public void replace(Header paramHeader1, Header paramHeader2) {
    if (remove(paramHeader1.getNamespaceURI(), paramHeader1.getLocalPart()) == null)
      throw new IllegalArgumentException(); 
    add(paramHeader2);
  }
  
  public Set<QName> getUnderstoodHeaders() { return this.understoodHeaders; }
  
  public Set<QName> getNotUnderstoodHeaders(Set<String> paramSet1, Set<QName> paramSet2, WSBinding paramWSBinding) {
    HashSet hashSet = new HashSet();
    if (this.notUnderstoodCount == null)
      return hashSet; 
    for (QName qName : this.notUnderstoodCount.keySet()) {
      int i = ((Integer)this.notUnderstoodCount.get(qName)).intValue();
      if (i <= 0)
        continue; 
      SOAPHeaderElement sOAPHeaderElement = find(qName);
      if (!sOAPHeaderElement.getMustUnderstand())
        continue; 
      SAAJHeader sAAJHeader = new SAAJHeader(sOAPHeaderElement);
      boolean bool = false;
      if (paramSet1 != null)
        bool = !paramSet1.contains(sAAJHeader.getRole(this.soapVersion)); 
      if (bool)
        continue; 
      if (paramWSBinding != null && paramWSBinding instanceof SOAPBindingImpl) {
        bool = ((SOAPBindingImpl)paramWSBinding).understandsHeader(qName);
        if (!bool && paramSet2 != null && paramSet2.contains(qName))
          bool = true; 
      } 
      if (!bool)
        hashSet.add(qName); 
    } 
    return hashSet;
  }
  
  public Iterator<Header> getHeaders() {
    SOAPHeader sOAPHeader = ensureSOAPHeader();
    if (sOAPHeader == null)
      return null; 
    Iterator iterator = sOAPHeader.examineAllHeaderElements();
    return new HeaderReadIterator(iterator, null, null);
  }
  
  public boolean hasHeaders() {
    SOAPHeader sOAPHeader = ensureSOAPHeader();
    if (sOAPHeader == null)
      return false; 
    Iterator iterator = sOAPHeader.examineAllHeaderElements();
    return iterator.hasNext();
  }
  
  public List<Header> asList() {
    SOAPHeader sOAPHeader = ensureSOAPHeader();
    if (sOAPHeader == null)
      return Collections.emptyList(); 
    Iterator iterator = sOAPHeader.examineAllHeaderElements();
    ArrayList arrayList = new ArrayList();
    while (iterator.hasNext()) {
      SOAPHeaderElement sOAPHeaderElement = (SOAPHeaderElement)iterator.next();
      arrayList.add(new SAAJHeader(sOAPHeaderElement));
    } 
    return arrayList;
  }
  
  private static class HeaderReadIterator extends Object implements Iterator<Header> {
    SOAPHeaderElement current;
    
    Iterator soapHeaders;
    
    String myNsUri;
    
    String myLocalName;
    
    public HeaderReadIterator(Iterator param1Iterator, String param1String1, String param1String2) {
      this.soapHeaders = param1Iterator;
      this.myNsUri = param1String1;
      this.myLocalName = param1String2;
    }
    
    public boolean hasNext() {
      if (this.current == null)
        advance(); 
      return (this.current != null);
    }
    
    public Header next() {
      if (!hasNext())
        return null; 
      if (this.current == null)
        return null; 
      SAAJHeader sAAJHeader = new SAAJHeader(this.current);
      this.current = null;
      return sAAJHeader;
    }
    
    public void remove() { throw new UnsupportedOperationException(); }
    
    private void advance() {
      while (this.soapHeaders.hasNext()) {
        SOAPHeaderElement sOAPHeaderElement = (SOAPHeaderElement)this.soapHeaders.next();
        if (sOAPHeaderElement != null && (this.myNsUri == null || sOAPHeaderElement.getNamespaceURI().equals(this.myNsUri)) && (this.myLocalName == null || sOAPHeaderElement.getLocalName().equals(this.myLocalName))) {
          this.current = sOAPHeaderElement;
          return;
        } 
      } 
      this.current = null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\saaj\SAAJMessageHeaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */