package com.sun.xml.internal.messaging.saaj.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamespaceContextIterator implements Iterator {
  Node context;
  
  NamedNodeMap attributes = null;
  
  int attributesLength;
  
  int attributeIndex;
  
  Attr next = null;
  
  Attr last = null;
  
  boolean traverseStack = true;
  
  public NamespaceContextIterator(Node paramNode) {
    this.context = paramNode;
    findContextAttributes();
  }
  
  public NamespaceContextIterator(Node paramNode, boolean paramBoolean) {
    this(paramNode);
    this.traverseStack = paramBoolean;
  }
  
  protected void findContextAttributes() {
    while (this.context != null) {
      short s = this.context.getNodeType();
      if (s == 1) {
        this.attributes = this.context.getAttributes();
        this.attributesLength = this.attributes.getLength();
        this.attributeIndex = 0;
        return;
      } 
      this.context = null;
    } 
  }
  
  protected void findNext() {
    while (this.next == null && this.context != null) {
      while (this.attributeIndex < this.attributesLength) {
        Node node = this.attributes.item(this.attributeIndex);
        String str = node.getNodeName();
        if (str.startsWith("xmlns") && (str.length() == 5 || str.charAt(5) == ':')) {
          this.next = (Attr)node;
          this.attributeIndex++;
          return;
        } 
        this.attributeIndex++;
      } 
      if (this.traverseStack) {
        this.context = this.context.getParentNode();
        findContextAttributes();
        continue;
      } 
      this.context = null;
    } 
  }
  
  public boolean hasNext() {
    findNext();
    return (this.next != null);
  }
  
  public Object next() { return getNext(); }
  
  public Attr nextNamespaceAttr() { return getNext(); }
  
  protected Attr getNext() {
    findNext();
    if (this.next == null)
      throw new NoSuchElementException(); 
    this.last = this.next;
    this.next = null;
    return this.last;
  }
  
  public void remove() {
    if (this.last == null)
      throw new IllegalStateException(); 
    ((Element)this.context).removeAttributeNode(this.last);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saa\\util\NamespaceContextIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */