package com.sun.xml.internal.fastinfoset.stax.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;

public class EndElementEvent extends EventBase implements EndElement {
  List _namespaces = null;
  
  QName _qname;
  
  public void reset() {
    if (this._namespaces != null)
      this._namespaces.clear(); 
  }
  
  public EndElementEvent() { setEventType(2); }
  
  public EndElementEvent(String paramString1, String paramString2, String paramString3) {
    this._qname = getQName(paramString2, paramString3, paramString1);
    setEventType(2);
  }
  
  public EndElementEvent(QName paramQName) {
    this._qname = paramQName;
    setEventType(2);
  }
  
  public QName getName() { return this._qname; }
  
  public void setName(QName paramQName) { this._qname = paramQName; }
  
  public Iterator getNamespaces() { return (this._namespaces != null) ? this._namespaces.iterator() : EmptyIterator.getInstance(); }
  
  public void addNamespace(Namespace paramNamespace) {
    if (this._namespaces == null)
      this._namespaces = new ArrayList(); 
    this._namespaces.add(paramNamespace);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("</").append(nameAsString());
    Iterator iterator = getNamespaces();
    while (iterator.hasNext())
      stringBuffer.append(" ").append(iterator.next().toString()); 
    stringBuffer.append(">");
    return stringBuffer.toString();
  }
  
  private String nameAsString() { return "".equals(this._qname.getNamespaceURI()) ? this._qname.getLocalPart() : ((this._qname.getPrefix() != null) ? ("['" + this._qname.getNamespaceURI() + "']:" + this._qname.getPrefix() + ":" + this._qname.getLocalPart()) : ("['" + this._qname.getNamespaceURI() + "']:" + this._qname.getLocalPart())); }
  
  private QName getQName(String paramString1, String paramString2, String paramString3) {
    QName qName = null;
    if (paramString3 != null && paramString1 != null) {
      qName = new QName(paramString1, paramString2, paramString3);
    } else if (paramString3 == null && paramString1 != null) {
      qName = new QName(paramString1, paramString2);
    } else if (paramString3 == null && paramString1 == null) {
      qName = new QName(paramString2);
    } 
    return qName;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\EndElementEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */