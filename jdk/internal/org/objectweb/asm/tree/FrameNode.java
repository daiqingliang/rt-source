package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class FrameNode extends AbstractInsnNode {
  public int type;
  
  public List<Object> local;
  
  public List<Object> stack;
  
  private FrameNode() { super(-1); }
  
  public FrameNode(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2) {
    super(-1);
    this.type = paramInt1;
    switch (paramInt1) {
      case -1:
      case 0:
        this.local = asList(paramInt2, paramArrayOfObject1);
        this.stack = asList(paramInt3, paramArrayOfObject2);
        break;
      case 1:
        this.local = asList(paramInt2, paramArrayOfObject1);
        break;
      case 2:
        this.local = Arrays.asList(new Object[paramInt2]);
        break;
      case 4:
        this.stack = asList(1, paramArrayOfObject2);
        break;
    } 
  }
  
  public int getType() { return 14; }
  
  public void accept(MethodVisitor paramMethodVisitor) {
    switch (this.type) {
      case -1:
      case 0:
        paramMethodVisitor.visitFrame(this.type, this.local.size(), asArray(this.local), this.stack.size(), asArray(this.stack));
        break;
      case 1:
        paramMethodVisitor.visitFrame(this.type, this.local.size(), asArray(this.local), 0, null);
        break;
      case 2:
        paramMethodVisitor.visitFrame(this.type, this.local.size(), null, 0, null);
        break;
      case 3:
        paramMethodVisitor.visitFrame(this.type, 0, null, 0, null);
        break;
      case 4:
        paramMethodVisitor.visitFrame(this.type, 0, null, 1, asArray(this.stack));
        break;
    } 
  }
  
  public AbstractInsnNode clone(Map<LabelNode, LabelNode> paramMap) {
    FrameNode frameNode = new FrameNode();
    frameNode.type = this.type;
    if (this.local != null) {
      frameNode.local = new ArrayList();
      for (byte b = 0; b < this.local.size(); b++) {
        Object object = this.local.get(b);
        if (object instanceof LabelNode)
          object = paramMap.get(object); 
        frameNode.local.add(object);
      } 
    } 
    if (this.stack != null) {
      frameNode.stack = new ArrayList();
      for (byte b = 0; b < this.stack.size(); b++) {
        Object object = this.stack.get(b);
        if (object instanceof LabelNode)
          object = paramMap.get(object); 
        frameNode.stack.add(object);
      } 
    } 
    return frameNode;
  }
  
  private static List<Object> asList(int paramInt, Object[] paramArrayOfObject) { return Arrays.asList(paramArrayOfObject).subList(0, paramInt); }
  
  private static Object[] asArray(List<Object> paramList) {
    Object[] arrayOfObject = new Object[paramList.size()];
    for (byte b = 0; b < arrayOfObject.length; b++) {
      Object object = paramList.get(b);
      if (object instanceof LabelNode)
        object = ((LabelNode)object).getLabel(); 
      arrayOfObject[b] = object;
    } 
    return arrayOfObject;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\FrameNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */