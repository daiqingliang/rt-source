package java.lang;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public interface Iterable<T> {
  Iterator<T> iterator();
  
  default void forEach(Consumer<? super T> paramConsumer) {
    Objects.requireNonNull(paramConsumer);
    for (Object object : this)
      paramConsumer.accept(object); 
  }
  
  default Spliterator<T> spliterator() { return Spliterators.spliteratorUnknownSize(iterator(), 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Iterable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */