package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;
import sun.misc.Unsafe;

public class Random implements Serializable {
  static final long serialVersionUID = 3905348978240129619L;
  
  private final AtomicLong seed;
  
  private static final long multiplier = 25214903917L;
  
  private static final long addend = 11L;
  
  private static final long mask = 281474976710655L;
  
  private static final double DOUBLE_UNIT = 1.1102230246251565E-16D;
  
  static final String BadBound = "bound must be positive";
  
  static final String BadRange = "bound must be greater than origin";
  
  static final String BadSize = "size must be non-negative";
  
  private static final AtomicLong seedUniquifier = new AtomicLong(8682522807148012L);
  
  private double nextNextGaussian;
  
  private boolean haveNextNextGaussian = false;
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("seed", long.class), new ObjectStreamField("nextNextGaussian", double.class), new ObjectStreamField("haveNextNextGaussian", boolean.class) };
  
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final long seedOffset;
  
  public Random() { this(seedUniquifier() ^ System.nanoTime()); }
  
  private static long seedUniquifier() {
    long l2;
    long l1;
    do {
      l1 = seedUniquifier.get();
      l2 = l1 * 181783497276652981L;
    } while (!seedUniquifier.compareAndSet(l1, l2));
    return l2;
  }
  
  public Random(long paramLong) {
    if (getClass() == Random.class) {
      this.seed = new AtomicLong(initialScramble(paramLong));
    } else {
      this.seed = new AtomicLong();
      setSeed(paramLong);
    } 
  }
  
  private static long initialScramble(long paramLong) { return (paramLong ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL; }
  
  public void setSeed(long paramLong) {
    this.seed.set(initialScramble(paramLong));
    this.haveNextNextGaussian = false;
  }
  
  protected int next(int paramInt) {
    long l2;
    long l1;
    AtomicLong atomicLong = this.seed;
    do {
      l1 = atomicLong.get();
      l2 = l1 * 25214903917L + 11L & 0xFFFFFFFFFFFFL;
    } while (!atomicLong.compareAndSet(l1, l2));
    return (int)(l2 >>> 48 - paramInt);
  }
  
  public void nextBytes(byte[] paramArrayOfByte) {
    int i = 0;
    int j = paramArrayOfByte.length;
    while (i < j) {
      int k = nextInt();
      int m = Math.min(j - i, 4);
      while (m-- > 0) {
        paramArrayOfByte[i++] = (byte)k;
        k >>= 8;
      } 
    } 
  }
  
  final long internalNextLong(long paramLong1, long paramLong2) {
    long l = nextLong();
    if (paramLong1 < paramLong2) {
      long l1 = paramLong2 - paramLong1;
      long l2 = l1 - 1L;
      if ((l1 & l2) == 0L) {
        l = (l & l2) + paramLong1;
      } else if (l1 > 0L) {
        long l3;
        for (l3 = l >>> true; l3 + l2 - (l = l3 % l1) < 0L; l3 = nextLong() >>> true);
        l += paramLong1;
      } else {
        while (l < paramLong1 || l >= paramLong2)
          l = nextLong(); 
      } 
    } 
    return l;
  }
  
  final int internalNextInt(int paramInt1, int paramInt2) {
    if (paramInt1 < paramInt2) {
      int j;
      int i = paramInt2 - paramInt1;
      if (i > 0)
        return nextInt(i) + paramInt1; 
      do {
        j = nextInt();
      } while (j < paramInt1 || j >= paramInt2);
      return j;
    } 
    return nextInt();
  }
  
  final double internalNextDouble(double paramDouble1, double paramDouble2) {
    double d = nextDouble();
    if (paramDouble1 < paramDouble2) {
      d = d * (paramDouble2 - paramDouble1) + paramDouble1;
      if (d >= paramDouble2)
        d = Double.longBitsToDouble(Double.doubleToLongBits(paramDouble2) - 1L); 
    } 
    return d;
  }
  
  public int nextInt() { return next(32); }
  
  public int nextInt(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException("bound must be positive"); 
    int i = next(31);
    int j = paramInt - 1;
    if ((paramInt & j) == 0) {
      i = (int)(paramInt * i >> 31);
    } else {
      int k;
      for (k = i; k - (i = k % paramInt) + j < 0; k = next(31));
    } 
    return i;
  }
  
  public long nextLong() { return (next(32) << 32) + next(32); }
  
  public boolean nextBoolean() { return (next(1) != 0); }
  
  public float nextFloat() { return next(24) / 1.6777216E7F; }
  
  public double nextDouble() { return ((next(26) << 27) + next(27)) * 1.1102230246251565E-16D; }
  
  public double nextGaussian() {
    double d3;
    double d2;
    double d1;
    if (this.haveNextNextGaussian) {
      this.haveNextNextGaussian = false;
      return this.nextNextGaussian;
    } 
    do {
      d1 = 2.0D * nextDouble() - 1.0D;
      d2 = 2.0D * nextDouble() - 1.0D;
      d3 = d1 * d1 + d2 * d2;
    } while (d3 >= 1.0D || d3 == 0.0D);
    double d4 = StrictMath.sqrt(-2.0D * StrictMath.log(d3) / d3);
    this.nextNextGaussian = d2 * d4;
    this.haveNextNextGaussian = true;
    return d1 * d4;
  }
  
  public IntStream ints(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    return StreamSupport.intStream(new RandomIntsSpliterator(this, 0L, paramLong, 2147483647, 0), false);
  }
  
  public IntStream ints() { return StreamSupport.intStream(new RandomIntsSpliterator(this, 0L, Float.MAX_VALUE, 2147483647, 0), false); }
  
  public IntStream ints(long paramLong, int paramInt1, int paramInt2) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    if (paramInt1 >= paramInt2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.intStream(new RandomIntsSpliterator(this, 0L, paramLong, paramInt1, paramInt2), false);
  }
  
  public IntStream ints(int paramInt1, int paramInt2) {
    if (paramInt1 >= paramInt2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.intStream(new RandomIntsSpliterator(this, 0L, Float.MAX_VALUE, paramInt1, paramInt2), false);
  }
  
  public LongStream longs(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    return StreamSupport.longStream(new RandomLongsSpliterator(this, 0L, paramLong, Float.MAX_VALUE, 0L), false);
  }
  
  public LongStream longs() { return StreamSupport.longStream(new RandomLongsSpliterator(this, 0L, Float.MAX_VALUE, Float.MAX_VALUE, 0L), false); }
  
  public LongStream longs(long paramLong1, long paramLong2, long paramLong3) {
    if (paramLong1 < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    if (paramLong2 >= paramLong3)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.longStream(new RandomLongsSpliterator(this, 0L, paramLong1, paramLong2, paramLong3), false);
  }
  
  public LongStream longs(long paramLong1, long paramLong2) {
    if (paramLong1 >= paramLong2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.longStream(new RandomLongsSpliterator(this, 0L, Float.MAX_VALUE, paramLong1, paramLong2), false);
  }
  
  public DoubleStream doubles(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    return StreamSupport.doubleStream(new RandomDoublesSpliterator(this, 0L, paramLong, Double.MAX_VALUE, 0.0D), false);
  }
  
  public DoubleStream doubles() { return StreamSupport.doubleStream(new RandomDoublesSpliterator(this, 0L, Float.MAX_VALUE, Double.MAX_VALUE, 0.0D), false); }
  
  public DoubleStream doubles(long paramLong, double paramDouble1, double paramDouble2) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    if (paramDouble1 >= paramDouble2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.doubleStream(new RandomDoublesSpliterator(this, 0L, paramLong, paramDouble1, paramDouble2), false);
  }
  
  public DoubleStream doubles(double paramDouble1, double paramDouble2) {
    if (paramDouble1 >= paramDouble2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.doubleStream(new RandomDoublesSpliterator(this, 0L, Float.MAX_VALUE, paramDouble1, paramDouble2), false);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    long l = getField.get("seed", -1L);
    if (l < 0L)
      throw new StreamCorruptedException("Random: invalid seed"); 
    resetSeed(l);
    this.nextNextGaussian = getField.get("nextNextGaussian", 0.0D);
    this.haveNextNextGaussian = getField.get("haveNextNextGaussian", false);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("seed", this.seed.get());
    putField.put("nextNextGaussian", this.nextNextGaussian);
    putField.put("haveNextNextGaussian", this.haveNextNextGaussian);
    paramObjectOutputStream.writeFields();
  }
  
  private void resetSeed(long paramLong) { unsafe.putObjectVolatile(this, seedOffset, new AtomicLong(paramLong)); }
  
  static  {
    try {
      seedOffset = unsafe.objectFieldOffset(Random.class.getDeclaredField("seed"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  static final class RandomDoublesSpliterator implements Spliterator.OfDouble {
    final Random rng;
    
    long index;
    
    final long fence;
    
    final double origin;
    
    final double bound;
    
    RandomDoublesSpliterator(Random param1Random, long param1Long1, long param1Long2, double param1Double1, double param1Double2) {
      this.rng = param1Random;
      this.index = param1Long1;
      this.fence = param1Long2;
      this.origin = param1Double1;
      this.bound = param1Double2;
    }
    
    public RandomDoublesSpliterator trySplit() {
      long l1 = this.index;
      long l2 = l1 + this.fence >>> true;
      return (l2 <= l1) ? null : new RandomDoublesSpliterator(this.rng, l1, this.index = l2, this.origin, this.bound);
    }
    
    public long estimateSize() { return this.fence - this.index; }
    
    public int characteristics() { return 17728; }
    
    public boolean tryAdvance(DoubleConsumer param1DoubleConsumer) {
      if (param1DoubleConsumer == null)
        throw new NullPointerException(); 
      long l1 = this.index;
      long l2 = this.fence;
      if (l1 < l2) {
        param1DoubleConsumer.accept(this.rng.internalNextDouble(this.origin, this.bound));
        this.index = l1 + 1L;
        return true;
      } 
      return false;
    }
    
    public void forEachRemaining(DoubleConsumer param1DoubleConsumer) {
      if (param1DoubleConsumer == null)
        throw new NullPointerException(); 
      long l1 = this.index;
      long l2 = this.fence;
      if (l1 < l2) {
        this.index = l2;
        Random random = this.rng;
        double d1 = this.origin;
        double d2 = this.bound;
        do {
          param1DoubleConsumer.accept(random.internalNextDouble(d1, d2));
        } while (++l1 < l2);
      } 
    }
  }
  
  static final class RandomIntsSpliterator implements Spliterator.OfInt {
    final Random rng;
    
    long index;
    
    final long fence;
    
    final int origin;
    
    final int bound;
    
    RandomIntsSpliterator(Random param1Random, long param1Long1, long param1Long2, int param1Int1, int param1Int2) {
      this.rng = param1Random;
      this.index = param1Long1;
      this.fence = param1Long2;
      this.origin = param1Int1;
      this.bound = param1Int2;
    }
    
    public RandomIntsSpliterator trySplit() {
      long l1 = this.index;
      long l2 = l1 + this.fence >>> true;
      return (l2 <= l1) ? null : new RandomIntsSpliterator(this.rng, l1, this.index = l2, this.origin, this.bound);
    }
    
    public long estimateSize() { return this.fence - this.index; }
    
    public int characteristics() { return 17728; }
    
    public boolean tryAdvance(IntConsumer param1IntConsumer) {
      if (param1IntConsumer == null)
        throw new NullPointerException(); 
      long l1 = this.index;
      long l2 = this.fence;
      if (l1 < l2) {
        param1IntConsumer.accept(this.rng.internalNextInt(this.origin, this.bound));
        this.index = l1 + 1L;
        return true;
      } 
      return false;
    }
    
    public void forEachRemaining(IntConsumer param1IntConsumer) {
      if (param1IntConsumer == null)
        throw new NullPointerException(); 
      long l1 = this.index;
      long l2 = this.fence;
      if (l1 < l2) {
        this.index = l2;
        Random random = this.rng;
        int i = this.origin;
        int j = this.bound;
        do {
          param1IntConsumer.accept(random.internalNextInt(i, j));
        } while (++l1 < l2);
      } 
    }
  }
  
  static final class RandomLongsSpliterator implements Spliterator.OfLong {
    final Random rng;
    
    long index;
    
    final long fence;
    
    final long origin;
    
    final long bound;
    
    RandomLongsSpliterator(Random param1Random, long param1Long1, long param1Long2, long param1Long3, long param1Long4) {
      this.rng = param1Random;
      this.index = param1Long1;
      this.fence = param1Long2;
      this.origin = param1Long3;
      this.bound = param1Long4;
    }
    
    public RandomLongsSpliterator trySplit() {
      long l1 = this.index;
      long l2 = l1 + this.fence >>> true;
      return (l2 <= l1) ? null : new RandomLongsSpliterator(this.rng, l1, this.index = l2, this.origin, this.bound);
    }
    
    public long estimateSize() { return this.fence - this.index; }
    
    public int characteristics() { return 17728; }
    
    public boolean tryAdvance(LongConsumer param1LongConsumer) {
      if (param1LongConsumer == null)
        throw new NullPointerException(); 
      long l1 = this.index;
      long l2 = this.fence;
      if (l1 < l2) {
        param1LongConsumer.accept(this.rng.internalNextLong(this.origin, this.bound));
        this.index = l1 + 1L;
        return true;
      } 
      return false;
    }
    
    public void forEachRemaining(LongConsumer param1LongConsumer) {
      if (param1LongConsumer == null)
        throw new NullPointerException(); 
      long l1 = this.index;
      long l2 = this.fence;
      if (l1 < l2) {
        this.index = l2;
        Random random = this.rng;
        long l3 = this.origin;
        long l4 = this.bound;
        do {
          param1LongConsumer.accept(random.internalNextLong(l3, l4));
        } while (++l1 < l2);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Random.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */