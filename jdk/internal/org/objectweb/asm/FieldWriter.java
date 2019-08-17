package jdk.internal.org.objectweb.asm;

final class FieldWriter extends FieldVisitor {
  private final ClassWriter cw;
  
  private final int access;
  
  private final int name;
  
  private final int desc;
  
  private int signature;
  
  private int value;
  
  private AnnotationWriter anns;
  
  private AnnotationWriter ianns;
  
  private AnnotationWriter tanns;
  
  private AnnotationWriter itanns;
  
  private Attribute attrs;
  
  FieldWriter(ClassWriter paramClassWriter, int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject) {
    super(327680);
    if (paramClassWriter.firstField == null) {
      paramClassWriter.firstField = this;
    } else {
      paramClassWriter.lastField.fv = this;
    } 
    paramClassWriter.lastField = this;
    this.cw = paramClassWriter;
    this.access = paramInt;
    this.name = paramClassWriter.newUTF8(paramString1);
    this.desc = paramClassWriter.newUTF8(paramString2);
    if (paramString3 != null)
      this.signature = paramClassWriter.newUTF8(paramString3); 
    if (paramObject != null)
      this.value = (paramClassWriter.newConstItem(paramObject)).index; 
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean) {
    ByteVector byteVector = new ByteVector();
    byteVector.putShort(this.cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter annotationWriter = new AnnotationWriter(this.cw, true, byteVector, byteVector, 2);
    if (paramBoolean) {
      annotationWriter.next = this.anns;
      this.anns = annotationWriter;
    } else {
      annotationWriter.next = this.ianns;
      this.ianns = annotationWriter;
    } 
    return annotationWriter;
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) {
    ByteVector byteVector = new ByteVector();
    AnnotationWriter.putTarget(paramInt, paramTypePath, byteVector);
    byteVector.putShort(this.cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter annotationWriter = new AnnotationWriter(this.cw, true, byteVector, byteVector, byteVector.length - 2);
    if (paramBoolean) {
      annotationWriter.next = this.tanns;
      this.tanns = annotationWriter;
    } else {
      annotationWriter.next = this.itanns;
      this.itanns = annotationWriter;
    } 
    return annotationWriter;
  }
  
  public void visitAttribute(Attribute paramAttribute) {
    paramAttribute.next = this.attrs;
    this.attrs = paramAttribute;
  }
  
  public void visitEnd() {}
  
  int getSize() {
    int i = 8;
    if (this.value != 0) {
      this.cw.newUTF8("ConstantValue");
      i += 8;
    } 
    if ((this.access & 0x1000) != 0 && ((this.cw.version & 0xFFFF) < 49 || (this.access & 0x40000) != 0)) {
      this.cw.newUTF8("Synthetic");
      i += 6;
    } 
    if ((this.access & 0x20000) != 0) {
      this.cw.newUTF8("Deprecated");
      i += 6;
    } 
    if (this.signature != 0) {
      this.cw.newUTF8("Signature");
      i += 8;
    } 
    if (this.anns != null) {
      this.cw.newUTF8("RuntimeVisibleAnnotations");
      i += 8 + this.anns.getSize();
    } 
    if (this.ianns != null) {
      this.cw.newUTF8("RuntimeInvisibleAnnotations");
      i += 8 + this.ianns.getSize();
    } 
    if (this.tanns != null) {
      this.cw.newUTF8("RuntimeVisibleTypeAnnotations");
      i += 8 + this.tanns.getSize();
    } 
    if (this.itanns != null) {
      this.cw.newUTF8("RuntimeInvisibleTypeAnnotations");
      i += 8 + this.itanns.getSize();
    } 
    if (this.attrs != null)
      i += this.attrs.getSize(this.cw, null, 0, -1, -1); 
    return i;
  }
  
  void put(ByteVector paramByteVector) {
    int i = 0x60000 | (this.access & 0x40000) / 64;
    paramByteVector.putShort(this.access & (i ^ 0xFFFFFFFF)).putShort(this.name).putShort(this.desc);
    int j = 0;
    if (this.value != 0)
      j++; 
    if ((this.access & 0x1000) != 0 && ((this.cw.version & 0xFFFF) < 49 || (this.access & 0x40000) != 0))
      j++; 
    if ((this.access & 0x20000) != 0)
      j++; 
    if (this.signature != 0)
      j++; 
    if (this.anns != null)
      j++; 
    if (this.ianns != null)
      j++; 
    if (this.tanns != null)
      j++; 
    if (this.itanns != null)
      j++; 
    if (this.attrs != null)
      j += this.attrs.getCount(); 
    paramByteVector.putShort(j);
    if (this.value != 0) {
      paramByteVector.putShort(this.cw.newUTF8("ConstantValue"));
      paramByteVector.putInt(2).putShort(this.value);
    } 
    if ((this.access & 0x1000) != 0 && ((this.cw.version & 0xFFFF) < 49 || (this.access & 0x40000) != 0))
      paramByteVector.putShort(this.cw.newUTF8("Synthetic")).putInt(0); 
    if ((this.access & 0x20000) != 0)
      paramByteVector.putShort(this.cw.newUTF8("Deprecated")).putInt(0); 
    if (this.signature != 0) {
      paramByteVector.putShort(this.cw.newUTF8("Signature"));
      paramByteVector.putInt(2).putShort(this.signature);
    } 
    if (this.anns != null) {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeVisibleAnnotations"));
      this.anns.put(paramByteVector);
    } 
    if (this.ianns != null) {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeInvisibleAnnotations"));
      this.ianns.put(paramByteVector);
    } 
    if (this.tanns != null) {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeVisibleTypeAnnotations"));
      this.tanns.put(paramByteVector);
    } 
    if (this.itanns != null) {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeInvisibleTypeAnnotations"));
      this.itanns.put(paramByteVector);
    } 
    if (this.attrs != null)
      this.attrs.put(this.cw, null, 0, -1, -1, paramByteVector); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\FieldWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */