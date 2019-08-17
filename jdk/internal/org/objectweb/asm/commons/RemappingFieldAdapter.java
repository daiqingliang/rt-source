package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public class RemappingFieldAdapter extends FieldVisitor {
  private final Remapper remapper;
  
  public RemappingFieldAdapter(FieldVisitor paramFieldVisitor, Remapper paramRemapper) { this(327680, paramFieldVisitor, paramRemapper); }
  
  protected RemappingFieldAdapter(int paramInt, FieldVisitor paramFieldVisitor, Remapper paramRemapper) {
    super(paramInt, paramFieldVisitor);
    this.remapper = paramRemapper;
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean) {
    AnnotationVisitor annotationVisitor = this.fv.visitAnnotation(this.remapper.mapDesc(paramString), paramBoolean);
    return (annotationVisitor == null) ? null : new RemappingAnnotationAdapter(annotationVisitor, this.remapper);
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) {
    AnnotationVisitor annotationVisitor = super.visitTypeAnnotation(paramInt, paramTypePath, this.remapper.mapDesc(paramString), paramBoolean);
    return (annotationVisitor == null) ? null : new RemappingAnnotationAdapter(annotationVisitor, this.remapper);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\commons\RemappingFieldAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */