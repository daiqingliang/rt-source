package jdk.internal.org.objectweb.asm;

public abstract class AnnotationVisitor {
  protected final int api;
  
  protected AnnotationVisitor av;
  
  public AnnotationVisitor(int paramInt) { this(paramInt, null); }
  
  public AnnotationVisitor(int paramInt, AnnotationVisitor paramAnnotationVisitor) {
    if (paramInt != 262144 && paramInt != 327680)
      throw new IllegalArgumentException(); 
    this.api = paramInt;
    this.av = paramAnnotationVisitor;
  }
  
  public void visit(String paramString, Object paramObject) {
    if (this.av != null)
      this.av.visit(paramString, paramObject); 
  }
  
  public void visitEnum(String paramString1, String paramString2, String paramString3) {
    if (this.av != null)
      this.av.visitEnum(paramString1, paramString2, paramString3); 
  }
  
  public AnnotationVisitor visitAnnotation(String paramString1, String paramString2) { return (this.av != null) ? this.av.visitAnnotation(paramString1, paramString2) : null; }
  
  public AnnotationVisitor visitArray(String paramString) { return (this.av != null) ? this.av.visitArray(paramString) : null; }
  
  public void visitEnd() {
    if (this.av != null)
      this.av.visitEnd(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\AnnotationVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */