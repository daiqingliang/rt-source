package jdk.internal.org.objectweb.asm.commons;

import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

public class CodeSizeEvaluator extends MethodVisitor implements Opcodes {
  private int minSize;
  
  private int maxSize;
  
  public CodeSizeEvaluator(MethodVisitor paramMethodVisitor) { this(327680, paramMethodVisitor); }
  
  protected CodeSizeEvaluator(int paramInt, MethodVisitor paramMethodVisitor) { super(paramInt, paramMethodVisitor); }
  
  public int getMinSize() { return this.minSize; }
  
  public int getMaxSize() { return this.maxSize; }
  
  public void visitInsn(int paramInt) {
    this.minSize++;
    this.maxSize++;
    if (this.mv != null)
      this.mv.visitInsn(paramInt); 
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2) {
    if (paramInt1 == 17) {
      this.minSize += 3;
      this.maxSize += 3;
    } else {
      this.minSize += 2;
      this.maxSize += 2;
    } 
    if (this.mv != null)
      this.mv.visitIntInsn(paramInt1, paramInt2); 
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2) {
    if (paramInt2 < 4 && paramInt1 != 169) {
      this.minSize++;
      this.maxSize++;
    } else if (paramInt2 >= 256) {
      this.minSize += 4;
      this.maxSize += 4;
    } else {
      this.minSize += 2;
      this.maxSize += 2;
    } 
    if (this.mv != null)
      this.mv.visitVarInsn(paramInt1, paramInt2); 
  }
  
  public void visitTypeInsn(int paramInt, String paramString) {
    this.minSize += 3;
    this.maxSize += 3;
    if (this.mv != null)
      this.mv.visitTypeInsn(paramInt, paramString); 
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3) {
    this.minSize += 3;
    this.maxSize += 3;
    if (this.mv != null)
      this.mv.visitFieldInsn(paramInt, paramString1, paramString2, paramString3); 
  }
  
  @Deprecated
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3) {
    if (this.api >= 327680) {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3);
      return;
    } 
    doVisitMethodInsn(paramInt, paramString1, paramString2, paramString3, (paramInt == 185));
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    if (this.api < 327680) {
      super.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
      return;
    } 
    doVisitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
  }
  
  private void doVisitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    if (paramInt == 185) {
      this.minSize += 5;
      this.maxSize += 5;
    } else {
      this.minSize += 3;
      this.maxSize += 3;
    } 
    if (this.mv != null)
      this.mv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean); 
  }
  
  public void visitInvokeDynamicInsn(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs) {
    this.minSize += 5;
    this.maxSize += 5;
    if (this.mv != null)
      this.mv.visitInvokeDynamicInsn(paramString1, paramString2, paramHandle, paramVarArgs); 
  }
  
  public void visitJumpInsn(int paramInt, Label paramLabel) {
    this.minSize += 3;
    if (paramInt == 167 || paramInt == 168) {
      this.maxSize += 5;
    } else {
      this.maxSize += 8;
    } 
    if (this.mv != null)
      this.mv.visitJumpInsn(paramInt, paramLabel); 
  }
  
  public void visitLdcInsn(Object paramObject) {
    if (paramObject instanceof Long || paramObject instanceof Double) {
      this.minSize += 3;
      this.maxSize += 3;
    } else {
      this.minSize += 2;
      this.maxSize += 3;
    } 
    if (this.mv != null)
      this.mv.visitLdcInsn(paramObject); 
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2) {
    if (paramInt1 > 255 || paramInt2 > 127 || paramInt2 < -128) {
      this.minSize += 6;
      this.maxSize += 6;
    } else {
      this.minSize += 3;
      this.maxSize += 3;
    } 
    if (this.mv != null)
      this.mv.visitIincInsn(paramInt1, paramInt2); 
  }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label... paramVarArgs) {
    this.minSize += 13 + paramVarArgs.length * 4;
    this.maxSize += 16 + paramVarArgs.length * 4;
    if (this.mv != null)
      this.mv.visitTableSwitchInsn(paramInt1, paramInt2, paramLabel, paramVarArgs); 
  }
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel) {
    this.minSize += 9 + paramArrayOfInt.length * 8;
    this.maxSize += 12 + paramArrayOfInt.length * 8;
    if (this.mv != null)
      this.mv.visitLookupSwitchInsn(paramLabel, paramArrayOfInt, paramArrayOfLabel); 
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt) {
    this.minSize += 4;
    this.maxSize += 4;
    if (this.mv != null)
      this.mv.visitMultiANewArrayInsn(paramString, paramInt); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\commons\CodeSizeEvaluator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */