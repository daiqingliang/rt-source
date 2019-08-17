package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class TableSwitchInsnNode extends AbstractInsnNode {
  public int min;
  
  public int max;
  
  public LabelNode dflt;
  
  public List<LabelNode> labels;
  
  public TableSwitchInsnNode(int paramInt1, int paramInt2, LabelNode paramLabelNode, LabelNode... paramVarArgs) {
    super(170);
    this.min = paramInt1;
    this.max = paramInt2;
    this.dflt = paramLabelNode;
    this.labels = new ArrayList();
    if (paramVarArgs != null)
      this.labels.addAll(Arrays.asList(paramVarArgs)); 
  }
  
  public int getType() { return 11; }
  
  public void accept(MethodVisitor paramMethodVisitor) {
    Label[] arrayOfLabel = new Label[this.labels.size()];
    for (byte b = 0; b < arrayOfLabel.length; b++)
      arrayOfLabel[b] = ((LabelNode)this.labels.get(b)).getLabel(); 
    paramMethodVisitor.visitTableSwitchInsn(this.min, this.max, this.dflt.getLabel(), arrayOfLabel);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap) { return (new TableSwitchInsnNode(this.min, this.max, clone(this.dflt, paramMap), clone(this.labels, paramMap))).cloneAnnotations(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\TableSwitchInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */