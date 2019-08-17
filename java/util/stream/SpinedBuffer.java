package java.util.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;

class SpinedBuffer<E> extends AbstractSpinedBuffer implements Consumer<E>, Iterable<E> {
  protected E[] curChunk = (Object[])new Object[1 << this.initialChunkPower];
  
  protected E[][] spine;
  
  private static final int SPLITERATOR_CHARACTERISTICS = 16464;
  
  SpinedBuffer(int paramInt) { super(paramInt); }
  
  SpinedBuffer() {}
  
  protected long capacity() { return (this.spineIndex == 0) ? this.curChunk.length : (this.priorElementCount[this.spineIndex] + this.spine[this.spineIndex].length); }
  
  private void inflateSpine() {
    if (this.spine == null) {
      this.spine = (Object[][])new Object[8][];
      this.priorElementCount = new long[8];
      this.spine[0] = this.curChunk;
    } 
  }
  
  protected final void ensureCapacity(long paramLong) {
    long l = capacity();
    if (paramLong > l) {
      inflateSpine();
      for (int i = this.spineIndex + 1; paramLong > l; i++) {
        if (i >= this.spine.length) {
          int k = this.spine.length * 2;
          this.spine = (Object[][])Arrays.copyOf(this.spine, k);
          this.priorElementCount = Arrays.copyOf(this.priorElementCount, k);
        } 
        int j = chunkSize(i);
        this.spine[i] = (Object[])new Object[j];
        this.priorElementCount[i] = this.priorElementCount[i - 1] + this.spine[i - 1].length;
        l += j;
      } 
    } 
  }
  
  protected void increaseCapacity() { ensureCapacity(capacity() + 1L); }
  
  public E get(long paramLong) {
    if (this.spineIndex == 0) {
      if (paramLong < this.elementIndex)
        return (E)this.curChunk[(int)paramLong]; 
      throw new IndexOutOfBoundsException(Long.toString(paramLong));
    } 
    if (paramLong >= count())
      throw new IndexOutOfBoundsException(Long.toString(paramLong)); 
    for (byte b = 0; b <= this.spineIndex; b++) {
      if (paramLong < this.priorElementCount[b] + this.spine[b].length)
        return (E)this.spine[b][(int)(paramLong - this.priorElementCount[b])]; 
    } 
    throw new IndexOutOfBoundsException(Long.toString(paramLong));
  }
  
  public void copyInto(E[] paramArrayOfE, int paramInt) {
    long l = paramInt + count();
    if (l > paramArrayOfE.length || l < paramInt)
      throw new IndexOutOfBoundsException("does not fit"); 
    if (this.spineIndex == 0) {
      System.arraycopy(this.curChunk, 0, paramArrayOfE, paramInt, this.elementIndex);
    } else {
      for (byte b = 0; b < this.spineIndex; b++) {
        System.arraycopy(this.spine[b], 0, paramArrayOfE, paramInt, this.spine[b].length);
        paramInt += this.spine[b].length;
      } 
      if (this.elementIndex > 0)
        System.arraycopy(this.curChunk, 0, paramArrayOfE, paramInt, this.elementIndex); 
    } 
  }
  
  public E[] asArray(IntFunction<E[]> paramIntFunction) {
    long l = count();
    if (l >= 2147483639L)
      throw new IllegalArgumentException("Stream size exceeds max array size"); 
    Object[] arrayOfObject = (Object[])paramIntFunction.apply((int)l);
    copyInto(arrayOfObject, 0);
    return (E[])arrayOfObject;
  }
  
  public void clear() {
    if (this.spine != null) {
      this.curChunk = this.spine[0];
      for (byte b = 0; b < this.curChunk.length; b++)
        this.curChunk[b] = null; 
      this.spine = (Object[][])null;
      this.priorElementCount = null;
    } else {
      for (byte b = 0; b < this.elementIndex; b++)
        this.curChunk[b] = null; 
    } 
    this.elementIndex = 0;
    this.spineIndex = 0;
  }
  
  public Iterator<E> iterator() { return Spliterators.iterator(spliterator()); }
  
  public void forEach(Consumer<? super E> paramConsumer) {
    byte b;
    for (b = 0; b < this.spineIndex; b++) {
      for (Object object : this.spine[b])
        paramConsumer.accept(object); 
    } 
    for (b = 0; b < this.elementIndex; b++)
      paramConsumer.accept(this.curChunk[b]); 
  }
  
  public void accept(E paramE) {
    if (this.elementIndex == this.curChunk.length) {
      inflateSpine();
      if (this.spineIndex + 1 >= this.spine.length || this.spine[this.spineIndex + 1] == null)
        increaseCapacity(); 
      this.elementIndex = 0;
      this.spineIndex++;
      this.curChunk = this.spine[this.spineIndex];
    } 
    this.curChunk[this.elementIndex++] = paramE;
  }
  
  public String toString() {
    ArrayList arrayList = new ArrayList();
    forEach(arrayList::add);
    return "SpinedBuffer:" + arrayList.toString();
  }
  
  public Spliterator<E> spliterator() {
    class Splitr extends Object implements Spliterator<E> {
      int splSpineIndex;
      
      final int lastSpineIndex;
      
      int splElementIndex;
      
      final int lastSpineElementFence;
      
      E[] splChunk;
      
      Splitr(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
        this.splSpineIndex = param1Int1;
        this.lastSpineIndex = param1Int2;
        this.splElementIndex = param1Int3;
        this.lastSpineElementFence = param1Int4;
        assert SpinedBuffer.this.spine != null || (param1Int1 == 0 && param1Int2 == 0);
        this.splChunk = (SpinedBuffer.this.spine == null) ? SpinedBuffer.this.curChunk : SpinedBuffer.this.spine[param1Int1];
      }
      
      public long estimateSize() { return (this.splSpineIndex == this.lastSpineIndex) ? (this.lastSpineElementFence - this.splElementIndex) : (SpinedBuffer.this.priorElementCount[this.lastSpineIndex] + this.lastSpineElementFence - SpinedBuffer.this.priorElementCount[this.splSpineIndex] - this.splElementIndex); }
      
      public int characteristics() { return 16464; }
      
      public boolean tryAdvance(Consumer<? super E> param1Consumer) {
        Objects.requireNonNull(param1Consumer);
        if (this.splSpineIndex < this.lastSpineIndex || (this.splSpineIndex == this.lastSpineIndex && this.splElementIndex < this.lastSpineElementFence)) {
          param1Consumer.accept(this.splChunk[this.splElementIndex++]);
          if (this.splElementIndex == this.splChunk.length) {
            this.splElementIndex = 0;
            this.splSpineIndex++;
            if (SpinedBuffer.this.spine != null && this.splSpineIndex <= this.lastSpineIndex)
              this.splChunk = SpinedBuffer.this.spine[this.splSpineIndex]; 
          } 
          return true;
        } 
        return false;
      }
      
      public void forEachRemaining(Consumer<? super E> param1Consumer) {
        Objects.requireNonNull(param1Consumer);
        if (this.splSpineIndex < this.lastSpineIndex || (this.splSpineIndex == this.lastSpineIndex && this.splElementIndex < this.lastSpineElementFence)) {
          int i = this.splElementIndex;
          for (int j = this.splSpineIndex; j < this.lastSpineIndex; j++) {
            Object[] arrayOfObject1 = SpinedBuffer.this.spine[j];
            while (i < arrayOfObject1.length) {
              param1Consumer.accept(arrayOfObject1[i]);
              i++;
            } 
            i = 0;
          } 
          Object[] arrayOfObject = (this.splSpineIndex == this.lastSpineIndex) ? this.splChunk : SpinedBuffer.this.spine[this.lastSpineIndex];
          int k = this.lastSpineElementFence;
          while (i < k) {
            param1Consumer.accept(arrayOfObject[i]);
            i++;
          } 
          this.splSpineIndex = this.lastSpineIndex;
          this.splElementIndex = this.lastSpineElementFence;
        } 
      }
      
      public Spliterator<E> trySplit() {
        if (this.splSpineIndex < this.lastSpineIndex) {
          Splitr splitr = new Splitr(SpinedBuffer.this, this.splSpineIndex, this.lastSpineIndex - 1, this.splElementIndex, SpinedBuffer.this.spine[this.lastSpineIndex - 1].length);
          this.splSpineIndex = this.lastSpineIndex;
          this.splElementIndex = 0;
          this.splChunk = SpinedBuffer.this.spine[this.splSpineIndex];
          return splitr;
        } 
        if (this.splSpineIndex == this.lastSpineIndex) {
          int i = (this.lastSpineElementFence - this.splElementIndex) / 2;
          if (i == 0)
            return null; 
          Spliterator spliterator = Arrays.spliterator(this.splChunk, this.splElementIndex, this.splElementIndex + i);
          this.splElementIndex += i;
          return spliterator;
        } 
        return null;
      }
    };
    return new Splitr(0, this.spineIndex, 0, this.elementIndex);
  }
  
  static class OfDouble extends OfPrimitive<Double, double[], DoubleConsumer> implements DoubleConsumer {
    OfDouble() {}
    
    OfDouble(int param1Int) { super(param1Int); }
    
    public void forEach(Consumer<? super Double> param1Consumer) {
      if (param1Consumer instanceof DoubleConsumer) {
        forEach((DoubleConsumer)param1Consumer);
      } else {
        if (Tripwire.ENABLED)
          Tripwire.trip(getClass(), "{0} calling SpinedBuffer.OfDouble.forEach(Consumer)"); 
        spliterator().forEachRemaining(param1Consumer);
      } 
    }
    
    protected double[][] newArrayArray(int param1Int) { return new double[param1Int][]; }
    
    public double[] newArray(int param1Int) { return new double[param1Int]; }
    
    protected int arrayLength(double[] param1ArrayOfDouble) { return param1ArrayOfDouble.length; }
    
    protected void arrayForEach(double[] param1ArrayOfDouble, int param1Int1, int param1Int2, DoubleConsumer param1DoubleConsumer) {
      for (int i = param1Int1; i < param1Int2; i++)
        param1DoubleConsumer.accept(param1ArrayOfDouble[i]); 
    }
    
    public void accept(double param1Double) {
      preAccept();
      (double[])this.curChunk[this.elementIndex++] = param1Double;
    }
    
    public double get(long param1Long) {
      int i = chunkFor(param1Long);
      return (this.spineIndex == 0 && i == 0) ? (double[])this.curChunk[(int)param1Long] : (double[][])this.spine[i][(int)(param1Long - this.priorElementCount[i])];
    }
    
    public PrimitiveIterator.OfDouble iterator() { return Spliterators.iterator(spliterator()); }
    
    public Spliterator.OfDouble spliterator() {
      class Splitr extends SpinedBuffer.OfPrimitive<Double, double[], DoubleConsumer>.BaseSpliterator<Spliterator.OfDouble> implements Spliterator.OfDouble {
        Splitr(int param2Int1, int param2Int2, int param2Int3, int param2Int4) { super(SpinedBuffer.OfDouble.this, param2Int1, param2Int2, param2Int3, param2Int4); }
        
        Splitr newSpliterator(int param2Int1, int param2Int2, int param2Int3, int param2Int4) { return new Splitr(SpinedBuffer.OfDouble.this, param2Int1, param2Int2, param2Int3, param2Int4); }
        
        void arrayForOne(double[] param2ArrayOfDouble, int param2Int, DoubleConsumer param2DoubleConsumer) { param2DoubleConsumer.accept(param2ArrayOfDouble[param2Int]); }
        
        Spliterator.OfDouble arraySpliterator(double[] param2ArrayOfDouble, int param2Int1, int param2Int2) { return Arrays.spliterator(param2ArrayOfDouble, param2Int1, param2Int1 + param2Int2); }
      };
      return new Splitr(0, this.spineIndex, 0, this.elementIndex);
    }
    
    public String toString() {
      double[] arrayOfDouble1 = (double[])asPrimitiveArray();
      if (arrayOfDouble1.length < 200)
        return String.format("%s[length=%d, chunks=%d]%s", new Object[] { getClass().getSimpleName(), Integer.valueOf(arrayOfDouble1.length), Integer.valueOf(this.spineIndex), Arrays.toString(arrayOfDouble1) }); 
      double[] arrayOfDouble2 = Arrays.copyOf(arrayOfDouble1, 200);
      return String.format("%s[length=%d, chunks=%d]%s...", new Object[] { getClass().getSimpleName(), Integer.valueOf(arrayOfDouble1.length), Integer.valueOf(this.spineIndex), Arrays.toString(arrayOfDouble2) });
    }
  }
  
  static class OfInt extends OfPrimitive<Integer, int[], IntConsumer> implements IntConsumer {
    OfInt() {}
    
    OfInt(int param1Int) { super(param1Int); }
    
    public void forEach(Consumer<? super Integer> param1Consumer) {
      if (param1Consumer instanceof IntConsumer) {
        forEach((IntConsumer)param1Consumer);
      } else {
        if (Tripwire.ENABLED)
          Tripwire.trip(getClass(), "{0} calling SpinedBuffer.OfInt.forEach(Consumer)"); 
        spliterator().forEachRemaining(param1Consumer);
      } 
    }
    
    protected int[][] newArrayArray(int param1Int) { return new int[param1Int][]; }
    
    public int[] newArray(int param1Int) { return new int[param1Int]; }
    
    protected int arrayLength(int[] param1ArrayOfInt) { return param1ArrayOfInt.length; }
    
    protected void arrayForEach(int[] param1ArrayOfInt, int param1Int1, int param1Int2, IntConsumer param1IntConsumer) {
      for (int i = param1Int1; i < param1Int2; i++)
        param1IntConsumer.accept(param1ArrayOfInt[i]); 
    }
    
    public void accept(int param1Int) {
      preAccept();
      (int[])this.curChunk[this.elementIndex++] = param1Int;
    }
    
    public int get(long param1Long) {
      int i = chunkFor(param1Long);
      return (this.spineIndex == 0 && i == 0) ? (int[])this.curChunk[(int)param1Long] : (int[][])this.spine[i][(int)(param1Long - this.priorElementCount[i])];
    }
    
    public PrimitiveIterator.OfInt iterator() { return Spliterators.iterator(spliterator()); }
    
    public Spliterator.OfInt spliterator() {
      class Splitr extends SpinedBuffer.OfPrimitive<Integer, int[], IntConsumer>.BaseSpliterator<Spliterator.OfInt> implements Spliterator.OfInt {
        Splitr(int param2Int1, int param2Int2, int param2Int3, int param2Int4) { super(SpinedBuffer.OfInt.this, param2Int1, param2Int2, param2Int3, param2Int4); }
        
        Splitr newSpliterator(int param2Int1, int param2Int2, int param2Int3, int param2Int4) { return new Splitr(SpinedBuffer.OfInt.this, param2Int1, param2Int2, param2Int3, param2Int4); }
        
        void arrayForOne(int[] param2ArrayOfInt, int param2Int, IntConsumer param2IntConsumer) { param2IntConsumer.accept(param2ArrayOfInt[param2Int]); }
        
        Spliterator.OfInt arraySpliterator(int[] param2ArrayOfInt, int param2Int1, int param2Int2) { return Arrays.spliterator(param2ArrayOfInt, param2Int1, param2Int1 + param2Int2); }
      };
      return new Splitr(0, this.spineIndex, 0, this.elementIndex);
    }
    
    public String toString() {
      int[] arrayOfInt1 = (int[])asPrimitiveArray();
      if (arrayOfInt1.length < 200)
        return String.format("%s[length=%d, chunks=%d]%s", new Object[] { getClass().getSimpleName(), Integer.valueOf(arrayOfInt1.length), Integer.valueOf(this.spineIndex), Arrays.toString(arrayOfInt1) }); 
      int[] arrayOfInt2 = Arrays.copyOf(arrayOfInt1, 200);
      return String.format("%s[length=%d, chunks=%d]%s...", new Object[] { getClass().getSimpleName(), Integer.valueOf(arrayOfInt1.length), Integer.valueOf(this.spineIndex), Arrays.toString(arrayOfInt2) });
    }
  }
  
  static class OfLong extends OfPrimitive<Long, long[], LongConsumer> implements LongConsumer {
    OfLong() {}
    
    OfLong(int param1Int) { super(param1Int); }
    
    public void forEach(Consumer<? super Long> param1Consumer) {
      if (param1Consumer instanceof LongConsumer) {
        forEach((LongConsumer)param1Consumer);
      } else {
        if (Tripwire.ENABLED)
          Tripwire.trip(getClass(), "{0} calling SpinedBuffer.OfLong.forEach(Consumer)"); 
        spliterator().forEachRemaining(param1Consumer);
      } 
    }
    
    protected long[][] newArrayArray(int param1Int) { return new long[param1Int][]; }
    
    public long[] newArray(int param1Int) { return new long[param1Int]; }
    
    protected int arrayLength(long[] param1ArrayOfLong) { return param1ArrayOfLong.length; }
    
    protected void arrayForEach(long[] param1ArrayOfLong, int param1Int1, int param1Int2, LongConsumer param1LongConsumer) {
      for (int i = param1Int1; i < param1Int2; i++)
        param1LongConsumer.accept(param1ArrayOfLong[i]); 
    }
    
    public void accept(long param1Long) {
      preAccept();
      (long[])this.curChunk[this.elementIndex++] = param1Long;
    }
    
    public long get(long param1Long) {
      int i = chunkFor(param1Long);
      return (this.spineIndex == 0 && i == 0) ? (long[])this.curChunk[(int)param1Long] : (long[][])this.spine[i][(int)(param1Long - this.priorElementCount[i])];
    }
    
    public PrimitiveIterator.OfLong iterator() { return Spliterators.iterator(spliterator()); }
    
    public Spliterator.OfLong spliterator() {
      class Splitr extends SpinedBuffer.OfPrimitive<Long, long[], LongConsumer>.BaseSpliterator<Spliterator.OfLong> implements Spliterator.OfLong {
        Splitr(int param2Int1, int param2Int2, int param2Int3, int param2Int4) { super(SpinedBuffer.OfLong.this, param2Int1, param2Int2, param2Int3, param2Int4); }
        
        Splitr newSpliterator(int param2Int1, int param2Int2, int param2Int3, int param2Int4) { return new Splitr(SpinedBuffer.OfLong.this, param2Int1, param2Int2, param2Int3, param2Int4); }
        
        void arrayForOne(long[] param2ArrayOfLong, int param2Int, LongConsumer param2LongConsumer) { param2LongConsumer.accept(param2ArrayOfLong[param2Int]); }
        
        Spliterator.OfLong arraySpliterator(long[] param2ArrayOfLong, int param2Int1, int param2Int2) { return Arrays.spliterator(param2ArrayOfLong, param2Int1, param2Int1 + param2Int2); }
      };
      return new Splitr(0, this.spineIndex, 0, this.elementIndex);
    }
    
    public String toString() {
      long[] arrayOfLong1 = (long[])asPrimitiveArray();
      if (arrayOfLong1.length < 200)
        return String.format("%s[length=%d, chunks=%d]%s", new Object[] { getClass().getSimpleName(), Integer.valueOf(arrayOfLong1.length), Integer.valueOf(this.spineIndex), Arrays.toString(arrayOfLong1) }); 
      long[] arrayOfLong2 = Arrays.copyOf(arrayOfLong1, 200);
      return String.format("%s[length=%d, chunks=%d]%s...", new Object[] { getClass().getSimpleName(), Integer.valueOf(arrayOfLong1.length), Integer.valueOf(this.spineIndex), Arrays.toString(arrayOfLong2) });
    }
  }
  
  static abstract class OfPrimitive<E, T_ARR, T_CONS> extends AbstractSpinedBuffer implements Iterable<E> {
    T_ARR curChunk = newArray(1 << this.initialChunkPower);
    
    T_ARR[] spine;
    
    OfPrimitive(int param1Int) { super(param1Int); }
    
    OfPrimitive() {}
    
    public abstract Iterator<E> iterator();
    
    public abstract void forEach(Consumer<? super E> param1Consumer);
    
    protected abstract T_ARR[] newArrayArray(int param1Int);
    
    public abstract T_ARR newArray(int param1Int);
    
    protected abstract int arrayLength(T_ARR param1T_ARR);
    
    protected abstract void arrayForEach(T_ARR param1T_ARR, int param1Int1, int param1Int2, T_CONS param1T_CONS);
    
    protected long capacity() { return (this.spineIndex == 0) ? arrayLength(this.curChunk) : (this.priorElementCount[this.spineIndex] + arrayLength(this.spine[this.spineIndex])); }
    
    private void inflateSpine() {
      if (this.spine == null) {
        this.spine = newArrayArray(8);
        this.priorElementCount = new long[8];
        this.spine[0] = this.curChunk;
      } 
    }
    
    protected final void ensureCapacity(long param1Long) {
      long l = capacity();
      if (param1Long > l) {
        inflateSpine();
        for (int i = this.spineIndex + 1; param1Long > l; i++) {
          if (i >= this.spine.length) {
            int k = this.spine.length * 2;
            this.spine = Arrays.copyOf(this.spine, k);
            this.priorElementCount = Arrays.copyOf(this.priorElementCount, k);
          } 
          int j = chunkSize(i);
          this.spine[i] = newArray(j);
          this.priorElementCount[i] = this.priorElementCount[i - 1] + arrayLength(this.spine[i - 1]);
          l += j;
        } 
      } 
    }
    
    protected void increaseCapacity() { ensureCapacity(capacity() + 1L); }
    
    protected int chunkFor(long param1Long) {
      if (this.spineIndex == 0) {
        if (param1Long < this.elementIndex)
          return 0; 
        throw new IndexOutOfBoundsException(Long.toString(param1Long));
      } 
      if (param1Long >= count())
        throw new IndexOutOfBoundsException(Long.toString(param1Long)); 
      for (byte b = 0; b <= this.spineIndex; b++) {
        if (param1Long < this.priorElementCount[b] + arrayLength(this.spine[b]))
          return b; 
      } 
      throw new IndexOutOfBoundsException(Long.toString(param1Long));
    }
    
    public void copyInto(T_ARR param1T_ARR, int param1Int) {
      long l = param1Int + count();
      if (l > arrayLength(param1T_ARR) || l < param1Int)
        throw new IndexOutOfBoundsException("does not fit"); 
      if (this.spineIndex == 0) {
        System.arraycopy(this.curChunk, 0, param1T_ARR, param1Int, this.elementIndex);
      } else {
        for (byte b = 0; b < this.spineIndex; b++) {
          System.arraycopy(this.spine[b], 0, param1T_ARR, param1Int, arrayLength(this.spine[b]));
          param1Int += arrayLength(this.spine[b]);
        } 
        if (this.elementIndex > 0)
          System.arraycopy(this.curChunk, 0, param1T_ARR, param1Int, this.elementIndex); 
      } 
    }
    
    public T_ARR asPrimitiveArray() {
      long l = count();
      if (l >= 2147483639L)
        throw new IllegalArgumentException("Stream size exceeds max array size"); 
      Object object = newArray((int)l);
      copyInto(object, 0);
      return (T_ARR)object;
    }
    
    protected void preAccept() {
      if (this.elementIndex == arrayLength(this.curChunk)) {
        inflateSpine();
        if (this.spineIndex + 1 >= this.spine.length || this.spine[this.spineIndex + true] == null)
          increaseCapacity(); 
        this.elementIndex = 0;
        this.spineIndex++;
        this.curChunk = this.spine[this.spineIndex];
      } 
    }
    
    public void clear() {
      if (this.spine != null) {
        this.curChunk = this.spine[0];
        this.spine = null;
        this.priorElementCount = null;
      } 
      this.elementIndex = 0;
      this.spineIndex = 0;
    }
    
    public void forEach(T_CONS param1T_CONS) {
      for (byte b = 0; b < this.spineIndex; b++)
        arrayForEach(this.spine[b], 0, arrayLength(this.spine[b]), param1T_CONS); 
      arrayForEach(this.curChunk, 0, this.elementIndex, param1T_CONS);
    }
    
    abstract class BaseSpliterator<T_SPLITR extends Spliterator.OfPrimitive<E, T_CONS, T_SPLITR>> extends Object implements Spliterator.OfPrimitive<E, T_CONS, T_SPLITR> {
      int splSpineIndex;
      
      final int lastSpineIndex;
      
      int splElementIndex;
      
      final int lastSpineElementFence;
      
      T_ARR splChunk;
      
      BaseSpliterator(int param2Int1, int param2Int2, int param2Int3, int param2Int4) {
        this.splSpineIndex = param2Int1;
        this.lastSpineIndex = param2Int2;
        this.splElementIndex = param2Int3;
        this.lastSpineElementFence = param2Int4;
        assert SpinedBuffer.OfPrimitive.this.spine != null || (param2Int1 == 0 && param2Int2 == 0);
        this.splChunk = (SpinedBuffer.OfPrimitive.this.spine == null) ? SpinedBuffer.OfPrimitive.this.curChunk : SpinedBuffer.OfPrimitive.this.spine[param2Int1];
      }
      
      abstract T_SPLITR newSpliterator(int param2Int1, int param2Int2, int param2Int3, int param2Int4);
      
      abstract void arrayForOne(T_ARR param2T_ARR, int param2Int, T_CONS param2T_CONS);
      
      abstract T_SPLITR arraySpliterator(T_ARR param2T_ARR, int param2Int1, int param2Int2);
      
      public long estimateSize() { return (this.splSpineIndex == this.lastSpineIndex) ? (this.lastSpineElementFence - this.splElementIndex) : (SpinedBuffer.OfPrimitive.this.priorElementCount[this.lastSpineIndex] + this.lastSpineElementFence - SpinedBuffer.OfPrimitive.this.priorElementCount[this.splSpineIndex] - this.splElementIndex); }
      
      public int characteristics() { return 16464; }
      
      public boolean tryAdvance(T_CONS param2T_CONS) {
        Objects.requireNonNull(param2T_CONS);
        if (this.splSpineIndex < this.lastSpineIndex || (this.splSpineIndex == this.lastSpineIndex && this.splElementIndex < this.lastSpineElementFence)) {
          arrayForOne(this.splChunk, this.splElementIndex++, param2T_CONS);
          if (this.splElementIndex == SpinedBuffer.OfPrimitive.this.arrayLength(this.splChunk)) {
            this.splElementIndex = 0;
            this.splSpineIndex++;
            if (SpinedBuffer.OfPrimitive.this.spine != null && this.splSpineIndex <= this.lastSpineIndex)
              this.splChunk = SpinedBuffer.OfPrimitive.this.spine[this.splSpineIndex]; 
          } 
          return true;
        } 
        return false;
      }
      
      public void forEachRemaining(T_CONS param2T_CONS) {
        Objects.requireNonNull(param2T_CONS);
        if (this.splSpineIndex < this.lastSpineIndex || (this.splSpineIndex == this.lastSpineIndex && this.splElementIndex < this.lastSpineElementFence)) {
          int i = this.splElementIndex;
          for (int j = this.splSpineIndex; j < this.lastSpineIndex; j++) {
            Object object1 = SpinedBuffer.OfPrimitive.this.spine[j];
            SpinedBuffer.OfPrimitive.this.arrayForEach(object1, i, SpinedBuffer.OfPrimitive.this.arrayLength(object1), param2T_CONS);
            i = 0;
          } 
          Object object = (this.splSpineIndex == this.lastSpineIndex) ? this.splChunk : SpinedBuffer.OfPrimitive.this.spine[this.lastSpineIndex];
          SpinedBuffer.OfPrimitive.this.arrayForEach(object, i, this.lastSpineElementFence, param2T_CONS);
          this.splSpineIndex = this.lastSpineIndex;
          this.splElementIndex = this.lastSpineElementFence;
        } 
      }
      
      public T_SPLITR trySplit() {
        if (this.splSpineIndex < this.lastSpineIndex) {
          Spliterator.OfPrimitive ofPrimitive = newSpliterator(this.splSpineIndex, this.lastSpineIndex - 1, this.splElementIndex, SpinedBuffer.OfPrimitive.this.arrayLength(SpinedBuffer.OfPrimitive.this.spine[this.lastSpineIndex - 1]));
          this.splSpineIndex = this.lastSpineIndex;
          this.splElementIndex = 0;
          this.splChunk = SpinedBuffer.OfPrimitive.this.spine[this.splSpineIndex];
          return (T_SPLITR)ofPrimitive;
        } 
        if (this.splSpineIndex == this.lastSpineIndex) {
          int i = (this.lastSpineElementFence - this.splElementIndex) / 2;
          if (i == 0)
            return null; 
          Spliterator.OfPrimitive ofPrimitive = arraySpliterator(this.splChunk, this.splElementIndex, i);
          this.splElementIndex += i;
          return (T_SPLITR)ofPrimitive;
        } 
        return null;
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\SpinedBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */