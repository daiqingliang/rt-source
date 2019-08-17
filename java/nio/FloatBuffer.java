package java.nio;

public abstract class FloatBuffer extends Buffer implements Comparable<FloatBuffer> {
  final float[] hb;
  
  final int offset;
  
  boolean isReadOnly;
  
  FloatBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat, int paramInt5) {
    super(paramInt1, paramInt2, paramInt3, paramInt4);
    this.hb = paramArrayOfFloat;
    this.offset = paramInt5;
  }
  
  FloatBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this(paramInt1, paramInt2, paramInt3, paramInt4, null, 0); }
  
  public static FloatBuffer allocate(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    return new HeapFloatBuffer(paramInt, paramInt);
  }
  
  public static FloatBuffer wrap(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    try {
      return new HeapFloatBuffer(paramArrayOfFloat, paramInt1, paramInt2);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IndexOutOfBoundsException();
    } 
  }
  
  public static FloatBuffer wrap(float[] paramArrayOfFloat) { return wrap(paramArrayOfFloat, 0, paramArrayOfFloat.length); }
  
  public abstract FloatBuffer slice();
  
  public abstract FloatBuffer duplicate();
  
  public abstract FloatBuffer asReadOnlyBuffer();
  
  public abstract float get();
  
  public abstract FloatBuffer put(float paramFloat);
  
  public abstract float get(int paramInt);
  
  public abstract FloatBuffer put(int paramInt, float paramFloat);
  
  public FloatBuffer get(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfFloat.length);
    if (paramInt2 > remaining())
      throw new BufferUnderflowException(); 
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
      paramArrayOfFloat[j] = get(); 
    return this;
  }
  
  public FloatBuffer get(float[] paramArrayOfFloat) { return get(paramArrayOfFloat, 0, paramArrayOfFloat.length); }
  
  public FloatBuffer put(FloatBuffer paramFloatBuffer) {
    if (paramFloatBuffer == this)
      throw new IllegalArgumentException(); 
    if (isReadOnly())
      throw new ReadOnlyBufferException(); 
    int i = paramFloatBuffer.remaining();
    if (i > remaining())
      throw new BufferOverflowException(); 
    for (byte b = 0; b < i; b++)
      put(paramFloatBuffer.get()); 
    return this;
  }
  
  public FloatBuffer put(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    checkBounds(paramInt1, paramInt2, paramArrayOfFloat.length);
    if (paramInt2 > remaining())
      throw new BufferOverflowException(); 
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++)
      put(paramArrayOfFloat[j]); 
    return this;
  }
  
  public final FloatBuffer put(float[] paramArrayOfFloat) { return put(paramArrayOfFloat, 0, paramArrayOfFloat.length); }
  
  public final boolean hasArray() { return (this.hb != null && !this.isReadOnly); }
  
  public final float[] array() {
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
  
  public abstract FloatBuffer compact();
  
  public abstract boolean isDirect();
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(getClass().getName());
    stringBuffer.append("[pos=");
    stringBuffer.append(position());
    stringBuffer.append(" lim=");
    stringBuffer.append(limit());
    stringBuffer.append(" cap=");
    stringBuffer.append(capacity());
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  public int hashCode() {
    int i = 1;
    int j = position();
    for (int k = limit() - 1; k >= j; k--)
      i = 31 * i + (int)get(k); 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof FloatBuffer))
      return false; 
    FloatBuffer floatBuffer = (FloatBuffer)paramObject;
    if (remaining() != floatBuffer.remaining())
      return false; 
    int i = position();
    int j = limit() - 1;
    for (int k = floatBuffer.limit() - 1; j >= i; k--) {
      if (!equals(get(j), floatBuffer.get(k)))
        return false; 
      j--;
    } 
    return true;
  }
  
  private static boolean equals(float paramFloat1, float paramFloat2) { return (paramFloat1 == paramFloat2 || (Float.isNaN(paramFloat1) && Float.isNaN(paramFloat2))); }
  
  public int compareTo(FloatBuffer paramFloatBuffer) {
    int i = position() + Math.min(remaining(), paramFloatBuffer.remaining());
    int j = position();
    for (int k = paramFloatBuffer.position(); j < i; k++) {
      int m = compare(get(j), paramFloatBuffer.get(k));
      if (m != 0)
        return m; 
      j++;
    } 
    return remaining() - paramFloatBuffer.remaining();
  }
  
  private static int compare(float paramFloat1, float paramFloat2) { return (paramFloat1 < paramFloat2) ? -1 : ((paramFloat1 > paramFloat2) ? 1 : ((paramFloat1 == paramFloat2) ? 0 : (Float.isNaN(paramFloat1) ? (Float.isNaN(paramFloat2) ? 0 : 1) : -1))); }
  
  public abstract ByteOrder order();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\FloatBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */