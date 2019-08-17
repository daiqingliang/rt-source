package com.sun.xml.internal.stream.util;

import java.util.Iterator;

public class ReadOnlyIterator implements Iterator {
  Iterator iterator = null;
  
  public ReadOnlyIterator() {}
  
  public ReadOnlyIterator(Iterator paramIterator) { this.iterator = paramIterator; }
  
  public boolean hasNext() { return (this.iterator != null) ? this.iterator.hasNext() : 0; }
  
  public Object next() { return (this.iterator != null) ? this.iterator.next() : null; }
  
  public void remove() { throw new UnsupportedOperationException("Remove operation is not supported"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\strea\\util\ReadOnlyIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */