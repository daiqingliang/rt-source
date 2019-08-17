package com.sun.xml.internal.bind.v2.runtime;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public final class AssociationMap<XmlNode> extends Object {
  private final Map<XmlNode, Entry<XmlNode>> byElement = new IdentityHashMap();
  
  private final Map<Object, Entry<XmlNode>> byPeer = new IdentityHashMap();
  
  private final Set<XmlNode> usedNodes = new HashSet();
  
  public void addInner(XmlNode paramXmlNode, Object paramObject) {
    Entry entry1 = (Entry)this.byElement.get(paramXmlNode);
    if (entry1 != null) {
      if (entry1.inner != null)
        this.byPeer.remove(entry1.inner); 
      entry1.inner = paramObject;
    } else {
      entry1.element = paramXmlNode;
      entry1.inner = paramObject;
    } 
    this.byElement.put(paramXmlNode, entry1);
    Entry entry2 = (Entry)this.byPeer.put(paramObject, entry1);
    if (entry2 != null) {
      if (entry2.outer != null)
        this.byPeer.remove(entry2.outer); 
      if (entry2.element != null)
        this.byElement.remove(entry2.element); 
    } 
  }
  
  public void addOuter(XmlNode paramXmlNode, Object paramObject) {
    Entry entry1 = (Entry)this.byElement.get(paramXmlNode);
    if (entry1 != null) {
      if (entry1.outer != null)
        this.byPeer.remove(entry1.outer); 
      entry1.outer = paramObject;
    } else {
      entry1.element = paramXmlNode;
      entry1.outer = paramObject;
    } 
    this.byElement.put(paramXmlNode, entry1);
    Entry entry2 = (Entry)this.byPeer.put(paramObject, entry1);
    if (entry2 != null) {
      entry2.outer = null;
      if (entry2.inner == null)
        this.byElement.remove(entry2.element); 
    } 
  }
  
  public void addUsed(XmlNode paramXmlNode) { this.usedNodes.add(paramXmlNode); }
  
  public Entry<XmlNode> byElement(Object paramObject) { return (Entry)this.byElement.get(paramObject); }
  
  public Entry<XmlNode> byPeer(Object paramObject) { return (Entry)this.byPeer.get(paramObject); }
  
  public Object getInnerPeer(XmlNode paramXmlNode) {
    Entry entry = byElement(paramXmlNode);
    return (entry == null) ? null : entry.inner;
  }
  
  public Object getOuterPeer(XmlNode paramXmlNode) {
    Entry entry = byElement(paramXmlNode);
    return (entry == null) ? null : entry.outer;
  }
  
  static final class Entry<XmlNode> extends Object {
    private XmlNode element;
    
    private Object inner;
    
    private Object outer;
    
    public XmlNode element() { return (XmlNode)this.element; }
    
    public Object inner() { return this.inner; }
    
    public Object outer() { return this.outer; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\AssociationMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */