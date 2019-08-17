package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class JumpInsnNode extends AbstractInsnNode {
  public LabelNode label;
  
  public JumpInsnNode(int paramInt, LabelNode paramLabelNode) {
    super(paramInt);
    this.label = paramLabelNode;
  }
  
  public void setOpcode(int paramInt) { this.opcode = paramInt; }
  
  public int getType() { return 7; }
  
  public void accept(MethodVisitor paramMethodVisitor) {
    paramMethodVisitor.visitJumpInsn(this.opcode, this.label.getLabel());
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap) { return (new JumpInsnNode(this.opcode, clone(this.label, paramMap))).cloneAnnotations(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\JumpInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */