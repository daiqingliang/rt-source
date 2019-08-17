package java.util;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public interface PrimitiveIterator<T, T_CONS> extends Iterator<T> {
  void forEachRemaining(T_CONS paramT_CONS);
  
  public static interface OfDouble extends PrimitiveIterator<Double, DoubleConsumer> {
    double nextDouble();
    
    default void forEachRemaining(DoubleConsumer param1DoubleConsumer) {
      Objects.requireNonNull(param1DoubleConsumer);
      while (hasNext())
        param1DoubleConsumer.accept(nextDouble()); 
    }
    
    default Double next() {
      if (Tripwire.ENABLED)
        Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfDouble.nextLong()"); 
      return Double.valueOf(nextDouble());
    }
    
    default void forEachRemaining(Consumer<? super Double> param1Consumer) {
      if (param1Consumer instanceof DoubleConsumer) {
        forEachRemaining((DoubleConsumer)param1Consumer);
      } else {
        Objects.requireNonNull(param1Consumer);
        if (Tripwire.ENABLED)
          Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfDouble.forEachRemainingDouble(action::accept)"); 
        forEachRemaining(param1Consumer::accept);
      } 
    }
  }
  
  public static interface OfInt extends PrimitiveIterator<Integer, IntConsumer> {
    int nextInt();
    
    default void forEachRemaining(IntConsumer param1IntConsumer) {
      Objects.requireNonNull(param1IntConsumer);
      while (hasNext())
        param1IntConsumer.accept(nextInt()); 
    }
    
    default Integer next() {
      if (Tripwire.ENABLED)
        Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfInt.nextInt()"); 
      return Integer.valueOf(nextInt());
    }
    
    default void forEachRemaining(Consumer<? super Integer> param1Consumer) {
      if (param1Consumer instanceof IntConsumer) {
        forEachRemaining((IntConsumer)param1Consumer);
      } else {
        Objects.requireNonNull(param1Consumer);
        if (Tripwire.ENABLED)
          Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfInt.forEachRemainingInt(action::accept)"); 
        forEachRemaining(param1Consumer::accept);
      } 
    }
  }
  
  public static interface OfLong extends PrimitiveIterator<Long, LongConsumer> {
    long nextLong();
    
    default void forEachRemaining(LongConsumer param1LongConsumer) {
      Objects.requireNonNull(param1LongConsumer);
      while (hasNext())
        param1LongConsumer.accept(nextLong()); 
    }
    
    default Long next() {
      if (Tripwire.ENABLED)
        Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfLong.nextLong()"); 
      return Long.valueOf(nextLong());
    }
    
    default void forEachRemaining(Consumer<? super Long> param1Consumer) {
      if (param1Consumer instanceof LongConsumer) {
        forEachRemaining((LongConsumer)param1Consumer);
      } else {
        Objects.requireNonNull(param1Consumer);
        if (Tripwire.ENABLED)
          Tripwire.trip(getClass(), "{0} calling PrimitiveIterator.OfLong.forEachRemainingLong(action::accept)"); 
        forEachRemaining(param1Consumer::accept);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\PrimitiveIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */