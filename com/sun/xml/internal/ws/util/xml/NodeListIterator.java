package com.sun.xml.internal.ws.util.xml;

import java.util.Iterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListIterator implements Iterator {
  protected NodeList _list;
  
  protected int _index;
  
  public NodeListIterator(NodeList paramNodeList) {
    this._list = paramNodeList;
    this._index = 0;
  }
  
  public boolean hasNext() { return (this._list == null) ? false : ((this._index < this._list.getLength())); }
  
  public Object next() {
    Node node = this._list.item(this._index);
    if (node != null)
      this._index++; 
    return node;
  }
  
  public void remove() { throw new UnsupportedOperationException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\xml\NodeListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */