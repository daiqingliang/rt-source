package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class LabelNode extends AbstractInsnNode {
  private Label label;
  
  public LabelNode() { super(-1); }
  
  public LabelNode(Label paramLabel) {
    super(-1);
    this.label = paramLabel;
  }
  
  public int getType() { return 8; }
  
  public Label getLabel() {
    if (this.label == null)
      this.label = new Label(); 
    return this.label;
  }
  
  public void accept(MethodVisitor paramMethodVisitor) { paramMethodVisitor.visitLabel(getLabel()); }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap) { return (AbstractInsnNode)paramMap.get(this); }
  
  public void resetLabel() { this.label = null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\LabelNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */