package java.nio;

import java.io.IOException;
import java.util.Spliterator;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public abstract class CharBuffer extends Buffer implements Comparable<CharBuffer>, Appendable, CharSequence, Readable {
  final char[] hb;
  
  final int offset;
  
  boolean isReadOnly;
  
  CharBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, char[] paramArrayOfChar, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.hb = paramArrayOfChar;
    this.offset = paramInt5;
  }
  
  CharBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this(paramInt1, paramInt2, paramInt3, paramInt4, null, 0); }
  
  public static CharBuffer allocate(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    return new HeapCharBuffer(paramInt, paramInt);
  }
  
  public static CharBuffer wrap(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    try {
      return new HeapCharBuffer(paramArrayOfChar, paramInt1, paramInt2);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IndexOutOfBoundsException();
    } 
  }
  
  public static CharBuffer wrap(char[] paramArrayOfChar) { return wrap(paramArrayOfChar, 0, paramArrayOfChar.length); }
  
  public int read(CharBuffer paramCharBuffer) throws IOException {
    int i = paramCharBuffer.remaining();
    int j = remaining();
    if (j == 0)
      return -1; 
    int k = Math.min(j, i);
    m = limit();
    if (i < j)
      limit(position() + k); 
    try {
      if (k > 0)
        paramCharBuffer.put(this); 
    } finally {
      limit(m);
    } 
    return k;
  }
  
  public static CharBuffer wrap(CharSequence paramCharSequence, int paramInt1, int paramInt2) {
    try {
      return new StringCharBuffer(paramCharSequence, paramInt1, paramInt2);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IndexOutOfBoundsException();
    } 
  }
  
  public static CharBuffer wrap(CharSequence paramCharSequence) { return wrap(paramCharSequence, 0, paramCharSequence.length()); }
  
  public abstract CharBuffer slice();
  
  public abstract CharBuffer duplicate();
  
  public abstract CharBuffer asReadOnlyBuffer();
  
  public abstract char get();
  
  public abstract CharBuffer put(char paramChar);
  
  public abstract char get(int paramInt);
  
  abstract char getUnchecked(int paramInt);
  
  public abstract CharBuffer put(int paramInt, char paramChar);
  
  public CharBuffer get(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfChar.length);
    if (paramInt2 > remaining())
      throw new BufferUnderflowException(); 
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
      paramArrayOfChar[j] = get(); 
    return this;
  }
  
  public CharBuffer get(char[] paramArrayOfChar) { return get(paramArrayOfChar, 0, paramArrayOfChar.length); }
  
  public CharBuffer put(CharBuffer paramCharBuffer) {
    if (paramCharBuffer == this)
      throw new IllegalArgumentException(); 
    if (isReadOnly())
      throw new ReadOnlyBufferException(); 
    int i = paramCharBuffer.remaining();
    if (i > remaining())
      throw new BufferOverflowException(); 
    for (byte b = 0; b < i; b++)
      put(paramCharBuffer.get()); 
    return this;
  }
  
  public CharBuffer put(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfChar.length);
    if (paramInt2 > remaining())
      throw new BufferOverflowException(); 
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
      put(paramArrayOfChar[j]); 
    return this;
  }
  
  public final CharBuffer put(char[] paramArrayOfChar) { return put(paramArrayOfChar, 0, paramArrayOfChar.length); }
  
  public CharBuffer put(String paramString, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2 - paramInt1, paramString.length());
    if (isReadOnly())
      throw new ReadOnlyBufferException(); 
    if (paramInt2 - paramInt1 > remaining())
      throw new BufferOverflowException(); 
    for (int i = paramInt1; i < paramInt2; i++)
      put(paramString.charAt(i)); 
    return this;
  }
  
  public final CharBuffer put(String paramString) { return put(paramString, 0, paramString.length()); }
  
  public final boolean hasArray() { return (this.hb != null && !this.isReadOnly); }
  
  public final char[] array() {
    if (this.hb == null)
      throw new UnsupportedOperationException(); 
    if (this.isReadOnly)
      throw new ReadOnlyBufferException(); 
    return this.hb;
  }
  
  public final int arrayOffset() {
    if (this.hb == null)
      throw new UnsupportedOperationException(); 
    if (this.isReadOnly)
      throw new ReadOnlyBufferException(); 
    return this.offset;
  }
  
  public abstract CharBuffer compact();
  
  public abstract boolean isDirect();
  
  public int hashCode() {
    char c = '\001';
    int i = position();
    for (int j = limit() - 1; j >= i; j--)
      c = 31 * c + get(j); 
    return c;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof CharBuffer))
      return false; 
    CharBuffer charBuffer = (CharBuffer)paramObject;
    if (remaining() != charBuffer.remaining())
      return false; 
    int i = position();
    int j = limit() - 1;
    for (int k = charBuffer.limit() - 1; j >= i; k--) {
      if (!equals(get(j), charBuffer.get(k)))
        return false; 
      j--;
    } 
    return true;
  }
  
  private static boolean equals(char paramChar1, char paramChar2) { return (paramChar1 == paramChar2); }
  
  public int compareTo(CharBuffer paramCharBuffer) throws IOException {
    int i = position() + Math.min(remaining(), paramCharBuffer.remaining());
    int j = position();
    for (int k = paramCharBuffer.position(); j < i; k++) {
      int m = compare(get(j), paramCharBuffer.get(k));
      if (m != 0)
        return m; 
      j++;
    } 
    return remaining() - paramCharBuffer.remaining();
  }
  
  private static int compare(char paramChar1, char paramChar2) { return Character.compare(paramChar1, paramChar2); }
  
  public String toString() { return toString(position(), limit()); }
  
  abstract String toString(int paramInt1, int paramInt2);
  
  public final int length() { return remaining(); }
  
  public final char charAt(int paramInt) { return get(position() + checkIndex(paramInt, 1)); }
  
  public abstract CharBuffer subSequence(int paramInt1, int paramInt2);
  
  public CharBuffer append(CharSequence paramCharSequence) { return (paramCharSequence == null) ? put("null") : put(paramCharSequence.toString()); }
  
  public CharBuffer append(CharSequence paramCharSequence, int paramInt1, int paramInt2) {
    String str = (paramCharSequence == null) ? "null" : paramCharSequence;
    return put(str.subSequence(paramInt1, paramInt2).toString());
  }
  
  public CharBuffer append(char paramChar) { return put(paramChar); }
  
  public abstract ByteOrder order();
  
  public IntStream chars() { return StreamSupport.intStream(() -> new CharBufferSpliterator(this), 16464, false); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\CharBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */