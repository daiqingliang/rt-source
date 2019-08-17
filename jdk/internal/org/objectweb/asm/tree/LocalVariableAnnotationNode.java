package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.TypePath;

public class LocalVariableAnnotationNode extends TypeAnnotationNode {
  public List<LabelNode> start;
  
  public List<LabelNode> end;
  
  public List<Integer> index;
  
  public LocalVariableAnnotationNode(int paramInt, TypePath paramTypePath, LabelNode[] paramArrayOfLabelNode1, LabelNode[] paramArrayOfLabelNode2, int[] paramArrayOfInt, String paramString) { this(327680, paramInt, paramTypePath, paramArrayOfLabelNode1, paramArrayOfLabelNode2, paramArrayOfInt, paramString); }
  
  public LocalVariableAnnotationNode(int paramInt1, int paramInt2, TypePath paramTypePath, LabelNode[] paramArrayOfLabelNode1, LabelNode[] paramArrayOfLabelNode2, int[] paramArrayOfInt, String paramString) {
    super(paramInt1, paramInt2, paramTypePath, paramString);
    this.start = new ArrayList(paramArrayOfLabelNode1.length);
    this.start.addAll(Arrays.asList(paramArrayOfLabelNode1));
    this.end = new ArrayList(paramArrayOfLabelNode2.length);
    this.end.addAll(Arrays.asList(paramArrayOfLabelNode2));
    this.index = new ArrayList(paramArrayOfInt.length);
    for (int i : paramArrayOfInt)
      this.index.add(Integer.valueOf(i)); 
  }
  
  public void accept(MethodVisitor paramMethodVisitor, boolean paramBoolean) {
    Label[] arrayOfLabel1 = new Label[this.start.size()];
    Label[] arrayOfLabel2 = new Label[this.end.size()];
    int[] arrayOfInt = new int[this.index.size()];
    for (byte b = 0; b < arrayOfLabel1.length; b++) {
      arrayOfLabel1[b] = ((LabelNode)this.start.get(b)).getLabel();
      arrayOfLabel2[b] = ((LabelNode)this.end.get(b)).getLabel();
      arrayOfInt[b] = ((Integer)this.index.get(b)).intValue();
    } 
    accept(paramMethodVisitor.visitLocalVariableAnnotation(this.typeRef, this.typePath, arrayOfLabel1, arrayOfLabel2, arrayOfInt, this.desc, true));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\LocalVariableAnnotationNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */