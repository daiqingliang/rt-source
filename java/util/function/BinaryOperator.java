package java.util.function;

import java.util.Comparator;
import java.util.Objects;

@FunctionalInterface
public interface BinaryOperator<T> extends BiFunction<T, T, T> {
  static <T> BinaryOperator<T> minBy(Comparator<? super T> paramComparator) {
    Objects.requireNonNull(paramComparator);
    return (paramObject1, paramObject2) -> (paramComparator.compare(paramObject1, paramObject2) <= 0) ? paramObject1 : paramObject2;
  }
  
  static <T> BinaryOperator<T> maxBy(Comparator<? super T> paramComparator) {
    Objects.requireNonNull(paramComparator);
    return (paramObject1, paramObject2) -> (paramComparator.compare(paramObject1, paramObject2) >= 0) ? paramObject1 : paramObject2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\function\BinaryOperator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */