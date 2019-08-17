package com.sun.xml.internal.bind.v2.util;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public final class FlattenIterator<T> extends Object implements Iterator<T> {
  private final Iterator<? extends Map<?, ? extends T>> parent;
  
  private Iterator<? extends T> child = null;
  
  private T next;
  
  public FlattenIterator(Iterable<? extends Map<?, ? extends T>> paramIterable) { this.parent = paramIterable.iterator(); }
  
  public void remove() { throw new UnsupportedOperationException(); }
  
  public boolean hasNext() {
    getNext();
    return (this.next != null);
  }
  
  public T next() {
    Object object = this.next;
    this.next = null;
    if (object == null)
      throw new NoSuchElementException(); 
    return (T)object;
  }
  
  private void getNext() {
    if (this.next != null)
      return; 
    if (this.child != null && this.child.hasNext()) {
      this.next = this.child.next();
      return;
    } 
    if (this.parent.hasNext()) {
      this.child = ((Map)this.parent.next()).values().iterator();
      getNext();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v\\util\FlattenIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */