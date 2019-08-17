package java.util.stream;

import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Supplier;

public final class StreamSupport {
  public static <T> Stream<T> stream(Spliterator<T> paramSpliterator, boolean paramBoolean) {
    Objects.requireNonNull(paramSpliterator);
    return new ReferencePipeline.Head(paramSpliterator, StreamOpFlag.fromCharacteristics(paramSpliterator), paramBoolean);
  }
  
  public static <T> Stream<T> stream(Supplier<? extends Spliterator<T>> paramSupplier, int paramInt, boolean paramBoolean) {
    Objects.requireNonNull(paramSupplier);
    return new ReferencePipeline.Head(paramSupplier, StreamOpFlag.fromCharacteristics(paramInt), paramBoolean);
  }
  
  public static IntStream intStream(Spliterator.OfInt paramOfInt, boolean paramBoolean) { return new IntPipeline.Head(paramOfInt, StreamOpFlag.fromCharacteristics(paramOfInt), paramBoolean); }
  
  public static IntStream intStream(Supplier<? extends Spliterator.OfInt> paramSupplier, int paramInt, boolean paramBoolean) { return new IntPipeline.Head(paramSupplier, StreamOpFlag.fromCharacteristics(paramInt), paramBoolean); }
  
  public static LongStream longStream(Spliterator.OfLong paramOfLong, boolean paramBoolean) { return new LongPipeline.Head(paramOfLong, StreamOpFlag.fromCharacteristics(paramOfLong), paramBoolean); }
  
  public static LongStream longStream(Supplier<? extends Spliterator.OfLong> paramSupplier, int paramInt, boolean paramBoolean) { return new LongPipeline.Head(paramSupplier, StreamOpFlag.fromCharacteristics(paramInt), paramBoolean); }
  
  public static DoubleStream doubleStream(Spliterator.OfDouble paramOfDouble, boolean paramBoolean) { return new DoublePipeline.Head(paramOfDouble, StreamOpFlag.fromCharacteristics(paramOfDouble), paramBoolean); }
  
  public static DoubleStream doubleStream(Supplier<? extends Spliterator.OfDouble> paramSupplier, int paramInt, boolean paramBoolean) { return new DoublePipeline.Head(paramSupplier, StreamOpFlag.fromCharacteristics(paramInt), paramBoolean); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\StreamSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */