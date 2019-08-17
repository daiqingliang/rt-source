package com.sun.xml.internal.ws.org.objectweb.asm;

public interface AnnotationVisitor {
  void visit(String paramString, Object paramObject);
  
  void visitEnum(String paramString1, String paramString2, String paramString3);
  
  AnnotationVisitor visitAnnotation(String paramString1, String paramString2);
  
  AnnotationVisitor visitArray(String paramString);
  
  void visitEnd();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\AnnotationVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */