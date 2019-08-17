package jdk.internal.org.objectweb.asm.util;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Attribute;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public final class TraceMethodVisitor extends MethodVisitor {
  public final Printer p;
  
  public TraceMethodVisitor(Printer paramPrinter) { this(null, paramPrinter); }
  
  public TraceMethodVisitor(MethodVisitor paramMethodVisitor, Printer paramPrinter) {
    super(327680, paramMethodVisitor);
    this.p = paramPrinter;
  }
  
  public void visitParameter(String paramString, int paramInt) {
    this.p.visitParameter(paramString, paramInt);
    super.visitParameter(paramString, paramInt);
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean) {
    Printer printer = this.p.visitMethodAnnotation(paramString, paramBoolean);
    AnnotationVisitor annotationVisitor = (this.mv == null) ? null : this.mv.visitAnnotation(paramString, paramBoolean);
    return new TraceAnnotationVisitor(annotationVisitor, printer);
  }
  
  public AnnotationVisitor visitTypeAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) {
    Printer printer = this.p.visitMethodTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    AnnotationVisitor annotationVisitor = (this.mv == null) ? null : this.mv.visitTypeAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    return new TraceAnnotationVisitor(annotationVisitor, printer);
  }
  
  public void visitAttribute(Attribute paramAttribute) {
    this.p.visitMethodAttribute(paramAttribute);
    super.visitAttribute(paramAttribute);
  }
  
  public AnnotationVisitor visitAnnotationDefault() {
    Printer printer = this.p.visitAnnotationDefault();
    AnnotationVisitor annotationVisitor = (this.mv == null) ? null : this.mv.visitAnnotationDefault();
    return new TraceAnnotationVisitor(annotationVisitor, printer);
  }
  
  public AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean) {
    Printer printer = this.p.visitParameterAnnotation(paramInt, paramString, paramBoolean);
    AnnotationVisitor annotationVisitor = (this.mv == null) ? null : this.mv.visitParameterAnnotation(paramInt, paramString, paramBoolean);
    return new TraceAnnotationVisitor(annotationVisitor, printer);
  }
  
  public void visitCode() {
    this.p.visitCode();
    super.visitCode();
  }
  
  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2) {
    this.p.visitFrame(paramInt1, paramInt2, paramArrayOfObject1, paramInt3, paramArrayOfObject2);
    super.visitFrame(paramInt1, paramInt2, paramArrayOfObject1, paramInt3, paramArrayOfObject2);
  }
  
  public void visitInsn(int paramInt) {
    this.p.visitInsn(paramInt);
    super.visitInsn(paramInt);
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2) {
    this.p.visitIntInsn(paramInt1, paramInt2);
    super.visitIntInsn(paramInt1, paramInt2);
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2) {
    this.p.visitVarInsn(paramInt1, paramInt2);
    super.visitVarInsn(paramInt1, paramInt2);
  }
  
  public void visitTypeInsn(int paramInt, String paramString) {
    this.p.visitTypeInsn(paramInt, paramString);
    super.visitTypeInsn(paramInt, paramString);
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3) {
    this.p.visitFieldInsn(paramInt, paramString1, paramString2, paramString3);
    super.visitFieldInsn(paramInt, paramString1, paramString2, paramString3);
  }
  
  @Deprecated
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3) {
    if (this.api >= 327680) {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
      return;
    } 
    this.p.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
    if (this.mv != null)
      this.mv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3); 
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    if (this.api < 327680) {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
      return;
    } 
    this.p.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
    if (this.mv != null)
      this.mv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean); 
  }
  
  public void visitInvokeDynamicInsn(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs) {
    this.p.visitInvokeDynamicInsn(paramString1, paramString2, paramHandle, paramVarArgs);
    super.visitInvokeDynamicInsn(paramString1, paramString2, paramHandle, paramVarArgs);
  }
  
  public void visitJumpInsn(int paramInt, Label paramLabel) {
    this.p.visitJumpInsn(paramInt, paramLabel);
    super.visitJumpInsn(paramInt, paramLabel);
  }
  
  public void visitLabel(Label paramLabel) {
    this.p.visitLabel(paramLabel);
    super.visitLabel(paramLabel);
  }
  
  public void visitLdcInsn(Object paramObject) {
    this.p.visitLdcInsn(paramObject);
    super.visitLdcInsn(paramObject);
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2) {
    this.p.visitIincInsn(paramInt1, paramInt2);
    super.visitIincInsn(paramInt1, paramInt2);
  }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label... paramVarArgs) {
    this.p.visitTableSwitchInsn(paramInt1, paramInt2, paramLabel, paramVarArgs);
    super.visitTableSwitchInsn(paramInt1, paramInt2, paramLabel, paramVarArgs);
  }
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel) {
    this.p.visitLookupSwitchInsn(paramLabel, paramArrayOfInt, paramArrayOfLabel);
    super.visitLookupSwitchInsn(paramLabel, paramArrayOfInt, paramArrayOfLabel);
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt) {
    this.p.visitMultiANewArrayInsn(paramString, paramInt);
    super.visitMultiANewArrayInsn(paramString, paramInt);
  }
  
  public AnnotationVisitor visitInsnAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) {
    Printer printer = this.p.visitInsnAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    AnnotationVisitor annotationVisitor = (this.mv == null) ? null : this.mv.visitInsnAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    return new TraceAnnotationVisitor(annotationVisitor, printer);
  }
  
  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString) {
    this.p.visitTryCatchBlock(paramLabel1, paramLabel2, paramLabel3, paramString);
    super.visitTryCatchBlock(paramLabel1, paramLabel2, paramLabel3, paramString);
  }
  
  public AnnotationVisitor visitTryCatchAnnotation(int paramInt, TypePath paramTypePath, String paramString, boolean paramBoolean) {
    Printer printer = this.p.visitTryCatchAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    AnnotationVisitor annotationVisitor = (this.mv == null) ? null : this.mv.visitTryCatchAnnotation(paramInt, paramTypePath, paramString, paramBoolean);
    return new TraceAnnotationVisitor(annotationVisitor, printer);
  }
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt) {
    this.p.visitLocalVariable(paramString1, paramString2, paramString3, paramLabel1, paramLabel2, paramInt);
    super.visitLocalVariable(paramString1, paramString2, paramString3, paramLabel1, paramLabel2, paramInt);
  }
  
  public AnnotationVisitor visitLocalVariableAnnotation(int paramInt, TypePath paramTypePath, Label[] paramArrayOfLabel1, Label[] paramArrayOfLabel2, int[] paramArrayOfInt, String paramString, boolean paramBoolean) {
    Printer printer = this.p.visitLocalVariableAnnotation(paramInt, paramTypePath, paramArrayOfLabel1, paramArrayOfLabel2, paramArrayOfInt, paramString, paramBoolean);
    AnnotationVisitor annotationVisitor = (this.mv == null) ? null : this.mv.visitLocalVariableAnnotation(paramInt, paramTypePath, paramArrayOfLabel1, paramArrayOfLabel2, paramArrayOfInt, paramString, paramBoolean);
    return new TraceAnnotationVisitor(annotationVisitor, printer);
  }
  
  public void visitLineNumber(int paramInt, Label paramLabel) {
    this.p.visitLineNumber(paramInt, paramLabel);
    super.visitLineNumber(paramInt, paramLabel);
  }
  
  public void visitMaxs(int paramInt1, int paramInt2) {
    this.p.visitMaxs(paramInt1, paramInt2);
    super.visitMaxs(paramInt1, paramInt2);
  }
  
  public void visitEnd() {
    this.p.visitMethodEnd();
    super.visitEnd();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\as\\util\TraceMethodVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */