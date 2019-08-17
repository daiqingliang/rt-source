package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class VarInsnNode extends AbstractInsnNode {
  public int var;
  
  public VarInsnNode(int paramInt1, int paramInt2) {
    super(paramInt1);
    this.var = paramInt2;
  }
  
  public void setOpcode(int paramInt) { this.opcode = paramInt; }
  
  public int getType() { return 2; }
  
  public void accept(MethodVisitor paramMethodVisitor) {
    paramMethodVisitor.visitVarInsn(this.opcode, this.var);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap) { return (new VarInsnNode(this.opcode, this.var)).cloneAnnotations(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\VarInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */