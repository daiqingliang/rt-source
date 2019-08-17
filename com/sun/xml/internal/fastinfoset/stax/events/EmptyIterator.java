package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class EmptyIterator implements Iterator {
  public static final EmptyIterator instance = new EmptyIterator();
  
  public static EmptyIterator getInstance() { return instance; }
  
  public boolean hasNext() { return false; }
  
  public Object next() throws NoSuchElementException { throw new NoSuchElementException(); }
  
  public void remove() { throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.emptyIterator")); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\EmptyIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */