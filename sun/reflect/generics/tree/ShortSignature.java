package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class ShortSignature implements BaseType {
  private static final ShortSignature singleton = new ShortSignature();
  
  public static ShortSignature make() { return singleton; }
  
  public void accept(TypeTreeVisitor<?> paramTypeTreeVisitor) { paramTypeTreeVisitor.visitShortSignature(this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\tree\ShortSignature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */