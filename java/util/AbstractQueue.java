package java.util;

public abstract class AbstractQueue<E> extends AbstractCollection<E> implements Queue<E> {
  public boolean add(E paramE) {
    if (offer(paramE))
      return true; 
    throw new IllegalStateException("Queue full");
  }
  
  public E remove() {
    Object object = poll();
    if (object != null)
      return (E)object; 
    throw new NoSuchElementException();
  }
  
  public E element() {
    Object object = peek();
    if (object != null)
      return (E)object; 
    throw new NoSuchElementException();
  }
  
  public void clear() {
    while (poll() != null);
  }
  
  public boolean addAll(Collection<? extends E> paramCollection) {
    if (paramCollection == null)
      throw new NullPointerException(); 
    if (paramCollection == this)
      throw new IllegalArgumentException(); 
    boolean bool = false;
    for (Object object : paramCollection) {
      if (add(object))
        bool = true; 
    } 
    return bool;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\AbstractQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */