package jdk.internal.org.objectweb.asm.tree;

import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class LineNumberNode extends AbstractInsnNode {
  public int line;
  
  public LabelNode start;
  
  public LineNumberNode(int paramInt, LabelNode paramLabelNode) {
    super(-1);
    this.line = paramInt;
    this.start = paramLabelNode;
  }
  
  public int getType() { return 15; }
  
  public void accept(MethodVisitor paramMethodVisitor) { paramMethodVisitor.visitLineNumber(this.line, this.start.getLabel()); }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap) { return new LineNumberNode(this.line, clone(this.start, paramMap)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\LineNumberNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */