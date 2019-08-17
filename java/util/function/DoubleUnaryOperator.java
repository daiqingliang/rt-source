package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface DoubleUnaryOperator {
  double applyAsDouble(double paramDouble);
  
  default DoubleUnaryOperator compose(DoubleUnaryOperator paramDoubleUnaryOperator) {
    Objects.requireNonNull(paramDoubleUnaryOperator);
    return paramDouble -> applyAsDouble(paramDoubleUnaryOperator.applyAsDouble(paramDouble));
  }
  
  default DoubleUnaryOperator andThen(DoubleUnaryOperator paramDoubleUnaryOperator) {
    Objects.requireNonNull(paramDoubleUnaryOperator);
    return paramDouble -> paramDoubleUnaryOperator.applyAsDouble(applyAsDouble(paramDouble));
  }
  
  static DoubleUnaryOperator identity() { return paramDouble -> paramDouble; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\function\DoubleUnaryOperator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */