package java.util;

import java.lang.invoke.SerializedLambda;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

@FunctionalInterface
public interface Comparator<T> {
  int compare(T paramT1, T paramT2);
  
  boolean equals(Object paramObject);
  
  default Comparator<T> reversed() { return Collections.reverseOrder(this); }
  
  default Comparator<T> thenComparing(Comparator<? super T> paramComparator) {
    Objects.requireNonNull(paramComparator);
    return (Comparator)((paramObject1, paramObject2) -> {
        int i = compare(paramObject1, paramObject2);
        return (i != 0) ? i : paramComparator.compare(paramObject1, paramObject2);
      });
  }
  
  default <U> Comparator<T> thenComparing(Function<? super T, ? extends U> paramFunction, Comparator<? super U> paramComparator) { return thenComparing(comparing(paramFunction, paramComparator)); }
  
  default <U extends Comparable<? super U>> Comparator<T> thenComparing(Function<? super T, ? extends U> paramFunction) { return thenComparing(comparing(paramFunction)); }
  
  default Comparator<T> thenComparingInt(ToIntFunction<? super T> paramToIntFunction) { return thenComparing(comparingInt(paramToIntFunction)); }
  
  default Comparator<T> thenComparingLong(ToLongFunction<? super T> paramToLongFunction) { return thenComparing(comparingLong(paramToLongFunction)); }
  
  default Comparator<T> thenComparingDouble(ToDoubleFunction<? super T> paramToDoubleFunction) { return thenComparing(comparingDouble(paramToDoubleFunction)); }
  
  static <T extends Comparable<? super T>> Comparator<T> reverseOrder() { return Collections.reverseOrder(); }
  
  static <T extends Comparable<? super T>> Comparator<T> naturalOrder() { return Comparators.NaturalOrderComparator.INSTANCE; }
  
  static <T> Comparator<T> nullsFirst(Comparator<? super T> paramComparator) { return new Comparators.NullComparator(true, paramComparator); }
  
  static <T> Comparator<T> nullsLast(Comparator<? super T> paramComparator) { return new Comparators.NullComparator(false, paramComparator); }
  
  static <T, U> Comparator<T> comparing(Function<? super T, ? extends U> paramFunction, Comparator<? super U> paramComparator) {
    Objects.requireNonNull(paramFunction);
    Objects.requireNonNull(paramComparator);
    return (Comparator)((paramObject1, paramObject2) -> paramComparator.compare(paramFunction.apply(paramObject1), paramFunction.apply(paramObject2)));
  }
  
  static <T, U extends Comparable<? super U>> Comparator<T> comparing(Function<? super T, ? extends U> paramFunction) {
    Objects.requireNonNull(paramFunction);
    return (Comparator)((paramObject1, paramObject2) -> ((Comparable)paramFunction.apply(paramObject1)).compareTo(paramFunction.apply(paramObject2)));
  }
  
  static <T> Comparator<T> comparingInt(ToIntFunction<? super T> paramToIntFunction) {
    Objects.requireNonNull(paramToIntFunction);
    return (Comparator)((paramObject1, paramObject2) -> Integer.compare(paramToIntFunction.applyAsInt(paramObject1), paramToIntFunction.applyAsInt(paramObject2)));
  }
  
  static <T> Comparator<T> comparingLong(ToLongFunction<? super T> paramToLongFunction) {
    Objects.requireNonNull(paramToLongFunction);
    return (Comparator)((paramObject1, paramObject2) -> Long.compare(paramToLongFunction.applyAsLong(paramObject1), paramToLongFunction.applyAsLong(paramObject2)));
  }
  
  static <T> Comparator<T> comparingDouble(ToDoubleFunction<? super T> paramToDoubleFunction) {
    Objects.requireNonNull(paramToDoubleFunction);
    return (Comparator)((paramObject1, paramObject2) -> Double.compare(paramToDoubleFunction.applyAsDouble(paramObject1), paramToDoubleFunction.applyAsDouble(paramObject2)));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Comparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */