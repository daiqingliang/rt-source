package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface DoubleConsumer {
  void accept(double paramDouble);
  
  default DoubleConsumer andThen(DoubleConsumer paramDoubleConsumer) {
    Objects.requireNonNull(paramDoubleConsumer);
    return paramDouble -> {
        accept(paramDouble);
        paramDoubleConsumer.accept(paramDouble);
      };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\function\DoubleConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */