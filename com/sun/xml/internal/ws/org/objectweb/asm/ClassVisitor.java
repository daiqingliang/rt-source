package com.sun.xml.internal.ws.org.objectweb.asm;

public interface ClassVisitor {
  void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString);
  
  void visitSource(String paramString1, String paramString2);
  
  void visitOuterClass(String paramString1, String paramString2, String paramString3);
  
  AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean);
  
  void visitAttribute(Attribute paramAttribute);
  
  void visitInnerClass(String paramString1, String paramString2, String paramString3, int paramInt);
  
  FieldVisitor visitField(int paramInt, String paramString1, String paramString2, String paramString3, Object paramObject);
  
  MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString);
  
  void visitEnd();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\ClassVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */