package com.sun.xml.internal.ws.util.xml;

import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamedNodeMapIterator implements Iterator {
  protected NamedNodeMap _map;
  
  protected int _index;
  
  public NamedNodeMapIterator(NamedNodeMap paramNamedNodeMap) {
    this._map = paramNamedNodeMap;
    this._index = 0;
  }
  
  public boolean hasNext() { return (this._map == null) ? false : ((this._index < this._map.getLength())); }
  
  public Object next() {
    Node node = this._map.item(this._index);
    if (node != null)
      this._index++; 
    return node;
  }
  
  public void remove() { throw new UnsupportedOperationException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\xml\NamedNodeMapIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */