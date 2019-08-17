package jdk.internal.org.objectweb.asm.tree;

import jdk.internal.org.objectweb.asm.ClassVisitor;

public class InnerClassNode {
  public String name;
  
  public String outerName;
  
  public String innerName;
  
  public int access;
  
  public InnerClassNode(String paramString1, String paramString2, String paramString3, int paramInt) {
    this.name = paramString1;
    this.outerName = paramString2;
    this.innerName = paramString3;
    this.access = paramInt;
  }
  
  public void accept(ClassVisitor paramClassVisitor) { paramClassVisitor.visitInnerClass(this.name, this.outerName, this.innerName, this.access); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\tree\InnerClassNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */