package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class InvokeDynamicInsnNode extends AbstractInsnNode {
  public String name;
  
  public String desc;
  
  public Handle bsm;
  
  public Object[] bsmArgs;
  
  public InvokeDynamicInsnNode(String paramString1, String paramString2, Handle paramHandle, Object... paramVarArgs) {
    super(186);
    this.name = paramString1;
    this.desc = paramString2;
    this.bsm = paramHandle;
    this.bsmArgs = paramVarArgs;
  }
  
  public int getType() { return 6; }
  
  public void accept(MethodVisitor paramMethodVisitor) {
    paramMethodVisitor.visitInvokeDynamicInsn(this.name, this.desc, this.bsm, this.bsmArgs);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap) { return (new InvokeDynamicInsnNode(this.name, this.desc, this.bsm, this.bsmArgs)).cloneAnnotations(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\InvokeDynamicInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */