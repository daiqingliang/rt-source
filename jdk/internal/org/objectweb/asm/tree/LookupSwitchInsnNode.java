package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class LookupSwitchInsnNode extends AbstractInsnNode {
  public LabelNode dflt;
  
  public List<Integer> keys;
  
  public List<LabelNode> labels;
  
  public LookupSwitchInsnNode(LabelNode paramLabelNode, int[] paramArrayOfInt, LabelNode[] paramArrayOfLabelNode) {
    super(171);
    this.dflt = paramLabelNode;
    this.keys = new ArrayList((paramArrayOfInt == null) ? 0 : paramArrayOfInt.length);
    this.labels = new ArrayList((paramArrayOfLabelNode == null) ? 0 : paramArrayOfLabelNode.length);
    if (paramArrayOfInt != null)
      for (byte b = 0; b < paramArrayOfInt.length; b++)
        this.keys.add(Integer.valueOf(paramArrayOfInt[b]));  
    if (paramArrayOfLabelNode != null)
      this.labels.addAll(Arrays.asList(paramArrayOfLabelNode)); 
  }
  
  public int getType() { return 12; }
  
  public void accept(MethodVisitor paramMethodVisitor) {
    int[] arrayOfInt = new int[this.keys.size()];
    for (byte b1 = 0; b1 < arrayOfInt.length; b1++)
      arrayOfInt[b1] = ((Integer)this.keys.get(b1)).intValue(); 
    Label[] arrayOfLabel = new Label[this.labels.size()];
    for (byte b2 = 0; b2 < arrayOfLabel.length; b2++)
      arrayOfLabel[b2] = ((LabelNode)this.labels.get(b2)).getLabel(); 
    paramMethodVisitor.visitLookupSwitchInsn(this.dflt.getLabel(), arrayOfInt, arrayOfLabel);
    acceptAnnotations(paramMethodVisitor);
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap) {
    LookupSwitchInsnNode lookupSwitchInsnNode = new LookupSwitchInsnNode(clone(this.dflt, paramMap), null, clone(this.labels, paramMap));
    lookupSwitchInsnNode.keys.addAll(this.keys);
    return lookupSwitchInsnNode.cloneAnnotations(this);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\LookupSwitchInsnNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */