package jdk.internal.org.objectweb.asm.util;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public class CheckFieldAdapter extends FieldVisitor {
  private boolean end;
  
  public CheckFieldAdapter(FieldVisitor paramFieldVisitor) {
    this(327680, paramFieldVisitor);
    if (getClass() != CheckFieldAdapter.class)
      throw new IllegalStateException(); 
  }
  
  protected CheckFieldAdapter(int paramInt, FieldVisitor paramFieldVisitor) { super(paramInt, paramFieldVisitor); }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean) {
    checkEnd();
    CheckMethodAdapter.checkDesc(paramString, false);
    return new CheckAnnotationAdapter(super.visitAnnotation(paramString, paramBoolean));
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) {
    checkEnd();
    int i = paramInt >>> 24;
    if (i != 19)
      throw new IllegalArgumentException("Invalid type reference sort 0x" + Integer.toHexString(i)); 
    CheckClassAdapter.checkTypeRefAndPath(paramInt, paramTypePath);
    CheckMethodAdapter.checkDesc(paramString, false);
    return new CheckAnnotationAdapter(super.visitTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean));
  }
  
  public void visitAttribute(Attribute paramAttribute) {
    checkEnd();
    if (paramAttribute == null)
      throw new IllegalArgumentException("Invalid attribute (must not be null)"); 
    super.visitAttribute(paramAttribute);
  }
  
  public void visitEnd() {
    checkEnd();
    this.end = true;
    super.visitEnd();
  }
  
  private void checkEnd() {
    if (this.end)
      throw new IllegalStateException("Cannot call a visit method after visitEnd has been called"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\as\\util\CheckFieldAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */