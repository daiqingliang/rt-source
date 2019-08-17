package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Consumer<T> {
  void accept(T paramT);
  
  default Consumer<T> andThen(Consumer<? super T> paramConsumer) {
    Objects.requireNonNull(paramConsumer);
    return paramObject -> {
        accept(paramObject);
        paramConsumer.accept(paramObject);
      };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\function\Consumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */