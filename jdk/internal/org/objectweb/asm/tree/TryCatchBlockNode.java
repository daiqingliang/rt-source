package jdk.internal.org.objectweb.asm.tree;

import java.util.List;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class TryCatchBlockNode {
  public LabelNode start;
  
  public LabelNode end;
  
  public LabelNode handler;
  
  public String type;
  
  public List<TypeAnnotationNode> visibleTypeAnnotations;
  
  public List<TypeAnnotationNode> invisibleTypeAnnotations;
  
  public TryCatchBlockNode(LabelNode paramLabelNode1, LabelNode paramLabelNode2, LabelNode paramLabelNode3, String paramString) {
    this.start = paramLabelNode1;
    this.end = paramLabelNode2;
    this.handler = paramLabelNode3;
    this.type = paramString;
  }
  
  public void updateIndex(int paramInt) {
    int i = 0x42000000 | paramInt << 8;
    if (this.visibleTypeAnnotations != null)
      for (TypeAnnotationNode typeAnnotationNode : this.visibleTypeAnnotations)
        typeAnnotationNode.typeRef = i;  
    if (this.invisibleTypeAnnotations != null)
      for (TypeAnnotationNode typeAnnotationNode : this.invisibleTypeAnnotations)
        typeAnnotationNode.typeRef = i;  
  }
  
  public void accept(MethodVisitor paramMethodVisitor) {
    paramMethodVisitor.visitTryCatchBlock(this.start.getLabel(), this.end.getLabel(), (this.handler == null) ? null : this.handler.getLabel(), this.type);
    boolean bool = (this.visibleTypeAnnotations == null) ? 0 : this.visibleTypeAnnotations.size();
    byte b;
    for (b = 0; b < bool; b++) {
      TypeAnnotationNode typeAnnotationNode = (TypeAnnotationNode)this.visibleTypeAnnotations.get(b);
      typeAnnotationNode.accept(paramMethodVisitor.visitTryCatchAnnotation(typeAnnotationNode.typeRef, typeAnnotationNode.typePath, typeAnnotationNode.desc, true));
    } 
    bool = (this.invisibleTypeAnnotations == null) ? 0 : this.invisibleTypeAnnotations.size();
    for (b = 0; b < bool; b++) {
      TypeAnnotationNode typeAnnotationNode = (TypeAnnotationNode)this.invisibleTypeAnnotations.get(b);
      typeAnnotationNode.accept(paramMethodVisitor.visitTryCatchAnnotation(typeAnnotationNode.typeRef, typeAnnotationNode.typePath, typeAnnotationNode.desc, false));
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\TryCatchBlockNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */