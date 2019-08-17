package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Predicate<T> {
  boolean test(T paramT);
  
  default Predicate<T> and(Predicate<? super T> paramPredicate) {
    Objects.requireNonNull(paramPredicate);
    return paramObject -> (test(paramObject) && paramPredicate.test(paramObject));
  }
  
  default Predicate<T> negate() { return paramObject -> !test(paramObject); }
  
  default Predicate<T> or(Predicate<? super T> paramPredicate) {
    Objects.requireNonNull(paramPredicate);
    return paramObject -> (test(paramObject) || paramPredicate.test(paramObject));
  }
  
  static <T> Predicate<T> isEqual(Object paramObject) { return (null == paramObject) ? Objects::isNull : (paramObject2 -> paramObject1.equals(paramObject2)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\function\Predicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */