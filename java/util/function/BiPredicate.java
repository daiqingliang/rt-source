package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface BiPredicate<T, U> {
  boolean test(T paramT, U paramU);
  
  default BiPredicate<T, U> and(BiPredicate<? super T, ? super U> paramBiPredicate) {
    Objects.requireNonNull(paramBiPredicate);
    return (paramObject1, paramObject2) -> (test(paramObject1, paramObject2) && paramBiPredicate.test(paramObject1, paramObject2));
  }
  
  default BiPredicate<T, U> negate() { return (paramObject1, paramObject2) -> !test(paramObject1, paramObject2); }
  
  default BiPredicate<T, U> or(BiPredicate<? super T, ? super U> paramBiPredicate) {
    Objects.requireNonNull(paramBiPredicate);
    return (paramObject1, paramObject2) -> (test(paramObject1, paramObject2) || paramBiPredicate.test(paramObject1, paramObject2));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\function\BiPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */