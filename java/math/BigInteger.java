package java.math;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import sun.misc.Unsafe;

public class BigInteger extends Number implements Comparable<BigInteger> {
  final int signum;
  
  final int[] mag;
  
  @Deprecated
  private int bitCount;
  
  @Deprecated
  private int bitLength;
  
  @Deprecated
  private int lowestSetBit;
  
  @Deprecated
  private int firstNonzeroIntNum;
  
  static final long LONG_MASK = 4294967295L;
  
  private static final int MAX_MAG_LENGTH = 67108864;
  
  private static final int PRIME_SEARCH_BIT_LENGTH_LIMIT = 500000000;
  
  private static final int KARATSUBA_THRESHOLD = 80;
  
  private static final int TOOM_COOK_THRESHOLD = 240;
  
  private static final int KARATSUBA_SQUARE_THRESHOLD = 128;
  
  private static final int TOOM_COOK_SQUARE_THRESHOLD = 216;
  
  static final int BURNIKEL_ZIEGLER_THRESHOLD = 80;
  
  static final int BURNIKEL_ZIEGLER_OFFSET = 40;
  
  private static final int SCHOENHAGE_BASE_CONVERSION_THRESHOLD = 20;
  
  private static final int MULTIPLY_SQUARE_THRESHOLD = 20;
  
  private static final int MONTGOMERY_INTRINSIC_THRESHOLD = 512;
  
  private static long[] bitsPerDigit = { 
      0L, 0L, 1024L, 1624L, 2048L, 2378L, 2648L, 2875L, 3072L, 3247L, 
      3402L, 3543L, 3672L, 3790L, 3899L, 4001L, 4096L, 4186L, 4271L, 4350L, 
      4426L, 4498L, 4567L, 4633L, 4696L, 4756L, 4814L, 4870L, 4923L, 4975L, 
      5025L, 5074L, 5120L, 5166L, 5210L, 5253L, 5295L };
  
  private static final int SMALL_PRIME_THRESHOLD = 95;
  
  private static final int DEFAULT_PRIME_CERTAINTY = 100;
  
  private static final BigInteger SMALL_PRIME_PRODUCT = valueOf(152125131763605L);
  
  private static final int MAX_CONSTANT = 16;
  
  private static BigInteger[] posConst = new BigInteger[17];
  
  private static BigInteger[] negConst = new BigInteger[17];
  
  private static final double[] logCache;
  
  private static final double LOG_TWO = Math.log(2.0D);
  
  public static final BigInteger ZERO;
  
  public static final BigInteger ONE;
  
  private static final BigInteger TWO;
  
  private static final BigInteger NEGATIVE_ONE;
  
  public static final BigInteger TEN;
  
  static int[] bnExpModThreshTable;
  
  private static String[] zeros;
  
  private static int[] digitsPerLong;
  
  private static BigInteger[] longRadix;
  
  private static int[] digitsPerInt;
  
  private static int[] intRadix;
  
  private static final long serialVersionUID = -8287574255936472291L;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  public BigInteger(byte[] paramArrayOfByte) {
    if (paramArrayOfByte.length == 0)
      throw new NumberFormatException("Zero length BigInteger"); 
    if (paramArrayOfByte[0] < 0) {
      this.mag = makePositive(paramArrayOfByte);
      this.signum = -1;
    } else {
      this.mag = stripLeadingZeroBytes(paramArrayOfByte);
      this.signum = (this.mag.length == 0) ? 0 : 1;
    } 
    if (this.mag.length >= 67108864)
      checkRange(); 
  }
  
  private BigInteger(int[] paramArrayOfInt) {
    if (paramArrayOfInt.length == 0)
      throw new NumberFormatException("Zero length BigInteger"); 
    if (paramArrayOfInt[0] < 0) {
      this.mag = makePositive(paramArrayOfInt);
      this.signum = -1;
    } else {
      this.mag = trustedStripLeadingZeroInts(paramArrayOfInt);
      this.signum = (this.mag.length == 0) ? 0 : 1;
    } 
    if (this.mag.length >= 67108864)
      checkRange(); 
  }
  
  public BigInteger(int paramInt, byte[] paramArrayOfByte) {
    this.mag = stripLeadingZeroBytes(paramArrayOfByte);
    if (paramInt < -1 || paramInt > 1)
      throw new NumberFormatException("Invalid signum value"); 
    if (this.mag.length == 0) {
      this.signum = 0;
    } else {
      if (paramInt == 0)
        throw new NumberFormatException("signum-magnitude mismatch"); 
      this.signum = paramInt;
    } 
    if (this.mag.length >= 67108864)
      checkRange(); 
  }
  
  private BigInteger(int paramInt, int[] paramArrayOfInt) {
    this.mag = stripLeadingZeroInts(paramArrayOfInt);
    if (paramInt < -1 || paramInt > 1)
      throw new NumberFormatException("Invalid signum value"); 
    if (this.mag.length == 0) {
      this.signum = 0;
    } else {
      if (paramInt == 0)
        throw new NumberFormatException("signum-magnitude mismatch"); 
      this.signum = paramInt;
    } 
    if (this.mag.length >= 67108864)
      checkRange(); 
  }
  
  public BigInteger(String paramString, int paramInt) {
    int i = 0;
    int k = paramString.length();
    if (paramInt < 2 || paramInt > 36)
      throw new NumberFormatException("Radix out of range"); 
    if (k == 0)
      throw new NumberFormatException("Zero length BigInteger"); 
    byte b = 1;
    int m = paramString.lastIndexOf('-');
    int n = paramString.lastIndexOf('+');
    if (m >= 0) {
      if (m != 0 || n >= 0)
        throw new NumberFormatException("Illegal embedded sign character"); 
      b = -1;
      i = 1;
    } else if (n >= 0) {
      if (n != 0)
        throw new NumberFormatException("Illegal embedded sign character"); 
      i = 1;
    } 
    if (i == k)
      throw new NumberFormatException("Zero length BigInteger"); 
    while (i < k && Character.digit(paramString.charAt(i), paramInt) == 0)
      i++; 
    if (i == k) {
      this.signum = 0;
      this.mag = ZERO.mag;
      return;
    } 
    int j = k - i;
    this.signum = b;
    long l = (j * bitsPerDigit[paramInt] >>> 10) + 1L;
    if (l + 31L >= 4294967296L)
      reportOverflow(); 
    int i1 = (int)(l + 31L) >>> 5;
    int[] arrayOfInt = new int[i1];
    int i2 = j % digitsPerInt[paramInt];
    if (i2 == 0)
      i2 = digitsPerInt[paramInt]; 
    String str = paramString.substring(i, i += i2);
    arrayOfInt[i1 - 1] = Integer.parseInt(str, paramInt);
    if (arrayOfInt[i1 - 1] < 0)
      throw new NumberFormatException("Illegal digit"); 
    int i3 = intRadix[paramInt];
    int i4 = 0;
    while (i < k) {
      str = paramString.substring(i, i += digitsPerInt[paramInt]);
      i4 = Integer.parseInt(str, paramInt);
      if (i4 < 0)
        throw new NumberFormatException("Illegal digit"); 
      destructiveMulAdd(arrayOfInt, i3, i4);
    } 
    this.mag = trustedStripLeadingZeroInts(arrayOfInt);
    if (this.mag.length >= 67108864)
      checkRange(); 
  }
  
  BigInteger(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    int i;
    for (i = 0; i < paramInt2 && Character.digit(paramArrayOfChar[i], 10) == 0; i++);
    if (i == paramInt2) {
      this.signum = 0;
      this.mag = ZERO.mag;
      return;
    } 
    int j = paramInt2 - i;
    this.signum = paramInt1;
    if (paramInt2 < 10) {
      k = 1;
    } else {
      long l = (j * bitsPerDigit[10] >>> 10) + 1L;
      if (l + 31L >= 4294967296L)
        reportOverflow(); 
      k = (int)(l + 31L) >>> 5;
    } 
    int[] arrayOfInt = new int[k];
    int m = j % digitsPerInt[10];
    if (m == 0)
      m = digitsPerInt[10]; 
    arrayOfInt[k - 1] = parseInt(paramArrayOfChar, i, i += m);
    while (i < paramInt2) {
      int n = parseInt(paramArrayOfChar, i, i += digitsPerInt[10]);
      destructiveMulAdd(arrayOfInt, intRadix[10], n);
    } 
    this.mag = trustedStripLeadingZeroInts(arrayOfInt);
    if (this.mag.length >= 67108864)
      checkRange(); 
  }
  
  private int parseInt(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    int i = Character.digit(paramArrayOfChar[paramInt1++], 10);
    if (i == -1)
      throw new NumberFormatException(new String(paramArrayOfChar)); 
    for (int j = paramInt1; j < paramInt2; j++) {
      int k = Character.digit(paramArrayOfChar[j], 10);
      if (k == -1)
        throw new NumberFormatException(new String(paramArrayOfChar)); 
      i = 10 * i + k;
    } 
    return i;
  }
  
  private static void destructiveMulAdd(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    long l1 = paramInt1 & 0xFFFFFFFFL;
    long l2 = paramInt2 & 0xFFFFFFFFL;
    int i = paramArrayOfInt.length;
    long l3 = 0L;
    long l4 = 0L;
    for (int j = i - 1; j >= 0; j--) {
      l3 = l1 * (paramArrayOfInt[j] & 0xFFFFFFFFL) + l4;
      paramArrayOfInt[j] = (int)l3;
      l4 = l3 >>> 32;
    } 
    long l5 = (paramArrayOfInt[i - 1] & 0xFFFFFFFFL) + l2;
    paramArrayOfInt[i - 1] = (int)l5;
    l4 = l5 >>> 32;
    for (int k = i - 2; k >= 0; k--) {
      l5 = (paramArrayOfInt[k] & 0xFFFFFFFFL) + l4;
      paramArrayOfInt[k] = (int)l5;
      l4 = l5 >>> 32;
    } 
  }
  
  public BigInteger(String paramString) { this(paramString, 10); }
  
  public BigInteger(int paramInt, Random paramRandom) { this(1, randomBits(paramInt, paramRandom)); }
  
  private static byte[] randomBits(int paramInt, Random paramRandom) {
    if (paramInt < 0)
      throw new IllegalArgumentException("numBits must be non-negative"); 
    int i = (int)((paramInt + 7L) / 8L);
    byte[] arrayOfByte = new byte[i];
    if (i > 0) {
      paramRandom.nextBytes(arrayOfByte);
      int j = 8 * i - paramInt;
      arrayOfByte[0] = (byte)(arrayOfByte[0] & (1 << 8 - j) - 1);
    } 
    return arrayOfByte;
  }
  
  public BigInteger(int paramInt1, int paramInt2, Random paramRandom) {
    if (paramInt1 < 2)
      throw new ArithmeticException("bitLength < 2"); 
    BigInteger bigInteger = (paramInt1 < 95) ? smallPrime(paramInt1, paramInt2, paramRandom) : largePrime(paramInt1, paramInt2, paramRandom);
    this.signum = 1;
    this.mag = bigInteger.mag;
  }
  
  public static BigInteger probablePrime(int paramInt, Random paramRandom) {
    if (paramInt < 2)
      throw new ArithmeticException("bitLength < 2"); 
    return (paramInt < 95) ? smallPrime(paramInt, 100, paramRandom) : largePrime(paramInt, 100, paramRandom);
  }
  
  private static BigInteger smallPrime(int paramInt1, int paramInt2, Random paramRandom) {
    BigInteger bigInteger;
    int i = paramInt1 + 31 >>> 5;
    int[] arrayOfInt = new int[i];
    int j = 1 << (paramInt1 + 31 & 0x1F);
    int k = (j << 1) - 1;
    while (true) {
      for (byte b = 0; b < i; b++)
        arrayOfInt[b] = paramRandom.nextInt(); 
      arrayOfInt[0] = arrayOfInt[0] & k | j;
      if (paramInt1 > 2)
        arrayOfInt[i - 1] = arrayOfInt[i - 1] | true; 
      bigInteger = new BigInteger(arrayOfInt, 1);
      if (paramInt1 > 6) {
        long l = bigInteger.remainder(SMALL_PRIME_PRODUCT).longValue();
        if (l % 3L == 0L || l % 5L == 0L || l % 7L == 0L || l % 11L == 0L || l % 13L == 0L || l % 17L == 0L || l % 19L == 0L || l % 23L == 0L || l % 29L == 0L || l % 31L == 0L || l % 37L == 0L || l % 41L == 0L)
          continue; 
      } 
      if (paramInt1 < 4)
        return bigInteger; 
      if (bigInteger.primeToCertainty(paramInt2, paramRandom))
        break; 
    } 
    return bigInteger;
  }
  
  private static BigInteger largePrime(int paramInt1, int paramInt2, Random paramRandom) {
    BigInteger bigInteger1 = (new BigInteger(paramInt1, paramRandom)).setBit(paramInt1 - 1);
    bigInteger1.mag[bigInteger1.mag.length - 1] = bigInteger1.mag[bigInteger1.mag.length - 1] & 0xFFFFFFFE;
    int i = getPrimeSearchLen(paramInt1);
    BitSieve bitSieve = new BitSieve(bigInteger1, i);
    BigInteger bigInteger2;
    for (bigInteger2 = bitSieve.retrieve(bigInteger1, paramInt2, paramRandom); bigInteger2 == null || bigInteger2.bitLength() != paramInt1; bigInteger2 = bitSieve.retrieve(bigInteger1, paramInt2, paramRandom)) {
      bigInteger1 = bigInteger1.add(valueOf((2 * i)));
      if (bigInteger1.bitLength() != paramInt1)
        bigInteger1 = (new BigInteger(paramInt1, paramRandom)).setBit(paramInt1 - 1); 
      bigInteger1.mag[bigInteger1.mag.length - 1] = bigInteger1.mag[bigInteger1.mag.length - 1] & 0xFFFFFFFE;
      bitSieve = new BitSieve(bigInteger1, i);
    } 
    return bigInteger2;
  }
  
  public BigInteger nextProbablePrime() {
    if (this.signum < 0)
      throw new ArithmeticException("start < 0: " + this); 
    if (this.signum == 0 || equals(ONE))
      return TWO; 
    BigInteger bigInteger = add(ONE);
    if (bigInteger.bitLength() < 95) {
      if (!bigInteger.testBit(0))
        bigInteger = bigInteger.add(ONE); 
      while (true) {
        if (bigInteger.bitLength() > 6) {
          long l = bigInteger.remainder(SMALL_PRIME_PRODUCT).longValue();
          if (l % 3L == 0L || l % 5L == 0L || l % 7L == 0L || l % 11L == 0L || l % 13L == 0L || l % 17L == 0L || l % 19L == 0L || l % 23L == 0L || l % 29L == 0L || l % 31L == 0L || l % 37L == 0L || l % 41L == 0L) {
            bigInteger = bigInteger.add(TWO);
            continue;
          } 
        } 
        if (bigInteger.bitLength() < 4)
          return bigInteger; 
        if (bigInteger.primeToCertainty(100, null))
          return bigInteger; 
        bigInteger = bigInteger.add(TWO);
      } 
    } 
    if (bigInteger.testBit(0))
      bigInteger = bigInteger.subtract(ONE); 
    int i = getPrimeSearchLen(bigInteger.bitLength());
    while (true) {
      BitSieve bitSieve = new BitSieve(bigInteger, i);
      BigInteger bigInteger1 = bitSieve.retrieve(bigInteger, 100, null);
      if (bigInteger1 != null)
        return bigInteger1; 
      bigInteger = bigInteger.add(valueOf((2 * i)));
    } 
  }
  
  private static int getPrimeSearchLen(int paramInt) {
    if (paramInt > 500000001)
      throw new ArithmeticException("Prime search implementation restriction on bitLength"); 
    return paramInt / 20 * 64;
  }
  
  boolean primeToCertainty(int paramInt, Random paramRandom) {
    int i = 0;
    int j = (Math.min(paramInt, 2147483646) + 1) / 2;
    int k = bitLength();
    if (k < 100) {
      i = 50;
      i = (j < i) ? j : i;
      return passesMillerRabin(i, paramRandom);
    } 
    if (k < 256) {
      i = 27;
    } else if (k < 512) {
      i = 15;
    } else if (k < 768) {
      i = 8;
    } else if (k < 1024) {
      i = 4;
    } else {
      i = 2;
    } 
    i = (j < i) ? j : i;
    return (passesMillerRabin(i, paramRandom) && passesLucasLehmer());
  }
  
  private boolean passesLucasLehmer() {
    BigInteger bigInteger1 = add(ONE);
    int i;
    for (i = 5; jacobiSymbol(i, this) != -1; i = (i < 0) ? (Math.abs(i) + 2) : -(i + 2));
    BigInteger bigInteger2 = lucasLehmerSequence(i, bigInteger1, this);
    return bigInteger2.mod(this).equals(ZERO);
  }
  
  private static int jacobiSymbol(int paramInt, BigInteger paramBigInteger) {
    if (paramInt == 0)
      return 0; 
    byte b = 1;
    int i = paramBigInteger.mag[paramBigInteger.mag.length - 1];
    if (paramInt < 0) {
      paramInt = -paramInt;
      int j = i & 0x7;
      if (j == 3 || j == 7)
        b = -b; 
    } 
    while ((paramInt & 0x3) == 0)
      paramInt >>= 2; 
    if ((paramInt & true) == 0) {
      paramInt >>= 1;
      if (((i ^ i >> 1) & 0x2) != 0)
        b = -b; 
    } 
    if (paramInt == 1)
      return b; 
    if ((paramInt & i & 0x2) != 0)
      b = -b; 
    for (i = paramBigInteger.mod(valueOf(paramInt)).intValue(); i != 0; i %= paramInt) {
      while ((i & 0x3) == 0)
        i >>= 2; 
      if ((i & true) == 0) {
        i >>= 1;
        if (((paramInt ^ paramInt >> 1) & 0x2) != 0)
          b = -b; 
      } 
      if (i == 1)
        return b; 
      assert i < paramInt;
      int j = i;
      i = paramInt;
      paramInt = j;
      if ((i & paramInt & 0x2) != 0)
        b = -b; 
    } 
    return 0;
  }
  
  private static BigInteger lucasLehmerSequence(int paramInt, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    BigInteger bigInteger1 = valueOf(paramInt);
    BigInteger bigInteger2 = ONE;
    BigInteger bigInteger3 = ONE;
    for (int i = paramBigInteger1.bitLength() - 2; i >= 0; i--) {
      BigInteger bigInteger4 = bigInteger2.multiply(bigInteger3).mod(paramBigInteger2);
      BigInteger bigInteger5 = bigInteger3.square().add(bigInteger1.multiply(bigInteger2.square())).mod(paramBigInteger2);
      if (bigInteger5.testBit(0))
        bigInteger5 = bigInteger5.subtract(paramBigInteger2); 
      bigInteger5 = bigInteger5.shiftRight(1);
      bigInteger2 = bigInteger4;
      bigInteger3 = bigInteger5;
      if (paramBigInteger1.testBit(i)) {
        bigInteger4 = bigInteger2.add(bigInteger3).mod(paramBigInteger2);
        if (bigInteger4.testBit(0))
          bigInteger4 = bigInteger4.subtract(paramBigInteger2); 
        bigInteger4 = bigInteger4.shiftRight(1);
        bigInteger5 = bigInteger3.add(bigInteger1.multiply(bigInteger2)).mod(paramBigInteger2);
        if (bigInteger5.testBit(0))
          bigInteger5 = bigInteger5.subtract(paramBigInteger2); 
        bigInteger5 = bigInteger5.shiftRight(1);
        bigInteger2 = bigInteger4;
        bigInteger3 = bigInteger5;
      } 
    } 
    return bigInteger2;
  }
  
  private boolean passesMillerRabin(int paramInt, Random paramRandom) {
    BigInteger bigInteger1 = subtract(ONE);
    BigInteger bigInteger2 = bigInteger1;
    int i = bigInteger2.getLowestSetBit();
    bigInteger2 = bigInteger2.shiftRight(i);
    if (paramRandom == null)
      paramRandom = ThreadLocalRandom.current(); 
    for (byte b = 0; b < paramInt; b++) {
      BigInteger bigInteger3;
      do {
        bigInteger3 = new BigInteger(bitLength(), paramRandom);
      } while (bigInteger3.compareTo(ONE) <= 0 || bigInteger3.compareTo(this) >= 0);
      byte b1 = 0;
      for (BigInteger bigInteger4 = bigInteger3.modPow(bigInteger2, this); (b1 || !bigInteger4.equals(ONE)) && !bigInteger4.equals(bigInteger1); bigInteger4 = bigInteger4.modPow(TWO, this)) {
        if ((b1 && bigInteger4.equals(ONE)) || ++b1 == i)
          return false; 
      } 
    } 
    return true;
  }
  
  BigInteger(int[] paramArrayOfInt, int paramInt) {
    this.signum = (paramArrayOfInt.length == 0) ? 0 : paramInt;
    this.mag = paramArrayOfInt;
    if (this.mag.length >= 67108864)
      checkRange(); 
  }
  
  private BigInteger(byte[] paramArrayOfByte, int paramInt) {
    this.signum = (paramArrayOfByte.length == 0) ? 0 : paramInt;
    this.mag = stripLeadingZeroBytes(paramArrayOfByte);
    if (this.mag.length >= 67108864)
      checkRange(); 
  }
  
  private void checkRange() {
    if (this.mag.length > 67108864 || (this.mag.length == 67108864 && this.mag[0] < 0))
      reportOverflow(); 
  }
  
  private static void reportOverflow() { throw new ArithmeticException("BigInteger would overflow supported range"); }
  
  public static BigInteger valueOf(long paramLong) { return (paramLong == 0L) ? ZERO : ((paramLong > 0L && paramLong <= 16L) ? posConst[(int)paramLong] : ((paramLong < 0L && paramLong >= -16L) ? negConst[(int)-paramLong] : new BigInteger(paramLong))); }
  
  private BigInteger(long paramLong) {
    if (paramLong < 0L) {
      paramLong = -paramLong;
      this.signum = -1;
    } else {
      this.signum = 1;
    } 
    int i = (int)(paramLong >>> 32);
    if (i == 0) {
      this.mag = new int[1];
      this.mag[0] = (int)paramLong;
    } else {
      this.mag = new int[2];
      this.mag[0] = i;
      this.mag[1] = (int)paramLong;
    } 
  }
  
  private static BigInteger valueOf(int[] paramArrayOfInt) { return (paramArrayOfInt[0] > 0) ? new BigInteger(paramArrayOfInt, 1) : new BigInteger(paramArrayOfInt); }
  
  public BigInteger add(BigInteger paramBigInteger) {
    if (paramBigInteger.signum == 0)
      return this; 
    if (this.signum == 0)
      return paramBigInteger; 
    if (paramBigInteger.signum == this.signum)
      return new BigInteger(add(this.mag, paramBigInteger.mag), this.signum); 
    int i = compareMagnitude(paramBigInteger);
    if (i == 0)
      return ZERO; 
    int[] arrayOfInt = (i > 0) ? subtract(this.mag, paramBigInteger.mag) : subtract(paramBigInteger.mag, this.mag);
    arrayOfInt = trustedStripLeadingZeroInts(arrayOfInt);
    return new BigInteger(arrayOfInt, (i == this.signum) ? 1 : -1);
  }
  
  BigInteger add(long paramLong) {
    if (paramLong == 0L)
      return this; 
    if (this.signum == 0)
      return valueOf(paramLong); 
    if (Long.signum(paramLong) == this.signum)
      return new BigInteger(add(this.mag, Math.abs(paramLong)), this.signum); 
    int i = compareMagnitude(paramLong);
    if (i == 0)
      return ZERO; 
    int[] arrayOfInt = (i > 0) ? subtract(this.mag, Math.abs(paramLong)) : subtract(Math.abs(paramLong), this.mag);
    arrayOfInt = trustedStripLeadingZeroInts(arrayOfInt);
    return new BigInteger(arrayOfInt, (i == this.signum) ? 1 : -1);
  }
  
  private static int[] add(int[] paramArrayOfInt, long paramLong) {
    int[] arrayOfInt;
    long l = 0L;
    int i = paramArrayOfInt.length;
    int j = (int)(paramLong >>> 32);
    if (j == 0) {
      arrayOfInt = new int[i];
      l = (paramArrayOfInt[--i] & 0xFFFFFFFFL) + paramLong;
      arrayOfInt[i] = (int)l;
    } else {
      if (i == 1) {
        int[] arrayOfInt1 = new int[2];
        l = paramLong + (paramArrayOfInt[0] & 0xFFFFFFFFL);
        arrayOfInt1[1] = (int)l;
        arrayOfInt1[0] = (int)(l >>> 32);
        return arrayOfInt1;
      } 
      arrayOfInt = new int[i];
      l = (paramArrayOfInt[--i] & 0xFFFFFFFFL) + (paramLong & 0xFFFFFFFFL);
      arrayOfInt[i] = (int)l;
      l = (paramArrayOfInt[--i] & 0xFFFFFFFFL) + (j & 0xFFFFFFFFL) + (l >>> 32);
      arrayOfInt[i] = (int)l;
    } 
    boolean bool;
    for (bool = (l >>> 32 != 0L) ? 1 : 0; i > 0 && bool; bool = (paramArrayOfInt[i] + 1 == 0) ? 1 : 0)
      arrayOfInt[--i] = paramArrayOfInt[i] + 1; 
    while (i > 0)
      arrayOfInt[--i] = paramArrayOfInt[i]; 
    if (bool) {
      int[] arrayOfInt1 = new int[arrayOfInt.length + 1];
      System.arraycopy(arrayOfInt, 0, arrayOfInt1, 1, arrayOfInt.length);
      arrayOfInt1[0] = 1;
      return arrayOfInt1;
    } 
    return arrayOfInt;
  }
  
  private static int[] add(int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    if (paramArrayOfInt1.length < paramArrayOfInt2.length) {
      int[] arrayOfInt1 = paramArrayOfInt1;
      paramArrayOfInt1 = paramArrayOfInt2;
      paramArrayOfInt2 = arrayOfInt1;
    } 
    int i = paramArrayOfInt1.length;
    int j = paramArrayOfInt2.length;
    int[] arrayOfInt = new int[i];
    long l = 0L;
    if (j == 1) {
      l = (paramArrayOfInt1[--i] & 0xFFFFFFFFL) + (paramArrayOfInt2[0] & 0xFFFFFFFFL);
      arrayOfInt[i] = (int)l;
    } else {
      while (j > 0) {
        l = (paramArrayOfInt1[--i] & 0xFFFFFFFFL) + (paramArrayOfInt2[--j] & 0xFFFFFFFFL) + (l >>> 32);
        arrayOfInt[i] = (int)l;
      } 
    } 
    boolean bool;
    for (bool = (l >>> 32 != 0L) ? 1 : 0; i > 0 && bool; bool = (paramArrayOfInt1[i] + 1 == 0) ? 1 : 0)
      arrayOfInt[--i] = paramArrayOfInt1[i] + 1; 
    while (i > 0)
      arrayOfInt[--i] = paramArrayOfInt1[i]; 
    if (bool) {
      int[] arrayOfInt1 = new int[arrayOfInt.length + 1];
      System.arraycopy(arrayOfInt, 0, arrayOfInt1, 1, arrayOfInt.length);
      arrayOfInt1[0] = 1;
      return arrayOfInt1;
    } 
    return arrayOfInt;
  }
  
  private static int[] subtract(long paramLong, int[] paramArrayOfInt) {
    int i = (int)(paramLong >>> 32);
    if (i == 0) {
      int[] arrayOfInt1 = new int[1];
      arrayOfInt1[0] = (int)(paramLong - (paramArrayOfInt[0] & 0xFFFFFFFFL));
      return arrayOfInt1;
    } 
    int[] arrayOfInt = new int[2];
    if (paramArrayOfInt.length == 1) {
      long l1 = ((int)paramLong & 0xFFFFFFFFL) - (paramArrayOfInt[0] & 0xFFFFFFFFL);
      arrayOfInt[1] = (int)l1;
      boolean bool = (l1 >> 32 != 0L) ? 1 : 0;
      if (bool) {
        arrayOfInt[0] = i - 1;
      } else {
        arrayOfInt[0] = i;
      } 
      return arrayOfInt;
    } 
    long l = ((int)paramLong & 0xFFFFFFFFL) - (paramArrayOfInt[1] & 0xFFFFFFFFL);
    arrayOfInt[1] = (int)l;
    l = (i & 0xFFFFFFFFL) - (paramArrayOfInt[0] & 0xFFFFFFFFL) + (l >> 32);
    arrayOfInt[0] = (int)l;
    return arrayOfInt;
  }
  
  private static int[] subtract(int[] paramArrayOfInt, long paramLong) {
    int i = (int)(paramLong >>> 32);
    int j = paramArrayOfInt.length;
    int[] arrayOfInt = new int[j];
    long l = 0L;
    if (i == 0) {
      l = (paramArrayOfInt[--j] & 0xFFFFFFFFL) - paramLong;
      arrayOfInt[j] = (int)l;
    } else {
      l = (paramArrayOfInt[--j] & 0xFFFFFFFFL) - (paramLong & 0xFFFFFFFFL);
      arrayOfInt[j] = (int)l;
      l = (paramArrayOfInt[--j] & 0xFFFFFFFFL) - (i & 0xFFFFFFFFL) + (l >> 32);
      arrayOfInt[j] = (int)l;
    } 
    boolean bool;
    for (bool = (l >> 32 != 0L) ? 1 : 0; j > 0 && bool; bool = (paramArrayOfInt[j] - 1 == -1) ? 1 : 0)
      arrayOfInt[--j] = paramArrayOfInt[j] - 1; 
    while (j > 0)
      arrayOfInt[--j] = paramArrayOfInt[j]; 
    return arrayOfInt;
  }
  
  public BigInteger subtract(BigInteger paramBigInteger) {
    if (paramBigInteger.signum == 0)
      return this; 
    if (this.signum == 0)
      return paramBigInteger.negate(); 
    if (paramBigInteger.signum != this.signum)
      return new BigInteger(add(this.mag, paramBigInteger.mag), this.signum); 
    int i = compareMagnitude(paramBigInteger);
    if (i == 0)
      return ZERO; 
    int[] arrayOfInt = (i > 0) ? subtract(this.mag, paramBigInteger.mag) : subtract(paramBigInteger.mag, this.mag);
    arrayOfInt = trustedStripLeadingZeroInts(arrayOfInt);
    return new BigInteger(arrayOfInt, (i == this.signum) ? 1 : -1);
  }
  
  private static int[] subtract(int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    int i = paramArrayOfInt1.length;
    int[] arrayOfInt = new int[i];
    int j = paramArrayOfInt2.length;
    long l = 0L;
    while (j > 0) {
      l = (paramArrayOfInt1[--i] & 0xFFFFFFFFL) - (paramArrayOfInt2[--j] & 0xFFFFFFFFL) + (l >> 32);
      arrayOfInt[i] = (int)l;
    } 
    boolean bool;
    for (bool = (l >> 32 != 0L) ? 1 : 0; i > 0 && bool; bool = (paramArrayOfInt1[i] - 1 == -1) ? 1 : 0)
      arrayOfInt[--i] = paramArrayOfInt1[i] - 1; 
    while (i > 0)
      arrayOfInt[--i] = paramArrayOfInt1[i]; 
    return arrayOfInt;
  }
  
  public BigInteger multiply(BigInteger paramBigInteger) {
    if (paramBigInteger.signum == 0 || this.signum == 0)
      return ZERO; 
    int i = this.mag.length;
    if (paramBigInteger == this && i > 20)
      return square(); 
    int j = paramBigInteger.mag.length;
    if (i < 80 || j < 80) {
      byte b = (this.signum == paramBigInteger.signum) ? 1 : -1;
      if (paramBigInteger.mag.length == 1)
        return multiplyByInt(this.mag, paramBigInteger.mag[0], b); 
      if (this.mag.length == 1)
        return multiplyByInt(paramBigInteger.mag, this.mag[0], b); 
      int[] arrayOfInt = multiplyToLen(this.mag, i, paramBigInteger.mag, j, null);
      arrayOfInt = trustedStripLeadingZeroInts(arrayOfInt);
      return new BigInteger(arrayOfInt, b);
    } 
    return (i < 240 && j < 240) ? multiplyKaratsuba(this, paramBigInteger) : multiplyToomCook3(this, paramBigInteger);
  }
  
  private static BigInteger multiplyByInt(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    if (Integer.bitCount(paramInt1) == 1)
      return new BigInteger(shiftLeft(paramArrayOfInt, Integer.numberOfTrailingZeros(paramInt1)), paramInt2); 
    int i = paramArrayOfInt.length;
    int[] arrayOfInt = new int[i + 1];
    long l1 = 0L;
    long l2 = paramInt1 & 0xFFFFFFFFL;
    int j = arrayOfInt.length - 1;
    for (int k = i - 1; k >= 0; k--) {
      long l = (paramArrayOfInt[k] & 0xFFFFFFFFL) * l2 + l1;
      arrayOfInt[j--] = (int)l;
      l1 = l >>> 32;
    } 
    if (l1 == 0L) {
      arrayOfInt = Arrays.copyOfRange(arrayOfInt, 1, arrayOfInt.length);
    } else {
      arrayOfInt[j] = (int)l1;
    } 
    return new BigInteger(arrayOfInt, paramInt2);
  }
  
  BigInteger multiply(long paramLong) {
    if (paramLong == 0L || this.signum == 0)
      return ZERO; 
    if (paramLong == Float.MIN_VALUE)
      return multiply(valueOf(paramLong)); 
    int i = (paramLong > 0L) ? this.signum : -this.signum;
    if (paramLong < 0L)
      paramLong = -paramLong; 
    long l1 = paramLong >>> 32;
    long l2 = paramLong & 0xFFFFFFFFL;
    int j = this.mag.length;
    int[] arrayOfInt1 = this.mag;
    int[] arrayOfInt2 = (l1 == 0L) ? new int[j + 1] : new int[j + 2];
    long l3 = 0L;
    int k = arrayOfInt2.length - 1;
    int m;
    for (m = j - 1; m >= 0; m--) {
      long l = (arrayOfInt1[m] & 0xFFFFFFFFL) * l2 + l3;
      arrayOfInt2[k--] = (int)l;
      l3 = l >>> 32;
    } 
    arrayOfInt2[k] = (int)l3;
    if (l1 != 0L) {
      l3 = 0L;
      k = arrayOfInt2.length - 2;
      for (m = j - 1; m >= 0; m--) {
        long l = (arrayOfInt1[m] & 0xFFFFFFFFL) * l1 + (arrayOfInt2[k] & 0xFFFFFFFFL) + l3;
        arrayOfInt2[k--] = (int)l;
        l3 = l >>> 32;
      } 
      arrayOfInt2[0] = (int)l3;
    } 
    if (l3 == 0L)
      arrayOfInt2 = Arrays.copyOfRange(arrayOfInt2, 1, arrayOfInt2.length); 
    return new BigInteger(arrayOfInt2, i);
  }
  
  private static int[] multiplyToLen(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2, int[] paramArrayOfInt3) {
    int i = paramInt1 - 1;
    int j = paramInt2 - 1;
    if (paramArrayOfInt3 == null || paramArrayOfInt3.length < paramInt1 + paramInt2)
      paramArrayOfInt3 = new int[paramInt1 + paramInt2]; 
    long l = 0L;
    int k = j;
    int m;
    for (m = j + 1 + i; k >= 0; m--) {
      long l1 = (paramArrayOfInt2[k] & 0xFFFFFFFFL) * (paramArrayOfInt1[i] & 0xFFFFFFFFL) + l;
      paramArrayOfInt3[m] = (int)l1;
      l = l1 >>> 32;
      k--;
    } 
    paramArrayOfInt3[i] = (int)l;
    for (k = i - 1; k >= 0; k--) {
      l = 0L;
      m = j;
      for (int n = j + 1 + k; m >= 0; n--) {
        long l1 = (paramArrayOfInt2[m] & 0xFFFFFFFFL) * (paramArrayOfInt1[k] & 0xFFFFFFFFL) + (paramArrayOfInt3[n] & 0xFFFFFFFFL) + l;
        paramArrayOfInt3[n] = (int)l1;
        l = l1 >>> 32;
        m--;
      } 
      paramArrayOfInt3[k] = (int)l;
    } 
    return paramArrayOfInt3;
  }
  
  private static BigInteger multiplyKaratsuba(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    int i = paramBigInteger1.mag.length;
    int j = paramBigInteger2.mag.length;
    int k = (Math.max(i, j) + 1) / 2;
    BigInteger bigInteger1 = paramBigInteger1.getLower(k);
    BigInteger bigInteger2 = paramBigInteger1.getUpper(k);
    BigInteger bigInteger3 = paramBigInteger2.getLower(k);
    BigInteger bigInteger4 = paramBigInteger2.getUpper(k);
    BigInteger bigInteger5 = bigInteger2.multiply(bigInteger4);
    BigInteger bigInteger6 = bigInteger1.multiply(bigInteger3);
    BigInteger bigInteger7 = bigInteger2.add(bigInteger1).multiply(bigInteger4.add(bigInteger3));
    BigInteger bigInteger8 = bigInteger5.shiftLeft(32 * k).add(bigInteger7.subtract(bigInteger5).subtract(bigInteger6)).shiftLeft(32 * k).add(bigInteger6);
    return (paramBigInteger1.signum != paramBigInteger2.signum) ? bigInteger8.negate() : bigInteger8;
  }
  
  private static BigInteger multiplyToomCook3(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    int i = paramBigInteger1.mag.length;
    int j = paramBigInteger2.mag.length;
    int k = Math.max(i, j);
    int m = (k + 2) / 3;
    int n = k - 2 * m;
    BigInteger bigInteger3 = paramBigInteger1.getToomSlice(m, n, 0, k);
    BigInteger bigInteger2 = paramBigInteger1.getToomSlice(m, n, 1, k);
    BigInteger bigInteger1 = paramBigInteger1.getToomSlice(m, n, 2, k);
    BigInteger bigInteger6 = paramBigInteger2.getToomSlice(m, n, 0, k);
    BigInteger bigInteger5 = paramBigInteger2.getToomSlice(m, n, 1, k);
    BigInteger bigInteger4 = paramBigInteger2.getToomSlice(m, n, 2, k);
    BigInteger bigInteger7 = bigInteger1.multiply(bigInteger4);
    BigInteger bigInteger15 = bigInteger3.add(bigInteger1);
    BigInteger bigInteger16 = bigInteger6.add(bigInteger4);
    BigInteger bigInteger10 = bigInteger15.subtract(bigInteger2).multiply(bigInteger16.subtract(bigInteger5));
    bigInteger15 = bigInteger15.add(bigInteger2);
    bigInteger16 = bigInteger16.add(bigInteger5);
    BigInteger bigInteger8 = bigInteger15.multiply(bigInteger16);
    BigInteger bigInteger9 = bigInteger15.add(bigInteger3).shiftLeft(1).subtract(bigInteger1).multiply(bigInteger16.add(bigInteger6).shiftLeft(1).subtract(bigInteger4));
    BigInteger bigInteger11 = bigInteger3.multiply(bigInteger6);
    BigInteger bigInteger13 = bigInteger9.subtract(bigInteger10).exactDivideBy3();
    BigInteger bigInteger14 = bigInteger8.subtract(bigInteger10).shiftRight(1);
    BigInteger bigInteger12 = bigInteger8.subtract(bigInteger7);
    bigInteger13 = bigInteger13.subtract(bigInteger12).shiftRight(1);
    bigInteger12 = bigInteger12.subtract(bigInteger14).subtract(bigInteger11);
    bigInteger13 = bigInteger13.subtract(bigInteger11.shiftLeft(1));
    bigInteger14 = bigInteger14.subtract(bigInteger13);
    int i1 = m * 32;
    BigInteger bigInteger17 = bigInteger11.shiftLeft(i1).add(bigInteger13).shiftLeft(i1).add(bigInteger12).shiftLeft(i1).add(bigInteger14).shiftLeft(i1).add(bigInteger7);
    return (paramBigInteger1.signum != paramBigInteger2.signum) ? bigInteger17.negate() : bigInteger17;
  }
  
  private BigInteger getToomSlice(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int j;
    int i;
    int m = this.mag.length;
    int n = paramInt4 - m;
    if (paramInt3 == 0) {
      i = 0 - n;
      j = paramInt2 - 1 - n;
    } else {
      i = paramInt2 + (paramInt3 - 1) * paramInt1 - n;
      j = i + paramInt1 - 1;
    } 
    if (i < 0)
      i = 0; 
    if (j < 0)
      return ZERO; 
    int k = j - i + 1;
    if (k <= 0)
      return ZERO; 
    if (i == 0 && k >= m)
      return abs(); 
    int[] arrayOfInt = new int[k];
    System.arraycopy(this.mag, i, arrayOfInt, 0, k);
    return new BigInteger(trustedStripLeadingZeroInts(arrayOfInt), 1);
  }
  
  private BigInteger exactDivideBy3() {
    int i = this.mag.length;
    int[] arrayOfInt = new int[i];
    long l = 0L;
    for (int j = i - 1; j >= 0; j--) {
      long l1 = this.mag[j] & 0xFFFFFFFFL;
      long l2 = l1 - l;
      if (l > l1) {
        l = 1L;
      } else {
        l = 0L;
      } 
      long l3 = l2 * 2863311531L & 0xFFFFFFFFL;
      arrayOfInt[j] = (int)l3;
      if (l3 >= 1431655766L) {
        l++;
        if (l3 >= 2863311531L)
          l++; 
      } 
    } 
    arrayOfInt = trustedStripLeadingZeroInts(arrayOfInt);
    return new BigInteger(arrayOfInt, this.signum);
  }
  
  private BigInteger getLower(int paramInt) {
    int i = this.mag.length;
    if (i <= paramInt)
      return abs(); 
    int[] arrayOfInt = new int[paramInt];
    System.arraycopy(this.mag, i - paramInt, arrayOfInt, 0, paramInt);
    return new BigInteger(trustedStripLeadingZeroInts(arrayOfInt), 1);
  }
  
  private BigInteger getUpper(int paramInt) {
    int i = this.mag.length;
    if (i <= paramInt)
      return ZERO; 
    int j = i - paramInt;
    int[] arrayOfInt = new int[j];
    System.arraycopy(this.mag, 0, arrayOfInt, 0, j);
    return new BigInteger(trustedStripLeadingZeroInts(arrayOfInt), 1);
  }
  
  private BigInteger square() {
    if (this.signum == 0)
      return ZERO; 
    int i = this.mag.length;
    if (i < 128) {
      int[] arrayOfInt = squareToLen(this.mag, i, null);
      return new BigInteger(trustedStripLeadingZeroInts(arrayOfInt), 1);
    } 
    return (i < 216) ? squareKaratsuba() : squareToomCook3();
  }
  
  private static final int[] squareToLen(int[] paramArrayOfInt1, int paramInt, int[] paramArrayOfInt2) {
    int i = paramInt << 1;
    if (paramArrayOfInt2 == null || paramArrayOfInt2.length < i)
      paramArrayOfInt2 = new int[i]; 
    implSquareToLenChecks(paramArrayOfInt1, paramInt, paramArrayOfInt2, i);
    return implSquareToLen(paramArrayOfInt1, paramInt, paramArrayOfInt2, i);
  }
  
  private static void implSquareToLenChecks(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2) throws RuntimeException {
    if (paramInt1 < 1)
      throw new IllegalArgumentException("invalid input length: " + paramInt1); 
    if (paramInt1 > paramArrayOfInt1.length)
      throw new IllegalArgumentException("input length out of bound: " + paramInt1 + " > " + paramArrayOfInt1.length); 
    if (paramInt1 * 2 > paramArrayOfInt2.length)
      throw new IllegalArgumentException("input length out of bound: " + (paramInt1 * 2) + " > " + paramArrayOfInt2.length); 
    if (paramInt2 < 1)
      throw new IllegalArgumentException("invalid input length: " + paramInt2); 
    if (paramInt2 > paramArrayOfInt2.length)
      throw new IllegalArgumentException("input length out of bound: " + paramInt1 + " > " + paramArrayOfInt2.length); 
  }
  
  private static final int[] implSquareToLen(int[] paramArrayOfInt1, int paramInt1, int[] paramArrayOfInt2, int paramInt2) {
    int i = 0;
    int j = 0;
    byte b = 0;
    while (j < paramInt1) {
      long l1 = paramArrayOfInt1[j] & 0xFFFFFFFFL;
      long l2 = l1 * l1;
      paramArrayOfInt2[b++] = i << 31 | (int)(l2 >>> 33);
      paramArrayOfInt2[b++] = (int)(l2 >>> true);
      i = (int)l2;
      j++;
    } 
    j = paramInt1;
    for (b = 1; j > 0; b += 2) {
      int k = paramArrayOfInt1[j - 1];
      k = mulAdd(paramArrayOfInt2, paramArrayOfInt1, b, j - 1, k);
      addOne(paramArrayOfInt2, b - 1, j, k);
      j--;
    } 
    primitiveLeftShift(paramArrayOfInt2, paramInt2, 1);
    paramArrayOfInt2[paramInt2 - 1] = paramArrayOfInt2[paramInt2 - 1] | paramArrayOfInt1[paramInt1 - 1] & true;
    return paramArrayOfInt2;
  }
  
  private BigInteger squareKaratsuba() {
    int i = (this.mag.length + 1) / 2;
    BigInteger bigInteger1 = getLower(i);
    BigInteger bigInteger2 = getUpper(i);
    BigInteger bigInteger3 = bigInteger2.square();
    BigInteger bigInteger4 = bigInteger1.square();
    return bigInteger3.shiftLeft(i * 32).add(bigInteger1.add(bigInteger2).square().subtract(bigInteger3.add(bigInteger4))).shiftLeft(i * 32).add(bigInteger4);
  }
  
  private BigInteger squareToomCook3() {
    int i = this.mag.length;
    int j = (i + 2) / 3;
    int k = i - 2 * j;
    BigInteger bigInteger3 = getToomSlice(j, k, 0, i);
    BigInteger bigInteger2 = getToomSlice(j, k, 1, i);
    BigInteger bigInteger1 = getToomSlice(j, k, 2, i);
    BigInteger bigInteger4 = bigInteger1.square();
    BigInteger bigInteger12 = bigInteger3.add(bigInteger1);
    BigInteger bigInteger7 = bigInteger12.subtract(bigInteger2).square();
    bigInteger12 = bigInteger12.add(bigInteger2);
    BigInteger bigInteger5 = bigInteger12.square();
    BigInteger bigInteger8 = bigInteger3.square();
    BigInteger bigInteger6 = bigInteger12.add(bigInteger3).shiftLeft(1).subtract(bigInteger1).square();
    BigInteger bigInteger10 = bigInteger6.subtract(bigInteger7).exactDivideBy3();
    BigInteger bigInteger11 = bigInteger5.subtract(bigInteger7).shiftRight(1);
    BigInteger bigInteger9 = bigInteger5.subtract(bigInteger4);
    bigInteger10 = bigInteger10.subtract(bigInteger9).shiftRight(1);
    bigInteger9 = bigInteger9.subtract(bigInteger11).subtract(bigInteger8);
    bigInteger10 = bigInteger10.subtract(bigInteger8.shiftLeft(1));
    bigInteger11 = bigInteger11.subtract(bigInteger10);
    int m = j * 32;
    return bigInteger8.shiftLeft(m).add(bigInteger10).shiftLeft(m).add(bigInteger9).shiftLeft(m).add(bigInteger11).shiftLeft(m).add(bigInteger4);
  }
  
  public BigInteger divide(BigInteger paramBigInteger) { return (paramBigInteger.mag.length < 80 || this.mag.length - paramBigInteger.mag.length < 40) ? divideKnuth(paramBigInteger) : divideBurnikelZiegler(paramBigInteger); }
  
  private BigInteger divideKnuth(BigInteger paramBigInteger) {
    MutableBigInteger mutableBigInteger1 = new MutableBigInteger();
    MutableBigInteger mutableBigInteger2 = new MutableBigInteger(this.mag);
    MutableBigInteger mutableBigInteger3 = new MutableBigInteger(paramBigInteger.mag);
    mutableBigInteger2.divideKnuth(mutableBigInteger3, mutableBigInteger1, false);
    return mutableBigInteger1.toBigInteger(this.signum * paramBigInteger.signum);
  }
  
  public BigInteger[] divideAndRemainder(BigInteger paramBigInteger) { return (paramBigInteger.mag.length < 80 || this.mag.length - paramBigInteger.mag.length < 40) ? divideAndRemainderKnuth(paramBigInteger) : divideAndRemainderBurnikelZiegler(paramBigInteger); }
  
  private BigInteger[] divideAndRemainderKnuth(BigInteger paramBigInteger) {
    BigInteger[] arrayOfBigInteger = new BigInteger[2];
    MutableBigInteger mutableBigInteger1 = new MutableBigInteger();
    MutableBigInteger mutableBigInteger2 = new MutableBigInteger(this.mag);
    MutableBigInteger mutableBigInteger3 = new MutableBigInteger(paramBigInteger.mag);
    MutableBigInteger mutableBigInteger4 = mutableBigInteger2.divideKnuth(mutableBigInteger3, mutableBigInteger1);
    arrayOfBigInteger[0] = mutableBigInteger1.toBigInteger((this.signum == paramBigInteger.signum) ? 1 : -1);
    arrayOfBigInteger[1] = mutableBigInteger4.toBigInteger(this.signum);
    return arrayOfBigInteger;
  }
  
  public BigInteger remainder(BigInteger paramBigInteger) { return (paramBigInteger.mag.length < 80 || this.mag.length - paramBigInteger.mag.length < 40) ? remainderKnuth(paramBigInteger) : remainderBurnikelZiegler(paramBigInteger); }
  
  private BigInteger remainderKnuth(BigInteger paramBigInteger) {
    MutableBigInteger mutableBigInteger1 = new MutableBigInteger();
    MutableBigInteger mutableBigInteger2 = new MutableBigInteger(this.mag);
    MutableBigInteger mutableBigInteger3 = new MutableBigInteger(paramBigInteger.mag);
    return mutableBigInteger2.divideKnuth(mutableBigInteger3, mutableBigInteger1).toBigInteger(this.signum);
  }
  
  private BigInteger divideBurnikelZiegler(BigInteger paramBigInteger) { return divideAndRemainderBurnikelZiegler(paramBigInteger)[0]; }
  
  private BigInteger remainderBurnikelZiegler(BigInteger paramBigInteger) { return divideAndRemainderBurnikelZiegler(paramBigInteger)[1]; }
  
  private BigInteger[] divideAndRemainderBurnikelZiegler(BigInteger paramBigInteger) {
    MutableBigInteger mutableBigInteger1 = new MutableBigInteger();
    MutableBigInteger mutableBigInteger2 = (new MutableBigInteger(this)).divideAndRemainderBurnikelZiegler(new MutableBigInteger(paramBigInteger), mutableBigInteger1);
    BigInteger bigInteger1 = mutableBigInteger1.isZero() ? ZERO : mutableBigInteger1.toBigInteger(this.signum * paramBigInteger.signum);
    BigInteger bigInteger2 = mutableBigInteger2.isZero() ? ZERO : mutableBigInteger2.toBigInteger(this.signum);
    return new BigInteger[] { bigInteger1, bigInteger2 };
  }
  
  public BigInteger pow(int paramInt) {
    int j;
    if (paramInt < 0)
      throw new ArithmeticException("Negative exponent"); 
    if (this.signum == 0)
      return (paramInt == 0) ? ONE : this; 
    BigInteger bigInteger1 = abs();
    int i = bigInteger1.getLowestSetBit();
    long l1 = i * paramInt;
    if (l1 > 2147483647L)
      reportOverflow(); 
    if (i > 0) {
      bigInteger1 = bigInteger1.shiftRight(i);
      j = bigInteger1.bitLength();
      if (j == 1)
        return (this.signum < 0 && (paramInt & true) == 1) ? NEGATIVE_ONE.shiftLeft(i * paramInt) : ONE.shiftLeft(i * paramInt); 
    } else {
      j = bigInteger1.bitLength();
      if (j == 1)
        return (this.signum < 0 && (paramInt & true) == 1) ? NEGATIVE_ONE : ONE; 
    } 
    long l2 = j * paramInt;
    if (bigInteger1.mag.length == 1 && l2 <= 62L) {
      byte b = (this.signum < 0 && (paramInt & true) == 1) ? -1 : 1;
      long l3 = 1L;
      long l4 = bigInteger1.mag[0] & 0xFFFFFFFFL;
      int m = paramInt;
      while (m != 0) {
        if ((m & true) == 1)
          l3 *= l4; 
        if (m >>>= 1 != 0)
          l4 *= l4; 
      } 
      return (i > 0) ? ((l1 + l2 <= 62L) ? valueOf((l3 << (int)l1) * b) : valueOf(l3 * b).shiftLeft((int)l1)) : valueOf(l3 * b);
    } 
    BigInteger bigInteger2 = ONE;
    int k = paramInt;
    while (k != 0) {
      if ((k & true) == 1)
        bigInteger2 = bigInteger2.multiply(bigInteger1); 
      if (k >>>= 1 != 0)
        bigInteger1 = bigInteger1.square(); 
    } 
    if (i > 0)
      bigInteger2 = bigInteger2.shiftLeft(i * paramInt); 
    return (this.signum < 0 && (paramInt & true) == 1) ? bigInteger2.negate() : bigInteger2;
  }
  
  public BigInteger gcd(BigInteger paramBigInteger) {
    if (paramBigInteger.signum == 0)
      return abs(); 
    if (this.signum == 0)
      return paramBigInteger.abs(); 
    MutableBigInteger mutableBigInteger1 = new MutableBigInteger(this);
    MutableBigInteger mutableBigInteger2 = new MutableBigInteger(paramBigInteger);
    MutableBigInteger mutableBigInteger3 = mutableBigInteger1.hybridGCD(mutableBigInteger2);
    return mutableBigInteger3.toBigInteger(1);
  }
  
  static int bitLengthForInt(int paramInt) { return 32 - Integer.numberOfLeadingZeros(paramInt); }
  
  private static int[] leftShift(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    int i = paramInt2 >>> 5;
    int j = paramInt2 & 0x1F;
    int k = bitLengthForInt(paramArrayOfInt[0]);
    if (paramInt2 <= 32 - k) {
      primitiveLeftShift(paramArrayOfInt, paramInt1, j);
      return paramArrayOfInt;
    } 
    if (j <= 32 - k) {
      int[] arrayOfInt1 = new int[i + paramInt1];
      System.arraycopy(paramArrayOfInt, 0, arrayOfInt1, 0, paramInt1);
      primitiveLeftShift(arrayOfInt1, arrayOfInt1.length, j);
      return arrayOfInt1;
    } 
    int[] arrayOfInt = new int[i + paramInt1 + 1];
    System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, paramInt1);
    primitiveRightShift(arrayOfInt, arrayOfInt.length, 32 - j);
    return arrayOfInt;
  }
  
  static void primitiveRightShift(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    int i = 32 - paramInt2;
    int j = paramInt1 - 1;
    int k = paramArrayOfInt[j];
    while (j > 0) {
      int m = k;
      k = paramArrayOfInt[j - 1];
      paramArrayOfInt[j] = k << i | m >>> paramInt2;
      j--;
    } 
    paramArrayOfInt[0] = paramArrayOfInt[0] >>> paramInt2;
  }
  
  static void primitiveLeftShift(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    if (paramInt1 == 0 || paramInt2 == 0)
      return; 
    int i = 32 - paramInt2;
    int j = 0;
    int k = paramArrayOfInt[j];
    int m = j + paramInt1 - 1;
    while (j < m) {
      int n = k;
      k = paramArrayOfInt[j + 1];
      paramArrayOfInt[j] = n << paramInt2 | k >>> i;
      j++;
    } 
    paramArrayOfInt[paramInt1 - 1] = paramArrayOfInt[paramInt1 - 1] << paramInt2;
  }
  
  private static int bitLength(int[] paramArrayOfInt, int paramInt) { return (paramInt == 0) ? 0 : ((paramInt - 1 << 5) + bitLengthForInt(paramArrayOfInt[0])); }
  
  public BigInteger abs() { return (this.signum >= 0) ? this : negate(); }
  
  public BigInteger negate() { return new BigInteger(this.mag, -this.signum); }
  
  public int signum() { return this.signum; }
  
  public BigInteger mod(BigInteger paramBigInteger) {
    if (paramBigInteger.signum <= 0)
      throw new ArithmeticException("BigInteger: modulus not positive"); 
    BigInteger bigInteger = remainder(paramBigInteger);
    return (bigInteger.signum >= 0) ? bigInteger : bigInteger.add(paramBigInteger);
  }
  
  public BigInteger modPow(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    BigInteger bigInteger2;
    if (paramBigInteger2.signum <= 0)
      throw new ArithmeticException("BigInteger: modulus not positive"); 
    if (paramBigInteger1.signum == 0)
      return paramBigInteger2.equals(ONE) ? ZERO : ONE; 
    if (equals(ONE))
      return paramBigInteger2.equals(ONE) ? ZERO : ONE; 
    if (equals(ZERO) && paramBigInteger1.signum >= 0)
      return ZERO; 
    if (equals(negConst[1]) && !paramBigInteger1.testBit(0))
      return paramBigInteger2.equals(ONE) ? ZERO : ONE; 
    boolean bool;
    if (bool = (paramBigInteger1.signum < 0) ? 1 : 0)
      paramBigInteger1 = paramBigInteger1.negate(); 
    BigInteger bigInteger1 = (this.signum < 0 || compareTo(paramBigInteger2) >= 0) ? mod(paramBigInteger2) : this;
    if (paramBigInteger2.testBit(0)) {
      bigInteger2 = bigInteger1.oddModPow(paramBigInteger1, paramBigInteger2);
    } else {
      int i = paramBigInteger2.getLowestSetBit();
      BigInteger bigInteger3 = paramBigInteger2.shiftRight(i);
      BigInteger bigInteger4 = ONE.shiftLeft(i);
      BigInteger bigInteger5 = (this.signum < 0 || compareTo(bigInteger3) >= 0) ? mod(bigInteger3) : this;
      BigInteger bigInteger6 = bigInteger3.equals(ONE) ? ZERO : bigInteger5.oddModPow(paramBigInteger1, bigInteger3);
      BigInteger bigInteger7 = bigInteger1.modPow2(paramBigInteger1, i);
      BigInteger bigInteger8 = bigInteger4.modInverse(bigInteger3);
      BigInteger bigInteger9 = bigInteger3.modInverse(bigInteger4);
      if (paramBigInteger2.mag.length < 33554432) {
        bigInteger2 = bigInteger6.multiply(bigInteger4).multiply(bigInteger8).add(bigInteger7.multiply(bigInteger3).multiply(bigInteger9)).mod(paramBigInteger2);
      } else {
        MutableBigInteger mutableBigInteger1 = new MutableBigInteger();
        (new MutableBigInteger(bigInteger6.multiply(bigInteger4))).multiply(new MutableBigInteger(bigInteger8), mutableBigInteger1);
        MutableBigInteger mutableBigInteger2 = new MutableBigInteger();
        (new MutableBigInteger(bigInteger7.multiply(bigInteger3))).multiply(new MutableBigInteger(bigInteger9), mutableBigInteger2);
        mutableBigInteger1.add(mutableBigInteger2);
        MutableBigInteger mutableBigInteger3 = new MutableBigInteger();
        bigInteger2 = mutableBigInteger1.divide(new MutableBigInteger(paramBigInteger2), mutableBigInteger3).toBigInteger();
      } 
    } 
    return bool ? bigInteger2.modInverse(paramBigInteger2) : bigInteger2;
  }
  
  private static int[] montgomeryMultiply(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int paramInt, long paramLong, int[] paramArrayOfInt4) {
    implMontgomeryMultiplyChecks(paramArrayOfInt1, paramArrayOfInt2, paramArrayOfInt3, paramInt, paramArrayOfInt4);
    if (paramInt > 512) {
      paramArrayOfInt4 = multiplyToLen(paramArrayOfInt1, paramInt, paramArrayOfInt2, paramInt, paramArrayOfInt4);
      return montReduce(paramArrayOfInt4, paramArrayOfInt3, paramInt, (int)paramLong);
    } 
    return implMontgomeryMultiply(paramArrayOfInt1, paramArrayOfInt2, paramArrayOfInt3, paramInt, paramLong, materialize(paramArrayOfInt4, paramInt));
  }
  
  private static int[] montgomerySquare(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt, long paramLong, int[] paramArrayOfInt3) {
    implMontgomeryMultiplyChecks(paramArrayOfInt1, paramArrayOfInt1, paramArrayOfInt2, paramInt, paramArrayOfInt3);
    if (paramInt > 512) {
      paramArrayOfInt3 = squareToLen(paramArrayOfInt1, paramInt, paramArrayOfInt3);
      return montReduce(paramArrayOfInt3, paramArrayOfInt2, paramInt, (int)paramLong);
    } 
    return implMontgomerySquare(paramArrayOfInt1, paramArrayOfInt2, paramInt, paramLong, materialize(paramArrayOfInt3, paramInt));
  }
  
  private static void implMontgomeryMultiplyChecks(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int paramInt, int[] paramArrayOfInt4) throws RuntimeException {
    if (paramInt % 2 != 0)
      throw new IllegalArgumentException("input array length must be even: " + paramInt); 
    if (paramInt < 1)
      throw new IllegalArgumentException("invalid input length: " + paramInt); 
    if (paramInt > paramArrayOfInt1.length || paramInt > paramArrayOfInt2.length || paramInt > paramArrayOfInt3.length || (paramArrayOfInt4 != null && paramInt > paramArrayOfInt4.length))
      throw new IllegalArgumentException("input array length out of bound: " + paramInt); 
  }
  
  private static int[] materialize(int[] paramArrayOfInt, int paramInt) {
    if (paramArrayOfInt == null || paramArrayOfInt.length < paramInt)
      paramArrayOfInt = new int[paramInt]; 
    return paramArrayOfInt;
  }
  
  private static int[] implMontgomeryMultiply(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3, int paramInt, long paramLong, int[] paramArrayOfInt4) {
    paramArrayOfInt4 = multiplyToLen(paramArrayOfInt1, paramInt, paramArrayOfInt2, paramInt, paramArrayOfInt4);
    return montReduce(paramArrayOfInt4, paramArrayOfInt3, paramInt, (int)paramLong);
  }
  
  private static int[] implMontgomerySquare(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt, long paramLong, int[] paramArrayOfInt3) {
    paramArrayOfInt3 = squareToLen(paramArrayOfInt1, paramInt, paramArrayOfInt3);
    return montReduce(paramArrayOfInt3, paramArrayOfInt2, paramInt, (int)paramLong);
  }
  
  private BigInteger oddModPow(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    if (paramBigInteger1.equals(ONE))
      return this; 
    if (this.signum == 0)
      return ZERO; 
    int[] arrayOfInt1 = (int[])this.mag.clone();
    int[] arrayOfInt2 = paramBigInteger1.mag;
    int[] arrayOfInt3 = paramBigInteger2.mag;
    int i = arrayOfInt3.length;
    if ((i & true) != 0) {
      int[] arrayOfInt9 = new int[i + 1];
      System.arraycopy(arrayOfInt3, 0, arrayOfInt9, 1, i);
      arrayOfInt3 = arrayOfInt9;
      i++;
    } 
    int j = 0;
    int k = bitLength(arrayOfInt2, arrayOfInt2.length);
    if (k != 17 || arrayOfInt2[0] != 65537)
      while (k > bnExpModThreshTable[j])
        j++;  
    byte b1 = 1 << j;
    int[][] arrayOfInt = new int[b1][];
    for (byte b2 = 0; b2 < b1; b2++)
      arrayOfInt[b2] = new int[i]; 
    long l1 = (arrayOfInt3[i - 1] & 0xFFFFFFFFL) + ((arrayOfInt3[i - 2] & 0xFFFFFFFFL) << 32);
    long l2 = -MutableBigInteger.inverseMod64(l1);
    int[] arrayOfInt4 = leftShift(arrayOfInt1, arrayOfInt1.length, i << 5);
    MutableBigInteger mutableBigInteger1 = new MutableBigInteger();
    MutableBigInteger mutableBigInteger2 = new MutableBigInteger(arrayOfInt4);
    MutableBigInteger mutableBigInteger3 = new MutableBigInteger(arrayOfInt3);
    mutableBigInteger3.normalize();
    MutableBigInteger mutableBigInteger4 = mutableBigInteger2.divide(mutableBigInteger3, mutableBigInteger1);
    arrayOfInt[0] = mutableBigInteger4.toIntArray();
    if (arrayOfInt[0].length < i) {
      int i2 = i - arrayOfInt[0].length;
      int[] arrayOfInt9 = new int[i];
      System.arraycopy(arrayOfInt[0], 0, arrayOfInt9, i2, arrayOfInt[0].length);
      arrayOfInt[0] = arrayOfInt9;
    } 
    int[] arrayOfInt5 = montgomerySquare(arrayOfInt[0], arrayOfInt3, i, l2, null);
    int[] arrayOfInt6 = Arrays.copyOf(arrayOfInt5, i);
    int m;
    for (m = 1; m < b1; m++)
      arrayOfInt[m] = montgomeryMultiply(arrayOfInt6, arrayOfInt[m - true], arrayOfInt3, i, l2, null); 
    m = 1 << (k - 1 & 0x1F);
    byte b3 = 0;
    int n = arrayOfInt2.length;
    byte b4 = 0;
    int i1;
    for (i1 = 0; i1 <= j; i1++) {
      b3 = b3 << true | (((arrayOfInt2[b4] & m) != 0) ? 1 : 0);
      m >>>= 1;
      if (m == 0) {
        b4++;
        m = Integer.MIN_VALUE;
        n--;
      } 
    } 
    i1 = k;
    k--;
    boolean bool = true;
    for (i1 = k - j; !(b3 & true); i1++)
      b3 >>>= true; 
    int[] arrayOfInt7 = arrayOfInt[b3 >>> true];
    b3 = 0;
    if (i1 == k)
      bool = false; 
    while (true) {
      k--;
      b3 <<= true;
      if (n != 0) {
        b3 |= (((arrayOfInt2[b4] & m) != 0) ? 1 : 0);
        m >>>= 1;
        if (m == 0) {
          b4++;
          m = Integer.MIN_VALUE;
          n--;
        } 
      } 
      if ((b3 & b1) != 0) {
        for (i1 = k - j; (b3 & true) == 0; i1++)
          b3 >>>= 1; 
        arrayOfInt7 = arrayOfInt[b3 >>> 1];
        b3 = 0;
      } 
      if (k == i1)
        if (bool) {
          arrayOfInt5 = (int[])arrayOfInt7.clone();
          bool = false;
        } else {
          arrayOfInt6 = arrayOfInt5;
          arrayOfInt4 = montgomeryMultiply(arrayOfInt6, arrayOfInt7, arrayOfInt3, i, l2, arrayOfInt4);
          arrayOfInt6 = arrayOfInt4;
          arrayOfInt4 = arrayOfInt5;
          arrayOfInt5 = arrayOfInt6;
        }  
      if (k == 0)
        break; 
      if (!bool) {
        arrayOfInt6 = arrayOfInt5;
        arrayOfInt4 = montgomerySquare(arrayOfInt6, arrayOfInt3, i, l2, arrayOfInt4);
        arrayOfInt6 = arrayOfInt4;
        arrayOfInt4 = arrayOfInt5;
        arrayOfInt5 = arrayOfInt6;
      } 
    } 
    int[] arrayOfInt8 = new int[2 * i];
    System.arraycopy(arrayOfInt5, 0, arrayOfInt8, i, i);
    arrayOfInt5 = montReduce(arrayOfInt8, arrayOfInt3, i, (int)l2);
    arrayOfInt8 = Arrays.copyOf(arrayOfInt5, i);
    return new BigInteger(1, arrayOfInt8);
  }
  
  private static int[] montReduce(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2) {
    int i = 0;
    int j = paramInt1;
    int k = 0;
    do {
      int m = paramArrayOfInt1[paramArrayOfInt1.length - 1 - k];
      int n = mulAdd(paramArrayOfInt1, paramArrayOfInt2, k, paramInt1, paramInt2 * m);
      i += addOne(paramArrayOfInt1, k, paramInt1, n);
      k++;
    } while (--j > 0);
    while (i > 0)
      i += subN(paramArrayOfInt1, paramArrayOfInt2, paramInt1); 
    while (intArrayCmpToLen(paramArrayOfInt1, paramArrayOfInt2, paramInt1) >= 0)
      subN(paramArrayOfInt1, paramArrayOfInt2, paramInt1); 
    return paramArrayOfInt1;
  }
  
  private static int intArrayCmpToLen(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    for (byte b = 0; b < paramInt; b++) {
      long l1 = paramArrayOfInt1[b] & 0xFFFFFFFFL;
      long l2 = paramArrayOfInt2[b] & 0xFFFFFFFFL;
      if (l1 < l2)
        return -1; 
      if (l1 > l2)
        return 1; 
    } 
    return 0;
  }
  
  private static int subN(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt) {
    long l = 0L;
    while (--paramInt >= 0) {
      l = (paramArrayOfInt1[paramInt] & 0xFFFFFFFFL) - (paramArrayOfInt2[paramInt] & 0xFFFFFFFFL) + (l >> 32);
      paramArrayOfInt1[paramInt] = (int)l;
    } 
    return (int)(l >> 32);
  }
  
  static int mulAdd(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2, int paramInt3) {
    implMulAddCheck(paramArrayOfInt1, paramArrayOfInt2, paramInt1, paramInt2, paramInt3);
    return implMulAdd(paramArrayOfInt1, paramArrayOfInt2, paramInt1, paramInt2, paramInt3);
  }
  
  private static void implMulAddCheck(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt2 > paramArrayOfInt2.length)
      throw new IllegalArgumentException("input length is out of bound: " + paramInt2 + " > " + paramArrayOfInt2.length); 
    if (paramInt1 < 0)
      throw new IllegalArgumentException("input offset is invalid: " + paramInt1); 
    if (paramInt1 > paramArrayOfInt1.length - 1)
      throw new IllegalArgumentException("input offset is out of bound: " + paramInt1 + " > " + (paramArrayOfInt1.length - 1)); 
    if (paramInt2 > paramArrayOfInt1.length - paramInt1)
      throw new IllegalArgumentException("input len is out of bound: " + paramInt2 + " > " + (paramArrayOfInt1.length - paramInt1)); 
  }
  
  private static int implMulAdd(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2, int paramInt3) {
    long l1 = paramInt3 & 0xFFFFFFFFL;
    long l2 = 0L;
    paramInt1 = paramArrayOfInt1.length - paramInt1 - 1;
    for (int i = paramInt2 - 1; i >= 0; i--) {
      long l = (paramArrayOfInt2[i] & 0xFFFFFFFFL) * l1 + (paramArrayOfInt1[paramInt1] & 0xFFFFFFFFL) + l2;
      paramArrayOfInt1[paramInt1--] = (int)l;
      l2 = l >>> 32;
    } 
    return (int)l2;
  }
  
  static int addOne(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3) {
    paramInt1 = paramArrayOfInt.length - 1 - paramInt2 - paramInt1;
    long l = (paramArrayOfInt[paramInt1] & 0xFFFFFFFFL) + (paramInt3 & 0xFFFFFFFFL);
    paramArrayOfInt[paramInt1] = (int)l;
    if (l >>> 32 == 0L)
      return 0; 
    while (--paramInt2 >= 0) {
      if (--paramInt1 < 0)
        return 1; 
      paramArrayOfInt[paramInt1] = paramArrayOfInt[paramInt1] + 1;
      if (paramArrayOfInt[paramInt1] != 0)
        return 0; 
    } 
    return 1;
  }
  
  private BigInteger modPow2(BigInteger paramBigInteger, int paramInt) {
    BigInteger bigInteger1 = ONE;
    BigInteger bigInteger2 = mod2(paramInt);
    byte b = 0;
    int i = paramBigInteger.bitLength();
    if (testBit(0))
      i = (paramInt - 1 < i) ? (paramInt - 1) : i; 
    while (b < i) {
      if (paramBigInteger.testBit(b))
        bigInteger1 = bigInteger1.multiply(bigInteger2).mod2(paramInt); 
      if (++b < i)
        bigInteger2 = bigInteger2.square().mod2(paramInt); 
    } 
    return bigInteger1;
  }
  
  private BigInteger mod2(int paramInt) {
    if (bitLength() <= paramInt)
      return this; 
    int i = paramInt + 31 >>> 5;
    int[] arrayOfInt = new int[i];
    System.arraycopy(this.mag, this.mag.length - i, arrayOfInt, 0, i);
    int j = (i << 5) - paramInt;
    arrayOfInt[0] = (int)(arrayOfInt[0] & (1L << 32 - j) - 1L);
    return (arrayOfInt[0] == 0) ? new BigInteger(1, arrayOfInt) : new BigInteger(arrayOfInt, 1);
  }
  
  public BigInteger modInverse(BigInteger paramBigInteger) {
    if (paramBigInteger.signum != 1)
      throw new ArithmeticException("BigInteger: modulus not positive"); 
    if (paramBigInteger.equals(ONE))
      return ZERO; 
    BigInteger bigInteger = this;
    if (this.signum < 0 || compareMagnitude(paramBigInteger) >= 0)
      bigInteger = mod(paramBigInteger); 
    if (bigInteger.equals(ONE))
      return ONE; 
    MutableBigInteger mutableBigInteger1 = new MutableBigInteger(bigInteger);
    MutableBigInteger mutableBigInteger2 = new MutableBigInteger(paramBigInteger);
    MutableBigInteger mutableBigInteger3 = mutableBigInteger1.mutableModInverse(mutableBigInteger2);
    return mutableBigInteger3.toBigInteger(1);
  }
  
  public BigInteger shiftLeft(int paramInt) { return (this.signum == 0) ? ZERO : ((paramInt > 0) ? new BigInteger(shiftLeft(this.mag, paramInt), this.signum) : ((paramInt == 0) ? this : shiftRightImpl(-paramInt))); }
  
  private static int[] shiftLeft(int[] paramArrayOfInt, int paramInt) {
    int i = paramInt >>> 5;
    int j = paramInt & 0x1F;
    int k = paramArrayOfInt.length;
    int[] arrayOfInt = null;
    if (j == 0) {
      arrayOfInt = new int[k + i];
      System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, k);
    } else {
      byte b1 = 0;
      int m = 32 - j;
      int n = paramArrayOfInt[0] >>> m;
      if (n != 0) {
        arrayOfInt = new int[k + i + 1];
        arrayOfInt[b1++] = n;
      } else {
        arrayOfInt = new int[k + i];
      } 
      byte b2 = 0;
      while (b2 < k - 1)
        arrayOfInt[b1++] = paramArrayOfInt[b2++] << j | paramArrayOfInt[b2] >>> m; 
      arrayOfInt[b1] = paramArrayOfInt[b2] << j;
    } 
    return arrayOfInt;
  }
  
  public BigInteger shiftRight(int paramInt) { return (this.signum == 0) ? ZERO : ((paramInt > 0) ? shiftRightImpl(paramInt) : ((paramInt == 0) ? this : new BigInteger(shiftLeft(this.mag, -paramInt), this.signum))); }
  
  private BigInteger shiftRightImpl(int paramInt) {
    int i = paramInt >>> 5;
    int j = paramInt & 0x1F;
    int k = this.mag.length;
    int[] arrayOfInt = null;
    if (i >= k)
      return (this.signum >= 0) ? ZERO : negConst[1]; 
    if (j == 0) {
      int m = k - i;
      arrayOfInt = Arrays.copyOf(this.mag, m);
    } else {
      byte b1 = 0;
      int m = this.mag[0] >>> j;
      if (m != 0) {
        arrayOfInt = new int[k - i];
        arrayOfInt[b1++] = m;
      } else {
        arrayOfInt = new int[k - i - 1];
      } 
      int n = 32 - j;
      byte b2 = 0;
      while (b2 < k - i - 1)
        arrayOfInt[b1++] = this.mag[b2++] << n | this.mag[b2] >>> j; 
    } 
    if (this.signum < 0) {
      boolean bool = false;
      int m = k - 1;
      int n = k - i;
      while (m >= n && !bool) {
        bool = (this.mag[m] != 0) ? 1 : 0;
        m--;
      } 
      if (!bool && j != 0)
        bool = (this.mag[k - i - 1] << 32 - j != 0) ? 1 : 0; 
      if (bool)
        arrayOfInt = javaIncrement(arrayOfInt); 
    } 
    return new BigInteger(arrayOfInt, this.signum);
  }
  
  int[] javaIncrement(int[] paramArrayOfInt) {
    int i = 0;
    for (int j = paramArrayOfInt.length - 1; j >= 0 && !i; j--)
      i = paramArrayOfInt[j] = paramArrayOfInt[j] + 1; 
    if (i == 0) {
      paramArrayOfInt = new int[paramArrayOfInt.length + 1];
      paramArrayOfInt[0] = 1;
    } 
    return paramArrayOfInt;
  }
  
  public BigInteger and(BigInteger paramBigInteger) {
    int[] arrayOfInt = new int[Math.max(intLength(), paramBigInteger.intLength())];
    for (int i = 0; i < arrayOfInt.length; i++)
      arrayOfInt[i] = getInt(arrayOfInt.length - i - 1) & paramBigInteger.getInt(arrayOfInt.length - i - 1); 
    return valueOf(arrayOfInt);
  }
  
  public BigInteger or(BigInteger paramBigInteger) {
    int[] arrayOfInt = new int[Math.max(intLength(), paramBigInteger.intLength())];
    for (int i = 0; i < arrayOfInt.length; i++)
      arrayOfInt[i] = getInt(arrayOfInt.length - i - 1) | paramBigInteger.getInt(arrayOfInt.length - i - 1); 
    return valueOf(arrayOfInt);
  }
  
  public BigInteger xor(BigInteger paramBigInteger) {
    int[] arrayOfInt = new int[Math.max(intLength(), paramBigInteger.intLength())];
    for (int i = 0; i < arrayOfInt.length; i++)
      arrayOfInt[i] = getInt(arrayOfInt.length - i - 1) ^ paramBigInteger.getInt(arrayOfInt.length - i - 1); 
    return valueOf(arrayOfInt);
  }
  
  public BigInteger not() {
    int[] arrayOfInt = new int[intLength()];
    for (int i = 0; i < arrayOfInt.length; i++)
      arrayOfInt[i] = getInt(arrayOfInt.length - i - 1) ^ 0xFFFFFFFF; 
    return valueOf(arrayOfInt);
  }
  
  public BigInteger andNot(BigInteger paramBigInteger) {
    int[] arrayOfInt = new int[Math.max(intLength(), paramBigInteger.intLength())];
    for (int i = 0; i < arrayOfInt.length; i++)
      arrayOfInt[i] = getInt(arrayOfInt.length - i - 1) & (paramBigInteger.getInt(arrayOfInt.length - i - 1) ^ 0xFFFFFFFF); 
    return valueOf(arrayOfInt);
  }
  
  public boolean testBit(int paramInt) {
    if (paramInt < 0)
      throw new ArithmeticException("Negative bit address"); 
    return ((getInt(paramInt >>> 5) & 1 << (paramInt & 0x1F)) != 0);
  }
  
  public BigInteger setBit(int paramInt) {
    if (paramInt < 0)
      throw new ArithmeticException("Negative bit address"); 
    int i = paramInt >>> 5;
    int[] arrayOfInt = new int[Math.max(intLength(), i + 2)];
    for (int j = 0; j < arrayOfInt.length; j++)
      arrayOfInt[arrayOfInt.length - j - 1] = getInt(j); 
    arrayOfInt[arrayOfInt.length - i - 1] = arrayOfInt[arrayOfInt.length - i - 1] | 1 << (paramInt & 0x1F);
    return valueOf(arrayOfInt);
  }
  
  public BigInteger clearBit(int paramInt) {
    if (paramInt < 0)
      throw new ArithmeticException("Negative bit address"); 
    int i = paramInt >>> 5;
    int[] arrayOfInt = new int[Math.max(intLength(), (paramInt + 1 >>> 5) + 1)];
    for (int j = 0; j < arrayOfInt.length; j++)
      arrayOfInt[arrayOfInt.length - j - 1] = getInt(j); 
    arrayOfInt[arrayOfInt.length - i - 1] = arrayOfInt[arrayOfInt.length - i - 1] & (1 << (paramInt & 0x1F) ^ 0xFFFFFFFF);
    return valueOf(arrayOfInt);
  }
  
  public BigInteger flipBit(int paramInt) {
    if (paramInt < 0)
      throw new ArithmeticException("Negative bit address"); 
    int i = paramInt >>> 5;
    int[] arrayOfInt = new int[Math.max(intLength(), i + 2)];
    for (int j = 0; j < arrayOfInt.length; j++)
      arrayOfInt[arrayOfInt.length - j - 1] = getInt(j); 
    arrayOfInt[arrayOfInt.length - i - 1] = arrayOfInt[arrayOfInt.length - i - 1] ^ 1 << (paramInt & 0x1F);
    return valueOf(arrayOfInt);
  }
  
  public int getLowestSetBit() {
    int i = this.lowestSetBit - 2;
    if (i == -2) {
      i = 0;
      if (this.signum == 0) {
        i--;
      } else {
        byte b;
        int j;
        for (b = 0; (j = getInt(b)) == 0; b++);
        i += (b << 5) + Integer.numberOfTrailingZeros(j);
      } 
      this.lowestSetBit = i + 2;
    } 
    return i;
  }
  
  public int bitLength() {
    int i = this.bitLength - 1;
    if (i == -1) {
      int[] arrayOfInt = this.mag;
      int j = arrayOfInt.length;
      if (j == 0) {
        i = 0;
      } else {
        int k = (j - 1 << 5) + bitLengthForInt(this.mag[0]);
        if (this.signum < 0) {
          boolean bool = (Integer.bitCount(this.mag[0]) == 1) ? 1 : 0;
          for (byte b = 1; b < j && bool; b++)
            bool = (this.mag[b] == 0) ? 1 : 0; 
          i = bool ? (k - 1) : k;
        } else {
          i = k;
        } 
      } 
      this.bitLength = i + 1;
    } 
    return i;
  }
  
  public int bitCount() {
    int i = this.bitCount - 1;
    if (i == -1) {
      i = 0;
      int j;
      for (j = 0; j < this.mag.length; j++)
        i += Integer.bitCount(this.mag[j]); 
      if (this.signum < 0) {
        j = 0;
        int k;
        for (k = this.mag.length - 1; this.mag[k] == 0; k--)
          j += 32; 
        j += Integer.numberOfTrailingZeros(this.mag[k]);
        i += j - 1;
      } 
      this.bitCount = i + 1;
    } 
    return i;
  }
  
  public boolean isProbablePrime(int paramInt) {
    if (paramInt <= 0)
      return true; 
    BigInteger bigInteger = abs();
    return bigInteger.equals(TWO) ? true : ((!bigInteger.testBit(0) || bigInteger.equals(ONE)) ? false : bigInteger.primeToCertainty(paramInt, null));
  }
  
  public int compareTo(BigInteger paramBigInteger) {
    if (this.signum == paramBigInteger.signum) {
      switch (this.signum) {
        case 1:
          return compareMagnitude(paramBigInteger);
        case -1:
          return paramBigInteger.compareMagnitude(this);
      } 
      return 0;
    } 
    return (this.signum > paramBigInteger.signum) ? 1 : -1;
  }
  
  final int compareMagnitude(BigInteger paramBigInteger) {
    int[] arrayOfInt1 = this.mag;
    int i = arrayOfInt1.length;
    int[] arrayOfInt2 = paramBigInteger.mag;
    int j = arrayOfInt2.length;
    if (i < j)
      return -1; 
    if (i > j)
      return 1; 
    for (byte b = 0; b < i; b++) {
      int k = arrayOfInt1[b];
      int m = arrayOfInt2[b];
      if (k != m)
        return ((k & 0xFFFFFFFFL) < (m & 0xFFFFFFFFL)) ? -1 : 1; 
    } 
    return 0;
  }
  
  final int compareMagnitude(long paramLong) {
    assert paramLong != Float.MIN_VALUE;
    int[] arrayOfInt = this.mag;
    int i = arrayOfInt.length;
    if (i > 2)
      return 1; 
    if (paramLong < 0L)
      paramLong = -paramLong; 
    int j = (int)(paramLong >>> 32);
    if (j == 0) {
      if (i < 1)
        return -1; 
      if (i > 1)
        return 1; 
      int n = arrayOfInt[0];
      int i1 = (int)paramLong;
      return (n != i1) ? (((n & 0xFFFFFFFFL) < (i1 & 0xFFFFFFFFL)) ? -1 : 1) : 0;
    } 
    if (i < 2)
      return -1; 
    int k = arrayOfInt[0];
    int m = j;
    if (k != m)
      return ((k & 0xFFFFFFFFL) < (m & 0xFFFFFFFFL)) ? -1 : 1; 
    k = arrayOfInt[1];
    m = (int)paramLong;
    return (k != m) ? (((k & 0xFFFFFFFFL) < (m & 0xFFFFFFFFL)) ? -1 : 1) : 0;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof BigInteger))
      return false; 
    BigInteger bigInteger = (BigInteger)paramObject;
    if (bigInteger.signum != this.signum)
      return false; 
    int[] arrayOfInt1 = this.mag;
    int i = arrayOfInt1.length;
    int[] arrayOfInt2 = bigInteger.mag;
    if (i != arrayOfInt2.length)
      return false; 
    for (byte b = 0; b < i; b++) {
      if (arrayOfInt2[b] != arrayOfInt1[b])
        return false; 
    } 
    return true;
  }
  
  public BigInteger min(BigInteger paramBigInteger) { return (compareTo(paramBigInteger) < 0) ? this : paramBigInteger; }
  
  public BigInteger max(BigInteger paramBigInteger) { return (compareTo(paramBigInteger) > 0) ? this : paramBigInteger; }
  
  public int hashCode() {
    int i = 0;
    for (byte b = 0; b < this.mag.length; b++)
      i = (int)((31 * i) + (this.mag[b] & 0xFFFFFFFFL)); 
    return i * this.signum;
  }
  
  public String toString(int paramInt) {
    if (this.signum == 0)
      return "0"; 
    if (paramInt < 2 || paramInt > 36)
      paramInt = 10; 
    if (this.mag.length <= 20)
      return smallToString(paramInt); 
    StringBuilder stringBuilder = new StringBuilder();
    if (this.signum < 0) {
      toString(negate(), stringBuilder, paramInt, 0);
      stringBuilder.insert(0, '-');
    } else {
      toString(this, stringBuilder, paramInt, 0);
    } 
    return stringBuilder.toString();
  }
  
  private String smallToString(int paramInt) {
    if (this.signum == 0)
      return "0"; 
    int i = (4 * this.mag.length + 6) / 7;
    String[] arrayOfString = new String[i];
    BigInteger bigInteger = abs();
    int j = 0;
    while (bigInteger.signum != 0) {
      BigInteger bigInteger1 = longRadix[paramInt];
      MutableBigInteger mutableBigInteger1 = new MutableBigInteger();
      MutableBigInteger mutableBigInteger2 = new MutableBigInteger(bigInteger.mag);
      MutableBigInteger mutableBigInteger3 = new MutableBigInteger(bigInteger1.mag);
      MutableBigInteger mutableBigInteger4 = mutableBigInteger2.divide(mutableBigInteger3, mutableBigInteger1);
      BigInteger bigInteger2 = mutableBigInteger1.toBigInteger(bigInteger.signum * bigInteger1.signum);
      BigInteger bigInteger3 = mutableBigInteger4.toBigInteger(bigInteger.signum * bigInteger1.signum);
      arrayOfString[j++] = Long.toString(bigInteger3.longValue(), paramInt);
      bigInteger = bigInteger2;
    } 
    StringBuilder stringBuilder = new StringBuilder(j * digitsPerLong[paramInt] + 1);
    if (this.signum < 0)
      stringBuilder.append('-'); 
    stringBuilder.append(arrayOfString[j - 1]);
    for (int k = j - 2; k >= 0; k--) {
      int m = digitsPerLong[paramInt] - arrayOfString[k].length();
      if (m != 0)
        stringBuilder.append(zeros[m]); 
      stringBuilder.append(arrayOfString[k]);
    } 
    return stringBuilder.toString();
  }
  
  private static void toString(BigInteger paramBigInteger, StringBuilder paramStringBuilder, int paramInt1, int paramInt2) {
    if (paramBigInteger.mag.length <= 20) {
      String str = paramBigInteger.smallToString(paramInt1);
      if (str.length() < paramInt2 && paramStringBuilder.length() > 0)
        for (int m = str.length(); m < paramInt2; m++)
          paramStringBuilder.append('0');  
      paramStringBuilder.append(str);
      return;
    } 
    int i = paramBigInteger.bitLength();
    int j = (int)Math.round(Math.log(i * LOG_TWO / logCache[paramInt1]) / LOG_TWO - 1.0D);
    BigInteger bigInteger = getRadixConversionCache(paramInt1, j);
    BigInteger[] arrayOfBigInteger = paramBigInteger.divideAndRemainder(bigInteger);
    int k = 1 << j;
    toString(arrayOfBigInteger[0], paramStringBuilder, paramInt1, paramInt2 - k);
    toString(arrayOfBigInteger[1], paramStringBuilder, paramInt1, k);
  }
  
  private static BigInteger getRadixConversionCache(int paramInt1, int paramInt2) {
    BigInteger[] arrayOfBigInteger = powerCache[paramInt1];
    if (paramInt2 < arrayOfBigInteger.length)
      return arrayOfBigInteger[paramInt2]; 
    int i = arrayOfBigInteger.length;
    arrayOfBigInteger = (BigInteger[])Arrays.copyOf(arrayOfBigInteger, paramInt2 + 1);
    for (int j = i; j <= paramInt2; j++)
      arrayOfBigInteger[j] = arrayOfBigInteger[j - 1].pow(2); 
    BigInteger[][] arrayOfBigInteger1 = powerCache;
    if (paramInt2 >= arrayOfBigInteger1[paramInt1].length) {
      arrayOfBigInteger1 = (BigInteger[][])arrayOfBigInteger1.clone();
      arrayOfBigInteger1[paramInt1] = arrayOfBigInteger;
      powerCache = arrayOfBigInteger1;
    } 
    return arrayOfBigInteger[paramInt2];
  }
  
  public String toString() { return toString(10); }
  
  public byte[] toByteArray() {
    int i = bitLength() / 8 + 1;
    byte[] arrayOfByte = new byte[i];
    int j = i - 1;
    byte b1 = 4;
    int k = 0;
    byte b2 = 0;
    while (j >= 0) {
      if (b1 == 4) {
        k = getInt(b2++);
        b1 = 1;
      } else {
        k >>>= 8;
        b1++;
      } 
      arrayOfByte[j] = (byte)k;
      j--;
    } 
    return arrayOfByte;
  }
  
  public int intValue() {
    null = 0;
    return getInt(0);
  }
  
  public long longValue() {
    long l = 0L;
    for (byte b = 1; b; b--)
      l = (l << 32) + (getInt(b) & 0xFFFFFFFFL); 
    return l;
  }
  
  public float floatValue() {
    int k;
    if (this.signum == 0)
      return 0.0F; 
    int i = (this.mag.length - 1 << 5) + bitLengthForInt(this.mag[0]) - 1;
    if (i < 63)
      return (float)longValue(); 
    if (i > 127)
      return (this.signum > 0) ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY; 
    int j = i - 24;
    int m = j & 0x1F;
    int n = 32 - m;
    if (m == 0) {
      k = this.mag[0];
    } else {
      k = this.mag[0] >>> m;
      if (k == 0)
        k = this.mag[0] << n | this.mag[1] >>> m; 
    } 
    int i1 = k >> 1;
    i1 &= 0x7FFFFF;
    boolean bool = ((k & true) != 0 && ((i1 & true) != 0 || abs().getLowestSetBit() < j)) ? 1 : 0;
    int i2 = bool ? (i1 + 1) : i1;
    int i3 = i + 127 << 23;
    i3 += i2;
    i3 |= this.signum & 0x80000000;
    return Float.intBitsToFloat(i3);
  }
  
  public double doubleValue() {
    int i1;
    int n;
    if (this.signum == 0)
      return 0.0D; 
    int i = (this.mag.length - 1 << 5) + bitLengthForInt(this.mag[0]) - 1;
    if (i < 63)
      return longValue(); 
    if (i > 1023)
      return (this.signum > 0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY; 
    int j = i - 53;
    int k = j & 0x1F;
    int m = 32 - k;
    if (k == 0) {
      n = this.mag[0];
      i1 = this.mag[1];
    } else {
      n = this.mag[0] >>> k;
      i1 = this.mag[0] << m | this.mag[1] >>> k;
      if (n == 0) {
        n = i1;
        i1 = this.mag[1] << m | this.mag[2] >>> k;
      } 
    } 
    long l1 = (n & 0xFFFFFFFFL) << 32 | i1 & 0xFFFFFFFFL;
    long l2 = l1 >> true;
    l2 &= 0xFFFFFFFFFFFFFL;
    boolean bool = ((l1 & 0x1L) != 0L && ((l2 & 0x1L) != 0L || abs().getLowestSetBit() < j)) ? 1 : 0;
    long l3 = bool ? (l2 + 1L) : l2;
    long l4 = (i + 1023) << 52;
    l4 += l3;
    l4 |= this.signum & Float.MIN_VALUE;
    return Double.longBitsToDouble(l4);
  }
  
  private static int[] stripLeadingZeroInts(int[] paramArrayOfInt) {
    int i = paramArrayOfInt.length;
    byte b;
    for (b = 0; b < i && paramArrayOfInt[b] == 0; b++);
    return Arrays.copyOfRange(paramArrayOfInt, b, i);
  }
  
  private static int[] trustedStripLeadingZeroInts(int[] paramArrayOfInt) {
    int i = paramArrayOfInt.length;
    byte b;
    for (b = 0; b < i && paramArrayOfInt[b] == 0; b++);
    return (b == 0) ? paramArrayOfInt : Arrays.copyOfRange(paramArrayOfInt, b, i);
  }
  
  private static int[] stripLeadingZeroBytes(byte[] paramArrayOfByte) {
    int i = paramArrayOfByte.length;
    int j;
    for (j = 0; j < i && paramArrayOfByte[j] == 0; j++);
    int k = i - j + 3 >>> 2;
    int[] arrayOfInt = new int[k];
    int m = i - 1;
    for (int n = k - 1; n >= 0; n--) {
      arrayOfInt[n] = paramArrayOfByte[m--] & 0xFF;
      int i1 = m - j + 1;
      int i2 = Math.min(3, i1);
      for (byte b = 8; b <= i2 << 3; b += 8)
        arrayOfInt[n] = arrayOfInt[n] | (paramArrayOfByte[m--] & 0xFF) << b; 
    } 
    return arrayOfInt;
  }
  
  private static int[] makePositive(byte[] paramArrayOfByte) {
    int j = paramArrayOfByte.length;
    int i;
    for (i = 0; i < j && paramArrayOfByte[i] == -1; i++);
    byte b;
    for (b = i; b < j && paramArrayOfByte[b] == 0; b++);
    int k = (b == j) ? 1 : 0;
    int m = j - i + k + 3 >>> 2;
    int[] arrayOfInt = new int[m];
    int n = j - 1;
    int i1;
    for (i1 = m - 1; i1 >= 0; i1--) {
      arrayOfInt[i1] = paramArrayOfByte[n--] & 0xFF;
      int i2 = Math.min(3, n - i + 1);
      if (i2 < 0)
        i2 = 0; 
      int i3;
      for (i3 = 8; i3 <= 8 * i2; i3 += 8)
        arrayOfInt[i1] = arrayOfInt[i1] | (paramArrayOfByte[n--] & 0xFF) << i3; 
      i3 = -1 >>> 8 * (3 - i2);
      arrayOfInt[i1] = (arrayOfInt[i1] ^ 0xFFFFFFFF) & i3;
    } 
    for (i1 = arrayOfInt.length - 1; i1 >= 0; i1--) {
      arrayOfInt[i1] = (int)((arrayOfInt[i1] & 0xFFFFFFFFL) + 1L);
      if (arrayOfInt[i1] != 0)
        break; 
    } 
    return arrayOfInt;
  }
  
  private static int[] makePositive(int[] paramArrayOfInt) {
    int i;
    for (i = 0; i < paramArrayOfInt.length && paramArrayOfInt[i] == -1; i++);
    byte b;
    for (b = i; b < paramArrayOfInt.length && paramArrayOfInt[b] == 0; b++);
    int j = (b == paramArrayOfInt.length) ? 1 : 0;
    int[] arrayOfInt = new int[paramArrayOfInt.length - i + j];
    int k;
    for (k = i; k < paramArrayOfInt.length; k++)
      arrayOfInt[k - i + j] = paramArrayOfInt[k] ^ 0xFFFFFFFF; 
    k = arrayOfInt.length - 1;
    arrayOfInt[k] = arrayOfInt[k] + 1;
    while (arrayOfInt[k] + 1 == 0)
      k--; 
    return arrayOfInt;
  }
  
  private int intLength() { return (bitLength() >>> 5) + 1; }
  
  private int signBit() { return (this.signum < 0) ? 1 : 0; }
  
  private int signInt() { return (this.signum < 0) ? -1 : 0; }
  
  private int getInt(int paramInt) {
    if (paramInt < 0)
      return 0; 
    if (paramInt >= this.mag.length)
      return signInt(); 
    int i = this.mag[this.mag.length - paramInt - 1];
    return (this.signum >= 0) ? i : ((paramInt <= firstNonzeroIntNum()) ? -i : (i ^ 0xFFFFFFFF));
  }
  
  private int firstNonzeroIntNum() {
    int i = this.firstNonzeroIntNum - 2;
    if (i == -2) {
      i = 0;
      int k = this.mag.length;
      int j;
      for (j = k - 1; j >= 0 && this.mag[j] == 0; j--);
      i = k - j - 1;
      this.firstNonzeroIntNum = i + 2;
    } 
    return i;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    int i = getField.get("signum", -2);
    byte[] arrayOfByte = (byte[])getField.get("magnitude", null);
    if (i < -1 || i > 1) {
      String str = "BigInteger: Invalid signum value";
      if (getField.defaulted("signum"))
        str = "BigInteger: Signum not present in stream"; 
      throw new StreamCorruptedException(str);
    } 
    int[] arrayOfInt = stripLeadingZeroBytes(arrayOfByte);
    if (((arrayOfInt.length == 0) ? 1 : 0) != ((i == 0) ? 1 : 0)) {
      String str = "BigInteger: signum-magnitude mismatch";
      if (getField.defaulted("magnitude"))
        str = "BigInteger: Magnitude not present in stream"; 
      throw new StreamCorruptedException(str);
    } 
    UnsafeHolder.putSign(this, i);
    UnsafeHolder.putMag(this, arrayOfInt);
    if (arrayOfInt.length >= 67108864)
      try {
        checkRange();
      } catch (ArithmeticException arithmeticException) {
        throw new StreamCorruptedException("BigInteger: Out of the supported range");
      }  
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("signum", this.signum);
    putField.put("magnitude", magSerializedForm());
    putField.put("bitCount", -1);
    putField.put("bitLength", -1);
    putField.put("lowestSetBit", -2);
    putField.put("firstNonzeroByteNum", -2);
    paramObjectOutputStream.writeFields();
  }
  
  private byte[] magSerializedForm() {
    int i = this.mag.length;
    boolean bool = (i == 0) ? 0 : ((i - 1 << 5) + bitLengthForInt(this.mag[0]));
    byte b1 = bool + 7 >>> 3;
    byte[] arrayOfByte = new byte[b1];
    byte b2 = b1 - 1;
    byte b3 = 4;
    int j = i - 1;
    int k = 0;
    while (b2 >= 0) {
      if (b3 == 4) {
        k = this.mag[j--];
        b3 = 1;
      } else {
        k >>>= 8;
        b3++;
      } 
      arrayOfByte[b2] = (byte)k;
      b2--;
    } 
    return arrayOfByte;
  }
  
  public long longValueExact() {
    if (this.mag.length <= 2 && bitLength() <= 63)
      return longValue(); 
    throw new ArithmeticException("BigInteger out of long range");
  }
  
  public int intValueExact() {
    if (this.mag.length <= 1 && bitLength() <= 31)
      return intValue(); 
    throw new ArithmeticException("BigInteger out of int range");
  }
  
  public short shortValueExact() {
    if (this.mag.length <= 1 && bitLength() <= 31) {
      int i = intValue();
      if (i >= -32768 && i <= 32767)
        return shortValue(); 
    } 
    throw new ArithmeticException("BigInteger out of short range");
  }
  
  public byte byteValueExact() {
    if (this.mag.length <= 1 && bitLength() <= 31) {
      int i = intValue();
      if (i >= -128 && i <= 127)
        return byteValue(); 
    } 
    throw new ArithmeticException("BigInteger out of byte range");
  }
  
  static  {
    byte b;
    for (b = 1; b <= 16; b++) {
      int[] arrayOfInt = new int[1];
      arrayOfInt[0] = b;
      posConst[b] = new BigInteger(arrayOfInt, 1);
      negConst[b] = new BigInteger(arrayOfInt, -1);
    } 
    powerCache = new BigInteger[37][];
    logCache = new double[37];
    for (b = 2; b <= 36; b++) {
      new BigInteger[1][0] = valueOf(b);
      powerCache[b] = new BigInteger[1];
      logCache[b] = Math.log(b);
    } 
    TEN = (NEGATIVE_ONE = (TWO = (ONE = (ZERO = new BigInteger(new int[0], 0)).valueOf(1L)).valueOf(2L)).valueOf(-1L)).valueOf(10L);
    bnExpModThreshTable = new int[] { 7, 25, 81, 241, 673, 1793, Integer.MAX_VALUE };
    zeros = new String[64];
    zeros[63] = "000000000000000000000000000000000000000000000000000000000000000";
    for (b = 0; b < 63; b++)
      zeros[b] = zeros[63].substring(0, b); 
    digitsPerLong = new int[] { 
        0, 0, 62, 39, 31, 27, 24, 22, 20, 19, 
        18, 18, 17, 17, 16, 16, 15, 15, 15, 14, 
        14, 14, 14, 13, 13, 13, 13, 13, 13, 12, 
        12, 12, 12, 12, 12, 12, 12 };
    longRadix = new BigInteger[] { null, (new BigInteger[37][35] = (new BigInteger[37][34] = (new BigInteger[37][33] = (new BigInteger[37][32] = (new BigInteger[37][31] = (new BigInteger[37][30] = (new BigInteger[37][29] = (new BigInteger[37][28] = (new BigInteger[37][27] = (new BigInteger[37][26] = (new BigInteger[37][25] = (new BigInteger[37][24] = (new BigInteger[37][23] = (new BigInteger[37][22] = (new BigInteger[37][21] = (new BigInteger[37][20] = (new BigInteger[37][19] = (new BigInteger[37][18] = (new BigInteger[37][17] = (new BigInteger[37][16] = (new BigInteger[37][15] = (new BigInteger[37][14] = (new BigInteger[37][13] = (new BigInteger[37][12] = (new BigInteger[37][11] = (new BigInteger[37][10] = (new BigInteger[37][9] = (new BigInteger[37][8] = (new BigInteger[37][7] = (new BigInteger[37][6] = (new BigInteger[37][5] = (new BigInteger[37][4] = (new BigInteger[37][3] = (new BigInteger[37][2] = (new BigInteger[37][1] = null).valueOf(4611686018427387904L)).valueOf(4052555153018976267L)).valueOf(4611686018427387904L)).valueOf(7450580596923828125L)).valueOf(4738381338321616896L)).valueOf(3909821048582988049L)).valueOf(1152921504606846976L)).valueOf(1350851717672992089L)).valueOf(1000000000000000000L)).valueOf(5559917313492231481L)).valueOf(2218611106740436992L)).valueOf(8650415919381337933L)).valueOf(2177953337809371136L)).valueOf(6568408355712890625L)).valueOf(1152921504606846976L)).valueOf(2862423051509815793L)).valueOf(6746640616477458432L)).valueOf(799006685782884121L)).valueOf(1638400000000000000L)).valueOf(3243919932521508681L)).valueOf(6221821273427820544L)).valueOf(504036361936467383L)).valueOf(876488338465357824L)).valueOf(1490116119384765625L)).valueOf(2481152873203736576L)).valueOf(4052555153018976267L)).valueOf(6502111422497947648L)).valueOf(353814783205469041L)).valueOf(531441000000000000L)).valueOf(787662783788549761L)).valueOf(1152921504606846976L)).valueOf(1667889514952984961L)).valueOf(2386420683693101056L)).valueOf(3379220508056640625L)).valueOf(4738381338321616896L) };
    digitsPerInt = new int[] { 
        0, 0, 30, 19, 15, 13, 11, 11, 10, 9, 
        9, 8, 8, 8, 8, 7, 7, 7, 7, 7, 
        7, 7, 6, 6, 6, 6, 6, 6, 6, 6, 
        6, 6, 6, 6, 6, 6, 5 };
    intRadix = new int[] { 
        0, 0, 1073741824, 1162261467, 1073741824, 1220703125, 362797056, 1977326743, 1073741824, 387420489, 
        1000000000, 214358881, 429981696, 815730721, 1475789056, 170859375, 268435456, 410338673, 612220032, 893871739, 
        1280000000, 1801088541, 113379904, 148035889, 191102976, 244140625, 308915776, 387420489, 481890304, 594823321, 
        729000000, 887503681, 1073741824, 1291467969, 1544804416, 1838265625, 60466176 };
    serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("signum", int.class), new ObjectStreamField("magnitude", byte[].class), new ObjectStreamField("bitCount", int.class), new ObjectStreamField("bitLength", int.class), new ObjectStreamField("firstNonzeroByteNum", int.class), new ObjectStreamField("lowestSetBit", int.class) };
  }
  
  private static class UnsafeHolder {
    private static final Unsafe unsafe;
    
    private static final long signumOffset;
    
    private static final long magOffset;
    
    static void putSign(BigInteger param1BigInteger, int param1Int) { unsafe.putIntVolatile(param1BigInteger, signumOffset, param1Int); }
    
    static void putMag(BigInteger param1BigInteger, int[] param1ArrayOfInt) { unsafe.putObjectVolatile(param1BigInteger, magOffset, param1ArrayOfInt); }
    
    static  {
      try {
        unsafe = Unsafe.getUnsafe();
        signumOffset = unsafe.objectFieldOffset(BigInteger.class.getDeclaredField("signum"));
        magOffset = unsafe.objectFieldOffset(BigInteger.class.getDeclaredField("mag"));
      } catch (Exception exception) {
        throw new ExceptionInInitializerError(exception);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\math\BigInteger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */