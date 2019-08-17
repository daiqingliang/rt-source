package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;

public class RemappingAnnotationAdapter extends AnnotationVisitor {
  protected final Remapper remapper;
  
  public RemappingAnnotationAdapter(AnnotationVisitor paramAnnotationVisitor, Remapper paramRemapper) { this(327680, paramAnnotationVisitor, paramRemapper); }
  
  protected RemappingAnnotationAdapter(int paramInt, AnnotationVisitor paramAnnotationVisitor, Remapper paramRemapper) {
    super(paramInt, paramAnnotationVisitor);
    this.remapper = paramRemapper;
  }
  
  public void visit(String paramString, Object paramObject) { this.av.visit(paramString, this.remapper.mapValue(paramObject)); }
  
  public void visitEnum(String paramString1, String paramString2, String paramString3) { this.av.visitEnum(paramString1, this.remapper.mapDesc(paramString2), paramString3); }
  
  public AnnotationVisitor visitAnnotation(String paramString1, String paramString2) {
    AnnotationVisitor annotationVisitor = this.av.visitAnnotation(paramString1, this.remapper.mapDesc(paramString2));
    return (annotationVisitor == null) ? null : ((annotationVisitor == this.av) ? this : new RemappingAnnotationAdapter(annotationVisitor, this.remapper));
  }
  
  public AnnotationVisitor visitArray(String paramString) {
    AnnotationVisitor annotationVisitor = this.av.visitArray(paramString);
    return (annotationVisitor == null) ? null : ((annotationVisitor == this.av) ? this : new RemappingAnnotationAdapter(annotationVisitor, this.remapper));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\commons\RemappingAnnotationAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */