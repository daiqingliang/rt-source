package java.util;

public interface Queue<E> extends Collection<E> {
  boolean add(E paramE);
  
  boolean offer(E paramE);
  
  E remove();
  
  E poll();
  
  E element();
  
  E peek();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Queue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */