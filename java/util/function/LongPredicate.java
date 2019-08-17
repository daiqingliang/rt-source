package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface LongPredicate {
  boolean test(long paramLong);
  
  default LongPredicate and(LongPredicate paramLongPredicate) {
    Objects.requireNonNull(paramLongPredicate);
    return paramLong -> (test(paramLong) && paramLongPredicate.test(paramLong));
  }
  
  default LongPredicate negate() { return paramLong -> !test(paramLong); }
  
  default LongPredicate or(LongPredicate paramLongPredicate) {
    Objects.requireNonNull(paramLongPredicate);
    return paramLong -> (test(paramLong) || paramLongPredicate.test(paramLong));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\function\LongPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */