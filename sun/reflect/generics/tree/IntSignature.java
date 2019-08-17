package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class IntSignature implements BaseType {
  private static final IntSignature singleton = new IntSignature();
  
  public static IntSignature make() { return singleton; }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitIntSignature(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\tree\IntSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */