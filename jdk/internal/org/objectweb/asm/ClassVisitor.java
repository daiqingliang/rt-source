package jdk.internal.org.objectweb.asm;

public abstract class ClassVisitor {
  protected final int api;
  
  protected ClassVisitor cv;
  
  public ClassVisitor(int paramInt) { this(paramInt, null); }
  
  public ClassVisitor(int paramInt, ClassVisitor paramClassVisitor) {
    if (paramInt != 262144 && paramInt != 327680)
      throw new IllegalArgumentException(); 
    this.api = paramInt;
    this.cv = paramClassVisitor;
  }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    if (this.cv != null)
      this.cv.visit(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString); 
  }
  
  public void visitSource(String paramString1, String paramString2) {
    if (this.cv != null)
      this.cv.visitSource(paramString1, paramString2); 
  }
  
  public void visitOuterClass(String paramString1, String paramString2, String paramString3) {
    if (this.cv != null)
      this.cv.visitOuterClass(paramString1, paramString2, paramString3); 
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean) { return (this.cv != null) ? this.cv.visitAnnotation(paramString, paramBoolean) : null; }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) {
    if (this.api < 327680)
      throw new RuntimeException(); 
    return (this.cv != null) ? this.cv.visitTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean) : null;
  }
  
  public void visitAttribute(Attribute paramAttribute) {
    if (this.cv != null)
      this.cv.visitAttribute(paramAttribute); 
  }
  
  public void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt) {
    if (this.cv != null)
      this.cv.visitInnerClass(paramString1, paramString2, paramString3, paramInt); 
  }
  
  public FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject) { return (this.cv != null) ? this.cv.visitField(paramInt, paramString1, paramString2, paramString3, paramObject) : null; }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) { return (this.cv != null) ? this.cv.visitMethod(paramInt, paramString1, paramString2, paramString3, paramArrayOfString) : null; }
  
  public void visitEnd() {
    if (this.cv != null)
      this.cv.visitEnd(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\ClassVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */