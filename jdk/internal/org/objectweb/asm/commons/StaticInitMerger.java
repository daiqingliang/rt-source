package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class StaticInitMerger extends ClassVisitor {
  private String name;
  
  private MethodVisitor clinit;
  
  private final String prefix;
  
  private int counter;
  
  public StaticInitMerger(String paramString, ClassVisitor paramClassVisitor) { this(327680, paramString, paramClassVisitor); }
  
  protected StaticInitMerger(int paramInt, String paramString, ClassVisitor paramClassVisitor) {
    super(paramInt, paramClassVisitor);
    this.prefix = paramString;
  }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    this.cv.visit(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
    this.name = paramString1;
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    MethodVisitor methodVisitor;
    if ("<clinit>".equals(paramString1)) {
      byte b = 10;
      String str = this.prefix + this.counter++;
      methodVisitor = this.cv.visitMethod(b, str, paramString2, paramString3, paramArrayOfString);
      if (this.clinit == null)
        this.clinit = this.cv.visitMethod(b, paramString1, paramString2, null, null); 
      this.clinit.visitMethodInsn(184, this.name, str, paramString2, false);
    } else {
      methodVisitor = this.cv.visitMethod(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
    } 
    return methodVisitor;
  }
  
  public void visitEnd() {
    if (this.clinit != null) {
      this.clinit.visitInsn(177);
      this.clinit.visitMaxs(0, 0);
    } 
    this.cv.visitEnd();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\commons\StaticInitMerger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */