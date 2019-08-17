package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface LongUnaryOperator {
  long applyAsLong(long paramLong);
  
  default LongUnaryOperator compose(LongUnaryOperator paramLongUnaryOperator) {
    Objects.requireNonNull(paramLongUnaryOperator);
    return paramLong -> applyAsLong(paramLongUnaryOperator.applyAsLong(paramLong));
  }
  
  default LongUnaryOperator andThen(LongUnaryOperator paramLongUnaryOperator) {
    Objects.requireNonNull(paramLongUnaryOperator);
    return paramLong -> paramLongUnaryOperator.applyAsLong(applyAsLong(paramLong));
  }
  
  static LongUnaryOperator identity() { return paramLong -> paramLong; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\function\LongUnaryOperator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */