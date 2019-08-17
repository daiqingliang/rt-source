package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class InsnNode extends AbstractInsnNode {
  public InsnNode(int paramInt) { super(paramInt); }
  
  public int getType() { return 0; }
  
  public void accept(MethodVisitor paramMethodVisitor) {
    paramMethodVisitor.visitInsn(this.opcode);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap) { return (new InsnNode(this.opcode)).cloneAnnotations(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\InsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */