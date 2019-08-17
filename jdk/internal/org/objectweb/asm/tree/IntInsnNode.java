package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class IntInsnNode extends AbstractInsnNode {
  public int operand;
  
  public IntInsnNode(int paramInt1, int paramInt2) {
    super(paramInt1);
    this.operand = paramInt2;
  }
  
  public void setOpcode(int paramInt) { this.opcode = paramInt; }
  
  public int getType() { return 1; }
  
  public void accept(MethodVisitor paramMethodVisitor) {
    paramMethodVisitor.visitIntInsn(this.opcode, this.operand);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap) { return (new IntInsnNode(this.opcode, this.operand)).cloneAnnotations(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\IntInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */