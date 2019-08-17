package com.sun.xml.internal.stream.events;

import com.sun.xml.internal.stream.util.ReadOnlyIterator;
import java.io.IOException;
import java.io.Writer;
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

public class StartElementEvent extends DummyEvent implements StartElement {
  private Map fAttributes;
  
  private List fNamespaces;
  
  private NamespaceContext fNamespaceContext = null;
  
  private QName fQName;
  
  public StartElementEvent(String paramString1, String paramString2, String paramString3) { this(new QName(paramString2, paramString3, paramString1)); }
  
  public StartElementEvent(QName paramQName) {
    this.fQName = paramQName;
    init();
  }
  
  public StartElementEvent(StartElement paramStartElement) {
    this(paramStartElement.getName());
    addAttributes(paramStartElement.getAttributes());
    addNamespaceAttributes(paramStartElement.getNamespaces());
  }
  
  protected void init() {
    setEventType(1);
    this.fAttributes = new HashMap();
    this.fNamespaces = new ArrayList();
  }
  
  public QName getName() { return this.fQName; }
  
  public void setName(QName paramQName) { this.fQName = paramQName; }
  
  public Iterator getAttributes() {
    if (this.fAttributes != null) {
      Collection collection = this.fAttributes.values();
      return new ReadOnlyIterator(collection.iterator());
    } 
    return new ReadOnlyIterator();
  }
  
  public Iterator getNamespaces() { return (this.fNamespaces != null) ? new ReadOnlyIterator(this.fNamespaces.iterator()) : new ReadOnlyIterator(); }
  
  public Attribute getAttributeByName(QName paramQName) { return (paramQName == null) ? null : (Attribute)this.fAttributes.get(paramQName); }
  
  public String getNamespace() { return this.fQName.getNamespaceURI(); }
  
  public String getNamespaceURI(String paramString) { return (getNamespace() != null && this.fQName.getPrefix().equals(paramString)) ? getNamespace() : ((this.fNamespaceContext != null) ? this.fNamespaceContext.getNamespaceURI(paramString) : null); }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("<");
    stringBuffer.append(nameAsString());
    if (this.fAttributes != null) {
      Iterator iterator = getAttributes();
      Attribute attribute = null;
      while (iterator.hasNext()) {
        attribute = (Attribute)iterator.next();
        stringBuffer.append(" ");
        stringBuffer.append(attribute.toString());
      } 
    } 
    if (this.fNamespaces != null) {
      Iterator iterator = this.fNamespaces.iterator();
      Namespace namespace = null;
      while (iterator.hasNext()) {
        namespace = (Namespace)iterator.next();
        stringBuffer.append(" ");
        stringBuffer.append(namespace.toString());
      } 
    } 
    stringBuffer.append(">");
    return stringBuffer.toString();
  }
  
  public String nameAsString() { return "".equals(this.fQName.getNamespaceURI()) ? this.fQName.getLocalPart() : ((this.fQName.getPrefix() != null) ? ("['" + this.fQName.getNamespaceURI() + "']:" + this.fQName.getPrefix() + ":" + this.fQName.getLocalPart()) : ("['" + this.fQName.getNamespaceURI() + "']:" + this.fQName.getLocalPart())); }
  
  public NamespaceContext getNamespaceContext() { return this.fNamespaceContext; }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext) { this.fNamespaceContext = paramNamespaceContext; }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter) throws IOException { paramWriter.write(toString()); }
  
  void addAttribute(Attribute paramAttribute) {
    if (paramAttribute.isNamespace()) {
      this.fNamespaces.add(paramAttribute);
    } else {
      this.fAttributes.put(paramAttribute.getName(), paramAttribute);
    } 
  }
  
  void addAttributes(Iterator paramIterator) {
    if (paramIterator == null)
      return; 
    while (paramIterator.hasNext()) {
      Attribute attribute = (Attribute)paramIterator.next();
      this.fAttributes.put(attribute.getName(), attribute);
    } 
  }
  
  void addNamespaceAttribute(Namespace paramNamespace) {
    if (paramNamespace == null)
      return; 
    this.fNamespaces.add(paramNamespace);
  }
  
  void addNamespaceAttributes(Iterator paramIterator) {
    if (paramIterator == null)
      return; 
    while (paramIterator.hasNext()) {
      Namespace namespace = (Namespace)paramIterator.next();
      this.fNamespaces.add(namespace);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\events\StartElementEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */