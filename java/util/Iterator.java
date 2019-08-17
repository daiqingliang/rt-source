package java.util;

import java.util.function.Consumer;

public interface Iterator<E> {
  boolean hasNext();
  
  E next();
  
  default void remove() { throw new UnsupportedOperationException("remove"); }
  
  default void forEachRemaining(Consumer<? super E> paramConsumer) {
    Objects.requireNonNull(paramConsumer);
    while (hasNext())
      paramConsumer.accept(next()); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Iterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */