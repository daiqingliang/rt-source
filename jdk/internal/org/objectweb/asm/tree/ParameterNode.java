package jdk.internal.org.objectweb.asm.tree;

import jdk.internal.org.objectweb.asm.MethodVisitor;

public class ParameterNode {
  public String name;
  
  public int access;
  
  public ParameterNode(String paramString, int paramInt) {
    this.name = paramString;
    this.access = paramInt;
  }
  
  public void accept(MethodVisitor paramMethodVisitor) { paramMethodVisitor.visitParameter(this.name, this.access); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\ParameterNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */