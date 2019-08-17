package java.util;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public final class Spliterators {
  private static final Spliterator<Object> EMPTY_SPLITERATOR = new EmptySpliterator.OfRef();
  
  private static final Spliterator.OfInt EMPTY_INT_SPLITERATOR = new EmptySpliterator.OfInt();
  
  private static final Spliterator.OfLong EMPTY_LONG_SPLITERATOR = new EmptySpliterator.OfLong();
  
  private static final Spliterator.OfDouble EMPTY_DOUBLE_SPLITERATOR = new EmptySpliterator.OfDouble();
  
  public static <T> Spliterator<T> emptySpliterator() { return EMPTY_SPLITERATOR; }
  
  public static Spliterator.OfInt emptyIntSpliterator() { return EMPTY_INT_SPLITERATOR; }
  
  public static Spliterator.OfLong emptyLongSpliterator() { return EMPTY_LONG_SPLITERATOR; }
  
  public static Spliterator.OfDouble emptyDoubleSpliterator() { return EMPTY_DOUBLE_SPLITERATOR; }
  
  public static <T> Spliterator<T> spliterator(Object[] paramArrayOfObject, int paramInt) { return new ArraySpliterator((Object[])Objects.requireNonNull(paramArrayOfObject), paramInt); }
  
  public static <T> Spliterator<T> spliterator(Object[] paramArrayOfObject, int paramInt1, int paramInt2, int paramInt3) {
    checkFromToBounds((Object[])Objects.requireNonNull(paramArrayOfObject).length, paramInt1, paramInt2);
    return new ArraySpliterator(paramArrayOfObject, paramInt1, paramInt2, paramInt3);
  }
  
  public static Spliterator.OfInt spliterator(int[] paramArrayOfInt, int paramInt) { return new IntArraySpliterator((int[])Objects.requireNonNull(paramArrayOfInt), paramInt); }
  
  public static Spliterator.OfInt spliterator(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3) {
    checkFromToBounds((int[])Objects.requireNonNull(paramArrayOfInt).length, paramInt1, paramInt2);
    return new IntArraySpliterator(paramArrayOfInt, paramInt1, paramInt2, paramInt3);
  }
  
  public static Spliterator.OfLong spliterator(long[] paramArrayOfLong, int paramInt) { return new LongArraySpliterator((long[])Objects.requireNonNull(paramArrayOfLong), paramInt); }
  
  public static Spliterator.OfLong spliterator(long[] paramArrayOfLong, int paramInt1, int paramInt2, int paramInt3) {
    checkFromToBounds((long[])Objects.requireNonNull(paramArrayOfLong).length, paramInt1, paramInt2);
    return new LongArraySpliterator(paramArrayOfLong, paramInt1, paramInt2, paramInt3);
  }
  
  public static Spliterator.OfDouble spliterator(double[] paramArrayOfDouble, int paramInt) { return new DoubleArraySpliterator((double[])Objects.requireNonNull(paramArrayOfDouble), paramInt); }
  
  public static Spliterator.OfDouble spliterator(double[] paramArrayOfDouble, int paramInt1, int paramInt2, int paramInt3) {
    checkFromToBounds((double[])Objects.requireNonNull(paramArrayOfDouble).length, paramInt1, paramInt2);
    return new DoubleArraySpliterator(paramArrayOfDouble, paramInt1, paramInt2, paramInt3);
  }
  
  private static void checkFromToBounds(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 > paramInt3)
      throw new ArrayIndexOutOfBoundsException("origin(" + paramInt2 + ") > fence(" + paramInt3 + ")"); 
    if (paramInt2 < 0)
      throw new ArrayIndexOutOfBoundsException(paramInt2); 
    if (paramInt3 > paramInt1)
      throw new ArrayIndexOutOfBoundsException(paramInt3); 
  }
  
  public static <T> Spliterator<T> spliterator(Collection<? extends T> paramCollection, int paramInt) { return new IteratorSpliterator((Collection)Objects.requireNonNull(paramCollection), paramInt); }
  
  public static <T> Spliterator<T> spliterator(Iterator<? extends T> paramIterator, long paramLong, int paramInt) { return new IteratorSpliterator((Iterator)Objects.requireNonNull(paramIterator), paramLong, paramInt); }
  
  public static <T> Spliterator<T> spliteratorUnknownSize(Iterator<? extends T> paramIterator, int paramInt) { return new IteratorSpliterator((Iterator)Objects.requireNonNull(paramIterator), paramInt); }
  
  public static Spliterator.OfInt spliterator(PrimitiveIterator.OfInt paramOfInt, long paramLong, int paramInt) { return new IntIteratorSpliterator((PrimitiveIterator.OfInt)Objects.requireNonNull(paramOfInt), paramLong, paramInt); }
  
  public static Spliterator.OfInt spliteratorUnknownSize(PrimitiveIterator.OfInt paramOfInt, int paramInt) { return new IntIteratorSpliterator((PrimitiveIterator.OfInt)Objects.requireNonNull(paramOfInt), paramInt); }
  
  public static Spliterator.OfLong spliterator(PrimitiveIterator.OfLong paramOfLong, long paramLong, int paramInt) { return new LongIteratorSpliterator((PrimitiveIterator.OfLong)Objects.requireNonNull(paramOfLong), paramLong, paramInt); }
  
  public static Spliterator.OfLong spliteratorUnknownSize(PrimitiveIterator.OfLong paramOfLong, int paramInt) { return new LongIteratorSpliterator((PrimitiveIterator.OfLong)Objects.requireNonNull(paramOfLong), paramInt); }
  
  public static Spliterator.OfDouble spliterator(PrimitiveIterator.OfDouble paramOfDouble, long paramLong, int paramInt) { return new DoubleIteratorSpliterator((PrimitiveIterator.OfDouble)Objects.requireNonNull(paramOfDouble), paramLong, paramInt); }
  
  public static Spliterator.OfDouble spliteratorUnknownSize(PrimitiveIterator.OfDouble paramOfDouble, int paramInt) { return new DoubleIteratorSpliterator((PrimitiveIterator.OfDouble)Objects.requireNonNull(paramOfDouble), paramInt); }
  
  public static <T> Iterator<T> iterator(final Spliterator<? extends T> spliterator) {
    Objects.requireNonNull(paramSpliterator);
    class Adapter extends Object implements Iterator<T>, Consumer<T> {
      boolean valueReady = false;
      
      T nextElement;
      
      public void accept(T param1T) {
        this.valueReady = true;
        this.nextElement = param1T;
      }
      
      public boolean hasNext() {
        if (!this.valueReady)
          spliterator.tryAdvance(this); 
        return this.valueReady;
      }
      
      public T next() {
        if (!this.valueReady && !hasNext())
          throw new NoSuchElementException(); 
        this.valueReady = false;
        return (T)this.nextElement;
      }
    };
    return new Adapter();
  }
  
  public static PrimitiveIterator.OfInt iterator(final Spliterator.OfInt spliterator) {
    Objects.requireNonNull(paramOfInt);
    class Adapter implements PrimitiveIterator.OfInt, IntConsumer {
      boolean valueReady = false;
      
      int nextElement;
      
      public void accept(int param1Int) {
        this.valueReady = true;
        this.nextElement = param1Int;
      }
      
      public boolean hasNext() {
        if (!this.valueReady)
          spliterator.tryAdvance(this); 
        return this.valueReady;
      }
      
      public int nextInt() {
        if (!this.valueReady && !hasNext())
          throw new NoSuchElementException(); 
        this.valueReady = false;
        return this.nextElement;
      }
    };
    return new Adapter();
  }
  
  public static PrimitiveIterator.OfLong iterator(final Spliterator.OfLong spliterator) {
    Objects.requireNonNull(paramOfLong);
    class Adapter implements PrimitiveIterator.OfLong, LongConsumer {
      boolean valueReady = false;
      
      long nextElement;
      
      public void accept(long param1Long) {
        this.valueReady = true;
        this.nextElement = param1Long;
      }
      
      public boolean hasNext() {
        if (!this.valueReady)
          spliterator.tryAdvance(this); 
        return this.valueReady;
      }
      
      public long nextLong() {
        if (!this.valueReady && !hasNext())
          throw new NoSuchElementException(); 
        this.valueReady = false;
        return this.nextElement;
      }
    };
    return new Adapter();
  }
  
  public static PrimitiveIterator.OfDouble iterator(final Spliterator.OfDouble spliterator) {
    Objects.requireNonNull(paramOfDouble);
    class Adapter implements PrimitiveIterator.OfDouble, DoubleConsumer {
      boolean valueReady = false;
      
      double nextElement;
      
      public void accept(double param1Double) {
        this.valueReady = true;
        this.nextElement = param1Double;
      }
      
      public boolean hasNext() {
        if (!this.valueReady)
          spliterator.tryAdvance(this); 
        return this.valueReady;
      }
      
      public double nextDouble() {
        if (!this.valueReady && !hasNext())
          throw new NoSuchElementException(); 
        this.valueReady = false;
        return this.nextElement;
      }
    };
    return new Adapter();
  }
  
  public static abstract class AbstractDoubleSpliterator implements Spliterator.OfDouble {
    static final int MAX_BATCH = 33554432;
    
    static final int BATCH_UNIT = 1024;
    
    private final int characteristics;
    
    private long est;
    
    private int batch;
    
    protected AbstractDoubleSpliterator(long param1Long, int param1Int) {
      this.est = param1Long;
      this.characteristics = ((param1Int & 0x40) != 0) ? (param1Int | 0x4000) : param1Int;
    }
    
    public Spliterator.OfDouble trySplit() {
      HoldingDoubleConsumer holdingDoubleConsumer = new HoldingDoubleConsumer();
      long l = this.est;
      if (l > 1L && tryAdvance(holdingDoubleConsumer)) {
        int i = this.batch + 1024;
        if (i > l)
          i = (int)l; 
        if (i > 33554432)
          i = 33554432; 
        double[] arrayOfDouble = new double[i];
        byte b = 0;
        do {
          arrayOfDouble[b] = holdingDoubleConsumer.value;
        } while (++b < i && tryAdvance(holdingDoubleConsumer));
        this.batch = b;
        if (this.est != Float.MAX_VALUE)
          this.est -= b; 
        return new Spliterators.DoubleArraySpliterator(arrayOfDouble, 0, b, characteristics());
      } 
      return null;
    }
    
    public long estimateSize() { return this.est; }
    
    public int characteristics() { return this.characteristics; }
    
    static final class HoldingDoubleConsumer implements DoubleConsumer {
      double value;
      
      public void accept(double param2Double) { this.value = param2Double; }
    }
  }
  
  public static abstract class AbstractIntSpliterator implements Spliterator.OfInt {
    static final int MAX_BATCH = 33554432;
    
    static final int BATCH_UNIT = 1024;
    
    private final int characteristics;
    
    private long est;
    
    private int batch;
    
    protected AbstractIntSpliterator(long param1Long, int param1Int) {
      this.est = param1Long;
      this.characteristics = ((param1Int & 0x40) != 0) ? (param1Int | 0x4000) : param1Int;
    }
    
    public Spliterator.OfInt trySplit() {
      HoldingIntConsumer holdingIntConsumer = new HoldingIntConsumer();
      long l = this.est;
      if (l > 1L && tryAdvance(holdingIntConsumer)) {
        int i = this.batch + 1024;
        if (i > l)
          i = (int)l; 
        if (i > 33554432)
          i = 33554432; 
        int[] arrayOfInt = new int[i];
        byte b = 0;
        do {
          arrayOfInt[b] = holdingIntConsumer.value;
        } while (++b < i && tryAdvance(holdingIntConsumer));
        this.batch = b;
        if (this.est != Float.MAX_VALUE)
          this.est -= b; 
        return new Spliterators.IntArraySpliterator(arrayOfInt, 0, b, characteristics());
      } 
      return null;
    }
    
    public long estimateSize() { return this.est; }
    
    public int characteristics() { return this.characteristics; }
    
    static final class HoldingIntConsumer implements IntConsumer {
      int value;
      
      public void accept(int param2Int) { this.value = param2Int; }
    }
  }
  
  public static abstract class AbstractLongSpliterator implements Spliterator.OfLong {
    static final int MAX_BATCH = 33554432;
    
    static final int BATCH_UNIT = 1024;
    
    private final int characteristics;
    
    private long est;
    
    private int batch;
    
    protected AbstractLongSpliterator(long param1Long, int param1Int) {
      this.est = param1Long;
      this.characteristics = ((param1Int & 0x40) != 0) ? (param1Int | 0x4000) : param1Int;
    }
    
    public Spliterator.OfLong trySplit() {
      HoldingLongConsumer holdingLongConsumer = new HoldingLongConsumer();
      long l = this.est;
      if (l > 1L && tryAdvance(holdingLongConsumer)) {
        int i = this.batch + 1024;
        if (i > l)
          i = (int)l; 
        if (i > 33554432)
          i = 33554432; 
        long[] arrayOfLong = new long[i];
        byte b = 0;
        do {
          arrayOfLong[b] = holdingLongConsumer.value;
        } while (++b < i && tryAdvance(holdingLongConsumer));
        this.batch = b;
        if (this.est != Float.MAX_VALUE)
          this.est -= b; 
        return new Spliterators.LongArraySpliterator(arrayOfLong, 0, b, characteristics());
      } 
      return null;
    }
    
    public long estimateSize() { return this.est; }
    
    public int characteristics() { return this.characteristics; }
    
    static final class HoldingLongConsumer implements LongConsumer {
      long value;
      
      public void accept(long param2Long) { this.value = param2Long; }
    }
  }
  
  public static abstract class AbstractSpliterator<T> extends Object implements Spliterator<T> {
    static final int BATCH_UNIT = 1024;
    
    static final int MAX_BATCH = 33554432;
    
    private final int characteristics;
    
    private long est;
    
    private int batch;
    
    protected AbstractSpliterator(long param1Long, int param1Int) {
      this.est = param1Long;
      this.characteristics = ((param1Int & 0x40) != 0) ? (param1Int | 0x4000) : param1Int;
    }
    
    public Spliterator<T> trySplit() {
      HoldingConsumer holdingConsumer = new HoldingConsumer();
      long l = this.est;
      if (l > 1L && tryAdvance(holdingConsumer)) {
        int i = this.batch + 1024;
        if (i > l)
          i = (int)l; 
        if (i > 33554432)
          i = 33554432; 
        Object[] arrayOfObject = new Object[i];
        byte b = 0;
        do {
          arrayOfObject[b] = holdingConsumer.value;
        } while (++b < i && tryAdvance(holdingConsumer));
        this.batch = b;
        if (this.est != Float.MAX_VALUE)
          this.est -= b; 
        return new Spliterators.ArraySpliterator(arrayOfObject, 0, b, characteristics());
      } 
      return null;
    }
    
    public long estimateSize() { return this.est; }
    
    public int characteristics() { return this.characteristics; }
    
    static final class HoldingConsumer<T> extends Object implements Consumer<T> {
      Object value;
      
      public void accept(T param2T) { this.value = param2T; }
    }
  }
  
  static final class ArraySpliterator<T> extends Object implements Spliterator<T> {
    private final Object[] array;
    
    private int index;
    
    private final int fence;
    
    private final int characteristics;
    
    public ArraySpliterator(Object[] param1ArrayOfObject, int param1Int) { this(param1ArrayOfObject, 0, param1ArrayOfObject.length, param1Int); }
    
    public ArraySpliterator(Object[] param1ArrayOfObject, int param1Int1, int param1Int2, int param1Int3) {
      this.array = param1ArrayOfObject;
      this.index = param1Int1;
      this.fence = param1Int2;
      this.characteristics = param1Int3 | 0x40 | 0x4000;
    }
    
    public Spliterator<T> trySplit() {
      int i = this.index;
      int j = i + this.fence >>> 1;
      return (i >= j) ? null : new ArraySpliterator(this.array, i, this.index = j, this.characteristics);
    }
    
    public void forEachRemaining(Consumer<? super T> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      Object[] arrayOfObject;
      int i;
      int j;
      if (arrayOfObject = this.array.length >= (j = this.fence) && (i = this.index) >= 0 && i < (this.index = j))
        do {
          param1Consumer.accept(arrayOfObject[i]);
        } while (++i < j); 
    }
    
    public boolean tryAdvance(Consumer<? super T> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      if (this.index >= 0 && this.index < this.fence) {
        Object object = this.array[this.index++];
        param1Consumer.accept(object);
        return true;
      } 
      return false;
    }
    
    public long estimateSize() { return (this.fence - this.index); }
    
    public int characteristics() { return this.characteristics; }
    
    public Comparator<? super T> getComparator() {
      if (hasCharacteristics(4))
        return null; 
      throw new IllegalStateException();
    }
  }
  
  static final class DoubleArraySpliterator implements Spliterator.OfDouble {
    private final double[] array;
    
    private int index;
    
    private final int fence;
    
    private final int characteristics;
    
    public DoubleArraySpliterator(double[] param1ArrayOfDouble, int param1Int) { this(param1ArrayOfDouble, 0, param1ArrayOfDouble.length, param1Int); }
    
    public DoubleArraySpliterator(double[] param1ArrayOfDouble, int param1Int1, int param1Int2, int param1Int3) {
      this.array = param1ArrayOfDouble;
      this.index = param1Int1;
      this.fence = param1Int2;
      this.characteristics = param1Int3 | 0x40 | 0x4000;
    }
    
    public Spliterator.OfDouble trySplit() {
      int i = this.index;
      int j = i + this.fence >>> 1;
      return (i >= j) ? null : new DoubleArraySpliterator(this.array, i, this.index = j, this.characteristics);
    }
    
    public void forEachRemaining(DoubleConsumer param1DoubleConsumer) {
      if (param1DoubleConsumer == null)
        throw new NullPointerException(); 
      double[] arrayOfDouble;
      int i;
      int j;
      if (arrayOfDouble = this.array.length >= (j = this.fence) && (i = this.index) >= 0 && i < (this.index = j))
        do {
          param1DoubleConsumer.accept(arrayOfDouble[i]);
        } while (++i < j); 
    }
    
    public boolean tryAdvance(DoubleConsumer param1DoubleConsumer) {
      if (param1DoubleConsumer == null)
        throw new NullPointerException(); 
      if (this.index >= 0 && this.index < this.fence) {
        param1DoubleConsumer.accept(this.array[this.index++]);
        return true;
      } 
      return false;
    }
    
    public long estimateSize() { return (this.fence - this.index); }
    
    public int characteristics() { return this.characteristics; }
    
    public Comparator<? super Double> getComparator() {
      if (hasCharacteristics(4))
        return null; 
      throw new IllegalStateException();
    }
  }
  
  static final class DoubleIteratorSpliterator implements Spliterator.OfDouble {
    static final int BATCH_UNIT = 1024;
    
    static final int MAX_BATCH = 33554432;
    
    private PrimitiveIterator.OfDouble it;
    
    private final int characteristics;
    
    private long est;
    
    private int batch;
    
    public DoubleIteratorSpliterator(PrimitiveIterator.OfDouble param1OfDouble, long param1Long, int param1Int) {
      this.it = param1OfDouble;
      this.est = param1Long;
      this.characteristics = ((param1Int & 0x1000) == 0) ? (param1Int | 0x40 | 0x4000) : param1Int;
    }
    
    public DoubleIteratorSpliterator(PrimitiveIterator.OfDouble param1OfDouble, int param1Int) {
      this.it = param1OfDouble;
      this.est = Float.MAX_VALUE;
      this.characteristics = param1Int & 0xFFFFBFBF;
    }
    
    public Spliterator.OfDouble trySplit() {
      PrimitiveIterator.OfDouble ofDouble = this.it;
      long l = this.est;
      if (l > 1L && ofDouble.hasNext()) {
        int i = this.batch + 1024;
        if (i > l)
          i = (int)l; 
        if (i > 33554432)
          i = 33554432; 
        double[] arrayOfDouble = new double[i];
        byte b = 0;
        do {
          arrayOfDouble[b] = ofDouble.nextDouble();
        } while (++b < i && ofDouble.hasNext());
        this.batch = b;
        if (this.est != Float.MAX_VALUE)
          this.est -= b; 
        return new Spliterators.DoubleArraySpliterator(arrayOfDouble, 0, b, this.characteristics);
      } 
      return null;
    }
    
    public void forEachRemaining(DoubleConsumer param1DoubleConsumer) {
      if (param1DoubleConsumer == null)
        throw new NullPointerException(); 
      this.it.forEachRemaining(param1DoubleConsumer);
    }
    
    public boolean tryAdvance(DoubleConsumer param1DoubleConsumer) {
      if (param1DoubleConsumer == null)
        throw new NullPointerException(); 
      if (this.it.hasNext()) {
        param1DoubleConsumer.accept(this.it.nextDouble());
        return true;
      } 
      return false;
    }
    
    public long estimateSize() { return this.est; }
    
    public int characteristics() { return this.characteristics; }
    
    public Comparator<? super Double> getComparator() {
      if (hasCharacteristics(4))
        return null; 
      throw new IllegalStateException();
    }
  }
  
  private static abstract class EmptySpliterator<T, S extends Spliterator<T>, C> extends Object {
    public S trySplit() { return null; }
    
    public boolean tryAdvance(C param1C) {
      Objects.requireNonNull(param1C);
      return false;
    }
    
    public void forEachRemaining(C param1C) { Objects.requireNonNull(param1C); }
    
    public long estimateSize() { return 0L; }
    
    public int characteristics() { return 16448; }
    
    private static final class OfDouble extends EmptySpliterator<Double, Spliterator.OfDouble, DoubleConsumer> implements Spliterator.OfDouble {}
    
    private static final class OfInt extends EmptySpliterator<Integer, Spliterator.OfInt, IntConsumer> implements Spliterator.OfInt {}
    
    private static final class OfLong extends EmptySpliterator<Long, Spliterator.OfLong, LongConsumer> implements Spliterator.OfLong {}
    
    private static final class OfRef<T> extends EmptySpliterator<T, Spliterator<T>, Consumer<? super T>> implements Spliterator<T> {}
  }
  
  private static final class OfDouble extends EmptySpliterator<Double, Spliterator.OfDouble, DoubleConsumer> implements Spliterator.OfDouble {}
  
  private static final class OfInt extends EmptySpliterator<Integer, Spliterator.OfInt, IntConsumer> implements Spliterator.OfInt {}
  
  private static final class OfLong extends EmptySpliterator<Long, Spliterator.OfLong, LongConsumer> implements Spliterator.OfLong {}
  
  private static final class OfRef<T> extends EmptySpliterator<T, Spliterator<T>, Consumer<? super T>> implements Spliterator<T> {}
  
  static final class IntArraySpliterator implements Spliterator.OfInt {
    private final int[] array;
    
    private int index;
    
    private final int fence;
    
    private final int characteristics;
    
    public IntArraySpliterator(int[] param1ArrayOfInt, int param1Int) { this(param1ArrayOfInt, 0, param1ArrayOfInt.length, param1Int); }
    
    public IntArraySpliterator(int[] param1ArrayOfInt, int param1Int1, int param1Int2, int param1Int3) {
      this.array = param1ArrayOfInt;
      this.index = param1Int1;
      this.fence = param1Int2;
      this.characteristics = param1Int3 | 0x40 | 0x4000;
    }
    
    public Spliterator.OfInt trySplit() {
      int i = this.index;
      int j = i + this.fence >>> 1;
      return (i >= j) ? null : new IntArraySpliterator(this.array, i, this.index = j, this.characteristics);
    }
    
    public void forEachRemaining(IntConsumer param1IntConsumer) {
      if (param1IntConsumer == null)
        throw new NullPointerException(); 
      int[] arrayOfInt;
      int i;
      int j;
      if (arrayOfInt = this.array.length >= (j = this.fence) && (i = this.index) >= 0 && i < (this.index = j))
        do {
          param1IntConsumer.accept(arrayOfInt[i]);
        } while (++i < j); 
    }
    
    public boolean tryAdvance(IntConsumer param1IntConsumer) {
      if (param1IntConsumer == null)
        throw new NullPointerException(); 
      if (this.index >= 0 && this.index < this.fence) {
        param1IntConsumer.accept(this.array[this.index++]);
        return true;
      } 
      return false;
    }
    
    public long estimateSize() { return (this.fence - this.index); }
    
    public int characteristics() { return this.characteristics; }
    
    public Comparator<? super Integer> getComparator() {
      if (hasCharacteristics(4))
        return null; 
      throw new IllegalStateException();
    }
  }
  
  static final class IntIteratorSpliterator implements Spliterator.OfInt {
    static final int BATCH_UNIT = 1024;
    
    static final int MAX_BATCH = 33554432;
    
    private PrimitiveIterator.OfInt it;
    
    private final int characteristics;
    
    private long est;
    
    private int batch;
    
    public IntIteratorSpliterator(PrimitiveIterator.OfInt param1OfInt, long param1Long, int param1Int) {
      this.it = param1OfInt;
      this.est = param1Long;
      this.characteristics = ((param1Int & 0x1000) == 0) ? (param1Int | 0x40 | 0x4000) : param1Int;
    }
    
    public IntIteratorSpliterator(PrimitiveIterator.OfInt param1OfInt, int param1Int) {
      this.it = param1OfInt;
      this.est = Float.MAX_VALUE;
      this.characteristics = param1Int & 0xFFFFBFBF;
    }
    
    public Spliterator.OfInt trySplit() {
      PrimitiveIterator.OfInt ofInt = this.it;
      long l = this.est;
      if (l > 1L && ofInt.hasNext()) {
        int i = this.batch + 1024;
        if (i > l)
          i = (int)l; 
        if (i > 33554432)
          i = 33554432; 
        int[] arrayOfInt = new int[i];
        byte b = 0;
        do {
          arrayOfInt[b] = ofInt.nextInt();
        } while (++b < i && ofInt.hasNext());
        this.batch = b;
        if (this.est != Float.MAX_VALUE)
          this.est -= b; 
        return new Spliterators.IntArraySpliterator(arrayOfInt, 0, b, this.characteristics);
      } 
      return null;
    }
    
    public void forEachRemaining(IntConsumer param1IntConsumer) {
      if (param1IntConsumer == null)
        throw new NullPointerException(); 
      this.it.forEachRemaining(param1IntConsumer);
    }
    
    public boolean tryAdvance(IntConsumer param1IntConsumer) {
      if (param1IntConsumer == null)
        throw new NullPointerException(); 
      if (this.it.hasNext()) {
        param1IntConsumer.accept(this.it.nextInt());
        return true;
      } 
      return false;
    }
    
    public long estimateSize() { return this.est; }
    
    public int characteristics() { return this.characteristics; }
    
    public Comparator<? super Integer> getComparator() {
      if (hasCharacteristics(4))
        return null; 
      throw new IllegalStateException();
    }
  }
  
  static class IteratorSpliterator<T> extends Object implements Spliterator<T> {
    static final int BATCH_UNIT = 1024;
    
    static final int MAX_BATCH = 33554432;
    
    private final Collection<? extends T> collection;
    
    private Iterator<? extends T> it;
    
    private final int characteristics;
    
    private long est;
    
    private int batch;
    
    public IteratorSpliterator(Collection<? extends T> param1Collection, int param1Int) {
      this.collection = param1Collection;
      this.it = null;
      this.characteristics = ((param1Int & 0x1000) == 0) ? (param1Int | 0x40 | 0x4000) : param1Int;
    }
    
    public IteratorSpliterator(Iterator<? extends T> param1Iterator, long param1Long, int param1Int) {
      this.collection = null;
      this.it = param1Iterator;
      this.est = param1Long;
      this.characteristics = ((param1Int & 0x1000) == 0) ? (param1Int | 0x40 | 0x4000) : param1Int;
    }
    
    public IteratorSpliterator(Iterator<? extends T> param1Iterator, int param1Int) {
      this.collection = null;
      this.it = param1Iterator;
      this.est = Float.MAX_VALUE;
      this.characteristics = param1Int & 0xFFFFBFBF;
    }
    
    public Spliterator<T> trySplit() {
      Iterator iterator = this.it = this.collection.iterator();
      long l = this.est = this.collection.size();
      l = this.est;
      if (l > 1L && iterator.hasNext()) {
        int i = this.batch + 1024;
        if (i > l)
          i = (int)l; 
        if (i > 33554432)
          i = 33554432; 
        Object[] arrayOfObject = new Object[i];
        byte b = 0;
        do {
          arrayOfObject[b] = iterator.next();
        } while (++b < i && iterator.hasNext());
        this.batch = b;
        if (this.est != Float.MAX_VALUE)
          this.est -= b; 
        return new Spliterators.ArraySpliterator(arrayOfObject, 0, b, this.characteristics);
      } 
      return null;
    }
    
    public void forEachRemaining(Consumer<? super T> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      Iterator iterator;
      if ((iterator = this.it) == null) {
        iterator = this.it = this.collection.iterator();
        this.est = this.collection.size();
      } 
      iterator.forEachRemaining(param1Consumer);
    }
    
    public boolean tryAdvance(Consumer<? super T> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      if (this.it == null) {
        this.it = this.collection.iterator();
        this.est = this.collection.size();
      } 
      if (this.it.hasNext()) {
        param1Consumer.accept(this.it.next());
        return true;
      } 
      return false;
    }
    
    public long estimateSize() {
      if (this.it == null) {
        this.it = this.collection.iterator();
        return this.est = this.collection.size();
      } 
      return this.est;
    }
    
    public int characteristics() { return this.characteristics; }
    
    public Comparator<? super T> getComparator() {
      if (hasCharacteristics(4))
        return null; 
      throw new IllegalStateException();
    }
  }
  
  static final class LongArraySpliterator implements Spliterator.OfLong {
    private final long[] array;
    
    private int index;
    
    private final int fence;
    
    private final int characteristics;
    
    public LongArraySpliterator(long[] param1ArrayOfLong, int param1Int) { this(param1ArrayOfLong, 0, param1ArrayOfLong.length, param1Int); }
    
    public LongArraySpliterator(long[] param1ArrayOfLong, int param1Int1, int param1Int2, int param1Int3) {
      this.array = param1ArrayOfLong;
      this.index = param1Int1;
      this.fence = param1Int2;
      this.characteristics = param1Int3 | 0x40 | 0x4000;
    }
    
    public Spliterator.OfLong trySplit() {
      int i = this.index;
      int j = i + this.fence >>> 1;
      return (i >= j) ? null : new LongArraySpliterator(this.array, i, this.index = j, this.characteristics);
    }
    
    public void forEachRemaining(LongConsumer param1LongConsumer) {
      if (param1LongConsumer == null)
        throw new NullPointerException(); 
      long[] arrayOfLong;
      int i;
      int j;
      if (arrayOfLong = this.array.length >= (j = this.fence) && (i = this.index) >= 0 && i < (this.index = j))
        do {
          param1LongConsumer.accept(arrayOfLong[i]);
        } while (++i < j); 
    }
    
    public boolean tryAdvance(LongConsumer param1LongConsumer) {
      if (param1LongConsumer == null)
        throw new NullPointerException(); 
      if (this.index >= 0 && this.index < this.fence) {
        param1LongConsumer.accept(this.array[this.index++]);
        return true;
      } 
      return false;
    }
    
    public long estimateSize() { return (this.fence - this.index); }
    
    public int characteristics() { return this.characteristics; }
    
    public Comparator<? super Long> getComparator() {
      if (hasCharacteristics(4))
        return null; 
      throw new IllegalStateException();
    }
  }
  
  static final class LongIteratorSpliterator implements Spliterator.OfLong {
    static final int BATCH_UNIT = 1024;
    
    static final int MAX_BATCH = 33554432;
    
    private PrimitiveIterator.OfLong it;
    
    private final int characteristics;
    
    private long est;
    
    private int batch;
    
    public LongIteratorSpliterator(PrimitiveIterator.OfLong param1OfLong, long param1Long, int param1Int) {
      this.it = param1OfLong;
      this.est = param1Long;
      this.characteristics = ((param1Int & 0x1000) == 0) ? (param1Int | 0x40 | 0x4000) : param1Int;
    }
    
    public LongIteratorSpliterator(PrimitiveIterator.OfLong param1OfLong, int param1Int) {
      this.it = param1OfLong;
      this.est = Float.MAX_VALUE;
      this.characteristics = param1Int & 0xFFFFBFBF;
    }
    
    public Spliterator.OfLong trySplit() {
      PrimitiveIterator.OfLong ofLong = this.it;
      long l = this.est;
      if (l > 1L && ofLong.hasNext()) {
        int i = this.batch + 1024;
        if (i > l)
          i = (int)l; 
        if (i > 33554432)
          i = 33554432; 
        long[] arrayOfLong = new long[i];
        byte b = 0;
        do {
          arrayOfLong[b] = ofLong.nextLong();
        } while (++b < i && ofLong.hasNext());
        this.batch = b;
        if (this.est != Float.MAX_VALUE)
          this.est -= b; 
        return new Spliterators.LongArraySpliterator(arrayOfLong, 0, b, this.characteristics);
      } 
      return null;
    }
    
    public void forEachRemaining(LongConsumer param1LongConsumer) {
      if (param1LongConsumer == null)
        throw new NullPointerException(); 
      this.it.forEachRemaining(param1LongConsumer);
    }
    
    public boolean tryAdvance(LongConsumer param1LongConsumer) {
      if (param1LongConsumer == null)
        throw new NullPointerException(); 
      if (this.it.hasNext()) {
        param1LongConsumer.accept(this.it.nextLong());
        return true;
      } 
      return false;
    }
    
    public long estimateSize() { return this.est; }
    
    public int characteristics() { return this.characteristics; }
    
    public Comparator<? super Long> getComparator() {
      if (hasCharacteristics(4))
        return null; 
      throw new IllegalStateException();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Spliterators.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */