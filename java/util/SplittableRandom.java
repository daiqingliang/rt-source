package java.util;

import java.security.AccessController;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;
import sun.security.action.GetPropertyAction;

public final class SplittableRandom {
  private static final long GOLDEN_GAMMA = -7046029254386353131L;
  
  private static final double DOUBLE_UNIT = 1.1102230246251565E-16D;
  
  private long seed;
  
  private final long gamma;
  
  private static final AtomicLong defaultGen = new AtomicLong(initialSeed());
  
  static final String BadBound = "bound must be positive";
  
  static final String BadRange = "bound must be greater than origin";
  
  static final String BadSize = "size must be non-negative";
  
  private SplittableRandom(long paramLong1, long paramLong2) {
    this.seed = paramLong1;
    this.gamma = paramLong2;
  }
  
  private static long mix64(long paramLong) {
    paramLong = (paramLong ^ paramLong >>> 30) * -4658895280553007687L;
    paramLong = (paramLong ^ paramLong >>> 27) * -7723592293110705685L;
    return paramLong ^ paramLong >>> 31;
  }
  
  private static int mix32(long paramLong) {
    paramLong = (paramLong ^ paramLong >>> 33) * 7109453100751455733L;
    return (int)((paramLong ^ paramLong >>> 28) * -3808689974395783757L >>> 32);
  }
  
  private static long mixGamma(long paramLong) {
    paramLong = (paramLong ^ paramLong >>> 33) * -49064778989728563L;
    paramLong = (paramLong ^ paramLong >>> 33) * -4265267296055464877L;
    paramLong = paramLong ^ paramLong >>> 33 | 0x1L;
    int i = Long.bitCount(paramLong ^ paramLong >>> true);
    return (i < 24) ? (paramLong ^ 0xAAAAAAAAAAAAAAAAL) : paramLong;
  }
  
  private long nextSeed() { return this.seed += this.gamma; }
  
  private static long initialSeed() {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.util.secureRandomSeed"));
    if (str != null && str.equalsIgnoreCase("true")) {
      byte[] arrayOfByte = SecureRandom.getSeed(8);
      long l = arrayOfByte[0] & 0xFFL;
      for (byte b = 1; b < 8; b++)
        l = l << 8 | arrayOfByte[b] & 0xFFL; 
      return l;
    } 
    return mix64(System.currentTimeMillis()) ^ mix64(System.nanoTime());
  }
  
  final long internalNextLong(long paramLong1, long paramLong2) {
    long l = mix64(nextSeed());
    if (paramLong1 < paramLong2) {
      long l1 = paramLong2 - paramLong1;
      long l2 = l1 - 1L;
      if ((l1 & l2) == 0L) {
        l = (l & l2) + paramLong1;
      } else if (l1 > 0L) {
        long l3;
        for (l3 = l >>> true; l3 + l2 - (l = l3 % l1) < 0L; l3 = mix64(nextSeed()) >>> true);
        l += paramLong1;
      } else {
        while (l < paramLong1 || l >= paramLong2)
          l = mix64(nextSeed()); 
      } 
    } 
    return l;
  }
  
  final int internalNextInt(int paramInt1, int paramInt2) {
    int i = mix32(nextSeed());
    if (paramInt1 < paramInt2) {
      int j = paramInt2 - paramInt1;
      int k = j - 1;
      if ((j & k) == 0) {
        i = (i & k) + paramInt1;
      } else if (j > 0) {
        int m;
        for (m = i >>> 1; m + k - (i = m % j) < 0; m = mix32(nextSeed()) >>> 1);
        i += paramInt1;
      } else {
        while (i < paramInt1 || i >= paramInt2)
          i = mix32(nextSeed()); 
      } 
    } 
    return i;
  }
  
  final double internalNextDouble(double paramDouble1, double paramDouble2) {
    double d = (nextLong() >>> 11) * 1.1102230246251565E-16D;
    if (paramDouble1 < paramDouble2) {
      d = d * (paramDouble2 - paramDouble1) + paramDouble1;
      if (d >= paramDouble2)
        d = Double.longBitsToDouble(Double.doubleToLongBits(paramDouble2) - 1L); 
    } 
    return d;
  }
  
  public SplittableRandom(long paramLong) { this(paramLong, -7046029254386353131L); }
  
  public SplittableRandom() {
    long l = defaultGen.getAndAdd(4354685564936845354L);
    this.seed = mix64(l);
    this.gamma = mixGamma(l + -7046029254386353131L);
  }
  
  public SplittableRandom split() { return new SplittableRandom(nextLong(), mixGamma(nextSeed())); }
  
  public int nextInt() { return mix32(nextSeed()); }
  
  public int nextInt(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException("bound must be positive"); 
    int i = mix32(nextSeed());
    int j = paramInt - 1;
    if ((paramInt & j) == 0) {
      i &= j;
    } else {
      int k;
      for (k = i >>> 1; k + j - (i = k % paramInt) < 0; k = mix32(nextSeed()) >>> 1);
    } 
    return i;
  }
  
  public int nextInt(int paramInt1, int paramInt2) {
    if (paramInt1 >= paramInt2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return internalNextInt(paramInt1, paramInt2);
  }
  
  public long nextLong() { return mix64(nextSeed()); }
  
  public long nextLong(long paramLong) {
    if (paramLong <= 0L)
      throw new IllegalArgumentException("bound must be positive"); 
    long l1 = mix64(nextSeed());
    long l2 = paramLong - 1L;
    if ((paramLong & l2) == 0L) {
      l1 &= l2;
    } else {
      long l;
      for (l = l1 >>> true; l + l2 - (l1 = l % paramLong) < 0L; l = mix64(nextSeed()) >>> true);
    } 
    return l1;
  }
  
  public long nextLong(long paramLong1, long paramLong2) {
    if (paramLong1 >= paramLong2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return internalNextLong(paramLong1, paramLong2);
  }
  
  public double nextDouble() { return (mix64(nextSeed()) >>> 11) * 1.1102230246251565E-16D; }
  
  public double nextDouble(double paramDouble) {
    if (paramDouble <= 0.0D)
      throw new IllegalArgumentException("bound must be positive"); 
    double d = (mix64(nextSeed()) >>> 11) * 1.1102230246251565E-16D * paramDouble;
    return (d < paramDouble) ? d : Double.longBitsToDouble(Double.doubleToLongBits(paramDouble) - 1L);
  }
  
  public double nextDouble(double paramDouble1, double paramDouble2) {
    if (paramDouble1 >= paramDouble2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return internalNextDouble(paramDouble1, paramDouble2);
  }
  
  public boolean nextBoolean() { return (mix32(nextSeed()) < 0); }
  
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
  
  static final class RandomDoublesSpliterator implements Spliterator.OfDouble {
    final SplittableRandom rng;
    
    long index;
    
    final long fence;
    
    final double origin;
    
    final double bound;
    
    RandomDoublesSpliterator(SplittableRandom param1SplittableRandom, long param1Long1, long param1Long2, double param1Double1, double param1Double2) {
      this.rng = param1SplittableRandom;
      this.index = param1Long1;
      this.fence = param1Long2;
      this.origin = param1Double1;
      this.bound = param1Double2;
    }
    
    public RandomDoublesSpliterator trySplit() {
      long l1 = this.index;
      long l2 = l1 + this.fence >>> true;
      return (l2 <= l1) ? null : new RandomDoublesSpliterator(this.rng.split(), l1, this.index = l2, this.origin, this.bound);
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
        SplittableRandom splittableRandom = this.rng;
        double d1 = this.origin;
        double d2 = this.bound;
        do {
          param1DoubleConsumer.accept(splittableRandom.internalNextDouble(d1, d2));
        } while (++l1 < l2);
      } 
    }
  }
  
  static final class RandomIntsSpliterator implements Spliterator.OfInt {
    final SplittableRandom rng;
    
    long index;
    
    final long fence;
    
    final int origin;
    
    final int bound;
    
    RandomIntsSpliterator(SplittableRandom param1SplittableRandom, long param1Long1, long param1Long2, int param1Int1, int param1Int2) {
      this.rng = param1SplittableRandom;
      this.index = param1Long1;
      this.fence = param1Long2;
      this.origin = param1Int1;
      this.bound = param1Int2;
    }
    
    public RandomIntsSpliterator trySplit() {
      long l1 = this.index;
      long l2 = l1 + this.fence >>> true;
      return (l2 <= l1) ? null : new RandomIntsSpliterator(this.rng.split(), l1, this.index = l2, this.origin, this.bound);
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
        SplittableRandom splittableRandom = this.rng;
        int i = this.origin;
        int j = this.bound;
        do {
          param1IntConsumer.accept(splittableRandom.internalNextInt(i, j));
        } while (++l1 < l2);
      } 
    }
  }
  
  static final class RandomLongsSpliterator implements Spliterator.OfLong {
    final SplittableRandom rng;
    
    long index;
    
    final long fence;
    
    final long origin;
    
    final long bound;
    
    RandomLongsSpliterator(SplittableRandom param1SplittableRandom, long param1Long1, long param1Long2, long param1Long3, long param1Long4) {
      this.rng = param1SplittableRandom;
      this.index = param1Long1;
      this.fence = param1Long2;
      this.origin = param1Long3;
      this.bound = param1Long4;
    }
    
    public RandomLongsSpliterator trySplit() {
      long l1 = this.index;
      long l2 = l1 + this.fence >>> true;
      return (l2 <= l1) ? null : new RandomLongsSpliterator(this.rng.split(), l1, this.index = l2, this.origin, this.bound);
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
        SplittableRandom splittableRandom = this.rng;
        long l3 = this.origin;
        long l4 = this.bound;
        do {
          param1LongConsumer.accept(splittableRandom.internalNextLong(l3, l4));
        } while (++l1 < l2);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\SplittableRandom.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */