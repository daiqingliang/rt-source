package java.util;

public interface ListIterator<E> extends Iterator<E> {
  boolean hasNext();
  
  E next();
  
  boolean hasPrevious();
  
  E previous();
  
  int nextIndex();
  
  int previousIndex();
  
  void remove();
  
  void set(E paramE);
  
  void add(E paramE);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\ListIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */