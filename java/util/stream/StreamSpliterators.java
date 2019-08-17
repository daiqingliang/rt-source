package java.util.stream;

import java.util.Comparator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongConsumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

class StreamSpliterators {
  private static abstract class AbstractWrappingSpliterator<P_IN, P_OUT, T_BUFFER extends AbstractSpinedBuffer> extends Object implements Spliterator<P_OUT> {
    final boolean isParallel;
    
    final PipelineHelper<P_OUT> ph;
    
    private Supplier<Spliterator<P_IN>> spliteratorSupplier;
    
    Spliterator<P_IN> spliterator;
    
    Sink<P_IN> bufferSink;
    
    BooleanSupplier pusher;
    
    long nextToConsume;
    
    T_BUFFER buffer;
    
    boolean finished;
    
    AbstractWrappingSpliterator(PipelineHelper<P_OUT> param1PipelineHelper, Supplier<Spliterator<P_IN>> param1Supplier, boolean param1Boolean) {
      this.ph = param1PipelineHelper;
      this.spliteratorSupplier = param1Supplier;
      this.spliterator = null;
      this.isParallel = param1Boolean;
    }
    
    AbstractWrappingSpliterator(PipelineHelper<P_OUT> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, boolean param1Boolean) {
      this.ph = param1PipelineHelper;
      this.spliteratorSupplier = null;
      this.spliterator = param1Spliterator;
      this.isParallel = param1Boolean;
    }
    
    final void init() {
      if (this.spliterator == null) {
        this.spliterator = (Spliterator)this.spliteratorSupplier.get();
        this.spliteratorSupplier = null;
      } 
    }
    
    final boolean doAdvance() {
      if (this.buffer == null) {
        if (this.finished)
          return false; 
        init();
        initPartialTraversalState();
        this.nextToConsume = 0L;
        this.bufferSink.begin(this.spliterator.getExactSizeIfKnown());
        return fillBuffer();
      } 
      this.nextToConsume++;
      boolean bool = (this.nextToConsume < this.buffer.count());
      if (!bool) {
        this.nextToConsume = 0L;
        this.buffer.clear();
        bool = fillBuffer();
      } 
      return bool;
    }
    
    abstract AbstractWrappingSpliterator<P_IN, P_OUT, ?> wrap(Spliterator<P_IN> param1Spliterator);
    
    abstract void initPartialTraversalState();
    
    public Spliterator<P_OUT> trySplit() {
      if (this.isParallel && !this.finished) {
        init();
        Spliterator spliterator1 = this.spliterator.trySplit();
        return (spliterator1 == null) ? null : wrap(spliterator1);
      } 
      return null;
    }
    
    private boolean fillBuffer() {
      while (this.buffer.count() == 0L) {
        if (this.bufferSink.cancellationRequested() || !this.pusher.getAsBoolean()) {
          if (this.finished)
            return false; 
          this.bufferSink.end();
          this.finished = true;
        } 
      } 
      return true;
    }
    
    public final long estimateSize() {
      init();
      return this.spliterator.estimateSize();
    }
    
    public final long getExactSizeIfKnown() {
      init();
      return StreamOpFlag.SIZED.isKnown(this.ph.getStreamAndOpFlags()) ? this.spliterator.getExactSizeIfKnown() : -1L;
    }
    
    public final int characteristics() {
      init();
      int i = StreamOpFlag.toCharacteristics(StreamOpFlag.toStreamFlags(this.ph.getStreamAndOpFlags()));
      if ((i & 0x40) != 0) {
        i &= 0xFFFFBFBF;
        i |= this.spliterator.characteristics() & 0x4040;
      } 
      return i;
    }
    
    public Comparator<? super P_OUT> getComparator() {
      if (!hasCharacteristics(4))
        throw new IllegalStateException(); 
      return null;
    }
    
    public final String toString() { return String.format("%s[%s]", new Object[] { getClass().getName(), this.spliterator }); }
  }
  
  static abstract class ArrayBuffer {
    int index;
    
    void reset() { this.index = 0; }
    
    static final class OfDouble extends OfPrimitive<DoubleConsumer> implements DoubleConsumer {
      final double[] array;
      
      OfDouble(int param2Int) { this.array = new double[param2Int]; }
      
      public void accept(double param2Double) { this.array[this.index++] = param2Double; }
      
      void forEach(DoubleConsumer param2DoubleConsumer, long param2Long) {
        for (byte b = 0; b < param2Long; b++)
          param2DoubleConsumer.accept(this.array[b]); 
      }
    }
    
    static final class OfInt extends OfPrimitive<IntConsumer> implements IntConsumer {
      final int[] array;
      
      OfInt(int param2Int) { this.array = new int[param2Int]; }
      
      public void accept(int param2Int) { this.array[this.index++] = param2Int; }
      
      public void forEach(IntConsumer param2IntConsumer, long param2Long) {
        for (byte b = 0; b < param2Long; b++)
          param2IntConsumer.accept(this.array[b]); 
      }
    }
    
    static final class OfLong extends OfPrimitive<LongConsumer> implements LongConsumer {
      final long[] array;
      
      OfLong(int param2Int) { this.array = new long[param2Int]; }
      
      public void accept(long param2Long) { this.array[this.index++] = param2Long; }
      
      public void forEach(LongConsumer param2LongConsumer, long param2Long) {
        for (byte b = 0; b < param2Long; b++)
          param2LongConsumer.accept(this.array[b]); 
      }
    }
    
    static abstract class OfPrimitive<T_CONS> extends ArrayBuffer {
      int index;
      
      void reset() { this.index = 0; }
      
      abstract void forEach(T_CONS param2T_CONS, long param2Long);
    }
    
    static final class OfRef<T> extends ArrayBuffer implements Consumer<T> {
      final Object[] array;
      
      OfRef(int param2Int) { this.array = new Object[param2Int]; }
      
      public void accept(T param2T) { this.array[this.index++] = param2T; }
      
      public void forEach(Consumer<? super T> param2Consumer, long param2Long) {
        for (byte b = 0; b < param2Long; b++) {
          Object object = this.array[b];
          param2Consumer.accept(object);
        } 
      }
    }
  }
  
  static class DelegatingSpliterator<T, T_SPLITR extends Spliterator<T>> extends Object implements Spliterator<T> {
    private final Supplier<? extends T_SPLITR> supplier;
    
    private T_SPLITR s;
    
    DelegatingSpliterator(Supplier<? extends T_SPLITR> param1Supplier) { this.supplier = param1Supplier; }
    
    T_SPLITR get() {
      if (this.s == null)
        this.s = (Spliterator)this.supplier.get(); 
      return (T_SPLITR)this.s;
    }
    
    public T_SPLITR trySplit() { return (T_SPLITR)get().trySplit(); }
    
    public boolean tryAdvance(Consumer<? super T> param1Consumer) { return get().tryAdvance(param1Consumer); }
    
    public void forEachRemaining(Consumer<? super T> param1Consumer) { get().forEachRemaining(param1Consumer); }
    
    public long estimateSize() { return get().estimateSize(); }
    
    public int characteristics() { return get().characteristics(); }
    
    public Comparator<? super T> getComparator() { return get().getComparator(); }
    
    public long getExactSizeIfKnown() { return get().getExactSizeIfKnown(); }
    
    public String toString() { return getClass().getName() + "[" + get() + "]"; }
    
    static final class OfDouble extends OfPrimitive<Double, DoubleConsumer, Spliterator.OfDouble> implements Spliterator.OfDouble {
      OfDouble(Supplier<Spliterator.OfDouble> param2Supplier) { super(param2Supplier); }
    }
    
    static final class OfInt extends OfPrimitive<Integer, IntConsumer, Spliterator.OfInt> implements Spliterator.OfInt {
      OfInt(Supplier<Spliterator.OfInt> param2Supplier) { super(param2Supplier); }
    }
    
    static final class OfLong extends OfPrimitive<Long, LongConsumer, Spliterator.OfLong> implements Spliterator.OfLong {
      OfLong(Supplier<Spliterator.OfLong> param2Supplier) { super(param2Supplier); }
    }
    
    static class OfPrimitive<T, T_CONS, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>> extends DelegatingSpliterator<T, T_SPLITR> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
      OfPrimitive(Supplier<? extends T_SPLITR> param2Supplier) { super(param2Supplier); }
      
      public boolean tryAdvance(T_CONS param2T_CONS) { return ((Spliterator.OfPrimitive)get()).tryAdvance(param2T_CONS); }
      
      public void forEachRemaining(T_CONS param2T_CONS) { ((Spliterator.OfPrimitive)get()).forEachRemaining(param2T_CONS); }
    }
  }
  
  static final class DistinctSpliterator<T> extends Object implements Spliterator<T>, Consumer<T> {
    private static final Object NULL_VALUE = new Object();
    
    private final Spliterator<T> s;
    
    private final ConcurrentHashMap<T, Boolean> seen;
    
    private T tmpSlot;
    
    DistinctSpliterator(Spliterator<T> param1Spliterator) { this(param1Spliterator, new ConcurrentHashMap()); }
    
    private DistinctSpliterator(Spliterator<T> param1Spliterator, ConcurrentHashMap<T, Boolean> param1ConcurrentHashMap) {
      this.s = param1Spliterator;
      this.seen = param1ConcurrentHashMap;
    }
    
    public void accept(T param1T) { this.tmpSlot = param1T; }
    
    private T mapNull(T param1T) { return (param1T != null) ? param1T : NULL_VALUE; }
    
    public boolean tryAdvance(Consumer<? super T> param1Consumer) {
      while (this.s.tryAdvance(this)) {
        if (this.seen.putIfAbsent(mapNull(this.tmpSlot), Boolean.TRUE) == null) {
          param1Consumer.accept(this.tmpSlot);
          this.tmpSlot = null;
          return true;
        } 
      } 
      return false;
    }
    
    public void forEachRemaining(Consumer<? super T> param1Consumer) { this.s.forEachRemaining(param1Object -> {
            if (this.seen.putIfAbsent(mapNull(param1Object), Boolean.TRUE) == null)
              param1Consumer.accept(param1Object); 
          }); }
    
    public Spliterator<T> trySplit() {
      Spliterator spliterator = this.s.trySplit();
      return (spliterator != null) ? new DistinctSpliterator(spliterator, this.seen) : null;
    }
    
    public long estimateSize() { return this.s.estimateSize(); }
    
    public int characteristics() { return this.s.characteristics() & 0xFFFFBFAB | true; }
    
    public Comparator<? super T> getComparator() { return this.s.getComparator(); }
  }
  
  static final class DoubleWrappingSpliterator<P_IN> extends AbstractWrappingSpliterator<P_IN, Double, SpinedBuffer.OfDouble> implements Spliterator.OfDouble {
    DoubleWrappingSpliterator(PipelineHelper<Double> param1PipelineHelper, Supplier<Spliterator<P_IN>> param1Supplier, boolean param1Boolean) { super(param1PipelineHelper, param1Supplier, param1Boolean); }
    
    DoubleWrappingSpliterator(PipelineHelper<Double> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, boolean param1Boolean) { super(param1PipelineHelper, param1Spliterator, param1Boolean); }
    
    StreamSpliterators.AbstractWrappingSpliterator<P_IN, Double, ?> wrap(Spliterator<P_IN> param1Spliterator) { return new DoubleWrappingSpliterator(this.ph, param1Spliterator, this.isParallel); }
    
    void initPartialTraversalState() {
      SpinedBuffer.OfDouble ofDouble = new SpinedBuffer.OfDouble();
      this.buffer = ofDouble;
      this.bufferSink = this.ph.wrapSink(ofDouble::accept);
      this.pusher = (() -> this.spliterator.tryAdvance(this.bufferSink));
    }
    
    public Spliterator.OfDouble trySplit() { return (Spliterator.OfDouble)super.trySplit(); }
    
    public boolean tryAdvance(DoubleConsumer param1DoubleConsumer) {
      Objects.requireNonNull(param1DoubleConsumer);
      boolean bool = doAdvance();
      if (bool)
        param1DoubleConsumer.accept(((SpinedBuffer.OfDouble)this.buffer).get(this.nextToConsume)); 
      return bool;
    }
    
    public void forEachRemaining(DoubleConsumer param1DoubleConsumer) {
      if (this.buffer == null && !this.finished) {
        Objects.requireNonNull(param1DoubleConsumer);
        init();
        this.ph.wrapAndCopyInto(param1DoubleConsumer::accept, this.spliterator);
        this.finished = true;
      } else {
        do {
        
        } while (tryAdvance(param1DoubleConsumer));
      } 
    }
  }
  
  static abstract class InfiniteSupplyingSpliterator<T> extends Object implements Spliterator<T> {
    long estimate;
    
    protected InfiniteSupplyingSpliterator(long param1Long) { this.estimate = param1Long; }
    
    public long estimateSize() { return this.estimate; }
    
    public int characteristics() { return 1024; }
    
    static final class OfDouble extends InfiniteSupplyingSpliterator<Double> implements Spliterator.OfDouble {
      final DoubleSupplier s;
      
      OfDouble(long param2Long, DoubleSupplier param2DoubleSupplier) {
        super(param2Long);
        this.s = param2DoubleSupplier;
      }
      
      public boolean tryAdvance(DoubleConsumer param2DoubleConsumer) {
        Objects.requireNonNull(param2DoubleConsumer);
        param2DoubleConsumer.accept(this.s.getAsDouble());
        return true;
      }
      
      public Spliterator.OfDouble trySplit() { return (this.estimate == 0L) ? null : new OfDouble(this.estimate >>>= true, this.s); }
    }
    
    static final class OfInt extends InfiniteSupplyingSpliterator<Integer> implements Spliterator.OfInt {
      final IntSupplier s;
      
      OfInt(long param2Long, IntSupplier param2IntSupplier) {
        super(param2Long);
        this.s = param2IntSupplier;
      }
      
      public boolean tryAdvance(IntConsumer param2IntConsumer) {
        Objects.requireNonNull(param2IntConsumer);
        param2IntConsumer.accept(this.s.getAsInt());
        return true;
      }
      
      public Spliterator.OfInt trySplit() { return (this.estimate == 0L) ? null : new OfInt(this.estimate >>>= true, this.s); }
    }
    
    static final class OfLong extends InfiniteSupplyingSpliterator<Long> implements Spliterator.OfLong {
      final LongSupplier s;
      
      OfLong(long param2Long, LongSupplier param2LongSupplier) {
        super(param2Long);
        this.s = param2LongSupplier;
      }
      
      public boolean tryAdvance(LongConsumer param2LongConsumer) {
        Objects.requireNonNull(param2LongConsumer);
        param2LongConsumer.accept(this.s.getAsLong());
        return true;
      }
      
      public Spliterator.OfLong trySplit() { return (this.estimate == 0L) ? null : new OfLong(this.estimate >>>= true, this.s); }
    }
    
    static final class OfRef<T> extends InfiniteSupplyingSpliterator<T> {
      final Supplier<T> s;
      
      OfRef(long param2Long, Supplier<T> param2Supplier) {
        super(param2Long);
        this.s = param2Supplier;
      }
      
      public boolean tryAdvance(Consumer<? super T> param2Consumer) {
        Objects.requireNonNull(param2Consumer);
        param2Consumer.accept(this.s.get());
        return true;
      }
      
      public Spliterator<T> trySplit() { return (this.estimate == 0L) ? null : new OfRef(this.estimate >>>= true, this.s); }
    }
  }
  
  static final class IntWrappingSpliterator<P_IN> extends AbstractWrappingSpliterator<P_IN, Integer, SpinedBuffer.OfInt> implements Spliterator.OfInt {
    IntWrappingSpliterator(PipelineHelper<Integer> param1PipelineHelper, Supplier<Spliterator<P_IN>> param1Supplier, boolean param1Boolean) { super(param1PipelineHelper, param1Supplier, param1Boolean); }
    
    IntWrappingSpliterator(PipelineHelper<Integer> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, boolean param1Boolean) { super(param1PipelineHelper, param1Spliterator, param1Boolean); }
    
    StreamSpliterators.AbstractWrappingSpliterator<P_IN, Integer, ?> wrap(Spliterator<P_IN> param1Spliterator) { return new IntWrappingSpliterator(this.ph, param1Spliterator, this.isParallel); }
    
    void initPartialTraversalState() {
      SpinedBuffer.OfInt ofInt = new SpinedBuffer.OfInt();
      this.buffer = ofInt;
      this.bufferSink = this.ph.wrapSink(ofInt::accept);
      this.pusher = (() -> this.spliterator.tryAdvance(this.bufferSink));
    }
    
    public Spliterator.OfInt trySplit() { return (Spliterator.OfInt)super.trySplit(); }
    
    public boolean tryAdvance(IntConsumer param1IntConsumer) {
      Objects.requireNonNull(param1IntConsumer);
      boolean bool = doAdvance();
      if (bool)
        param1IntConsumer.accept(((SpinedBuffer.OfInt)this.buffer).get(this.nextToConsume)); 
      return bool;
    }
    
    public void forEachRemaining(IntConsumer param1IntConsumer) {
      if (this.buffer == null && !this.finished) {
        Objects.requireNonNull(param1IntConsumer);
        init();
        this.ph.wrapAndCopyInto(param1IntConsumer::accept, this.spliterator);
        this.finished = true;
      } else {
        do {
        
        } while (tryAdvance(param1IntConsumer));
      } 
    }
  }
  
  static final class LongWrappingSpliterator<P_IN> extends AbstractWrappingSpliterator<P_IN, Long, SpinedBuffer.OfLong> implements Spliterator.OfLong {
    LongWrappingSpliterator(PipelineHelper<Long> param1PipelineHelper, Supplier<Spliterator<P_IN>> param1Supplier, boolean param1Boolean) { super(param1PipelineHelper, param1Supplier, param1Boolean); }
    
    LongWrappingSpliterator(PipelineHelper<Long> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, boolean param1Boolean) { super(param1PipelineHelper, param1Spliterator, param1Boolean); }
    
    StreamSpliterators.AbstractWrappingSpliterator<P_IN, Long, ?> wrap(Spliterator<P_IN> param1Spliterator) { return new LongWrappingSpliterator(this.ph, param1Spliterator, this.isParallel); }
    
    void initPartialTraversalState() {
      SpinedBuffer.OfLong ofLong = new SpinedBuffer.OfLong();
      this.buffer = ofLong;
      this.bufferSink = this.ph.wrapSink(ofLong::accept);
      this.pusher = (() -> this.spliterator.tryAdvance(this.bufferSink));
    }
    
    public Spliterator.OfLong trySplit() { return (Spliterator.OfLong)super.trySplit(); }
    
    public boolean tryAdvance(LongConsumer param1LongConsumer) {
      Objects.requireNonNull(param1LongConsumer);
      boolean bool = doAdvance();
      if (bool)
        param1LongConsumer.accept(((SpinedBuffer.OfLong)this.buffer).get(this.nextToConsume)); 
      return bool;
    }
    
    public void forEachRemaining(LongConsumer param1LongConsumer) {
      if (this.buffer == null && !this.finished) {
        Objects.requireNonNull(param1LongConsumer);
        init();
        this.ph.wrapAndCopyInto(param1LongConsumer::accept, this.spliterator);
        this.finished = true;
      } else {
        do {
        
        } while (tryAdvance(param1LongConsumer));
      } 
    }
  }
  
  static abstract class SliceSpliterator<T, T_SPLITR extends Spliterator<T>> extends Object {
    final long sliceOrigin;
    
    final long sliceFence;
    
    T_SPLITR s;
    
    long index;
    
    long fence;
    
    SliceSpliterator(T_SPLITR param1T_SPLITR, long param1Long1, long param1Long2, long param1Long3, long param1Long4) {
      assert param1T_SPLITR.hasCharacteristics(16384);
      this.s = param1T_SPLITR;
      this.sliceOrigin = param1Long1;
      this.sliceFence = param1Long2;
      this.index = param1Long3;
      this.fence = param1Long4;
    }
    
    protected abstract T_SPLITR makeSpliterator(T_SPLITR param1T_SPLITR, long param1Long1, long param1Long2, long param1Long3, long param1Long4);
    
    public T_SPLITR trySplit() {
      long l2;
      long l1;
      Spliterator spliterator;
      if (this.sliceOrigin >= this.fence)
        return null; 
      if (this.index >= this.fence)
        return null; 
      while (true) {
        spliterator = this.s.trySplit();
        if (spliterator == null)
          return null; 
        l1 = this.index + spliterator.estimateSize();
        l2 = Math.min(l1, this.sliceFence);
        if (this.sliceOrigin >= l2) {
          this.index = l2;
          continue;
        } 
        if (l2 >= this.sliceFence) {
          this.s = spliterator;
          this.fence = l2;
          continue;
        } 
        break;
      } 
      if (this.index >= this.sliceOrigin && l1 <= this.sliceFence) {
        this.index = l2;
        return (T_SPLITR)spliterator;
      } 
      return (T_SPLITR)makeSpliterator(spliterator, this.sliceOrigin, this.sliceFence, this.index, this.index = l2);
    }
    
    public long estimateSize() { return (this.sliceOrigin < this.fence) ? (this.fence - Math.max(this.sliceOrigin, this.index)) : 0L; }
    
    public int characteristics() { return this.s.characteristics(); }
    
    static final class OfDouble extends OfPrimitive<Double, Spliterator.OfDouble, DoubleConsumer> implements Spliterator.OfDouble {
      OfDouble(Spliterator.OfDouble param2OfDouble, long param2Long1, long param2Long2) { super(param2OfDouble, param2Long1, param2Long2); }
      
      OfDouble(Spliterator.OfDouble param2OfDouble, long param2Long1, long param2Long2, long param2Long3, long param2Long4) { super(param2OfDouble, param2Long1, param2Long2, param2Long3, param2Long4, null); }
      
      protected Spliterator.OfDouble makeSpliterator(Spliterator.OfDouble param2OfDouble, long param2Long1, long param2Long2, long param2Long3, long param2Long4) { return new OfDouble(param2OfDouble, param2Long1, param2Long2, param2Long3, param2Long4); }
      
      protected DoubleConsumer emptyConsumer() { return param2Double -> {
          
          }; }
    }
    
    static final class OfInt extends OfPrimitive<Integer, Spliterator.OfInt, IntConsumer> implements Spliterator.OfInt {
      OfInt(Spliterator.OfInt param2OfInt, long param2Long1, long param2Long2) { super(param2OfInt, param2Long1, param2Long2); }
      
      OfInt(Spliterator.OfInt param2OfInt, long param2Long1, long param2Long2, long param2Long3, long param2Long4) { super(param2OfInt, param2Long1, param2Long2, param2Long3, param2Long4, null); }
      
      protected Spliterator.OfInt makeSpliterator(Spliterator.OfInt param2OfInt, long param2Long1, long param2Long2, long param2Long3, long param2Long4) { return new OfInt(param2OfInt, param2Long1, param2Long2, param2Long3, param2Long4); }
      
      protected IntConsumer emptyConsumer() { return param2Int -> {
          
          }; }
    }
    
    static final class OfLong extends OfPrimitive<Long, Spliterator.OfLong, LongConsumer> implements Spliterator.OfLong {
      OfLong(Spliterator.OfLong param2OfLong, long param2Long1, long param2Long2) { super(param2OfLong, param2Long1, param2Long2); }
      
      OfLong(Spliterator.OfLong param2OfLong, long param2Long1, long param2Long2, long param2Long3, long param2Long4) { super(param2OfLong, param2Long1, param2Long2, param2Long3, param2Long4, null); }
      
      protected Spliterator.OfLong makeSpliterator(Spliterator.OfLong param2OfLong, long param2Long1, long param2Long2, long param2Long3, long param2Long4) { return new OfLong(param2OfLong, param2Long1, param2Long2, param2Long3, param2Long4); }
      
      protected LongConsumer emptyConsumer() { return param2Long -> {
          
          }; }
    }
    
    static abstract class OfPrimitive<T, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>, T_CONS> extends SliceSpliterator<T, T_SPLITR> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
      OfPrimitive(T_SPLITR param2T_SPLITR, long param2Long1, long param2Long2) { this(param2T_SPLITR, param2Long1, param2Long2, 0L, Math.min(param2T_SPLITR.estimateSize(), param2Long2)); }
      
      private OfPrimitive(T_SPLITR param2T_SPLITR, long param2Long1, long param2Long2, long param2Long3, long param2Long4) { super(param2T_SPLITR, param2Long1, param2Long2, param2Long3, param2Long4); }
      
      public boolean tryAdvance(T_CONS param2T_CONS) {
        Objects.requireNonNull(param2T_CONS);
        if (this.sliceOrigin >= this.fence)
          return false; 
        while (this.sliceOrigin > this.index) {
          ((Spliterator.OfPrimitive)this.s).tryAdvance(emptyConsumer());
          this.index++;
        } 
        if (this.index >= this.fence)
          return false; 
        this.index++;
        return ((Spliterator.OfPrimitive)this.s).tryAdvance(param2T_CONS);
      }
      
      public void forEachRemaining(T_CONS param2T_CONS) {
        Objects.requireNonNull(param2T_CONS);
        if (this.sliceOrigin >= this.fence)
          return; 
        if (this.index >= this.fence)
          return; 
        if (this.index >= this.sliceOrigin && this.index + ((Spliterator.OfPrimitive)this.s).estimateSize() <= this.sliceFence) {
          ((Spliterator.OfPrimitive)this.s).forEachRemaining(param2T_CONS);
          this.index = this.fence;
        } else {
          while (this.sliceOrigin > this.index) {
            ((Spliterator.OfPrimitive)this.s).tryAdvance(emptyConsumer());
            this.index++;
          } 
          while (this.index < this.fence) {
            ((Spliterator.OfPrimitive)this.s).tryAdvance(param2T_CONS);
            this.index++;
          } 
        } 
      }
      
      protected abstract T_CONS emptyConsumer();
    }
    
    static final class OfRef<T> extends SliceSpliterator<T, Spliterator<T>> implements Spliterator<T> {
      OfRef(Spliterator<T> param2Spliterator, long param2Long1, long param2Long2) { this(param2Spliterator, param2Long1, param2Long2, 0L, Math.min(param2Spliterator.estimateSize(), param2Long2)); }
      
      private OfRef(Spliterator<T> param2Spliterator, long param2Long1, long param2Long2, long param2Long3, long param2Long4) { super(param2Spliterator, param2Long1, param2Long2, param2Long3, param2Long4); }
      
      protected Spliterator<T> makeSpliterator(Spliterator<T> param2Spliterator, long param2Long1, long param2Long2, long param2Long3, long param2Long4) { return new OfRef(param2Spliterator, param2Long1, param2Long2, param2Long3, param2Long4); }
      
      public boolean tryAdvance(Consumer<? super T> param2Consumer) {
        Objects.requireNonNull(param2Consumer);
        if (this.sliceOrigin >= this.fence)
          return false; 
        while (this.sliceOrigin > this.index) {
          this.s.tryAdvance(param2Object -> {
              
              });
          this.index++;
        } 
        if (this.index >= this.fence)
          return false; 
        this.index++;
        return this.s.tryAdvance(param2Consumer);
      }
      
      public void forEachRemaining(Consumer<? super T> param2Consumer) {
        Objects.requireNonNull(param2Consumer);
        if (this.sliceOrigin >= this.fence)
          return; 
        if (this.index >= this.fence)
          return; 
        if (this.index >= this.sliceOrigin && this.index + this.s.estimateSize() <= this.sliceFence) {
          this.s.forEachRemaining(param2Consumer);
          this.index = this.fence;
        } else {
          while (this.sliceOrigin > this.index) {
            this.s.tryAdvance(param2Object -> {
                
                });
            this.index++;
          } 
          while (this.index < this.fence) {
            this.s.tryAdvance(param2Consumer);
            this.index++;
          } 
        } 
      }
    }
  }
  
  static abstract class UnorderedSliceSpliterator<T, T_SPLITR extends Spliterator<T>> extends Object {
    static final int CHUNK_SIZE = 128;
    
    protected final T_SPLITR s;
    
    protected final boolean unlimited;
    
    private final long skipThreshold;
    
    private final AtomicLong permits;
    
    UnorderedSliceSpliterator(T_SPLITR param1T_SPLITR, long param1Long1, long param1Long2) {
      this.s = param1T_SPLITR;
      this.unlimited = (param1Long2 < 0L);
      this.skipThreshold = (param1Long2 >= 0L) ? param1Long2 : 0L;
      this.permits = new AtomicLong((param1Long2 >= 0L) ? (param1Long1 + param1Long2) : param1Long1);
    }
    
    UnorderedSliceSpliterator(T_SPLITR param1T_SPLITR, UnorderedSliceSpliterator<T, T_SPLITR> param1UnorderedSliceSpliterator) {
      this.s = param1T_SPLITR;
      this.unlimited = param1UnorderedSliceSpliterator.unlimited;
      this.permits = param1UnorderedSliceSpliterator.permits;
      this.skipThreshold = param1UnorderedSliceSpliterator.skipThreshold;
    }
    
    protected final long acquirePermits(long param1Long) {
      long l2;
      long l1;
      assert param1Long > 0L;
      do {
        l1 = this.permits.get();
        if (l1 == 0L)
          return this.unlimited ? param1Long : 0L; 
        l2 = Math.min(l1, param1Long);
      } while (l2 > 0L && !this.permits.compareAndSet(l1, l1 - l2));
      return this.unlimited ? Math.max(param1Long - l2, 0L) : ((l1 > this.skipThreshold) ? Math.max(l2 - l1 - this.skipThreshold, 0L) : l2);
    }
    
    protected final PermitStatus permitStatus() { return (this.permits.get() > 0L) ? PermitStatus.MAYBE_MORE : (this.unlimited ? PermitStatus.UNLIMITED : PermitStatus.NO_MORE); }
    
    public final T_SPLITR trySplit() {
      if (this.permits.get() == 0L)
        return null; 
      Spliterator spliterator = this.s.trySplit();
      return (T_SPLITR)((spliterator == null) ? null : makeSpliterator(spliterator));
    }
    
    protected abstract T_SPLITR makeSpliterator(T_SPLITR param1T_SPLITR);
    
    public final long estimateSize() { return this.s.estimateSize(); }
    
    public final int characteristics() { return this.s.characteristics() & 0xFFFFBFAF; }
    
    static final class OfDouble extends OfPrimitive<Double, DoubleConsumer, StreamSpliterators.ArrayBuffer.OfDouble, Spliterator.OfDouble> implements Spliterator.OfDouble, DoubleConsumer {
      double tmpValue;
      
      OfDouble(Spliterator.OfDouble param2OfDouble, long param2Long1, long param2Long2) { super(param2OfDouble, param2Long1, param2Long2); }
      
      OfDouble(Spliterator.OfDouble param2OfDouble, OfDouble param2OfDouble1) { super(param2OfDouble, param2OfDouble1); }
      
      public void accept(double param2Double) { this.tmpValue = param2Double; }
      
      protected void acceptConsumed(DoubleConsumer param2DoubleConsumer) { param2DoubleConsumer.accept(this.tmpValue); }
      
      protected StreamSpliterators.ArrayBuffer.OfDouble bufferCreate(int param2Int) { return new StreamSpliterators.ArrayBuffer.OfDouble(param2Int); }
      
      protected Spliterator.OfDouble makeSpliterator(Spliterator.OfDouble param2OfDouble) { return new OfDouble(param2OfDouble, this); }
    }
    
    static final class OfInt extends OfPrimitive<Integer, IntConsumer, StreamSpliterators.ArrayBuffer.OfInt, Spliterator.OfInt> implements Spliterator.OfInt, IntConsumer {
      int tmpValue;
      
      OfInt(Spliterator.OfInt param2OfInt, long param2Long1, long param2Long2) { super(param2OfInt, param2Long1, param2Long2); }
      
      OfInt(Spliterator.OfInt param2OfInt, OfInt param2OfInt1) { super(param2OfInt, param2OfInt1); }
      
      public void accept(int param2Int) { this.tmpValue = param2Int; }
      
      protected void acceptConsumed(IntConsumer param2IntConsumer) { param2IntConsumer.accept(this.tmpValue); }
      
      protected StreamSpliterators.ArrayBuffer.OfInt bufferCreate(int param2Int) { return new StreamSpliterators.ArrayBuffer.OfInt(param2Int); }
      
      protected Spliterator.OfInt makeSpliterator(Spliterator.OfInt param2OfInt) { return new OfInt(param2OfInt, this); }
    }
    
    static final class OfLong extends OfPrimitive<Long, LongConsumer, StreamSpliterators.ArrayBuffer.OfLong, Spliterator.OfLong> implements Spliterator.OfLong, LongConsumer {
      long tmpValue;
      
      OfLong(Spliterator.OfLong param2OfLong, long param2Long1, long param2Long2) { super(param2OfLong, param2Long1, param2Long2); }
      
      OfLong(Spliterator.OfLong param2OfLong, OfLong param2OfLong1) { super(param2OfLong, param2OfLong1); }
      
      public void accept(long param2Long) { this.tmpValue = param2Long; }
      
      protected void acceptConsumed(LongConsumer param2LongConsumer) { param2LongConsumer.accept(this.tmpValue); }
      
      protected StreamSpliterators.ArrayBuffer.OfLong bufferCreate(int param2Int) { return new StreamSpliterators.ArrayBuffer.OfLong(param2Int); }
      
      protected Spliterator.OfLong makeSpliterator(Spliterator.OfLong param2OfLong) { return new OfLong(param2OfLong, this); }
    }
    
    static abstract class OfPrimitive<T, T_CONS, T_BUFF extends StreamSpliterators.ArrayBuffer.OfPrimitive<T_CONS>, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>> extends UnorderedSliceSpliterator<T, T_SPLITR> implements Spliterator.OfPrimitive<T, T_CONS, T_SPLITR> {
      OfPrimitive(T_SPLITR param2T_SPLITR, long param2Long1, long param2Long2) { super(param2T_SPLITR, param2Long1, param2Long2); }
      
      OfPrimitive(T_SPLITR param2T_SPLITR, OfPrimitive<T, T_CONS, T_BUFF, T_SPLITR> param2OfPrimitive) { super(param2T_SPLITR, param2OfPrimitive); }
      
      public boolean tryAdvance(T_CONS param2T_CONS) {
        Objects.requireNonNull(param2T_CONS);
        OfPrimitive ofPrimitive = this;
        while (permitStatus() != StreamSpliterators.UnorderedSliceSpliterator.PermitStatus.NO_MORE) {
          if (!((Spliterator.OfPrimitive)this.s).tryAdvance(ofPrimitive))
            return false; 
          if (acquirePermits(1L) == 1L) {
            acceptConsumed(param2T_CONS);
            return true;
          } 
        } 
        return false;
      }
      
      protected abstract void acceptConsumed(T_CONS param2T_CONS);
      
      public void forEachRemaining(T_CONS param2T_CONS) {
        Objects.requireNonNull(param2T_CONS);
        StreamSpliterators.ArrayBuffer.OfPrimitive ofPrimitive = null;
        StreamSpliterators.UnorderedSliceSpliterator.PermitStatus permitStatus;
        while ((permitStatus = permitStatus()) != StreamSpliterators.UnorderedSliceSpliterator.PermitStatus.NO_MORE) {
          if (permitStatus == StreamSpliterators.UnorderedSliceSpliterator.PermitStatus.MAYBE_MORE) {
            if (ofPrimitive == null) {
              ofPrimitive = bufferCreate(128);
            } else {
              ofPrimitive.reset();
            } 
            StreamSpliterators.ArrayBuffer.OfPrimitive ofPrimitive1 = ofPrimitive;
            long l = 0L;
            do {
            
            } while (((Spliterator.OfPrimitive)this.s).tryAdvance(ofPrimitive1) && ++l < 128L);
            if (l == 0L)
              return; 
            ofPrimitive.forEach(param2T_CONS, acquirePermits(l));
            continue;
          } 
          ((Spliterator.OfPrimitive)this.s).forEachRemaining(param2T_CONS);
          return;
        } 
      }
      
      protected abstract T_BUFF bufferCreate(int param2Int);
    }
    
    static final class OfRef<T> extends UnorderedSliceSpliterator<T, Spliterator<T>> implements Spliterator<T>, Consumer<T> {
      T tmpSlot;
      
      OfRef(Spliterator<T> param2Spliterator, long param2Long1, long param2Long2) { super(param2Spliterator, param2Long1, param2Long2); }
      
      OfRef(Spliterator<T> param2Spliterator, OfRef<T> param2OfRef) { super(param2Spliterator, param2OfRef); }
      
      public final void accept(T param2T) { this.tmpSlot = param2T; }
      
      public boolean tryAdvance(Consumer<? super T> param2Consumer) {
        Objects.requireNonNull(param2Consumer);
        while (permitStatus() != StreamSpliterators.UnorderedSliceSpliterator.PermitStatus.NO_MORE) {
          if (!this.s.tryAdvance(this))
            return false; 
          if (acquirePermits(1L) == 1L) {
            param2Consumer.accept(this.tmpSlot);
            this.tmpSlot = null;
            return true;
          } 
        } 
        return false;
      }
      
      public void forEachRemaining(Consumer<? super T> param2Consumer) {
        Objects.requireNonNull(param2Consumer);
        StreamSpliterators.ArrayBuffer.OfRef ofRef = null;
        StreamSpliterators.UnorderedSliceSpliterator.PermitStatus permitStatus;
        while ((permitStatus = permitStatus()) != StreamSpliterators.UnorderedSliceSpliterator.PermitStatus.NO_MORE) {
          if (permitStatus == StreamSpliterators.UnorderedSliceSpliterator.PermitStatus.MAYBE_MORE) {
            if (ofRef == null) {
              ofRef = new StreamSpliterators.ArrayBuffer.OfRef(128);
            } else {
              ofRef.reset();
            } 
            long l = 0L;
            do {
            
            } while (this.s.tryAdvance(ofRef) && ++l < 128L);
            if (l == 0L)
              return; 
            ofRef.forEach(param2Consumer, acquirePermits(l));
            continue;
          } 
          this.s.forEachRemaining(param2Consumer);
          return;
        } 
      }
      
      protected Spliterator<T> makeSpliterator(Spliterator<T> param2Spliterator) { return new OfRef(param2Spliterator, this); }
    }
    
    enum PermitStatus {
      NO_MORE, MAYBE_MORE, UNLIMITED;
    }
  }
  
  static final class WrappingSpliterator<P_IN, P_OUT> extends AbstractWrappingSpliterator<P_IN, P_OUT, SpinedBuffer<P_OUT>> {
    WrappingSpliterator(PipelineHelper<P_OUT> param1PipelineHelper, Supplier<Spliterator<P_IN>> param1Supplier, boolean param1Boolean) { super(param1PipelineHelper, param1Supplier, param1Boolean); }
    
    WrappingSpliterator(PipelineHelper<P_OUT> param1PipelineHelper, Spliterator<P_IN> param1Spliterator, boolean param1Boolean) { super(param1PipelineHelper, param1Spliterator, param1Boolean); }
    
    WrappingSpliterator<P_IN, P_OUT> wrap(Spliterator<P_IN> param1Spliterator) { return new WrappingSpliterator(this.ph, param1Spliterator, this.isParallel); }
    
    void initPartialTraversalState() {
      SpinedBuffer spinedBuffer = new SpinedBuffer();
      this.buffer = spinedBuffer;
      this.bufferSink = this.ph.wrapSink(spinedBuffer::accept);
      this.pusher = (() -> this.spliterator.tryAdvance(this.bufferSink));
    }
    
    public boolean tryAdvance(Consumer<? super P_OUT> param1Consumer) {
      Objects.requireNonNull(param1Consumer);
      boolean bool = doAdvance();
      if (bool)
        param1Consumer.accept(((SpinedBuffer)this.buffer).get(this.nextToConsume)); 
      return bool;
    }
    
    public void forEachRemaining(Consumer<? super P_OUT> param1Consumer) {
      if (this.buffer == null && !this.finished) {
        Objects.requireNonNull(param1Consumer);
        init();
        this.ph.wrapAndCopyInto(param1Consumer::accept, this.spliterator);
        this.finished = true;
      } else {
        do {
        
        } while (tryAdvance(param1Consumer));
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\StreamSpliterators.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */