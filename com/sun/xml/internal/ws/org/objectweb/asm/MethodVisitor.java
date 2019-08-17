package com.sun.xml.internal.ws.org.objectweb.asm;

public interface MethodVisitor {
  AnnotationVisitor visitAnnotationDefault();
  
  AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean);
  
  AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean);
  
  void visitAttribute(Attribute paramAttribute);
  
  void visitCode();
  
  void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2);
  
  void visitInsn(int paramInt);
  
  void visitIntInsn(int paramInt1, int paramInt2);
  
  void visitVarInsn(int paramInt1, int paramInt2);
  
  void visitTypeInsn(int paramInt, String paramString);
  
  void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3);
  
  void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3);
  
  void visitJumpInsn(int paramInt, Label paramLabel);
  
  void visitLabel(Label paramLabel);
  
  void visitLdcInsn(Object paramObject);
  
  void visitIincInsn(int paramInt1, int paramInt2);
  
  void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label[] paramArrayOfLabel);
  
  void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel);
  
  void visitMultiANewArrayInsn(String paramString, int paramInt);
  
  void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString);
  
  void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt);
  
  void visitLineNumber(int paramInt, Label paramLabel);
  
  void visitMaxs(int paramInt1, int paramInt2);
  
  void visitEnd();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\MethodVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */