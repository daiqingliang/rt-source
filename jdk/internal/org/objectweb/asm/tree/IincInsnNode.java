package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class IincInsnNode extends AbstractInsnNode {
  public int var;
  
  public int incr;
  
  public IincInsnNode(int paramInt1, int paramInt2) {
    super(132);
    this.var = paramInt1;
    this.incr = paramInt2;
  }
  
  public int getType() { return 10; }
  
  public void accept(MethodVisitor paramMethodVisitor) {
    paramMethodVisitor.visitIincInsn(this.var, this.incr);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap) { return (new IincInsnNode(this.var, this.incr)).cloneAnnotations(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\IincInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */