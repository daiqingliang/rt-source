package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface BiFunction<T, U, R> {
  R apply(T paramT, U paramU);
  
  default <V> BiFunction<T, U, V> andThen(Function<? super R, ? extends V> paramFunction) {
    Objects.requireNonNull(paramFunction);
    return (paramObject1, paramObject2) -> paramFunction.apply(apply(paramObject1, paramObject2));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\function\BiFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */