package java.util.concurrent;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;
import sun.misc.Unsafe;
import sun.misc.VM;

public class ThreadLocalRandom extends Random {
  private static final AtomicInteger probeGenerator = new AtomicInteger();
  
  private static final AtomicLong seeder = new AtomicLong(initialSeed());
  
  private static final long GAMMA = -7046029254386353131L;
  
  private static final int PROBE_INCREMENT = -1640531527;
  
  private static final long SEEDER_INCREMENT = -4942790177534073029L;
  
  private static final double DOUBLE_UNIT = 1.1102230246251565E-16D;
  
  private static final float FLOAT_UNIT = 5.9604645E-8F;
  
  private static final ThreadLocal<Double> nextLocalGaussian = new ThreadLocal();
  
  boolean initialized = true;
  
  static final ThreadLocalRandom instance = new ThreadLocalRandom();
  
  static final String BadBound = "bound must be positive";
  
  static final String BadRange = "bound must be greater than origin";
  
  static final String BadSize = "size must be non-negative";
  
  private static final long serialVersionUID = -5851777807851030925L;
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("rnd", long.class), new ObjectStreamField("initialized", boolean.class) };
  
  private static final Unsafe UNSAFE;
  
  private static final long SEED;
  
  private static final long PROBE;
  
  private static final long SECONDARY;
  
  private static long initialSeed() {
    String str = VM.getSavedProperty("java.util.secureRandomSeed");
    if (Boolean.parseBoolean(str)) {
      byte[] arrayOfByte = SecureRandom.getSeed(8);
      long l = arrayOfByte[0] & 0xFFL;
      for (byte b = 1; b < 8; b++)
        l = l << 8 | arrayOfByte[b] & 0xFFL; 
      return l;
    } 
    return mix64(System.currentTimeMillis()) ^ mix64(System.nanoTime());
  }
  
  private static long mix64(long paramLong) {
    paramLong = (paramLong ^ paramLong >>> 33) * -49064778989728563L;
    paramLong = (paramLong ^ paramLong >>> 33) * -4265267296055464877L;
    return paramLong ^ paramLong >>> 33;
  }
  
  private static int mix32(long paramLong) {
    paramLong = (paramLong ^ paramLong >>> 33) * -49064778989728563L;
    return (int)((paramLong ^ paramLong >>> 33) * -4265267296055464877L >>> 32);
  }
  
  static final void localInit() {
    int i = probeGenerator.addAndGet(-1640531527);
    byte b = (i == 0) ? 1 : i;
    long l = mix64(seeder.getAndAdd(-4942790177534073029L));
    Thread thread = Thread.currentThread();
    UNSAFE.putLong(thread, SEED, l);
    UNSAFE.putInt(thread, PROBE, b);
  }
  
  public static ThreadLocalRandom current() {
    if (UNSAFE.getInt(Thread.currentThread(), PROBE) == 0)
      localInit(); 
    return instance;
  }
  
  public void setSeed(long paramLong) {
    if (this.initialized)
      throw new UnsupportedOperationException(); 
  }
  
  final long nextSeed() {
    Thread thread;
    long l;
    UNSAFE.putLong(thread = Thread.currentThread(), SEED, l = UNSAFE.getLong(thread, SEED) + -7046029254386353131L);
    return l;
  }
  
  protected int next(int paramInt) { return (int)(mix64(nextSeed()) >>> 64 - paramInt); }
  
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
  
  public float nextFloat() { return (mix32(nextSeed()) >>> 8) * 5.9604645E-8F; }
  
  public double nextGaussian() {
    double d3;
    double d2;
    double d1;
    Double double = (Double)nextLocalGaussian.get();
    if (double != null) {
      nextLocalGaussian.set(null);
      return double.doubleValue();
    } 
    do {
      d1 = 2.0D * nextDouble() - 1.0D;
      d2 = 2.0D * nextDouble() - 1.0D;
      d3 = d1 * d1 + d2 * d2;
    } while (d3 >= 1.0D || d3 == 0.0D);
    double d4 = StrictMath.sqrt(-2.0D * StrictMath.log(d3) / d3);
    nextLocalGaussian.set(new Double(d2 * d4));
    return d1 * d4;
  }
  
  public IntStream ints(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    return StreamSupport.intStream(new RandomIntsSpliterator(0L, paramLong, 2147483647, 0), false);
  }
  
  public IntStream ints() { return StreamSupport.intStream(new RandomIntsSpliterator(0L, Float.MAX_VALUE, 2147483647, 0), false); }
  
  public IntStream ints(long paramLong, int paramInt1, int paramInt2) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    if (paramInt1 >= paramInt2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.intStream(new RandomIntsSpliterator(0L, paramLong, paramInt1, paramInt2), false);
  }
  
  public IntStream ints(int paramInt1, int paramInt2) {
    if (paramInt1 >= paramInt2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.intStream(new RandomIntsSpliterator(0L, Float.MAX_VALUE, paramInt1, paramInt2), false);
  }
  
  public LongStream longs(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    return StreamSupport.longStream(new RandomLongsSpliterator(0L, paramLong, Float.MAX_VALUE, 0L), false);
  }
  
  public LongStream longs() { return StreamSupport.longStream(new RandomLongsSpliterator(0L, Float.MAX_VALUE, Float.MAX_VALUE, 0L), false); }
  
  public LongStream longs(long paramLong1, long paramLong2, long paramLong3) {
    if (paramLong1 < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    if (paramLong2 >= paramLong3)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.longStream(new RandomLongsSpliterator(0L, paramLong1, paramLong2, paramLong3), false);
  }
  
  public LongStream longs(long paramLong1, long paramLong2) {
    if (paramLong1 >= paramLong2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.longStream(new RandomLongsSpliterator(0L, Float.MAX_VALUE, paramLong1, paramLong2), false);
  }
  
  public DoubleStream doubles(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    return StreamSupport.doubleStream(new RandomDoublesSpliterator(0L, paramLong, Double.MAX_VALUE, 0.0D), false);
  }
  
  public DoubleStream doubles() { return StreamSupport.doubleStream(new RandomDoublesSpliterator(0L, Float.MAX_VALUE, Double.MAX_VALUE, 0.0D), false); }
  
  public DoubleStream doubles(long paramLong, double paramDouble1, double paramDouble2) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("size must be non-negative"); 
    if (paramDouble1 >= paramDouble2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.doubleStream(new RandomDoublesSpliterator(0L, paramLong, paramDouble1, paramDouble2), false);
  }
  
  public DoubleStream doubles(double paramDouble1, double paramDouble2) {
    if (paramDouble1 >= paramDouble2)
      throw new IllegalArgumentException("bound must be greater than origin"); 
    return StreamSupport.doubleStream(new RandomDoublesSpliterator(0L, Float.MAX_VALUE, paramDouble1, paramDouble2), false);
  }
  
  static final int getProbe() { return UNSAFE.getInt(Thread.currentThread(), PROBE); }
  
  static final int advanceProbe(int paramInt) {
    paramInt ^= paramInt << 13;
    paramInt ^= paramInt >>> 17;
    paramInt ^= paramInt << 5;
    UNSAFE.putInt(Thread.currentThread(), PROBE, paramInt);
    return paramInt;
  }
  
  static final int nextSecondarySeed() {
    Thread thread = Thread.currentThread();
    int i;
    if ((i = UNSAFE.getInt(thread, SECONDARY)) != 0) {
      i ^= i << 13;
      i ^= i >>> 17;
      i ^= i << 5;
    } else {
      localInit();
      if ((i = (int)UNSAFE.getLong(thread, SEED)) == 0)
        i = 1; 
    } 
    UNSAFE.putInt(thread, SECONDARY, i);
    return i;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("rnd", UNSAFE.getLong(Thread.currentThread(), SEED));
    putField.put("initialized", true);
    paramObjectOutputStream.writeFields();
  }
  
  private Object readResolve() { return current(); }
  
  static  {
    try {
      UNSAFE = Unsafe.getUnsafe();
      Class clazz = Thread.class;
      SEED = UNSAFE.objectFieldOffset(clazz.getDeclaredField("threadLocalRandomSeed"));
      PROBE = UNSAFE.objectFieldOffset(clazz.getDeclaredField("threadLocalRandomProbe"));
      SECONDARY = UNSAFE.objectFieldOffset(clazz.getDeclaredField("threadLocalRandomSecondarySeed"));
    } catch (Exception exception) {
      throw new Error(exception);
    } 
  }
  
  static final class RandomDoublesSpliterator implements Spliterator.OfDouble {
    long index;
    
    final long fence;
    
    final double origin;
    
    final double bound;
    
    RandomDoublesSpliterator(long param1Long1, long param1Long2, double param1Double1, double param1Double2) {
      this.index = param1Long1;
      this.fence = param1Long2;
      this.origin = param1Double1;
      this.bound = param1Double2;
    }
    
    public RandomDoublesSpliterator trySplit() {
      long l1 = this.index;
      long l2 = l1 + this.fence >>> true;
      return (l2 <= l1) ? null : new RandomDoublesSpliterator(l1, this.index = l2, this.origin, this.bound);
    }
    
    public long estimateSize() { return this.fence - this.index; }
    
    public int characteristics() { return 17728; }
    
    public boolean tryAdvance(DoubleConsumer param1DoubleConsumer) {
      if (param1DoubleConsumer == null)
        throw new NullPointerException(); 
      long l1 = this.index;
      long l2 = this.fence;
      if (l1 < l2) {
        param1DoubleConsumer.accept(ThreadLocalRandom.current().internalNextDouble(this.origin, this.bound));
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
        double d1 = this.origin;
        double d2 = this.bound;
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        do {
          param1DoubleConsumer.accept(threadLocalRandom.internalNextDouble(d1, d2));
        } while (++l1 < l2);
      } 
    }
  }
  
  static final class RandomIntsSpliterator implements Spliterator.OfInt {
    long index;
    
    final long fence;
    
    final int origin;
    
    final int bound;
    
    RandomIntsSpliterator(long param1Long1, long param1Long2, int param1Int1, int param1Int2) {
      this.index = param1Long1;
      this.fence = param1Long2;
      this.origin = param1Int1;
      this.bound = param1Int2;
    }
    
    public RandomIntsSpliterator trySplit() {
      long l1 = this.index;
      long l2 = l1 + this.fence >>> true;
      return (l2 <= l1) ? null : new RandomIntsSpliterator(l1, this.index = l2, this.origin, this.bound);
    }
    
    public long estimateSize() { return this.fence - this.index; }
    
    public int characteristics() { return 17728; }
    
    public boolean tryAdvance(IntConsumer param1IntConsumer) {
      if (param1IntConsumer == null)
        throw new NullPointerException(); 
      long l1 = this.index;
      long l2 = this.fence;
      if (l1 < l2) {
        param1IntConsumer.accept(ThreadLocalRandom.current().internalNextInt(this.origin, this.bound));
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
        int i = this.origin;
        int j = this.bound;
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        do {
          param1IntConsumer.accept(threadLocalRandom.internalNextInt(i, j));
        } while (++l1 < l2);
      } 
    }
  }
  
  static final class RandomLongsSpliterator implements Spliterator.OfLong {
    long index;
    
    final long fence;
    
    final long origin;
    
    final long bound;
    
    RandomLongsSpliterator(long param1Long1, long param1Long2, long param1Long3, long param1Long4) {
      this.index = param1Long1;
      this.fence = param1Long2;
      this.origin = param1Long3;
      this.bound = param1Long4;
    }
    
    public RandomLongsSpliterator trySplit() {
      long l1 = this.index;
      long l2 = l1 + this.fence >>> true;
      return (l2 <= l1) ? null : new RandomLongsSpliterator(l1, this.index = l2, this.origin, this.bound);
    }
    
    public long estimateSize() { return this.fence - this.index; }
    
    public int characteristics() { return 17728; }
    
    public boolean tryAdvance(LongConsumer param1LongConsumer) {
      if (param1LongConsumer == null)
        throw new NullPointerException(); 
      long l1 = this.index;
      long l2 = this.fence;
      if (l1 < l2) {
        param1LongConsumer.accept(ThreadLocalRandom.current().internalNextLong(this.origin, this.bound));
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
        long l3 = this.origin;
        long l4 = this.bound;
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        do {
          param1LongConsumer.accept(threadLocalRandom.internalNextLong(l3, l4));
        } while (++l1 < l2);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\ThreadLocalRandom.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */