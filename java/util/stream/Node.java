package java.util.stream;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;

interface Node<T> {
  Spliterator<T> spliterator();
  
  void forEach(Consumer<? super T> paramConsumer);
  
  default int getChildCount() { return 0; }
  
  default Node<T> getChild(int paramInt) { throw new IndexOutOfBoundsException(); }
  
  default Node<T> truncate(long paramLong1, long paramLong2, IntFunction<T[]> paramIntFunction) {
    if (paramLong1 == 0L && paramLong2 == count())
      return this; 
    Spliterator spliterator = spliterator();
    long l = paramLong2 - paramLong1;
    Builder builder = Nodes.builder(l, paramIntFunction);
    builder.begin(l);
    byte b;
    for (b = 0; b < paramLong1 && spliterator.tryAdvance(paramObject -> {
        
        }); b++);
    for (b = 0; b < l && spliterator.tryAdvance(builder); b++);
    builder.end();
    return builder.build();
  }
  
  T[] asArray(IntFunction<T[]> paramIntFunction);
  
  void copyInto(T[] paramArrayOfT, int paramInt);
  
  default StreamShape getShape() { return StreamShape.REFERENCE; }
  
  long count();
  
  public static interface Builder<T> extends Sink<T> {
    Node<T> build();
    
    public static interface OfDouble extends Builder<Double>, Sink.OfDouble {
      Node.OfDouble build();
    }
    
    public static interface OfInt extends Builder<Integer>, Sink.OfInt {
      Node.OfInt build();
    }
    
    public static interface OfLong extends Builder<Long>, Sink.OfLong {
      Node.OfLong build();
    }
  }
  
  public static interface OfDouble extends OfPrimitive<Double, DoubleConsumer, double[], Spliterator.OfDouble, OfDouble> {
    default void forEach(Consumer<? super Double> param1Consumer) {
      if (param1Consumer instanceof DoubleConsumer) {
        forEach((DoubleConsumer)param1Consumer);
      } else {
        if (Tripwire.ENABLED)
          Tripwire.trip(getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)"); 
        ((Spliterator.OfDouble)spliterator()).forEachRemaining(param1Consumer);
      } 
    }
    
    default void copyInto(Double[] param1ArrayOfDouble, int param1Int) {
      if (Tripwire.ENABLED)
        Tripwire.trip(getClass(), "{0} calling Node.OfDouble.copyInto(Double[], int)"); 
      double[] arrayOfDouble = (double[])asPrimitiveArray();
      for (int i = 0; i < arrayOfDouble.length; i++)
        param1ArrayOfDouble[param1Int + i] = Double.valueOf(arrayOfDouble[i]); 
    }
    
    default OfDouble truncate(long param1Long1, long param1Long2, IntFunction<Double[]> param1IntFunction) {
      if (param1Long1 == 0L && param1Long2 == count())
        return this; 
      long l = param1Long2 - param1Long1;
      Spliterator.OfDouble ofDouble = (Spliterator.OfDouble)spliterator();
      Node.Builder.OfDouble ofDouble1 = Nodes.doubleBuilder(l);
      ofDouble1.begin(l);
      byte b;
      for (b = 0; b < param1Long1 && ofDouble.tryAdvance(param1Double -> {
          
          }); b++);
      for (b = 0; b < l && ofDouble.tryAdvance(ofDouble1); b++);
      ofDouble1.end();
      return ofDouble1.build();
    }
    
    default double[] newArray(int param1Int) { return new double[param1Int]; }
    
    default StreamShape getShape() { return StreamShape.DOUBLE_VALUE; }
  }
  
  public static interface OfInt extends OfPrimitive<Integer, IntConsumer, int[], Spliterator.OfInt, OfInt> {
    default void forEach(Consumer<? super Integer> param1Consumer) {
      if (param1Consumer instanceof IntConsumer) {
        forEach((IntConsumer)param1Consumer);
      } else {
        if (Tripwire.ENABLED)
          Tripwire.trip(getClass(), "{0} calling Node.OfInt.forEachRemaining(Consumer)"); 
        ((Spliterator.OfInt)spliterator()).forEachRemaining(param1Consumer);
      } 
    }
    
    default void copyInto(Integer[] param1ArrayOfInteger, int param1Int) {
      if (Tripwire.ENABLED)
        Tripwire.trip(getClass(), "{0} calling Node.OfInt.copyInto(Integer[], int)"); 
      int[] arrayOfInt = (int[])asPrimitiveArray();
      for (int i = 0; i < arrayOfInt.length; i++)
        param1ArrayOfInteger[param1Int + i] = Integer.valueOf(arrayOfInt[i]); 
    }
    
    default OfInt truncate(long param1Long1, long param1Long2, IntFunction<Integer[]> param1IntFunction) {
      if (param1Long1 == 0L && param1Long2 == count())
        return this; 
      long l = param1Long2 - param1Long1;
      Spliterator.OfInt ofInt = (Spliterator.OfInt)spliterator();
      Node.Builder.OfInt ofInt1 = Nodes.intBuilder(l);
      ofInt1.begin(l);
      byte b;
      for (b = 0; b < param1Long1 && ofInt.tryAdvance(param1Int -> {
          
          }); b++);
      for (b = 0; b < l && ofInt.tryAdvance(ofInt1); b++);
      ofInt1.end();
      return ofInt1.build();
    }
    
    default int[] newArray(int param1Int) { return new int[param1Int]; }
    
    default StreamShape getShape() { return StreamShape.INT_VALUE; }
  }
  
  public static interface OfLong extends OfPrimitive<Long, LongConsumer, long[], Spliterator.OfLong, OfLong> {
    default void forEach(Consumer<? super Long> param1Consumer) {
      if (param1Consumer instanceof LongConsumer) {
        forEach((LongConsumer)param1Consumer);
      } else {
        if (Tripwire.ENABLED)
          Tripwire.trip(getClass(), "{0} calling Node.OfLong.forEachRemaining(Consumer)"); 
        ((Spliterator.OfLong)spliterator()).forEachRemaining(param1Consumer);
      } 
    }
    
    default void copyInto(Long[] param1ArrayOfLong, int param1Int) {
      if (Tripwire.ENABLED)
        Tripwire.trip(getClass(), "{0} calling Node.OfInt.copyInto(Long[], int)"); 
      long[] arrayOfLong = (long[])asPrimitiveArray();
      for (int i = 0; i < arrayOfLong.length; i++)
        param1ArrayOfLong[param1Int + i] = Long.valueOf(arrayOfLong[i]); 
    }
    
    default OfLong truncate(long param1Long1, long param1Long2, IntFunction<Long[]> param1IntFunction) {
      if (param1Long1 == 0L && param1Long2 == count())
        return this; 
      long l = param1Long2 - param1Long1;
      Spliterator.OfLong ofLong = (Spliterator.OfLong)spliterator();
      Node.Builder.OfLong ofLong1 = Nodes.longBuilder(l);
      ofLong1.begin(l);
      byte b;
      for (b = 0; b < param1Long1 && ofLong.tryAdvance(param1Long -> {
          
          }); b++);
      for (b = 0; b < l && ofLong.tryAdvance(ofLong1); b++);
      ofLong1.end();
      return ofLong1.build();
    }
    
    default long[] newArray(int param1Int) { return new long[param1Int]; }
    
    default StreamShape getShape() { return StreamShape.LONG_VALUE; }
  }
  
  public static interface OfPrimitive<T, T_CONS, T_ARR, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>, T_NODE extends OfPrimitive<T, T_CONS, T_ARR, T_SPLITR, T_NODE>> extends Node<T> {
    T_SPLITR spliterator();
    
    void forEach(T_CONS param1T_CONS);
    
    default T_NODE getChild(int param1Int) { throw new IndexOutOfBoundsException(); }
    
    T_NODE truncate(long param1Long1, long param1Long2, IntFunction<T[]> param1IntFunction);
    
    default T[] asArray(IntFunction<T[]> param1IntFunction) {
      if (Tripwire.ENABLED)
        Tripwire.trip(getClass(), "{0} calling Node.OfPrimitive.asArray"); 
      long l = count();
      if (l >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      Object[] arrayOfObject = (Object[])param1IntFunction.apply((int)count());
      copyInto(arrayOfObject, 0);
      return (T[])arrayOfObject;
    }
    
    T_ARR asPrimitiveArray();
    
    T_ARR newArray(int param1Int);
    
    void copyInto(T_ARR param1T_ARR, int param1Int);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\Node.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */