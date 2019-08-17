package java.util.stream;

import java.util.Iterator;
import java.util.Spliterator;

public interface BaseStream<T, S extends BaseStream<T, S>> extends AutoCloseable {
  Iterator<T> iterator();
  
  Spliterator<T> spliterator();
  
  boolean isParallel();
  
  S sequential();
  
  S parallel();
  
  S unordered();
  
  S onClose(Runnable paramRunnable);
  
  void close();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\BaseStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */