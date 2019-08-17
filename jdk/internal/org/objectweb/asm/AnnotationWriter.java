package jdk.internal.org.objectweb.asm;

final class AnnotationWriter extends AnnotationVisitor {
  private final ClassWriter cw;
  
  private int size;
  
  private final boolean named;
  
  private final ByteVector bv;
  
  private final ByteVector parent;
  
  private final int offset;
  
  AnnotationWriter next;
  
  AnnotationWriter prev;
  
  AnnotationWriter(ClassWriter paramClassWriter, boolean paramBoolean, ByteVector paramByteVector1, ByteVector paramByteVector2, int paramInt) {
    super(327680);
    this.cw = paramClassWriter;
    this.named = paramBoolean;
    this.bv = paramByteVector1;
    this.parent = paramByteVector2;
    this.offset = paramInt;
  }
  
  public void visit(String paramString, Object paramObject) {
    this.size++;
    if (this.named)
      this.bv.putShort(this.cw.newUTF8(paramString)); 
    if (paramObject instanceof String) {
      this.bv.put12(115, this.cw.newUTF8((String)paramObject));
    } else if (paramObject instanceof Byte) {
      this.bv.put12(66, (this.cw.newInteger(((Byte)paramObject).byteValue())).index);
    } else if (paramObject instanceof Boolean) {
      byte b = ((Boolean)paramObject).booleanValue() ? 1 : 0;
      this.bv.put12(90, (this.cw.newInteger(b)).index);
    } else if (paramObject instanceof Character) {
      this.bv.put12(67, (this.cw.newInteger(((Character)paramObject).charValue())).index);
    } else if (paramObject instanceof Short) {
      this.bv.put12(83, (this.cw.newInteger(((Short)paramObject).shortValue())).index);
    } else if (paramObject instanceof Type) {
      this.bv.put12(99, this.cw.newUTF8(((Type)paramObject).getDescriptor()));
    } else if (paramObject instanceof byte[]) {
      byte[] arrayOfByte = (byte[])paramObject;
      this.bv.put12(91, arrayOfByte.length);
      for (byte b = 0; b < arrayOfByte.length; b++)
        this.bv.put12(66, (this.cw.newInteger(arrayOfByte[b])).index); 
    } else if (paramObject instanceof boolean[]) {
      boolean[] arrayOfBoolean = (boolean[])paramObject;
      this.bv.put12(91, arrayOfBoolean.length);
      for (byte b = 0; b < arrayOfBoolean.length; b++)
        this.bv.put12(90, (this.cw.newInteger(arrayOfBoolean[b] ? 1 : 0)).index); 
    } else if (paramObject instanceof short[]) {
      short[] arrayOfShort = (short[])paramObject;
      this.bv.put12(91, arrayOfShort.length);
      for (byte b = 0; b < arrayOfShort.length; b++)
        this.bv.put12(83, (this.cw.newInteger(arrayOfShort[b])).index); 
    } else if (paramObject instanceof char[]) {
      char[] arrayOfChar = (char[])paramObject;
      this.bv.put12(91, arrayOfChar.length);
      for (byte b = 0; b < arrayOfChar.length; b++)
        this.bv.put12(67, (this.cw.newInteger(arrayOfChar[b])).index); 
    } else if (paramObject instanceof int[]) {
      int[] arrayOfInt = (int[])paramObject;
      this.bv.put12(91, arrayOfInt.length);
      for (byte b = 0; b < arrayOfInt.length; b++)
        this.bv.put12(73, (this.cw.newInteger(arrayOfInt[b])).index); 
    } else if (paramObject instanceof long[]) {
      long[] arrayOfLong = (long[])paramObject;
      this.bv.put12(91, arrayOfLong.length);
      for (byte b = 0; b < arrayOfLong.length; b++)
        this.bv.put12(74, (this.cw.newLong(arrayOfLong[b])).index); 
    } else if (paramObject instanceof float[]) {
      float[] arrayOfFloat = (float[])paramObject;
      this.bv.put12(91, arrayOfFloat.length);
      for (byte b = 0; b < arrayOfFloat.length; b++)
        this.bv.put12(70, (this.cw.newFloat(arrayOfFloat[b])).index); 
    } else if (paramObject instanceof double[]) {
      double[] arrayOfDouble = (double[])paramObject;
      this.bv.put12(91, arrayOfDouble.length);
      for (byte b = 0; b < arrayOfDouble.length; b++)
        this.bv.put12(68, (this.cw.newDouble(arrayOfDouble[b])).index); 
    } else {
      Item item = this.cw.newConstItem(paramObject);
      this.bv.put12(".s.IFJDCS".charAt(item.type), item.index);
    } 
  }
  
  public void visitEnum(String paramString1, String paramString2, String paramString3) {
    this.size++;
    if (this.named)
      this.bv.putShort(this.cw.newUTF8(paramString1)); 
    this.bv.put12(101, this.cw.newUTF8(paramString2)).putShort(this.cw.newUTF8(paramString3));
  }
  
  public AnnotationVisitor visitAnnotation(String paramString1, String paramString2) {
    this.size++;
    if (this.named)
      this.bv.putShort(this.cw.newUTF8(paramString1)); 
    this.bv.put12(64, this.cw.newUTF8(paramString2)).putShort(0);
    return new AnnotationWriter(this.cw, true, this.bv, this.bv, this.bv.length - 2);
  }
  
  public AnnotationVisitor visitArray(String paramString) {
    this.size++;
    if (this.named)
      this.bv.putShort(this.cw.newUTF8(paramString)); 
    this.bv.put12(91, 0);
    return new AnnotationWriter(this.cw, false, this.bv, this.bv, this.bv.length - 2);
  }
  
  public void visitEnd() {
    if (this.parent != null) {
      byte[] arrayOfByte = this.parent.data;
      arrayOfByte[this.offset] = (byte)(this.size >>> 8);
      arrayOfByte[this.offset + 1] = (byte)this.size;
    } 
  }
  
  int getSize() {
    int i = 0;
    for (AnnotationWriter annotationWriter = this; annotationWriter != null; annotationWriter = annotationWriter.next)
      i += annotationWriter.bv.length; 
    return i;
  }
  
  void put(ByteVector paramByteVector) {
    byte b = 0;
    int i = 2;
    AnnotationWriter annotationWriter1 = this;
    AnnotationWriter annotationWriter2 = null;
    while (annotationWriter1 != null) {
      b++;
      i += annotationWriter1.bv.length;
      annotationWriter1.visitEnd();
      annotationWriter1.prev = annotationWriter2;
      annotationWriter2 = annotationWriter1;
      annotationWriter1 = annotationWriter1.next;
    } 
    paramByteVector.putInt(i);
    paramByteVector.putShort(b);
    for (annotationWriter1 = annotationWriter2; annotationWriter1 != null; annotationWriter1 = annotationWriter1.prev)
      paramByteVector.putByteArray(annotationWriter1.bv.data, 0, annotationWriter1.bv.length); 
  }
  
  static void put(AnnotationWriter[] paramArrayOfAnnotationWriter, int paramInt, ByteVector paramByteVector) {
    int i = 1 + 2 * (paramArrayOfAnnotationWriter.length - paramInt);
    int j;
    for (j = paramInt; j < paramArrayOfAnnotationWriter.length; j++)
      i += ((paramArrayOfAnnotationWriter[j] == null) ? 0 : paramArrayOfAnnotationWriter[j].getSize()); 
    paramByteVector.putInt(i).putByte(paramArrayOfAnnotationWriter.length - paramInt);
    for (j = paramInt; j < paramArrayOfAnnotationWriter.length; j++) {
      AnnotationWriter annotationWriter1 = paramArrayOfAnnotationWriter[j];
      AnnotationWriter annotationWriter2 = null;
      byte b = 0;
      while (annotationWriter1 != null) {
        b++;
        annotationWriter1.visitEnd();
        annotationWriter1.prev = annotationWriter2;
        annotationWriter2 = annotationWriter1;
        annotationWriter1 = annotationWriter1.next;
      } 
      paramByteVector.putShort(b);
      for (annotationWriter1 = annotationWriter2; annotationWriter1 != null; annotationWriter1 = annotationWriter1.prev)
        paramByteVector.putByteArray(annotationWriter1.bv.data, 0, annotationWriter1.bv.length); 
    } 
  }
  
  static void putTarget(int paramInt, TypePath paramTypePath, ByteVector paramByteVector) {
    switch (paramInt >>> 24) {
      case 0:
      case 1:
      case 22:
        paramByteVector.putShort(paramInt >>> 16);
        break;
      case 19:
      case 20:
      case 21:
        paramByteVector.putByte(paramInt >>> 24);
        break;
      case 71:
      case 72:
      case 73:
      case 74:
      case 75:
        paramByteVector.putInt(paramInt);
        break;
      default:
        paramByteVector.put12(paramInt >>> 24, (paramInt & 0xFFFF00) >> 8);
        break;
    } 
    if (paramTypePath == null) {
      paramByteVector.putByte(0);
    } else {
      byte b = paramTypePath.b[paramTypePath.offset] * 2 + 1;
      paramByteVector.putByteArray(paramTypePath.b, paramTypePath.offset, b);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\AnnotationWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */