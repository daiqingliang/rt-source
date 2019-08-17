package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Function<T, R> {
  R apply(T paramT);
  
  default <V> Function<V, R> compose(Function<? super V, ? extends T> paramFunction) {
    Objects.requireNonNull(paramFunction);
    return paramObject -> apply(paramFunction.apply(paramObject));
  }
  
  default <V> Function<T, V> andThen(Function<? super R, ? extends V> paramFunction) {
    Objects.requireNonNull(paramFunction);
    return paramObject -> paramFunction.apply(apply(paramObject));
  }
  
  static <T> Function<T, T> identity() { return paramObject -> paramObject; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\function\Function.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */