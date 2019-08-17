package com.sun.xml.internal.fastinfoset.stax.events;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.util.Iterator;

public class ReadIterator implements Iterator {
  Iterator iterator = EmptyIterator.getInstance();
  
  public ReadIterator() {}
  
  public ReadIterator(Iterator paramIterator) {
    if (paramIterator != null)
      this.iterator = paramIterator; 
  }
  
  public boolean hasNext() { return this.iterator.hasNext(); }
  
  public Object next() { return this.iterator.next(); }
  
  public void remove() { throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.readonlyList")); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\ReadIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */