package com.sun.xml.internal.ws.org.objectweb.asm;

final class FieldWriter implements FieldVisitor {
  FieldWriter next;
  
  private final ClassWriter cw;
  
  private final int access;
  
  private final int name;
  
  private final int desc;
  
  private int signature;
  
  private int value;
  
  private AnnotationWriter anns;
  
  private AnnotationWriter ianns;
  
  private Attribute attrs;
  
  FieldWriter(ClassWriter paramClassWriter, int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject) {
    if (paramClassWriter.firstField == null) {
      paramClassWriter.firstField = this;
    } else {
      paramClassWriter.lastField.next = this;
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
    if ((this.access & 0x1000) != 0 && (this.cw.version & 0xFFFF) < 49) {
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
    if (this.attrs != null)
      i += this.attrs.getSize(this.cw, null, 0, -1, -1); 
    return i;
  }
  
  void put(ByteVector paramByteVector) {
    paramByteVector.putShort(this.access).putShort(this.name).putShort(this.desc);
    int i = 0;
    if (this.value != 0)
      i++; 
    if ((this.access & 0x1000) != 0 && (this.cw.version & 0xFFFF) < 49)
      i++; 
    if ((this.access & 0x20000) != 0)
      i++; 
    if (this.signature != 0)
      i++; 
    if (this.anns != null)
      i++; 
    if (this.ianns != null)
      i++; 
    if (this.attrs != null)
      i += this.attrs.getCount(); 
    paramByteVector.putShort(i);
    if (this.value != 0) {
      paramByteVector.putShort(this.cw.newUTF8("ConstantValue"));
      paramByteVector.putInt(2).putShort(this.value);
    } 
    if ((this.access & 0x1000) != 0 && (this.cw.version & 0xFFFF) < 49)
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
    if (this.attrs != null)
      this.attrs.put(this.cw, null, 0, -1, -1, paramByteVector); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\FieldWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */