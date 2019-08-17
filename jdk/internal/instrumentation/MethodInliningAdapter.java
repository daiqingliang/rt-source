package jdk.internal.instrumentation;

import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;

final class MethodInliningAdapter extends MethodVisitor {
  private final Label end;
  
  private final int remapOffset;
  
  public MethodInliningAdapter(MethodVisitor paramMethodVisitor, Label paramLabel, int paramInt1, String paramString, int paramInt2) {
    super(327680, paramMethodVisitor);
    this.remapOffset = paramInt2;
    this.end = paramLabel;
    Type[] arrayOfType = Type.getArgumentTypes(paramString);
    int i = isStatic(paramInt1) ? 0 : 1;
    for (Type type : arrayOfType)
      i += type.getSize(); 
    int j = i;
    for (int k = arrayOfType.length - 1; k >= 0; k--) {
      j -= arrayOfType[k].getSize();
      int m = j + paramInt2;
      int n = arrayOfType[k].getOpcode(54);
      super.visitVarInsn(n, m);
    } 
    if (!isStatic(paramInt1))
      super.visitVarInsn(58, 0 + paramInt2); 
  }
  
  private boolean isStatic(int paramInt) { return ((paramInt & 0x8) != 0); }
  
  public void visitInsn(int paramInt) {
    if (paramInt == 177 || paramInt == 172 || paramInt == 176 || paramInt == 173) {
      visitJumpInsn(167, this.end);
    } else {
      super.visitInsn(paramInt);
    } 
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2) { super.visitVarInsn(paramInt1, paramInt2 + this.remapOffset); }
  
  public void visitIincInsn(int paramInt1, int paramInt2) { super.visitIincInsn(paramInt1 + this.remapOffset, paramInt2); }
  
  public void visitMaxs(int paramInt1, int paramInt2) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\instrumentation\MethodInliningAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */