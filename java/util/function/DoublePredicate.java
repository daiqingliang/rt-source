package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface DoublePredicate {
  boolean test(double paramDouble);
  
  default DoublePredicate and(DoublePredicate paramDoublePredicate) {
    Objects.requireNonNull(paramDoublePredicate);
    return paramDouble -> (test(paramDouble) && paramDoublePredicate.test(paramDouble));
  }
  
  default DoublePredicate negate() { return paramDouble -> !test(paramDouble); }
  
  default DoublePredicate or(DoublePredicate paramDoublePredicate) {
    Objects.requireNonNull(paramDoublePredicate);
    return paramDouble -> (test(paramDouble) || paramDoublePredicate.test(paramDouble));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\function\DoublePredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */