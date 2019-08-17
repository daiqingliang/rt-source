package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface IntUnaryOperator {
  int applyAsInt(int paramInt);
  
  default IntUnaryOperator compose(IntUnaryOperator paramIntUnaryOperator) {
    Objects.requireNonNull(paramIntUnaryOperator);
    return paramInt -> applyAsInt(paramIntUnaryOperator.applyAsInt(paramInt));
  }
  
  default IntUnaryOperator andThen(IntUnaryOperator paramIntUnaryOperator) {
    Objects.requireNonNull(paramIntUnaryOperator);
    return paramInt -> paramIntUnaryOperator.applyAsInt(applyAsInt(paramInt));
  }
  
  static IntUnaryOperator identity() { return paramInt -> paramInt; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\function\IntUnaryOperator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */