package com.sun.xml.internal.fastinfoset.stax.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;

public class StartElementEvent extends EventBase implements StartElement {
  private Map _attributes;
  
  private List _namespaces;
  
  private NamespaceContext _context = null;
  
  private QName _qname;
  
  public void reset() {
    if (this._attributes != null)
      this._attributes.clear(); 
    if (this._namespaces != null)
      this._namespaces.clear(); 
    if (this._context != null)
      this._context = null; 
  }
  
  public StartElementEvent() { init(); }
  
  public StartElementEvent(String paramString1, String paramString2, String paramString3) {
    init();
    if (paramString2 == null)
      paramString2 = ""; 
    if (paramString1 == null)
      paramString1 = ""; 
    this._qname = new QName(paramString2, paramString3, paramString1);
    setEventType(1);
  }
  
  public StartElementEvent(QName paramQName) {
    init();
    this._qname = paramQName;
  }
  
  public StartElementEvent(StartElement paramStartElement) {
    this(paramStartElement.getName());
    addAttributes(paramStartElement.getAttributes());
    addNamespaces(paramStartElement.getNamespaces());
  }
  
  protected void init() {
    setEventType(1);
    this._attributes = new HashMap();
    this._namespaces = new ArrayList();
  }
  
  public QName getName() { return this._qname; }
  
  public Iterator getAttributes() {
    if (this._attributes != null) {
      Collection collection = this._attributes.values();
      return new ReadIterator(collection.iterator());
    } 
    return EmptyIterator.getInstance();
  }
  
  public Iterator getNamespaces() { return (this._namespaces != null) ? new ReadIterator(this._namespaces.iterator()) : EmptyIterator.getInstance(); }
  
  public Attribute getAttributeByName(QName paramQName) { return (paramQName == null) ? null : (Attribute)this._attributes.get(paramQName); }
  
  public NamespaceContext getNamespaceContext() { return this._context; }
  
  public void setName(QName paramQName) { this._qname = paramQName; }
  
  public String getNamespace() { return this._qname.getNamespaceURI(); }
  
  public String getNamespaceURI(String paramString) { return (getNamespace() != null) ? getNamespace() : ((this._context != null) ? this._context.getNamespaceURI(paramString) : null); }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(64);
    stringBuilder.append('<').append(nameAsString());
    if (this._attributes != null) {
      Iterator iterator = getAttributes();
      Attribute attribute = null;
      while (iterator.hasNext()) {
        attribute = (Attribute)iterator.next();
        stringBuilder.append(' ').append(attribute.toString());
      } 
    } 
    if (this._namespaces != null) {
      Iterator iterator = this._namespaces.iterator();
      Namespace namespace = null;
      while (iterator.hasNext()) {
        namespace = (Namespace)iterator.next();
        stringBuilder.append(' ').append(namespace.toString());
      } 
    } 
    stringBuilder.append('>');
    return stringBuilder.toString();
  }
  
  public String nameAsString() { return "".equals(this._qname.getNamespaceURI()) ? this._qname.getLocalPart() : ((this._qname.getPrefix() != null) ? ("['" + this._qname.getNamespaceURI() + "']:" + this._qname.getPrefix() + ":" + this._qname.getLocalPart()) : ("['" + this._qname.getNamespaceURI() + "']:" + this._qname.getLocalPart())); }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext) { this._context = paramNamespaceContext; }
  
  public void addAttribute(Attribute paramAttribute) { this._attributes.put(paramAttribute.getName(), paramAttribute); }
  
  public void addAttributes(Iterator paramIterator) {
    if (paramIterator != null)
      while (paramIterator.hasNext()) {
        Attribute attribute = (Attribute)paramIterator.next();
        this._attributes.put(attribute.getName(), attribute);
      }  
  }
  
  public void addNamespace(Namespace paramNamespace) {
    if (paramNamespace != null)
      this._namespaces.add(paramNamespace); 
  }
  
  public void addNamespaces(Iterator paramIterator) {
    if (paramIterator != null)
      while (paramIterator.hasNext()) {
        Namespace namespace = (Namespace)paramIterator.next();
        this._namespaces.add(namespace);
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\StartElementEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */