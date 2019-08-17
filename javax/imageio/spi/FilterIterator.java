package javax.imageio.spi;

import java.util.Iterator;
import java.util.NoSuchElementException;

class FilterIterator<T> extends Object implements Iterator<T> {
  private Iterator<T> iter;
  
  private ServiceRegistry.Filter filter;
  
  private T next = null;
  
  public FilterIterator(Iterator<T> paramIterator, ServiceRegistry.Filter paramFilter) {
    this.iter = paramIterator;
    this.filter = paramFilter;
    advance();
  }
  
  private void advance() {
    while (this.iter.hasNext()) {
      Object object = this.iter.next();
      if (this.filter.filter(object)) {
        this.next = object;
        return;
      } 
    } 
    this.next = null;
  }
  
  public boolean hasNext() { return (this.next != null); }
  
  public T next() {
    if (this.next == null)
      throw new NoSuchElementException(); 
    Object object = this.next;
    advance();
    return (T)object;
  }
  
  public void remove() { throw new UnsupportedOperationException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\spi\FilterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */