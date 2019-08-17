package com.sun.org.apache.xerces.internal.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class XMLAttributesIteratorImpl extends XMLAttributesImpl implements Iterator {
  protected int fCurrent = 0;
  
  protected XMLAttributesImpl.Attribute fLastReturnedItem;
  
  public boolean hasNext() { return (this.fCurrent < getLength()); }
  
  public Object next() {
    if (hasNext())
      return this.fLastReturnedItem = this.fAttributes[this.fCurrent++]; 
    throw new NoSuchElementException();
  }
  
  public void remove() {
    if (this.fLastReturnedItem == this.fAttributes[this.fCurrent - true]) {
      removeAttributeAt(this.fCurrent--);
    } else {
      throw new IllegalStateException();
    } 
  }
  
  public void removeAllAttributes() {
    super.removeAllAttributes();
    this.fCurrent = 0;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\XMLAttributesIteratorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */