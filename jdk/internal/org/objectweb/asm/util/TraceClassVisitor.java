package jdk.internal.org.objectweb.asm.util;

import java.io.PrintWriter;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public final class TraceClassVisitor extends ClassVisitor {
  private final PrintWriter pw;
  
  public final Printer p;
  
  public TraceClassVisitor(PrintWriter paramPrintWriter) { this(null, paramPrintWriter); }
  
  public TraceClassVisitor(ClassVisitor paramClassVisitor, PrintWriter paramPrintWriter) { this(paramClassVisitor, new Textifier(), paramPrintWriter); }
  
  public TraceClassVisitor(ClassVisitor paramClassVisitor, Printer paramPrinter, PrintWriter paramPrintWriter) {
    super(327680, paramClassVisitor);
    this.pw = paramPrintWriter;
    this.p = paramPrinter;
  }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    this.p.visit(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
    super.visit(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
  }
  
  public void visitSource(String paramString1, String paramString2) {
    this.p.visitSource(paramString1, paramString2);
    super.visitSource(paramString1, paramString2);
  }
  
  public void visitOuterClass(String paramString1, String paramString2, String paramString3) {
    this.p.visitOuterClass(paramString1, paramString2, paramString3);
    super.visitOuterClass(paramString1, paramString2, paramString3);
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean) {
    Printer printer = this.p.visitClassAnnotation(paramString, paramBoolean);
    AnnotationVisitor annotationVisitor = (this.cv == null) ? null : this.cv.visitAnnotation(paramString, paramBoolean);
    return new TraceAnnotationVisitor(annotationVisitor, printer);
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) {
    Printer printer = this.p.visitClassTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    AnnotationVisitor annotationVisitor = (this.cv == null) ? null : this.cv.visitTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    return new TraceAnnotationVisitor(annotationVisitor, printer);
  }
  
  public void visitAttribute(Attribute paramAttribute) {
    this.p.visitClassAttribute(paramAttribute);
    super.visitAttribute(paramAttribute);
  }
  
  public void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt) {
    this.p.visitInnerClass(paramString1, paramString2, paramString3, paramInt);
    super.visitInnerClass(paramString1, paramString2, paramString3, paramInt);
  }
  
  public FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject) {
    Printer printer = this.p.visitField(paramInt, paramString1, paramString2, paramString3, paramObject);
    FieldVisitor fieldVisitor = (this.cv == null) ? null : this.cv.visitField(paramInt, paramString1, paramString2, paramString3, paramObject);
    return new TraceFieldVisitor(fieldVisitor, printer);
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    Printer printer = this.p.visitMethod(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
    MethodVisitor methodVisitor = (this.cv == null) ? null : this.cv.visitMethod(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
    return new TraceMethodVisitor(methodVisitor, printer);
  }
  
  public void visitEnd() {
    this.p.visitClassEnd();
    if (this.pw != null) {
      this.p.print(this.pw);
      this.pw.flush();
    } 
    super.visitEnd();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\as\\util\TraceClassVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */