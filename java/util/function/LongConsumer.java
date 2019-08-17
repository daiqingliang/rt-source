package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface LongConsumer {
  void accept(long paramLong);
  
  default LongConsumer andThen(LongConsumer paramLongConsumer) {
    Objects.requireNonNull(paramLongConsumer);
    return paramLong -> {
        accept(paramLong);
        paramLongConsumer.accept(paramLong);
      };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\function\LongConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */