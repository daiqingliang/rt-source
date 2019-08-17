package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class LdcInsnNode extends AbstractInsnNode {
  public Object cst;
  
  public LdcInsnNode(Object paramObject) {
    super(18);
    this.cst = paramObject;
  }
  
  public int getType() { return 9; }
  
  public void accept(MethodVisitor paramMethodVisitor) {
    paramMethodVisitor.visitLdcInsn(this.cst);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap) { return (new LdcInsnNode(this.cst)).cloneAnnotations(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\LdcInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */