package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class VoidDescriptor implements ReturnType {
  private static final VoidDescriptor singleton = new VoidDescriptor();
  
  public static VoidDescriptor make() { return singleton; }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitVoidDescriptor(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\tree\VoidDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */