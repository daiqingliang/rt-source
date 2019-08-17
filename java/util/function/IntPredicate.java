package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface IntPredicate {
  boolean test(int paramInt);
  
  default IntPredicate and(IntPredicate paramIntPredicate) {
    Objects.requireNonNull(paramIntPredicate);
    return paramInt -> (test(paramInt) && paramIntPredicate.test(paramInt));
  }
  
  default IntPredicate negate() { return paramInt -> !test(paramInt); }
  
  default IntPredicate or(IntPredicate paramIntPredicate) {
    Objects.requireNonNull(paramIntPredicate);
    return paramInt -> (test(paramInt) || paramIntPredicate.test(paramInt));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\function\IntPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */