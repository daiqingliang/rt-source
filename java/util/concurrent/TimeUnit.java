package java.util.concurrent;

public static final abstract enum TimeUnit {
  NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS;
  
  static final long C0 = 1L;
  
  static final long C1 = 1000L;
  
  static final long C2 = 1000000L;
  
  static final long C3 = 1000000000L;
  
  static final long C4 = 60000000000L;
  
  static final long C5 = 3600000000000L;
  
  static final long C6 = 86400000000000L;
  
  static final long MAX = 9223372036854775807L;
  
  static long x(long paramLong1, long paramLong2, long paramLong3) { return (paramLong1 > paramLong3) ? Float.MAX_VALUE : ((paramLong1 < -paramLong3) ? Float.MIN_VALUE : (paramLong1 * paramLong2)); }
  
  public long convert(long paramLong, TimeUnit paramTimeUnit) { throw new AbstractMethodError(); }
  
  public long toNanos(long paramLong) { throw new AbstractMethodError(); }
  
  public long toMicros(long paramLong) { throw new AbstractMethodError(); }
  
  public long toMillis(long paramLong) { throw new AbstractMethodError(); }
  
  public long toSeconds(long paramLong) { throw new AbstractMethodError(); }
  
  public long toMinutes(long paramLong) { throw new AbstractMethodError(); }
  
  public long toHours(long paramLong) { throw new AbstractMethodError(); }
  
  public long toDays(long paramLong) { throw new AbstractMethodError(); }
  
  abstract int excessNanos(long paramLong1, long paramLong2);
  
  public void timedWait(Object paramObject, long paramLong) throws InterruptedException {
    if (paramLong > 0L) {
      long l = toMillis(paramLong);
      int i = excessNanos(paramLong, l);
      paramObject.wait(l, i);
    } 
  }
  
  public void timedJoin(Thread paramThread, long paramLong) throws InterruptedException {
    if (paramLong > 0L) {
      long l = toMillis(paramLong);
      int i = excessNanos(paramLong, l);
      paramThread.join(l, i);
    } 
  }
  
  public void sleep(long paramLong) throws InterruptedException {
    if (paramLong > 0L) {
      long l = toMillis(paramLong);
      int i = excessNanos(paramLong, l);
      Thread.sleep(l, i);
    } 
  }
  
  static  {
    // Byte code:
    //   0: new java/util/concurrent/TimeUnit$1
    //   3: dup
    //   4: ldc 'NANOSECONDS'
    //   6: iconst_0
    //   7: invokespecial <init> : (Ljava/lang/String;I)V
    //   10: putstatic java/util/concurrent/TimeUnit.NANOSECONDS : Ljava/util/concurrent/TimeUnit;
    //   13: new java/util/concurrent/TimeUnit$2
    //   16: dup
    //   17: ldc 'MICROSECONDS'
    //   19: iconst_1
    //   20: invokespecial <init> : (Ljava/lang/String;I)V
    //   23: putstatic java/util/concurrent/TimeUnit.MICROSECONDS : Ljava/util/concurrent/TimeUnit;
    //   26: new java/util/concurrent/TimeUnit$3
    //   29: dup
    //   30: ldc 'MILLISECONDS'
    //   32: iconst_2
    //   33: invokespecial <init> : (Ljava/lang/String;I)V
    //   36: putstatic java/util/concurrent/TimeUnit.MILLISECONDS : Ljava/util/concurrent/TimeUnit;
    //   39: new java/util/concurrent/TimeUnit$4
    //   42: dup
    //   43: ldc 'SECONDS'
    //   45: iconst_3
    //   46: invokespecial <init> : (Ljava/lang/String;I)V
    //   49: putstatic java/util/concurrent/TimeUnit.SECONDS : Ljava/util/concurrent/TimeUnit;
    //   52: new java/util/concurrent/TimeUnit$5
    //   55: dup
    //   56: ldc 'MINUTES'
    //   58: iconst_4
    //   59: invokespecial <init> : (Ljava/lang/String;I)V
    //   62: putstatic java/util/concurrent/TimeUnit.MINUTES : Ljava/util/concurrent/TimeUnit;
    //   65: new java/util/concurrent/TimeUnit$6
    //   68: dup
    //   69: ldc 'HOURS'
    //   71: iconst_5
    //   72: invokespecial <init> : (Ljava/lang/String;I)V
    //   75: putstatic java/util/concurrent/TimeUnit.HOURS : Ljava/util/concurrent/TimeUnit;
    //   78: new java/util/concurrent/TimeUnit$7
    //   81: dup
    //   82: ldc 'DAYS'
    //   84: bipush #6
    //   86: invokespecial <init> : (Ljava/lang/String;I)V
    //   89: putstatic java/util/concurrent/TimeUnit.DAYS : Ljava/util/concurrent/TimeUnit;
    //   92: bipush #7
    //   94: anewarray java/util/concurrent/TimeUnit
    //   97: dup
    //   98: iconst_0
    //   99: getstatic java/util/concurrent/TimeUnit.NANOSECONDS : Ljava/util/concurrent/TimeUnit;
    //   102: aastore
    //   103: dup
    //   104: iconst_1
    //   105: getstatic java/util/concurrent/TimeUnit.MICROSECONDS : Ljava/util/concurrent/TimeUnit;
    //   108: aastore
    //   109: dup
    //   110: iconst_2
    //   111: getstatic java/util/concurrent/TimeUnit.MILLISECONDS : Ljava/util/concurrent/TimeUnit;
    //   114: aastore
    //   115: dup
    //   116: iconst_3
    //   117: getstatic java/util/concurrent/TimeUnit.SECONDS : Ljava/util/concurrent/TimeUnit;
    //   120: aastore
    //   121: dup
    //   122: iconst_4
    //   123: getstatic java/util/concurrent/TimeUnit.MINUTES : Ljava/util/concurrent/TimeUnit;
    //   126: aastore
    //   127: dup
    //   128: iconst_5
    //   129: getstatic java/util/concurrent/TimeUnit.HOURS : Ljava/util/concurrent/TimeUnit;
    //   132: aastore
    //   133: dup
    //   134: bipush #6
    //   136: getstatic java/util/concurrent/TimeUnit.DAYS : Ljava/util/concurrent/TimeUnit;
    //   139: aastore
    //   140: putstatic java/util/concurrent/TimeUnit.$VALUES : [Ljava/util/concurrent/TimeUnit;
    //   143: return
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\TimeUnit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */