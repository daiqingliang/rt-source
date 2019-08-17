package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface IntConsumer {
  void accept(int paramInt);
  
  default IntConsumer andThen(IntConsumer paramIntConsumer) {
    Objects.requireNonNull(paramIntConsumer);
    return paramInt -> {
        accept(paramInt);
        paramIntConsumer.accept(paramInt);
      };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\function\IntConsumer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */