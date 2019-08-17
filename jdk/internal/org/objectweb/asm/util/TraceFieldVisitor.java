package jdk.internal.org.objectweb.asm.util;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public final class TraceFieldVisitor extends FieldVisitor {
  public final Printer p;
  
  public TraceFieldVisitor(Printer paramPrinter) { this(null, paramPrinter); }
  
  public TraceFieldVisitor(FieldVisitor paramFieldVisitor, Printer paramPrinter) {
    super(327680, paramFieldVisitor);
    this.p = paramPrinter;
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean) {
    Printer printer = this.p.visitFieldAnnotation(paramString, paramBoolean);
    AnnotationVisitor annotationVisitor = (this.fv == null) ? null : this.fv.visitAnnotation(paramString, paramBoolean);
    return new TraceAnnotationVisitor(annotationVisitor, printer);
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) {
    Printer printer = this.p.visitFieldTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    AnnotationVisitor annotationVisitor = (this.fv == null) ? null : this.fv.visitTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    return new TraceAnnotationVisitor(annotationVisitor, printer);
  }
  
  public void visitAttribute(Attribute paramAttribute) {
    this.p.visitFieldAttribute(paramAttribute);
    super.visitAttribute(paramAttribute);
  }
  
  public void visitEnd() {
    this.p.visitFieldEnd();
    super.visitEnd();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\as\\util\TraceFieldVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */