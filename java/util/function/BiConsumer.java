package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface BiConsumer<T, U> {
  void accept(T paramT, U paramU);
  
  default BiConsumer<T, U> andThen(BiConsumer<? super T, ? super U> paramBiConsumer) {
    Objects.requireNonNull(paramBiConsumer);
    return (paramObject1, paramObject2) -> {
        accept(paramObject1, paramObject2);
        paramBiConsumer.accept(paramObject1, paramObject2);
      };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\function\BiConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */