package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class BitSet implements Cloneable, Serializable {
  private static final int ADDRESS_BITS_PER_WORD = 6;
  
  private static final int BITS_PER_WORD = 64;
  
  private static final int BIT_INDEX_MASK = 63;
  
  private static final long WORD_MASK = -1L;
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("bits", long[].class) };
  
  private long[] words;
  
  private int wordsInUse = 0;
  
  private boolean sizeIsSticky = false;
  
  private static final long serialVersionUID = 7997698588986878753L;
  
  private static int wordIndex(int paramInt) { return paramInt >> 6; }
  
  private void checkInvariants() {
    assert this.wordsInUse == 0 || this.words[this.wordsInUse - 1] != 0L;
    assert this.wordsInUse >= 0 && this.wordsInUse <= this.words.length;
    assert this.wordsInUse == this.words.length || this.words[this.wordsInUse] == 0L;
  }
  
  private void recalculateWordsInUse() {
    int i;
    for (i = this.wordsInUse - 1; i >= 0 && this.words[i] == 0L; i--);
    this.wordsInUse = i + 1;
  }
  
  public BitSet() {
    initWords(64);
    this.sizeIsSticky = false;
  }
  
  public BitSet(int paramInt) {
    if (paramInt < 0)
      throw new NegativeArraySizeException("nbits < 0: " + paramInt); 
    initWords(paramInt);
    this.sizeIsSticky = true;
  }
  
  private void initWords(int paramInt) { this.words = new long[wordIndex(paramInt - 1) + 1]; }
  
  private BitSet(long[] paramArrayOfLong) {
    this.words = paramArrayOfLong;
    this.wordsInUse = paramArrayOfLong.length;
    checkInvariants();
  }
  
  public static BitSet valueOf(long[] paramArrayOfLong) {
    int i;
    for (i = paramArrayOfLong.length; i > 0 && paramArrayOfLong[i - 1] == 0L; i--);
    return new BitSet(Arrays.copyOf(paramArrayOfLong, i));
  }
  
  public static BitSet valueOf(LongBuffer paramLongBuffer) {
    paramLongBuffer = paramLongBuffer.slice();
    int i;
    for (i = paramLongBuffer.remaining(); i > 0 && paramLongBuffer.get(i - 1) == 0L; i--);
    long[] arrayOfLong = new long[i];
    paramLongBuffer.get(arrayOfLong);
    return new BitSet(arrayOfLong);
  }
  
  public static BitSet valueOf(byte[] paramArrayOfByte) { return valueOf(ByteBuffer.wrap(paramArrayOfByte)); }
  
  public static BitSet valueOf(ByteBuffer paramByteBuffer) {
    paramByteBuffer = paramByteBuffer.slice().order(ByteOrder.LITTLE_ENDIAN);
    int i;
    for (i = paramByteBuffer.remaining(); i > 0 && paramByteBuffer.get(i - 1) == 0; i--);
    long[] arrayOfLong = new long[(i + 7) / 8];
    paramByteBuffer.limit(i);
    byte b1 = 0;
    while (paramByteBuffer.remaining() >= 8)
      arrayOfLong[b1++] = paramByteBuffer.getLong(); 
    int j = paramByteBuffer.remaining();
    for (byte b2 = 0; b2 < j; b2++)
      arrayOfLong[b1] = arrayOfLong[b1] | (paramByteBuffer.get() & 0xFFL) << 8 * b2; 
    return new BitSet(arrayOfLong);
  }
  
  public byte[] toByteArray() {
    int i = this.wordsInUse;
    if (i == 0)
      return new byte[0]; 
    int j = 8 * (i - 1);
    for (long l1 = this.words[i - 1]; l1 != 0L; l1 >>>= 8)
      j++; 
    byte[] arrayOfByte = new byte[j];
    ByteBuffer byteBuffer = ByteBuffer.wrap(arrayOfByte).order(ByteOrder.LITTLE_ENDIAN);
    for (byte b = 0; b < i - 1; b++)
      byteBuffer.putLong(this.words[b]); 
    long l2;
    for (l2 = this.words[i - 1]; l2 != 0L; l2 >>>= 8)
      byteBuffer.put((byte)(int)(l2 & 0xFFL)); 
    return arrayOfByte;
  }
  
  public long[] toLongArray() { return Arrays.copyOf(this.words, this.wordsInUse); }
  
  private void ensureCapacity(int paramInt) {
    if (this.words.length < paramInt) {
      int i = Math.max(2 * this.words.length, paramInt);
      this.words = Arrays.copyOf(this.words, i);
      this.sizeIsSticky = false;
    } 
  }
  
  private void expandTo(int paramInt) {
    int i = paramInt + 1;
    if (this.wordsInUse < i) {
      ensureCapacity(i);
      this.wordsInUse = i;
    } 
  }
  
  private static void checkRange(int paramInt1, int paramInt2) {
    if (paramInt1 < 0)
      throw new IndexOutOfBoundsException("fromIndex < 0: " + paramInt1); 
    if (paramInt2 < 0)
      throw new IndexOutOfBoundsException("toIndex < 0: " + paramInt2); 
    if (paramInt1 > paramInt2)
      throw new IndexOutOfBoundsException("fromIndex: " + paramInt1 + " > toIndex: " + paramInt2); 
  }
  
  public void flip(int paramInt) {
    if (paramInt < 0)
      throw new IndexOutOfBoundsException("bitIndex < 0: " + paramInt); 
    int i = wordIndex(paramInt);
    expandTo(i);
    this.words[i] = this.words[i] ^ 1L << paramInt;
    recalculateWordsInUse();
    checkInvariants();
  }
  
  public void flip(int paramInt1, int paramInt2) {
    checkRange(paramInt1, paramInt2);
    if (paramInt1 == paramInt2)
      return; 
    int i = wordIndex(paramInt1);
    int j = wordIndex(paramInt2 - 1);
    expandTo(j);
    long l1 = -1L << paramInt1;
    long l2 = -1L >>> -paramInt2;
    if (i == j) {
      this.words[i] = this.words[i] ^ l1 & l2;
    } else {
      this.words[i] = this.words[i] ^ l1;
      for (int k = i + 1; k < j; k++)
        this.words[k] = this.words[k] ^ 0xFFFFFFFFFFFFFFFFL; 
      this.words[j] = this.words[j] ^ l2;
    } 
    recalculateWordsInUse();
    checkInvariants();
  }
  
  public void set(int paramInt) {
    if (paramInt < 0)
      throw new IndexOutOfBoundsException("bitIndex < 0: " + paramInt); 
    int i = wordIndex(paramInt);
    expandTo(i);
    this.words[i] = this.words[i] | 1L << paramInt;
    checkInvariants();
  }
  
  public void set(int paramInt, boolean paramBoolean) {
    if (paramBoolean) {
      set(paramInt);
    } else {
      clear(paramInt);
    } 
  }
  
  public void set(int paramInt1, int paramInt2) {
    checkRange(paramInt1, paramInt2);
    if (paramInt1 == paramInt2)
      return; 
    int i = wordIndex(paramInt1);
    int j = wordIndex(paramInt2 - 1);
    expandTo(j);
    long l1 = -1L << paramInt1;
    long l2 = -1L >>> -paramInt2;
    if (i == j) {
      this.words[i] = this.words[i] | l1 & l2;
    } else {
      this.words[i] = this.words[i] | l1;
      for (int k = i + 1; k < j; k++)
        this.words[k] = -1L; 
      this.words[j] = this.words[j] | l2;
    } 
    checkInvariants();
  }
  
  public void set(int paramInt1, int paramInt2, boolean paramBoolean) {
    if (paramBoolean) {
      set(paramInt1, paramInt2);
    } else {
      clear(paramInt1, paramInt2);
    } 
  }
  
  public void clear(int paramInt) {
    if (paramInt < 0)
      throw new IndexOutOfBoundsException("bitIndex < 0: " + paramInt); 
    int i = wordIndex(paramInt);
    if (i >= this.wordsInUse)
      return; 
    this.words[i] = this.words[i] & (1L << paramInt ^ 0xFFFFFFFFFFFFFFFFL);
    recalculateWordsInUse();
    checkInvariants();
  }
  
  public void clear(int paramInt1, int paramInt2) {
    checkRange(paramInt1, paramInt2);
    if (paramInt1 == paramInt2)
      return; 
    int i = wordIndex(paramInt1);
    if (i >= this.wordsInUse)
      return; 
    int j = wordIndex(paramInt2 - 1);
    if (j >= this.wordsInUse) {
      paramInt2 = length();
      j = this.wordsInUse - 1;
    } 
    long l1 = -1L << paramInt1;
    long l2 = -1L >>> -paramInt2;
    if (i == j) {
      this.words[i] = this.words[i] & (l1 & l2 ^ 0xFFFFFFFFFFFFFFFFL);
    } else {
      this.words[i] = this.words[i] & (l1 ^ 0xFFFFFFFFFFFFFFFFL);
      for (int k = i + 1; k < j; k++)
        this.words[k] = 0L; 
      this.words[j] = this.words[j] & (l2 ^ 0xFFFFFFFFFFFFFFFFL);
    } 
    recalculateWordsInUse();
    checkInvariants();
  }
  
  public void clear() {
    while (this.wordsInUse > 0)
      this.words[--this.wordsInUse] = 0L; 
  }
  
  public boolean get(int paramInt) {
    if (paramInt < 0)
      throw new IndexOutOfBoundsException("bitIndex < 0: " + paramInt); 
    checkInvariants();
    int i = wordIndex(paramInt);
    return (i < this.wordsInUse && (this.words[i] & 1L << paramInt) != 0L);
  }
  
  public BitSet get(int paramInt1, int paramInt2) {
    checkRange(paramInt1, paramInt2);
    checkInvariants();
    int i = length();
    if (i <= paramInt1 || paramInt1 == paramInt2)
      return new BitSet(0); 
    if (paramInt2 > i)
      paramInt2 = i; 
    BitSet bitSet;
    int j = (bitSet = new BitSet(paramInt2 - paramInt1)).wordIndex(paramInt2 - paramInt1 - 1) + 1;
    int k = wordIndex(paramInt1);
    boolean bool = ((paramInt1 & 0x3F) == 0) ? 1 : 0;
    byte b = 0;
    while (b < j - 1) {
      bitSet.words[b] = bool ? this.words[k] : (this.words[k] >>> paramInt1 | this.words[k + 1] << -paramInt1);
      b++;
      k++;
    } 
    long l = -1L >>> -paramInt2;
    bitSet.words[j - 1] = ((paramInt2 - 1 & 0x3F) < (paramInt1 & 0x3F)) ? (this.words[k] >>> paramInt1 | (this.words[k + 1] & l) << -paramInt1) : ((this.words[k] & l) >>> paramInt1);
    bitSet.wordsInUse = j;
    bitSet.recalculateWordsInUse();
    bitSet.checkInvariants();
    return bitSet;
  }
  
  public int nextSetBit(int paramInt) {
    if (paramInt < 0)
      throw new IndexOutOfBoundsException("fromIndex < 0: " + paramInt); 
    checkInvariants();
    int i = wordIndex(paramInt);
    if (i >= this.wordsInUse)
      return -1; 
    for (long l = this.words[i] & -1L << paramInt;; l = this.words[i]) {
      if (l != 0L)
        return i * 64 + Long.numberOfTrailingZeros(l); 
      if (++i == this.wordsInUse)
        return -1; 
    } 
  }
  
  public int nextClearBit(int paramInt) {
    if (paramInt < 0)
      throw new IndexOutOfBoundsException("fromIndex < 0: " + paramInt); 
    checkInvariants();
    int i = wordIndex(paramInt);
    if (i >= this.wordsInUse)
      return paramInt; 
    for (long l = (this.words[i] ^ 0xFFFFFFFFFFFFFFFFL) & -1L << paramInt;; l = this.words[i] ^ 0xFFFFFFFFFFFFFFFFL) {
      if (l != 0L)
        return i * 64 + Long.numberOfTrailingZeros(l); 
      if (++i == this.wordsInUse)
        return this.wordsInUse * 64; 
    } 
  }
  
  public int previousSetBit(int paramInt) {
    if (paramInt < 0) {
      if (paramInt == -1)
        return -1; 
      throw new IndexOutOfBoundsException("fromIndex < -1: " + paramInt);
    } 
    checkInvariants();
    int i = wordIndex(paramInt);
    if (i >= this.wordsInUse)
      return length() - 1; 
    for (long l = this.words[i] & -1L >>> -(paramInt + 1);; l = this.words[i]) {
      if (l != 0L)
        return (i + 1) * 64 - 1 - Long.numberOfLeadingZeros(l); 
      if (i-- == 0)
        return -1; 
    } 
  }
  
  public int previousClearBit(int paramInt) {
    if (paramInt < 0) {
      if (paramInt == -1)
        return -1; 
      throw new IndexOutOfBoundsException("fromIndex < -1: " + paramInt);
    } 
    checkInvariants();
    int i = wordIndex(paramInt);
    if (i >= this.wordsInUse)
      return paramInt; 
    for (long l = (this.words[i] ^ 0xFFFFFFFFFFFFFFFFL) & -1L >>> -(paramInt + 1);; l = this.words[i] ^ 0xFFFFFFFFFFFFFFFFL) {
      if (l != 0L)
        return (i + 1) * 64 - 1 - Long.numberOfLeadingZeros(l); 
      if (i-- == 0)
        return -1; 
    } 
  }
  
  public int length() { return (this.wordsInUse == 0) ? 0 : (64 * (this.wordsInUse - 1) + 64 - Long.numberOfLeadingZeros(this.words[this.wordsInUse - 1])); }
  
  public boolean isEmpty() { return (this.wordsInUse == 0); }
  
  public boolean intersects(BitSet paramBitSet) {
    for (int i = Math.min(this.wordsInUse, paramBitSet.wordsInUse) - 1; i >= 0; i--) {
      if ((this.words[i] & paramBitSet.words[i]) != 0L)
        return true; 
    } 
    return false;
  }
  
  public int cardinality() {
    int i = 0;
    for (byte b = 0; b < this.wordsInUse; b++)
      i += Long.bitCount(this.words[b]); 
    return i;
  }
  
  public void and(BitSet paramBitSet) {
    if (this == paramBitSet)
      return; 
    while (this.wordsInUse > paramBitSet.wordsInUse)
      this.words[--this.wordsInUse] = 0L; 
    for (byte b = 0; b < this.wordsInUse; b++)
      this.words[b] = this.words[b] & paramBitSet.words[b]; 
    recalculateWordsInUse();
    checkInvariants();
  }
  
  public void or(BitSet paramBitSet) {
    if (this == paramBitSet)
      return; 
    int i = Math.min(this.wordsInUse, paramBitSet.wordsInUse);
    if (this.wordsInUse < paramBitSet.wordsInUse) {
      ensureCapacity(paramBitSet.wordsInUse);
      this.wordsInUse = paramBitSet.wordsInUse;
    } 
    for (byte b = 0; b < i; b++)
      this.words[b] = this.words[b] | paramBitSet.words[b]; 
    if (i < paramBitSet.wordsInUse)
      System.arraycopy(paramBitSet.words, i, this.words, i, this.wordsInUse - i); 
    checkInvariants();
  }
  
  public void xor(BitSet paramBitSet) {
    int i = Math.min(this.wordsInUse, paramBitSet.wordsInUse);
    if (this.wordsInUse < paramBitSet.wordsInUse) {
      ensureCapacity(paramBitSet.wordsInUse);
      this.wordsInUse = paramBitSet.wordsInUse;
    } 
    for (byte b = 0; b < i; b++)
      this.words[b] = this.words[b] ^ paramBitSet.words[b]; 
    if (i < paramBitSet.wordsInUse)
      System.arraycopy(paramBitSet.words, i, this.words, i, paramBitSet.wordsInUse - i); 
    recalculateWordsInUse();
    checkInvariants();
  }
  
  public void andNot(BitSet paramBitSet) {
    for (int i = Math.min(this.wordsInUse, paramBitSet.wordsInUse) - 1; i >= 0; i--)
      this.words[i] = this.words[i] & (paramBitSet.words[i] ^ 0xFFFFFFFFFFFFFFFFL); 
    recalculateWordsInUse();
    checkInvariants();
  }
  
  public int hashCode() {
    long l = 1234L;
    int i = this.wordsInUse;
    while (--i >= 0)
      l ^= this.words[i] * (i + 1); 
    return (int)(l >> 32 ^ l);
  }
  
  public int size() { return this.words.length * 64; }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof BitSet))
      return false; 
    if (this == paramObject)
      return true; 
    BitSet bitSet = (BitSet)paramObject;
    checkInvariants();
    bitSet.checkInvariants();
    if (this.wordsInUse != bitSet.wordsInUse)
      return false; 
    for (byte b = 0; b < this.wordsInUse; b++) {
      if (this.words[b] != bitSet.words[b])
        return false; 
    } 
    return true;
  }
  
  public Object clone() {
    if (!this.sizeIsSticky)
      trimToSize(); 
    try {
      BitSet bitSet = (BitSet)super.clone();
      bitSet.words = (long[])this.words.clone();
      bitSet.checkInvariants();
      return bitSet;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  private void trimToSize() {
    if (this.wordsInUse != this.words.length) {
      this.words = Arrays.copyOf(this.words, this.wordsInUse);
      checkInvariants();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    checkInvariants();
    if (!this.sizeIsSticky)
      trimToSize(); 
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("bits", this.words);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    this.words = (long[])getField.get("bits", null);
    this.wordsInUse = this.words.length;
    recalculateWordsInUse();
    this.sizeIsSticky = (this.words.length > 0 && this.words[this.words.length - 1] == 0L);
    checkInvariants();
  }
  
  public String toString() {
    checkInvariants();
    int i = (this.wordsInUse > 128) ? cardinality() : (this.wordsInUse * 64);
    StringBuilder stringBuilder = new StringBuilder(6 * i + 2);
    stringBuilder.append('{');
    int j = nextSetBit(0);
    if (j != -1) {
      stringBuilder.append(j);
      label20: while (++j >= 0 && (j = nextSetBit(j)) >= 0) {
        int k = nextClearBit(j);
        while (true) {
          stringBuilder.append(", ").append(j);
          if (++j == k)
            continue label20; 
        } 
      } 
    } 
    stringBuilder.append('}');
    return stringBuilder.toString();
  }
  
  public IntStream stream() {
    class BitSetIterator implements PrimitiveIterator.OfInt {
      int next = BitSet.this.nextSetBit(0);
      
      public boolean hasNext() { return (this.next != -1); }
      
      public int nextInt() {
        if (this.next != -1) {
          int i = this.next;
          this.next = BitSet.this.nextSetBit(this.next + 1);
          return i;
        } 
        throw new NoSuchElementException();
      }
    };
    return StreamSupport.intStream(() -> Spliterators.spliterator(new BitSetIterator(), cardinality(), 21), 16469, false);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\BitSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */