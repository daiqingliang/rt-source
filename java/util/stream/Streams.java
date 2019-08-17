package java.util.stream;

import java.util.Comparator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

final class Streams {
  static final Object NONE = new Object();
  
  private Streams() { throw new Error("no instances"); }
  
  static Runnable composeWithExceptions(final Runnable a, final Runnable b) { return new Runnable() {
        public void run() {
          try {
            a.run();
          } catch (Throwable throwable) {
            try {
              b.run();
            } catch (Throwable throwable1) {
              try {
                throwable.addSuppressed(throwable1);
              } catch (Throwable throwable2) {}
            } 
            throw throwable;
          } 
          b.run();
        }
      }; }
  
  static Runnable composedClose(final BaseStream<?, ?> a, final BaseStream<?, ?> b) { return new Runnable() {
        public void run() {
          try {
            a.close();
          } catch (Throwable throwable) {
            try {
              b.close();
            } catch (Throwable throwable1) {
              try {
                throwable.addSuppressed(throwable1);
              } catch (Throwable throwable2) {}
            } 
            throw throwable;
          } 
          b.close();
        }
      }; }
  
  private static abstract class AbstractStreamBuilderImpl<T, S extends Spliterator<T>> extends Object implements Spliterator<T> {
    int count;
    
    private AbstractStreamBuilderImpl() {}
    
    public S trySplit() { return null; }
    
    public long estimateSize() { return (-this.count - 1); }
    
    public int characteristics() { return 17488; }
  }
  
  static abstract class ConcatSpliterator<T, T_SPLITR extends Spliterator<T>> extends Object implements Spliterator<T> {
    protected final T_SPLITR aSpliterator;
    
    protected final T_SPLITR bSpliterator;
    
    boolean beforeSplit;
    
    final boolean unsized;
    
    public ConcatSpliterator(T_SPLITR param1T_SPLITR1, T_SPLITR param1T_SPLITR2) {
      this.aSpliterator = param1T_SPLITR1;
      this.bSpliterator = param1T_SPLITR2;
      this.beforeSplit = true;
      this.unsized = (param1T_SPLITR1.estimateSize() + param1T_SPLITR2.estimateSize() < 0L);
    }
    
    public T_SPLITR trySplit() {
      Spliterator spliterator = this.beforeSplit ? this.aSpliterator : this.bSpliterator.trySplit();
      this.beforeSplit = false;
      return (T_SPLITR)spliterator;
    }
    
    public boolean tryAdvance(Consumer<? super T> param1Consumer) {
      boolean bool;
      if (this.beforeSplit) {
        bool = this.aSpliterator.tryAdvance(param1Consumer);
        if (!bool) {
          this.beforeSplit = false;
          bool = this.bSpliterator.tryAdvance(param1Consumer);
        } 
      } else {
        bool = this.bSpliterator.tryAdvance(param1Consumer);
      } 
      return bool;
    }
    
    public void forEachRemaining(Consumer<? super T> param1Consumer) {
      if (this.beforeSplit)
        this.aSpliterator.forEachRemaining(param1Consumer); 
      this.bSpliterator.forEachRemaining(param1Consumer);
    }
    
    public long estimateSize() {
      if (this.beforeSplit) {
        long l = this.aSpliterator.estimateSize() + this.bSpliterator.estimateSize();
        return (l >= 0L) ? l : Float.MAX_VALUE;
      } 
      return this.bSpliterator.estimateSize();
    }
    
    public int characteristics() { return this.beforeSplit ? (this.aSpliterator.characteristics() & this.bSpliterator.characteristics() & ((0x5 | (this.unsized ? 16448 : 0)) ^ 0xFFFFFFFF)) : this.bSpliterator.characteristics(); }
    
    public Comparator<? super T> getComparator() {
      if (this.beforeSplit)
        throw new IllegalStateException(); 
      return this.bSpliterator.getComparator();
    }
    
    static class OfDouble extends OfPrimitive<Double, DoubleConsumer, Spliterator.OfDouble> implements Spliterator.OfDouble {
      OfDouble(Spliterator.OfDouble param2OfDouble1, Spliterator.OfDouble param2OfDouble2) { super(param2OfDouble1, param2OfDouble2, null); }
    }
    
    static class OfInt extends OfPrimitive<Integer, IntConsumer, Spliterator.OfInt> implements Spliterator.OfInt {
      OfInt(Spliterator.OfInt param2OfInt1, Spliterator.OfInt param2OfInt2) { super(param2OfInt1, param2OfInt2, null); }
    }
    
    static class OfLong extends OfPrimitive<Long, LongConsumer, Spliterator.OfLong> implements Spliterator.OfLong {
      OfLong(Spliterator.OfLong param2OfLong1, Spliterator.OfLong param2OfLong2) { super(param2OfLong1, param2OfLong2, null); }
    }
    
    private static abstract class OfPrimitive<T, T_CONS, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>> extends ConcatSpliterator<T, T_SPLITR> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
      private OfPrimitive(T_SPLITR param2T_SPLITR1, T_SPLITR param2T_SPLITR2) { super(param2T_SPLITR1, param2T_SPLITR2); }
      
      public boolean tryAdvance(T_CONS param2T_CONS) {
        boolean bool;
        if (this.beforeSplit) {
          bool = ((Spliterator.OfPrimitive)this.aSpliterator).tryAdvance(param2T_CONS);
          if (!bool) {
            this.beforeSplit = false;
            bool = ((Spliterator.OfPrimitive)this.bSpliterator).tryAdvance(param2T_CONS);
          } 
        } else {
          bool = ((Spliterator.OfPrimitive)this.bSpliterator).tryAdvance(param2T_CONS);
        } 
        return bool;
      }
      
      public void forEachRemaining(T_CONS param2T_CONS) {
        if (this.beforeSplit)
          ((Spliterator.OfPrimitive)this.aSpliterator).forEachRemaining(param2T_CONS); 
        ((Spliterator.OfPrimitive)this.bSpliterator).forEachRemaining(param2T_CONS);
      }
    }
    
    static class OfRef<T> extends ConcatSpliterator<T, Spliterator<T>> {
      OfRef(Spliterator<T> param2Spliterator1, Spliterator<T> param2Spliterator2) { super(param2Spliterator1, param2Spliterator2); }
    }
  }
  
  static final class DoubleStreamBuilderImpl extends AbstractStreamBuilderImpl<Double, Spliterator.OfDouble> implements DoubleStream.Builder, Spliterator.OfDouble {
    double first;
    
    SpinedBuffer.OfDouble buffer;
    
    DoubleStreamBuilderImpl() { super(null); }
    
    DoubleStreamBuilderImpl(double param1Double) {
      super(null);
      this.first = param1Double;
      this.count = -2;
    }
    
    public void accept(double param1Double) {
      if (this.count == 0) {
        this.first = param1Double;
        this.count++;
      } else if (this.count > 0) {
        if (this.buffer == null) {
          this.buffer = new SpinedBuffer.OfDouble();
          this.buffer.accept(this.first);
          this.count++;
        } 
        this.buffer.accept(param1Double);
      } else {
        throw new IllegalStateException();
      } 
    }
    
    public DoubleStream build() {
      int i = this.count;
      if (i >= 0) {
        this.count = -this.count - 1;
        return (i < 2) ? StreamSupport.doubleStream(this, false) : StreamSupport.doubleStream(this.buffer.spliterator(), false);
      } 
      throw new IllegalStateException();
    }
    
    public boolean tryAdvance(DoubleConsumer param1DoubleConsumer) {
      Objects.requireNonNull(param1DoubleConsumer);
      if (this.count == -2) {
        param1DoubleConsumer.accept(this.first);
        this.count = -1;
        return true;
      } 
      return false;
    }
    
    public void forEachRemaining(DoubleConsumer param1DoubleConsumer) {
      Objects.requireNonNull(param1DoubleConsumer);
      if (this.count == -2) {
        param1DoubleConsumer.accept(this.first);
        this.count = -1;
      } 
    }
  }
  
  static final class IntStreamBuilderImpl extends AbstractStreamBuilderImpl<Integer, Spliterator.OfInt> implements IntStream.Builder, Spliterator.OfInt {
    int first;
    
    SpinedBuffer.OfInt buffer;
    
    IntStreamBuilderImpl() { super(null); }
    
    IntStreamBuilderImpl(int param1Int) {
      super(null);
      this.first = param1Int;
      this.count = -2;
    }
    
    public void accept(int param1Int) {
      if (this.count == 0) {
        this.first = param1Int;
        this.count++;
      } else if (this.count > 0) {
        if (this.buffer == null) {
          this.buffer = new SpinedBuffer.OfInt();
          this.buffer.accept(this.first);
          this.count++;
        } 
        this.buffer.accept(param1Int);
      } else {
        throw new IllegalStateException();
      } 
    }
    
    public IntStream build() {
      int i = this.count;
      if (i >= 0) {
        this.count = -this.count - 1;
        return (i < 2) ? StreamSupport.intStream(this, false) : StreamSupport.intStream(this.buffer.spliterator(), false);
      } 
      throw new IllegalStateException();
    }
    
    public boolean tryAdvance(IntConsumer param1IntConsumer) {
      Objects.requireNonNull(param1IntConsumer);
      if (this.count == -2) {
        param1IntConsumer.accept(this.first);
        this.count = -1;
        return true;
      } 
      return false;
    }
    
    public void forEachRemaining(IntConsumer param1IntConsumer) {
      Objects.requireNonNull(param1IntConsumer);
      if (this.count == -2) {
        param1IntConsumer.accept(this.first);
        this.count = -1;
      } 
    }
  }
  
  static final class LongStreamBuilderImpl extends AbstractStreamBuilderImpl<Long, Spliterator.OfLong> implements LongStream.Builder, Spliterator.OfLong {
    long first;
    
    SpinedBuffer.OfLong buffer;
    
    LongStreamBuilderImpl() { super(null); }
    
    LongStreamBuilderImpl(long param1Long) {
      super(null);
      this.first = param1Long;
      this.count = -2;
    }
    
    public void accept(long param1Long) {
      if (this.count == 0) {
        this.first = param1Long;
        this.count++;
      } else if (this.count > 0) {
        if (this.buffer == null) {
          this.buffer = new SpinedBuffer.OfLong();
          this.buffer.accept(this.first);
          this.count++;
        } 
        this.buffer.accept(param1Long);
      } else {
        throw new IllegalStateException();
      } 
    }
    
    public LongStream build() {
      int i = this.count;
      if (i >= 0) {
        this.count = -this.count - 1;
        return (i < 2) ? StreamSupport.longStream(this, false) : StreamSupport.longStream(this.buffer.spliterator(), false);
      } 
      throw new IllegalStateException();
    }
    
    public boolean tryAdvance(LongConsumer param1LongConsumer) {
      Objects.requireNonNull(param1LongConsumer);
      if (this.count == -2) {
        param1LongConsumer.accept(this.first);
        this.count = -1;
        return true;
      } 
      return false;
    }
    
    public void forEachRemaining(LongConsumer param1LongConsumer) {
      Objects.requireNonNull(param1LongConsumer);
      if (this.count == -2) {
        param1LongConsumer.accept(this.first);
        this.count = -1;
      } 
    }
  }
  
  static final class RangeIntSpliterator implements Spliterator.OfInt {
    private int from;
    
    private final int upTo;
    
    private int last;
    
    private static final int BALANCED_SPLIT_THRESHOLD = 16777216;
    
    private static final int RIGHT_BALANCED_SPLIT_RATIO = 8;
    
    RangeIntSpliterator(int param1Int1, int param1Int2, boolean param1Boolean) { this(param1Int1, param1Int2, param1Boolean ? 1 : 0); }
    
    private RangeIntSpliterator(int param1Int1, int param1Int2, int param1Int3) {
      this.from = param1Int1;
      this.upTo = param1Int2;
      this.last = param1Int3;
    }
    
    public boolean tryAdvance(IntConsumer param1IntConsumer) {
      Objects.requireNonNull(param1IntConsumer);
      int i = this.from;
      if (i < this.upTo) {
        this.from++;
        param1IntConsumer.accept(i);
        return true;
      } 
      if (this.last > 0) {
        this.last = 0;
        param1IntConsumer.accept(i);
        return true;
      } 
      return false;
    }
    
    public void forEachRemaining(IntConsumer param1IntConsumer) {
      Objects.requireNonNull(param1IntConsumer);
      int i = this.from;
      int j = this.upTo;
      int k = this.last;
      this.from = this.upTo;
      this.last = 0;
      while (i < j)
        param1IntConsumer.accept(i++); 
      if (k > 0)
        param1IntConsumer.accept(i); 
    }
    
    public long estimateSize() { return this.upTo - this.from + this.last; }
    
    public int characteristics() { return 17749; }
    
    public Comparator<? super Integer> getComparator() { return null; }
    
    public Spliterator.OfInt trySplit() {
      long l = estimateSize();
      return (l <= 1L) ? null : new RangeIntSpliterator(this.from, this.from += splitPoint(l), 0);
    }
    
    private int splitPoint(long param1Long) {
      byte b = (param1Long < 16777216L) ? 2 : 8;
      return (int)(param1Long / b);
    }
  }
  
  static final class RangeLongSpliterator implements Spliterator.OfLong {
    private long from;
    
    private final long upTo;
    
    private int last;
    
    private static final long BALANCED_SPLIT_THRESHOLD = 16777216L;
    
    private static final long RIGHT_BALANCED_SPLIT_RATIO = 8L;
    
    RangeLongSpliterator(long param1Long1, long param1Long2, boolean param1Boolean) { this(param1Long1, param1Long2, param1Boolean ? 1 : 0); }
    
    private RangeLongSpliterator(long param1Long1, long param1Long2, int param1Int) {
      assert param1Long2 - param1Long1 + param1Int > 0L;
      this.from = param1Long1;
      this.upTo = param1Long2;
      this.last = param1Int;
    }
    
    public boolean tryAdvance(LongConsumer param1LongConsumer) {
      Objects.requireNonNull(param1LongConsumer);
      long l = this.from;
      if (l < this.upTo) {
        this.from++;
        param1LongConsumer.accept(l);
        return true;
      } 
      if (this.last > 0) {
        this.last = 0;
        param1LongConsumer.accept(l);
        return true;
      } 
      return false;
    }
    
    public void forEachRemaining(LongConsumer param1LongConsumer) { // Byte code:
      //   0: aload_1
      //   1: invokestatic requireNonNull : (Ljava/lang/Object;)Ljava/lang/Object;
      //   4: pop
      //   5: aload_0
      //   6: getfield from : J
      //   9: lstore_2
      //   10: aload_0
      //   11: getfield upTo : J
      //   14: lstore #4
      //   16: aload_0
      //   17: getfield last : I
      //   20: istore #6
      //   22: aload_0
      //   23: aload_0
      //   24: getfield upTo : J
      //   27: putfield from : J
      //   30: aload_0
      //   31: iconst_0
      //   32: putfield last : I
      //   35: lload_2
      //   36: lload #4
      //   38: lcmp
      //   39: ifge -> 56
      //   42: aload_1
      //   43: lload_2
      //   44: dup2
      //   45: lconst_1
      //   46: ladd
      //   47: lstore_2
      //   48: invokeinterface accept : (J)V
      //   53: goto -> 35
      //   56: iload #6
      //   58: ifle -> 68
      //   61: aload_1
      //   62: lload_2
      //   63: invokeinterface accept : (J)V
      //   68: return }
    
    public long estimateSize() { return this.upTo - this.from + this.last; }
    
    public int characteristics() { return 17749; }
    
    public Comparator<? super Long> getComparator() { return null; }
    
    public Spliterator.OfLong trySplit() {
      long l = estimateSize();
      return (l <= 1L) ? null : new RangeLongSpliterator(this.from, this.from += splitPoint(l), 0);
    }
    
    private long splitPoint(long param1Long) {
      long l = (param1Long < 16777216L) ? 2L : 8L;
      return param1Long / l;
    }
  }
  
  static final class StreamBuilderImpl<T> extends AbstractStreamBuilderImpl<T, Spliterator<T>> implements Stream.Builder<T> {
    T first;
    
    SpinedBuffer<T> buffer;
    
    StreamBuilderImpl() { super(null); }
    
    StreamBuilderImpl(T param1T) {
      super(null);
      this.first = param1T;
      this.count = -2;
    }
    
    public void accept(T param1T) {
      if (this.count == 0) {
        this.first = param1T;
        this.count++;
      } else if (this.count > 0) {
        if (this.buffer == null) {
          this.buffer = new SpinedBuffer();
          this.buffer.accept(this.first);
          this.count++;
        } 
        this.buffer.accept(param1T);
      } else {
        throw new IllegalStateException();
      } 
    }
    
    public Stream.Builder<T> add(T param1T) {
      accept(param1T);
      return this;
    }
    
    public Stream<T> build() {
      int i = this.count;
      if (i >= 0) {
        this.count = -this.count - 1;
        return (i < 2) ? StreamSupport.stream(this, false) : StreamSupport.stream(this.buffer.spliterator(), false);
      } 
      throw new IllegalStateException();
    }
    
    public boolean tryAdvance(Consumer<? super T> param1Consumer) {
      Objects.requireNonNull(param1Consumer);
      if (this.count == -2) {
        param1Consumer.accept(this.first);
        this.count = -1;
        return true;
      } 
      return false;
    }
    
    public void forEachRemaining(Consumer<? super T> param1Consumer) {
      Objects.requireNonNull(param1Consumer);
      if (this.count == -2) {
        param1Consumer.accept(this.first);
        this.count = -1;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\Streams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */