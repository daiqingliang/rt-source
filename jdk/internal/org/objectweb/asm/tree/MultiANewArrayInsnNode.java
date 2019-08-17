package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class MultiANewArrayInsnNode extends AbstractInsnNode {
  public String desc;
  
  public int dims;
  
  public MultiANewArrayInsnNode(String paramString, int paramInt) {
    super(197);
    this.desc = paramString;
    this.dims = paramInt;
  }
  
  public int getType() { return 13; }
  
  public void accept(MethodVisitor paramMethodVisitor) {
    paramMethodVisitor.visitMultiANewArrayInsn(this.desc, this.dims);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap) { return (new MultiANewArrayInsnNode(this.desc, this.dims)).cloneAnnotations(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\MultiANewArrayInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */